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
        TextureSet buildingshort = newTile("buildingshort");
        TextureSet buildingmedium = newTile("buildingmedium");
        TextureSet buildingtall = newTile("buildingtall");
        TextureSet buildingtallroof = newTile("buildingtallroof");
        TextureSet trainrampleft = newTile("trainrampleft");
        TextureSet trainrampright = newTile("trainrampright");
        TextureSet trainstationopen = newTile("trainstationopen");
        TextureSet trainstationroofed = newTile("trainstationroofed");

        TextureSet streetocc = newTile("streetoccluded");
        TextureSet buildingshortocc = newTile("buildingshortoccluded");
        TextureSet buildingmediumocc = newTile("buildingmediumoccluded");
        TextureSet buildingtallocc = newTile("buildingtalloccluded");
        TextureSet trainrampleftocc = newTile("trainrampleftoccluded");
        TextureSet trainramprightocc = newTile("trainramprightoccluded");
        TextureSet trainstationopenocc = newTile("trainstationopenoccluded");
        TextureSet trainstationroofedocc = newTile("trainstationroofedoccluded");
        
        //trainramp.stitchToMutual(trainstationopen, trainstationroofed);
        
        
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