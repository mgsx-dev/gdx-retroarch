package net.mgsx.retroarch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.retroarch.service.RetroArchService;
import net.mgsx.retroarch.service.db.RetroArchDatabase;
import net.mgsx.retroarch.service.db.RetroArchItem;
import net.mgsx.retroarch.service.model.RetroArchMachine;

@RunWith(Parameterized.class)
public class RDBReaderAllTest {
	
	@Parameters
	public static Object[][] data(){
		Array<RetroArchMachine> machines = RetroArchService.i().getAllMachines();
		Object[][] objects = new Object[machines.size][];
		for(int i=0 ; i<machines.size ; i++){
			
			FileHandle file = RetroArchService.getDatabaseFile(machines.get(i).name);
			objects[i] = new Object[]{file};
		}
		return objects;
	}
	
	@Parameter
	public FileHandle file;
	
	@Test
	public void testReadDatabase() throws IOException {
		
		Array<RetroArchItem> items = new RetroArchDatabase().load(file);
		Assert.assertNotNull(items);

	}
}

