package com.capgemini.employee;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double salary;
    public LocalDate startDate;
    String gender;
    public EmployeePayrollData(int id, String name, String gender,double salary, LocalDate startDate) {
        this(id,name,salary);
        this.startDate=startDate;
        this.gender=gender;
    }

    public EmployeePayrollData(int id, String name, double salary) {
        this.id=id;
        this.name=name;
        this.salary=salary;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this(id,name,salary);
        this.startDate=startDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,gender,salary,startDate);
    }

    @Override
    public String toString()
    {
        return "id="+id+" name= "+name+" salary= "+salary;
    }
    @Override
    public boolean equals(Object e)
    {
        if(this==e)return true;
        if(e==null || getClass()!=e.getClass()) return false;
        EmployeePayrollData that=(EmployeePayrollData)e;
        return id==that.id &&
                Double.compare(that.salary,salary)==0 &&
                name.equals(that.name);
    }
}
