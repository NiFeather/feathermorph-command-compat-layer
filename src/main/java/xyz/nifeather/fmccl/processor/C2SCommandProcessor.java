package xyz.nifeather.fmccl.processor;

import xyz.nifeather.fmccl.network.commands.C2S.*;
import xyz.nifeather.fmccl.network.commands.CommandRegistries;

public class C2SCommandProcessor
{
    private final CommandRegistries registries = new CommandRegistries();

    public C2SCommandProcessor()
    {
        registries.registerC2S(NetheriteC2SCommandNames.Initial, a -> new NetheriteC2SInitialCommand())
                .registerC2S(NetheriteC2SCommandNames.Morph, NetheriteC2SMorphCommand::new)
                .registerC2S(NetheriteC2SCommandNames.Skill, a -> new NetheriteC2SSkillCommand())
                .registerC2S(NetheriteC2SCommandNames.Option, NetheriteC2SOptionCommand::fromString)
                .registerC2S(NetheriteC2SCommandNames.ToggleSelf, a -> new NetheriteC2SToggleSelfCommand(NetheriteC2SToggleSelfCommand.SelfViewMode.fromString(a)))
                .registerC2S(NetheriteC2SCommandNames.Unmorph, a -> new NetheriteC2SUnmorphCommand())
                .registerC2S(NetheriteC2SCommandNames.Request, NetheriteC2SRequestCommand::new)
                .registerC2S(NetheriteC2SCommandNames.Animation, NetheriteC2SAnimationCommand::new);
    }

    /**
     * @param commandLine
     * @return Whether handle success
     */
    public NetheriteC2SCommand<?> processLegacyCommandLine(String commandLine) throws RuntimeException
    {
        var str = commandLine.split(" ", 2);

        if (str.length < 1)
            throw new RuntimeException("Incomplete server command: " + commandLine);

        var baseCommand = str[0];
        var c2sCommand = registries.createC2SCommand(baseCommand, str.length == 2 ? str[1] : "");

        if (c2sCommand == null)
            throw new RuntimeException("Unknown server command for name '%s'".formatted(baseCommand));

        return c2sCommand;
    }
}
