import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './community-message.reducer';

export const CommunityMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const communityMessageEntity = useAppSelector(state => state.communityMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="communityMessageDetailsHeading">
          <Translate contentKey="disasterApp.communityMessage.detail.title">CommunityMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{communityMessageEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="disasterApp.communityMessage.content">Content</Translate>
            </span>
          </dt>
          <dd>{communityMessageEntity.content}</dd>
          <dt>
            <span id="time_posted">
              <Translate contentKey="disasterApp.communityMessage.time_posted">Time Posted</Translate>
            </span>
          </dt>
          <dd>
            {communityMessageEntity.time_posted ? (
              <TextFormat value={communityMessageEntity.time_posted} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="type">
              <Translate contentKey="disasterApp.communityMessage.type">Type</Translate>
            </span>
          </dt>
          <dd>{communityMessageEntity.type}</dd>
          <dt>
            <span id="parentId">
              <Translate contentKey="disasterApp.communityMessage.parentId">Parent Id</Translate>
            </span>
          </dt>
          <dd>{communityMessageEntity.parentId}</dd>
          <dt>
            <span id="approved">
              <Translate contentKey="disasterApp.communityMessage.approved">Approved</Translate>
            </span>
          </dt>
          <dd>{communityMessageEntity.approved ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="disasterApp.communityMessage.user">User</Translate>
          </dt>
          <dd>{communityMessageEntity.user ? communityMessageEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/community-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/community-message/${communityMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CommunityMessageDetail;
