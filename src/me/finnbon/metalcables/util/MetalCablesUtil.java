package me.finnbon.metalcables.util;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms.Arm;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static me.finnbon.metalcables.MetalCablesAbility.METAL_COLOR;

/**
 * @author Finn Bon
 */
public class MetalCablesUtil {

	public static Location getHand(Player player, Arm side)
	{
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

}
