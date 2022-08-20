package com.desierto.Ranky.application.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@AllArgsConstructor
public class RankyGuildJoinListener extends ListenerAdapter {

  public static final Logger log = Logger.getLogger("RankyGuildJoinListener.class");

  public static final String PRIVATE_CONFIG_CHANNEL = "config-channel";
  public static final String RANKY_USER_ROLE = "Ranky user";
  public static final int RANKING_LIMIT = 100;


  @Override
  public void onGuildJoin(GuildJoinEvent event) {

    Guild guild = event.getGuild();
    List<Member> loadedMembers = guild.getMembers();
    Optional<Member> owner = loadedMembers.stream()
        .filter(Member::isOwner).findFirst();

    log.info("JOINED GUILD: " + event.getGuild().getName());
    if (owner.isPresent()) {
      log.info(
          "OWNER OF THE GUILD: " + owner.get().getUser().getName() + "/" + owner.get().getUser()
              .getId());
    } else {
      log.info("GUILD HAS NO OWNER.");
    }

    event.getJDA().getPresence().setActivity(
        Activity
            .playing("currently at " + event.getJDA().getGuilds().size() + " different servers."));
    
    event.getGuild().createTextChannel(PRIVATE_CONFIG_CHANNEL).clearPermissionOverrides().queue();
    event.getGuild().createRole().setName(RANKY_USER_ROLE).queue();
    String welcomeMessage =
        "Hello to you that invited me, people of " + event.getGuild().getName() + "!. \n"
            +
            "I'm Ranky and I'm here to help you creating your own customized soloQ rankings. \n"
            +
            "I will explain my functioning right now, but you can get this information anytime again as long as you type in the /helpRanky command.\n"
            +
            "This Discord bot will use one of your channels as storage for different soloQ rankings made in the server.\n\n"
            +
            "The complete use of my commands is only given to the users with a role named 'Ranky' (which I already created myself) in them. Otherwise, just '/helpRanky' and '/ranking' are open to the users.\n"
            +
            "This channel is referred to as #config-channel (I already created it as well) and only uses the last "
            + RANKING_LIMIT
            + " messages as storage. So please do not spam in it. If something escapes the threshold it won't be able to retrieve it anymore.\n\n"
            +
            "- /create \"RANKINGNAME\" creates a ranking with that name.\n"
            +
            "- /addAccount \"RANKINGNAME\" ACCOUNT adds the account to the ranking if it exists. Supports spaces in the name.\n"
            +
            "- /addMultiple \"RANKINGNAME\" ACCOUNT1,ACCOUNT2... adds all the accounts to the ranking if they exist.\n"
            +
            "- /removeAccount \"RANKINGNAME\" ACCOUNT removes the account from the ranking if it exists.\n"
            +
            "- /ranking \"RANKINGNAME\" gives the soloQ information of the accounts in the ranking ordered by rank.";

    BaseGuildMessageChannel textChannel = event.getGuild().getDefaultChannel();
    if (textChannel == null) {
      if (event.getGuild().getSystemChannel() != null) {
        event.getGuild().getSystemChannel().sendMessage(welcomeMessage).complete();
      } else {
        event.getGuild().getTextChannels().get(0).sendMessage(welcomeMessage).complete();
      }
    } else {
      textChannel.sendMessage(welcomeMessage).complete();
    }
    if (event.getGuild().getOwner() != null) {
      String ownerMessage = "Hello " + event.getGuild().getOwner().getUser().getName() + "!. \n"
          +
          "I'm Ranky and I was just invited to the server named " + event.getGuild().getName()
          + " where you are the owner. \n"
          +
          "First of all, I just created the necessary #config-channel for me to store all the information of the rankings and the role 'Ranky' so users can be assigned the role and interact with me.\n"
          +
          "I (hopefully) left a message in your server explaining how I can be used. But anyway I will leave the instructions here as well. You can get this information anytime again as long as you type in the /helpRanky command.\n"
          +
          "This Discord bot will use one of your channels as storage for different soloQ rankings made in the server.\n\n"
          +
          "The complete use of my commands is only given to the users with a role named 'Ranky' in them. Otherwise, just '/helpRanky' and '/ranking' are open to the users.\n"
          +
          "This channel is referred to as #config-channel and only uses the last " + RANKING_LIMIT
          + " messages as storage. So please do not spam in it. If something escapes the threshold it won't be able to retrieve it anymore.\n\n"
          +
          "- /create \"RANKINGNAME\" creates a ranking with that name.\n"
          +
          "- /addAccount \"RANKINGNAME\" ACCOUNT adds the account to the ranking if it exists. Supports spaces in the name.\n"
          +
          "- /addMultiple \"RANKINGNAME\" ACCOUNT1,ACCOUNT2... adds all the accounts to the ranking if they exist.\n"
          +
          "- /removeAccount \"RANKINGNAME\" ACCOUNT removes the account from the ranking if it exists.\n"
          +
          "- /ranking \"RANKINGNAME\" gives the soloQ information of the accounts in the ranking ordered by rank.";
      sendMessage(event.getGuild().getOwner().getUser(), ownerMessage);

    }
  }

  static void sendMessage(User user, String content) {
    log.info("ENTERED MESSAGE SENDING METHOD.");
    user.openPrivateChannel().queue(channel -> {
      channel.sendMessage(content).complete();
      log.info("SENT MESSAGE: \n" + content);
    });
  }

}

