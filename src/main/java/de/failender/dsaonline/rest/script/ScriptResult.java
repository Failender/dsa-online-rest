package de.failender.dsaonline.rest.script;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScriptResult {
	private String[] logs;
	private Object result;
}
