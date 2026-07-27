package com.poc.emp_sp_camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeEvent {

    @JsonProperty("empId")
    private long empId;

    @JsonProperty("empName")
    private String empName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("hireDate")
    private String hireDate;

    @JsonProperty("line1")
    private String line1;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zipCode")
    private String zipCode;

    @JsonProperty("country")
    private String country;

    // Getters and Setters
    public long getEmpId()               { return empId; }
    public void setEmpId(long empId)     { this.empId = empId; }

    public String getEmpName()                  { return empName; }
    public void setEmpName(String empName)      { this.empName = empName; }

    public String getDepartment()               { return department; }
    public void setDepartment(String dept)      { this.department = dept; }

    public String getHireDate()                 { return hireDate; }
    public void setHireDate(String hireDate)    { this.hireDate = hireDate; }

    public String getLine1()                    { return line1; }
    public void setLine1(String line1)          { this.line1 = line1; }

    public String getCity()                     { return city; }
    public void setCity(String city)            { this.city = city; }

    public String getState()                    { return state; }
    public void setState(String state)          { this.state = state; }

    public String getZipCode()                  { return zipCode; }
    public void setZipCode(String zipCode)      { this.zipCode = zipCode; }

    public String getCountry()                  { return country; }
    public void setCountry(String country)      { this.country = country; }
}