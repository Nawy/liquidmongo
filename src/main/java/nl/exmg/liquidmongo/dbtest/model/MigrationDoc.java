package nl.exmg.liquidmongo.dbtest.model;

import org.mongojack.Id;

public class MigrationDoc {

    @Id
    private String id;
    private int version;
}
