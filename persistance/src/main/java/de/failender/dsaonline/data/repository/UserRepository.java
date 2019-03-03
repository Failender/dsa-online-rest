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
			"INNER JOIN ROLES_TO_RIGHTS RTR ON RTR.ROLE_ID = RTU.ROLE_ID INNER JOIN RIGHTS ON RIGHTS.ID = RTR.RIGHT_ID WHERE U.ID = ?1")
	List<String> getUserRights(int userid);

	@Query(nativeQuery = true, value="SELECT ROLES.NAME FROM USERS U INNER JOIN ROLES_TO_USER RTU ON RTU.USER_ID = U.ID " +
			" INNER JOIN ROLES ON ROLES.ID = RTU.ROLE_ID WHERE U.ID = ?1")
	List<String> findRoleNamesForUser(int userid);

	UserEntity findByToken(String token);

	@Transactional
	@Modifying
	@Query(nativeQuery =true, value="INSERT INTO ROLES_TO_USER VALUES (?1, ?2)")
	void addUserRole(int roleid, int userid);

	@Query(nativeQuery = true, value="SELECT R.ID FROM ROLES R WHERE R.NAME = ?1")
	Integer getRoleId(String role);

	@Query(nativeQuery = true, value="SELECT GRUPPE_ID FROM USER_TO_MEISTER WHERE USER_ID = ?1")
	List<Integer> getMeisterGruppen(int userid);

	@Query(nativeQuery = true, value ="SELECT GRUPPEN.NAME FROM USER_TO_MEISTER INNER JOIN GRUPPEN ON USER_TO_MEISTER.GRUPPE_ID = GRUPPEN.ID WHERE USER_TO_MEISTER.USER_ID = ?1")
	List<String> getMeisterGruppenNames(int userid);


	@Transactional
	@Modifying
	@Query(nativeQuery = true, value="INSERT INTO USER_TO_MEISTER VALUES (?1, ?2)")
	void addMeisterForGruppe(int userid, int gruppeid);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value="DELETE FROM USER_TO_MEISTER WHERE USER_ID =?1 AND GRUPPE_ID = ?2")
	void removeMeisterForGruppe(int userid, int gruppeid);





}
