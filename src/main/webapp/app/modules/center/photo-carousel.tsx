import React, { useState } from 'react';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const PhotoCarousel = ({ photos }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [modalPhoto, setModalPhoto] = useState<string | null>(null);

  const sliderSettings = {
    dots: true,
    infinite: photos.length > 1,
    speed: 500,
    slidesToShow: Math.min(photos.length, 4),
    slidesToScroll: 1,
    centerMode: photos.length < 4, // Only enable center mode when not full
    centerPadding: photos.length === 1 ? '40%' : '0px', // Large padding centers one item
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: Math.min(photos.length, 2),
          slidesToScroll: 1,
          centerMode: photos.length < 2,
          centerPadding: photos.length === 1 ? '40%' : '0px',
        },
      },
      {
        breakpoint: 768,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
          centerMode: true,
          centerPadding: '30%',
        },
      },
    ],
    afterChange: index => setCurrentIndex(index),
    customPaging: index => (
      <div
        style={{
          width: '10px',
          height: '10px',
          border: '1px solid #1a237e',
          backgroundColor: currentIndex === index ? '#1a237e' : '#e0e0e0',
          borderRadius: '50%',
          margin: '0 5px',
          cursor: 'pointer',
        }}
      />
    ),
  };

  return (
    <div>
      <Slider {...sliderSettings}>
        {photos.map((photo, index) => (
          <div key={index} style={{ padding: '10px', textAlign: 'center' }}>
            <img
              src={photo.url || ''}
              alt={`Slide ${index + 1}`}
              onClick={() => setModalPhoto(photo.url)}
              style={{
                width: '100%',
                maxWidth: '350px',
                height: '200px',
                objectFit: 'cover',
                borderRadius: '8px',
                cursor: 'pointer',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
              }}
            />
          </div>
        ))}
      </Slider>

      {modalPhoto && (
        <div
          onClick={() => setModalPhoto(null)}
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            backgroundColor: 'rgba(0,0,0,0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            cursor: 'zoom-out',
          }}
        >
          <img
            src={modalPhoto}
            alt="Full view"
            style={{
              maxWidth: '90%',
              maxHeight: '90%',
              borderRadius: '10px',
              boxShadow: '0 6px 12px rgba(0,0,0,0.3)',
            }}
          />
        </div>
      )}
    </div>
  );
};

export default PhotoCarousel;
