package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.rest.dto.DropdownData;
import de.failender.dsaonline.rest.dto.ScriptResult;
import de.failender.dsaonline.rest.dto.TypeDto;
import de.failender.dsaonline.scripting.ResultType;
import de.failender.dsaonline.scripting.ScriptService;
import de.failender.dsaonline.scripting.helper.MethodInformation;
import de.failender.dsaonline.scripting.helper.ScriptHelper;
import de.failender.dsaonline.scripting.helper.ScriptHelperInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	@GetMapping("helper/names")
	public List<String> getScriptHelperNames() {
		return scriptService.getScriptHelpers()
				.stream()
				.map(ScriptHelper::getName)
				.collect(Collectors.toList());
	}

	@GetMapping("helper/information")
	public List<ScriptHelperInformation> getScriptHelperInformations() {
		return scriptService.getScriptHelperInformation();
	}

	@GetMapping("result/types")
	public List<DropdownData> getResultTypesAsDropdown() {
		List<DropdownData> data = new ArrayList<>(ResultType.values().length);
		for (int i = 0; i < ResultType.values().length; i++) {
			ResultType type = ResultType.values()[i];
			data.add(new DropdownData(type.toString(), type.toString()));
		}
		return data;
	}

	@GetMapping("helper/{name}/methods/")
	public List<MethodInformation> getScriptHelperMethods(@PathVariable String name) {
		return scriptService.getScriptHelperMethods(name);
	}

	@GetMapping("all")
	public Iterable<ScriptEntity> getAllScripts() {
		return this.scriptService.getAllScripts();
	}

	@PostMapping("save")
	public int save(@RequestBody  ScriptEntity scriptEntity) {
		return scriptService.saveScript(scriptEntity);
	}


	@PostMapping("test")
	public ScriptResult test(@RequestBody ScriptEntity scriptEntity) {
		return scriptService.execute(scriptEntity);
	}

	@GetMapping("execute/{id}")
	public ScriptResult execute(@PathVariable int id) {
		return scriptService.execute(id);
	}


}
