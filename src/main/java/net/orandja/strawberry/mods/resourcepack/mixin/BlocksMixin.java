package net.orandja.strawberry.mods.resourcepack.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", at = @At("TAIL"))
    private static void register(String id, Block block, CallbackInfoReturnable<Block> info) {
        if(block instanceof StrawberryBlockState strawberry) {
            strawberry.register();
            StrawberryResourcePackGenerator.save();
        }
    }

    @Inject(method = "register(Lnet/minecraft/util/Identifier;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", at = @At("TAIL"))
    private static void register(Identifier id, Block block, CallbackInfoReturnable<Block> info) {
        if(block instanceof StrawberryBlockState strawberry) {
            strawberry.register();
            StrawberryResourcePackGenerator.save();
        }
    }
}
