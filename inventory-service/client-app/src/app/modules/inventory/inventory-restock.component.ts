import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

/**
 * Provides a simple form for restocking a product by its ID.
 *
 * The user enters a product ID and a quantity, then submits a
 * `POST /api/inventory/product/{id}/restock` request. Success and error
 * feedback is shown inline via the {@link message} property.
 */
@Component({
  selector: 'app-inventory-restock',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Restock Inventory</h2>
    <div class="restock-form">
      <label>Product ID: <input type="number" [(ngModel)]="productId" /></label>
      <label>Quantity: <input type="number" [(ngModel)]="quantity" /></label>
      <button (click)="restock()">Restock</button>
    </div>
    <div *ngIf="message" class="message">{{ message }}</div>
  `
})
export class InventoryRestockComponent {
  /** The numeric product identifier entered by the user. */
  productId = 0;
  /** The number of units to add to the product's stock. */
  quantity = 0;
  /** Feedback message displayed after a restock attempt (success or error). */
  message = '';

  /** @param http Angular HTTP client used to call the Inventory REST API. */
  constructor(private http: HttpClient) {}

  /**
   * Sends a restock request to the backend for the selected product.
   * Updates {@link message} with the result or error details.
   */
  restock() {
    this.http.post<any>(`${environment.apiUrl}/api/inventory/product/${this.productId}/restock`, { quantity: this.quantity })
      .subscribe({
        next: (data) => this.message = `Restocked product ${data.productName}. New quantity: ${data.quantityOnHand}`,
        error: (err) => this.message = `Error: ${err.error?.error || err.message}`
      });
  }
}
