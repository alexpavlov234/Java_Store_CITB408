import dao.*;
import model.*;
import service.*;
import util.DataInitializer;

import java.util.List;
import java.util.Optional;

public class Main {
    private static final CashierService cashierService = ServiceFactory.getCashierService();
    private static final StoreService storeService = ServiceFactory.getStoreService();
    private static final ProductService productService = ServiceFactory.getProductService();
    private static final ClientService clientService = ServiceFactory.getClientService();

    public static void main(String[] args) {
        DataInitializer.initializeData();
        FileStorage.loadAllData();

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
                System.out.println("Невалиден избор. Моля, въведете число 1 или 2.");
            }
        }

        switch (choice) {
            case 1:
                clientService.registerClient();
                break;
            case 2:
                clientService.loginClient();
                break;
            default:
                System.out.println("Невалиден избор. Моля, опитайте отново.");
                return;
        }


        Store store = storeService.selectStore();

        System.out.println("Сега можете да изберете продукти за покупка.");
        System.out.println("Налични продукти в магазина:");
        List<Product> products = store.getAvailableProducts();

        if (products.isEmpty()) {
            System.out.println("Няма налични продукти в магазина.");
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("%d. %s - %.2f лв. (Категория: %s, Срок на годност: %s)\n",
                    (i + 1), product.getName(), product.getUnitSalePrice(),
                    product.getCategory(), product.getExpirationDate());
        }

        System.out.println("Изберете продукт, който искате да закупите (въведете стойността на номера):");
        int productIndex = -1;
        while (productIndex < 0 || productIndex >= products.size()) {
            System.out.print("Въведете номера на продукта: ");
            try {
                productIndex = Integer.parseInt(System.console().readLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Невалиден номер. Моля, опитайте отново.");
            }
        }
        Product selectedProduct = products.get(productIndex);
        System.out.println("Избрахте продукт: " + selectedProduct.getName());
        System.out.print("Колко броя искате да закупите? ");
        int quantity = -1;
        while (quantity <= 0) {
            try {
                quantity = Integer.parseInt(System.console().readLine());
                if (quantity <= 0) {
                    System.out.println("Количеството трябва да бъде положително число. Моля, опитайте отново.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Невалидно количество. Моля, въведете цяло число.");
            }
        }
        System.out.println("Вие сте избрали " + quantity + " броя от продукта: " + selectedProduct.getName());
        System.out.println("Общата цена за покупката е: " + (selectedProduct.getUnitSalePrice() * quantity) + " лв.");





    }
}