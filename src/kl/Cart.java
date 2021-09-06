/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Ng Pheng Loong
 */
public class Cart {
    private static String cartId;
    private ArrayList<String> productName = new ArrayList<String>();
    private ArrayList<Double> totalPrice = new ArrayList<Double>();
    private ArrayList<Integer> quantity = new ArrayList<Integer>();
    private double subTotalPrice;

    public Cart() {
        this("","",0.0,0,0.0);
    }

    public Cart(String cartId, String productName, double totalPrice, int quantity, double subTotalPrice) {
        this.cartId = cartId;
        this.subTotalPrice = subTotalPrice;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public double getSubTotalPrice() {
        return subTotalPrice;
    }

    public void setSubTotalPrice(double subTotalPrice) {
        this.subTotalPrice = subTotalPrice; 
    }
    
    public void clearValue(ArrayList<String> productName, ArrayList<Integer> quantity, ArrayList<Double> totalPrice){
        productName.clear();
        quantity.clear();
        totalPrice.clear();
    }
    
    public void calculateSubTotalPrice() {
        this.subTotalPrice = 0;
        for (double s : this.totalPrice) {
            this.subTotalPrice += s;
        }
    }
   
    
    public void addToCart(Connection con, String customerId, String newProductName, double newTotalPrice, int newQuantity) {
        clearValue(this.productName, this.quantity, this.totalPrice);
        ArrayList<String> testPrice = new ArrayList<String>();
        ArrayList<String> testQuantity = new ArrayList<String>();
        int index = 0;
        String[] strSplit;
        double[] dblSplit;
        String stringProductName;
        String stringTotalPrice;
        String stringQuantity;
        String filterColumn = "ProductName";
        boolean sameProduct = false;
        boolean existingRow = false;
        try {
            ResultSet rs = getCartData(con,customerId);
            ResultSetMetaData rsMetaData = getCartMetaData(rs);

            int count = rsMetaData.getColumnCount();
            
            while (rs.next()) {
                for (int i = 1; i<count; i++) {
                    String columnValue = rs.getString(i);
                    System.out.println(rsMetaData.getColumnName(i));
                    if (rsMetaData.getColumnName(i).equals("ProductName")) {
                        if (columnValue.toLowerCase().contains(newProductName.toLowerCase())) {
                            sameProduct =  true;
                            index = getProductPositionInCart(columnValue,newProductName);
                            appendCartProductNames(columnValue, index, newProductName);
                        }
                            
                        else {
                            this.productName.add(columnValue + "," + newProductName);
                        } 
                            
                    } else if (rsMetaData.getColumnName(i).equals("TotalPrice")) {
                        if (sameProduct) {
                            System.out.println("Running updateCartPrice");
                            testPrice = updateCartPrice(columnValue, index, newTotalPrice);
                        }
                            
                        else {
                            strSplit = columnValue.split(",");
                            dblSplit = Arrays.stream(strSplit).mapToDouble(Double::parseDouble).toArray();
                            for (double s : dblSplit) {
                                this.totalPrice.add(s);
                            }
                            testPrice.add(columnValue + "," + String.valueOf(newTotalPrice));
                        }
                            
                    } else if (rsMetaData.getColumnName(i).equals("Quantity")) {
                        if (sameProduct) {
                            
                            testQuantity = updateCartQuantity(columnValue, index, newQuantity);
                        }
                            
                        else
                            testQuantity.add(columnValue + "," + String.valueOf(newQuantity));
                    }
                }
                existingRow = true;
            }
            
            calculateSubTotalPrice();
            System.out.println(this.subTotalPrice);
            if (existingRow) {
                
                stringProductName = String.join(",", productName);
                stringTotalPrice = testPrice.stream().map(Object::toString).collect(Collectors.joining(","));
                stringQuantity = testQuantity.stream().map(Object::toString).collect(Collectors.joining(","));
                runUpdateCartQuery(con, stringProductName, stringTotalPrice, stringQuantity, customerId);
            } else {
                setSubTotalPrice(newTotalPrice);
                runInsertCartQuery(con, newProductName, newTotalPrice, newQuantity);
            }
 

      } catch(SQLException ex) {
          System.out.println(ex);
      }
    }
    
    public void appendCartProductNames(String columnValue, int index, String newProductName) {
        String[] strSplit;
        strSplit = columnValue.split(",");
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        index = getProductPositionInCart(columnValue,newProductName);
        this.productName.add(columnValue);
    }
    
    public int getProductPositionInCart(String columnValue, String newProductName) {
        String[] strSplit;
        int index;
        strSplit = columnValue.split(",");
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        index = strList.indexOf(newProductName);
        return index;
    }
    
    public ArrayList<String> updateCartPrice(String columnValue, int index, double newTotalPrice) {
        String[] strSplit;
        double[] dblSplit;
        ArrayList<String> testPrice = new ArrayList<String>();
        strSplit = columnValue.split(",");
        dblSplit = Arrays.stream(strSplit).mapToDouble(Double::parseDouble).toArray();
        System.out.println("Before dblSplit[index]: " + dblSplit[index]);
        dblSplit[index] = dblSplit[index] + newTotalPrice;
        System.out.println("After dblSplit[index]: " + dblSplit[index]);
        for (double s : dblSplit) {
            this.totalPrice.add(s);
            testPrice.add(String.valueOf(s));
        }
        return testPrice;
    }
    
    public ArrayList<String> updateCartQuantity(String columnValue, int index, int newQuantity) {
        String[] strSplit;
        int[] intSplit;
        ArrayList<String> testQuantity = new ArrayList<String>();
        strSplit = columnValue.split(",");
        intSplit = Arrays.stream(strSplit).mapToInt(Integer::parseInt).toArray();
        intSplit[index] = intSplit[index] + newQuantity;
        for (int s : intSplit) {
            testQuantity.add(String.valueOf(s));
            this.quantity.add(s);
        }
        return testQuantity;
    }
    
    public void runUpdateCartQuery(Connection con, String productNames, String totalPrices, String quantities, String customerId) {
        try {
            String updateSql = "UPDATE Cart SET ProductName='" + productNames + "', TotalPrice='" + totalPrices + "',Quantity='" + quantities + "', SubTotalPrice='" + subTotalPrice + "' WHERE cartId='" + customerId +"'";
            Statement updateStmt = con.createStatement();
            updateStmt.execute(updateSql);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void runInsertCartQuery(Connection con, String newProductName, double newTotalPrice, int newQuantity) {
        this.productName.add(newProductName);
        this.totalPrice.add(newTotalPrice);
        this.quantity.add(newQuantity);
        String stringProductName;
        String stringTotalPrice;
        String stringQuantity; 
        stringProductName = String.join(",", this.productName);
        stringTotalPrice = totalPrice.stream().map(Object::toString).collect(Collectors.joining(","));
        stringQuantity = quantity.stream().map(Object::toString).collect(Collectors.joining(","));
        String insertNewSql = "INSERT INTO Cart(CartId, ProductName, TotalPrice, Quantity, SubTotalPrice)" + "VALUES (?,?,?,?,?)";
        try {
        Statement insertStmt = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement(insertNewSql);   
        preparedStatement.setString(1, this.cartId);
        preparedStatement.setString(2, stringProductName);
        preparedStatement.setString(3, stringTotalPrice);
        preparedStatement.setString(4, stringQuantity);
        preparedStatement.setDouble(5, subTotalPrice);
        preparedStatement.executeUpdate();   
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String removeProductNameFromCart(String columnValue, int index) {
        String[] strSplit;
        strSplit = columnValue.split(",");
        int index1 = 1;
        StringBuffer sb = new StringBuffer();
        String replacedString = "";
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        strList.remove(index);
        for (String s : strList) {
            if (index1 == strList.size())
                 sb.append(s);
            else {
                sb.append(s);
                sb.append(",");
            }
            
        index1 += 1;
        }
        return sb.toString();
    }
    
    public String removePriceFromCart(String columnValue, int index) {
        String[] strSplit;
        strSplit = columnValue.split(",");
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        strList.remove(index);
        return String.join(",", strList);
    }
    
    public String removeQuantityFromCart(String columnValue, int index) {
        String[] strSplit;
        strSplit = columnValue.split(",");
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        strList.remove(index);
        return String.join(",", strList);
    }
    
    public void removeFromCart(Connection con, String newProductName) {
            
        
        String replacedString = "";
        String replacedTotalPrice = "";
        String replacedQuantity = "";
        int index = 0;
        try {
           ResultSet rs = getCartData(con,this.cartId);
           ResultSetMetaData rsMetaData = getCartMetaData(rs);
           int count = rsMetaData.getColumnCount();
           while(rs.next()) {
               for (int i = 1; i<count; i++) {
                String columnValue = rs.getString(i);
                if (rsMetaData.getColumnName(i).equals("ProductName")) {
                    if (columnValue.toLowerCase().contains(newProductName.toLowerCase())) {
                        this.productName.clear();
                        index = getProductPositionInCart(columnValue, newProductName);
                        
                        replacedString = removeProductNameFromCart(columnValue, index);
                    }
                    
               }else if (rsMetaData.getColumnName(i).equals("TotalPrice")) {

                        this.totalPrice.clear();
                        replacedTotalPrice = removePriceFromCart(columnValue, index);
                        
               }else if (rsMetaData.getColumnName(i).equals("Quantity")) {
                   this.quantity.clear();
                   replacedQuantity = removeQuantityFromCart(columnValue, index);
               }
            } 
                runUpdateCartQuery(con, replacedString, replacedTotalPrice, replacedQuantity, this.cartId);
            }
          
        } catch(SQLException ex) {
                System.out.println(ex);
            }
    }
    
    public ResultSetMetaData getCartMetaData(ResultSet rs) {
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            
            return rsMetaData;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }
    
    public ResultSet getCartData(Connection con, String customerId) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from Cart where CartId='" + customerId + "'");
            
            return rs;
        } catch(SQLException ex) {
            System.out.println(ex);
        }
       return null;
    }
    
    public void checkout() {
        
    }
    
    public void removeAllFromCart() {
        this.productName.clear();
        this.totalPrice.clear();
        this.quantity.clear();
    }
    
    public void changeQuantity(String productName, int newQuantity) {
        int index = this.productName.indexOf(productName);
        this.quantity.set(index, newQuantity);
    }

    public String toString(Connection con) {
        String test = "SELECT * FROM Cart WHERE CartId='" + this.cartId + "'";
        try {
            Statement stmt = con.createStatement();
            ResultSet set = stmt.executeQuery(test);  
            if (set.next()) {
                return "Cart{" + "productName=" + set.getString(2) + ", totalPrice=" + set.getString(3) + ", quantity=" + set.getString(4) + ", subTotalPrice=" + set.getString(5) + '}'; 
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}