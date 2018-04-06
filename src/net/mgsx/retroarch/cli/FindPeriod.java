package net.mgsx.retroarch.cli;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.retroarch.service.RetroArchService;
import net.mgsx.retroarch.service.db.RetroArchItem;
import net.mgsx.retroarch.service.model.RetroArchMachine;

public class FindPeriod {
	
	protected static interface Getter<T, V>{
		public T get(V item);
	}
	
	protected static <T, V> ObjectMap<T, Array<V>> groupBy(Array<V> items, Getter<T, V> getter){
		ObjectMap<T, Array<V>> map = new ObjectMap<T, Array<V>>();
		for(V item : items){
			T key = getter.get(item);
			if(key != null){
				Array<V> group = map.get(key);
				if(group == null) map.put(key, group = new Array<V>());
				group.add(item);
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		// find("NEC - PC Engine - TurboGrafx 16");
		
		for(RetroArchMachine machine : RetroArchService.i().getAllMachines()){
			find(machine.name);
		}
	}
	
	private static void find(String machine){
		
		System.out.println(machine);
		
		Array<RetroArchItem> games = RetroArchService.i().getGameList(machine);
		Integer minYear = null, maxYear = null;
		int known = 0, unknown = 0;
//		ObjectMap<Integer, Array<RetroArchItem>> gamesByYear = groupBy(games, new Getter<Integer, RetroArchItem>() {
//			@Override
//			public Integer get(RetroArchItem item) {
//				return item.releaseyear == null ? null : Integer.parseInt(item.releaseyear);
//			}
//		});
//		Array<RetroArchItem> lastGames = gamesByYear.get(2016);
		for(RetroArchItem game : games){
			if(game.releaseyear != null){
				int year = Integer.parseInt(game.releaseyear);
				if(minYear == null || minYear > year) minYear = year;
				if(maxYear == null || maxYear < year) maxYear = year;
				known++;
			}else{
				unknown++;
			}
		}
		System.out.println("known : " + known + " / " + (known + unknown));
		if(known > 0)
			System.out.println("period : " + minYear + "-" + maxYear);
		else
			System.out.println("period unknown");
	}
}
