package com.jarrodqsimpson.calmloop.ui.about

/**
 * In-app copies of CalmLoop's legal text. Keep in sync with docs/privacy-policy.md and
 * docs/terms-of-use.md (the versions published on the web and linked from the store listings).
 */
object LegalText {
    const val LAST_UPDATED = "November 14, 2026"

    const val MEDICAL_DISCLAIMER =
        "CalmLoop is a self-help tool for everyday anxiety and stress. It is not a medical device, " +
            "does not diagnose or treat any condition, and is not a substitute for a doctor, therapist " +
            "or emergency services. If you are in crisis or thinking about harming yourself, call or " +
            "text 988 (US), call your local emergency number, or open Crisis Support in the app."

    const val AI_SUMMARY =
        "AI reflections are optional and off unless you turn them on. When on, the text of a journal " +
            "entry (or a search and your entries' text) is sent over an encrypted connection to " +
            "CalmLoop's relay server and on to Google's Gemini API to generate a CBT summary. The relay " +
            "does not store or log your text. Google processes it under the Gemini API terms. AI output " +
            "is labeled in the app and can be wrong. AI tools were also used to help build this app."

    val privacySections: List<Pair<String, String>> = listOf(
        "Our approach" to
            "CalmLoop has no accounts and no user database. We don't sell data, show ads, or use " +
            "analytics or tracking. Your journal stays on your phone.",
        "What is stored on your device" to
            "Journal entries and their summaries, breathing and grounding session history, your " +
            "preferred name and app settings. This is stored only in the app's private storage on your " +
            "device and is excluded from Google cloud backups. Settings > Delete All My Data erases it; " +
            "uninstalling the app also removes it.",
        "Microphone" to
            "Voice journaling and voice grounding use your phone's speech recognition service to turn " +
            "speech into text. CalmLoop does not record or keep audio. Depending on your device, the " +
            "speech service (usually Google) may process audio under its own privacy policy.",
        "AI reflections (optional)" to AI_SUMMARY,
        "Links and phone calls" to
            "Crisis lines open your phone or messaging app; book links open your browser. CalmLoop " +
            "doesn't see what happens after you leave the app. Book link taps are counted only on your " +
            "device and never sent anywhere.",
        "Children" to "CalmLoop is intended for adults 18 and over and does not knowingly collect data from children.",
        "Changes and contact" to
            "If this policy changes we'll update the date above and show the notice in the app again. " +
            "Questions: contact the developer through the email listed on CalmLoop's store page."
    )

    val termsSections: List<Pair<String, String>> = listOf(
        "Not medical care" to MEDICAL_DISCLAIMER,
        "AI content" to
            "AI-generated summaries are reflections, not advice. They may be inaccurate or incomplete. " +
            "Don't rely on them for decisions about your health.",
        "Your content" to
            "You own what you write. Because it's stored only on your device, you're responsible for " +
            "keeping your own copies (use Export on any entry).",
        "Provided as is" to
            "CalmLoop is provided \"as is\" without warranties. To the extent the law allows, the " +
            "developer isn't liable for losses arising from use of the app.",
        "Third-party services" to
            "Crisis lines, helplines, speech recognition, Google Gemini and book stores are run by third " +
            "parties under their own terms. Phone numbers listed are for the United States unless stated."
    )
}
