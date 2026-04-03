export interface InventoryItem {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  quantityOnHand: number;
  reorderLevel: number;
  warehouseLocation: string;
  lastRestocked: string;
}

export interface RestockRequest {
  quantity: number;
}
