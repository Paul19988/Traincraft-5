/*******************************************************************************
 * Copyright (c) 2012 Mrbrutal. All rights reserved.
 * 
 * @name TrainCraft
 * @author Mrbrutal
 ******************************************************************************/

package train.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import train.blocks.generator.TileGeneratorDiesel;
import train.library.Info;
import train.render.models.blocks.ModelGeneratorDiesel;

public class RenderGeneratorDiesel extends TileEntitySpecialRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
		return true;
	}
	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
		return true;
	}
	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
		renderTileEntityAt(null,0,0,0,0);
	}

	private static final ModelGeneratorDiesel modelGenerator = new ModelGeneratorDiesel((float) (1.0 / 16.0));
	private static final ResourceLocation texture = new ResourceLocation(Info.resourceLocation,Info.modelTexPrefix + "generator_diesel.png");

	public RenderGeneratorDiesel() {
	}

	public void render(TileEntity var1, double x, double y, double z) {
		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);

		if(var1==null || var1.getWorldObj()==null){
			GL11.glTranslatef(0, -0.5F, 0);
			GL11.glScalef(0.85f,0.85f,0.85f);
		} else {
			GL11.glTranslatef(0.5F, 0.0F, 0.5F);
		}
		modelGenerator.render(0.0625F, var1==null?0:((TileGeneratorDiesel) var1).facing);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
		render(var1, var2, var4, var6);
	}
}
