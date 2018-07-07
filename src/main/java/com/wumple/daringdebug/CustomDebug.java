package com.wumple.daringdebug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CustomDebug
{
   static final Minecraft mc = Minecraft.getMinecraft();

   @SubscribeEvent(priority = EventPriority.HIGH)
   static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
   {
	   if (mc.gameSettings.showDebugInfo == true)
	   {
		   addTileEntityDebug(e);
	   }
   }

   /*
    * Add TileEntity debug text to debug screen if looking at Block with a TileEntity
    */
   static public void addTileEntityDebug(RenderGameOverlayEvent.Text e)
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
        	   e.getRight().add(I18n.format("daringdebug.tileentity", key));
           }
       }	   
   }
}