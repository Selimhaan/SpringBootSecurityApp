import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { FiUpload, FiDownload, FiTrash2, FiFileText } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
  const { user, isAdmin } = useAuth();
  const [documents, setDocuments] = useState([]);
  const [otherDocuments, setOtherDocuments] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('mine'); // 'mine' or 'others'

  const isManager = user?.roles?.includes('ROLE_MANAGER');
  const showTabs = isAdmin || isManager;

  useEffect(() => {
    fetchDocuments();
    if (showTabs) {
      fetchOtherDocuments();
    }
  }, [showTabs]);

  const fetchDocuments = async () => {
    try {
      const res = await api.get('/documents');
      setDocuments(res.data);
    } catch (err) {
      console.error('Failed to fetch documents', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchOtherDocuments = async () => {
    try {
      const res = await api.get('/documents/others');
      setOtherDocuments(res.data);
    } catch (err) {
      console.error('Failed to fetch other documents', err);
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    setIsUploading(true);
    try {
      await api.post('/documents', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      fetchDocuments();
    } catch (err) {
      console.error('Failed to upload', err);
      alert('File upload failed!');
    } finally {
      setIsUploading(false);
      e.target.value = null; // reset input
    }
  };

  const handleDownload = async (docId, filename) => {
    try {
      const response = await api.get(`/documents/${docId}/download`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Download failed', err);
      alert('Download failed!');
    }
  };

  const handleDelete = async (docId, isOther = false) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;
    
    try {
      await api.delete(`/documents/${docId}`);
      if (isOther) {
        fetchOtherDocuments();
      } else {
        fetchDocuments();
      }
    } catch (err) {
      console.error('Delete failed', err);
      alert('Delete failed! You might not have permission.');
    }
  };

  const formatBytes = (bytes, decimals = 2) => {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  };

  const renderTable = (docs, isOther = false) => {
    if (loading) return <div className="text-center py-4 text-muted">Loading documents...</div>;
    if (docs.length === 0) {
      return (
        <div className="text-center py-4" style={{ color: 'var(--text-muted)' }}>
          <FiFileText size={48} style={{ opacity: 0.2, marginBottom: '1rem' }} />
          <p>No documents found.</p>
        </div>
      );
    }

    return (
      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={{ padding: '1rem' }}>Name</th>
              <th style={{ padding: '1rem' }}>Owner</th>
              <th style={{ padding: '1rem' }}>Size</th>
              <th style={{ padding: '1rem', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {docs.map((doc) => {
              // Determine if delete should be shown
              const canDelete = !isOther || isAdmin; // Owner can delete their own (!isOther), Admin can delete anything

              return (
                <tr key={doc.id} style={{ borderBottom: '1px solid var(--border-color)', transition: 'background 0.2s' }}>
                  <td style={{ padding: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <FiFileText style={{ color: 'var(--color-secondary)' }} />
                    <span style={{ fontWeight: 500 }}>{doc.name}</span>
                  </td>
                  <td style={{ padding: '1rem', color: 'var(--text-muted)' }}>{doc.ownerName}</td>
                  <td style={{ padding: '1rem', color: 'var(--text-muted)' }}>{formatBytes(doc.size)}</td>
                  <td style={{ padding: '1rem', textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                      <button 
                        onClick={() => handleDownload(doc.id, doc.name)}
                        className="btn btn-secondary"
                        style={{ padding: '0.5rem', borderRadius: '4px' }}
                        title="Download"
                      >
                        <FiDownload />
                      </button>
                      {canDelete && (
                        <button 
                          onClick={() => handleDelete(doc.id, isOther)}
                          className="btn btn-danger"
                          style={{ padding: '0.5rem', borderRadius: '4px' }}
                          title="Delete"
                        >
                          <FiTrash2 />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Document Dashboard</h1>
        <div>
          <input 
            type="file" 
            id="file-upload" 
            style={{ display: 'none' }} 
            onChange={handleFileUpload}
            disabled={isUploading}
          />
          <label htmlFor="file-upload" className="btn btn-primary">
            <FiUpload /> {isUploading ? 'Uploading...' : 'Upload Document'}
          </label>
        </div>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        {showTabs && (
          <div style={{ display: 'flex', borderBottom: '1px solid var(--border-color)', background: 'var(--bg-main)' }}>
            <button 
              onClick={() => setActiveTab('mine')}
              style={{
                flex: 1, padding: '1rem', background: 'transparent', border: 'none', cursor: 'pointer',
                borderBottom: activeTab === 'mine' ? '3px solid var(--color-secondary)' : '3px solid transparent',
                fontWeight: activeTab === 'mine' ? '600' : '400',
                color: activeTab === 'mine' ? 'var(--color-primary)' : 'var(--text-muted)'
              }}
            >
              My Documents
            </button>
            <button 
              onClick={() => setActiveTab('others')}
              style={{
                flex: 1, padding: '1rem', background: 'transparent', border: 'none', cursor: 'pointer',
                borderBottom: activeTab === 'others' ? '3px solid var(--color-secondary)' : '3px solid transparent',
                fontWeight: activeTab === 'others' ? '600' : '400',
                color: activeTab === 'others' ? 'var(--color-primary)' : 'var(--text-muted)'
              }}
            >
              Other Users' Documents
            </button>
          </div>
        )}
        
        <div style={{ padding: '2rem' }}>
          {activeTab === 'mine' ? renderTable(documents, false) : renderTable(otherDocuments, true)}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
