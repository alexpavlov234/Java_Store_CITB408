package util;

import model.*;
import service.CashierService;
import service.ServiceFactory;

import java.io.File;

/**
 * Клас за предварително зареждане на тестови данни в приложението,
 * ако няма съществуващи данни
 */
public class DataInitializer {
    
    private static final String DATA_DIR = "data/";
    
    // Услуги
    private static final CashierService cashierService = ServiceFactory.getCashierService();

    /**
     * Инициализира данни, ако е необходимо
     */
    public static void initializeData() {
        // Проверка дали директорията за данни съществува
        File dataDir = new File(DATA_DIR);
        boolean isFirstRun = !dataDir.exists() || dataDir.list() == null || dataDir.list().length == 0;
        
        if (isFirstRun) {
            System.out.println("Инициализиране на първоначални тестови данни...");
            
            // Зареждане на основни данни
            initializeCashiers();

            
            System.out.println("Тестовите данни са заредени успешно!");
        } else {
            System.out.println("Съществуващи данни бяха намерени. Прескачане на инициализацията.");
        }
    }
    

    /**
     * Инициализира касиери
     */
    private static void initializeCashiers() {
        cashierService.createEntity(new Cashier("Петър Петров", 1200));
        cashierService.createEntity(new Cashier("Мария Иванова", 1300));
        cashierService.createEntity(new Cashier("Георги Георгиев", 1100));
        cashierService.createEntity(new Cashier("Анна Димитрова", 1250));
        cashierService.createEntity(new Cashier("Иван Колев", 1150));
    }

}
