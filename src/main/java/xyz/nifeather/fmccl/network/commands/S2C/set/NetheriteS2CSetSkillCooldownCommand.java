package xyz.nifeather.fmccl.network.commands.S2C.set;

import xyz.nifeather.fmccl.network.BasicServerHandler;
import xyz.nifeather.fmccl.network.annotations.Environment;
import xyz.nifeather.fmccl.network.annotations.EnvironmentType;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;

public class NetheriteS2CSetSkillCooldownCommand extends NetheriteS2CSetCommand<Long>
{
    public NetheriteS2CSetSkillCooldownCommand(long value)
    {
        super(value);
    }

    @Override
    public String getBaseName()
    {
        return NetheriteS2CCommandNames.SetSkillCooldown;
    }

    @Environment(EnvironmentType.CLIENT)
    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
        handler.onSetSkillCooldownCommand(this);
    }
}
