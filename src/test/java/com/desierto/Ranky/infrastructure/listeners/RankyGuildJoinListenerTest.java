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
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

  public static final String RANKY_USER_ROLE = "rankyUserRole";
  public static final String CONFIG_CHANNEL = "configChannel";
  public static final String PATH_TO_EMBED_MESSAGE_TXT = "src/main/java/com/desierto/Ranky/infrastructure/commands/onGuildJoinEmbedMessage.txt";
  public static final String PATH_TO_NON_RIOT_ENDORSEMENT_MESSAGE_TXT = "src/main/java/com/desierto/Ranky/infrastructure/commands/nonRiotEndorsementMessage.txt";
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
    when(config.getConfigChannel()).thenReturn(CONFIG_CHANNEL);
    when(config.getRankyUserRole()).thenReturn(RANKY_USER_ROLE);
    when(config.getRankingLimit()).thenReturn(100);
    welcomeEmbedMessage = String.format(read(
            PATH_TO_EMBED_MESSAGE_TXT),
        config.getRankyUserRole(),
        config.getConfigChannel(),
        config.getRankingLimit());
    nonRiotEndorsementMessage = read(
        PATH_TO_NON_RIOT_ENDORSEMENT_MESSAGE_TXT);
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

  @Test
  public void onGuildJoin_doesNotCreateRole_whenRoleIsAlreadyPresent() {
    GuildJoinEvent event = getGuildJoinEventWithPresentRole();

    cut.onGuildJoin(event);

    verify(event.getGuild(), times(0)).createRole();
  }

  @Test
  public void onGuildJoin_doesNotCreateConfigChannel_whenConfigChannelIsAlreadyPresent() {
    GuildJoinEvent event = getGuildJoinEventWithPresentConfigChannel();

    cut.onGuildJoin(event);

    verify(event.getGuild(), times(0)).createTextChannel(CONFIG_CHANNEL);
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

  @NotNull
  private GuildJoinEvent getGuildJoinEventWithPresentRole() {
    GuildJoinEvent event = Mockito.mock(GuildJoinEvent.class);
    Member member = Mockito.mock(Member.class);
    Guild guild = Mockito.mock(Guild.class);
    CacheRestAction restAction = Mockito.mock(CacheRestAction.class);
    User user = Mockito.mock(User.class);
    ChannelAction channelAction = Mockito.mock(ChannelAction.class);
    RoleAction roleAction = Mockito.mock(RoleAction.class);
    Role role = Mockito.mock(Role.class);
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
    when(guild.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn(RANKY_USER_ROLE);
    return event;
  }

  @NotNull
  private GuildJoinEvent getGuildJoinEventWithPresentConfigChannel() {
    GuildJoinEvent event = Mockito.mock(GuildJoinEvent.class);
    Member member = Mockito.mock(Member.class);
    Guild guild = Mockito.mock(Guild.class);
    CacheRestAction restAction = Mockito.mock(CacheRestAction.class);
    User user = Mockito.mock(User.class);
    ChannelAction channelAction = Mockito.mock(ChannelAction.class);
    RoleAction roleAction = Mockito.mock(RoleAction.class);
    TextChannel textChannel = Mockito.mock(TextChannel.class);
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
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    when(textChannel.getName()).thenReturn(CONFIG_CHANNEL);
    return event;
  }
}
