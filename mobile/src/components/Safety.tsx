import Ionicons from '@expo/vector-icons/Ionicons';
import { StyleSheet, View } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

import { AppText } from './AppText';

const NOTES: { icon: keyof typeof Ionicons.glyphMap; title: string; body: string; urgent?: boolean }[] = [
  {
    icon: 'medkit-outline',
    title: 'Medical emergency? Call 911 first',
    body: 'Chest pain, trouble breathing, fainting or any sign you could be hurt: call 911 or your local emergency number before anything else. CalmLoop can’t tell panic from a medical problem.',
    urgent: true,
  },
  {
    icon: 'location-outline',
    title: 'Notice where you are',
    body: 'Before you open CalmLoop, look around. If the spot is busy, loud or unsafe, move somewhere quieter and safe first.',
  },
  {
    icon: 'car-outline',
    title: 'Never while driving',
    body: 'If you’re driving, pull over somewhere safe and stop the car before you use the app. Breathing exercises can make you lightheaded.',
  },
];

/** The three safety rules. Shown during onboarding and at the top of the panic plan. */
export function SafetyNotes({ compact }: { compact?: boolean }) {
  const t = useTheme();
  return (
    <View style={{ gap: compact ? space.md : space.lg }}>
      {NOTES.map((n) => (
        <View key={n.title} style={styles.row}>
          <View style={[styles.icon, { backgroundColor: n.urgent ? t.dangerSurface : t.background }]}>
            <Ionicons name={n.icon} size={22} color={n.urgent ? t.dangerStrong : t.blueStrong} />
          </View>
          <View style={{ flex: 1, gap: 2 }}>
            <AppText variant="bodyStrong">{n.title}</AppText>
            {compact ? null : (
              <AppText variant="caption" tone="secondary">
                {n.body}
              </AppText>
            )}
          </View>
        </View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'flex-start', gap: space.md },
  icon: { width: 44, height: 44, borderRadius: radius.pill, alignItems: 'center', justifyContent: 'center' },
});
