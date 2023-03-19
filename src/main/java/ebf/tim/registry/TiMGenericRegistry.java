package ebf.tim.registry;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import ebf.tim.TrainsInMotion;
import ebf.tim.blocks.BlockDynamic;
import ebf.tim.blocks.BlockTrainFluid;
import ebf.tim.blocks.OreGen;
import ebf.tim.blocks.TileRenderFacing;
import ebf.tim.entities.GenericRailTransport;
import ebf.tim.items.ItemBlockTiM;
import ebf.tim.items.ItemCraftGuide;
import ebf.tim.items.ItemRail;
import ebf.tim.items.ItemTransport;
import ebf.tim.utility.*;
import fexcraft.tmt.slim.ModelBase;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ebf.tim.utility.RecipeManager.getRecipe;
import static net.minecraftforge.registries.GameData.BLOCKS;
import static net.minecraftforge.registries.GameData.ITEMS;

/**
 * <h1>Train registry</h1>
 * this class lists the data necessary to register trains and rollingstock.
 * this is not intended to be a way to get specific trains from the mod. (use the unlocalized entity name, or a class instanceof check)
 * @author Eternal Blue Flame
 */
public class TiMGenericRegistry {

    public GenericRailTransport transport;
    public Item[] recipe;

    //todo:purge redundancy checks on postinit
    private static List<String> usedNames = new ArrayList<>();
    private static List<String> redundantTiles = new ArrayList<>();
    private static List<String> redundantBlocks = new ArrayList<>();
    public static int registryPosition = 18;

    public static Map<Block, Item> fluidMap = new HashMap<>();

    public TiMGenericRegistry(GenericRailTransport transport, Item[] recipe) {
        this.transport = transport;
        this.recipe = recipe;
    }

    /**
     * @param block             the block in question
     * @param tab               the creative tab to put the block's item into, leave null for no creative tab entry.
     * @param MODID             modid of the host add-on, used for translations, texture names, and generic identification.
     * @param unlocalizedName   unlocalized name, used for translations, texture names, and generic identification.
     * @param oreDictionaryName ore directory name, used for other mods to identify type, mainly used for ingots, ores, and wood.
     * @param render            a ModelBase instance for rendering the tile entity.
     *                          Can instead be a TESR instance for more rendering control.
     *                          Null will fallback to a normal textured block render.
     * @return
     */
    public static Block registerBlock(Block block, CreativeTabs tab, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable Object render) {
        if (render instanceof ModelBase) {
            return registerBlock(block, tab, MODID, unlocalizedName, oreDictionaryName, TrainsInMotion.proxy.getRailTESR(), (ModelBase) render, unlocalizedName);
        } else {
            return registerBlock(block, tab, MODID, unlocalizedName, oreDictionaryName, render, null, unlocalizedName);
        }
    }
    public static Block registerBlock(Block block, CreativeTabs tab, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable Object render, String textureName) {
        if (render instanceof ModelBase) {
            return registerBlock(block, tab, MODID, unlocalizedName, oreDictionaryName, TrainsInMotion.proxy.getRailTESR(), (ModelBase) render, textureName);
        } else {
            return registerBlock(block, tab, MODID, unlocalizedName, oreDictionaryName, render, null, textureName);
        }
    }

    public static Block registerBlock(Block block, CreativeTabs tab, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable Object render) {
        return registerBlock(block, tab, null, unlocalizedName, oreDictionaryName, render);
    }

    public static Block registerBlock(Block block, CreativeTabs tab, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable Object TESR, @Nullable ModelBase model) {
        return registerBlock(block, tab, MODID, unlocalizedName, oreDictionaryName, TESR, model, unlocalizedName);
    }

