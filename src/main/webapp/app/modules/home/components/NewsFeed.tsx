import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { parseLinkHeader } from '../utils/mapUtils';
import { useAppSelector } from 'app/config/store';
import { IOfficialMessage } from 'app/shared/model/official-message.model';
import { getLoginUrl } from 'app/shared/util/url-utils';
import { useLocation, useNavigate } from 'react-router-dom';
import { translate } from 'react-jhipster';

interface NewsFeedProps {
  showFeed: boolean;
  setShowToast: (visible: boolean) => void;
  setToastMessage: (message: string) => void;
  setSelectedMessage: (msg: IOfficialMessage) => void;
}

const NewsFeed: React.FC<NewsFeedProps> = ({ showFeed, setShowToast, setToastMessage, setSelectedMessage }) => {
  const [messages, setMessages] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loadingPage, setLoadingPage] = useState(false);
  const [activeTab, setActiveTab] = useState<'community' | 'official'>('community');
  const [newMessageContent, setNewMessageContent] = useState('');
  const [showNewMessageInput, setShowNewMessageInput] = useState(false);

  const navigate = useNavigate();
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const location = useLocation();

  useEffect(() => {
    setPage(0);
  }, [activeTab]);

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
            params: { page, size: 2 },
          });

          let officialMessages: any[] = [];

          if (Array.isArray(res.data.content)) {
            officialMessages = res.data.content;
          } else if (Array.isArray(res.data)) {
            officialMessages = res.data;
          } else {
            officialMessages = [];
          }

          const mapped = officialMessages.map(msg => ({ ...msg, type: 'OFFICIAL' }));
          setMessages(mapped);

          if (res.data.totalPages !== undefined) {
            setHasMore(page + 1 < res.data.totalPages);
          } else if (res.data.last !== undefined) {
            setHasMore(!res.data.last);
          } else {
            // fallback logic: assume no more if response is not paged
            setHasMore(officialMessages.length === 1); // assumes 1-per-page
          }
        }
      } catch (err) {
        console.error('Failed to fetch messages:', err);
        setMessages([]);
        setHasMore(false);
      } finally {
        setLoadingPage(false);
      }
    };

    fetchMessages();
  }, [page, activeTab]);

  const postCommunityMessage = async () => {
    const content = newMessageContent.trim();
    if (!content) return;

    const isAdmin = account?.authorities?.includes('ROLE_ADMIN');

    try {
      const response = await axios.post('/api/community-messages', {
        content,
        time_posted: new Date().toISOString(),
        approved: isAdmin,
        parent: null,
        user: { id: account.id, login: account.login },
        replies: [],
      });

      const newMessage = response.data;

      setNewMessageContent('');
      setShowNewMessageInput(false);

      if (isAdmin) {
        const enrichedMessage = {
          ...newMessage,
          type: 'COMMUNITY',
          replies: [],
          showReplyInput: false,
          replyDraft: '',
        };
        setMessages(prev => [enrichedMessage, ...prev]);
      } else {
        setToastMessage(translate('disasterApp.center.toast.message'));
        setShowToast(true);
      }
    } catch (error) {
      console.error('Post error:', error);
    }
  };

  const postReply = async (msgId: number, content: string) => {
    if (!content) return;

    const isAdmin = account?.authorities?.includes('ROLE_ADMIN');

    try {
      const response = await axios.post('/api/community-messages', {
        content,
        time_posted: new Date().toISOString(),
        approved: isAdmin,
        parent: { id: msgId },
        user: { id: account.id, login: account.login },
        replies: [],
      });

      const newReply = response.data;

      setMessages(prev =>
        prev.map(m =>
          m.id === msgId
            ? {
                ...m,
                showReplyInput: false,
                replyDraft: '',
                replies: isAdmin ? [...(m.replies || []), newReply] : m.replies,
              }
            : m,
        ),
      );

      if (!isAdmin) {
        setToastMessage(translate('disasterApp.center.toast.reply'));
        setShowToast(true);
      }
    } catch (error) {
      console.error('Reply error:', error);
    }
  };

  return (
    <div className={`side-panel ${showFeed ? 'visible' : ''}`}>
      <h5>{translate('disasterApp.center.feed.title')}</h5>

      <div className="tab-selector">
        <button className={activeTab === 'community' ? 'active' : ''} onClick={() => setActiveTab('community')}>
          {translate('disasterApp.center.feed.community')}
        </button>
        <button className={activeTab === 'official' ? 'active' : ''} onClick={() => setActiveTab('official')}>
          {translate('disasterApp.center.feed.official')}
        </button>
      </div>
      {activeTab === 'official' && account?.authorities?.includes('ROLE_ADMIN') && (
        <div className="new-message-wrapper">
          <button className="new-message-btn" onClick={() => navigate('official-message/new')}>
            + {translate('disasterApp.center.feed.newOfficial')}
          </button>
        </div>
      )}

      {activeTab === 'community' && !showNewMessageInput && (
        <div className="new-message-wrapper">
          <button
            className="new-message-btn"
            onClick={() => {
              if (!isAuthenticated) {
                navigate(getLoginUrl(), { state: { from: location } });
              } else {
                setShowNewMessageInput(true);
              }
            }}
          >
            + {translate('disasterApp.center.feed.newMessage')}
          </button>
        </div>
      )}

      {activeTab === 'community' && showNewMessageInput && (
        <div className="new-message-box">
          <textarea value={newMessageContent} onChange={e => setNewMessageContent(e.target.value)} placeholder="What's on your mind?" />
          <button onClick={postCommunityMessage}>Post</button>
        </div>
      )}

      <div className="feed-list">
        {messages.map(msg => (
          <div key={msg.id} className="feed-item">
            {msg.type === 'COMMUNITY' ? (
              <>
                <p>
                  <strong>@{msg.user?.login || 'unknown'}:</strong> {msg.content}
                </p>
                <div className="message-footer">
                  <button
                    className="reply-btn"
                    onClick={() => {
                      if (!isAuthenticated) {
                        navigate(getLoginUrl(), { state: { from: location } });
                      } else {
                        setMessages(prev => prev.map(m => (m.id === msg.id ? { ...m, showReplyInput: !m.showReplyInput } : m)));
                      }
                    }}
                  >
                    💬 {translate('disasterApp.center.feed.reply')}
                  </button>
                </div>
                {msg.showReplyInput && (
                  <div className="reply-input">
                    <input
                      type="text"
                      value={msg.replyDraft || ''}
                      onChange={e =>
                        setMessages(prev =>
                          prev.map(m =>
                            m.id === msg.id
                              ? {
                                  ...m,
                                  replyDraft: e.target.value,
                                }
                              : m,
                          ),
                        )
                      }
                    />
                    <button onClick={() => postReply(msg.id, msg.replyDraft)}>Send</button>
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
              <div onClick={() => setSelectedMessage(msg)} style={{ cursor: 'pointer' }}>
                <h6>{msg.title}</h6>
                <p>{msg.body}</p>
                <small>by @{msg.user?.login || 'unknown'}</small>
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="pagination-controls">
        <button disabled={page === 0 || loadingPage} onClick={() => setPage(p => p - 1)}>
          ◀ {translate('disasterApp.center.feed.previous')}
        </button>
        <button disabled={!hasMore || loadingPage} onClick={() => setPage(p => p + 1)}>
          {translate('disasterApp.center.feed.next')} ▶
        </button>
      </div>
    </div>
  );
};

export default NewsFeed;
