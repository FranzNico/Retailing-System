package database;

import model.Customers;
import model.Products;
import model.Sales;
import model.SalesDet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 * @author scis
 */
public class Data {
    private static Connection con;
    private static Integer lastid;

    private Data() {
        // private constructor
    }

    public static void setConnection(){
        try {
            String conStr = "jdbc:mysql://localhost:3308/sales?user=root&password=&useUnicode=true" +
                    "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            con = DriverManager.getConnection(conStr);
            System.out.println("connected");
            lastid = 0;
            setLastCust();
        } catch (Exception e) {
            System.out.println("bad connection");
        }
    }

    public static String saveProduct(Products newP) {
        String s = "";
        PreparedStatement ps, psc;
        String st = "INSERT INTO products(prodid, description, price)  VALUES (?,?,?)";
        try {
            ps = con.prepareStatement(st);
            ps.setString(1, newP.getProdid());
            ps.setString(2, newP.getDescription());
            ps.setDouble(3, newP.getPrice());
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException se) {
            s =   se.getErrorCode() + " " + se.getMessage();
        } catch (Exception e) {
            s = e.getMessage();
        }
        return s;
    }

    public static String saveCustomer(Customers newC) {
        String s = "";
        PreparedStatement ps, psc;
        String st = "INSERT INTO customers(custid, custname, address, telno)  VALUES (?,?,?,?)";
        try {
            lastid = lastid + 1;
            ps = con.prepareStatement(st);
            ps.setInt(1, lastid);
            ps.setString(2, newC.getCustname());
            ps.setString(3, newC.getAddress());
            ps.setString(4, newC.getTelno());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            s =   se.getErrorCode() + " " + se.getMessage();
        } catch (Exception e) {
            s = e.getMessage();
        }
        return s;
    }

    public static ArrayList<Customers> getCustomers() {
        ArrayList<Customers> ca = new ArrayList<>();
        Customers cust;
        try {
            Statement st = con.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
            ResultSet rs = st.executeQuery("Select * from customers order by custname");
            rs.beforeFirst();
            while (rs.next()) {
                cust = new Customers(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getDouble(5));
                ca.add(cust);
            }
            rs.close();
            st.close();
        } catch (SQLException se){

        }
        return ca;
    }

    public static List<Sales> getSales() {
        final List<Sales> sales = new ArrayList<>();
        final String query = "SELECT * FROM sales JOIN customers USING(custid)";

        try {
            final Statement statement = con.createStatement(ResultSet.CONCUR_UPDATABLE,ResultSet.TYPE_SCROLL_SENSITIVE);
            final ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                final String invid = resultSet.getString(1);
                final Date invdate = resultSet.getDate(2);

                final String prodid = resultSet.getString(3);
                final String productDescription = resultSet.getString(4);
                final double productPrice = resultSet.getDouble(5);
                final Products product = new Products(prodid, productDescription, productPrice);

                final int quantitySold = resultSet.getInt(6);
                final SalesDet salesDet = new SalesDet(product, quantitySold);

                final Integer customerId = resultSet.getInt(7);
                final String customerName = resultSet.getString(8);
                final String address = resultSet.getString(9);
                final String telno = resultSet.getString(10);
                final double balance = resultSet.getDouble(11);
                final Customers customer = new Customers(customerId, customerName, address, telno, balance);

                final double amount = resultSet.getDouble(13);

                sales.add(new Sales(invid, invdate, salesDet, customer, amount));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return sales;
    }

    public static ArrayList<Products> getProducts() {
        ArrayList<Products> pa = new ArrayList<>();
        try {
            Statement st = con.createStatement(ResultSet.CONCUR_UPDATABLE,ResultSet.TYPE_SCROLL_SENSITIVE);
            ResultSet rs = st.executeQuery("Select * from Products order by description");
            rs.beforeFirst();
            while (rs.next()) {
                Products prod = new Products(rs.getString(1),rs.getString(2),rs.getDouble(3));
                pa.add(prod);
            }
            rs.close();
            st.close();
        } catch (SQLException se){

        }
        return pa;
    }
/*
    public static String saveSales(Sales newS) {

        String s = "";
        PreparedStatement ps, psc;
        String stsa = "INSERT INTO sales(invid, invdate, custid)  VALUES (?,?,?)";
        String stsd = "INSERT INTO salesdetails(invid, prodid, qtysold, unitprice)  VALUES (?,?,?,?)";
        try {
            //update sales header
            ps = con.prepareStatement(stsa);
            //transaction processing
            ps.setString(1, newS.getInvid());
            ps.setDate(2, new java.sql.Date(newS.getInvdate().getTime()));
            ps.setInt(3, newS.getCustid());
            ps.execute();
            //update sales details
            ps.close();
            ps= con.prepareStatement(stsd);
            ArrayList<SalesDet> salesdets = newS.getSalesDet();
            for (SalesDet sdet: salesdets) {
                ps.setString(1, newS.getInvid());
                ps.setString(2,sdet.getProdid() );
                ps.setInt(3, sdet.getQtysold());
                ps.setDouble(4, sdet.getPrice());
                ps.execute();
            }
            ps.close();
        }
        catch (SQLException se) {
            s =   se.getErrorCode() + " " + se.getMessage();
        } catch (Exception e) {
            s = e.getMessage();
        }
        return s;
    }*/

    private static void setLastCust() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("Select max(custid) from customers");
            rs.beforeFirst();
            while (rs.next()) {
                lastid = rs.getInt(1);
            }
            rs.close();
            st.close();
        } catch (SQLException se){

        }
    }

    public static Integer getLastid(){
        return lastid;
    }

    public static void DbDone() throws Exception {
        if (con!=null) {
            con.close();
            System.out.println("connection closed");
        }
    }

}
