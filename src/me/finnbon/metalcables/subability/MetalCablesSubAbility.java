package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

import static me.finnbon.metalcables.MetalCablesAbility.*;

/**
 * @author Finn Bon
 */
public abstract class MetalCablesSubAbility extends MetalAbility implements AddonAbility {

	protected static final List<Class<? extends MetalCablesSubAbility>> SUB_ABILITY_CLASSES = Arrays.asList(
		Grapple.class,
		Grab.class,
		Slam.class
	);

	public abstract static class SubAbilityActivationHandler {
		public void activate(Player player) {
			WaterArms.Arm[] sidesToDisable = getSidesToDisable();
			disableAbilitiesOnSides(Arrays.asList(sidesToDisable), player);
			onActivate(player);
		}
		protected abstract void onActivate(Player player);
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[0];
		}

		/**
		 * When a subability is activated, all active subabilities that use that side of the player need to be disabled
		 * @param sides The sides to disable
		 * @param player The player to disable it for
		 */
		private void disableAbilitiesOnSides(List<WaterArms.Arm> sides, Player player) {
			for (Class<? extends MetalCablesSubAbility> subAbilityClass : SUB_ABILITY_CLASSES) {
				Collection<? extends MetalCablesSubAbility> abilities = CoreAbility.getAbilities(player, subAbilityClass);
				for (MetalCablesSubAbility ability : abilities) {
					if (sides.contains(ability.getSide())) {
						ability.remove();
					}
				}
			}
		}
	}

	private final int requiredSlot;
	protected WaterArms.Arm side;
	protected Location cableHead;
	protected final Vector direction;
	protected double distanceTravelled;
	protected boolean isStuck;

	public MetalCablesSubAbility(Player player, int requiredSlot) {
		super(player);
		this.requiredSlot = requiredSlot;
		side = slotToSide(requiredSlot);
		cableHead = player.getEyeLocation();
		direction = cableHead.getDirection();
		distanceTravelled = 0;
	}

	@Override
	public void progress() {
		if (!hasStruckTerrain()) {
			// travel
			cableHead.add(direction);
			distanceTravelled += getSpeed();
			if (getSpeed() >= 0.75) {
				checkClipping();
			}
			if (distanceTravelled > getMaxDistance()) {
				remove();
				return;
			}
		}
		// check again if we've hit terrain and if we hadn't before
		if (hasStruckTerrain() && !isStuck) {
			playMetalbendingSound(cableHead);
			playMetalbendingSound(player.getEyeLocation());
			isStuck = true;
		} else if (!hasStruckTerrain() && isStuck) {
			isStuck = false;
		}
		render();
	}

	protected void checkClipping() {
		Location previous = cableHead.clone().subtract(direction);
		double distance = previous.distance(cableHead);
		Vector delta = direction.clone().normalize().multiply(.25);
		for (double step = 0; step < distance; step += .25) {
			if (isLocationInTerrain(previous)) {
				cableHead = previous;
				distanceTravelled -= getSpeed();
				distanceTravelled += step;
				return;
			}
			previous.add(delta);
		}
	}

	public Location getCableHead() {
		return cableHead.clone();
	}

	protected void render() {
		MetalCablesUtil.renderLine(
			Objects.requireNonNull(
				MetalCablesUtil.getHand(player, getSide())
			),
			cableHead,
			0.2
		);
	}

	public boolean hasStruckTerrain() {
		return cableHead.getBlock().getType().isSolid();
	}

	protected boolean isLocationInTerrain(Location location) {
		return location.getBlock().getType().isSolid();
	}

	@Override
	public String getName() {
		return "MetalCables [" + getSubName() + "]";
	}

	public final boolean hasRequiredSlot() {
		return this.player.getInventory().getHeldItemSlot() == requiredSlot;
	}

	public abstract int getPointsSpentPerUse();
	public abstract String getSubName();
	public abstract double getSpeed();
	public abstract double getMaxDistance();
	public abstract WaterArms.Arm slotToSide(int slot);

	public WaterArms.Arm getSide() {
		return side;
	}

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
