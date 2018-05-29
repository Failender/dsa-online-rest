package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.VersionEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface VersionRepository extends CrudRepository<VersionEntity, VersionEntity.VersionId> {

	List<VersionEntity> findByIdHeldid(BigInteger id);

	VersionEntity findFirstByIdHeldidOrderByIdVersionDesc(BigInteger id);
}
