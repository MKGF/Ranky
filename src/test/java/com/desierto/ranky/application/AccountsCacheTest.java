package com.desierto.ranky.application;

import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.desierto.ranky.application.fixtures.AccountFixtures;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.exception.account.AccountCouldNotBeDesambiguatedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AccountsCacheTest {

  AccountsCache cut;

  private Map<String, List<Account>> rankingsSoloQ;

  private Map<String, LocalDateTime> introductionTimesSoloQ;

  private Map<String, List<Account>> rankingsFlexQ;

  private Map<String, LocalDateTime> introductionTimesFlexQ;

  @BeforeEach
  void setup() {
    rankingsSoloQ = new HashMap<>();
    introductionTimesSoloQ = new HashMap<>();
    rankingsFlexQ = new HashMap<>();
    introductionTimesFlexQ = new HashMap<>();
    cut = new AccountsCache(rankingsSoloQ, introductionTimesSoloQ, rankingsFlexQ,
        introductionTimesFlexQ);
  }

  @Test
  void whenSave_introducesListOfAccountsAndTime() {
    assertEquals(0, rankingsSoloQ.size());
    assertEquals(0, introductionTimesSoloQ.size());
    cut.save("guildId", "test", List.of(), RANKED_SOLO_5x5);
    assertEquals(1, rankingsSoloQ.size());
    assertEquals(1, introductionTimesSoloQ.size());
  }

  @Test
  void whenFind_returnsList() {
    cut.save("guildId", "test", List.of(), RANKED_SOLO_5x5);
    assertTrue(cut.find("guildId", "test", RANKED_SOLO_5x5).isPresent());
  }

  @Test
  void whenFind_ifNoValueWasFound_returnsEmpty() {
    assertTrue(cut.find("guildId", "test", RANKED_SOLO_5x5).isEmpty());
  }

  @Test
  void whenClearingCache_ifTimeIsBelowThreshold_doesNotDelete() {
    rankingsSoloQ.put("test", List.of());
    introductionTimesSoloQ.put("test", LocalDateTime.now());
    assertEquals(1, rankingsSoloQ.size());
    assertEquals(1, introductionTimesSoloQ.size());
    cut.clearCache();
    assertEquals(1, rankingsSoloQ.size());
    assertEquals(1, introductionTimesSoloQ.size());
  }

  @Test
  void whenClearingCache_ifTimeIsAboveThreshold_deletesListOfAccountsAndTime() {
    rankingsSoloQ.put("test", List.of());
    introductionTimesSoloQ.put("test", LocalDateTime.now().minusMinutes(20L));
    assertEquals(1, rankingsSoloQ.size());
    assertEquals(1, introductionTimesSoloQ.size());
    cut.clearCache();
    assertEquals(0, rankingsSoloQ.size());
    assertEquals(0, introductionTimesSoloQ.size());
  }

  @Test
  void checksPressenceOfRanking() {
    cut.save("guildId", "test",
        List.of(AccountFixtures.anAccount(), AccountFixtures.anotherAccount()), RANKED_SOLO_5x5);
    assertTrue(cut.containsSoloQRanking("test", "guildId"));
    assertFalse(cut.containsSoloQRanking("anotherTest", "anotherGuildId"));
  }

  @Test
  void whenRankingPresentInCache_ifAddAccount_thenAddsAccountToCache() {
    Account present = AccountFixtures.anAccount();
    Account added = AccountFixtures.anotherAccount();
    cut.save("guildId", "test",
        List.of(present), RANKED_SOLO_5x5);
    cut.addAccountsIfRankingCached("guildId", "test", List.of(added), List.of());
    List<Account> result = cut.find("guildId", "test", RANKED_SOLO_5x5).get();
    assertTrue(result.containsAll(List.of(present, added)));
    assertEquals(2, result.size());
  }

  @Test
  void whenRankingPresentInCache_ifRemoveAccount_thenRemovesAccountFromCache() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove);
    cut.save("guildId", "test",
        present, RANKED_SOLO_5x5);
    cut.removeAccountsIfRankingCached("guildId", "test", List.of(toRemove));
    List<Account> result = cut.find("guildId", "test", RANKED_SOLO_5x5).get();
    assertFalse(result.contains(toRemove));
    assertEquals(1, result.size());
  }

  @Test
  void whenRemovingAccountWithoutTag_removesSuccessfully() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove);
    cut.save("guildId", "test",
        present, RANKED_SOLO_5x5);
    cut.removeAccountsIfRankingCached("guildId", "test",
        List.of(new Account(toRemove.getName(), "")));
    List<Account> result = cut.find("guildId", "test", RANKED_SOLO_5x5).get();
    assertFalse(result.contains(toRemove));
    assertEquals(1, result.size());
  }

  @Test
  void whenRemovingAccountWithAnExistingSameNameAccountWithoutTag_throwsDesambiguationException() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove,
        AccountFixtures.getDifferentWithSameName(toRemove));
    cut.save("guildId", "test",
        present, RANKED_SOLO_5x5);
    assertThrows(AccountCouldNotBeDesambiguatedException.class,
        () -> cut.removeAccountsIfRankingCached("guildId", "test",
            List.of(new Account(toRemove.getName(), ""))));
  }
}
