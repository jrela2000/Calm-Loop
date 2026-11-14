export type ThemePreference = 'system' | 'light' | 'dark';

export type JournalEntry = {
  id: string;
  createdAt: number;
  text: string;
  activatingEvent: string;
  beliefs: string;
  consequences: string;
  synopsis: string;
  /** True when the synopsis and ABC breakdown came from AI; false for on-device prompts. */
  aiGenerated: boolean;
};

export type BreathingOutcome = 'better' | 'same' | 'worse';

export type BreathingSession = {
  id: string;
  createdAt: number;
  rounds: number;
  outcome?: BreathingOutcome;
};

export type GroundingSession = {
  id: string;
  createdAt: number;
  /** Answers per sense, in order: see, touch, hear, smell, taste. */
  answers: string[][];
};

export type TrustedContact = { name: string; phone: string };

export type PlanStepId = 'safe' | 'breathe' | 'ground' | 'reminder' | 'contact' | 'water' | 'journal';

/** The person's panic plan: the steps they chose and the words they wrote for themselves. */
export type PanicPlan = { steps: PlanStepId[]; reminder: string };

export type Settings = {
  userName: string;
  acceptedDisclaimerVersion: number;
  aiEnabled: boolean;
  soundEnabled: boolean;
  hapticsEnabled: boolean;
  theme: ThemePreference;
  trustedContact: TrustedContact | null;
  panicPlan: PanicPlan;
  /** Agreed to use the phone's or browser's online speech service when on-device speech isn't available. */
  voiceConsent: boolean;
};

export type JournalJob =
  | { status: 'idle' }
  | { status: 'processing'; startedAt: number }
  | { status: 'done'; entryId: string };
