package com.example.ethereumserviceapp.repository;

import com.example.ethereumserviceapp.model.entities.SsiApplication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SsiApplicationRepository extends MongoRepository<SsiApplication, String> {

    public Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findBySubmittedMunicipality(String municipality);

    public List<SsiApplication> findByTaxisAfm(String taxisAfm);

    public Optional<SsiApplication> findByUuid(String uuid);

    public List<SsiApplication> findAll();

    @Query(value = "{'uuid' : ?0}", fields = "{credentialIds: 1, _id: 0}")
    public String[] findCredentialIdsByUuid(String uuid);
}
