package me.finnbon.metalcables.listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import me.finnbon.metalcables.MetalCablesAbility;
import me.finnbon.metalcables.subability.Grapple;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import javax.swing.*;

/**
 * @author Finn Bon
 */
public class MetalCablesListener implements Listener {

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
		if (MultiAbilityManager.hasMultiAbilityBound(player, metalCablesAbility.getName())) {
			MetalCablesAbility multiAbility = CoreAbility.getAbility(player, MetalCablesAbility.class);
			multiAbility.activateSubAbility();
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) return;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer.canBend(CoreAbility.getAbility(MetalCablesAbility.class)) && !MultiAbilityManager.hasMultiAbilityBound(player)) {
			new MetalCablesAbility(player);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Grapple grapple = CoreAbility.getAbility(event.getPlayer(), Grapple.class);
		if (grapple != null) {
			grapple.launchUpwards();
		}
	}

}
