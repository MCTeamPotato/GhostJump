package com.teampotato.ghostjump;

import com.finallion.graveyard.blocks.AbstractCoffinBlock;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

@Mod(GhostJump.ID)
@Mod.EventBusSubscriber()
public class GhostJump {
    public static final String ID = "ghostjump";

    public static final ForgeConfigSpec configSpec;
    public static final ForgeConfigSpec.IntValue chance;
    public static final ForgeConfigSpec.ConfigValue<String> entity;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> entityList;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("GhostJump");
        chance = builder.defineInRange("Chance", 7, 1, 100);
        entity = builder.define("SummonedEntity", "graveyard:reaper");
        entityList = builder
                .comment("If this list is not empty, the SummonedEntity will be useless and the entity to be summoned will be selected randomly in this pool")
                .defineList("SummonedEntitiesPool", Lists.newArrayList(), o -> o instanceof String);
        builder.pop();
        configSpec = builder.build();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        if (!(level.getBlockState(pos).getBlock() instanceof AbstractCoffinBlock<?>)) return;
        MinecraftServer server = level.getServer();
        if (server == null) return;
        if (RandomUtils.nextInt(0, 101) > chance.get()) return;
        List<? extends String> entities = entityList.get();
        if (!entities.isEmpty()) {
            server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "execute in " + level.dimension().location() + " run summon " + entities.get(RandomUtils.nextInt(0, entities.size())) + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ()
            );
        } else {
            server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "execute in " + level.dimension().location() + " run summon " + entity + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ()
            );
        }
    }

    public GhostJump() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
    }
}
