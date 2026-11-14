import Ionicons from '@expo/vector-icons/Ionicons';
import { type ReactNode, useEffect } from 'react';
import { StyleSheet, Switch, View } from 'react-native';
import Animated, {
  FadeInDown,
  useAnimatedStyle,
  useReducedMotion,
  useSharedValue,
  withRepeat,
  withSequence,
  withTiming,
} from 'react-native-reanimated';

import { feedback } from '@/lib/feedback';
import { useTheme } from '@/theme/ThemeProvider';
import { motion, space } from '@/theme/tokens';

import { AppText } from './AppText';
import { Button } from './Button';

/** Staggered entrance used by every list of cards; skipped when Reduce Motion is on. */
export function Appear({ index = 0, children }: { index?: number; children: ReactNode }) {
  return (
    <Animated.View entering={FadeInDown.duration(motion.slow).delay(index * motion.stagger).easing(motion.easing)}>
      {children}
    </Animated.View>
  );
}

export function SectionLabel({ children }: { children: ReactNode }) {
  return (
    <AppText variant="label" tone="muted" style={styles.sectionLabel}>
      {typeof children === 'string' ? children.toUpperCase() : children}
    </AppText>
  );
}

export function ToggleRow({
  icon,
  title,
  description,
  value,
  onChange,
  disabled,
  testID,
}: {
  icon: keyof typeof Ionicons.glyphMap;
  title: string;
  description?: string;
  value: boolean;
  onChange: (v: boolean) => void;
  disabled?: boolean;
  testID?: string;
}) {
  const t = useTheme();
  return (
    <View style={styles.toggleRow}>
      <Ionicons name={icon} size={22} color={t.lavenderStrong} />
      <View style={{ flex: 1 }}>
        <AppText variant="bodyStrong">{title}</AppText>
        {description ? (
          <AppText variant="caption" tone="secondary">
            {description}
          </AppText>
        ) : null}
      </View>
      <Switch
        testID={testID}
        accessibilityLabel={title}
        value={value}
        disabled={disabled}
        onValueChange={(v) => {
          feedback.select();
          onChange(v);
        }}
        trackColor={{ true: t.blue, false: t.hairline }}
        thumbColor="#FFFFFF"
        // react-native-web ignores thumbColor for the "on" state.
        {...({ activeThumbColor: '#FFFFFF' } as object)}
        ios_backgroundColor={t.hairline}
      />
    </View>
  );
}

/** Friendly empty state with a softly floating icon and one clear next action. */
export function EmptyState({
  icon,
  title,
  body,
  actionLabel,
  onAction,
}: {
  icon: keyof typeof Ionicons.glyphMap;
  title: string;
  body: string;
  actionLabel?: string;
  onAction?: () => void;
}) {
  const t = useTheme();
  const reduced = useReducedMotion();
  const y = useSharedValue(0);
  useEffect(() => {
    if (reduced) return;
    y.set(withRepeat(withSequence(withTiming(-6, { duration: 1600 }), withTiming(0, { duration: 1600 })), -1));
  }, [reduced, y]);
  const float = useAnimatedStyle(() => ({ transform: [{ translateY: y.get() }] }));
  return (
    <View style={styles.empty}>
      <Animated.View style={[styles.emptyIcon, { backgroundColor: t.surface, borderColor: t.border }, float]}>
        <Ionicons name={icon} size={34} color={t.lavenderStrong} />
      </Animated.View>
      <AppText variant="heading" style={styles.center}>
        {title}
      </AppText>
      <AppText tone="secondary" style={styles.center}>
        {body}
      </AppText>
      {actionLabel && onAction ? <Button label={actionLabel} onPress={onAction} /> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  sectionLabel: { marginTop: space.sm },
  toggleRow: { flexDirection: 'row', alignItems: 'center', gap: space.md, minHeight: 48 },
  empty: { alignItems: 'center', gap: space.md, paddingVertical: space.xxl },
  emptyIcon: { width: 76, height: 76, borderRadius: 38, borderWidth: 1, alignItems: 'center', justifyContent: 'center' },
  center: { textAlign: 'center' },
});
