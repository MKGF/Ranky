package com.desierto.ranky.application.service;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import com.desierto.ranky.domain.repository.RankingRepository;
import com.desierto.ranky.domain.repository.RiotAccountRepository;
import com.desierto.ranky.domain.service.IAccountsService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountsService implements IAccountsService {

  @Autowired
  private RankingRepository rankingRepository;

  @Autowired
  private RiotAccountRepository riotAccountRepository;

  @Override
  public Ranking addAccounts(String rankingId, Guild guild, List<Account> accounts) {
    Ranking ranking = rankingRepository.read(rankingId, guild);
    accounts.stream().map(this::enrich).filter(account -> !account.getId().isEmpty()).forEach(
        ranking::addAccount);
    return rankingRepository.update(ranking, guild);
  }

  @Override
  public Ranking removeAccounts(String rankingId, Guild guild, List<Account> accounts) {
    Ranking ranking = rankingRepository.read(rankingId, guild);
    accounts.stream().map(this::enrich).forEach(ranking::removeAccount);
    return rankingRepository.update(ranking, guild);
  }

  private Account enrich(Account account) {
    log.debug("INTO ENRICHMENT WITH ACCOUNT: " + account.getNameAndTagLine());
    return riotAccountRepository.enrichIdentification(account);
  }
}
