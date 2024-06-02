package com.desierto.Ranky.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.Ranky.domain.entity.Account;
import com.desierto.Ranky.domain.entity.Ranking;
import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.exception.ranking.RankingNotFoundException;
import com.desierto.Ranky.infrastructure.configuration.ConfigLoader;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ConfigChannelRankingRepositoryTest {

  public static final String CONFIG_CHANNEL = "configChannel";
  public static final int RANKING_LIMIT = 5;
  @Mock
  ConfigLoader config;

  Gson gson;

  @BeforeEach
  public void setUp() {
    gson = new Gson();
    when(config.getRankingLimit()).thenReturn(RANKING_LIMIT);
    when(config.getConfigChannel()).thenReturn(CONFIG_CHANNEL);
  }

  @Test
  public void onCreateRepo_whenNoConfigChannel_throwsException() {
    Guild guild = mock(Guild.class);
    assertThrows(ConfigChannelNotFoundException.class, () -> fromGuild(guild));
  }

  @Test
  public void onCreateRepo_withConfigChannel_createsSuccessfully() {
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    fromGuild(guild);
  }

  @Test
  public void onCreateRanking_whenRankingAlreadyExists_throwsException() {
    String jsonRanking = "{\"id\":\"Test\",\"accounts\":[]}";
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(restAction.complete()).thenReturn(List.of(message));

    ConfigChannelRankingRepository cut = fromGuild(guild);

    assertThrows(RankingAlreadyExistsException.class, () -> cut.create(new Ranking("Test")));
  }

  @Test
  public void onCreateRanking_whenRankingDoesNotExist_createsSuccessfully() {
    String jsonRanking = "{\"id\":\"Test\",\"accounts\":[]}";
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    Ranking ranking = new Ranking("Test2");
    MessageCreateAction mca = mock(MessageCreateAction.class);
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(restAction.complete()).thenReturn(List.of(message));
    when(configChannel.sendMessage(gson.toJson(ranking))).thenReturn(mca);

    ConfigChannelRankingRepository cut = fromGuild(guild);

    cut.create(ranking);
  }

  @Test
  public void onUpdateRanking_updatesSuccessfully() {
    String jsonRanking = "{\"id\":\"Test\",\"accounts\":[]}";
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    Ranking ranking = new Ranking("Test", List.of(new Account("id", "name", "tagLine")));
    MessageEditAction mea = mock(MessageEditAction.class);
    String jsonUpdatedRanking = gson.toJson(ranking);
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(restAction.complete()).thenReturn(List.of(message));
    when(message.editMessage(jsonUpdatedRanking)).thenReturn(mea);

    ConfigChannelRankingRepository cut = fromGuild(guild);

    cut.update(ranking);

    verify(message.editMessage(jsonUpdatedRanking), times(1)).complete();
  }

  @Test
  public void onUpdateRanking_whenRankingDoesNotExist_throwsException() {
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Ranking ranking = new Ranking("Test", List.of(new Account("id", "name", "tagLine")));
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(restAction.complete()).thenReturn(List.of());

    ConfigChannelRankingRepository cut = fromGuild(guild);

    assertThrows(RankingNotFoundException.class, () -> cut.update(ranking));
  }

  private ConfigChannelRankingRepository fromGuild(Guild guild) {
    return new ConfigChannelRankingRepository(config, guild, gson);
  }
}
