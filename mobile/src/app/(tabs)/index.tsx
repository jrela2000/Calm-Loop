import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { StyleSheet, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear } from '@/components/Bits';
import { BreathingOrbStatic } from '@/components/BreathingOrb';
import { Card } from '@/components/Card';
import { IconButton } from '@/components/Header';
import { PressableScale } from '@/components/PressableScale';
import { Screen } from '@/components/Screen';
import { currentStreak } from '@/lib/streak';
import { activityTimestamps, DEFAULT_USER_NAME, useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

function greeting(now = new Date()) {
  const h = now.getHours();
  return h < 12 ? 'Good morning' : h < 18 ? 'Good afternoon' : 'Good evening';
}

type Tool = {
  href: '/breathing' | '/grounding' | '/journal/new';
  title: string;
  body: string;
  icon: keyof typeof Ionicons.glyphMap;
  tone: 'lavender' | 'sage' | 'blue';
};

const TOOLS: Tool[] = [
  { href: '/breathing', title: 'Box breathing', body: 'Slow your heart rate in about a minute.', icon: 'square-outline', tone: 'lavender' },
  { href: '/grounding', title: '5-4-3-2-1 grounding', body: 'Come back to the present through your senses.', icon: 'leaf-outline', tone: 'sage' },
  { href: '/journal/new', title: 'Write it out', body: 'Untangle the thought that’s looping.', icon: 'create-outline', tone: 'blue' },
];

export default function Home() {
  const t = useTheme();
  const name = useAppStore((s) => s.settings.userName);
  const streak = useAppStore((s) => currentStreak(activityTimestamps(s)));
  const accent = { lavender: t.lavenderStrong, sage: t.sageStrong, blue: t.blueStrong };

  return (
    <Screen edges={['top']} testID="home-screen">
      <View style={styles.topRow}>
        <View style={{ flex: 1 }}>
          <AppText tone="secondary">{greeting()},</AppText>
          <AppText variant="display" numberOfLines={1}>
            {name.trim() || DEFAULT_USER_NAME}
          </AppText>
        </View>
        <IconButton icon="settings-outline" label="Settings" onPress={() => router.push('/settings')} testID="open-settings" />
      </View>

      <PressableScale
        testID="open-plan"
        onPress={() => router.push('/plan')}
        accessibilityLabel="Panicking right now? Open my panic plan."
        style={[styles.crisis, { backgroundColor: t.dangerSurface, borderColor: t.dangerBorder }]}>
        <Ionicons name="medkit-outline" size={22} color={t.dangerStrong} />
        <View style={{ flex: 1 }}>
          <AppText variant="bodyStrong" tone="danger">
            Panicking right now?
          </AppText>
          <AppText variant="caption" tone="secondary">
            Open your plan: safety first, then one step at a time.
          </AppText>
        </View>
        <Ionicons name="chevron-forward" size={18} color={t.dangerStrong} />
      </PressableScale>

      <Appear>
        <Card style={styles.hero}>
          <BreathingOrbStatic size={84} />
          <View style={{ flex: 1, gap: space.xs }}>
            <AppText variant="heading">You’re here. That’s step one.</AppText>
            <AppText variant="caption" tone="secondary">
              Pick whatever feels doable right now. There’s no wrong choice.
            </AppText>
          </View>
        </Card>
      </Appear>

      {TOOLS.map((tool, i) => (
        <Appear key={tool.href} index={i + 1}>
          <Card tone={tool.tone} onPress={() => router.push(tool.href)} testID={`tool-${tool.href}`} accessibilityLabel={tool.title} accessibilityHint={tool.body}>
            <View style={styles.toolRow}>
              <View style={[styles.toolIcon, { backgroundColor: t.background }]}>
                <Ionicons name={tool.icon} size={24} color={accent[tool.tone]} />
              </View>
              <View style={{ flex: 1 }}>
                <AppText variant="heading">{tool.title}</AppText>
                <AppText variant="caption" tone="secondary">
                  {tool.body}
                </AppText>
              </View>
              <Ionicons name="chevron-forward" size={20} color={t.textMuted} />
            </View>
          </Card>
        </Appear>
      ))}

      <Appear index={4}>
        <Card onPress={() => router.push('/progress')} testID="streak-card" accessibilityLabel={`Current streak ${streak} days`}>
          <View style={styles.toolRow}>
            <AppText style={styles.flame}>{streak > 0 ? '🔥' : '🌱'}</AppText>
            <View style={{ flex: 1 }}>
              <AppText variant="heading">
                {streak > 0 ? `${streak}-day streak` : 'Start a streak today'}
              </AppText>
              <AppText variant="caption" tone="secondary">
                {streak > 0 ? 'Showing up for yourself, day after day.' : 'Any exercise or journal entry counts.'}
              </AppText>
            </View>
          </View>
        </Card>
      </Appear>

      <PressableScale
        testID="open-crisis"
        onPress={() => router.push('/crisis')}
        accessibilityLabel="Crisis support. Call or text a helpline now."
        style={[styles.crisis, { backgroundColor: t.dangerSurface, borderColor: t.dangerBorder }]}>
        <Ionicons name="call" size={20} color={t.dangerStrong} />
        <AppText variant="bodyStrong" tone="danger" style={{ flex: 1 }}>
          Need to talk to someone now?
        </AppText>
        <Ionicons name="chevron-forward" size={18} color={t.dangerStrong} />
      </PressableScale>
    </Screen>
  );
}

const styles = StyleSheet.create({
  topRow: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  hero: { flexDirection: 'row', alignItems: 'center', gap: space.md, paddingVertical: space.md },
  toolRow: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  toolIcon: { width: 48, height: 48, borderRadius: radius.md, alignItems: 'center', justifyContent: 'center' },
  flame: { fontSize: 30, lineHeight: 36 },
  crisis: { flexDirection: 'row', alignItems: 'center', gap: space.sm, padding: space.lg, borderRadius: radius.lg, borderWidth: 1 },
});
