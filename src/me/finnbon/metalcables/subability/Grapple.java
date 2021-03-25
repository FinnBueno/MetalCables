package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import me.finnbon.metalcables.util.ConfigValue;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Finn Bon
 */
public class Grapple extends MetalCablesSubAbility {

	public static class Left extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Grapple(player, 0, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
		}
	}
	public static class Both extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			// grapple both just activates left and right at the same time
			new Grapple(player, 0, metalCablesAbility);
			new Grapple(player, 2, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT, WaterArms.Arm.LEFT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
		}
	}
	public static class Right extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Grapple(player, 2, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
		}
	}

	@ConfigValue("PullSpeed")
	public static double PULL_SPEED = 1.2;
	@ConfigValue("Range")
	@Attribute(Attribute.RANGE)
	private static double RANGE = 100;
	@ConfigValue("Speed")
	@Attribute(Attribute.SPEED)
	private static double SPEED = 3;
	@ConfigValue("Cooldown")
	private static long COOLDOWN = 0;

	public Grapple(Player player, int requiredSlot, MetalCablesAbility metalCablesAbility) {
		super(player, requiredSlot, metalCablesAbility);
		direction.multiply(SPEED);
		start();
	}

	@Override
	public void progress() {
		super.progress();
		if (player.isSneaking() && hasRequiredSlot() && hasStruckTerrain()) {
			player.setFallDistance(0);
			MetalCablesUtil.pullPlayer(player, cableHead, PULL_SPEED);
		}
	}

	public void launchUpwards() {
		if (player.getLocation().distance(cableHead) < 2) {
			player.setVelocity(new Vector(0, 1.2, 0));
		}
	}

	@Override
	public double getSpeed() {
		return SPEED;
	}

	@Override
	public double getMaxDistance() {
		return RANGE;
	}

	@Override
	public WaterArms.Arm slotToSide(int slot) {
		switch (slot) {
			case 0:
				return WaterArms.Arm.LEFT;
			case 2:
				return WaterArms.Arm.RIGHT;
		}
		return null;
	}

	@Override
	public int getPointsSpentPerUse() {
		return 1;
	}

	@Override
	public String getSubName() {
		return "Grapple";
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return COOLDOWN;
	}
}
