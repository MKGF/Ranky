package com.desierto.ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.application.fixtures.AccountFixtures;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.exception.account.AccountCouldNotBeDesambiguatedException;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.valueobject.RankedMode;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  RiotAccountRepository riotAccountRepository;

  @Mock
  AccountsCache accountsCache;

  @InjectMocks
  AccountsService cut;

  @Test
  void addsAccountsToRanking() {
    Guild guild = mock(Guild.class);
    Account account = new Account("id");
    Ranking ranking = new Ranking("rankingId");
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(accountsCache.containsRanking(ranking.getId(), guild.getId())).thenReturn(false);
    cut.addAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    assertEquals(1, captor.getValue().getAccounts().size());
  }

  @Test
  void removesAccountsFromRanking() {
    Guild guild = mock(Guild.class);
    Account account = AccountFixtures.anAccount();
    Ranking ranking = new Ranking("rankingId");
    ranking.addAccount(account);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(accountsCache.containsRanking(ranking.getId(), guild.getId())).thenReturn(false);
    cut.removeAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    assertEquals(0, captor.getValue().getAccounts().size());
  }

  @Test
  void addsAccountsToRankingWhileItsCached() {
    Guild guild = mock(Guild.class);
    Account account = new Account("id");
    Ranking ranking = new Ranking("rankingId");
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(accountsCache.containsRanking(ranking.getId(), guild.getId())).thenReturn(true);
    when(riotAccountRepository.enrichWithRankedStats(account,
        RankedMode.RANKED_SOLO_5x5)).thenReturn(account);
    cut.addAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    verify(accountsCache, times(1)).addAccountsIfRankingCached(guild.getId(), ranking.getId(),
        List.of(account));
    assertEquals(1, captor.getValue().getAccounts().size());
  }

  @Test
  void removesAccountsFromRankingWhileItsCached() {
    Guild guild = mock(Guild.class);
    Account account = AccountFixtures.anAccount();
    Ranking ranking = new Ranking("rankingId");
    ranking.addAccount(account);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(accountsCache.containsRanking(ranking.getId(), guild.getId())).thenReturn(true);
    cut.removeAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    verify(accountsCache, times(1)).removeAccountsIfRankingCached(guild.getId(), ranking.getId(),
        List.of(account));
    assertEquals(0, captor.getValue().getAccounts().size());
  }

  @Test
  void removesAccountsFromRankingWithoutTag() {
    Guild guild = mock(Guild.class);
    Account account = AccountFixtures.anAccount();
    Ranking ranking = new Ranking("rankingId");
    ranking.addAccount(account);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(accountsCache.containsRanking(ranking.getId(), guild.getId())).thenReturn(false);
    cut.removeAccounts("rankingId", guild, List.of(new Account(account.getName(), "")));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    assertEquals(0, captor.getValue().getAccounts().size());
  }

  @Test
  void removingAccountsFromRankingWithoutTagAndExistingSameNameAccount_throwsDesambiguationException() {
    Guild guild = mock(Guild.class);
    Account account = AccountFixtures.anAccount();
    Account copy = AccountFixtures.getDifferentWithSameName(account);
    Ranking ranking = new Ranking("rankingId");
    ranking.addAccount(account);
    ranking.addAccount(copy);
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    when(riotAccountRepository.enrichIdentification(copy)).thenReturn(copy);
    assertThrows(AccountCouldNotBeDesambiguatedException.class,
        () -> cut.removeAccounts("rankingId", guild, List.of(new Account(account.getName(), ""))));
  }
}