---

title: "Spring Framework Integration"
weight: 50

menu:
  main:
    identifier: "user-guide-spring-framework-integration"
    parent: "user-guide"

---

The Camunda engine Spring Framework integration is provided via a Maven module and can be added to Apache Maven–based projects using the following dependency:

{{< note title="" class="info" >}}
Please import the [Camunda BOM](/get-started/apache-maven/) to ensure consistent dependency versions.
{{< /note >}}

```xml
<dependency>
  <groupId>org.camunda.bpm</groupId>
  <artifactId>camunda-engine-spring</artifactId>
</dependency>
```

The process engine Spring artifact should be added as a library to the process application.

The following minimal set of Spring dependencies must be added in the desired version:

```xml
<properties>
  <spring.version>X.Y.Z</spring.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-framework-bom</artifactId>
      <version>${spring.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
  </dependency>
</dependencies>
```

# Using camunda-engine-spring with Spring 6 or 7

As of Camunda 7.24.6, the `camunda-engine-spring` artifact is compiled and tested against Spring Framework 7 by default.

The previously available `camunda-engine-spring-6` artifact has been removed. A single artifact, `camunda-engine-spring`, is now used for both Spring 6 and Spring 7.

The actual Spring version used at runtime is controlled by your project's dependency management.

{{< note title="Spring version configuration" class="info" >}}
You can choose the Spring Framework version by overriding it via your dependency management (e.g., using the Spring BOM).
{{< /note >}}

## Using Spring Framework 6

To use Spring Framework 6, override the Spring dependencies in your project as shown below.

**1. Add the camunda-engine-spring dependency**

```xml
<dependency>
  <groupId>org.camunda.bpm</groupId>
  <artifactId>camunda-engine-spring</artifactId>
  <version>${camunda.version}</version>
</dependency>
```

**2. Override Spring to 6.x**

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-framework-bom</artifactId>
      <version>6.2.1</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**3. Verify the Spring version**

```bash
mvn dependency:tree -Dincludes=org.springframework
```

Expected output should contain Spring 6.x artifacts, for example:

```
org.springframework:spring-context:jar:6.2.1:compile
org.springframework:spring-core:jar:6.2.1:compile
```

