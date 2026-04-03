import React from 'react';
import { Text, StyleSheet } from 'react-native';

const emojiMap: Record<string, string> = {
  'cube-outline': '\u{1F4E6}',
  'alert-circle-outline': '\u{1F534}',
  'location-outline': '\u{1F4CD}',
  'time-outline': '\u{1F552}',
  'add-circle-outline': '\u{2795}',
  'cloud-offline-outline': '\u{2601}',
  'warning': '\u{26A0}',
  'warning-outline': '\u{26A0}',
  'checkmark-circle': '\u{2705}',
};

interface IconProps {
  name: string;
  size?: number;
  color?: string;
}

export function Icon({ name, size = 16, color }: IconProps) {
  const emoji = emojiMap[name] ?? '\u{2022}';
  return (
    <Text style={[styles.icon, { fontSize: size * 0.85 }, color ? { color } : undefined]}>
      {emoji}
    </Text>
  );
}

const styles = StyleSheet.create({
  icon: {
    textAlign: 'center',
  },
});
