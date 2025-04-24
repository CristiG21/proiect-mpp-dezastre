import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CenterTypeWrapper from './center-type-wrapper';
import CenterTypeWrapperDetail from './center-type-wrapper-detail';
import CenterTypeWrapperUpdate from './center-type-wrapper-update';
import CenterTypeWrapperDeleteDialog from './center-type-wrapper-delete-dialog';

const CenterTypeWrapperRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CenterTypeWrapper />} />
    <Route path="new" element={<CenterTypeWrapperUpdate />} />
    <Route path=":id">
      <Route index element={<CenterTypeWrapperDetail />} />
      <Route path="edit" element={<CenterTypeWrapperUpdate />} />
      <Route path="delete" element={<CenterTypeWrapperDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CenterTypeWrapperRoutes;
