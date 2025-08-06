package com.desierto.ranky.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuildDto {

  private String id;

  private String name;

  private String iconUrl;

  public static GuildDto fromDomain(Guild guild) {
    return new GuildDto(guild.getId(), guild.getName(), guild.getIconUrl());
  }
}
