package xyz.hurrhnn.discordbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;

public class Main {

    public static Connection con = SQL.initSQLConnection("discordjavabot");

    private Main() {

        JDABuilder builder = JDABuilder.createDefault(SQL.getSQLData(con, "info", "token", null)[0]);
        try {
                     builder.setAutoReconnect(true)
                    .setActivity(Activity.watching("鬼滅の刃"))
                    .addEventListeners(new EventListener())
                    .build();
        } catch (LoginException e) {
            LoggerFactory.getLogger(Main.class).error("Error to Login: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
