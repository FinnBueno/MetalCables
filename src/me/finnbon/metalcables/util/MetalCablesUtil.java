package me.finnbon.metalcables.util;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms.Arm;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static me.finnbon.metalcables.MetalCablesAbility.METAL_COLOR;

/**
 * @author Finn Bon
 */
public class MetalCablesUtil {

	public static Location getHand(Player player, Arm side) {
		switch (side) {
			case RIGHT:
				return GeneralMethods.getRightSide(player.getEyeLocation(), 0.4D).subtract(0.0D, 0.6D, 0.0D);
			case LEFT :
				return GeneralMethods.getLeftSide(player.getEyeLocation(), 0.4D).subtract(0.0D, 0.6D, 0.0D);
		}
		return null;
	}

	public static void renderLine(Location start, Vector direction, double length, double step) {
		direction = direction.clone().normalize().multiply(step);
		for (double stepCounter = 0; stepCounter <= length; stepCounter += step) {
			GeneralMethods.displayColoredParticle(METAL_COLOR, start, 1, 0.05, 0.05, 0.05);
			start.add(direction);
		}
	}

	public static void renderLine(Location start, Location end, double step) {
		double length = start.distance(end);
		Vector direction = end.toVector().subtract(start.toVector());
		renderLine(start, direction, length, step);
	}

	public static void pullPlayer(Player player, Location target, double pullSpeed) {
		Vector pull = target.toVector().subtract(player.getLocation().toVector()).normalize().multiply(pullSpeed);
		if (pull.angle(player.getLocation().getDirection()) < Math.toRadians(15)) {
			pull = player.getLocation().getDirection().multiply(pullSpeed);
		}
		if (player.getLocation().distance(target) <= .8) {
			return;
		} else if (player.getLocation().distance(target) < 3) {
			pull.normalize().multiply(0.2);
		}
		player.setVelocity(pull);
	}

	public static void pullEntity(Entity entity, Location target, double pullSpeed) {
		Vector pull = target.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(pullSpeed);
		if (entity.getLocation().distance(target) <= .8) {
			return;
		} else if (entity.getLocation().distance(target) < 3) {
			pull.normalize().multiply(0.2);
		}
		entity.setVelocity(pull);
	}

}
