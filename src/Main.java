import model.*;
import service.*;
import util.DataInitializer;

public class Main {
    private static final CashierService cashierService = ServiceFactory.getCashierService();
    private static final StoreService storeService = ServiceFactory.getStoreService();
    private static final ProductService productService = ServiceFactory.getProductService();
    private static final ClientService clientService = ServiceFactory.getClientService();

    public static void main(String[] args) {

        try {

            DataInitializer.initializeData();

            // Актуализиране на цените на продуктите във всички магазини, защото може да наближава крайният срок на годност на продуктите и трябва да се приложи отстъпка
            storeService.updatePricesForAllStores();

            System.out.println("Здравейте! Добре дошли в системата за покупка от магазини за хранителни стоки!");
            System.out.println("Изберете опция:");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход като клиент");

            int choice = -1;
            while (choice < 1 || choice > 2) {
                System.out.print("Въведете вашия избор (1 или 2): ");
                try {
                    choice = Integer.parseInt(System.console().readLine());
                    if (choice < 1 || choice > 2) {
                        System.out.println("Невалиден избор. Моля, опитайте отново.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Невалиден избор. Моля, опитайте отново.");
                }
            }

            Client client = null;
            switch (choice) {
                case 1:
                    client = clientService.registerClient();
                    break;
                case 2:
                    client = clientService.loginClient();
                    break;
            }


            Store store = storeService.selectStore();
            storeService.makePurchase(store, client);

        } catch (Exception e) {
            System.err.println("Възникна системна грешка, моля свържете се с администратор: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Благодарим ви, че пазарувахте при нас! Надяваме се да се видим отново. :)");
        }
    }
}