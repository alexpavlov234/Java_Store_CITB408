package service;

import dao.FileStorage;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class StoreService implements DataService<Store, Integer> {

    @Override
    public Store createEntity(Store entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    @Override
    public Store updateEntity(Store entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, s -> s.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Магазин с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public Optional<Store> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Store.class, integer);
    }

    @Override
    public ArrayList<Store> getAllEntities() {
        return FileStorage.getCollection(Store.class);
    }

    @Override
    public Optional<Store> findEntityByFilter(Predicate<Store> filter) {
        return FileStorage.getCollection(Store.class)
                .stream()
                .filter(filter)
                .findFirst();
    }

    @Override
    public ArrayList<Store> findEntitiesByFilter(Predicate<Store> filter) {
        return (ArrayList<Store>) FileStorage.getCollection(Store.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void validateEntity(Store entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Магазинът не може да бъде null");
        }
        if (entity.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на магазина");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Името на магазин с ID " + entity.getId() + " не може да бъде null или празно");
        }
    }

    public Store selectStore(){
        ArrayList<Store> stores = getAllEntities();
        if (stores.isEmpty()) {
            throw new IllegalStateException("Няма налични магазини");
        }

        System.out.println("Изберете магазин, в който искате да пазарувате:");
        for (int i = 0; i < stores.size(); i++) {
            System.out.println((i + 1) + ". " + stores.get(i).getName());
        }

        int storeIndex = -1;
        while (storeIndex < 0 || storeIndex >= stores.size()) {
            System.out.print("Въведете номера на магазина: ");
            try {
                storeIndex = Integer.parseInt(System.console().readLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Невалиден номер. Моля, опитайте отново.");
            }
        }
        Store selectedStore = stores.get(storeIndex);
        System.out.println("Избрахте магазин: " + selectedStore.getName());
        return selectedStore;
    }

    public void updatePricesForAllStores() {
        ProductService productService = ServiceFactory.getProductService();
        ArrayList<Store> stores = getAllEntities();
        if (stores.isEmpty()) {
            throw new RuntimeException("Няма заредени магазини в системата!");
        }

        for (Store store : stores) {
            for (Map.Entry<Integer, Integer> entry : store.getProductsInStock().entrySet()) {
                int productId = entry.getKey();
                Optional<Product> productOpt = productService.findEntityById(productId);
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    if (!product.isProductExpired()) {
                        double finalPrice = store.getProductFinalPrice(product);
                        product.setUnitSalePrice(finalPrice);
                        productService.updateEntity(product);
                    }
                }
            }
        }
        System.out.println("Цените са актуализирани успешно за всички магазини.");
    }

    public void makePurchase(Store store, Client client) {

        CashierService cashierService = ServiceFactory.getCashierService();
        ClientService clientService = ServiceFactory.getClientService();
        ReceiptService receiptService = ServiceFactory.getReceiptService();

        if (store == null) {
            throw new IllegalArgumentException("Магазинът не може да бъде null");
        }
        if (client == null) {
            throw new IllegalArgumentException("Клиентът не може да бъде null");
        }

        // Проверка дали клиентът има достатъчно баланс
        if (client.getBalance() <= 0) {
            System.out.println("Вашият баланс е нулев. Моля, заредете сметката си, за да пазарувате.");
            System.out.println("Въведете баланс: ");
            double balance = -1;
            while (balance < 0) {
                try {
                    balance = Double.parseDouble(System.console().readLine());
                    if (balance < 0) {
                        System.out.println("Балансът трябва да бъде положително число. Моля, опитайте отново.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Невалиден баланс. Моля, въведете валидно число.");
                }
            }
            client.setBalance(balance);
            clientService.updateEntity(client);
        }

        // Избор на продукти
        System.out.println("Сега можете да изберете продукти за покупка.");
        System.out.println("Вашият текущ баланс е: " + client.getBalance() + " лв.");
        System.out.println("Налични продукти в магазина:");
        ArrayList<Product> products = store.getAvailableProducts();
        if (products.isEmpty()) {
            throw new RuntimeException("Няма налични продукти в магазина " + store.getName() + ". Моля, опитайте по-късно.");
        }

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("%d. %s - %.2f лв. (Категория: %s, Срок на годност: %s, Количество: %s)\n",
                    (i + 1), product.getName(), product.getUnitSalePrice(),
                    product.getCategory(), product.getExpirationDate(), store.getProductStock(product.getId()));
        }

        System.out.println("За да спрете избора на продукти, въведете 'stop'. Когато избирате продукт въведете неговия номер и количеството, което искате да закупите.");
        System.out.println("Формат: [номер на продукта] [количество]; Пример: 1 2 (за закупуване на 2 броя от продукт с номер 1)");

        Map<Product, Integer> selectedProducts = new HashMap<>();
        while (true) {
            System.out.print("Въведете продукт и количество: ");
            String input = System.console().readLine();
            if (input.equalsIgnoreCase("stop")) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Невалиден формат. Моля, опитайте отново.");
                continue;
            }

            int productIndex = -1;
            int quantity = -1;

            try {
                productIndex = Integer.parseInt(parts[0]) - 1;
                quantity = Integer.parseInt(parts[1]);

                if (productIndex < 0 || productIndex >= products.size() || quantity <= 0) {
                    System.out.println("Невалиден избор. Моля, опитайте отново.");
                    continue;
                }

                if (store.getProductStock(products.get(productIndex).getId()) < quantity) {
                    throw new IllegalArgumentException("Недостатъчно количество от продукта " + products.get(productIndex).getName() + ". Налични са само " + store.getProductStock(products.get(productIndex).getId()) + " броя.");
                }

                Product selectedProduct = products.get(productIndex);
                selectedProducts.put(selectedProduct, quantity);
            } catch (NumberFormatException e) {
                System.out.println("Невалидно число. Моля, опитайте отново.");
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("Не сте избрали никакви продукти.");
            return;
        }

        // Изберете си каса
        System.out.println("Изберете каса за плащане:");
        ArrayList<CashDesk> cashDesks = store.getCashDesks();

        if (cashDesks.isEmpty()) {
            throw new RuntimeException("Няма налични каси в магазин " + store.getName() + ". Моля, опитайте по-късно.");
        }

        for (int i = 0; i < cashDesks.size(); i++) {
            Optional<Cashier> cashierOpt = cashierService.findEntityById(cashDesks.get(i).getCashier());
            if (cashierOpt.isPresent()) {
                System.out.println((i + 1) + ". Каса № " + (i + 1) + " с касиер " + cashierOpt.get().getName());
            }
        }

        int cashDeskIndex = -1;
        while (cashDeskIndex < 0 || cashDeskIndex >= cashDesks.size()) {
            System.out.print("Въведете номера на касата: ");
            try {
                cashDeskIndex = Integer.parseInt(System.console().readLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Невалиден номер. Моля, опитайте отново.");
            }
        }

        CashDesk selectedCashDesk = cashDesks.get(cashDeskIndex);
        Cashier selectedCashier = cashierService.findEntityById(selectedCashDesk.getCashier())
                .orElseThrow(() -> new IllegalArgumentException("Касиерът с ID " + selectedCashDesk.getCashier() + " не съществува"));

        System.out.println("Избрахте каса: " + selectedCashDesk.getId() + " с касиер " + selectedCashier.getName());

        // Маркиране на продуктите - анимация с изчакване - 3 секунди с принтиране на точки
        // За по-реалистично изживяване
        System.out.print("Маркиране на продуктите");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Грешка при изчакване: " + e.getMessage());
            }
        }

        System.out.println("\nПродуктите са маркирани успешно!");

        System.out.println("Вашите избрани продукти:");

        // Изчисляване на общата цена
        double totalPrice = 0.0;
        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double unitPrice = product.getUnitSalePrice();
            totalPrice += unitPrice * quantity;

            System.out.printf("Продукт: %s, Количество: %d, Единична цена: %.2f лв., Обща цена: %.2f лв.\n",
                    product.getName(), quantity, unitPrice, unitPrice * quantity);
        }

        // Проверка дали клиентът има достатъчно баланс
        if (client.getBalance() < totalPrice) {
            throw new IllegalArgumentException("Недостатъчен баланс. Вашият баланс е " + client.getBalance() + " лв., а общата цена е " + totalPrice + " лв.");
        }

        // Плащане
        System.out.println("Искате ли да платите общата сума от " + totalPrice + " лв.? (y/n)");
        String paymentChoice = System.console().readLine().trim().toLowerCase();
        if (!paymentChoice.equals("y")) {
            System.out.println("Плащането е отменено. Благодарим ви, че пазарувахте при нас!");
            return;
        }

        System.out.println("Плащане на общата сума от " + totalPrice + " лв. на каса " + selectedCashDesk.getId() + " с касиер " + selectedCashier.getName());
        client.setBalance(client.getBalance() - totalPrice);
        clientService.updateEntity(client);
        System.out.println("Плащането е успешно!");
        // Създаване на касовата бележка
        Receipt receipt = new Receipt(client.getId(),selectedCashier.getId(),java.time.LocalDateTime.now(),totalPrice,selectedProducts);

        // Актуализиране на наличностите в магазина
        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            int currentStock = store.getProductStock(product.getId());
            store.setProductStock(product.getId(), currentStock - quantity);
        }
        this.updateEntity(store);

        System.out.printf("Вашият нов баланс е: %.2f лв.\n", client.getBalance());

        // Записване на касовата бележка
        receipt = receiptService.createEntity(receipt);
        System.out.println("Вашата разписка е създадена успешно!");
        System.out.println("Можете да я откриете на следния адрес: " + FileStorage.getFilePathForObject(receipt));
    }
}
