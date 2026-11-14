import { entryAsText, makeBackup, parseBackup } from '@/lib/exporting';
import type { JournalEntry } from '@/store/types';

const entry: JournalEntry = {
  id: 'e1', createdAt: 0, text: 'Line one\nline two', activatingEvent: 'Meeting', beliefs: "I'll fail",
  consequences: 'Racing heart', synopsis: 'Be kind to yourself.', aiGenerated: true,
};

describe('export and backup', () => {
  it('labels AI text and keeps multi-line entries intact', () => {
    const text = entryAsText(entry);
    expect(text).toContain('AI-generated reflection:');
    expect(text.endsWith('Line one\nline two')).toBe(true);
    expect(entryAsText({ ...entry, aiGenerated: false })).not.toContain('AI-generated');
  });

  it('round-trips a backup', () => {
    const backup = makeBackup({ entries: [entry], breathing: [{ id: 'b1', createdAt: 1, rounds: 4, outcome: 'better' }], grounding: [{ id: 'g1', createdAt: 2, answers: [['mug'], [], [], [], []] }] });
    expect(parseBackup(JSON.stringify(backup))).toEqual({ entries: backup.entries, breathing: backup.breathing, grounding: backup.grounding });
  });

  it('drops malformed records', () => {
    const json = JSON.stringify({ app: 'calmloop', format: 1, entries: [entry, { id: 3 }], breathing: 'nope' });
    expect(parseBackup(json)).toEqual({ entries: [entry], breathing: [], grounding: [] });
  });

  it('rejects files that are not CalmLoop backups', () => {
    expect(() => parseBackup('not json')).toThrow('isn’t a CalmLoop backup');
    expect(() => parseBackup('{"app":"other"}')).toThrow('isn’t a CalmLoop backup');
    expect(() => parseBackup('{"app":"calmloop","format":2}')).toThrow('newer version');
  });
});
