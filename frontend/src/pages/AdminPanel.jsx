import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { FiUserPlus, FiUser, FiList } from 'react-icons/fi';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('list'); // 'list' or 'register'
  
  const [isCreating, setIsCreating] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'EMPLOYEE'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (activeTab === 'list') {
      fetchUsers();
    }
  }, [activeTab]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/users');
      setUsers(res.data);
    } catch (err) {
      console.error('Failed to fetch users', err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsCreating(true);

    try {
      await api.post('/users', formData);
      setSuccess('User registered successfully!');
      setFormData({ firstName: '', lastName: '', email: '', password: '', role: 'EMPLOYEE' });
    } catch (err) {
      console.error('Failed to register user', err);
      setError('Failed to register user. Ensure email is unique.');
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Admin Panel</h1>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ display: 'flex', borderBottom: '1px solid var(--border-color)', background: 'var(--bg-main)' }}>
          <button 
            onClick={() => setActiveTab('list')}
            style={{
              flex: 1, padding: '1rem', background: 'transparent', border: 'none', cursor: 'pointer',
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem',
              borderBottom: activeTab === 'list' ? '3px solid var(--color-secondary)' : '3px solid transparent',
              fontWeight: activeTab === 'list' ? '600' : '400',
              color: activeTab === 'list' ? 'var(--color-primary)' : 'var(--text-muted)'
            }}
          >
            <FiList /> Users List
          </button>
          <button 
            onClick={() => setActiveTab('register')}
            style={{
              flex: 1, padding: '1rem', background: 'transparent', border: 'none', cursor: 'pointer',
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem',
              borderBottom: activeTab === 'register' ? '3px solid var(--color-secondary)' : '3px solid transparent',
              fontWeight: activeTab === 'register' ? '600' : '400',
              color: activeTab === 'register' ? 'var(--color-primary)' : 'var(--text-muted)'
            }}
          >
            <FiUserPlus /> Register New User
          </button>
        </div>

        <div style={{ padding: '2rem' }}>
          {activeTab === 'list' && (
            <div>
              <h2 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiUser /> Registered Users
              </h2>
              
              {loading ? (
                <div className="text-muted">Loading users...</div>
              ) : (
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                    <thead>
                      <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                        <th style={{ padding: '0.75rem' }}>Name</th>
                        <th style={{ padding: '0.75rem' }}>Email</th>
                        <th style={{ padding: '0.75rem' }}>Roles</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((user) => (
                        <tr key={user.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                          <td style={{ padding: '0.75rem', fontWeight: 500 }}>
                            {user.firstName} {user.lastName}
                          </td>
                          <td style={{ padding: '0.75rem', color: 'var(--text-muted)' }}>{user.email}</td>
                          <td style={{ padding: '0.75rem' }}>
                            {user.roles?.map((r, i) => (
                              <span key={i} style={{ 
                                background: r.name === 'ROLE_ADMIN' ? 'var(--color-primary)' : 'var(--color-secondary)', 
                                color: 'white', 
                                padding: '0.25rem 0.5rem', 
                                borderRadius: '999px', 
                                fontSize: '0.75rem',
                                marginRight: '0.25rem'
                              }}>
                                {r.name.replace('ROLE_', '')}
                              </span>
                            ))}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeTab === 'register' && (
            <div style={{ maxWidth: '600px', margin: '0 auto' }}>
              <h2 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem', justifyContent: 'center' }}>
                <FiUserPlus /> Register a New User
              </h2>
              
              {error && <div style={{ color: '#dc2626', marginBottom: '1rem', padding: '0.75rem', background: '#fee2e2', borderRadius: '0.5rem', textAlign: 'center' }}>{error}</div>}
              {success && <div style={{ color: '#16a34a', marginBottom: '1rem', padding: '0.75rem', background: '#dcfce3', borderRadius: '0.5rem', textAlign: 'center' }}>{success}</div>}

              <form onSubmit={handleSubmit}>
                <div className="grid grid-cols-2" style={{ gap: '1rem' }}>
                  <div className="input-group">
                    <label htmlFor="firstName">First Name</label>
                    <input type="text" id="firstName" value={formData.firstName} onChange={handleInputChange} required />
                  </div>
                  <div className="input-group">
                    <label htmlFor="lastName">Last Name</label>
                    <input type="text" id="lastName" value={formData.lastName} onChange={handleInputChange} required />
                  </div>
                </div>

                <div className="input-group">
                  <label htmlFor="email">Email</label>
                  <input type="email" id="email" value={formData.email} onChange={handleInputChange} required />
                </div>

                <div className="input-group">
                  <label htmlFor="password">Password</label>
                  <input type="password" id="password" value={formData.password} onChange={handleInputChange} required />
                </div>

                <div className="input-group">
                  <label htmlFor="role">Role</label>
                  <select id="role" value={formData.role} onChange={handleInputChange}>
                    <option value="EMPLOYEE">Employee</option>
                    <option value="MANAGER">Manager</option>
                    <option value="ADMIN">Admin</option>
                  </select>
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem', padding: '1rem' }} disabled={isCreating}>
                  {isCreating ? 'Registering...' : 'Register User'}
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;
