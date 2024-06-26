package com.desierto.Ranky.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.desierto.Ranky.infrastructure.dto.RankingDTO;
import com.google.gson.Gson;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
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
    when(config.getAccountLimit()).thenReturn(10);
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
    when(configChannel.sendMessage(gson.toJson(RankingDTO.fromDomain(ranking)))).thenReturn(mca);

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
    MessageCreateAction mca = mock(MessageCreateAction.class);
    AuditableRestAction ara = mock(AuditableRestAction.class);
    String jsonUpdatedRanking = gson.toJson(RankingDTO.fromDomain(ranking));
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(restAction.complete()).thenReturn(List.of(message));
    when(configChannel.sendMessage(anyString())).thenReturn(mca);
    when(message.delete()).thenReturn(ara);

    ConfigChannelRankingRepository cut = fromGuild(guild);

    cut.update(ranking);

    verify(configChannel.sendMessage(jsonUpdatedRanking), times(1)).complete();
    verify(message.delete(), times(1)).complete();
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

  @Test
  public void onRead_whenRankingDoesNotExist_throwsException() {
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    String rankingId = "Test";
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(restAction.complete()).thenReturn(List.of());

    ConfigChannelRankingRepository cut = fromGuild(guild);

    assertThrows(RankingNotFoundException.class, () -> cut.read(rankingId));
  }

  @Test
  public void onRead_whenRankingExists_returnsRanking() {
    String jsonRanking = "{\"id\":\"Test\",\"accounts\":[{\"id\": \"id\"}]}";
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    Ranking expected = new Ranking("Test", List.of(new Account("id")));
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(restAction.complete()).thenReturn(List.of(message));

    ConfigChannelRankingRepository cut = fromGuild(guild);

    assertEquals(expected, cut.read(expected.getId()));
  }

  @Test
  public void onDeleteRanking_deletesSuccessfully() {
    String jsonRanking = "{\"id\":\"Test\",\"accounts\":[{\"id\": \"id\", \"name\": \"name\", \"tagLine\": \"tagLine\"}]}";
    Guild guild = mock(Guild.class);
    TextChannel configChannel = mock(TextChannel.class);
    MessageHistory history = mock(MessageHistory.class);
    RestAction restAction = mock(RestAction.class);
    Ranking ranking = new Ranking("Test", List.of(new Account("id", "name", "tagLine")));
    Message message = mock(Message.class);
    AuditableRestAction ara = mock(AuditableRestAction.class);
    when(configChannel.getName()).thenReturn(CONFIG_CHANNEL);
    when(guild.getTextChannels()).thenReturn(List.of(configChannel));
    when(configChannel.getHistory()).thenReturn(history);
    when(history.retrievePast(RANKING_LIMIT)).thenReturn(restAction);
    when(restAction.complete()).thenReturn(List.of(message));
    when(message.getContentRaw()).thenReturn(jsonRanking);
    when(message.delete()).thenReturn(ara);

    ConfigChannelRankingRepository cut = fromGuild(guild);

    assertTrue(cut.delete(ranking.getId()));
  }

  @Test
  public void onDeleteRanking_whenRankingDoesNotExist_throwsException() {
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

    assertThrows(RankingNotFoundException.class, () -> cut.delete(ranking.getId()));
  }

  private ConfigChannelRankingRepository fromGuild(Guild guild) {
    return new ConfigChannelRankingRepository(config, guild, gson);
  }
}
