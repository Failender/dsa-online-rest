package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.FavTalentEntity;
import de.failender.dsaonline.data.entity.FavTalentKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

public interface FavTalentRepository extends CrudRepository<FavTalentEntity, FavTalentKey> {

    List<FavTalentEntity> findByHeldid(BigInteger heldid);

    @Transactional
    @Modifying
    void deleteByHeldidAndName(BigInteger heldid, String name);
}
