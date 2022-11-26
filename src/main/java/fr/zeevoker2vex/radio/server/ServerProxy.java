package fr.zeevoker2vex.radio.server;

import fr.nathanael2611.modularvoicechat.api.VoiceDispatchEvent;
import fr.nathanael2611.modularvoicechat.api.VoiceProperties;
import fr.zeevoker2vex.radio.common.CommonProxy;
import fr.zeevoker2vex.radio.common.RadioAddon;
import fr.zeevoker2vex.radio.common.network.NetworkHandler;

import fr.zeevoker2vex.radio.server.config.AddonConfig;
import fr.zeevoker2vex.radio.server.config.FrequenciesConfig;
import fr.zeevoker2vex.radio.server.radio.RadioManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ServerProxy extends CommonProxy {

    private static FrequenciesConfig addonConfig;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        File configFolder = new File(event.getModConfigurationDirectory().getPath(), RadioAddon.CONFIG_FOLDER);
        if (!configFolder.exists()) configFolder.mkdirs();

        File configFile = new File(configFolder.getPath(), "RadioFrequencies.json");
        addonConfig = new FrequenciesConfig(configFile);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static FrequenciesConfig getConfig() {
        return addonConfig;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @SubscribeEvent
    public void dispatchVoice(VoiceDispatchEvent event) {
        EntityPlayer player = event.getSpeaker();
// Le joueur est actuellement en train de parler en radio
        if(RadioManager.isSpeaking(player)){
            short frequency = RadioManager.getFrequencyOf(player);
            List<UUID> connected = RadioManager.getPlayersConnectedOnFrequency(frequency);
            // Pour chaque UUID connectée à cette fréquence, on va récupérer le joueur et lui envoyer l'audio de la radio
            for(UUID uuid : connected){
                if(player.getUniqueID()==uuid) continue;

                event.setProperties(VoiceProperties.builder().with("isRadio", true).build());
                event.dispatchTo(FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(uuid), 100, VoiceProperties.builder().with("isRadio", true).build());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        RadioManager.setPlayerSpeaking(player, false);
        RadioManager.disconnectPlayer(player);
    }

}