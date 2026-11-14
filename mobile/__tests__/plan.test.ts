import { cleanPhone, cleanPlan, DEFAULT_PLAN, DEFAULT_REMINDER, togglePlanStep } from '@/lib/plan';
import { makeBackup } from '@/lib/exporting';
import { defaultSettings, useAppStore } from '@/store/app-store';

describe('panic plan', () => {
  it('always starts with getting somewhere safe, in catalogue order', () => {
    expect(cleanPlan({ steps: ['ground', 'breathe'] }).steps).toEqual(['safe', 'breathe', 'ground']);
    expect(cleanPlan({ steps: ['nonsense', 'water'] }).steps).toEqual(['safe', 'water']);
  });
  it('falls back to the suggested plan', () => {
    expect(cleanPlan(undefined)).toEqual(DEFAULT_PLAN);
    expect(cleanPlan({ steps: ['breathe'], reminder: 42 }).reminder).toBe(DEFAULT_REMINDER);
  });
  it('never lets the safety step be removed', () => {
    expect(togglePlanStep(DEFAULT_PLAN, 'safe')).toEqual(DEFAULT_PLAN);
    const off = togglePlanStep(DEFAULT_PLAN, 'breathe');
    expect(off.steps).not.toContain('breathe');
    expect(togglePlanStep(off, 'breathe').steps).toEqual(DEFAULT_PLAN.steps);
  });
  it('caps the reminder length', () => expect(cleanPlan({ steps: [], reminder: 'x'.repeat(400) }).reminder).toHaveLength(280));
});

describe('phone numbers', () => {
  it('keeps digits and a leading plus', () => {
    expect(cleanPhone(' (555) 010-2030 ')).toBe('5550102030');
    expect(cleanPhone('+44 20 7946 0000')).toBe('+442079460000');
  });
  it('rejects things that cannot be numbers', () => {
    expect(cleanPhone('')).toBe('');
    expect(cleanPhone('call mum')).toBe('');
    expect(cleanPhone('1'.repeat(16))).toBe('');
  });
});

describe('emergency contact privacy', () => {
  beforeEach(() => useAppStore.setState({ settings: { ...defaultSettings }, entries: [], breathing: [], grounding: [] }));

  it('is never part of an exported backup', () => {
    useAppStore.getState().updateSettings({ trustedContact: { name: 'Sam', phone: '5550102030' } });
    const { entries, breathing, grounding } = useAppStore.getState();
    const text = JSON.stringify(makeBackup({ entries, breathing, grounding }));
    expect(text).not.toContain('Sam');
    expect(text).not.toContain('5550102030');
  });
  it('is removed by Delete all my data', () => {
    useAppStore.getState().updateSettings({ trustedContact: { name: 'Sam', phone: '5550102030' } });
    useAppStore.getState().deleteAllData();
    expect(useAppStore.getState().settings.trustedContact).toBeNull();
    expect(useAppStore.getState().settings.panicPlan).toEqual(DEFAULT_PLAN);
  });
});