    public static Block registerBlock(Block block, CreativeTabs tab, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable Object TESR, @Nullable ModelBase model, String textureName) {
        if (usedNames.contains(unlocalizedName)) {
            DebugUtil.println("ERROR: ", "attempted to register Block with a used unlocalizedName", unlocalizedName);
            DebugUtil.throwStackTrace();
        }
        if (TrainsInMotion.proxy.isClient() && tab != null) {
            block.setCreativeTab(tab);
        }
        if (unlocalizedName.length() > 0) {
            block.setTranslationKey(unlocalizedName);
            ((ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(BLOCKS))
                    .register(block.setRegistryName(MODID,unlocalizedName));
            if(TrainsInMotion.proxy.isClient() && (model!=null || (block instanceof ITileEntityProvider && ((ITileEntityProvider) block).createNewTileEntity(null,0) instanceof TileRenderFacing))) {
                RegisterItem(new ItemBlockTiM(block), MODID, unlocalizedName, oreDictionaryName + ".item", tab, null, ebf.tim.items.CustomItemModel.instance, textureName);
                if(TrainsInMotion.proxy.isClient() && TESR==null){
                    TESR=new ebf.tim.render.BlockTESR();
                }
            } else {
                RegisterItem(new ItemBlockTiM(block), MODID, unlocalizedName, oreDictionaryName + ".item", tab, null, null);
            }
            usedNames.add(unlocalizedName);
        } else {
            DebugUtil.println("ERROR: ", "attempted to register Block with no unlocalizedName");
            DebugUtil.throwStackTrace();
        }

        if (oreDictionaryName != null) {
            OreDictionary.registerOre(oreDictionaryName, block);
        }
        if (DebugUtil.dev && TrainsInMotion.proxy.isClient() && block.getTranslationKey().equals(CommonUtil.translate(block.getTranslationKey()))) {
            DebugUtil.println("Block missing lang entry: " + block.getTranslationKey() + " : " + unlocalizedName);
        }
        if (block instanceof ITileEntityProvider) {
            Class<? extends TileEntity> tile = ((ITileEntityProvider) block).createNewTileEntity(null, 0).getClass();
            if (!redundantTiles.contains(tile.getName())) {
                GameRegistry.registerTileEntity(tile, new ResourceLocation(MODID,unlocalizedName + ".tile"));
                redundantTiles.add(tile.getName());
                redundantTiles.add(unlocalizedName + "tile");
                if (TrainsInMotion.proxy.isClient() && TESR != null) {
                    regTileRender(MODID,unlocalizedName,block, tile, model, TESR);
                }
            } else if(!redundantTiles.contains(unlocalizedName + "tile")) {
                if (TrainsInMotion.proxy.isClient() && TESR != null) {
                    regTileRender(MODID,unlocalizedName,block, tile, model, TESR);
                } else if (TrainsInMotion.proxy.isClient()) {
                    registerBlockStateModel(block,MODID,textureName);
                }
            } else {
                DebugUtil.println("redundant tile name found",tile.getName(), unlocalizedName + "tile");
                DebugUtil.printStackTrace();
            }
        } else if (TrainsInMotion.proxy.isClient() && MODID != null) {
            registerBlockStateModel(block,MODID,textureName);
        }

        return block;
    }

    public static Item RegisterItem(Item itm, String MODID, String unlocalizedName, CreativeTabs tab) {
        return RegisterItem(itm, MODID, unlocalizedName, null, tab, null, null);
    }

    public static Item RegisterItem(Item itm, String MODID, String unlocalizedName, CreativeTabs tab, String textureName) {
        return RegisterItem(itm, MODID, unlocalizedName, null, tab, null, null, textureName);
    }

    public static Item RegisterItem(Item itm, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable CreativeTabs tab, @Nullable Item container, @Nullable Object itemRender) {
        return RegisterItem(itm, MODID, unlocalizedName, oreDictionaryName, tab, container, itemRender, unlocalizedName.replace("item.", ""));
    }

    public static Item RegisterItem(Item itm, String MODID, String unlocalizedName, @Nullable String oreDictionaryName, @Nullable CreativeTabs tab, @Nullable Item container, @Nullable Object itemRender, String textureName) {
        if (usedNames.contains(unlocalizedName)) {
            DebugUtil.println("ERROR: ", "attempted to register Item with a used unlocalizedName", unlocalizedName);
            DebugUtil.throwStackTrace();
        }
        if (TrainsInMotion.proxy.isClient() && tab != null) {
            itm.setCreativeTab(tab);
        }
        if (container != null) {
            itm.setContainerItem(container);
        }
        if (!unlocalizedName.equals("")) {
            itm.setTranslationKey(unlocalizedName);
            usedNames.add(unlocalizedName);
        } else {
            DebugUtil.println("ERROR: ", "attempted to register Item with no unlocalizedName");
            DebugUtil.throwStackTrace();
        }
        ((ForgeRegistry<Item>)RegistryManager.ACTIVE.getRegistry(ITEMS))
                .register(itm.setRegistryName(MODID,unlocalizedName));
        if (oreDictionaryName != null) {
            OreDictionary.registerOre(oreDictionaryName, itm);
        }
        if (DebugUtil.dev && TrainsInMotion.proxy != null && TrainsInMotion.proxy.isClient() && itm.getTranslationKey().equals(CommonUtil.translate(itm.getTranslationKey()+".name"))) {
            DebugUtil.println("Item missing lang entry: " + itm.getTranslationKey());
        }
        if (TrainsInMotion.proxy.isClient() && (itemRender != null || itm instanceof ItemTransport || itm instanceof ItemRail)) {
            //MinecraftForgeClient.registerItemRenderer(itm, (IItemRenderer) itemRender);
            ebf.tim.items.CustomItemModel.renderItems.add(new ResourceLocation(MODID,unlocalizedName));

            //todo:this somehow? actually it might just be forced in 1.8+
            if (ClientProxy.preRenderModels) {
                //ebf.tim.items.CustomItemModel.instance.renderItem(IItemRenderer.ItemRenderType.INVENTORY, new ItemStack(itm));
            }
        }
        if(TrainsInMotion.proxy.isClient()){
            //make the .json files because it's stupid and im lazy.
            if(DebugUtil.dev && !(itm instanceof ItemTransport || itm instanceof ItemRail || itm instanceof ItemBlock)){
                File dir =  new File(new File(ClientProxy.configDirectory).getParentFile().toString()+
                        "/src/main/resources/assets/" + MODID + "/models");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                dir =  new File(dir.toString()+ "/item");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                if(textureName.contains("/")) {
                    String[] split = textureName.split("/");
                    for (int i=0;i<split.length-1;i++) {
                        dir =  new File(dir.toString()+ "/"+split[i].replace("/",""));
                        if(!dir.exists()){
                            dir.mkdirs();
                        }
                    }
                }
                File conf = new File(dir.toString() + "/"+ unlocalizedName.toLowerCase()+".json");

                //todo: remove the true later
                if(!conf.exists()){
                    FileOutputStream fileoutputstream=null;
                    try {
                        fileoutputstream=new FileOutputStream(conf);

                        StringBuilder sb = new StringBuilder();

                        sb.append("{\n  \"parent\": \"item/generated\",\n  \"textures\": {\n    \"layer0\": \"");
                        sb.append(MODID);
                        sb.append(":");
                        sb.append("items/");
                        sb.append(textureName);
                        sb.append(".png");
                        sb.append("\"\n  }\n}");

                        fileoutputstream.write(sb.toString().getBytes());
                        fileoutputstream.close();
                        DebugUtil.println("creating json model at", conf.toString());
                    } catch (IOException e) {
                        if(fileoutputstream!=null) {
                            try {
                                fileoutputstream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        e.printStackTrace();
                    }
                }
            }
            net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(itm,0,new ModelResourceLocation(new ResourceLocation(MODID,textureName),"inventory"));
        }
        return itm;
    }


    public static Item RegisterFluid(Fluid fluid, String MODID, String unlocalizedName, boolean isGaseous, int density, MapColor color, @Nullable CreativeTabs tab) {
        if (usedNames.contains(unlocalizedName)) {
            DebugUtil.println("ERROR: ", "attempted to register Fluid with a used unlocalizedName", unlocalizedName);
            DebugUtil.throwStackTrace();
        }
        if (!unlocalizedName.equals("")) {
            fluid.setUnlocalizedName(unlocalizedName);
            usedNames.add(unlocalizedName);
        } else {
            DebugUtil.println("ERROR: ", "attempted to register Fluid with no unlocalizedName");
            DebugUtil.throwStackTrace();
        }
        fluid.setGaseous(isGaseous).setDensity(density);
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);

        Block block = new BlockTrainFluid(fluid, Material.WATER)
                .getBlockState().getBlock().setTranslationKey("block." + unlocalizedName.replace(".item", ""));
            //todo: block textures aren't this configurable anymore
            //.setBlockTextureName(MODID + ":block_" + unlocalizedName);
        ((BlockTrainFluid) block).setModID(MODID);

        ((ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(BLOCKS))
                .register(block.setRegistryName(MODID,"block." + unlocalizedName));
        if (TrainsInMotion.proxy.isClient()) {
            //todo: block textures aren't this configurable anymore
            //block.setBlockTextureName(MODID + ":" + unlocalizedName);
        }
        fluid.setBlock(block);


        Item bucket = new UniversalBucket(){
            @Override
            public void getSubItems(@Nullable final CreativeTabs tab, final NonNullList<ItemStack> subItems) {
                if (!this.isInCreativeTab(tab)){return;}

                ItemStack filledBucket = new ItemStack(ForgeModContainer.getInstance().universalBucket);

                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("FluidName", fluid.getName());
                nbt.setInteger("Amount", 1000);

                if (filledBucket.getTagCompound() != null) {
                    nbt.setTag("Tag", filledBucket.getTagCompound());
                }
                filledBucket.setTagCompound(nbt);
                subItems.add(filledBucket);

            }
        };


        if (TrainsInMotion.proxy.isClient()) {
            bucket.setTranslationKey(MODID + ":bucket_" + unlocalizedName);
        }
        bucket.setTranslationKey(unlocalizedName + ".bucket").setCreativeTab(tab).setContainerItem(bucket);
        ((ForgeRegistry<Item>)RegistryManager.ACTIVE.getRegistry(ITEMS))
                .register(bucket.setRegistryName(MODID,"fluid." +unlocalizedName + ".bucket"));
        //todo: 1.12 uses instanceof stuff, so this _shouldn't_ be needed?
        //FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.BUCKET));

        fluidMap.put(block, bucket);

        if (DebugUtil.dev && TrainsInMotion.proxy.isClient()) {
            if (fluid.getUnlocalizedName().equals(CommonUtil.translate(fluid.getUnlocalizedName()))) {
                DebugUtil.println("Fluid missing lang entry: " + fluid.getUnlocalizedName());
            }
            if (bucket.getTranslationKey().equals(block.getLocalizedName())) {
                DebugUtil.println("Item missing lang entry: " + bucket.getTranslationKey());
            }
            if (block.getTranslationKey().equals(block.getLocalizedName())) {
                DebugUtil.println("Block missing lang entry: " + block.getTranslationKey());
            }

        }
        return bucket;
    }


    public static void registerTransports(String MODID, List<GenericRailTransport> entities, Object entityRender) {
        registerTransports(MODID,entities.toArray(new GenericRailTransport[]{}), entityRender);
    }

    public static void registerTransports(String MODID, GenericRailTransport[] entities, Object entityRender) {
        if (registryPosition == -1) {
            DebugUtil.println("ERROR", "ADDING TRANSPORT REGISTRY ITEMS OUTSIDE MOD INIT", "PLEASE REGISTER YOUR ENTITIES IN THE FOLLOWING EVENT:",
                    "@Mod.EventHandler public void init(FMLInitializationEvent event)");
        }
        for (GenericRailTransport registry : entities) {
            if (DebugUtil.dev && usedNames.contains(registry.transportName())) {
                DebugUtil.println(registry.getClass().getName(), "is trying to register under the name", usedNames.contains(registry.transportName()), "which is already used");
                DebugUtil.throwStackTrace();
            }
            net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
                    new ResourceLocation(MODID,registry.transportName().replace(" ", "") + ".entity"),
                    registry.getClass(),
                    registry.transportName().replace(" ", "") + ".entity",
                    registryPosition, TrainsInMotion.instance, 1600, 3, true);

            RegisterItem(registry.getCartItem().getItem(),MODID,registry.transportName().replace(" ", "")+".item",
                    null,registry.getItem().getCreativeTab(),null,null);
            if (registry.getRecipe() != null) {
                tempReipes.add(new recipePreReg(registry.getRecipe(), registry.getCartItem(), registry.getCraftingTable(), MODID));
            }
            registry.registerSkins();
            ItemCraftGuide.itemEntries.add(registry.getClass());
            if (TrainsInMotion.proxy.isClient()) {
                regEntityRender(registry,entityRender);
            }
            usedNames.add(registry.transportName());
            registryPosition++;
        }
    }

    @SideOnly(Side.CLIENT)
    private static void regEntityRender(GenericRailTransport registry, Object entityRender){
        if(entityRender==null){
            net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(registry.getClass(), ebf.tim.render.RenderWagon.INSTANCE);
            if (ClientProxy.preRenderModels) {
                //((net.minecraftforge.fml.client.registry.IRenderFactory<GenericRailTransport>) TrainsInMotion.proxy.getEntityRender()).ren(registry, 0, 0, 0, 0, 0);
            }
        } else {
              net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(registry.getClass(), (net.minecraft.client.renderer.entity.Render<GenericRailTransport>) entityRender);
            if (ClientProxy.preRenderModels) {
                //((net.minecraftforge.fml.client.registry.IRenderFactory<GenericRailTransport>) entityRender).doRender(registry, 0, 0, 0, 0, 0);
            }
        }
        if (ClientProxy.preRenderModels && ClientProxy.hdTransportItems) {
            ebf.tim.items.CustomItemModel.instance.renderItemModel(registry.getCartItem(), net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GUI,null);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void regTileRender(String MODID, String unlocalizedName, Block block, Class<? extends TileEntity> tile, ModelBase model, Object TESR){

        if (block instanceof BlockDynamic) {
            if (model != null) {
                ((BlockDynamic) block).setModel(model);
            } else if (TESR != null) {
                ((BlockDynamic) block).setTESR(TESR);
            }
        }
        ebf.tim.items.CustomItemModel.renderItems.add(new ResourceLocation(MODID,unlocalizedName));
        ebf.tim.items.CustomItemModel.renderItems.add(new ResourceLocation(MODID,unlocalizedName+".item"));
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(tile, (net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer) TESR);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), CustomItemModel.instance);
        //ebf.tim.items.CustomItemModel.renderItems.add(new ResourceLocation(MODID, unlocalizedName + "tile"));
        ebf.tim.items.CustomItemModel.registerBlockTextures(Item.getItemFromBlock(block), ((ITileEntityProvider) block).createNewTileEntity(null, 0));

    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockStateModel(Block block, String MODID, String textureName){
        net.minecraftforge.client.model.ModelLoader.setCustomStateMapper(block, new net.minecraft.client.renderer.block.statemap.IStateMapper() {
            @Override
            public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
                Map value = new HashMap<IBlockState, ModelResourceLocation>(){};
                value.put(blockIn.getDefaultState(), new ModelResourceLocation(new ResourceLocation(MODID,textureName),""));
                return value;
            }
        });
    }

    /**
     * @param priority the priority to generate, higher numbers tend to generate after other mods.
     */
    public static void registerOreGen(int priority, OreGen veinConfig) {
        GameRegistry.registerWorldGenerator(veinConfig, priority);
    }



    public static void endRegistration() {

        for(recipePreReg recipe : tempReipes){
            if (!CommonProxy.recipesInMods.containsKey(recipe.modid)) {
                CommonProxy.recipesInMods.put(recipe.modid, new ArrayList<Recipe>());
            }
            CommonProxy.recipesInMods.get(recipe.modid).add(new Recipe(
                    recipe.result,
                    recipe.inputs[0],recipe.inputs[1],recipe.inputs[2],
                    recipe.inputs[3],recipe.inputs[4],recipe.inputs[5],
                    recipe.inputs[6],recipe.inputs[7],recipe.inputs[8]
            ));
            RecipeManager.registerRecipe(recipe.inputs, recipe.result, recipe.tier);
        }

        usedNames = null;
        registryPosition = -1;
        redundantTiles = null;
    }

    private static List<recipePreReg> tempReipes = new ArrayList<>();
    private static class recipePreReg {
        Block tier;ItemStack result;ItemStack[] inputs; String modid;

        public recipePreReg(ItemStack[] i, ItemStack r, Block t, String m) {
            this.inputs=i;
            this.result = r;
            this.tier=t;
            this.modid=m;
        }
    }

    //todo:add support for buildcraft/railcraft burnable fluids

    @Optional.Method(modid = "BuildCraft|Energy")
    static void registerBCFluid(Fluid f, int powerPerCycle, int totalBurningTime) {
        BuildcraftFuelRegistry.fuel.addFuel(f, powerPerCycle, totalBurningTime);
    }

    /*@Optional.Method(modid = "Railcraft")
    static void registerRCFluid(Fluid f, int totalBurningTime) {
        FuelManager.addBoilerFuel(f, totalBurningTime);
    }*/

}
