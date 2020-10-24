package xyz.hurrhnn.discordbot.cmd.music;

import com.google.common.collect.Queues;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ShuffleCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild()).scheduler;

        if(scheduler.getQueue().size() < 2)
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - shuffle", "```E: There are less than 2 pending songs.```").build()).queue();

        LinkedList<AudioTrack> audioTracks = new LinkedList<>(scheduler.getQueue());
        Collections.shuffle(audioTracks);
        scheduler.queue = Queues.newLinkedBlockingQueue(audioTracks);
        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - shuffle", "Successfully shuffle " + scheduler.queue.size() + " queued songs.").build()).queue();
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music shuffle\n" +
                "-- Mix the order of pending music.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("sh");
    }
}
