import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Disaster from './disaster';
import DisasterDetail from './disaster-detail';
import DisasterUpdate from './disaster-update';
import DisasterDeleteDialog from './disaster-delete-dialog';

const DisasterRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Disaster />} />
    <Route path="new" element={<DisasterUpdate />} />
    <Route path=":id">
      <Route index element={<DisasterDetail />} />
      <Route path="edit" element={<DisasterUpdate />} />
      <Route path="delete" element={<DisasterDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DisasterRoutes;
