package net.mgsx.retroarch.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.retroarch.service.db.RetroArchDatabase;
import net.mgsx.retroarch.service.db.RetroArchItem;
import net.mgsx.retroarch.service.model.RetroArchMachine;
import net.mgsx.retroarch.service.model.RetroArchRun;

public class RetroArchService {

	private static RetroArchService i;
	public static RetroArchService i(){
		return i== null ? i = new RetroArchService() : i;
	}
	
	private Comparator<RetroArchItem> itemNameComparator = new Comparator<RetroArchItem>() {
		@Override
		public int compare(RetroArchItem o1, RetroArchItem o2) {
			return o1.name.compareTo(o2.name);
		}
	};
	
	private ObjectMap<String, RetroArchRun> runByPath = new ObjectMap<String, RetroArchRun>();
	private ObjectMap<String, Array<RetroArchItem>> dbByCore = new ObjectMap<String, Array<RetroArchItem>>();
	
	public RetroArchService() {
		loadPlaylists();
	}
	
	public static FileHandle getRootFolder(){
		return new FileHandle(System.getProperty("user.home")).child(".config").child("retroarch");
	}
	
	public static FileHandle getDatabaseFolder(){
		return getRootFolder().child("database").child("rdb");
	}
	public static FileHandle getDatabaseFile(String name) {
		return getDatabaseFolder().child(name + ".rdb");
	}
	
	public Array<RetroArchRun> getHistory(){
		return getLPLFile(getRootFolder().child("content_history.lpl"));
	}
	public Array<RetroArchRun> getFavorites(){
		return getLPLFile(getRootFolder().child("content_favorites.lpl"));
	}
	private void loadPlaylists(){
		
		for(FileHandle playlist : getRootFolder().child("playlists").list()){
			Array<RetroArchRun> items = getLPLFile(playlist);
			for(RetroArchRun item : items){
				runByPath.put(item.romPath, item);
			}
		}
		
	}
	
	public Array<RetroArchMachine> getAllMachines(){
		Array<RetroArchMachine> items = new Array<RetroArchMachine>();
		for(FileHandle file : getDatabaseFolder().list(".rdb")){
			RetroArchMachine item = new RetroArchMachine();
			item.name = file.nameWithoutExtension();
			items.add(item);
		}
		items.sort();
		return items;
	}
	
	public Array<String> getMachineVendors(){
		Array<String> vendors = new Array<String>();
		Array<RetroArchMachine> machines = getAllMachines();
		ObjectMap<String, Array<RetroArchMachine>> map = new ObjectMap<String, Array<RetroArchMachine>>();
		for(RetroArchMachine machine : machines){
			String vendor = machine.name.split(" - ", 2)[0];
			Array<RetroArchMachine> vendorMachines = map.get(vendor);
			if(vendorMachines == null) map.put(vendor, vendorMachines = new Array<RetroArchMachine>());
			vendorMachines.add(machine);
		}
		for(Entry<String, Array<RetroArchMachine>> entry : map){
			vendors.add(entry.key);
		}
		vendors.sort();
		return vendors;
	}
	
	public Array<RetroArchItem> getGameList(RetroArchMachine machine) {
		return getGameList(machine.name);
	}
	public Array<RetroArchItem> getGameList(String coreName) {
		Array<RetroArchItem> db = dbByCore.get(coreName);
		if(db == null){
			try {
				db = new RetroArchDatabase().load(getDatabaseFolder().child(coreName + ".rdb"));
				dbByCore.put(coreName, db);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		db.sort(itemNameComparator);
		return db;
	}
	
	public FileHandle getPicture(RetroArchItem item){
		return getPicture(item.machine, item.name);
	}
	public FileHandle getPicture(RetroArchRun run){
		RetroArchRun runPlay = runByPath.get(run.romPath);
		return getPicture(new FileHandle(runPlay.playlist).nameWithoutExtension(), runPlay.romName);
	}
	public FileHandle getPicture(String machineName, String romName){
		
		// TODO more replacements
		String renamed = romName.replaceAll("\\(J\\)", "(Japan)");
		FileHandle pict = getRootFolder().child("thumbnails").child(machineName).child("Named_Boxarts").child(renamed + ".png");
		
//		Array<RetroArchItem> db = getGameList(machineName);
//		if(db != null){
//			String crc = runPlay.crc.split("\\|")[0].toLowerCase();
//			for(RetroArchItem item : db){
//				if(item.crc.equals(crc)){
//					System.out.println(item.rom_name);
//					break;
//				}
//			}
//		}
		
		if(!pict.exists()) return null;
		return pict;
	}
	
	private Array<RetroArchRun> getLPLFile(FileHandle file){
		Array<RetroArchRun> items = new Array<RetroArchRun>();
		
		String content = file.readString();
		
		Scanner scan = new Scanner(content);
		
		while(scan.hasNextLine()){
			RetroArchRun run = new RetroArchRun();
			run.romPath = scan.nextLine().split("#", 2)[0]; // skip path in archive
			run.romName = scan.nextLine();
			run.corePath = scan.nextLine();
			run.coreName = scan.nextLine();
			run.crc = scan.nextLine();
			run.playlist = scan.nextLine();
			items.add(run);
		}
		scan.close();
		return items;
	}

	public void run(RetroArchRun run) 
	{
		// TODO fullscreen option
		runRetroArch("-f", "-v", "-L", run.corePath, run.romPath);
	}
	
	public void run() {
		runRetroArch();
	}
	
	private void runRetroArch(String...args) {
		// TODO Auto-generated method stub
		String [] command = new String[args.length+1];
		for(int i=0 ; i<args.length ; i++){
			command[i+1] = args[i];
		}
		command[0] = "retroarch";
		try {
			Process proc = new ProcessBuilder(command)
					.inheritIO()
					.start();
			proc.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
