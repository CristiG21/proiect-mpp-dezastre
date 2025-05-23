import React from 'react';
import '../toast.scss';

const ToastNotification: React.FC<{ message: string | null; visible: boolean }> = ({ message, visible }) => {
  return (
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
        opacity: visible ? 1 : 0,
        transform: visible ? 'translateY(0)' : 'translateY(20px)',
        transition: 'opacity 0.4s ease, transform 0.4s ease',
        pointerEvents: 'none',
        visibility: message ? 'visible' : 'hidden',
      }}
    >
      {message}
    </div>
  );
};

export default ToastNotification;
