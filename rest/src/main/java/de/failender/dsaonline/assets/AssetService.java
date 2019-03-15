package de.failender.dsaonline.assets;

import de.failender.dsaonline.asset.AssetEntity;
import de.failender.dsaonline.asset.AssetRepositoryService;
import de.failender.dsaonline.exceptions.EntityNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class AssetService {

	private final AssetRepositoryService assetRepositoryService;

	private final File cacheRoot;
	private final File assetRoot;
	public AssetService(AssetRepositoryService assetRepositoryService, @Value("${dsa.online.cache.directory}") String cacheRoot) {
		this.assetRepositoryService = assetRepositoryService;
		this.cacheRoot = new File(cacheRoot);
		if(!this.cacheRoot.exists()) {
			this.cacheRoot.mkdirs();
		}
		this.assetRoot = new File(this.cacheRoot, "assets");
		if(!this.assetRoot.exists()) {
			this.assetRoot.mkdir();
		}


	}


	public List<AssetEntity> getAssetsByKampagne(@PathVariable int kampagneid) {
		return assetRepositoryService.getAssetsForKampagne(kampagneid);
	}


	public void uploadAsset(MultipartFile file, int kampagneid, String name, boolean hidden) {

		AssetEntity assetEntity = new AssetEntity();
		assetEntity.setKampagne(kampagneid);
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		assetEntity.setName(name + "." + extension);
		assetEntity.setHidden(hidden);
		assetRepositoryService.save(assetEntity);


		File cacheFile = new File(assetRoot, assetEntity.getFilename());
		try {
			IOUtils.copy(file.getInputStream(), new FileOutputStream(cacheFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(file);
	}

	public byte[] getImage(String name) {
		try {
			return IOUtils.toByteArray(new FileInputStream(getFile(name)));
		} catch (IOException e) {
			throw new EntityNotFoundException();
		}
	}

	public void deleteImage(Integer id) {
		AssetEntity assetEntity = assetRepositoryService.getImage(id);
		getFile(assetEntity.getFilename()).delete();
		assetRepositoryService.delete(assetEntity);
	}

	private File getFile(String filename) {
		return new File(assetRoot, filename);
	}
}
