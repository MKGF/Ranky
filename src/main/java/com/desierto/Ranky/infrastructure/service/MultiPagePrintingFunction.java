package com.desierto.Ranky.infrastructure.service;

import net.dv8tion.jda.api.events.GenericEvent;

public interface MultiPagePrintingFunction {

  void printBeginning(GenericEvent hook, String formattedRanking);

  void printGeneric(GenericEvent hook, String formattedRanking);

  void printEnding(GenericEvent hook, String formattedRanking);
}
