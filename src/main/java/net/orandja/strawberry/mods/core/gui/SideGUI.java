package net.orandja.strawberry.mods.core.gui;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.orandja.chocoflavor.utils.Utils;

public interface SideGUI extends ExtraGui {

    enum TitlePosition {
        CENTER("centered"),
        LEFT("left");

        private final String text;

        TitlePosition(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    @Override
    default void begin(Object instance, MutableText newTitle, Text guiName) {
        Utils.apply(Text.literal("").formatted(Formatting.WHITE), it -> {
            switch(getTitlePosition()) {
                case CENTER -> ExtraGui.appendGUINegativeHalfOffset(it, guiName, getDefaultTranslationKey());
                case LEFT -> ExtraGui.appendGUINegativeOffset(it, guiName, getDefaultTranslationKey());
            }
            it.append(Text.translatable("sidecontainer." + getTitlePosition()));
        }, newTitle::append);
    }

    @Override
    default void end(Object instance, MutableText newTitle, Text guiName) {
        Utils.apply(Text.literal("").formatted(Formatting.WHITE), it -> {
            it.append(Text.translatable("sidecontainer." + getTitlePosition() + ".end"));
            switch(getTitlePosition()) {
                case CENTER -> ExtraGui.appendGUIPositiveHalfOffset(it, guiName, getDefaultTranslationKey());
                case LEFT -> ExtraGui.appendGUIPositiveOffset(it, guiName, getDefaultTranslationKey());
            }
        }, newTitle::append);
    }

    TitlePosition getTitlePosition();

    String getDefaultTranslationKey();
}
