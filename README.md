# antiquecities
compatibility mod for antique atlas and lost cities

it isn't pretty as pretty as it could, it doesn't support all of the lost cities things, but it's more than we had to begin with.

as of 1.1, it is server safe. as of 1.2, it's core feature complete (with caveats). as of 1.2.1, it's 'good enough' for general use in my estimation.

# currently supported

*  streets
*  bridges (currently stitching weird with streets, pending API adjustments from antique atlas)
*  single-tile buildings (with a range of sizes), with some contextual occluding other tiles for really tall buildings
*  ruins (with a range of sizes), includes contextual occlusion
*  highways (fairly robust, although explosions have a tendency to knock out bits that still show on the map)
*  train stations
*  downard rail sections adjacent to train stations
*  partial park/fountain support 

# currently unsupported but planned eventually

*  multipart buildings (doing this right will require a lot of tiles, because we can't trust antique atlas stitching for this)
*  more data-driven assets (a lot of messy scrawl-by-hand at present)
*  more park/fountain assets (pending lost cities API support)
*  Make bridges blend better with water (pending antique atlas API support)
*  consider support for other events (antique atlas has the ability to mark surface lava pools, but that apparently gets superceded by antique cities' custom tiles, fixing this may require borrowing antique atlas' structure watcher infrastructure)

# known issues

* parks and fountains aren't being consistently identified (lost cities API issue)
* highways sometimes get detected when completely absent in world (not merely destroyed by explosions) and vice versa (lost cities API issue)
* streets stitch weirdly to bridges (antique atlas API issue)


# important notes

because the data is generated at worldgen, adding antique cities to an existing world will only show newly generated city chunks.

moving to new versions of antique cities with additional features also won't work right unless you create a new world.

in theory, retrogen is possible, but it may be beyond my ability (or beyond my motivation)

# pretty picture

This is from 1.2, so doesn't reflect some fixes in 1.2.1, but still you get the idea.

![Sample Image](https://cdn.discordapp.com/attachments/360995219321126935/468049140950237184/unknown.png)
