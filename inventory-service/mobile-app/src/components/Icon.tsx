import React from 'react';
import { Ionicons } from '@expo/vector-icons';

interface IconProps {
  name: string;
  size?: number;
  color?: string;
}

export function Icon({ name, size = 16, color }: IconProps) {
  return (
    <Ionicons
      name={name as keyof typeof Ionicons.glyphMap}
      size={size}
      color={color}
    />
  );
}
