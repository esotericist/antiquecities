package org.esotericist.antiquecities.proxy;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.TextureSet;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {

    private TileAPI api;

    private TextureSet newTile(String name) {
        
        String texturepath = "textures/gui/tiles/"+name+".png";
        TextureSet textureSet = api.registerTextureSet(name,
            new ResourceLocation("antiquecities", texturepath));
        api.setCustomTileTexture(name+"Tile", textureSet);
        return textureSet;
    }

    @Override
    public void preInit (FMLPreInitializationEvent event) {
        super.preInit(event);        
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

        api = AtlasAPI.getTileAPI();
        
        
        TextureSet street = newTile("street");
        TextureSet bridgexopen = newTile("bridge_openx");
        TextureSet bridgezopen = newTile("bridge_openz");
        TextureSet bridgexcovered = newTile("bridge_coveredx");
        TextureSet bridgezcovered = newTile("bridge_coveredz");
        
        
        TextureSet buildingfloor1 = newTile("buildingfloor1");
        TextureSet buildingfloor2 = newTile("buildingfloor2");
        TextureSet buildingfloor3 = newTile("buildingfloor3");
        TextureSet buildingfloor4 = newTile("buildingfloor4");
        TextureSet buildingtall = newTile("buildingtall");
        TextureSet buildingtallroof = newTile("buildingtallroof");
        
        TextureSet ruinfloor1 = newTile("ruinfloor1");
        TextureSet ruinfloor2 = newTile("ruinfloor2");
        TextureSet ruinfloor3 = newTile("ruinfloor3");
        TextureSet ruinfloor4 = newTile("ruinfloor4");
        
        TextureSet highwayx = newTile("highwayx");
        TextureSet highwayz = newTile("highwayz");

        TextureSet fountain = newTile("fountain");
        TextureSet park = newTile("park");
        
        TextureSet trainrampleft = newTile("trainrampleft");
        TextureSet trainrampright = newTile("trainrampright");
        TextureSet trainstationopen = newTile("trainstationopen");
        TextureSet trainstationroofed = newTile("trainstationroofed");


        TextureSet streetocc = newTile("streetoccluded");
        TextureSet bridgexopenocc = newTile("bridge_openxoccluded");
        TextureSet bridgezopenocc = newTile("bridge_openzoccluded");
        TextureSet bridgexcoveredocc = newTile("bridge_coveredxoccluded");
        TextureSet bridgezcoveredocc = newTile("bridge_coveredzoccluded");

        TextureSet buildingfloor1occ = newTile("buildingfloor1occluded");
        TextureSet buildingfloor2occ = newTile("buildingfloor2occluded");
        TextureSet buildingfloor3occ = newTile("buildingfloor3occluded");
        TextureSet buildingfloor4occ = newTile("buildingfloor4occluded");
        TextureSet buildingtallocc = newTile("buildingtalloccluded");

        TextureSet trainrampleftocc = newTile("trainrampleftoccluded");
        TextureSet trainramprightocc = newTile("trainramprightoccluded");
        TextureSet trainstationopenocc = newTile("trainstationopenoccluded");
        TextureSet trainstationroofedocc = newTile("trainstationroofedoccluded");

        TextureSet highwayxocc = newTile("highwayxoccluded");
        TextureSet highwayzocc = newTile("highwayzoccluded");

        TextureSet fountainocc = newTile("fountainoccluded");
        TextureSet parkocc = newTile("parkoccluded");
        
        
        //trainramp.stitchToMutual(trainstationopen, trainstationroofed);
        
        streetocc.stitchToMutual(street);
        
        street.stitchToMutual(bridgexopen, bridgexcovered, bridgezopen, bridgezcovered,
            bridgexopenocc, bridgexcoveredocc, bridgezopenocc, bridgezcoveredocc);
        
        
        /*
        TextureSet textureSet = api.registerTextureSet("street",
            new ResourceLocation("antiquecities",
            "textures/gui/tiles/street.png"));
        api.setCustomTileTexture("streetTile", textureSet);

        TextureSet textureSet2 = api.registerTextureSet("building",
            new ResourceLocation("antiquecities",
            "textures/gui/tiles/citybuilding.png"));
        api.setCustomTileTexture("buildingTile", textureSet2);
        */

        super.postInit(event);
        
    }
}