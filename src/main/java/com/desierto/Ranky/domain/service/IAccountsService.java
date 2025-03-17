package com.desierto.Ranky.domain.service;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IAccountsService {

  Ranking addAccounts(String rankingId, Guild guild, List<Account> accounts);

  Ranking removeAccounts(String rankingId, Guild guild, List<Account> accounts);
}
