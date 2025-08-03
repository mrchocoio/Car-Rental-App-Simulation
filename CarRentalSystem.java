import java.io.*;
import java.util.*;
import java.time.*;

public class CarRentalSystem {
    static String[] Car_models = new String[100];
    static double[] Car_rates = new double[Car_models.length];
    static boolean[] is_Available = new boolean[Car_models.length];
    static int carCount = 0;
    public static Scanner scanner = new Scanner(System.in);
    static String[] username = new String[40];
    static String[] pwd = new String[username.length];
    static int count = 0;
    static String current_User = null;
    public static void main(String[] args) {
        cars();
        customers(); 
        while (true) {
            System.out.println("\n--------> Car Rental System <-------");
            System.out.println("1. Admin Login");
            System.out.println("2. Register as Customer");
            System.out.println("3. Customer Login");
            System.out.println("4. Rent a Car");
            System.out.println("5. Return a Car");
            System.out.println("6. Exit");
            System.out.print("Select option: ");
            int choice;
            try{
            choice = scanner.nextInt();
            scanner.nextLine();}
            catch(InputMismatchException e){
                System.out.println("Wrong Input");
                scanner.nextLine();
                continue;
            }

            if (choice == 1) {
                admin_login();
            } else if (choice == 2) {
                customer_regitration();
            } else if (choice == 3) {
                login_customer();
            } else if (choice == 4) {
                if (current_User == null) {
                    System.out.println("login as a customer first.");
                }
                else{
                    renting();
                }
            } 
            else if (choice == 5) {
                if (current_User == null) {
                    System.out.println("login as a customer first");
                } 
                else {
                    returning();
                }
            } 
            else if (choice == 6) {
                System.out.println("exiting");
                break;
            } 
            else {
                System.out.println("invalid option.");
            }
        }
    }
    static void admin_login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (username.equals("./admin") && password.equals("admin123")) {
            menu_admin();
        } 
        else {
            System.out.println("invalid admin credentials");
        }
    }

    static void menu_admin() {
        while (true) {
            System.out.println("\n-------- Admin Menu --------");
            System.out.println("1. Add Car");
            System.out.println("2. Remove Car");
            System.out.println("3. View Cars");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select option: ");
            int option = 10;
            try{
            option = scanner.nextInt();
            scanner.nextLine();}catch(InputMismatchException e){
                System.out.println("Wrong input");
                scanner.nextLine();
            }

            if (option == 1) {
                System.out.print("enter car model: ");
                Car_models[carCount] = scanner.nextLine();
                System.out.print("enter daily rate:  ");
                Car_rates[carCount] = scanner.nextDouble();
                is_Available[carCount] = true;
                carCount++;
                saving_cars();
                System.out.println("car added uccessfully");
            } else if (option == 2) {
                cars_menu();
                System.out.print("enter car index to remove ");
                int index = scanner.nextInt();
                scanner.nextLine();
                if (index >= 0 && index < carCount) {
                    for (int i = index; i < carCount - 1; i++) {
                        Car_models[i] = Car_models[i + 1];
                        Car_rates[i] = Car_rates[i + 1];
                        is_Available[i] = is_Available[i + 1];
                    }
                    carCount--;
                    saving_cars();
                    System.out.println("car successfully removed");
                } else {
                    System.out.println("enter a valid option");
                }
            } else if (option == 3) {
                cars_menu();
            } else if (option == 4) {
                break;
            } else {
                System.out.println("enter a valid option");
            }
        }
    }
    static void cars_menu() {
        System.out.println("\n-------Car List-------");
        for (int i = 0; i < carCount; i++) {
            System.out.println(i + ". " + Car_models[i] + " | Rate: " + Car_rates[i] + " | Available: " + is_Available[i]);
        }
    }

    static void renting() {
        cars_menu();
        System.out.print("Enter car index to rent: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index < 0 || index >= carCount || !is_Available[index]) {
            System.out.println("Car not available.");
            return;
        }
        System.out.print("Enter number of days to rent: ");
        int days = scanner.nextInt();
        scanner.nextLine();
        double cost = Car_rates[index] * days;
        is_Available[index] = false;
        saving_cars();
        logs(current_User, Car_models[index], days, cost, "RENTED");
        System.out.println("Car rented. Total cost: " + cost + "Pkr");
    }

    static void returning() {
    cars_menu();
    System.out.print("Enter car index to return: ");
    int index = scanner.nextInt();
    scanner.nextLine();

    if (index < 0 || index >= carCount || is_Available[index]) {
        System.out.println("Invalid return.");
        return;
    }
    if (!if_user_rented_car(current_User, Car_models[index])) {
        System.out.println("You did not rent this car. You cannot return it.");
        return;
    }
    System.out.print("Was the return late? (yes/no): ");
    String late = scanner.nextLine();
    System.out.print("Enter number of days rented: ");
    int days = scanner.nextInt();
    scanner.nextLine();

    double cost = Car_rates[index] * days;
    if (late.equalsIgnoreCase("yes")) {
        cost *= 1.2;
    }
    is_Available[index] = true;
    saving_cars();
    logs(current_User, Car_models[index], days, cost, "RETURNED");
    System.out.println("Return successful. Total cost: " + cost);
}


    static void saving_cars() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("cars.txt"));
            for (int i = 0; i < carCount; i++) {
                writer.write(Car_models[i] + "," + Car_rates[i] + "," + is_Available[i] + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("couldnt save car information");
        }
    }

    static void cars() {
    try {
        File file = new File("cars.txt");
        if (!file.exists()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.close();
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null && carCount < 50) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                Car_models[carCount] = parts[0];
                Car_rates[carCount] = Double.parseDouble(parts[1]);
                is_Available[carCount] = Boolean.parseBoolean(parts[2]);
                carCount++;
            }
        }
        br.close();
    } catch (Exception e) {
        System.out.println("couldnt load cars an error occured.");
    }
}


    static void customer_regitration() {
    String user;
    while (true) {
        System.out.print("Enter new username: ");
        user = scanner.nextLine();

        boolean CustomerExists = customer_exists(user);
        if (CustomerExists) {
            System.out.println("username already taken... think of another username");
        } else {
            break;
        }
    }
    System.out.print("enter your new pass: ");
    String pass = scanner.nextLine();
    customer_information(user, pass);
    username[count] = user;
    pwd[count] = pass;
    count++;

    System.out.println("successfully registered as a customer");
}
    static void login_customer() {
        System.out.print("username: ");
        String user = scanner.nextLine();
        System.out.print("pass: ");
        String pass = scanner.nextLine();

        for (int i = 0; i < count; i++) {
            if (username[i].equals(user) && pwd[i].equals(pass)) {
                current_User = user;
                System.out.println("you logged in..... Welocome, " + user);
                return;
            }
        }
        System.out.println("invalid credentials.");
    }
    static void logs(String username, String carModel, int days, double cost, String action) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("logs.txt", true));
            writer.write(username + "," + carModel + "," + days + " days," + cost + "," + action + "," + LocalDate.now() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("error couldnt write logs");
        }
    }
    static void customer_information(String username, String pwd){
    try{
        BufferedWriter writer = new BufferedWriter(new FileWriter("Customer Information.txt", true)); 
        writer.write(username + "," + pwd);
        writer.newLine();
        writer.close();
    }catch(IOException e){
        System.out.println("couldnt write customer information");
    }
}
static void customers() {
    try {
        File file = new File("Customer Information.txt");
        if (!file.exists()) return;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null && count < username.length) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                username[count] = parts[0].trim();
                pwd[count] = parts[1].trim();
                count++;
            }
        }
        br.close();
    } catch (IOException e) {
        System.out.println("customer ata couldnt be loaded");
    }
}
    static boolean customer_exists(String username){
    try {
        BufferedReader br = new BufferedReader(new FileReader("Customer Information.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                if (parts[0].trim().equals(username.trim())) {
                    br.close();
                    return true;
                }
            }
        }
        br.close();
    } catch (Exception e) {
        System.out.println("Customer records couldnt be located");
    }
    return false;
}
static boolean if_user_rented_car(String user, String model) {
    try {
        BufferedReader br = new BufferedReader(new FileReader("logs.txt"));
        String line;
        boolean found = false;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String user_temp = parts[0].trim();
                String car_temp = parts[1].trim();
                String car_rented_temp = parts[4].trim();

                if (user_temp.equals(user) && car_temp.equals(model)) {
                    if (car_rented_temp.equals("RENTED")) {
                        found = true;
                    } else if (car_rented_temp.equals("RETURNED")) {
                        found = false;
                    }
                }
            }
        }

        br.close();
        return found;
    } catch (IOException e) {
        System.out.println("Error counldnt read the logs");
        return false;
    }
}
}
