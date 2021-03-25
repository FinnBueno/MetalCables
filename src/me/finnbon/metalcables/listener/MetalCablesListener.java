package me.finnbon.metalcables.listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import me.finnbon.metalcables.MetalCablesAbility;
import me.finnbon.metalcables.subability.Grapple;
import me.finnbon.metalcables.util.ConfigValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Finn Bon
 */
public class MetalCablesListener implements Listener {

	@ConfigValue("CanBendWithWeapons")
	public static boolean CAN_BEND_WITH_WEAPONS = true;
	@ConfigValue("SourceRequired")
	public static boolean SOURCE_REQUIRED = true;
	@ConfigValue("ValidSourceItems")
	public static List<String> VALID_SOURCE_ITEMS = Arrays.asList(
		Material.GOLD_ORE.name(), Material.IRON_ORE.name(), Material.GOLD_BLOCK.name(),
		Material.IRON_BLOCK.name(), Material.IRON_DOOR.name(), Material.IRON_BARS.name(),
		Material.LIGHT_WEIGHTED_PRESSURE_PLATE.name(), Material.HEAVY_WEIGHTED_PRESSURE_PLATE.name(),
		Material.IRON_TRAPDOOR.name(), Material.IRON_SHOVEL.name(), Material.IRON_PICKAXE.name(),
		Material.IRON_AXE.name(), Material.IRON_INGOT.name(), Material.GOLD_INGOT.name(),
		Material.IRON_SWORD.name(), Material.GOLDEN_SWORD.name(), Material.GOLDEN_SHOVEL.name(),
		Material.GOLDEN_PICKAXE.name(), Material.GOLDEN_AXE.name(), Material.IRON_HOE.name(),
		Material.GOLDEN_HOE.name(), Material.IRON_HELMET.name(), Material.IRON_CHESTPLATE.name(),
		Material.IRON_LEGGINGS.name(), Material.IRON_BOOTS.name(), Material.GOLDEN_HELMET.name(),
		Material.GOLDEN_CHESTPLATE.name(), Material.GOLDEN_LEGGINGS.name(),
		Material.GOLDEN_BOOTS.name(), Material.GOLDEN_APPLE.name(), Material.IRON_DOOR.name(),
		Material.IRON_HORSE_ARMOR.name(), Material.GOLDEN_HORSE_ARMOR.name(),
		Material.IRON_BOOTS.name(), Material.GOLDEN_LEGGINGS.name(), Material.CHAINMAIL_BOOTS.name(),
		Material.IRON_LEGGINGS.name(), Material.GOLDEN_LEGGINGS.name(), Material.CHAINMAIL_LEGGINGS.name(),
		Material.IRON_CHESTPLATE.name(), Material.GOLDEN_CHESTPLATE.name(),
		Material.CHAINMAIL_CHESTPLATE.name(), Material.IRON_HELMET.name(), Material.GOLDEN_HELMET.name(),
		Material.CHAINMAIL_HELMET.name()
	);

	private final MetalCablesAbility metalCablesAbility;

	public MetalCablesListener(MetalCablesAbility metalCablesAbility) {
		this.metalCablesAbility = metalCablesAbility;
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(this, ProjectKorra.plugin);
	}

	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (MultiAbilityManager.hasMultiAbilityBound(player, metalCablesAbility.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (!CAN_BEND_WITH_WEAPONS && GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType())) return;
		if (MultiAbilityManager.hasMultiAbilityBound(player, metalCablesAbility.getName())) {
			System.out.println("PlayerAnimationEvent");
			MetalCablesAbility multiAbility = CoreAbility.getAbility(player, MetalCablesAbility.class);
			if (multiAbility == null) return;
			multiAbility.activateSubAbility();
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (!CAN_BEND_WITH_WEAPONS && GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType())) return;
		if (player.isSneaking()) return;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer.canBend(CoreAbility.getAbility(MetalCablesAbility.class)) && !MultiAbilityManager.hasMultiAbilityBound(player)) {
			if (SOURCE_REQUIRED && !hasValidSourceItem(player)) return;
			new MetalCablesAbility(player);
		}
	}

	private boolean hasValidSourceItem(Player player) {
		PlayerInventory inventory = player.getInventory();
		List<ItemStack> sourceSlots = new LinkedList<>(Arrays.asList(inventory.getArmorContents()));
		sourceSlots.add(inventory.getItemInMainHand());
		sourceSlots.add(inventory.getItemInOffHand());
		for (ItemStack sourceSlot : sourceSlots) {
			if (sourceSlot != null && VALID_SOURCE_ITEMS.contains(sourceSlot.getType().name())) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!CAN_BEND_WITH_WEAPONS && GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType())) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Grapple grapple = CoreAbility.getAbility(player, Grapple.class);
		if (grapple != null) {
			grapple.launchUpwards();
		}
	}

}
