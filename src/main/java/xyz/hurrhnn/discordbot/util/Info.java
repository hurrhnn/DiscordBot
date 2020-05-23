package xyz.hurrhnn.discordbot.util;

import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;

public class Info {

    public static String prefix = SQL.getSQLData(Main.con, "info", "prefix", null)[0];
    public static String owner_id = SQL.getSQLData(Main.con, "info", "owner_id", null)[0];
    public static String version = SQL.getSQLData(Main.con, "info", "version", null)[0];

    public static void reloadInfo()
    {
        prefix = SQL.getSQLData(Main.con, "info", "prefix", null)[0];
        owner_id = SQL.getSQLData(Main.con, "info", "owner_id", null)[0];
        version = SQL.getSQLData(Main.con, "info", "version", null)[0];
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getOwnerId()
    {
        return owner_id;
    }

    public static String getVersion() { return version; }
}
