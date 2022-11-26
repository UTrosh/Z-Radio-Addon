package fr.zeevoker2vex.radio.common.network.server;

import fr.zeevoker2vex.radio.common.network.client.RadioResponsePacket;
import fr.zeevoker2vex.radio.server.radio.RadioManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerDisconnectRadioPacket implements IMessage {

    public byte b;

    public PlayerDisconnectRadioPacket(byte b) {
        this.b = b;
    }
    public PlayerDisconnectRadioPacket(){}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.b = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.b);
    }

    public static class ServerHandler implements IMessageHandler<PlayerDisconnectRadioPacket, IMessage> {
        @Override
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PlayerDisconnectRadioPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;

// Si le joueur est connecté à une fréquence alors on le déconnecte
            if(RadioManager.isConnected(player)){
                RadioManager.disconnectPlayer(player);
                return new RadioResponsePacket((short) -1, RadioResponsePacket.ResponseCode.DISCONNECT_SUCCESS);
            }
            return new RadioResponsePacket((short) -1, RadioResponsePacket.ResponseCode.DISCONNECT_ALREADY);
    }
}}