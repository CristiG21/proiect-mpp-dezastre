import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Center from './center';
import CenterDetail from './center-detail';
import CenterUpdate from './center-update';
import CenterDeleteDialog from './center-delete-dialog';

console.warn('CenterRoutes loaded');
const CenterRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Center />} />
    <Route path="new" element={<CenterUpdate />} />
    <Route path=":id">
      <Route index element={<CenterDetail />} />
      <Route path="edit" element={<CenterUpdate />} />
      <Route path="delete" element={<CenterDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CenterRoutes;
