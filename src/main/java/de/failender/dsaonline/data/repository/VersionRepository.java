package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.VersionEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface VersionRepository extends CrudRepository<VersionEntity, Integer> {

	List<VersionEntity> findByHeldidOrderByVersionDesc(BigInteger id);
	List<VersionEntity> findByHeldid(BigInteger id);
	Optional<VersionEntity> findByVersionAndHeldid(int version, BigInteger heldid);

	VersionEntity findFirstByHeldidOrderByVersionDesc(BigInteger id);
}
