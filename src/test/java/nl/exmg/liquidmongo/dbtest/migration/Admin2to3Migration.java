package nl.exmg.liquidmongo.dbtest.migration;

import nl.exmg.liquidmongo.migration.MigrationStep;
import nl.exmg.liquidmongo.dbtest.model.AdminVersion2;
import nl.exmg.liquidmongo.dbtest.model.AdminVersion3;
import nl.exmg.liquidmongo.dbtest.model.CredentialsNew;
import nl.exmg.liquidmongo.dbtest.model.CredentialsOld;

public class Admin2to3Migration extends MigrationStep<AdminVersion2, AdminVersion3> {

    private String defaultReferralLink = "referral_link_of_";

    public Admin2to3Migration(int order) {
        super(order, "admins", AdminVersion2.class, AdminVersion3.class);
    }

    @Override
    public AdminVersion3 migrateEntity(AdminVersion2 oldObject) {
        int level = Integer.parseInt(oldObject.getLevel());
        return new AdminVersion3(
                oldObject.getId(),
                oldObject.getFirstName(),
                oldObject.getLastName(),
                oldObject.getEmail(),
                level,
                this.toNewCredentials(
                        oldObject.getCredentials(),
                        level
                )
        );
    }

    @Override
    public AdminVersion2 rollbackEntity(AdminVersion3 newObject) {
        return new AdminVersion2(
                newObject.getId(),
                newObject.getFirstName(),
                newObject.getLastName(),
                newObject.getEmail(),
                String.valueOf(newObject.getLevel()),
                this.toOldCredentials(newObject.getCredentials())

        );
    }

    private CredentialsNew toNewCredentials(CredentialsOld oldCredentials, int level) {
        return new CredentialsNew(
                oldCredentials.getLogin(),
                oldCredentials.getPassword(),
                level > 2 ? (defaultReferralLink + oldCredentials.getLogin()) : null
        );
    }

    private CredentialsOld toOldCredentials(CredentialsNew newCredentials) {
        return new CredentialsOld(
                newCredentials.getLogin(),
                newCredentials.getPassword()
        );
    }
}
