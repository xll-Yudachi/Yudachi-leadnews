package com.yudachi.common.kafkastream.serializer;

import com.yudachi.common.common.contants.Contants;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {

    Class<T> cla;

    public JsonSerializer(Class<T> cla){
        this.cla = cla;
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, T data) {
        if (data == null)
            return null;
        try {
            return Contants.objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {

    }

}
