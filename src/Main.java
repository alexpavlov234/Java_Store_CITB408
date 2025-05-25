import model.Client;
import model.Store;
import service.*;
import util.DataInitializer;

public class Main {
    private static final StoreService storeService = ServiceFactory.getStoreService();
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
            System.out.println("3. Администраторски изглед");

            int choice = -1;
            while (choice < 1 || choice > 3) {
                System.out.print("Въведете вашия избор (1-3): ");
                try {
                    choice = Integer.parseInt(System.console().readLine());
                    if (choice < 1 || choice > 3) {
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
                    Store storeClient = storeService.selectStore();
                    storeService.makePurchase(storeClient, client);
                    break;
                case 2:
                    client = clientService.loginClient();
                    Store storeLoggedInClient = storeService.selectStore();
                    storeService.makePurchase(storeLoggedInClient, client);
                    break;
                case 3:
                    System.out.println("Администраторски изглед:");
                    Store selectedStore = storeService.selectStore();
                    if (selectedStore != null) {
                        System.out.println("\nФинансова информация за магазин: " + selectedStore.getName());
                        System.out.printf("Разходи за заплати: %.2f лв.\n", selectedStore.calculateTotalSalariesExpense());
                        System.out.printf("Разходи за доставени стоки: %.2f лв.\n", selectedStore.calculateDeliveredGoodsExpense());
                        System.out.printf("Приходи от продадени стоки: %.2f лв.\n", selectedStore.calculateTotalIncome());
                        System.out.printf("Печалба: %.2f лв.\n", selectedStore.calculateProfit());
                    }
                    break;
            }

        } catch (Exception e) {
            System.err.println("Възникна системна грешка, моля свържете се с администратор: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Благодарим ви, че пазарувахте при нас! Надяваме се да се видим отново. :)");
        }
    }
}