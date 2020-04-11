package com.wumple.daringdebug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class EntityDebug
{
    /// Draw debug screen extras
    static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
    {
    	final Minecraft mc = Minecraft.getInstance();

        if (mc.gameSettings.showDebugInfo == true)
        {
            if (ConfigManager.Debugging.tileEntityDebug.get() == true)
            {
                addTileEntityDebug(e);
            }
        }
    }

    /// Add TileEntity debug text to debug screen if looking at Block with a TileEntity
    public static void addTileEntityDebug(RenderGameOverlayEvent.Text e)
    {
    	final Minecraft mc = Minecraft.getInstance();

        // tile entity
        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK)
        {
            final BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) mc.objectMouseOver;
            BlockPos blockpos = rayTraceResult.getPos();
            
            TileEntity te = (blockpos == null) ? null : mc.world.getTileEntity(blockpos);
            ResourceLocation loc = (te == null) ? null :  te.getType().getRegistryName();
            String key = (loc == null) ? null : loc.toString();
            if (key != null)
            {
                e.getRight().add(I18n.format("misc.daringdebug.debug.tileentity", key));
            }
        }
    }
}
