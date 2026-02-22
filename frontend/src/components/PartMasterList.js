import React, { useState, useEffect, useCallback } from 'react';
import { partMasterAPI } from '../services/api';
import './PartMasterList.css';

const PartMasterList = () => {
  const [parts, setParts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [message, setMessage] = useState(null);
  const [showImportModal, setShowImportModal] = useState(false);
  const [importFile, setImportFile] = useState(null);

  const loadParts = useCallback(async () => {
    setLoading(true);
    try {
      const response = await partMasterAPI.getAll(page, size, searchTerm);
      setParts(response.data.parts || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to load parts: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  }, [page, size, searchTerm]);

  useEffect(() => {
    loadParts();
  }, [loadParts]);

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
    setPage(0);
  };

  const handleImport = async () => {
    if (!importFile) {
      setMessage({ type: 'error', text: 'Please select a file' });
      return;
    }

    setLoading(true);
    try {
      const response = await partMasterAPI.importFromExcel(importFile);
      setMessage({ type: 'success', text: `Successfully imported ${response.data.count} parts` });
      setShowImportModal(false);
      setImportFile(null);
      loadParts();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to import: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="part-master-list">
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Part Master Data</h2>
          <button className="btn btn-primary" onClick={() => setShowImportModal(true)}>
            Import from Excel
          </button>
        </div>

        {message && (
          <div className={`alert alert-${message.type}`}>
            {message.text}
          </div>
        )}

        <div className="search-box">
          <input
            type="text"
            className="search-input"
            placeholder="Search by Part Number, Part Name, or Category..."
            value={searchTerm}
            onChange={handleSearch}
          />
        </div>

        {loading ? (
          <div className="loading">Loading...</div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Part Number</th>
                    <th>Part Name</th>
                    <th>Make/Buy</th>
                    <th>Category</th>
                    <th>Subcategory</th>
                    <th>Unit of Measure</th>
                    <th>Drawing Number</th>
                    <th>Revision Number</th>
                    <th>Revised Date</th>
                    <th>Location</th>
                  </tr>
                </thead>
                <tbody>
                  {parts.length === 0 ? (
                    <tr>
                      <td colSpan="10" style={{ textAlign: 'center' }}>
                        No parts found
                      </td>
                    </tr>
                  ) : (
                    parts.map((part) => (
                      <tr key={part.partNumber}>
                        <td>{part.partNumber}</td>
                        <td>{part.partName}</td>
                        <td>{part.makeOrBuy}</td>
                        <td>{part.category}</td>
                        <td>{part.subcategory || '-'}</td>
                        <td>{part.unitOfMeasure}</td>
                        <td>{part.drawingNumber || '-'}</td>
                        <td>{part.revisionNumber || '-'}</td>
                        <td>{part.revisedDate || '-'}</td>
                        <td>{part.locationName || '-'}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {!searchTerm && (
              <div className="pagination">
                <button onClick={() => setPage(0)} disabled={page === 0}>
                  First
                </button>
                <button onClick={() => setPage(page - 1)} disabled={page === 0}>
                  Previous
                </button>
                <span>
                  Page {page + 1} of {totalPages} (Total: {totalElements})
                </span>
                <button onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1}>
                  Next
                </button>
                <button onClick={() => setPage(totalPages - 1)} disabled={page >= totalPages - 1}>
                  Last
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {showImportModal && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Import Parts from Excel</h3>
              <span className="close" onClick={() => setShowImportModal(false)}>&times;</span>
            </div>
            <div className="file-upload">
              <label>Select Excel File:</label>
              <input
                type="file"
                accept=".xlsx,.xls"
                onChange={(e) => setImportFile(e.target.files[0])}
              />
            </div>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button className="btn btn-primary" onClick={handleImport} disabled={loading}>
                Import
              </button>
              <button className="btn btn-secondary" onClick={() => setShowImportModal(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PartMasterList;

