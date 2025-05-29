import '../map.scss';
import React, { useEffect, useRef } from 'react';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import axios from 'axios';
import * as turf from '@turf/turf';
import { ICenter } from 'app/shared/model/center.model';
import { IDisaster } from 'app/shared/model/disaster.model';
import { createPopupContent } from '../utils/mapUtils';

mapboxgl.accessToken = MAPBOX_KEY!;

const MapComponent: React.FC<{ setError: (msg: string) => void }> = ({ setError }) => {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!mapContainerRef.current) {
      setError('Map container is not available.');
      return;
    }

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v11',
      center: [25.0, 45.9432],
      zoom: 6.5,
    });

    axios
      .get<ICenter[]>('/api/centers')
      .then(response => {
        const centers = response.data;

        if (Array.isArray(centers)) {
          centers
            .filter(center => center.status === true)
            .forEach((center, index) => {
              if (center.longitude != null && center.latitude != null) {
                const popup = new mapboxgl.Popup({
                  offset: 40,
                  anchor: 'bottom',
                  closeOnMove: false,
                  closeButton: true,
                }).setDOMContent(createPopupContent(center.name, center.id));

                new mapboxgl.Marker().setLngLat([center.longitude, center.latitude]).setPopup(popup).addTo(map);

                popup.on('open', () => {
                  map.flyTo({
                    center: [center.longitude, center.latitude],
                    zoom: 10,
                    speed: 1.5,
                    curve: 1,
                  });
                });
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

    axios.get<IDisaster[]>('/api/disasters').then(response => {
      const disasters = response.data;

      map.once('style.load', () => {
        if (Array.isArray(disasters)) {
          disasters.forEach((disaster, index) => {
            if (disaster.longitude != null && disaster.latitude != null) {
              const disasterColorMap = {
                MINOR: '#ffeaa7',
                MODERAT: '#fdcb6e',
                MAJOR: '#ff922b',
                CATASTROFAL: '#ff4d4f',
              };
              const color = disasterColorMap[disaster.type] || '#6c757d';

              const circle = turf.circle([disaster.longitude, disaster.latitude], disaster.radius, {
                steps: 64,
                units: 'kilometers',
              });

              map.addSource(`circle-source-${index}`, {
                type: 'geojson',
                data: circle,
              });

              map.addLayer({
                id: `circle-fill-${index}`,
                type: 'fill',
                source: `circle-source-${index}`,
                paint: {
                  'fill-color': color,
                  'fill-opacity': 0.3,
                },
              });

              map.addLayer({
                id: `circle-outline-${index}`,
                type: 'line',
                source: `circle-source-${index}`,
                paint: {
                  'line-color': color,
                  'line-width': 2,
                },
              });
            } else {
              console.error('Invalid center coordinates:', disaster);
              setError('Invalid center coordinates received from API.');
            }
          });
        }
      });
    });

    return () => map.remove();
  }, []);

  return <div ref={mapContainerRef} style={{ width: '100%', height: '80vh', borderRadius: '12px' }} />;
};

export default MapComponent;
