package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.BotCommons;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.LoggerFactory;
import xyz.hurrhnn.discordbot.EventListener;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShutdownCommand implements ICmd{
    @Override
    public void handle(CmdContext cmdContext) {
        RestAction<ApplicationInfo> applicationInfoRestAction = cmdContext.getJDA().retrieveApplicationInfo();
        ApplicationInfo applicationInfo = applicationInfoRestAction.complete();

        for(TeamMember teamMember : Objects.requireNonNull(applicationInfo.getTeam()).getMembers())
        {
            if(teamMember.getUser().getId().equals(cmdContext.getAuthor().getId()))
            {
                LoggerFactory.getLogger(EventListener.class).info("Shutting down...");
                cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedImageWithTitle("\"Performing Shutting down...\"", null, "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Emoji_u1f44b.svg/1200px-Emoji_u1f44b.svg.png").build()).complete();

                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                SQL.finSQLConnection(Main.con);
                cmdContext.getJDA().shutdown();
                BotCommons.shutdown(cmdContext.getJDA());
                System.exit(0);
            }
        }
        cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Oops", "```\nYou are not authorized to execute this command!\n```").build()).queue();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!shutdown\n\n" +
                "-- Shutting down a bot.\n-- Only those who belong to the bot developers' team are allowed.\n```";
    }

    @Override
    public List<String> getAliases() { return Collections.singletonList("sh");}

    @Override
    public void errHandler(Exception e, TextChannel textChannel) { }
}
