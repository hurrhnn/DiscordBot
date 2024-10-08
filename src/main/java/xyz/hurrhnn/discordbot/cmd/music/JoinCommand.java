package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JoinCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {

        TextChannel textChannel = cmdContext.getChannel().asTextChannel();

        if (isVoiceChannelConnected(cmdContext.getGuild().getAudioManager())) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Join!", "```E: The bot was already connected to the voice channel.```").build()).queue();
            return;
        }

        GuildVoiceState memberVoiceState = Objects.requireNonNull(cmdContext.getMember()).getVoiceState();

        if (!isAuthorVoiceChannelConnected(memberVoiceState)) {
            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Join!", "Hey, " + cmdContext.getAuthor().getAsMention() + ", please connect the voice channel first.").build()).queue();
            return;
        }

        VoiceChannel voiceChannel = null;
        if (memberVoiceState != null) {
            voiceChannel = memberVoiceState.getChannel().asVoiceChannel();
        }

        if (hasBotPermissionToConnectToVoiceChannel(voiceChannel, cmdContext.getSelfMember())) {

            textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Join", "```E: The bot doesn't have permission to connect to the voice channel. Please check if you have permission to connect the voice.```").build()).queue();
            return;
        }
        GuildMusicInfo.SetIsGuildSkipRequestDelayedMap(textChannel.getId(), false);
        cmdContext.getGuild().getAudioManager().openAudioConnection(voiceChannel);

        PlayerManager.getInstance().getMusicManager(cmdContext.getGuild()).scheduler.player.setVolume(80);
        textChannel.sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Music - Join", "```Connected to the voice channel.```").build()).queue();

    }

    public boolean isVoiceChannelConnected(AudioManager audioManager) { return audioManager.isConnected(); }

    public boolean isAuthorVoiceChannelConnected(GuildVoiceState memberVoiceState) { return memberVoiceState != null && memberVoiceState.inAudioChannel(); }

    public boolean hasBotPermissionToConnectToVoiceChannel(VoiceChannel voiceChannel, Member selfMember) { return voiceChannel != null && !selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT);}

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music join\n" +
                "-- Connect the bot to the voice channel with the user who sent the command.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("j");
    }
}
