package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.Info;
import xyz.hurrhnn.discordbot.util.SQL;

public class PrefixCommand implements ICmd {

    @Override
    public void handle(CmdContext cmdContext) {
        TextChannel textChannel = cmdContext.getChannel();
        if (!cmdContext.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: You don't have administrator rights on this server to execute this command.\n```").build()).queue();
        else {
            if (cmdContext.getArgs().size() < 1)
                textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: No prefix was entered.\n```").build()).queue();
            else if (cmdContext.getArgs().get(0).equals("reset")) {
                SQL.updateSQLData(Main.con, "prefix", "prefix", "!!", "guild_id", cmdContext.getGuild().getId(), cmdContext.getEvent());
                if (Info.getPrefix(cmdContext.getEvent()).equals("!!"))
                    textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - reset!", "```\nThe set prefix has been successfully initialized to !!\n```").build()).queue();
            } else if (cmdContext.getArgs().size() > 1)
                textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: Prefix cannot contain spaces.\n```").build()).queue();
            else {
                String replacement = cmdContext.getArgs().get(0);
                if (replacement.length() > 2)
                    textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: Prefix must be no more than 2 characters.\n```").build()).queue();
                else if (replacement.contains("<:") && replacement.contains(">"))
                    textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: A prefix cannot contain emoji.\n```").build()).queue();
                else {
                    final String[] unicodeSpaces = {"\u0020", "\u00A0", "\u1680", "\u180E", "\u2000", "\u2001", "\u2002", "\u2003", "\u2004", "\u2005", "\u2006", "\u2007", "\u2008", "\u2009", "\u200A", "\u200B", "\u202F", "\u205F", "\u3000", "\uFEFF", "\u2423", "\u2422", "\u2420"};
                    for (String unicodeSpace : unicodeSpaces)
                        if (replacement.contains(unicodeSpace)) {
                            textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Error!", "```\nE: Prefix cannot contain spaces.\n```").build()).queue();
                            return;
                        }

                    String beforePrefix = Info.getPrefix(cmdContext.getEvent());
                    SQL.updateSQLData(Main.con, "prefix", "prefix", replacement, "guild_id", cmdContext.getGuild().getId(), cmdContext.getEvent());
                    String afterPrefix = Info.getPrefix(cmdContext.getEvent());

                    if (!beforePrefix.equals(afterPrefix))
                        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Successfully Changed!", "```\nPrefix changed from " + beforePrefix + " to " + afterPrefix + " successfully.\n```").build()).queue();
                    else textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Prefix - Not Changed.", "```\nThe prefix is the same as the existing one to replaceable.\nCurrent prefix: " + afterPrefix + "\n```").build()).queue();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!prefix [prefix to change] or [reset] (Only users with administrator privileges on the server are allowed.)\n\n" +
                "-- [prefix to change] Enter the prefix you want to change. (No more than three characters of prefix and space characters are allowed).\n\n-- [reset] Reset the set prefix.\n```";
    }
}
