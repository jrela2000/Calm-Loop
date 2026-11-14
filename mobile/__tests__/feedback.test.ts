import { createAudioPlayer } from 'expo-audio';
import * as Haptics from 'expo-haptics';

import { feedback } from '@/lib/feedback';
import { defaultSettings, useAppStore } from '@/store/app-store';

const flush = () => new Promise<void>((r) => setTimeout(r, 0));

beforeEach(() => {
  jest.clearAllMocks();
  useAppStore.setState({ settings: { ...defaultSettings } });
});

describe('feedback', () => {
  it('plays a sound and a haptic for success', async () => {
    feedback.success();
    await flush();
    expect(Haptics.notificationAsync).toHaveBeenCalledWith('success');
    expect(createAudioPlayer).toHaveBeenCalled();
  });

  it('respects the sound and haptics switches', async () => {
    useAppStore.setState({ settings: { ...defaultSettings, soundEnabled: false, hapticsEnabled: false } });
    feedback.step();
    await flush();
    expect(Haptics.impactAsync).not.toHaveBeenCalled();
    expect(createAudioPlayer).not.toHaveBeenCalled();
  });
});
