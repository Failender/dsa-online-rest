package de.failender.dsaonline.asset;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface AssetRepository extends CrudRepository<AssetEntity, Integer> {

	List<AssetEntity> findByKampagne(int kampagne);
	List<AssetEntity> findByKampagneAndHiddenFalse(int kampagne);
}
