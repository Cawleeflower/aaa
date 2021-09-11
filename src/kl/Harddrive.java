/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import wagu.Board;
import wagu.Table;

/**
 *
 * @author Ng Pheng Loong
 */
public class Harddrive extends Product{
    
    private String capacity;
    private String rpm;
    private String cache;

    public Harddrive() {
        this("","","","","",0.0,"","");
    }

    public Harddrive(String capacity, String rpm, String cache, String productName, String warranty, double price, String dateOfManufacture, String origin) {
        super(productName, warranty, price, dateOfManufacture, origin);
        this.capacity = capacity;
        this.rpm = rpm;
        this.cache = cache;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getRpm() {
        return rpm;
    }

    public void setRpm(String rpm) {
        this.rpm = rpm;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public void addProduct(Database database) {
        String monitorId = autoGenerateId(database);
        String insertNewSql = "INSERT INTO Harddrive (HarddriveId, Capacity, Rpm, Cache)" + " VALUES (?,?,?,?)";
        String insertProductSql = "INSERT INTO Product (ProductName, Price, DateOfManufacture, Origin, Warranty,SubProductId)" + " VALUES (?,?,?,?,?,?)";
         try {
         PreparedStatement preparedStatement = database.getCon().prepareStatement(insertNewSql);   
         preparedStatement.setString(1, monitorId);
         preparedStatement.setString(2, getCapacity());
         preparedStatement.setString(3, getRpm());
         preparedStatement.setString(4, getCache());
         preparedStatement.executeUpdate();
         
         PreparedStatement preparedStatement1 = database.getCon().prepareStatement(insertProductSql);  
         preparedStatement1.setString(1, super.getProductName());
         preparedStatement1.setDouble(2, super.getPrice());
         preparedStatement1.setString(3, super.getDateOfManufacture());
         preparedStatement1.setString(4, super.getOrigin());
         preparedStatement1.setString(5, super.getWarranty());
         preparedStatement1.setString(6, monitorId);
         preparedStatement1.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    public <T> void updateProduct(Database database, String columnToUpdate, String newValue) {
        String insertNewSql;
        if (columnToUpdate.equals("Price")) {
            Double.parseDouble(newValue);
            insertNewSql = "Update Product set " + columnToUpdate + "=" + newValue + "where " + "productName='" + super.getProductName() + "'";
        } if (columnToUpdate.toLowerCase().equals("product name")) {
            String[] test = columnToUpdate.split(" ");
            insertNewSql = "Update Product set " + test[0] + test[1] + "='" + newValue + "'where " + "productName='" + super.getProductName() + "'";
        }else
            insertNewSql = "Update Product set " + columnToUpdate + "='" + newValue + "'where " + "productName='" + super.getProductName() + "'";
         try {
            PreparedStatement preparedStatement = database.getCon().prepareStatement(insertNewSql);
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    public void deleteProduct(Database database, String productName) {
        String deleteNewSql = "Delete from Product Where ProductName='" + productName + "'";
        try {
            Statement stmt = database.getCon().createStatement();
            boolean isDeleted = stmt.execute(deleteNewSql);
            if (isDeleted) {
                System.out.println("Product successfully deleted!");
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void listProducts(Database database, Product product) {
        try {
            Statement stmt = database.getCon().createStatement();
            ResultSet rs = stmt.executeQuery("select * from Product");
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    
                    String columnValue = rs.getString(i);
                    
                    if (rsMetaData.getColumnName(i).equals("ProductName")) {
                     System.out.print(columnValue);   
                    }
                }
            System.out.println("");
        }
      } catch(Exception ex) {
          System.out.println(ex);
      }
    }
    
    public void getProductDetailsFromUser() {
        
        Scanner scan = new Scanner(System.in);
        Scanner scan1 = new Scanner(System.in);
        
        super.getProductDetailsFromUser();
        
        //Get and as input and set product name
        System.out.println("Please enter capacity of harddrive");
        String capacity = scan.nextLine();
        setCapacity(capacity);
        
        //Get and as input and set product detail
        System.out.println("Please enter Rpm of harddrive");
        String rpm = scan.nextLine();
        setRpm(rpm);
        
        System.out.println("Please enter cache of harddrive");
        String cache = scan.nextLine();
        setCache(cache);
        
        
    }
    
    public String autoGenerateId(Database database) {
        String getCount = "SELECT COUNT(HarddriveId) FROM Harddrive";
        String monitorId;
        try {
            Statement stmt = database.getCon().createStatement();
            ResultSet rs = stmt.executeQuery(getCount);
            if (rs.next()) {
                monitorId = "H000" + String.valueOf(rs.getInt(1) + 1);
                return monitorId;
            }
            
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    public void printColumnNames() {
        super.printColumnNames();
        System.out.println("Capacity");
        System.out.println("Rpm");
        System.out.println("Cache\n");
    }
    
    public static String getProductsFromDatabase(Database database) {
        List<List<String>> listOfLists = new ArrayList<>();
        List<String> headersList = Arrays.asList("PRODUCTNAME", "PRICE", "DATEOFMANUFACTURE", "ORIGIN", "WARRANTY", "CAPACITY", "CACHE", "RPM");
        try {
            Statement stmt = database.getCon().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT p.ProductName, "
                    + "p.Price, "
                    + "p.DateOfManufacture, "
                    + "p.Origin, "
                    + "p.Warranty, "
                    + "h.Capacity, "
                    + "h.Cache, "
                    + "h.rpm FROM Product p INNER JOIN Harddrive h ON p.SubProductId = h.HarddriveId");
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                List<String> innerList = new ArrayList<>();
                
                innerList.add(rs.getString(1));
                innerList.add(rs.getString(2));
                innerList.add(rs.getString(3));
                innerList.add(rs.getString(4));
                innerList.add(rs.getString(5));   
                innerList.add(rs.getString(6));  
                innerList.add(rs.getString(7));  
                innerList.add(rs.getString(8));  
                listOfLists.add(innerList);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (!listOfLists.isEmpty()) {
            Board board = new Board(75);
            String tableString1 = board.setInitialBlock(new Table(board, 75, headersList, listOfLists).tableToBlocks()).build().getPreview();
            return tableString1;
        }
        return "";
        
    }
}
