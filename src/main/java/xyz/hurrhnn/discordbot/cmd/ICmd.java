package xyz.hurrhnn.discordbot.cmd;

import java.util.Collections;
import java.util.List;

public interface ICmd {
    void handle(CmdContext cmdContext);

    String getName();

    String getHelp();

    default List<String> getAliases() {
        return Collections.emptyList();
    }
}
