import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear, ToggleRow } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { BreathingOrbStatic } from '@/components/BreathingOrb';
import { Screen } from '@/components/Screen';
import { isAiConfigured } from '@/lib/ai';
import { feedback } from '@/lib/feedback';
import { AI_SUMMARY, MEDICAL_DISCLAIMER } from '@/lib/legal';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { space } from '@/theme/tokens';

/** First launch: what CalmLoop is (and isn't), how privacy works, and opt-in AI. */
export default function Welcome() {
  const t = useTheme();
  const acceptDisclaimer = useAppStore((s) => s.acceptDisclaimer);
  const aiAvailable = isAiConfigured();
  const [aiEnabled, setAiEnabled] = useState(false);

  return (
    <Screen
      testID="welcome-screen"
      footer={
        <Button
          testID="welcome-accept"
          label="I understand — let’s begin"
          onPress={() => {
            feedback.success();
            acceptDisclaimer(aiEnabled && aiAvailable);
          }}
          feedbackKind="none"
        />
      }>
      <Appear>
        <View style={styles.hero}>
          <BreathingOrbStatic size={120} />
          <AppText variant="display" style={styles.center}>
            Welcome to CalmLoop
          </AppText>
          <AppText tone="secondary" style={styles.center}>
            Breathing, grounding and journaling to help you ride out anxious moments.
          </AppText>
        </View>
      </Appear>

      <Appear index={1}>
        <Card tone="danger">
          <View style={styles.row}>
            <Ionicons name="medkit-outline" size={22} color={t.dangerStrong} />
            <AppText variant="heading">Not a substitute for care</AppText>
          </View>
          <AppText variant="caption" tone="secondary" style={styles.body}>
            {MEDICAL_DISCLAIMER}
          </AppText>
        </Card>
      </Appear>

      <Appear index={2}>
        <Card>
          <View style={styles.row}>
            <Ionicons name="lock-closed-outline" size={22} color={t.sageStrong} />
            <AppText variant="heading">Private by design</AppText>
          </View>
          <AppText variant="caption" tone="secondary" style={styles.body}>
            No account, no ads, no tracking. Your journal and history stay on this device.
          </AppText>
        </Card>
      </Appear>

      <Appear index={3}>
        <Card>
          <ToggleRow
            testID="welcome-ai-toggle"
            icon="sparkles-outline"
            title="AI reflections (optional)"
            value={aiEnabled && aiAvailable}
            onChange={setAiEnabled}
            disabled={!aiAvailable}
          />
          <AppText variant="caption" tone="secondary" style={styles.body}>
            {aiAvailable
              ? `${AI_SUMMARY} You can change this any time in Settings.`
              : 'AI reflections aren’t available in this build. Everything else works fully offline.'}
          </AppText>
        </Card>
      </Appear>

      <Button variant="ghost" label="Read the full Privacy Policy & Terms" onPress={() => router.push('/privacy')} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  hero: { alignItems: 'center', gap: space.md, paddingTop: space.lg },
  center: { textAlign: 'center' },
  row: { flexDirection: 'row', alignItems: 'center', gap: space.sm },
  body: { marginTop: space.sm },
});
