import './home.scss';
import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Alert } from 'reactstrap';
import MapComponent from '../home/components/MapComponent';
import NewsFeed from '../home/components/NewsFeed';
import ToastNotification from '../home/components/ToastNotification';
import MessageModal from '../home/components/MessageModal';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { IOfficialMessage } from 'app/shared/model/official-message.model';

const Home = () => {
  const [error, setError] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [selectedMessage, setSelectedMessage] = useState<IOfficialMessage | null>(null);
  const [showFeed, setShowFeed] = useState(true);
  const pageLocation = useLocation();

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  }, []);

  useEffect(() => {
    const toggleFeedListener = () => setShowFeed(prev => !prev);
    window.addEventListener('toggleFeed', toggleFeedListener);
    return () => window.removeEventListener('toggleFeed', toggleFeedListener);
  }, []);

  return (
    <div style={{ position: 'relative' }}>
      {error && <Alert color="danger">{error}</Alert>}
      <MapComponent setError={setError} />
      <NewsFeed
        showFeed={!showFeed}
        setShowToast={setShowToast}
        setToastMessage={setToastMessage}
        setSelectedMessage={setSelectedMessage}
      />

      <ToastNotification message={toastMessage} visible={showToast} />
      {selectedMessage && <MessageModal message={selectedMessage} onClose={() => setSelectedMessage(null)} />}
    </div>
  );
};

export default Home;
