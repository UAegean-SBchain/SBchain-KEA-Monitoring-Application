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

    Optional<SsiApplication> findFirstByTaxisAfm(String taxisAfm);

    List<SsiApplication> findBySubmittedMunicipality(String municipality);

    List<SsiApplication> findByTaxisAfm(String taxisAfm);

    List<SsiApplication> findByTaxisAfmIn(Set<String> taxisAfms);

    Optional<SsiApplication> findByUuid(String uuid);

    List<SsiApplication> findAll();

    List<SsiApplication> findByMeterNumber(String meterNumber);

    List<SsiApplication> findByIban(String iban);

    List<SsiApplication> findByHouseholdCompositionIn(Map<String, String> household);

    List<SsiApplication> findByHouseholdComposition(HouseholdMember member);

    List<SsiApplication> findByHouseholdPrincipalIn(List<HouseholdMember> members);

    List<String> findUuidByTaxisAfmIn(Set<String> afms);

    void deleteByUuid(String uuid);

    @Query(value = "{'uuid' : ?0}", fields = "{credentialIds: 1, _id: 0}")
    String[] findCredentialIdsByUuid(String uuid);
}
