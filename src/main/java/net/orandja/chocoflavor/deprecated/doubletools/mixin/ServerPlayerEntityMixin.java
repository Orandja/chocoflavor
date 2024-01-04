//package net.orandja.chocoflavor.mods.doubletools.mixin;
//
//import com.mojang.authlib.GameProfile;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
//import net.orandja.chocoflavor.mods.doubletools.ToolTask;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//
//import java.util.HashMap;
//
//@Mixin(ServerPlayerEntity.class)
//public abstract class ServerPlayerEntityMixin extends PlayerEntity implements DoubleTools.ToolUser {
//
//    @Unique HashMap<Item, ToolTask> toolModes = new HashMap<>();
//
//    public ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
//        super(world, blockPos, f, gameProfile);
//    }
//
//    @Override
//    public HashMap<Item, ToolTask> getToolTasks() {
//        return toolModes;
//    }
//
//    @Override
//    public void setToolTasks(HashMap<Item, ToolTask> toolTasks) {
//        this.toolModes = toolTasks;
//    }
//}
