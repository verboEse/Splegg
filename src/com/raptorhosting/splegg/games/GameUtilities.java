package com.raptorhosting.splegg.games;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.earth2me.essentials.*;

import com.raptorhosting.splegg.Splegg;
import com.raptorhosting.splegg.players.SpleggPlayer;

public class GameUtilities {
	
	public HashMap<String, Game> GAMES = new HashMap<String, Game>();
	
	public Game getGame(String map) {
		return (Game)GAMES.get(map);
	}
	
	public void addGame(String map, Game game) {
		GAMES.put(map, game);
	}
	
	public SpleggPlayer getPlayer(Player player) {
		
		if (player == null) {
			return null;
		}
		
		SpleggPlayer sp = null;
		for (Game games : this.GAMES.values()) {
			for (SpleggPlayer sps : games.players.values()) {
				if (sps.getPlayer().getName().equalsIgnoreCase(player.getName())) {
					sp = sps;
				}
			}
		}
		return sp;
	}
	
	public Game getMatchedGame(Player player) {
		Game game = null;
		for (Game g : GAMES.values()) {
			if (g.players.containsKey(player.getName())) {
				game = g;
			}
		}
		return game;
	}
	
	public int howManyOpenGames() {
	    ArrayList<Game> all = new ArrayList<Game>();
	    for (Game games : this.GAMES.values()) {
	      if (games.getStatus() == Status.LOBBY) {
	        all.add(games);
	      }
	    }

	    return all.size();
	  }
	
	public void checkWinner(Game game) {
		if (game.players.size() <= 1) {
			if (game.players.size() == 0) {
				game.splegg.game.stopGame(game, 0);
			} else {
				String w = "";
				for (SpleggPlayer sp : game.players.values()) {
					w = sp.getPlayer().getName();
					
					if (game.splegg.getConfig().getBoolean("fireworks")) {
						
						for (int i = 0; i < 8; i++) {
							Firework fw = (Firework)sp.getPlayer().getWorld().spawnEntity(sp.getPlayer().getLocation(), EntityType.FIREWORK);
						    FireworkMeta fwm = fw.getFireworkMeta();
						    Random r = new Random();

						    int rt = r.nextInt(4) + 1;
						    FireworkEffect.Type type = FireworkEffect.Type.BALL;
						    if (rt == 1) type = FireworkEffect.Type.BALL;
						    if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
						    if (rt == 3) type = FireworkEffect.Type.BURST;
						    if (rt == 4) type = FireworkEffect.Type.CREEPER;
						    if (rt == 5) type = FireworkEffect.Type.STAR;

						    Color c1 = Color.RED;
						    Color c2 = Color.FUCHSIA;
						    Color c3 = Color.ORANGE;
						    Color c4 = Color.TEAL;

						    FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withColor(c2).withFade(c3).withColor(c4).with(type).trail(false).build();
						    fwm.addEffect(effect);

						    fwm.setPower(1);

						    fw.setFireworkMeta(fwm);
						}
					}
					
					game.leaveGame(sp.getUtilPlayer());
					if ( game.splegg.getConfig().getBoolean("economy.use") ) {
						Player sp1 = sp.getPlayer();
						Essentials ess = Splegg.getSplegg().getEssentials();
						User wp = ess.getUser(sp1);
						double c = game.splegg.getConfig().getDouble("economy.win");
						BigDecimal amount = new BigDecimal (c);
						wp.giveMoney(amount);
					}
				}
				
				game.splegg.chat.bc("&b" + (game.splegg.special.contains(w) ? "§4" : "§a") + w + "&6 has won Splegg on &c" + game.map.getName() + "&6.");
				game.splegg.game.stopGame(game, 5);
			}
		}
	}
	
}
