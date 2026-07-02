package com.desierto.ranky.infrastructure.controller;

import static com.desierto.ranky.application.fixtures.GuildFixtures.aGuild;
import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_FLEX_SR;
import static com.desierto.ranky.domain.valueobject.RankedMode.RANKED_SOLO_5x5;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desierto.ranky.application.AccountsCache;
import com.desierto.ranky.application.fixtures.AccountFixtures;
import com.desierto.ranky.application.fixtures.RankingFixtures;
import com.desierto.ranky.domain.entity.Account;
import com.desierto.ranky.domain.valueobject.RankedMode;
import com.desierto.ranky.infrastructure.BaseIT;
import com.desierto.ranky.infrastructure.dto.RankingDto;
import com.desierto.ranky.infrastructure.repository.RestRiotAccountRepository;
import com.desierto.ranky.infrastructure.service.auth.SessionCache;
import com.desierto.ranky.infrastructure.service.auth.UserSession;
import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

class RankingsControllerTest extends BaseIT {

  @MockitoBean
  private JDA jda;

  @MockitoBean
  private RestRiotAccountRepository restRiotAccountRepository;

  @Autowired
  private SessionCache sessionCache;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountsCache accountsCache;

  @BeforeEach
  void clearAccountsCache() {
    accountsCache.delete("rankingid", "guildid");
  }


  @Test
  void givenNoSession_returnsForbidden() throws Exception {
    mockMvc.perform(
        get("/rankings/mutual")
    ).andExpect(status().isForbidden());
  }

