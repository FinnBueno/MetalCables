package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import me.finnbon.metalcables.util.ConfigValue;
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
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Slam.class;
		}
	}
	public static class Right extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Slam(player, 6, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Slam.class;
		}
	}

	@ConfigValue("Range")
	@Attribute(Attribute.RANGE)
	private static double RANGE = 100;
	@ConfigValue("Speed")
	@Attribute(Attribute.SPEED)
	private static double SPEED = 3;
	@ConfigValue("Damage")
	@Attribute(Attribute.DAMAGE)
	private static double DAMAGE = 2;
	@ConfigValue("Cooldown")
	private static long COOLDOWN = 1000;

	public Slam(Player player, int requiredSlot, MetalCablesAbility metalCablesAbility) {
		super(player, requiredSlot, metalCablesAbility);
		direction.normalize().multiply(SPEED);
	}

	@Override
	public void progress() {
		super.progress();
		if (target != null) {
			if (target instanceof LivingEntity) {
				DamageHandler.damageEntity(target, DAMAGE, this);
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
		return RANGE;
	}

	@Override
	public double getSpeed() {
		return SPEED;
	}

	@Override
	public String getSubName() {
		return "Slam";
	}

	@Override
	public long getCooldown() {
		return COOLDOWN;
	}
}
