package xyz.nifeather.fmccl.network.commands.S2C.set;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public abstract class NetheriteS2CSetFakeEquipCommand<TItemStack> extends NetheriteS2CSetCommand<TItemStack>
{
    public NetheriteS2CSetFakeEquipCommand(TItemStack item, ProtocolEquipmentSlot slot)
    {
        super(item);
        this.slot = slot;
    }

    private final ProtocolEquipmentSlot slot;

    public ProtocolEquipmentSlot getSlot()
    {
        return slot;
    }

    public TItemStack getItemStack()
    {
        return getArgumentAt(0);
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.SetFakeEquip;
    }

    public abstract String serializeArguments();

    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onSetFakeEquipCommand(this);
    }

    public enum ProtocolEquipmentSlot
    {
        MAINHAND,
        OFF_HAND,

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;

        @Override
        public String toString()
        {
            return this.name().toLowerCase();
        }
    }
}
