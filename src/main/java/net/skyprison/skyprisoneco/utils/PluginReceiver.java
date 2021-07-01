package net.skyprison.skyprisoneco.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.skyprison.skyprisoneco.SkyPrisonEco;

public class PluginReceiver extends AbstractModule {

	protected final SkyPrisonEco plugin;

	public PluginReceiver(SkyPrisonEco plugin) {
		this.plugin = plugin;
	}

	public Injector createInjector() {
		return Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		this.bind(SkyPrisonEco.class).toInstance(this.plugin);
	}
}
