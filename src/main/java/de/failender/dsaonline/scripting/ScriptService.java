package de.failender.dsaonline.scripting;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.data.entity.ScriptVariable;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScriptService {

	private static final String SCRIPT_HEADER ="var fun = function(params) {";
	private static final String SCRIPT_FOOTER = "};";


	private final Map<String, ScriptSupplier> scriptSuppliers = new HashMap<>();

	public ScriptService(List<ScriptSupplier> scriptSuppliers) {
		for (ScriptSupplier scriptSupplier : scriptSuppliers) {
			this.scriptSuppliers.put(scriptSupplier.type(), scriptSupplier);
		}
	}

	public Object execute(ScriptEntity scriptEntity) {
		Map<String, Object> params = new HashMap<>();
		for (ScriptVariable scriptVariable : scriptEntity.getScriptVariables()) {
			ScriptSupplier scriptSupplier = scriptSuppliers.get(scriptVariable.getType());
			Object value = scriptSupplier.supply(scriptVariable.getValue());
			params.put(scriptVariable.getName(), value);
		}
		String script = SCRIPT_HEADER + "\n";
		script += scriptEntity.getBody() + "\n";
		script += SCRIPT_FOOTER;

		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			scriptEngine.eval(script);
			Invocable invocable = (Invocable) scriptEngine;
			return invocable.invokeFunction("fun", params);

		} catch (ScriptException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPossibleValuesForType(String type) {
		return scriptSuppliers.get(type).getPossibleValues();
	}

	public Map<String,List<String>> getTypesWithValues() {
		return scriptSuppliers.entrySet()
				.stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getPossibleValues()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
