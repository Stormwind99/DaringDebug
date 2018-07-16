package com.wumple.daringdebug;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CapabilityDebug
{
    static final Minecraft mc = Minecraft.getMinecraft();
    
    /*
     * Reflection Fields for private capability class fields
     */
    public static final Field itemStackCapabilities;
    public static final Field entityCapabilities;
    public static final Field tileEntityCapabilities;
    public static final Field chunkCapabilities;
    public static final Field worldCapabilities;
    public static final Field capNames;

    /*
     * Look up the reflection Fields we need
     */
    static
    {
        itemStackCapabilities = ReflectionHelper.findField(ItemStack.class, new String[] { "capabilities"} );
        entityCapabilities = ReflectionHelper.findField(Entity.class, new String[] { "capabilities"} );
        tileEntityCapabilities = ReflectionHelper.findField(TileEntity.class, new String[] { "capabilities"} );
        chunkCapabilities = ReflectionHelper.findField(Chunk.class, new String[] { "capabilities"} );
        worldCapabilities = ReflectionHelper.findField(World.class, new String[] { "capabilities"} );
        capNames = ReflectionHelper.findField(CapabilityDispatcher.class, new String[] { "names" } );
    }

    /*
     * Draw debug screen extras
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
    {
        if (mc.gameSettings.showDebugInfo == true)
        {
            if (ModConfig.capabilitiesDebug == true)
            {
                addCapTileEntityDebug(e);
                addCapEntityDebug(e);
                addCapChunkDebug(e);
                addCapWorldDebug(e);
            }
        }
    }

    @SubscribeEvent
    public static void addCapabilityTooltips(ItemTooltipEvent event)
    {
        if (event.getFlags().isAdvanced() && (ModConfig.capabilitiesDebug == true))
        {
            ItemStack stack = event.getItemStack();
            try
            {
                if (stack != null)
                {
                    Object field = itemStackCapabilities.get(stack);
                    if (field != null)
                    {
                        CapabilityDispatcher capabilities = (CapabilityDispatcher) field;
                        Object namesField = capNames.get(capabilities);
                        if (namesField != null)
                        {
                            String names[] = (String[]) namesField;

                            for (String name : names)
                            {
                                event.getToolTip()
                                        .add(new TextComponentTranslation("misc.daringdebug.tooltip.advanced.capability.entry", name).getUnformattedText());
                            }
                        }
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void addCapTileEntityDebug(RenderGameOverlayEvent.Text event)
    {
        // tile entity
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
        {
            BlockPos blockpos = (mc.objectMouseOver == null) ? null : mc.objectMouseOver.getBlockPos();
            TileEntity te = (blockpos == null) ? null : mc.world.getTileEntity(blockpos);

            try
            {
                if (te != null)
                {
                    Object field = tileEntityCapabilities.get(te);
                    addToDebugScreen((CapabilityDispatcher) field, "misc.daringdebug.debug.capabilities.tileentity", event);
                }

            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void addCapChunkDebug(RenderGameOverlayEvent.Text event)
    {
            Chunk chunk = mc.world.getChunkFromBlockCoords(mc.player.getPosition());
                    
            try
            {
                if (chunk != null)
                {
                    Object field = chunkCapabilities.get(chunk);
                    addToDebugScreen((CapabilityDispatcher) field, "misc.daringdebug.debug.capabilities.chunk", event);
                }

            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
    }
    
    public static void addCapEntityDebug(RenderGameOverlayEvent.Text event)
    {
        // entity
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit != null)
        {
            Entity entity = mc.objectMouseOver.entityHit;

            try
            {
                if (entity != null)
                {
                    Object field = entityCapabilities.get(entity);
                    addToDebugScreen((CapabilityDispatcher) field, "misc.daringdebug.debug.capabilities.entity", event);
                }

            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static void addCapWorldDebug(RenderGameOverlayEvent.Text event)
    {
        World world = mc.world;
            try
            {
                if (world != null)
                {
                    Object field = worldCapabilities.get(world);
                    addToDebugScreen((CapabilityDispatcher) field, "misc.daringdebug.debug.capabilities.world", event);
                }

            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
    }
    
    protected static void addToDebugScreen(CapabilityDispatcher capabilities, String lockey, RenderGameOverlayEvent.Text event) throws IllegalArgumentException, IllegalAccessException
    {
        if (capabilities != null)
        {
            Object namesField = capNames.get(capabilities);
            if (namesField != null)
            {
                String names[] = (String[]) namesField;

                for (String name : names)
                {
                    event.getRight().add(I18n.format("misc.daringdebug.debug.capabilities.tileentity", name));
                }
            }
        }
    }

    // TODO: World and Chunk?
}