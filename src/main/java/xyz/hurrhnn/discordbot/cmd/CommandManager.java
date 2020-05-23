package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.hurrhnn.discordbot.Main;
import xyz.hurrhnn.discordbot.util.SQL;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager extends Thread{
    private final List<ICmd> commands = new ArrayList<>();

    public CommandManager(){
        addCommand(new CleanChatCommand());
        addCommand(new DebugCommand());
        addCommand(new MemeCommand());
        addCommand(new PingCommand());
        addCommand(new ShutdownCommand());
        addCommand(new HelpCommand(this));
    }

    private void addCommand(ICmd cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            throw new IllegalArgumentException("A command with this name is already present.");
        }

        commands.add(cmd);
    }

    public List<ICmd> getCommands() {
        return commands;
    }

    @Nullable
    public ICmd getCommand(String search) {
        String searchLower = search.toLowerCase();

        for(ICmd cmd : this.commands) {
            if(cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) return cmd;
        }

        return null;
    }

    public void handle(GuildMessageReceivedEvent event)
    {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(SQL.getSQLData(Main.con, "info", "prefix", event)[0]), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICmd cmd = this.getCommand(invoke);

        if(cmd != null)
        {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CmdContext cmdContext = new CmdContext(event, args);
            cmd.handle(cmdContext);
        }
    }
}
