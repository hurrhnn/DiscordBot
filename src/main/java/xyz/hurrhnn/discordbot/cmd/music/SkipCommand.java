package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class SkipCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(cmdContext.getGuild());

        if (musicManager.player.getPlayingTrack() == null) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "```E: There are no songs in the queue to play.```").build()).queue();
            return;
        }

        try {
            if (GuildMusicInfo.isGuildSkipRequestDelayed.get(textChannel.getId())) return;
        } catch (NullPointerException ignored) {
            return;
        }

        GuildMusicInfo.isGuildSkipRequestDelayed.put(textChannel.getId(), true);
        playerManager = PlayerManager.getInstance();
        musicManager = playerManager.getGuildMusicManager(cmdContext.getGuild());

        if (isAuthorAdministrator(cmdContext.getMember())) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip!", "You are the administrator of this server.\nSkip [" + musicManager.player.getPlayingTrack().getInfo().title + "](" + musicManager.player.getPlayingTrack().getInfo().uri + ") without voting.").build()).queue();
            musicManager.scheduler.nextTrack();
            GuildMusicInfo.isGuildSkipRequestDelayed.put(textChannel.getId(), false);
            return;
        }

        MessageAction messageAction = textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip!", "```fix\nShall we skip " + musicManager.player.getPlayingTrack().getInfo().title + "?```").build());
        Message embedMessage = messageAction.complete();
        String embedMessageID = embedMessage.getId();
        embedMessage.addReaction("U+2B55").complete();
        embedMessage.addReaction("U+274C").complete();

        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        embedMessage = textChannel.retrieveMessageById(embedMessageID).complete();
        List<MessageReaction> list = embedMessage.getReactions();
        int O = 0, X = 0;
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    O = list.get(i).getCount() - 1;
                case 1:
                    X = list.get(i).getCount() - 1;
                default:
                    break;
            }
        }
        if (O == X && O + X == 0) textChannel.sendMessage("투표한 사람이 없습니다. 노래는 스킵되지 않습니다!").queue();
        else if (O == X) textChannel.sendMessage("의견이 분분하네요! 노래는 스킵되지 않습니다!").queue();
        else if (O > X) {
            textChannel.sendMessage(musicManager.player.getPlayingTrack().getInfo().title + "을 스킵합니다!").queue();
            musicManager.player.setPaused(true);
            musicManager.scheduler.nextTrack();
            musicManager.player.setPaused(false);
        } else textChannel.sendMessage("스킵하지 말자는 의견이 많군요! 노래를 스킵하지 않습니다!").queue();
        GuildMusicInfo.isGuildSkipRequestDelayed.put(textChannel.getId(), false);
    }

    public boolean isAuthorAdministrator(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("s");
    }
}
