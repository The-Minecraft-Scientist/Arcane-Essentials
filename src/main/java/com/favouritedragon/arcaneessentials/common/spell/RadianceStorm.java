package com.favouritedragon.arcaneessentials.common.spell;

import com.favouritedragon.arcaneessentials.ArcaneEssentials;
import com.favouritedragon.arcaneessentials.common.util.ArcaneUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryParticleType;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RadianceStorm extends Spell {
	public RadianceStorm() {
		super(Tier.MASTER, 300, Element.HEALING, "radiance_storm", SpellType.ATTACK, 300, EnumAction.BOW, false, ArcaneEssentials.MODID);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		float damage = 8 * modifiers.get(WizardryItems.blast_upgrade);
		int fireTime = 10 + 2 * (int) modifiers.get(WizardryItems.duration_upgrade);

		//TODO: Add an explosion for when the beams hit the ground
		for(int r = 0; r < 10; r++) {
			float radius = 4 + world.rand.nextFloat() * 6 * modifiers.get(WizardryItems.blast_upgrade);
			double angle = world.rand.nextDouble() * Math.PI * 2;
			double x = caster.posX + radius * Math.cos(angle);
			double z = caster.posZ + radius * Math.sin(angle);
			double y = WizardryUtilities.getNearestFloorLevel(world, new BlockPos(x, caster.posY, z), 6 + 2 *(int) modifiers.get(WizardryItems.range_upgrade));
			Vec3d startPos = new Vec3d(x, caster.getEntityBoundingBox().minY + 30, z);
			Vec3d endPos = new Vec3d(x, y, z);
			Vec3d direction = endPos.subtract(startPos);
			spawnRadiantBeam(world, caster, startPos, endPos, radius, damage, direction, fireTime);
		}
		/*for (int i = 0; i < 6; i++) {
			boolean pX = world.rand.nextBoolean();
			double x = world.rand.nextInt(4) + 1;
			boolean pZ = world.rand.nextBoolean();
			double z = world.rand.nextInt(4) + 1;
			x = pX ? x : -x;
			z = pZ ? z : -z;
			Vec3d startPos = new Vec3d(caster.posX + x, caster.getEntityBoundingBox().minY + 30, caster.posZ + z);
			Vec3d endPos = new Vec3d(caster.posX + x, caster.getEntityBoundingBox().minY, caster.posZ + z);
			Vec3d direction = endPos.subtract(startPos);
			spawnRadiantBeam(world, caster, startPos, endPos, radius, damage, direction, fireTime);
		}**/
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	private void spawnRadiantBeam(World world, EntityLivingBase caster, Vec3d startPos, Vec3d endPos, float radius, float damage, Vec3d knockBack, int fireTime) {
		if (!world.isRemote) {
			ArcaneUtils.handlePiercingBeamCollision(world, caster, startPos, endPos, radius, null, true, MagicDamage.DamageType.RADIANT,
					damage, knockBack, true, fireTime, radius);
		}
		if (world.isRemote) {
			ArcaneUtils.spawnSpinningHelix(world, 480, 30, radius, WizardryParticleType.SPARKLE, endPos,
					new Vec3d(0.025, -0.0025, 0.025), Vec3d.ZERO, 30, 1.0F, 1.0F, 0.3F);
		}
	}
}