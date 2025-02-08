import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './log.reducer';

export const LogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const logEntity = useAppSelector(state => state.log.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="logDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.log.detail.title">Log</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{logEntity.id}</dd>
          <dt>
            <span id="eventType">
              <Translate contentKey="jhipsterSampleApplicationApp.log.eventType">Event Type</Translate>
            </span>
          </dt>
          <dd>{logEntity.eventType}</dd>
          <dt>
            <span id="eventTime">
              <Translate contentKey="jhipsterSampleApplicationApp.log.eventTime">Event Time</Translate>
            </span>
          </dt>
          <dd>{logEntity.eventTime ? <TextFormat value={logEntity.eventTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="jhipsterSampleApplicationApp.log.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{logEntity.ipAddress}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="jhipsterSampleApplicationApp.log.description">Description</Translate>
            </span>
          </dt>
          <dd>{logEntity.description}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.log.user">User</Translate>
          </dt>
          <dd>{logEntity.user ? logEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/log/${logEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LogDetail;
