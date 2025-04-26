import './home.scss';

import React, { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row } from 'reactstrap';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';

import { REDIRECT_URL, getLoginUrl } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';

mapboxgl.accessToken = MAPBOX_KEY!;

export const Home = () => {
  const [error, setError] = useState<string | null>(null);
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  }, []);

  useEffect(() => {
    if (!mapContainerRef.current) {
      setError('Map container is not available.');
      return;
    }

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v11',
      center: [25.0, 45.9432],
      zoom: 6,
    });

    // Fetch centers from API
    axios
      .get<ICenter[]>('/api/centers')
      .then(response => {
        console.error('API response:', response.data); // Log to check the actual response

        const centers = response.data; // The returned data is now a plain array

        if (Array.isArray(centers)) {
          centers.forEach(center => {
            if (center.longitude != null && center.latitude != null) {
              const popup = new mapboxgl.Popup({ offset: 25 }).setHTML(`<h4>${center.name}</h4>`);

              const marker = new mapboxgl.Marker().setLngLat([center.latitude, center.longitude]).setPopup(popup).addTo(map);
            } else {
              setError('Invalid center coordinates received from API.');
            }
          });
        } else {
          setError('API response is not an array.');
        }
      })
      .catch(fetchError => {
        console.error(fetchError);
        setError('Error fetching centers from the API.');
      });

    return () => map.remove(); // Cleanup map on unmount
  }, []);

  return (
    <div>
      {error && (
        <Alert color="danger">{error}</Alert> // ✅ show errors if any
      )}
      <div ref={mapContainerRef} style={{ width: '100%', height: '80vh', borderRadius: '12px' }} />
    </div>
  );
};

export default Home;
