package com.wumple.daringdebug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID)
public class DebugOutput
{
	/// Draw debug screen extras
	@SubscribeEvent(priority = EventPriority.HIGH)
	static public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
	{
    	final Minecraft mc = Minecraft.getInstance();

        if (mc.gameSettings.showDebugInfo == true)
        {
        	e.getRight().add("");
        	e.getRight().add(I18n.format("misc.daringdebug.debug.title"));
        	EntityDebug.onDrawOverlay(e);
        	CapabilityDebug.onDrawOverlay(e);
        }
	}
	
    @SubscribeEvent
    public static void addDebugTooltips(ItemTooltipEvent event)
    {
    	TagDebug.addTooltips(event);
    	CapabilityDebug.addTooltips(event);
    }
}
