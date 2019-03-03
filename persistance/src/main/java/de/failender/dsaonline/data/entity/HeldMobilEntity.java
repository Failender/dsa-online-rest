package de.failender.dsaonline.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "HELD_MOBIL")
public class HeldMobilEntity {

    @Id
    @Column(name="HELDID")
    private BigInteger heldid;
    @Column(name="ASP")
    private int asp;
    @Column(name="LEP")
    private int lep;

    public BigInteger getHeldid() {
        return heldid;
    }

    public void setHeldid(BigInteger heldid) {
        this.heldid = heldid;
    }

    public int getAsp() {
        return asp;
    }

    public void setAsp(int asp) {
        this.asp = asp;
    }

    public int getLep() {
        return lep;
    }

    public void setLep(int lep) {
        this.lep = lep;
    }
}
