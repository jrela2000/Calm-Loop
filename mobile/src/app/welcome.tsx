import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear, ToggleRow } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { BreathingOrbStatic } from '@/components/BreathingOrb';
import { PlanEditor } from '@/components/PlanEditor';
import { SafetyNotes } from '@/components/Safety';
import { Screen } from '@/components/Screen';
import { isAiConfigured } from '@/lib/ai';
import { feedback } from '@/lib/feedback';
import { AI_SUMMARY, MEDICAL_DISCLAIMER } from '@/lib/legal';
import { cleanPhone, DEFAULT_PLAN } from '@/lib/plan';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { space } from '@/theme/tokens';

const PHONE_ERROR = 'That doesn’t look like a phone number. Use digits, with a + at the start for other countries.';

/**
 * First launch, in three steps: what CalmLoop is (and isn't) with opt-in AI, the safety rules,
 * then the panic plan and emergency contact, written while calm.
 */
export default function Welcome() {
  const t = useTheme();
  const settings = useAppStore((s) => s.settings);
  const updateSettings = useAppStore((s) => s.updateSettings);
  const acceptDisclaimer = useAppStore((s) => s.acceptDisclaimer);
  const aiAvailable = isAiConfigured();
  const [step, setStep] = useState(0);
  const [aiEnabled, setAiEnabled] = useState(settings.aiEnabled);
  const [plan, setPlan] = useState(settings.panicPlan);
  const [name, setName] = useState(settings.trustedContact?.name ?? '');
  const [phone, setPhone] = useState(settings.trustedContact?.phone ?? '');
  const [phoneError, setPhoneError] = useState('');

  const finish = (usePlan: boolean) => {
    if (usePlan) {
      const cleaned = cleanPhone(phone);
      if (phone.trim() && !cleaned) {
        feedback.warning();
        setPhoneError(PHONE_ERROR);
        return;
      }
      updateSettings({ panicPlan: plan, trustedContact: cleaned ? { name: name.trim() || 'My person', phone: cleaned } : null });
    } else {
      updateSettings({ panicPlan: DEFAULT_PLAN });
    }
    feedback.success();
    acceptDisclaimer(aiEnabled && aiAvailable);
  };

  if (step === 1) {
    return (
      <Screen
        key="safety"
        testID="welcome-safety-screen"
        footer={<Button testID="welcome-safety" label="I understand" onPress={() => setStep(2)} />}>
        <Appear>
          <AppText variant="label" tone="muted">
            STEP 2 OF 3
          </AppText>
          <AppText variant="display">Safety first</AppText>
          <AppText tone="secondary" style={styles.body}>
            CalmLoop helps with panic. It isn’t an emergency service. Keep these three things in mind every time.
          </AppText>
        </Appear>
        <Appear index={1}>
          <Card>
            <SafetyNotes />
          </Card>
        </Appear>
      </Screen>
    );
  }

  if (step === 2) {
    return (
      <Screen
        key="plan"
        testID="welcome-plan-screen"
        footer={
          <View style={{ gap: space.sm }}>
            <Button testID="welcome-accept" label="Save my plan" onPress={() => finish(true)} feedbackKind="none" />
            <Button testID="welcome-skip-plan" variant="ghost" label="Use the suggested plan for now" onPress={() => finish(false)} feedbackKind="none" />
          </View>
        }>
        <Appear>
          <AppText variant="label" tone="muted">
            STEP 3 OF 3
          </AppText>
          <AppText variant="display">Your panic plan</AppText>
          <AppText tone="secondary" style={styles.body}>
            Decide now, while you’re calm, so you don’t have to decide anything in the moment. It’s one tap away on the home screen.
          </AppText>
        </Appear>
        <PlanEditor
          plan={plan}
          setPlan={setPlan}
          name={name}
          setName={setName}
          phone={phone}
          setPhone={(v) => {
            setPhone(v);
            setPhoneError('');
          }}
          phoneError={phoneError}
        />
      </Screen>
    );
  }

  return (
    <Screen
      testID="welcome-screen"
      footer={<Button testID="welcome-next" label="Continue" onPress={() => setStep(1)} />}>
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
