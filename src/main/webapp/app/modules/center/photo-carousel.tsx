import React, { useEffect, useState } from 'react';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const PhotoCarousel = ({ photos }) => {
  const [currentIndex, setCurrentIndex] = useState(0); // State to track the current slide

  const sliderSettings = {
    dots: true, // Enable dots for pagination
    infinite: true, // Allow infinite looping
    speed: 500, // Animation speed
    slidesToShow: 4, // Show 4 photos at a time
    slidesToScroll: 1, // Scroll 1 photo at a time
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
    afterChange: index => setCurrentIndex(index), // Update current index when the slide changes
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
          display: 'inline-block',
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
              style={{
                width: '100%',
                maxWidth: '350px',
                height: '200px',
                objectFit: 'cover',
                borderRadius: '8px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
              }}
            />
          </div>
        ))}
      </Slider>
    </div>
  );
};

export default PhotoCarousel;
