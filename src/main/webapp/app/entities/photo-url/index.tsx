import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PhotoURL from './photo-url';
import PhotoURLDetail from './photo-url-detail';
import PhotoURLUpdate from './photo-url-update';
import PhotoURLDeleteDialog from './photo-url-delete-dialog';

const PhotoURLRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PhotoURL />} />
    <Route path="new" element={<PhotoURLUpdate />} />
    <Route path=":id">
      <Route index element={<PhotoURLDetail />} />
      <Route path="edit" element={<PhotoURLUpdate />} />
      <Route path="delete" element={<PhotoURLDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PhotoURLRoutes;
