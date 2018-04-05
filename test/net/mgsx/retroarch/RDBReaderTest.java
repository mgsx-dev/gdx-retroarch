package net.mgsx.retroarch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.retroarch.service.db.RetroArchDatabase;
import net.mgsx.retroarch.service.db.RetroArchItem;

public class RDBReaderTest {
	
	@Test
	public void testReadDatabase() throws IOException {
		Array<RetroArchItem> items = new RetroArchDatabase().load(new FileHandle("test/example.rdb"));
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size);
		
		RetroArchItem item1 = items.get(0);
		
		Assert.assertEquals("RPG Maker 2003 Test Suite", item1.name);
		Assert.assertEquals("RPG Maker 2003 Test Suite", item1.description);
		Assert.assertEquals("c36c5324", item1.crc);
		Assert.assertEquals("a55aec8a8b6bdeda883874ff9150ace1", item1.md5);
		Assert.assertEquals("RPG_RT.ini", item1.rom_name);
		Assert.assertEquals("3e1cf524c60ae73f61b3639c1ff8478849d9822a", item1.sha1);
		Assert.assertEquals(77, item1.size);
		
		RetroArchItem item2 = items.get(1);
		
		Assert.assertEquals("RPG Maker 2000 Test Suite", item2.name);
		Assert.assertEquals("RPG Maker 2000 Test Suite", item2.description);
		Assert.assertEquals("bd141b82", item2.crc);
		Assert.assertEquals("58f83b777a9d6d7e6d929c82bf2c1252", item2.md5);
		Assert.assertEquals("RPG_RT.ini", item2.rom_name);
		Assert.assertEquals("45b830034deae5491776f69d923336ff05194d6d", item2.sha1);
		Assert.assertEquals(77, item2.size);

	}
}

