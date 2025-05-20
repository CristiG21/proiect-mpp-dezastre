import './home.scss';

import React, { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Alert } from 'reactstrap';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
import axios from 'axios';
import * as turf from '@turf/turf';

import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';
import { ICenter } from 'app/shared/model/center.model';
import { IDisaster } from 'app/shared/model/disaster.model';

mapboxgl.accessToken = MAPBOX_KEY!;

export const Home = () => {
  const [error, setError] = useState<string | null>(null);
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [showFeed, setShowFeed] = useState(false);
  const [activeTab, setActiveTab] = useState<'community' | 'official'>('community');

  type CommunityMessage = {
    id: number;
    type: 'COMMUNITY';
    content: string;
    user: { login: string };
  };

  type OfficialMessage = {
    id: number;
    type: 'OFFICIAL';
    title: string;
    body: string;
    user: { login: string };
  };

  type Message = CommunityMessage | OfficialMessage;

  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    const toggleFeedListener = () => setShowFeed(prev => !prev);
    window.addEventListener('toggleFeed', toggleFeedListener);
    return () => window.removeEventListener('toggleFeed', toggleFeedListener);
  }, []);

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
              const circle = turf.circle([disaster.latitude, disaster.longitude], disaster.radius, {
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
                  'fill-color': 'red',
                  'fill-opacity': 0.3,
                },
              });

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
        setError('Error fetching disasters from the API.');
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

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const [communityRes, officialRes] = await Promise.all([
          axios.get('/api/community-messages', { params: { page: 0, size: 20 } }),
          axios.get('/api/official-messages', { params: { page: 0, size: 20 } }),
        ]);

        const communityMessages = Array.isArray(communityRes.data)
          ? communityRes.data.map((msg: any) => ({ ...msg, type: 'COMMUNITY' }))
          : Array.isArray(communityRes.data.content)
            ? communityRes.data.content.map((msg: any) => ({ ...msg, type: 'COMMUNITY' }))
            : [];

        const officialMessages = Array.isArray(officialRes.data)
          ? officialRes.data.map((msg: any) => ({ ...msg, type: 'OFFICIAL' }))
          : Array.isArray(officialRes.data.content)
            ? officialRes.data.content.map((msg: any) => ({ ...msg, type: 'OFFICIAL' }))
            : [];

        setMessages([...communityMessages, ...officialMessages]);
      } catch (err) {
        console.error('Failed to fetch messages:', err);
        setMessages([]);
      }
    };

    fetchMessages();
  }, []);

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
    button.onclick = () => {
      window.location.href = `/center/${centerId}`;
    };
    content.appendChild(button);

    return content;
  };

  return (
    <div style={{ position: 'relative' }}>
      {error && <Alert color="danger">{error}</Alert>}

      <div ref={mapContainerRef} style={{ width: '100%', height: '80vh', borderRadius: '12px' }} />

      <div className={`side-panel ${showFeed ? 'visible' : ''}`}>
        <h5 style={{ fontWeight: 'bold', marginBottom: '1rem' }}>News Feed</h5>

        <div className="tab-selector">
          <button className={activeTab === 'community' ? 'active' : ''} onClick={() => setActiveTab('community')}>
            Community
          </button>
          <button className={activeTab === 'official' ? 'active' : ''} onClick={() => setActiveTab('official')}>
            Official
          </button>
        </div>

        <div className="feed-list">
          {messages
            .filter(msg => msg.type === (activeTab === 'community' ? 'COMMUNITY' : 'OFFICIAL'))
            .map(msg => (
              <div key={msg.id} className="feed-item">
                {msg.type === 'COMMUNITY' ? (
                  <p>
                    <strong>@{msg.user?.login || 'unknown'}:</strong> {msg.content}
                  </p>
                ) : (
                  <>
                    <h6 style={{ fontWeight: 'bold' }}>{msg.title}</h6>
                    <p>{msg.body}</p>
                    <small className="text-muted">by @{msg.user?.login || 'unknown'}</small>
                  </>
                )}
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};

export default Home;
