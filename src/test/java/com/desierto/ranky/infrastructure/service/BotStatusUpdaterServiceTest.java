package com.desierto.ranky.infrastructure.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.Presence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class BotStatusUpdaterServiceTest {

  @InjectMocks
  BotStatusUpdaterService cut;

  @Mock
  JDA bot;

  @Test
  void onExecute_setsNewActivity_basedOnPresentGuilds() {
    Presence presence = Mockito.mock(Presence.class);
    when(bot.getPresence()).thenReturn(presence);
    Guild firstGuild = Mockito.mock(Guild.class);
    Guild secondGuild = Mockito.mock(Guild.class);
    when(bot.getGuilds()).thenReturn(List.of(firstGuild, secondGuild));
    Activity activity = Activity
        .customStatus(
            "Currently at " + bot.getGuilds().size() + " different servers.")
        .withState("Vibing");

    cut.execute();

    verify(presence, times(1)).setActivity(activity);
  }
}
