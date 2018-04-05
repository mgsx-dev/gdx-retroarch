package net.mgsx.retroarch.service.model;

import com.badlogic.gdx.files.FileHandle;

public class RetroArchRun {
	public String romPath, 
	romName,
	corePath, 
	coreName, 
	crc,
	playlist;
	
	@Override
	public String toString() {
		String name = new FileHandle(romPath).nameWithoutExtension();
		name += " (" + coreName + ")";
		return name;
	}
}
