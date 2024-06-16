package com.desierto.Ranky.infrastructure.service;

import static com.desierto.Ranky.domain.utils.FileReader.read;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
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
public class WelcomeGuildServiceTest {

  public static final String PATH_TO_MESSAGE_TO_GUILD_TXT = "src/main/resources/config/onGuildJoinMessageToGuild.txt";
  private static final String EMBED_MESSAGE = "embedMessage";
  private static final String NON_RIOT_ENDORSEMENT_MESSAGE = "nonRiotEndorsementMessage";
  private static final EmbedBuilder EMBED_MESSAGE_BUILDER = new EmbedBuilder().setDescription(
      EMBED_MESSAGE);
  WelcomeGuildService cut;
  @Mock
  ConfigLoader config;

  @BeforeAll
  public void setUp() {
    cut = new WelcomeGuildService(config);
    when(config.getPathToGuildPresentationMessage()).thenReturn(PATH_TO_MESSAGE_TO_GUILD_TXT);
  }

  @Test
  public void onExecute_withFullyQualifiedGuildForWelcomeMessage_sendsMessageInSystemChannel() {
    Guild guild = buildGuildWithAllRelevantChannelsForWelcomeMessage();
    String welcomeMessage = String.format(read(
            PATH_TO_MESSAGE_TO_GUILD_TXT),
        guild.getName());
    TextChannel channel = guild.getSystemChannel();
    MessageCreateAction mca = Mockito.mock(MessageCreateAction.class);

    when(channel.sendMessage(welcomeMessage)).thenReturn(mca);
    when(channel.sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build())).thenReturn(mca);
    when(channel.sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE)).thenReturn(mca);

    cut.execute(guild, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);

    verify(channel, times(1)).sendMessage(welcomeMessage);
    verify(channel, times(1)).sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build());
    verify(channel, times(1)).sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE);
  }

  @Test
  public void onExecute_withGuildWithoutSystemChannelForWelcomeMessage_sendsMessageInDefaultChannel() {
    Guild guild = buildGuildWithoutSystemChannel();
    String welcomeMessage = String.format(read(
            PATH_TO_MESSAGE_TO_GUILD_TXT),
        guild.getName());
    TextChannel channel = guild.getDefaultChannel().asTextChannel();
    MessageCreateAction mca = Mockito.mock(MessageCreateAction.class);

    when(channel.sendMessage(welcomeMessage)).thenReturn(mca);
    when(channel.sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build())).thenReturn(mca);
    when(channel.sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE)).thenReturn(mca);

    cut.execute(guild, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);

    verify(channel, times(1)).sendMessage(welcomeMessage);
    verify(channel, times(1)).sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build());
    verify(channel, times(1)).sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE);
  }

  @Test
  public void onExecute_withGuildWithoutSystemNorDefaultChannelsForWelcomeMessage_sendsMessageInFirstMessage() {
    Guild guild = buildGuildWithoutSystemChannelAndDefaultChannel();
    String welcomeMessage = String.format(read(
            PATH_TO_MESSAGE_TO_GUILD_TXT),
        guild.getName());
    TextChannel channel = guild.getTextChannels().get(0);
    MessageCreateAction mca = Mockito.mock(MessageCreateAction.class);

    when(channel.sendMessage(welcomeMessage)).thenReturn(mca);
    when(channel.sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build())).thenReturn(mca);
    when(channel.sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE)).thenReturn(mca);

    cut.execute(guild, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);

    verify(channel, times(1)).sendMessage(welcomeMessage);
    verify(channel, times(1)).sendMessageEmbeds(EMBED_MESSAGE_BUILDER.build());
    verify(channel, times(1)).sendMessage(NON_RIOT_ENDORSEMENT_MESSAGE);
  }

  @Test
  public void onExecute_withGuildWithoutChannelsForWelcomeMessage_doesNothing() {
    Guild guild = Mockito.mock(Guild.class);

    cut.execute(guild, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);
  }


  private Guild buildGuildWithAllRelevantChannelsForWelcomeMessage() {
    Guild guild = Mockito.mock(Guild.class);
    TextChannel systemChannel = Mockito.mock(TextChannel.class);
    DefaultGuildChannelUnion defaultGuildChannelUnion = Mockito.mock(
        DefaultGuildChannelUnion.class);
    TextChannel defaultChannel = Mockito.mock(TextChannel.class);
    TextChannel firstChannel = Mockito.mock(TextChannel.class);

    when(guild.getSystemChannel()).thenReturn(systemChannel);
    when(guild.getDefaultChannel()).thenReturn(defaultGuildChannelUnion);
    when(defaultGuildChannelUnion.asTextChannel()).thenReturn(defaultChannel);
    when(guild.getTextChannels()).thenReturn(List.of(firstChannel));

    return guild;
  }

  private Guild buildGuildWithoutSystemChannel() {
    Guild guild = Mockito.mock(Guild.class);
    DefaultGuildChannelUnion defaultGuildChannelUnion = Mockito.mock(
        DefaultGuildChannelUnion.class);
    TextChannel defaultChannel = Mockito.mock(TextChannel.class);
    TextChannel firstChannel = Mockito.mock(TextChannel.class);

    when(guild.getDefaultChannel()).thenReturn(defaultGuildChannelUnion);
    when(defaultGuildChannelUnion.asTextChannel()).thenReturn(defaultChannel);
    when(guild.getTextChannels()).thenReturn(List.of(firstChannel));

    return guild;
  }

  private Guild buildGuildWithoutSystemChannelAndDefaultChannel() {
    Guild guild = Mockito.mock(Guild.class);
    TextChannel firstChannel = Mockito.mock(TextChannel.class);

    when(guild.getTextChannels()).thenReturn(List.of(firstChannel));

    return guild;
  }
}
