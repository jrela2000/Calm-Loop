import { Alert, Linking, Platform } from 'react-native';

/** Opens a URL, explaining what to do instead when the device can't (e.g. no phone app). */
export async function openSafely(url: string, fallbackMessage: string) {
  try {
    await Linking.openURL(url);
  } catch {
    if (Platform.OS === 'web') window.alert(fallbackMessage);
    else Alert.alert('Couldn’t open that', fallbackMessage);
  }
}

export const telUrl = (number: string) => `tel:${number.replace(/[^\d+]/g, '')}`;

/** iOS uses `&body=`, everything else `?body=`. */
export function smsUrl(number: string, body?: string) {
  const n = number.replace(/[^\d+]/g, '');
  if (!body) return `sms:${n}`;
  const sep = Platform.OS === 'ios' ? '&' : '?';
  return `sms:${n}${sep}body=${encodeURIComponent(body)}`;
}

export type CrisisLine = {
  id: string;
  name: string;
  detail: string;
  badge: string;
  action: { kind: 'call' | 'text'; number: string; body?: string };
};

export const US_CRISIS_LINES: CrisisLine[] = [
  {
    id: '988-call',
    name: '988 Suicide & Crisis Lifeline',
    detail: 'Free, confidential, 24/7 support in English and Spanish.',
    badge: 'Call 988',
    action: { kind: 'call', number: '988' },
  },
  {
    id: '988-text',
    name: 'Text the 988 Lifeline',
    detail: 'Prefer texting? Message a trained crisis counselor.',
    badge: 'Text 988',
    action: { kind: 'text', number: '988' },
  },
  {
    id: 'ctl',
    name: 'Crisis Text Line',
    detail: 'Text HOME to 741741 to reach a crisis counselor.',
    badge: 'Text 741741',
    action: { kind: 'text', number: '741741', body: 'HOME' },
  },
  {
    id: 'samhsa',
    name: 'SAMHSA National Helpline',
    detail: 'Treatment referral and information, 24/7.',
    badge: '1-800-662-4357',
    action: { kind: 'call', number: '18006624357' },
  },
];
