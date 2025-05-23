import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './official-message.reducer';

export const OfficialMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const officialMessageEntity = useAppSelector(state => state.officialMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="officialMessageDetailsHeading">
          <Translate contentKey="disasterApp.officialMessage.detail.title">OfficialMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{officialMessageEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="disasterApp.officialMessage.title">Title</Translate>
            </span>
          </dt>
          <dd>{officialMessageEntity.title}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="disasterApp.officialMessage.body">Body</Translate>
            </span>
          </dt>
          <dd>{officialMessageEntity.body}</dd>
          <dt>
            <span id="timePosted">
              <Translate contentKey="disasterApp.officialMessage.timePosted">Time Posted</Translate>
            </span>
          </dt>
          <dd>
            {officialMessageEntity.timePosted ? (
              <TextFormat value={officialMessageEntity.timePosted} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="disasterApp.officialMessage.user">User</Translate>
          </dt>
          <dd>{officialMessageEntity.user ? officialMessageEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/official-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/official-message/${officialMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfficialMessageDetail;
