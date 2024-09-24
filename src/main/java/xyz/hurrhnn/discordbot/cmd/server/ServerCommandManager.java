package xyz.hurrhnn.discordbot.cmd.server;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.util.Info;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ServerCommandManager extends Thread{
    private final List<ICmd> commands = new ArrayList<>();

    public ServerCommandManager(){
        addCommand(new ConnectCommand());
        addCommand(new DisconnectCommand());
        addCommand(new ServerStatusCommand());
        addCommand(new ServerHelpCommand(this));
    }

    private void addCommand(ICmd cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            throw new IllegalArgumentException("A command with this name is already present.");
        }

        commands.add(cmd);
    }

    @Nullable
    public ICmd getCommand(String search) {
        String searchLower = search.toLowerCase();

        for(ICmd cmd : this.commands) {
            if(cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) return cmd;
        }

        return null;
    }

    public List<ICmd> getCommands() {
        return commands;
    }

    public void handle(MessageReceivedEvent event)
    {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?-i)" + Pattern.quote(Info.getPrefix(event)), "")
                .split("\\s+");

        String invoke = split[1].toLowerCase();
        ICmd cmd = this.getCommand(invoke);

        if(cmd != null)
        {
            List<String> args = Arrays.asList(split).subList(2, split.length);

            CmdContext cmdContext = new CmdContext(event, args);
            cmd.handle(cmdContext);
        }
    }
}
