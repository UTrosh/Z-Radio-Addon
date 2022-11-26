package fr.zeevoker2vex.radio.server.config;

import fr.zeevoker2vex.radio.common.RadioAddon;
import net.minecraftforge.common.config.Config;

@Config(modid = RadioAddon.MOD_ID, name = RadioAddon.CONFIG_FOLDER + "/RadioAddon")
public class AddonConfig {

    @Config.Comment({"This is the general config of the Radio Addon"})
    public static General generalConfig = new General();

    public static class General {

        @Config.Comment("If true, op players can connect on all frequencies, even if they are restricted and even blacklisted.")
        public boolean opBypassRestrictions = true;
    }


}