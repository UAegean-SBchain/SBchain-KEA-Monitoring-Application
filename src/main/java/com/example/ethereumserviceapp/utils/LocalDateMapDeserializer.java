package com.example.ethereumserviceapp.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;

public class LocalDateMapDeserializer   extends JsonDeserializer<LinkedHashMap<LocalDateTime, String>> {



    @SneakyThrows
    @Override
    public LinkedHashMap<LocalDateTime, String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ObjectCodec oc = jp.getCodec();
        TextNode node = (TextNode) oc.readTree(jp);
        String dateString = node.textValue();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<LinkedHashMap<String, String>> typeRef
                = new TypeReference<LinkedHashMap<String, String>>() {};
        LinkedHashMap<String, String> asString =  mapper.readValue(dateString, typeRef);

        LinkedHashMap<LocalDateTime, String> result = new LinkedHashMap<>();
        Optional<String> dateStringKey =  asString.keySet().stream().findFirst();
        if(dateStringKey.isPresent()){
            LocalDateTime ldt =LocalDateTime.parse(dateStringKey.get(),formatter);
            result.put(ldt, asString.get(dateStringKey.get()));
            return  result;
        }

        throw  new Exception("error marshaling");
//        String itemName = node.get("itemName").asText();
//        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();
//
//        return new Item(id, itemName, new User(userId, null));
    }
}