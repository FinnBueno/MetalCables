package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import org.bukkit.entity.Player;

/**
 * @author Finn Bon
 */
public class Clear extends MetalCablesSubAbility {

	public static class Activator extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			for (Class<? extends MetalCablesSubAbility> subAbilityActivationHandler : SUB_ABILITY_CLASSES) {
				for (MetalCablesSubAbility metalCablesSubAbility : CoreAbility.getAbilities(player.getPlayer(), subAbilityActivationHandler)) {
					metalCablesSubAbility.remove();
				}
			}
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return null;
		}
	}

	public Clear(Player player, int requiredSlot, MetalCablesAbility metalCablesAbility) {
		super(player, requiredSlot, metalCablesAbility);
	}

	@Override
	public int getPointsSpentPerUse() {
		return 0;
	}

	@Override
	public String getSubName() {
		return "Clear";
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
	public void progress() {}

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
