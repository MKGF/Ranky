package com.desierto.ranky.application.fixtures;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public class GuildFixtures {

  public static Guild aGuild() {
    Guild guild = mock(Guild.class);
    TextChannel systemChannel = mock(TextChannel.class);
    DefaultGuildChannelUnion defaultGuildChannelUnion = mock(
        DefaultGuildChannelUnion.class);
    TextChannel defaultChannel = mock(TextChannel.class);
    TextChannel firstChannel = mock(TextChannel.class);
    Member member = mock(Member.class);
    CacheRestAction<Member> craMember = mock(CacheRestAction.class);

    when(guild.getSystemChannel()).thenReturn(systemChannel);
    when(guild.getDefaultChannel()).thenReturn(defaultGuildChannelUnion);
    when(defaultGuildChannelUnion.asTextChannel()).thenReturn(defaultChannel);
    when(guild.getTextChannels()).thenReturn(List.of(firstChannel));
    when(guild.retrieveMemberById(anyString())).thenReturn(craMember);
    when(craMember.complete()).thenReturn(member);
    when(guild.getId()).thenReturn("guildId");
    when(guild.getName()).thenReturn("guildName");
    when(guild.getIconUrl()).thenReturn("guildIconUrl");

    return guild;
  }
}
