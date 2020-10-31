package com.capgemini.employee;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.capgemini.employee.EmployeePayrollService.IOService.DB_IO;
import static com.capgemini.employee.EmployeePayrollService.IOService.FILE_IO;

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
}
