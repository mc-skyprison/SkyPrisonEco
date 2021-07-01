package net.skyprison.skyprisoneco.commands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.google.inject.Inject;
import net.skyprison.skyprisoneco.SkyPrisonEco;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class Tokens implements CommandExecutor {
	private final SkyPrisonEco plugin;

	@Inject
	public Tokens(SkyPrisonEco plugin) {
		this.plugin = plugin;
	}


	private void sendHelpMessage(Player player) {
		player.sendMessage(plugin.colourMessage("&8m━━━━━━━━━━━━━━━━━|  &bTokens &8&m|━━━━━━━━━━━━━━━━━━"));
		player.sendMessage(plugin.colourMessage("&b/tokens balance (player) &8» &7Check your own or other players token balance"));
		player.sendMessage(plugin.colourMessage("&b/tokens shop &8» &7Opens the token shop"));
		player.sendMessage(plugin.colourMessage("&b/tokens top &8» &7Displays the top token balances"));
		if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
			player.sendMessage(plugin.colourMessage("&b/tokens add <player> <anount> &8» &7Adds tokens to the specified player"));
			player.sendMessage(plugin.colourMessage("&b/tokens remove <player> <anount> &8» &7Removes tokens from the specified player"));
			player.sendMessage(plugin.colourMessage("&b/tokens set <player> <anount> &8» &7Sets tokens of the specified amount for the specified player"));
			player.sendMessage(plugin.colourMessage("&b/tokens giveall <amount> &8» &7Gives tokens of the specified amount to everyone online"));
		}
	}


	public void addTokens(CMIUser player, Integer amount) {
		File fData = new File(plugin.getDataFolder() + File.separator + "tokensdata.yml");
		YamlConfiguration tokenConf = YamlConfiguration.loadConfiguration(fData);

		int tokens = amount;
		if(tokenConf.contains("players." + player.getUniqueId().toString())) {
			tokens += tokenConf.getInt("players." + player.getUniqueId());
		}
		tokenConf.set("players." + player.getUniqueId(), tokens);
		try {
			tokenConf.save(fData);
			plugin.tokensData.put(player.getUniqueId().toString(), (long) tokens);
			if(player.isOnline())
				player.sendMessage(plugin.colourMessage("&bTokens &8» &7You received &b" + plugin.formatNumber(amount) + " tokens"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		File fData = new File(plugin.getDataFolder() + File.separator + "tokensdata.yml");
		YamlConfiguration tokenConf = YamlConfiguration.loadConfiguration(fData);
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "help":
						sendHelpMessage(player);
						break;
					case "giveall":
						if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
							if(args.length > 1) {
								if (plugin.isInt(args[1])) {
									if(Integer.parseInt(args[2]) >= 0) {
										for(Player oPlayer : Bukkit.getOnlinePlayers()) {
											CMIUser user = CMI.getInstance().getPlayerManager().getUser(oPlayer);
											addTokens(user, Integer.parseInt(args[2]));
										}
									} else {
										player.sendMessage(plugin.colourMessage("&cThe number must be positive!"));
									}
								} else {
									player.sendMessage(plugin.colourMessage("&cThe amount must be a number!"));
								}
								try {
									tokenConf.save(fData);
									player.sendMessage(plugin.colourMessage("&bTokens &8» &7Added &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7to everyone online"));
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								player.sendMessage(plugin.colourMessage("&cCorrect Usage: /tokens giveall <amount>"));
							}
						} else {
							player.sendMessage(plugin.colourMessage("&4Error:&c You do not have permission to execute this command..."));
						}
						break;
					case "add":
						if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
							if(args.length > 2) {
								if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
									CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
									if (plugin.isInt(args[2])) {
										if(Integer.parseInt(args[2]) >= 0) {
											addTokens(oPlayer, Integer.parseInt(args[2]));
											player.sendMessage(plugin.colourMessage("&bTokens &8» &7Added &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7to " + oPlayer.getName()));
										} else {
											player.sendMessage(plugin.colourMessage("&cThe number must be positive!"));
										}
									} else {
										player.sendMessage(plugin.colourMessage("&cThe amount must be a number!"));
									}
								}
							} else {
								player.sendMessage(plugin.colourMessage("&cCorrect Usage: /tokens add <player> <amount>"));
							}
						} else {
							player.sendMessage(plugin.colourMessage("&4Error:&c You do not have permission to execute this command..."));
						}
						break;
					case "remove":
						if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
							if(args.length > 2) {
								if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
									CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
									if(tokenConf.contains("players." + oPlayer.getUniqueId().toString())) {
										int tokens = tokenConf.getInt("players." + oPlayer.getUniqueId());
										if(tokens != 0) {
											if (plugin.isInt(args[2])) {
												tokens -= Integer.parseInt(args[2]);
												tokenConf.set("players." + oPlayer.getUniqueId(), Math.max(tokens, 0));
												try {
													tokenConf.save(fData);
													player.sendMessage(plugin.colourMessage("&bTokens &8» &7Removed &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7from " + oPlayer.getName()));
													plugin.tokensData.put(player.getUniqueId().toString(), (long) tokens);
													if(oPlayer.isOnline())
														oPlayer.sendMessage(plugin.colourMessage("&bTokens &8» &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7was removed from your balance"));
												} catch (IOException e) {
													e.printStackTrace();
												}
											} else {
												player.sendMessage(plugin.colourMessage("&cThe amount must be a number!"));
											}
										} else {
											player.sendMessage(plugin.colourMessage("&cYou can't remove tokens from soneone with 0 tokens!"));
										}
									} else {
										player.sendMessage(plugin.colourMessage("&cYou can't remove tokens from soneone with 0 tokens!"));
									}
								}
							} else {
								player.sendMessage(plugin.colourMessage("&cCorrect Usage: /tokens remove <player> <amount>"));
							}
						} else {
							player.sendMessage(plugin.colourMessage("&4Error:&c You do not have permission to execute this command..."));
						}
						break;
					case "set":
						if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
							if(args.length > 2) {
								if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
									CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
									if (plugin.isInt(args[2])) {
										if(Integer.parseInt(args[2]) >= 0) {
											tokenConf.set("players." + oPlayer.getUniqueId(), Integer.parseInt(args[2]));
											try {
												tokenConf.save(fData);
												player.sendMessage(plugin.colourMessage("&bTokens &8» &7Set " + oPlayer.getName() + "'s tokens to &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens"));
												plugin.tokensData.put(player.getUniqueId().toString(), Long.parseLong(args[2]));
												if(oPlayer.isOnline())
													oPlayer.sendMessage(plugin.colourMessage("&bTokens &8» &7SYour token balance was set to &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens"));
											} catch (IOException e) {
												e.printStackTrace();
											}
										} else {
											player.sendMessage(plugin.colourMessage("&cYou can't set the token balance to a negative number!"));
										}
									} else {
										player.sendMessage(plugin.colourMessage("&cThe amount must be a number!"));
									}
								}
							} else {
								player.sendMessage(plugin.colourMessage("&cCorrect Usage: /tokens set <player> <amount>"));
							}
						} else {
							player.sendMessage(plugin.colourMessage("&4Error:&c You do not have permission to execute this command..."));
						}
						break;
					case "update":
						if(player.hasPermission("skyprisoneco.command.tokens.admin")) {
							final Set<String> playerUUIDs = Objects.requireNonNull(tokenConf.getConfigurationSection("players")).getKeys(false);
							plugin.tokensData = new HashMap<>();
							for(final String pUUID : playerUUIDs) {
								plugin.tokensData.put(pUUID, tokenConf.getLong("players." + pUUID));
							}
						} else {
							player.sendMessage(plugin.colourMessage("&4Error:&c You do not have permission to execute this command..."));
						}
						break;
					case "balance":
					case "bal":
						if(args.length > 1) {
							if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
								CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
								if(tokenConf.contains("players." + oPlayer.getUniqueId().toString())) {
									player.sendMessage(plugin.colourMessage("&bTokens &8» &7" + oPlayer.getName() + "'s token balance is &b" + plugin.formatNumber(plugin.tokensData.get(player.getUniqueId().toString())) + " tokens"));
								} else {
									player.sendMessage(plugin.colourMessage("&bTokens &8» &7" + oPlayer.getName() + "'s token balance is &b0 tokens"));
								}
							} else {
								player.sendMessage(plugin.colourMessage("&cPlayer does not exist!"));
							}
						} else {
							if(tokenConf.contains("players." + player.getUniqueId())) {
								player.sendMessage(plugin.colourMessage("&bTokens &8» &7Your token balance is &b" + plugin.formatNumber(plugin.tokensData.get(player.getUniqueId().toString())) + " tokens"));
							} else {
								player.sendMessage(plugin.colourMessage("&bTokens &8» &7Your token balance is &b0 tokens"));
							}
						}
						break;
					case "shop":
						Bukkit.dispatchCommand(player, "cp tokenshop");
						break;
					case "top":
						player.chat("/lb tokens");
						break;
				}
			} else {
				sendHelpMessage(player);
			}
		} else {
			if(args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "giveall":
						if(args.length > 1) {
							for(Player oPlayer : Bukkit.getOnlinePlayers()) {
								int tokens = 0;
								if(tokenConf.contains("players." + oPlayer.getUniqueId())) {
									tokens = tokenConf.getInt("players." + oPlayer.getUniqueId());
								}
								if (plugin.isInt(args[1])) {
									tokens += Integer.parseInt(args[1]);
									tokenConf.set("players." + oPlayer.getUniqueId(), tokens);
								} else {
									plugin.tellConsole(plugin.colourMessage("&cThe amount must be a number!"));
								}
							}
							try {
								tokenConf.save(fData);
								plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7Added &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7to everyone online"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							plugin.tellConsole(plugin.colourMessage("&cCorrect Usage: /tokens giveall <amount>"));
						}
						break;
					case "add":
						if(args.length > 2) {
							if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
								CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
								if (plugin.isInt(args[2])) {
									if(Integer.parseInt(args[2]) >= 0) {
										addTokens(oPlayer, Integer.parseInt(args[2]));
										plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7Added &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7to " + oPlayer.getName()));
									} else {
										plugin.tellConsole(plugin.colourMessage("&cThe number must be positive!"));
									}
								} else {
									plugin.tellConsole(plugin.colourMessage("&cThe amount must be a number!"));
								}
							}
						} else {
							plugin.tellConsole(plugin.colourMessage("&cCorrect Usage: /tokens add <player> <amount>"));
						}
						break;
					case "remove":
						if(args.length > 2) {
							if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
								CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
								if(tokenConf.contains("players." + oPlayer.getUniqueId().toString())) {
									int tokens = tokenConf.getInt("players." + oPlayer.getUniqueId());
									if(tokens != 0) {
										if (plugin.isInt(args[2])) {
											tokens -= Integer.parseInt(args[2]);
											tokenConf.set("players." + oPlayer.getUniqueId(), Math.max(tokens, 0));
											try {
												tokenConf.save(fData);
												plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7Removed &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7from " + oPlayer.getName()));
												if(oPlayer.isOnline())
													oPlayer.sendMessage(plugin.colourMessage("&bTokens &8» &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens &7was removed from your balance"));
												plugin.tokensData.put(oPlayer.getUniqueId().toString(), (long) tokens);
											} catch (IOException e) {
												e.printStackTrace();
											}
										} else {
											plugin.tellConsole(plugin.colourMessage("&cThe amount must be a number!"));
										}
									} else {
										plugin.tellConsole(plugin.colourMessage("&cYou can't remove tokens from soneone with 0 tokens!"));
									}
								} else {
									plugin.tellConsole(plugin.colourMessage("&cYou can't remove tokens from soneone with 0 tokens!"));
								}
							}
						} else {
							plugin.tellConsole(plugin.colourMessage("&cCorrect Usage: /tokens remove <player> <amount>"));
						}
						break;
					case "set":
						if(args.length > 2) {
							if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
								CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
								if (plugin.isInt(args[2])) {
									if(Integer.parseInt(args[2]) >= 0) {
										tokenConf.set("players." + oPlayer.getUniqueId(), Integer.parseInt(args[2]));
										try {
											tokenConf.save(fData);
											plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7Set " + oPlayer.getName() + "'s tokens to &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens"));
											if(oPlayer.isOnline())
												oPlayer.sendMessage(plugin.colourMessage("&bTokens &8» &7SYour token balance was set to &b" + plugin.formatNumber(Integer.parseInt(args[2])) + " tokens"));
											plugin.tokensData.put(oPlayer.getUniqueId().toString(), Long.parseLong(args[2]));
										} catch (IOException e) {
											e.printStackTrace();
										}
									} else {
										plugin.tellConsole(plugin.colourMessage("&cYou can't set the token balance to a negative number!"));
									}
								} else {
									plugin.tellConsole(plugin.colourMessage("&cThe amount must be a number!"));
								}
							}
						} else {
							plugin.tellConsole(plugin.colourMessage("&cCorrect Usage: /tokens set <player> <amount>"));
						}
						break;
					case "balance":
					case "bal":
						if(args.length > 1) {
							if(CMI.getInstance().getPlayerManager().getUser(args[1]) != null) {
								CMIUser oPlayer = CMI.getInstance().getPlayerManager().getUser(args[1]);
								if(tokenConf.contains("players." + oPlayer.getUniqueId().toString())) {
									plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7" + oPlayer.getName() + "'S token balance is &b" + plugin.formatNumber(plugin.tokensData.get(oPlayer.getUniqueId().toString())) + "tokens"));
								} else {
									plugin.tellConsole(plugin.colourMessage("&bTokens &8» &7" + oPlayer.getName() + "'S token balance is &b0 tokens"));
								}
							} else {
								plugin.tellConsole(plugin.colourMessage("&cPlayer does not exist!"));
							}
						} else {
							plugin.tellConsole(plugin.colourMessage("&cYou must specify a player!!"));
						}
						break;
					case "top":
						break;
				}
			}
		}
		return true;
	}
}
