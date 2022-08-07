package com.desierto.Ranky.application.service.dto;

import com.desierto.Ranky.domain.exception.ConfigChannelNotFoundException;
import com.desierto.Ranky.domain.exception.RankingAlreadyExistsException;
import com.desierto.Ranky.domain.valueobject.RankingConfiguration;
import com.desierto.Ranky.infrastructure.Ranky;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandDTO extends ListenerAdapter {

  public static final String CREATE_COMMAND = "/create";
  public static final String DEADLINE_COMMAND = "/setDeadline";
  public static final String ADD_ACCOUNT_COMMAND = "/addAccount";
  public static final String REMOVE_ACCOUNT_COMMAND = "/removeAccount";
  public static final String RANKING_COMMAND = "/ranking";
  public static final String PRIVATE_CONFIG_CHANNEL = "desarrollo-ranky";

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Gson gson = new Gson();
    JDA bot = event.getJDA();
    if (event.getMessage().getContentRaw().startsWith(Ranky.prefix)) {
      String command = event.getMessage().getContentRaw();
      if (command.contains(CREATE_COMMAND)) {
        TextChannel channel;
        try {
          channel = bot.getTextChannelsByName(PRIVATE_CONFIG_CHANNEL, true).stream()
              .findFirst().orElseThrow(
                  ConfigChannelNotFoundException::new);
        } catch (ConfigChannelNotFoundException e) {
          event.getChannel().sendMessage(e.getMessage()).queue();
          throw e;
        }
        String[] words = command.split(" ");
        String rankingName = "##" + words[1] + "##";
        try {
          if (channel.getHistory().retrievePast(10).complete().stream()
              .anyMatch(message -> message.getContentRaw().contains(rankingName))) {
            throw new RankingAlreadyExistsException();
          }
        } catch (RankingAlreadyExistsException e) {
          event.getChannel().sendMessage(e.getMessage()).queue();
          throw e;
        }
        String json = rankingName + "\n" + gson
            .toJson(new RankingConfiguration(rankingName));
        channel.sendTyping().queue();
        channel.sendMessage(json).queue();
      }
      if (command.contains(DEADLINE_COMMAND)) {

      }
      if (command.contains(ADD_ACCOUNT_COMMAND)) {

      }
      if (command.contains(REMOVE_ACCOUNT_COMMAND)) {

      }
      if (command.contains(RANKING_COMMAND)) {

      }


    }
  }

}
