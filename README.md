# Liquidmongo v0.1 Alpha
Hello, everyone! It's only an alpha version, if you want to help me, so please join! I've began to develop this library because
I don't know good proper libraries for migration data in MongoDB. (excluding Mongobee and a few others).
Concept:

Database has version. Each version has migration rules to this version and rollbacks.

#### Examples
Admin Version 1
```java
public class AdminVersion1 {

    @Id
    private ObjectId id;
    private String name; // needs to split name to the first name and the last name
    private String email;
    private String levelName;
    private CredentialsOld credentials;

    // constructor
    // getters & setters
}
```

Admin Version 2

```java
public class AdminVersion2 {

    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private String level;
    private CredentialsOld credentials;

    // constructor
    // getters & setters
}
```

And migration
```java
@Migration(version = 2, order = 1)
public class Admin1to2Migration extends MigrationStep<AdminVersion1, AdminVersion2> {

    public Admin1to2Migration(int order) {
        super(order, "adminus", AdminVersion1.class, AdminVersion2.class);
    }

    // mapping to new entity
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

    // mapping back
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
```

And the execution:
```java
class App {

	void main() {
		Liquidmongo liquidmongo = new Liquidmongo();
		liquidmongo.setCurrentVersion(-1); // starts from the lowest version
		liquidmongo.addMigrations(
				new Migration(VERSION_2)
						.addStep(new Admin1to2Migration(1))
						// other steps...
				// other migrations...
		);

		liquidmongo.setTargetVersion(VERSION_2);
		liquidmongo.prepared();
		liquidmongo.execute();
	}

}
```
## Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/maven-plugin/)

