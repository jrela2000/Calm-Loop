import { router } from 'expo-router';
import { useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, { useAnimatedStyle, useReducedMotion, useSharedValue, withDelay, withTiming } from 'react-native-reanimated';

import { AppText } from '@/components/AppText';
import { Appear, EmptyState, SectionLabel } from '@/components/Bits';
import { Card } from '@/components/Card';
import { Screen } from '@/components/Screen';
import { currentStreak, localDayKey, longestStreak } from '@/lib/streak';
import { activityTimestamps, useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { motion, radius, space } from '@/theme/tokens';

function Stat({ label, value, index }: { label: string; value: number | string; index: number }) {
  return (
    <View style={{ flex: 1 }}>
      <Appear index={index}>
        <Card style={styles.stat}>
          <AppText variant="display">{value}</AppText>
          <AppText variant="caption" tone="secondary">
            {label}
          </AppText>
        </Card>
      </Appear>
    </View>
  );
}

/** One bar per day for the last 14 days; bars grow in on first view. */
function Bar({ fraction, index, isToday }: { fraction: number; index: number; isToday: boolean }) {
  const t = useTheme();
  const reduced = useReducedMotion();
  const h = useSharedValue(reduced ? fraction : 0);
  useEffect(() => {
    h.set(reduced ? fraction : withDelay(index * 30, withTiming(fraction, { duration: motion.slow, easing: motion.easing })));
  }, [fraction, index, reduced, h]);
  const style = useAnimatedStyle(() => ({ height: `${Math.max(6, h.get() * 100)}%` }));
  return (
    <View style={styles.barSlot}>
      <Animated.View style={[styles.bar, { backgroundColor: fraction > 0 ? (isToday ? t.blueStrong : t.blue) : t.hairline }, style]} />
    </View>
  );
}

export default function Progress() {
  const entries = useAppStore((s) => s.entries);
  const breathing = useAppStore((s) => s.breathing);
  const grounding = useAppStore((s) => s.grounding);
  const stamps = activityTimestamps({ entries, breathing, grounding });
  const total = stamps.length;

  const days = Array.from({ length: 14 }, (_, i) => {
    const d = new Date();
    d.setDate(d.getDate() - (13 - i));
    return localDayKey(d.getTime());
  });
  const counts = new Map<string, number>();
  stamps.forEach((ts) => counts.set(localDayKey(ts), (counts.get(localDayKey(ts)) ?? 0) + 1));
  const max = Math.max(1, ...days.map((d) => counts.get(d) ?? 0));
  const rated = breathing.filter((b) => b.outcome);
  const better = rated.filter((b) => b.outcome === 'better').length;

  return (
    <Screen edges={['top']} testID="progress-tab">
      <AppText variant="display">Progress</AppText>
      {total === 0 ? (
        <EmptyState
          icon="leaf-outline"
          title="Nothing here yet"
          body="Every breathing round, grounding session and journal entry shows up here. Small steps add up."
          actionLabel="Try box breathing"
          onAction={() => router.push('/breathing')}
        />
      ) : (
        <>
          <View style={styles.row}>
            <Stat index={0} label="Current streak" value={currentStreak(stamps)} />
            <Stat index={1} label="Best streak" value={longestStreak(stamps)} />
          </View>
          <View style={styles.row}>
            <Stat index={2} label="Breathing" value={breathing.length} />
            <Stat index={3} label="Grounding" value={grounding.length} />
            <Stat index={4} label="Journal" value={entries.length} />
          </View>

          <SectionLabel>Last 14 days</SectionLabel>
          <Card>
            <View style={styles.chart} accessibilityLabel={`Activity over the last 14 days: ${days.filter((d) => counts.has(d)).length} active days`}>
              {days.map((d, i) => (
                <Bar key={d} index={i} fraction={(counts.get(d) ?? 0) / max} isToday={i === days.length - 1} />
              ))}
            </View>
          </Card>

          {rated.length > 0 ? (
            <Card tone="sage">
              <AppText variant="heading">
                You felt better after {better} of {rated.length} breathing sessions
              </AppText>
              <AppText variant="caption" tone="secondary" style={{ marginTop: space.xs }}>
                Noticing what helps is a skill in itself.
              </AppText>
            </Card>
          ) : null}

          <Card>
            <AppText variant="caption" tone="secondary">
              🔒 All of this lives only on your device. CalmLoop has no account and no servers storing your data.
            </AppText>
          </Card>
        </>
      )}
    </Screen>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: space.md },
  stat: { alignItems: 'flex-start', gap: space.xs },
  chart: { flexDirection: 'row', alignItems: 'flex-end', height: 120, gap: 4 },
  barSlot: { flex: 1, height: '100%', justifyContent: 'flex-end' },
  bar: { borderRadius: radius.sm / 2, width: '100%' },
});
