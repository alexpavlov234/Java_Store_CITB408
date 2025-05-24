package service;

/**
 * Фабрика за създаване на услуги (services).
 * Този клас използва шаблона Singleton, за да гарантира, че
 * има само по една инстанция от всяка услуга в приложението.
 */
public class ServiceFactory {
    
    // Статични инстанции на услугите (lazy initialization)
    private static CashierService cashierService;
    
    /**
     * Връща услуга (service) за работа с касиери
     * @return сървис за касиери
     */
    public static CashierService getCashierService() {
        if (cashierService == null) {
            cashierService = new CashierService();
        }
        return cashierService;
    }

    // Частен конструктор, за да предотвратим създаването на инстанции
    private ServiceFactory() {
    }
}