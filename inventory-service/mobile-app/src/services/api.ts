import { InventoryItem, RestockRequest } from '../types/inventory';

// Configure this to point to your Inventory Service backend.
// For local development: http://localhost:5000
// For Android emulator: http://10.0.2.2:5000
// For production: replace with your deployed API URL
const API_BASE_URL = 'http://localhost:5000';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API error ${response.status}: ${errorText}`);
  }
  return response.json();
}

export const inventoryApi = {
  async getAll(): Promise<InventoryItem[]> {
    const response = await fetch(`${API_BASE_URL}/api/inventory`);
    return handleResponse<InventoryItem[]>(response);
  },

  async getByProductId(productId: number): Promise<InventoryItem> {
    const response = await fetch(`${API_BASE_URL}/api/inventory/product/${productId}`);
    return handleResponse<InventoryItem>(response);
  },

  async restock(productId: number, request: RestockRequest): Promise<InventoryItem> {
    const response = await fetch(`${API_BASE_URL}/api/inventory/product/${productId}/restock`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    return handleResponse<InventoryItem>(response);
  },

  async getLowStock(): Promise<InventoryItem[]> {
    const response = await fetch(`${API_BASE_URL}/api/inventory/low-stock`);
    return handleResponse<InventoryItem[]>(response);
  },

  async healthCheck(): Promise<boolean> {
    try {
      const response = await fetch(`${API_BASE_URL}/health`);
      return response.ok;
    } catch {
      return false;
    }
  },
};
