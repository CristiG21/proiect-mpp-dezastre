import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './center.reducer';

export const CenterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const centerEntity = useAppSelector(state => state.center.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="centerDetailsHeading">
          <Translate contentKey="disasterApp.center.detail.title">Center</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{centerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="disasterApp.center.name">Name</Translate>
            </span>
          </dt>
          <dd>{centerEntity.name}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="disasterApp.center.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{centerEntity.longitude}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="disasterApp.center.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{centerEntity.latitude}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="disasterApp.center.status">Status</Translate>
            </span>
          </dt>
          <dd>{centerEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/center" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/center/${centerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CenterDetail;
