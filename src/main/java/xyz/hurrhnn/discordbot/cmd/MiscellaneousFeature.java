package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class MiscellaneousFeature {
    public MiscellaneousFeature(GuildMessageReceivedEvent event, String raw) {

//        if(raw.contains("<@!345473282654470146>") || raw.contains("<@345473282654470146>"))
//            event.getChannel().sendFile(new File("isOwnerMentioned.png")).queue();

//        if(raw.contains("@everyone"))
//            event.getChannel().sendFile(new File("isEveryoneMentioned.png")).queue();

        if(raw.contains("초특가 야놀자!")) //TextChannel clean code
        {
            TextChannel textChannelToDelete = event.getChannel();
            int pos = textChannelToDelete.getPosition();
            ChannelAction<TextChannel> createCopiedTextChannelAction = event.getChannel().createCopy();
            createCopiedTextChannelAction.setPosition(pos).complete();
            textChannelToDelete.delete().queue();
        }
    }
}
