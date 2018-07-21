package com.wumple.daringdebug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES, updateJSON = Reference.UPDATEJSON, certificateFingerprint=Reference.FINGERPRINT, canBeDeactivated=true, clientSideOnly = true, acceptedMinecraftVersions = "[1.12,1.12.2]")
public class DaringDebug
{
    @Mod.Instance(Reference.MOD_ID)
    public static DaringDebug instance;
    
    @SidedProxy(clientSide = "com.wumple.daringdebug.ClientProxy", serverSide = "com.wumple.daringdebug.ServerProxy")
    public static ISidedProxy proxy;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
    	proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        if (logger == null)
        {
            logger = LogManager.getLogger(Reference.MOD_ID);
        }
        if (logger != null)
        {
            logger.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
            logger.warn("Expected " + event.getExpectedFingerprint() + " found " + event.getFingerprints().toString());
        }
    }
}
