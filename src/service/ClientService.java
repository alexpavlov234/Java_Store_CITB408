package service;

import dao.FileStorage;
import model.Client;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class ClientService implements DataService<Client, Integer> {
    @Override
    public Client createEntity(Client entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity; // Return the created entity
    }

    @Override
    public Client updateEntity(Client entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, c -> c.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Клиент с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public Optional<Client> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Client.class, integer);
    }

    @Override
    public ArrayList<Client> getAllEntities() {
        return FileStorage.getCollection(Client.class);
    }

    @Override
    public Optional<Client> findEntityByFilter(Predicate<Client> filter) {
        return FileStorage.getCollection(Client.class)
                .stream()
                .filter(filter)
                .findFirst();
    }

    @Override
    public ArrayList<Client> findEntitiesByFilter(Predicate<Client> filter) {
        return (ArrayList<Client>) FileStorage.getCollection(Client.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(Client entity) {

    }

    @Override
    public void validateEntity(Client entity) throws IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Клиентът не може да бъде null");
        }
        if (entity.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на клиент");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Името на клиент с ID " + entity.getId() + " не може да бъде null или празно");
        }
        if (entity.getBalance() < 0) {
            throw new IllegalArgumentException("Балансът на клиент с ID " + entity.getId() + " не може да бъде отрицателен");
        }
    }

    public Client registerClient(){
        Client client;
        System.out.print("Въведете вашето име: ");
        String name = System.console().readLine();
        System.out.print("Въведете вашия баланс: ");
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
        client = this.createEntity(new Client(name, balance));
        System.out.println("Регистрирахме ви успешно като клиент: " + client.getName() + " с баланс: " + client.getBalance() + " лв.");

        return client;
    }

    public Client loginClient() {
        ArrayList<Client> clients = this.getAllEntities();
        if (clients.isEmpty()) {
            System.out.println("Няма регистрирани клиенти. Моля, регистрирайте се първо.");
            registerClient();
        }
        System.out.println("Изберете клиент от списъка:");
        for (int i = 0; i < clients.size(); i++) {
            System.out.println((i + 1) + ". " + clients.get(i).getName());
        }
        int clientIndex = -1;
        while (clientIndex < 0 || clientIndex >= clients.size()) {
            System.out.print("Въведете номера на клиента: ");
            try {
                clientIndex = Integer.parseInt(System.console().readLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Невалиден номер. Моля, опитайте отново.");
            }
        }
        Client client = clients.get(clientIndex);
        System.out.println("Добре дошли, " + client.getName() + "!");

        return client;

    }
}
