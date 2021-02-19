# SBchain KEA Monitoring Application

The MonitoringApp consists of two major services: 
* the daily monitoring service
* the monthly payment service.

The monitoring and payment services run on a set time schedule, 
the monitoring service each day at 12:00:00, 
and the payment service on the first day of each month 
(applies to the previous month)
* The monitoring service implements several credential checks and application validation checks that may disqualify and remove applications from the system.
* It also comprises of algorithms for the automatic calculation of the payment value that a household should receive at the time of execution and any potential due payment (“offset”) that may have occurred, by factoring in criteria such as altered or expired credentials and age differences that may exist during the monitoring period (a minor coming of age). Offsets are retrospective, in the sense if information arrives in the system invalidating a previous payment made to the applicant, the offset might result in diminished or greater amounts being paid or even no payment at all.

Daily monitoring service: checks & calculation of the payment values (debit and credit)
Monthly payment service: no checks, calculate the payments values (offset / positive or negative - retrospective value through the daily monitoring service)

### Testing

This unit test generates 4 mock applications mirroring the financial data of the example shown in http://e-learning.keaprogram.gr/el/Lesson/L03/Asset/258091/faq_manual, and also generates a mock case that emulates the lifecycle of those applications in the SbChain Monitoring Application from 15/12/2020 to 3/2/2021.

The run of the unit test simulates a run of the monitoring service on the date 4/2/2021 during which one of the application gets 2 updated financial criteria that are dated before the date of the latest payment (1/2/2021), this forces a recalculation of the total income of the household and results in different benefit amounts being attributed to the case and also the calculation of the offset that occurs for the intermediary period between the dates of the updated data and the latest payment.

In order to execute this test  please do the following:
* Open a command line or terminal
* Navigate to the root of the cloned repository
*  Run the command “mvn -Dtest=ExampleRunTest test”

To modify the unit test navigate to ExampleRunTest.java test file and
alter the generated applications with different values to get different results, by altering the data inside the history fields and also in the current fields in the methods : generateExampleSsiApp1(),  generateExampleSsiApp2() , generateExampleSsiApp3(),  generateExampleSsiApp4()

For example: 
```
ssiApp.setFreelanceR("700");
LinkedHashMap<String , String> freelanceHistory = new LinkedHashMap<>();
freelanceHistory.put(DateUtils.dateToString(LocalDateTime.of(2020, 12, 15, 00, 00, 00)), "500");
freelanceHistory.put(DateUtils.dateToString(LocalDateTime.of(2021, 1, 17, 00, 00, 00)), "700");
```
The user should alter or add new values to the freelanceHistory map but also update the value in the setFreelanceR() field with the latest value chronologically




This project was developed by the University of the Aegean for the 
purposes of the ultra-Social Benefits Transparency & Accountability
(ultra-SocBenTΑ) project. This project was funded by Siemens via Settlement Agreement with Hellenic Republic 