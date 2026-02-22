import React, { useState, useEffect } from 'react';
import { stockAPI, partMasterAPI } from '../services/api';
import './StockManagement.css';

const StockManagement = () => {
  const [stocks, setStocks] = useState([]);
  const [parts, setParts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [message, setMessage] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showBulkUploadModal, setShowBulkUploadModal] = useState(false);
  const [editingStock, setEditingStock] = useState(null);
  const [uploadFile, setUploadFile] = useState(null);
  const [formData, setFormData] = useState({
    partNumber: '',
    stockQuantity: '',
    binNumber: '',
    rackNumber: '',
  });

  useEffect(() => {
    loadStocks();
    loadParts();
  }, [page]);

  const loadStocks = async () => {
    setLoading(true);
    try {
      const response = await stockAPI.getAll(page, size);
      setStocks(response.data.stocks || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to load stocks: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  };

  const loadParts = async () => {
    try {
      const response = await partMasterAPI.getAllList();
      setParts(response.data || []);
    } catch (error) {
      console.error('Failed to load parts:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleCreate = () => {
    setEditingStock(null);
    setFormData({
      partNumber: '',
      stockQuantity: '',
      binNumber: '',
      rackNumber: '',
    });
    setShowModal(true);
  };

  const handleEdit = (stock) => {
    setEditingStock(stock);
    setFormData({
      partNumber: stock.partNumber,
      stockQuantity: stock.stockQuantity,
      binNumber: stock.binNumber || '',
      rackNumber: stock.rackNumber || '',
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const stockData = {
        partNumber: formData.partNumber,
        stockQuantity: parseFloat(formData.stockQuantity),
        binNumber: formData.binNumber,
        rackNumber: formData.rackNumber,
      };

      if (editingStock) {
        await stockAPI.update(editingStock.stockId, stockData);
        setMessage({ type: 'success', text: 'Stock updated successfully' });
      } else {
        await stockAPI.create(stockData);
        setMessage({ type: 'success', text: 'Stock created successfully' });
      }

      setShowModal(false);
      loadStocks();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to save stock: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (stockId) => {
    if (!window.confirm('Are you sure you want to delete this stock entry?')) {
      return;
    }

    setLoading(true);
    try {
      await stockAPI.delete(stockId);
      setMessage({ type: 'success', text: 'Stock deleted successfully' });
      loadStocks();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to delete stock: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  };

  const handleBulkUpload = async () => {
    if (!uploadFile) {
      setMessage({ type: 'error', text: 'Please select a file' });
      return;
    }

    setLoading(true);
    try {
      const response = await stockAPI.bulkUpload(uploadFile);
      const data = response.data;
      
      let messageText = `Upload completed: ${data.successCount} successful, ${data.failureCount} failed`;
      if (data.warnings && data.warnings.length > 0) {
        messageText += '\nWarnings: ' + data.warnings.join(', ');
      }
      if (data.errors && data.errors.length > 0) {
        messageText += '\nErrors: ' + data.errors.join(', ');
      }

      setMessage({ 
        type: data.failureCount > 0 ? 'warning' : 'success', 
        text: messageText 
      });
      setShowBulkUploadModal(false);
      setUploadFile(null);
      loadStocks();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to upload: ' + (error.response?.data?.error || error.message) });
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadTemplate = async () => {
    try {
      const response = await stockAPI.downloadTemplate();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'stock_upload_template.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to download template' });
    }
  };

  const handleExportExcel = async () => {
    try {
      const response = await stockAPI.exportToExcel();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'stock_inventory.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
      setMessage({ type: 'success', text: 'Excel file downloaded successfully' });
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to export to Excel' });
    }
  };

  const handleExportPdf = async () => {
    try {
      const response = await stockAPI.exportToPdf();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'stock_inventory.pdf');
      document.body.appendChild(link);
      link.click();
      link.remove();
      setMessage({ type: 'success', text: 'PDF file downloaded successfully' });
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to export to PDF' });
    }
  };

  return (
    <div className="stock-management">
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Stock Management</h2>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button className="btn btn-primary" onClick={handleCreate}>
              Add Stock
            </button>
            <button className="btn btn-success" onClick={() => setShowBulkUploadModal(true)}>
              Bulk Upload
            </button>
            <button className="btn btn-secondary" onClick={handleExportExcel}>
              Export Excel
            </button>
            <button className="btn btn-secondary" onClick={handleExportPdf}>
              Export PDF
            </button>
          </div>
        </div>

        {message && (
          <div className={`alert alert-${message.type}`} style={{ whiteSpace: 'pre-line' }}>
            {message.text}
          </div>
        )}

        {loading && !stocks.length ? (
          <div className="loading">Loading...</div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Part Number</th>
                    <th>Part Name</th>
                    <th>Stock Quantity</th>
                    <th>Bin Number</th>
                    <th>Rack Number</th>
                    <th>Last Updated</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {stocks.length === 0 ? (
                    <tr>
                      <td colSpan="7" style={{ textAlign: 'center' }}>
                        No stock entries found
                      </td>
                    </tr>
                  ) : (
                    stocks.map((stock) => (
                      <tr key={stock.stockId}>
                        <td>{stock.partNumber}</td>
                        <td>{stock.partName || '-'}</td>
                        <td>{stock.stockQuantity}</td>
                        <td>{stock.binNumber || '-'}</td>
                        <td>{stock.rackNumber || '-'}</td>
                        <td>{stock.lastUpdated ? new Date(stock.lastUpdated).toLocaleString() : '-'}</td>
                        <td>
                          <div className="actions">
                            <button className="btn btn-primary" onClick={() => handleEdit(stock)}>
                              Edit
                            </button>
                            <button className="btn btn-danger" onClick={() => handleDelete(stock.stockId)}>
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

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
          </>
        )}
      </div>

      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>{editingStock ? 'Edit Stock' : 'Add Stock'}</h3>
              <span className="close" onClick={() => setShowModal(false)}>&times;</span>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Part Number *</label>
                <select
                  name="partNumber"
                  value={formData.partNumber}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Part Number</option>
                  {parts.map((part) => (
                    <option key={part.partNumber} value={part.partNumber}>
                      {part.partNumber} - {part.partName}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Stock Quantity *</label>
                <input
                  type="number"
                  name="stockQuantity"
                  value={formData.stockQuantity}
                  onChange={handleInputChange}
                  step="0.01"
                  required
                />
              </div>
              <div className="form-group">
                <label>Bin Number</label>
                <input
                  type="text"
                  name="binNumber"
                  value={formData.binNumber}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>Rack Number</label>
                <input
                  type="text"
                  name="rackNumber"
                  value={formData.rackNumber}
                  onChange={handleInputChange}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {editingStock ? 'Update' : 'Create'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showBulkUploadModal && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Bulk Upload Stocks</h3>
              <span className="close" onClick={() => setShowBulkUploadModal(false)}>&times;</span>
            </div>
            <div className="file-upload">
              <p>Download the template file, fill it with stock data, and upload it here.</p>
              <button className="btn btn-secondary" onClick={handleDownloadTemplate} style={{ marginBottom: '1rem' }}>
                Download Template
              </button>
              <label>Select Excel File:</label>
              <input
                type="file"
                accept=".xlsx,.xls"
                onChange={(e) => setUploadFile(e.target.files[0])}
              />
            </div>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button className="btn btn-primary" onClick={handleBulkUpload} disabled={loading || !uploadFile}>
                Upload
              </button>
              <button className="btn btn-secondary" onClick={() => setShowBulkUploadModal(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StockManagement;

