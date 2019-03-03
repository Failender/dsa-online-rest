package de.failender.dsaonline.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="GEGENSTAND_TO_LAGERORT")
public class GegenstandLagerort extends BaseEntity{

    @Column(name="LAGERORT")
    private int lagerort;
    @Column(name="NAME")
    private String name;
    @Column(name="AMOUNT")
    private int amount;

    public int getLagerort() {
        return lagerort;
    }

    public void setLagerort(int lagerort) {
        this.lagerort = lagerort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
