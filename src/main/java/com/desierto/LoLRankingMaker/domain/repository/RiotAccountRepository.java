package com.desierto.LoLRankingMaker.domain.repository;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import net.rithms.riot.api.RiotApiException;

public interface RiotAccountRepository {

  AccountInformation getAccountInformation(Account account) throws RiotApiException;
}
