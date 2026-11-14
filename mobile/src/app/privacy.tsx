import { View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Card } from '@/components/Card';
import { Header } from '@/components/Header';
import { Screen } from '@/components/Screen';
import { LAST_UPDATED, MEDICAL_DISCLAIMER, privacySections, termsSections } from '@/lib/legal';
import { space } from '@/theme/tokens';

function Section({ title, items }: { title: string; items: [string, string][] }) {
  return (
    <Card>
      <AppText variant="title">{title}</AppText>
      {items.map(([heading, body]) => (
        <View key={heading} style={{ marginTop: space.md, gap: space.xs }}>
          <AppText variant="bodyStrong">{heading}</AppText>
          <AppText variant="caption" tone="secondary">
            {body}
          </AppText>
        </View>
      ))}
    </Card>
  );
}

/** Privacy policy, terms and disclaimer, readable offline. */
export default function Privacy() {
  return (
    <Screen testID="privacy-screen">
      <Header title="Privacy & Terms" />
      <AppText variant="caption" tone="muted">
        Last updated {LAST_UPDATED}
      </AppText>
      <Section title="Important" items={[['Not a substitute for care', MEDICAL_DISCLAIMER]]} />
      <Section title="Privacy Policy" items={privacySections} />
      <Section title="Terms of Use" items={termsSections} />
    </Screen>
  );
}
