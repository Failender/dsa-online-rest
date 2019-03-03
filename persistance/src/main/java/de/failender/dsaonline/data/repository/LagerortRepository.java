package de.failender.dsaonline.data.repository;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import de.failender.dsaonline.data.entity.LagerortEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface LagerortRepository extends CrudRepository<LagerortEntity, Integer> {

    List<LagerortEntity> findByHeldid(BigInteger heldid);
    LagerortEntity findByNameAndHeldid(String name, BigInteger heldid);
    void deleteByHeldidAndGegenstandLagerorteNameAndGegenstandLagerorteAmount(BigInteger heldid, String gegenstand, int amount);
}
