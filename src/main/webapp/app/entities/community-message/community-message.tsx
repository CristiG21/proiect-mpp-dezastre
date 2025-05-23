import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './community-message.reducer';

export const CommunityMessage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const communityMessageList = useAppSelector(state => state.communityMessage.entities);
  const loading = useAppSelector(state => state.communityMessage.loading);
  const totalItems = useAppSelector(state => state.communityMessage.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="community-message-heading" data-cy="CommunityMessageHeading">
        <Translate contentKey="disasterApp.communityMessage.home.title">Community Messages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="disasterApp.communityMessage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/community-message/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="disasterApp.communityMessage.home.createLabel">Create new Community Message</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {communityMessageList && communityMessageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="disasterApp.communityMessage.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('content')}>
                  <Translate contentKey="disasterApp.communityMessage.content">Content</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('content')} />
                </th>
                <th className="hand" onClick={sort('time_posted')}>
                  <Translate contentKey="disasterApp.communityMessage.time_posted">Time Posted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('time_posted')} />
                </th>
                <th className="hand" onClick={sort('approved')}>
                  <Translate contentKey="disasterApp.communityMessage.approved">Approved</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('approved')} />
                </th>
                <th className="hand" onClick={sort('timeApproved')}>
                  <Translate contentKey="disasterApp.communityMessage.timeApproved">Time Approved</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timeApproved')} />
                </th>
                <th>
                  <Translate contentKey="disasterApp.communityMessage.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="disasterApp.communityMessage.parent">Parent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {communityMessageList.map((communityMessage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/community-message/${communityMessage.id}`} color="link" size="sm">
                      {communityMessage.id}
                    </Button>
                  </td>
                  <td>{communityMessage.content}</td>
                  <td>
                    {communityMessage.time_posted ? (
                      <TextFormat type="date" value={communityMessage.time_posted} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{communityMessage.approved ? 'true' : 'false'}</td>
                  <td>
                    {communityMessage.timeApproved ? (
                      <TextFormat type="date" value={communityMessage.timeApproved} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{communityMessage.user ? communityMessage.user.login : ''}</td>
                  <td>
                    {communityMessage.parent ? (
                      <Link to={`/community-message/${communityMessage.parent.id}`}>{communityMessage.parent.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/community-message/${communityMessage.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/community-message/${communityMessage.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/community-message/${communityMessage.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="disasterApp.communityMessage.home.notFound">No Community Messages found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={communityMessageList && communityMessageList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default CommunityMessage;
