import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

/**
 * Root component for the Inventory Service Angular SPA.
 *
 * Renders the top-level navigation bar with links to the inventory list
 * and restock pages, and hosts a `<router-outlet>` for child route views.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav>
      <h1>Inventory Service</h1>
      <a routerLink="/inventory">Inventory</a>
      <a routerLink="/restock">Restock</a>
    </nav>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {
  /** Application title displayed in the navigation header. */
  title = 'Inventory Service';
}
