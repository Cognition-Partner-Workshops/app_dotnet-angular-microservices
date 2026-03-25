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
    <div class="actions">
      <button (click)="loadInventory()">Refresh</button>
      <button (click)="showLowStock = !showLowStock">
        {{ showLowStock ? 'Show All' : 'Show Low Stock' }}
      </button>
    </div>
    <table *ngIf="items.length">
      <thead>
        <tr>
          <th>Product</th>
          <th>On Hand</th>
          <th>Reorder Level</th>
          <th>Location</th>
          <th>Last Restocked</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let i of filteredItems" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{ i.productName }}</td>
          <td>{{ i.quantityOnHand }}</td>
          <td>{{ i.reorderLevel }}</td>
          <td>{{ i.warehouseLocation }}</td>
          <td>{{ i.lastRestocked | date }}</td>
          <td>
            <input type="number" [(ngModel)]="restockQuantity" min="1" placeholder="Qty" style="width:60px">
            <button (click)="restock(i.productId)">Restock</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p *ngIf="!items.length">No inventory records found.</p>
  `
})
export class InventoryListComponent implements OnInit {
  items: any[] = [];
  showLowStock = false;
  restockQuantity = 10;
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.http.get<any[]>(`${this.baseUrl}/api/inventory`).subscribe(data => this.items = data);
  }

  get filteredItems() {
    if (!this.showLowStock) return this.items;
    return this.items.filter(i => i.quantityOnHand <= i.reorderLevel);
  }

  restock(productId: number) {
    this.http.post(`${this.baseUrl}/api/inventory/product/${productId}/restock`, { quantity: this.restockQuantity })
      .subscribe(() => this.loadInventory());
  }
}
