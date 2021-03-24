package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Finn Bon
 */
public class Grapple extends MetalCablesSubAbility {

	public static class Left extends SubAbilityActivationHandler {

		@Override
		protected void onActivate(Player player) {
			new Grapple(player, 0);
		}
		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}

	}
	public static class Both extends SubAbilityActivationHandler {

		@Override
		protected void onActivate(Player player) {
			// grapple both just activates left and right at the same time
			new Grapple(player, 0);
			new Grapple(player, 2);
		}
		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT, WaterArms.Arm.LEFT };
		}

	}
	public static class Right extends SubAbilityActivationHandler {

		@Override
		protected void onActivate(Player player) {
			new Grapple(player, 2);
		}
		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT };
		}

	}

	@Attribute("pullSpeed")
	private double pullSpeed = 1.2;
	@Attribute(Attribute.RANGE)
	private double maxDistance = 100;
	@Attribute(Attribute.SPEED)
	private double speed = 3;

	public Grapple(Player player, int requiredSlot) {
		super(player, requiredSlot);
		direction.multiply(speed);
		start();
	}

	@Override
	public void progress() {
		super.progress();
		player.setFallDistance(0);
		if (player.isSneaking() && hasRequiredSlot() && hasStruckTerrain()) {
			MetalCablesUtil.pullEntity(player, cableHead, pullSpeed);
		}
	}

	public void launchUpwards() {
		if (player.getLocation().distance(cableHead) < 2) {
			player.setVelocity(new Vector(0, 1.2, 0));
		}
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getMaxDistance() {
		return maxDistance;
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
		return 0;
	}
}
