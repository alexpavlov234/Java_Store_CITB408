package util;

import model.*;
import service.*;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;


/**
 * Клас за предварително зареждане на тестови данни в приложението,
 * ако няма съществуващи данни
 */
public class DataInitializer {

    private static final String DATA_DIR = "data/";

    // Услуги
    private static final CashierService cashierService = ServiceFactory.getCashierService();
    private static final StoreService storeService = ServiceFactory.getStoreService();
    private static final ProductService productService = ServiceFactory.getProductService();
    private static final ClientService clientService = ServiceFactory.getClientService();
    private static final CashDeskService cashDeskService = ServiceFactory.getCashDeskService();

    /**
     * Инициализира данни, ако е необходимо.
     * Проверява дали съществува директория за данни и ако не,
     * създава тестови данни за магазини, касиери, продукти и касови апарати.
     * В случай на грешка при инициализацията, изтрива създадените данни.
     */
    public static void initializeData() {
        // Проверка дали директорията за данни съществува
        File dataDir = new File(DATA_DIR);
        boolean isFirstRun = !dataDir.exists() || dataDir.list() == null || dataDir.list().length == 0;

        try {
            if (isFirstRun) {
                System.out.println("Инициализиране на първоначални тестови данни...");

                Store store1 = storeService.createEntity(new Store("Магазин София", new HashMap<>() {{
                    put(ProductCategory.FOOD, 20.0);
                    put(ProductCategory.NON_FOOD, 30.0);
                }}, 3, 25.0));

                Store store2 = storeService.createEntity(new Store("Магазин Пловдив", new HashMap<>() {{
                    put(ProductCategory.FOOD, 15.0);
                    put(ProductCategory.NON_FOOD, 25.0);
                }}, 2, 20.0));

                Cashier cashier1 = cashierService.createEntity(new Cashier("Петър Петров", 1200));
                Cashier cashier2 = cashierService.createEntity(new Cashier("Мария Иванова", 1300));

                store1.addCashier(cashier1.getId());
                store1.addCashier(cashier2.getId());

                cashDeskService.createEntity(new CashDesk(store1.getId(), cashier1.getId()));
                cashDeskService.createEntity(new CashDesk(store1.getId(), cashier2.getId()));

                Cashier cashier3 = cashierService.createEntity(new Cashier("Георги Георгиев", 1100));
                Cashier cashier4 = cashierService.createEntity(new Cashier("Анна Димитрова", 1250));
                Cashier cashier5 = cashierService.createEntity(new Cashier("Иван Колев", 1150));

                store2.addCashier(cashier3.getId());
                store2.addCashier(cashier4.getId());
                store2.addCashier(cashier5.getId());

                cashDeskService.createEntity(new CashDesk(store2.getId(), cashier3.getId()));
                cashDeskService.createEntity(new CashDesk(store2.getId(), cashier4.getId()));
                cashDeskService.createEntity(new CashDesk(store2.getId(), cashier5.getId()));

                store1.addProductStock(productService.createEntity(new Product("Хляб Симид", 0.90, ProductCategory.FOOD, LocalDate.now().plusMonths(2))), 20);
                store1.addProductStock(productService.createEntity(new Product("Мляко Верея", 1.20, ProductCategory.FOOD, LocalDate.now().plusMonths(1))), 15);
                store1.addProductStock(productService.createEntity(new Product("Паста за зъби Colgate", 2.5, ProductCategory.NON_FOOD, LocalDate.now().plusMonths(6))), 10);
                store1.addProductStock(productService.createEntity(new Product("Кафе Jacobs", 5.0, ProductCategory.FOOD, LocalDate.now().plusMonths(3))), 5);
                store1.addProductStock(productService.createEntity(new Product("Шампоан Head & Shoulders", 6.0, ProductCategory.NON_FOOD, LocalDate.now().plusMonths(4))), 8);
                store1.addProductStock(productService.createEntity(new Product("Бира Кракра", 1.5, ProductCategory.FOOD, LocalDate.now().plusMonths(2))), 12);
                store1.addProductStock(productService.createEntity(new Product("Сок Дари", 1.0, ProductCategory.FOOD, LocalDate.now().plusDays(1))), 25);
                store1.addProductStock(productService.createEntity(new Product("Пакетирани оризови крекери", 2.0, ProductCategory.FOOD, LocalDate.now().minusDays(5))), 30);
                store1.addProductStock(productService.createEntity(new Product("Кафе на зърна", 10.0, ProductCategory.FOOD, LocalDate.now().plusMonths(6))), 3);

                store2.addProductStock(productService.createEntity(new Product("Паста Barilla", 1.5, ProductCategory.FOOD, LocalDate.now().plusMonths(4))), 20);
                store2.addProductStock(productService.createEntity(new Product("Кисело мляко Златна Добруджа", 0.80, ProductCategory.FOOD, LocalDate.now().plusMonths(1))), 15);
                store2.addProductStock(productService.createEntity(new Product("Шампоан Pantene", 5.5, ProductCategory.NON_FOOD, LocalDate.now().plusMonths(5))), 10);
                store2.addProductStock(productService.createEntity(new Product("Кафе Nescafe", 4.0, ProductCategory.FOOD, LocalDate.now().plusMonths(3))), 5);
                store2.addProductStock(productService.createEntity(new Product("Пакетирани бисквити", 1.2, ProductCategory.FOOD, LocalDate.now().plusMonths(2))), 12);
                store2.addProductStock(productService.createEntity(new Product("Сок Сънрайз", 1.0, ProductCategory.FOOD, LocalDate.now().plusMonths(1))), 25);
                store2.addProductStock(productService.createEntity(new Product("Пакетирани чипсове", 1.8, ProductCategory.FOOD, LocalDate.now().plusDays(2))), 30);
                store2.addProductStock(productService.createEntity(new Product("Кафе на зърна Lavazza", 12.0, ProductCategory.FOOD, LocalDate.now().minusDays(1))), 3);
                store2.addProductStock(productService.createEntity(new Product("Бира Загорка", 1.8, ProductCategory.FOOD, LocalDate.now().plusMonths(2))), 12);

                storeService.updateEntity(store1);
                storeService.updateEntity(store2);

                System.out.println("Тестовите данни са заредени успешно!");
            } else {
                System.out.println("Съществуващи данни бяха намерени. Прескачане на инициализацията.");
            }
        } catch (Exception e) {
            System.err.println("Грешка при инициализацията на данните: " + e.getMessage());
            e.printStackTrace();

            System.err.println("Изтриване на създадените данни...");

            if (dataDir.exists()) {
                for (File file : dataDir.listFiles()) {
                    if (!file.delete()) {
                        System.err.println("Неуспешно изтриване на файл: " + file.getName());
                    }
                }
                if (!dataDir.delete()) {
                    System.err.println("Неуспешно изтриване на директорията: " + dataDir.getName());
                }
            }
        }
    }


}
