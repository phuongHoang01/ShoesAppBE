import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './product.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProductDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const productEntity = useAppSelector(state => state.product.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productDetailsHeading">Product</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{productEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{productEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{productEntity.description}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{productEntity.price}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>{productEntity.image}</dd>
          <dt>
            <span id="productSize">Product Size</span>
          </dt>
          <dd>{productEntity.productSize}</dd>
          <dt>
            <span id="color">Color</span>
          </dt>
          <dd>{productEntity.color}</dd>
          <dt>
            <span id="quantity">Quantity</span>
          </dt>
          <dd>{productEntity.quantity}</dd>
          <dt>Category</dt>
          <dd>{productEntity.category ? productEntity.category.name : ''}</dd>
          <dt>Size</dt>
          <dd>
            {productEntity.sizes
              ? productEntity.sizes.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {productEntity.sizes && i === productEntity.sizes.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product/${productEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductDetail;
