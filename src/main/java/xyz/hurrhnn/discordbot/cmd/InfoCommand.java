package xyz.hurrhnn.discordbot.cmd;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class InfoCommand implements ICmd{
    @Override
    public void handle(CmdContext cmdContext) {
        TextChannel textChannel = cmdContext.getChannel().asTextChannel();
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();

        String OS = System.getProperty("os.name");

        if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        Runtime
                                .getRuntime()
                                .exec("uname -s -r")
                                .getInputStream(), StandardCharsets.UTF_8));
                OS = br.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        textChannel.sendMessage("System Spec:\n" + "> CPU - " + hal.getProcessor().getProcessorIdentifier().getName().replace("CPU ", "").replace("       ", "") + "\n" +
                "> Memory - MaxHeapSize: " + Runtime.getRuntime().maxMemory() / 1048576 + "M,  " +
                "AllocatedHeapSize: " + Runtime.getRuntime().totalMemory() / 1048576 + "M,  " +
                "FreeHeapSize: " + Runtime.getRuntime().freeMemory() / 1048576 + "M\n" +
                "> OS - " + OS + ", " + System.getProperty("os.arch") + "\n" +
                "> JVM - " + System.getProperty("java.vm.name") + " (build " + System.getProperty("java.runtime.version") + ")").queue();
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!info\n" +
                "-- Shows system information for the bot.\n```";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("i");
    }
}
