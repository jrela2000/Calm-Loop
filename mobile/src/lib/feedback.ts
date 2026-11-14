import { type AudioPlayer, createAudioPlayer, setAudioModeAsync } from 'expo-audio';
import * as Haptics from 'expo-haptics';
import { Platform } from 'react-native';

import { useAppStore } from '@/store/app-store';

/**
 * CalmLoop's interaction map: every meaningful moment has one haptic and (optionally) one sound,
 * both soft, and both switchable in Settings. Sounds respect the device's silent switch.
 *
 * | Moment             | Haptic                 | Sound    |
 * | ------------------ | ---------------------- | -------- |
 * | tap                | light impact           | tap      |
 * | toggle / select    | selection              | select   |
 * | breathe in / out   | soft impact            | inhale / exhale |
 * | hold               | selection              | hold     |
 * | round complete     | medium impact          | —        |
 * | grounding step     | medium impact          | step     |
 * | success / saved    | success notification   | success  |
 * | warning (delete)   | warning notification   | —        |
 */
const sources = {
  tap: require('../../assets/sounds/tap.wav'),
  select: require('../../assets/sounds/select.wav'),
  inhale: require('../../assets/sounds/inhale.wav'),
  hold: require('../../assets/sounds/hold.wav'),
  exhale: require('../../assets/sounds/exhale.wav'),
  step: require('../../assets/sounds/step.wav'),
  success: require('../../assets/sounds/success.wav'),
} as const;

type SoundName = keyof typeof sources;

const VOLUME: Record<SoundName, number> = {
  tap: 0.35,
  select: 0.4,
  inhale: 0.55,
  hold: 0.5,
  exhale: 0.55,
  step: 0.6,
  success: 0.6,
};

const players = new Map<SoundName, AudioPlayer>();
let audioReady: Promise<void> | null = null;

function ensureAudio() {
  if (!audioReady) {
    // Play UI sounds alongside other audio, and stay silent when the ringer is off.
    audioReady = setAudioModeAsync({ playsInSilentMode: false, interruptionMode: 'mixWithOthers' }).catch(() => {});
  }
  return audioReady;
}

function playSound(name: SoundName) {
  if (!useAppStore.getState().settings.soundEnabled) return;
  void ensureAudio().then(() => {
    try {
      let player = players.get(name);
      if (!player) {
        player = createAudioPlayer(sources[name]);
        player.volume = VOLUME[name];
        players.set(name, player);
      }
      player.seekTo(0);
      player.play();
    } catch {
      // Sound is decoration; never let it break an interaction.
    }
  });
}

function haptic(run: () => Promise<void>) {
  if (!useAppStore.getState().settings.hapticsEnabled) return;
  // Web vibration is buzzy and unsupported on iOS Safari; keep the web demo quiet.
  if (Platform.OS === 'web') return;
  run().catch(() => {});
}

export const feedback = {
  tap() {
    haptic(() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light));
    playSound('tap');
  },
  select() {
    haptic(() => Haptics.selectionAsync());
    playSound('select');
  },
  inhale() {
    haptic(() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Soft));
    playSound('inhale');
  },
  hold() {
    haptic(() => Haptics.selectionAsync());
    playSound('hold');
  },
  exhale() {
    haptic(() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Soft));
    playSound('exhale');
  },
  rest() {
    haptic(() => Haptics.selectionAsync());
  },
  round() {
    haptic(() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium));
  },
  step() {
    haptic(() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium));
    playSound('step');
  },
  success() {
    haptic(() => Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success));
    playSound('success');
  },
  warning() {
    haptic(() => Haptics.notificationAsync(Haptics.NotificationFeedbackType.Warning));
  },
};
