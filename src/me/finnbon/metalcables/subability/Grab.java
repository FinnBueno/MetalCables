package me.finnbon.metalcables.subability;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
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
		protected void onActivate(Player player) {
			new Grab(player, 3);
		}

		@Override
		protected WaterArms.Arm[] getSidesToDisable() {
			return new WaterArms.Arm[] { WaterArms.Arm.LEFT };
		}
	}
	public static class Right extends SubAbilityActivationHandler {
		@Override
		protected void onActivate(Player player) {
			new Grab(player, 4);
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

	protected Entity target;

	public Grab(Player player, int requiredSlot) {
		super(player, requiredSlot);
		direction.multiply(speed);
		start();
	}

	@Override
	public void progress() {
		super.progress();
		if (target != null) {
			target.setFallDistance(0);
			cableHead = target.getLocation().add(0, 0.5, 0);
			if (player.isSneaking() && getSide() == slotToSide(player.getInventory().getHeldItemSlot())) {
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
		MetalCablesUtil.pullEntity(target, player.getLocation().add(0, 1, 0), pullSpeed);
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
		return maxDistance;
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
		return 0;
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
		return 0;
	}
}
