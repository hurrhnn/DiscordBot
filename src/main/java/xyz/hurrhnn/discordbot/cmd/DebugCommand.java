package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Objects;

public class DebugCommand implements ICmd{
    @Override
    public void handle(CmdContext cmdContext) {

        RestAction<ApplicationInfo> applicationInfoRestAction = cmdContext.getJDA().retrieveApplicationInfo();
        ApplicationInfo applicationInfo = applicationInfoRestAction.complete();
        for(TeamMember teamMember : Objects.requireNonNull(applicationInfo.getTeam()).getMembers())
        {
            if(teamMember.getUser().getId().equals(cmdContext.getAuthor().getId()))
            {
                cmdContext.getChannel().sendMessage("Guild ID: " + cmdContext.getGuild().getId()).queue();
                return;
            }
        }
        cmdContext.getChannel().sendMessage("You aren't my Team!").queue();
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getHelp() {
        return "부엉이 바위쪽으로 가자";
    }
}
