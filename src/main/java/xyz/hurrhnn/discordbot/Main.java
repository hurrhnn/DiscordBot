package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;
import xyz.hurrhnn.discordbot.util.SQL;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.util.Objects;

public class Main {

    public static Connection con = SQL.initSQLConnection("discordjavabot");

    private Main() {
        JDABuilder builder = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        builder.setToken(Objects.requireNonNull(SQL.getSQLData(con, "info", "token", null))[0]);
        try {
                     builder.setAutoReconnect(true)
                    .addEventListeners(new EventListener())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                    .build();
        } catch (LoginException e) {
            LoggerFactory.getLogger(Main.class).error("Error to Login: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
