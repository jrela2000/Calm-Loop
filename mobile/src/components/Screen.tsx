import { LinearGradient } from 'expo-linear-gradient';
import { StatusBar } from 'expo-status-bar';
import { type ReactNode } from 'react';
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet, View } from 'react-native';
import { type Edge, SafeAreaView } from 'react-native-safe-area-context';

import { useTheme } from '@/theme/ThemeProvider';
import { space } from '@/theme/tokens';

type Props = {
  children: ReactNode;
  scroll?: boolean;
  /** Tab screens sit above the tab bar, which already handles the bottom inset. */
  edges?: Edge[];
  footer?: ReactNode;
  testID?: string;
};

export const MAX_CONTENT_WIDTH = 640;

export function Background({ children }: { children?: ReactNode }) {
  const t = useTheme();
  return (
    <LinearGradient colors={[...t.gradient]} start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }} style={StyleSheet.absoluteFill}>
      {children}
    </LinearGradient>
  );
}

export function Screen({ children, scroll = true, edges = ['top', 'bottom'], footer, testID }: Props) {
  const t = useTheme();
  const content = <View style={styles.content}>{children}</View>;
  return (
    <View style={styles.flex} testID={testID}>
      <StatusBar style={t.scheme === 'dark' ? 'light' : 'dark'} />
      <Background />
      <SafeAreaView edges={edges} style={styles.flex}>
        <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          {scroll ? (
            <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled" showsVerticalScrollIndicator={false}>
              {content}
            </ScrollView>
          ) : (
            <View style={[styles.flex, styles.scroll]}>{content}</View>
          )}
          {footer ? <View style={styles.footer}>{footer}</View> : null}
        </KeyboardAvoidingView>
      </SafeAreaView>
    </View>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  scroll: { paddingHorizontal: space.xl, paddingTop: space.md, paddingBottom: space.xxxl, flexGrow: 1 },
  content: { width: '100%', maxWidth: MAX_CONTENT_WIDTH, alignSelf: 'center', gap: space.lg, flexGrow: 1 },
  footer: { paddingHorizontal: space.xl, paddingBottom: space.md, width: '100%', maxWidth: MAX_CONTENT_WIDTH + space.xl * 2, alignSelf: 'center' },
});
