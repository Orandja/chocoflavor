package net.orandja.strawberry;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.orandja.chocoflavor.ChocoChests;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.PlayerUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class StrawberryChestBlockUI {
    private StrawberryChestBlockUI() {}

    public interface WhitelistedChestUI extends StrawberryExtraUI.SideUI {

        @Override
        default boolean isEnabled(Object instance) {
            return instance instanceof ChocoChests.Handler handler && handler.hasWhitelist();
        }


        @Override
        default void content(Object instance, MutableText newTitle, Text guiName) {
            GlobalUtils.apply(Text.literal(""), it -> {
                StrawberryExtraUI.GlyphUtils.appendLiteralTo(it, 0, true, "Whitelist:");
                AtomicInteger lastOffset = new AtomicInteger();
                if(instance instanceof ChocoChests.Handler handler) {
                    AtomicInteger offset = new AtomicInteger(2);
                    handler.getWhitelist().stream().map(PlayerUtils::getUsernameFromUUID).forEach(name -> {
                        StrawberryExtraUI.GlyphUtils.appendTranslatableTo(it, offset.getAndIncrement(), true,
                                "container.list.symbol",
                                ScreenTexts.SPACE,
                                Text.literal(name)
                        );
                    });
                }
            }, newTitle::append);
        }
    }
}
