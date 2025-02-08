import alert from 'app/entities/alert/alert.reducer';
import amortization from 'app/entities/amortization/amortization.reducer';
import loan from 'app/entities/loan/loan.reducer';
import log from 'app/entities/log/log.reducer';
import userData from 'app/entities/user-data/user-data.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  alert,
  amortization,
  loan,
  log,
  userData,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
