import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { IReview } from 'app/shared/model/review.model';
import { IPhotoURL } from 'app/shared/model/photo-url.model';
import { Alert } from 'reactstrap';
import { CenterType } from 'app/shared/model/enumerations/center-type.model';
import { useAppSelector } from 'app/config/store';
import PhotoCarousel from './photo-carousel';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHouse, faKitMedical, faUtensils, faFaucet, faRestroom, faNetworkWired, faBus } from '@fortawesome/free-solid-svg-icons';

const centerTypeIcons: Record<CenterType, JSX.Element> = {
  [CenterType.SHELTER]: <FontAwesomeIcon icon={faHouse} style={{ color: '#ff5722' }} />,
  [CenterType.MEDICAL]: <FontAwesomeIcon icon={faKitMedical} style={{ color: '#e91e63' }} />,
  [CenterType.FOOD]: <FontAwesomeIcon icon={faUtensils} style={{ color: '#4caf50' }} />,
  [CenterType.WATER]: <FontAwesomeIcon icon={faFaucet} style={{ color: '#2196f3' }} />,
  [CenterType.SANITATION]: <FontAwesomeIcon icon={faRestroom} style={{ color: '#8e24aa' }} />,
  [CenterType.COMMUNICATION]: <FontAwesomeIcon icon={faNetworkWired} style={{ color: '#ff9800' }} />,
  [CenterType.TRANSPORTATION]: <FontAwesomeIcon icon={faBus} style={{ color: '#607d8b' }} />,
};

