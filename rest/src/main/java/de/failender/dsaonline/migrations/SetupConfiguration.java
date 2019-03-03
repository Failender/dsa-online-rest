package de.failender.dsaonline.migrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SetupConfiguration {

    public static SetupConfiguration INSTANCE;

    {
        INSTANCE = this;
    }

    public static ApplicationContext context;

    private final String adminPassword;
    private final String defaultGroup;

    public SetupConfiguration(@Value("${dsa.gruppen.online.adminPassword}")String adminPassword,
                              @Value("${dsa.gruppen.online.defaultGroup}") String defaultGroup,
                              ApplicationContext ctx) {
        this.adminPassword = adminPassword;
        this.defaultGroup = defaultGroup;
        context = ctx;

    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}
