package com.capgemini.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
    public EmployeePayrollService() {
        employeePayrollDBService=EmployeePayrollDBService.getInstance();
    }
    private static EmployeePayrollDBService employeePayrollDBService;
    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}
    private List<EmployeePayrollData> employeePayrollList;
    public void updateEmployeeSalary(String name, double salary) {
        int result=EmployeePayrollDBService.getInstance().updateEmployeeData(name,salary);
        if(result==0) return;
        EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
        if(employeePayrollData!=null) employeePayrollData.salary=salary;
    }
    private EmployeePayrollData getEmployeePayrollData(String name)
    {
        return this.employeePayrollList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
    }
    public int employeeBetweenDates()
    {
        this.employeePayrollList=EmployeePayrollDBService.getInstance().readDataWithDate();
        return employeePayrollList.size();
    }
    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList=EmployeePayrollDBService.getInstance().getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }
    public EmployeePayrollService(List<EmployeePayrollData> emp)
    {
        this();this.employeePayrollList=emp;
    }
    public double checksSum(String gender)
    {
        double sum=EmployeePayrollDBService.getInstance().checkSumSalary(gender);
        return sum;
    }
    public double checkAverage(String gender)
    {
        double avg=EmployeePayrollDBService.getInstance().checkAverage(gender);
        return avg;
    }
    public double checkMin(String gender)
    {
        double min=EmployeePayrollDBService.getInstance().checkMinimum(gender);
        return min;
    }
    public double checkMax(String gender)
    {
        double max=EmployeePayrollDBService.getInstance().checkMaximum(gender);
        return max;
    }
    public double checkCount(String gender)
    {
        double cont=EmployeePayrollDBService.getInstance().checkCounts(gender);
        return cont;
    }
//    public void writeEmployeePayrollData(IOService ioService)
//    {
//        if(ioService.equals(IOService.CONSOLE_IO))
//            System.out.println("\nWriting Employee Payroll Roaster to Console");
//        else if(ioService.equals(IOService.FILE_IO))
//            new EmployeePayrollFileIOService().writeData(employeePayrollList);
//    }
//    public static void main(String args[])
//    {
//        List<EmployeePayrollData> emp=new ArrayList<>();
//        EmployeePayrollService employeePayrollService=new EmployeePayrollService(emp);
//        Scanner consoleInputReader=new Scanner(System.in);
//        employeePayrollService.readEmployeePayrollData(consoleInputReader);
//        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
//    }
    private void readEmployeePayrollData(Scanner consoleInputReader)
    {
        System.out.println("Enter Employee ID:");
        int id=consoleInputReader.nextInt();
        System.out.println("Enter Employee Name:");
        String name=consoleInputReader.nextLine();
        System.out.println("Enter Employee Salary:");
        double salary=consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id,name,salary));
    }
    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService)
    {
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList=EmployeePayrollDBService.getInstance().readData();
        return this.employeePayrollList;
    }
//    public void printData(IOService ioService)
//    {
//        if(ioService.equals(IOService.FILE_IO))
//            new EmployeePayrollFileIOService().printData();
//    }
//    public long countEntries(IOService ioService)
//    {
//        if(ioService.equals(IOService.FILE_IO))
//            return new EmployeePayrollFileIOService().countEntries();
//        return 0;
//    }
}
