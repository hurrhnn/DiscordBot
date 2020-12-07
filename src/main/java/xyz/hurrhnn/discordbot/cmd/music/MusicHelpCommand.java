package xyz.hurrhnn.discordbot.cmd.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;
import xyz.hurrhnn.discordbot.util.Info;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MusicHelpCommand implements ICmd {

    private final MusicCommandManager manager;

    public MusicHelpCommand(MusicCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CmdContext cmdContext) {
        List<String> args = cmdContext.getArgs();
        AtomicReference<TextChannel> textChannel = new AtomicReference<>(cmdContext.getChannel());

        if(args.isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Available commands are: \n");

            manager.getCommands().stream().map(ICmd::getName).forEach((it) -> stringBuilder.append("`").append(Info.getPrefix(cmdContext.getEvent())).append("music ").append(it).append("`, "));

            textChannel.get().sendMessage(stringBuilder.substring(0, stringBuilder.length() - 2)).queue();
            return;
        }

        String search = args.get(0);
        ICmd cmd = manager.getCommand(search);

        if(cmd == null)
        {
            textChannel.get().sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```E: Nothing found for " + search).build()+ "```").queue();
            return;
        }
        textChannel.get().sendMessage(EmbedUtils.embedMessageWithTitle("Usage", cmd.getHelp()).build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!music help [command]\n" +
                "-- Shows the list with music commands in the bot.\n```";
    }
}
