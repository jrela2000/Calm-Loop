import { router } from 'expo-router';
import { useEffect, useRef } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, {
  Easing,
  useAnimatedStyle,
  useReducedMotion,
  useSharedValue,
  withDelay,
  withRepeat,
  withTiming,
} from 'react-native-reanimated';

import { AppText } from '@/components/AppText';
import { BreathingOrbStatic } from '@/components/BreathingOrb';
import { Screen } from '@/components/Screen';
import { feedback } from '@/lib/feedback';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { space } from '@/theme/tokens';

/** A word drifting inward and dissolving: tangled thoughts being gathered up. */
function Mote({ index }: { index: number }) {
  const t = useTheme();
  const p = useSharedValue(0);
  const angle = (index / 8) * Math.PI * 2;
  useEffect(() => {
    p.set(withDelay(index * 220, withRepeat(withTiming(1, { duration: 1800, easing: Easing.inOut(Easing.quad) }), -1)));
  }, [index, p]);
  const style = useAnimatedStyle(() => ({
    opacity: Math.sin(p.get() * Math.PI),
    transform: [{ translateX: Math.cos(angle) * 130 * (1 - p.get()) }, { translateY: Math.sin(angle) * 130 * (1 - p.get()) }],
  }));
  return <Animated.View style={[styles.mote, { backgroundColor: [t.blue, t.lavender, t.sage][index % 3] }, style]} />;
}

export default function Processing() {
  const job = useAppStore((s) => s.journalJob);
  const aiEnabled = useAppStore((s) => s.settings.aiEnabled);
  const resetJournalJob = useAppStore((s) => s.resetJournalJob);
  const reduced = useReducedMotion();
  const navigated = useRef(false);

  useEffect(() => {
    if (navigated.current) return;
    if (job.status === 'done') {
      navigated.current = true;
      feedback.success();
      router.replace(`/journal/${job.entryId}`);
      resetJournalJob();
    } else if (job.status === 'idle') {
      navigated.current = true;
      // Opened directly (e.g. a web refresh) with nothing in progress.
      router.replace('/journal');
    }
  }, [job, resetJournalJob]);

  return (
    <Screen scroll={false} testID="journal-processing">
      <View style={styles.stage}>
        <View style={styles.orb}>
          {!reduced && Array.from({ length: 8 }, (_, i) => <Mote key={i} index={i} />)}
          <BreathingOrbStatic size={110} />
        </View>
        <AppText variant="title" style={styles.center} accessibilityLiveRegion="polite">
          {aiEnabled ? 'Untangling your thoughts…' : 'Saving your entry…'}
        </AppText>
        <AppText tone="secondary" style={styles.center}>
          {aiEnabled
            ? 'Sorting it into what happened, what you thought, and how it felt.'
            : 'Adding a few reflection prompts to help you look at it from another angle.'}
        </AppText>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  stage: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: space.lg },
  orb: { alignItems: 'center', justifyContent: 'center' },
  mote: { position: 'absolute', width: 10, height: 10, borderRadius: 5 },
  center: { textAlign: 'center' },
});
