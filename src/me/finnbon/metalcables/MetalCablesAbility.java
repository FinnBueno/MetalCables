package me.finnbon.metalcables;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.listener.MetalCablesListener;
import me.finnbon.metalcables.subability.*;
import me.finnbon.metalcables.util.ConfigValue;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;

import java.lang.reflect.Modifier;
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

	private Permission perm;
	private Listener listener;

	public static final List<MetalCablesSubAbility.SubAbilityActivationHandler> SUB_ABILITIES = new ArrayList<>();

	@ConfigValue("Cooldown")
	private static long COOLDOWN = 10000;
	@ConfigValue("Uses")
	private static int MAX_USES = 100;
	@ConfigValue("GenericAbilityCooldown")
	public static long GLOBAL_SUB_ABILITY_COOLDOWN = 750L;

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
			player.setFallDistance(0);
			MetalCablesUtil.pullPlayer(player, target, Grapple.PULL_SPEED);
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
		return player.isDead() || !player.isOnline();
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
		for (WaterArms.Arm side : subAbilityActivationHandler.getSides()) {
			if (
				bPlayer.isOnCooldown("MetalCables-" + side.name()) ||
				bPlayer.isOnCooldown("MetalCables-" + subAbilityActivationHandler.getAbilityClass().getSimpleName() + "-" + side.name())
			) {
				return;
			}
		}
		// if the activator has an ability class associated with it, check whether or not the player can bend it
		// (exit and clear don't have an ability class associated with their activator, and thus return null)
		if (
			subAbilityActivationHandler.getAbilityClass() == null ||
			bPlayer.canBendIgnoreBinds(
				CoreAbility.getAbility(
					subAbilityActivationHandler.getAbilityClass()
				)
			)
		) {
			subAbilityActivationHandler.activate(player, this);
			for (WaterArms.Arm side : subAbilityActivationHandler.getSides()) {
				bPlayer.addCooldown("MetalCables-" + side.name(), GLOBAL_SUB_ABILITY_COOLDOWN);
			}
		}
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
		return COOLDOWN;
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
		listener = new MetalCablesListener(this);
		perm = new Permission("bending.ability." + getName(), PermissionDefault.OP);
		ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
		setupConfigValues();
		FileConfiguration config = ConfigManager.languageConfig.get();
		config.addDefault("Abilities." + getElement() + "." + getName() + ".Description", "This ability allows a metalbender to swing around on long metalcables, as well as slam and grab others with it. To active, press shift. You will see a bar at the top of your screen displaying your uses left for this move. To use any of the subabilities, simply left click with the desired slot selected. With grapple and grab, you can sneak with the ability active to either pull yourself towards the hook, or pull a grabbed entity towards you. Sneaking on Grapple (L&R) will also activate Grab.");
		ConfigManager.languageConfig.save();
	}

	private void setupConfigValues() {
		FileConfiguration config = ConfigManager.getConfig();
		Class<?>[] classesWithConfigValues = new Class<?>[] {
			MetalCablesAbility.class,
			Grapple.class,
			Grab.class,
			Slam.class,
			MetalCablesListener.class
		};
		for (Class<?> classWithConfigValues : classesWithConfigValues) {
			Arrays
				.stream(classWithConfigValues.getDeclaredFields())
				.filter(field -> Modifier.isStatic(field.getModifiers()))
				.forEach(field -> {
					field.setAccessible(true);
					ConfigValue configValue = field.getAnnotation(ConfigValue.class);
					if (configValue == null) return;
					String configPath = getConfigPath(configValue.value(), classWithConfigValues);
					try {
						config.addDefault(
							configPath,
							field.get(null)
						);
						Object valueFromConfig = config.get(configPath);
						if (valueFromConfig != null) {
							field.set(null, valueFromConfig);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						System.err.println("Could not set default value for " + configPath);
					}
				});
		}
		ConfigManager.defaultConfig.save();
	}

	public String getConfigPath(String name, Class<?> clazz) {
		StringBuilder builder = new StringBuilder("ExtraAbilities.FinnBueno.").append(getName()).append(".");
		if (MetalCablesSubAbility.class.isAssignableFrom(clazz)) {
			builder.append(clazz.getSimpleName()).append(".");
		}
		builder.append(name);
		return builder.toString();
	}

	@Override
	public void stop() {
		ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
		HandlerList.unregisterAll(listener);
	}

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
