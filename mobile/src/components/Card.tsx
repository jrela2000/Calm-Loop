import { type ReactNode } from 'react';
import { type StyleProp, StyleSheet, View, type ViewStyle } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

import { PressableScale } from './PressableScale';

type Props = {
  children: ReactNode;
  onPress?: () => void;
  tone?: 'default' | 'danger' | 'blue' | 'sage' | 'lavender';
  style?: StyleProp<ViewStyle>;
  testID?: string;
  accessibilityLabel?: string;
  accessibilityHint?: string;
};

export function Card({ children, onPress, tone = 'default', style, testID, accessibilityLabel, accessibilityHint }: Props) {
  const t = useTheme();
  const tint = {
    default: { backgroundColor: t.surface, borderColor: t.border },
    danger: { backgroundColor: t.dangerSurface, borderColor: t.dangerBorder },
    blue: { backgroundColor: t.surface, borderColor: t.blue },
    sage: { backgroundColor: t.surface, borderColor: t.sage },
    lavender: { backgroundColor: t.surface, borderColor: t.lavender },
  }[tone];
  const cardStyle = [styles.card, tint, { shadowColor: t.shadow }, style];
  if (onPress) {
    return (
      <PressableScale testID={testID} onPress={onPress} style={cardStyle} scaleTo={0.98} accessibilityLabel={accessibilityLabel} accessibilityHint={accessibilityHint}>
        {children}
      </PressableScale>
    );
  }
  return (
    <View testID={testID} style={cardStyle}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: radius.lg,
    borderWidth: 1,
    padding: space.lg,
    shadowOpacity: 0.6,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 8 },
    elevation: 2,
  },
});