  @Test
  void givenSession_accessIsGranted_butNoGuildIsFound() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJda(userId);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
        get("/rankings/mutual").cookie(cookie)
    ).andExpect(status().isNotFound());
  }

  @Test
  void givenSession_whenRequestingMutualGuilds_returnsGuilds() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJda(aGuild(), userId);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
            get("/rankings/mutual").cookie(cookie)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is("guildId")))
        .andExpect(jsonPath("$[0].name", is("guildName")))
        .andExpect(jsonPath("$[0].iconUrl", is("guildIconUrl")));
  }

  @Test
  void givenSession_whenRequestingRankings_returnsRankings() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    Guild guild = aGuild();
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJdaForGetRankingsCall(guild, userId);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    mockMvc.perform(
            get(String.format("/rankings/fromGuild/%s", guild.getId())).cookie(cookie)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name", is("rankingId")))
        .andExpect(jsonPath("$[0].amountOfAccounts", is(1)));
  }

  @Test
  void givenSession_whenRequestingSpecificSoloQRanking_returnsRanking() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    Guild guild = aGuild();
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJdaForGetRankingsCall(guild, userId);
    mockRiot(RANKED_SOLO_5x5);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    Account expected = AccountFixtures.anAccount();
    mockMvc.perform(
            get(String.format("/rankings/fromGuild/%s/soloQ/%s", guild.getId(),
                "rankingId")).cookie(cookie)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("id", is("rankingId")))
        .andExpect(jsonPath("isPublic", is(false)))
        .andExpect(jsonPath("accounts[0].id", is(expected.getId())))
        .andExpect(jsonPath("accounts[0].name", is(expected.getName())))
        .andExpect(jsonPath("accounts[0].tagLine", is(expected.getTagLine())))
        .andExpect(jsonPath("accounts[0].rank.tier", is(expected.getRank().getTier().name())))
        .andExpect(
            jsonPath("accounts[0].rank.division",
                is(expected.getRank().getDivision().name() + " ")))
        .andExpect(
            jsonPath("accounts[0].rank.leaguePoints", is(expected.getRank().getLeaguePoints())))
        .andExpect(jsonPath("accounts[0].rank.winrate.wins",
            is(expected.getRank().getWinrate().getWins())))
        .andExpect(jsonPath("accounts[0].rank.winrate.losses",
            is(expected.getRank().getWinrate().getLosses())));
  }

  @Test
  void givenSession_whenRequestingSpecificFlexQRanking_returnsRanking() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    Guild guild = aGuild();
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJdaForGetRankingsCall(guild, userId);
    mockRiot(RANKED_FLEX_SR);
    Cookie cookie = new Cookie("SESSION_ID", sessionId);
    Account expected = AccountFixtures.anAccount();
    mockMvc.perform(
            get(String.format("/rankings/fromGuild/%s/flexQ/%s", guild.getId(),
                "rankingId")).cookie(cookie)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("id", is("rankingId")))
        .andExpect(jsonPath("isPublic", is(false)))
        .andExpect(jsonPath("accounts[0].id", is(expected.getId())))
        .andExpect(jsonPath("accounts[0].name", is(expected.getName())))
        .andExpect(jsonPath("accounts[0].tagLine", is(expected.getTagLine())))
        .andExpect(jsonPath("accounts[0].rank.tier", is(expected.getRank().getTier().name())))
        .andExpect(
            jsonPath("accounts[0].rank.division",
                is(expected.getRank().getDivision().name() + " ")))
        .andExpect(
            jsonPath("accounts[0].rank.leaguePoints", is(expected.getRank().getLeaguePoints())))
        .andExpect(jsonPath("accounts[0].rank.winrate.wins",
            is(expected.getRank().getWinrate().getWins())))
        .andExpect(jsonPath("accounts[0].rank.winrate.losses",
            is(expected.getRank().getWinrate().getLosses())));
  }

  @Test
  void givenSession_whenAddingAccountsWithJsonBody_returnsUpdatedRanking() throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    Guild guild = mock(Guild.class);
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJdaForRankingMutationCall(guild, userId, RankingFixtures.aRanking());
    when(restRiotAccountRepository.enrichIdentification(any(Account.class))).thenAnswer(
        invocation -> {
          Account account = invocation.getArgument(0);
          return new Account("newId", account.getName(), account.getTagLine());
        });
    Cookie cookie = new Cookie("SESSION_ID", sessionId);

    mockMvc.perform(post(String.format("/rankings/forGuild/%s/forRanking/%s/add", guild.getId(),
            "rankingId")).cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"name":"summoner","tagLine":"EUW"}]
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id", is("rankingId")))
        .andExpect(jsonPath("accounts", hasSize(2)))
        .andExpect(jsonPath("accounts[1].id", is("newId")))
        .andExpect(jsonPath("accounts[1].name", is("summoner")))
        .andExpect(jsonPath("accounts[1].tagLine", is("EUW")));
  }

  @Test
  void givenSession_whenRemovingAccountsWithLegacyTagBody_returnsUpdatedRanking()
      throws Exception {
    String userId = "userId";
    String sessionId = sessionCache.generate();
    Guild guild = mock(Guild.class);
    sessionCache.store(sessionId, new UserSession("token", "username", userId, "iconUrl"));
    mockJdaForRankingMutationCall(guild, userId, RankingFixtures.aRanking());
    when(restRiotAccountRepository.enrichIdentification(any(Account.class))).thenAnswer(
        invocation -> {
          Account account = invocation.getArgument(0);
          if (!account.lacksId()) {
            return AccountFixtures.anAccount();
          }
          return new Account("id", account.getName(), account.getTagLine());
        });
    Cookie cookie = new Cookie("SESSION_ID", sessionId);

    mockMvc.perform(post(String.format("/rankings/forGuild/%s/forRanking/%s/remove",
            guild.getId(), "rankingId")).cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"name":"name","tag":"tagLine"}]
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id", is("rankingId")))
        .andExpect(jsonPath("accounts", hasSize(0)));
  }

  private void mockJda(Guild guild, String userId) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of(guild));
    when(jda.getGuilds()).thenReturn(List.of(guild));
  }

  private void mockJda(String userId) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of());
    when(jda.getGuilds()).thenReturn(List.of());
  }

  private void mockJdaForGetRankingsCall(Guild guild, String userId) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of(guild));
    when(jda.getGuilds()).thenReturn(List.of(guild));
    TextChannel textChannel = mock(TextChannel.class);
    MessageHistory messageHistory = mock(MessageHistory.class);
    RestAction<List<Message>> restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    Gson gson = new Gson();
    when(message.getContentRaw()).thenReturn(
        gson.toJson(RankingDto.fromDomain(RankingFixtures.aRanking())));
    when(restAction.complete()).thenReturn(List.of(message));
    when(messageHistory.retrievePast(anyInt())).thenReturn(restAction);
    when(textChannel.getHistory()).thenReturn(messageHistory);
    when(textChannel.getName()).thenReturn("config-channel");
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
  }

  private void mockJdaForRankingMutationCall(Guild guild, String userId,
      com.desierto.ranky.domain.entity.Ranking ranking) {
    User user = mock(User.class);
    CacheRestAction<User> craUser = mock(CacheRestAction.class);
    CacheRestAction<Member> craMember = mock(CacheRestAction.class);
    Member member = mock(Member.class);
    Role role = mock(Role.class);
    TextChannel textChannel = mock(TextChannel.class);
    MessageHistory messageHistory = mock(MessageHistory.class);
    RestAction<List<Message>> restAction = mock(RestAction.class);
    Message message = mock(Message.class);
    MessageCreateAction messageCreateAction = mock(MessageCreateAction.class);
    AuditableRestAction<Void> deleteAction = mock(AuditableRestAction.class);
    Gson gson = new Gson();
    when(guild.getId()).thenReturn("guildId");
    when(jda.retrieveUserById(userId)).thenReturn(craUser);
    when(craUser.complete()).thenReturn(user);
    when(jda.getMutualGuilds(user)).thenReturn(List.of(guild));
    when(jda.getGuilds()).thenReturn(List.of(guild));
    when(guild.retrieveMemberById(userId)).thenReturn(craMember);
    when(craMember.complete()).thenReturn(member);
    when(member.getRoles()).thenReturn(List.of(role));
    when(role.getName()).thenReturn("Ranky user");
    when(message.getContentRaw()).thenReturn(gson.toJson(RankingDto.fromDomain(ranking)));
    when(restAction.complete()).thenReturn(List.of(message));
    when(messageHistory.retrievePast(anyInt())).thenReturn(restAction);
    when(textChannel.getHistory()).thenReturn(messageHistory);
    when(textChannel.getName()).thenReturn("config-channel");
    when(textChannel.sendMessage(org.mockito.ArgumentMatchers.anyString())).thenReturn(
        messageCreateAction);
    when(message.delete()).thenReturn(deleteAction);
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
  }

  private void mockRiot(RankedMode rankedMode) {
    when(restRiotAccountRepository.enrichAccountsWithRankedStats(anyList(),
        eq(rankedMode))).thenReturn(List.of(AccountFixtures.anAccount()));
  }

}
