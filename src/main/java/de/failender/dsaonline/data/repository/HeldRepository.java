package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.HeldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface HeldRepository extends JpaRepository<HeldEntity, BigInteger> {
	List<HeldEntity> findByUserId(Integer userId);
}
