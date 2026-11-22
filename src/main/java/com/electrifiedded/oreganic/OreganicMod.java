package com.electrifiedded.oreganic;

import com.electrifiedded.archaiclib.original.OriginalModAssets;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.25.0,)")
@Mod.EventBusSubscriber
public class OreganicMod {
    public static final String ORIGINAL_NAME = "oreganic_mod_1.7.10.jar - Oreganic MC1.7.10 R1.0";
    public static final String ORIGINAL_URL = "https://github.com/Crystic/Oreganic";

    @Mod.Instance(Reference.MOD_ID)
    public static OreganicMod INSTANCE;

    public static final String[] NAMES = {"coal", "iron", "gold", "lapis", "redstone", "diamond", "emerald"};
    public static final Block[] ORES = {Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE,
        Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE};
    public static final OrganicOre[] ORGANIC = new OrganicOre[NAMES.length];
    public static final PlantStem[] STEMS = new PlantStem[NAMES.length];
    public static final Item[] SEEDS = new Item[NAMES.length];
    public static final MachineBlock INFUSER = new MachineBlock(true);
    public static final MachineBlock EXTRACTOR = new MachineBlock(false);

    public static final CreativeTabs TAB = new CreativeTabs("OreGanic") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ORGANIC[0]);
        }
    };

    static {
        INFUSER.setRegistryName(Reference.MOD_ID, "organic_infuser");
        INFUSER.setTranslationKey("organic_infuser");
        EXTRACTOR.setRegistryName(Reference.MOD_ID, "mineral_extractor");
        EXTRACTOR.setTranslationKey("mineral_extractor");
        for (int i = 0; i < NAMES.length; i++) {
            String n = NAMES[i];
            OrganicOre o = new OrganicOre();
            o.setRegistryName(Reference.MOD_ID, "organic_" + n);
            o.setTranslationKey("organic_" + n);
            ORGANIC[i] = o;
            PlantStem s = new PlantStem(o);
            s.setRegistryName(Reference.MOD_ID, n + "_stem");
            s.setTranslationKey(n + "_stem");
            ItemSeeds seeds = new ItemSeeds(s, Blocks.FARMLAND);
            seeds.setRegistryName(Reference.MOD_ID, n + "_seeds");
            seeds.setTranslationKey(n + "_seeds");
            seeds.setCreativeTab(TAB);
            s.seeds = seeds;
            STEMS[i] = s;
            SEEDS[i] = seeds;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        // textures come from the original 1.7.10 jar (client side only)
        OriginalModAssets.require(Reference.MOD_ID, "Oreganic", ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
        GameRegistry.registerTileEntity(TileInfuser.class, new ResourceLocation(Reference.MOD_ID, "organic_infuser"));
        GameRegistry.registerTileEntity(TileExtractor.class, new ResourceLocation(Reference.MOD_ID, "mineral_extractor"));
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ItemStack slab = new ItemStack(Blocks.STONE_SLAB, 1, 0);
        GameRegistry.addShapedRecipe(new ResourceLocation(Reference.MOD_ID, "organic_infuser"), null,
            new ItemStack(INFUSER), "BBB", "GFG", "SSS",
            'B', Blocks.STONE, 'G', Blocks.GLASS_PANE, 'F', Blocks.FURNACE, 'S', slab);
        GameRegistry.addShapedRecipe(new ResourceLocation(Reference.MOD_ID, "mineral_extractor"), null,
            new ItemStack(EXTRACTOR), "BGB", "BIB", "SSS",
            'B', Blocks.STONE, 'G', Blocks.GLASS_PANE, 'I', Blocks.IRON_BLOCK, 'S', slab);
    }

    @SubscribeEvent
    public static void blocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(ORGANIC);
        e.getRegistry().registerAll(STEMS);
        e.getRegistry().registerAll(INFUSER, EXTRACTOR);
    }

    @SubscribeEvent
    public static void items(RegistryEvent.Register<Item> e) {
        for (Block b : ORGANIC) e.getRegistry().register(new ItemBlock(b).setRegistryName(b.getRegistryName()));
        e.getRegistry().registerAll(SEEDS);
        e.getRegistry().register(new ItemBlock(INFUSER).setRegistryName(INFUSER.getRegistryName()));
        e.getRegistry().register(new ItemBlock(EXTRACTOR).setRegistryName(EXTRACTOR.getRegistryName()));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void models(ModelRegistryEvent e) {
        Block[] all = new Block[ORGANIC.length + 2];
        System.arraycopy(ORGANIC, 0, all, 0, ORGANIC.length);
        all[ORGANIC.length] = INFUSER;
        all[ORGANIC.length + 1] = EXTRACTOR;
        for (Block b : all) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0,
                new ModelResourceLocation(b.getRegistryName(), "inventory"));
        }
        for (int i = 0; i < NAMES.length; i++) {
            ModelLoader.setCustomModelResourceLocation(SEEDS[i], 0,
                new ModelResourceLocation(SEEDS[i].getRegistryName(), "inventory"));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void colors(ColorHandlerEvent.Block e) {
        // all stems share the same dark green tint of the original
        for (Block stem : STEMS) {
            e.getBlockColors().registerBlockColorHandler((state, w, pos, tint) -> 0x197C47, stem);
        }
    }
}
