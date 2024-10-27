package com.awakenedredstone.sakuracake.network;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.data.PlayerData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SelectedCauldronSlotPacket(int slot) implements CustomPayload {
    public static final CustomPayload.Id<SelectedCauldronSlotPacket> ID = new CustomPayload.Id<>(SakuraCake.id("selected_cauldron_slot"));
    public static final PacketCodec<RegistryByteBuf, SelectedCauldronSlotPacket> PACKET_CODEC = PacketCodec.of(SelectedCauldronSlotPacket::write, SelectedCauldronSlotPacket::read);

    private void write(RegistryByteBuf buf) {
        buf.writeByte(slot);
    }

    private static SelectedCauldronSlotPacket read(RegistryByteBuf buf) {
        return new SelectedCauldronSlotPacket(buf.readByte());
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ((AttachmentTarget) context.player()).setAttached(PlayerData.CAULDRON_SLOT, slot);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
