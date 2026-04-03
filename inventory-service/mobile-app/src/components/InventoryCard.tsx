import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Alert } from 'react-native';
import { Icon } from './Icon';
import { InventoryItem } from '../types/inventory';

interface InventoryCardProps {
  item: InventoryItem;
  onRestock: (productId: number, quantity: number) => Promise<void>;
}

export function InventoryCard({ item, onRestock }: InventoryCardProps) {
  const [restockQty, setRestockQty] = useState('10');
  const [isRestocking, setIsRestocking] = useState(false);
  const isLowStock = item.quantityOnHand <= item.reorderLevel;

  const handleRestock = async () => {
    const qty = parseInt(restockQty, 10);
    if (isNaN(qty) || qty <= 0) {
      Alert.alert('Invalid Quantity', 'Please enter a positive number.');
      return;
    }
    setIsRestocking(true);
    try {
      await onRestock(item.productId, qty);
    } catch (error) {
      Alert.alert('Restock Failed', error instanceof Error ? error.message : 'Unknown error');
    } finally {
      setIsRestocking(false);
    }
  };

  const formattedDate = new Date(item.lastRestocked).toLocaleDateString();

  return (
    <View style={[styles.card, isLowStock && styles.lowStockCard]}>
      <View style={styles.header}>
        <View style={styles.titleRow}>
          <Text style={styles.productName}>{item.productName}</Text>
          {isLowStock && (
            <View style={styles.lowStockBadge}>
              <Icon name="warning" size={12} color="#fff" />
              <Text style={styles.lowStockBadgeText}>Low Stock</Text>
            </View>
          )}
        </View>
        <Text style={styles.sku}>{item.productSku}</Text>
      </View>

      <View style={styles.details}>
        <View style={styles.detailRow}>
          <Icon name="cube-outline" size={16} color="#666" />
          <Text style={styles.detailLabel}>On Hand:</Text>
          <Text style={[styles.detailValue, isLowStock && styles.lowStockValue]}>
            {item.quantityOnHand}
          </Text>
        </View>
        <View style={styles.detailRow}>
          <Icon name="alert-circle-outline" size={16} color="#666" />
          <Text style={styles.detailLabel}>Reorder Level:</Text>
          <Text style={styles.detailValue}>{item.reorderLevel}</Text>
        </View>
        <View style={styles.detailRow}>
          <Icon name="location-outline" size={16} color="#666" />
          <Text style={styles.detailLabel}>Location:</Text>
          <Text style={styles.detailValue}>{item.warehouseLocation}</Text>
        </View>
        <View style={styles.detailRow}>
          <Icon name="time-outline" size={16} color="#666" />
          <Text style={styles.detailLabel}>Last Restocked:</Text>
          <Text style={styles.detailValue}>{formattedDate}</Text>
        </View>
      </View>

      <View style={styles.restockSection}>
        <TextInput
          style={styles.restockInput}
          value={restockQty}
          onChangeText={setRestockQty}
          keyboardType="numeric"
          placeholder="Qty"
          editable={!isRestocking}
        />
        <TouchableOpacity
          style={[styles.restockButton, isRestocking && styles.restockButtonDisabled]}
          onPress={handleRestock}
          disabled={isRestocking}
        >
          <Icon name="add-circle-outline" size={18} color="#fff" />
          <Text style={styles.restockButtonText}>
            {isRestocking ? 'Restocking...' : 'Restock'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  lowStockCard: {
    borderLeftWidth: 4,
    borderLeftColor: '#e74c3c',
  },
  header: {
    marginBottom: 12,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  productName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1a1a2e',
    flex: 1,
  },
  lowStockBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#e74c3c',
    borderRadius: 12,
    paddingHorizontal: 8,
    paddingVertical: 3,
    gap: 4,
  },
  lowStockBadgeText: {
    color: '#fff',
    fontSize: 11,
    fontWeight: '600',
  },
  sku: {
    fontSize: 13,
    color: '#888',
    marginTop: 2,
  },
  details: {
    gap: 8,
    marginBottom: 14,
  },
  detailRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  detailLabel: {
    fontSize: 14,
    color: '#666',
  },
  detailValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  lowStockValue: {
    color: '#e74c3c',
    fontWeight: '700',
  },
  restockSection: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    borderTopWidth: 1,
    borderTopColor: '#eee',
    paddingTop: 12,
  },
  restockInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    width: 80,
    fontSize: 16,
    textAlign: 'center',
    backgroundColor: '#f9f9f9',
  },
  restockButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1a73e8',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    gap: 6,
    flex: 1,
    justifyContent: 'center',
  },
  restockButtonDisabled: {
    backgroundColor: '#93b8f0',
  },
  restockButtonText: {
    color: '#fff',
    fontSize: 15,
    fontWeight: '600',
  },
});
