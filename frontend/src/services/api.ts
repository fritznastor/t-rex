import axios from 'axios';

// Use environment variable or default to localhost for development
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:4567';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Core entity interfaces
export interface Item {
  id: number;
  name: string;
}

export interface InventoryItem {
  id: number;        // This is the item ID, not inventory ID
  name: string;
  stock: number;
  capacity: number;
}

export interface Distributor {
  id: number;
  name: string;
}

export interface DistributorPrice {
  distributor_id: number;
  distributor_name: string;
  item_id: number;
  item_name: string;
  cost: number;
}

// API response interfaces
export interface DistributorItem {
  id: number;
  name: string;
  cost: number;
}

export interface ItemDistributor {
  id: number;
  name: string;
  cost: number;
}

// API functions
export const apiService = {
  // Basic endpoints
  getVersion: () => api.get<string>('/version'),
  
  // Items
  getItems: () => api.get<Item[]>('/items'),
  addItem: (name: string) => api.post('/items', null, { params: { name } }),

  // Inventory
  getInventory: () => api.get<InventoryItem[]>('/inventory'),
  getInventoryById: (id: number) => api.get<InventoryItem>(`/inventory/${id}`),
  getOutOfStockItems: () => api.get<InventoryItem[]>('/inventory/out-of-stock'),
  getOverstockedItems: () => api.get<InventoryItem[]>('/inventory/overstocked'),
  getLowStockItems: () => api.get<InventoryItem[]>('/inventory/low-stock'),
  addInventoryItem: (itemId: number, stock: number, capacity: number) => 
    api.post('/inventory', null, { params: { itemId, stock, capacity } }),
  updateInventoryItem: (id: number, stock?: number, capacity?: number) => {
    const params: any = {};
    if (stock !== undefined) params.stock = stock;
    if (capacity !== undefined) params.capacity = capacity;
    return api.put(`/inventory/${id}`, null, { params });
  },
  deleteInventoryItem: (id: number) => api.delete(`/inventory/${id}`),

  // Distributors
  getDistributors: () => api.get<Distributor[]>('/distributors'),
  getDistributorItems: (distributorId: number) => api.get<DistributorItem[]>(`/distributors/${distributorId}/items`),
  getItemDistributors: (itemId: number) => api.get<ItemDistributor[]>(`/items/${itemId}/distributors`),
  addDistributor: (name: string) => api.post('/distributors', null, { params: { name } }),
  addDistributorPrice: (distributorId: number, itemId: number, cost: number) => 
    api.post(`/distributors/${distributorId}/items`, null, { params: { itemId, cost } }),
  updateDistributorPrice: (distributorId: number, itemId: number, cost: number) => 
    api.put(`/distributors/${distributorId}/items/${itemId}`, null, { params: { cost } }),
  deleteDistributor: (id: number) => api.delete(`/distributors/${id}`),
  deleteDistributorPrice: (distributorId: number, itemId: number) => 
    api.delete(`/distributors/${distributorId}/items/${itemId}`),

  // Special
  getCheapestRestock: (itemId: number, quantity: number) => 
    api.get(`/items/${itemId}/cheapest`, { params: { quantity } }),

  // Export
  exportTableToCsv: (tableName: string) => 
    api.get('/export/csv', { params: { table: tableName }, responseType: 'blob' }),
};
