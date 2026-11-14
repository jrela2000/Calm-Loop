# Google Play release checklist

Status of the store blockers found in the readiness review, and what is left for a person to do in
the Play Console. Items marked **(you)** can't be done from the code.

## Done in code

- [x] Real package name: `com.jarrodqsimpson.calmloop` (permanent once uploaded; change it now if needed,
      in `app/build.gradle.kts` `applicationId`)
- [x] No API key in the app: AI goes through the stateless relay in `proxy/`
- [x] AI is opt-in (off by default), labeled in-app, and can be switched off in Settings
- [x] First-launch medical disclaimer, privacy summary and AI consent
- [x] In-app Privacy Policy & Terms (Settings → Privacy Policy & Terms)
- [x] Journal data excluded from Google cloud backups
- [x] Release build shrunk with R8; Gradle wrapper; unit tests and lint pass
- [x] Unused SDKs removed (Firebase, Retrofit, Moshi, Accompanist): fewer third parties to disclose

## 1. Signing **(you)**

```
keytool -genkeypair -v -keystore calmloop-upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

Keep `calmloop-upload.jks` and its passwords somewhere safe outside the repo (losing it means
contacting Google support to reset the upload key). Then create `keystore.properties` in the repo
root (git-ignored):

```
storeFile=/absolute/path/to/calmloop-upload.jks
storePassword=...
keyAlias=upload
keyPassword=...
```

Build the bundle with `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`.
Enroll in **Play App Signing** when creating the app (the default).

## 2. AI relay **(you)**

Deploy `proxy/` (see `proxy/README.md`), put its URL in `.env` as `AI_PROXY_URL`, rebuild. Without
it the app ships with AI reflections unavailable and everything else works.

## 3. Privacy policy URL **(you)**

Publish `docs/privacy-policy.md` (and `docs/terms-of-use.md`) on jarrodqsimpson.com and paste the
privacy URL into Play Console → App content → Privacy policy. Add a contact email to the policy's
Contact section and the store listing.

## 4. Data safety form (suggested answers, verify before submitting)

- Does the app collect or share user data? **Yes** (only when AI reflections are on).
- Data type: **App activity → Other user-generated content** (journal text). Consider also declaring
  **Health and fitness → Health info**, since entries describe anxiety symptoms.
- Collected, **not shared** (Cloudflare and Google act as service providers for the developer).
- **Processed ephemerally**: yes. **Required or optional**: optional.
- Purpose: **App functionality**.
- Encrypted in transit: **yes**. Deletion request: nothing is retained, so state that no data is
  stored off-device.
- Microphone/audio: speech recognition is done by the device's system service; CalmLoop receives text
  only and doesn't collect audio.

## 5. Other App content declarations **(you)**

- **Health apps declaration**: category "Mental and behavioral health"; not a medical device.
- **Target audience**: 18+ (matches the privacy policy).
- **Content rating** questionnaire (IARC).
- **Ads**: no ads.
- **Financial features / government**: none.

## 6. Store listing **(you)**

App name, short and full description (repeat "not a substitute for professional care"), 512×512
icon, 1024×500 feature graphic, at least 2 phone screenshots, category Health & Fitness, contact email.

## 7. Testing track **(you)**

New personal developer accounts must run a **closed test with at least 12 testers for 14 continuous
days** before applying for production access. Upload the AAB to the closed track first.

## Still open in the app (not store blockers)

- Dark mode: the theme is light-only by design for now (stores don't require dark mode).
- Launcher icon: no monochrome (themed) layer yet, and the foreground is a 376 KB JPG; a vector
  foreground would look sharper and enable Android themed icons.
- Crisis numbers are US-specific; a "find a helpline" link covers other countries. Consider limiting
  initial distribution to the US in Play Console.
