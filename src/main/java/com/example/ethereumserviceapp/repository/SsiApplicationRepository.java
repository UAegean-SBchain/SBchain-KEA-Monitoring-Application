package com.example.ethereumserviceapp.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SsiApplicationRepository extends MongoRepository<SsiApplication, String> {

    public Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findBySubmittedMunicipality(String municipality);

    public List<SsiApplication> findByTaxisAfm(String taxisAfm);

    public List<SsiApplication> findByTaxisAfmIn(Set<String> taxisAfms);

    public Optional<SsiApplication> findByUuid(String uuid);

    public List<SsiApplication> findAll();

    public List<SsiApplication> findByMeterNumber(String meterNumber);

    public List<SsiApplication> findByIban(String iban);

    public List<SsiApplication> findByHouseholdCompositionIn(Map<String, String> household);

    public List<SsiApplication> findByHouseholdComposition(HouseholdMember member);

    public List<SsiApplication> findByHouseholdPrincipalIn(List<HouseholdMember> members);

    public List<String> findUuidByTaxisAfmIn(Set<String> afms);

    public void deleteByUuid(String uuid);

    @Query(value = "{'uuid' : ?0}", fields = "{credentialIds: 1, _id: 0}")
    public String[] findCredentialIdsByUuid(String uuid);
}
