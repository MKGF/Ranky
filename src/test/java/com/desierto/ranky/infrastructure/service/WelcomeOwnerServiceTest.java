package com.desierto.ranky.infrastructure.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.desierto.ranky.infrastructure.configuration.ConfigLoader;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.junit.jupiter.api.BeforeEach;
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
public class WelcomeOwnerServiceTest {

  private static final String EMBED_MESSAGE = "embedMessage";
  private static final String NON_RIOT_ENDORSEMENT_MESSAGE = "nonRiotEndorsementMessage";
  @InjectMocks
  WelcomeOwnerService cut;
  @Mock
  ConfigLoader config;

  @BeforeEach
  void configurePathToPresentationMessage() {
    lenient().when(config.getPathToOwnerPresentationMessage()).thenReturn("");
  }

  @Test
  public void onExecute_withOwner_sendsOwnerWelcomeMessage() {
    Guild guild = Mockito.mock(Guild.class);
    Member owner = Mockito.mock(Member.class);
    User user = Mockito.mock(User.class);
    CacheRestAction cra = Mockito.mock(CacheRestAction.class);
    when(owner.getUser()).thenReturn(user);
    when(user.getName()).thenReturn("Corin");
    when(user.openPrivateChannel()).thenReturn(cra);
    doNothing().when(cra).queue(any());

    cut.execute(guild, owner, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);

    verify(user, times(1)).openPrivateChannel();
  }

  @Test
  public void onExecute_withoutOwner_doesNothing() {
    Guild guild = Mockito.mock(Guild.class);

    cut.execute(guild, null, EMBED_MESSAGE, NON_RIOT_ENDORSEMENT_MESSAGE);
  }
}
