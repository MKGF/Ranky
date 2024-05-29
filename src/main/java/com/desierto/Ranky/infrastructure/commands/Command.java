package com.desierto.Ranky.infrastructure.commands;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Getter
@ToString
public class Command {

  public static final Logger log = Logger.getLogger("Command.class");
  public static final Command HELP_RANKY;
  public static final Command RANKING;
  public static final Command CREATE;
  public static final Command DELETE;
  public static final Command ADD_ACCOUNTS;
  public static final Command REMOVE_ACCOUNTS;

  static {
    HELP_RANKY = Command.of("help_ranky",
        "Shows a detailed explanation of the possibilites of Ranky", emptyList());
    RANKING = Command.of("ranking", "Shows information of the specified ranking", emptyList());
    CREATE = Command.of(
        "create",
        "Creates a new ranking with the given name",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING)
        )
    );
    DELETE = Command.of(
        "delete",
        "Deletes the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING)
        )
    );
    ADD_ACCOUNTS = Command.of(
        "add_accounts",
        "Adds the given accounts (format: summonerName#tagLine)",
        emptyList()
    );
    REMOVE_ACCOUNTS = Command.of(
        "remove_accounts",
        "Removes the given accounts (format: summonerName#tagLine)",
        emptyList()
    );
  }

  String commandId;
  String description;

  List<Parameter> parameters;

  public Command(String commandId, String description, List<Parameter> parameters) {
    this.commandId = commandId;
    this.description = description;
    this.parameters = parameters;
  }

  public static Command of(String commandId, String description, List<Parameter> parameters) {
    return new Command(commandId, description, parameters);
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
    SlashCommandData command = Commands.slash(this.commandId, this.description);
    parameters.forEach(
        parameter -> command.addOption(parameter.optionType(), parameter.name(),
            parameter.description(),
            parameter.required()));
    log.info("INTRODUCED COMMAND: " + this);
    return command;
  }
}
