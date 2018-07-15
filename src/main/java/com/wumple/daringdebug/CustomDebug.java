package com.wumple.daringdebug;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CustomDebug
{
   static final Minecraft mc = Minecraft.getMinecraft();

   @SubscribeEvent(priority = EventPriority.HIGH)
   static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
   { 
	   if (mc.gameSettings.showDebugInfo == true)
	   {
		   if (ModConfig.tileEntityDebug == true)
		   {
			   addTileEntityDebug(e);
		   }

		   if (ModConfig.entityDebug == true)
		   {
			   addEntityDebug(e);
		   }
		   
		   if (ModConfig.oreDictDebug == true)
		   {
			   addOreDictBlockDebug(e);
		   }
	   }
   }

   /*
    * Add TileEntity debug text to debug screen if looking at Block with a TileEntity
    */
   public static void addTileEntityDebug(RenderGameOverlayEvent.Text e)
   {          
       // tile entity
       if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
       {
           BlockPos blockpos = (mc.objectMouseOver == null) ? null : mc.objectMouseOver.getBlockPos();
           TileEntity te = (blockpos == null) ? null : mc.world.getTileEntity(blockpos);
           ResourceLocation loc = (te == null) ? null : TileEntity.getKey(te.getClass());
           String key = (loc == null) ? null : loc.toString();
           if (key != null)
           {
        	   e.getRight().add(I18n.format("misc.daringdebug.debug.tileentity", key));
           }
       }
   }
   
   /*
    * Add ore dictionary debug text to debug screen
    */  
   public static void addOreDictBlockDebug(RenderGameOverlayEvent.Text e)
   {         
       // tile entity
       if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
       {
           BlockPos blockpos = (mc.objectMouseOver == null) ? null : mc.objectMouseOver.getBlockPos();
           Block block = (blockpos == null) ? null : mc.world.getBlockState(blockpos).getBlock();

           int[] ids = (block != null) ? OreDictionary.getOreIDs(new ItemStack(block, 1)) : new int[0];

		   String key = null;
		   for (int id : ids)
		   {
			   String name = OreDictionary.getOreName(id);
			   if (key == null)
			   {
				   key = name;
			   }
			   else
			   {
				   key = key.concat(" ");
				   key = key.concat(name);
			   }
		   }
           
           if (key != null)
           {
        	   e.getRight().add(I18n.format("misc.daringdebug.debug.oredict.block", key));
           }
       }
   }
   
   /*
    * Add Entity debug text to debug screen if looking at an Entity
    */
   public static void addEntityDebug(RenderGameOverlayEvent.Text e)
   {    
       // entity
       if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit != null)
       {
    	   Entity entity = mc.objectMouseOver.entityHit;
    	   String name = (entity == null) ? null : EntityList.getEntityString(entity);
           if (name != null)
           {
        	   e.getRight().add(I18n.format("misc.daringdebug.debug.entity", name));
           }
       }
   }
   
   /*
    * Add ore dictionary debug text to advanced tooltips
    */   
   @SubscribeEvent
   public static void addOreDictTooltips(ItemTooltipEvent event)
   {
	   if (event.getFlags().isAdvanced() && (ModConfig.oreDictTooltips == true))
	   {
		   ItemStack stack = event.getItemStack();
		   int[] ids = OreDictionary.getOreIDs(stack);
		   if (ids.length > 0)
		   {
			   event.getToolTip().add(new TextComponentTranslation("misc.daringdebug.tooltip.advanced.oredict.header", ids.length).getUnformattedText());
		   
			   for (int id : ids)
			   {
				   event.getToolTip().add(new TextComponentTranslation("misc.daringdebug.tooltip.advanced.oredict.entry", OreDictionary.getOreName(id)).getUnformattedText());
			   }
		   }
	   }
   }
}