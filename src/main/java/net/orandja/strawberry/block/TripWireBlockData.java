package net.orandja.strawberry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import static net.minecraft.state.property.Properties.*;

public class TripWireBlockData {

    public static final TripWireBlockData fullState = new TripWireBlockData(true, true, true, true, true, true, true);
    public static final TripWireBlockData emptyState = new TripWireBlockData(false, false, false, false, false, false, false);

    public static TripWireBlockData fromID(int id) {
        return new TripWireBlockData((id & 64) == 64, (id & 32) == 32, (id & 16) == 16, (id & 8) == 8, (id & 4) == 4,(id & 2) == 2, (id & 1) == 1);
    }

    public final boolean attached;
    public final boolean powered;
    public final boolean east;
    public final boolean north;
    public final boolean south;
    public final boolean west;
    public final boolean disarmed;

    public TripWireBlockData(boolean attached, boolean powered, boolean east, boolean north, boolean south, boolean west, boolean disarmed) {
        this.attached = attached;
        this.powered = powered;
        this.east = east;
        this.north = north;
        this.south = south;
        this.west = west;
        this.disarmed = disarmed;
    }

    public BlockState generateState() {
        return Blocks.TRIPWIRE.getDefaultState().with(ATTACHED, attached).with(POWERED, powered).with(EAST, east).with(NORTH, north).with(SOUTH, south).with(WEST, west).with(DISARMED, disarmed);
    }

    public static BlockState assignStateProperties(int id) {
        return fromID(id).generateState();
    }

    public int toID() {
        return (this.attached ? 0 : 1) << 6
                + (this.powered ? 0 : 1) << 5
                + (this.east ? 0 : 1) << 4
                + (this.north ? 0 : 1) << 3
                + (this.south ? 0 : 1) << 2
                + (this.west ? 0 : 1) << 1
                + (this.disarmed ? 0 : 1);
    }

    public String toBlockStateString() {
        return "attached="+ attached +",powered="+ powered +",east="+ east +",north="+ north +",south="+ south +",west="+ west +",disarmed="+ disarmed;
    }

}
