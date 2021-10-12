package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class PlayCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();

        if (isArgsEmpty(cmdContext.getArgs())) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Play", getHelp()).build()).queue();
            return;
        }

        if (!isVoiceChannelConnected(cmdContext.getGuild().getAudioManager())) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Play", "```E: The bot is NOT connected to the voice channel.```").build()).queue();
            return;
        }

        VoiceChannel voiceChannel = cmdContext.getGuild().getAudioManager().getConnectedChannel();
        if (!isAuthorVoiceChannelConnectedWithBot(voiceChannel, cmdContext.getMember())) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Play", "```E: You cannot play the music because you are not connected to the voice channel with bot.```").build()).queue();
            return;
        }
        new PlayCommander(cmdContext.getArgs(), cmdContext.getEvent(), cmdContext.getArgs().get(0).contains("mp3/") ? cmdContext.getArgs().get(0).replace("mp3/", "") : null);
    }

    public boolean isVoiceChannelConnected(AudioManager audioManager) {
        return audioManager.isConnected();
    }

    public boolean isAuthorVoiceChannelConnectedWithBot(VoiceChannel voiceChannel, Member member) {
        return !(voiceChannel != null && !voiceChannel.getMembers().contains(member));
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music play [music]\n" +
                "-- [music] can be youtube video, playlist link, music titles to search for on youtube, and direct audio link.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("p");
    }
}