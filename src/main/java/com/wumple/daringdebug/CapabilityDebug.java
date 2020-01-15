package com.wumple.daringdebug;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ObjectArrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CapabilityDebug
{
    /// Reflection Fields for private capability class fields
    protected static final Field capProviders;

    /// Look up the reflection Fields we need
    static
    {
        capProviders = ObfuscationReflectionHelper.findField(CapabilityManager.class, "providers");
    }

    /// Draw debug screen extras
    static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
    {
     	final Minecraft mc = Minecraft.getInstance();

        if (mc.gameSettings.showDebugInfo == true)
        {
            if (ConfigManager.Debugging.capabilitiesDebug.get() == true)
            {
                addCapTileEntityDebug(e);
                addCapEntityDebug(e);
                addCapChunkDebug(e);
                addCapWorldDebug(e);
            }
        }
    }

    public static void addTooltips(ItemTooltipEvent event)
    {
        if (event.getFlags().isAdvanced() && (ConfigManager.Debugging.capabilitiesDebug.get() == true))
        {
        	addCapTooltips(event);
        }
    }

    protected static void addCapTooltips(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        forEachCap(stack, name -> {
            event.getToolTip().add(new TranslationTextComponent("misc.daringdebug.tooltip.advanced.capability.entry", name));
        });
    }

    public static void addCapTileEntityDebug(RenderGameOverlayEvent.Text event)
    {
    	final Minecraft mc = Minecraft.getInstance();

        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK)
        {
        	final BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) mc.objectMouseOver;
            BlockPos blockpos = rayTraceResult.getPos();
            TileEntity te = (blockpos == null) ? null : mc.world.getTileEntity(blockpos);
            addCapsToDebugScreen(te, "misc.daringdebug.debug.capabilities.tileentity", event);
        }

    }

    public static void addCapChunkDebug(RenderGameOverlayEvent.Text event)
    {
     	final Minecraft mc = Minecraft.getInstance();

        Chunk chunk = mc.world.getChunkAt(mc.player.getPosition());
        addCapsToDebugScreen(chunk, "misc.daringdebug.debug.capabilities.chunk", event);
    }

    public static void addCapEntityDebug(RenderGameOverlayEvent.Text event)
    {
    	final Minecraft mc = Minecraft.getInstance();

        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY)
        {
        	final EntityRayTraceResult rayTraceResult = (EntityRayTraceResult) mc.objectMouseOver;
        	
            Entity entity = rayTraceResult.getEntity();
            addCapsToDebugScreen(entity, "misc.daringdebug.debug.capabilities.entity", event);
        }
    }

    public static void addCapWorldDebug(RenderGameOverlayEvent.Text event)
    {
     	final Minecraft mc = Minecraft.getInstance();
        World world = mc.world;
        addCapsToDebugScreen(world, "misc.daringdebug.debug.capabilities.world", event);
    }

    protected static void addCapsToDebugScreen(ICapabilityProvider stack, String lockey, RenderGameOverlayEvent.Text event)
    {
        forEachCap(stack, name -> event.getRight().add(I18n.format(lockey, name)));
    }

    @SuppressWarnings("unchecked")
    protected static void forEachCap(ICapabilityProvider stack, Consumer<String> block)
    {
        try
        {
            if (stack != null)
            {
                Object field = capProviders.get(CapabilityManager.INSTANCE);
                IdentityHashMap<String, Capability<?>> providers = (IdentityHashMap<String, Capability<?>>) field;

                if (providers != null)
                {
                    for (Iterator<Map.Entry<String, Capability<?>>> entries = providers.entrySet().iterator(); entries.hasNext();)
                    {
                        Map.Entry<String, Capability<?>> entry = entries.next();

                        // check null + 6 actual facings
                        Direction[] nullfacing = { null };
                        Direction[] facings = ObjectArrays.concat(nullfacing, Direction.values(), Direction.class);

                        String facingsString = null;

                        // iterate through all facings, checking for cap from each one
                        for (Direction facing : facings)
                        {
                        	LazyOptional<?> t = stack.getCapability(entry.getValue(), facing);
                            if (t.isPresent())
                            {
                                // build facings sting for more compact output (fewer lines)
                                if (facingsString == null)
                                {
                                    facingsString = new String();
                                }

                                if (facing == null)
                                {
                                    facingsString += "0";
                                }
                                else
                                {
                                    facingsString += facing.getName2().charAt(0);
                                }
                            }
                        }

                        // if any facing had the cap, then do it
                        if (facingsString != null)
                        {
                            String name = entry.getKey() + ":" + facingsString;
                            block.accept(name);
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}