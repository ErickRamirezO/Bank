import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { createEntity, getEntity, reset, updateEntity } from './loan.reducer';

export const LoanUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const loanEntity = useAppSelector(state => state.loan.entity);
  const loading = useAppSelector(state => state.loan.loading);
  const updating = useAppSelector(state => state.loan.updating);
  const updateSuccess = useAppSelector(state => state.loan.updateSuccess);
  const currentUserIsAdmin = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN])
  );

  const handleClose = () => {
    navigate(`/loan${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.requestedAmount !== undefined && typeof values.requestedAmount !== 'number') {
      values.requestedAmount = Number(values.requestedAmount);
    }
    if (values.interestRate !== undefined && typeof values.interestRate !== 'number') {
      values.interestRate = Number(values.interestRate);
    }
    if (values.paymentTermMonths !== undefined && typeof values.paymentTermMonths !== 'number') {
      values.paymentTermMonths = Number(values.paymentTermMonths);
    }

    const entity = {
      ...loanEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
    };

    if (!currentUserIsAdmin) {
      entity.status = loanEntity.status; // Mantiene el estado actual sin cambios
    }

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...loanEntity,
          user: loanEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.prestamos.home.createOrEditLabel" data-cy="LoanCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.prestamos.home.createOrEditLabel">Create or edit a Loan</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="loan-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.requestedAmount')}
                id="loan-requestedAmount"
                name="requestedAmount"
                data-cy="requestedAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.interestRate')}
                id="loan-interestRate"
                name="interestRate"
                data-cy="interestRate"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.paymentTermMonths')}
                id="loan-paymentTermMonths"
                name="paymentTermMonths"
                data-cy="paymentTermMonths"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.prestamos.applicationDate')}
                id="loan-applicationDate"
                name="applicationDate"
                data-cy="applicationDate"
                type="date"
              />
              {currentUserIsAdmin ? (
                <ValidatedField
                  label={translate('jhipsterSampleApplicationApp.prestamos.status')}
                  id="loan-status"
                  name="status"
                  data-cy="status"
                  type="select"
                >
                  <option value="0">Pendiente</option>
                  <option value="1">Aprobado</option>
                  <option value="2">Rechazado</option>
                  <option value="3">Pagado</option>
                  <option value="4">En mora</option>
                </ValidatedField>
              ) : (
                <input type="hidden" name="status" value={defaultValues().status} />
              )}
              <ValidatedField
                id="loan-user"
                name="user"
                data-cy="user"
                label={translate('jhipsterSampleApplicationApp.prestamos.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/loan" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default LoanUpdate;
