import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Center from './center';
import CenterTypeWrapper from './center-type-wrapper';
import Disaster from './disaster';
import PhotoURL from './photo-url';
import CommunityMessage from './community-message';
import OfficialMessage from './official-message';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="center/*" element={<Center />} />
        <Route path="center-type-wrapper/*" element={<CenterTypeWrapper />} />
        <Route path="disaster/*" element={<Disaster />} />
        <Route path="community-message/*" element={<CommunityMessage />} />
        <Route path="photo-url/*" element={<PhotoURL />} />
        <Route path="official-message/*" element={<OfficialMessage />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
