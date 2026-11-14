import { Text, type TextProps } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { type as typeScale } from '@/theme/tokens';

type Variant = keyof typeof typeScale;
type Tone = 'primary' | 'secondary' | 'muted' | 'accent' | 'danger' | 'onAccent';

export function AppText({
  variant = 'body',
  tone = 'primary',
  style,
  ...rest
}: TextProps & { variant?: Variant; tone?: Tone }) {
  const t = useTheme();
  const color = {
    primary: t.text,
    secondary: t.textSecondary,
    muted: t.textMuted,
    accent: t.blueStrong,
    danger: t.dangerStrong,
    onAccent: t.onAccent,
  }[tone];
  return (
    <Text
      accessibilityRole={variant === 'display' || variant === 'title' ? 'header' : undefined}
      maxFontSizeMultiplier={1.6}
      style={[typeScale[variant], { color }, style]}
      {...rest}
    />
  );
}
