import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Icon } from './src/components/Icon';
import { InventoryListScreen } from './src/screens/InventoryListScreen';
import { LowStockScreen } from './src/screens/LowStockScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <StatusBar style="light" />
      <Tab.Navigator
        screenOptions={{
          headerStyle: { backgroundColor: '#1a73e8' },
          headerTintColor: '#fff',
          headerTitleStyle: { fontWeight: '700' },
          tabBarActiveTintColor: '#1a73e8',
          tabBarInactiveTintColor: '#888',
          tabBarStyle: {
            paddingBottom: 4,
            height: 56,
          },
          tabBarLabelStyle: {
            fontSize: 12,
            fontWeight: '600',
          },
        }}
      >
        <Tab.Screen
          name="Inventory"
          component={InventoryListScreen}
          options={{
            headerTitle: 'Inventory',
            tabBarIcon: ({ color, size }) => (
              <Icon name="cube-outline" size={size} color={color} />
            ),
          }}
        />
        <Tab.Screen
          name="Low Stock"
          component={LowStockScreen}
          options={{
            headerTitle: 'Low Stock Alerts',
            tabBarIcon: ({ color, size }) => (
              <Icon name="warning-outline" size={size} color={color} />
            ),
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
