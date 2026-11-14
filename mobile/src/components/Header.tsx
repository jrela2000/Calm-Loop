import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { type ReactNode } from 'react';
import { StyleSheet, View } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

import { AppText } from './AppText';
import { PressableScale } from './PressableScale';

export function IconButton({
  icon,
  label,
  onPress,
  testID,
}: {
  icon: keyof typeof Ionicons.glyphMap;
  label: string;
  onPress: () => void;
  testID?: string;
}) {
  const t = useTheme();
  return (
    <PressableScale
      testID={testID}
      onPress={onPress}
      accessibilityLabel={label}
      style={[styles.iconButton, { backgroundColor: t.surface, borderColor: t.border }]}>
      <Ionicons name={icon} size={22} color={t.text} />
    </PressableScale>
  );
}

export function goBack() {
  if (router.canGoBack()) router.back();
  else router.replace('/');
}

/** Stack-screen header: back button, title, optional trailing action. */
export function Header({ title, right, onBack = goBack }: { title: string; right?: ReactNode; onBack?: () => void }) {
  return (
    <View style={styles.row}>
      <IconButton icon="chevron-back" label="Back" onPress={onBack} testID="header-back" />
      <AppText variant="title" style={styles.title} numberOfLines={1}>
        {title}
      </AppText>
      {right ?? <View style={styles.spacer} />}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  title: { flex: 1 },
  spacer: { width: 48 },
  iconButton: { width: 48, height: 48, borderRadius: radius.md, borderWidth: 1, alignItems: 'center', justifyContent: 'center' },
});
