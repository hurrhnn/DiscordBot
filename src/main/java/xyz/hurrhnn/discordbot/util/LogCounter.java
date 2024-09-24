package xyz.hurrhnn.discordbot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import xyz.hurrhnn.discordbot.Main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogCounter extends Thread {

    private final Logger LOGGER;
    private final MessageReceivedEvent event;
    private final String raw;

    public LogCounter(MessageReceivedEvent event, Logger LOGGER) {
        this.event = event;
        this.raw = (!event.getMessage().getContentRaw().isEmpty() ? (!event.getMessage().getAttachments().isEmpty() ? (event.getMessage().getContentRaw().replace("\n", " ") + " (FILE_ATTACHMENT)") : (event.getMessage().getContentRaw().replace("\n", " "))) : (!event.getMessage().getAttachments().isEmpty() ? ("(FILE_ATTACHMENT)") : ("(EMPTY_MESSAGE)")));
        this.LOGGER = LOGGER;
    }

    public void run() {
        try {
            List<String> chat = Collections.singletonList(raw);
            List<Message.Attachment> attachmentList = event.getMessage().getAttachments();

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.format(date);

            if (!event.getMessage().getAttachments().isEmpty()) {
                for (int i = 0; i < attachmentList.size(); i++) {
                    LOGGER.info("[CHAT] [{}][{}][{}]: {}", event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getAsTag(), raw);

                    PreparedStatement prepareStatement = Main.con.prepareStatement("insert into log values (?, ?, ?)");
                    Message.Attachment attachment = attachmentList.get(i);
                    InputStream inputStream = attachment.getProxy().download().get();

                    int b;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((b = inputStream.read()) != -1) byteArrayOutputStream.write(b);
                    inputStream.close();
                    byteArrayOutputStream.close();

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    prepareStatement.setString(1, chat.size() < i ? "(ATTACHMENT_ONLY)" : MessageFormat.format("{0} [{1}] INFO - {2} [CHAT][{2}][{3}][{4}][{5}]: {6}", dateFormat.format(date), currentThread().getName(), getClass().getSimpleName(), event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getAsTag(), raw));
                    prepareStatement.setString(2, attachment.getFileName());
                    prepareStatement.setBinaryStream(3, byteArrayInputStream, byteArrayOutputStream.size());
                    prepareStatement.executeUpdate();
                    prepareStatement.close();
                }
            } else {
                LOGGER.info("[CHAT] [{}][{}][{}]: {}", event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName(), raw);

                PreparedStatement prepareStatement = Main.con.prepareStatement("insert into log values (?, ?, ?)");
                prepareStatement.setString(1, MessageFormat.format("{0} [{1}] INFO - {2} [CHAT][{2}][{3}][{4}][{5}]: {6}", dateFormat.format(date), currentThread().getName(), getClass().getSimpleName(), event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getAsTag(), raw));
                prepareStatement.setString(2, null);
                prepareStatement.setBinaryStream(3, null);
                prepareStatement.executeUpdate();
                prepareStatement.close();
            }
        } catch (Exception e) {
            SQL.errSQLConnection(e.getMessage(), event);
        }
    }
}
