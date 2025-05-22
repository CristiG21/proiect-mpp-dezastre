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

  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);

  const showNotification = (message: string) => {
    setToastMessage(message);
    setShowToast(false); // ensure reset before animation
    setTimeout(() => {
      setShowToast(true); // triggers transition in
    }, 50); // slight delay lets the "hidden" style render first

    // auto-hide after 3s
    setTimeout(() => setShowToast(false), 3050);
  };

  const [newMessageContent, setNewMessageContent] = useState('');
  const [showNewMessageInput, setShowNewMessageInput] = useState(false);

  const [showFeed, setShowFeed] = useState(false);
  const [activeTab, setActiveTab] = useState<'community' | 'official'>('community');

  const [messages, setMessages] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const [loadingPage, setLoadingPage] = useState(false);

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

              new mapboxgl.Marker().setLngLat([center.longitude, center.latitude]).setPopup(popup).addTo(map);
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
      setLoadingPage(true);
      try {
        if (activeTab === 'community') {
          const res = await axios.get('/api/community-messages/paged', {
            params: { page, size: 2, onlyTopLevel: true },
          });

          const rawMessages = Array.isArray(res.data.content) ? res.data.content : [];

          const sortedTopLevel = rawMessages
            .filter(msg => msg.parent === null && msg.approved === true)
            .sort((a, b) => new Date(b.time_posted).getTime() - new Date(a.time_posted).getTime())
            .map(msg => {
              const sortedReplies = (msg.replies || []).sort(
                (a, b) => new Date(b.time_posted).getTime() - new Date(a.time_posted).getTime(),
              );
              return { ...msg, type: 'COMMUNITY', replies: sortedReplies };
            });

          setMessages(sortedTopLevel);
          setHasMore(!res.data.last);
        }

        if (activeTab === 'official') {
          const res = await axios.get('/api/official-messages', {
            params: { page: 0, size: 20 },
          });

          const officialMessages = Array.isArray(res.data.content) ? res.data.content : Array.isArray(res.data) ? res.data : [];

          const mapped = officialMessages.map(msg => ({ ...msg, type: 'OFFICIAL' }));
          setMessages(mapped);
          setHasMore(false); // official messages aren't paginated
        }
      } catch (err) {
        console.error('Failed to fetch messages:', err);
        setMessages([]);
      } finally {
        setLoadingPage(false);
      }
    };

    fetchMessages();
  }, [page, activeTab]);

  useEffect(() => {
    setPage(0);
  }, [activeTab]);

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
      window.location.href = `/center/detailing/${centerId}`;
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
        {activeTab === 'community' && (
          <div className="new-message-box">
            {showNewMessageInput ? (
              <>
                <textarea
                  value={newMessageContent}
                  onChange={e => setNewMessageContent(e.target.value)}
                  placeholder="What's on your mind?"
                />
                <button
                  onClick={async () => {
                    const content = newMessageContent.trim();
                    if (!content) return;
                    try {
                      const response = await axios.post('/api/community-messages', {
                        content,
                        time_posted: new Date().toISOString(),
                        approved: false,
                        parent: null,
                        user: {
                          id: account.id,
                          login: account.login,
                        },
                        replies: [],
                      });
                      const newMsg = { ...response.data, type: 'COMMUNITY', replies: [] };
                      setNewMessageContent('');
                      setShowNewMessageInput(false);
                      showNotification('✅ Thanks! Your reply will appear once approved.');
                      setNewMessageContent('');
                      setShowNewMessageInput(false);
                    } catch (err) {
                      console.error('Error posting message', err);
                    }
                  }}
                >
                  Post
                </button>
              </>
            ) : (
              <button onClick={() => setShowNewMessageInput(true)}>+ New Message</button>
            )}
          </div>
        )}

        <div className="feed-list">
          {messages
            .filter(msg => msg.type === (activeTab === 'community' ? 'COMMUNITY' : 'OFFICIAL'))
            .map(msg => (
              <div key={msg.id} className="feed-item">
                {msg.type === 'COMMUNITY' ? (
                  <>
                    <p>
                      <strong>@{msg.user?.login || 'unknown'}:</strong> {msg.content}
                    </p>
                    <div className="message-footer" style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                      <button
                        className="reply-btn"
                        onClick={() => {
                          if (!account?.login) {
                            navigate('/login');
                          } else {
                            setMessages(prev =>
                              prev.map(m =>
                                m.id === msg.id
                                  ? {
                                      ...m,
                                      showReplyInput: !m.showReplyInput,
                                    }
                                  : m,
                              ),
                            );
                          }
                        }}
                      >
                        💬 Reply
                      </button>
                    </div>
                    {msg.showReplyInput && (
                      <div className="reply-input">
                        <input
                          type="text"
                          value={msg.replyDraft || ''}
                          placeholder="Write your reply..."
                          onChange={e => {
                            const val = e.target.value;
                            setMessages(prev => prev.map(m => (m.id === msg.id ? { ...m, replyDraft: val } : m)));
                          }}
                        />
                        <button
                          onClick={async () => {
                            const content = msg.replyDraft?.trim();
                            if (!content) return;
                            try {
                              const response = await axios.post('/api/community-messages', {
                                content,
                                time_posted: new Date().toISOString(),
                                approved: false,
                                parent: { id: msg.id }, // ✅ FIXED: wrap it in an object
                                user: {
                                  id: account.id,
                                  login: account.login,
                                },
                                replies: [],
                              });
                              const newReply = response.data;

                              if (newReply.approved) {
                                setMessages(prev =>
                                  prev.map(m =>
                                    m.id === msg.id
                                      ? {
                                          ...m,
                                          showReplyInput: false,
                                          replyDraft: '',
                                          replies: [...(m.replies || []), newReply],
                                        }
                                      : m,
                                  ),
                                );
                              } else {
                                // Hide input and show alert/notification
                                setMessages(prev =>
                                  prev.map(m =>
                                    m.id === msg.id
                                      ? {
                                          ...m,
                                          showReplyInput: false,
                                          replyDraft: '',
                                        }
                                      : m,
                                  ),
                                );
                                showNotification('✅ Thanks! Your reply will appear once approved.');
                              }
                            } catch (err) {
                              console.error('Error posting reply', err);
                            }
                          }}
                        >
                          Send
                        </button>
                      </div>
                    )}

                    {msg.replies?.length > 0 && (
                      <div className="replies">
                        {msg.replies.map(reply => (
                          <div key={reply.id} className="reply-item">
                            <small>
                              <strong>@{reply.user?.login || 'unknown'}:</strong> {reply.content}
                            </small>
                          </div>
                        ))}
                      </div>
                    )}
                  </>
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

        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            gap: '1rem',
            marginTop: '1rem',
          }}
        >
          <button
            onClick={() => setPage(p => p - 1)}
            disabled={page === 0 || loadingPage}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              border: '1px solid #ccc',
              backgroundColor: page === 0 || loadingPage ? '#eee' : '#007bff',
              color: page === 0 || loadingPage ? '#999' : 'white',
              cursor: page === 0 || loadingPage ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s ease',
            }}
          >
            ◀ Previous
          </button>
          <button
            onClick={() => setPage(p => p + 1)}
            disabled={!hasMore || loadingPage}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              border: '1px solid #ccc',
              backgroundColor: !hasMore || loadingPage ? '#eee' : '#007bff',
              color: !hasMore || loadingPage ? '#999' : 'white',
              cursor: !hasMore || loadingPage ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s ease',
            }}
          >
            Next ▶
          </button>
        </div>

        <div
          style={{
            position: 'fixed',
            bottom: '1.5rem',
            right: '1.5rem',
            background: 'rgba(0,0,0,0.85)',
            color: 'white',
            padding: '1rem 1.5rem',
            borderRadius: '8px',
            zIndex: 9999,
            boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
            opacity: showToast ? 1 : 0,
            transform: showToast ? 'translateY(0)' : 'translateY(20px)',
            transition: 'opacity 0.4s ease, transform 0.4s ease',
            pointerEvents: 'none',
            visibility: toastMessage ? 'visible' : 'hidden',
          }}
        >
          {toastMessage}
        </div>
      </div>
    </div>
  );
};

export default Home;
