package com.desierto.ranky.infrastructure.service;

import com.desierto.ranky.domain.valueobject.RankedMode;
import net.dv8tion.jda.api.events.GenericEvent;

public interface MultiPagePrintingFunction {

  void printBeginning(GenericEvent hook, String formattedRanking);

  void printGeneric(GenericEvent hook, String formattedRanking);

  void printEnding(GenericEvent hook, String formattedRanking, RankedMode rankedMode);
}
