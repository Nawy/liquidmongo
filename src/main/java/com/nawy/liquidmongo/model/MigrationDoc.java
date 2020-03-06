package com.nawy.liquidmongo.model;

import org.mongojack.Id;

public class MigrationDoc {

    @Id
    private String id;
    private int version;
}
