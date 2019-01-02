package de.failender.dsaonline.migrations;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V0_0_2__setup extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {

        SetupConfiguration configuration = SetupConfiguration.INSTANCE;

        context.getConnection().prepareStatement("INSERT INTO GRUPPEN VALUES(1, '" + configuration.getDefaultGroup() + "');").execute();
        context.getConnection().prepareStatement(
                "INSERT INTO USERS VALUES(1, 'Admin', '" + configuration.getAdminPassword()+  "', null,1, NOW());").execute();
    }
}
