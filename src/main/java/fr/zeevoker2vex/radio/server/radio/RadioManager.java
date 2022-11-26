package fr.zeevoker2vex.radio.server.radio;

import fr.zeevoker2vex.radio.common.network.NetworkHandler;
import fr.zeevoker2vex.radio.common.network.client.PlayRadioSoundPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.*;
import java.util.stream.Collectors;



@SideOnly(Side.SERVER)



public class RadioManager {

    public static HashMap<UUID, Short> playersFrequencies = new HashMap<>();
    public static List<UUID> playersSpeaking = new ArrayList<>();
    public static void connectPlayerTo(EntityPlayer player, short frequency) {
        playersFrequencies.put(getUUID(player), frequency);
    }

    public static void disconnectPlayer(EntityPlayer player) {
        playersFrequencies.remove(getUUID(player));
    }

    public static short getFrequencyOf(EntityPlayer player) {
        return playersFrequencies.getOrDefault(getUUID(player), (short) -1);
    }

    public static boolean isConnected(EntityPlayer player) {
        return playersFrequencies.containsKey(getUUID(player));
    }

    public static boolean isConnectTo(EntityPlayer player, short frequency) {
        return getFrequencyOf(player) == frequency;
    }

    private static UUID getUUID(EntityPlayer player) {
        return player.getUniqueID();
    }

    public static List<UUID> getPlayersConnectedOnFrequency(short frequency) {
        return playersFrequencies.entrySet().stream().filter(entry -> entry.getValue() == frequency).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static void setPlayerSpeaking(EntityPlayer player, boolean speaking) {
        playRadioSoundToFrequency(speaking, getFrequencyOf(player));
        if (speaking) {
            playersSpeaking.add(getUUID(player));
        } else {
            playersSpeaking.remove(getUUID(player));
        }
    }

    public static boolean isSpeaking(EntityPlayer player) {
        return playersSpeaking.contains(getUUID(player));
    }
    public static void playRadioSoundToFrequency(boolean speaking, short frequency){
        getPlayersConnectedOnFrequency(frequency).forEach(uuid-> NetworkHandler.getInstance().getNetwork().sendTo(new PlayRadioSoundPacket(speaking), FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(uuid)));
    }
}
