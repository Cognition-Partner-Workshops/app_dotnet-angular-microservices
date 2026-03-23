import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

/**
 * Displays a table of all customers fetched from the Customer API.
 *
 * Each row shows the customer’s name, email, phone number, and city/state.
 */
@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Customers</h2>
    <table *ngIf="customers.length">
      <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>City</th></tr></thead>
      <tbody>
        <tr *ngFor="let c of customers">
          <td>{{c.name}}</td><td>{{c.email}}</td><td>{{c.phone}}</td><td>{{c.city}}, {{c.state}}</td>
        </tr>
      </tbody>
    </table>
  `
})
export class CustomerListComponent implements OnInit {
  /** Full list of customer records retrieved from the backend. */
  customers: any[] = [];

  /** @param http Angular HTTP client used to call the Customer REST API. */
  constructor(private http: HttpClient) {}

  /** Fetches all customers from `GET /api/customers` on component initialisation. */
  ngOnInit() {
    this.http.get<any[]>(`${environment.apiUrl}/api/customers`).subscribe(data => this.customers = data);
  }
}
