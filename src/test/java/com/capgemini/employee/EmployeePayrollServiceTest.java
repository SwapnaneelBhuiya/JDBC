package com.capgemini.employee;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.capgemini.employee.EmployeePayrollService.IOService.*;
public class EmployeePayrollServiceTest {


    //    @Test
//    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries()
//    {
//        EmployeePayrollData[] arrayOfEmps= {
//                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
//                new EmployeePayrollData(2, "Bill Gates", 200000.0),
//                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
//        };
//        EmployeePayrollService employeePayrollService;
//        employeePayrollService=new EmployeePayrollService(Arrays.asList(arrayOfEmps));
//        employeePayrollService.writeEmployeePayrollData(FILE_IO);
//        employeePayrollService.printData(FILE_IO);
//        long entries=employeePayrollService.countEntries(FILE_IO);
//        Assert.assertEquals(3,entries);
//    }
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount()
    {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(DB_IO);
        Assert.assertEquals(4,employeePayrollData.size());
    }
    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch()
    {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.updateEmployeeSalary("Shikha",300000.00);
        boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Shikha");
        Assert.assertTrue(result);
    }
    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount()
    {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        LocalDate startDate=LocalDate.of(2018,01,01);
        LocalDate endDate= LocalDate.now();
        List<EmployeePayrollData> employeePayrollData=
                employeePayrollService.readEmployeePayrollForDateRange(DB_IO,startDate,endDate);
        Assert.assertEquals(4,employeePayrollData.size());

    }
    @Test
    public void checkVariousOperations()
    {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(DB_IO);
        double sum= employeePayrollService.checksSum("M");
        double avg= employeePayrollService.checkAverage("M");
        double min= employeePayrollService.checkMin("M");
        double max= employeePayrollService.checkMax("M");
        double cont= employeePayrollService.checkCount("M");
        Assert.assertEquals(avg, 193333,0.33333333334);
        Assert.assertEquals(sum,580000,0.00);
        Assert.assertEquals(min,80000,0.00);
        Assert.assertEquals(max,400000,0.00);
        Assert.assertEquals(cont,3,0.00);
    }
    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeeToPayroll("Mark",500000.00,LocalDate.now(),"M");
        boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }
    @Test
    public void givenEmployee_WhenRemoved_ShouldMatch() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.removeEmployee("Shikha");
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Shikha");
        Assert.assertTrue(result);
    }
    @Test
    public  void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps={
                new EmployeePayrollData(0,"Jeff Bezos","M",100000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Bill gates","M",200000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Mark Zuckerberg","M",300000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Sunder","M",600000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Mukesh","M",1000000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Anil","M",200000.0,LocalDate.now())
        };
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        Instant start=Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end=Instant.now();
        System.out.println("Duration without thread:"+ Duration.between(start,end));
        Instant threadStart=Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd=Instant.now();
        System.out.println("Duration with Thread:" +Duration.between(threadStart,threadEnd));
        Assert.assertEquals(6,employeePayrollService.countEntries(DB_IO));
    }
    @Before
    public void setup(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=3000;
    }
    public EmployeePayrollData[] getEmployeeList(){
        Response response=RestAssured.get("/employee_payroll");
        System.out.println("EMPLOYEE PAYROLL ENTRIES IN JSONServer:\n" +response.asString());
        EmployeePayrollData[] arrayOfEmps=new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
        return arrayOfEmps;
    }
    public Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData)
    {
        String empJson=new Gson().toJson((employeePayrollData));
        RequestSpecification request=RestAssured.given();
        request.header("Content_type","application/json");
        request.body(empJson);
        return request.post("/employee_payroll");
    }
    @Test
    public void givenEmployeeDataInServer_WhenRetrieved_ShouldMatchTheCount(){
        EmployeePayrollData[] arrayOfEmps=getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService=new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        long entries=employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(2,entries);
    }
    @Test
    public void giveNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() throws SQLException {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps=getEmployeeList();
        employeePayrollService=new EmployeePayrollService((Arrays.asList(arrayOfEmps)));
        EmployeePayrollData employeePayrollData=new EmployeePayrollData(0,"Mark Zuckerberg","M",300000.0,LocalDate.now());
        Response response=addEmployeeToJsonServer(employeePayrollData);
        int statusCode =response.getStatusCode();
        Assert.assertEquals(201,statusCode);
        employeePayrollData=new Gson().fromJson(response.asString(),EmployeePayrollData.class);
        employeePayrollService.addEmployeeToPayroll(employeePayrollData,REST_IO);
        long entries=employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(3,entries);
    }
    @Test
    public void givenListOfNewEmployees_WhenAdded_ShouldMatch201ResponseAndCount() throws SQLException {
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps=getEmployeeList();
        employeePayrollService=new EmployeePayrollService((Arrays.asList(arrayOfEmps)));
        EmployeePayrollData[] arrayOfEmpPayrolls={
                new EmployeePayrollData(0,"Sunder","M",100000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Mukesh","M",400000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Anil","M",50000.0,LocalDate.now())
        };
        for(EmployeePayrollData employeePayrollData:arrayOfEmpPayrolls){
            Response response=addEmployeeToJsonServer(employeePayrollData);
            int statusCode =response.getStatusCode();
            Assert.assertEquals(201,statusCode);
            employeePayrollData=new Gson().fromJson(response.asString(),EmployeePayrollData.class);
            employeePayrollService.addEmployeeToPayroll(employeePayrollData,REST_IO);
        }
        long entries=employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(6,entries);
    }
    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch200Response(){
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps=getEmployeeList();
        employeePayrollService=new EmployeePayrollService((Arrays.asList(arrayOfEmps)));
        employeePayrollService.updateEmployeeSalary("Anil",300000.00,REST_IO);
        EmployeePayrollData employeePayrollData=employeePayrollService.getEmployeePayrollData("Anil");
        String empJson=new Gson().toJson((employeePayrollData));
        RequestSpecification request=RestAssured.given();
        request.header("Content_type","application/json");
        request.body(empJson);
        Response response=request.put("/employee_payroll/"+employeePayrollData.id);
        int statusCode= response.getStatusCode();
        Assert.assertEquals(200,statusCode);
    }
    @Test
    public void givenEmployeeToDelete_WhenDeleted_ShouldMatch200ResponseAndCount(){
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps=getEmployeeList();
        employeePayrollService=new EmployeePayrollService((Arrays.asList(arrayOfEmps)));
        EmployeePayrollData employeePayrollData=employeePayrollService.getEmployeePayrollData("Anil");
        RequestSpecification request=RestAssured.given();
        request.header("Content_type","application/json");
        Response response=request.delete("/employee_payroll/"+employeePayrollData.id);
        int statusCode= response.getStatusCode();
        Assert.assertEquals(200,statusCode);
    }
}
