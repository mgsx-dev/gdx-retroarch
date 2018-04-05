package net.mgsx.retroarch.service.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.JsonValue.ValueType;

public class RDBReader implements BaseJsonReader {

	@Override
	public JsonValue parse(FileHandle file) {
		return parse(file.read());
	}
	
	@Override
	public JsonValue parse(InputStream input) {
		ByteBuffer buffer;
		try {
			byte[] bytes = new byte[input.available()];
			input.read(bytes);
			buffer = ByteBuffer.wrap(bytes);
			return parse(buffer);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	private JsonValue parse(ByteBuffer buffer)
	{
		byte [] head = new byte[16];
        buffer.get(head); // RARCHDB.........
        char [] header = {'R', 'A', 'R', 'C', 'H', 'D', 'B', 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for(int i=0 ; i<header.length ; i++){
        	if(head[i] != header[i]) throw new GdxRuntimeException("RDB header mismatch");
        }
        
        JsonValue items = parseArray(buffer);
        JsonValue root = parseObject(buffer);
        
        int itemsSize = 0;
        for(JsonIterator i = items.iterator() ; i.hasNext() ; i.next()) itemsSize++;
        
		int count = root.getInt("count");
		if(count != itemsSize){
			throw new GdxRuntimeException("entries count mismatch : found=" + itemsSize + ", count=" + count);
		}
		if(buffer.hasRemaining()){
			throw new GdxRuntimeException("EOF expected");
		}
    
        return items;
	}
	
	private JsonValue parseArray(ByteBuffer buffer){
		JsonValue items = new JsonValue(ValueType.array);
		for(;;){
			JsonValue item = parseObject(buffer);
    		if(item == null) break;
    		items.addChild(item);
        }
		return items;
	}
	
	private JsonValue parseObject(ByteBuffer buffer){
		
		int headToken = buffer.get() & 0xFF;
		if(headToken == 0xc0) return null;
		int numFields;
		if(headToken == 0xdf){
			numFields = buffer.getInt(); // 32 bits length
		}else if(headToken >> 7 == 1){
			numFields = headToken & 0xF; // 4 bits length
		}else{
			throw new GdxRuntimeException("invalid token " + String.format("%x", headToken));
		}
		
		JsonValue item = new JsonValue(ValueType.object);
		
		for(int fieldIndex=0 ; fieldIndex<numFields ; fieldIndex++){
			String fieldName = parseString(buffer);
			JsonValue fieldValue = parseValue(buffer);
			item.addChild(fieldName, fieldValue);
		}
		
		return item;
	}
	
	private String parseString(ByteBuffer buffer){
		int fieldToken = buffer.get() & 0xFF;
		if((fieldToken & 0xa0) == 0xa0){
			int fieldNameSize = fieldToken & 0x1F;
			return parseString(buffer, fieldNameSize);
		}else{
			throw new GdxRuntimeException("string expected");
		}
	}
	private String parseString(ByteBuffer buffer, int len){
		byte[] bytes = new byte[len];
		buffer.get(bytes);
		return new String(bytes);
	}

	private final static char[] hexArray = "0123456789abcdef".toCharArray();
	
	private JsonValue parseValue(ByteBuffer buffer){
		int fieldToken = buffer.get() & 0xFF;
		if(fieldToken == 0xc4){
			// binary to hex string
			int binLen = buffer.get() & 0xFF;
			char [] chars = new char[binLen*2];
			for(int i=0 ; i<binLen ; i++){
				int b = buffer.get() & 0xFF;
				chars[i*2] = hexArray[b >> 4];
				chars[i*2+1] = hexArray[b & 0xF];
			}
			return new JsonValue(new String(chars));
		}
		else if(fieldToken == 0xcd){
			// size 16 bits
			int num = buffer.getShort() & 0xFFFF;
			return new JsonValue(num); 
		}
		else if(fieldToken == 0xce){
			// size 32 bits
			long num = buffer.getInt() & 0xFFFFFFFFL;
			return new JsonValue(num); 
		}
		else if(fieldToken == 0xcf){
			// size 64 bits TODO unsigned ?
			long num = buffer.getLong();
			if(num < 0){ 
				throw new GdxRuntimeException("64 bits unsigned integer not supported");
			}
			return new JsonValue(num); 
		}
		else if(fieldToken == 0xcc){
			// size 8 bits
			int num = buffer.get() & 0xFF;
			return new JsonValue(num);
		}
		else if((fieldToken & 0xa0) == 0xa0){
			int fieldNameSize = fieldToken & 0x1F;
			return new JsonValue(parseString(buffer, fieldNameSize));
		}else if((fieldToken & 0xd0) == 0xd0){
			int fieldNameSize;
			if(fieldToken == 0xd9){
				fieldNameSize = buffer.get() & 0xFF;
			}else if(fieldToken == 0xda){
				fieldNameSize = buffer.getShort() & 0xFFFF;
			}else{
				throw new GdxRuntimeException("unknow string subtype " + String.format("%x", fieldToken));
			}
			return new JsonValue(parseString(buffer, fieldNameSize));
		}else{
			throw new GdxRuntimeException("unknow value type " + String.format("%x", fieldToken));
		}
	}

}
