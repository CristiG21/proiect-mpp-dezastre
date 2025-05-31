import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { IPhotoURL } from 'app/shared/model/photo-url.model';
import { Alert } from 'reactstrap';
import { CenterType } from 'app/shared/model/enumerations/center-type.model';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import PhotoCarousel from './photo-carousel';

export const CenterDetail = () => {
  const { id } = useParams<{ id: string }>();
  const [center, setCenter] = useState<ICenter | null>(null);
  const [photos, setPhotos] = useState<IPhotoURL[]>([]);
  const [centerTypes, setCenterTypes] = useState<CenterType[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Fetch center info
    axios
      .get<ICenter>(`/api/centers/${id}`)
      .then(response => setCenter(response.data))
      .catch(err => setError('Could not fetch center details.'));

    // Fetch associated photos
    axios
      .get<IPhotoURL[]>(`/api/centers/${id}/photos`)
      .then(response => setPhotos(response.data))
      .catch(console.error);

    // Fetch associated center types
    axios
      .get<CenterType[]>(`/api/centers/${id}/types`)
      .then(response => setCenterTypes(response.data))
      .catch(console.error);
  }, [id]);

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

  // Config for react-slick carousel
  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 1,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 1,
        },
      },
      {
        breakpoint: 768,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
        },
      },
    ],
  };

  return (
    <div style={{ padding: '40px', maxWidth: '1200px', margin: '0 auto', color: '#333' }}>
      {/* Page Header */}
      <header style={{ textAlign: 'center', marginBottom: '30px' }}>
        <h1 style={{ fontSize: '2.8rem', fontWeight: 'bold', color: '#1a237e' }}>{center.name}</h1>
      </header>

      {/* Center Info */}
      <section>
        {center.description && (
          <div style={{ textAlign: 'center', marginBottom: '30px', lineHeight: '1.6' }}>
            <p style={{ fontSize: '1.3rem', color: '#424242', padding: '0 20px' }}>
              <strong>Description:</strong> {center.description}
            </p>
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', marginBottom: '30px' }}>
          {center.openTime && center.closeTime && (
            <div
              style={{
                flex: '1 1 45%',
                padding: '15px',
                margin: '10px',
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
              }}
            >
              <p
                style={{
                  textAlign: 'center',
                  fontSize: '1.2rem',
                  margin: '0',
                  color: '#0277bd',
                  fontWeight: 'bold',
                }}
              >
                Operating Hours
              </p>
              <p style={{ textAlign: 'center', marginTop: '10px' }}>
                {center.openTime} - {center.closeTime}
              </p>
            </div>
          )}

          {center.availableSeats != null && (
            <div
              style={{
                flex: '1 1 45%',
                padding: '15px',
                margin: '10px',
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
              }}
            >
              <p
                style={{
                  textAlign: 'center',
                  fontSize: '1.2rem',
                  margin: '0',
                  color: '#2e7d32',
                  fontWeight: 'bold',
                }}
              >
                Available Seats
              </p>
              <p style={{ textAlign: 'center', marginTop: '10px' }}>{center.availableSeats}</p>
            </div>
          )}
        </div>

        {/* Center Types */}
        <div
          style={{
            marginBottom: '40px',
            padding: '10px 20px',
            border: '1px solid #e0e0e0',
            borderRadius: '8px',
            backgroundColor: '#f5f5f5',
          }}
        >
          <p style={{ fontSize: '1.2rem', marginBottom: '15px', color: '#3e2723', fontWeight: 'bold' }}>
            <strong>Types: </strong>
          </p>
          {centerTypes.length > 0 ? (
            centerTypes.map((type, index) => (
              <span
                key={index}
                style={{
                  marginRight: '10px',
                  backgroundColor: '#e3f2fd',
                  color: '#0d47a1',
                  padding: '5px 10px',
                  fontSize: '1rem',
                  borderRadius: '8px',
                  fontWeight: 'bold',
                }}
              >
                {type}
              </span>
            ))
          ) : (
            <span style={{ fontStyle: 'italic', color: '#9e9e9e' }}>No types available</span>
          )}
        </div>
      </section>

      {/* Photo Carousel */}
      {photos.length > 0 ? (
        <PhotoCarousel photos={photos} />
      ) : (
        <p style={{ textAlign: 'center', fontStyle: 'italic', marginTop: '20px' }}>No photos available for this center.</p>
      )}
    </div>
  );
};

export default CenterDetail;
