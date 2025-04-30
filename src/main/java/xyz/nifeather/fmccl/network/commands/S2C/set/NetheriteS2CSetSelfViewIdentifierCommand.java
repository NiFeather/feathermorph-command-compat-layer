package xyz.nifeather.fmccl.network.commands.S2C.set;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public class NetheriteS2CSetSelfViewIdentifierCommand extends NetheriteS2CSetCommand<String>
{
    public NetheriteS2CSetSelfViewIdentifierCommand(String identifier)
    {
        super(identifier);
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.SetSelfViewIdentifier;
    }

    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onSetSelfViewIdentifierCommand(this);
    }
}
