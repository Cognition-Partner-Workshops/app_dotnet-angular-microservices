import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Inventory</h2>
    <table *ngIf="items.length">
      <thead>
        <tr>
          <th>Product</th>
          <th>Product ID</th>
          <th>On Hand</th>
          <th>Reorder Level</th>
          <th>Location</th>
          <th>Last Restocked</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let i of items" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{i.productName}}</td>
          <td>{{i.productId}}</td>
          <td>{{i.quantityOnHand}}</td>
          <td>{{i.reorderLevel}}</td>
          <td>{{i.warehouseLocation}}</td>
          <td>{{i.lastRestocked | date}}</td>
          <td>
            <span class="badge" [class.badge-warning]="i.quantityOnHand <= i.reorderLevel" [class.badge-ok]="i.quantityOnHand > i.reorderLevel">
              {{i.quantityOnHand <= i.reorderLevel ? 'Low Stock' : 'OK'}}
            </span>
          </td>
          <td>
            <input type="number" [(ngModel)]="restockQty[i.productId]" min="1" placeholder="Qty" style="width:60px; margin-right:4px;">
            <button (click)="restock(i.productId)">Restock</button>
          </td>
        </tr>
      </tbody>
    </table>
  `
})
export class InventoryListComponent implements OnInit {
  items: any[] = [];
  restockQty: { [key: number]: number } = {};

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.http.get<any[]>('/api/inventory').subscribe(data => this.items = data);
  }

  restock(productId: number) {
    const qty = this.restockQty[productId] || 0;
    if (qty <= 0) return;
    this.http.post(`/api/inventory/product/${productId}/restock`, { quantity: qty })
      .subscribe(() => {
        this.restockQty[productId] = 0;
        this.loadInventory();
      });
  }
}
