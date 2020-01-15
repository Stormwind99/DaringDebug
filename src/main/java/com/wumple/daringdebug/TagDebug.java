package com.wumple.daringdebug;

import java.util.Collection;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TagDebug
{
    /// Add ore dictionary debug text to advanced tooltips
    public static void addTooltips(ItemTooltipEvent event)
    {
        if (event.getFlags().isAdvanced() && (ConfigManager.Debugging.tagDebug.get() == true))
        {
            ItemStack stack = event.getItemStack();
            Item item = ((stack != null) && (!stack.isEmpty())) ? stack.getItem() : null;
            
            if (item != null)
            {
            	Collection<ResourceLocation> tags = ItemTags.getCollection().getOwningTags(item);
            
                event.getToolTip().add(new TranslationTextComponent("misc.daringdebug.tooltip.advanced.tags.header", tags.size()));

                for (ResourceLocation tag : tags)
                {
                    event.getToolTip().add(
                            new TranslationTextComponent("misc.daringdebug.tooltip.advanced.tags.entry", tag.toString()) );
                }
            }
        }
    }
}
