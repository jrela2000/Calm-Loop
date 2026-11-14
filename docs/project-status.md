# CalmLoop — project record

| | |
| --- | --- |
| Phase | Build (Expo rebuild in `mobile/`), heading to store submission. Panic-plan onboarding added 2026-11-14 on branch `panic-plan` (not yet run on a device) |
| SLC scope | Box breathing · 5-4-3-2-1 grounding · journal with optional AI CBT reflection · progress · crisis support · panic plan with a one-tap emergency contact |
| Out of v1 | Accounts, cloud sync, premium features and any paywall/RevenueCat (decided 2026-11-14: no premium features at this time), reminders/notifications, non-US emergency and crisis numbers beyond the helpline finder |
| Stack | Expo SDK 58 (React Native, TypeScript, Expo Router), Reanimated, expo-audio, expo-haptics, Zustand + AsyncStorage |
| Third parties | Google Gemini (optional AI, via the stateless Cloudflare Worker relay in `proxy/`); device speech recognition (Apple/Google/browser): on-device when supported, otherwise only after a one-time consent |
| Data | On-device only; no accounts, analytics or user database; export/restore backups. The emergency contact is also on-device only and is left out of backup files |
| Demo URL | Not yet published (build with `npx expo export --platform web` in `mobile/`) |
| iOS | Not submitted |
| Android | Not submitted (Kotlin app in `app/` was prepared earlier; the Expo app replaces it under the same package name) |
| Moodboard | Not linked: the UX Reference Library wasn't reachable from the build environment |
| Open premium-feel gaps | Verify haptic timing and sound levels on physical devices; reconcile the haptic map with the studio library; onboarding is now three steps (welcome and AI opt-in, safety first, panic plan) but still has no learn-by-doing step, so it needs a decision against the 3-screen limit; verify the new screens on devices, including the 911, call and text buttons; welcome-screen footer may cover the AI card's last line |
| Last status update | — |
| Safety design | Onboarding asks for three things before the app is used: call 911 for a medical emergency, move somewhere safe first, never use it while driving. The panic plan (`/plan`) is reachable from Home, Crisis and Settings. The 911 button is US-only |
| Decided 2026-11-14 | No premium features, paywall or RevenueCat for now; the calming tools and AI reflections stay free |
