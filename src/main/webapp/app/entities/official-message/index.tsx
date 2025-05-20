import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OfficialMessage from './official-message';
import OfficialMessageDetail from './official-message-detail';
import OfficialMessageUpdate from './official-message-update';
import OfficialMessageDeleteDialog from './official-message-delete-dialog';

const OfficialMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OfficialMessage />} />
    <Route path="new" element={<OfficialMessageUpdate />} />
    <Route path=":id">
      <Route index element={<OfficialMessageDetail />} />
      <Route path="edit" element={<OfficialMessageUpdate />} />
      <Route path="delete" element={<OfficialMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfficialMessageRoutes;
