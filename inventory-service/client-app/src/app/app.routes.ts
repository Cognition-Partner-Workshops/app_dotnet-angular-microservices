import { Routes } from '@angular/router';

/**
 * Application route definitions for the Inventory Service SPA.
 *
 * Routes:
 * - `''`          – Redirects to `/inventory` (default landing page).
 * - `/inventory`  – Lazy-loads {@link InventoryListComponent} to display current stock levels.
 * - `/restock`    – Lazy-loads {@link InventoryRestockComponent} to submit restock requests.
 */
export const routes: Routes = [
  { path: '', redirectTo: '/inventory', pathMatch: 'full' },
  { path: 'inventory', loadComponent: () => import('./modules/inventory/inventory-list.component').then(m => m.InventoryListComponent) },
  { path: 'restock', loadComponent: () => import('./modules/inventory/inventory-restock.component').then(m => m.InventoryRestockComponent) },
];
