# What is it?

This is a reference project for Java intensive course.

## Required capabilities

* Add a repairer
* Remove a repairer
* List repairers sorted by name, status
* Add a garage slot
* Remove a garage slot
* List garage slots sorted by availability
* Create an order
* Assign a garage slot to the order
* Assign one or more repairers to the order
* Complete an order
* Cancel an order
* List orders sorted by creation date, completion date, price, status
* Get full information about an order: repairers, garage slot, creation date, completion date

## How to run?

```bash
./gradlew application:run
```

## Tags

Git tags used to mark different stages in projects' lifecycle. You can use them to quickly navigate to and inspect the
state.

* **stage-1** - All required use cases implemented. Unit tests in place. CI configured
* **stage-2** - Application state saved on disk instead of keeping it in memory. Application can be configured using '
  application.toml' file
* **stage-3** - Application can be interacted with through HTTP requests instead of console interface
* **stage-4** - Application state saved in database using JDBC instead of disk.
* **stage-5** - Application state saved in database using JPA instead of JDBC.
