// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.scripting.helper;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ScriptHelper {
	public abstract String getName();

	private List<MethodInformation> methods;

	{
		methods = Stream.of(this.getClass().getMethods()).filter(method -> Modifier.isStatic(method.getModifiers())).filter(method -> Modifier.isPublic(method.getModifiers())).map(method -> {
			DocumentedMethod methodDoc = method.getAnnotation(DocumentedMethod.class);
			if (methodDoc == null) {
				throw new IllegalArgumentException("Methods need to be documented " + method.getName());
			}
			List<MethodParameter> methodParameterList = Stream.of(method.getParameters()).map(parameter -> {
				DocumentedParameter parameterDoc = parameter.getAnnotation(DocumentedParameter.class);
				if (parameterDoc == null) {
					throw new IllegalArgumentException("Parameter need to be documented " + parameter.getName());
				}
				return new MethodParameter(parameterDoc.name(), parameter.getType(), parameterDoc.description());
			}).collect(Collectors.toList());
			return new MethodInformation(method.getName(), method.getReturnType(), methodDoc.returnDescription(), methodDoc.description(), methodParameterList);
		}).sorted(Comparator.comparing(MethodInformation::getName)).collect(Collectors.toList());
	}


	public List<MethodInformation> getMethods() {
		return this.methods;
	}
}