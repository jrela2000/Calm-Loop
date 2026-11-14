/**
 * In-app copies of CalmLoop's legal text. Keep in sync with docs/privacy-policy.md and
 * docs/terms-of-use.md at the repo root (published on the web and linked from store listings).
 */
export const LAST_UPDATED = 'November 14, 2026';

export const SAFETY_TERMS =
  'Never use CalmLoop while driving: pull over somewhere safe and stop first. If you are somewhere unsafe, move to a safe place ' +
  'before using the app. If you might be having a medical emergency, such as chest pain, trouble breathing or fainting, call 911 ' +
  'or your local emergency number first. CalmLoop cannot tell panic from a medical problem.';

export const MEDICAL_DISCLAIMER =
  'CalmLoop is a self-help tool for everyday anxiety and stress. It is not a medical device, does not ' +
  'diagnose or treat any condition, and is not a substitute for a doctor, therapist or emergency ' +
  'services. If you are in crisis or thinking about harming yourself, call or text 988 (US), call ' +
  'your local emergency number, or open Crisis Support in the app.';

export const AI_SUMMARY =
  'AI reflections are optional and off unless you turn them on. When on, the text of a journal entry ' +
  "(or a search and your entries' text) is sent over an encrypted connection to CalmLoop's relay " +
  "server and on to Google's Gemini API to generate a CBT summary. The relay does not store or log " +
  'your text. Google processes it under the Gemini API terms. AI output is labeled in the app and can ' +
  'be wrong. AI tools were also used to help build this app.';

export const privacySections: [string, string][] = [
  [
    'Our approach',
    'CalmLoop has no accounts and no user database. We don’t sell data, show ads, or use analytics or tracking. Your journal stays on your device.',
  ],
  [
    'What is stored on your device',
    'Journal entries and their summaries, breathing and grounding history, your preferred name, your panic plan, an optional emergency contact (a name and phone number) and app settings. The emergency contact is never sent to us or anyone else and is not included in backup files you export; calling or texting them opens your own phone or messages app. It lives only in the app’s private storage on your device (in your browser’s storage on the web demo). On Android it is excluded from Google cloud backups; on iPhone it is part of your own encrypted iCloud or computer backup if you use one, like any app’s data. Settings › Export creates a backup file only when you ask, and Settings › Delete all my data erases everything. Uninstalling the app also removes it.',
  ],
  [
    'Microphone',
    'Voice input turns speech into text using your device’s speech recognition (Apple, Google, or your browser). CalmLoop does not record or keep audio. When your device can transcribe by itself, CalmLoop uses that. When it can’t, the app asks you once before using the online service, which may process audio under its own privacy policy. You can always type instead.',
  ],
  ['AI reflections (optional)', AI_SUMMARY],
  [
    'Links and phone calls',
    'Crisis lines open your phone or messaging app; book links open your browser. CalmLoop doesn’t see what happens after you leave the app and earns nothing from book links.',
  ],
  ['Children', 'CalmLoop is intended for adults 18 and over and does not knowingly collect data from children.'],
  [
    'Changes and contact',
    'If this policy changes we’ll update the date above and show the notice in the app again. Questions: contact the developer through the email listed on CalmLoop’s store page.',
  ],
];

export const termsSections: [string, string][] = [
  ['Not medical care', MEDICAL_DISCLAIMER],
  ['Use it safely', SAFETY_TERMS],
  [
    'AI content',
    'AI-generated summaries are reflections, not advice. They may be inaccurate or incomplete. Don’t rely on them for decisions about your health.',
  ],
  [
    'Your content',
    'You own what you write. Because it’s stored only on your device, you’re responsible for keeping your own copies (Settings › Export).',
  ],
  [
    'Provided as is',
    'CalmLoop is provided “as is” without warranties. To the extent the law allows, the developer isn’t liable for losses arising from use of the app.',
  ],
  [
    'Third-party services',
    'Crisis lines, helplines, speech recognition, Google Gemini and book stores are run by third parties under their own terms. Phone numbers listed are for the United States unless stated.',
  ],
];
