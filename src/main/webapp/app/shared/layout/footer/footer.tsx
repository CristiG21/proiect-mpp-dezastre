import './footer.scss';

import React from 'react';
import { Col, Row } from 'reactstrap';
// Adjust the path as necessary

const Footer = () => (
  <div className="footer page-content text-center py-4">
    <Row>
      <Col md="12">
        <img src="content/images/BB-logo.png" alt="BlackBox Logo" style={{ width: '256px', marginBottom: '10px' }} />
        <p className="footer-description mt-2">
          <strong>BlackBox</strong> is a modern disaster response platform designed for rapid coordination and community resilience.
          Visualize real-time disaster data across Romania, contribute vital help centers, and stay connected through a live communication
          feed — all in one unified space.
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
