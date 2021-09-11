/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class DeliveryClass {
    private String DeliveryID;
    private String DeliveryCompany;
    private String Details;
    private String DateDelivery;
    private String States;
    private String Address;
    private double deliveryFees;

    public DeliveryClass() {
        this("","","","","","",0.0);
    }

    public DeliveryClass(String DeliveryID, String DeliveryCompany, String Details, String DateDelivery,String States, String Address, double deliveryFees) {
        this.DeliveryID = DeliveryID;
        this.DeliveryCompany = DeliveryCompany;
        this.Details = Details;
        this.DateDelivery = DateDelivery;
        this.States = States;
        this.Address = Address;
        this.deliveryFees = deliveryFees;
    }

    public String getDeliveryID() {
        return DeliveryID;
    }

    public void setDeliveryID(String DeliveryID) {
        this.DeliveryID = DeliveryID;
    }

    public String getDeliveryCompany() {
        return DeliveryCompany;
    }

    public void setDeliveryCompany(String DeliveryCompany) {
        this.DeliveryCompany = DeliveryCompany;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String Details) {
        this.Details = Details;
    }
    
    public String getDateDelivery() {
        return DateDelivery;
    }

    public void setDateDelivery(String DateDelivery) {
        this.DateDelivery = DateDelivery;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getStates() {
        return States;
    }

    public void setStates(String States) {
        this.States = States;
    }

    public double getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(double deliveryFees) {
        this.deliveryFees = deliveryFees;
    }
    
    
        public static void insertDelivery(Connection myConObj, String random, String date, String deliveryCompany, String details, String address, String states, double deliveryFees){
            Scanner s1 = new Scanner(System.in);
     try{
            myConObj = DriverManager.getConnection("jdbc:derby://localhost:1527/test", "ngphengloong", "123");
            String insertNewUserSQL = "INSERT INTO computershop.Delivery(DeliveryID, DeliveryDate, DeliveryCompany, Details, Address, States, DeliveryFees )" + " VALUES (?,?, ?,?,?,?,?)";
            //String updateUserSQL = "UPDATE computershop.Delivery SET DeliveryID = ?" + " WHERE DeliveryID = ?";
            PreparedStatement pstmt = myConObj.prepareStatement(insertNewUserSQL);
            pstmt.setString(1, random);
            pstmt.setString(2, date);
            pstmt.setString(3, deliveryCompany);
            pstmt.setString(4, details);
            pstmt.setString(5, address);
            pstmt.setString(6, states);
            pstmt.setDouble(7, deliveryFees);
           
          
            System.out.println("Please comfirm the information is correct(Yes/No):");
            String Comfirm = s1.nextLine();
            Comfirm.toLowerCase();
            if( Comfirm.toLowerCase().equals("yes") ){
                pstmt.executeUpdate();
                System.out.println("===============================");
                System.out.println("Successfull");
            }else{
                System.out.println("===============================");
                System.out.println("The data wouldn't be save");
                int opt = Staff.mainPage();
                Delivery.delivery();
            }
            }catch (SQLException e) {
            e.printStackTrace();
            
        }          
    
        }
        public static void updateDelivery(Connection myConObj, String deliveryID, String details){
            String updateSql = "UPDATE Delivery SET " + "Details" + "=" + "'"+ details + "'" + " WHERE DeliveryID = '" + deliveryID +"'";
        try {
            Statement mystatObj = myConObj.createStatement();
            
            mystatObj.execute(updateSql);
            
            System.out.println("Successfull");
        } catch (Exception ex) {
          System.out.println(ex);
        }
        }
        public double calculateState(){
            double st =0;
            
           if(this.States.equals("Johor"))
               st = 10;
           else if(this.States.equals("Selangor"))
               st = 5;
           else if(this.States.equals("Pahang"))
               st = 12;
           else if(this.States.equals("Pinang"))
               st = 20;
           else if(this.States.equals("Melake"))
               st = 6;
          return st;
       }
}
