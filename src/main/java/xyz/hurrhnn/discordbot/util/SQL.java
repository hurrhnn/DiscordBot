package xyz.hurrhnn.discordbot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.LoggerFactory;
import xyz.hurrhnn.discordbot.EventListener;
import xyz.hurrhnn.discordbot.Main;

import javax.annotation.Nullable;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.util.Base64;

public class SQL {

    public static Connection initSQLConnection(String database) {

        Connection connection = null;
        String server = "", userName = "", password = "";

        try {
            server = getSQLServerInfo("server");
            userName = getSQLServerInfo("userName");
            password = getSQLServerInfo("password");
        }catch (IOException e) {
            System.out.println("Can't connect Secure Server. Exit.");
            System.exit(1);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("<JDBC Error> Driver load error: " + e.getMessage());
            LoggerFactory.getLogger(EventListener.class).error("<JDBC Error> Driver load error: {}", e.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?validationQuery=\"select 1\"", userName, password);
        } catch (SQLException e) {
            System.err.println("SQL Connection error. Program Exit.");
            LoggerFactory.getLogger(EventListener.class).error("SQL Connection error. Program Exit.");
            System.exit(0);
        }
        return connection;
    }

    private static String getSQLServerInfo(String value) throws IOException {

        String addrSecureServer = new String(Base64.getDecoder().decode("aHVycmhubi54eXo="));
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(addrSecureServer, 516));

        InputStream input = socket.getInputStream();
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(input));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

        switch (value) {
            case "server":
                printWriter.println("server");

                value = socketReader.readLine();
                for(int i = 0; i < 5; i++) value = new String(Base64.getDecoder().decode(value));
                return value;

            case "userName":
                printWriter.println("userName");

                value = socketReader.readLine();
                for(int i = 0; i < 5; i++) value = new String(Base64.getDecoder().decode(value));
                return value;

            case "password":
                printWriter.println("password");

                value = socketReader.readLine();
                for(int i = 0; i < 5; i++) value = new String(Base64.getDecoder().decode(value));
                return value;
        }
        return null;
    }

    public static String[] getSQLData(Connection connection, String table, String column, @Nullable MessageReceivedEvent event) {

        try {
            String query = "select * from " + table;
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            StringBuilder resultSum = new StringBuilder();
            while (result.next()) resultSum.append(result.getString(column)).append("\u200B");
            return resultSum.toString().split("\u200B");

        } catch (Exception e) {
            if (event != null) errSQLConnection(e.getMessage(), event);
            else System.err.println(e.getMessage());
        }
        return null;
    }

    public static void insertSQLData(Connection connection, String table, String[] data, MessageReceivedEvent event) {

        try {
            for (int i = 0; i < data.length; i++) data[i] = data[i].replace("'", "''").replace("\"", "\"\"");

            Statement statement = connection.createStatement();
            StringBuilder query = new StringBuilder("insert into " + table + " values(");

            for (String value : data) {
                query.append("'").append(value).append("', ");
            }
            query.deleteCharAt(query.length() - 1).deleteCharAt(query.length() - 1).append(")");
            statement.executeUpdate(query.toString());

        } catch (Exception e) {
            errSQLConnection(e.getMessage(), event);
        }
    }

    public static void updateSQLData(Connection connection, String table, String column, String columnValue, String condition, String conditionValue, MessageReceivedEvent event) {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("update " + table + " set " + column + " = '" + columnValue.replace("'", "''").replace("\"", "\"\"") + "' where " + condition + " = '" + conditionValue.replace("'", "''").replace("\"", "\"\"") + "'");
        } catch (Exception e) {
            errSQLConnection(e.getMessage(), event);
        }
    }

//    public static void dropSQLData(Connection connection, String table, String column, String value, GuildMessageReceivedEvent event) {
//
//        try {
//            Statement statement = connection.createStatement();
//            statement.executeUpdate("delete from " + table + " where " + column + "='" + value + "';");
//        } catch (Exception e) {
//            errSQLConnection(e.getMessage(), event);
//        }
//    }

    public static void errSQLConnection(String errMessage, MessageReceivedEvent event) {

        Main.con = SQL.initSQLConnection("discordjavabot");
        try {
            if(Main.con != null && !Main.con.isClosed()) return;
        }catch (SQLException ignored){ }

        MessageChannel textChannel = event.getChannel();
        User user = event.getAuthor();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("DB서버와 통신 중 오류가 발생했습니다!");
        embedBuilder.setDescription("```Java\n" + errMessage + "\n```");
        embedBuilder.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());

        LoggerFactory.getLogger(EventListener.class).error("DB서버와 통신 중 오류가 발생했습니다!");
        LoggerFactory.getLogger(EventListener.class).error("오류: {}", errMessage);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void finSQLConnection(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ignored) { }
    }
}
