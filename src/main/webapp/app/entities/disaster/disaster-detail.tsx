import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './disaster.reducer';

export const DisasterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const disasterEntity = useAppSelector(state => state.disaster.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="disasterDetailsHeading">
          <Translate contentKey="disasterApp.disaster.detail.title">Disaster</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="disasterApp.disaster.name">Name</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.name}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="disasterApp.disaster.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.longitude}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="disasterApp.disaster.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.latitude}</dd>
          <dt>
            <span id="radius">
              <Translate contentKey="disasterApp.disaster.radius">Radius</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.radius}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="disasterApp.disaster.type">Type</Translate>
            </span>
          </dt>
          <dd>{disasterEntity.type}</dd>
        </dl>
        <Button tag={Link} to="/disaster" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/disaster/${disasterEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DisasterDetail;
