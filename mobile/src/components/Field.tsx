import { StyleSheet, TextInput, type TextInputProps } from 'react-native';

import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

/** Single text field with the label exposed to screen readers. */
export function Field({ label, style, multiline, ...rest }: TextInputProps & { label: string }) {
  const t = useTheme();
  return (
    <TextInput
      accessibilityLabel={label}
      placeholderTextColor={t.textMuted}
      multiline={multiline}
      {...rest}
      style={[
        styles.input,
        multiline && styles.multiline,
        { outlineColor: t.blue, color: t.text, backgroundColor: t.background, borderColor: t.border },
        style,
      ]}
    />
  );
}

const styles = StyleSheet.create({
  input: { borderWidth: 1, borderRadius: radius.md, paddingHorizontal: space.md, minHeight: 48, fontSize: 16 },
  multiline: { minHeight: 80, paddingTop: space.md, textAlignVertical: 'top' },
});
