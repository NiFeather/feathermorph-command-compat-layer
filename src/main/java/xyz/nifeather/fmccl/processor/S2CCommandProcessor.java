package xyz.nifeather.fmccl.processor;

import xyz.nifeather.fmccl.network.commands.CommandRegistries;
import xyz.nifeather.fmccl.network.commands.S2C.*;
import xyz.nifeather.fmccl.network.commands.S2C.clientrender.*;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapClearCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapRemoveCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CPartialMapCommand;
import xyz.nifeather.fmccl.network.commands.S2C.query.NetheriteS2CQueryCommand;
import xyz.nifeather.fmccl.network.commands.S2C.set.NetheriteS2CSetRevealingCommand;

import java.util.logging.Logger;

public abstract class S2CCommandProcessor
{
    private final CommandRegistries registries = new CommandRegistries();
    private final NetheriteS2CSetCommandsAgent agent = new NetheriteS2CSetCommandsAgent();

    private final Logger logger = Logger.getLogger("FeatherMorph - FMCCL");

    protected abstract void registerSetEquipCommand(NetheriteS2CSetCommandsAgent agent);

    public S2CCommandProcessor()
    {
        registerSetEquipCommand(agent);
        agent.register(NetheriteS2CCommandNames.SetRevealing, a ->
                {
                    try
                    {
                        var val = Float.parseFloat(a);
                        return new NetheriteS2CSetRevealingCommand(val);
                    }
                    catch (Throwable t)
                    {
                        logger.warning(t.getMessage());
                        t.printStackTrace();
                    }

                    return new NetheriteS2CSetRevealingCommand(0);
                });

        registries.registerS2C(NetheriteS2CCommandNames.Current, NetheriteS2CCurrentCommand::new)
                .registerS2C(NetheriteS2CCommandNames.Query, NetheriteS2CQueryCommand::from)
                .registerS2C(NetheriteS2CCommandNames.ReAuth, a -> new NetheriteS2CReAuthCommand())
                .registerS2C(NetheriteS2CCommandNames.UnAuth, a -> new NetheriteS2CUnAuthCommand())
                .registerS2C(NetheriteS2CCommandNames.SwapHands, a -> new NetheriteS2CSwapCommand())
                .registerS2C(NetheriteS2CCommandNames.BaseSet, agent::getCommand)
                .registerS2C(NetheriteS2CCommandNames.Request, NetheriteS2CRequestCommand::new)
                .registerS2C(NetheriteS2CCommandNames.Map, NetheriteS2CMapCommand::ofStr)
                .registerS2C(NetheriteS2CCommandNames.MapPartial, NetheriteS2CPartialMapCommand::ofStr)
                .registerS2C(NetheriteS2CCommandNames.MapClear, a -> new NetheriteS2CMapClearCommand())
                .registerS2C(NetheriteS2CCommandNames.MapRemove, a -> new NetheriteS2CMapRemoveCommand(Integer.parseInt(a)))
                .registerS2C(NetheriteS2CCommandNames.CRAdd, NetheriteS2CRenderMapAddCommand::of)
                .registerS2C(NetheriteS2CCommandNames.CRClear, a -> new NetheriteS2CRenderMapClearCommand())
                .registerS2C(NetheriteS2CCommandNames.CRMap, NetheriteS2CRenderMapSyncCommand::ofStr)
                .registerS2C(NetheriteS2CCommandNames.CRRemove, NetheriteS2CRenderMapRemoveCommand::of)
                .registerS2C(NetheriteS2CCommandNames.CRMeta, NetheriteS2CRenderMapMetaCommand::fromStr)
                .registerS2C("animation", NetheriteS2CAnimationCommand::new);
    }

    /**
     * @param commandLine
     * @return Whether handle success
     */
    public NetheriteS2CCommand<?> processLegacyCommandLine(String commandLine) throws RuntimeException
    {
        var str = commandLine.split(" ", 2);

        if (str.length < 1)
            throw new RuntimeException("Incomplete server command: " + commandLine);

        var baseCommand = str[0];
        var s2cCommand = registries.createS2CCommand(baseCommand, str.length == 2 ? str[1] : "");

        if (s2cCommand == null)
            throw new RuntimeException("Unknown server command for name '%s'".formatted(baseCommand));

        return s2cCommand;
    }
}
