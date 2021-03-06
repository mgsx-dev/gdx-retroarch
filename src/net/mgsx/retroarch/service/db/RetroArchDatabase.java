package net.mgsx.retroarch.service.db;

import java.io.FileNotFoundException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RetroArchDatabase {

	public Array<RetroArchItem> load(FileHandle file) throws FileNotFoundException
	{
		JsonValue value = new RDBReader().parse(file);
		Array<RetroArchItem> items = new Json().readValue(Array.class, RetroArchItem.class, value);
		String machineName = file.nameWithoutExtension();
		for(RetroArchItem item : items){
			item.machine = machineName ;
		}
		return items;
	}
}
