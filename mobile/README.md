# CalmLoop (iOS · Android · web)

The cross-platform CalmLoop app, built with Expo SDK 58 / React Native. One codebase ships to
the App Store, Google Play and the web demo.

- **Box breathing**: a breathing orb and a light tracing the box, paced by tones and haptics
- **5-4-3-2-1 grounding**: tap "Noticed one" for each item; typing is optional
- **Journal**: type or dictate, then get an optional AI-generated CBT ABC reflection
- **Progress**: streaks, a 14-day chart, and how you felt after breathing sessions
- **Crisis support**: US lines, an international helpline finder, and your own trusted contact
- **Privacy**: no accounts or servers storing data; everything lives on the device; export/restore backups

## Run it

```bash
npm install
npx expo start          # press w for web; native features need a dev build (below)
npm test                # Jest (logic, store, feedback, welcome screen)
npm run typecheck && npm run lint
```

Voice dictation uses `expo-speech-recognition`, a native module that isn't in Expo Go. Use a
development build: `npx eas-cli@latest build --profile development` (or `npx expo run:android|ios`
with a local toolchain). In Expo Go the mic button simply doesn't appear.

AI reflections need the relay in [`../proxy`](../proxy/README.md). Copy `.env.example` to `.env`
and set `EXPO_PUBLIC_AI_PROXY_URL`. Without it, AI is unavailable and everything else works.

## Design system

`src/theme/tokens.ts` holds the light and dark palettes, spacing, radii, type scale and
**motion tokens** (one ease-out curve, one spring, a stagger). Appearance follows the system
by default and can be forced in Settings.

| Premium-feel piece | Where |
| --- | --- |
| Press feedback on every tap (spring scale, ~100 ms) | `src/components/PressableScale.tsx` |
| Staggered card entrances, animated empty states | `src/components/Bits.tsx` |
| Breathing orb + box tracer (signature moment) | `src/components/BreathingOrb.tsx` |
| Completion burst (signature moment) | `src/components/Celebration.tsx` |
| "Untangling" particles while a journal entry saves | `src/app/journal/processing.tsx` |
| Haptic + sound map, mute and haptics switches | `src/lib/feedback.ts` |
| Sounds (synthesized, regenerate with `npm run sounds`) | `assets/sounds/`, `scripts/generate-sounds.py` |

Reduce Motion is respected everywhere (scale animations become fades, particles are skipped).
Sounds respect the silent switch and mix with other audio. Haptics are off on the web demo.

## Ship it

```bash
npx eas-cli@latest build --platform all --profile production
npx eas-cli@latest submit --platform ios
npx eas-cli@latest submit --platform android
npx expo export --platform web   # static web demo in dist/ (single-page app)
```

See [`../docs/store-release.md`](../docs/store-release.md) for the full store checklist.
