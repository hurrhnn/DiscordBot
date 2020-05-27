package xyz.hurrhnn.discordbot.cmd;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import xyz.hurrhnn.discordbot.cmd.music.GuildMusicManager;
import xyz.hurrhnn.discordbot.cmd.music.PlayCommand;
import xyz.hurrhnn.discordbot.cmd.music.PlayerManager;
import xyz.hurrhnn.discordbot.cmd.music.TrackScheduler;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {

        ArrayList<String> argsList = new ArrayList<>(cmdContext.getArgs());
        argsList.add(0, "!!music");
        String[] args = argsList.toArray(new String[0]);

        GuildMessageReceivedEvent event = cmdContext.getEvent();
        Guild guild = event.getGuild();
        TextChannel textChannel = cmdContext.getChannel();
        EmbedBuilder eb = new EmbedBuilder();
        User user = cmdContext.getAuthor();
        Member member = cmdContext.getMember();
        Member selfMember = cmdContext.getGuild().getSelfMember();
        AudioManager audioManager = cmdContext.getChannel().getGuild().getAudioManager();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer player = musicManager.player;
        TrackScheduler scheduler = musicManager.scheduler;


        if (System.getProperty("os.arch").equalsIgnoreCase("arm")) {
            eb.setTitle("죄송합니다...");
            eb.setDescription("이 시스템은 음악 기능이 지원하지 않아요...");
            eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
            textChannel.sendMessage(eb.build()).queue();
            return;
        }

        if (args[1].equalsIgnoreCase("join") || args[1].equalsIgnoreCase("j")) {

            if (audioManager.isConnected()) {
                textChannel.sendMessage("이미 음성 채팅방에 연결 되어 있습니다.").queue();
                return;
            }

            GuildVoiceState memberVoiceState = Objects.requireNonNull(member).getVoiceState();

            if (memberVoiceState != null && !memberVoiceState.inVoiceChannel()) {
                textChannel.sendMessage(user.getAsMention() + "님, " + "음성 채팅방을 먼저 연결 하세요.").queue();
                return;
            }

            VoiceChannel voiceChannel = null;
            if (memberVoiceState != null) {
                voiceChannel = memberVoiceState.getChannel();
            }

            if (voiceChannel != null && !selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                textChannel.sendMessageFormat("%s 채널에 연결할 권한이 부족해요.", voiceChannel.getName()).queue();
                return;
            }

            audioManager.openAudioConnection(voiceChannel);
            playerManager.getGuildMusicManager(cmdContext.getGuild());
            player.setVolume(100);
            textChannel.sendMessage("음성 채팅방에 연결 했습니다.").queue();

        } else if (args[1].equalsIgnoreCase("leave") || args[1].equalsIgnoreCase("l")) {

            if (!audioManager.isConnected()) {
                textChannel.sendMessage("음성 채팅방에 연결되어 있지 않습니다.").queue();
                return;
            }

            VoiceChannel voiceChannel = audioManager.getConnectedChannel();
            if (voiceChannel != null && !voiceChannel.getMembers().contains(member)) {
                textChannel.sendMessage("당신은 음성 채팅방에 연결되어 있지 않으므로, 연결을 끊을 수 없습니다.").queue();
                return;
            }
            audioManager.closeAudioConnection();
            textChannel.sendMessage("음성 채팅방을 떠납니다.").queue();
        } else if (args[1].equalsIgnoreCase("play") || args[1].equalsIgnoreCase("p")) {

            if (!audioManager.isConnected()) {
                textChannel.sendMessage("봇이 음성 채팅방에 연결되어 있지 않습니다.").queue();
                return;
            }

            new PlayCommand(args, event, audioManager, null);

        } else if (args[1].equalsIgnoreCase("force_play") || args[1].equalsIgnoreCase("fp")) {

            if (!user.getId().equals("345473282654470146")) {
                eb.setTitle("봇 관리자가 아닙니다!");
                eb.setDescription("당신은 이 봇의 관리자가 아니어서 강제 재생을 할 수 없습니다!");
                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                textChannel.sendMessage(eb.build()).queue();
                return;
            }

            if (player.getPlayingTrack() == null) {
                textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                return;
            }
            for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                if (args[2].equalsIgnoreCase(audioTrack.getInfo().title) || audioTrack.getInfo().title.toLowerCase().contains(args[2].toLowerCase())) {
                    textChannel.sendMessage("기존 음악을 스킵하고, " + audioTrack.getInfo().title + "을 바로 재생합니다!").queue();
                    musicManager.scheduler.getQueue().remove(audioTrack);
                    musicManager.player.startTrack(audioTrack, false);
                    return;
                }
            }
        } else if (args[1].equalsIgnoreCase("stop")) {
            if (!user.getId().equals("345473282654470146")) {
                eb.setTitle("봇 관리자가 아닙니다!");
                eb.setDescription("당신은 이 봇의 관리자가 아니어서 대기열을 초기화 할 수 없습니다!");
                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                textChannel.sendMessage(eb.build()).queue();
                return;
            }

            musicManager.scheduler.getQueue().clear();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);

            textChannel.sendMessage("음악을 멈추고 대기열을 비웁니다.").queue();
        } else if (args[1].equalsIgnoreCase("skip") || args[1].equalsIgnoreCase("s")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());

            if (player.getPlayingTrack() == null) {
                textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                return;
            }

            if (user.getId().equals("345473282654470146")) {
                textChannel.sendMessage("당신은 이 봇의 관리자 입니다.\n" + player.getPlayingTrack().getInfo().title + "을 투표 없이 스킵합니다!").queue();
                musicManager.scheduler.nextTrack();
                return;
            }

            eb.setTitle(player.getPlayingTrack().getInfo().title + "를 스킵할까요?");
            eb.setDescription(player.getPlayingTrack().getInfo().uri);
            MessageAction messageAction = textChannel.sendMessage(eb.build());
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

            textChannel.deleteMessageById(embedMessageID).complete();

            if (O == X) textChannel.sendMessage("의견이 분분하네요! 노래는 스킵되지 않습니다!").queue();
            else if (O > X) {
                textChannel.sendMessage(player.getPlayingTrack().getInfo().title + "을 스킵합니다!").queue();
                player.setPaused(true);
                scheduler.nextTrack();
                player.setPaused(false);
            } else textChannel.sendMessage("스킵하지 말자는 의견이 많군요! 노래를 스킵하지 않습니다!").queue();

        } else if (args[1].equalsIgnoreCase("np")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());
            player = musicManager.player;

            if (player.getPlayingTrack() == null) {
                textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                return;
            }
            AudioTrackInfo info = player.getPlayingTrack().getInfo();

            eb.setTitle(!player.isPaused() ? "재생 중: [" + info.title + "]" : "일시 정지됨: [" + info.title + "]");
            eb.setDescription(player.isPaused() ? formatTime(player.getPlayingTrack().getPosition()) + " \u23F8 " + formatTime(player.getPlayingTrack().getDuration()) : formatTime(player.getPlayingTrack().getPosition()) + " ▶ " + formatTime(player.getPlayingTrack().getDuration()));
            eb.setFooter(info.uri);
            textChannel.sendMessage(eb.build()).queue();
        } else if (args[1].equalsIgnoreCase("pause")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());
            player = musicManager.player;
            if (!player.isPaused()) {
                player.setPaused(true);
                textChannel.sendMessage("플레이어를 일시정지 합니다.").queue();
            } else textChannel.sendMessage("플레이어가 이미 일시정지 되어 있습니다.").queue();
        } else if (args[1].equalsIgnoreCase("resume")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());
            player = musicManager.player;
            if (player.isPaused()) {
                player.setPaused(false);
                textChannel.sendMessage("플레이어를 다시 재생 합니다.").queue();
            } else textChannel.sendMessage("플레이어가 이미 재생되고 있습니다.").queue();
        } else if (args[1].equalsIgnoreCase("queue") || args[1].equalsIgnoreCase("q")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());
            player = musicManager.player;

            if (args.length > 2) {
                        /*
                        if (args.length != 4) {
                            textChannel.sendMessage(getHelpString.main());
                            return;
                        }*/
                if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {

                    if (!user.getId().equals("345473282654470146")) {
                        eb.setTitle("봇 관리자가 아닙니다!");
                        eb.setDescription("당신은 이 봇의 관리자가 아니어서 대기열를 제거 할 수 없습니다!");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    if (Integer.parseInt(args[3]) == 0) {
                        eb.setTitle(args[3] + "번째 대기열은 제거할 수 없습니다!");
                        eb.setDescription("!!music skip을 사용하세요!");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    if (player.getPlayingTrack() == null) {
                        eb.setTitle("음악 플레이리스트");
                        eb.setDescription("아무 것도 재생 되어 있지 않습니다!");
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }
                    Object[] str = musicManager.scheduler.getQueue().toArray();
                    for (int i = 0; i < musicManager.scheduler.getQueue().size(); i++) {
                        AudioTrack audioTrack = (AudioTrack) str[i];
                        if (Integer.parseInt(args[3]) == (i + 1)) {
                            musicManager.scheduler.getQueue().remove(audioTrack);
                            eb.setTitle("음악 플레이리스트");
                            eb.setDescription((i + 1) + "번째 대기열을 제거했습니다!");
                            eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }
                    }
                    eb.setTitle("음악 플레이리스트");
                    eb.setDescription("대기열을 제거하지 못했어요.. 대기열이 올바른지 확인하세요!");
                    eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                if (args[2].equalsIgnoreCase("show") || args[2].equalsIgnoreCase("s")) {
                    if (player.getPlayingTrack() == null) {
                        eb.setTitle("음악 플레이리스트");
                        eb.setDescription("아무 것도 재생 되어 있지 않습니다!");
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    RestAction<Message> restAction = textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [현재 재생 중]\n");
                    Message tmpMessage = restAction.complete();
                    String tmpMessageID = tmpMessage.getId();

                    int cnt = 0;
                    StringBuilder tmp = new StringBuilder();
                    if (args.length > 3) {
                        String[] SelectStr = new String[(musicManager.scheduler.getQueue().size() / 10) + 1];
                        for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                            cnt++;
                            tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                            if (cnt % 10 == 0) {
                                SelectStr[(cnt / 10) - 1] = tmp.toString();
                                tmp = new StringBuilder();
                            }
                        }
                        SelectStr[(musicManager.scheduler.getQueue().size() / 10)] = tmp.toString();
                        try {
                            textChannel.sendMessage(SelectStr[Integer.parseInt(args[3]) - 1]).queue();
                            textChannel.sendMessage(args[3] + " / " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + " 페이지").queue();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            textChannel.deleteMessageById(tmpMessageID).complete();
                            eb.setTitle("오류! " + args[3] + "페이지는 존재하지 않습니다!");
                            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                        }
                    } else {
                        for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                            cnt++;
                            tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                            if (cnt % 10 == 0) {
                                textChannel.sendMessage(tmp.toString()).queue();
                                textChannel.sendMessage("현재 플레이어의 재생 목록은 " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + "개의 페이지가 있습니다.").queue();
                                tmp = new StringBuilder();
                                break;
                            }
                        }
                        if (tmp.toString().equals("")) return;
                        textChannel.sendMessage(tmp.toString()).queue();
                        textChannel.sendMessage("현재 플레이어의 재생 목록은 " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + "개의 페이지가 있습니다.").queue();
                    }
                }
            }
        } else if (args[1].equalsIgnoreCase("volume") | args[1].equalsIgnoreCase("v")) {
            if (args.length != 3)  return;
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getGuildMusicManager(event.getGuild());
            player = musicManager.player;
            if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 101) {
                player.setVolume(Integer.parseInt(args[2]));
                textChannel.sendMessage("플레이어의 볼륨이 " + Integer.parseInt(args[2]) + "으로 설정되었습니다.").queue();
            } else textChannel.sendMessage("볼륨이 너무 크거나 작습니다.").queue();
        }
    }

    public String formatTime(long timeInMillis) {

        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, (minutes > 59) ? minutes % 60 : minutes, seconds);
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) {

    }

    @Override
    public String getName() {
        return "music";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("m");
    }
}
