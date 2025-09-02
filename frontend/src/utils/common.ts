/**
 * Common utility functions used across React components
 */

// Text utilities
export const truncateText = (text: string, maxLength: number = 25): string => {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// Stock status utilities
export interface StockStatus {
  label: string;
  color: 'error' | 'warning' | 'success';
}

export const getStockStatus = (stock: number, capacity: number): StockStatus => {
  if (stock === 0) return { label: 'Out of Stock', color: 'error' };
  if (stock > capacity) return { label: 'Overstocked', color: 'warning' };
  if (stock < capacity * 0.35) return { label: 'Low Stock', color: 'warning' };
  return { label: 'Good', color: 'success' };
};

// Form validation utilities
export const validateRequired = (value: string, fieldName: string): string | null => {
  if (!value || value.trim().length === 0) {
    return `${fieldName} is required`;
  }
  return null;
};

export const validateNumber = (value: string, fieldName: string, min?: number, max?: number): string | null => {
  const num = parseFloat(value);
  if (isNaN(num)) {
    return `${fieldName} must be a valid number`;
  }
  if (min !== undefined && num < min) {
    return `${fieldName} must be at least ${min}`;
  }
  if (max !== undefined && num > max) {
    return `${fieldName} must be no more than ${max}`;
  }
  return null;
};

export const validateInteger = (value: string, fieldName: string, min?: number, max?: number): string | null => {
  const num = parseInt(value, 10);
  if (isNaN(num) || !Number.isInteger(num)) {
    return `${fieldName} must be a valid integer`;
  }
  if (min !== undefined && num < min) {
    return `${fieldName} must be at least ${min}`;
  }
  if (max !== undefined && num > max) {
    return `${fieldName} must be no more than ${max}`;
  }
  return null;
};

// Date/time utilities
export const formatTimestamp = (timestamp: number): string => {
  return new Date(timestamp).toLocaleTimeString();
};

export const formatDate = (timestamp: number): string => {
  return new Date(timestamp).toLocaleDateString();
};

export const formatDateTime = (timestamp: number): string => {
  return new Date(timestamp).toLocaleString();
};

// Error handling utilities
export const getErrorMessage = (error: any): string => {
  if (error.response?.data?.error) {
    return error.response.data.error;
  }
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.message) {
    return error.message;
  }
  return 'An unexpected error occurred';
};

// Array utilities
export const groupBy = <T, K extends keyof any>(
  array: T[],
  keySelector: (item: T) => K
): Record<K, T[]> => {
  return array.reduce((groups, item) => {
    const key = keySelector(item);
    if (!groups[key]) {
      groups[key] = [];
    }
    groups[key].push(item);
    return groups;
  }, {} as Record<K, T[]>);
};

// Local storage utilities
export const getFromLocalStorage = <T>(key: string, defaultValue: T): T => {
  try {
    const item = window.localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch (error) {
    console.warn(`Error reading from localStorage for key "${key}":`, error);
    return defaultValue;
  }
};

export const setToLocalStorage = <T>(key: string, value: T): void => {
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.warn(`Error writing to localStorage for key "${key}":`, error);
  }
};

export const removeFromLocalStorage = (key: string): void => {
  try {
    window.localStorage.removeItem(key);
  } catch (error) {
    console.warn(`Error removing from localStorage for key "${key}":`, error);
  }
};