// Component definition
export const CenterDetail = () => {
  const { id } = useParams<{ id: string }>();
  const [center, setCenter] = useState<ICenter | null>(null);
  const [photos, setPhotos] = useState<IPhotoURL[]>([]);
  const [reviews, setReviews] = useState<IReview[]>([]);
  const [centerTypes, setCenterTypes] = useState<CenterType[]>([]);
  const [error, setError] = useState<string | null>(null);

  const [reviewText, setReviewText] = useState<string>('');
  const [reviewStars, setReviewStars] = useState<number>(0);

  // Redux authentication states
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  useEffect(() => {
    // Fetch center details
    axios
      .get<ICenter>(`/api/centers/${id}`)
      .then(response => setCenter(response.data))
      .catch(() => setError('Could not fetch center details.'));

    // Fetch photos
    axios
      .get<IPhotoURL[]>(`/api/centers/${id}/photos`)
      .then(response => setPhotos(response.data))
      .catch(console.error);

    // Fetch center types
    axios
      .get<CenterType[]>(`/api/centers/${id}/types`)
      .then(response => setCenterTypes(response.data))
      .catch(console.error);

    // Fetch reviews for the center
    axios
      .get<IReview[]>(`/api/reviews/by-center/${id}`)
      .then(response => setReviews(response.data))
      .catch(console.error);
  }, [id]);

  const submitReview = () => {
    // Check if the user selected at least one star for the review
    if (reviewStars === 0) {
      alert('Please select a rating before submitting your review.');
      return;
    }

    // Submit the review
    if (isAuthenticated) {
      axios
        .post('/api/reviews', {
          text: reviewText.trim() || null, // Text is optional
          stars: reviewStars,
          center: { id }, // Link to the current center
          user: { id: account.id, login: account.login }, // Associate the user
        })
        .then(response => {
          // Add the newly submitted review to the list automatically and reset inputs
          setReviews([...reviews, response.data]);
          setReviewText(''); // Optional text field
          setReviewStars(0); // Reset selected stars
        })
        .catch(errorC => {
          console.error('Failed to submit review:', errorC);
          alert('An error occurred while submitting your review.');
        });
    } else {
      alert('You must be logged in to submit a review.');
    }
  };

  if (error) {
    return (
      <Alert color="danger" style={{ margin: '20px auto', maxWidth: '600px' }}>
        {error}
      </Alert>
    );
  }

  if (!center) {
    return <div style={{ textAlign: 'center', marginTop: '50px', fontSize: '1.5rem' }}>Loading...</div>;
  }

  // Configuration for photo carousel
  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 1,
    responsive: [
      {
        breakpoint: 1024,
        settings: { slidesToShow: 2, slidesToScroll: 1 },
      },
      {
        breakpoint: 768,
        settings: { slidesToShow: 1, slidesToScroll: 1 },
      },
    ],
  };

  const calculateAverageRating = (reviewsL: IReview[]): number => {
    if (reviewsL.length === 0) return 0;
    const totalStars = reviewsL.reduce((acc, review) => acc + (review.stars || 0), 0);
    return totalStars / reviewsL.length;
  };

  const hasReviewed = reviews.some(review => review.user?.id === account?.id);

  return (
    <div
      style={{
        padding: '40px',
        maxWidth: '1000px',
        margin: '40px auto',
        fontFamily: 'Arial, sans-serif',
        color: '#424242', // Default body text color (dark gray)
      }}
    >
      {/* Header Section */}
      <header
        style={{
          textAlign: 'center',
          marginBottom: '30px',
        }}
      >
        {/* Center Name */}
        <h1
          style={{
            fontSize: '3rem',
            fontWeight: '700',
            color: '#1E88E5', // Blue text for title
            marginBottom: '10px',
          }}
        >
          {center.name}
        </h1>

        {/* Average Rating */}
        <div
          style={{
            fontSize: '2rem',
            marginTop: '10px',
          }}
        >
          {/* Stars Display */}
          {[1, 2, 3, 4, 5].map(star => (
            <span
              key={star}
              style={{
                color: star <= calculateAverageRating(reviews) ? '#FFD700' : '#E0E0E0',
                marginRight: 2,
              }}
            >
              ★
            </span>
          ))}
          <span
            style={{
              fontSize: '1.5rem',
              marginLeft: '10px',
              color: '#1E88E5', // Blue for rating text
            }}
          >
            {calculateAverageRating(reviews).toFixed(1)} / 5
          </span>
        </div>
      </header>

      {/* Center Types Section */}
      {centerTypes.length > 0 && (
        <section style={{ marginBottom: '40px', textAlign: 'center' }}>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', flexWrap: 'wrap' }}>
            {centerTypes.map(type => (
              <div key={type} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100px' }}>
                <div style={{ fontSize: '2.5rem' }}>{centerTypeIcons[type]}</div>
                <p style={{ fontSize: '1rem', color: '#424242', marginTop: '10px', textTransform: 'capitalize' }}>{type.toLowerCase()}</p>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* Center Info Section */}
      <section
        style={{
          marginBottom: '40px',
        }}
      >
        {/* Description */}
        {center.description && (
          <div
            style={{
              textAlign: 'center',
              padding: '20px',
              backgroundColor: '#F5F5F5', // Light gray background
              marginBottom: '20px',
              borderRadius: '10px',
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
            }}
          >
            <p
              style={{
                fontSize: '1.3rem',
                color: '#424242', // Text in gray
                lineHeight: '1.8',
              }}
            >
              {center.description}
            </p>
          </div>
        )}

        {/* Info Cards */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
          }}
        >
          {center.openTime && center.closeTime && (
            <div
              style={{
                flex: '1 1 45%',
                backgroundColor: '#F5F5F5', // Light gray background
                padding: '20px',
                margin: '10px',
                borderRadius: '10px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                textAlign: 'center',
              }}
            >
              <p
                style={{
                  fontSize: '1rem',
                  fontWeight: 'bold',
                  marginBottom: '10px',
                  color: '#1E88E5', // Blue label
                }}
              >
                Operating Hours
              </p>
              <span
                style={{
                  fontSize: '1.2rem',
                  color: '#424242', // Gray text
                }}
              >
                {center.openTime} - {center.closeTime}
              </span>
            </div>
          )}

          {center.availableSeats != null && (
            <div
              style={{
                flex: '1 1 45%',
                backgroundColor: '#F5F5F5', // Light gray background
                padding: '20px',
                margin: '10px',
                borderRadius: '10px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                textAlign: 'center',
              }}
            >
              <p
                style={{
                  fontSize: '1rem',
                  fontWeight: 'bold',
                  marginBottom: '10px',
                  color: '#1E88E5', // Blue label
                }}
              >
                Available Seats
              </p>
              <span
                style={{
                  fontSize: '1.2rem',
                  color: '#424242', // Gray text
                }}
              >
                {center.availableSeats}
              </span>
            </div>
          )}
        </div>
      </section>

      {/* Photo Carousel */}
      {photos.length > 0 ? (
        <PhotoCarousel photos={photos} />
      ) : (
        <p
          style={{
            textAlign: 'center',
            fontStyle: 'italic',
            color: '#999',
          }}
        >
          No photos available for this center.
        </p>
      )}

      {/* Reviews Section */}
      <section
        style={{
          marginTop: '40px',
        }}
      >
        <h2
          style={{
            fontSize: '2rem',
            fontWeight: 'bold',
            color: '#1E88E5', // Blue text
            marginBottom: '20px',
          }}
        >
          Reviews
        </h2>

        {/* Add Review Form or Message */}
        {isAuthenticated && (
          <>
            {hasReviewed ? (
              <p
                style={{
                  fontSize: '1.2rem',
                  color: '#424242', // Gray message text
                  backgroundColor: '#F5F5F5',
                  padding: '15px',
                  borderRadius: '8px',
                  textAlign: 'center',
                  boxShadow: '0 2px 6px rgba(0, 0, 0, 0.1)',
                }}
              >
                You’ve already submitted a review for this center.
              </p>
            ) : (
              <div
                style={{
                  padding: '20px',
                  backgroundColor: '#F5F5F5',
                  borderRadius: '10px',
                  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                  marginBottom: '20px',
                }}
              >
                <h3
                  style={{
                    fontSize: '1.5rem',
                    fontWeight: 'bold',
                    marginBottom: '15px',
                    color: '#1E88E5',
                  }}
                >
                  Add Your Review
                </h3>
                <textarea
                  value={reviewText}
                  onChange={e => setReviewText(e.target.value)}
                  rows={4}
                  style={{
                    width: '100%',
                    padding: '10px',
                    fontSize: '1rem',
                    borderRadius: '8px',
                    border: '1px solid #ddd',
                    marginBottom: '15px',
                  }}
                  placeholder="Write your review here..."
                ></textarea>
                <div>
                  {[1, 2, 3, 4, 5].map(star => (
                    <span
                      key={star}
                      onClick={() => setReviewStars(star)}
                      style={{
                        fontSize: '2rem',
                        color: star <= reviewStars ? '#FFD700' : '#ccc',
                        cursor: 'pointer',
                        marginRight: '5px',
                      }}
                    >
                      ★
                    </span>
                  ))}
                </div>
                <button
                  onClick={submitReview} // Function to handle review submission
                  style={{
                    marginTop: '15px',
                    padding: '12px 20px',
                    backgroundColor: '#1E88E5', // Blue button
                    color: '#ffffff',
                    border: 'none',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    fontWeight: 'bold',
                    fontSize: '1rem',
                  }}
                >
                  Submit Review
                </button>
              </div>
            )}
          </>
        )}

        {/* Reviews List */}
        {reviews.length > 0 ? (
          <div>
            {reviews.map(review => (
              <div
                key={review.id}
                style={{
                  borderBottom: '1px solid #ddd',
                  padding: '15px 0',
                }}
              >
                <p
                  style={{
                    fontSize: '1rem',
                    fontWeight: 'bold',
                    marginBottom: '5px',
                    color: '#1E88E5', // Blue text for reviewer
                  }}
                >
                  {review.user?.firstName || review.user?.login} ({review.user?.login})
                </p>
                <p
                  style={{
                    fontSize: '1rem',
                    marginBottom: '10px',
                    color: '#424242', // Review text in dark gray
                  }}
                >
                  {review.text}
                </p>
                <div>
                  {[1, 2, 3, 4, 5].map(star => (
                    <span
                      key={star}
                      style={{
                        fontSize: '1.2rem',
                        color: star <= (review.stars || 0) ? '#FFD700' : '#E0E0E0',
                      }}
                    >
                      ★
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p
            style={{
              fontSize: '1rem',
              color: '#666',
              textAlign: 'center',
              fontStyle: 'italic',
            }}
          >
            No reviews available for this center.
          </p>
        )}
      </section>
    </div>
  );
};

export default CenterDetail;
