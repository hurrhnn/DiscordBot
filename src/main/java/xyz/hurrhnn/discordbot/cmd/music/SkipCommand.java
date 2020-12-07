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
        GuildMusicManager musicManager = playerManager.getMusicManager(cmdContext.getGuild());

        if (musicManager.scheduler.player.getPlayingTrack() == null) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "```E: There are no songs in the queue to skip.```").build()).queue();
            return;
        }

        if(musicManager.scheduler.isLoopQueue)
        {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "```E: Loop mode is set on the player.```").build()).queue();
            return;
        }

        try {
            if (GuildMusicInfo.isGuildSkipRequestDelayedMap.get(textChannel.getId())) return;
        } catch (NullPointerException ignored) {
            return;
        }

        GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), true);
        playerManager = PlayerManager.getInstance();
        musicManager = playerManager.getMusicManager(cmdContext.getGuild());

        if (isAuthorAdministrator(cmdContext.getMember())) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "You are the administrator of this server.\nSkip [" + musicManager.scheduler.player.getPlayingTrack().getInfo().title + "](" + musicManager.scheduler.player.getPlayingTrack().getInfo().uri + ") without voting.").build()).queue();
            musicManager.scheduler.nextTrack();
            GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), false);
            return;
        }

        MessageAction messageAction = textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "Skip\n[" + musicManager.scheduler.player.getPlayingTrack().getInfo().title + "](" + musicManager.scheduler.player.getPlayingTrack().getInfo().uri + ")?").build());
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
        if (O == X && O + X == 0) textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "No one voted. The song will not be skipped.").build()).queue();
        else if (O == X) textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "There's a lot of course! The song will not be skipped.").build()).queue();
        else if (O > X) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "Skipped\n[" + musicManager.scheduler.player.getPlayingTrack().getInfo().title + "](" + musicManager.scheduler.player.getPlayingTrack().getInfo().uri + ") by the vote.").build()).queue();
            musicManager.scheduler.nextTrack();
            GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), false);
        } else textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - Skip", "There are many opinions that we shouldn't skip it! Cancel skipping the song.").build()).queue();
        GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), false);
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
