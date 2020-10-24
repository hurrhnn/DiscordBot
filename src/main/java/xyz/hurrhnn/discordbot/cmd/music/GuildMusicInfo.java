package xyz.hurrhnn.discordbot.cmd.music;

import java.util.HashMap;
import java.util.Map;

public class GuildMusicInfo {
    public static Map<String, Boolean> isGuildSkipRequestDelayedMap = new HashMap<>();

    public static void SetIsGuildSkipRequestDelayedMap(String guildId, Boolean isGuildSkipRequestDelayed)
    {
        isGuildSkipRequestDelayedMap.remove(guildId);
        isGuildSkipRequestDelayedMap.put(guildId, isGuildSkipRequestDelayed);
    }
}
