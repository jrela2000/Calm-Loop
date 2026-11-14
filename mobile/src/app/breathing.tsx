import { activateKeepAwakeAsync, deactivateKeepAwake } from 'expo-keep-awake';
import { router } from 'expo-router';
import { useEffect, useReducer, useRef } from 'react';
import { AccessibilityInfo, StyleSheet, View } from 'react-native';
import Animated, { FadeIn, FadeOut } from 'react-native-reanimated';

import { AppText } from '@/components/AppText';
import { Appear } from '@/components/Bits';
import { BreathingBox, type Phase, PHASES } from '@/components/BreathingOrb';
import { breathingReducer as reducer, initialBreathing as initial, PHASE_SECONDS } from '@/lib/breathing';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Celebration } from '@/components/Celebration';
import { Header } from '@/components/Header';
import { PressableScale } from '@/components/PressableScale';
import { Screen } from '@/components/Screen';
import { feedback } from '@/lib/feedback';
import { useAppStore } from '@/store/app-store';
import type { BreathingOutcome } from '@/store/types';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

const ROUND_OPTIONS = [3, 4, 6];
const LABEL: Record<Phase, string> = { inhale: 'Breathe in', hold: 'Hold', exhale: 'Breathe out', rest: 'Rest' };
const CUE: Record<Phase, string> = {
  inhale: 'Slowly through your nose',
  hold: 'Gently, no strain',
  exhale: 'Slowly through your mouth',
  rest: 'Empty and easy',
};

