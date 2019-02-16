package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.HeldMobilEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface HeldMobilRepository extends CrudRepository<HeldMobilEntity, BigInteger> {
}
