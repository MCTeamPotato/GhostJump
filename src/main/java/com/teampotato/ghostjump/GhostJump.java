package com.teampotato.ghostjump;

import com.finallion.graveyard.blocks.AbstractCoffinBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.concurrent.ThreadLocalRandom;

@Mod(GhostJump.ID)
public class GhostJump {
    public static final String ID = "ghostjump";

    public static final ForgeConfigSpec configSpec;
    public static final ForgeConfigSpec.IntValue chance;
    public static final ForgeConfigSpec.ConfigValue<String> entity;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("GhostJump");
        chance = builder.defineInRange("Chance", 7, 1, 100);
        entity = builder.define("SummonedEntity", "graveyard:reaper");
        builder.pop();
        configSpec = builder.build();
    }

    private static CommandSourceStack silentCommandSourceStack = null;

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        if (!(level.getBlockState(pos).getBlock() instanceof AbstractCoffinBlock<?>)) return;
        MinecraftServer server = level.getServer();
        if (server == null) return;
        if (ThreadLocalRandom.current().nextInt(0, 101) > chance.get()) return;
        if (silentCommandSourceStack == null) silentCommandSourceStack = server.createCommandSourceStack().withSuppressedOutput();
        server.getCommands().performPrefixedCommand(silentCommandSourceStack, "execute in " + level.dimension().location() + " run summon " + entity.get() + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ());
    }

    public GhostJump() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRightClickBlock);
    }
}
