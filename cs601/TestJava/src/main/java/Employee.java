/**
 * Created by treexy1230 on 5/14/15.
 */
class Employee {
    String ID;
    static String companyName;
    static public void setCompanyName(String n) {
        companyName = n;
        // test
//        String id = getID();  // method in static should also be static
    }

    public String getID() {
        setCompanyName("a");
        return ID;
    }

//    static public void setCompanyName(String n) {
//        this.companyName = n;  // Employee.this cannot be referenced from a static context
//    }

    public static void main(String[] args) {
        Employee employee = new Employee();

    }
}