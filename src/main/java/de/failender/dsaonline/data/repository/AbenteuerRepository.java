package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.abenteuer.AbenteuerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AbenteuerRepository extends CrudRepository<AbenteuerEntity, Integer> {

	List<AbenteuerEntity> findByGruppeId(Integer gruppeId);
}
