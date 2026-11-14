import { type ReactNode } from 'react';
import { Pressable, type PressableProps, type StyleProp, type ViewStyle } from 'react-native';
import Animated, { useAnimatedStyle, useReducedMotion, useSharedValue, withSpring, withTiming } from 'react-native-reanimated';

import { feedback } from '@/lib/feedback';
import { motion } from '@/theme/tokens';

type Props = Omit<PressableProps, 'style' | 'children'> & {
  style?: StyleProp<ViewStyle>;
  /** Layout for the touch target itself (e.g. `{ flex: 1 }` inside a row). */
  containerStyle?: StyleProp<ViewStyle>;
  children: ReactNode;
  /** 'tap' (default) plays the tap haptic/sound; 'none' leaves feedback to the caller. */
  feedbackKind?: 'tap' | 'none';
  scaleTo?: number;
};

/** Every tappable surface: springs down on press-in (instant response) and back on release. */
export function PressableScale({ style, containerStyle, children, feedbackKind = 'tap', scaleTo = motion.pressScale, onPress, onPressIn, onPressOut, disabled, ...rest }: Props) {
  const reduced = useReducedMotion();
  const scale = useSharedValue(1);
  const opacity = useSharedValue(1);
  const animated = useAnimatedStyle(() => ({ transform: [{ scale: scale.get() }], opacity: opacity.get() }));

  return (
    <Pressable
      accessibilityRole="button"
      disabled={disabled}
      hitSlop={6}
      style={containerStyle}
      onPressIn={(e) => {
        if (reduced) opacity.set(withTiming(0.7, { duration: motion.fast }));
        else scale.set(withSpring(scaleTo, motion.spring));
        onPressIn?.(e);
      }}
      onPressOut={(e) => {
        scale.set(withSpring(1, motion.spring));
        opacity.set(withTiming(1, { duration: motion.fast }));
        onPressOut?.(e);
      }}
      onPress={(e) => {
        if (feedbackKind === 'tap') feedback.tap();
        onPress?.(e);
      }}
      {...rest}>
      <Animated.View style={[style, animated, disabled && { opacity: 0.5 }]}>{children}</Animated.View>
    </Pressable>
  );
}
