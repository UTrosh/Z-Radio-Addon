package fr.zeevoker2vex.radio.common.network.server;

import fr.zeevoker2vex.radio.client.ClientProxy;
import fr.zeevoker2vex.radio.server.radio.RadioManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerSpeakingOnRadioPacket implements IMessage {

    public boolean startSpeaking;

    public PlayerSpeakingOnRadioPacket(boolean startSpeaking) {
        this.startSpeaking = startSpeaking;
    }
    public PlayerSpeakingOnRadioPacket(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.startSpeaking = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.startSpeaking);
    }

    public static class ServerHandler implements IMessageHandler<PlayerSpeakingOnRadioPacket, IMessage> {
        @Override
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PlayerSpeakingOnRadioPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;

            RadioManager.setPlayerSpeaking(player, message.startSpeaking);

            return new PlayerSpeakingOnRadioPacket(message.startSpeaking);
        }
    }

    public static class ClientHandler implements IMessageHandler<PlayerSpeakingOnRadioPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PlayerSpeakingOnRadioPacket message, MessageContext ctx) {
            ClientProxy.speaking = message.startSpeaking;
            return null;
        }
    }
}