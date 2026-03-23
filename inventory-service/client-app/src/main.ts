/**
 * Entry point for the Inventory Service Angular application.
 *
 * Bootstraps {@link AppComponent} as a standalone component and registers
 * the application-wide providers:
 * - `provideHttpClient()` – enables `HttpClient` for REST API calls.
 * - `provideRouter(routes)` – configures client-side routing.
 */
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    provideRouter(routes)
  ]
});
