package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class MiscellaneousFeature {
    public MiscellaneousFeature(MessageReceivedEvent event, String raw) {
        if (event.getGuild().getId().equals("769169202744918076")) {
            if (raw.contains("<@!345473282654470146>") || raw.contains("<@345473282654470146>"))
                event.getChannel().asTextChannel().sendFiles(FileUpload.fromData(new File("isOwnerMentioned.png"))).queue();

            if (raw.contains("@everyone"))
                event.getChannel().asTextChannel().sendFiles(FileUpload.fromData(new File("isEveryoneMentioned.png"))).queue();
        }

        if (raw.contains("초특가 야놀자!")) //TextChannel clean code
        {
            TextChannel textChannelToDelete = event.getChannel().asTextChannel();
            int pos = textChannelToDelete.getPosition();
            ChannelAction<TextChannel> createCopiedTextChannelAction = event.getChannel().asTextChannel().createCopy();
            createCopiedTextChannelAction.setPosition(pos).complete();
            textChannelToDelete.delete().queue();
        }
    }
}
