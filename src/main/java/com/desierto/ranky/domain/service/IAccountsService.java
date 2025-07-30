package com.desierto.ranky.domain.service;

import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IAccountsService {

  Ranking addAccounts(String rankingId, Guild guild, List<Account> accounts);

  Ranking removeAccounts(String rankingId, Guild guild, List<Account> accounts);
}
