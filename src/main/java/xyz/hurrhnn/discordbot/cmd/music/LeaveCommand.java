package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;

public class LeaveCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel();

        if (!isVoiceChannelConnected(cmdContext.getGuild().getAudioManager())) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - leave!", "```E: The bot is NOT connected to the voice channel.```").build()).queue();
            return;
        }

        VoiceChannel voiceChannel = cmdContext.getGuild().getAudioManager().getConnectedChannel();
        if (isAuthorVoiceChannelConnectedWithBot(voiceChannel, cmdContext.getMember())) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - leave!", "```E: You cannot disconnect the bot's voice channel because you are not connected to the voice channel.```").build()).queue();
            return;
        }
        GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), false);

        cmdContext.getGuild().getAudioManager().closeAudioConnection();
        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music - leave!", "```Disconnected to the voice channel.```").build()).queue();
    }

    public boolean isVoiceChannelConnected(AudioManager audioManager) { return audioManager.isConnected(); }

    public boolean isAuthorVoiceChannelConnectedWithBot(VoiceChannel voiceChannel, Member member) { return voiceChannel != null && !voiceChannel.getMembers().contains(member); }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("l");
    }
}
