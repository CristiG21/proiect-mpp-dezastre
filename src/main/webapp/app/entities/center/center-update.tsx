import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { createEntity, getEntity, reset, updateEntity } from './center.reducer';
import mapboxgl from 'mapbox-gl';
import axios from 'axios';

export const CenterUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const centerEntity = useAppSelector(state => state.center.entity);
  const loading = useAppSelector(state => state.center.loading);
  const updating = useAppSelector(state => state.center.updating);
  const updateSuccess = useAppSelector(state => state.center.updateSuccess);
  const account = useAppSelector(state => state.authentication.account);

  const [address, setAddress] = useState(centerEntity?.address || '');
  const [latitude, setLatitude] = useState(centerEntity?.latitude || null);
  const [longitude, setLongitude] = useState(centerEntity?.longitude || null);
  const [addressSuggestions, setAddressSuggestions] = useState([]);
  const CENTER_TYPE_ENUM = ['SHELTER', 'MEDICAL', 'FOOD', 'WATER', 'SANITATION', 'COMMUNICATION', 'TRANSPORTATION'];
  const [selectedTypes, setSelectedTypes] = useState<string[]>([]);
  const [photoFiles, setPhotoFiles] = useState<File[]>([]);

  const handleAddressChange = async e => {
    const value = e.target.value;
    setAddress(value);
    if (value.length < 3) {
      setAddressSuggestions([]);
      return;
    }

    try {
      const res = await axios.get(`https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(value)}.json`, {
        params: {
          access_token: mapboxgl.accessToken,
          autocomplete: true,
          limit: 5,
        },
      });
      setAddressSuggestions(res.data.features);
    } catch (err) {
      console.error('Mapbox autocomplete error', err);
    }
  };

  const handleSuggestionSelect = suggestion => {
    setAddress(suggestion.place_name);
    setLatitude(suggestion.center[1]);
    setLongitude(suggestion.center[0]);
    setAddressSuggestions([]);
  };
  const handlePhotoFilesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setPhotoFiles(Array.from(e.target.files));
    }
  };
  const handleClose = () => {
    if (account.authorities?.includes('ROLE_ADMIN')) {
      navigate(`/center${location.search}`);
    } else {
      navigate('/my-centers', {
        state: {
          centerCreated: true,
        },
      });
    }
  };
  useEffect(() => {
    if (!isNew && centerEntity) {
      setLatitude(centerEntity.latitude);
      setLongitude(centerEntity.longitude);
    }
  }, [centerEntity]);
  useEffect(() => {
    if (!isNew && latitude && longitude) {
      const fetchAddressFromCoordinates = async () => {
        try {
          const res = await axios.get(`https://api.mapbox.com/geocoding/v5/mapbox.places/${longitude},${latitude}.json`, {
            params: {
              access_token: mapboxgl.accessToken,
              limit: 1,
            },
          });
          if (res.data.features?.length > 0) {
            setAddress(res.data.features[0].place_name);
          }
        } catch (err) {
          console.error('Failed to reverse geocode coordinates:', err);
        }
      };

      fetchAddressFromCoordinates();
    }
  }, [isNew, latitude, longitude]);

  useEffect(() => {
    if (!isNew && centerEntity?.types) {
      setSelectedTypes(centerEntity.types.map(t => t.type));
    }
  }, [centerEntity]);
  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    // Normalize numeric fields
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.longitude !== undefined && typeof values.longitude !== 'number') {
      values.longitude = Number(values.longitude);
    }
    if (values.latitude !== undefined && typeof values.latitude !== 'number') {
      values.latitude = Number(values.latitude);
    }
    if (values.availableSeats !== undefined && typeof values.availableSeats !== 'number') {
      values.availableSeats = Number(values.availableSeats);
    }

    // Overwrite with geocoded values if present
    values.longitude = longitude;
    values.latitude = latitude;

    const entity = {
      ...centerEntity,
      ...values,
      user: account,
    };
    console.error(account);

    const uploadPhotos = async (centerId: number) => {
      const formData = new FormData();
      photoFiles.forEach(file => {
        formData.append('files', file);
      });

      try {
        await axios.post(`/api/photos/upload/${centerId}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
      } catch (err) {
        console.error('Failed to upload photos:', err);
      }
    };

    const saveWrappers = centerId => {
      const wrapperRequests = selectedTypes.map(type =>
        axios.post('/api/center-type-wrappers', {
          center: { id: centerId },
          type,
        }),
      );

      return Promise.all(wrapperRequests);
    };

    if (isNew) {
      dispatch(createEntity(entity)).then(async action => {
        const saved = (action.payload as any)?.data;
        if (saved?.id) {
          //await uploadPhotos(saved.id);
          await saveWrappers(saved.id);
          handleClose();
        }
      });
    } else {
      dispatch(updateEntity(entity)).then(async action => {
        const saved = (action.payload as any)?.data;
        if (saved?.id) {
          //await uploadPhotos(saved.id);
          await saveWrappers(saved.id);
          handleClose();
        }
      });
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...centerEntity,
          user: centerEntity?.user?.id,
          types: centerEntity.types || [],
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="disasterApp.center.home.createOrEditLabel" data-cy="CenterCreateUpdateHeading">
            <Translate contentKey="disasterApp.center.home.createOrEditLabel">Create or edit a Center</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="center-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('disasterApp.center.name')} id="center-name" name="name" data-cy="name" type="text" />
              <div className="mb-3">
                <label htmlFor="center-address" className="form-label">
                  {translate('disasterApp.center.address')}
                </label>
                <input
                  type="text"
                  id="center-address"
                  name="address"
                  className="form-control"
                  value={address}
                  onChange={handleAddressChange}
                  autoComplete="off"
                />
                {addressSuggestions.length > 0 && (
                  <ul className="list-group position-absolute w-100 zindex-dropdown" style={{ maxHeight: '150px', overflowY: 'auto' }}>
                    {addressSuggestions.map(suggestion => (
                      <li
                        key={suggestion.id}
                        className="list-group-item list-group-item-action"
                        onClick={() => handleSuggestionSelect(suggestion)}
                      >
                        {suggestion.place_name}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
              {/* <ValidatedField*/}
              {/*  label={translate('disasterApp.center.longitude')}*/}
              {/*  id="center-longitude"*/}
              {/*  name="longitude"*/}
              {/*  data-cy="longitude"*/}
              {/*  type="text"*/}
              {/* />*/}
              {/* <ValidatedField*/}
              {/*  label={translate('disasterApp.center.latitude')}*/}
              {/*  id="center-latitude"*/}
              {/*  name="latitude"*/}
              {/*  data-cy="latitude"*/}
              {/*  type="text"*/}
              {/* />*/}
              {/* <ValidatedField*/}
              {/*  label={translate('disasterApp.center.status')}*/}
              {/*  id="center-status"*/}
              {/*  name="status"*/}
              {/*  data-cy="status"*/}
              {/*  check*/}
              {/*  type="checkbox"*/}
              {/* />*/}
              <ValidatedField
                label={translate('disasterApp.center.description')}
                id="center-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('disasterApp.center.availableSeats')}
                id="center-availableSeats"
                name="availableSeats"
                data-cy="availableSeats"
                type="text"
              />
              <ValidatedField
                label={translate('disasterApp.center.openTime')}
                id="center-openTime"
                name="openTime"
                data-cy="openTime"
                type="time"
                placeholder="HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('disasterApp.center.closeTime')}
                id="center-closeTime"
                name="closeTime"
                data-cy="closeTime"
                type="time"
                placeholder="HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <div className="mb-3">
                <label className="form-label">{translate('disasterApp.center.types')}</label>
                <div role="group" aria-labelledby="center-types">
                  {CENTER_TYPE_ENUM.map(type => (
                    <div className="form-check" key={type}>
                      <input
                        className="form-check-input"
                        type="checkbox"
                        id={`type-${type}`}
                        value={type}
                        checked={selectedTypes.includes(type)}
                        onChange={e => {
                          const updatedTypes = e.target.checked ? [...selectedTypes, type] : selectedTypes.filter(t => t !== type);
                          setSelectedTypes(updatedTypes);
                        }}
                      />
                      <label className="form-check-label" htmlFor={`type-${type}`}>
                        {type.charAt(0) + type.slice(1).toLowerCase()}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
              <div className="mb-3">
                <label htmlFor="center-image" className="form-label">
                  Upload Center Image
                </label>
                <input type="file" id="center-image" accept="image/*" className="form-control" multiple onChange={handlePhotoFilesChange} />
              </div>
              {/* <ValidatedField id="center-user" name="user" data-cy="user" label={translate('disasterApp.center.user')} type="select">*/}
              {/*  <option value="" key="0" />*/}
              {/*  {users*/}
              {/*    ? users.map(otherEntity => (*/}
              {/*        <option value={otherEntity.id} key={otherEntity.id}>*/}
              {/*          {otherEntity.login}*/}
              {/*        </option>*/}
              {/*      ))*/}
              {/*    : null}*/}
              {/* </ValidatedField>*/}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/center" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CenterUpdate;
