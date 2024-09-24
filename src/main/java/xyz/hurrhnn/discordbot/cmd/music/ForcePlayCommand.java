package xyz.hurrhnn.discordbot.cmd.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class ForcePlayCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel().asTextChannel();
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild());

        if(isArgsEmpty(cmdContext.getArgs())) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Force_play!", getHelp()).build()).queue();
            return;
        }

        if (musicManager.scheduler.player.getPlayingTrack() == null) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Force_play!", "```E: There are no songs in the queue to play.```").build()).queue();
            return;
        }

        if(musicManager.scheduler.isLoopQueue)
        {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - skip", "```E: Loop mode is set on the player.```").build()).queue();
            return;
        }

        for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
            String strArgs = String.join(" ", cmdContext.getArgs().toArray(new String[0])).toLowerCase();
            if (strArgs.equalsIgnoreCase(audioTrack.getInfo().title) || audioTrack.getInfo().title.toLowerCase().contains(strArgs)) {
                textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Force_play!", "Skip existing music and play: \n" + "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")").build()).queue();
                musicManager.scheduler.getQueue().remove(audioTrack);
                musicManager.scheduler.player.startTrack(audioTrack, false);
                return;                 
            }
        }
        textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Force_play!", "```E: No matching music found in queue.```").build()).queue();
    }

    @Override
    public String getName() {
        return "force_play";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music force_play [Part of a music name]\n" +
                "-- Skip the song you are playing and force the selected song to play.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("fp");
    }
}
