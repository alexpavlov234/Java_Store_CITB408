package service;

import dao.FileStorage;
import model.Cashier;
import model.Product;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CashierService implements DataService<Cashier, Integer> {


    @Override
    public Cashier createEntity(Cashier entity) {
        validateCashier(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    @Override
    public Cashier updateEntity(Cashier entity) {
        validateCashier(entity);

        boolean updated = FileStorage.updateObject(
                entity, c -> c.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Касиер с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public boolean deleteEntity(Integer integer) {
        boolean deleted = FileStorage.removeObject(
                Cashier.class, c -> c.getId() == integer);

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Касиер с ID " + integer + " не съществува");
        } else {
            return true;
        }
    }

    @Override
    public Optional<Cashier> findEntityById(Integer integer) {
        return FileStorage.findObject(Cashier.class, c -> c.getId() == integer);
    }

    @Override
    public List<Cashier> findAllEntities() {
        return FileStorage.getCollection(Cashier.class);
    }

    @Override
    public List<Cashier> findEntityByFilter(Predicate<Cashier> filter) {
        return FileStorage.getCollection(Cashier.class)
                .stream()
                .filter(filter)
                .toList();
    }

    // Допълнителни специфични методи за касиери могат да бъдат добавени тук

    /**
     * Изчислява сумата на заплатите на всички касиери
     * @return Сумата на заплатите на всички касиери
     */
    public double calculateTotalSalaries() {
        return findAllEntities().stream()
                .mapToDouble(Cashier::getSalary)
                .sum();
    }

    @Override
    public void printEntity(Cashier entity) {
        if (entity == null) {
            System.out.println("Касиерът е null");
            return;
        }
        System.out.println("Касиер: " + entity.getName() + ", Заплата: " + entity.getSalary());
    }

    private void validateCashier(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Грешка: Касиерът не може да бъде null");

        }

        if (cashier.getName() == null || cashier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Грешка: Името на касиер с ID " + cashier.getId() + " не може да бъде празно");
        }

        if (cashier.getSalary() < 0) {
            throw new IllegalArgumentException("Грешка: Заплатата на касиер с ID " + cashier.getId() + " не може да бъде отрицателна");
        }

    }
}
