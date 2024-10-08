package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.hurrhnn.discordbot.cmd.server.ServerCommand;
import xyz.hurrhnn.discordbot.util.Info;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager extends Thread {
    private final List<ICmd> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new CleanChatCommand());
        addCommand(new DebugCommand());
        addCommand(new MemeCommand());
        addCommand(new PingCommand());
        addCommand(new ShutdownCommand());
        addCommand(new HelpCommand(this));
        addCommand(new MusicCommand());
        addCommand(new PrefixCommand());
        addCommand(new ServerCommand());
        addCommand(new InfoCommand());
        addCommand(new MP3Command());
        addCommand(new WordCommand());
    }

    private void addCommand(ICmd cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
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

        for (ICmd cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) return cmd;
        }
        return null;
    }

    public void handle(MessageReceivedEvent event) {

        EmbedUtils.setEmbedBuilder(
                () -> new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setFooter("Requested By " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
        );

        if (event.getMessage().getContentRaw().equals("?prefix"))
            event.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Prefix", "The prefix " + event.getJDA().getSelfUser().getName() + " bot uses on this server: `" + Info.getPrefix(event) + "`").build()).queue();

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Info.getPrefix(event)), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICmd cmd = this.getCommand(invoke);

        if (cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CmdContext cmdContext = new CmdContext(event, args);
            cmd.handle(cmdContext);
        }
    }
}
