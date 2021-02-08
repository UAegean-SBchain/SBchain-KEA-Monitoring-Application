package com.example.ethereumserviceapp.utils;

import java.io.*;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.ethereumserviceapp.model.HouseholdMember;
import com.example.ethereumserviceapp.model.entities.SsiApplication;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.StringUtils;

@Slf4j
public class CsvUtils {
    //    public static String TYPE = "text/csv";
    static String[] HEADERs = {"_id/$oid", "uuid", "ssn", "taxisAfm", "taxisFamilyName",
            "taxisFirstName", "taxisFathersName", "taxisMothersName", "taxisFamilyNameLatin", "taxisFirstNameLatin", "taxisFathersNameLatin", "taxisMothersNameLatin",
            "taxisAmka", "taxisDateOfBirth", "taxisGender", "nationality", "maritalStatus", "hospitalized", "hospitalizedSpecific",
            "monk", "luxury", "street", "streetNumber", "po", "prefecture", "ownership", "supplyType", "meterNumber",
            "participateFead", "selectProvider", "gender", "disabilityStatus", "levelOfEducation", "employmentStatus", "unemployed", "employed",
            "oaedId", "oaedDate", "email", "mobilePhone", "landline", "iban", "streetNumber_1", "prefecture_2", "municipality", "parenthood", "custody",
            "additionalAdults", "salariesR", "pensionsR", "farmingR", "freelanceR", "rentIncomeR", "unemploymentBenefitR", "otherBenefitsR", "ekasR",
            "otherIncomeR", "ergomeR", "depositInterestA", "depositsA", "domesticRealEstateA", "foreignRealEstateA", "vehicleValueA", "investmentsA",
            "totalIncome", "savedInDb", "status", "submittedMunicipality", "time", "householdPrincipal", "householdComposition", "householdCompositionHistory",

            "salariesRHistory", "pensionsRHistory", "farmingRHistory", "freelanceRHistory", "otherBenefitsRHistory", "depositsAHistory", "domesticRealEstateAHistory",
            "foreignRealEstateAHistory", "monthlyGuarantee", "totalIncome_3", "monthlyIncome", "monthlyAid", "savedInDb_4", "status_5"};


    private final static List<String> GREEK_FIRST_NAMES_MALE = new ArrayList<>();
    private final static List<String> GREEK_FIRST_NAMES_FEMALE = new ArrayList<>();
    private final static List<String> GREEK_LAST_NAMES = new ArrayList<>();
    private final static DateTimeFormatter ddMMYYYFormater_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final static DateTimeFormatter yyyyMMddFormater_DASH = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//    public static boolean hasCSVFormat(MultipartFile file) {
//        final boolean equals = TYPE.equals(file.getContentType());
//        return equals;
//    }

