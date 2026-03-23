import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

/**
 * Root component for the Customer Service Angular SPA.
 *
 * Renders the top-level navigation bar with a link to the customer list
 * page and hosts a `<router-outlet>` for child route views.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav>
      <h1>Customer Service</h1>
      <a routerLink="/customers">Customers</a>
    </nav>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {
  /** Application title displayed in the navigation header. */
  title = 'Customer Service';
}
