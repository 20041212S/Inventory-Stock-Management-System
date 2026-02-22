import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const partMasterAPI = {
  getAll: (page = 0, size = 10, search = '') => {
    const params = { page, size };
    if (search) params.search = search;
    return api.get('/parts', { params });
  },
  getAllList: () => api.get('/parts/all'),
  getByPartNumber: (partNumber) => api.get(`/parts/${partNumber}`),
  importFromExcel: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/parts/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export const stockAPI = {
  getAll: (page = 0, size = 10) => api.get('/stocks', { params: { page, size } }),
  getAllList: () => api.get('/stocks/all'),
  getById: (stockId) => api.get(`/stocks/${stockId}`),
  getByPartNumber: (partNumber) => api.get(`/stocks/part/${partNumber}`),
  create: (stock) => api.post('/stocks', stock),
  update: (stockId, stock) => api.put(`/stocks/${stockId}`, stock),
  delete: (stockId) => api.delete(`/stocks/${stockId}`),
  bulkUpload: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/stocks/bulk-upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  downloadTemplate: () => api.get('/stocks/template/download', { responseType: 'blob' }),
  exportToExcel: () => api.get('/stocks/export/excel', { responseType: 'blob' }),
  exportToPdf: () => api.get('/stocks/export/pdf', { responseType: 'blob' }),
};

export const locationAPI = {
  getAll: () => api.get('/locations'),
  create: (location) => api.post('/locations', location),
};

export default api;

