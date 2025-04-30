package xyz.nifeather.fmccl.network.commands.S2C.set;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public class NetheriteS2CSetSelfViewingCommand extends NetheriteS2CSetCommand<Boolean>
{
    public NetheriteS2CSetSelfViewingCommand(Boolean val)
    {
        super(val);
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.SetSelfViewing;
    }

    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onSetSelfViewingCommand(this);
    }
}
