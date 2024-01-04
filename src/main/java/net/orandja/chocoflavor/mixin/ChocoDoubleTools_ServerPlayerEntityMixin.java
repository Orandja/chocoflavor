package net.orandja.chocoflavor.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoDoubleTools;
import net.orandja.chocoflavor.tooltask.DoubleToolTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(ServerPlayerEntity.class)
public abstract class ChocoDoubleTools_ServerPlayerEntityMixin extends PlayerEntity implements ChocoDoubleTools.UserHandler {

    @Unique HashMap<Item, DoubleToolTask> toolModes = new HashMap<>();

    public ChocoDoubleTools_ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Override
    public HashMap<Item, DoubleToolTask> getTasks() {
        return toolModes;
    }

    @Override
    public void setToolTasks(HashMap<Item, DoubleToolTask> toolTasks) {
        this.toolModes = toolTasks;
    }
}
