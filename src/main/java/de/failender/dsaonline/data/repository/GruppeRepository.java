package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.GruppeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface GruppeRepository extends JpaRepository<GruppeEntity, Integer> {

	GruppeEntity findByName(String name);

	@Query("SELECT g.id FROM GruppeEntity g")
	List<Integer> getAllGruppenIds();
}
