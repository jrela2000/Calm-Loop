import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useEffect, useState } from 'react';
import { StyleSheet, TextInput, View } from 'react-native';
import Animated, { useAnimatedStyle, useReducedMotion, useSharedValue, withRepeat, withSequence, withTiming } from 'react-native-reanimated';

import { AppText } from '@/components/AppText';
import { Button } from '@/components/Button';
import { Header } from '@/components/Header';
import { PressableScale } from '@/components/PressableScale';
import { Screen } from '@/components/Screen';
import { feedback } from '@/lib/feedback';
import { useDictation } from '@/lib/speech';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

const PROMPTS = [
  'What’s on your mind right now?',
  'What happened just before you started feeling this way?',
  'What is the worst-case thought looping in your head?',
];

function MicButton({ listening, onPress }: { listening: boolean; onPress: () => void }) {
  const t = useTheme();
  const reduced = useReducedMotion();
  const pulse = useSharedValue(1);
  useEffect(() => {
    pulse.set(listening && !reduced ? withRepeat(withSequence(withTiming(1.25, { duration: 700 }), withTiming(1, { duration: 700 })), -1) : withTiming(1));
  }, [listening, reduced, pulse]);
  const ring = useAnimatedStyle(() => ({ transform: [{ scale: pulse.get() }], opacity: listening ? 0.35 : 0 }));
  return (
    <PressableScale
      testID="journal-mic"
      feedbackKind="none"
      onPress={onPress}
      accessibilityLabel={listening ? 'Stop dictation' : 'Speak instead of typing'}
      style={styles.micWrap}>
      <Animated.View style={[styles.micRing, { backgroundColor: t.danger }, ring]} />
      <View style={[styles.mic, { backgroundColor: listening ? t.danger : t.blue }]}>
        <Ionicons name={listening ? 'stop' : 'mic'} size={24} color={listening ? '#FFFFFF' : t.onAccent} />
      </View>
    </PressableScale>
  );
}

export default function NewEntry() {
  const t = useTheme();
  const submitJournal = useAppStore((s) => s.submitJournal);
  const aiEnabled = useAppStore((s) => s.settings.aiEnabled);
  const [text, setText] = useState('');
  const [prompt] = useState(() => PROMPTS[Math.floor(Math.random() * PROMPTS.length)]);
  const dictation = useDictation((phrase) => setText((prev) => (prev.trim() ? `${prev.trim()} ${phrase}` : phrase)));
  const listening = dictation.state === 'listening';
  const shown = dictation.partial ? `${text}${text && !text.endsWith(' ') ? ' ' : ''}${dictation.partial}` : text;

  function save() {
    if (listening) dictation.stop();
    if (!text.trim()) return;
    feedback.tap();
    submitJournal(text);
    router.replace('/journal/processing');
  }

  return (
    <Screen
      testID="journal-new"
      footer={
        <Button
          testID="journal-save"
          feedbackKind="none"
          label={aiEnabled ? 'Save & reflect' : 'Save entry'}
          icon="checkmark"
          onPress={save}
          disabled={!text.trim()}
        />
      }>
      <Header title="New entry" />
      <AppText variant="heading">{prompt}</AppText>
      <AppText variant="caption" tone="secondary">
        No need for full sentences. Let it out unfiltered — this stays on your device
        {aiEnabled ? ' unless you’ve turned on AI reflections.' : '.'}
      </AppText>
      <TextInput
        testID="journal-text"
        value={shown}
        onChangeText={setText}
        editable={!listening}
        multiline
        autoFocus={!dictation.supported}
        placeholder="I’m feeling…"
        placeholderTextColor={t.textMuted}
        textAlignVertical="top"
        accessibilityLabel="Journal entry"
        style={[styles.input, { outlineColor: t.blue, color: t.text, backgroundColor: t.surface, borderColor: listening ? t.danger : t.border }]}
      />
      {dictation.supported ? (
        <View style={styles.micRow}>
          <MicButton listening={listening} onPress={() => (listening ? dictation.stop() : dictation.start())} />
          <AppText variant="caption" tone="secondary" style={{ flex: 1 }} accessibilityLiveRegion="polite">
            {listening
              ? 'Listening… tap stop when you’re done.'
              : dictation.state === 'denied'
                ? 'Microphone access is off. You can turn it on in your device settings, or keep typing.'
                : dictation.state === 'error'
                  ? 'Couldn’t hear that. Try again, or type instead.'
                  : 'Tap the mic to speak. Audio isn’t recorded or saved.'}
          </AppText>
        </View>
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  input: { minHeight: 220, borderWidth: 1.5, borderRadius: radius.lg, padding: space.lg, fontSize: 17, lineHeight: 25 },
  micRow: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  micWrap: { width: 64, height: 64, alignItems: 'center', justifyContent: 'center' },
  micRing: { position: 'absolute', width: 64, height: 64, borderRadius: 32 },
  mic: { width: 56, height: 56, borderRadius: 28, alignItems: 'center', justifyContent: 'center' },
});
