package service;

import model.Product;

/**
 * Фабрика за създаване на услуги (services).
 * Този клас използва шаблона Singleton, за да гарантира, че
 * има само по една инстанция от всяка услуга в приложението.
 */
public class ServiceFactory {
    
    // Статични инстанции на услугите (lazy initialization)
    private static CashierService cashierService;
    private static CashDeskService cashDeskService;
    private static ClientService clientService;
    private static ProductService productService;
    private static StoreService storeService;
    
    /**
     * Връща услуга (service) за работа с касиери
     * @return услуга за касиери
     */
    public static CashierService getCashierService() {
        if (cashierService == null) {
            cashierService = new CashierService();
        }
        return cashierService;
    }

    /**
     * Връща услуга (service) за работа с каси
     * @return услуга за каси
     */
    public static CashDeskService getCashDeskService() {
        if (cashDeskService == null) {
            cashDeskService = new CashDeskService();
        }
        return cashDeskService;
    }

    /**
     * Връща услуга (service) за работа с клиенти
     * @return услуга за клиенти
     */
    public static ClientService getClientService() {
        if (clientService == null) {
            clientService = new ClientService();
        }
        return clientService;
    }

    /**
     * Връща услуга (service) за работа с продукти
     * @return услуга за продукти
     */
    public static ProductService getProductService() {
        if (productService == null) {
            productService = new ProductService();
        }
        return productService;
    }

    /**
     * Връща услуга (service) за работа с магазини
     * @return услуга за магазини
     */
    public static StoreService getStoreService() {
        if (storeService == null) {
            storeService = new StoreService();
        }
        return storeService;
    }

    // Частен конструктор, за да предотвратим създаването на инстанции
    private ServiceFactory() {
    }
}