package com.zeml.rotp_zhp.entity.damaging.projectile;

import com.github.standobyte.jojo.capability.entity.hamonutil.EntityHamonChargeCapProvider;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitSounds;
import com.zeml.rotp_zhp.init.InitStands;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.UUID;


    public class HPGrapplingVineEntity extends OwnerBoundProjectileEntity {
        private static final UUID MANUAL_MOVEMENT_LOCK = UUID.fromString("ccf94bd5-8f0f-4d1e-b606-ba0773d963f3");
        private IStandPower userStandPower;
        private boolean bindEntities;
        private StandEntity stand;
        private boolean ischarge;
        private boolean spreed;
        private boolean scarlet;
        private float hamomlevel;
        private float hamonDamage;
        private float hamonDamageCost;
        int n;
        private UUID userU;


        private boolean caughtAnEntity = false;

        public HPGrapplingVineEntity(World world, StandEntity entity, IStandPower userStand, boolean charg, LivingEntity suer) {
            super(InitEntities.HP_GRAPPLING_VINE.get(), entity, world);
            this.stand = entity;
            this.userStandPower = userStand;
            this.ischarge = charg;
            this.userU = suer.getUUID();
        }

        public HPGrapplingVineEntity(EntityType<? extends HPGrapplingVineEntity> entityType, World world) {
            super(entityType, world);
        }

        @Override
        public void remove() {
            super.remove();
            if (!level.isClientSide() && stand != null && caughtAnEntity) {
                stand.getManualMovementLocks().removeLock(MANUAL_MOVEMENT_LOCK);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (!isAlive()) {
                return;
            }
            if (!level.isClientSide() && (userStandPower == null || userStandPower.getHeldAction() != (
                    bindEntities ? InitStands.HP_GRAPPLE_ENTITY.get() :
                            InitStands.HP_GRAPPLE.get()))) {
                remove();
                return;
            }
            LivingEntity bound = getEntityAttachedTo();
            if (bound != null) {
                LivingEntity owner = getOwner();
                if(this.ischarge){
                    DamageUtil.dealHamonDamage(bound, 0.15F, this, owner);
                }
                if(spreed){
                    this.n+=1;
                    int eff = Math.floorDiv(n,10);
                    bound.addEffect(new EffectInstance(ModStatusEffects.HAMON_SPREAD.get(),100,eff));
                }
                if (!bound.isAlive()) {
                    if (!level.isClientSide()) {
                        remove();
                    }
                }
                else {
                    Vector3d vecToOwner = owner.position().subtract(bound.position());

                    double length = vecToOwner.length();
                    if (length < 2) {
                        if (!level.isClientSide()) {
                            remove();
                        }
                    }
                    else {
                        dragTarget(bound, vecToOwner.normalize().scale(1));
                        bound.fallDistance = 0;
                    }
                }
            }
        }

        public void setBindEntities(boolean bindEntities) {
            this.bindEntities = bindEntities;
        }


        @Override
        protected boolean moveToBlockAttached() {
            if (super.moveToBlockAttached()) {
                LivingEntity owner = getOwner();
                Vector3d vecFromOwner = position().subtract(owner.position());
                if (vecFromOwner.lengthSqr() > 4) {
                    Vector3d grappleVec = vecFromOwner.normalize().scale(2);
                    Entity entity = owner;
                    if (stand == null && owner instanceof StandEntity) {
                        stand = (StandEntity) owner;
                    }
                    if (stand != null && stand.isFollowingUser()) {
                        LivingEntity user = stand.getUser();
                        if (user != null) {
                            entity = user;
                        }
                    }
                    entity = entity.getRootVehicle();
                    entity.setDeltaMovement(grappleVec);
                    entity.fallDistance = 0;
                }
                else if (!level.isClientSide()) {
                    remove();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean isBodyPart() {
            return true;
        }

        private static final Vector3d OFFSET = new Vector3d(-0.3, -0.2, 0.75);
        @Override
        protected Vector3d getOwnerRelativeOffset() {
            return OFFSET;
        }

        @Override
        public int ticksLifespan() {
            return getEntityAttachedTo() == null && !getBlockPosAttachedTo().isPresent() ? 20 : Integer.MAX_VALUE;
        }

        @Override
        protected float movementSpeed() {
            return 4F;
        }

        @Override
        protected boolean canHitEntity(Entity entity) {
            LivingEntity owner = getOwner();
            if (entity.is(owner) || !(entity instanceof LivingEntity)) {
                return false;
            }
            if (owner instanceof StandEntity) {
                StandEntity stand = (StandEntity) getOwner();
                return !entity.is(stand.getUser()) || !stand.isFollowingUser();
            }
            return true;
        }

        @Override
        protected boolean hurtTarget(Entity target, LivingEntity owner) {
            if (getEntityAttachedTo() == null && bindEntities) {
                if (target instanceof LivingEntity) {
                    LivingEntity livingTarget = (LivingEntity) target;
                    if (!JojoModUtil.isTargetBlocking(livingTarget)) {
                        attachToEntity((LivingEntity) target);
                        playSound(InitSounds.HP_GRAPPLE_ENT.get(), .5F, 1.0F);
                        caughtAnEntity = true;
                        target.addTag(String.valueOf(userU));

                        if(scarlet){
                            DamageUtil.dealDamageAndSetOnFire(target,
                                    entity -> DamageUtil.dealHamonDamage(entity, hamonDamage, this.getOwner() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_RED.get())),
                                    MathHelper.floor(2 + 8F *  hamomlevel / (float) HamonData.MAX_STAT_LEVEL * hamonDamageCost), false);
                        } else if(ischarge){
                            DamageUtil.dealHamonDamage(target, hamonDamage, this.getOwner() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void updateMotionFlags() {}


        private static final BlockState LAMP = Blocks.REDSTONE_LAMP.defaultBlockState().setValue(RedstoneLampBlock.LIT,true);
        @Override
        protected void afterBlockHit(BlockRayTraceResult blockRayTraceResult, boolean brokenBlock) {
            if (!brokenBlock && !bindEntities) {
                if (!getBlockPosAttachedTo().isPresent()) {
                    playSound(InitSounds.HP_GRAPPLE_CATCH.get(), 1.0F, 1.0F);
                    attachToBlockPos(blockRayTraceResult.getBlockPos());
                }
            }
            if(!level.isClientSide){
                if(level.getBlockState(blockRayTraceResult.getBlockPos()).getBlock()==Blocks.REDSTONE_LAMP){
                    level.setBlockAndUpdate(blockRayTraceResult.getBlockPos(),LAMP);
                }
            }
        }

        public void isScarlet(boolean scarlet, float hamonlvl){
            this.scarlet=scarlet;
            this.hamomlevel=hamonlvl;
        }

        public void setHamonDamageOnHit(float damage, float hitCost){
            this.hamonDamage = damage;
            this.hamonDamageCost = hitCost;
        }

        @Override
        public void writeSpawnData(PacketBuffer buffer){
            super.writeSpawnData(buffer);
            buffer.writeUUID(userU);
        }

        @Override
        public void readSpawnData(PacketBuffer additionalData) {
            super.readSpawnData(additionalData);
            this.userU = additionalData.readUUID();
        }

        public UUID ownerUUID(){
            return this.userU;
        }

        public LivingEntity getBou(){
            return getEntityAttachedTo();
        }
        public void isCharged(boolean charg){
            this.ischarge=charg;
        }

        public void isSpread(boolean spred){
            this.spreed = spred;
        }

        @Override
        protected void defineSynchedData() {
            super.defineSynchedData();
        }

        @Override
        public float getBaseDamage() {
            return 0;
        }

        @Override
        protected float getMaxHardnessBreakable() {
            return 0;
        }

        @Override
        public boolean standDamage() {
            return true;
        }
    }
