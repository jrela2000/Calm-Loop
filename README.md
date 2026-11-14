# CalmLoop 🌿

> **Mobile-first CBT & Grounding Companion App for Panic and Anxiety Relief**

> **The app now lives in [`mobile/`](mobile/)**: one Expo (React Native) codebase for iOS,
> Android and the web demo. The original Kotlin Android app in `app/` is kept for reference
> until the Expo app reaches the stores.

**CalmLoop** is a mobile app designed to guide users through anxiety spikes and panic attacks using Cognitive Behavioral Therapy (CBT) techniques, interactive box breathing exercises, a 5-4-3-2-1 sensory grounding game, and AI-powered voice journaling with theme analysis and pattern recognition.

---

## ✨ Core Features

### 🎙️ AI Voice Journal & Pattern Analysis
- **Raw Thought Unfiltering**: Record or type unfiltered feelings during moments of distress.
- **Cognitive Reframing (optional AI, off by default)**: AI analyzes entries to pull key emotional themes, offer gentle reframing, and highlight recurring patterns over time.
- **Journal Search**: Search past entries by feelings, symptoms, or contexts (e.g., *"work panic"*); AI-assisted when AI is on, keyword search otherwise.

### 🫁 Guided Box Breathing Exercise
- **Visual Tracer & Animation**: Follow an animated expanding aura and tracer orb moving around a guided box cycle (4s Inhale, 4s Hold, 4s Exhale, 4s Rest).
- **Soothing Audio Synthesizer**: Optional frequency chimes pitched specifically to promote physiological calm.

### 🧩 5-4-3-2-1 Grounding Game
- **Interactive Sensory Steps**: Walk through 5 things you see, 4 you touch, 3 you hear, 2 you smell, and 1 you taste with tactile step confirmations and soothing visual cards.

### 📊 Progress & Mood Tracker
- **Streak & Check-in Logging**: Track pre- and post-exercise anxiety reduction levels and view consistency streaks.
- **Local Data Privacy**: No accounts. All journal entries and metrics are stored only on the device (Room + DataStore) and excluded from cloud backups.

### 🆘 One-Tap Crisis Support
- **Immediate Access**: One tap to US crisis lines (988, Crisis Text Line, SAMHSA, NAMI), your contacts, and an international helpline finder.

---

## 🛠️ Built With

- **App** ([`mobile/`](mobile/)): Expo SDK 58, React Native, TypeScript, Expo Router
- **Motion, sound, haptics**: Reanimated, expo-audio (synthesized UI tones), expo-haptics
- **Storage**: on-device only (AsyncStorage via Zustand); export/restore backups
- **AI**: Google Gemini via a stateless relay ([`proxy/`](proxy/)), so no API key ships in the app
- **Legacy Android app** (`app/`): Kotlin, Jetpack Compose, Room

## 📱 Build & Run

```
cd mobile && npm install && npx expo start   # see mobile/README.md
node --test proxy/worker.test.mjs           # AI relay tests
```

The legacy Kotlin app still builds with `./gradlew assembleDebug` (JDK 17+, Android SDK 36).

## 📄 Release & compliance

- [Store release checklist (App Store + Google Play)](docs/store-release.md)
- [Privacy Policy](docs/privacy-policy.md) · [Terms of Use](docs/terms-of-use.md)

CalmLoop is a self-help tool, not a medical device or a substitute for professional care.
