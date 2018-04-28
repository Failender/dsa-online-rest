package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.GruppeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GruppeRepository extends JpaRepository<GruppeEntity, Integer> {

	GruppeEntity findByName(String name);
}
