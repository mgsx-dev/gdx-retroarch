package net.mgsx.retroarch.service.db;

public class RetroArchItem {
	public String name, description, rom_name, crc, md5, sha1;
	public int size;
	
	public String releasemonth, releaseyear, developer, publisher, serial, genre, users, tgdb_rating, franchise, coop;
	
	public String edge_rating, rumble, esrb_rating, enhancement_hw, origin, edge_issue, elspa_rating, edge_review, 
	analog, famitsu_rating;
	
	public transient String machine;

	@Override
	public String toString() {
		return name;
	}
}
