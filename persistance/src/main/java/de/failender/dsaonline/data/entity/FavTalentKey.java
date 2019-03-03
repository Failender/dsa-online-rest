package de.failender.dsaonline.data.entity;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigInteger;

public class FavTalentKey implements Serializable {
    @Column(name="TNAME")
    private String tname;
    @Column(name="HELDID")
    private BigInteger heldid;


    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public BigInteger getHeldid() {
        return heldid;
    }

    public void setHeldid(BigInteger heldid) {
        this.heldid = heldid;
    }
}
