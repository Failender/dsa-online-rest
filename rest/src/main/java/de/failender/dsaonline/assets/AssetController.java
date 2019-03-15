package de.failender.dsaonline.assets;

import de.failender.dsaonline.asset.AssetEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/assets")
public class AssetController {

	private final AssetService assetService;

	public AssetController(AssetService assetService) {
		this.assetService = assetService;
	}


	@GetMapping("kampagne/{kampagneid}")
	public List<AssetEntity> getAssetsByKampagne(@PathVariable int kampagneid) {
		return assetService.getAssetsByKampagne(kampagneid);
	}

	@PostMapping("kampagne/{kampagneid}")
	public void uploadAsset(@RequestParam("file") MultipartFile[] file, @PathVariable int kampagneid, @RequestHeader("name")String name, @RequestHeader("hidden")boolean hidden) {
		this.assetService.uploadAsset(file[0], kampagneid, name, hidden);
	}

	@GetMapping("{name}")
	public byte[] getImage(@PathVariable String name) {
		return assetService.getImage(name);
	}

	@DeleteMapping("{id}")
	public void deleteImage(@PathVariable Integer id) {
		assetService.deleteImage(id);
	}


}
