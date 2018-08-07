package com.wumple.daringdebug;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
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
public class OreDictDebug
{
    static final Minecraft mc = Minecraft.getMinecraft();
    
    /*
     * Draw debug screen extras
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
    {
        if (mc.gameSettings.showDebugInfo == true)
        {
            if (ModConfig.oreDictDebug == true)
            {
                addOreDictBlockDebug(e);
            }
        }
    }

     /*
     * Add ore dictionary debug text to advanced tooltips
     */
    @SubscribeEvent
    public static void addOreDictTooltips(ItemTooltipEvent event)
    {
        if (event.getFlags().isAdvanced() && (ModConfig.oreDictDebug == true))
        {
            ItemStack stack = event.getItemStack();
            int[] ids = ((stack != null) && !stack.isEmpty()) ? OreDictionary.getOreIDs(stack) : new int[0];
            if (ids.length > 0)
            {
                event.getToolTip().add(new TextComponentTranslation("misc.daringdebug.tooltip.advanced.oredict.header", ids.length).getUnformattedText());

                for (int id : ids)
                {
                    event.getToolTip().add(
                            new TextComponentTranslation("misc.daringdebug.tooltip.advanced.oredict.entry", OreDictionary.getOreName(id)).getUnformattedText());
                }
            }
        }
    }
    
    /*
     * Add ore dictionary debug text to debug screen
     */
    public static void addOreDictBlockDebug(RenderGameOverlayEvent.Text e)
    {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
        {
            BlockPos blockpos = (mc.objectMouseOver == null) ? null : mc.objectMouseOver.getBlockPos();
            Block block = (blockpos == null) ? null : mc.world.getBlockState(blockpos).getBlock();

            ItemStack stack = (block != null) ? new ItemStack(block, 1) : null;
            int[] ids = ((stack != null) && !stack.isEmpty()) ? OreDictionary.getOreIDs(stack) : new int[0];

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
}
