import { Easing } from 'react-native-reanimated';

/**
 * CalmLoop design tokens. Palette carried over from the Android app (soft blue, sage, lavender
 * on an icy gradient), with a matching night palette. Text colors meet WCAG AA on their surfaces.
 */
const light = {
  scheme: 'light' as const,
  gradient: ['#F2F6FC', '#EDF2F8', '#ECE7F6'] as const,
  background: '#F2F6FC',
  surface: 'rgba(255,255,255,0.82)',
  surfaceStrong: '#FFFFFF',
  border: 'rgba(255,255,255,0.95)',
  hairline: 'rgba(35,48,68,0.08)',
  text: '#233044',
  textSecondary: '#4F5F76',
  textMuted: '#6B7A90',
  blue: '#639BD3',
  blueStrong: '#2A6CB0',
  sage: '#A2CBA8',
  sageStrong: '#2E7D32',
  lavender: '#BFB7EB',
  lavenderStrong: '#6B5B95',
  onAccent: '#1B2636',
  danger: '#DC2626',
  dangerStrong: '#991B1B',
  dangerSurface: 'rgba(254,242,242,0.92)',
  dangerBorder: '#FECACA',
  shadow: 'rgba(42,108,176,0.18)',
  tabBar: 'rgba(255,255,255,0.92)',
};

export type Palette = { [K in keyof typeof light]: K extends 'scheme' ? 'light' | 'dark' : K extends 'gradient' ? readonly [string, string, string] : string };

const dark: Palette = {
  scheme: 'dark',
  gradient: ['#0E1526', '#121A2E', '#1C1934'],
  background: '#0E1526',
  surface: 'rgba(30,41,59,0.78)',
  surfaceStrong: '#1E293B',
  border: 'rgba(148,163,184,0.16)',
  hairline: 'rgba(226,232,240,0.08)',
  text: '#F1F5F9',
  textSecondary: '#B4C0D3',
  textMuted: '#8D9AB0',
  blue: '#7FB2E5',
  blueStrong: '#9CC6F0',
  sage: '#8FC79A',
  sageStrong: '#A8DDB2',
  lavender: '#B7AEF0',
  lavenderStrong: '#CEC7F7',
  onAccent: '#0B1220',
  danger: '#F87171',
  dangerStrong: '#FCA5A5',
  dangerSurface: 'rgba(127,29,29,0.32)',
  dangerBorder: 'rgba(248,113,113,0.35)',
  shadow: 'rgba(0,0,0,0.45)',
  tabBar: 'rgba(17,24,39,0.94)',
};

export const palettes = { light: light as Palette, dark };

export const space = { xs: 4, sm: 8, md: 12, lg: 16, xl: 24, xxl: 32, xxxl: 48 } as const;
export const radius = { sm: 12, md: 18, lg: 24, xl: 32, pill: 999 } as const;

export const type = {
  display: { fontSize: 30, lineHeight: 36, fontWeight: '800' as const, letterSpacing: -0.4 },
  title: { fontSize: 22, lineHeight: 28, fontWeight: '700' as const, letterSpacing: -0.2 },
  heading: { fontSize: 17, lineHeight: 23, fontWeight: '700' as const },
  body: { fontSize: 15, lineHeight: 22, fontWeight: '400' as const },
  bodyStrong: { fontSize: 15, lineHeight: 22, fontWeight: '600' as const },
  caption: { fontSize: 13, lineHeight: 18, fontWeight: '400' as const },
  label: { fontSize: 12, lineHeight: 16, fontWeight: '700' as const, letterSpacing: 1.1 },
};

/** Motion tokens: one easing family and spring so every transition feels related. */
export const motion = {
  fast: 140,
  base: 240,
  slow: 420,
  easing: Easing.bezier(0.22, 1, 0.36, 1), // ease-out-quint: quick start, soft landing
  breathEasing: Easing.inOut(Easing.sin),
  spring: { damping: 16, stiffness: 220, mass: 0.8 },
  pressScale: 0.96,
  stagger: 60,
};
