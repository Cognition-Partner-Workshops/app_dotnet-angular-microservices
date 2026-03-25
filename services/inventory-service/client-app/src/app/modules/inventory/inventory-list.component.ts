import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService } from './inventory.service';
import { InventoryItem } from './inventory.model';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Inventory Management</h2>

    <div class="filters">
      <button (click)="loadAll()" [class.active]="!showLowStockOnly">All Items</button>
      <button (click)="loadLowStock()" [class.active]="showLowStockOnly">Low Stock</button>
    </div>

    <table *ngIf="items.length">
      <thead>
        <tr>
          <th>Product ID</th>
          <th>Product</th>
          <th>On Hand</th>
          <th>Reorder Level</th>
          <th>Location</th>
          <th>Last Restocked</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let i of items" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{ i.productId }}</td>
          <td>{{ i.productName }}</td>
          <td>{{ i.quantityOnHand }}</td>
          <td>{{ i.reorderLevel }}</td>
          <td>{{ i.warehouseLocation }}</td>
          <td>{{ i.lastRestocked | date }}</td>
          <td>
            <input type="number" [(ngModel)]="restockQuantities[i.productId]" placeholder="Qty" min="1" style="width:60px" />
            <button (click)="restock(i.productId)">Restock</button>
          </td>
        </tr>
      </tbody>
    </table>

    <p *ngIf="!items.length && !loading">No inventory items found.</p>
    <p *ngIf="loading">Loading...</p>
    <p *ngIf="message" class="message">{{ message }}</p>
  `,
  styles: [`
    .filters { margin-bottom: 1rem; }
    .filters button { margin-right: 0.5rem; padding: 0.4rem 1rem; cursor: pointer; }
    .filters button.active { font-weight: bold; background: #007bff; color: #fff; border: none; border-radius: 4px; }
    table { width: 100%; border-collapse: collapse; }
    th, td { padding: 0.5rem; border: 1px solid #ddd; text-align: left; }
    th { background: #f5f5f5; }
    .low-stock { background: #fff3cd; }
    .message { color: green; margin-top: 0.5rem; }
    input[type="number"] { margin-right: 0.25rem; }
  `]
})
export class InventoryListComponent implements OnInit {
  items: InventoryItem[] = [];
  restockQuantities: { [productId: number]: number } = {};
  showLowStockOnly = false;
  loading = false;
  message = '';

  constructor(private inventoryService: InventoryService) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.showLowStockOnly = false;
    this.loading = true;
    this.inventoryService.getAll().subscribe({
      next: data => { this.items = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadLowStock() {
    this.showLowStockOnly = true;
    this.loading = true;
    this.inventoryService.getLowStock().subscribe({
      next: data => { this.items = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  restock(productId: number) {
    const qty = this.restockQuantities[productId];
    if (!qty || qty < 1) return;
    this.inventoryService.restock(productId, qty).subscribe({
      next: () => {
        this.message = `Restocked product ${productId} with ${qty} units.`;
        this.restockQuantities[productId] = 0;
        this.showLowStockOnly ? this.loadLowStock() : this.loadAll();
        setTimeout(() => this.message = '', 3000);
      },
      error: () => { this.message = 'Restock failed.'; }
    });
  }
}
