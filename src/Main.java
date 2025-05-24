import dao.*;
import model.*;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        // Добавяне
        Product milk = new Product(1, "Мляко", 2.50, ProductCategory.FOOD);
        FileStorage.addObject(milk);

        List<Product> allProducts = FileStorage.getCollection(Product.class);

        System.out.println(allProducts.getFirst().getCategory() == ProductCategory.FOOD);



        // Запазете всички данни при изход
        Runtime.getRuntime().addShutdownHook(new Thread(FileStorage::saveAllData));
    }
}