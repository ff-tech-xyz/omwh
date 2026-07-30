package com.omwh.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.omwh.OMWH;
import com.omwh.config.ConfigManager;
import com.omwh.utils.HomeRespawnDecision;
import com.omwh.utils.MountedHomeFallback;
import com.omwh.utils.TeleportVehicles;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class HomeCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
       String alias = ConfigManager.get().homeCommand;

       var base = Commands.literal(alias)
               .requires(source -> source.getPlayer() != null)
               .executes(ctx -> executeWrapped(ctx));

       dispatcher.register(base);
   }

   private static int executeWrapped(CommandContext<CommandSourceStack> context) {
       ServerPlayer player = context.getSource().getPlayer();
       if (player == null) return 0;
       try {
           return executeHomeCommand(player) ? 1 : 0;
       } catch (Throwable t) {
           OMWH.MESSAGE_UTILS.sendMessage(player, "§cInternal error executing /home. Check server log.");
           org.slf4j.LoggerFactory.getLogger("omwh").error("Error executing /home", t);
           return 0;
       }
   }

   private static boolean executeHomeCommand(ServerPlayer player) {
       var cfg = ConfigManager.get();

       if (OMWH.COOLDOWN_MANAGER.shouldBlockTeleport(player)) {
           if (OMWH.COOLDOWN_MANAGER.isInPvpCooldown(player)) {
               OMWH.MESSAGE_UTILS.sendMessage(player, cfg.pvpCooldownMessage.replace("{time}", String.valueOf(OMWH.COOLDOWN_MANAGER.getRemainingPvpCooldown(player))));
           } else if (OMWH.COOLDOWN_MANAGER.isInDamageCooldown(player)) {
               OMWH.MESSAGE_UTILS.sendMessage(player, cfg.damageCooldownMessage.replace("{time}", String.valueOf(OMWH.COOLDOWN_MANAGER.getRemainingDamageCooldown(player))));
           } else if (OMWH.COOLDOWN_MANAGER.isInJoinCooldown(player)) {
               OMWH.MESSAGE_UTILS.sendMessage(player, cfg.joinCooldownMessage.replace("{time}", String.valueOf(OMWH.COOLDOWN_MANAGER.getRemainingJoinCooldown(player))));
           } else if (OMWH.COOLDOWN_MANAGER.isInRegularCooldown(player)) {
               OMWH.MESSAGE_UTILS.sendMessage(player, cfg.regularCooldownMessage.replace("{time}", String.valueOf(OMWH.COOLDOWN_MANAGER.getRemainingRegularCooldown(player))));
           }
           return false;
       }

        var respawnConfig = player.getRespawnConfig();
        boolean hasRespawnConfig = respawnConfig != null;
        if (!hasRespawnConfig) {
            OMWH.MESSAGE_UTILS.sendMessage(player, cfg.noHomepointMessage);
            return false;
        }

        // Ask vanilla for the same bed/anchor/forced-respawn destination used by death respawning.
        // false is intentional: /home must not consume a respawn-anchor charge.
        TeleportTransition respawn = player.findRespawnPositionAndUseSpawnBlock(
                false, TeleportTransition.DO_NOTHING);
        ServerLevel currentLevel = (ServerLevel) player.level();
        ServerLevel targetLevel = respawn.newLevel();
        var root = player.getRootVehicle();
        boolean mounted = root != player;
        boolean missingRespawnBlock = respawn.missingRespawnBlock();
        boolean sameDimension = currentLevel.dimension().equals(targetLevel.dimension());
        BlockPos homeBlock = respawnConfig.respawnData().pos();
        boolean canCheckVehicle = mounted && !missingRespawnBlock && sameDimension;
        boolean exactFits = !canCheckVehicle
                || TeleportVehicles.hasExactRoom(root, targetLevel, respawn.position(), homeBlock);
        boolean homeIsBed = canCheckVehicle
                && targetLevel.getBlockState(homeBlock).getBlock() instanceof BedBlock;
        Vec3 fallbackPosition = null;
        boolean fallbackFits = false;
        if (!exactFits && homeIsBed && !respawnConfig.forced()) {
            MountedHomeFallback.Position fallback = MountedHomeFallback.aboveBed(
                    homeBlock.getX(), homeBlock.getY(), homeBlock.getZ());
            fallbackPosition = new Vec3(fallback.x(), fallback.y(), fallback.z());
            fallbackFits = TeleportVehicles.hasExactRoom(
                    root, targetLevel, fallbackPosition, homeBlock);
        }
        MountedHomeFallback.Choice destinationChoice = MountedHomeFallback.choose(
                mounted, homeIsBed, respawnConfig.forced(), exactFits, fallbackFits);
        boolean rootFits = destinationChoice != MountedHomeFallback.Choice.DENY;

        HomeRespawnDecision.Outcome decision = HomeRespawnDecision.decide(
                hasRespawnConfig, missingRespawnBlock, sameDimension, mounted, rootFits);
        if (decision == HomeRespawnDecision.Outcome.NO_HOME) {
            OMWH.MESSAGE_UTILS.sendMessage(player, cfg.noHomepointMessage);
            return false;
        }
        if (decision == HomeRespawnDecision.Outcome.CROSS_DIMENSION) {
            OMWH.MESSAGE_UTILS.sendMessage(player, cfg.crossDimensionMessage);
            return false;
        }
        if (decision == HomeRespawnDecision.Outcome.VEHICLE_TOO_BIG) {
            OMWH.MESSAGE_UTILS.sendMessage(player,
                    "§cYour vehicle is too big. Please dismount and try again.");
            return false;
        }

        OMWH.EFFECTS_MANAGER.playTeleportEffects(player);
        Vec3 targetPosition = destinationChoice == MountedHomeFallback.Choice.ABOVE_BED
                ? fallbackPosition : respawn.position();
        float targetYaw = mounted ? root.getYRot() : respawn.yRot();
        float targetPitch = mounted ? root.getXRot() : respawn.xRot();
        TeleportVehicles.Result result = TeleportVehicles.teleportWithMount(
                player, targetLevel, targetPosition, targetYaw, targetPitch);
       boolean teleportSuccessful = result.success;
       java.util.List<ServerPlayer> passengerPlayers = result.passengerPlayers;

       if (!teleportSuccessful) {
           OMWH.MESSAGE_UTILS.sendMessage(player, cfg.unsafeHomeMessage);
           return false;
       }

       OMWH.COOLDOWN_MANAGER.setRegularCooldown(player);
       OMWH.MESSAGE_UTILS.sendMessage(player, cfg.homeSuccessMessage);

       if (!passengerPlayers.isEmpty()) {
           for (ServerPlayer p : passengerPlayers) {
               OMWH.MESSAGE_UTILS.sendMessage(p, "§e" + player.getName().getString() + " teleported you with their vehicle to their home.");
           }
       }

       return true;
   }
}

