import { useCallback, useEffect, useRef, useState } from 'react';

type SpeechModule = {
  isRecognitionAvailable(): boolean;
  requestPermissionsAsync(): Promise<{ granted: boolean }>;
  start(options: Record<string, unknown>): void;
  stop(): void;
  abort(): void;
  addListener(event: string, cb: (e: any) => void): { remove(): void };
};

// The native module only exists in development/production builds (not Expo Go), and the web
// version needs a browser with the Web Speech API. Load it defensively so the app always runs.
let speech: SpeechModule | null = null;
try {
  // eslint-disable-next-line @typescript-eslint/no-require-imports -- optional native module
  speech = require('expo-speech-recognition').ExpoSpeechRecognitionModule as SpeechModule;
} catch {
  speech = null;
}

function available(): boolean {
  try {
    return !!speech && speech.isRecognitionAvailable();
  } catch {
    return false;
  }
}

export type DictationState = 'idle' | 'listening' | 'denied' | 'error';

/**
 * Speech-to-text for journaling. Calls `onFinal` with each finished phrase and exposes the
 * in-progress phrase as `partial`. Audio is never recorded or stored.
 */
export function useDictation(onFinal: (text: string) => void) {
  const [state, setState] = useState<DictationState>('idle');
  const [partial, setPartial] = useState('');
  const [supported] = useState(available);
  const onFinalRef = useRef(onFinal);
  useEffect(() => {
    onFinalRef.current = onFinal;
  }, [onFinal]);

  useEffect(() => {
    if (!speech || !supported) return;
    const subs = [
      speech.addListener('start', () => setState('listening')),
      speech.addListener('end', () => {
        setPartial('');
        setState((s) => (s === 'listening' ? 'idle' : s));
      }),
      speech.addListener('result', (e: { isFinal: boolean; results: { transcript: string }[] }) => {
        const text = e.results?.[0]?.transcript ?? '';
        if (e.isFinal) {
          setPartial('');
          if (text.trim()) onFinalRef.current(text.trim());
        } else {
          setPartial(text);
        }
      }),
      speech.addListener('error', (e: { error: string }) => {
        setPartial('');
        setState(e.error === 'not-allowed' ? 'denied' : e.error === 'no-speech' || e.error === 'aborted' ? 'idle' : 'error');
      }),
    ];
    return () => {
      subs.forEach((s) => s.remove());
      try {
        speech?.abort();
      } catch {}
    };
  }, [supported]);

  const start = useCallback(async () => {
    if (!speech || !supported) return;
    const perm = await speech.requestPermissionsAsync();
    if (!perm.granted) {
      setState('denied');
      return;
    }
    speech.start({ lang: Intl.DateTimeFormat().resolvedOptions().locale, interimResults: true, continuous: true, addsPunctuation: true });
  }, [supported]);

  const stop = useCallback(() => {
    try {
      speech?.stop();
    } catch {}
  }, []);

  return { supported, state, partial, start, stop };
}
