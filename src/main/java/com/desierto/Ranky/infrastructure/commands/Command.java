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
  public static final Command HELP;
  public static final Command RANKING;
  public static final Command CREATE;
  public static final Command DELETE;
  public static final Command ADD_ACCOUNTS;
  public static final Command REMOVE_ACCOUNTS;

  public static final Command GET_GUILDS;

  public static final Command GET_ENROLLED_USERS;

  public static final Command EXISTS_CONFIG_CHANNEL;

  public static final Command RETRIEVE_CONFIG_CHANNEL_CONTENT;

  public static final Command DUMMY;


  static {
    HELP = Command.of("help",
        "Shows a detailed explanation of the possibilites of Ranky", List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING)
        ));
    RANKING = Command.of(
        "ranking",
        "Shows information of the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING)
        )
    );
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
        "Adds the given accounts (being separated by a comma (',')",
        List.of(
            new Parameter(
                "ranking_name",
                "Name of the ranking to add the accounts to",
                true,
                OptionType.STRING
            ),
            new Parameter("accounts",
                "Accounts to add to the ranking (format: summonerName#tagLine,summonerName#tagLine...)",
                true,
                OptionType.STRING
            )
        )
    );
    REMOVE_ACCOUNTS = Command.of(
        "remove_accounts",
        "Removes the given accounts (format: summonerName#tagLine)",
        List.of(
            new Parameter(
                "ranking_name",
                "Name of the ranking to add the accounts to",
                true,
                OptionType.STRING
            ),
            new Parameter(
                "accounts",
                "Accounts to remove from the ranking (format: summonerName#tagLine,summonerName#tagLine...)",
                true,
                OptionType.STRING
            )
        )
    );
    GET_GUILDS = Command.of("get_guilds",
        "Retrieves all the guild names in which Ranky is present",
        emptyList()
    );
    GET_ENROLLED_USERS = Command.of("get_enrolled_users",
        "Retrieves all users with the power role in a guild",
        List.of(
            new Parameter(
                "guild",
                "Guild name",
                true,
                OptionType.STRING
            )
        )
    );
    EXISTS_CONFIG_CHANNEL = Command.of("exists_config_channel",
        "Checks the existence of a config channel in a guild",
        List.of(
            new Parameter(
                "guild",
                "Guild name",
                true,
                OptionType.STRING
            )
        )
    );
    RETRIEVE_CONFIG_CHANNEL_CONTENT = Command.of("retrieve_config_channel_content",
        "Retrieves the content of the config channel of the guild",
        List.of(
            new Parameter(
                "guild",
                "Guild name",
                true,
                OptionType.STRING
            )
        )
    );
    DUMMY = Command.of(
        "dummy",
        "Dummy description",
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
    return List.of(HELP.toDiscordCommand(),
        RANKING.toDiscordCommand(),
        CREATE.toDiscordCommand(),
        DELETE.toDiscordCommand(),
        ADD_ACCOUNTS.toDiscordCommand(),
        REMOVE_ACCOUNTS.toDiscordCommand(),
        GET_GUILDS.toDiscordCommand(),
        GET_ENROLLED_USERS.toDiscordCommand(),
        EXISTS_CONFIG_CHANNEL.toDiscordCommand(),
        RETRIEVE_CONFIG_CHANNEL_CONTENT.toDiscordCommand()
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
