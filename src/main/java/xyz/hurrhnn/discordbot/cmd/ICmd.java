package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Collections;
import java.util.List;

public interface ICmd {
    void handle(CmdContext cmdContext);

    default void errHandler(Exception e, TextChannel textChannel) { e.printStackTrace(); }

    String getName();

    String getHelp();

    default List<String> getAliases() {
        return Collections.emptyList();
    }

    default boolean isArgsEmpty(List<String> args) { return args.isEmpty(); }
}
