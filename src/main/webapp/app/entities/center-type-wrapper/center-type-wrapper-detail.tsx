import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './center-type-wrapper.reducer';

export const CenterTypeWrapperDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const centerTypeWrapperEntity = useAppSelector(state => state.centerTypeWrapper.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="centerTypeWrapperDetailsHeading">
          <Translate contentKey="disasterApp.centerTypeWrapper.detail.title">CenterTypeWrapper</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{centerTypeWrapperEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="disasterApp.centerTypeWrapper.type">Type</Translate>
            </span>
          </dt>
          <dd>{centerTypeWrapperEntity.type}</dd>
          <dt>
            <Translate contentKey="disasterApp.centerTypeWrapper.center">Center</Translate>
          </dt>
          <dd>{centerTypeWrapperEntity.center ? centerTypeWrapperEntity.center.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/center-type-wrapper" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/center-type-wrapper/${centerTypeWrapperEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CenterTypeWrapperDetail;
