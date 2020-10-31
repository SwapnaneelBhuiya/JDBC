package com.capgemini.employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService(){

    }
    public static EmployeePayrollDBService getInstance()
    {
        if(employeePayrollDBService==null)
            employeePayrollDBService=new EmployeePayrollDBService();
        return employeePayrollDBService;
    }
    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDetailsUsingPreparedStatement(name,salary);
    }
    private int updateEmployeeDetailsUsingPreparedStatement(String name, double salary)
    {
        String sql=String.format("update employee_payroll set salary=%.2f where name ='%s';",salary,name);
        try(Connection con=this.getConnection())
        {
            Statement st=con.createStatement();
            return st.executeUpdate(sql);
        }
        catch(SQLException e){}
        return 0;
    }
    public List<EmployeePayrollData> readData()
    {
        String sql="SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
        try(Connection connection=this.getConnection();){
            Statement statement=connection.createStatement();
            ResultSet result=statement.executeQuery(sql);
            employeePayrollList=this.getEmployeePayrollData((result));
        }
        catch(SQLException e){e.printStackTrace();}
        return employeePayrollList;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName="root";
        String password="lamborginigallardo";
        Connection con;
        System.out.println("Connecting to database:"+jdbcURL);
        con= DriverManager.getConnection(jdbcURL,userName,password);
        System.out.println("Connection is successful");
        return con;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList=null;
        if(this.employeePayrollDataStatement==null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet=employeePayrollDataStatement.executeQuery();
            employeePayrollList=this.getEmployeePayrollData((resultSet));
        }
        catch(SQLException e){}
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while(resultSet.next()){
                int id=resultSet.getInt("id");
                String name=resultSet.getString("name");
                double salary=resultSet.getDouble("salary");
                LocalDate startDate=resultSet.getDate(("start")).toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id,name,salary,startDate));
            }
        }
        catch(SQLException e){}
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try{
            Connection con=this.getConnection();
            String sql="SELECT * FROM employee_payroll WHERE name=?";
            employeePayrollDataStatement=con.prepareStatement(sql);
        }
        catch(SQLException e)
        {e.printStackTrace();}
    }
}
