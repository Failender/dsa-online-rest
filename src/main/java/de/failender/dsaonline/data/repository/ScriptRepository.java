package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.ScriptEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScriptRepository extends CrudRepository<ScriptEntity, Integer> {

	public List<ScriptEntity> findAll();
}
