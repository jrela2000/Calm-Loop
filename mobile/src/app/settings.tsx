import Ionicons from '@expo/vector-icons/Ionicons';
import Constants from 'expo-constants';
import { router } from 'expo-router';
import { useState } from 'react';
import { StyleSheet, TextInput, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { SectionLabel, ToggleRow } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Header } from '@/components/Header';
import { PressableScale } from '@/components/PressableScale';
import { Screen } from '@/components/Screen';
import { isAiConfigured } from '@/lib/ai';
import { pickBackupText, saveBackup } from '@/lib/backup-io';
import { confirmDestructive, notify } from '@/lib/dialog';
import { makeBackup, parseBackup } from '@/lib/exporting';
import { feedback } from '@/lib/feedback';
import { useAppStore } from '@/store/app-store';
import type { ThemePreference } from '@/store/types';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

const THEMES: { value: ThemePreference; label: string; icon: keyof typeof Ionicons.glyphMap }[] = [
  { value: 'system', label: 'Auto', icon: 'phone-portrait-outline' },
  { value: 'light', label: 'Light', icon: 'sunny-outline' },
  { value: 'dark', label: 'Dark', icon: 'moon-outline' },
];

function Field({ label, value, onChangeText, placeholder, keyboardType, testID }: {
  label: string;
  value: string;
  onChangeText: (v: string) => void;
  placeholder?: string;
  keyboardType?: 'default' | 'phone-pad';
  testID?: string;
}) {
  const t = useTheme();
  return (
    <TextInput
      testID={testID}
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor={t.textMuted}
      keyboardType={keyboardType}
      accessibilityLabel={label}
      style={[styles.input, { outlineColor: t.blue, color: t.text, backgroundColor: t.background, borderColor: t.border }]}
    />
  );
}

