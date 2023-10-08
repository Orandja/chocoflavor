package net.orandja.chocoflavor.mods.deathmessage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface DeathMessage {

    LivingEntity getEntity();

    default void sendDeathPosition(CallbackInfoReturnable<Text> info) {
        if(info.getReturnValue() instanceof MutableText text) {
            BlockPos pos = getEntity().getBlockPos();
            Text.of(" ["+ pos.getX() +";"+ pos.getY() +";"+ pos.getZ() +"] in " + getEntity().getEntityWorld().getRegistryKey().getValue().toString())
                    .getWithStyle(Style.EMPTY.withColor(TextColor.parse("green")))
                    .forEach(text::append);
        }
    }
}
