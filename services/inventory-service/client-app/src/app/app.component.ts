import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav>
      <h1>Inventory Service</h1>
      <a routerLink="/inventory" style="color: white; margin-left: 24px;">All Items</a>
      <a routerLink="/low-stock" style="color: #ffcc80; margin-left: 16px;">Low Stock</a>
    </nav>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {
  title = 'Inventory Service';
}
