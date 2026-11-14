import Ionicons from '@expo/vector-icons/Ionicons';
import { useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, {
  Easing,
  useAnimatedStyle,
  useReducedMotion,
  useSharedValue,
  withDelay,
  withSpring,
  withTiming,
} from 'react-native-reanimated';

import { useTheme } from '@/theme/ThemeProvider';
import { motion } from '@/theme/tokens';

const PARTICLES = 14;

function Particle({ index, color }: { index: number; color: string }) {
  const p = useSharedValue(0);
  const angle = (index / PARTICLES) * Math.PI * 2;
  const distance = 70 + (index % 3) * 18;
  useEffect(() => {
    p.set(withDelay(80, withTiming(1, { duration: 900, easing: Easing.out(Easing.cubic) })));
  }, [p]);
  const style = useAnimatedStyle(() => ({
    opacity: 1 - p.get(),
    transform: [
      { translateX: Math.cos(angle) * distance * p.get() },
      { translateY: Math.sin(angle) * distance * p.get() },
      { scale: 1 - p.get() * 0.5 },
    ],
  }));
  return <Animated.View style={[styles.particle, { backgroundColor: color }, style]} />;
}

/** A soft burst of light around a check mark: the "you did it" moment after a session. */
export function Celebration({ icon = 'checkmark' }: { icon?: keyof typeof Ionicons.glyphMap }) {
  const t = useTheme();
  const reduced = useReducedMotion();
  const scale = useSharedValue(reduced ? 1 : 0.4);
  useEffect(() => {
    if (!reduced) scale.set(withSpring(1, { ...motion.spring, damping: 10 }));
  }, [reduced, scale]);
  const badge = useAnimatedStyle(() => ({ transform: [{ scale: scale.get() }] }));
  const colors = [t.blue, t.sage, t.lavender];
  return (
    <View style={styles.wrap} accessibilityElementsHidden importantForAccessibility="no-hide-descendants">
      {!reduced && Array.from({ length: PARTICLES }, (_, i) => <Particle key={i} index={i} color={colors[i % 3]} />)}
      <Animated.View style={[styles.badge, { backgroundColor: t.sage }, badge]}>
        <Ionicons name={icon} size={44} color={t.onAccent} />
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { height: 200, alignItems: 'center', justifyContent: 'center' },
  particle: { position: 'absolute', width: 10, height: 10, borderRadius: 5 },
  badge: { width: 96, height: 96, borderRadius: 48, alignItems: 'center', justifyContent: 'center' },
});
