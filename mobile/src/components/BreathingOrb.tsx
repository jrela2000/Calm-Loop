import { LinearGradient } from 'expo-linear-gradient';
import { useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, {
  cancelAnimation,
  Easing,
  type SharedValue,
  useAnimatedStyle,
  useReducedMotion,
  useSharedValue,
  withRepeat,
  withSequence,
  withTiming,
} from 'react-native-reanimated';

import { useTheme } from '@/theme/ThemeProvider';
import { motion } from '@/theme/tokens';

export const MIN_SCALE = 0.62;

/** Idle orb used on welcome/home: breathes slowly on its own (6 s cycle). */
export function BreathingOrbStatic({ size = 140 }: { size?: number }) {
  const reduced = useReducedMotion();
  const s = useSharedValue(1);
  useEffect(() => {
    if (reduced) return;
    s.set(withRepeat(
      withSequence(
        withTiming(1.06, { duration: 3000, easing: motion.breathEasing }),
        withTiming(0.94, { duration: 3000, easing: motion.breathEasing }),
      ),
      -1,
    ));
    return () => cancelAnimation(s);
  }, [reduced, s]);
  return <Orb size={size} scale={s} />;
}

function Orb({ size, scale, glow }: { size: number; scale: SharedValue<number>; glow?: SharedValue<number> }) {
  const t = useTheme();
  const orbStyle = useAnimatedStyle(() => ({ transform: [{ scale: scale.get() }] }));
  const auraStyle = useAnimatedStyle(() => ({
    transform: [{ scale: 0.9 + scale.get() * 0.45 }],
    opacity: (glow?.get() ?? 0.5) * 0.55,
  }));
  return (
    <View style={{ width: size * 1.5, height: size * 1.5, alignItems: 'center', justifyContent: 'center' }}>
      <Animated.View
        style={[{ position: 'absolute', width: size, height: size, borderRadius: size / 2, backgroundColor: t.lavender }, auraStyle]}
      />
      <Animated.View style={[{ width: size, height: size, borderRadius: size / 2, overflow: 'hidden' }, orbStyle]}>
        <LinearGradient
          colors={[t.blue, t.lavender, t.sage]}
          start={{ x: 0.1, y: 0 }}
          end={{ x: 0.9, y: 1 }}
          style={StyleSheet.absoluteFill}
        />
        <LinearGradient
          colors={['rgba(255,255,255,0.38)', 'rgba(255,255,255,0)']}
          start={{ x: 0.15, y: 0.05 }}
          end={{ x: 0.6, y: 0.65 }}
          style={StyleSheet.absoluteFill}
        />
      </Animated.View>
    </View>
  );
}

export type Phase = 'inhale' | 'hold' | 'exhale' | 'rest';
export const PHASES: Phase[] = ['inhale', 'hold', 'exhale', 'rest'];

/**
 * Box-breathing visual: the orb swells on the inhale and settles on the exhale while a light
 * traces one side of the box per phase. With Reduce Motion on, only the glow changes.
 */
export function BreathingBox({
  phase,
  phaseMs,
  running,
  size = 190,
  children,
}: {
  phase: Phase;
  phaseMs: number;
  running: boolean;
  size?: number;
  children?: React.ReactNode;
}) {
  const t = useTheme();
  const reduced = useReducedMotion();
  const scale = useSharedValue(MIN_SCALE);
  const glow = useSharedValue(0.3);
  const progress = useSharedValue(0); // 0..4 around the box
  const box = size * 1.5;

  useEffect(() => {
    const i = PHASES.indexOf(phase);
    if (!running) {
      cancelAnimation(scale);
      cancelAnimation(progress);
      cancelAnimation(glow);
      return;
    }
    const timing = { duration: phaseMs, easing: motion.breathEasing };
    progress.set(i);
    progress.set(withTiming(i + 1, { duration: phaseMs, easing: Easing.linear }));
    if (phase === 'inhale') {
      if (!reduced) scale.set(withTiming(1, timing));
      glow.set(withTiming(1, timing));
    } else if (phase === 'exhale') {
      if (!reduced) scale.set(withTiming(MIN_SCALE, timing));
      glow.set(withTiming(0.3, timing));
    } else if (phase === 'hold') {
      glow.set(withRepeat(withSequence(withTiming(0.8, { duration: phaseMs / 2 }), withTiming(1, { duration: phaseMs / 2 })), 1));
    }
  }, [phase, phaseMs, running, reduced, scale, glow, progress]);

  const tracer = useAnimatedStyle(() => {
    const p = progress.get() % 4;
    const edge = Math.floor(p);
    const f = p - edge;
    const max = box - 16;
    // inhale: top → right, hold: down right side, exhale: bottom ← left, rest: up left side
    const x = edge === 0 ? f * max : edge === 1 ? max : edge === 2 ? (1 - f) * max : 0;
    const y = edge === 0 ? 0 : edge === 1 ? f * max : edge === 2 ? max : (1 - f) * max;
    return { transform: [{ translateX: x }, { translateY: y }], opacity: running ? 1 : 0.35 };
  });

  return (
    <View style={{ width: box, height: box, alignItems: 'center', justifyContent: 'center' }}>
      <View style={[StyleSheet.absoluteFill, styles.track, { borderColor: t.hairline, margin: 7 }]} />
      {!reduced && <Animated.View style={[styles.tracer, { backgroundColor: t.blueStrong, shadowColor: t.blue }, tracer]} />}
      <View style={StyleSheet.absoluteFill}>
        <View style={styles.center}>
          <Orb size={size} scale={scale} glow={glow} />
        </View>
      </View>
      <View style={[StyleSheet.absoluteFill, styles.center]}>{children}</View>
    </View>
  );
}

const styles = StyleSheet.create({
  track: { borderWidth: 2, borderRadius: 36 },
  tracer: { position: 'absolute', top: 0, left: 0, width: 16, height: 16, borderRadius: 8, shadowOpacity: 0.9, shadowRadius: 8, elevation: 4 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center' },
});
