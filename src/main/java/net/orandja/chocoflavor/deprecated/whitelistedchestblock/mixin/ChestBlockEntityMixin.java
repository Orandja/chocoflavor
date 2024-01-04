//package net.orandja.chocoflavor.mods.whitelistedchestblock.mixin;
//
//import com.google.common.collect.Lists;
//import lombok.Getter;
//import net.minecraft.block.entity.ChestBlockEntity;
//import net.minecraft.nbt.NbtCompound;
//import net.orandja.chocoflavor.mods.whitelistedchestblock.WhitelistedChestBlock;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.List;
//
//@SuppressWarnings("unused")
//@Mixin(ChestBlockEntity.class)
//public abstract class ChestBlockEntityMixin implements WhitelistedChestBlock {
//
//    @Getter List<String> whitelist = Lists.newArrayList();
//
//    @Inject(method = "readNbt", at = @At("RETURN"))
//    void readNbt(NbtCompound nbt, CallbackInfo info) {
//        loadWhitelist(nbt);
//    }
//
//    @Inject(method = "writeNbt", at = @At("RETURN"))
//    void writeNbt(NbtCompound nbt, CallbackInfo info) {
//        saveWhitelist(nbt);
//    }
//}
