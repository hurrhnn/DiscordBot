package xyz.hurrhnn.discordbot.cmd.server;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.utils.FileUpload;
import xyz.hurrhnn.discordbot.cmd.CmdContext;
import xyz.hurrhnn.discordbot.cmd.ICmd;

import java.io.*;

public class ServerStatusCommand implements ICmd {
    @Override
    public void handle(CmdContext cmdContext) {
        if(ServerStateSocket.clientSocket == null)
            cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Server - Connect", "```E: Can't connect the remote server.```").build()).queue();
        else if(ServerStateSocket.clientSocket.isClosed())
            cmdContext.getChannel().sendMessageEmbeds(EmbedUtils.embedMessageWithTitle("Server - Connect", "```E: Connection closed with the server.```").build()).queue();
        else
        {
            try {
                InputStream socketInputStream = ServerStateSocket.clientSocket.getInputStream();
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(socketInputStream));
                PrintWriter printWriter = new PrintWriter(ServerStateSocket.clientSocket.getOutputStream(), true);

                printWriter.println("FILE_SIZE?");
                long fileSize = Long.parseLong(socketReader.readLine());

                File file = new File("latest_svState.png");
                if(0 < fileSize)
                {
                    printWriter.println("FILE_READY OK");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int receivedSize;
                    long receivedSizeAll = 0;
                    byte[] buffer = new byte[4096];

                    while (receivedSizeAll < fileSize) {
                        if ((receivedSize = socketInputStream.read(buffer)) == -1) break;
                        receivedSizeAll += receivedSize;
                        fileOutputStream.write(buffer, 0, receivedSize);
                    }
                    fileOutputStream.close();
                    System.out.println(file.length());
                }
                cmdContext.getChannel().asTextChannel().sendFiles(FileUpload.fromData(file)).queue();
            }catch (IOException e)
            { e.printStackTrace();}
        }
    }

    @Override
    public String getName() {
        return "state";
    }

    @Override
    public String getHelp() {
        return "```diff\n+ Usage: !!server state\n" +
                "-- Sends the server status received from the svState server you are connected to.\n```";
    }
}