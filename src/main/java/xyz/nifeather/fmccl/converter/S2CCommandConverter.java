package xyz.nifeather.fmccl.converter;

import xyz.nifeather.fmccl.network.commands.S2C.*;
import xyz.nifeather.fmccl.network.commands.S2C.clientrender.*;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapClearCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CMapRemoveCommand;
import xyz.nifeather.fmccl.network.commands.S2C.map.NetheriteS2CPartialMapCommand;
import xyz.nifeather.fmccl.network.commands.S2C.query.NetheriteQueryType;
import xyz.nifeather.fmccl.network.commands.S2C.query.NetheriteS2CQueryCommand;
import xyz.nifeather.fmccl.network.commands.S2C.set.*;
import xyz.nifeather.morph.network.commands.S2C.*;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CAddAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CClearAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CRemoveAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CSyncAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.clientrender.*;
import xyz.nifeather.morph.network.commands.S2C.query.QueryType;
import xyz.nifeather.morph.network.commands.S2C.query.S2CQueryCommand;
import xyz.nifeather.morph.network.commands.S2C.set.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class S2CCommandConverter
{
    public S2CCommandConverter()
    {
        registerFromModernToNetherite();
        registerFromNetheriteToModern();

        registerFakeEquipCommandConversions();
    }

    protected abstract void registerFakeEquipCommandConversions();

    private final Map<String, Function<AbstractS2CCommand<?>, NetheriteS2CCommand<?>>> fromModernToNetherite = new ConcurrentHashMap<>();

    public <X extends AbstractS2CCommand<?>> S2CCommandConverter registerModernToNetherite(String cmdName,
                                                                                           Class<X> expectedClass,
                                                                                           Function<X, NetheriteS2CCommand<?>> method)
    {
        //       Input                  Output
        Function<AbstractS2CCommand<?>, NetheriteS2CCommand<?>> mth = modernCommand ->
        {
            if (!expectedClass.isInstance(modernCommand))
            {
                throw new IllegalArgumentException("Input command '%s' is not expected type '%s'"
                        .formatted(modernCommand.getClass().getSimpleName(), expectedClass.getSimpleName()));
            }

            return method.apply((X) modernCommand);
        };

        fromModernToNetherite.put(cmdName, mth);
        return this;
    }

    protected void registerFromModernToNetherite()
    {
        this.registerModernToNetherite(S2CCommandNames.Current, S2CCurrentCommand.class, cmd ->
        {
            return new NetheriteS2CCurrentCommand(cmd.getDisguiseIdentifier());
        }).registerModernToNetherite(S2CCommandNames.ReAuth, S2CReAuthCommand.class, cmd ->
        {
            return new NetheriteS2CReAuthCommand();
        }).registerModernToNetherite(S2CCommandNames.UnAuth, S2CUnAuthCommand.class, cmd ->
        {
            return new NetheriteS2CUnAuthCommand();
        }).registerModernToNetherite(S2CCommandNames.SwapHands, S2CSwapCommand.class, cmd ->
        {
            return new NetheriteS2CSwapCommand();
        }).registerModernToNetherite(S2CCommandNames.Query, S2CQueryCommand.class, cmd ->
        {
            var type = cmd.queryType();
            var diff = cmd.getDiff();

            NetheriteQueryType netheriteType = switch (type)
            {
                case UNKNOWN -> NetheriteQueryType.UNKNOWN;
                case ADD -> NetheriteQueryType.ADD;
                case REMOVE -> NetheriteQueryType.REMOVE;
                case SET -> NetheriteQueryType.SET;
            };

            return new NetheriteS2CQueryCommand(netheriteType, diff.toArray(String[]::new));
        }).registerModernToNetherite(S2CCommandNames.Request, S2CRequestCommand.class, cmd ->
        {
            var requestType = cmd.type;
            var targetName = cmd.sourcePlayer;

            NetheriteS2CRequestCommand.NetheriteRequestType netheriteRequestType = switch (requestType)
            {
                case Unknown -> NetheriteS2CRequestCommand.NetheriteRequestType.Unknown;

                case NewRequest -> NetheriteS2CRequestCommand.NetheriteRequestType.NewRequest;

                case RequestSend -> NetheriteS2CRequestCommand.NetheriteRequestType.RequestSend;
                case RequestExpired -> NetheriteS2CRequestCommand.NetheriteRequestType.RequestExpired;
                case RequestExpiredOwner -> NetheriteS2CRequestCommand.NetheriteRequestType.RequestExpiredOwner;

                case RequestAccepted -> NetheriteS2CRequestCommand.NetheriteRequestType.RequestAccepted;
                case RequestDenied -> NetheriteS2CRequestCommand.NetheriteRequestType.RequestDenied;
            };

            return new NetheriteS2CRequestCommand(netheriteRequestType, targetName);
        }).registerModernToNetherite(S2CCommandNames.SetAggressive, S2CSetAggressiveCommand.class, cmd ->
        {
            return new NetheriteS2CSetAggressiveCommand(cmd.val);
        }).registerModernToNetherite(S2CCommandNames.SetProfile, S2CSetProfileCommand.class, cmd ->
        {
            return new NetheriteS2CSetProfileCommand(cmd.getProfileSNbt());
        }).registerModernToNetherite(S2CCommandNames.SetSelfViewIdentifier, S2CSetSelfViewIdentifierCommand.class, cmd ->
        {
            return new NetheriteS2CSetSelfViewIdentifierCommand(cmd.getIdentifier());
        }).registerModernToNetherite(S2CCommandNames.SetSkillCooldown, S2CSetSkillCooldownCommand.class, cmd ->
        {
            return new NetheriteS2CSetSkillCooldownCommand(cmd.val);
        }).registerModernToNetherite(S2CCommandNames.SetSNbt, S2CSetSNbtCommand.class, cmd ->
        {
            return new NetheriteS2CSetSNbtCommand(cmd.getSNbt());
        }).registerModernToNetherite(S2CCommandNames.SetSneaking, S2CSetSneakingCommand.class, cmd ->
        {
            return new NetheriteS2CSetSneakingCommand(cmd.sneaking);
        }).registerModernToNetherite(S2CCommandNames.SetSelfViewing, S2CSetSelfViewingStatusCommand.class, cmd ->
        {
            return new NetheriteS2CSetSelfViewingCommand(cmd.selfViewing());
        }).registerModernToNetherite(S2CCommandNames.SetModifyBoundingBox, S2CSetModifyBoundingBoxCommand.class, cmd ->
        {
            return new NetheriteS2CSetModifyBoundingBoxCommand(cmd.getModifyBoundingBox());
        }).registerModernToNetherite(S2CCommandNames.SetRevealing, S2CSetMobRevealingCommand.class, cmd ->
        {
            return new NetheriteS2CSetRevealingCommand(cmd.getValue());
        }).registerModernToNetherite(S2CCommandNames.SetAvailableAnimations, S2CSetAvailableAnimationsCommand.class, cmd ->
        {
            return new NetheriteS2CSetAvailableAnimationsCommand(cmd.getAvailableAnimations());
        }).registerModernToNetherite(S2CCommandNames.SetAnimationDisplayName, S2CSetAnimationDisplayNameCommand.class, cmd ->
        {
            return new NetheriteS2CSetAnimationDisplayNameCommand(cmd.getDisplayIdentifier());
        }).registerModernToNetherite(S2CCommandNames.SetDisplayingFakeEquip, S2CSetDisplayingFakeEquipCommand.class, cmd ->
        {
            return new NetheriteS2CSetDisplayingFakeEquipCommand(cmd.displaying);
        });

        // Admin reveal
        this.registerModernToNetherite(S2CCommandNames.SetReveal, S2CSyncAdminRevealCommand.class, cmd ->
        {
            return new NetheriteS2CMapCommand(cmd.getMap());
        }).registerModernToNetherite(S2CCommandNames.AddReveal, S2CAddAdminRevealCommand.class, cmd ->
        {
            return new NetheriteS2CPartialMapCommand(cmd.getMap());
        }).registerModernToNetherite(S2CCommandNames.RemoveReveal, S2CRemoveAdminRevealCommand.class, cmd ->
        {
            return new NetheriteS2CMapRemoveCommand(cmd.getTargetId());
        }).registerModernToNetherite(S2CCommandNames.ClearReveal, S2CClearAdminRevealCommand.class, cmd ->
        {
            return new NetheriteS2CMapClearCommand();
        });

        // Animation

        this.registerModernToNetherite(S2CCommandNames.Animation, S2CAnimationCommand.class, cmd ->
        {
            return new NetheriteS2CAnimationCommand(cmd.getAnimId());
        });

        // Client Renderer

        this.registerModernToNetherite(S2CCommandNames.CRSyncRender, S2CCRSyncRegisterCommand.class, cmd ->
        {
            return new NetheriteS2CRenderMapSyncCommand(cmd.getMap());
        }).registerModernToNetherite(S2CCommandNames.CRAdd, S2CCRRegisterCommand.class, cmd ->
        {
            return new NetheriteS2CRenderMapAddCommand(cmd.getPlayerNetworkId(), cmd.getMobId());
        }).registerModernToNetherite(S2CCommandNames.CRRemove, S2CCRUnregisterCommand.class, cmd ->
        {
            return new NetheriteS2CRenderMapRemoveCommand(cmd.getPlayerNetworkId());
        }).registerModernToNetherite(S2CCommandNames.CRClear, S2CCRClearCommand.class, cmd ->
        {
            return new NetheriteS2CRenderMapClearCommand();
        }).registerModernToNetherite(S2CCommandNames.CRMeta, S2CCRSetMetaCommand.class, cmd ->
        {
            var modernMeta = cmd.renderMeta;

            NetheriteS2CRenderMeta netheriteS2CRenderMeta = new NetheriteS2CRenderMeta(modernMeta.networkId);
            netheriteS2CRenderMeta.sNbt = modernMeta.sNbt;
            netheriteS2CRenderMeta.profileCompound = modernMeta.profileCompound;
            netheriteS2CRenderMeta.showOverridedEquipment = modernMeta.showOverridedEquipment;

            var modernEquipment = modernMeta.overridedEquipment;

            // 我们可能在未来修改新协议的东西，所以先这样？
            if (modernEquipment != null)
            {
                var netheriteEquipment = new NetheriteEquipment();

                netheriteEquipment.headId = modernEquipment.headId;
                netheriteEquipment.headNbt = modernEquipment.headNbt;

                netheriteEquipment.chestId = modernEquipment.chestId;
                netheriteEquipment.chestNbt = modernEquipment.chestNbt;

                netheriteEquipment.leggingId = modernEquipment.leggingId;
                netheriteEquipment.leggingNbt = modernEquipment.leggingNbt;

                netheriteEquipment.feetId = modernEquipment.feetId;
                netheriteEquipment.feetNbt = modernEquipment.feetNbt;

                netheriteEquipment.handId = modernEquipment.handId;
                netheriteEquipment.handNbt = modernEquipment.handNbt;

                netheriteEquipment.offhandId = modernEquipment.offhandId;
                netheriteEquipment.offhandNbt = modernEquipment.offhandNbt;

                netheriteS2CRenderMeta.overridedEquipment = netheriteEquipment;
            }

            return new NetheriteS2CRenderMapMetaCommand(netheriteS2CRenderMeta);
        });
    }

    public NetheriteS2CCommand<?> toNetheriteCommand(AbstractS2CCommand<?> modernCommand) throws RuntimeException
    {
        var convertMethod = fromModernToNetherite.getOrDefault(modernCommand.getBaseName(), null);

        if (convertMethod == null)
            throw new RuntimeException("No convert method found for input command %s".formatted(modernCommand.getBaseName()));

        return convertMethod.apply(modernCommand);
    }

    private final Map<String, Function<NetheriteS2CCommand<?>, AbstractS2CCommand<?>>> fromNetheriteToModern = new ConcurrentHashMap<>();

    public <X extends NetheriteS2CCommand<?>> S2CCommandConverter registerNetheriteToModern(String cmdName,
                                                                                            Class<X> expectedClass,
                                                                                            Function<X, AbstractS2CCommand<?>> method)
    {
        //       Input                  Output
        Function<NetheriteS2CCommand<?>, AbstractS2CCommand<?>> mth = modernCommand ->
        {
            if (!expectedClass.isInstance(modernCommand))
            {
                throw new IllegalArgumentException("Input command '%s' is not expected type '%s'"
                        .formatted(modernCommand.getClass().getSimpleName(), expectedClass.getSimpleName()));
            }

            return method.apply((X) modernCommand);
        };

        fromNetheriteToModern.put(cmdName, mth);
        return this;
    }

    protected void registerFromNetheriteToModern()
    {
        this.registerNetheriteToModern(NetheriteS2CCommandNames.Current, NetheriteS2CCurrentCommand.class, cmd ->
        {
            return new S2CCurrentCommand(cmd.getDisguiseIdentifier());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.ReAuth, NetheriteS2CReAuthCommand.class, cmd ->
        {
            return new S2CReAuthCommand();
        }).registerNetheriteToModern(NetheriteS2CCommandNames.UnAuth, NetheriteS2CUnAuthCommand.class, cmd ->
        {
            return new S2CUnAuthCommand();
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SwapHands, NetheriteS2CSwapCommand.class, cmd ->
        {
            return new S2CSwapCommand();
        }).registerNetheriteToModern(NetheriteS2CCommandNames.Query, NetheriteS2CQueryCommand.class, cmd ->
        {
            var netheriteQueryType = cmd.queryType();
            var diff = cmd.getDiff();

            QueryType netheriteType = switch (netheriteQueryType)
            {
                case UNKNOWN -> QueryType.UNKNOWN;
                case ADD -> QueryType.ADD;
                case REMOVE -> QueryType.REMOVE;
                case SET -> QueryType.SET;
            };

            return new S2CQueryCommand(netheriteType, diff);
        }).registerNetheriteToModern(NetheriteS2CCommandNames.Request, NetheriteS2CRequestCommand.class, cmd ->
        {
            var netheriteRequestType = cmd.netheriteRequestType;
            var targetName = cmd.sourcePlayer;

            S2CRequestCommand.Type requestType = switch (netheriteRequestType)
            {
                case Unknown -> S2CRequestCommand.Type.Unknown;

                case NewRequest -> S2CRequestCommand.Type.NewRequest;

                case RequestSend -> S2CRequestCommand.Type.RequestSend;
                case RequestExpired -> S2CRequestCommand.Type.RequestExpired;
                case RequestExpiredOwner -> S2CRequestCommand.Type.RequestExpiredOwner;

                case RequestAccepted -> S2CRequestCommand.Type.RequestAccepted;
                case RequestDenied -> S2CRequestCommand.Type.RequestDenied;
            };

            return new S2CRequestCommand(requestType, targetName);
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetAggressive, NetheriteS2CSetAggressiveCommand.class, cmd ->
        {
            return new S2CSetAggressiveCommand(cmd.getArgumentAt(0, false));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetProfile, NetheriteS2CSetProfileCommand.class, cmd ->
        {
            return new S2CSetProfileCommand(cmd.getArgumentAt(0, "{}"));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetSelfViewIdentifier, NetheriteS2CSetSelfViewIdentifierCommand.class, cmd ->
        {
            return new S2CSetSelfViewIdentifierCommand(cmd.getArgumentAt(0));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetSkillCooldown, NetheriteS2CSetSkillCooldownCommand.class, cmd ->
        {
            return new S2CSetSkillCooldownCommand(cmd.getArgumentAt(0, 0L));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetSNbt, NetheriteS2CSetSNbtCommand.class, cmd ->
        {
            return new S2CSetSNbtCommand(cmd.getArgumentAt(0, "{}"));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetSneaking, NetheriteS2CSetSneakingCommand.class, cmd ->
        {
            return new S2CSetSneakingCommand(cmd.getArgumentAt(0, false));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetSelfViewing, NetheriteS2CSetSelfViewingCommand.class, cmd ->
        {
            return new S2CSetSelfViewingStatusCommand(cmd.getArgumentAt(0, false));
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetModifyBoundingBox, NetheriteS2CSetModifyBoundingBoxCommand.class, cmd ->
        {
            return new S2CSetModifyBoundingBoxCommand(cmd.getModifyBoundingBox());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetRevealing, NetheriteS2CSetRevealingCommand.class, cmd ->
        {
            return new S2CSetMobRevealingCommand(cmd.getValue());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetAvailableAnimations, NetheriteS2CSetAvailableAnimationsCommand.class, cmd ->
        {
            return new S2CSetAvailableAnimationsCommand(cmd.getAvailableAnimations());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetAnimationDisplayName, NetheriteS2CSetAnimationDisplayNameCommand.class, cmd ->
        {
            return new S2CSetAnimationDisplayNameCommand(cmd.getDisplayIdentifier());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.SetDisplayingFakeEquip, NetheriteS2CSetDisplayingFakeEquipCommand.class, cmd ->
        {
            return new S2CSetDisplayingFakeEquipCommand(cmd.getArgumentAt(0, false));
        });

        // Admin reveal
        this.registerNetheriteToModern(NetheriteS2CCommandNames.Map, NetheriteS2CMapCommand.class, cmd ->
        {
            return new S2CSyncAdminRevealCommand(cmd.getMap());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.MapPartial, NetheriteS2CPartialMapCommand.class, cmd ->
        {
            return new S2CAddAdminRevealCommand(cmd.getMap());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.MapRemove, NetheriteS2CMapRemoveCommand.class, cmd ->
        {
            return new S2CRemoveAdminRevealCommand(cmd.getTargetId());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.MapClear, NetheriteS2CMapClearCommand.class, cmd ->
        {
            return new S2CClearAdminRevealCommand();
        });

        // Animation

        this.registerNetheriteToModern(NetheriteS2CCommandNames.Animation, NetheriteS2CAnimationCommand.class, cmd ->
        {
            return new S2CAnimationCommand(cmd.getAnimId());
        });

        // Client Renderer

        this.registerNetheriteToModern(NetheriteS2CCommandNames.CRMap, NetheriteS2CRenderMapSyncCommand.class, cmd ->
        {
            return new S2CCRSyncRegisterCommand(cmd.getMap());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.CRAdd, NetheriteS2CRenderMapAddCommand.class, cmd ->
        {
            return new S2CCRRegisterCommand(cmd.getPlayerNetworkId(), cmd.getMobId());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.CRRemove, NetheriteS2CRenderMapRemoveCommand.class, cmd ->
        {
            return new S2CCRUnregisterCommand(cmd.getPlayerNetworkId());
        }).registerNetheriteToModern(NetheriteS2CCommandNames.CRClear, NetheriteS2CRenderMapClearCommand.class, cmd ->
        {
            return new S2CCRClearCommand();
        }).registerNetheriteToModern(NetheriteS2CCommandNames.CRMeta, NetheriteS2CRenderMapMetaCommand.class, cmd ->
        {
            var netheriteMeta = cmd.getArgumentAt(0);

            S2CRenderMeta modernMeta = new S2CRenderMeta(netheriteMeta.networkId);
            modernMeta.sNbt = netheriteMeta.sNbt;
            modernMeta.profileCompound = netheriteMeta.profileCompound;
            modernMeta.showOverridedEquipment = netheriteMeta.showOverridedEquipment;

            var netheriteEquipment = netheriteMeta.overridedEquipment;

            // 我们可能在未来修改新协议的东西，所以先这样？
            if (netheriteEquipment != null)
            {
                var modernEquipment = new Equipment();

                modernEquipment.headId = netheriteEquipment.headId;
                modernEquipment.headNbt = netheriteEquipment.headNbt;

                modernEquipment.chestId = netheriteEquipment.chestId;
                modernEquipment.chestNbt = netheriteEquipment.chestNbt;

                modernEquipment.leggingId = netheriteEquipment.leggingId;
                modernEquipment.leggingNbt = netheriteEquipment.leggingNbt;

                modernEquipment.feetId = netheriteEquipment.feetId;
                modernEquipment.feetNbt = netheriteEquipment.feetNbt;

                modernEquipment.handId = netheriteEquipment.handId;
                modernEquipment.handNbt = netheriteEquipment.handNbt;

                modernEquipment.offhandId = netheriteEquipment.offhandId;
                modernEquipment.offhandNbt = netheriteEquipment.offhandNbt;

                modernMeta.overridedEquipment = modernEquipment;
            }

            return new S2CCRSetMetaCommand(modernMeta);
        });
    }

    public AbstractS2CCommand<?> fromNetheriteCommand(NetheriteS2CCommand<?> netheriteCommand) throws RuntimeException
    {
        var convertMethod = fromNetheriteToModern.getOrDefault(netheriteCommand.getBaseName(), null);

        if (convertMethod == null)
            throw new RuntimeException("No convert method found for input command %s".formatted(netheriteCommand.getBaseName()));

        return convertMethod.apply(netheriteCommand);
    }
}
