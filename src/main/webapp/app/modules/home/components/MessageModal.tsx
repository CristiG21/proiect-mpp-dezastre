import React from 'react';
import '../modal.scss';
import { IOfficialMessage } from 'app/shared/model/official-message.model';

const MessageModal: React.FC<{ message: IOfficialMessage; onClose: () => void }> = ({ message, onClose }) => {
  return (
    <div
      className="modal-overlay"
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        backgroundColor: 'rgba(0,0,0,0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 10000,
      }}
      onClick={onClose}
    >
      <div
        className="modal-content"
        style={{
          background: 'white',
          padding: '2rem',
          borderRadius: '10px',
          width: '90%',
          maxWidth: '500px',
          boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
        }}
        onClick={e => e.stopPropagation()}
      >
        <h5 style={{ fontWeight: 'bold', marginBottom: '0.5rem' }}>{message.title}</h5>
        <small className="text-muted" style={{ display: 'block', marginBottom: '1rem' }}>
          by @{message.user?.login || 'unknown'}
        </small>
        <p>{message.body}</p>
        <button
          onClick={onClose}
          style={{
            marginTop: '1.5rem',
            padding: '0.5rem 1rem',
            borderRadius: '6px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            cursor: 'pointer',
          }}
        >
          Close
        </button>
      </div>
    </div>
  );
};

export default MessageModal;
