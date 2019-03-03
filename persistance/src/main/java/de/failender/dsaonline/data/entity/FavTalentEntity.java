package de.failender.dsaonline.data.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="FAV_TALENT")
@IdClass(FavTalentEntity.class)
public class FavTalentEntity implements Serializable {

    @Id
    @Column(name="HELDID")
    private BigInteger heldid;

    @Id
    @Column(name="TNAME")
    private String name;

    public BigInteger getHeldid() {
        return heldid;
    }

    public void setHeldid(BigInteger heldid) {
        this.heldid = heldid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
