package xyz.hurrhnn.discordbot.cmd;

import groovy.lang.GroovyShell;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DebugCommand implements ICmd {

    private final GroovyShell engine;
    private final String imports;

    public DebugCommand() {
        this.engine = new GroovyShell();
        this.imports =
                "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.lang.String.*\n"+
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.api.*\n" +
                "import net.dv8tion.jda.api.entities.*\n" +
                "import net.dv8tion.jda.api.entities.impl.*\n" +
                "import net.dv8tion.jda.api.managers.*\n" +
                "import net.dv8tion.jda.api.managers.impl.*\n" +
                "import net.dv8tion.jda.api.utils.*\n";
    }

    @Override
    public void handle(CmdContext cmdContext) {
        RestAction<ApplicationInfo> applicationInfoRestAction = cmdContext.getJDA().retrieveApplicationInfo();
        ApplicationInfo applicationInfo = applicationInfoRestAction.complete();
        for (TeamMember teamMember : Objects.requireNonNull(applicationInfo.getTeam()).getMembers()) {
            if (teamMember.getUser().getId().equals(cmdContext.getAuthor().getId())) {
                if (!cmdContext.getArgs().isEmpty()) {
                    try {
                        engine.setProperty("args", cmdContext.getArgs());
                        engine.setProperty("event", cmdContext.getEvent());
                        engine.setProperty("message", cmdContext.getMessage());
                        engine.setProperty("channel", cmdContext.getChannel());
                        engine.setProperty("jda", cmdContext.getJDA());
                        engine.setProperty("guild", cmdContext.getGuild());
                        engine.setProperty("member", cmdContext.getMember());

                        String script = imports + cmdContext.getEvent().getMessage().getContentRaw().split("\\s+", 2)[1];
                        Object out = engine.evaluate(script);
                        cmdContext.getEvent().getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Eval!", "```Java\n" + (out == null ? "Executed Successfully without Error!\n```" : "Result: \"" + out.toString() + "\"" + "\n```")).build()).queue();
                        return;
                    }catch (Exception e) {
                        errHandler(e, cmdContext.getChannel());
                        return;
                    }
                }
                cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Debug", "```Java\nThe number of Threads: " + Thread.activeCount() + "\n```").build()).queue();
                return;
            }
        }
        cmdContext.getChannel().sendMessage(EmbedUtils.embedMessageWithTitle("Oops", "You are not authorized to execute this command!").build()).queue();
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ !!debug (args)\n" +
                "-- Debugging Bot (Restricted Command)\n```";
    }

    @Override
    public void errHandler(Exception e, TextChannel textChannel) {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        e.printStackTrace(errPrintStream);
        textChannel.sendMessage(EmbedUtils.embedMessageWithTitle("An error has occurred!", "```Java\n" + err.toString().split("\n")[0] + "\n```").build()).queue();
    }
    public List<String> getAliases() {
        return Collections.singletonList("eval");
    }
}
