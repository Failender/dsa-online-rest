package de.failender.dsaonline.data.repository;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface HeldRepository extends JpaRepository<HeldEntity, BigInteger> {
	List<HeldEntity> findByUserIdAndActive(Integer userId, boolean active);

	Optional<HeldEntity> findFirstByIdOrderByVersion(BigInteger id);
}
