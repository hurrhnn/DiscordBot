package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;

import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class MP3Command implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        String[] args = cmdContext.getArgs().toArray(new String[0]);
        Message message = cmdContext.getMessage();
        TextChannel textChannel = cmdContext.getChannel();
        EmbedBuilder eb = new EmbedBuilder();
        User user = cmdContext.getAuthor();
        try {
            if (args[0].equals("upload")) {
                if (message.getAttachments().size() > 1) {
                    textChannel.sendMessage("파일을 하나씩만 보내주세요!").queue();
                    return;
                }

                Message.Attachment attachment;
                try {
                    attachment = message.getAttachments().get(0);
                } catch (IndexOutOfBoundsException e) {
                    eb.setTitle("오류!");
                    eb.setDescription("```\n음악 파일이 필요합니다!\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }
                try {
                    InputStream attachmentInputStream = attachment.retrieveInputStream().get();
                    StringBuilder fileSignature = new StringBuilder();

                    for (int i = 0; i < 3; i++) {
                        fileSignature.append(attachmentInputStream.read());
                        fileSignature.append(" ");
                    }
                    fileSignature.deleteCharAt(fileSignature.length() - 1);

                    if (fileSignature.toString().equals("73 68 51") || fileSignature.toString().equals("255 251 144")) {
                        textChannel.sendMessage("MP3 파일입니다!").queue();
                        attachmentInputStream.reset();

                        try {
                            int b;
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            while ((b = attachmentInputStream.read()) != -1) byteArrayOutputStream.write(b);
                            attachmentInputStream.close();
                            byteArrayOutputStream.close();

                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            PreparedStatement prepareStatement = Main.con.prepareStatement("insert into saved_mp3 values (?, ?)");
                            prepareStatement.setString(1, new String(Base64.getEncoder().encode(attachment.getFileName().getBytes())));
                            prepareStatement.setBinaryStream(2, byteArrayInputStream, byteArrayOutputStream.size());
                            textChannel.sendMessage("업로드 중...").queue();
                            prepareStatement.executeUpdate();

                            textChannel.sendMessage("업로드 성공!").queue();
                            prepareStatement.close();
                        } catch (Exception e) {
                            textChannel.sendMessage(e.getMessage()).queue();
                        }
                    } else {
                        eb.setTitle("오류!");
                        eb.setDescription("```Java\nMP3 파일이 아닙니다!\n```");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                    }
                } catch (Exception ignored) {
                    eb.setTitle("오류!");
                    eb.setDescription("```\n파일을 받을 수 없습니다!\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                }

            } else if (args[0].equalsIgnoreCase("download") || args[0].equalsIgnoreCase("down")) {
                String mp3Name;
                //if(!user.getId().equals("345473282654470146")) textChannel.sendMessage("준비중입니다!").queue();
                if (args.length < 2) {
                    eb.setTitle("오류!");
                    eb.setDescription("```Java\nMP3 파일의 번호를 입력하지 않았습니다!\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                String[] mp3List = SQL.getSQLData(Main.con, "saved_mp3", "filename", cmdContext.getEvent());

                if (Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[1]) > mp3List.length) {
                    eb.setTitle("오류!");
                    eb.setDescription("```Java\n파일 번호에 일치하는 MP3 파일이 없습니다!\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                mp3Name = new String(Base64.getDecoder().decode(mp3List[Integer.parseInt(args[1]) - 1]));
                textChannel.sendMessage("Found MP3 Music File: " + mp3Name).queue();

                RestAction<Message> messageAction = textChannel.sendMessage("전송 중....");
                String messageId = messageAction.complete().getId();

                try {
                    String tmpPath = File.createTempFile("temp", ".tmp").getParent() + File.separator;
                    String sql = "select * from saved_mp3 where filename = ?";
                    PreparedStatement prepareStatement = Main.con.prepareStatement(sql);

                    prepareStatement.setString(1, new String(Base64.getEncoder().encode(mp3Name.getBytes())));
                    ResultSet result = prepareStatement.executeQuery();

                    if (result.next()) {
                        Blob blob = result.getBlob("file");
                        InputStream inputStream = blob.getBinaryStream();

                        if (cmdContext.getEvent().getJDA().getSelfUser().getAllowedFileSize() <= blob.length()) {
                            eb.setTitle("경고!");
                            eb.setDescription("```Java\n전송하려는 파일이 너무 큽니다! (분할 압축 후 전송)\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.editMessageById(messageId, eb.build()).queue();

                            ZipParameters zipParameters = new ZipParameters();
                            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                            zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
                            zipParameters.setFileNameInZip(mp3Name);

                            ZipFile mp3Zip = new ZipFile(tmpPath + mp3Name.replace(mp3Name.substring(mp3Name.lastIndexOf(".") + 1), "zip"));

                            File mp3File = new File(tmpPath + System.currentTimeMillis() + "");
                            FileUtils.copyInputStreamToFile(inputStream, mp3File);
                            inputStream.close();

                            mp3Zip.createSplitZipFile(Collections.singletonList(mp3File), zipParameters, true,8388300);
                            for(File splitMP3ZipFile : mp3Zip.getSplitZipFiles())
                            {
                                textChannel.sendFile(splitMP3ZipFile).complete();
                                splitMP3ZipFile.delete();
                            }
                            textChannel.deleteMessageById(messageId).queue();
                            mp3File.delete();
                            return;
                        }

                        textChannel.sendFile(inputStream, mp3Name).complete();
                        textChannel.deleteMessageById(messageId).queue();
                    }

                    result.close();
                    prepareStatement.close();

                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    textChannel.sendMessage(e.getMessage()).queue();
                    textChannel.deleteMessageById(messageId).queue();
                }

            } else if (args[0].equals("list")) {
                String[] mp3List = SQL.getSQLData(Main.con, "saved_mp3", "filename", cmdContext.getEvent());
                if (mp3List.length == 0) {
                    eb.setTitle("Music file list: ");
                    eb.setDescription("```\n" + "DB에 저장된 음악 파일이 없습니다!" + "\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                }
                int cnt = 0;
                StringBuilder stringBuilder = new StringBuilder();
                for (String mp3Name : mp3List) {
                    stringBuilder.append(++cnt).append(": ").append(new String(Base64.getDecoder().decode(mp3Name.getBytes()))).append("\n");
                }
                eb.setTitle("Music file list: ");
                eb.setDescription("```\n" + stringBuilder.toString() + "\n```");
                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                textChannel.sendMessage(eb.build()).queue();
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            eb.setTitle("사용법");
            eb.setDescription("```\n" + "!!mp3 [upload / download / list]\n\n" +
                    "[upload] - 파일 붙여서 보내기\n" +
                    "[download] [num] - MP3 파일의 번호를 입력합니다.\n" +
                    "[list] DB에 저장된 음악 파일 번호와 이름을 출력합니다.\n\n" +
                    "!!m p mp3/[filename]\n" +
                    "[filename] - 재생할 파일 이름을 입력헙나다.\n" + "\n```");
            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
            textChannel.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public String getName() {
        return "mp3";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mp");
    }
}
