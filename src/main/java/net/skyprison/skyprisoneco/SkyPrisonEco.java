package net.skyprison.skyprisoneco;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.skyprison.skyprisoneco.commands.Tokens;
import net.skyprison.skyprisoneco.utils.CommandsTabCompleter;
import net.skyprison.skyprisoneco.utils.LangCreator;
import net.skyprison.skyprisoneco.utils.Placeholders;
import net.skyprison.skyprisoneco.utils.PluginReceiver;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SkyPrisonEco extends JavaPlugin {

	@Inject private Tokens tokens;
	@Inject private CommandsTabCompleter CommandsTabCompleter;

	@Inject private LangCreator langCreator;

	public Map<String, Long> tokensData;

	@Override
	public void onEnable() {
		PluginReceiver module = new PluginReceiver(this);
		Injector injector = module.createInjector();
		injector.injectMembers(this);

		this.langCreator.init();

		File f = new File(this.getDataFolder() + File.separator + "tokensdata.yml");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tokensData = new HashMap<>();
		File fData = new File(this.getDataFolder() + File.separator + "tokensdata.yml");
		YamlConfiguration tokenConf = YamlConfiguration.loadConfiguration(fData);

		final Set<String> playerUUIDs = tokenConf.getConfigurationSection("players").getKeys(false);

		for(final String pUUID : playerUUIDs) {
			tokensData.put(pUUID, tokenConf.getLong("players." + pUUID));
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new Placeholders(this).register();
			getLogger().info("Placeholders registered");
		}

		Objects.requireNonNull(getCommand("tokens")).setExecutor(tokens);
		Objects.requireNonNull(getCommand("token")).setExecutor(tokens);
		Objects.requireNonNull(getCommand("tokens")).setTabCompleter(CommandsTabCompleter);
		Objects.requireNonNull(getCommand("token")).setTabCompleter(CommandsTabCompleter);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled SkyPrisonCore v" + getDescription().getVersion());
	}

	public String colourMessage(String message) {
		message = translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', message));
		return message;
	}


	public void tellConsole(String message){
		Bukkit.getConsoleSender().sendMessage(message);
	}
	public void asConsole(String command){
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public String formatNumber(double value) {
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		return df.format(value);
	}

	public String translateHexColorCodes(String message) {
		final Pattern hexPattern = Pattern.compile("\\{#" + "([A-Fa-f0-9]{6})" + "}");
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
					+ ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
					+ ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
					+ ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
			);
		}
		return matcher.appendTail(buffer).toString();
	}

	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
