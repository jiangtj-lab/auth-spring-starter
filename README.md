# auth-spring-starter
Auth utils for spring web

## Install

1/2: Add this to pom.xml:

```xml
<dependency>
  <groupId>com.jiangtj.utils</groupId>
  <artifactId>auth-spring-starter</artifactId>
  <version>${last-version}</version>
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

## Why

When I use the Spring Boot framework, I generally follow the following rules to generate tokens.

```json
{
  "iss": "auth-app",
  "iat": 1617156560,
  "aud": "*",
  "exp": 1617242960
}
```

- iss as the application name `spring.application.name`
- iat/exp as a basis for time verification
- aud as authorization target, which may be the target application name or some other identification, such as `user`

I need to create the same tool classes in different Spring Boot every time. So I extracted them.
