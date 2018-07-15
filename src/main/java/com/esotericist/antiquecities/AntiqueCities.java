package org.esotericist.antiquecities;

import org.esotericist.antiquecities.proxy.CommonProxy;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.TextureSet;


import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
//import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.PutBiomeTilePacket;

import mcjty.lostcities.api.ILostCities;
import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.LostCityEvent;
import mcjty.lostcities.api.RailChunkType;



import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.Railway;

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
import java.util.ArrayList;

@Mod(modid = AntiqueCities.MODID, name = AntiqueCities.NAME, version = AntiqueCities.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]", dependencies = AntiqueCities.dependencies)
@Mod.EventBusSubscriber
public class AntiqueCities
{
    public static final String MODID = "antiquecities";
    public static final String NAME = "AntiqueCities";
    public static final String VERSION = "1.0";
    public static final String dependencies = "required-after:antiqueatlas@[1.12.2-4.4.9,);required-after:lostcities@1.12-2.0.10,)";

    private static Logger logger;
    
    private static final ArrayList<String> occludable = new ArrayList<String>();
    private static final ArrayList<String> occluding = new ArrayList<String>();
    private static final ArrayList<String> trainpart = new ArrayList<String>();
    
    private void putTile(TileAPI tiles, World world, String name, int X, int Z, boolean occluded) {
        if (occluded) {
            name = name+"occluded";
            logger.info("occluded tile: "+name+", X:"+X+", Z"+Z);
        }
        tiles.putCustomGlobalTile(world, name+"Tile", X, Z);
    }
    
    @SubscribeEvent
    public void CityGenEvent(LostCityEvent.PostGenCityChunkEvent event )
    {
        TileAPI tiles = AtlasAPI.getTileAPI();
        
        World world = event.getWorld();
        int dimension = world.provider.getDimension();
        
        int X = event.getChunkX();// * 16;
        int Z = event.getChunkZ();// * 16;
        
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
        int tileID = data.getBiomeIdAt(dimension, X, Z+1);
        String adjtilename = "";
        boolean tiledownistall = false;
        if (tileID != -1) {
            adjtilename = ExtTileIdMap.instance().getPseudoBiomeName(tileID);
            tiledownistall = occluding.contains(adjtilename);
            //logger.info("below tileID: "+tileID+ ", name:"+adjtilename+", tall:"+tiledownistall);
        }
        
        tileID = data.getBiomeIdAt(dimension, X, Z-1);
        boolean tileupisoccludable = false;
        if (tileID != -1) {
            adjtilename = ExtTileIdMap.instance().getPseudoBiomeName(tileID);
            if (occludable.contains(adjtilename)) {
                tileupisoccludable = true;
                adjtilename = adjtilename.substring(0, adjtilename.length() - 4);  
                //ExtTileTextureMap.instance().getTexture(adjtilename).name;
            } else {
            adjtilename= "";
            //logger.info("above tileID: "+tileID+", name:"+adjtilename+", X: "+X+", Z: "+Z);
            }
        } else {
            adjtilename = "";
        }

        
        ILostChunkGenerator generator = event.getGenerator();
        LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(dimension);
        
        ILostChunkInfo chunkinfo = generator.getChunkInfo(X, Z);
        BuildingInfo info = BuildingInfo.getBuildingInfo(X, Z, provider);
        
        String buildingtype = chunkinfo.getBuildingType();
        int floors = chunkinfo.getNumFloors();
        RailChunkType railtype = chunkinfo.getRailType();
        
        if (buildingtype != null) {
            if (floors < 3) {
                putTile(tiles, world, "buildingshort", X, Z, tiledownistall);
            } else if (floors < 6) {
                putTile(tiles, world, "buildingmedium", X, Z, tiledownistall);
            } else {
                putTile(tiles, world, "buildingtall", X, Z, tiledownistall);
                logger.info("X: "+X+", Z:"+Z+", up: "+adjtilename+", occludable:"+tileupisoccludable);
                if (tileupisoccludable) {
                    tiles.deleteCustomGlobalTile(world, X, Z-1);
                    putTile(tiles, world, adjtilename, X, Z-1, true);
                    logger.info("X: "+X+", Z:"+Z+", "+adjtilename);
                } else {
                    putTile(tiles, world, "buildingtallroof", X, Z-1, false);
                }
            }
        } else {
            switch (railtype) {
                case STATION_SURFACE: 
                    putTile(tiles, world, "trainstationroofed", X, Z, tiledownistall);
                    break;
                case STATION_EXTENSION_SURFACE: 
                    putTile(tiles, world, "trainstationopen", X, Z, tiledownistall);
                    break;
                case GOING_DOWN_TWO_FROM_SURFACE:
                case GOING_DOWN_ONE_FROM_SURFACE:
                //case GOING_DOWN_FURTHER:
                    Railway.RailChunkInfo railInfo = 
                        Railway.getRailChunkType(X, Z, provider, info.profile);
                    if (railInfo.getDirection() == Railway.RailDirection.WEST) {
                        putTile(tiles, world, "trainrampleft", X, Z, tiledownistall);
                    } else {
                        putTile(tiles, world, "trainrampright", X, Z, tiledownistall);
                    }
                    break;
                default:
                    putTile(tiles, world, "street", X, Z, tiledownistall);
            }
                    
        }
        
        /*        
        if (generator.getChunkInfo(X, Z).getBuildingType() != null) {
            tiles.putCustomGlobalTile(world, "buildingTile", X, Z);
        } else {
            tiles.putCustomGlobalTile(world, "streetTile", X, Z);
        }
        */
        
        //logger.info("CityGen: X" + X + ", Z:" + Z +"; " + chunkinfo.getBuildingType());
        
    }
    
    @SidedProxy(clientSide="org.esotericist.antiquecities.proxy.ClientProxy", serverSide="org.esotericist.antiquecities.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("antique cities, initializing");
        proxy.init(event);
    }
    
    @EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        occludable.add("buildingshortTile");
        occludable.add("buildingmediumTile");
        occludable.add("buildingtallTile");
        occludable.add("trainrampleftTile");
        occludable.add("trainramprightTile");
        occludable.add("trainstationopenTile");
        occludable.add("trainstationroofedTile");
        occludable.add("street");
        
        occluding.add("buildingtallTile");
        occluding.add("buildingtalloccludedTile");
        
        trainpart.add("trainstationopenTile");
        trainpart.add("trainstationroofedTile");
        trainpart.add("trainrampleftTile");
        trainpart.add("trainramprightTile");
        trainpart.add("trainstationopenoccludedTile");
        trainpart.add("trainstationroofedoccludedTile");
        trainpart.add("trainrampleftoccludedTile");
        trainpart.add("trainramprightoccludedTile");
        
        
        proxy.postInit(event);
    
    }
    
}
