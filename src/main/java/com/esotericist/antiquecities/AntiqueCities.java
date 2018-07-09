package com.esotericist.antiqueatlas;


import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.TextureSet;

import mcjty.lostcities.api.ILostCities;
import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.LostCityEvent;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AntiqueCities.MODID, name = AntiqueCities.NAME, version = AntiqueCities.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]", dependencies = AntiqueCities.dependencies)
@Mod.EventBusSubscriber
public class AntiqueCities
{
    public static final String MODID = "antiquecities";
    public static final String NAME = "AntiqueCities";
    public static final String VERSION = "1.0";
    public static final String dependencies = "required-after:antiqueatlas@[1.12.2-4.4.9,);required-after:lostcities@1.12-2.0.10,)";

    private static Logger logger;
    
    
    @SubscribeEvent
    public void CityGenEvent(LostCityEvent.PostGenCityChunkEvent event )
    {
        TileAPI tiles = AtlasAPI.getTileAPI();
        
        World world = event.getWorld();
        int X = event.getChunkX();// * 16;
        int Z = event.getChunkZ();// * 16;
        ILostChunkGenerator generator = event.getGenerator();
        //ILostChunkInfo chunkinfo = 
        if (generator.getChunkInfo(X, Z).getBuildingType() != null) {
            tiles.putCustomGlobalTile(world, "buildingTile", X, Z);
        } else {
            tiles.putCustomGlobalTile(world, "streetTile", X, Z);
        }
        
        //logger.info("CityGen: X" + X + ", Z:" + Z +"; " + chunkinfo.getBuildingType());
        
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("antique cities, initializing");
    }
    
    @EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        if (event.getSide().isClient()) {
            TileAPI api = AtlasAPI.getTileAPI();
            TextureSet textureSet = api.registerTextureSet("street",
                new ResourceLocation("antiquecities",
                "textures/gui/tiles/street.png"));
            api.setCustomTileTexture("streetTile", textureSet);
            TextureSet textureSet2 = api.registerTextureSet("building",
                new ResourceLocation("antiquecities",
                "textures/gui/tiles/citybuilding.png"));
            api.setCustomTileTexture("buildingTile", textureSet2);
        
        }
    }
    
}
