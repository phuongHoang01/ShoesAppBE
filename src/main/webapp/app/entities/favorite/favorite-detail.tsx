import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './favorite.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FavoriteDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const favoriteEntity = useAppSelector(state => state.favorite.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="favoriteDetailsHeading">Favorite</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{favoriteEntity.id}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>
            {favoriteEntity.createdDate ? (
              <TextFormat value={favoriteEntity.createdDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>User</dt>
          <dd>{favoriteEntity.user ? favoriteEntity.user.login : ''}</dd>
          <dt>Product</dt>
          <dd>
            {favoriteEntity.products
              ? favoriteEntity.products.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {favoriteEntity.products && i === favoriteEntity.products.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/favorite" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/favorite/${favoriteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default FavoriteDetail;