    // transforms csv to JPA entity
    public static List<SsiApplication> csvToSsiApplication(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<SsiApplication> ssiAppList = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (CSVRecord csvRecord : csvRecords) {
                SsiApplication ssiApp = new SsiApplication();
                ssiApp.setId(csvRecord.get("_id/$oid"));
                ssiApp.setUuid(csvRecord.get("uuid"));
                //ssiApp.setCredentialIds(transformCrendentialIds(csvRecord.get("credentialIds")));
                ssiApp.setSsn(csvRecord.get("ssn"));
                ssiApp.setTaxisAfm(csvRecord.get("taxisAfm"));
                ssiApp.setTaxisFamilyName(csvRecord.get("taxisFamilyName"));
                ssiApp.setTaxisFirstName(csvRecord.get("taxisFirstName"));
                ssiApp.setTaxisFathersName(csvRecord.get("taxisFathersName"));
                ssiApp.setTaxisMothersName(csvRecord.get("taxisMothersName"));
                ssiApp.setTaxisFamilyNameLatin(csvRecord.get("taxisFamilyNameLatin"));
                ssiApp.setTaxisFirstNameLatin(csvRecord.get("taxisFirstNameLatin"));
                ssiApp.setTaxisFathersNameLatin(csvRecord.get("taxisFathersNameLatin"));
                ssiApp.setTaxisMothersNameLatin(csvRecord.get("taxisMothersNameLatin"));
                ssiApp.setTaxisAmka(csvRecord.get("taxisAmka"));
                ssiApp.setTaxisDateOfBirth(csvRecord.get("taxisDateOfBirth"));
                ssiApp.setTaxisGender(csvRecord.get("taxisGender"));
                ssiApp.setNationality(csvRecord.get("nationality"));
                ssiApp.setMaritalStatus(csvRecord.get("maritalStatus"));
                ssiApp.setHospitalized(csvRecord.get("hospitalized"));
                ssiApp.setHospitalizedSpecific(csvRecord.get("hospitalizedSpecific"));
                ssiApp.setMonk(csvRecord.get("monk"));
                ssiApp.setLuxury(csvRecord.get("luxury"));
                ssiApp.setStreet(csvRecord.get("street"));
                ssiApp.setStreetNumber(csvRecord.get("streetNumber"));
                ssiApp.setPo(csvRecord.get("po"));
                ssiApp.setMunicipality(csvRecord.get("municipality"));
                ssiApp.setPrefecture(csvRecord.get("prefecture"));
                ssiApp.setOwnership(csvRecord.get("ownership"));
                ssiApp.setSupplyType(csvRecord.get("supplyType"));
                ssiApp.setMeterNumber(csvRecord.get("meterNumber"));
                ssiApp.setParticipateFead(csvRecord.get("participateFead"));
                ssiApp.setSelectProvider(csvRecord.get("selectProvider"));
                ssiApp.setGender(csvRecord.get("gender"));
                ssiApp.setDisabilityStatus(csvRecord.get("disabilityStatus"));
                ssiApp.setLevelOfEducation(csvRecord.get("levelOfEducation"));
                ssiApp.setEmploymentStatus(csvRecord.get("employmentStatus"));
                ssiApp.setUnemployed(csvRecord.get("unemployed"));
                ssiApp.setEmployed(csvRecord.get("employed"));
                ssiApp.setOaedId(csvRecord.get("oaedId"));
                ssiApp.setOaedDate(csvRecord.get("oaedDate"));
                ssiApp.setEmail(csvRecord.get("email"));
                ssiApp.setMobilePhone(csvRecord.get("mobilePhone"));
                ssiApp.setLandline(csvRecord.get("landline"));
                ssiApp.setIban(csvRecord.get("iban"));
                //ssiApp.setMailAddress(transformMailAddress(csvRecord.get("mailAddress")));
                ssiApp.setParenthood(csvRecord.get("parenthood"));
                ssiApp.setCustody(csvRecord.get("custody"));
                ssiApp.setAdditionalAdults(csvRecord.get("additionalAdults"));
                ssiApp.setSalariesR(csvRecord.get("salariesR"));
                ssiApp.setPensionsR(csvRecord.get("pensionsR"));
                ssiApp.setFarmingR(csvRecord.get("farmingR"));
                ssiApp.setFreelanceR(csvRecord.get("freelanceR"));
                ssiApp.setRentIncomeR(csvRecord.get("rentIncomeR"));
                ssiApp.setUnemploymentBenefitR(csvRecord.get("unemploymentBenefitR"));
                ssiApp.setOtherBenefitsR(csvRecord.get("otherBenefitsR"));
                ssiApp.setEkasR(csvRecord.get("ekasR"));
                ssiApp.setOtherIncomeR(csvRecord.get("otherIncomeR"));
                ssiApp.setErgomeR(csvRecord.get("ergomeR"));
                ssiApp.setDepositInterestA(csvRecord.get("depositInterestA"));
                ssiApp.setDepositsA(csvRecord.get("depositsA"));
                ssiApp.setDomesticRealEstateA(csvRecord.get("domesticRealEstateA"));
                ssiApp.setForeignRealEstateA(csvRecord.get("foreignRealEstateA"));
                ssiApp.setVehicleValueA(csvRecord.get("vehicleValueA"));
                ssiApp.setInvestmentsA(csvRecord.get("investmentsA"));
                ssiApp.setSalariesRHistory(transformHistoryField(csvRecord.get("salariesRHistory")));
                ssiApp.setPensionsRHistory(transformHistoryField(csvRecord.get("pensionsRHistory")));
                ssiApp.setFarmingRHistory(transformHistoryField(csvRecord.get("farmingRHistory")));
                ssiApp.setFreelanceRHistory(transformHistoryField(csvRecord.get("freelanceRHistory")));
                ssiApp.setOtherBenefitsRHistory(transformHistoryField(csvRecord.get("otherBenefitsRHistory")));
                ssiApp.setDepositsAHistory(transformHistoryField(csvRecord.get("depositsAHistory")));
                ssiApp.setDomesticRealEstateAHistory(transformHistoryField(csvRecord.get("domesticRealEstateAHistory")));
                ssiApp.setForeignRealEstateAHistory(transformHistoryField(csvRecord.get("foreignRealEstateAHistory")));
                ssiApp.setHouseholdPrincipal(transformHousehold(csvRecord.get("householdPrincipal")).get(0));
                ssiApp.setHouseholdComposition(transformHousehold(csvRecord.get("householdComposition")));
                ssiApp.setHouseholdCompositionHistory(transformHhHistory(csvRecord.get("householdCompositionHistory")));
                ssiApp.setMonthlyGuarantee(csvRecord.get("monthlyGuarantee"));
                ssiApp.setTotalIncome(csvRecord.get("totalIncome"));
                ssiApp.setMonthlyIncome(csvRecord.get("monthlyIncome"));
                ssiApp.setMonthlyAid(csvRecord.get("monthlyAid"));
                ssiApp.setSavedInDb(Boolean.parseBoolean(csvRecord.get("savedInDb")));
                ssiApp.setStatus(csvRecord.get("status"));
                ssiApp.setSubmittedMunicipality(csvRecord.get("submittedMunicipality"));
                ssiApp.setTime(LocalDate.parse(csvRecord.get("time"), formatter));

                ssiAppList.add(ssiApp);
            }
            return ssiAppList;
        } catch (IOException e) {
            log.error("error on submit csv :{}", e.getMessage());
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private static List<HouseholdMember> transformHousehold(String householdComp) {

        List<HouseholdMember> householdComposition = new ArrayList<>();
        String[] hhComp = householdComp.split("\\|");
        for (String s : hhComp) {
            String[] hhEntry = s.split(";");
            HouseholdMember hhMember = new HouseholdMember();
            hhMember.setName(hhEntry[0]);
            hhMember.setSurname(hhEntry[1]);
            hhMember.setAfm(hhEntry[2]);
            hhMember.setDateOfBirth(hhEntry[3]);

            householdComposition.add(hhMember);
        }
        return householdComposition;
    }

    private static LinkedHashMap<String, List<HouseholdMember>> transformHhHistory(String history) {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LinkedHashMap<String, List<HouseholdMember>> hhHistory = new LinkedHashMap<>();
        try {
            String[] hhs = history.split("-");
            for (String s : hhs) {
                String[] hh = s.split("_");
                List<HouseholdMember> householdEntry = transformHousehold(hh[1]);
//                hhHistory.put(LocalDateTime.parse(hh[0], formatter), householdEntry);
                hhHistory.put(hh[0], householdEntry);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e.getMessage());
        }

        return hhHistory;
    }

    private static LinkedHashMap<String, String> transformHistoryField(String history) {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LinkedHashMap<String, String> histField = new LinkedHashMap<>();
        if (!"".equals(history)) {
            String[] histArray = history.split("\\|");
            for (String s : histArray) {
                String[] historyEntry = s.split("_");
                histField.put(historyEntry[0], historyEntry[1]);
            }
        }
        return histField;
    }



    public static List<SsiApplication> generateMockData(int size) {
        List<SsiApplication> finalHouseholdApp = generateMockHouseholdApplications();
        while (finalHouseholdApp.size() < size) {
            List<SsiApplication> additionalHousholdApp = generateMockHouseholdApplications();
            finalHouseholdApp = Stream.concat(finalHouseholdApp.stream(), additionalHousholdApp.stream()).collect(Collectors.toList());
        }
        return finalHouseholdApp;
    }


    public static List<SsiApplication> generateMockHouseholdApplications() {

        List<SsiApplication> householdAppList = new ArrayList<>();
        List<HouseholdMember> householdMemberList = new ArrayList<>();

        int householdSize = new Random().nextInt(5);
        //make principal
        //ie : Jimmy;Page;68933130;05/04/1953
        SsiApplication principalApp = makeRandomApplicant();

        HouseholdMember principalMember = new HouseholdMember();
        principalMember.setAfm(principalApp.getTaxisAfm());
        principalMember.setName(principalApp.getTaxisFirstName());
        principalMember.setSurname(principalApp.getTaxisFamilyName());
        principalMember.setDateOfBirth(getAdultDateOfBirth());

        principalApp.setTaxisDateOfBirth(principalMember.getDateOfBirth());
        principalApp.setHouseholdPrincipal(principalMember);

        principalApp.setTime(LocalDate.now());
        householdAppList.add(principalApp);
        householdMemberList.add(principalMember);

        //generate additional members (household size -1)
        for (int i = 0; i < householdSize - 1; i++) {

            SsiApplication memberApp = makeRandomApplicant();
            memberApp.setTime(LocalDate.now());
            memberApp.setHouseholdPrincipal(principalMember);
            // initially everyone is an adult, minors are added later based on percentages
            memberApp.setTaxisDateOfBirth(getAdultDateOfBirth());
            HouseholdMember member = new HouseholdMember();
            member.setName(memberApp.getTaxisFirstName());
            member.setSurname(memberApp.getTaxisFamilyName());
            member.setAfm(memberApp.getTaxisAfm());
            member.setDateOfBirth(memberApp.getTaxisDateOfBirth());
            householdAppList.add(memberApp);
            householdMemberList.add(member);
        }

        // generate houshold entries for principal and additional memebrs


        LocalDate now = LocalDate.now();
        String history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,List<HouseholdMember>> compositionHistory = new LinkedHashMap();
        compositionHistory.put(history_time,householdMemberList);
        principalApp.setHouseholdComposition(householdMemberList);
        principalApp.setHouseholdCompositionHistory(compositionHistory);

        householdAppList.forEach(memberApp -> {
            memberApp.setHouseholdComposition(householdMemberList);
            memberApp.setHouseholdCompositionHistory(compositionHistory);
        });

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM/yyyy");
        long additionalAdults = principalApp.getHouseholdComposition().stream().filter(member -> {
            LocalDate dateTime = LocalDate.parse(member.getDateOfBirth(), yyyyMMddFormater_DASH);
            return ChronoUnit.YEARS.between(dateTime, now) >= 18;
        }).count();
        principalApp.setAdditionalAdults(String.valueOf(additionalAdults));

//        System.out.println(householdAppList.toString());

        int maxAmount = (200 + (int) additionalAdults * 100 + (principalApp.getHouseholdComposition().size() - (int) additionalAdults) * 50) * 6;
        int leftOverAmount = addFinancialDataToSsiApp(principalApp, maxAmount);
        for (SsiApplication app : householdAppList) {// for all non principal applications
            if (!app.getHouseholdPrincipal().getAfm().equals(app.getTaxisAfm()) && isAdult(app.getTaxisDateOfBirth())) {
                leftOverAmount = addFinancialDataToSsiApp(app, leftOverAmount);
            }
        }
        return householdAppList;
    }


    public static SsiApplication makeRandomApplicant() {
        SsiApplication randomSsiApp = new SsiApplication();
        randomSsiApp.setId(String.format("%09d", new Random().nextInt(10000)));
        randomSsiApp.setUuid(RandomIdGenerator.GetBase36(16));
        //ssiApp.setCredentialIds(transformCrendentialIds(csvRecord.get("credentialIds")));
        randomSsiApp.setSsn(String.format("%09d", new Random().nextInt(10000)));
        randomSsiApp.setTaxisAfm(getRandomNumberByLength(9));
        String[] surname = getRandomSurName();
        String[] name = new String[0];
        try {
            name = getRandomFirstName();
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
        }
        randomSsiApp.setTaxisFamilyName(surname[1]);
        randomSsiApp.setTaxisFirstName(name[1]);

        String[] fatherName = new String[0];
        try {
            fatherName = getRandomFirstName();
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
        }
        String[] motherName = new String[0];
        try {
            motherName = getRandomFirstNameFemale();
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
        }
        randomSsiApp.setTaxisFathersName(fatherName[1]);
        randomSsiApp.setTaxisMothersName(motherName[1]);
        //memberApp.setHouseholdPrincipal(principalMember);
//        if (isMinor()) {
//            randomSsiApp.setTaxisDateOfBirth(getMinorDateOfBirth());
//        } else {
        randomSsiApp.setTaxisDateOfBirth(getAdultDateOfBirth());
//        }
        // taxisFamilyNameLatin
        randomSsiApp.setTaxisFamilyNameLatin(translitarate(surname[0]));
        //taxisFirstNameLatin
        randomSsiApp.setTaxisFirstNameLatin(translitarate(name[0]));
        //taxisFathersNameLatin
        randomSsiApp.setTaxisFathersNameLatin(translitarate(fatherName[0]));
        //taxisMothersNameLatin
        randomSsiApp.setTaxisMothersNameLatin(translitarate(motherName[0]));
        //taxisAmka
        randomSsiApp.setTaxisAmka(getRandomNumberByLength(11));
        //taxisGender
        randomSsiApp.setTaxisGender(getGender());
        randomSsiApp.setGender(randomSsiApp.getTaxisGender());
        //nationality
        randomSsiApp.setNationality(getNationality());
        //maritalStatus
        randomSsiApp.setMaritalStatus(getIsMarried());
        //hospitalized
        randomSsiApp.setHospitalized("FALSE");
        //hospitalizedSpecific
        randomSsiApp.setHospitalizedSpecific("FALSE");
        //monk
        randomSsiApp.setMonk("FALSE");
        //luxury
        randomSsiApp.setLuxury("FALSE");
        //street
        randomSsiApp.setStreet(getRandomStreet());
        //streetNumber
        randomSsiApp.setStreetNumber(getRandomNumberByLength(2));
        //po
        randomSsiApp.setPo("19002");
        //prefecture
        randomSsiApp.setPrefecture("Anatoliki Attiki");
        //ownership
        randomSsiApp.setOwnership(getRandomOwnership());
        //supplyType
        randomSsiApp.setSupplyType(getRandomSupplyType());
        //meterNumber
        randomSsiApp.setMeterNumber(getRandomNumberByLength(10));
        //participateFead
        randomSsiApp.setParticipateFead(booleanString());
        //selectProvider
        randomSsiApp.setSelectProvider("Fead provider 1");
        //gender
        randomSsiApp.setGender(randomSsiApp.getTaxisGender());
        //disabilityStatus
        randomSsiApp.setDisabilityStatus("none");
        //levelOfEducation
        randomSsiApp.setLevelOfEducation("none");
        //initially all are employed with a percentage becoming undemployed later
        //employmentStatus
        randomSsiApp.setEmploymentStatus("employed");
        //unemployed
        randomSsiApp.setUnemployed("FALSE");
        //employed
        randomSsiApp.setEmployed("TRUE");
        //oaedId
        randomSsiApp.setOaedId("");
        //oaedDate
        randomSsiApp.setOaedDate("");
        //email
        randomSsiApp.setEmail("xxx@gmail.com");
        //mobilePhone
        randomSsiApp.setMobilePhone("003069XXXXXXX");
        //landline
        randomSsiApp.setLandline("0030210XXXXXXX");
        //iban
        randomSsiApp.setIban("GR" + getRandomNumberByLength(25));
        //streetNumber_1
        randomSsiApp.setStreetNumber1(randomSsiApp.getStreetNumber());
        randomSsiApp.setPrefecture2(randomSsiApp.getPrefecture());
        randomSsiApp.setMunicipality("Paiania");
        //parenthood
        // intially these are fase and added later as percentages
        randomSsiApp.setParenthood("FALSE");
        //custody
        randomSsiApp.setCustody("FALSE");
        //
        randomSsiApp.setAdditionalAdults("0");
        return randomSsiApp;
    }


    public static String getRandomNumberByLength(int length) {
        //05108304677
        return RandomStringUtils.random(length, false, true);
        //return String.format(format, RandomStringUtils.random(length, false, true));

    }


//    public static String makeHouseHoldString(List<SsiApplication> householdAppList) {
//        StringJoiner strJoiner = new StringJoiner(
//                "|", "", "");
//        householdAppList.stream().map(app -> {
//            return makeMemberToHouseholdString(app);
//        }).forEach(memberStr -> {
//            strJoiner.add(memberStr);
//        });
//        return strJoiner.toString();
//    }

    public static String makeHouseHoldString(List<HouseholdMember> householdAppList) {
        StringJoiner strJoiner = new StringJoiner(
                "|", "", "");
        householdAppList.stream().map(CsvUtils::makeMemberToHouseholdString).forEach(strJoiner::add);
        return strJoiner.toString();
    }

//    public static String makeMemberToHouseholdString(SsiApplication member) {
//        //for 2 members: name;surname;afm;date|name;surname;afm;date
//        StringBuilder sb = new StringBuilder();
//        sb.append(member.getTaxisFirstName()).append(";")
//                .append(member.getTaxisFamilyName()).append(";")
//                .append(member.getTaxisAfm()).append(";")
//                .append(StringUtils.isEmpty(member.getTaxisDateOfBirth()) ? "" : member.getTaxisDateOfBirth()).append(";")
//        ;
//        return sb.toString();
//    }

    public static String makeMemberToHouseholdString(HouseholdMember member) {
        //for 2 members: name;surname;afm;date|name;surname;afm;date
        return member.getName() + ";" +
                member.getSurname() + ";" +
                member.getAfm() + ";" +
                (StringUtils.isEmpty(member.getDateOfBirth()) ? "" : member.getDateOfBirth()) + ";";
    }


    /**
     * @return string[] with two elements first is the Latin name and the second is the Greekname
     */
    public static String[] getRandomFirstName() throws FileNotFoundException {
//        String[] names = {"John", "Jack", "Joey"};
//        String[] namesGR = {"Γιάννης", "Ιάκωβος", "Χάρης"};

        if (GREEK_FIRST_NAMES_MALE.size() == 0) {
//            ClassLoader classLoader = CsvUtils.class.getClassLoader();
            InputStream inputStream = new FileInputStream("male_names_gr.txt");// classLoader.getResourceAsStream("male_names_gr.txt");
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    GREEK_FIRST_NAMES_MALE.add(line);
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        int index = new Random().nextInt(GREEK_FIRST_NAMES_MALE.size() - 1);
        return new String[]{GREEK_FIRST_NAMES_MALE.get(index), GREEK_FIRST_NAMES_MALE.get(index)};
    }


    public static String[] getRandomFirstNameFemale() throws FileNotFoundException {
        if (GREEK_FIRST_NAMES_FEMALE.size() == 0) {
            InputStream inputStream = new FileInputStream("female_names_gr.txt");// classLoader.getResourceAsStream("male_names_gr.txt");
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    GREEK_FIRST_NAMES_FEMALE.add(line);
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        int index = new Random().nextInt(GREEK_FIRST_NAMES_FEMALE.size() - 1);
        return new String[]{GREEK_FIRST_NAMES_FEMALE.get(index), GREEK_FIRST_NAMES_FEMALE.get(index)};
    }


    /**
     * @return string[] with two elements first is the Latin name and the second is the Greekname
     */
    public static String[] getRandomSurName() {
        if (GREEK_LAST_NAMES.size() == 0) {
//            ClassLoader classLoader = CsvUtils.class.getClassLoader();
            InputStream inputStream = null;// classLoader.getResourceAsStream("male_names_gr.txt");
            try {
                inputStream = new FileInputStream("surnames_gr.txt");
            } catch (FileNotFoundException e) {
                log.info(e.getMessage());
            }
            try {
                assert inputStream != null;
                try (BufferedReader br
                             = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        GREEK_LAST_NAMES.add(line);
                    }
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        int index = new Random().nextInt(GREEK_LAST_NAMES.size() - 1);
        return new String[]{GREEK_LAST_NAMES.get(index), GREEK_LAST_NAMES.get(index)};
    }

    public static String getAdultDateOfBirth() {
        Random random = new Random();
        String month = String.format("%02d", random.nextInt(12 - 1) + 1);
        String day = String.format("%02d", random.nextInt(30 - 1) + 1);
        String year = String.format("%04d", random.nextInt(2002 - 1920) + 1920);
        return year + "-" + month + "-" + day;
    }

    public static String getMinorDateOfBirth() {
        Random random = new Random();
        String month = String.format("%02d", random.nextInt(12 - 1) + 1);
        String day = String.format("%02d", random.nextInt(30 - 1) + 1);
        String year = String.format("%04d", random.nextInt(2021 - 2004) + 2004);
        return year + "-" + month + "-" + day;
    }


    public static String getGender() {
        Random random = new Random();
        return random.nextInt(2) == 0 ? "male" : "female";
    }

    public static String getNationality() {
        Random random = new Random();
        return random.nextInt(2) == 0 ? "Ελληνική" : "Άλλο";
    }

    public static String getIsMarried() {
        Random random = new Random();
        return random.nextInt(2) == 0 ? "married" : "notmarried";
    }

//    public static String getEmploymentStatus() {
//        Random random = new Random();
//        return random.nextInt(2) == 0 ? "employed" : "unemployed";
//    }

    public static String booleanString() {
        Random random = new Random();
        return random.nextInt(2) == 0 ? "TRUE" : "FALSE";
    }

    public static String getRandomStreet() {
        String[] names = {"Οδός1", "Οδός2", "Οδός3"};
        int index = new Random().nextInt(names.length - 1);
        return names[index];
    }


    public static String getRandomOwnership() {
        String[] names = {"own", "rent", "free", "guest", "homeless"};
        int index = new Random().nextInt(names.length - 1);
        return names[index];
    }

    public static String getRandomSupplyType() {
        String[] names = {"power", "οικισμός", "camping"};
        int index = new Random().nextInt(names.length - 1);
        return names[index];
    }


    public static String getRandomDateIn2020() {
        Random random = new Random();
        String year = "2020";
        String month = String.format("%02d", random.nextInt(12 - 1) + 1);
        String day = String.format("%02d", random.nextInt(30 - 1) + 1);
        StringJoiner strJoiner = new StringJoiner(
                "-", "", "");
        return strJoiner.add(day).add(month).add(year).toString();
    }


    public static boolean isAdult(String dateOfBirth) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        LocalDate dateTime = LocalDate.parse(dateOfBirth, yyyyMMddFormater_DASH);
        return ChronoUnit.YEARS.between(dateTime, now) >= 18;
    }

    public static int addFinancialDataToSsiApp(SsiApplication application, int maxAmount) {
        Random random = new Random();
        int nextAmount = random.nextInt(maxAmount);
        application.setSalariesR(String.valueOf(nextAmount));
        LocalDate now = LocalDate.now();
        String history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> salHistory = new LinkedHashMap<>();
        salHistory.put(history_time,String.valueOf(nextAmount));
        application.setSalariesRHistory(salHistory);



        maxAmount = maxAmount - nextAmount * 0.8 <= 0 ? 0 : maxAmount - (int) (nextAmount * 0.8);
        nextAmount = random.nextInt(maxAmount);
        application.setPensionsR("0");
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> penHistory = new LinkedHashMap<>();
        penHistory.put(history_time,"0");
        application.setPensionsRHistory(penHistory);


        application.setFarmingR(String.valueOf(0));
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> farmHistory = new LinkedHashMap<>();
        farmHistory.put(history_time,"0");
        application.setFarmingRHistory(farmHistory);
        //--
        application.setFreelanceR(String.valueOf(nextAmount));
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> freelanceHistory = new LinkedHashMap<>();
        freelanceHistory.put(history_time,String.valueOf(nextAmount));
        application.setFreelanceRHistory(freelanceHistory);
        //--
        maxAmount = Math.max(maxAmount - nextAmount, 0);
        nextAmount = random.nextInt(maxAmount);


        //rentIncomeR
        application.setRentIncomeR(String.valueOf(0));
        application.setUnemploymentBenefitR(String.valueOf(0));
        application.setOtherBenefitsR(String.valueOf(0));
        //
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> otherBenHistory = new LinkedHashMap<>();
        otherBenHistory.put(history_time,String.valueOf(nextAmount));
        application.setOtherBenefitsRHistory(otherBenHistory);
        //
        application.setEkasR(String.valueOf(0));
        application.setOtherIncomeR(String.valueOf(nextAmount));
        maxAmount = Math.max(maxAmount - nextAmount, 0);
        nextAmount = random.nextInt(maxAmount);
        application.setErgomeR(String.valueOf(0));
        application.setDepositInterestA(String.valueOf(0));
        //depositsA
        application.setDepositsA(String.valueOf(nextAmount));
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> depHistory = new LinkedHashMap<>();
        depHistory.put(history_time,String.valueOf(nextAmount));
        application.setDepositsAHistory(depHistory);
        //
        application.setDomesticRealEstateA(String.valueOf(0));
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> domRealHistory = new LinkedHashMap<>();
        domRealHistory.put(history_time,"0");
        application.setDomesticRealEstateAHistory(domRealHistory);
        //
        application.setForeignRealEstateA(String.valueOf(0));
        history_time =now.format(ddMMYYYFormater_SLASH);
        LinkedHashMap<String,String> forRealHistory = new LinkedHashMap<>();
        forRealHistory.put(history_time,"0");
        application.setForeignRealEstateAHistory(forRealHistory);
        //
        application.setVehicleValueA(String.valueOf(0));
        application.setInvestmentsA(String.valueOf(0));
        //totalIncome
        double total = Double.parseDouble(application.getEkasR() == null ? "0.0" : application.getEkasR()) +
                Double.parseDouble(application.getDepositsA() == null ? "0.0" : application.getDepositsA()) +
                Double.parseDouble(application.getDepositInterestA() == null ? "0.0" : application.getDepositInterestA()) +
                Double.parseDouble(application.getDomesticRealEstateA() == null ? "0.0" : application.getDomesticRealEstateA()) +
                Double.parseDouble(application.getErgomeR() == null ? "0.0" : application.getErgomeR()) +
                Double.parseDouble(application.getFarmingR() == null ? "0.0" : application.getFarmingR()) +
                Double.parseDouble(application.getForeignRealEstateA() == null ? "0.0" : application.getForeignRealEstateA()) +
                Double.parseDouble(application.getFreelanceR() == null ? "0.0" : application.getFreelanceR()) +
                Double.parseDouble(application.getFreelanceR() == null ? "0.0" : application.getFreelanceR()) +
                Double.parseDouble(application.getInvestmentsA() == null ? "0.0" : application.getInvestmentsA()) +
                Double.parseDouble(application.getOtherBenefitsR() == null ? "0.0" : application.getOtherBenefitsR()) +
                Double.parseDouble(application.getOtherIncomeR() == null ? "0.0" : application.getOtherIncomeR()) +
                Double.parseDouble(application.getRentIncomeR() == null ? "0.0" : application.getRentIncomeR()) +
                Double.parseDouble(application.getSalariesR() == null ? "0.0" : application.getSalariesR());

        application.setTotalIncome(Double.toString(total));
        return maxAmount;
    }

    public static List<SsiApplication> makePercentages(List<SsiApplication> appList) throws FileNotFoundException {
        int sampleSize = appList.size();
        int euCitizens = (sampleSize * 5) / 100;
        for (int i = 0; i < euCitizens; i++) {
            appList.get(i).setNationality("EU Citizen");
        }
        int womenSize = (sampleSize) / 2;
        for (int i = 0; i < womenSize; i++) {
            appList.get(i).setTaxisGender("female");
            appList.get(i).setGender("female");
            appList.get(i).setTaxisFirstName(getRandomFirstNameFemale()[0]);
        }
        int marriedSize = (sampleSize * 70) / 100;
        for (int i = 0; i < marriedSize; i++) {
            appList.get(i).setMaritalStatus("married");
        }
        int childrenPercent = (sampleSize * 25) / 100;
        int unemployedPercent = (sampleSize) / 2;
        int parenthoodPercent = (sampleSize * 18) / 100;
        //only non principal applications can be children
        int childrenAdded = 0;
        int unemployedAdded = 0;
        int parenthoodAdded = 0;
        for (SsiApplication application : appList) {
            if (childrenAdded < childrenPercent) {
                if (!application.getHouseholdPrincipal().getAfm().equals(application.getTaxisAfm())) {
                    application.setTaxisDateOfBirth(getMinorDateOfBirth());
                    application.setMaritalStatus("nonMarried");
                    application.setEmployed("FALSE");
                    application.setUnemployed("TRUE");
                    application.setEmploymentStatus("unemployed");
                    application.setParenthood("FALSE");
                    application.setCustody("FALSE");
                    application.setOaedDate("");
                    application.setOaedId("");
                    childrenAdded++;
                }
            }
            if (isAdult(application.getTaxisDateOfBirth())) {
                if (unemployedAdded < unemployedPercent) {
                    application.setUnemployed("TRUE");
                    application.setEmployed("FALSE");
                    application.setEmploymentStatus("unemployed");
                    application.setOaedId(getRandomNumberByLength(10));
                    application.setOaedDate(getRandomDateIn2020());
                    unemployedAdded++;
                }
                if (parenthoodAdded < parenthoodPercent) {
                    parenthoodAdded++;
                    application.setParenthood("TRUE");
                    application.setCustody("TRUE");
                }
            }
        }

        return appList;
    }


    public static void writeToCSV(List<SsiApplication> appList) throws FileNotFoundException {
        appList = makePercentages(appList);
        final String CSV_SEPARATOR = ",";
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("applications.csv"), StandardCharsets.UTF_8));
            StringBuffer oneLine = new StringBuffer();
            for (String header : HEADERs) {
                oneLine.append(header).append(CSV_SEPARATOR);
            }
            bw.write(oneLine.toString());
            bw.newLine();
            for (SsiApplication app : appList) {
                LocalDate now = LocalDate.now();
                String history_time =now.format(ddMMYYYFormater_SLASH);

                oneLine = new StringBuffer();
                oneLine.append(UUID.randomUUID().toString()).append(CSV_SEPARATOR);

                oneLine.append(app.getUuid()).append(CSV_SEPARATOR);
                //ssn
                oneLine.append(CSV_SEPARATOR);
                //uuid
                //taxisAfm
                oneLine.append(app.getTaxisAfm()).append(CSV_SEPARATOR);
                //taxisFamilyName
                oneLine.append(app.getTaxisFamilyName()).append(CSV_SEPARATOR);
                //taxisFirstName
                oneLine.append(app.getTaxisFirstName()).append(CSV_SEPARATOR);
                //taxisFathersName
                oneLine.append(app.getTaxisFathersName()).append(CSV_SEPARATOR);
                //taxisMothersName
                oneLine.append(app.getTaxisMothersName()).append(CSV_SEPARATOR);
                //taxisFamilyNameLatin
                oneLine.append(app.getTaxisFamilyNameLatin()).append(CSV_SEPARATOR);
                //taxisFirstNameLatin
                oneLine.append(app.getTaxisFirstNameLatin()).append(CSV_SEPARATOR);
                //taxisFathersNameLatin
                oneLine.append(app.getTaxisFathersNameLatin()).append(CSV_SEPARATOR);
                oneLine.append(app.getTaxisMothersNameLatin()).append(CSV_SEPARATOR);
                //taxisMothersNameLatin
                oneLine.append(app.getTaxisAmka()).append(CSV_SEPARATOR);
                //taxisAmka
                oneLine.append(app.getTaxisDateOfBirth()).append(CSV_SEPARATOR);
                //taxisDateOfBirth
                oneLine.append(app.getTaxisGender()).append(CSV_SEPARATOR);
                //taxisGender
                oneLine.append(app.getNationality()).append(CSV_SEPARATOR);
                //nationality
                oneLine.append(app.getMaritalStatus()).append(CSV_SEPARATOR);
                //maritalStatus
                oneLine.append(app.getHospitalized()).append(CSV_SEPARATOR);
                //hospitalized
                oneLine.append(app.getHospitalizedSpecific()).append(CSV_SEPARATOR);
                //hospitalizedSpecific
                oneLine.append(app.getMonk()).append(CSV_SEPARATOR);
                //monk
                oneLine.append(app.getLuxury()).append(CSV_SEPARATOR);
                //luxury
                oneLine.append(app.getStreet()).append(CSV_SEPARATOR);
                //street
                oneLine.append(app.getStreetNumber()).append(CSV_SEPARATOR);
                //streetNumber
                oneLine.append(app.getPo()).append(CSV_SEPARATOR);
                //po
                oneLine.append(app.getPrefecture()).append(CSV_SEPARATOR);
                //prefecture
                oneLine.append(app.getOwnership()).append(CSV_SEPARATOR);
                //ownership
                oneLine.append(app.getSupplyType()).append(CSV_SEPARATOR);
                //supplyType
                oneLine.append(app.getMeterNumber()).append(CSV_SEPARATOR);
                //meterNumber
                oneLine.append(app.getParticipateFead()).append(CSV_SEPARATOR);
                //participateFead
                oneLine.append(app.getSelectProvider()).append(CSV_SEPARATOR);
                //selectProvider
                oneLine.append(app.getGender()).append(CSV_SEPARATOR);
                //gender
                oneLine.append(app.getDisabilityStatus()).append(CSV_SEPARATOR);
                //disabilityStatus
                oneLine.append(app.getLevelOfEducation()).append(CSV_SEPARATOR);
                //levelOfEducation
                oneLine.append(app.getEmploymentStatus()).append(CSV_SEPARATOR);
                //employmentStatus
                oneLine.append(app.getUnemployed()).append(CSV_SEPARATOR);
                //unemployed
                oneLine.append(app.getEmployed()).append(CSV_SEPARATOR);
                //employed
                oneLine.append(app.getOaedId()).append(CSV_SEPARATOR);
                //oaedId
                oneLine.append(app.getOaedDate()).append(CSV_SEPARATOR);
                //oaedDate
                oneLine.append(app.getEmail()).append(CSV_SEPARATOR);
                //email
                oneLine.append(app.getMobilePhone()).append(CSV_SEPARATOR);
                //mobilePhone
                oneLine.append(app.getLandline()).append(CSV_SEPARATOR);
                //landline
                oneLine.append(app.getIban()).append(CSV_SEPARATOR);
                //iban
                oneLine.append(app.getStreetNumber1()).append(CSV_SEPARATOR);
                //streetNumber_1
                oneLine.append(app.getPrefecture2()).append(CSV_SEPARATOR);
                //prefecture_2
                oneLine.append(app.getMunicipality()).append(CSV_SEPARATOR);
                //municipality
                oneLine.append(app.getParenthood()).append(CSV_SEPARATOR);
                //parenthood
                oneLine.append(app.getCustody()).append(CSV_SEPARATOR);
                //custody
                oneLine.append(app.getAdditionalAdults()).append(CSV_SEPARATOR);
                //additionalAdults
                oneLine.append(app.getSalariesR()).append(CSV_SEPARATOR);
                //salariesR
                oneLine.append(app.getPensionsR()).append(CSV_SEPARATOR);
                //pensionsR
                oneLine.append(app.getFarmingR()).append(CSV_SEPARATOR);
                //farmingR
                oneLine.append(app.getFreelanceR()).append(CSV_SEPARATOR);
                //freelanceR
                oneLine.append(app.getRentIncomeR()).append(CSV_SEPARATOR);
                //rentIncomeR
                oneLine.append(app.getUnemploymentBenefitR()).append(CSV_SEPARATOR);
                //unemploymentBenefitR
                oneLine.append(app.getOtherBenefitsR()).append(CSV_SEPARATOR);
                //otherBenefitsR
                oneLine.append(app.getEkasR()).append(CSV_SEPARATOR);
                //ekasR
                oneLine.append(app.getOtherIncomeR()).append(CSV_SEPARATOR);
                //otherIncomeR
                oneLine.append(app.getErgomeR()).append(CSV_SEPARATOR);
                //ergomeR
                oneLine.append(app.getDepositInterestA()).append(CSV_SEPARATOR);
                //depositInterestA
                oneLine.append(app.getDepositsA()).append(CSV_SEPARATOR);
                //depositsA
                oneLine.append(app.getDomesticRealEstateA()).append(CSV_SEPARATOR);
                //domesticRealEstateA
                oneLine.append(app.getForeignRealEstateA()).append(CSV_SEPARATOR);
                //foreignRealEstateA
                oneLine.append(app.getVehicleValueA()).append(CSV_SEPARATOR);
                //vehicleValueA
                oneLine.append(app.getInvestmentsA()).append(CSV_SEPARATOR);
                //investmentsA

                // "totalIncome",
                oneLine.append(app.getTotalIncome()).append(CSV_SEPARATOR);
                //            "savedInDb",
                oneLine.append("OK").append(CSV_SEPARATOR);
                //            "status",
                oneLine.append("ACCEPTED").append(CSV_SEPARATOR);
                //            "submittedMunicipality",
                oneLine.append("Dimos Paianias").append(CSV_SEPARATOR);
                //            "time",
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                oneLine.append(now.format(formatter)).append(CSV_SEPARATOR);
                //            "householdPrincipal",
                oneLine.append(makeMemberToHouseholdString(app.getHouseholdPrincipal())).append(CSV_SEPARATOR);
                //            "householdComposition",
                oneLine.append(makeHouseHoldString(app.getHouseholdComposition())).append(CSV_SEPARATOR);
                //            "householdCompositionHistory",
                //TODO this should appear on the writing of the CSV only
                String householdCompositionString = history_time+";"+ app.getHouseholdComposition().stream().map(hm ->
                        hm.getName()+";"+hm.getSurname()+";"+hm.getAfm()+";"+hm.getDateOfBirth()
                ).collect(Collectors.joining("|"));
                oneLine.append(householdCompositionString).append(CSV_SEPARATOR);

                        //            "salariesRHistory",
                String salariesHistoryString =app.getSalariesRHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(salariesHistoryString).append(CSV_SEPARATOR);
                //            "pensionsRHistory",
                String penHis =app.getPensionsRHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(penHis).append(CSV_SEPARATOR);
                //            "farmingRHistory",
                String farmHis =app.getFarmingRHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(farmHis).append(CSV_SEPARATOR);
                //            "freelanceRHistory",
                String freeHis =app.getFreelanceRHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(freeHis).append(CSV_SEPARATOR);
                //            "otherBenefitsRHistory",
                String otherHis =app.getOtherBenefitsRHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(otherHis).append(CSV_SEPARATOR);
                //            "depositsAHistory",
                String dephis =app.getDepositsAHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(dephis).append(CSV_SEPARATOR);
                //            "domesticRealEstateAHistory",
                String domHis =app.getDomesticRealEstateAHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(domHis).append(CSV_SEPARATOR);
                //            "foreignRealEstateAHistory",
                String forHis =app.getForeignRealEstateAHistory().entrySet().stream().map(sh-> sh.getKey() +"_" + sh.getValue()).collect(Collectors.joining("|"));
                oneLine.append(forHis).append(CSV_SEPARATOR);
                //            "monthlyGuarantee",
                oneLine.append(" ").append(CSV_SEPARATOR);
                //            "totalIncome_3",
                oneLine.append(" ").append(CSV_SEPARATOR);
                //            "monthlyIncome",
                oneLine.append(" ").append(CSV_SEPARATOR);
                //            "monthlyAid",
                oneLine.append(" ").append(CSV_SEPARATOR);
                //            "savedInDb_4",
                oneLine.append("TRUE").append(CSV_SEPARATOR);
                //            "status_5"
                oneLine.append(" ").append(CSV_SEPARATOR);


                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    private static String translitarate(String name){
        char[] nameLetters = name.toUpperCase().toCharArray();
        StringBuilder res = new StringBuilder();
        for(char letter:nameLetters ){
            res.append(greekToLatin(letter));
        }
        return res.toString();
    }

    private static String greekToLatin(char c){

        switch (c){
            case 'Α': return "A";
            case 'Β': return String.valueOf('B');
            case 'Γ': return String.valueOf('C');
            case 'Δ': return String.valueOf('D');
            case 'Ε': return String.valueOf('E');
            case 'Ζ': return String.valueOf('Z');
            case 'Η':
            case 'Ι':
                return String.valueOf('I');
            case 'Θ': return "TH";
            case 'Κ': return String.valueOf('K');
            case 'Λ': return String.valueOf('L');
            case 'Μ': return String.valueOf('M');
            case 'Ν': return String.valueOf('N');
            case 'Ξ': return String.valueOf('X');
            case 'Ο': return String.valueOf('O');
            case 'Π': return String.valueOf('P');
            case 'Ρ': return String.valueOf('R');
            case 'Σ': return String.valueOf('S');
            case 'Τ': return String.valueOf('T');
            case 'Υ': return String.valueOf('Y');
            case 'Φ': return String.valueOf('F');
            case 'Χ': return "CH";
            case 'Ψ': return "PS";
            case 'Ω': return "O";
            default: return String.valueOf(c);
        }

    }


}
