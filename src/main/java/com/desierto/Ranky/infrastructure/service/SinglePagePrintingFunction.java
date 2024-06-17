package com.desierto.Ranky.infrastructure.service;

import net.dv8tion.jda.api.events.GenericEvent;

public interface SinglePagePrintingFunction {

  void print(GenericEvent event, String formattedRanking);
}
