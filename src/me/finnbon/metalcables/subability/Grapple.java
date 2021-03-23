package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import org.bukkit.entity.Player;

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

		@Override
		protected Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
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

		@Override
		protected Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
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

		@Override
		protected Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grapple.class;
		}
	}

	private WaterArms.Arm side;

	public Grapple(Player player, int requiredSlot) {
		super(player, requiredSlot);
		switch (requiredSlot) {
			case 0:
				side = WaterArms.Arm.LEFT;
				break;
			case 2:
				side = WaterArms.Arm.RIGHT;
		}
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
	public WaterArms.Arm getSide() {
		return side;
	}

	@Override
	public void progress() {

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
