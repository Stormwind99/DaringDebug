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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CapabilityDebug
{
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /*
     * Reflection Fields for private capability class fields
     */
    protected static final Field capProviders;

    /*
     * Look up the reflection Fields we need
     */
    static
    {
        capProviders = ReflectionHelper.findField(CapabilityManager.class, new String[] { "providers" });
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
            forEachCap(stack, name -> {
                event.getToolTip().add(new TextComponentTranslation("misc.daringdebug.tooltip.advanced.capability.entry", name).getUnformattedText());
            });
        }
    }

    public static void addCapTileEntityDebug(RenderGameOverlayEvent.Text event)
    {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
        {
            BlockPos blockpos = (mc.objectMouseOver == null) ? null : mc.objectMouseOver.getBlockPos();
            TileEntity te = (blockpos == null) ? null : mc.world.getTileEntity(blockpos);
            addCapsToDebugScreen(te, "misc.daringdebug.debug.capabilities.tileentity", event);
        }

    }

    public static void addCapChunkDebug(RenderGameOverlayEvent.Text event)
    {
        Chunk chunk = mc.world.getChunk(mc.player.getPosition());
        addCapsToDebugScreen(chunk, "misc.daringdebug.debug.capabilities.chunk", event);
    }

    public static void addCapEntityDebug(RenderGameOverlayEvent.Text event)
    {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit != null)
        {
            Entity entity = mc.objectMouseOver.entityHit;
            addCapsToDebugScreen(entity, "misc.daringdebug.debug.capabilities.entity", event);
        }
    }

    public static void addCapWorldDebug(RenderGameOverlayEvent.Text event)
    {
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
                        EnumFacing[] nullfacing = { null };
                        EnumFacing[] facings = ObjectArrays.concat(nullfacing, EnumFacing.VALUES, EnumFacing.class);

                        String facingsString = null;

                        // iterate through all facings, checking for cap from each one
                        for (EnumFacing facing : facings)
                        {
                            if (stack.hasCapability(entry.getValue(), facing))
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
                                    ;
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