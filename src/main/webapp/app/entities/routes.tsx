import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Alert from './alert';
import Amortization from './amortization';
import Loan from './loan';
import Log from './log';
import UserData from './user-data';
import { Route } from 'react-router';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="amortization/*" element={<Amortization />} />
        <Route path="loan/*" element={<Loan />} />
        <Route path="user-data/*" element={<UserData />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
