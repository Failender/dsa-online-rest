package de.failender.dsaonline.rest.script;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.scripting.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/scripts")
public class ScriptController {

	@Autowired
	private ScriptService scriptService;

	@GetMapping("values/{type}")
	public List<String> getPossibleValuesForType(@PathVariable String type) {
		return scriptService.getPossibleValuesForType(type);
	}

	@GetMapping("types")
	public List<TypeDto> getTypesWithValues() {
		return scriptService.getTypesWithValues();
	}

	@GetMapping("all")
	public Iterable<ScriptEntity> getAllScripts() {
		return this.scriptService.getAllScripts();
	}

	@PostMapping("save")
	public void save(ScriptEntity scriptEntity) {
		scriptService.saveScript(scriptEntity);
	}


}
