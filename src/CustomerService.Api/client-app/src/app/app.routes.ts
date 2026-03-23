import { Routes } from '@angular/router';

/**
 * Application route definitions for the Customer Service SPA.
 *
 * Routes:
 * - `''`          – Redirects to `/customers` (default landing page).
 * - `/customers`  – Lazy-loads {@link CustomerListComponent} to display the customer directory.
 */
export const routes: Routes = [
  { path: '', redirectTo: '/customers', pathMatch: 'full' },
  { path: 'customers', loadComponent: () => import('./modules/customers/customer-list.component').then(m => m.CustomerListComponent) },
];
