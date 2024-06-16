package com.desierto.Ranky.infrastructure.configuration;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ConfigLoader {

  @Value("${riot.api.key}")
  private String riotApiKey;

  @Value("${riot.base.url}")
  private String riotBaseUrl;

  @Value("${disc.api.key}")
  private String discApiKey;

  @Value("${ranking.limit}")
  private int rankingLimit;

  @Value("${ranky.user.role}")
  private String rankyUserRole;

  @Value("${config.channel}")
  private String configChannel;

  @Value("${message.listener.enabled}")
  private Boolean isMessageListenerEnabled;

  @Value("${creator}")
  private String creator;

  @Value("${sponsors}")
  private String sponsors;

  @Value("${admin.ids}")
  private List<String> adminIds;

  @Value("${account.limit}")
  private int accountLimit;

  @Value("${messages.help}")
  private String pathToHelpMessage;

  @Value("${messages.non_riot_endorsement}")
  private String pathToNonRiotEndorsementMessage;

  @Value("${messages.embed}")
  private String pathToEmbedMessage;

  @Value("${messages.to_guild}")
  private String pathToGuildPresentationMessage;

  @Value("${messages.to_owner}")
  private String pathToOwnerPresentationMessage;
}
