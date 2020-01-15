package com.wumple.daringdebug;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

// See
// https://github.com/McJty/YouTubeModding14/blob/master/src/main/java/com/mcjty/mytutorial/Config.java
// https://wiki.mcjty.eu/modding/index.php?title=Tut14_Ep6

@Mod.EventBusSubscriber
public class ConfigManager
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static final String CATEGORY_DEBUGGING = "Debugging";

	public static class Debugging
	{
		public static ForgeConfigSpec.BooleanValue tileEntityDebug;
		public static ForgeConfigSpec.BooleanValue entityDebug;
		public static ForgeConfigSpec.BooleanValue tagDebug;
		public static ForgeConfigSpec.BooleanValue capabilitiesDebug;
		
		private static void setupConfig()
		{
			// @Config.Comment("Debugging options")
			COMMON_BUILDER.comment("Debugging settings").push(CATEGORY_DEBUGGING);

			// @Name("TileEntity on debug screen")
			tileEntityDebug = COMMON_BUILDER.comment("Show TileEntity name on debug screen for block under cursor").define("tileEntityDebug",
					true);
			
			// @Name("Entity on debug screen")
			entityDebug = COMMON_BUILDER.comment("Show Entity name on debug screen for entity under cursor").define("entityDebug",
					true);

			// @Name("OreDict debugging")
			tagDebug = COMMON_BUILDER.comment("Show OreDict entries on advanced tooltips and debug screen for thing under cursor").define("tagDebug",
					true);

			// @Name("Capabilities debugging")
			capabilitiesDebug = COMMON_BUILDER.comment("Show capabilities on advanced tooltips and debug screen for thing under cursor").define("capabilitiesDebug",
					false);

			COMMON_BUILDER.pop();
		}
	}
	
	static
	{
		Debugging.setupConfig();

		COMMON_CONFIG = COMMON_BUILDER.build();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path)
	{

		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();

		configData.load();
		spec.setConfig(configData);
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent)
	{
	}

	@SubscribeEvent
	public static void onReload(final ModConfig.ConfigReloading configEvent)
	{
	}

	public static void register(final ModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
		context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);

		loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-client.toml"));
		loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-common.toml"));
	}

}