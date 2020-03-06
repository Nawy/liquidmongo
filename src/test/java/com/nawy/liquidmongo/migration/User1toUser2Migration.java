package com.nawy.liquidmongo.migration;

import com.nawy.liquidmongo.model.UserVersion1;
import com.nawy.liquidmongo.model.UserVersion2;

public class User1toUser2Migration extends MigrationStep<UserVersion1, UserVersion2> {


    public User1toUser2Migration(int order) {
        super(order, "users", UserVersion1.class, UserVersion2.class);
    }

    @Override
    UserVersion2 migrateEntity(UserVersion1 oldObject) {
        return new UserVersion2(
                oldObject.getId(),
                oldObject.getName(),
                true
        );
    }

    @Override
    UserVersion1 rollbackEntity(UserVersion2 newObject) {
        return new UserVersion1(
                newObject.getId(),
                newObject.getLogin()
        );
    }
}
