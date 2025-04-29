import './home.scss';

import React, { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert } from 'reactstrap';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';

import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { IDisaster } from 'app/shared/model/disaster.model';
import * as turf from '@turf/turf';
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

    axios
      .get<IDisaster[]>('/api/disasters')
      .then(response => {
        const disasters = response.data;

        if (Array.isArray(disasters)) {
          disasters.forEach((disaster, index) => {
            if (disaster.longitude != null && disaster.latitude != null) {
              // Create a 10km radius circle using Turf.js
              const circle = turf.circle([disaster.latitude, disaster.longitude], disaster.radius, {
                steps: 64,
                units: 'kilometers',
              });

              // Add circle as a source
              map.addSource(`circle-source-${index}`, {
                type: 'geojson',
                data: circle,
              });

              // Add circle as a fill layer
              map.addLayer({
                id: `circle-fill-${index}`,
                type: 'fill',
                source: `circle-source-${index}`,
                paint: {
                  'fill-color': 'red',
                  'fill-opacity': 0.3,
                },
              });

              // Optional: add a circle border (outline)
              map.addLayer({
                id: `circle-outline-${index}`,
                type: 'line',
                source: `circle-source-${index}`,
                paint: {
                  'line-color': 'red',
                  'line-width': 2,
                },
              });
            } else {
              console.error('Invalid center coordinates:', disaster);
              setError('Invalid center coordinates received from API.');
            }
          });
        } else {
          setError('API response is not an array.');
          console.error(disasters);
        }
      })
      .catch(fetchError => {
        console.error(fetchError);
        setError('Error fetching centers from the API.');
      });

    axios
      .get<ICenter[]>('/api/centers')
      .then(response => {
        const centers = response.data;

        if (Array.isArray(centers)) {
          centers.forEach((center, index) => {
            if (center.longitude != null && center.latitude != null) {
              const popup = new mapboxgl.Popup({
                offset: 40,
                anchor: 'bottom',
                closeOnMove: false,
                closeButton: true,
              }).setDOMContent(createPopupContent(center.name, center.id));

              // Create the marker
              const marker = new mapboxgl.Marker().setLngLat([center.latitude, center.longitude]).setPopup(popup).addTo(map);
            } else {
              console.error('Invalid center coordinates:', center);
              setError('Invalid center coordinates received from API.');
            }
          });
        } else {
          setError('API response is not an array.');
          console.error(centers);
        }
      })
      .catch(fetchError => {
        console.error(fetchError);
        setError('Error fetching centers from the API.');
      });

    return () => map.remove();
  }, []);

  // Create popup content with custom styling
  const createPopupContent = (name: string, centerId: number): HTMLElement => {
    const content = document.createElement('div');
    content.classList.add('popup-content');

    const textWrapper = document.createElement('div');
    textWrapper.classList.add('text-wrapper');
    textWrapper.innerText = name;
    content.appendChild(textWrapper);

    const button = document.createElement('button');
    button.innerText = 'View Details';
    button.classList.add('popup-button');

    // When clicking the button, navigate to the details page
    button.onclick = () => {
      window.location.href = `/center/${centerId}`;
    };

    content.appendChild(button);

    return content;
  };

  return (
    <div>
      {error && <Alert color="danger">{error}</Alert>}
      <div ref={mapContainerRef} style={{ width: '100%', height: '80vh', borderRadius: '12px' }} />
    </div>
  );
};

export default Home;
