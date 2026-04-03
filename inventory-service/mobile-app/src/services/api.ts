import { Platform } from 'react-native';
import { InventoryItem, RestockRequest } from '../types/inventory';

// Auto-detect the correct API URL based on platform:
//   Web / iOS Simulator  → localhost
//   Android Emulator      → 10.0.2.2 (special alias for host loopback)
//   Physical device / Prod → override via EXPO_PUBLIC_API_URL env var
function getApiBaseUrl(): string {
  // Allow explicit override (set in .env or app.config.js)
  const envUrl: string | undefined = process.env.EXPO_PUBLIC_API_URL;
  if (envUrl) return envUrl;

  if (Platform.OS === 'android') {
    return 'http://10.0.2.2:5000';
  }
  // iOS simulator & web both resolve localhost to the host machine
  return 'http://localhost:5000';
}

const API_BASE_URL = getApiBaseUrl();

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
