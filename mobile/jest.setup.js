/* eslint-env jest */
jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock'),
);

// Sound and haptics are fire-and-forget; record calls instead of touching native modules.
jest.mock('expo-audio', () => ({
  createAudioPlayer: jest.fn(() => ({ play: jest.fn(), seekTo: jest.fn(), volume: 1 })),
  setAudioModeAsync: jest.fn(() => Promise.resolve()),
}));
jest.mock('expo-haptics', () => ({
  impactAsync: jest.fn(() => Promise.resolve()),
  selectionAsync: jest.fn(() => Promise.resolve()),
  notificationAsync: jest.fn(() => Promise.resolve()),
  ImpactFeedbackStyle: { Light: 'light', Medium: 'medium', Heavy: 'heavy', Soft: 'soft', Rigid: 'rigid' },
  NotificationFeedbackType: { Success: 'success', Warning: 'warning', Error: 'error' },
}));
jest.mock('expo-speech-recognition', () => {
  throw new Error('native module unavailable in tests');
});

// Reanimated 4.7's bundled Jest mock still boots the native module, so screens render against
// this small stand-in: animations jump straight to their end values. (Motion itself is checked
// in the browser run of the web build.)
jest.mock('react-native-worklets', () => require('react-native-worklets/lib/module/mock'));
jest.mock('react-native-reanimated', () => {
  const { View, Text, ScrollView, Image } = require('react-native');
  const chain = () => new Proxy(function () {}, { get: () => chain, apply: () => chain() });
  const passThrough = (v) => v;
  const Easing = new Proxy({}, { get: () => () => passThrough });
  const Animated = { View, Text, ScrollView, Image, createAnimatedComponent: (c) => c };
  return {
    __esModule: true,
    default: Animated,
    ...Animated,
    Easing,
    ReduceMotion: { System: 'system', Always: 'always', Never: 'never' },
    useSharedValue: (initial) => {
      let value = initial;
      return { get: () => value, set: (v) => { value = typeof v === 'function' ? v(value) : v; }, value };
    },
    useAnimatedStyle: (fn) => fn(),
    useReducedMotion: () => false,
    withTiming: passThrough,
    withSpring: passThrough,
    withDelay: (_d, v) => v,
    withRepeat: passThrough,
    withSequence: (...vs) => vs[vs.length - 1],
    cancelAnimation: () => {},
    FadeIn: chain(), FadeOut: chain(), FadeInDown: chain(), FadeInRight: chain(), FadeOutLeft: chain(), ZoomIn: chain(),
  };
});
