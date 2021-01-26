/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp;

import com.example.ethereumserviceapp.model.ColletionOfCredentials;
import com.example.ethereumserviceapp.model.entities.SsiApplication;
import com.example.ethereumserviceapp.repository.SsiApplicationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author nikos
 */
@SpringBootTest
public class TestEmbeddedMongo {

    @Autowired
    SsiApplicationRepository repo;

    @Test
    public void someTest(@Autowired MongoTemplate mongoTemplate) {
        SsiApplication ssiapp = new SsiApplication();
        repo.save(ssiapp);
        ssiapp = new SsiApplication();
        repo.save(ssiapp);
//        List<SsiApplication> result = repo.findAll();
//        assertEquals(result.size(), 2);

    }

    @Test
    public void getCredentialIdsFromCase(@Autowired MongoTemplate mongoTemplate) throws JsonProcessingException {
//        SsiApplication ssiapp = new SsiApplication();
//        ssiapp.setUuid("123");
//        ssiapp.setCredentialIds(Arrays.asList(new String[]{"foo", "bar"}));
//        repo.save(ssiapp);
//
//        ssiapp = new SsiApplication();
//        ssiapp.setUuid("456");
//        repo.save(ssiapp);
        List<String> result = Arrays.asList(repo.findCredentialIdsByUuid("F8SO3KXU0SOCYO7Y"));
        ObjectMapper mapper = new ObjectMapper();
//        ColletionOfCredentials creds = mapper.readValue(result.get(0), ColletionOfCredentials.class);
//
//        assertEquals(creds.getCredentialIds().length, 1);

    }

}
