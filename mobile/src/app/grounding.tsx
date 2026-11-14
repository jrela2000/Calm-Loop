import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useEffect, useRef, useState } from 'react';
import { AccessibilityInfo, StyleSheet, TextInput, View } from 'react-native';
import Animated, { FadeInRight, FadeOutLeft, ZoomIn } from 'react-native-reanimated';

import { AppText } from '@/components/AppText';
import { Appear } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Celebration } from '@/components/Celebration';
import { Header } from '@/components/Header';
import { Screen } from '@/components/Screen';
import { feedback } from '@/lib/feedback';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { motion, radius, space } from '@/theme/tokens';

const SENSES = [
  { count: 5, verb: 'see', icon: 'eye-outline', hint: 'A color, a shadow, something small you hadn’t noticed.' },
  { count: 4, verb: 'feel', icon: 'hand-left-outline', hint: 'Your feet on the floor, fabric, the air on your skin.' },
  { count: 3, verb: 'hear', icon: 'ear-outline', hint: 'Near or far — traffic, a fan, your own breath.' },
  { count: 2, verb: 'smell', icon: 'flower-outline', hint: 'Or two smells you like, if nothing’s around.' },
  { count: 1, verb: 'taste', icon: 'cafe-outline', hint: 'Or take a sip of water and notice it.' },
] as const;

export default function Grounding() {
  const t = useTheme();
  const logGrounding = useAppStore((s) => s.logGrounding);
  const [step, setStep] = useState(0);
  const [answers, setAnswers] = useState<string[][]>(SENSES.map(() => []));
  const [draft, setDraft] = useState('');
  const logged = useRef(false);
  const done = step >= SENSES.length;
  const sense = SENSES[Math.min(step, SENSES.length - 1)];
  const current = answers[Math.min(step, SENSES.length - 1)];

  useEffect(() => {
    if (done && !logged.current) {
      logged.current = true;
      logGrounding(answers);
      feedback.success();
    }
  }, [done, answers, logGrounding]);

  function notice() {
    // Ignore extra taps while the finished step animates out, so a double tap can't skip a sense.
    if (done || answers[step].length >= sense.count) return;
    const value = draft.trim() || '✓';
    const next = answers.map((a, i) => (i === step ? [...a, value] : a));
    setAnswers(next);
    setDraft('');
    if (next[step].length >= sense.count) {
      feedback.step();
      AccessibilityInfo.announceForAccessibility(`${sense.count} things you ${sense.verb}. Done.`);
      setTimeout(() => setStep((s) => s + 1), 450);
    } else {
      feedback.select();
    }
  }

  if (done) {
    return (
      <Screen testID="grounding-done">
        <Header title="Grounding" />
        <Celebration icon="leaf" />
        <Appear>
          <AppText variant="title" style={styles.center}>
            You’re here, right now.
          </AppText>
          <AppText tone="secondary" style={styles.center}>
            You just brought your attention back to the present through all five senses. Take one slow breath.
          </AppText>
        </Appear>
        <Appear index={1}>
          <View style={{ gap: space.md }}>
            <Button label="Back home" onPress={() => router.back()} />
            <Button variant="secondary" label="Box breathing" icon="square-outline" onPress={() => router.replace('/breathing')} />
            <Button variant="ghost" label="Still struggling? Talk to someone" onPress={() => router.replace('/crisis')} />
          </View>
        </Appear>
      </Screen>
    );
  }

  return (
    <Screen
      testID="grounding-screen"
      footer={
        <Button
          testID="grounding-notice"
          feedbackKind="none"
          label={draft.trim() ? 'Add it' : 'Noticed one'}
          icon="checkmark"
          onPress={notice}
        />
      }>
      <Header title="5-4-3-2-1 grounding" />
      <View style={styles.steps} accessibilityLabel={`Step ${step + 1} of 5`}>
        {SENSES.map((s, i) => (
          <View key={s.verb} style={[styles.stepDot, { backgroundColor: i < step ? t.sage : i === step ? t.blue : t.hairline }]} />
        ))}
      </View>

      <Animated.View key={step} entering={FadeInRight.duration(motion.slow).easing(motion.easing)} exiting={FadeOutLeft.duration(motion.base)} style={{ gap: space.lg }}>
        <Card tone="sage" style={styles.prompt}>
          <View style={[styles.senseIcon, { backgroundColor: t.background }]}>
            <Ionicons name={sense.icon} size={30} color={t.sageStrong} />
          </View>
          <AppText variant="title" style={styles.center} accessibilityLiveRegion="polite">
            Name {sense.count} {sense.count === 1 ? 'thing' : 'things'} you can {sense.verb}
          </AppText>
          <AppText variant="caption" tone="secondary" style={styles.center}>
            {sense.hint}
          </AppText>
          <View style={styles.slots}>
            {Array.from({ length: sense.count }, (_, i) => {
              const filled = i < current.length;
              return (
                <View key={i} style={[styles.slot, { borderColor: t.sage, borderStyle: filled ? 'solid' : 'dashed', backgroundColor: filled ? t.sage : 'transparent' }]}>
                  {filled ? (
                    <Animated.View entering={ZoomIn.springify().damping(12)}>
                      <Ionicons name="checkmark" size={20} color={t.onAccent} />
                    </Animated.View>
                  ) : null}
                </View>
              );
            })}
          </View>
        </Card>

        <View>
          <TextInput
            testID="grounding-input"
            value={draft}
            onChangeText={setDraft}
            onSubmitEditing={notice}
            placeholder={`Optional: what do you ${sense.verb}?`}
            placeholderTextColor={t.textMuted}
            returnKeyType="done"
            accessibilityLabel={`Optional: describe something you ${sense.verb}`}
            style={[styles.input, { outlineColor: t.blue, color: t.text, backgroundColor: t.surface, borderColor: t.border }]}
          />
          <AppText variant="caption" tone="muted" style={{ marginTop: space.sm }}>
            Typing is optional — just noticing is enough. Tap the button for each one.
          </AppText>
        </View>

        {current.filter((a) => a !== '✓').length > 0 ? (
          <View style={styles.chips}>
            {current.filter((a) => a !== '✓').map((a, i) => (
              <Animated.View key={`${a}-${i}`} entering={ZoomIn.duration(motion.base)} style={[styles.chip, { backgroundColor: t.surface, borderColor: t.border }]}>
                <AppText variant="caption">{a}</AppText>
              </Animated.View>
            ))}
          </View>
        ) : null}
      </Animated.View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: { textAlign: 'center' },
  steps: { flexDirection: 'row', gap: space.sm },
  stepDot: { flex: 1, height: 6, borderRadius: 3 },
  prompt: { alignItems: 'center', gap: space.md, paddingVertical: space.xl },
  senseIcon: { width: 64, height: 64, borderRadius: 32, alignItems: 'center', justifyContent: 'center' },
  slots: { flexDirection: 'row', gap: space.sm, marginTop: space.sm },
  slot: { width: 40, height: 40, borderRadius: 20, borderWidth: 2, alignItems: 'center', justifyContent: 'center' },
  input: { borderWidth: 1, borderRadius: radius.md, paddingHorizontal: space.lg, minHeight: 52, fontSize: 16 },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: space.sm },
  chip: { borderWidth: 1, borderRadius: radius.pill, paddingHorizontal: space.md, paddingVertical: space.xs },
});
