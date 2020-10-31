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
        return this.getEmployeePayrollDataUsingDB(sql);

    }
    public List<EmployeePayrollData> readDataWithDate()
    {
        String sql="select * from employee_payroll where start between cast('2019-01-01' as date) and date (now());";
        return this.getEmployeePayrollDataUsingDB(sql);
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

    public double checkSumSalary(String gender) {
        String sql="select sum(salary) as sum from employee_payroll where gender=? group by gender;";
        double sum=0;
        try(Connection connection=this.getConnection();){
            employeePayrollDataStatement = connection.prepareStatement(sql);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                sum = resultSet.getInt("sum");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return sum;
    }

    public double checkAverage(String gender) {
        String sql="select avg(salary) as avg from employee_payroll where gender=? group by gender;";
        double avg=0;
        try(Connection connection=this.getConnection();){
            employeePayrollDataStatement = connection.prepareStatement(sql);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                avg = resultSet.getInt("avg");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return avg;
    }

    public double checkMinimum(String gender) {
        String sql="select min(salary) as min from employee_payroll where gender=? group by gender;";
        double min=0;
        try(Connection connection=this.getConnection();){
            employeePayrollDataStatement = connection.prepareStatement(sql);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                min = resultSet.getInt("min");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return min;
    }

    public double checkMaximum(String gender) {
        String sql="select max(salary) as max from employee_payroll where gender=? group by gender;";
        double max=0;
        try(Connection connection=this.getConnection();){
            employeePayrollDataStatement = connection.prepareStatement(sql);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet result = employeePayrollDataStatement.executeQuery();
            while (result.next()) {
                max = result.getDouble("max");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return max;
    }

    public double checkCounts(String gender) {
        String sql="SELECT COUNT(*) as Count FROM employee_payroll WHERE gender = ?;";
        double count=0;
        try(Connection connection=this.getConnection();){
            employeePayrollDataStatement = connection.prepareStatement(sql);
            employeePayrollDataStatement.setString(1, gender);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("Count");
            }
        }
        catch(SQLException e){e.printStackTrace();}
        return count;
    }

    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql=String.format("Select * from employee_payroll where start between '%s' and '%s';",Date.valueOf(startDate),Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
        try(Connection connection=this.getConnection();){
            Statement statement=connection.createStatement();
            ResultSet result=statement.executeQuery(sql);
            employeePayrollList=this.getEmployeePayrollData((result));
        }
        catch(SQLException e){e.printStackTrace();}
        return  employeePayrollList;
    }

    public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender) {
        int id = -1;
        EmployeePayrollData employeePayrollData = null;
            String sql = String.format("Insert into employee_payroll(name,gender,salary,start) values ('%s','%s','%s','%s','%s','%s')", name, salary, Date.valueOf(startDate),gender);
        try (Connection connection=this.getConnection();) {
            Statement statement=connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    id = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(id, name, salary, startDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) throws SQLException {
        int id = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (Statement statement = connection.createStatement();) {
            String sql = String.format("Insert into employee_payroll(name,gender,salary,start) values ('%s','%s','%s','%s','%s','%s')", name, salary, Date.valueOf(startDate),gender);
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    id = resultSet.getInt(1);
            }
//            employeePayrollData = new EmployeePayrollData(id, name, salary, address, gender, date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement();){
            double deductions=salary*0.2;
            double taxablePay=salary-deductions;
            double tax=taxablePay*0.1;
            double netPay=salary-tax;
            String sql=String.format("Insert into payroll(empID,basic_pay,deductions,taxable_pay,tax,net_pay) values (%s,%s,%s,%s,%s,%s)",id,salary,deductions,taxablePay,tax,netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(id, name, salary, startDate);

            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            connection.rollback();
        }
        finally{
            try{connection.close();}
            catch(Exception e){}
        }
        return employeePayrollData;
    }
    public void removeEmployeeData(String name) {
        String sql = String.format("update employee_payroll set isActive='F' where name='%s';",name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
