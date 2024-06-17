package com.desierto.Ranky.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.desierto.Ranky.domain.entity.Account;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AccountsCacheTest {

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
  public void whenSave_introducesListOfAccountsAndTime() {
    assertEquals(0, rankings.size());
    assertEquals(0, introductionTimes.size());
    cut.save("test", List.of());
    assertEquals(1, rankings.size());
    assertEquals(1, introductionTimes.size());
  }

  @Test
  public void whenFind_returnsList() {
    cut.save("test", List.of());
    assertTrue(cut.find("test").isPresent());
  }

  @Test
  public void whenFind_ifNoValueWasFound_returnsEmpty() {
    assertTrue(cut.find("test").isEmpty());
  }

  @Test
  public void whenClearingCache_ifTimeIsBelowThreshold_doesNotDelete() {
    rankings.put("test", List.of());
    introductionTimes.put("test", LocalDateTime.now());
    assertEquals(rankings.size(), 1);
    assertEquals(introductionTimes.size(), 1);
    cut.clearCache();
    assertEquals(rankings.size(), 1);
    assertEquals(introductionTimes.size(), 1);
  }

  @Test
  public void whenClearingCache_ifTimeIsAboveThreshold_deletesListOfAccountsAndTime() {
    rankings.put("test", List.of());
    introductionTimes.put("test", LocalDateTime.now().minusMinutes(20L));
    assertEquals(rankings.size(), 1);
    assertEquals(introductionTimes.size(), 1);
    cut.clearCache();
    assertEquals(rankings.size(), 0);
    assertEquals(introductionTimes.size(), 0);
  }
}
