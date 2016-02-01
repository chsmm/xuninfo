package com.xuninfo.proxyCrawler.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
public class KryoRedisSerializer implements RedisSerializer<String>{
	
	private Kryo kryo;
	
	public KryoRedisSerializer() {
		kryo = new Kryo();
		kryo.setRegistrationRequired(true);
	}
	
	public byte[] serialize(final String value) throws SerializationException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		Output output = new Output(out);
		kryo.writeObjectOrNull(output, value, String.class);
		output.close();
		return out.toByteArray();
	}

	public String deserialize(final byte[] bytes) throws SerializationException {
		if(bytes==null)return null;
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		Input input = new Input(in);
		String result =kryo.readObject(input,String.class);
		input.close();
		return result;
	}

}
