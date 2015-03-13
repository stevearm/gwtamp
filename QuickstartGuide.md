# Introduction #

These instructions will get you started with GwtAMP quickly.


# Requirements #

You'll need maven installed on your computer, as 90% of the worth of GwtAMP is the maven plugins that compile the php and sql for you. And if you've got maven, you obviously have a jdk installed, so you're all set.

# Basic instructions #

To use GwtAMP, you need to do the following things (I'll describe these in detail below):
  * Create a maven project
  * Add GwtAMP and GWT as dependencies to your pom file
  * Create a GWT module that extends the GwtAMP module
  * Subclass `DataSource` for each record or database table you want
  * Create a class that implements the `DataSourceBundle` class
  * Add a configuration to your pom for gwtamp-plugin that specifies:
    * The full gwt package name
    * The full package name for the `DataSourceBundle` implementation

# Detailed instructions #

For most of this section, I will refer to the **contact-list** sample project as an example.

## Add GwtAMP and GWT as dependencies to your pom file ##

Add the following dependencies to your pom file:
```
<dependencies>
...
  <dependency>
    <groupId>com.horsefire</groupId>
    <artifactId>gwtamp</artifactId>
    <version>0.0.1</version>
  </dependency>

  <dependency>
    <groupId>com.google.gwt</groupId>
    <artifactId>gwt-user</artifactId>
    <version>1.5.3</version>
  </dependency>
...
</dependencies>
```

And to let maven know where to download the gwtamp jar from, add this:
```
<repositories>
  <repository>
    <id>gwtamp-repo</id>
    <name>GwtAMP repository</name>
    <url>http://gwtamp.googlecode.com/svn/m2repo</url>
  </repository>
</repositories>
```
## Create a GWT module that extends the GwtAMP module ##

I assume you already know how to create a GWT module. Just add the following line to your `MyModule.gwt.xml` file:
```
<module>
  <inherits name='com.horsefire.gwtamp.GwtAmp' />
...
</module>
```
## Subclass the `DataSource` class for each record or database table you want ##

Think of your data model. For each record type you want, create a class that extends `com.horsefire.gwtamp.client.DataSource` (in contact-list, this is `ContactDataSource` and `PhoneNumberDataSource`). I usually make private constructor and use a static factory method, so I can build the field list ahead of time. See `com.horsefire.contactList.client.ContactDataSource` for an example.

Make sure your class does not directly reference any GWT code. The plugin will instantiate this class, and so it can't contain any code that will not run outside of hosted mode. This is why the `PleaseWaitDialog` that the `DataSource` constructor asks for is an interface, not a real class, so you can pass in the `PleaseWaitDialog.NullDialog` stub when constructing it server-side.

## Create a class that implements the `DataSourceBundle` class ##

Create a class that implements the `DataSourceBundle` interface. I usually use this class to instantiate my `DataSource` subclasses in my code, but at the least, it's required for the plugin.

Make sure that your implementation has a default constructor that will create non-gwt-hosted-mode versions of your `DataSource` subclasses. Also, read the comments for  `DataSourceBundle.getDataSources()` about the order of the returned `DataSource` subclasses.

## Configuration the gwtamp-plugin in your pom ##

```
<build>
  ...
  <!-- The source must be included so the GWT compiler can get at it -->
  <resources>
    <resource>
      <directory>src/main/java</directory>
    </resource>
  </resources>

  <plugins>
    ...
    <plugin>
      <groupId>com.horsefire</groupId>
      <artifactId>gwtamp-plugin</artifactId>
      <version>${gwtamp.version}</version>
      <executions>
        <execution>
          <configuration>
            <gwtModule>com.horsefire.contactList.ContactList</gwtModule>
            <dataSourceBundle>com.horsefire.contactList.client.DataSourceBundle</dataSourceBundle>
            <databaseTablePrefix>contactList</databaseTablePrefix>
          </configuration>
          <phase>package</phase>
          <goals>
            <goal>doEverything</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    ...
  </plugins>
  ...
</build>
```
Configure the following tags to your specific project:
  * `gwtModule` - Set to the full path of your GWT module
  * `dataSourceBundle` - Set to the full path of your implementation of `DataSourceBundle`
  * `databaseTablePrefix` - All of your `MySQL` tables will be prefixed with this string

Then add the following section so that maven can find the plugin on the repository here:
```
<pluginRepositories>
  <pluginRepository>
    <id>gwtamp-plugin-repo</id>
    <name>GwtAMP repository</name>
    <url>http://gwtamp.googlecode.com/svn/m2repo</url>
  </pluginRepository>
</pluginRepositories>
```

## Try it out ##
Run `mvn clean package` from the root of your new project, and it should produce the entire deployment, as well as all the required SQL to setup the database.