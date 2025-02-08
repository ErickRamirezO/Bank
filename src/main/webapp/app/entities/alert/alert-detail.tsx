import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './alert.reducer';

export const AlertDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const alertEntity = useAppSelector(state => state.alert.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="alertDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.alert.detail.title">Alert</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{alertEntity.id}</dd>
          <dt>
            <span id="alertType">
              <Translate contentKey="jhipsterSampleApplicationApp.alert.alertType">Alert Type</Translate>
            </span>
          </dt>
          <dd>{alertEntity.alertType}</dd>
          <dt>
            <span id="alertTime">
              <Translate contentKey="jhipsterSampleApplicationApp.alert.alertTime">Alert Time</Translate>
            </span>
          </dt>
          <dd>{alertEntity.alertTime ? <TextFormat value={alertEntity.alertTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="emailSent">
              <Translate contentKey="jhipsterSampleApplicationApp.alert.emailSent">Email Sent</Translate>
            </span>
          </dt>
          <dd>{alertEntity.emailSent ? 'true' : 'false'}</dd>
          <dt>
            <span id="details">
              <Translate contentKey="jhipsterSampleApplicationApp.alert.details">Details</Translate>
            </span>
          </dt>
          <dd>{alertEntity.details}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.alert.user">User</Translate>
          </dt>
          <dd>{alertEntity.user ? alertEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/alert" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/alert/${alertEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AlertDetail;
