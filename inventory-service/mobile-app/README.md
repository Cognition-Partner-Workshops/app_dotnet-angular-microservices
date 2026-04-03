# Inventory Mobile App

A React Native (Expo) mobile app for the Inventory Service microservice. Provides a native mobile experience for managing inventory stock levels, restocking products, and monitoring low-stock alerts.

## Screenshots

The app features two main screens:
- **Inventory** — Browse all products with stock levels, warehouse locations, and restock capability
- **Low Stock** — View items at or below their reorder level with visual alerts

## Tech Stack

- **Framework**: React Native with Expo SDK 50
- **Navigation**: React Navigation (Bottom Tabs)
- **Language**: TypeScript
- **Icons**: Ionicons via @expo/vector-icons (native) with emoji fallback (web)
- **Platforms**: iOS, Android, Web

## Getting Started

### Prerequisites
- Node.js 18+
- Expo CLI (`npm install -g expo-cli`)
- The Inventory Service backend running (see parent README)

### Install Dependencies

```bash
cd mobile-app
npm install
```

### Configure API URL

The app automatically selects the correct API URL per platform:
- **Web / iOS Simulator** → `http://localhost:5000`
- **Android Emulator** → `http://10.0.2.2:5000` (Android's host loopback alias)
- **Physical device / Production** → set the `EXPO_PUBLIC_API_URL` environment variable:

```bash
# Point to a deployed backend for physical devices
EXPO_PUBLIC_API_URL=https://your-api.example.com npx expo start
```

### Run the App

```bash
# Start Expo dev server
npm start

# Run on web (for quick testing)
npm run web

# Run on iOS simulator
npm run ios

# Run on Android emulator
npm run android
```

## Features

### Inventory List
- View all products with stock levels, SKU, warehouse location, and last restocked date
- Restock any product with a configurable quantity
- Pull-to-refresh to reload data
- Low-stock items are visually highlighted with a red border and badge

### Low Stock Alerts
- Dedicated view for items at or below their reorder level
- Quick stats showing on-hand quantity vs reorder level
- Green checkmark state when all items are sufficiently stocked

### Connectivity
- Automatic API health checking
- Offline banner when the backend is unreachable
- Error states with tap-to-retry

## Project Structure

```
mobile-app/
  App.tsx                  # Root component with navigation
  app.json                 # Expo configuration
  src/
    components/
      InventoryCard.tsx     # Product card with restock controls
      StatusBanner.tsx      # API connectivity banner
    screens/
      InventoryListScreen.tsx  # Main inventory list
      LowStockScreen.tsx       # Low stock alerts
    services/
      api.ts               # API client for the Inventory Service
    types/
      inventory.ts         # TypeScript interfaces
```

## License

MIT
