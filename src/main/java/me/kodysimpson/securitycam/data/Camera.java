package me.kodysimpson.securitycam.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Camera {

    private ObjectId id;
    private String name;
    private UUID owner;
    private Location corner1;
    private Location corner2;

    public Camera(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
    }

    public boolean isInRegion(Location location){
        double x1 = corner1.getX();
        double x2 = corner2.getX();
        double y1 = corner1.getY();
        double y2 = corner2.getY();
        double z1 = corner1.getZ();
        double z2 = corner2.getZ();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2) && z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }

    public List<Material> getMaterialsInRegion() {
        List<Material> materials = new ArrayList<>();
        World world = corner1.getWorld();

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());

        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());

        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Material material = world.getBlockAt(x, y, z).getType();
                    materials.add(material);
                }
            }
        }

        return materials;
    }

}
