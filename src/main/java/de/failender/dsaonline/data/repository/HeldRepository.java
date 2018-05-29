package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.HeldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

public interface HeldRepository extends JpaRepository<HeldEntity, BigInteger> {
	List<HeldEntity> findByUserIdAndDeleted(Integer userId, boolean deleted);

	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.gruppe.id = ?1 WHERE h.id.id = ?2")
	void updateHeldenGruppe(Integer gruppeid, BigInteger heldid);

	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.public = ?1 WHERE h.id.id = ?2")
	void updateHeldenPublic(boolean isPublic, BigInteger heldid);


}
