package com.desierto.Ranky.infrastructure.listeners;

import static com.desierto.Ranky.domain.utils.FileReader.read;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.desierto.Ranky.infrastructure.service.BotStatusUpdaterService;
import com.desierto.Ranky.infrastructure.service.WelcomeGuildService;
import com.desierto.Ranky.infrastructure.service.WelcomeOwnerService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.jetbrains.annotations.NotNull;
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
public class RankyGuildJoinListenerTest {

  RankyGuildJoinListener cut;

  @Mock
  ConfigLoader config;

  @Mock
  JDA bot;

  @Mock
  WelcomeGuildService welcomeGuildService;

  @Mock
  WelcomeOwnerService welcomeOwnerService;

  @Mock
  BotStatusUpdaterService botStatusUpdaterService;

  String welcomeEmbedMessage;

  String nonRiotEndorsementMessage;


  @BeforeAll
  public void setUp() {
    when(config.getConfigChannel()).thenReturn("configChannel");
    when(config.getRankyUserRole()).thenReturn("rankyUserRole");
    when(config.getRankingLimit()).thenReturn(100);
    welcomeEmbedMessage = String.format(read(
            "src/main/java/com/desierto/Ranky/infrastructure/commands/onGuildJoinEmbedMessage.txt"),
        config.getRankyUserRole(),
        config.getConfigChannel(),
        config.getRankingLimit());
    nonRiotEndorsementMessage = read(
        "src/main/java/com/desierto/Ranky/infrastructure/commands/nonRiotEndorsementMessage.txt");
    cut = new RankyGuildJoinListener(config, bot, welcomeGuildService, welcomeOwnerService,
        botStatusUpdaterService);
  }

  @Test
  public void onGuildJoin_updatesBotStatus() {
    GuildJoinEvent event = getGuildJoinEvent();

    cut.onGuildJoin(event);

    verify(botStatusUpdaterService, times(1)).execute();
  }

  @Test
  public void onGuildJoin_welcomesGuild() {
    GuildJoinEvent event = getGuildJoinEvent();

    cut.onGuildJoin(event);

    verify(welcomeGuildService, times(1)).execute(event.getGuild(), welcomeEmbedMessage,
        nonRiotEndorsementMessage);
  }

  @Test
  public void onGuildJoin_welcomesOwner() {
    GuildJoinEvent event = getGuildJoinEvent();

    cut.onGuildJoin(event);

    verify(welcomeOwnerService, times(1)).execute(event.getGuild(),
        event.getGuild().retrieveOwner().complete(),
        welcomeEmbedMessage,
        nonRiotEndorsementMessage);
  }

  @NotNull
  private GuildJoinEvent getGuildJoinEvent() {
    GuildJoinEvent event = Mockito.mock(GuildJoinEvent.class);
    Member member = Mockito.mock(Member.class);
    Guild guild = Mockito.mock(Guild.class);
    CacheRestAction restAction = Mockito.mock(CacheRestAction.class);
    User user = Mockito.mock(User.class);
    ChannelAction channelAction = Mockito.mock(ChannelAction.class);
    RoleAction roleAction = Mockito.mock(RoleAction.class);
    when(event.getGuild()).thenReturn(guild);
    when(guild.retrieveOwner()).thenReturn(restAction);
    when(restAction.complete()).thenReturn(member);
    when(member.getUser()).thenReturn(user);
    when(user.getName()).thenReturn("Lirex");
    when(guild.createTextChannel(anyString())).thenReturn(channelAction);
    when(channelAction.clearPermissionOverrides()).thenReturn(channelAction);
    doNothing().when(channelAction).queue();
    when(guild.createRole()).thenReturn(roleAction);
    when(roleAction.setName(anyString())).thenReturn(roleAction);
    doNothing().when(roleAction).queue();
    return event;
  }
}
