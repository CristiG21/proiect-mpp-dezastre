import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { ICenter } from 'app/shared/model/center.model';
import { IPhotoURL } from 'app/shared/model/photo-url.model';
import { Alert } from 'reactstrap';
import { ICenterTypeWrapper } from 'app/shared/model/center-type-wrapper.model';
import { CenterType } from 'app/shared/model/enumerations/center-type.model';

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
      .catch(err => {
        console.error(err);
        setError('Could not fetch center details.');
      });

    // Fetch associated photos
    axios
      .get<IPhotoURL[]>(`/api/centers/${id}/photos`)
      .then(response => setPhotos(response.data))
      .catch(err => {
        console.error(err);
      });

    // Fetch associated center types
    axios
      .get<CenterType[]>(`/api/centers/${id}/types`)
      .then(response => setCenterTypes(response.data))
      .catch(err => {
        console.error(err);
      });
  }, [id]);

  if (error) {
    return <Alert color="danger">{error}</Alert>;
  }

  if (!center) {
    return <div>Loading...</div>;
  }

  return (
    <div style={{ padding: '30px', maxWidth: '1200px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '2.5rem', marginBottom: '20px', textAlign: 'center' }}>{center.name}</h1>

      {/* Description */}
      {center.description && (
        <p style={{ fontSize: '1.2rem', lineHeight: '1.6', marginBottom: '15px', textAlign: 'center' }}>
          <strong>Description:</strong> {center.description}
        </p>
      )}

      {/* Available Seats */}
      {center.availableSeats != null && (
        <p style={{ fontSize: '1.2rem', marginBottom: '15px', textAlign: 'center' }}>
          <strong>Available Seats:</strong> {center.availableSeats}
        </p>
      )}

      {/* Center Types */}
      <p style={{ fontSize: '1.2rem', marginBottom: '30px', textAlign: 'center' }}>
        <strong>Types:</strong>{' '}
        {centerTypes.length > 0 ? (
          centerTypes.map((type, index) => (
            <span key={index} className="badge badge-info mr-1">
              {type}
            </span>
          ))
        ) : (
          <span className="text-muted">No types</span>
        )}
      </p>

      {/* Photos */}
      {photos.length > 0 && (
        <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', justifyContent: 'center' }}>
          {photos.map((photo, index) => (
            <div
              key={photo.id ?? index}
              style={{
                overflow: 'hidden',
                borderRadius: '12px',
                boxShadow: '0 4px 10px rgba(0,0,0,0.15)',
                transition: 'transform 0.3s',
                width: '300px',
                height: '200px',
              }}
            >
              <img
                src={photo.url || ''}
                alt={`Photo ${index + 1}`}
                style={{
                  width: '100%',
                  height: '100%',
                  objectFit: 'cover',
                  transition: 'transform 0.3s',
                }}
                onMouseOver={e => (e.currentTarget.style.transform = 'scale(1.05)')}
                onMouseOut={e => (e.currentTarget.style.transform = 'scale(1.0)')}
              />
            </div>
          ))}
        </div>
      )}

      {photos.length === 0 && (
        <p style={{ textAlign: 'center', fontStyle: 'italic', marginTop: '20px' }}>No photos available for this center.</p>
      )}
    </div>
  );
};

export default CenterDetail;
