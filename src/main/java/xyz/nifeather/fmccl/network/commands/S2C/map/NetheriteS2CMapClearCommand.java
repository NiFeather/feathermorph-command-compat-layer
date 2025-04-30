package xyz.nifeather.fmccl.network.commands.S2C.map;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommand;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public class NetheriteS2CMapClearCommand extends NetheriteS2CCommand<String>
{
    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.MapClear;
    }

    public NetheriteS2CMapClearCommand()
    {
        super(NetheriteS2CCommandNames.MapClear);
    }

    @Override
    public void onCommand(BasicServerHandler<?> listener)
    {
        listener.onMapClearCommand(this);
    }
}
