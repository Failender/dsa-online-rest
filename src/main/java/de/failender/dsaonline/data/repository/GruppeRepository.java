package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GruppeRepository extends JpaRepository<GruppeEntity, Integer> {

	GruppeEntity findByName(String name);

	@Query("SELECT g.id FROM GruppeEntity g")
	List<Integer> getAllGruppenIds();

	boolean existsByName(String name);

	@Query("SELECT g.meister FROM GruppeEntity  g WHERE g.id = ?1")
	List<UserEntity> findMeisterForGruppe(int gruppe);
}
