package net.orandja.strawberry;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.orandja.chocoflavor.utils.GlobalUtils;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StrawberryExtraUI {
    private StrawberryExtraUI() {

    }

    public static class GlyphUtils {

        private static final int MAXIMUM_OFFSET = 200;
        private static final Map<Character, Character[]> charMap = new HashMap<>() {{
            try {
                JsonParser.parseString(
                        String.join("", Files.readAllLines(FabricLoader.getInstance().getModContainer("chocoflavor").get().findPath("charmapper.json").get()))
                ).getAsJsonObject().entrySet().forEach(entry -> {
                    JsonArray values = entry.getValue().getAsJsonArray();
                    this.put(entry.getKey().charAt(0), GlobalUtils.apply(new Character[values.size()], arr -> {
                        for(int i = 0; i < values.size(); i++) {
                            arr[i] = values.get(i).getAsString().charAt(0);
                        }
                    }));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }};


        private static final Map<Integer, Integer> widthMap = new HashMap<>() {{
            try {
                JsonParser.parseString(
                        String.join("", Files.readAllLines(FabricLoader.getInstance().getModContainer("chocoflavor").get().findPath("charwidth.json").get()))
                ).getAsJsonArray().forEach(element -> {
                    element.getAsJsonObject().entrySet().forEach(entry -> {
                        this.put((int) entry.getKey().toCharArray()[0], entry.getValue().getAsInt());
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }};

        public static int getWidth(String input) {
            int width = 0;

            for (char c : input.toCharArray()) {
                width += widthMap.getOrDefault((int)c, 0);
            }

            if(width > 200) {
                GlobalUtils.log(input);
                try {
                    throw new Error("das");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            return width;
        }

        public static void getWidth(String input, Consumer<Integer> consumer) {
            int width = getWidth(input);
            if(width > 0) {
                consumer.accept(width);
            }
        }

        public static String convertString(String input, int offset) {
            if(offset == 0) {
                return input;
            }

            StringBuilder output = new StringBuilder();

            for (char c : input.toCharArray()) {
                if(c == ' ') {
                    output.append(' ');
                    continue;
                }
                output.append(charMap.get(c)[offset - 1]);
            }

            return output.toString();
        }

        public static <T extends MutableText> T appendTranslatableTo(T mainText, int offset, boolean negate, Object... translatables) {
            for (int i = 0; i < translatables.length; i++) {
                if(translatables[i] instanceof MutableText mutableText) {
                    if (mutableText.getContent() instanceof LiteralTextContent content) {
                        mainText.append(convertString(content.string(), offset));
                    } else {
                        mainText.append(mutableText);
                    }
                    continue;
                }
                mainText.append(Text.translatable(translatables[i] + (offset > 0 ? ".offset" + offset: "")));
            }

            if(negate) {
                for (Object translatable : translatables) {
                    if(translatable.equals(ScreenTexts.SPACE)) {
                        mainText.append(Text.translatable("negative.6"));
                        continue;
                    }

                    if(translatable instanceof MutableText mutableText) {
                        if (mutableText.getContent() instanceof LiteralTextContent content) {
                            mainText.append(negateLiteral(content.string()));
                        }
                        continue;
                    }


                    mainText.append(Text.translatable(translatable + ".offset_neg"));
                }
            }
            return mainText;
        }

        public static Text negateLiteral(String input) {
            int width = getWidth(input);
            width += 2;
            MutableText out = Text.literal("");
            while(width > 0) {
                int subAmount = Math.min(width, MAXIMUM_OFFSET);
                out.append(Text.translatable("negative." + subAmount));
                width -= subAmount;
            }
            return out;
        }

        public static <T extends MutableText> T appendLiteralTo(T mainText, int offset, boolean negate, String... literals) {
            for (int i = 0; i < literals.length; i++) {
                mainText.append(Text.literal(convertString(literals[i], offset)));
            }

            if(negate) {
                for (int i = 0; i < literals.length; i++) {
                    mainText.append(negateLiteral(literals[i]));
                }
            }
            return mainText;
        }

        public static <T extends MutableText> T appendGUIPositiveHalfOffset(T mainText, Text guiName, String translationKey) {
            if(TranslatableTextContent.class.isAssignableFrom(guiName.getContent().getClass())) {
                mainText.append(Text.translatable(translationKey + ".offset_pos_half"));
            } else {
                getWidth(guiName.getString(), width -> mainText.append(Text.translatable("positive." + (width / 2))));
            }

            return mainText;
        }

        public static <T extends MutableText> T appendGUINegativeHalfOffset(T mainText, Text guiName, String translationKey) {
            if(TranslatableTextContent.class.isAssignableFrom(guiName.getContent().getClass())) {
                mainText.append(Text.translatable(translationKey + ".offset_neg_half"));
            } else {
                getWidth(guiName.getString(), width -> mainText.append(Text.translatable("negative." + (width / 2))));
            }

            return mainText;
        }

        public static <T extends MutableText> T appendGUINegativeHalfOffset(T mainText, Text literal) {
            getWidth(literal.getString(), width -> mainText.append(Text.translatable("negative." + (width / 2))));

            return mainText;
        }

        public static <T extends MutableText> T appendGUINegativeOffset(T mainText, Text literal) {
            getWidth(literal.getString(), width -> mainText.append(Text.translatable("negative." + width)));
            return mainText;
        }

        public static <T extends MutableText> T appendGUIPositiveOffset(T mainText, Text guiName, String translationKey) {
            if(TranslatableTextContent.class.isAssignableFrom(guiName.getContent().getClass())) {
                mainText.append(Text.translatable(translationKey + ".offset_pos"));
            } else {
                getWidth(guiName.getString(), width -> mainText.append(Text.translatable("positive." + width)));
            }

            return mainText;
        }

        public static <T extends MutableText> T appendGUINegativeOffset(T mainText, Text guiName, String translationKey) {
            if(TranslatableTextContent.class.isAssignableFrom(guiName.getContent().getClass())) {
                mainText.append(Text.translatable(translationKey + ".offset_neg"));
            } else {
                getWidth(guiName.getString(), width -> mainText.append(Text.translatable("negative." + width)));
            }

            return mainText;
        }

        public static <T extends MutableText> T appendTranslatableTo(T mainText, boolean negate, String... translatables) {
            return appendTranslatableTo(mainText, -1, negate, (Object[]) translatables);
        }

        public static <T extends MutableText> T appendTranslatableTo(T mainText, int offset, String... translatables) {
            return appendTranslatableTo(mainText, offset, false, (Object[]) translatables);
        }

        public static <T extends MutableText> T appendTranslatableTo(T mainText, String... translatables) {
            return appendTranslatableTo(mainText, -1, false, (Object[]) translatables);
        }
    }

    public interface SideUI extends Handler {

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
            GlobalUtils.apply(Text.literal("").formatted(Formatting.WHITE), it -> {
                switch(getTitlePosition()) {
                    case CENTER -> GlyphUtils.appendGUINegativeHalfOffset(it, guiName, getDefaultTranslationKey());
                    case LEFT -> GlyphUtils.appendGUINegativeOffset(it, guiName, getDefaultTranslationKey());
                }
                it.append(Text.translatable("sidecontainer." + getTitlePosition()));
            }, newTitle::append);
        }

        @Override
        default void end(Object instance, MutableText newTitle, Text guiName) {
            GlobalUtils.apply(Text.literal("").formatted(Formatting.WHITE), it -> {
                it.append(Text.translatable("sidecontainer." + getTitlePosition() + ".end"));
                switch(getTitlePosition()) {
                    case CENTER -> GlyphUtils.appendGUIPositiveHalfOffset(it, guiName, getDefaultTranslationKey());
                    case LEFT -> GlyphUtils.appendGUIPositiveOffset(it, guiName, getDefaultTranslationKey());
                }
            }, newTitle::append);
        }

        TitlePosition getTitlePosition();

        String getDefaultTranslationKey();
    }

    public interface Handler {
        default boolean isEnabled(Object instance) {
            return true;
        }

        default void begin(Object instance, MutableText newTitle, Text guiName) {

        }

        default void content(Object instance, MutableText newTitle, Text guiName) {
        }

        default void end(Object instance, MutableText newTitle, Text guiName) {
        }
    }
}
