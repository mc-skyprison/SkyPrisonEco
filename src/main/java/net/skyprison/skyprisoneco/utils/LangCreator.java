package net.skyprison.skyprisoneco.utils;

import com.google.inject.Inject;
import net.skyprison.skyprisoneco.SkyPrisonEco;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LangCreator {
	private SkyPrisonEco plugin;

	@Inject
	public LangCreator(SkyPrisonEco plugin) {
		this.plugin = plugin;
	}

	public void init() {
		File lang = new File(plugin.getDataFolder() + File.separator
				+ "lang" + File.separator + plugin.getConfig().getString("lang-file"));
		try {
			FileConfiguration langConf = YamlConfiguration.loadConfiguration(lang);

			// Global Messages

			langConf.addDefault("global.plugin-prefix", "&f[&cSkyPrisonEco&f] ");

			langConf.options().copyDefaults(true);
			langConf.save(lang);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
