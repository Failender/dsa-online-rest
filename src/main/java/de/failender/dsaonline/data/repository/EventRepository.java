package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface EventRepository extends CrudRepository<EventEntity, Integer> {

	@Query("SELECT e FROM EventEntity e WHERE e.ownerId = ?1 AND e.type = ?2 AND e.date BETWEEN ?3 AND ?4")
	List<EventEntity> findByOwnerAndTypeAndDateBetween(BigInteger owner, EventEntity.Type type, int start, int end);
}
