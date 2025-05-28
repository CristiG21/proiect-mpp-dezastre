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

  const [address, setAddress] = useState(centerEntity?.address || '');
  const [latitude, setLatitude] = useState(centerEntity?.latitude || null);
  const [longitude, setLongitude] = useState(centerEntity?.longitude || null);
  const [addressSuggestions, setAddressSuggestions] = useState([]);

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

  const handleClose = () => {
    navigate(`/center${location.search}`);
  };

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
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    // if (values.longitude !== undefined && typeof values.longitude !== 'number') {
    //   values.longitude = Number(values.longitude);
    // }
    // if (values.latitude !== undefined && typeof values.latitude !== 'number') {
    //   values.latitude = Number(values.latitude);
    // }
    if (values.availableSeats !== undefined && typeof values.availableSeats !== 'number') {
      values.availableSeats = Number(values.availableSeats);
    }
    values.longitude = longitude;
    values.latitude = latitude;

    const entity = {
      ...centerEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...centerEntity,
          user: centerEntity?.user?.id,
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
