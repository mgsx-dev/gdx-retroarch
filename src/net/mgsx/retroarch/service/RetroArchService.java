package net.mgsx.retroarch.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.retroarch.service.db.RetroArchDatabase;
import net.mgsx.retroarch.service.db.RetroArchItem;

public class RetroArchService {

	private static RetroArchService i;
	public static RetroArchService i(){
		return i== null ? i = new RetroArchService() : i;
	}
	
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
	
	public FileHandle getPicture(RetroArchRun run){
		
		RetroArchRun runPlay = runByPath.get(run.romPath);
		// TODO more replacements
		String renamed = runPlay.romName.replaceAll("\\(J\\)", "(Japan)");
		String playList = new FileHandle(runPlay.playlist).nameWithoutExtension();
		FileHandle pict = getRootFolder().child("thumbnails").child(playList).child("Named_Boxarts").child(renamed + ".png");
		
		Array<RetroArchItem> db = dbByCore.get(playList);
		if(db == null){
			try {
				db = new RetroArchDatabase().load(getDatabaseFolder().child(playList + ".rdb"));
				dbByCore.put(playList, db);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(db != null){
			String crc = runPlay.crc.split("\\|")[0].toLowerCase();
			for(RetroArchItem item : db){
				if(item.crc.equals(crc)){
					System.out.println(item.rom_name);
					break;
				}
			}
		}
		
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
		try {
			Process proc = new ProcessBuilder("retroarch", "-f", "-v", "-L", run.corePath, run.romPath)
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
