package xyz.hurrhnn.discordbot.cmd.music.queue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.cmd.music.GuildMusicManager;
import xyz.hurrhnn.discordbot.cmd.music.PlayerManager;

import java.util.Collections;
import java.util.List;

public class MusicQueueShowCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getMusicManager(cmdContext.getGuild());
        AudioPlayer player = musicManager.audioPlayer;

        if (player.getPlayingTrack() == null) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: There are no songs in the queue to remove.```").build()).queue();
            return;
        }

        try {
            int arg = Integer.parseInt(cmdContext.getArgs().get(0));

            int cnt = 0;
            StringBuilder tmp = new StringBuilder();
            if (!isArgsEmpty(cmdContext.getArgs())) {
                String[] selectStr = new String[(musicManager.scheduler.getQueue().size() / 10) + 1];
                for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                    cnt++;
                    tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                    if (cnt % 10 == 0) {
                        selectStr[(cnt / 10) - 1] = tmp.toString();
                        tmp = new StringBuilder();
                    }
                }
                selectStr[(musicManager.scheduler.getQueue().size() / 10)] = tmp.toString();
                try {
                    if (selectStr[Integer.parseInt(cmdContext.getArgs().get(0)) - 1].isEmpty())
                        throw new ArrayIndexOutOfBoundsException();
                    textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [Now Playing]\n").queue();
                    textChannel.sendMessage(selectStr[arg - 1]).queue();
                    textChannel.sendMessage(toString() + " / " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + " Page").queue();
                } catch (ArrayIndexOutOfBoundsException e) {
                    cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: " + arg + " Page does not exist.```").build()).queue();
                }
            } else {
                if (musicManager.scheduler.getQueue().size() <= 0) {
                    textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [Now Playing]\n").queue();
                } else {
                    for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                        cnt++;
                        tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                        if (cnt % 10 == 0) {
                            textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [Now Playing]\n").queue();
                            textChannel.sendMessage(tmp.toString()).queue();
                            textChannel.sendMessage("The current player's playlist has " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + " pages.").queue();
                            return;
                        }
                    }
                    textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [Now Playing]\n").queue();
                    textChannel.sendMessage(tmp.toString()).queue();
                }
                textChannel.sendMessage("The current player's playlist has " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + " pages.").queue();
            }
        } catch (NumberFormatException ignored) {
            cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Music - Queue", "```E: Couldn't parse the page number. Make sure the page number is correct!```").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music queue show (Page Number)\n" +
                "-- Shows 10 pending music per page.\n" +
                "-- If a page number is given, it shows the queue for that page.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("s");
    }

    @Override
    public boolean isArgsEmpty(List<String> args) {
        return args.size() == 0;
    }
}
