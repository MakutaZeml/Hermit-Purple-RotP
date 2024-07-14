package com.zeml.rotp_zhp.entity.damaging.projectile;

import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModDataSerializers;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

public class HPVineBarrierEntity extends OwnerBoundProjectileEntity {
    protected static final DataParameter<Boolean> WAS_RIPPED = EntityDataManager.defineId(HPVineBarrierEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Optional<Vector3d>> RIPPED_POINT = EntityDataManager.defineId(HPVineBarrierEntity.class,
            (IDataSerializer<Optional<Vector3d>>) ModDataSerializers.OPTIONAL_VECTOR3D.get().getSerializer());
    private boolean rippedHurtOwner = false;
    private LivingEntity standUser;
    private IStandPower userPower;
    private BlockPos originBlockPos;
    private int rippedTicks = -1;
    private boolean timeStop = false;

    public HPVineBarrierEntity(World world, StandEntity entity, IStandPower power) {
        super(InitEntities.HP_BARRIER.get(), entity, world);
        this.standUser = entity.getUser();
        this.userPower = power;
    }

    public HPVineBarrierEntity(EntityType<? extends HPVineBarrierEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setOriginBlockPos(BlockPos blockPos) {
        this.originBlockPos = blockPos;
    }

    public BlockPos getOriginBlockPos() {
        return originBlockPos;
    }

    @Override
    public void tick() {
        if (!timeStop) {
            if (rippedTicks > 0) {
                if (--rippedTicks == 0) {
                    if (!level.isClientSide()) {
                        remove();
                    }
                    return;
                }
                if (!level.isClientSide() && !rippedHurtOwner) {
                    DamageUtil.hurtThroughInvulTicks(standUser, DamageSource.GENERIC, 0.0F);
                    rippedHurtOwner = false;
                }
            }
            else {
                if(userPower != null){
                    if(userPower.getHeldAction() == InitStands.HP_UNBARRIER.get()){
                        remove();
                        return;
                    }
                }

                if (standUser == null) {
                    remove();
                    return;
                }
                if(this.distanceTo(standUser)>50){
                    remove();
                    return;
                }


                super.tick();
                if (!isAlive()) {
                    return;
                }
            }
        }
        else if (!level.isClientSide()) {
            if (!wasRipped()) {
                RayTraceResult[] rayTrace = rayTrace();
                for (RayTraceResult result : rayTrace) {
                    if (result.getType() == RayTraceResult.Type.ENTITY && !ForgeEventFactory.onProjectileImpact(this, result)) {
                        ripAt(result.getLocation());
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected float knockbackMultiplier() {
        return 0;
    }

    @Override
    protected boolean moveBoundToOwner() {
        moveTo(getOwner().getEyePosition(1.0F).add(getOwnerRelativeOffset()));
        return true;
    }

    @Override
    public boolean isBodyPart() {
        return true;
    }

    @Override
    public Vector3d getOriginPoint(float partialTick) {
        return originBlockPos == null
                ? standUser == null ? position() : MCUtil.getEntityPosition(standUser, partialTick)
                : Vector3d.atCenterOf(originBlockPos);
    }

    @Deprecated
    @Override
    protected Vector3d getNextOriginOffset() { return Vector3d.ZERO; }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(WAS_RIPPED, false);
        entityData.define(RIPPED_POINT, Optional.empty());
    }

    private static final Vector3d OFFSET = new Vector3d(0.15D, -1.4D, 0);
    @Override
    protected Vector3d getOwnerRelativeOffset() {
        return OFFSET;
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        super.onSyncedDataUpdated(dataParameter);
        if (WAS_RIPPED.equals(dataParameter) && wasRipped()) {
            rippedTicks = 40;
        }
    }

    public boolean wasRipped() {
        return entityData.get(WAS_RIPPED);
    }

    public Optional<Vector3d> wasRippedAt() {
        return entityData.get(RIPPED_POINT);
    }

    private void ripAt(@Nonnull Vector3d pos) {
        entityData.set(WAS_RIPPED, true);
        entityData.set(RIPPED_POINT, Optional.of(pos));
    }

    @Override
    public boolean isGlowing() {
        return level.isClientSide() && !timeStop && wasRipped() && getOwner() instanceof StandEntity &&
                ((StandEntity) getOwner()).getUser() == ClientUtil.getClientPlayer() || super.isGlowing();
    }

    @Override
    public int getTeamColor() {
        return wasRipped() ? 0xFF0000 : super.getTeamColor();
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        if (getBlockPosAttachedTo().isPresent()) {
            Entity target = entityRayTraceResult.getEntity();
            target.setDeltaMovement(Vector3d.ZERO);
            if (!level.isClientSide()) {
                super.onHitEntity(entityRayTraceResult);
                ripAt(entityRayTraceResult.getLocation());
                if (getOwner() instanceof HermitPurpleEntity) {
                    HermitPurpleEntity stand = (HermitPurpleEntity) getOwner();
                    DamageUtil.dealHamonDamage(target, 1F, this.standUser , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));

                }
                MCUtil.playSound(level, null, target.getX(), target.getY(), target.getZ(),
                        ModSounds.HIEROPHANT_GREEN_BARRIER_RIPPED.get(), getSoundSource(), 1.0F, 1.0F, StandUtil::playerCanHearStands);
            }
        }
    }

    @Override
    public int ticksLifespan() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    protected float movementSpeed() {
        return 0F;
    }

    @Override
    public float getBaseDamage() {
        return 2.0F;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Override
    public void canUpdate(boolean canUpdate) {
        timeStop = !canUpdate;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(standUser.getId());
        boolean isBlockOrigin = originBlockPos != null;
        buffer.writeBoolean(isBlockOrigin);
        if (isBlockOrigin) {
            buffer.writeBlockPos(originBlockPos);
        }
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        Entity entity = level.getEntity(additionalData.readInt());
        if (entity instanceof LivingEntity) {
            standUser = (LivingEntity) entity;
        }
        if (additionalData.readBoolean()) {
            originBlockPos = additionalData.readBlockPos();
        }
    }
}
