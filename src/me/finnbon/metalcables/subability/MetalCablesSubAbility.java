package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.finnbon.metalcables.MetalCablesAbility.*;

/**
 * @author Finn Bon
 */
public abstract class MetalCablesSubAbility extends MetalAbility implements AddonAbility {

	public abstract static class SubAbilityActivationHandler {
		public void activate(Player player) {
			WaterArms.Arm[] sidesToDisable = getSidesToDisable();
			disableAbilitiesOnSides(Arrays.asList(sidesToDisable), player);
			onActivate(player);
		}
		protected abstract void onActivate(Player player);
		protected abstract WaterArms.Arm[] getSidesToDisable();
		protected abstract Class<? extends MetalCablesSubAbility> getAbilityClass();

		/**
		 * When a subability is activated, all active subabilities that use that side of the player need to be disabled
		 * @param sides The sides to disable
		 * @param player The player to disable it for
		 */
		private void disableAbilitiesOnSides(List<WaterArms.Arm> sides, Player player) {
			for (SubAbilityActivationHandler subAbilityActivationHandler : SUB_ABILITIES) {
				MetalCablesSubAbility activeAbility = CoreAbility.getAbility(player.getPlayer(), subAbilityActivationHandler.getAbilityClass());
				if (sides.contains(activeAbility.getSide())) {
					activeAbility.remove();
				}
			}
		}

	}

	private final int requiredSlot;

	public MetalCablesSubAbility(Player player, int requiredSlot) {
		super(player);
		this.requiredSlot = requiredSlot;
	}

	@Override
	public String getName() {
		return "MetalCables Sub Ability [" + getSubName() + "]";
	}

	public final boolean hasRequiredSlot() {
		return this.player.getInventory().getHeldItemSlot() == requiredSlot;
	}

	public abstract int getPointsSpentPerUse();
	public abstract String getSubName();
	public abstract WaterArms.Arm getSide();

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public boolean isHiddenAbility() {
		return true;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}

	@Override
	public String getAuthor() {
		return AUTHOR;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}
}
