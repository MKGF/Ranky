package com.desierto.ranky.infrastructure.commands;

import static java.util.Collections.emptyList;

import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Getter
@ToString
@Slf4j
public class Command {

  public static final Command HELP;
  public static final Command SOLOQ;
  public static final Command FLEXQ;
  public static final Command CREATE;
  public static final Command DELETE;
  public static final Command ADD_ACCOUNTS;
  public static final Command REMOVE_ACCOUNTS;

  public static final Command GET_GUILDS;

  public static final Command GET_ENROLLED_USERS;

  public static final Command EXISTS_CONFIG_CHANNEL;

  public static final Command RETRIEVE_CONFIG_CHANNEL_CONTENT;

  public static final Command MAKE_PUBLIC;

  public static final Command FORCE_REFRESH;


  static {
    HELP = Command.of("help",
        "Shows a detailed explanation of the possibilites of Ranky", emptyList());
    SOLOQ = Command.of(
        "soloq",
        "Shows soloQ information of the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, true)
        )
    );
    FLEXQ = Command.of(
        "flexq",
        "Shows flexQ information of the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, true)
        )
    );
    CREATE = Command.of(
        "create",
        "Creates a new ranking with the given name",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, false)
        )
    );
    DELETE = Command.of(
        "delete",
        "Deletes the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, false)
        )
    );
    ADD_ACCOUNTS = Command.of(
        "add_accounts",
        "Adds accounts (being separated by a comma (',')",
        List.of(
            new Parameter(
                "ranking_name",
                "Name of the ranking to add the accounts to",
                true,
                OptionType.STRING,
                false
            ),
            new Parameter("accounts",
                "Accounts to add to the ranking (format: summonerName#tagLine,summonerName#tagLine...)",
                true,
                OptionType.STRING,
                false
            )
        )
    );
    REMOVE_ACCOUNTS = Command.of(
        "remove_accounts",
        "Removes accounts (format: summonerName(#tagLine to desambiguate)",
        List.of(
            new Parameter(
                "ranking_name",
                "Name of the ranking to add the accounts to",
                true,
                OptionType.STRING,
                false
            ),
            new Parameter(
                "accounts",
                "Accounts to remove from the ranking (format: summonerName(#tagLine),summonerName(#tagLine)...)",
                true,
                OptionType.STRING,
                false
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
                OptionType.STRING,
                false
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
                OptionType.STRING,
                false
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
                OptionType.STRING,
                false
            )
        )
    );
    MAKE_PUBLIC = Command.of(
        "make_public",
        "Makes the ranking of public access",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, false)
        )
    );
    FORCE_REFRESH = Command.of(
        "refresh",
        "Shows *live* information of the specified ranking",
        List.of(
            new Parameter("name", "Name of the ranking", true, OptionType.STRING, true),
            new Parameter("queue_type", "Queue type (solo or flex, default: solo)", false,
                OptionType.STRING, false)
        )
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
    return List.of(
        HELP.toDiscordCommand(),
        SOLOQ.toDiscordCommand(),
        FLEXQ.toDiscordCommand(),
        CREATE.toDiscordCommand(),
        DELETE.toDiscordCommand(),
        ADD_ACCOUNTS.toDiscordCommand(),
        REMOVE_ACCOUNTS.toDiscordCommand(),
        GET_GUILDS.toDiscordCommand(),
        GET_ENROLLED_USERS.toDiscordCommand(),
        EXISTS_CONFIG_CHANNEL.toDiscordCommand(),
        RETRIEVE_CONFIG_CHANNEL_CONTENT.toDiscordCommand(),
        MAKE_PUBLIC.toDiscordCommand(),
        FORCE_REFRESH.toDiscordCommand()
    );
  }

  private SlashCommandData toDiscordCommand() {
    SlashCommandData command = Commands.slash(this.commandId, this.description);
    parameters.forEach(
        parameter -> command.addOption(parameter.optionType(), parameter.name(),
            parameter.description(),
            parameter.required(), parameter.autoComplete()));
    log.info("INTRODUCED COMMAND: " + this);
    return command;
  }
}
