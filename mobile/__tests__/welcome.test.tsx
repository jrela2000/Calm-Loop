import { fireEvent, render, screen } from '@testing-library/react-native';

import Welcome from '@/app/welcome';
import { DEFAULT_PLAN } from '@/lib/plan';
import { defaultSettings, selectHasAccepted, useAppStore } from '@/store/app-store';
import { ThemeProvider } from '@/theme/ThemeProvider';

jest.mock('expo-router', () => ({ router: { push: jest.fn(), back: jest.fn(), replace: jest.fn(), canGoBack: () => false } }));

beforeEach(() => useAppStore.setState({ settings: { ...defaultSettings } }));

async function toPlanStep() {
  await render(
    <ThemeProvider>
      <Welcome />
    </ThemeProvider>,
  );
  expect(screen.getByText('Not a substitute for care')).toBeTruthy();
  // No relay is configured in tests, so the AI switch is disabled.
  expect(screen.getByText(/aren’t available in this build/)).toBeTruthy();
  await fireEvent.press(screen.getByTestId('welcome-next'));
  expect(screen.getByText('Safety first')).toBeTruthy();
  expect(screen.getByText('Medical emergency? Call 911 first')).toBeTruthy();
  expect(screen.getByText('Never while driving')).toBeTruthy();
  expect(screen.getByText('Notice where you are')).toBeTruthy();
  await fireEvent.press(screen.getByTestId('welcome-safety'));
  expect(screen.getByText('Your panic plan')).toBeTruthy();
}

describe('welcome', () => {
  it('walks through safety and the plan, and accepts with AI off by default', async () => {
    await toPlanStep();
    await fireEvent.press(screen.getByTestId('plan-step-water'));
    await fireEvent.changeText(screen.getByTestId('plan-contact-name'), 'Sam');
    await fireEvent.changeText(screen.getByTestId('plan-contact-phone'), '(555) 010-2030');
    await fireEvent.press(screen.getByTestId('welcome-accept'));
    const s = useAppStore.getState();
    expect(selectHasAccepted(s)).toBe(true);
    expect(s.settings.aiEnabled).toBe(false);
    expect(s.settings.panicPlan.steps).toContain('water');
    expect(s.settings.trustedContact).toEqual({ name: 'Sam', phone: '5550102030' });
  });

  it('refuses a phone number that is not one', async () => {
    await toPlanStep();
    await fireEvent.changeText(screen.getByTestId('plan-contact-phone'), 'call me');
    await fireEvent.press(screen.getByTestId('welcome-accept'));
    expect(screen.getByText(/doesn’t look like a phone number/)).toBeTruthy();
    expect(selectHasAccepted(useAppStore.getState())).toBe(false);
  });

  it('can skip to the suggested plan', async () => {
    await toPlanStep();
    await fireEvent.press(screen.getByTestId('welcome-skip-plan'));
    const s = useAppStore.getState();
    expect(selectHasAccepted(s)).toBe(true);
    expect(s.settings.panicPlan).toEqual(DEFAULT_PLAN);
    expect(s.settings.trustedContact).toBeNull();
  });
});
