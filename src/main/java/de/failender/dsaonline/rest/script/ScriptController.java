package de.failender.dsaonline.rest.script;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.scripting.ScriptService;
import de.failender.dsaonline.scripting.helper.ScriptHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

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

	@GetMapping("helper")
	public Collection<ScriptHelper> getScriptHelper() {
		return scriptService.getScriptHelpers();
	}

	@GetMapping("all")
	public Iterable<ScriptEntity> getAllScripts() {
		return this.scriptService.getAllScripts();
	}

	@PostMapping("save")
	public void save(@RequestBody  ScriptEntity scriptEntity) {
		scriptService.saveScript(scriptEntity);
	}


	@PostMapping("test")
	public ScriptResult test(@RequestBody ScriptEntity scriptEntity) {
		return scriptService.execute(scriptEntity);
	}

}
