import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { getEntities as getCommunityMessages } from 'app/entities/community-message/community-message.reducer';
import { createEntity, getEntity, reset, updateEntity } from './community-message.reducer';

export const CommunityMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const communityMessages = useAppSelector(state => state.communityMessage.entities);
  const communityMessageEntity = useAppSelector(state => state.communityMessage.entity);
  const loading = useAppSelector(state => state.communityMessage.loading);
  const updating = useAppSelector(state => state.communityMessage.updating);
  const updateSuccess = useAppSelector(state => state.communityMessage.updateSuccess);

  const handleClose = () => {
    navigate(`/community-message${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getCommunityMessages({}));
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
    values.time_posted = convertDateTimeToServer(values.time_posted);
    values.timeApproved = convertDateTimeToServer(values.timeApproved);

    const entity = {
      ...communityMessageEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      parent: communityMessages.find(it => it.id.toString() === values.parent?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          time_posted: displayDefaultDateTime(),
          timeApproved: displayDefaultDateTime(),
        }
      : {
          ...communityMessageEntity,
          time_posted: convertDateTimeFromServer(communityMessageEntity.time_posted),
          timeApproved: convertDateTimeFromServer(communityMessageEntity.timeApproved),
          user: communityMessageEntity?.user?.id,
          parent: communityMessageEntity?.parent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="disasterApp.communityMessage.home.createOrEditLabel" data-cy="CommunityMessageCreateUpdateHeading">
            <Translate contentKey="disasterApp.communityMessage.home.createOrEditLabel">Create or edit a CommunityMessage</Translate>
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
                  id="community-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('disasterApp.communityMessage.content')}
                id="community-message-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('disasterApp.communityMessage.time_posted')}
                id="community-message-time_posted"
                name="time_posted"
                data-cy="time_posted"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('disasterApp.communityMessage.approved')}
                id="community-message-approved"
                name="approved"
                data-cy="approved"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('disasterApp.communityMessage.timeApproved')}
                id="community-message-timeApproved"
                name="timeApproved"
                data-cy="timeApproved"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="community-message-user"
                name="user"
                data-cy="user"
                label={translate('disasterApp.communityMessage.user')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="community-message-parent"
                name="parent"
                data-cy="parent"
                label={translate('disasterApp.communityMessage.parent')}
                type="select"
              >
                <option value="" key="0" />
                {communityMessages
                  ? communityMessages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/community-message" replace color="info">
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

export default CommunityMessageUpdate;
