import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { faCheck, faBan } from '@fortawesome/free-solid-svg-icons';
import { getEntities, updateEntity } from './center.reducer';

export const Center = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = account?.authorities?.includes('ROLE_ADMIN');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const centerList = useAppSelector(state => state.center.entities);
  const loading = useAppSelector(state => state.center.loading);
  const totalItems = useAppSelector(state => state.center.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };
  const toggleStatus = center => {
    dispatch(
      updateEntity({
        ...center,
        status: !center.status,
      }),
    ).then(() => getAllEntities()); // Refresh the list after update
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
      <h2 id="center-heading" data-cy="CenterHeading">
        <Translate contentKey="disasterApp.center.home.title">Centers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="disasterApp.center.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/center/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="disasterApp.center.home.createLabel">Create new Center</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {centerList && centerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="disasterApp.center.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="disasterApp.center.name">Name</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('longitude')}>
                  <Translate contentKey="disasterApp.center.longitude">Longitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('longitude')} />
                </th>
                <th className="hand" onClick={sort('latitude')}>
                  <Translate contentKey="disasterApp.center.latitude">Latitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latitude')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="disasterApp.center.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="disasterApp.center.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('availableSeats')}>
                  <Translate contentKey="disasterApp.center.availableSeats">Available Seats</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('availableSeats')} />
                </th>
                <th className="hand" onClick={sort('openTime')}>
                  <Translate contentKey="disasterApp.center.openTime">Open Time</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('openTime')} />
                </th>
                <th className="hand" onClick={sort('closeTime')}>
                  <Translate contentKey="disasterApp.center.closeTime">Close Time</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('closeTime')} />
                </th>
                <th>
                  <Translate contentKey="disasterApp.center.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {centerList.map((center, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/center/${center.id}`} color="link" size="sm">
                      {center.id}
                    </Button>
                  </td>
                  <td>{center.name}</td>
                  <td>{center.longitude}</td>
                  <td>{center.latitude}</td>
                  <td>{center.status ? 'true' : 'false'}</td>
                  <td>{center.description}</td>
                  <td>{center.availableSeats}</td>
                  <td>{center.openTime}</td>
                  <td>{center.closeTime}</td>
                  <td>{center.user ? center.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      {/* Existing buttons */}
                      <Button tag={Link} to={`/center/${center.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/center/${center.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/center/${center.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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

                      {/* New Approve/Unapprove Button */}
                      {isAdmin && (
                        <Button onClick={() => toggleStatus(center)} color={center.status ? 'warning' : 'success'} size="sm">
                          <FontAwesomeIcon icon={center.status ? faBan : faCheck} />{' '}
                          <span className="d-none d-md-inline">
                            {center.status
                              ? translate('disasterApp.center.action.unapprove')
                              : translate('disasterApp.center.action.approve')}
                          </span>
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="disasterApp.center.home.notFound">No Centers found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={centerList && centerList.length > 0 ? '' : 'd-none'}>
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

export default Center;
