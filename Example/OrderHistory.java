
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.math.BigDecimal;

import edu.drexel.cs.jah473.sqlite.QueryManager;
import edu.drexel.cs.jah473.sqlite.QueryResults;

public class OrderHistory {

    public static void main(String[] args) {
        QueryManager qm;
        try {
            qm = new QueryManager("orderhistory.db");
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            return;
        }
        Object ALL_ORDERS_QUERY = new Object();
        Object ORDER_DETAIL_QUERY = new Object();
        class AllOrdersResults extends QueryResults {
            LocalDateTime date;
            int orderID;
            BigDecimal total;
        }
        class OrderDetailResults extends QueryResults {
            String item;
            BigDecimal price;
            int quantity;
        }

        
        qm.addQuery(ALL_ORDERS_QUERY,
                "SELECT DATETIME(date) AS dt, orderid, total FROM orders WHERE customerid = ? ORDER BY dt;",
                AllOrdersResults.class);
        qm.addQuery(
                ORDER_DETAIL_QUERY,
                "SELECT itemname, itemprice, quantity FROM items, orderitems WHERE orderid = ? and items.itemid = orderitems.itemid;",
                OrderDetailResults.class);
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("1. All orders");
            System.out.println("2. Order details");
            System.out.println("3. Quit");
            System.out.print("\nEnter selection --> ");
            int choice = input.nextInt();
            if (choice == 1) {
                System.out.print("\nEnter customer ID --> ");
                int custID = input.nextInt();
                AllOrdersResults res = qm.executeQuery(ALL_ORDERS_QUERY, custID);
                Format dateTimeFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a").toFormat();
                Format moneyFormat = NumberFormat.getCurrencyInstance();
                System.out.format("\n%-30s%-10s%-10s\n", "Date", "Order ID", "Total");
                System.out.println("---------------------------------------------");
                while (res.nextRow()) {
                    System.out.format("%-30s", dateTimeFormat.format(res.date));
                    System.out.format("%-10s", res.orderID);
                    System.out.format("%-10s\n", moneyFormat.format(res.total));
                }
            } else if (choice == 2) {
                System.out.print("\nEnter order ID --> ");
                int orderID = input.nextInt();
                OrderDetailResults res = qm.executeQuery(ORDER_DETAIL_QUERY, orderID);
                Format moneyFormat = NumberFormat.getCurrencyInstance();
                System.out.format("\n%-20s%-10s%-10s\n", "Item", "Price", "Quantity");
                System.out.println("-------------------------------------------");
                while (res.nextRow()) {
                    System.out.format("%-20s", res.item);
                    System.out.format("%-10s", moneyFormat.format(res.price));
                    System.out.format("%-10s\n", res.quantity);
                }
            } else if (choice == 3) {
                qm.close();
                input.close();
                return;
            } else {
                System.out.println("Invalid selection, try again.");
            }
        }
    }

}
