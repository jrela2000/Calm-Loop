import { isAiConfigured, keywordSearch, offlineReframe, reframeEntry, searchEntries } from '@/lib/ai';
import type { JournalEntry } from '@/store/types';

const RELAY = 'https://relay.test';
const entry = (id: string, text: string): JournalEntry => ({
  id, text, createdAt: 0, activatingEvent: '', beliefs: '', consequences: '', synopsis: '', aiGenerated: false,
});
const entries = [entry('a', 'Panic before a work presentation'), entry('b', 'Calm walk in the park'), entry('c', 'Work emails at night made me panic')];

const mockFetch = (body: unknown, ok = true, status = 200) =>
  (globalThis.fetch = jest.fn(() => Promise.resolve({ ok, status, json: () => Promise.resolve(body) })) as unknown as typeof fetch);

afterEach(() => jest.restoreAllMocks());

describe('AI reflections', () => {
  it('only treats https (or localhost) as configured', () => {
    expect(isAiConfigured('')).toBe(false);
    expect(isAiConfigured('http://example.com')).toBe(false);
    expect(isAiConfigured(RELAY)).toBe(true);
    expect(isAiConfigured('http://localhost:8787')).toBe(true);
  });

  it('sends nothing when AI is off', async () => {
    const f = mockFetch({});
    const r = await reframeEntry('private thoughts', false, RELAY);
    expect(r).toEqual(offlineReframe());
    expect(f).not.toHaveBeenCalled();
  });

  it('uses the relay response when on', async () => {
    const f = mockFetch({ activating_event: 'A', beliefs: 'B', consequences: 'C', ai_synopsis: 'S' });
    const r = await reframeEntry('I panicked', true, RELAY);
    expect(r).toEqual({ activatingEvent: 'A', beliefs: 'B', consequences: 'C', synopsis: 'S', fromAi: true });
    const [url, init] = (f as jest.Mock).mock.calls[0];
    expect(url).toBe(`${RELAY}/v1/reframe`);
    expect(JSON.parse(init.body)).toEqual({ transcription: 'I panicked' });
  });

  it('falls back honestly on server errors and bad responses', async () => {
    mockFetch({}, false, 500);
    expect((await reframeEntry('x', true, RELAY)).fromAi).toBe(false);
    mockFetch({ ai_synopsis: '' });
    expect((await reframeEntry('x', true, RELAY)).fromAi).toBe(false);
  });

  it('maps relay search ids back to entry ids and ignores invented ones', async () => {
    const f = mockFetch({ matching_ids: [1, 3, 99, 1], pattern_insight: 'Work triggers panic.' });
    const r = await searchEntries('work', entries, true, RELAY);
    expect(r).toEqual({ matchingIds: ['a', 'c'], insight: 'Work triggers panic.', fromAi: true });
    const sent = JSON.parse((f as jest.Mock).mock.calls[0][1].body);
    expect(sent.entries.map((e: { id: number }) => e.id)).toEqual([1, 2, 3]);
  });

  it('keyword search needs every word, in any order', () => {
    expect(keywordSearch('panic work', entries).matchingIds).toEqual(['a', 'c']);
    expect(keywordSearch('beach', entries).matchingIds).toEqual([]);
  });
});
