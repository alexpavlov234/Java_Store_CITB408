package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Представлява касиер в магазин.
 */
public class Cashier implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double salary;

    /**
     * Конструктор за създаване на касиер с име и заплата. ID-то се генерира автоматично.
     *
     * @param name   Име на касиера.
     * @param salary Заплата на касиера.
     */
    public Cashier(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    /**
     * Връща ID на касиера.
     *
     * @return ID на касиера.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на касиера.
     *
     * @param id Ново ID на касиера.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща името на касиера.
     *
     * @return Име на касиера.
     */
    public String getName() {
        return name;
    }

    /**
     * Задава името на касиера.
     *
     * @param name Ново име на касиера.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Връща заплатата на касиера.
     *
     * @return Заплата на касиера.
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Задава заплатата на касиера.
     *
     * @param salary Нова заплата на касиера.
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }
}
