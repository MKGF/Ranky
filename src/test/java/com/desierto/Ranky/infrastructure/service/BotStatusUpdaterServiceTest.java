package com.desierto.Ranky.infrastructure.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.Presence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BotStatusUpdaterServiceTest {

  BotStatusUpdaterService cut;

  @Mock
  JDA bot;


  @BeforeAll
  public void setUp() {
    cut = new BotStatusUpdaterService(bot);
  }

  @Test
  public void onExecute_setsNewActivity_basedOnPresentGuilds() {
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
