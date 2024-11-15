import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;


class IceCream{
    private static class Sale{
        LocalDate date;
        String sku;
        int unitPrice;
        int quantity;
        int totalPrice;

        public Sale(LocalDate date,String sku,int unitPrice,  int quantity, int totalPrice){
            this.date=date;
            this.sku=sku;
            this.unitPrice=unitPrice;
            this.quantity=quantity;
            this.totalPrice=totalPrice;
        }
    }
    public static void main(String args[]){

        List<Sale> sales=new ArrayList<>();
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        TreeMap<String,Integer> monthSales=new TreeMap<>();
        HashMap<String,HashMap<String,Integer>> eachProductQuantity=new HashMap<>();
        HashMap<String,HashMap<String,Integer>> eachProductRevenue=new HashMap<>();
        HashMap<String,HashMap<String,List<Integer>>> eachProductPerDayQuantity=new HashMap<>();
        int totalSale=0;
        try(BufferedReader br=new BufferedReader(new FileReader("data.txt"))){
            br.readLine();
            while(br.ready()){
                String[] sale=br.readLine().split(",");
                LocalDate date=LocalDate.parse(sale[0], dtf);
                String sku=sale[1];
                int unitPrice=Integer.parseInt(sale[2]);
                int quantity=Integer.parseInt(sale[3]);
                int totalPrice=Integer.parseInt(sale[4]);
                sales.add(new Sale(date,sku,unitPrice,quantity,totalPrice));

            }
        }
        catch(IOException e){
            System.out.println(e);
        }

        for(Sale sale: sales){
            totalSale+=sale.totalPrice;
            String month=sale.date.getMonthValue()+"-"+sale.date.getYear()+"-"+sale.date.getMonth();

            //managing each month revenue
            monthSales.put(month,monthSales.getOrDefault(month,0)+sale.totalPrice);

            //managing each product quantity in each month
            eachProductQuantity.putIfAbsent(month,new HashMap<>());
            eachProductQuantity.get(month).put(sale.sku,eachProductQuantity.get(month).getOrDefault(sale.sku,0)+sale.quantity);

            //managing each product revenue in each month
            eachProductRevenue.putIfAbsent(month,new HashMap<>());
            eachProductRevenue.get(month).put(sale.sku,eachProductRevenue.get(month).getOrDefault(sale.sku,0)+sale.totalPrice);

            // managing data of number of quantity of each day in a month of a particular product
            eachProductPerDayQuantity.putIfAbsent(month,new HashMap<>());
            eachProductPerDayQuantity.get(month).putIfAbsent(sale.sku,new ArrayList<Integer>());
            eachProductPerDayQuantity.get(month).get(sale.sku).add(sale.quantity);


        }
        System.out.println("Total Sales: " + totalSale);
        System.out.println();
        System.out.println("Month-wise Sales Totals:");
        System.out.println();

        for(String month: monthSales.keySet()){
            String MM=month.split("-")[2];
            System.out.println(MM+"-"+monthSales.get(month));
            
            //Most popular item (most quantity sold) in each month.
            String mostPopular=null;
            int maxQuantity=0;
            for(Map.Entry<String,Integer> e:eachProductQuantity.get(month).entrySet()){
                if(maxQuantity<e.getValue()){
                    maxQuantity=e.getValue();
                    mostPopular=e.getKey();
                }
            }
            System.out.println("Most popular item in "+MM+" - "+mostPopular);

            //Items generating most revenue in each month.
            String mostRevenueItem=null;
            int maxRevenue=0;

            for(Map.Entry<String,Integer> e: eachProductRevenue.get(month).entrySet()){
                if(maxRevenue<e.getValue()){
                    maxRevenue=e.getValue();
                    mostRevenueItem=e.getKey();
                }
            }
            System.out.println("Most revenue generating item in "+MM+" - "+mostRevenueItem);

            //For the most popular item, find the min, max and average number of orders each month.
            if(mostPopular!=null){
                List<Integer> list=eachProductPerDayQuantity.get(month).get(mostPopular);
                int min=Collections.min(list);
                int max=Collections.max(list);
                double sum=0.0;
                for(int i: list){
                    sum+=i;
                }
                double average=sum/list.size();
                System.out.println("Min Quantity: "+min);
                System.out.println("Max Quantity: "+max);
                System.out.println("Average Quantity: "+average);
            }
            System.out.println();
        }



    }
}