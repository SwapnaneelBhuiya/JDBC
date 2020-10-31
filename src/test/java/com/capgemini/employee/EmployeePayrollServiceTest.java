package com.capgemini.employee;

import org.junit.Assert;
import org.junit.Test;

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
}
