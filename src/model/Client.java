package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Представлява клиент на магазин.
 */
public class Client implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double balance;

    /**
     * Конструктор за създаване на клиент с име и баланс. ID-то се генерира автоматично.
     *
     * @param name    Име на клиента.
     * @param balance Баланс на клиента.
     */
    public Client(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    /**
     * Връща ID на клиента.
     *
     * @return ID на клиента.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на клиента.
     *
     * @param id Ново ID на клиента.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща името на клиента.
     *
     * @return Име на клиента.
     */
    public String getName() {
        return name;
    }

    /**
     * Задава името на клиента.
     *
     * @param name Ново име на клиента.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Връща баланса на клиента.
     *
     * @return Баланс на клиента.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Задава баланса на клиента.
     *
     * @param balance Нов баланс на клиента.
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

}
