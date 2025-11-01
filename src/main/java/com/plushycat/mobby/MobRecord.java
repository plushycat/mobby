package com.plushycat.mobby;

import java.util.UUID;

public class MobRecord {
    private final UUID entityId;
    private final String displayName;
    private final String typeName;
    private boolean ageLocked;
    private String world;
    private double x;
    private double y;
    private double z;

    public MobRecord(UUID entityId, String displayName, String typeName, boolean ageLocked, String world, double x, double y, double z) {
        this.entityId = entityId;
        this.displayName = displayName;
        this.typeName = typeName;
        this.ageLocked = ageLocked;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID getEntityId() { return entityId; }
    public String getDisplayName() { return displayName; }
    public String getTypeName() { return typeName; }
    public boolean isAgeLocked() { return ageLocked; }
    public void setAgeLocked(boolean ageLocked) { this.ageLocked = ageLocked; }

    public String getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public void setLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
