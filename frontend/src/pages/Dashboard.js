import { useState, useEffect } from 'react';
import api from '../api/axios';

function Dashboard({ user, onLogout, onSelectDocument }) {
  const [documents, setDocuments] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    try {
      const response = await api.get('/api/documents');
      setDocuments(response.data);
    } catch (err) {
      console.error('Failed to fetch documents:', err);
    }
  };

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    setMessage('');

    const formData = new FormData();
    formData.append('file', file);

    try {
      await api.post('/api/documents/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setMessage('✅ Document uploaded successfully!');
      fetchDocuments();
    } catch (err) {
      setMessage('❌ Upload failed. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <h1 style={styles.logo}>📄 DocuMind AI</h1>
        <div style={styles.userInfo}>
          <span>👋 {user?.username}</span>
          <button style={styles.logoutBtn} onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div style={styles.content}>
        <h2 style={styles.sectionTitle}>My Documents</h2>

        {/* Upload Section */}
        <div style={styles.uploadSection}>
          <label style={styles.uploadLabel}>
            {uploading ? '⏳ Uploading...' : '📤 Upload PDF'}
            <input
              type="file"
              accept=".pdf"
              onChange={handleUpload}
              style={{ display: 'none' }}
              disabled={uploading}
            />
          </label>
          {message && <p style={styles.message}>{message}</p>}
        </div>

        {/* Documents List */}
        {documents.length === 0 ? (
          <div style={styles.emptyState}>
            <p>No documents yet. Upload a PDF to get started!</p>
          </div>
        ) : (
          <div style={styles.documentGrid}>
            {documents.map((doc) => (
              <div key={doc.id} style={styles.documentCard}>
                <div style={styles.docIcon}>📄</div>
                <div style={styles.docInfo}>
                  <h3 style={styles.docName}>{doc.originalName}</h3>
                  <p style={styles.docMeta}>
                    {doc.chunkCount} chunks • {new Date(doc.createdAt)
                      .toLocaleDateString()}
                  </p>
                </div>
                <button
                  style={styles.askBtn}
                  onClick={() => onSelectDocument(doc)}
                >
                  Ask Questions →
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    backgroundColor: '#f0f2f5',
  },
  header: {
    backgroundColor: '#1a1a2e',
    padding: '16px 32px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  logo: {
    color: 'white',
    margin: 0,
    fontSize: '24px',
  },
  userInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
    color: 'white',
  },
  logoutBtn: {
    padding: '8px 16px',
    backgroundColor: '#ef4444',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
  },
  content: {
    maxWidth: '900px',
    margin: '0 auto',
    padding: '32px 16px',
  },
  sectionTitle: {
    color: '#1a1a2e',
    marginBottom: '24px',
  },
  uploadSection: {
    marginBottom: '32px',
  },
  uploadLabel: {
    display: 'inline-block',
    padding: '12px 24px',
    backgroundColor: '#4f46e5',
    color: 'white',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '16px',
  },
  message: {
    marginTop: '12px',
    fontSize: '14px',
  },
  emptyState: {
    textAlign: 'center',
    padding: '60px',
    backgroundColor: 'white',
    borderRadius: '12px',
    color: '#666',
  },
  documentGrid: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  documentCard: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '12px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  docIcon: {
    fontSize: '32px',
  },
  docInfo: {
    flex: 1,
  },
  docName: {
    margin: '0 0 4px 0',
    color: '#1a1a2e',
    fontSize: '16px',
  },
  docMeta: {
    margin: 0,
    color: '#666',
    fontSize: '14px',
  },
  askBtn: {
    padding: '10px 20px',
    backgroundColor: '#4f46e5',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    whiteSpace: 'nowrap',
  },
};

export default Dashboard;