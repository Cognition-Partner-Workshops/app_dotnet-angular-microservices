/**
 * Production environment configuration for the Customer Service client.
 *
 * `apiUrl` is empty because the Angular SPA is served from the same origin
 * as the .NET API (via `wwwroot`), so relative paths resolve correctly.
 */
export const environment = {
  /** Indicates this is a production build (disables debugging aids, enables optimisations). */
  production: true,
  /** Base URL for backend API calls. Empty string = same-origin. */
  apiUrl: ''
};
