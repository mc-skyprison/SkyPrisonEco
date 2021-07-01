package net.skyprison.skyprisoneco.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.skyprison.skyprisoneco.SkyPrisonEco;
import com.google.inject.Inject;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;

public class Placeholders extends PlaceholderExpansion {
	private SkyPrisonEco plugin;

	@Inject
	public Placeholders(SkyPrisonEco plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist(){
		return true;
	}

	@Override
	public boolean canRegister(){
		return true;
	}

	@Override
	public String getAuthor(){
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier(){
		return "SkyPrisonEco";
	}

	@Override
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}



	@Override
	public String onPlaceholderRequest(Player player, String identifier){

		if(player == null) {
			return "";
		}

		if(identifier.equalsIgnoreCase("token_balance_formatted")) {
			if(plugin.tokensData.containsKey(player.getUniqueId().toString())) {
				return plugin.formatNumber(plugin.tokensData.get(player.getUniqueId().toString()));
			} else {
				return "0";
			}
		}

		if(identifier.equalsIgnoreCase("token_balance")) {
			if(plugin.tokensData.containsKey(player.getUniqueId().toString())) {
				return String.valueOf(plugin.tokensData.get(player.getUniqueId().toString()));
			} else {
				return "0";
			}
		}

		return null;
	}
}

