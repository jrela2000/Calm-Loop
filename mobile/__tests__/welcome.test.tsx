import { fireEvent, render, screen } from '@testing-library/react-native';

import Welcome from '@/app/welcome';
import { defaultSettings, selectHasAccepted, useAppStore } from '@/store/app-store';
import { ThemeProvider } from '@/theme/ThemeProvider';

jest.mock('expo-router', () => ({ router: { push: jest.fn(), back: jest.fn(), replace: jest.fn(), canGoBack: () => false } }));

beforeEach(() => useAppStore.setState({ settings: { ...defaultSettings } }));

describe('welcome screen', () => {
  it('shows the disclaimer and accepts with AI off by default', async () => {
    await render(
      <ThemeProvider>
        <Welcome />
      </ThemeProvider>,
    );
    expect(screen.getByText('Not a substitute for care')).toBeTruthy();
    // No relay is configured in tests, so the AI switch is disabled.
    expect(screen.getByText(/aren’t available in this build/)).toBeTruthy();
    await fireEvent.press(screen.getByTestId('welcome-accept'));
    const s = useAppStore.getState();
    expect(selectHasAccepted(s)).toBe(true);
    expect(s.settings.aiEnabled).toBe(false);
  });
});
