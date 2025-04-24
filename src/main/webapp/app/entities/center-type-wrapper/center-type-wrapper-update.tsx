import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCenters } from 'app/entities/center/center.reducer';
import { CenterType } from 'app/shared/model/enumerations/center-type.model';
import { createEntity, getEntity, reset, updateEntity } from './center-type-wrapper.reducer';

export const CenterTypeWrapperUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const centers = useAppSelector(state => state.center.entities);
  const centerTypeWrapperEntity = useAppSelector(state => state.centerTypeWrapper.entity);
  const loading = useAppSelector(state => state.centerTypeWrapper.loading);
  const updating = useAppSelector(state => state.centerTypeWrapper.updating);
  const updateSuccess = useAppSelector(state => state.centerTypeWrapper.updateSuccess);
  const centerTypeValues = Object.keys(CenterType);

  const handleClose = () => {
    navigate(`/center-type-wrapper${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCenters({}));
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

    const entity = {
      ...centerTypeWrapperEntity,
      ...values,
      center: centers.find(it => it.id.toString() === values.center?.toString()),
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
          type: 'SHELTER',
          ...centerTypeWrapperEntity,
          center: centerTypeWrapperEntity?.center?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="disasterApp.centerTypeWrapper.home.createOrEditLabel" data-cy="CenterTypeWrapperCreateUpdateHeading">
            <Translate contentKey="disasterApp.centerTypeWrapper.home.createOrEditLabel">Create or edit a CenterTypeWrapper</Translate>
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
                  id="center-type-wrapper-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('disasterApp.centerTypeWrapper.type')}
                id="center-type-wrapper-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {centerTypeValues.map(centerType => (
                  <option value={centerType} key={centerType}>
                    {translate(`disasterApp.CenterType.${centerType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="center-type-wrapper-center"
                name="center"
                data-cy="center"
                label={translate('disasterApp.centerTypeWrapper.center')}
                type="select"
              >
                <option value="" key="0" />
                {centers
                  ? centers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/center-type-wrapper" replace color="info">
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

export default CenterTypeWrapperUpdate;
