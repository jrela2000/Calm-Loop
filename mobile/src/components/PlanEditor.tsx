import Ionicons from '@expo/vector-icons/Ionicons';
import { StyleSheet, View } from 'react-native';

import { feedback } from '@/lib/feedback';
import { PLAN_STEPS, togglePlanStep } from '@/lib/plan';
import type { PanicPlan } from '@/store/types';
import { useTheme } from '@/theme/ThemeProvider';
import { space } from '@/theme/tokens';

import { AppText } from './AppText';
import { Card } from './Card';
import { Field } from './Field';
import { PressableScale } from './PressableScale';

type Props = {
  plan: PanicPlan;
  setPlan: (p: PanicPlan) => void;
  name: string;
  setName: (v: string) => void;
  phone: string;
  setPhone: (v: string) => void;
  phoneError?: string;
};

/** Pick the steps, write your own reminder, and choose one person to reach. */
export function PlanEditor({ plan, setPlan, name, setName, phone, setPhone, phoneError }: Props) {
  const t = useTheme();
  return (
    <View style={{ gap: space.lg }}>
      <Card>
        <AppText variant="label" tone="muted" style={{ marginBottom: space.sm }}>
          WHEN PANIC HITS, I WILL
        </AppText>
        {PLAN_STEPS.map((s) => {
          const on = plan.steps.includes(s.id);
          const row = (
            <View style={styles.step}>
              <Ionicons
                name={s.locked ? 'lock-closed-outline' : on ? 'checkmark-circle' : 'ellipse-outline'}
                size={24}
                color={on ? t.blueStrong : t.textMuted}
              />
              <View style={{ flex: 1 }}>
                <AppText variant="bodyStrong">{s.title}</AppText>
                <AppText variant="caption" tone="secondary">
                  {s.locked ? 'Always the first step.' : s.body}
                </AppText>
              </View>
            </View>
          );
          if (s.locked) {
            return (
              <View key={s.id} accessible accessibilityLabel={`${s.title}. Always the first step.`}>
                {row}
              </View>
            );
          }
          return (
            <PressableScale
              key={s.id}
              testID={`plan-step-${s.id}`}
              feedbackKind="none"
              scaleTo={0.98}
              accessibilityRole="checkbox"
              accessibilityState={{ checked: on }}
              accessibilityLabel={s.title}
              onPress={() => {
                feedback.select();
                setPlan(togglePlanStep(plan, s.id));
              }}>
              {row}
            </PressableScale>
          );
        })}
      </Card>

      {plan.steps.includes('reminder') ? (
        <View style={{ gap: space.sm }}>
          <AppText variant="bodyStrong">What I want to hear in that moment</AppText>
          <Field
            testID="plan-reminder"
            label="What I want to hear in that moment"
            multiline
            maxLength={280}
            value={plan.reminder}
            onChangeText={(reminder) => setPlan({ ...plan, reminder })}
            placeholder="This is panic. It will pass."
          />
        </View>
      ) : null}

      <View style={{ gap: space.sm }}>
        <AppText variant="bodyStrong">My emergency contact</AppText>
        <AppText variant="caption" tone="secondary">
          One person you trust to call or text. Optional, and you can change it any time.
        </AppText>
        <Field testID="plan-contact-name" label="Contact name" value={name} onChangeText={setName} placeholder="Name, e.g. Sam" maxLength={60} autoComplete="name" />
        <Field
          testID="plan-contact-phone"
          label="Contact phone number"
          value={phone}
          onChangeText={setPhone}
          placeholder="Phone number"
          keyboardType="phone-pad"
          autoComplete="tel"
          maxLength={24}
        />
        {phoneError ? (
          <AppText variant="caption" tone="danger" accessibilityLiveRegion="polite">
            {phoneError}
          </AppText>
        ) : null}
        <View style={styles.note}>
          <Ionicons name="lock-closed-outline" size={16} color={t.sageStrong} />
          <AppText variant="caption" tone="secondary" style={{ flex: 1 }}>
            Saved on this device only. It isn’t sent to us or anyone else, it’s left out of CalmLoop backup files, and Delete all my data removes it.
          </AppText>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  step: { flexDirection: 'row', alignItems: 'flex-start', gap: space.md, minHeight: 52, paddingVertical: space.sm },
  note: { flexDirection: 'row', alignItems: 'flex-start', gap: space.sm },
});
