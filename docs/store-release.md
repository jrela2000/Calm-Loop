# Store release checklist (App Store + Google Play)

CalmLoop ships from the Expo app in [`mobile/`](../mobile). The older Android-only Kotlin app in
`app/` is kept for reference until the Expo app reaches the stores. Items marked **(you)** can't
be done from the code.

## Done in code

- [x] One codebase for iOS, Android and the web demo (Expo SDK 58)
- [x] Bundle ID / package: `com.jarrodqsimpson.calmloop` (same as the Kotlin app, so the Expo
      build can replace it on Play). Permanent once uploaded; change it now in `mobile/app.json` if needed.
- [x] No account, no user database, no analytics; all data on the device; export and restore backups
- [x] No AI key in the app: AI goes through the stateless relay in `proxy/` (CORS limited to the web demo's origins)
- [x] AI is opt-in, off by default, labeled where it appears, and switchable in Settings
- [x] First-launch disclaimer and consent; in-app Privacy Policy & Terms
- [x] Microphone and speech-recognition permission strings explain why (iOS rejects vague ones)
- [x] No background audio or foreground services (avoids extra Play declarations)
- [x] Light and dark mode, Reduce Motion, screen-reader labels, 44 pt+ tap targets
- [x] Tests, typecheck, lint, expo-doctor clean; iOS, Android and web bundles build

## 1. Accounts **(you)**

- Apple Developer Program (US$99/yr) and a Google Play developer account
- An Expo account, then from `mobile/`: `npx eas-cli@latest init` (links the project; EAS will
  create and store the iOS certificates and the Android upload key for you)

## 2. AI relay **(you)**

Deploy `proxy/` (see `proxy/README.md`), then set `EXPO_PUBLIC_AI_PROXY_URL` as an EAS environment
variable (or in `mobile/.env` for local builds). Check `ALLOWED_ORIGINS` in `proxy/wrangler.toml`
matches the web demo's address. Without the relay the app ships with AI unavailable.

## 3. Build and submit **(you)**

```bash
cd mobile
npx eas-cli@latest build --platform all --profile production
npx eas-cli@latest submit --platform ios       # goes to App Store Connect / TestFlight
npx eas-cli@latest submit --platform android   # goes to the Play Console
```

## 4. Privacy policy and support URL **(you)**

Publish `docs/privacy-policy.md` and `docs/terms-of-use.md` on jarrodqsimpson.com. Both stores
require the privacy URL; Apple also requires a support URL. Add a contact email to the policy.

## 5. Apple: App Privacy ("nutrition label") — suggested answers, verify before submitting

- **Data used to track you:** none.
- **Data linked to you:** none (no accounts or identifiers).
- **Data not linked to you:** *User Content → Other User Content* (journal text, only when AI
  reflections are on), purpose **App Functionality**. Processed by the relay and Google and not retained.
- Speech recognition is done by Apple's on-device/Siri service; CalmLoop receives text only.

## 6. Google Play: Data safety — suggested answers, verify before submitting

- Collects data: **Yes**, only when AI reflections are on: *App activity → Other user-generated
  content* (consider also *Health and fitness → Health info*). Not shared (Cloudflare and Google
  are service providers). **Processed ephemerally**, **optional**, encrypted in transit, purpose
  **App functionality**.
- Microphone: speech recognition by the device's system service; CalmLoop doesn't collect audio.

## 7. Review-sensitive points

- **Apple 1.4.1 (medical):** the app makes no diagnostic or treatment claims, shows the "not a
  substitute for care" disclaimer at first launch, and links to crisis services. Keep store copy
  consistent ("self-help tool", never "treats anxiety").
- **Apple 5.1.1:** no account is required, and data deletion is in Settings.
- **Play Health apps declaration:** category "Mental and behavioral health"; not a medical device.
- **Age rating / target audience:** 18+ (matches the privacy policy).
- **Crisis numbers are US-specific.** There is an international helpline link, but consider
  limiting initial availability to the US in both stores.

## 8. Store listings **(you)**

Name, subtitle/short description, description, keywords, category Health & Fitness, screenshots
(take them from a production build on a 6.9" iPhone and a Pixel), 1024×500 Play feature graphic,
support email. The app icon is generated from `mobile/assets/brand/logo-source.jpg`
(`mobile/scripts/generate-icons.py`).

## 9. Testing tracks **(you)**

- iOS: TestFlight internal testing, then submit for review.
- Android: new personal developer accounts need a **closed test with at least 12 testers for 14
  continuous days** before production access.

## 10. Web demo **(you)**

`npx expo export --platform web` in `mobile/` produces a static single-page app in `mobile/dist/`.
Host it (e.g. on DreamHost) with every path falling back to `index.html`, and link it from the
CalmLoop project section on jarrodqsimpson.com.

## Known gaps (not store blockers)

- Not yet run on physical iPhones/Android phones: test haptic timing, sound levels and dictation on devices.
- The studio UX Reference Library moodboard and haptic map weren't available in this environment;
  the haptic/sound map in `mobile/src/lib/feedback.ts` should be reconciled with it.
- No paywall/RevenueCat yet (the Kotlin app had none either).
