package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class StopCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        if (!isAuthorAdministrator(cmdContext.getMember())) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Stop!", "E: You cannot reset the music queue because you are not the administrator of this server.").build()).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(cmdContext.getGuild());
        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);
        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Stop!", "Stop the music and clear the queue.").build()).queue();
    }

    public boolean isAuthorAdministrator(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("st");
    }
}
