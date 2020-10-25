package xyz.hurrhnn.discordbot.cmd.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class VolumeCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(cmdContext.getGuild()).scheduler.player;

        if(isArgsEmpty(cmdContext.getArgs()))
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - volume", "Current Volume: " + audioPlayer.getVolume()).build()).queue();

        try {
            audioPlayer.setVolume(Integer.parseInt(cmdContext.getArgs().get(0)));
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - volume", ("The volume for the music player is set to " + audioPlayer.getVolume() + ".")).build()).queue();
        }catch (NumberFormatException ignored) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - volume", "```E: Volume must be integer!").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music volume [0-1000]\n" +
                "-- Set the volume for the music player.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("v");
    }
}
