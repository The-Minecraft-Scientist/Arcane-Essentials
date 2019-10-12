package com.favouritedragon.arcaneessentials.common.spell.ice;

import com.favouritedragon.arcaneessentials.common.spell.ArcaneSpell;
import electroblob.wizardry.block.BlockStatue;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

import static electroblob.wizardry.util.SpellModifiers.POTENCY;

public class AbsoluteZero extends ArcaneSpell {

	//Freezes all enemies within a small radius, and slows enemies outside of it.
	//Shatters frozen entities
	public AbsoluteZero() {
		super("absolute_zero", EnumAction.BOW, false);
		addProperties(EFFECT_STRENGTH, EFFECT_RADIUS, EFFECT_DURATION);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double range = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(POTENCY);
		float particleSize = getProperty(EFFECT_STRENGTH).floatValue();


		List<EntityLivingBase> nearby = WizardryUtilities.getEntitiesWithinRadius(range, caster.posX, caster.getEntityBoundingBox().minY, caster.posZ, world);
		nearby.remove(caster);
		if (!nearby.isEmpty()) {
			for (EntityLivingBase target : nearby) {
				if (AllyDesignationSystem.isValidTarget(caster, target)) {
					((BlockStatue) WizardryBlocks.ice_statue).convertToStatue((EntityLiving) target, getProperty(EFFECT_DURATION).intValue() * 20);
				}
			}
		}
		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.SPHERE).entity(caster).time(6).scale((float) range * 1.25F).clr(205, 254, 255).spawn(world);
		}
		caster.swingArm(hand);

		return true;
	}
}
