import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { Button, Table } from 'reactstrap';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { getPaginationState, translate, Translate } from 'react-jhipster';
import { getEntities } from 'app/entities/center/center.reducer';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import ToastNotification from 'app/modules/home/components/ToastNotification';

const MyCenters = () => {
  const [centers, setCenters] = useState<ICenter[]>([]);
  const pageLocation = useLocation();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const loading = useAppSelector(state => state.center.loading);
  const location = useLocation();
  const [showToast, setShowToast] = useState(false);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  useEffect(() => {
    axios.get<ICenter[]>('/api/centers/my-centers').then(res => setCenters(res.data));
  }, []);
  const handleSyncList = () => {
    sortEntities();
  };

  useEffect(() => {
    if (location.state?.centerCreated) {
      setShowToast(true);
      const timer = setTimeout(() => setShowToast(false), 5000);
      return () => clearTimeout(timer);
    }
  }, [location.state]);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };
  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };
  return (
    <div>
      <ToastNotification visible={showToast} message={translate('disasterApp.center.toast.addCenter')} />
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
        {centers.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Longitude</th>
                <th>Latitude</th>
                <th>Status</th>
                <th>Description</th>
                <th>Available Seats</th>
                <th>Open Time</th>
                <th>Close Time</th>
                <th>User</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {centers.map(center => (
                <tr key={center.id}>
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
                  <td>{center.user?.login}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/center/${center.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/center/${center.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`/center/${center.id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <div className="alert alert-warning">
            <Translate contentKey="disasterApp.center.home.notFound">No Centers found</Translate>
          </div>
        )}
      </div>
    </div>
  );
};
export default MyCenters;
