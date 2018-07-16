package org.esotericist.antiquecities;

import org.esotericist.antiquecities.proxy.CommonProxy;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.TextureSet;


import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
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

import mcjty.lostcities.dimensions.world.lost.cityassets.*;

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
import org.apache.commons.lang3.StringUtils;

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
        }
        tiles.putCustomGlobalTile(world, name+"Tile", X, Z);
    }
    
    private String getTile(int dimension, int X, int Z) {
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
        int tileID = data.getBiomeIdAt(dimension, X, Z);
        String tilename = "";
        if (tileID != -1) {
            tilename = ExtTileIdMap.instance().getPseudoBiomeName(tileID);
            StringUtils.removeEnd(tilename, "Tile");
        }
        return tilename;
    }
    
    @SubscribeEvent
    public void worldGenEvent(LostCityEvent.PreExplosionEvent event )
    {
        TileAPI tiles = AtlasAPI.getTileAPI();
        
        World world = event.getWorld();
        int dimension = world.provider.getDimension();
        
        int X = event.getChunkX();
        int Z = event.getChunkZ();
        
        
        String belowtile = getTile(dimension, X, Z+1);
        boolean tiledownistall = occluding.contains(belowtile);
        
        String abovetile = getTile(dimension, X, Z-1);
        boolean tileupisoccludable = occludable.contains(abovetile);
        
        String thistile = getTile(dimension, X, Z);
        
        ILostChunkGenerator generator = event.getGenerator();
        LostCityChunkGenerator provider = WorldTypeTools.getChunkGenerator(dimension);
        
        ILostChunkInfo chunkinfo = generator.getChunkInfo(X, Z);

        BuildingInfo info = BuildingInfo.getBuildingInfo(X, Z, provider);
        
        String buildingtype = chunkinfo.getBuildingType();
        int floors = chunkinfo.getNumFloors();
        RailChunkType railtype = chunkinfo.getRailType();
        int citylevel = info.cityLevel;
        
        if (chunkinfo.isCity())
        {
            if (buildingtype != null) {
                String prefix = "building";
                if (chunkinfo.getRuinLevel() > 0) {
                    prefix = "ruin";
                }
                switch (floors) {
                    case 1:
                        putTile(tiles, world, prefix+"floor1", X, Z, tiledownistall);
                        break;
                    case 2:
                        putTile(tiles, world, prefix+"floor2", X, Z, tiledownistall);
                        break;
                    case 3:
                        putTile(tiles, world, prefix+"floor3", X, Z, tiledownistall);
                        break;
                    case 4:
                        putTile(tiles, world, prefix+"floor4", X, Z, tiledownistall);
                        break;
                    case 5:
                        putTile(tiles, world, prefix+"floor4", X, Z, tiledownistall);
                        break;
                    default:
                        putTile(tiles, world, prefix+"tall", X, Z, tiledownistall);
                        if ((tileupisoccludable) && (prefix == "building")) {
                            tiles.deleteCustomGlobalTile(world, X, Z-1);
                            putTile(tiles, world, abovetile, X, Z-1, true);
                        } else {
                            putTile(tiles, world, "buildingtallroof", X, Z-1, false);
                        }
                        break;
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
                        Railway.RailChunkInfo railInfo = 
                            Railway.getRailChunkType(X, Z, provider, info.profile);
                        if (railInfo.getDirection() == Railway.RailDirection.WEST) {
                            putTile(tiles, world, "trainrampleft", X, Z, tiledownistall);
                        } else {
                            putTile(tiles, world, "trainrampright", X, Z, tiledownistall);
                        }
                        break;
                    default:
                        BuildingInfo.StreetType streetType = info.streetType;
                        BuildingPart fountainType = info.fountainType;
                        BuildingPart parkType = info.parkType;
                        
                        if (streetType == BuildingInfo.StreetType.PARK ) {
                            if (StringUtils.lowerCase(parkType.getName()).contains("fountain")) {
                                putTile(tiles, world, "fountain", X, Z, tiledownistall);
                            } else {
                                putTile(tiles, world, "park", X, Z, tiledownistall);
                            }
                        } else if (fountainType != null) {
                            putTile(tiles, world, "fountain", X, Z, tiledownistall);
                        } else {
                            putTile(tiles, world, "street", X, Z, tiledownistall);
                        }
                }
         
            }           

        } else {
            BuildingPart bridgex = info.hasXBridge(provider);
            BuildingPart bridgez = info.hasZBridge(provider);
            
            
            if (bridgex != null) {
                putTile(tiles, world, bridgex.getName()+"x", X, Z, tiledownistall);
            } else if (bridgez != null) {
                putTile(tiles, world, bridgez.getName()+"z", X, Z, tiledownistall);
            }
        }

        int highwayx = info.getHighwayXLevel();
        int highwayz = info.getHighwayZLevel();
        String highwaytile = "";
        
        if ((highwayx > highwayz) && (highwayx >= citylevel)) {
            highwaytile = "highwayx";
        } else if ((highwayz > highwayx) && (highwayz >= citylevel)) {
            highwaytile = "highwayz";
        } else if ((highwayx == highwayz) && (highwayx >= citylevel)) {
            highwaytile = "highwayintersection";
        }
        
        if (highwaytile != "") {
            putTile(tiles, world, highwaytile, X, Z, tiledownistall);
        }
        
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
        occludable.add("buildingfloor1");
        occludable.add("buildingfloor2");
        occludable.add("buildingfloor3");
        occludable.add("buildingfloor4");
        occludable.add("buildingtall");
        occludable.add("trainrampleft");
        occludable.add("trainrampright");
        occludable.add("trainstationopen");
        occludable.add("trainstationroofed");
        occludable.add("street");
        occludable.add("bridgexopen");
        occludable.add("bridgezopen");
        occludable.add("bridgexcovered");
        occludable.add("bridgezcovered");
        occludable.add("highwayx");
        occludable.add("highwayz");
        occludable.add("highwayintersection");
        occludable.add("park");
        occludable.add("fountain");
        occludable.add("ruinfloor1");
        occludable.add("ruinfloor2");
        occludable.add("ruinfloor3");
        occludable.add("ruinfloor4");
        occludable.add("ruintall");
        
        
        occluding.add("buildingtall");
        occluding.add("buildingtalloccluded");
        
        trainpart.add("trainstationopen");
        trainpart.add("trainstationroofed");
        trainpart.add("trainrampleft");
        trainpart.add("trainrampright");
        trainpart.add("trainstationopenoccluded");
        trainpart.add("trainstationroofedoccluded");
        trainpart.add("trainrampleftoccluded");
        trainpart.add("trainramprightoccluded");
        
        
        proxy.postInit(event);
    
    }
    
}
