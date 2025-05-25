package model;

import service.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

/**
 * Представлява магазин.
 */
public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private Set<Integer> cashiersIds = new HashSet<>();
    private Set<Integer> receiptsIds = new HashSet<>();
    private Map<Integer, Integer> productsInStock = new HashMap<>();
    private Map<Integer, Integer> productsSold = new HashMap<>();

    private Map<ProductCategory, Double> markupPercentages;

    private int daysBeforeExpirationThreshold;
    private double discountPercentNearExpiration;

    /**
     * Конструктор за създаване на магазин.
     *
     * @param name                          Име на магазина.
     * @param markupPercentages             Речник с хеш-таблица с проценти на надценка по категории продукти.
     * @param daysBeforeExpirationThreshold Брой дни преди изтичане на срока на годност, за които се прилага отстъпка.
     * @param discountPercentNearExpiration Процент на отстъпка за продукти с наближаващ срок на годност.
     */
    public Store(String name, Map<ProductCategory, Double> markupPercentages, int daysBeforeExpirationThreshold, double discountPercentNearExpiration) {
        this.name = name;
        this.markupPercentages = markupPercentages;
        this.daysBeforeExpirationThreshold = daysBeforeExpirationThreshold;
        this.discountPercentNearExpiration = discountPercentNearExpiration;
    }

    /**
     * Изчислява крайната цена на продукт, като взима предвид надценката и евентуална отстъпка за наближаващ срок на годност.
     *
     * @param product Продуктът, за който се изчислява цената.
     * @return Крайната цена на продукта.
     */
    public double getProductFinalPrice(Product product) {
        double markupPercentage = markupPercentages.get(product.getCategory());

        double productFinalPrice = product.getUnitPurchasePrice() + (product.getUnitPurchasePrice() * markupPercentage / 100);

        if (isProductExpirationDiscountable(product)) {
            return productFinalPrice - (productFinalPrice * discountPercentNearExpiration / 100);
        } else {
            return productFinalPrice;
        }
    }

    /**
     * Проверява дали продуктът подлежи на отстъпка поради наближаващ срок на годност.
     *
     * @param product Продуктът за проверка.
     * @return true, ако продуктът подлежи на отстъпка, false в противен случай.
     */
    public boolean isProductExpirationDiscountable(Product product) {
        return daysBeforeExpirationThreshold >= ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
    }


    /**
     * Връща ID на магазина.
     *
     * @return ID на магазина.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на магазина.
     *
     * @param id Ново ID на магазина.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща името на магазина.
     *
     * @return Име на магазина.
     */
    public String getName() {
        return name;
    }

    /**
     * Задава името на магазина.
     *
     * @param name Ново име на магазина.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Връща множество от ID-та на касиерите в магазина.
     *
     * @return Множество от ID-та на касиери.
     */
    public Set<Integer> getCashiersIds() {
        return cashiersIds;
    }

    /**
     * Задава множество от ID-та на касиерите в магазина.
     *
     * @param cashiersIds Ново множество от ID-та на касиери.
     */
    public void setCashiersIds(Set<Integer> cashiersIds) {
        this.cashiersIds = cashiersIds;
    }

    /**
     * Добавя ID на касиер към магазина.
     *
     * @param cashierId ID на касиера за добавяне.
     */
    public void addCashier(int cashierId) {
        cashiersIds.add(cashierId);
    }

    /**
     * Премахва ID на касиер от магазина.
     *
     * @param cashierId ID на касиера за премахване.
     */
    public void removeCashier(int cashierId) {
        cashiersIds.remove(cashierId);
    }

    /**
     * Връща множество от ID-та на касовите бележки, издадени в магазина.
     *
     * @return Множество от ID-та на касови бележки.
     */
    public Set<Integer> getReceiptsIds() {
        return receiptsIds;
    }

    /**
     * Задава множество от ID-та на касовите бележки, издадени в магазина.
     *
     * @param receiptsIds Ново множество от ID-та на касови бележки.
     */
    public void setReceiptsIds(Set<Integer> receiptsIds) {
        this.receiptsIds = receiptsIds;
    }

    /**
     * Добавя ID на касова бележка към магазина.
     *
     * @param receiptId ID на касовата бележка за добавяне.
     */
    public void addReceipt(int receiptId) {
        receiptsIds.add(receiptId);
    }

    /**
     * Премахва ID на касова бележка от магазина.
     *
     * @param receiptId ID на касовата бележка за премахване.
     */
    public void removeReceipt(int receiptId) {
        receiptsIds.remove(receiptId);
    }

    /**
     * Добавя количество към наличността на даден продукт в магазина.
     *
     * @param product  Продуктът, на който се добавя наличност.
     * @param quantity Количество за добавяне.
     * @throws IllegalArgumentException ако количеството е отрицателно.
     */
    public void addProductStock(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }
        int currentStock = productsInStock.getOrDefault(product.getId(), 0);
        productsInStock.put(product.getId(), currentStock + quantity);
    }

    /**
     * Премахва количество от наличността на даден продукт в магазина.
     *
     * @param product  Продуктът, от който се премахва наличност.
     * @param quantity Количество за премахване.
     * @return true, ако операцията е успешна, false ако няма достатъчно наличност.
     * @throws IllegalArgumentException ако количеството е отрицателно.
     */
    public boolean removeProductStock(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }

        int currentStock = productsInStock.getOrDefault(product.getId(), 0);
        if (currentStock < quantity) {
            return false;
        }

        productsInStock.put(product.getId(), currentStock - quantity);
        return true;
    }

    /**
     * Добавя количество към продадените бройки на даден продукт в магазина.
     *
     * @param product  Продуктът, на който се добавят продадени бройки.
     * @param quantity Количество продадени бройки.
     * @throws IllegalArgumentException ако количеството е отрицателно.
     */
    public void addProductSold(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }
        int currentSold = productsSold.getOrDefault(product.getId(), 0);
        productsSold.put(product.getId(), currentSold + quantity);
    }

    /**
     * Премахва количество от продадените бройки на даден продукт в магазина.
     *
     * @param product  Продуктът, от който се премахват продадени бройки.
     * @param quantity Количество за премахване.
     * @return true, ако операцията е успешна, false ако няма достатъчно продадени бройки.
     * @throws IllegalArgumentException ако количеството е отрицателно.
     */
    public boolean removeProductSold(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + product.getId() + " не може да бъде отрицателно");
        }

        int currentSold = productsSold.getOrDefault(product.getId(), 0);
        if (currentSold < quantity) {
            return false; // Not enough sold
        }

        productsSold.put(product.getId(), currentSold - quantity);
        return true;
    }

    /**
     * Връща текущата наличност на продукт по неговото ID.
     *
     * @param productId ID на продукта.
     * @return Наличност на продукта.
     */
    public int getProductStock(int productId) {
        return productsInStock.getOrDefault(productId, 0);
    }

    /**
     * Връща речник с хеш-таблица с наличностите на всички продукти в магазина (ID на продукт -> количество).
     *
     * @return Речник с хеш-таблица с наличностите на продуктите.
     */
    public Map<Integer, Integer> getProductsInStock() {
        return productsInStock;
    }

    /**
     * Задава речник с хеш-таблица с наличностите на всички продукти в магазина.
     *
     * @param productsInStock Нова речник с хеш-таблица с наличностите на продуктите.
     */
    public void setProductsInStock(Map<Integer, Integer> productsInStock) {
        this.productsInStock = productsInStock;
    }

    /**
     * Връща речник с хеш-таблица с продадените бройки на всички продукти в магазина (ID на продукт -> количество).
     *
     * @return Речник с хеш-таблица с продадените бройки на продуктите.
     */
    public Map<Integer, Integer> getProductsSold() {
        return productsSold;
    }

    /**
     * Задава речник с хеш-таблица с продадените бройки на всички продукти в магазина.
     *
     * @param productsSold Нов речник с хеш-таблица с продадените бройки на продуктите.
     */
    public void setProductsSold(Map<Integer, Integer> productsSold) {
        this.productsSold = productsSold;
    }

    /**
     * Връща речник с хеш-таблица с процентите на надценка по категории продукти.
     *
     * @return Речник с хеш-таблица с проценти на надценка.
     */
    public Map<ProductCategory, Double> getMarkupPercentages() {
        return markupPercentages;
    }

    /**
     * Задава речник с хеш-таблица с процентите на надценка по категории продукти.
     *
     * @param markupPercentages Нов речник с хеш-таблица с проценти на надценка.
     */
    public void setMarkupPercentages(Map<ProductCategory, Double> markupPercentages) {
        this.markupPercentages = markupPercentages;
    }

    /**
     * Връща броя дни преди изтичане на срока на годност, за които се прилага отстъпка.
     *
     * @return Брой дни за отстъпка.
     */
    public int getDaysBeforeExpirationThreshold() {
        return daysBeforeExpirationThreshold;
    }

    /**
     * Задава броя дни преди изтичане на срока на годност, за които се прилага отстъпка.
     *
     * @param daysBeforeExpirationThreshold Нов брой дни за отстъпка.
     */
    public void setDaysBeforeExpirationThreshold(int daysBeforeExpirationThreshold) {
        this.daysBeforeExpirationThreshold = daysBeforeExpirationThreshold;
    }

    /**
     * Връща процента на отстъпка за продукти с наближаващ срок на годност.
     *
     * @return Процент на отстъпка.
     */
    public double getDiscountPercentNearExpiration() {
        return discountPercentNearExpiration;
    }

    /**
     * Задава процента на отстъпка за продукти с наближаващ срок на годност.
     *
     * @param discountPercentNearExpiration Нов процент на отстъпка.
     */
    public void setDiscountPercentNearExpiration(double discountPercentNearExpiration) {
        this.discountPercentNearExpiration = discountPercentNearExpiration;
    }

    /**
     * Изчислява общите разходи за заплати на касиерите в магазина.
     * @return Общата сума на заплатите.
     * @throws IllegalArgumentException ако касиер с дадено ID не е намерен при изчисляване на заплати.
     */
    public double calculateTotalSalariesExpense() {
        CashierService cashierService = ServiceFactory.getCashierService();
        double totalSalaries = 0;
        for (Integer cashierId : cashiersIds) {
            Optional<Cashier> cashierOpt = cashierService.findEntityById(cashierId);
            if (cashierOpt.isPresent()) {
                totalSalaries += cashierOpt.get().getSalary();
            } else {
                throw new IllegalArgumentException("Касиер с ID " + cashierId + " не е намерен при изчисляване на заплати.");
            }
        }
        return totalSalaries;
    }

    /**
     * Изчислява общите разходи за доставени стоки (продадени + налични).
     * @return Общата сума на покупните цени на доставените стоки.
     * @throws IllegalArgumentException ако продукт с дадено ID не е намерен при изчисляване на разходи за стоки.
     */
    public double calculateDeliveredGoodsExpense() {
        ProductService productService = ServiceFactory.getProductService();
        double totalCost = 0;

        // Разходи за стоки в наличност
        for (Map.Entry<Integer, Integer> entry : productsInStock.entrySet()) {
            Integer productId = entry.getKey();
            Integer quantity = entry.getValue();
            Optional<Product> productOpt = productService.findEntityById(productId);
            if (productOpt.isPresent()) {
                totalCost += productOpt.get().getUnitPurchasePrice() * quantity;
            } else {
                throw new IllegalArgumentException("Продукт с ID " + productId + " не е намерен при изчисляване на разходи за налични стоки.");
            }
        }

        // Разходи за продадени стоки
        for (Map.Entry<Integer, Integer> entry : productsSold.entrySet()) {
            Integer productId = entry.getKey();
            Integer quantity = entry.getValue();
            Optional<Product> productOpt = productService.findEntityById(productId);
            if (productOpt.isPresent()) {
                totalCost += productOpt.get().getUnitPurchasePrice() * quantity;
            } else {
                throw new IllegalArgumentException("Продукт с ID " + productId + " не е намерен при изчисляване на разходи за продадени стоки.");
            }
        }
        return totalCost;
    }

    /**
     * Изчислява общите приходи от продадени стоки на база издадените касови бележки.
     * @return Общата сума на приходите.
     * @throws IllegalStateException ако ReceiptService не е наличен.
     */
    public double calculateTotalIncome() {
        ReceiptService receiptService = ServiceFactory.getReceiptService();
        double totalIncome = 0;
        for (Integer receiptId : receiptsIds) {
            Optional<Receipt> receiptOpt = receiptService.findEntityById(receiptId);
            if (receiptOpt.isPresent()) {
                totalIncome += receiptOpt.get().getTotalPrice();
            } else {
                System.err.println("Касова бележка с ID " + receiptId + " не е намерена при изчисляване на приходи.");
            }
        }
        return totalIncome;
    }

    /**
     * Изчислява печалбата на магазина.
     * Печалба = Общи приходи - (Общи разходи за заплати + Общи разходи за доставени стоки)
     * @return Печалбата на магазина.
     */
    public double calculateProfit() {
        double totalIncome = calculateTotalIncome();
        double totalSalariesExpense = calculateTotalSalariesExpense();
        double totalGoodsExpense = calculateDeliveredGoodsExpense();
        return totalIncome - (totalSalariesExpense + totalGoodsExpense);
    }

    /**
     * Връща списък с наличните за продажба продукти в магазина (с валиден срок на годност и налични количества).
     *
     * @return Списък с налични продукти.
     */
    public ArrayList<Product> getAvailableProducts() {
        ArrayList<Product> availableProducts = new ArrayList<>();
        ProductService productService = ServiceFactory.getProductService();
        for (Map.Entry<Integer, Integer> entry : productsInStock.entrySet()) {
            int productId = entry.getKey();
            int stockQuantity = entry.getValue();

            if (stockQuantity > 0) {
                Optional<Product> productOpt = productService.findEntityById(productId);
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    if (!product.isProductExpired()) {
                        availableProducts.add(product);
                    }
                }
            }
        }
        return availableProducts;
    }

    /**
     * Връща списък с касовите апарати в магазина.
     *
     * @return Списък с касови апарати.
     * @throws IllegalArgumentException ако няма налични каси в системата или ако каса с даден касиер не съществува.
     */
    public ArrayList<CashDesk> getCashDesks() {
        ArrayList<CashDesk> cashDesks = new ArrayList<>();
        CashDeskService cashDeskService = ServiceFactory.getCashDeskService();

        if (cashDeskService.getAllEntities().isEmpty()) {
            throw new IllegalArgumentException("Няма налични каси в системата");
        }

        for (Integer cashierId : cashiersIds) {
            Predicate<CashDesk> filter = cashDesk -> cashDesk.getCashier() == cashierId;

            Optional<CashDesk> cashDeskOpt = cashDeskService.findEntityByFilter(filter);
            if (cashDeskOpt.isPresent()) {
                CashDesk cashDesk = cashDeskOpt.get();
                cashDesks.add(cashDesk);
            } else {
                throw new IllegalArgumentException("Каса с касиер с ID " + cashierId + " не съществува");
            }

        }

        return cashDesks;
    }


    /**
     * Задава наличността на продукт по неговото ID.
     *
     * @param id ID на продукта.
     * @param i  Количество наличност.
     * @throws IllegalArgumentException ако количеството е отрицателно.
     */
    public void setProductStock(int id, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Количеството на продукт с ID " + id + " не може да бъде отрицателно");
        }
        productsInStock.put(id, i);
    }
}
