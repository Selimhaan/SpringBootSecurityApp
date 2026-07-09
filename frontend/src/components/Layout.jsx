import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiHome, FiUsers, FiLogOut, FiFileText } from 'react-icons/fi';

const Layout = () => {
  const { user, logout, isAdmin } = useAuth();
  const location = useLocation();

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <Link to="/" className="sidebar-logo">
          <span>ARGELA</span> Portal
        </Link>
        
        <nav style={{ flex: 1 }}>
          <Link 
            to="/" 
            className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
          >
            <FiHome /> Dashboard
          </Link>
          
          {isAdmin && (
            <Link 
              to="/admin" 
              className={`nav-link ${location.pathname === '/admin' ? 'active' : ''}`}
            >
              <FiUsers /> Admin Panel
            </Link>
          )}
        </nav>

        <div className="user-section" style={{ marginTop: 'auto', paddingTop: '2rem', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
          <div style={{ marginBottom: '1rem', fontSize: '0.875rem', color: 'rgba(255,255,255,0.7)' }}>
            Logged in as:<br/>
            <strong style={{ color: 'white' }}>{user?.email}</strong>
          </div>
          <button onClick={logout} className="btn" style={{ width: '100%', backgroundColor: 'rgba(255,255,255,0.1)', color: 'white' }}>
            <FiLogOut /> Logout
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="main-content animate-fade-in">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
