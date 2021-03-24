package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author Finn Bon
 */
public class Slam extends Grab {

	public static class Left extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Slam(player, 5, metalCablesAbility);
		}

		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}
	}
	public static class Right extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Slam(player, 6, metalCablesAbility);
		}

		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT };
		}
	}

	@Attribute(Attribute.RANGE)
	private double maxDistance = 100;
	@Attribute(Attribute.SPEED)
	private double speed = 3;
	@Attribute(Attribute.DAMAGE)
	private double damage = 2;

	public Slam(Player player, int requiredSlot, MetalCablesAbility metalCablesAbility) {
		super(player, requiredSlot, metalCablesAbility);
		direction.normalize().multiply(speed);
	}

	@Override
	public void progress() {
		super.progress();
		if (target != null) {
			if (target instanceof LivingEntity) {
				DamageHandler.damageEntity(target, damage, this);
			}
			remove();
		}
	}

	@Override
	public WaterArms.Arm slotToSide(int slot) {
		switch (slot) {
			case 5:
				return WaterArms.Arm.LEFT;
			case 6:
				return WaterArms.Arm.RIGHT;
		}
		return null;
	}

	@Override
	public double getMaxDistance() {
		return maxDistance;
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public String getSubName() {
		return "Slam";
	}
}
