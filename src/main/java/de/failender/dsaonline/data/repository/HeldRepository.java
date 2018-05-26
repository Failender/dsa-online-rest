package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.HeldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface HeldRepository extends JpaRepository<HeldEntity, BigInteger> {
	List<HeldEntity> findByUserIdAndActive(Integer userId, boolean active);
	List<HeldEntity> findByIdId(BigInteger id);
	Optional<HeldEntity> findFirstByIdIdOrderByIdVersionDesc(BigInteger id);
	Optional<HeldEntity> findByIdIdAndIdVersion(BigInteger id, int version);

	@Transactional
	@Modifying
	@Query("UPDATE HeldEntity h SET h.gruppe.id = ?1 WHERE h.id.id = ?2")
	void updateHeldenGruppe(Integer gruppeid, BigInteger heldid);
}
