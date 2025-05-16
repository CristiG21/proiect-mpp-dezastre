import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CommunityMessage from './community-message';
import CommunityMessageDetail from './community-message-detail';
import CommunityMessageUpdate from './community-message-update';
import CommunityMessageDeleteDialog from './community-message-delete-dialog';

const CommunityMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CommunityMessage />} />
    <Route path="new" element={<CommunityMessageUpdate />} />
    <Route path=":id">
      <Route index element={<CommunityMessageDetail />} />
      <Route path="edit" element={<CommunityMessageUpdate />} />
      <Route path="delete" element={<CommunityMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CommunityMessageRoutes;
