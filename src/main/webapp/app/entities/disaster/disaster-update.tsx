import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { DisasterType } from 'app/shared/model/enumerations/disaster-type.model';
import { createEntity, getEntity, reset, updateEntity } from './disaster.reducer';

export const DisasterUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const disasterEntity = useAppSelector(state => state.disaster.entity);
  const loading = useAppSelector(state => state.disaster.loading);
  const updating = useAppSelector(state => state.disaster.updating);
  const updateSuccess = useAppSelector(state => state.disaster.updateSuccess);
  const disasterTypeValues = Object.keys(DisasterType);

  const handleClose = () => {
    navigate(`/disaster${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.longitude !== undefined && typeof values.longitude !== 'number') {
      values.longitude = Number(values.longitude);
    }
    if (values.latitude !== undefined && typeof values.latitude !== 'number') {
      values.latitude = Number(values.latitude);
    }
    if (values.radius !== undefined && typeof values.radius !== 'number') {
      values.radius = Number(values.radius);
    }

    const entity = {
      ...disasterEntity,
      ...values,
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
          type: 'MINOR',
          ...disasterEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="disasterApp.disaster.home.createOrEditLabel" data-cy="DisasterCreateUpdateHeading">
            <Translate contentKey="disasterApp.disaster.home.createOrEditLabel">Create or edit a Disaster</Translate>
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
                  id="disaster-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('disasterApp.disaster.name')} id="disaster-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label={translate('disasterApp.disaster.longitude')}
                id="disaster-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
              />
              <ValidatedField
                label={translate('disasterApp.disaster.latitude')}
                id="disaster-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
              />
              <ValidatedField
                label={translate('disasterApp.disaster.radius')}
                id="disaster-radius"
                name="radius"
                data-cy="radius"
                type="text"
              />
              <ValidatedField label={translate('disasterApp.disaster.type')} id="disaster-type" name="type" data-cy="type" type="select">
                {disasterTypeValues.map(disasterType => (
                  <option value={disasterType} key={disasterType}>
                    {translate(`disasterApp.DisasterType.${disasterType}`)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/disaster" replace color="info">
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

export default DisasterUpdate;
