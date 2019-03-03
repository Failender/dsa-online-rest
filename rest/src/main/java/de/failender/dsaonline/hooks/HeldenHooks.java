package de.failender.dsaonline.hooks;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;

public interface HeldenHooks {

	void versionCreated(UserEntity userEntity, HeldEntity heldEntity, VersionEntity versionEntity);
	void heldCreated(UserEntity userEntity,VersionEntity versionEntity, HeldEntity heldEntity);
}
