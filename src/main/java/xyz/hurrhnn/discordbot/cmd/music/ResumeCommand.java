package xyz.hurrhnn.discordbot.cmd.music;

import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

public class ResumeCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(cmdContext.getGuild());
        if (musicManager.player.isPaused()) {
            musicManager.player.setPaused(false);
            cmdContext.getChannel().sendMessage("플레이어를 다시 재생 합니다.").queue();
        } else cmdContext.getChannel().sendMessage("플레이어가 이미 재생되고 있습니다.").queue();
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
