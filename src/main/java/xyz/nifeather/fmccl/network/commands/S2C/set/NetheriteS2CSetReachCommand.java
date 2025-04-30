package xyz.nifeather.fmccl.network.commands.S2C.set;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public class NetheriteS2CSetReachCommand extends NetheriteS2CSetCommand<Integer>
{
    public NetheriteS2CSetReachCommand(int reach)
    {
        super(reach);
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.SetReach;
    }

    public int getReach()
    {
        return getArgumentAt(0, -1);
    }

    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onSetReach(this);
    }
}
