package xyz.nifeather.fmccl.network.commands.S2C;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;

public class NetheriteS2CCurrentCommand extends NetheriteS2CCommand<String>
{
    public NetheriteS2CCurrentCommand(String identifier)
    {
        super(identifier);
    }

    public String getDisguiseIdentifier()
    {
        return getArgumentAt(0, "null");
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.Current;
    }
    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onCurrentCommand(this);
    }
}
