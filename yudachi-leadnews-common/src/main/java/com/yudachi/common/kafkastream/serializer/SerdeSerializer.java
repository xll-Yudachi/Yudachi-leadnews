package com.yudachi.common.kafkastream.serializer;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdeSerializer<T> {
    JsonSerializer<T> serializer;
    JsonDeserializer<T> deserializer;

    public SerdeSerializer(Class<T> tClass){
        this.serializer = new JsonSerializer<>(tClass);
        this.deserializer = new JsonDeserializer<>(tClass);
    }

    public Serde<T> serdes(){
       return Serdes.serdeFrom(this.serializer, this.deserializer);
    }
}
