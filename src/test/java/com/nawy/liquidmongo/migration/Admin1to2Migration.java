package com.nawy.liquidmongo.migration;

import com.nawy.liquidmongo.model.AdminVersion1;
import com.nawy.liquidmongo.model.AdminVersion2;

public class Admin1to2Migration extends MigrationStep<AdminVersion1, AdminVersion2> {

    public Admin1to2Migration(int order) {
        super(order, "adminus", "admins", AdminVersion1.class, AdminVersion2.class);
    }

    @Override
    AdminVersion2 migrateEntity(AdminVersion1 oldObject) {
        String[] names = oldObject.getName().split(" ");

        return new AdminVersion2(
                oldObject.getId(),
                names[0],
                names[1],
                oldObject.getEmail(),
                oldObject.getLevelName(),
                oldObject.getCredentials()
        );
    }

    @Override
    AdminVersion1 rollbackEntity(AdminVersion2 newObject) {
        return new AdminVersion1(
                newObject.getId(),
                newObject.getFirstName() + " " + newObject.getLastName(),
                newObject.getEmail(),
                newObject.getLevel(),
                newObject.getCredentials()
        );
    }
}
