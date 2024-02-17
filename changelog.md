## 0.4.1
### Compatibility
> Added a compatibility mixin for Lithium where chunks wouldn't load since BlockStates wouldn't be replaced before being sent to the client.
### Bug fixes
> Anvil now have the vanilla maximum level of enchantment combining. They did not and it resulted in an error.  
> Fixed a crash with Enchanted Furnaces.  
> Fixed a crash with Enchanted Hoppers.  
> Water Buckets can now be enchanted again.  
> Empty Buckets can now be enchanted with Efficiency again.  
> Fixed a problem with Hoppers that wouldn't empty themselves completely.  
> Fixed a loading problem with Cloud Boxes where they wouldn't load their content on the overworld's loading.  
> Fixed a problem where custom blocks would continue being destroyed after the player used a Teleporter block.  
### DoubleTools
> Vein Miner is now available for Pickaxes.
### Tools
> Crossbows now accept two new enchantments: Infinity and Power.
### Teleporter
> You may now teleport by sneaking and hitting the block. This is a counter measure to the base game mechanic where the interaction would be cancelled if you hold an item in the secondary hand while sneaking.

## 0.4
### Global Refactoring
> A large portion of the code was rewritten to put the multiple parts that affected the same game mechanics together.
### More Tools
> Added shears made of: Copper, Diamond, Netherite, Obsidian, Crying Obsidian and Reinforced Obsidian.
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Diamond.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Diamond.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Shears.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Diamond.png" width="32" height="32"> |
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Shears.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> |
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Shears.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> |
> |     |                |  |
> |---------|----------------------|------------|
> |  | <img src="https://minecraft.wiki/images/Invicon_Copper_Ingot.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Copper_Ingot.png" width="32" height="32"> |  |
### Chests
> An extra UI is added to the WhitelistedChests to show which players are allowed.
### DoubleTools
> Shovels and Pickaxes have two new modes to break a square of 3x3 going down from the block destroyed.  
> Axes and Hoes no longer have a task list since they only had one to begin with.
### Furnaces
> Furnaces can now be enchanted in the enchanting table with a low enchantability rate. This was implemented to help with speeding up the early game.
### Hoppers
> Efficiency no longer reduces the cooldown of the hopper, but repeats the extration 
### Ingot Charger
> Used to charge any kind of Charged Ingot with lightnings.  
> You'll need to put a Lightning Rod over it to be able to use it.  
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Block_of_Copper.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Block_of_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Copper_Ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Cobblestone.png" width="32" height="32"> |
### Teleporter
> Used to teleport at locations by shift-clicking on it.  
> You can assign new points by interacting with Teleporting Essences for a maximum of 4 points.  
> You can select the point by hitting the block.  
> You'll need a fully charged Charged Diamond Ingot to craft it.  
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Glass.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Glass.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Glass.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Glass.png" width="32" height="32"> | <img src="https://raw.githubusercontent.com/ShoukaSeikyo/shoukaseikyo.github.io/master/images/energized_0_diamond_ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Glass.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> |
### Diamond Ingot
> A new ingot has been added, you can craft it in a smithing table:  
>  |     |                |  |
>  |---------|----------------------|------------|
>  | <img src="https://minecraft.wiki/images/Invicon_Brick.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Lava_Bucket.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Diamond.png" width="32" height="32"> |
### Charged Diamond Ingot
> A variant of the Diamond Ingot has been added. You can charge inside an Ingot Charger.  
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://raw.githubusercontent.com/ShoukaSeikyo/shoukaseikyo.github.io/master/images/diamond_ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> |
### Teleporting Essence
> This item can be used once to assign a Teleporting Point.  
> After that it can be consumed to teleport back to that point or used in a Teleporter.   
> |                                                                                           |                                                                                           |  |
> |-------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Eye_of_Ender.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Chorus_Fruit.png" width="32" height="32"> |

## 0.3
### Bugs fixes
> Infinity Buckets won't be transformed into Lava Bucket without enchantments when emptying a Lava Cauldron.
### Dispensers, Buckets and Cauldrons
> Dispensers with Buckets can now interact with Cauldrons.
### Double Tools
> Breaking Mangrove Roots or Mangrove Logs now destroys both when using two axes.
> Breaking non-persistant Leaves and Vines now destroys adjascents leaves/vines when using two shears.
### More Tools
> Obsidian Tools, Crying Obsidian Tools and Reinforced Obsidian Tools cannot be enchanted in the Enchanting Table anymore.
### More Armors
> Obsidian Armors, Crying Obsidian Armors and Reinforced Obsidian Armors cannot be enchanted in the Enchanting Table anymore.
### Muffler
> Muffler has been added.  
> This block blocks any sound in a radius of 25.  
> There will eventually be a GUI to select sound categories you want to mute.  
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_White_Wool.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_White_Wool.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_White_Wool.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Note_Block.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Redstone.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Obsidian.png" width="32" height="32"> |
### Deep Storage Barrel
> Hoppers won't select the first slot of a Deep Storage Barrel when extracting items.
### More Enchantments
> Shears can now be enchanted with Fortune III (in an anvil) allows to get more a randomly increased amount of wools from sheeps.
### ExtraGUI
> Enchanted Blocks Entities now have a custom GUI added to them showing the enchantments applied to them.
> Deep Storage Barrel also have additional lines about its contents.
## 0.2
### Translation
> Translation file have been added. WIP
### Enchanted Furnace
> Removed ability to output more items if the recipe outputs a block:  
> ex.: Sand doesn't give more than 1 Glass when cooked.
### Deep Storage Barrels
> Fixed bugs when Quick Moving items leaving the slot empty or moving more than one stack at a time.  
> **Flame** enchantment can now be added to Deep Storage Barrels to destroy any item put in its last slot.
### More Armors
> **Copper** and **Emerald** armors have been added.  
> The recipes are the same as any other armor type, but with **copper ingots** or **emeralds**.  
> | Type    | Helmet               | Chestplate | Leggings | Boots |
> |---------|----------------------|------------|----------|-------|
> | Copper  | 110                  | 160        | 150      |  130  |
> | Emerald | 242                  | 352        | 330      |  286  |
### More blocks:
> **Reinforced Obsidian** has been added.  
> **Reinforced Obsidian** recipe:  
> |     |                |  |
> |---------|----------------------|------------|
> | <img src="https://minecraft.wiki/images/Invicon_Crying_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Crying_Obsidian.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Crying_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> |
> | <img src="https://minecraft.wiki/images/Invicon_Crying_Obsidian.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Netherite_Ingot.png" width="32" height="32"> | <img src="https://minecraft.wiki/images/Invicon_Crying_Obsidian.png" width="32" height="32"> |
### More tools:
> **Obsidian tools** durability have been reduced from 10 000 to 3 000;  
> **Obsidian tools** cannot be enchanted anymore.  
> **Obisidan tools** breaking speed has been modified to replicate the client's side speed.
>   
> **Crying Obsidian tools** have been added.  
> **Crying Obsidian tools** have a durability of 6 000.  
> **Crying Obsidian tools** can only be enchanted with **Efficiency** 5 or lower.
>
> **Reinforced Obsidian tools** have been added.  
> **Reinforced Obsidian tools** have a durability of 10 000.  
> **Reinforced Obsidian tools** cannot be enchanted with Unbreaking.  
### Double Tools:
> Fixed tool modes not working with Custom Tools.
### Whitelisted Chest:
> Fixed not being able to add new players to Whitelist.