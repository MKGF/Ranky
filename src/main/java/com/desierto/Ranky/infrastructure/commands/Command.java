package com.desierto.Ranky.infrastructure.commands;

import java.util.List;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Getter
public class Command {

  public static final Command HELP_RANKY;
  public static final Command RANKING;
  public static final Command CREATE;
  public static final Command DELETE;
  public static final Command ADD_ACCOUNTS;
  public static final Command REMOVE_ACCOUNTS;

  static {
    HELP_RANKY = Command.of("help_ranky",
        "Shows a detailed explanation of the possibilites of Ranky");
    RANKING = Command.of("ranking", "Shows information of the specified ranking");
    CREATE = Command.of("create", "Creates a new ranking with the given name");
    DELETE = Command.of("delete", "Deletes the specified ranking");
    ADD_ACCOUNTS = Command.of("add_accounts",
        "Adds the given accounts (format: summonerName#tagLine)");
    REMOVE_ACCOUNTS = Command.of("remove_accounts",
        "Removes the given accounts (format: summonerName#tagLine)");
  }

  String commandId;
  String description;

  public Command(String commandId, String description) {
    this.commandId = commandId;
    this.description = description;
  }

  public static Command of(String commandId, String description) {
    return new Command(commandId, description);
  }

  public static List<SlashCommandData> getDiscordCommands() {
    return List.of(HELP_RANKY.toDiscordCommand(),
        RANKING.toDiscordCommand(),
        CREATE.toDiscordCommand(),
        DELETE.toDiscordCommand(),
        ADD_ACCOUNTS.toDiscordCommand(),
        REMOVE_ACCOUNTS.toDiscordCommand()
    );
  }

  private SlashCommandData toDiscordCommand() {
    return Commands.slash(this.commandId, this.description);
  }
}
