package com.zeml.rotp_zhp.entity.damaging.projectile;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.OwnerBoundProjectileEntity;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zhp.init.InitEntities;
import com.zeml.rotp_zhp.init.InitSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HPVineEntity extends OwnerBoundProjectileEntity {
    private float yRotOffset;
    private float xRotOffset;
    private boolean isBinding;
    private boolean dealtDamage;
    private float knockback = 0;
    private float hamonDamage;
    private float hamonDamageCost;
    private boolean spendHamonStability;
    private boolean ischarge;
    private boolean spreed;
    private boolean ishamonstu;
    private float baseHitPoints;
    private boolean gaveHamonPointsForBaseHit = false;
    private boolean scarlet;
    private float hamomlevel;


    public HPVineEntity(World world, LivingEntity entity, float angleXZ, float angleYZ, boolean isBinding) {
        super(InitEntities.HP_VINE.get(), entity, world);
        this.yRotOffset = angleXZ;
        this.xRotOffset = angleYZ;
        this.isBinding = isBinding;
    }

    public HPVineEntity(EntityType<? extends HPVineEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    public boolean isBinding() {
        return isBinding;
    }

    @Override
    public float getBaseDamage() {
        double mult = JojoModConfig.getCommonConfigInstance(false).standDamageMultiplier.get();
        return isBinding ? 1.5F * (float) mult  : 2F * (float) mult;
    }

    public void addKnockback(float knockback) {
        this.knockback = knockback;
    }

    @Override
    protected boolean hurtTarget(Entity target, LivingEntity owner) {
        if(scarlet){
            DamageUtil.dealDamageAndSetOnFire(target,
                    entity -> DamageUtil.dealHamonDamage(entity, hamonDamage, this.getOwner() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK_RED.get())),
                    MathHelper.floor(2 + 8F *  hamomlevel / (float) HamonData.MAX_STAT_LEVEL * hamonDamageCost), false);
        } else if(ischarge){
            DamageUtil.dealHamonDamage(target, hamonDamage, this.getOwner() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()));
        }
        if(spreed){
            if(target instanceof LivingEntity){
                LivingEntity liv = (LivingEntity) target;
                liv.addEffect(new EffectInstance(ModStatusEffects.HAMON_SPREAD.get(),100));
            }
        }
        return !dealtDamage ? super.hurtTarget(target, owner) : false;
    }

    @Override
    protected boolean shouldHurtThroughInvulTicks() {
        return true;
    }

    @Override
    protected void afterEntityHit(EntityRayTraceResult entityRayTraceResult, boolean entityHurt) {
        if (entityHurt) {
            dealtDamage = true;
            Entity target = entityRayTraceResult.getEntity();
            playSound(InitSounds.HP_GRAPPLE_ENT.get(), .25F, 1.0F);

            if (isBinding) {
                if (target instanceof LivingEntity) {
                    LivingEntity livingTarget = (LivingEntity) target;


                    if (!JojoModUtil.isTargetBlocking(livingTarget)) {
                        attachToEntity(livingTarget);
                        if(ishamonstu){
                            livingTarget.addEffect(new EffectInstance(ModStatusEffects.HAMON_SHOCK.get(), ticksLifespan() - tickCount));
                        }else {
                            livingTarget.addEffect(new EffectInstance(ModStatusEffects.STUN.get(), ticksLifespan() - tickCount));

                        }


                    }
                }
            }
            else {
                if (knockback > 0 && target instanceof LivingEntity) {
                    DamageUtil.knockback((LivingEntity) target, knockback, yRot);
                }
                setIsRetracting(true);
            }
        }
    }

    @Override
    protected float knockbackMultiplier() {
        return 0F;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0.0F;
    }

    @Override
    public int ticksLifespan() {
        int ticks = super.ticksLifespan();
        if (isBinding && isAttachedToAnEntity()) {
            ticks += 10;
        }
        return ticks;
    }



    @Override
    protected float movementSpeed() {
        return 16 / (float) ticksLifespan();
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
    protected Vector3d originOffset(float yRot, float xRot, double distance) {
        return super.originOffset(yRot + yRotOffset, xRot + xRotOffset, distance);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("HamonDamage", hamonDamage);
        nbt.putFloat("HamonDamageCost", hamonDamageCost);
        nbt.putBoolean("SpendStab", spendHamonStability);
        nbt.putBoolean("PointsGiven", gaveHamonPointsForBaseHit);
        nbt.putFloat("Points", baseHitPoints);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        hamonDamage = nbt.getFloat("HamonDamage");
        hamonDamageCost = nbt.getFloat("HamonDamageCost");
        spendHamonStability = nbt.getBoolean("SpendStab");
        gaveHamonPointsForBaseHit = nbt.getBoolean("PointsGiven");
        baseHitPoints = nbt.getFloat("Points");
    }


    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(yRotOffset);
        buffer.writeFloat(xRotOffset);
        buffer.writeBoolean(isBinding);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        this.yRotOffset = additionalData.readFloat();
        this.xRotOffset = additionalData.readFloat();
        this.isBinding = additionalData.readBoolean();

    }


    public void isCharged(boolean charg){
        this.ischarge=charg;
    }

    public void isScarlet(boolean scarlet, float hamonlvl){
        this.scarlet=scarlet;
        this.hamomlevel=hamonlvl;
    }
    public void hamonStuned(boolean stun){
        this.ishamonstu =stun;
    }


    public HPVineEntity setHamonDamageOnHit(float damage, float hitCost, boolean useBreathStab){
        this.hamonDamage = damage;
        this.hamonDamageCost = hitCost;
        this.spendHamonStability = useBreathStab;
        return this;
    }

    public HPVineEntity setBaseUsageStatPoints (float points){
        this.baseHitPoints = points;
        return this;
    }

    public void isSpread(boolean spred){
        this.spreed = spred;
    }

}
