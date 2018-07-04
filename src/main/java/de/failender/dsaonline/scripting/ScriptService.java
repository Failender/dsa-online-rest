package de.failender.dsaonline.scripting;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.data.entity.ScriptVariableEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.ScriptRepository;
import de.failender.dsaonline.rest.script.ScriptResult;
import de.failender.dsaonline.rest.script.TypeDto;
import de.failender.dsaonline.scripting.helper.ScriptHelper;
import de.failender.dsaonline.scripting.supplier.ScriptSupplier;
import de.failender.dsaonline.security.SecurityUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptService {


	private static final String SCRIPT_HEADER =
			"var logs = [];\r\nvar execute = function(params) {\r\nvar result = fun(params);\r\nreturn {result: result, logs: logs}\r\n}\r\nvar log = function(param) {\r\nlogs.push(param)\r\n}\r\n\r\nvar fun = function(params) {";
	private static final String SCRIPT_FOOTER = "};";


	@Autowired
	private ScriptRepository scriptRepository;
	private final Map<String, ScriptSupplier> scriptSuppliers = new HashMap<>();
	private final Map<String, ScriptHelper> scriptHelpers = new HashMap<>();


	public ScriptService(List<ScriptSupplier> scriptSuppliers, List<ScriptHelper> scriptHelpers) {

		for (ScriptSupplier scriptSupplier : scriptSuppliers) {
			this.scriptSuppliers.put(scriptSupplier.type(), scriptSupplier);
		}
		for (ScriptHelper scriptHelper : scriptHelpers) {
			this.scriptHelpers.put(scriptHelper.getName(), scriptHelper);
		}
	}

	public ScriptResult execute(ScriptEntity scriptEntity) {
		Map<String, Object> params = new HashMap<>();
		for (ScriptVariableEntity scriptVariable : scriptEntity.getScriptVariables()) {
			ScriptSupplier scriptSupplier = scriptSuppliers.get(scriptVariable.getType());
			Object value = scriptSupplier.supply(scriptVariable.getValue());
			params.put(scriptVariable.getName(), value);
		}
		String script = SCRIPT_HEADER + "\n";

		for (String s : scriptEntity.getScriptHelper()) {
			ScriptHelper scriptHelper = findScriptHelper(s);
			script += "var " + scriptHelper.getName() + " = Java.type('"+ scriptHelper.getClass().getCanonicalName() + "')\n";
		}
		script += scriptEntity.getBody() + "\n";
		script += SCRIPT_FOOTER;
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			scriptEngine.eval(script);
			Invocable invocable = (Invocable) scriptEngine;

				ScriptObjectMirror som = (ScriptObjectMirror) invocable.invokeFunction("execute", params);
				ScriptObjectMirror logs = (ScriptObjectMirror) som.get("logs");
				String[] logArr = logs.to(String[].class);
				return new ScriptResult(logArr, som.get("result"));

		} catch (ScriptException e) {
			return new ScriptResult(new String[]{e.getMessage()}, null);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPossibleValuesForType(String type) {
		return scriptSuppliers.get(type).getPossibleValues();
	}

	public List<TypeDto> getTypesWithValues() {
		Stream<TypeDto> s = scriptSuppliers.entrySet()
				.stream()
				.map(entry -> new TypeDto(entry.getKey(), entry.getValue().description(), entry.getValue().getPossibleValues()));
		return s.collect(Collectors.toList());
	}

	public ScriptHelper findScriptHelper(String name) {
		return scriptHelpers.get(name);
	}

	public Iterable<ScriptEntity> getAllScripts() {
		return this.scriptRepository.findAll();
	}

	public void saveScript(ScriptEntity scriptEntity) {
		UserEntity user = SecurityUtils.getCurrentUser();
		if(scriptEntity.getId() != null && scriptRepository.existsById(scriptEntity.getId())) {
			ScriptEntity db = scriptRepository.findById(scriptEntity.getId()).get();

			if(user.getId() != db.getOwner()) {
				throw new AccessDeniedException("Scripts can only be edited by their owner");
			}
		} else {
			scriptEntity.setOwner(user.getId());
		}

		if(scriptEntity.getId() == null) {
			List<ScriptVariableEntity> scriptVariables = scriptEntity.getScriptVariables();
			scriptEntity.setScriptVariables(Collections.EMPTY_LIST);
			scriptRepository.save(scriptEntity);
			scriptEntity.setScriptVariables(scriptVariables);
		}
		for (ScriptVariableEntity scriptVariableEntity : scriptEntity.getScriptVariables()) {
			scriptVariableEntity.setScriptId(scriptEntity.getId());
		}
		scriptRepository.save(scriptEntity);
	}

	public Collection<ScriptHelper> getScriptHelpers() {
		return scriptHelpers.values();
	}
}
