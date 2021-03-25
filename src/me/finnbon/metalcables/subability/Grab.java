package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.MetalCablesAbility;
import me.finnbon.metalcables.util.ConfigValue;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Finn Bon
 */
public class Grab extends MetalCablesSubAbility {

	public static class Left extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Grab(player, 3, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grab.class;
		}
	}
	public static class Right extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player, MetalCablesAbility metalCablesAbility) {
			new Grab(player, 4, metalCablesAbility);
		}
		@Override
		public WaterArms.Arm[] getSides() {
			return new WaterArms.Arm[] { WaterArms.Arm.RIGHT };
		}
		@Override
		public Class<? extends MetalCablesSubAbility> getAbilityClass() {
			return Grab.class;
		}
	}

	@ConfigValue("PullSpeed")
	private static double PULL_SPEED = 1.2;
	@ConfigValue("Range")
	@Attribute(Attribute.RANGE)
	private static double RANGE = 100;
	@ConfigValue("Speed")
	@Attribute(Attribute.SPEED)
	private static double speed = 3;
	@ConfigValue("Cooldown")
	private static long COOLDOWN = 2000;
	@ConfigValue("Duration")
	private static long DURATION = 3000;

	protected Entity target;

	public Grab(Player player, int requiredSlot, MetalCablesAbility metalCablesAbility) {
		super(player, requiredSlot, metalCablesAbility);
		direction.multiply(speed);
		start();
	}

	@Override
	public void progress() {
		super.progress();
		if (System.currentTimeMillis() - getStartTime() > DURATION) {
			remove();
			return;
		}
		if (target != null) {
			cableHead = target.getLocation().add(0, 0.5, 0);
			if (player.isSneaking() && getSide() == slotToSide(player.getInventory().getHeldItemSlot())) {
				target.setFallDistance(0);
				pullTarget();
			}
		} else {
			if (hasStruckTerrain()) {
				remove();
				return;
			}
			Optional<Entity> optionalTarget = GeneralMethods.getEntitiesAroundPoint(cableHead, 1.5)
				.stream()
				.filter(entity ->
					entity.getEntityId() != player.getEntityId()
				)
				.findFirst();
			optionalTarget.ifPresent(entity -> {
				target = entity;
				playMetalbendingSound(cableHead);
				playMetalbendingSound(player.getEyeLocation());
			});
		}
 	}

	public void pullTarget() {
		MetalCablesUtil.pullEntity(target, player.getLocation().add(0, 1, 0), PULL_SPEED);
	}

	@Override
	public boolean hasStruckTerrain() {
		return target != null || super.hasStruckTerrain();
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getMaxDistance() {
		return RANGE;
	}

	@Override
	public WaterArms.Arm slotToSide(int slot) {
		switch (slot) {
			case 3:
				return WaterArms.Arm.LEFT;
			case 4:
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
		return "Grab";
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
