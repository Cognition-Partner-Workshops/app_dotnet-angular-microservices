import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Inventory Management</h2>
    <table *ngIf="items.length">
      <thead>
        <tr>
          <th>Product</th>
          <th>On Hand</th>
          <th>Reorder Level</th>
          <th>Location</th>
          <th>Last Restocked</th>
          <th>Restock</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let i of items" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{i.productName}}</td>
          <td>{{i.quantityOnHand}}</td>
          <td>{{i.reorderLevel}}</td>
          <td>{{i.warehouseLocation}}</td>
          <td>{{i.lastRestocked | date}}</td>
          <td>
            <input type="number" [(ngModel)]="restockQuantities[i.productId]" min="1" placeholder="Qty">
            <button (click)="restock(i.productId)">Restock</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p *ngIf="!items.length">Loading inventory...</p>

    <h3 style="margin-top: 24px;">Low Stock Items</h3>
    <table *ngIf="lowStockItems.length">
      <thead><tr><th>Product</th><th>On Hand</th><th>Reorder Level</th><th>Location</th></tr></thead>
      <tbody>
        <tr *ngFor="let i of lowStockItems" class="low-stock">
          <td>{{i.productName}}</td><td>{{i.quantityOnHand}}</td><td>{{i.reorderLevel}}</td><td>{{i.warehouseLocation}}</td>
        </tr>
      </tbody>
    </table>
    <p *ngIf="!lowStockItems.length && items.length">No low stock items.</p>
  `
})
export class InventoryListComponent implements OnInit {
  items: any[] = [];
  lowStockItems: any[] = [];
  restockQuantities: { [productId: number]: number } = {};

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadInventory();
    this.loadLowStock();
  }

  loadInventory() {
    this.http.get<any[]>(`${this.apiUrl}/api/inventory`).subscribe(data => this.items = data);
  }

  loadLowStock() {
    this.http.get<any[]>(`${this.apiUrl}/api/inventory/low-stock`).subscribe(data => this.lowStockItems = data);
  }

  restock(productId: number) {
    const qty = this.restockQuantities[productId] || 0;
    if (qty <= 0) return;
    this.http.post(`${this.apiUrl}/api/inventory/product/${productId}/restock`, { quantity: qty })
      .subscribe(() => {
        this.restockQuantities[productId] = 0;
        this.loadInventory();
        this.loadLowStock();
      });
  }
}
