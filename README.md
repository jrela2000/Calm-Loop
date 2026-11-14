# CalmLoop 🌿

> **Mobile-first CBT & Grounding Companion App for Panic and Anxiety Relief**

**CalmLoop** is an Android application designed to guide users through anxiety spikes and panic attacks using Cognitive Behavioral Therapy (CBT) techniques, interactive box breathing exercises, a 5-4-3-2-1 sensory grounding game, and AI-powered voice journaling with theme analysis and pattern recognition.

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

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material Design 3 with custom glassmorphic styling)
- **Architecture**: MVVM + Clean Architecture
- **Local Storage**: Room (KSP, exported schemas) + DataStore
- **Async & Reactive Flow**: Kotlin Coroutines & StateFlow
- **AI Integration**: Google Gemini via a stateless relay (`proxy/`), so no API key ships in the app

---

## 📱 Build & Run

Requirements: JDK 17+, Android SDK (API 36). The Gradle wrapper downloads Gradle itself.

```
./gradlew assembleDebug          # debug APK
./gradlew testDebugUnitTest      # unit + Robolectric tests
./gradlew lintRelease            # lint
./gradlew bundleRelease          # Play Store bundle (signed if keystore.properties exists)
node --test proxy/worker.test.mjs  # AI relay tests
```

AI reflections need the relay: deploy `proxy/` (see `proxy/README.md`), then copy `.env.example`
to `.env` and set `AI_PROXY_URL`. Without it the app builds and runs with AI unavailable.

## 📄 Release & compliance

- [Google Play release checklist](docs/play-store-release.md)
- [Privacy Policy](docs/privacy-policy.md) · [Terms of Use](docs/terms-of-use.md)

CalmLoop is a self-help tool, not a medical device or a substitute for professional care.