export default function Breathing() {
  const t = useTheme();
  const [s, dispatch] = useReducer(reducer, initial);
  const logBreathing = useAppStore((st) => st.logBreathing);
  const setOutcome = useAppStore((st) => st.setBreathingOutcome);
  const sessionId = useRef<string | null>(null);
  const [outcome, setOutcomeLocal] = useReducer((_: BreathingOutcome | null, v: BreathingOutcome | null) => v, null);
  const phase = PHASES[s.phase];

  useEffect(() => {
    if (s.status !== 'running') return;
    const id = setInterval(() => dispatch({ type: 'tick' }), 1000);
    // Keep the screen on for the session so the guide doesn't vanish mid-breath.
    activateKeepAwakeAsync('breathing').catch(() => {});
    return () => {
      clearInterval(id);
      deactivateKeepAwake('breathing').catch(() => {});
    };
  }, [s.status]);

  // Sound + haptic + screen-reader cue at the start of each phase.
  useEffect(() => {
    if (s.status !== 'running' || s.left !== PHASE_SECONDS) return;
    if (phase === 'inhale' && s.round > 1) feedback.round();
    feedback[phase]();
    AccessibilityInfo.announceForAccessibility(LABEL[phase]);
  }, [s.status, s.phase, s.round, s.left, phase]);

  useEffect(() => {
    if (s.status === 'done' && !sessionId.current) {
      sessionId.current = logBreathing(s.round);
      feedback.success();
    }
  }, [s.status, s.round, logBreathing]);

  const running = s.status === 'running';
  const progressPct = ((s.round - 1) * 4 + s.phase) / (s.rounds * 4);

  if (s.status === 'done') {
    return (
      <Screen testID="breathing-done">
        <Header title="Box breathing" />
        <Celebration />
        <Appear>
          <AppText variant="title" style={styles.center}>
            {s.round} {s.round === 1 ? 'round' : 'rounds'} complete
          </AppText>
          <AppText tone="secondary" style={styles.center}>
            Notice your shoulders, your jaw, your breath. How do you feel now?
          </AppText>
        </Appear>
        {!outcome ? (
          <Appear index={1}>
            <View style={styles.outcomes}>
              {(
                [
                  ['better', '😌', 'Calmer'],
                  ['same', '😐', 'About the same'],
                  ['worse', '😟', 'Still anxious'],
                ] as const
              ).map(([value, emoji, label]) => (
                <PressableScale
                  key={value}
                  testID={`outcome-${value}`}
                  feedbackKind="none"
                  onPress={() => {
                    feedback.select();
                    setOutcomeLocal(value);
                    if (sessionId.current) setOutcome(sessionId.current, value);
                  }}
                  accessibilityLabel={label}
                  containerStyle={{ flex: 1 }}
                  style={[styles.outcome, { backgroundColor: t.surface, borderColor: t.border }]}>
                  <AppText style={styles.emoji}>{emoji}</AppText>
                  <AppText variant="caption" style={styles.center}>
                    {label}
                  </AppText>
                </PressableScale>
              ))}
            </View>
          </Appear>
        ) : (
          <Animated.View entering={FadeIn} style={{ gap: space.md }}>
            {outcome === 'better' ? (
              <Card tone="sage">
                <AppText variant="heading">Well done.</AppText>
                <AppText variant="caption" tone="secondary">
                  Your nervous system just practiced settling. It gets easier each time.
                </AppText>
              </Card>
            ) : (
              <Card tone={outcome === 'worse' ? 'danger' : 'lavender'}>
                <AppText variant="heading">That’s okay. Let’s try something else.</AppText>
                <AppText variant="caption" tone="secondary">
                  Grounding can help when breathing isn’t enough
                  {outcome === 'worse' ? ', and it’s always okay to reach out to someone.' : '.'}
                </AppText>
              </Card>
            )}
            {outcome !== 'better' && <Button label="Try 5-4-3-2-1 grounding" icon="leaf-outline" onPress={() => router.replace('/grounding')} />}
            {outcome === 'worse' && <Button variant="danger" label="Talk to someone now" icon="call" onPress={() => router.replace('/crisis')} />}
            <Button variant="secondary" label="Another session" onPress={() => {
              sessionId.current = null;
              setOutcomeLocal(null);
              dispatch({ type: 'start' });
            }} />
            <Button variant="ghost" label="Back home" onPress={() => router.back()} />
          </Animated.View>
        )}
      </Screen>
    );
  }

  return (
    <Screen
      scroll={false}
      testID="breathing-screen"
      footer={
        s.status === 'ready' ? (
          <Button testID="breathing-start" label="Begin" icon="play" onPress={() => dispatch({ type: 'start' })} />
        ) : (
          <View style={styles.footerRow}>
            <View style={{ flex: 1 }}>
              <Button
                variant="secondary"
                label={running ? 'Pause' : 'Resume'}
                icon={running ? 'pause' : 'play'}
                onPress={() => dispatch({ type: running ? 'pause' : 'resume' })}
              />
            </View>
            <View style={{ flex: 1 }}>
              <Button variant="ghost" label="Finish" onPress={() => dispatch({ type: 'finish' })} />
            </View>
          </View>
        )
      }>
      <Header title="Box breathing" />
      <View style={[styles.progressTrack, { backgroundColor: t.hairline }]}>
        <View style={[styles.progressFill, { backgroundColor: t.blue, width: `${progressPct * 100}%` }]} />
      </View>

      <View style={styles.stage}>
        <BreathingBox phase={phase} phaseMs={PHASE_SECONDS * 1000} running={running}>
          {s.status === 'ready' ? (
            <AppText variant="title" tone="onAccent">
              4 · 4 · 4 · 4
            </AppText>
          ) : (
            <Animated.View key={`${s.round}-${s.phase}`} entering={FadeIn.duration(300)} exiting={FadeOut.duration(200)} style={styles.centerItems}>
              <AppText variant="display" tone="onAccent" accessibilityLiveRegion="polite">
                {s.left}
              </AppText>
            </Animated.View>
          )}
        </BreathingBox>
        <View style={styles.centerItems} accessibilityLiveRegion="polite">
          <AppText variant="title">{s.status === 'ready' ? 'Box breathing' : LABEL[phase]}</AppText>
          <AppText tone="secondary">
            {s.status === 'ready'
              ? 'Breathe in, hold, out and rest for 4 seconds each. Follow the light around the box.'
              : s.status === 'paused'
                ? 'Paused — take your time.'
                : `${CUE[phase]} · Round ${s.round} of ${s.rounds}`}
          </AppText>
        </View>
        {s.status === 'ready' ? (
          <View style={styles.rounds} accessibilityRole="radiogroup" accessibilityLabel="Number of rounds">
            {ROUND_OPTIONS.map((n) => (
              <PressableScale
                key={n}
                feedbackKind="none"
                onPress={() => {
                  feedback.select();
                  dispatch({ type: 'rounds', n });
                }}
                accessibilityRole="radio"
                accessibilityState={{ selected: s.rounds === n }}
                accessibilityLabel={`${n} rounds, about ${n * 16} seconds`}
                style={[
                  styles.chip,
                  { borderColor: s.rounds === n ? t.blue : t.border, backgroundColor: s.rounds === n ? t.blue : t.surface },
                ]}>
                <AppText variant="bodyStrong" tone={s.rounds === n ? 'onAccent' : 'primary'}>
                  {n} rounds
                </AppText>
              </PressableScale>
            ))}
          </View>
        ) : null}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: { textAlign: 'center' },
  centerItems: { alignItems: 'center', gap: space.xs },
  stage: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: space.xl },
  progressTrack: { height: 4, borderRadius: 2, overflow: 'hidden' },
  progressFill: { height: 4, borderRadius: 2 },
  footerRow: { flexDirection: 'row', gap: space.md },
  rounds: { flexDirection: 'row', gap: space.sm },
  chip: { paddingHorizontal: space.lg, minHeight: 44, justifyContent: 'center', borderRadius: radius.pill, borderWidth: 1 },
  outcomes: { flexDirection: 'row', gap: space.sm },
  outcome: { alignItems: 'center', gap: space.xs, padding: space.md, borderRadius: radius.md, borderWidth: 1 },
  emoji: { fontSize: 32, lineHeight: 40 },
});
