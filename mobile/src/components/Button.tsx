import Ionicons from '@expo/vector-icons/Ionicons';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

import { AppText } from './AppText';
import { PressableScale } from './PressableScale';

type Props = {
  label: string;
  onPress: () => void;
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  icon?: keyof typeof Ionicons.glyphMap;
  disabled?: boolean;
  loading?: boolean;
  testID?: string;
  accessibilityHint?: string;
  feedbackKind?: 'tap' | 'none';
};

export function Button({ label, onPress, variant = 'primary', icon, disabled, loading, testID, accessibilityHint, feedbackKind }: Props) {
  const t = useTheme();
  const bg = { primary: t.blue, secondary: t.surface, danger: t.danger, ghost: 'transparent' }[variant];
  const fg = { primary: t.onAccent, secondary: t.text, danger: '#FFFFFF', ghost: t.blueStrong }[variant];
  return (
    <PressableScale
      testID={testID}
      onPress={onPress}
      disabled={disabled || loading}
      feedbackKind={feedbackKind}
      accessibilityLabel={label}
      accessibilityHint={accessibilityHint}
      accessibilityState={{ disabled: !!(disabled || loading), busy: !!loading }}
      style={[
        styles.base,
        { backgroundColor: bg },
        variant === 'secondary' && { borderWidth: 1, borderColor: t.border },
        variant === 'primary' && { shadowColor: t.shadow, shadowOpacity: 1, shadowRadius: 14, shadowOffset: { width: 0, height: 6 }, elevation: 3 },
      ]}>
      <View style={styles.row}>
        {loading ? <ActivityIndicator color={fg} /> : icon ? <Ionicons name={icon} size={20} color={fg} /> : null}
        <AppText variant="bodyStrong" style={{ color: fg }}>
          {label}
        </AppText>
      </View>
    </PressableScale>
  );
}

const styles = StyleSheet.create({
  base: { minHeight: 54, borderRadius: radius.lg, paddingHorizontal: space.xl, justifyContent: 'center' },
  row: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: space.sm },
});
