package me.finnbon.metalcables;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.waterbending.multiabilities.WaterArms;
import me.finnbon.metalcables.listener.MetalCablesListener;
import me.finnbon.metalcables.subability.Grapple;
import me.finnbon.metalcables.subability.MetalCablesSubAbility;
import me.finnbon.metalcables.util.MetalCablesUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static long MAX_USES;

	static {
		SUB_ABILITIES.addAll(Arrays.asList(
			new Grapple.Right(),
			new Grapple.Both(),
			new Grapple.Left()
//			Clear.class,
//			Grab.Left.class,
//			Grab.Left.class,
//			Slam.Left.class,
//			Slam.Right.class,
//			Exit.class
		));

		FileConfiguration c = ConfigManager.defaultConfig.get();
		COOLDOWN = c.getLong("ExtraAbilities.Finn_Bueno_.MetalCables.Cooldown");
		MAX_USES = c.getInt("ExtraAbilities.Finn_Bueno_.MetalCables.Uses");
	}

	public MetalCablesAbility(Player player) {
		super(player);
		// open multi ability menu
		MultiAbilityManager.bindMultiAbility(player, getName());
		System.out.printf("Found %s abilities%n", String.valueOf(MultiAbilityManager.getMultiAbility(getName()).getAbilities().size()));

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
		MetalCablesSubAbility.SubAbilityActivationHandler subAbilityActivationHandler = SUB_ABILITIES.get(slot);
		subAbilityActivationHandler.activate(player);
	}

	@Override
	public void remove() {
		super.remove();
		bPlayer.addCooldown(this);
		MultiAbilityManager.unbindMultiAbility(player);
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
