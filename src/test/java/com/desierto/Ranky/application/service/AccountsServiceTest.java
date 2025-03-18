package com.desierto.Ranky.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.repository.RankingRepository;
import com.desierto.Ranky.domain.repository.RiotAccountRepository;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AccountsServiceTest {

  @Mock
  RankingRepository rankingRepository;

  @Mock
  RiotAccountRepository riotAccountRepository;

  AccountsService cut;

  @BeforeEach
  public void setUp() {
    cut = new AccountsService(rankingRepository, riotAccountRepository);
  }

  @Test
  public void addsAccountsToRanking() {
    Guild guild = mock(Guild.class);
    Account account = new Account("id");
    Ranking ranking = new Ranking("rankingId");
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    cut.addAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    assertEquals(1, captor.getValue().getAccounts().size());
  }

  @Test
  public void removesAccountsFromRanking() {
    Guild guild = mock(Guild.class);
    Account account = new Account("id");
    Ranking ranking = new Ranking("rankingId");
    ranking.addAccount(account);
    when(guild.getId()).thenReturn("guildId");
    when(rankingRepository.read("rankingId", guild)).thenReturn(ranking);
    when(riotAccountRepository.enrichIdentification(account)).thenReturn(account);
    cut.removeAccounts("rankingId", guild, List.of(account));
    ArgumentCaptor<Ranking> captor = ArgumentCaptor.forClass(Ranking.class);
    verify(rankingRepository, times(1)).read("rankingId", guild);
    verify(rankingRepository, times(1)).update(captor.capture(), eq(guild));
    verify(riotAccountRepository, times(1)).enrichIdentification(account);
    assertEquals(0, captor.getValue().getAccounts().size());
  }
}