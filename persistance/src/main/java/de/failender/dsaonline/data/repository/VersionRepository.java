package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.VersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VersionRepository extends JpaRepository<VersionEntity, Integer> {

	List<VersionEntity> findByHeldidOrderByVersionDesc(BigInteger id);
	Optional<VersionEntity> findByHeldidAndCreatedDate(BigInteger heldid, Date createdDate);
	List<VersionEntity> findByHeldid(BigInteger id);
	Optional<VersionEntity> findByVersionAndHeldid(int version, BigInteger heldid);

	VersionEntity findFirstByHeldidOrderByVersionDesc(BigInteger id);
	List<VersionEntity> findByVersionGreaterThanOrderByVersionAsc(int version);

	int countByHeldid(BigInteger heldid);
}
