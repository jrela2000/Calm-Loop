import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { useEffect, useMemo, useRef, useState } from 'react';
import { ActivityIndicator, StyleSheet, TextInput, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear, EmptyState } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Screen } from '@/components/Screen';
import { searchEntries, type SearchResult } from '@/lib/ai';
import { useAppStore } from '@/store/app-store';
import type { JournalEntry } from '@/store/types';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

function EntryCard({ entry, index }: { entry: JournalEntry; index: number }) {
  const date = new Date(entry.createdAt).toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' });
  return (
    <Appear index={Math.min(index, 6)}>
      <Card onPress={() => router.push(`/journal/${entry.id}`)} testID={`entry-${entry.id}`} accessibilityLabel={`Journal entry from ${date}`}>
        <AppText variant="label" tone="muted">
          {date.toUpperCase()}
        </AppText>
        <AppText variant="bodyStrong" numberOfLines={2} style={{ marginTop: space.xs }}>
          {entry.text}
        </AppText>
        <AppText variant="caption" tone="secondary" numberOfLines={2} style={{ marginTop: space.xs }}>
          {entry.aiGenerated ? '✨ ' : ''}
          {entry.synopsis}
        </AppText>
      </Card>
    </Appear>
  );
}

export default function JournalTab() {
  const t = useTheme();
  const entries = useAppStore((s) => s.entries);
  const aiEnabled = useAppStore((s) => s.settings.aiEnabled);
  const [query, setQuery] = useState('');
  const [resolved, setResolved] = useState<{ query: string; result: SearchResult } | null>(null);
  const latest = useRef(0);
  const q = query.trim();
  const result = q && resolved?.query === q ? resolved.result : null;
  const searching = !!q && resolved?.query !== q;

  // Debounced search; a newer query always wins over a slower older one.
  useEffect(() => {
    if (!q) return;
    const ticket = ++latest.current;
    const timer = setTimeout(async () => {
      const r = await searchEntries(q, entries, aiEnabled);
      if (ticket === latest.current) setResolved({ query: q, result: r });
    }, aiEnabled ? 600 : 200);
    return () => clearTimeout(timer);
  }, [q, entries, aiEnabled]);

  const shown = useMemo(() => {
    // Keep showing everything until the first result for this query arrives.
    if (!q || !result) return entries;
    const order = new Map(result.matchingIds.map((id, i) => [id, i]));
    return entries.filter((e) => order.has(e.id));
  }, [entries, result, q]);

  return (
    <Screen edges={['top']} testID="journal-tab">
      <View style={styles.titleRow}>
        <AppText variant="display" style={{ flex: 1 }}>
          Journal
        </AppText>
        <Button label="New" icon="add" onPress={() => router.push('/journal/new')} testID="journal-new" />
      </View>

      {entries.length === 0 ? (
        <EmptyState
          icon="book-outline"
          title="Your journal is empty"
          body="When a thought keeps looping, write it down. CalmLoop helps you see it from a kinder angle."
          actionLabel="Write your first entry"
          onAction={() => router.push('/journal/new')}
        />
      ) : (
        <>
          <View style={[styles.search, { backgroundColor: t.surface, borderColor: t.border }]}>
            <Ionicons name="search" size={18} color={t.textMuted} />
            <TextInput
              testID="journal-search"
              value={query}
              onChangeText={setQuery}
              placeholder={aiEnabled ? 'Search feelings or themes, e.g. “work panic”' : 'Search your entries'}
              placeholderTextColor={t.textMuted}
              style={[styles.input, { outlineColor: t.blue, color: t.text }]}
              accessibilityLabel="Search journal"
              returnKeyType="search"
            />
            {searching ? <ActivityIndicator color={t.blueStrong} /> : null}
          </View>

          {result?.insight ? (
            <Card tone="lavender">
              <AppText variant="label" tone="muted">
                {result.fromAi ? '✨ AI-GENERATED PATTERN INSIGHT' : 'SEARCH'}
              </AppText>
              <AppText variant="caption" style={{ marginTop: space.xs }}>
                {result.insight}
              </AppText>
            </Card>
          ) : null}

          {shown.map((e, i) => (
            <EntryCard key={e.id} entry={e} index={i} />
          ))}
        </>
      )}
    </Screen>
  );
}

const styles = StyleSheet.create({
  titleRow: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  search: { flexDirection: 'row', alignItems: 'center', gap: space.sm, borderWidth: 1, borderRadius: radius.md, paddingHorizontal: space.md, minHeight: 48 },
  input: { flex: 1, fontSize: 15, paddingVertical: space.sm },
});
