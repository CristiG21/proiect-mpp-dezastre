import './home.scss';

import React, { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row } from 'reactstrap';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';

import { REDIRECT_URL, getLoginUrl } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';

mapboxgl.accessToken = MAPBOX_KEY!;

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const mapContainerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  }, []);

  useEffect(() => {
    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v11',
      center: [25.0, 45.9432],
      zoom: 6,
    });

    new mapboxgl.Marker().setLngLat([21.3123, 46.1866]).addTo(map);

    return () => map.remove();
  }, []);

  return (
    <div>
      <div ref={mapContainerRef} style={{ width: '100%', height: '80vh', borderRadius: '12px' }} />
    </div>
  );
};

export default Home;
