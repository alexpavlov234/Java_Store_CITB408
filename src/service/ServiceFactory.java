package service;

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
    private static ReceiptService receiptService;

    // Частен конструктор, за да предотвратим създаването на инстанции
    private ServiceFactory() {
    }

    /**
     * Връща инстанция на услугата за касиери (CashierService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на CashierService.
     */
    public static CashierService getCashierService() {
        if (cashierService == null) {
            cashierService = new CashierService();
        }
        return cashierService;
    }

    /**
     * Връща инстанция на услугата за касови апарати (CashDeskService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на CashDeskService.
     */
    public static CashDeskService getCashDeskService() {
        if (cashDeskService == null) {
            cashDeskService = new CashDeskService();
        }
        return cashDeskService;
    }

    /**
     * Връща инстанция на услугата за клиенти (ClientService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на ClientService.
     */
    public static ClientService getClientService() {
        if (clientService == null) {
            clientService = new ClientService();
        }
        return clientService;
    }

    /**
     * Връща инстанция на услугата за продукти (ProductService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на ProductService.
     */
    public static ProductService getProductService() {
        if (productService == null) {
            productService = new ProductService();
        }
        return productService;
    }

    /**
     * Връща инстанция на услугата за магазини (StoreService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на StoreService.
     */
    public static StoreService getStoreService() {
        if (storeService == null) {
            storeService = new StoreService();
        }
        return storeService;
    }

    /**
     * Връща инстанция на услугата за касови бележки (ReceiptService).
     * При първо извикване създава нова инстанция (lazy initialization).
     *
     * @return Инстанция на ReceiptService.
     */
    public static ReceiptService getReceiptService() {
        if (receiptService == null) {
            receiptService = new ReceiptService();
        }
        return receiptService;
    }

}

