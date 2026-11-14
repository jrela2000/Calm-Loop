import { createContext, type ReactNode, useContext } from 'react';
import { useColorScheme } from 'react-native';

import { useAppStore } from '@/store/app-store';

import { type Palette, palettes } from './tokens';

const ThemeContext = createContext<Palette>(palettes.light);

export function ThemeProvider({ children }: { children: ReactNode }) {
  const system = useColorScheme();
  const preference = useAppStore((s) => s.settings.theme);
  const scheme = preference === 'system' ? (system === 'dark' ? 'dark' : 'light') : preference;
  return <ThemeContext.Provider value={palettes[scheme]}>{children}</ThemeContext.Provider>;
}

export function useTheme(): Palette {
  return useContext(ThemeContext);
}
