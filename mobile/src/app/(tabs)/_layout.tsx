import Ionicons from '@expo/vector-icons/Ionicons';
import { Tabs } from 'expo-router';
import type { ColorValue } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { feedback } from '@/lib/feedback';
import { useTheme } from '@/theme/ThemeProvider';

type IconName = keyof typeof Ionicons.glyphMap;

export default function TabLayout() {
  const t = useTheme();
  const insets = useSafeAreaInsets();
  const icon = (active: IconName, inactive: IconName) =>
    function TabIcon({ color, focused, size }: { color: ColorValue; focused: boolean; size: number }) {
      return <Ionicons name={focused ? active : inactive} size={size} color={color as string} />;
    };
  return (
    <Tabs
      screenListeners={{ tabPress: () => feedback.select() }}
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: t.blueStrong,
        tabBarInactiveTintColor: t.textMuted,
        tabBarStyle: {
          backgroundColor: t.tabBar,
          borderTopColor: t.hairline,
          height: 70 + insets.bottom,
          paddingTop: 6,
          paddingBottom: Math.max(insets.bottom, 8),
        },
        tabBarLabelStyle: { fontSize: 12, lineHeight: 16, fontWeight: '600' },
        sceneStyle: { backgroundColor: t.background },
        animation: 'shift',
      }}>
      <Tabs.Screen name="index" options={{ title: 'Home', tabBarIcon: icon('home', 'home-outline') }} />
      <Tabs.Screen name="journal" options={{ title: 'Journal', tabBarIcon: icon('book', 'book-outline') }} />
      <Tabs.Screen name="progress" options={{ title: 'Progress', tabBarIcon: icon('stats-chart', 'stats-chart-outline') }} />
    </Tabs>
  );
}
