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
    <h2>Inventory</h2>
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
        <tr *ngFor="let i of items" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{i.productName}}</td>
          <td>{{i.quantityOnHand}}</td>
          <td>{{i.reorderLevel}}</td>
          <td>{{i.warehouseLocation}}</td>
          <td>{{i.lastRestocked | date}}</td>
          <td>
            <input type="number" [(ngModel)]="restockQtyMap[i.productId]" min="1" placeholder="Qty" style="width:60px">
            <button (click)="restock(i.productId)">Restock</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p *ngIf="!items.length">No inventory items found.</p>
  `
})
export class InventoryListComponent implements OnInit {
  items: any[] = [];
  restockQtyMap: { [productId: number]: number } = {};
  constructor(private http: HttpClient) {}
  ngOnInit() {
    this.loadItems();
  }
  loadItems() {
    this.http.get<any[]>(`${environment.apiUrl}/api/inventory`).subscribe(data => {
      this.items = data;
      data.forEach(item => {
        if (!(item.productId in this.restockQtyMap)) {
          this.restockQtyMap[item.productId] = 10;
        }
      });
    });
  }
  restock(productId: number) {
    const qty = this.restockQtyMap[productId] || 10;
    this.http.post(`${environment.apiUrl}/api/inventory/product/${productId}/restock`, { quantity: qty })
      .subscribe(() => this.loadItems());
  }
}
