package xyz.hurrhnn.discordbot.util;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.hurrhnn.discordbot.Main;

public class Info {
    public static int logThreadCount = 0;

    public static String getPrefix(GuildMessageReceivedEvent event) {
        String[] serverPrefixes = SQL.getSQLData(Main.con, "prefix", "prefix", event);
        for (int i = 0; i < serverPrefixes.length; i++) if (SQL.getSQLData(Main.con, "prefix", "guild_id", event)[i].equals(event.getGuild().getId())) return serverPrefixes[i];
        SQL.insertSQLData(Main.con, "prefix", ("!!\u200B" + event.getGuild().getName() + "\u200B" + event.getGuild().getId()).split("\u200B"), null);
        return getPrefix(event);
    }

    public static String getVersion() { return SQL.getSQLData(Main.con, "info", "version", null)[0]; }
}
