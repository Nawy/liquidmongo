package nl.exmg.liquidmongo.dbtest.migration;

import nl.exmg.liquidmongo.annotation.Migration;
import nl.exmg.liquidmongo.migration.MigrationStep;
import nl.exmg.liquidmongo.dbtest.model.AdminVersion1;
import nl.exmg.liquidmongo.dbtest.model.AdminVersion2;

@Migration(version = 2, order = 1)
public class Admin1to2Migration extends MigrationStep<AdminVersion1, AdminVersion2> {

    public Admin1to2Migration(int order) {
        super(order, "adminus", AdminVersion1.class, AdminVersion2.class);
    }

    @Override
    public AdminVersion2 migrateEntity(AdminVersion1 oldObject) {
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
    public AdminVersion1 rollbackEntity(AdminVersion2 newObject) {
        return new AdminVersion1(
                newObject.getId(),
                newObject.getFirstName() + " " + newObject.getLastName(),
                newObject.getEmail(),
                newObject.getLevel(),
                newObject.getCredentials()
        );
    }
}
