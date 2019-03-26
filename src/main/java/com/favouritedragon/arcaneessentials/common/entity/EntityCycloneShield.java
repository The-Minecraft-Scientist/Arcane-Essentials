package com.favouritedragon.arcaneessentials.common.entity;

import com.favouritedragon.arcaneessentials.common.util.ArcaneUtils;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;

import static com.favouritedragon.arcaneessentials.common.util.DamageSources.PRESSURE;

public class EntityCycloneShield extends EntityMagicConstruct {

	public static final DataParameter<Float> SYNC_RADIUS = EntityDataManager.createKey(EntityCycloneShield.class, DataSerializers.FLOAT);

	public EntityCycloneShield(World par1World) {
		super(par1World);
	}

	public EntityCycloneShield(World world, double x, double y, double z, EntityLivingBase caster, int lifetime, float damageMultiplier, float radius) {
		super(world, x, y, z, caster, lifetime, damageMultiplier);
		setRadius(radius);
	}

	public static void Dissipate(EntityCycloneShield shield) {
		if (!shield.world.isRemote && shield.getCaster() != null) {
			double x = shield.posX;
			double y = shield.posY + shield.getCaster().getEyeHeight();
			double z = shield.posZ;
			List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(shield.getRadius(), x, y,
					z, shield.world);
			for (EntityLivingBase target : targets) {
				if (shield.isValidTarget(target)) {
					target.addVelocity((target.posX - x) * 2,
							(target.posY - (y)) * 0.5, (target.posZ - z) * 2);
					// Player motion is handled on that player's client so needs packets
					if (!MagicDamage.isEntityImmune(PRESSURE, target)) {
						target.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(shield, shield.getCaster(), PRESSURE), 2 * shield.damageMultiplier);
					}
					target.setEntityInvulnerable(false);
					ArcaneUtils.applyPlayerKnockback(target);
				}
			}
		}

	}

	public float getRadius() {
		return dataManager.get(SYNC_RADIUS);
	}

	public void setRadius(float radius) {
		dataManager.set(SYNC_RADIUS, radius);
	}

	@Override
	protected void entityInit() {
		dataManager.register(SYNC_RADIUS, 1F);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.world.isRemote && getCaster() != null) {
			double x = posX;
			double y = posY + getCaster().getEyeHeight();
			double z = posZ;
			List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(getRadius(), x, y,
					z, world);
			for (EntityLivingBase target : targets) {
				if (this.isValidTarget(target)) {
					int i = world.rand.nextBoolean() ? 1 : -1;
					double multiplier = (getRadius() - target.getDistance(x, y, z)) * 0.005 * i;
					target.addVelocity((target.posX - x) * multiplier,
							(target.posY - (y)) * multiplier, (target.posZ - z) * multiplier);
					// Player motion is handled on that player's client so needs packets
					if (!MagicDamage.isEntityImmune(PRESSURE, target)) {
						target.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(this, getCaster(), PRESSURE), 0.1F * damageMultiplier);
					}
					target.setEntityInvulnerable(false);
					ArcaneUtils.applyPlayerKnockback(target);
				}
			}
			if (ticksExisted % 2 == 0) {
				world.playSound(getCaster().posX, getCaster().posY, getCaster().posZ, WizardrySounds.SPELL_LOOP_WIND, SoundCategory.WEATHER, 1.0F + world.rand.nextFloat() / 10, 2.0F + world.rand.nextFloat() / 10, true);
			}
		}
	}

	@Override
	public void setDead() {
		Dissipate(this);
		this.isDead = true;

	}
}
