//package com.example.ethereumserviceapp;
//
//import com.example.ethereumserviceapp.model.entities.SsiApplication;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class TestMongoWrappers{
//
//
//        // 2021-01-26T12:33:00466{
//        @Test
//        public void testTimeMapping() {
////            String time = "2021-01-26 12:33:00";
//            final LocalDateTime nowLocalTime
//                    = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String lt = nowLocalTime.format(formatter);
//            System.out.println(lt);
////            lt = lt.replace(".", "");
//            LocalDateTime ldt =LocalDateTime.parse(lt,formatter);
//
//
//        }
//        @Test
//        public  void testSSiApplication() throws JsonProcessingException {
//            String json= "{\"_id\":{\"$oid\":\"600ff08d75daa27a321a5868\"},\"uuid\":\"N4RUDGMPVDCYV9WK\"," +
//                    "\"credentialIds\":[{\"_id\":\"R5J93\",\"exp\":\"1618496069\",\"name\":\"Taxis\"},{\"_id\":\"IIHO5\",\"exp\":\"1618650138\",\"name\":\"KEA_PERSONAL_DECLARATION\"},{\"_id\":\"1J9OY2\",\"exp\":\"1622021432\",\"name\":\"Financial Information\"},{\"_id\":\"ZCPCQ\",\"exp\":\"1617181405\",\"name\":\"Electronic_Bill_ID\"},{\"_id\":\"K0DMS\",\"exp\":\"1614511412\",\"name\":\"Contact Details\"},{\"_id\":\"97I81\",\"exp\":\"1618567139\",\"name\":\"CIVIL_ID\"}],\"taxisAfm\":\"068933130\",\"taxisFamilyName\":\"ΒΑΒΟΥΛΑ\",\"taxisFirstName\":\"ΕΥΤΥΧΙΑ\",\"taxisFathersName\":\"ΕΜΜΑΝΟΥΗΛ\",\"taxisMothersName\":\"ΑΝΝΑ\",\"surnameLatin\":\"TRIANTAFYLLOU\",\"nameLatin\":\"NIKOLAOS\",\"fatherNameLatin\":\"ANASTASIOS\",\"motherNameLatin\":\"ANGELIKI\",\"taxisAmka\":\"05108304675\",\"taxisDateOfBirth\":\"1950\",\"nationality\":\"Ελληνική\",\"maritalStatus\":\"married\",\"hospitalized\":\"false\",\"hospitalizedSpecific\":\"false\",\"monk\":\"false\",\"luxury\":\"false\",\"street\":\"Καλλιστρατους\",\"streetNumber\":\"ΧΧ\",\"po\":\"15ΧΧΧ\",\"municipality\":\"Ζωγράφου\",\"prefecture\":\"Attikis\",\"ownership\":\"owned\",\"supplyType\":\"power\",\"meterNumber\":\"1-08047474-03-6\",\"participateFead\":\"true\",\"selectProvider\":\"fead provider\",\"gender\":\"male\",\"employmentStatus\":\"unemployed\",\"unemployed\":\"false\",\"employed\":\"false\",\"oaedId\":\"12345\",\"oaedDate\":\"05-10-20\",\"email\":\"triantafyllou.ni@gmail.com\",\"mobilePhone\":\"00306943808730\",\"landline\":\"2107786359\",\"iban\":\"123456778123\",\"mailAddress\":{\"streetNumber\":\"ΧΧ\",\"street\":\"Καλλιστρατους\",\"municipality\":\"Ζωγράφου\",\"PO\":\"15ΧΧΧ\"},\"parenthood\":\"true\",\"custody\":\"yes\",\"additionalAdults\":\"2\",\"salariesR\":\"580\",\"pensionsR\":\"0\",\"farmingR\":\"0\",\"freelanceR\":\"500\",\"rentIncomeR\":\"0\",\"unemploymentBenefitR\":\"0\",\"otherBenefitsR\":\"100\",\"ekasR\":\"0\",\"otherIncomeR\":\"0\",\"depositInterestA\":\"249\",\"depositsA\":\"0\",\"domesticRealEstateA\":\"0\",\"foreignRealEstateA\":\"0\",\"vehicleValueA\":\"0\",\"investmentsA\":\"0\",\"salariesRHistory\":{\"2021-01-26 12:33:00\":\"580\"},\"pensionsRHistory\":{\"2021-01-26 12:33:00\":\"0\"}," +
//                    "\"freelanceRHistory\":{\"2021-01-26 12:33:00\":\"500\"},\"otherBenefitsRHistory\":{\"2021-01-26 12:33:00\":\"100\"},\"depositsAHistory\":{\"2021-01-26 12:33:00\":\"0\"},\"domesticRealEstateAHistory\":{\"2021-01-26 12:33:00\":\"0\"},\"foreignRealEstateAHistory\":{\"2021-01-26 12:33:00 \":\"0\"},\"householdPrincipal\":{\"afm\":\"068933130\"},\"householdComposition\":[{\"name\":\"ΠΟΛΥΝΙΚΟΣ ΤΣΑΓΓΑΛΗΣ\",\"relationship\":\"Σύζυγος\"}],\"householdCompositionHistory\":{\"2021-01-26 12:33:01\":[{\"name\":\"ΠΟΛΥΝΙΚΟΣ ΤΣΑΓΓΑΛΗΣ\",\"relationship\":\"Σύζυγος\"}]},\"totalIncome\":\"1429\",\"savedInDb\":false,\"status\":\"active\",\"submittedMunicipality\":\"tmpMunicipality\",\"time\":{\"$date\":{\"$numberLong\":\"1611612000000\"}},\"_class\":\"com.example.sbchainssioicdoauth2.model.entity.SsiApplication\"}";
//            ObjectMapper mapper = new ObjectMapper();
//            SsiApplication app = mapper.readValue(json, SsiApplication.class);
//            System.out.println(app.getUuid());
//
//        }
//}
