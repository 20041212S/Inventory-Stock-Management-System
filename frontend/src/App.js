import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import PartMasterList from './components/PartMasterList';
import StockManagement from './components/StockManagement';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <div className="nav-container">
            <h1 className="nav-title">Inventory Stock Management System</h1>
            <div className="nav-links">
              <Link to="/" className="nav-link">Part Master</Link>
              <Link to="/stocks" className="nav-link">Stock Management</Link>
            </div>
          </div>
        </nav>
        
        <div className="container">
          <Routes>
            <Route path="/" element={<PartMasterList />} />
            <Route path="/stocks" element={<StockManagement />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;

