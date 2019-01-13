package de.failender.dsaonline.data.entity;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name="LAGERORT")
public class LagerortEntity extends BaseEntity{

    @Column(name="NAME")
    private String name;
    @Column(name="HELDID")
    private BigInteger heldid;
    @Column(name="NOTIZ")
    private String notiz;

    @JoinColumn(name = "LAGERORT")
    @OneToMany(cascade = CascadeType.ALL)
    private List<GegenstandLagerort> gegenstandLagerorte;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getHeldid() {
        return heldid;
    }

    public void setHeldid(BigInteger heldid) {
        this.heldid = heldid;
    }

    public String getNotiz() {
        return notiz;
    }

    public void setNotiz(String notiz) {
        this.notiz = notiz;
    }

    public List<GegenstandLagerort> getGegenstandLagerorte() {
        return gegenstandLagerorte;
    }

    public void setGegenstandLagerorte(List<GegenstandLagerort> gegenstandLagerorte) {
        this.gegenstandLagerorte = gegenstandLagerorte;
    }
}
