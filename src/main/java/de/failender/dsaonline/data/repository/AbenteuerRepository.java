package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.entity.abenteuer.AbenteuerEntity;
import de.failender.dsaonline.rest.dto.AbenteuerDto;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface AbenteuerRepository extends CrudRepository<AbenteuerEntity, Integer> {

	List<AbenteuerEntity> findByGruppeIdOrderByDatumDesc(Integer gruppeId);
	AbenteuerEntity findTopOneByGruppeIdOrderByDatumDesc(Integer gruppeId);

	boolean existsByGruppeIdAndName(int gruppeId, String name);
	boolean existsByGruppeIdAndNameAndKampagne(int gruppeId, String name, int kampagne);

	List<AbenteuerEntity> findByKampagneOrderByDatumDesc(int kampagneid);

	List<AbenteuerEntity> findByGruppeIdAndDatumBetween(int gruppe, int start, int end);


}
