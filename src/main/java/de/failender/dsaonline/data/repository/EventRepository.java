package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.EventEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface EventRepository extends CrudRepository<EventEntity, Integer> {
	List<EventEntity> findByOwnerIdAndTypeAndStartMonat(BigInteger ownerId, EventEntity.Type type, int startMonat);
	List<EventEntity> findByOwnerIdAndTypeAndEndMonat(BigInteger ownerId, EventEntity.Type type, int endMonat);
}
