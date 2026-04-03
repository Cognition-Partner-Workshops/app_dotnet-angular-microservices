import React, { useCallback, useEffect, useState } from 'react';
import { View, FlatList, StyleSheet, Text, ActivityIndicator, RefreshControl } from 'react-native';
import { Icon } from '../components/Icon';
import { InventoryItem } from '../types/inventory';
import { inventoryApi } from '../services/api';
import { StatusBanner } from '../components/StatusBanner';

export function LowStockScreen() {
  const [items, setItems] = useState<InventoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [isConnected, setIsConnected] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadItems = useCallback(async () => {
    try {
      setError(null);
      const data = await inventoryApi.getLowStock();
      setItems(data);
      setIsConnected(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load low stock items');
      setIsConnected(false);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadItems();
  }, [loadItems]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    loadItems();
  }, [loadItems]);

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#e74c3c" />
        <Text style={styles.loadingText}>Checking stock levels...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <StatusBanner isConnected={isConnected} />
      {error ? (
        <View style={styles.centered}>
          <Text style={styles.errorText}>{error}</Text>
          <Text style={styles.retryText} onPress={loadItems}>Tap to retry</Text>
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <View style={styles.card}>
              <View style={styles.cardHeader}>
                <Icon name="warning" size={20} color="#e74c3c" />
                <Text style={styles.productName}>{item.productName}</Text>
              </View>
              <Text style={styles.sku}>{item.productSku}</Text>
              <View style={styles.statsRow}>
                <View style={styles.stat}>
                  <Text style={styles.statLabel}>On Hand</Text>
                  <Text style={styles.statValueDanger}>{item.quantityOnHand}</Text>
                </View>
                <View style={styles.stat}>
                  <Text style={styles.statLabel}>Reorder At</Text>
                  <Text style={styles.statValue}>{item.reorderLevel}</Text>
                </View>
                <View style={styles.stat}>
                  <Text style={styles.statLabel}>Location</Text>
                  <Text style={styles.statValue}>{item.warehouseLocation}</Text>
                </View>
              </View>
            </View>
          )}
          contentContainerStyle={styles.list}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#e74c3c']} />
          }
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Icon name="checkmark-circle" size={64} color="#27ae60" />
              <Text style={styles.emptyTitle}>All stocked up!</Text>
              <Text style={styles.emptySubtitle}>No items are below reorder level.</Text>
            </View>
          }
          ListHeaderComponent={
            items.length > 0 ? (
              <Text style={styles.headerText}>
                {items.length} item{items.length !== 1 ? 's' : ''} need attention
              </Text>
            ) : null
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f6fa',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  list: {
    paddingVertical: 8,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#666',
  },
  errorText: {
    fontSize: 16,
    color: '#e74c3c',
    textAlign: 'center',
    marginBottom: 12,
  },
  retryText: {
    fontSize: 16,
    color: '#1a73e8',
    fontWeight: '600',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 6,
    borderLeftWidth: 4,
    borderLeftColor: '#e74c3c',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  productName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1a1a2e',
  },
  sku: {
    fontSize: 13,
    color: '#888',
    marginTop: 2,
    marginLeft: 28,
  },
  statsRow: {
    flexDirection: 'row',
    marginTop: 14,
    gap: 16,
  },
  stat: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 10,
  },
  statLabel: {
    fontSize: 11,
    color: '#888',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  statValue: {
    fontSize: 18,
    fontWeight: '700',
    color: '#333',
    marginTop: 4,
  },
  statValueDanger: {
    fontSize: 18,
    fontWeight: '700',
    color: '#e74c3c',
    marginTop: 4,
  },
  emptyContainer: {
    alignItems: 'center',
    paddingTop: 60,
  },
  emptyTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: '#27ae60',
    marginTop: 16,
  },
  emptySubtitle: {
    fontSize: 15,
    color: '#888',
    marginTop: 8,
  },
  headerText: {
    fontSize: 13,
    color: '#e74c3c',
    fontWeight: '600',
    paddingHorizontal: 20,
    paddingVertical: 8,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
});
