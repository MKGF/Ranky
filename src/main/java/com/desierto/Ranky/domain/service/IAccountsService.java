package com.desierto.Ranky.domain.service;

import com.desierto.Ranky.domain.entity.Account;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;

public interface IAccountsService {

  void addAccounts(String rankingId, Guild guild, List<Account> accounts);

  void removeAccounts(String rankingId, Guild guild, List<Account> accounts);
}
