package de.failender.dsaonline.scripting;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.data.entity.ScriptVariable;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
