package xyz.hurrhnn.discordbot.cmd;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.hurrhnn.discordbot.util.Info;
import java.util.List;

public class HelpCommand implements ICmd {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CmdContext cmdContext) {
        List<String> args = cmdContext.getArgs();
        TextChannel textChannel = cmdContext.getChannel();

        if(args.isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Available commands are: \n");

            manager.getCommands().stream().map(ICmd::getName).forEach((it) -> stringBuilder.append("`").append(Info.getPrefix()).append(it).append("`, "));

            textChannel.sendMessage(stringBuilder.toString().substring(0, stringBuilder.length() - 2)).queue();
            return;
        }

        String search = args.get(0);
        ICmd cmd = manager.getCommand(search);

        if(cmd == null)
        {
            textChannel.sendMessage("Nothing found for " + search).queue();
            return;
        }
        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("Usage", cmd.getHelp()).build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!help [command]\n" +
                "-- Shows the list with commands in the bot.\n```";
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) { }
}
