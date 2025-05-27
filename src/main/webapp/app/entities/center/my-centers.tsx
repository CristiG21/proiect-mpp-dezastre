import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { Button, Table } from 'reactstrap';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';

const MyCenters = () => {
  const [centers, setCenters] = useState<ICenter[]>([]);

  useEffect(() => {
    axios.get<ICenter[]>('/api/centers/my-centers').then(res => setCenters(res.data));
  }, []);

  return centers.length > 0 ? (
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
  );
};

export default MyCenters;
