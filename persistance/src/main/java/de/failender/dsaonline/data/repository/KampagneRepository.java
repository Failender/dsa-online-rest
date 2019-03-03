package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.KampagneEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KampagneRepository extends CrudRepository<KampagneEntity, Integer> {

	List<KampagneEntity> findByGruppeId(int gruppeId);
}
