package net.orandja.strawberry.mixin;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.StrawberryExtraUI;
import net.orandja.strawberry.intf.StrawberryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("LombokGetterMayBeUsed")
@Mixin(ServerPlayerEntity.class)
public abstract class StrawberryExtraUI_ServerPlayerEntityMixin implements StrawberryPlayer {

    @Redirect(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/NamedScreenHandlerFactory;getDisplayName()Lnet/minecraft/text/Text;"))
    public Text addExtraGUI(NamedScreenHandlerFactory instance) {
        if(instance instanceof StrawberryExtraUI.SideUI extraGui && extraGui.isEnabled(instance) && instance.getDisplayName() instanceof MutableText baseName) {
            return GlobalUtils.apply(instance.getDisplayName().copy(), it -> {
                extraGui.begin(instance, it, baseName);
                extraGui.content(instance, it, baseName);
                extraGui.end(instance, it, baseName);
            });
        }
        return instance.getDisplayName();
    }
}
