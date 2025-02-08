import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './user-data.reducer';

export const UserData = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const userDataList = useAppSelector(state => state.userData.entities);
  const loading = useAppSelector(state => state.userData.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="user-data-heading" data-cy="UserDataHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.userData.home.title">User Data</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.userData.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/user-data/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.userData.home.createLabel">Create new User Data</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {userDataList && userDataList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('salary')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.salary">Salary</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('salary')} />
                </th>
                <th className="hand" onClick={sort('familyLoad')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.familyLoad">Family Load</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('familyLoad')} />
                </th>
                <th className="hand" onClick={sort('workplace')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.workplace">Workplace</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('workplace')} />
                </th>
                <th className="hand" onClick={sort('housingType')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.housingType">Housing Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('housingType')} />
                </th>
                <th className="hand" onClick={sort('rentCost')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.rentCost">Rent Cost</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rentCost')} />
                </th>
                <th className="hand" onClick={sort('yearsOfEmployment')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.yearsOfEmployment">Years Of Employment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('yearsOfEmployment')} />
                </th>
                <th className="hand" onClick={sort('employmentStatus')}>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.employmentStatus">Employment Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('employmentStatus')} />
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.userData.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userDataList.map((userData, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/user-data/${userData.id}`} color="link" size="sm">
                      {userData.id}
                    </Button>
                  </td>
                  <td>{userData.salary}</td>
                  <td>{userData.familyLoad}</td>
                  <td>{userData.workplace}</td>
                  <td>{userData.housingType}</td>
                  <td>{userData.rentCost}</td>
                  <td>{userData.yearsOfEmployment}</td>
                  <td>{userData.employmentStatus}</td>
                  <td>{userData.user ? userData.user.id : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/user-data/${userData.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/user-data/${userData.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/user-data/${userData.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="jhipsterSampleApplicationApp.userData.home.notFound">No User Data found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default UserData;
