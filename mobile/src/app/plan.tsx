import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear, SectionLabel } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Header } from '@/components/Header';
import { PlanEditor } from '@/components/PlanEditor';
import { PressableScale } from '@/components/PressableScale';
import { SafetyNotes } from '@/components/Safety';
import { Screen } from '@/components/Screen';
import { feedback } from '@/lib/feedback';
import { openSafely, smsUrl, telUrl } from '@/lib/links';
import { cleanPhone, PANIC_TEXT, PLAN_STEPS } from '@/lib/plan';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

function Edit({ onDone }: { onDone: () => void }) {
  const settings = useAppStore((s) => s.settings);
  const updateSettings = useAppStore((s) => s.updateSettings);
  const [plan, setPlan] = useState(settings.panicPlan);
  const [name, setName] = useState(settings.trustedContact?.name ?? '');
  const [phone, setPhone] = useState(settings.trustedContact?.phone ?? '');
  const [phoneError, setPhoneError] = useState('');

  const save = () => {
    const cleaned = cleanPhone(phone);
    if (phone.trim() && !cleaned) {
      feedback.warning();
      setPhoneError('That doesn’t look like a phone number. Use digits, with a + at the start for other countries.');
      return;
    }
    updateSettings({ panicPlan: plan, trustedContact: cleaned ? { name: name.trim() || 'My person', phone: cleaned } : null });
    feedback.success();
    onDone();
  };

  return (
    <Screen testID="plan-edit-screen" footer={<Button testID="plan-save" label="Save my plan" onPress={save} feedbackKind="none" />}>
      <Header title="Edit my plan" onBack={onDone} />
      <AppText tone="secondary">Write it now, while you’re calm, so future you doesn’t have to decide anything.</AppText>
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

/** The emergency screen: safety first, one-tap 911 and contact, then the person's own steps. */
export default function PanicPlanScreen() {
  const t = useTheme();
  const plan = useAppStore((s) => s.settings.panicPlan);
  const contact = useAppStore((s) => s.settings.trustedContact);
  const [editing, setEditing] = useState(false);

  if (editing) return <Edit onDone={() => setEditing(false)} />;

  const steps = PLAN_STEPS.filter((s) => plan.steps.includes(s.id));
  const call = (number: string) => openSafely(telUrl(number), `Please call ${number} from a phone.`);

  return (
    <Screen testID="plan-screen">
      <Header title="My panic plan" />
      <Appear>
        <AppText variant="display">You’re going to be okay</AppText>
        <AppText tone="secondary" style={{ marginTop: space.xs }}>
          One step at a time. You wrote this plan for exactly this moment.
        </AppText>
      </Appear>

      <Button testID="plan-911" variant="danger" icon="call" label="Medical emergency: call 911" onPress={() => call('911')} />

      <Appear index={1}>
        <Card>
          <SectionLabel>Safety first</SectionLabel>
          <View style={{ marginTop: space.sm }}>
            <SafetyNotes compact />
          </View>
        </Card>
      </Appear>

      <Appear index={2}>
        {contact ? (
          <Card tone="sage">
            <View style={styles.row}>
              <View style={[styles.icon, { backgroundColor: t.background }]}>
                <Ionicons name="heart-outline" size={22} color={t.sageStrong} />
              </View>
              <View style={{ flex: 1 }}>
                <AppText variant="heading">{contact.name}</AppText>
                <AppText variant="caption" tone="secondary">
                  Your emergency contact
                </AppText>
              </View>
            </View>
            <View style={[styles.row, { marginTop: space.md }]}>
              <View style={{ flex: 1 }}>
                <Button testID="plan-call-contact" icon="call-outline" label={`Call ${contact.name}`} onPress={() => call(contact.phone)} />
              </View>
              <View style={{ flex: 1 }}>
                <Button
                  testID="plan-text-contact"
                  variant="secondary"
                  icon="chatbubble-outline"
                  label="Text"
                  onPress={() => openSafely(smsUrl(contact.phone, PANIC_TEXT), `Please text ${contact.phone} from a phone.`)}
                />
              </View>
            </View>
            <AppText variant="caption" tone="secondary" style={{ marginTop: space.sm }}>
              Text opens your messages with a short note ready. You choose whether to send it.
            </AppText>
          </Card>
        ) : (
          <Card onPress={() => setEditing(true)} accessibilityLabel="Add an emergency contact">
            <View style={styles.row}>
              <Ionicons name="person-add-outline" size={22} color={t.blueStrong} />
              <AppText variant="caption" tone="secondary" style={{ flex: 1 }}>
                Add one person you can reach in one tap.
              </AppText>
            </View>
          </Card>
        )}
      </Appear>

      <SectionLabel>My steps</SectionLabel>
      {steps.map((s, i) => {
        const action = s.href
          ? () => router.push(s.href!)
          : s.id === 'contact' && contact
            ? () => call(contact.phone)
            : undefined;
        const body = (
          <View style={styles.step}>
            <View style={[styles.num, { backgroundColor: t.background }]}>
              <AppText variant="bodyStrong" tone="accent">
                {i + 1}
              </AppText>
            </View>
            <View style={{ flex: 1, gap: 2 }}>
              <AppText variant="bodyStrong">{s.title}</AppText>
              {s.id === 'reminder' ? (
                <AppText style={{ fontStyle: 'italic' }}>“{plan.reminder}”</AppText>
              ) : (
                <AppText variant="caption" tone="secondary">
                  {s.id === 'contact' ? (contact ? `Call or text ${contact.name}.` : 'Add a contact to make this one tap.') : s.body}
                </AppText>
              )}
            </View>
            {action ? <Ionicons name="chevron-forward" size={20} color={t.textMuted} /> : null}
          </View>
        );
        return (
          <Appear key={s.id} index={i}>
            {action ? (
              <Card onPress={action} testID={`plan-do-${s.id}`} accessibilityLabel={`Step ${i + 1}: ${s.title}`}>
                {body}
              </Card>
            ) : (
              <Card accessibilityLabel={`Step ${i + 1}: ${s.title}`}>{body}</Card>
            )}
          </Appear>
        );
      })}

      <Button variant="secondary" icon="call-outline" label="24/7 crisis lines" onPress={() => router.push('/crisis')} />
      <PressableScale testID="plan-edit" onPress={() => setEditing(true)} accessibilityLabel="Edit my plan" style={styles.link}>
        <AppText variant="bodyStrong" tone="accent">
          Edit my plan
        </AppText>
      </PressableScale>
    </Screen>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  icon: { width: 44, height: 44, borderRadius: radius.pill, alignItems: 'center', justifyContent: 'center' },
  step: { flexDirection: 'row', alignItems: 'flex-start', gap: space.md },
  num: { width: 32, height: 32, borderRadius: radius.pill, alignItems: 'center', justifyContent: 'center' },
  link: { minHeight: 48, alignItems: 'center', justifyContent: 'center' },
});
