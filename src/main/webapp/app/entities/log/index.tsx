import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Log from './log';
import LogDetail from './log-detail';
import LogUpdate from './log-update';
import LogDeleteDialog from './log-delete-dialog';

const LogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Log />} />
    <Route path="new" element={<LogUpdate />} />
    <Route path=":id">
      <Route index element={<LogDetail />} />
      <Route path="edit" element={<LogUpdate />} />
      <Route path="delete" element={<LogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LogRoutes;
