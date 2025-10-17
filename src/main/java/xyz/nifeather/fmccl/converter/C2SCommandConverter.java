package xyz.nifeather.fmccl.converter;

import xyz.nifeather.fmccl.network.commands.C2S.*;
import xyz.nifeather.morph.network.commands.C2S.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class C2SCommandConverter
{
    public C2SCommandConverter()
    {
        registerC2SModernToNetherite();
        registerFromNetheriteToModernMethods();
    }

    // Modern -> Netherite

    private final Map<String, Function<AbstractC2SCommand<?>, NetheriteC2SCommand<?>>> c2sFromModernToNetherite = new ConcurrentHashMap<>();

    public <X extends AbstractC2SCommand<?>> void registerC2SFromModernToNetherite(String cmdName,
                                                 Class<X> expectedClass,
                                                 Function<X, NetheriteC2SCommand<?>> method)
    {
        //       Input                  Output
        Function<AbstractC2SCommand<?>, NetheriteC2SCommand<?>> mth = modernCommand ->
        {
            if (!expectedClass.isInstance(modernCommand))
            {
                throw new IllegalArgumentException("Input command '%s' is not expected type '%s'"
                        .formatted(modernCommand.getClass().getSimpleName(), expectedClass.getSimpleName()));
            }

            return method.apply((X) modernCommand);
        };

        c2sFromModernToNetherite.put(cmdName, mth);
    }

    protected void registerC2SModernToNetherite()
    {
        this.registerC2SFromModernToNetherite(C2SCommandNames.RequestInitial, C2SRequestInitialCommand.class, cmd ->
        {
            return new NetheriteC2SInitialCommand();
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.RequestAnimation, C2SRequestAnimationCommand.class, cmd ->
        {
            return new NetheriteC2SAnimationCommand(cmd.getAnimationId());
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.Morph, C2SMorphCommand.class, cmd ->
        {
            var id = cmd.identifier();

            return new NetheriteC2SMorphCommand(id);
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.SetSingleOption, C2SSetSingleOptionCommand.class, cmd ->
        {
            var optionModern = cmd.getOption();

            NetheriteC2SOptionCommand.ClientOptions optionNetherite = switch (optionModern)
            {
                case HUD -> NetheriteC2SOptionCommand.ClientOptions.HUD;
                case CLIENTVIEW -> NetheriteC2SOptionCommand.ClientOptions.CLIENTVIEW;
            };

            return new NetheriteC2SOptionCommand(optionNetherite).setValue(cmd.getValue());
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.ActivateSkill, C2SActivateSkillCommand.class, cmd ->
        {
            return new NetheriteC2SSkillCommand();
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.ToggleSelf, C2SToggleSelfCommand.class, cmd ->
        {
            var modeModern = cmd.getSelfViewMode();

            NetheriteC2SToggleSelfCommand.SelfViewMode modeNetherite = switch (modeModern)
            {
                case ON -> NetheriteC2SToggleSelfCommand.SelfViewMode.ON;
                case OFF -> NetheriteC2SToggleSelfCommand.SelfViewMode.OFF;
                case CLIENT_ON -> NetheriteC2SToggleSelfCommand.SelfViewMode.CLIENT_ON;
                case CLIENT_OFF -> NetheriteC2SToggleSelfCommand.SelfViewMode.CLIENT_OFF;
            };

            return new NetheriteC2SToggleSelfCommand(modeNetherite);
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.Unmorph, C2SUnmorphCommand.class, cmd ->
        {
            return new NetheriteC2SUnmorphCommand();
        });

        this.registerC2SFromModernToNetherite(C2SCommandNames.ExchangeRequestManagement, C2SExchangeRequestManagementCommand.class, cmd ->
        {
            var modernDecision = cmd.decision;

            NetheriteC2SRequestCommand.Decision netheriteDecision = switch (modernDecision)
            {
                case ACCEPT -> NetheriteC2SRequestCommand.Decision.ACCEPT;
                case DENY -> NetheriteC2SRequestCommand.Decision.DENY;
                case UNKNOWN -> NetheriteC2SRequestCommand.Decision.UNKNOWN;
            };

            return new NetheriteC2SRequestCommand(netheriteDecision, cmd.targetRequestName);
        });
    }

    public NetheriteC2SCommand<?> toNetheriteCommand(AbstractC2SCommand<?> modernCommand) throws RuntimeException
    {
        var convertMethod = c2sFromModernToNetherite.getOrDefault(modernCommand.getBaseName(), null);

        if (convertMethod == null)
            throw new RuntimeException("No convert method found for input command %s".formatted(modernCommand.getBaseName()));

        return convertMethod.apply(modernCommand);
    }

    // Netherite -> Modern

    private final Map<String, Function<NetheriteC2SCommand<?>, AbstractC2SCommand<?>>> fromNetheriteToModernConverts = new ConcurrentHashMap<>();

    private <X extends NetheriteC2SCommand<?>> C2SCommandConverter registerNetheriteToModern(String cmdName,
                                                                                                Class<X> expectedClass,
                                                                                                Function<X, AbstractC2SCommand<?>> method)
    {
        Function<NetheriteC2SCommand<?>, AbstractC2SCommand<?>> mth = netheriteCommand ->
        {
            if (!expectedClass.isInstance(netheriteCommand))
            {
                throw new IllegalArgumentException("Input command '%s' is not expected type '%s'"
                        .formatted(netheriteCommand.getClass().getSimpleName(), expectedClass.getSimpleName()));
            }

            return method.apply((X) netheriteCommand);
        };

        fromNetheriteToModernConverts.put(cmdName, mth);

        return this;
    }

    protected void registerFromNetheriteToModernMethods()
    {
        this.registerNetheriteToModern(NetheriteC2SCommandNames.Animation, NetheriteC2SAnimationCommand.class, cmd ->
        {
            var animId = cmd.getAnimationId();
            return new C2SRequestAnimationCommand(animId);
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Initial, NetheriteC2SInitialCommand.class, cmd ->
        {
            return new C2SRequestInitialCommand();
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Morph, NetheriteC2SMorphCommand.class, cmd ->
        {
            var id = cmd.getArgumentAt(0, "");
            return new C2SMorphCommand(id, Collections.emptyMap());
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Option, NetheriteC2SOptionCommand.class, cmd ->
        {
            var option = cmd.getOption();
            var value = cmd.getValue();

            C2SSetSingleOptionCommand.ClientOptionEnum optionEnum = switch (option)
            {
                case CLIENTVIEW -> C2SSetSingleOptionCommand.ClientOptionEnum.CLIENTVIEW;
                case HUD -> C2SSetSingleOptionCommand.ClientOptionEnum.HUD;
            };

            return new C2SSetSingleOptionCommand(optionEnum, value);
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Request, NetheriteC2SRequestCommand.class, cmd ->
        {
            C2SExchangeRequestManagementCommand.Decision decision = switch (cmd.decision)
            {
                case ACCEPT -> C2SExchangeRequestManagementCommand.Decision.ACCEPT;
                case DENY -> C2SExchangeRequestManagementCommand.Decision.DENY;
                case UNKNOWN -> C2SExchangeRequestManagementCommand.Decision.UNKNOWN;
            };

            return new C2SExchangeRequestManagementCommand(decision, cmd.targetRequestName);
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Skill, NetheriteC2SSkillCommand.class, cmd ->
        {
            return new C2SActivateSkillCommand();
        }).registerNetheriteToModern(NetheriteC2SCommandNames.ToggleSelf, NetheriteC2SToggleSelfCommand.class, cmd ->
        {
            C2SToggleSelfCommand.SelfViewMode mode = switch (cmd.getSelfViewMode())
            {
                case ON -> C2SToggleSelfCommand.SelfViewMode.ON;
                case OFF -> C2SToggleSelfCommand.SelfViewMode.OFF;
                case CLIENT_ON -> C2SToggleSelfCommand.SelfViewMode.CLIENT_ON;
                case CLIENT_OFF -> C2SToggleSelfCommand.SelfViewMode.CLIENT_OFF;
            };

            return new C2SToggleSelfCommand(mode);
        }).registerNetheriteToModern(NetheriteC2SCommandNames.Unmorph, NetheriteC2SUnmorphCommand.class, cmd ->
        {
            return new C2SUnmorphCommand();
        });
    }

    public AbstractC2SCommand<?> fromNetheriteCommand(NetheriteC2SCommand<?> netheriteCommand) throws RuntimeException
    {
        var convertMethod = fromNetheriteToModernConverts.getOrDefault(netheriteCommand.getBaseName(), null);

        if (convertMethod == null)
            throw new RuntimeException("No convert method found for input command %s".formatted(netheriteCommand.getBaseName()));

        return convertMethod.apply(netheriteCommand);
    }

}
