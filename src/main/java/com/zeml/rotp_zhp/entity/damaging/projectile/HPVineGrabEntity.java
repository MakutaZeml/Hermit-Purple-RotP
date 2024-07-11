package com.zeml.rotp_zhp.entity.damaging.projectile;

import com.github.standobyte.jojo.capability.world.TimeStopHandler;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.MRRedBindEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HPVineGrabEntity extends OwnerBoundProjectileEntity {

    protected static final DataParameter<Boolean> OVER_DRIVE = EntityDataManager.defineId(HPVineGrabEntity.class, DataSerializers.BOOLEAN);

    private StandEntity ownerStand;
    private EffectInstance immobilizedEffect = null;
    private int ticksTargetClose = 0;
    private boolean scarlet;

    public HPVineGrabEntity(World world, StandEntity entity) {
        super(InitEntities.HP_GRAB_ENTITY.get(), entity, world);
        this.ownerStand = entity;
    }

    public HPVineGrabEntity(EntityType<? extends HPVineGrabEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isAlive()) {
            return;
        }
        if (!level.isClientSide()) {
            if (ownerStand == null || ownerStand.getCurrentTaskAction() != InitStands.HP_GRAB_COMMAND.get() && !isInOverDriveAttack()) {
                remove();
                return;
            }
        }
        LivingEntity bound = getEntityAttachedTo();
        if (bound != null) {
            LivingEntity owner = getOwner();
            if (!bound.isAlive() || owner.distanceToSqr(bound) > 100) {
                if (!level.isClientSide()) {
                    remove();
                }
            }
            else {
                Vector3d vecToOwner = owner.position().subtract(bound.position());
                if (vecToOwner.lengthSqr() > 16) {
                    dragTarget(bound, vecToOwner.normalize().scale(0.75));
                    ticksTargetClose = 0;
                }
                else if (!level.isClientSide() && !isInOverDriveAttack() && ticksTargetClose++ > 100 && !TimeStopHandler.isTimeStopped(this.level, this.blockPosition())){
                    remove();
                }
            }
        }
    }



    @Override
    protected boolean hurtTarget(Entity target, LivingEntity owner) {
        if (getEntityAttachedTo() == null) {
            if (target instanceof LivingEntity && !target.isInvulnerableTo(getDamageSource(owner))) {
                LivingEntity targetLiving = (LivingEntity) target;
                if (!JojoModUtil.isTargetBlocking(targetLiving)) {
                    playSound(InitSounds.HP_GRAPPLE_ENT.get(), .5F, 1.0F);
                    attachToEntity(targetLiving);
                    if (!level.isClientSide()) {
                        boolean thisEffect = immobilizedEffect == targetLiving.getEffect(ModStatusEffects.IMMOBILIZE.get());
                        targetLiving.addEffect(new EffectInstance(ModStatusEffects.IMMOBILIZE.get(), ticksLifespan() - tickCount));
                        if (thisEffect) {
                            immobilizedEffect = targetLiving.getEffect(ModStatusEffects.IMMOBILIZE.get());
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected double attachedTargetHeight() {
        return this.isInOverDriveAttack() ? 0.75 : super.attachedTargetHeight();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!level.isClientSide() && immobilizedEffect != null) {
            LivingEntity bound = getEntityAttachedTo();
            if (bound != null) {
                MCUtil.removeEffectInstance(bound, immobilizedEffect);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OVER_DRIVE, false);
    }

    public void setOverDrive(boolean scarlet) {
        entityData.set(OVER_DRIVE, true);
        LivingEntity target = getEntityAttachedTo();
        this.scarlet = scarlet;
        if (target != null) {
            MCUtil.removeEffectInstance(target, immobilizedEffect);
            target.addEffect(new EffectInstance(ModStatusEffects.STUN.get(), ticksLifespan() - tickCount));
            immobilizedEffect = target.getEffect(ModStatusEffects.STUN.get());
        }
    }

    public boolean isInOverDriveAttack() {
        return entityData.get(OVER_DRIVE);
    }

    public StandEntity getOwnerAsStand() {
        if (ownerStand == null) {
            LivingEntity owner = getOwner();
            ownerStand = owner instanceof StandEntity ? (StandEntity) owner : null;
        }
        return ownerStand;
    }

    private static final Vector3d OFFSET = new Vector3d(0, -0.25, 0.5);
    @Override
    protected Vector3d getOwnerRelativeOffset() {
        return OFFSET;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Override
    public float getBaseDamage() {
        return 0.0F;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0.0F;
    }

    @Override
    public int ticksLifespan() {
        return isAttachedToAnEntity() ?
                isInOverDriveAttack() ? Integer.MAX_VALUE : 160
                : 7;
    }

    @Override
    protected float movementSpeed() {
        return 2.0F;
    }

    @Override
    protected void updateMotionFlags() {}
}
