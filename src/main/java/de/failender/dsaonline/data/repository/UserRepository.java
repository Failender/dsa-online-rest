package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	UserEntity findByName(String name);
	boolean existsByName(String name);

	@Query(nativeQuery = true, value = "SELECT RIGHTS.NAME FROM USERS U INNER JOIN ROLES_TO_USER RTU ON RTU.USER_ID = U.ID " +
			"INNER JOIN ROLES_TO_RIGHTS RTR ON RTR.ROLE_ID = RTU.ROLE_ID INNER JOIN RIGHTS RIGHTS ON RIGHTS.ID = RTR.RIGHT_ID WHERE U.ID = ?1")
	List<String> getUserRights(int userid);

	UserEntity findByToken(String token);

	@Transactional
	@Modifying
	@Query(nativeQuery =true, value="INSERT INTO ROLES_TO_USER VALUES (?1, ?2)")
	void addUserRole(int roleid, int userid);

	@Query(nativeQuery = true, value="SELECT R.ID FROM ROLES R WHERE R.NAME = ?1")
	Integer getRoleId(String role);



}
