package xyz.hurrhnn.discordbot.cmd;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import xyz.hurrhnn.discordbot.cmd.music.GuildMusicManager;
import xyz.hurrhnn.discordbot.cmd.music.MusicCommandManager;
import xyz.hurrhnn.discordbot.cmd.music.PlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {

        ArrayList<String> argsList = new ArrayList<>(cmdContext.getArgs());
        argsList.add(0, "!!music");
        String[] args = argsList.toArray(new String[0]);

        GuildMessageReceivedEvent event = cmdContext.getEvent();
        TextChannel textChannel = cmdContext.getChannel();
        EmbedBuilder eb = new EmbedBuilder();
        User user = cmdContext.getAuthor();
        PlayerManager playerManager;
        GuildMusicManager musicManager;
        AudioPlayer player;

        if (args.length < 2) {
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Music", getHelp()).build()).queue();
            return;
        }

        final MusicCommandManager manager = new MusicCommandManager();
        manager.handle(cmdContext.getEvent());

        if (args[1].equalsIgnoreCase("queue") || args[1].equalsIgnoreCase("q")) {
            playerManager = PlayerManager.getInstance();
            musicManager = playerManager.getMusicManager(event.getGuild());
            player = musicManager.scheduler.player;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {

                    if (!user.getId().equals("345473282654470146")) {
                        eb.setTitle("봇 관리자가 아닙니다!");
                        eb.setDescription("당신은 이 봇의 관리자가 아니어서 대기열를 제거 할 수 없습니다!");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    if (Integer.parseInt(args[3]) == 0) {
                        eb.setTitle("Music - Error!");
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
                        if(!tmp.toString().isEmpty()) textChannel.sendMessage(tmp.toString()).queue();
                        textChannel.sendMessage("현재 플레이어의 재생 목록은 " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + "개의 페이지가 있습니다.").queue();
                    }
                }
            }
        }
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
