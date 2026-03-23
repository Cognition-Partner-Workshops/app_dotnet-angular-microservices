/**
 * Development environment configuration for the Customer Service client.
 *
 * `apiUrl` points to the local .NET backend so that `ng serve` can
 * reach the API without a reverse proxy during development.
 */
export const environment = {
  /** Indicates this is the development build (enables debugging aids). */
  production: false,
  /** Base URL for backend API calls; points to the local .NET dev server. */
  apiUrl: 'http://localhost:5101'
};
