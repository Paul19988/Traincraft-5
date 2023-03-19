package ebf.tim.items;

import ebf.tim.blocks.TileRenderFacing;
import ebf.tim.entities.GenericRailTransport;
import ebf.tim.render.models.ModelRail;
import ebf.tim.utility.ClientProxy;
import ebf.tim.utility.ClientUtil;
import ebf.tim.utility.Vec5f;
import fexcraft.tmt.slim.ModelRendererTurbo;
import fexcraft.tmt.slim.Tessellator;
import fexcraft.tmt.slim.TextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;

import static ebf.tim.render.models.Model1x1Rail.addVertexWithOffsetAndUV;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class CustomItemModel implements ICustomModelLoader {

    public static final CustomItemModel instance = new CustomItemModel();

    private static HashMap<Item, TileRenderFacing> blockTextures = new HashMap<>();

    private static Integer itemSprite = null;

    public static void registerBlockTextures(Item itm, TileEntity tile){
        if(tile instanceof TileRenderFacing) {
            blockTextures.put(itm, (TileRenderFacing) tile);
        }
    }



    /*@Override// generally useless but needs to be here
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }*/

    //@Override // generally useless but needs to be here
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return renderItems.contains(modelLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new Model(modelLocation);
    }


    /*@Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;//models.containsKey(new ResourceLocation(item.getUnlocalizedName()));
    }
    float scale;

    public static void render2dItem(ResourceLocation texture, ItemRenderType type){
        TextureManager.bindTexture(texture);
        GL11.glRotatef(225,0,1,0);
        GL11.glScalef(1.4f,-1.4f,1);

        if(itemSprite==null || !GL11.glIsList(itemSprite)) {
            ModelRendererTurbo box =new ModelRendererTurbo("", 0, 0, 16, 16)
                    .addBox(-8, -8, 0, 16, 16, 0f);

            itemSprite= net.minecraft.client.renderer.GLAllocation.generateDisplayLists(1);
            GL11.glNewList(itemSprite, GL11.GL_COMPILE);
            box.render();
            GL11.glEndList();
        } else {

            switch (type){
                case INVENTORY: {
                    GL11.glEnable(GL11.GL_BLEND);
                    break;}
                case EQUIPPED_FIRST_PERSON:{
                    GL11.glTranslatef(0,-0.5f,-1);
                    break;
                }
                case EQUIPPED:{
                    GL11.glTranslatef(0,-0.5f,-1);
                    GL11.glRotatef(-45,1,0,0);
                    break;
                }
                default:{//item frame case
                    GL11.glRotatef(50,0,1,0);
                    GL11.glScalef(0.8f,0.8f,0.8f);
                }

            }
            if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
                GL11.glTranslatef(0,0,-0.005f);
                for(int i=0;i<20;i++){
                    GL11.glTranslatef(0,0,0.00025f*i);
                    GL11.glCallList(itemSprite);
                }
            } else {
                GL11.glCallList(itemSprite);
            }
        }

    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(item==null){return;}

        int boundTexture = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D);
        if (item.getItem() instanceof ItemTransport){
            GL11.glPushMatrix();

            if(!ClientProxy.hdTransportItems && TextureManager.textureExists(((ItemTransport) item.getItem()).getIconResource())){
                render2dItem(((ItemTransport) item.getItem()).getIconResource(), type);
                GL11.glPopMatrix();
                return;
            }

            GenericRailTransport entity = ((ItemTransport) item.getItem()).entity;
            float scale = entity.getHitboxSize()[0];
            if(scale!=0){
                scale = 1.3f/(scale /1.3f);
            }
            GL11.glPushMatrix();
            GL11.glScalef(scale,scale,scale);
            switch (type){
                case FIRST_PERSON_RIGHT_HAND:{
                    GL11.glRotatef(270+(1*entity.getHitboxSize()[0]),0,1,0);
                    GL11.glRotatef(10+(-1*entity.getHitboxSize()[0]),0,0,1);
                    GL11.glRotatef(-1*entity.getHitboxSize()[0],1,0,0);
                    GL11.glRotatef(180,1,0,0);
                    GL11.glScalef(0.6f,0.6f,0.6f);
                    GL11.glTranslatef(0,-2,0.5f);
                    break;
                }
                case FIRST_PERSON_LEFT_HAND:{
                    GL11.glRotatef(90+(1*entity.getHitboxSize()[0]),0,1,0);
                    GL11.glRotatef(190+(-1*entity.getHitboxSize()[0]),0,0,1);
                    GL11.glRotatef(-1*entity.getHitboxSize()[0],1,0,0);
                    GL11.glTranslatef(1f,0.1f*(entity.getHitboxSize()[0]),0.7f*(entity.getHitboxSize()[0]));
                    break;
                }
                case GUI: {
                    GL11.glRotatef(225,0,1,0);
                    GL11.glRotatef(-185,0,0,1);
                    GL11.glRotatef(5,1,0,0);
                    GL11.glScalef(0.6f,0.6f,0.6f);
                    GL11.glTranslatef(0,-1.3f,0);
                    break;
                }
                case THIRD_PERSON_RIGHT_HAND:{
                    GL11.glRotatef(180,0,0,1);
                    GL11.glRotatef(90,1,0,0);
                    GL11.glRotatef(180,0,0,1);
                    GL11.glTranslatef(0.25f*(entity.getHitboxSize()[0]),-0.15f*(entity.getHitboxSize()[0]),
                            0.1f*(entity.getHitboxSize()[0]));
                    GL11.glTranslatef(-2,-1,0);
                    GL11.glScalef(0.75f,0.75f,0.75f);
                    break;
                }
                case THIRD_PERSON_LEFT_HAND:{
                    GL11.glRotatef(90+(0*entity.getHitboxSize()[0]),0,1,0);
                    GL11.glRotatef(80+(-1*entity.getHitboxSize()[0]),0,0,1);
                    GL11.glRotatef(-1*entity.getHitboxSize()[0],1,0,0);
                    GL11.glTranslatef(0.35f*(entity.getHitboxSize()[0]),-0.05f*(entity.getHitboxSize()[0]),
                            -0.5f*(entity.getHitboxSize()[0]));
                    break;
                }
                default:{//item frame case
                    GL11.glRotatef(90,0,1,0);
                    GL11.glScalef(0.5f,0.5f,0.5f);
                    GL11.glTranslatef(0,1.5f,0);
                    GL11.glRotatef(180,1,0,0);
                }

            }
            ClientProxy.getTransportRenderer().render(
                    ((ItemTransport)item.getItem()).entity,0,0,0, 0, true, null);
            GL11.glPopMatrix();
            org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D,boundTexture);
        } else if (item.getItem() instanceof ItemRail){
            if(item.getTagCompound()==null){
                return;
            }
            GL11.glPushMatrix();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslated(0, 0.05, 0);
            Tessellator.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            if(type== ItemCameraTransforms.TransformType.GUI){
                GL11.glRotatef(90,1,0,0);
                GL11.glRotatef(-45,0,0,1);
                GL11.glRotatef(90,0,1,0);
                GL11.glScalef(0.8f,0.8f,0.8f);
                GL11.glTranslatef(-0.0625f,0,0);
            } else if (type== ItemCameraTransforms.TransformType.GROUND){
                GL11.glRotatef(90,0,1,0);
                GL11.glScalef(0.4f,0.4f,0.4f);
            } else if (type== ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND){
                GL11.glRotatef(95,0,0,1);
                GL11.glRotatef(15,0,1,0);
                GL11.glRotatef(-5,1,0,0);
                GL11.glScalef(0.6f,0.6f,0.6f);
                GL11.glTranslatef(0.0625f,-0.125f,0);
            }
            if(item.getTagCompound().hasKey("ballast") && item.getTagCompound().getCompoundTag("ballast").hasKey("id")
                    && !item.getTagCompound().getCompoundTag("ballast").getString("id").equals("minecraft:air")) {
                TextureAtlasSprite iicon = TextureManager.bindBlockTextureFromSide(EnumFacing.UP.ordinal(),
                        new ItemStack(item.getTagCompound().getCompoundTag("ballast")));

                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(start, 0.625f, 0, 0, iicon.getMinU(), iicon.getMinV());
                addVertexWithOffsetAndUV(start, -0.625f, 0, 0, iicon.getMinU(), iicon.getMaxV());
                addVertexWithOffsetAndUV(end, 0.625f, 0, 0, iicon.getMaxU(), iicon.getMinV());
                addVertexWithOffsetAndUV(end, -0.625f, 0, 0, iicon.getMaxU(), iicon.getMaxV());

                Tessellator.getInstance().draw();
            }

            if(item.getTagCompound().hasKey("ties") && item.getTagCompound().getCompoundTag("ties").hasKey("id")
                    && !item.getTagCompound().getCompoundTag("ties").getString("id").equals("minecraft:air")) {
                TextureAtlasSprite iicon = TextureManager.bindBlockTextureFromSide(EnumFacing.WEST.ordinal(),
                        new ItemStack(item.getTagCompound().getCompoundTag("ties")));

                GL11.glPushMatrix();
                GL11.glTranslatef(-0.45f,0.01f,0);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(tieStart, 0.625f, 0, 0, iicon.getMinU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieStart, -0.625f, 0, 0, iicon.getMinU(), iicon.getMaxV());
                addVertexWithOffsetAndUV(tieEnd, 0.625f, 0, 0, iicon.getMaxU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieEnd, -0.625f, 0, 0, iicon.getMaxU(), iicon.getMaxV());
                Tessellator.getInstance().draw();
                GL11.glTranslatef(0.3f,0,0);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(tieStart, 0.625f, 0, 0, iicon.getMinU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieStart, -0.625f, 0, 0, iicon.getMinU(), iicon.getMaxV());
                addVertexWithOffsetAndUV(tieEnd, 0.625f, 0, 0, iicon.getMaxU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieEnd, -0.625f, 0, 0, iicon.getMaxU(), iicon.getMaxV());
                Tessellator.getInstance().draw();
                GL11.glTranslatef(0.3f,0,0);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(tieStart, 0.625f, 0, 0, iicon.getMinU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieStart, -0.625f, 0, 0, iicon.getMinU(), iicon.getMaxV());
                addVertexWithOffsetAndUV(tieEnd, 0.625f, 0, 0, iicon.getMaxU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieEnd, -0.625f, 0, 0, iicon.getMaxU(), iicon.getMaxV());
                Tessellator.getInstance().draw();
                GL11.glTranslatef(0.3f,0,0);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(tieStart, 0.625f, 0, 0, iicon.getMinU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieStart, -0.625f, 0, 0, iicon.getMinU(), iicon.getMaxV());
                addVertexWithOffsetAndUV(tieEnd, 0.625f, 0, 0, iicon.getMaxU(), iicon.getMinV());
                addVertexWithOffsetAndUV(tieEnd, -0.625f, 0, 0, iicon.getMaxU(), iicon.getMaxV());
                Tessellator.getInstance().draw();

                GL11.glPopMatrix();
            }

            if(item.getTagCompound().hasKey("rail")) {
                int[] color = {255,255,255};
                ItemStack rail = new ItemStack(item.getTagCompound().getCompoundTag("rail"));
                if(TextureManager.ingotColors.containsKey(rail)) {
                    color = TextureManager.ingotColors.get(rail);
                } else {
                    for (Map.Entry<ItemStack, int[]> e : TextureManager.ingotColors.entrySet()) {
                        if (e.getKey().getItem() ==rail.getItem() &&
                                e.getKey().getTagCompound() == rail.getTagCompound() &&
                                e.getKey().getItemDamage() == rail.getItemDamage()) {
                            color = TextureManager.ingotColors.get(e.getKey());
                            break;
                        }
                    }
                }

                GL11.glPushMatrix();
                GL11.glDisable(GL_TEXTURE_2D);
                GL11.glTranslatef(0f,0.02f,-0.05f);
                ModelRail.centerShading(-0.4f,color,30,true);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(start, -0.3f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(start, -0.4f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, -0.3f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, -0.4f, 0, 0, 0, 0);
                Tessellator.getInstance().draw();

                ModelRail.centerShading(-0.4f,color,30,false);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(start, -0.2f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(start, -0.3f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, -0.2f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, -0.3f, 0, 0, 0, 0);
                Tessellator.getInstance().draw();

                ModelRail.centerShading(0.4f,color,30,true);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(start, 0.5f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(start, 0.4f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, 0.5f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, 0.4f, 0, 0, 0, 0);
                Tessellator.getInstance().draw();

                ModelRail.centerShading(0.4f,color,30,false);
                Tessellator.getInstance().startDrawing(GL11.GL_QUAD_STRIP);
                addVertexWithOffsetAndUV(start, 0.4f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(start, 0.3f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, 0.4f, 0, 0, 0,0);
                addVertexWithOffsetAndUV(end, 0.3f, 0, 0, 0, 0);
                Tessellator.getInstance().draw();
                GL11.glEnable(GL_TEXTURE_2D);
                GL11.glColor4f(1,1,1,1);
                GL11.glPopMatrix();
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D,boundTexture);
        } else if(blockTextures.containsKey(item.getItem())) {
            GL11.glPushMatrix();

            if(blockTextures.get(item.getItem()).getIconResource()!=null &&
                    (!ClientProxy.hdTransportItems || blockTextures.get(item.getItem()).force2dItem(type))) {
                render2dItem(blockTextures.get(item.getItem()).getIconResource(), type);
                GL11.glPopMatrix();
                return;
            }
            switch (type){
                case EQUIPPED_FIRST_PERSON:{
                    break;
                }
                case INVENTORY:{break;}
                case EQUIPPED:{break;}
                case FIRST_PERSON_MAP:{break;}
                case ENTITY:{
                    GL11.glTranslatef(-0.5f,-0.4f,-0.5f);
                    break;
                }
                default:{//item frame case
                    break;
                }
            }
            GL11.glScalef(0.95f,0.95f,0.95f);
            GL11.glTranslatef(0,-0.1f,0);
            if(blockTextures.get(item.getItem()).host.tesr instanceof TileEntitySpecialRenderer){
                ((TileEntitySpecialRenderer)blockTextures.get(item.getItem()).host.tesr)
                        .render(blockTextures.get(item.getItem()),0,0,0,0,0,0);
            } else {
                blockTextures.get(item.getItem()).addInfoToCrashReport(null);
            }
            GL11.glPopMatrix();
            org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D,boundTexture);

        }

    }
    private static final Vec5f start = new Vec5f(0.625f, 0, 0, 0, 0), end = new Vec5f(-0.625f, 0, 0, 0, 0)
            ,tieStart = new Vec5f(0.625f*0.125f, 0, 0, 0, 0),tieEnd = new Vec5f(-0.625f*0.125f, 0, 0, 0, 0);




    private static class Model implements IModel {

        private final ResourceLocation modelloc;

        private Model(ResourceLocation rs){
            this.modelloc = rs;
        }

        @Override
        public Collection<ResourceLocation> getDependencies(){return Collections.emptyList();}

        @Override
        public IModelState getDefaultState(){
            return new IModelState() {
                @Override
                public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
                    return Optional.empty();
                }
            };
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> func){
            return new IBakedModel(){

                private ItemStack stack;
                private EntityLivingBase holder;

                @Override @Nonnull
                public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
                    return Collections.emptyList();
                }

                @Override @Nonnull
                public ItemCameraTransforms getItemCameraTransforms(){return ItemCameraTransforms.DEFAULT;}

                @Override
                public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType perspective){
                    renderItemModel(stack, perspective, holder);
                    return Pair.of(this, null);
                }

                @Override
                public boolean isBuiltInRenderer(){return true;}

                @Override @Nonnull
                public TextureAtlasSprite getParticleTexture(){return ModelLoader.White.INSTANCE;}


                @Override @Nonnull
                public ItemOverrideList getOverrides(){
                    return new ItemOverrideList(ItemOverrideList.NONE.getOverrides()){

                        @Override
                        public IBakedModel handleItemState(IBakedModel org_model, ItemStack itemstack, World world, EntityLivingBase entity){
                            stack=itemstack;
                            holder=entity;
                            return org_model;
                        }
                    };
                }

                @Override
                public boolean isGui3d(){return true;}

                @Override
                public boolean isAmbientOcclusion(){return false;}
            };
        }

        @Override
        public Collection<ResourceLocation> getTextures(){
            return Collections.singleton(modelloc);
        }
    }

}
