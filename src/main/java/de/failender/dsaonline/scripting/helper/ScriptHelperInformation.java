package de.failender.dsaonline.scripting.helper;

import java.util.List;

public class ScriptHelperInformation {
    private String helperName;
    private List<MethodInformation> methodInformation;

    public ScriptHelperInformation(String helperName, List<MethodInformation> methodInformation) {
        this.helperName = helperName;
        this.methodInformation = methodInformation;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public List<MethodInformation> getMethodInformation() {
        return methodInformation;
    }

    public void setMethodInformation(List<MethodInformation> methodInformation) {
        this.methodInformation = methodInformation;
    }
}
