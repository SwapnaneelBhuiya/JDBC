package com.capgemini.employee;

import java.time.LocalDate;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double salary;
    public LocalDate startDate;
    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this(id,name,salary);
        this.startDate=startDate;
    }

    public EmployeePayrollData(int id, String name, double salary) {
        this.id=id;
        this.name=name;
        this.salary=salary;
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
