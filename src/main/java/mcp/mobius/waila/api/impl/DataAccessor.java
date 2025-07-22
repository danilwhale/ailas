package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.unmapped.C_9836145;
import net.minecraft.unmapped.C_0190618;
import net.minecraft.world.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DataAccessor implements ICommonAccessor, IDataAccessor, IEntityAccessor {

    public static final DataAccessor INSTANCE = new DataAccessor();

    public World world;
    public PlayerEntity player;
    public HitResult hitResult;
    public Vec3d renderingvec = null;
    public Block block = Blocks.AIR;
    public BlockState state = Blocks.AIR.defaultState();
    public BlockPos pos = BlockPos.ORIGIN;
    public Identifier blockRegistryName = Registry.ITEM.m_0551904();
    public BlockEntity blockEntity;
    public Entity entity;
    public NbtCompound serverData = null;
    public long timeLastUpdate = System.currentTimeMillis();
    public double partialFrame;
    public ItemStack stack = ItemStack.EMPTY;

    public void set(World world, PlayerEntity player, HitResult hit) {
        this.set(world, player, hit, null, 0.0);
    }

    public void set(World world, PlayerEntity player, HitResult hit, Entity viewEntity, double partialTicks) {
        this.world = world;
        this.player = player;
        this.hitResult = hit;

        if (this.hitResult.m_5790247() == HitResult.Type.BLOCK) {
            this.pos = ((C_9836145) hit).m_6754399();
            this.state = this.world.m_2431061(this.pos);
            this.block = this.state.getBlock();
            this.blockEntity = this.world.getBlockEntity(this.pos);
            this.entity = null;
            this.blockRegistryName = Registry.BLOCK.getKey(block);
            this.stack = block.getPickItem(world, pos, state);
        } else if (this.hitResult.m_5790247() == HitResult.Type.ENTITY) {
            this.entity = ((C_0190618) hit).m_5279979();
            this.pos = new BlockPos(entity);
            this.state = Blocks.AIR.defaultState();
            this.block = Blocks.AIR;
            this.blockEntity = null;
            this.stack = ItemStack.EMPTY;
        }

        if (viewEntity != null) {
            double px = viewEntity.prevX + (viewEntity.x - viewEntity.prevX) * partialTicks;
            double py = viewEntity.prevY + (viewEntity.y - viewEntity.prevY) * partialTicks;
            double pz = viewEntity.prevZ + (viewEntity.z - viewEntity.prevZ) * partialTicks;
            this.renderingvec = new Vec3d(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
            this.partialFrame = partialTicks;
        }
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public BlockPos getPosition() {
        return this.pos;
    }

    public HitResult getHitResult() {
        return this.hitResult;
    }

    @Override
    public Vec3d getRenderingPosition() {
        return this.renderingvec;
    }

    @Override
    public NbtCompound getServerData() {
        if ((this.blockEntity != null) && this.isTagCorrectTileEntity(this.serverData))
            return serverData;

        if ((this.entity != null) && this.isTagCorrectEntity(this.serverData))
            return serverData;

        if (this.blockEntity != null)
            return blockEntity.writeNbt(new NbtCompound());

        if (this.entity != null)
            return entity.writeEntityNbt(new NbtCompound());

        return new NbtCompound();
    }

    public void setServerData(NbtCompound tag) {
        this.serverData = tag;
    }

    private boolean isTagCorrectTileEntity(NbtCompound tag) {
        if (tag == null) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        BlockPos hitPos = ((C_9836145) hitResult).m_6754399();
        if (x == hitPos.getX() && y == hitPos.getY() && z == hitPos.getZ())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    private boolean isTagCorrectEntity(NbtCompound tag) {
        if (tag == null || !tag.contains("WailaEntityID")) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int id = tag.getInt("WailaEntityID");

        if (id == this.entity.getNetworkId())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    @Override
    public double getPartialFrame() {
        return this.partialFrame;
    }

    @Override
    public Direction getSide() {
        return hitResult == null ? null : hitResult.m_5790247() == HitResult.Type.ENTITY ? null : ((C_9836145) hitResult).m_1906345();
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    public boolean isTimeElapsed(long time) {
        return System.currentTimeMillis() - this.timeLastUpdate >= time;
    }

    public void resetTimer() {
        this.timeLastUpdate = System.currentTimeMillis();
    }

    @Override
    public Identifier getBlockId() {
        return blockRegistryName;
    }
}
