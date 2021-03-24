package me.finnbon.metalcables;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.listener.MetalCablesListener;
import me.finnbon.metalcables.subability.*;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

import static me.finnbon.metalcables.subability.MetalCablesSubAbility.SUB_ABILITY_CLASSES;

/**
 * @author Finn Bon
 */
public class MetalCablesAbility extends MetalAbility implements MultiAbility, AddonAbility {

	public static final String AUTHOR = "FinnBueno";
	public static final String VERSION = "3.0.0";
	public static final String METAL_COLOR = "#8a94ab";
	public static final Vector DOWN = new Vector(0, -1, 0);

	public static final List<MetalCablesSubAbility.SubAbilityActivationHandler> SUB_ABILITIES = new ArrayList<>();

	private static long COOLDOWN;
	private static int MAX_USES = 100;

	private static final double DOUBLE_SPEED = 1.2;

	static {
		SUB_ABILITIES.addAll(Arrays.asList(
			new Grapple.Left(),
			new Grapple.Both(),
			new Grapple.Right(),
			new Grab.Left(),
			new Grab.Right(),
			new Slam.Left(),
			new Slam.Right(),
			new Clear.Activator(),
			new Exit.Activator()
		));

		FileConfiguration c = ConfigManager.defaultConfig.get();
		COOLDOWN = c.getLong("ExtraAbilities.FinnBueno.MetalCables.Cooldown", 5000);
		MAX_USES = c.getInt("ExtraAbilities.FinnBueno.MetalCables.Uses", 100);
	}

	private final BossBar usesLeftRenderer;

	private boolean hadAllowFlight;
	private int uses;

	public MetalCablesAbility(Player player) {
		super(player);
		// open multi ability menu
		MultiAbilityManager.bindMultiAbility(player, getName());
		hadAllowFlight = player.getAllowFlight();
		player.setAllowFlight(true);
		uses = MAX_USES;
		usesLeftRenderer = Bukkit.createBossBar(
			ChatColor.translateAlternateColorCodes('&',
				"&8MetalCables > &2" + this.uses + "/" + MAX_USES),
			BarColor.GREEN, BarStyle.SOLID);
		usesLeftRenderer.addPlayer(player);
		usesLeftRenderer.setProgress(1);
		playMetalbendingSound(player.getEyeLocation().subtract(0, 1, 0));
		start();
	}

	@Override
	public void progress() {
		if (shouldDisable()) {
			remove();
			return;
		}
		renderHips();
		if (player.isSneaking() && player.getInventory().getHeldItemSlot() == 1) {
			handlePullBothGrapple();
			handlePullBothGrab();
		}
	}

	private void handlePullBothGrab() {
		CoreAbility.getAbilities(player, Grab.class).forEach(Grab::pullTarget);
	}

	private void handlePullBothGrapple() {
		// find all active grapples
		Collection<Grapple> grapples = CoreAbility.getAbilities(player, Grapple.class);
		Location target = null;
		if (grapples.size() == 0) { // if there aren't any grapples, don't pull
			return;
		} else if (grapples.size() == 1) { // if there's only one, check if it's struck terrain and then use it
			Grapple grapple = grapples.iterator().next();
			target = grapple.hasStruckTerrain() ? grapple.getCableHead() : null;
		} else { // if there's 2, things get a bit trickier
			// get both grapples through an iterator because Collection
			Iterator<Grapple> it = grapples.iterator();
			Grapple grapple1 = it.next();
			Grapple grapple2 = it.next();
			// check if they've struck terrain and if so get their cable head
			Location loc1 = grapple1.hasStruckTerrain() ? grapple1.getCableHead() : null;
			Location loc2 = grapple2.hasStruckTerrain() ? grapple2.getCableHead() : null;
			if (loc1 == null || loc2 == null) { // if either (or both) of the grapples have not struck terrain
				if (loc1 != null) { // if grapple1 is not null, use it
					target = loc1;
				} else if (loc2 != null) { // if grapple2 is not null, use it
					target = loc2;
				}
			} else { // if both of them have struck terrain, find the middle and use that
				Vector delta = loc2.toVector().subtract(loc1.toVector()).multiply(0.5);
				loc1.add(delta);
				target = loc1;
			}
		}
		// if we found a target to pull towards, proceed to pull the player
		if (target != null) {
			MetalCablesUtil.pullEntity(player, target, DOUBLE_SPEED);
		}
	}

	private void renderHips() {
		for (WaterArms.Arm arm : WaterArms.Arm.values()) {
			MetalCablesUtil.renderLine(
				MetalCablesUtil.getHand(player, arm),
				DOWN,
				0.8,
				0.2
			);
		}
	}

	/**
	 * Returns whether or not the ability should stop. True if the player is dead, offline,
	 * if they have no more uses left or when they have swapped their items to an invalid position.
	 * @return Whether or not the ability should stop
	 */
	private boolean shouldDisable() {
		if (player.isDead() || !player.isOnline()) {
			return true;
		}
		// TODO: Check for uses and armor
		return false;
	}

	public void activateSubAbility() {
		int slot = player.getInventory().getHeldItemSlot();
		if (slot >= SUB_ABILITIES.size()) {
			return;
		}
		if (uses <= 0) {
			remove();
			return;
		}
		MetalCablesSubAbility.SubAbilityActivationHandler subAbilityActivationHandler = SUB_ABILITIES.get(slot);
		subAbilityActivationHandler.activate(player, this);
	}

	public void decreaseUses(int pointsSpentPerUse) {
		uses-= pointsSpentPerUse;
		usesLeftRenderer.setProgress(Math.max(0, uses / ((double) MAX_USES)));
		usesLeftRenderer.setTitle(
			ChatColor.translateAlternateColorCodes('&',
				"&8MetalCables > &2" + this.uses + "/" + MAX_USES)
		);
	}

	@Override
	public void remove() {
		super.remove();
		usesLeftRenderer.removeAll();
		bPlayer.addCooldown(this);
		player.setAllowFlight(hadAllowFlight);
		MultiAbilityManager.unbindMultiAbility(player);
		for (Class<? extends MetalCablesSubAbility> subAbilityClass : SUB_ABILITY_CLASSES) {
			CoreAbility.getAbilities(player, subAbilityClass).forEach(MetalCablesSubAbility::remove);
		}
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public String getName() {
		return "MetalCables";
	}

	@Override
	public Location getLocation() {
		return player.getEyeLocation();
	}

	@Override
	public void load() {
		new MetalCablesListener(this);
	}

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

	@Override
	public ArrayList<MultiAbilityManager.MultiAbilityInfoSub> getMultiAbilities() {
		return new ArrayList<>(
			Arrays.asList(
				new MultiAbilityManager.MultiAbilityInfoSub("Grapple (L)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Grapple (L&R)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Grapple (R)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Grab (L)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Grab (R)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Slam (L)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Slam (R)", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Clear", Element.METAL),
				new MultiAbilityManager.MultiAbilityInfoSub("Exit", Element.METAL))
		);
	}
}
