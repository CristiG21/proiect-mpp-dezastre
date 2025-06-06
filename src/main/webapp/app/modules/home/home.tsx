import './home.scss';
import './filter.scss';
import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Alert } from 'reactstrap';
import MapComponent from '../home/components/MapComponent';
import NewsFeed from '../home/components/NewsFeed';
import ToastNotification from '../home/components/ToastNotification';
import MessageModal from '../home/components/MessageModal';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { IOfficialMessage } from 'app/shared/model/official-message.model';
import { useAppSelector } from 'app/config/store';
import axios from 'axios';
import { translate } from 'react-jhipster';
import { townsByCounty } from 'app/shared/constants/townsByCounty';

const Home = () => {
  const [error, setError] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [selectedMessage, setSelectedMessage] = useState<IOfficialMessage | null>(null);
  const [showFeed, setShowFeed] = useState(true);
  const pageLocation = useLocation();
  const [showFilterPanel, setShowFilterPanel] = useState(false);
  const [filterAnimating, setFilterAnimating] = useState(false);

  const [selectedCounty, setSelectedCounty] = useState('');
  const [selectedTown, setSelectedTown] = useState('');
  const [pendingCenters, setPendingCenters] = useState<number | null>(null);
  const [bannerDismissed, setBannerDismissed] = useState(false);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  }, []);

  const handleFindCity = () => {
    if (!selectedCounty || !selectedTown) {
      alert('Please select both a county and a city.');
      return;
    }

    const countyTowns = townsByCounty[selectedCounty];
    if (!countyTowns) {
      alert('Invalid county selected.');
      return;
    }

    const townIndex = parseInt(selectedTown, 10);
    const town = countyTowns[townIndex];

    if (!town) {
      alert('Invalid city selected.');
      return;
    }

    // Parse and validate latitude and longitude
    const lat = parseFloat(town.lat);
    const lng = parseFloat(town.lng);

    if (isNaN(lat) || isNaN(lng)) {
      console.error('Invalid town coordinates:', town);
      alert('Selected city has invalid or missing coordinates.');
      return;
    }

    // Dispatch the event with valid coordinates
    const event = new CustomEvent('goToTown', {
      detail: {
        lat,
        lng,
      },
    });

    window.dispatchEvent(event);
  };

  useEffect(() => {
    const toggle = () => {
      if (showFilterPanel) {
        setFilterAnimating(true);
        setShowFilterPanel(false);
        setTimeout(() => setFilterAnimating(false), 300); // Match SCSS animation duration
      } else {
        setShowFilterPanel(true);
      }
    };
    window.addEventListener('toggleFilterPanel', toggle);
    return () => window.removeEventListener('toggleFilterPanel', toggle);
  }, [showFilterPanel]);

  useEffect(() => {
    const toggleFeedListener = () => setShowFeed(prev => !prev);
    window.addEventListener('toggleFeed', toggleFeedListener);
    return () => window.removeEventListener('toggleFeed', toggleFeedListener);
  }, []);

  const account = useAppSelector(state => state.authentication.account);

  useEffect(() => {
    const fetchPendingCenters = async () => {
      if (account?.authorities?.includes('ROLE_ADMIN')) {
        try {
          const res = await axios.get('/api/centers');
          const pending = res.data.filter(center => center.status === false);
          const count = Array.isArray(pending) ? pending.length : 0;
          if (count > 0) setPendingCenters(count);
        } catch (err) {
          console.error('Error fetching pending centers:', err);
        }
      }
    };

    fetchPendingCenters();
  }, [account]);

  return (
    <div style={{ position: 'relative' }}>
      {pendingCenters !== null && pendingCenters > 0 && !bannerDismissed && (
        <div
          style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            zIndex: 9999,
            backgroundColor: '#fff3cd',
            color: '#856404',
            border: '1px solid #ffeeba',
            padding: '2rem 3rem',
            borderRadius: '12px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
            textAlign: 'center',
            minWidth: '350px',
          }}
        >
          <button
            onClick={() => setBannerDismissed(true)}
            style={{
              position: 'absolute',
              top: '0.5rem',
              right: '0.75rem',
              background: 'transparent',
              border: 'none',
              fontSize: '1.25rem',
              fontWeight: 'bold',
              color: '#856404',
              cursor: 'pointer',
            }}
            aria-label="Close"
          >
            ×
          </button>
          <h4>
            ⚠️ {pendingCenters} {translate('disasterApp.center.toast.header')}
          </h4>
          <p>{translate('disasterApp.center.toast.paragraph')}</p>
          <button
            onClick={() => (window.location.href = '/center')}
            style={{
              marginTop: '1rem',
              padding: '0.5rem 1.25rem',
              backgroundColor: '#856404',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              cursor: 'pointer',
              fontWeight: 'bold',
            }}
          >
            {translate('disasterApp.center.toast.button')}
          </button>
        </div>
      )}
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
      {(showFilterPanel || filterAnimating) && (
        <div className={`filter-panel-overlay ${showFilterPanel ? 'slide-in' : 'slide-out'}`}>
          <div className="filter-panel-content">
            <h4>Filter by County & City</h4>
            <select onChange={e => setSelectedCounty(e.target.value)} value={selectedCounty}>
              <option value="">Select County</option>
              {Object.keys(townsByCounty).map(county => (
                <option key={county} value={county}>
                  {county}
                </option>
              ))}
            </select>
            <select disabled={!selectedCounty} value={selectedTown} onChange={e => setSelectedTown(e.target.value)}>
              <option value="">Select City</option>
              {selectedCounty &&
                townsByCounty[selectedCounty].map((city, index) => (
                  <option key={index} value={index.toString()}>
                    {city.name}
                  </option>
                ))}
            </select>
            <button onClick={handleFindCity}>Find</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