export default function Settings() {
  const t = useTheme();
  const settings = useAppStore((s) => s.settings);
  const updateSettings = useAppStore((s) => s.updateSettings);
  const importData = useAppStore((s) => s.importData);
  const deleteAllData = useAppStore((s) => s.deleteAllData);
  const [name, setName] = useState(settings.userName);
  const [contactName, setContactName] = useState(settings.trustedContact?.name ?? '');
  const [contactPhone, setContactPhone] = useState(settings.trustedContact?.phone ?? '');
  const aiAvailable = isAiConfigured();

  const contactChanged =
    contactName.trim() !== (settings.trustedContact?.name ?? '') || contactPhone.trim() !== (settings.trustedContact?.phone ?? '');

  async function exportAll() {
    const { entries, breathing, grounding } = useAppStore.getState();
    try {
      await saveBackup(makeBackup({ entries, breathing, grounding }));
    } catch {
      notify('Export failed', 'Couldn’t create the backup file. Please try again.');
    }
  }

  async function importAll() {
    try {
      const text = await pickBackupText();
      if (text === null) return;
      const added = importData(parseBackup(text));
      feedback.success();
      notify('Backup restored', `Added ${added.entries} journal entries and ${added.sessions} sessions. Nothing was overwritten.`);
    } catch (e) {
      notify('Couldn’t import', e instanceof Error ? e.message : 'That file couldn’t be read.');
    }
  }

  return (
    <Screen testID="settings-screen">
      <Header title="Settings" />

      <SectionLabel>You</SectionLabel>
      <Card>
        <AppText variant="bodyStrong">What should we call you?</AppText>
        <View style={styles.inline}>
          <View style={{ flex: 1 }}>
            <Field label="Your name" value={name} onChangeText={setName} placeholder="Your name" testID="settings-name" />
          </View>
          <Button
            label="Save"
            onPress={() => updateSettings({ userName: name.trim() })}
            disabled={name.trim() === settings.userName}
            testID="settings-name-save"
          />
        </View>
      </Card>

      <Card>
        <AppText variant="bodyStrong">Trusted contact</AppText>
        <AppText variant="caption" tone="secondary">
          Someone you can call in a hard moment. Shown on the Crisis screen; stored only on this device.
        </AppText>
        <View style={{ gap: space.sm, marginTop: space.sm }}>
          <Field label="Contact name" value={contactName} onChangeText={setContactName} placeholder="Name" testID="contact-name" />
          <Field label="Contact phone number" value={contactPhone} onChangeText={setContactPhone} placeholder="Phone number" keyboardType="phone-pad" testID="contact-phone" />
          <Button
            variant="secondary"
            label={!contactName.trim() && !contactPhone.trim() && settings.trustedContact ? 'Remove contact' : 'Save contact'}
            disabled={!contactChanged || (!!contactName.trim() !== !!contactPhone.trim())}
            onPress={() =>
              updateSettings({
                trustedContact: contactName.trim() && contactPhone.trim() ? { name: contactName.trim(), phone: contactPhone.trim() } : null,
              })
            }
          />
        </View>
      </Card>

      <SectionLabel>Feel</SectionLabel>
      <Card>
        <AppText variant="bodyStrong">Appearance</AppText>
        <View style={[styles.segment, { backgroundColor: t.background, borderColor: t.border }]} accessibilityRole="radiogroup">
          {THEMES.map((opt) => {
            const selected = settings.theme === opt.value;
            return (
              <PressableScale
                key={opt.value}
                testID={`theme-${opt.value}`}
                feedbackKind="none"
                accessibilityRole="radio"
                accessibilityState={{ selected }}
                accessibilityLabel={`${opt.label} appearance`}
                onPress={() => {
                  feedback.select();
                  updateSettings({ theme: opt.value });
                }}
                containerStyle={{ flex: 1 }}
                style={[styles.segmentItem, selected && { backgroundColor: t.blue }]}>
                <Ionicons name={opt.icon} size={18} color={selected ? t.onAccent : t.textSecondary} />
                <AppText variant="caption" style={{ color: selected ? t.onAccent : t.textSecondary, fontWeight: '700' }}>
                  {opt.label}
                </AppText>
              </PressableScale>
            );
          })}
        </View>
        <View style={[styles.divider, { backgroundColor: t.hairline }]} />
        <ToggleRow
          testID="toggle-sound"
          icon="musical-notes-outline"
          title="Sounds"
          description="Soft tones for taps and breathing cues. Silent when your phone is on silent."
          value={settings.soundEnabled}
          onChange={(v) => updateSettings({ soundEnabled: v })}
        />
        <View style={[styles.divider, { backgroundColor: t.hairline }]} />
        <ToggleRow
          testID="toggle-haptics"
          icon="pulse-outline"
          title="Haptics"
          description="Gentle vibrations that pace your breathing and confirm taps."
          value={settings.hapticsEnabled}
          onChange={(v) => updateSettings({ hapticsEnabled: v })}
        />
      </Card>

      <SectionLabel>AI</SectionLabel>
      <Card>
        <ToggleRow
          testID="toggle-ai"
          icon="sparkles-outline"
          title="AI reflections"
          value={settings.aiEnabled && aiAvailable}
          disabled={!aiAvailable}
          onChange={(v) => updateSettings({ aiEnabled: v })}
        />
        <AppText variant="caption" tone="secondary" style={{ marginTop: space.sm }}>
          {!aiAvailable
            ? 'Not available in this build. Entries get on-device reflection prompts instead.'
            : settings.aiEnabled
              ? 'On. Journal text is sent to CalmLoop’s relay and Google Gemini to create a summary. Nothing is stored there.'
              : 'Off. Nothing you write leaves this device.'}
        </AppText>
      </Card>

      <SectionLabel>Your data</SectionLabel>
      <Card style={{ gap: space.sm }}>
        <AppText variant="caption" tone="secondary">
          Everything lives on this device. Export a backup to keep a copy or move to a new phone.
        </AppText>
        <Button variant="secondary" icon="download-outline" label="Export backup" onPress={exportAll} />
        <Button variant="secondary" icon="cloud-upload-outline" label="Restore from backup" onPress={importAll} />
      </Card>
      <Card onPress={() => router.push('/privacy')} accessibilityLabel="Privacy Policy and Terms">
        <View style={styles.inline}>
          <Ionicons name="shield-checkmark-outline" size={22} color={t.blueStrong} />
          <View style={{ flex: 1 }}>
            <AppText variant="bodyStrong">Privacy Policy & Terms</AppText>
            <AppText variant="caption" tone="secondary">
              CalmLoop is a self-help tool, not medical care.
            </AppText>
          </View>
          <Ionicons name="chevron-forward" size={20} color={t.textMuted} />
        </View>
      </Card>
      <Card tone="danger">
        <AppText variant="bodyStrong" tone="danger">
          Delete all my data
        </AppText>
        <AppText variant="caption" tone="secondary" style={{ marginVertical: space.sm }}>
          Permanently erases journal entries, history and settings from this device.
        </AppText>
        <Button
          variant="danger"
          label="Delete everything"
          icon="trash-outline"
          feedbackKind="none"
          testID="delete-all"
          onPress={() => {
            feedback.warning();
            confirmDestructive('Delete all data?', 'All journal entries, history and settings will be permanently erased. This can’t be undone.', 'Delete everything', deleteAllData);
          }}
        />
      </Card>

      <AppText variant="caption" tone="muted" style={{ textAlign: 'center' }}>
        CalmLoop {Constants.expoConfig?.version ?? ''}
      </AppText>
    </Screen>
  );
}

const styles = StyleSheet.create({
  inline: { flexDirection: 'row', alignItems: 'center', gap: space.sm, marginTop: space.sm },
  input: { borderWidth: 1, borderRadius: radius.md, paddingHorizontal: space.md, minHeight: 48, fontSize: 16 },
  segment: { flexDirection: 'row', borderWidth: 1, borderRadius: radius.md, padding: 4, marginTop: space.sm, gap: 4 },
  segmentItem: { minHeight: 44, borderRadius: radius.sm, flexDirection: 'row', gap: 6, alignItems: 'center', justifyContent: 'center' },
  divider: { height: 1, marginVertical: space.md },
});
