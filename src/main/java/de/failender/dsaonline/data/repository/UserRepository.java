package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	UserEntity findByName(String name);
	boolean existsByName(String name);
}
