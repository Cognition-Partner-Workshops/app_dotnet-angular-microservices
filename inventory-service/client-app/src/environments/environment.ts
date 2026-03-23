/**
 * Development environment configuration for the Inventory Service client.
 *
 * `apiUrl` is empty so that API requests use a relative path, which is
 * proxied to the .NET backend via `proxy.conf.json` during `ng serve`.
 */
export const environment = {
  /** Indicates this is the development build (enables debugging aids). */
  production: false,
  /** Base URL for backend API calls. Empty string = same-origin / proxy. */
  apiUrl: ''
};
