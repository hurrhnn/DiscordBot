package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import xyz.hurrhnn.discordbot.util.SQL;

import java.sql.Connection;
import java.util.Objects;

public class Main {

    public static Connection con = SQL.initSQLConnection("discordjavabot");

    private Main() {
        JDABuilder builder = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        builder.setToken(Objects.requireNonNull(SQL.getSQLData(con, "info", "token", null))[0]);
        builder.setAutoReconnect(true)
       .addEventListeners(new EventListener())
       .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
       .build();
    }

    public static void main(String[] args) {
        new Main();
    }
}
