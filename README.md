# auth-spring-starter
Auth utils for spring web

## Install

1/2: Add this to pom.xml:

```xml
<dependency>
  <groupId>com.jiangtj.utils</groupId>
  <artifactId>auth-spring-starter</artifactId>
  <version>0.0.2</version>
</dependency>
```

2/2: Configuring github packages

[configuring-apache-maven-for-use-with-github-packages](https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages)

## Example

```java
@Resource
private AuthServer authServer;
String token = authServer.builder().build();
Jws<Claims> verify = authServer.verifier().verify(token);
```

For complete use, see the test case.
