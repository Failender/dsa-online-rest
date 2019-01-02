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

	List<HeldEntity> findByGruppeId(int id);
	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.gruppe.id = ?1 WHERE h.id = ?2")
	void updateHeldenGruppe(Integer gruppeid, BigInteger heldid);

	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.isPublic = ?1 WHERE h.id = ?2")
	void updateHeldenPublic(boolean isPublic, BigInteger heldid);

	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.isActive = ?1 WHERE h.id = ?2")
	void updateHeldenActive(boolean isActive, BigInteger heldid);

	List<HeldEntity> findByGruppeId(Integer gruppeId);
	List<HeldEntity> findByGruppeIdAndIsActiveIsTrue(Integer gruppeId);


	@Query("SELECT h.id FROM HeldEntity h")
	List<BigInteger> getAllHeldenIds();

	HeldEntity findByNameAndGruppeId(String name, Integer gruppeId);

	List<HeldEntity> findByDeletedIsFalse();
}
