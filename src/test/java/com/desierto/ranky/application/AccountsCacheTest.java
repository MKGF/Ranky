package com.desierto.ranky.application;

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

  private Map<String, List<Account>> rankings;

  private Map<String, LocalDateTime> introductionTimes;

  @BeforeEach
  public void setup() {
    rankings = new HashMap<>();
    introductionTimes = new HashMap<>();
    cut = new AccountsCache(rankings, introductionTimes);
  }

  @Test
  void whenSave_introducesListOfAccountsAndTime() {
    assertEquals(0, rankings.size());
    assertEquals(0, introductionTimes.size());
    cut.save("guildId", "test", List.of());
    assertEquals(1, rankings.size());
    assertEquals(1, introductionTimes.size());
  }

  @Test
  void whenFind_returnsList() {
    cut.save("guildId", "test", List.of());
    assertTrue(cut.find("guildId", "test").isPresent());
  }

  @Test
  void whenFind_ifNoValueWasFound_returnsEmpty() {
    assertTrue(cut.find("guildId", "test").isEmpty());
  }

  @Test
  void whenClearingCache_ifTimeIsBelowThreshold_doesNotDelete() {
    rankings.put("test", List.of());
    introductionTimes.put("test", LocalDateTime.now());
    assertEquals(1, rankings.size());
    assertEquals(1, introductionTimes.size());
    cut.clearCache();
    assertEquals(1, rankings.size());
    assertEquals(1, introductionTimes.size());
  }

  @Test
  void whenClearingCache_ifTimeIsAboveThreshold_deletesListOfAccountsAndTime() {
    rankings.put("test", List.of());
    introductionTimes.put("test", LocalDateTime.now().minusMinutes(20L));
    assertEquals(1, rankings.size());
    assertEquals(1, introductionTimes.size());
    cut.clearCache();
    assertEquals(0, rankings.size());
    assertEquals(0, introductionTimes.size());
  }

  @Test
  void checksPressenceOfRanking() {
    cut.save("guildId", "test",
        List.of(AccountFixtures.anAccount(), AccountFixtures.anotherAccount()));
    assertTrue(cut.containsRanking("test", "guildId"));
    assertFalse(cut.containsRanking("anotherTest", "anotherGuildId"));
  }

  @Test
  void whenRankingPresentInCache_ifAddAccount_thenAddsAccountToCache() {
    Account present = AccountFixtures.anAccount();
    Account added = AccountFixtures.anotherAccount();
    cut.save("guildId", "test",
        List.of(present));
    cut.addAccountsIfRankingCached("guildId", "test", List.of(added));
    List<Account> result = cut.find("guildId", "test").get();
    assertTrue(result.containsAll(List.of(present, added)));
    assertEquals(2, result.size());
  }

  @Test
  void whenRankingPresentInCache_ifRemoveAccount_thenRemovesAccountFromCache() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove);
    cut.save("guildId", "test",
        present);
    cut.removeAccountsIfRankingCached("guildId", "test", List.of(toRemove));
    List<Account> result = cut.find("guildId", "test").get();
    assertFalse(result.contains(toRemove));
    assertEquals(1, result.size());
  }

  @Test
  void whenRemovingAccountWithoutTag_removesSuccessfully() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove);
    cut.save("guildId", "test",
        present);
    cut.removeAccountsIfRankingCached("guildId", "test",
        List.of(new Account(toRemove.getName(), "")));
    List<Account> result = cut.find("guildId", "test").get();
    assertFalse(result.contains(toRemove));
    assertEquals(1, result.size());
  }

  @Test
  void whenRemovingAccountWithAnExistingSameNameAccountWithoutTag_throwsDesambiguationException() {
    Account toRemove = AccountFixtures.anotherAccount();
    List<Account> present = List.of(AccountFixtures.anAccount(), toRemove,
        AccountFixtures.getDifferentWithSameName(toRemove));
    cut.save("guildId", "test",
        present);
    assertThrows(AccountCouldNotBeDesambiguatedException.class,
        () -> cut.removeAccountsIfRankingCached("guildId", "test",
            List.of(new Account(toRemove.getName(), ""))));
  }
}
