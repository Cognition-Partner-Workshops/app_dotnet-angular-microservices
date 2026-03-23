import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

/**
 * Displays a table of all inventory items fetched from the Inventory API.
 *
 * Rows whose `quantityOnHand` is at or below their `reorderLevel` receive
 * the `low-stock` CSS class so they can be visually highlighted.
 */
@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Inventory</h2>
    <table *ngIf="items.length">
      <thead><tr><th>Product</th><th>SKU</th><th>On Hand</th><th>Reorder Level</th><th>Location</th><th>Last Restocked</th></tr></thead>
      <tbody>
        <tr *ngFor="let i of items" [class.low-stock]="i.quantityOnHand <= i.reorderLevel">
          <td>{{i.productName}}</td><td>{{i.productSku}}</td><td>{{i.quantityOnHand}}</td><td>{{i.reorderLevel}}</td><td>{{i.warehouseLocation}}</td><td>{{i.lastRestocked | date}}</td>
        </tr>
      </tbody>
    </table>
  `
})
export class InventoryListComponent implements OnInit {
  /** Full list of inventory items retrieved from the backend. */
  items: any[] = [];

  /** @param http Angular HTTP client used to call the Inventory REST API. */
  constructor(private http: HttpClient) {}

  /** Fetches all inventory items from `GET /api/inventory` on component initialisation. */
  ngOnInit() {
    this.http.get<any[]>(`${environment.apiUrl}/api/inventory`).subscribe(data => this.items = data);
  }
}
