---

title: "Spring Boot Version Compatibility"
weight: 10

menu:
  main:
    name: "Version Compatibility"
    identifier: "user-guide-spring-boot-version-compatibility"
    parent: "user-guide-spring-boot-integration"

---

Each version of the Camunda Spring Boot Starter is bound to a specific version of Camunda 7 and Spring Boot. 
Only these default combinations are recommended (and supported) by Camunda.
Other combinations must be thoroughly tested before being used in production.

{{< note title="Heads Up" class="info" >}}
  Starting with version 7.13.0, Camunda 7 and its compatible Spring Boot Starter always share the same version.
  Also, the Camunda 7 version used in the Spring Boot Starter doesn't have to be overridden anymore. Simply pick
  the version of the Starter that resembles the version of Camunda 7 you want to use.
{{< /note >}}

{{< note title="Spring Boot 3 and 4 Artifacts" class="info" >}}
  Starting with version 7.24.3, Camunda provides separate Spring Boot Starter artifacts for Spring Boot 3 and Spring Boot 4:
  
  * **Spring Boot 3**: Use the original artifact names (e.g., `camunda-bpm-spring-boot-starter`)
  * **Spring Boot 4**: Use the `-4` suffix artifacts (e.g., `camunda-bpm-spring-boot-starter-4`)
  
  See the [Patch Level Update Guide]({{< ref "/update/patch-level.md#spring-boot-starter-4-support-7-24-3-only" >}}) for migration details.
{{< /note >}}

<table class="table table-striped">
  <tr>
    <th>Spring Boot Starter version</th>
    <th>Camunda 7 version</th>
    <th>Spring Boot version</th>
  </tr>
  <tr>
    <td>7.18.x<br/>7.19.x</td>
    <td>7.18.x<br/>7.19.x</td>
    <td>2.7.x</td>
  </tr>
  <tr>
    <td>7.20.x<br/>7.20.3+&#42;&#42;&#42;</td>
    <td>7.20.x<br/>7.20.3+</td>
    <td>3.1.x<br/>3.2.x</td>
  </tr>
  <tr>
    <td>7.21.x<br/>7.21.3+&#42;&#42;&#42;</td>
    <td>7.21.x<br/>7.21.3+</td>
    <td>3.2.x<br/>3.3.x</td>
  </tr>
  <tr>
    <td>7.22.x<br/>7.22.2+&#42;&#42;&#42;</td>
    <td>7.22.x<br/>7.22.2+</td>
    <td>3.3.x<br/>3.4.x</td>
  </tr>
  <tr>
    <td>7.23.x<br/>7.23.2+&#42;&#42;&#42;</td>
    <td>7.23.x<br/>7.23.2+</td>
    <td>3.4.x<br/>3.5.x</td>
  </tr>
  <tr>
    <td>7.24.x<br/>7.24.3+&#42;&#42;&#42;&#42;</td>
    <td>7.24.x<br/>7.24.3+</td>
    <td>3.5.x<br/>4.0.x</td>
  </tr>
</table>

\* For these versions, use the following Maven coordinates:
```
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-spring-boot-starter</artifactId>
  <version>1.x</version> <!-- set correct version here -->
</dependency>
```

\*\* For these versions, use the following Maven coordinates:
```
<dependency>
  <groupId>org.camunda.bpm.extension.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter</artifactId>
  <version>2.x</version> <!-- set correct version here -->
</dependency>
```

\*\*\* For these versions, all listed Spring Boot versions are supported 
while the oldest one is used by default. If you want to use a newer supported version,
configure `dependencyManagement` in your application, e.g. add the following when using Maven:
```
<dependencyManagement>
  <dependencies>
  ...
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>2.x.y.RELEASE</version> <!-- set correct version here -->
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  ...
  </dependencies>
</dependencyManagement>
```

\*\*\*\* Starting with version 7.24.3, Spring Boot 4 is supported via separate artifacts with the `-4` suffix.
For Spring Boot 3, use the original artifact names (e.g., `camunda-bpm-spring-boot-starter`).
For Spring Boot 4, use the `-4` suffix artifacts (e.g., `camunda-bpm-spring-boot-starter-4`).

**Spring Boot 4 Example:**
```xml
<dependency>
  <groupId>org.camunda.bpm.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter-4</artifactId>
  <version>7.24.3-ee</version>
</dependency>
```

