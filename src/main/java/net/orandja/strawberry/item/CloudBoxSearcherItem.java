package net.orandja.strawberry.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoShulkerBoxes;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.StrawberryExtraUI;
import net.orandja.strawberry.intf.StrawberryItemHandler;
import net.orandja.strawberry.screen.SearchAnvilScreenHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class CloudBoxSearcherItem extends Item implements StrawberryItemHandler {

    public static void searchFor(PlayerEntity player, String searchTerm) {
        player.openHandledScreen(SearchAnvilScreenHandler.create(
            GlobalUtils.apply(Text.literal(""), global -> {
                global.append(Text.translatable("container.anvilsearch").formatted(Formatting.WHITE));
                StrawberryExtraUI.GlyphUtils.appendTranslatableTo(global, 0, true, "container.cloudboxsearcher");
                AtomicInteger i = new AtomicInteger(4);
                GlobalUtils.run(ChocoShulkerBoxes.inventories.keySet().stream().filter(it -> it.startsWith("public:")).map(it -> it.split(":")[1]), it -> {
                    return (searchTerm != null) ? it.filter(name -> name.contains(searchTerm)) : it;
                }).limit(12).forEach(name -> {
                    StrawberryExtraUI.GlyphUtils.appendLiteralTo(global, i.getAndIncrement(), true, name);
                });
            }), (searchTerm == null ? "" : searchTerm), CloudBoxSearcherItem::searchFor
        ));
    }

    public CloudBoxSearcherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        searchFor(player, null);
        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return transform(sourceStack, Items.MAP, 1);
    }
}
