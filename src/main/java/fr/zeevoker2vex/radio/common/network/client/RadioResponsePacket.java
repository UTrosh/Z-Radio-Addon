package fr.zeevoker2vex.radio.common.network.client;

import fr.zeevoker2vex.radio.client.ClientProxy;
import fr.zeevoker2vex.radio.client.gui.RadioGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class RadioResponsePacket implements IMessage {

    public short frequency;
    public int responseCode;

    public RadioResponsePacket(short frequency, ResponseCode responseCode) {
        this.frequency = frequency;
        this.responseCode = responseCode.ordinal();
    }
    public RadioResponsePacket(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.frequency = buf.readShort();
        this.responseCode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.frequency);
        buf.writeInt(this.responseCode);
    }

    public static class ClientHandler implements IMessageHandler<RadioResponsePacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(RadioResponsePacket message, MessageContext ctx) {

            ResponseCode responseCode = ResponseCode.values()[message.responseCode];
            short frequency = message.frequency;

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;

            switch(responseCode){
                case CONNECT_SUCCESS:
                case CONNECT_NO_PERM:
                case CONNECT_BLACKLISTED:
                    ClientProxy.frequency = frequency; break;
                case DISCONNECT_SUCCESS:
                    ClientProxy.frequency = -1; break;
                default: break;
            }
            if(mc.currentScreen instanceof RadioGui){
                RadioGui radioGui = (RadioGui) mc.currentScreen;

                if(frequency>0) radioGui.frequencyField.setText(Short.toString(frequency));

                radioGui.showResponse(responseCode);
            }
            return null;
        }
    }

    public enum ResponseCode {

        CONNECT_SUCCESS("radio.responseCode.connect.success", new Color(40, 119, 11).getRGB()), // Change frequency and state
        CONNECT_INVALID_FREQUENCY("radio.responseCode.connect.invalidFrequency", new Color(159, 6, 6).getRGB()), // Do nothing
        CONNECT_NO_PERM("radio.responseCode.connect.noPerm", Color.RED.getRGB()), // Change frequency but not state
        CONNECT_BLACKLISTED("radio.responseCode.connect.blacklisted",Color.RED.getRGB() ), // Change frequency

        DISCONNECT_SUCCESS("radio.responseCode.disconnect.success", new Color(16, 154, 6).getRGB()), // Change state
        DISCONNECT_ALREADY("radio.responseCode.disconnect.already", new Color(227, 127, 13).getRGB()); // Do nothing


        public String unlocalizedText;
        public int textColor;

        /**
         * The constructor of ResponseCode
         * @param unlocalizedText The unlocalized code of the text
         * @param textColor The color of the text
         */
        ResponseCode(String unlocalizedText, int textColor) {
            this.unlocalizedText = unlocalizedText;
            this.textColor = textColor;
        }

        public String getUnlocalizedText() {
            return unlocalizedText;
        }

        public int getTextColor() {
            return textColor;
        }
    }
}