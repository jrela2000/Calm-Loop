import { DarkTheme, DefaultTheme, Stack, ThemeProvider as NavThemeProvider } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { useEffect } from 'react';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

import { selectHasAccepted, useAppStore } from '@/store/app-store';
import { ThemeProvider, useTheme } from '@/theme/ThemeProvider';

SplashScreen.preventAutoHideAsync().catch(() => {});

function Navigator() {
  const t = useTheme();
  const accepted = useAppStore(selectHasAccepted);
  const base = t.scheme === 'dark' ? DarkTheme : DefaultTheme;
  const navTheme = {
    ...base,
    colors: { ...base.colors, background: t.background, card: t.background, text: t.text, primary: t.blueStrong, border: t.hairline },
  };
  return (
    <NavThemeProvider value={navTheme}>
      <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: t.background }, animation: 'slide_from_right' }}>
        <Stack.Protected guard={!accepted} redirectTo="/">
          <Stack.Screen name="welcome" options={{ animation: 'fade' }} />
        </Stack.Protected>
        <Stack.Protected guard={accepted} redirectTo="/welcome">
          <Stack.Screen name="(tabs)" options={{ animation: 'fade' }} />
          <Stack.Screen name="breathing" options={{ animation: 'fade_from_bottom', gestureEnabled: true }} />
          <Stack.Screen name="grounding" options={{ animation: 'fade_from_bottom' }} />
          <Stack.Screen name="journal/new" options={{ animation: 'slide_from_bottom' }} />
          <Stack.Screen name="journal/processing" options={{ animation: 'fade', gestureEnabled: false }} />
          <Stack.Screen name="journal/[id]" />
          <Stack.Screen name="crisis" options={{ animation: 'slide_from_bottom' }} />
          <Stack.Screen name="plan" options={{ animation: 'slide_from_bottom' }} />
          <Stack.Screen name="settings" />
        </Stack.Protected>
        <Stack.Screen name="privacy" />
      </Stack>
    </NavThemeProvider>
  );
}

export default function RootLayout() {
  const hydrated = useAppStore((s) => s.hydrated);
  useEffect(() => {
    if (hydrated) SplashScreen.hideAsync().catch(() => {});
  }, [hydrated]);
  // Keep the splash screen up for the few milliseconds it takes to read local data,
  // so returning users never see the welcome screen flash.
  if (!hydrated) return null;
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <ThemeProvider>
        <Navigator />
      </ThemeProvider>
    </GestureHandlerRootView>
  );
}
