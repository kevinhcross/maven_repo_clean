maven_repo_clean
================

A maven plug-in designed as a short term workaround for the maven bug MNG-4142.

It will check all the maven-metadata-local.xml files in the supplied paths of
the local Maven repository and change all localCopy values from true to false.

This plug-in is designed to be used in a situation where you have many
different jobs on a build server and you do not want to clean out the shared
local maven repo on a regular basis.

To build you will need to skip the tests as there are some issues with the
maven test harness dependencies that I have yet to resolve.

    mvn -DskipTests=true clean install

To use add this plug-in config to the build section of your pom.

_Note that you should change the `relativeRepoPaths` section to the paths you want to clean._

```xml
    <plugin>
      <groupId>org.kevinhcross.maven</groupId>
      <artifactId>repo_clean</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <configuration>
        <relativeRepoPaths>
          <string>org/kevinhcross</string>
          <string>com/kevinhcross</string>
          <string>org/apache/maven</string>
        </relativeRepoPaths>
      </configuration>
      <executions>
        <execution>
          <phase>clean</phase>
          <goals>
            <goal>clean-metadata</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
```


