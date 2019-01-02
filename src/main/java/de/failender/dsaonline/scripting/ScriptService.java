package de.failender.dsaonline.scripting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.data.entity.ScriptVariableEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.ScriptRepository;
import de.failender.dsaonline.exceptions.EntityNotFoundException;
import de.failender.dsaonline.rest.dto.ScriptResult;
import de.failender.dsaonline.rest.dto.TypeDto;
import de.failender.dsaonline.scripting.helper.MethodInformation;
import de.failender.dsaonline.scripting.helper.ScriptHelper;
import de.failender.dsaonline.scripting.helper.ScriptHelperInformation;
import de.failender.dsaonline.scripting.supplier.ScriptSupplier;
import de.failender.dsaonline.security.SecurityUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptService {


	private static final String SCRIPT_HEADER = readFrom("scripting/header.js");
	private static final String SCRIPT_FOOTER = readFrom("scripting/footer.js");

	@Value("${dsa.online.scripts.directory}")
	private String directory;


	private static final String readFrom(String resourceName) {
		try {
			return IOUtils.toString(ScriptService.class.getClassLoader().getResourceAsStream(resourceName),  "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Autowired
	private ScriptRepository scriptRepository;

	@Autowired
	private ObjectMapper objectMapper;
	private final Map<String, ScriptSupplier> scriptSuppliers = new HashMap<>();
	private final Map<String, ScriptHelper> scriptHelpers = new HashMap<>();

	private static ScriptObjectMirror json;
	{
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			json = (ScriptObjectMirror) scriptEngine.eval("JSON");
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
	public static String stringify(ScriptObjectMirror data) {
		return (String) json.callMember("stringify", data);
	}

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
			script += "var " + scriptHelper.getName() + " = Java.type('" + scriptHelper.getClass().getCanonicalName() + "')\n";
		}
		script += scriptEntity.getBody() + "\n";
		script += SCRIPT_FOOTER;
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			scriptEngine.eval(script);
			Invocable invocable = (Invocable) scriptEngine;
			ScriptObjectMirror json = (ScriptObjectMirror) scriptEngine.eval("JSON");
			ScriptObjectMirror som = (ScriptObjectMirror) invocable.invokeFunction("execute", params);
			ScriptObjectMirror logs = (ScriptObjectMirror) som.get("logs");
			String logString = stringify(logs);
			new TypeReference<String>(){};
			ScriptLog[] log = objectMapper.readValue(logString, new TypeReference<ScriptLog[]>(){});


			return new ScriptResult(log, som.get("result"));

		} catch (Exception e) {
			return new ScriptResult(new ScriptLog[]{new ScriptLog("error", e.getMessage())}, null);
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

	public List<ScriptEntity> getAllScripts() {
		return this.scriptRepository.findAll()
				.stream()
				.map(entity -> {
					File file = new File(directory, entity.getId().toString() + ".js");
					try {
						String body = FileUtils.readFileToString(file, "UTF-8");
						entity.setBody(body);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return entity;
				}).collect(Collectors.toList());
	}

	public int saveScript(ScriptEntity scriptEntity) {
		SecurityUtils.hasRight(SecurityUtils.EDIT_SCRIPTS);
		UserEntity user = SecurityUtils.getCurrentUser();
		if (scriptEntity.getId() != null && scriptRepository.existsById(scriptEntity.getId())) {
			ScriptEntity db = scriptRepository.findById(scriptEntity.getId()).get();

			if (user.getId() != db.getOwner() && SecurityUtils.hasRight(SecurityUtils.EDIT_ALL)) {
				throw new AccessDeniedException("Scripts can only be edited by their owner");
			}
		} else {
			scriptEntity.setOwner(user.getId());
		}

		if (scriptEntity.getId() == null) {
			List<ScriptVariableEntity> scriptVariables = scriptEntity.getScriptVariables();
			scriptEntity.setScriptVariables(Collections.EMPTY_LIST);
			scriptRepository.save(scriptEntity);
			scriptEntity.setScriptVariables(scriptVariables);
		}
		for (ScriptVariableEntity scriptVariableEntity : scriptEntity.getScriptVariables()) {
			scriptVariableEntity.setScriptId(scriptEntity.getId());
		}

		scriptEntity.setId(scriptRepository.save(scriptEntity).getId());

		try {

			FileUtils.writeStringToFile(getFileForEntity(scriptEntity), scriptEntity.getBody(), "UTF-8");
			return scriptEntity.getId();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private File getFileForEntity(ScriptEntity entity) {
		return new File(directory, entity.getId() + ".js");
	}

	public Collection<ScriptHelper> getScriptHelpers() {
		return scriptHelpers.values();
	}

	public List<MethodInformation> getScriptHelperMethods(String name) {
		return scriptHelpers.get(name).getMethods();
	}

	public ScriptResult execute(int id) {
		ScriptEntity entity = scriptRepository.findById(id).get();
		try {
			entity.setBody(IOUtils.toString(new FileInputStream(getFileForEntity(entity)), "UTF-8"));
		} catch (IOException e) {
			throw new EntityNotFoundException();
		}
		return execute(entity);
	}

	public List<ScriptHelperInformation> getScriptHelperInformation() {
		return scriptHelpers.values()
				.stream()
				.map(helper ->
					new ScriptHelperInformation(helper.getName(), getScriptHelperMethods(helper.getName())))
				.collect(Collectors.toList());
	}
}
