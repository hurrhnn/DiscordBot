package xyz.hurrhnn.discordbot.cmd.music;

import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

public class PauseCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());

        if (!musicManager.scheduler.player.isPaused()) {
            musicManager.scheduler.player.setPaused(true);
            cmdContext.getChannel().sendMessage("플레이어를 일시정지 합니다.").queue();
        } else cmdContext.getChannel().sendMessage("플레이어가 이미 일시정지 되어 있습니다.").queue();
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
