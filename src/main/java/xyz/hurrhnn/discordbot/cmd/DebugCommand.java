package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DebugCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        RestAction<ApplicationInfo> applicationInfoRestAction = cmdContext.getJDA().retrieveApplicationInfo();
        ApplicationInfo applicationInfo = applicationInfoRestAction.complete();
        for(TeamMember teamMember : Objects.requireNonNull(applicationInfo.getTeam()).getMembers())
        {
            if(teamMember.getUser().getId().equals(cmdContext.getAuthor().getId()))
            {
                cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Debug", "The number of Threads:  " + Thread.activeCount()).build()).queue();
                return;
            }
        }
        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Oops", "You are not authorized to execute this command!").build()).queue();
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ !!debug (args)\n" +
                "-- Debugging Bot (Restricted Command)\n```";
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel)
    {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try { errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name()); } catch (UnsupportedEncodingException ignored) { }
        e.printStackTrace(errPrintStream);

        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + err.toString().split("\n")[0] + "\n```").build()).queue();
    }
}
