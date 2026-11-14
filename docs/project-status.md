# CalmLoop — project record

| | |
| --- | --- |
| Phase | Build (Expo rebuild in `mobile/`), heading to store submission |
| SLC scope | Box breathing · 5-4-3-2-1 grounding · journal with optional AI CBT reflection · progress · crisis support |
| Out of v1 | Accounts, cloud sync, paywall/RevenueCat, reminders/notifications, non-US crisis numbers beyond the helpline finder |
| Stack | Expo SDK 58 (React Native, TypeScript, Expo Router), Reanimated, expo-audio, expo-haptics, Zustand + AsyncStorage |
| Third parties | Google Gemini (optional AI, via the stateless Cloudflare Worker relay in `proxy/`); device speech recognition (Apple/Google/browser) |
| Data | On-device only; no accounts, analytics or user database; export/restore backups |
| Demo URL | Not yet published (build with `npx expo export --platform web` in `mobile/`) |
| iOS | Not submitted |
| Android | Not submitted (Kotlin app in `app/` was prepared earlier; the Expo app replaces it under the same package name) |
| Moodboard | Not linked: the UX Reference Library wasn't reachable from the build environment |
| Open premium-feel gaps | Verify haptic timing and sound levels on physical devices; reconcile the haptic map with the studio library; onboarding is one screen (fine) but has no learn-by-doing step |
| Last status update | — |
