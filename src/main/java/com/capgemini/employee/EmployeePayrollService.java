package com.capgemini.employee;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService{

    public EmployeePayrollService() {
        employeePayrollDBService=EmployeePayrollDBService.getInstance();
    }
    private static EmployeePayrollDBService employeePayrollDBService;

    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate,endDate);
        return null;
    }

    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) throws SQLException {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,salary,startDate,gender));
    }

    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee Being added: "+employeePayrollData.name);
            try {
                this.addEmployeeToPayroll(employeePayrollData.name,employeePayrollData.salary,employeePayrollData.startDate,employeePayrollData.gender);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println("Employee Added: "+employeePayrollData.name);
        });
        System.out.println(this.employeePayrollList);

    }

    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus=new HashMap<Integer,Boolean>();
        employeePayrollDataList.forEach(employeePayrollData ->{
                Runnable task=()->{
            employeeAdditionStatus.put(employeePayrollData.hashCode(),false);
            System.out.println("Employee Being Added: "+Thread.currentThread().getName());
                    try {
                        this.addEmployeeToPayroll(employeePayrollData.name,employeePayrollData.salary,employeePayrollData.startDate,employeePayrollData.gender);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    employeeAdditionStatus.put(employeePayrollData.hashCode(),true);
            System.out.println("Employee Added "+employeePayrollData.name);
        };
        Thread thread=new Thread(task,employeePayrollData.name);
        thread.start();
    });
        while(employeeAdditionStatus.containsValue(false)){
            try{
                Thread.sleep(20);
            }
            catch(InterruptedException e){}
        }
        System.out.println(this.employeePayrollList);
    }

    public int countEntries(IOService dbIo) {
        return this.employeePayrollList.size();
    }

    public void addEmployeeToPayroll(EmployeePayrollData employeePayrollData, IOService ioService) throws SQLException {
        if(ioService.equals(IOService.DB_IO))
            this.addEmployeeToPayroll(employeePayrollData.name,employeePayrollData.salary,employeePayrollData.startDate,employeePayrollData.gender);
        else
            employeePayrollList.add(employeePayrollData);
    }

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}
    private List<EmployeePayrollData> employeePayrollList;
    public void updateEmployeeSalary(String name, double salary) {
        int result=EmployeePayrollDBService.getInstance().updateEmployeeData(name,salary);
        if(result==0) return;
        EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
        if(employeePayrollData!=null) employeePayrollData.salary=salary;
    }
    public void updateEmployeeSalary(String name, double salary, IOService ioService) {
        if(ioService.equals(IOService.DB_IO)){
            int result=EmployeePayrollDBService.getInstance().updateEmployeeData(name,salary);
            if(result==0) return;
        }
        EmployeePayrollData employeePayrollData=this.getEmployeePayrollData(name);
        if(employeePayrollData!=null) employeePayrollData.salary=salary;
    }
    public EmployeePayrollData getEmployeePayrollData(String name)
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
        this();this.employeePayrollList=new ArrayList<>(emp);
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
    public void removeEmployee(String name){
        employeePayrollDBService.removeEmployeeData(name);
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
