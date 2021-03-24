package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import org.bukkit.entity.Player;

/**
 * @author Finn Bon
 */
public class Exit extends MetalCablesSubAbility {

	public static class Activator extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player) {
			CoreAbility.getAbility(player, MetalCablesAbility.class).remove();
		}
	}

	public Exit(Player player, int requiredSlot) {
		super(player, requiredSlot);
	}

	@Override
	public int getPointsSpentPerUse() {
		return 0;
	}

	@Override
	public String getSubName() {
		return "Exit";
	}

	@Override
	public double getSpeed() {
		return 0;
	}

	@Override
	public double getMaxDistance() {
		return 0;
	}

	@Override
	public WaterArms.Arm slotToSide(int slot) {
		return null;
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
