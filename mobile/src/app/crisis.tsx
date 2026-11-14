import Ionicons from '@expo/vector-icons/Ionicons';
import { router } from 'expo-router';
import { StyleSheet, View } from 'react-native';

import { AppText } from '@/components/AppText';
import { Appear, SectionLabel } from '@/components/Bits';
import { Button } from '@/components/Button';
import { Card } from '@/components/Card';
import { Header } from '@/components/Header';
import { Screen } from '@/components/Screen';
import { type CrisisLine, openSafely, smsUrl, telUrl, US_CRISIS_LINES } from '@/lib/links';
import { useAppStore } from '@/store/app-store';
import { useTheme } from '@/theme/ThemeProvider';
import { radius, space } from '@/theme/tokens';

function open(line: CrisisLine['action']) {
  if (line.kind === 'call') {
    return openSafely(telUrl(line.number), `Please call ${line.number} from a phone.`);
  }
  return openSafely(smsUrl(line.number, line.body), `Please text ${line.body ? `${line.body} to ` : ''}${line.number} from a phone.`);
}

function LineCard({ icon, title, detail, badge, onPress, testID }: { icon: keyof typeof Ionicons.glyphMap; title: string; detail: string; badge: string; onPress: () => void; testID?: string }) {
  const t = useTheme();
  return (
    <Card onPress={onPress} testID={testID} accessibilityLabel={`${title}. ${badge}`} accessibilityHint={detail}>
      <View style={styles.row}>
        <View style={[styles.icon, { backgroundColor: t.dangerSurface }]}>
          <Ionicons name={icon} size={22} color={t.dangerStrong} />
        </View>
        <View style={{ flex: 1, gap: 2 }}>
          <AppText variant="bodyStrong">{title}</AppText>
          <AppText variant="caption" tone="secondary">
            {detail}
          </AppText>
          <AppText variant="caption" tone="danger" style={{ fontWeight: '700' }}>
            {badge}
          </AppText>
        </View>
      </View>
    </Card>
  );
}

export default function Crisis() {
  const t = useTheme();
  const contact = useAppStore((s) => s.settings.trustedContact);
  return (
    <Screen testID="crisis-screen">
      <Header title="Crisis support" />
      <Appear>
        <Card tone="danger">
          <AppText variant="heading">You don’t have to do this alone.</AppText>
          <AppText variant="caption" tone="secondary" style={{ marginTop: space.xs }}>
            Free, confidential support is one tap away, 24/7.
          </AppText>
          <AppText variant="bodyStrong" tone="danger" style={{ marginTop: space.sm }}>
            In immediate danger? Call 911 or your local emergency number now.
          </AppText>
        </Card>
      </Appear>

      <Card onPress={() => router.push('/plan')} testID="crisis-open-plan" accessibilityLabel="Open my panic plan">
        <View style={styles.row}>
          <Ionicons name="list-outline" size={22} color={t.blueStrong} />
          <View style={{ flex: 1 }}>
            <AppText variant="bodyStrong">My panic plan</AppText>
            <AppText variant="caption" tone="secondary">
              The steps you chose, one tap each.
            </AppText>
          </View>
          <Ionicons name="chevron-forward" size={20} color={t.textMuted} />
        </View>
      </Card>

      <SectionLabel>Someone you trust</SectionLabel>
      {contact ? (
        <LineCard
          testID="crisis-contact"
          icon="heart-outline"
          title={`Call ${contact.name}`}
          detail="Your trusted contact, saved only on this device."
          badge={contact.phone}
          onPress={() => openSafely(telUrl(contact.phone), `Please call ${contact.phone} from a phone.`)}
        />
      ) : (
        <Card onPress={() => router.push('/plan')} accessibilityLabel="Add an emergency contact to your panic plan">
          <View style={styles.row}>
            <Ionicons name="person-add-outline" size={22} color={t.blueStrong} />
            <AppText variant="caption" tone="secondary" style={{ flex: 1 }}>
              Add a friend or family member to your panic plan so they’re one tap away here.
            </AppText>
          </View>
        </Card>
      )}

      <SectionLabel>24/7 crisis lines (United States)</SectionLabel>
      {US_CRISIS_LINES.map((line, i) => (
        <Appear key={line.id} index={i}>
          <LineCard
            testID={`crisis-${line.id}`}
            icon={line.action.kind === 'call' ? 'call-outline' : 'chatbubble-ellipses-outline'}
            title={line.name}
            detail={line.detail}
            badge={line.badge}
            onPress={() => open(line.action)}
          />
        </Appear>
      ))}

      <SectionLabel>Outside the United States</SectionLabel>
      <LineCard
        testID="crisis-international"
        icon="globe-outline"
        title="Find a helpline in your country"
        detail="Find A Helpline lists free, confidential support lines worldwide."
        badge="findahelpline.com"
        onPress={() => openSafely('https://findahelpline.com', 'Visit findahelpline.com in a browser.')}
      />

      <View style={[styles.divider, { backgroundColor: t.hairline }]} />
      <Button variant="secondary" label="Try box breathing while you wait" icon="square-outline" onPress={() => router.replace('/breathing')} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: space.md },
  icon: { width: 44, height: 44, borderRadius: radius.pill, alignItems: 'center', justifyContent: 'center' },
  divider: { height: 1, marginVertical: space.sm },
});
