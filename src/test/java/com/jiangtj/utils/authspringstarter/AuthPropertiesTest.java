package com.jiangtj.utils.authspringstarter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created At 2021/3/30.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthConfiguration.class)
class AuthPropertiesTest {

    private final Options defaultOptions = new Options();

    @Nested
    class DefaultEnv {

        @Resource
        private AuthProperties properties;

        @Test
        void testOptions() {
            Options def = properties.getDef();
            assertNotNull(def);
            assertNotNull(def.getRequest());
            assertEquals(defaultOptions.getSecret(), def.getSecret());
        }

        @Test
        void testSpec() {
            assertNotNull(properties.getSpec());
            int size = properties.getSpec().size();
            assertEquals(0 ,size);
        }

        @Test
        void testGetKey() {
            properties.getDef().getKey();
        }
    }

    @Nested
    @TestPropertySource(properties = {
            "auth.def.secret=newval",
    })
    class CustomKey {

        @Resource
        private AuthProperties properties;

        @Test
        void testOptions() {
            Options def = properties.getDef();
            assertNotNull(def);
            assertNotNull(def.getRequest());
            assertEquals("newval", def.getSecret());
        }

        @Test
        void testSpec() {
            assertNotNull(properties.getSpec());
            int size = properties.getSpec().size();
            assertEquals(0 ,size);
        }
    }

    @Nested
    @TestPropertySource(properties = {
            "auth.def.request.header-name=auth",
    })
    class CustomRequest {

        @Resource
        private AuthProperties properties;

        @Test
        void testOptions() {
            Options def = properties.getDef();
            assertNotNull(def);
            assertEquals(defaultOptions.getSecret(), def.getSecret());
            Options.Request request = def.getRequest();
            assertNotNull(request);
            assertEquals("auth", request.getHeaderName());
            assertEquals(defaultOptions.getRequest().getHeaderPrefix(), request.getHeaderPrefix());
        }

        @Test
        void testSpec() {
            assertNotNull(properties.getSpec());
            int size = properties.getSpec().size();
            assertEquals(0 ,size);
        }
    }

    @Nested
    @TestPropertySource(properties = {
            "auth.spec.user.expires=1d",
            "auth.spec.user.max-expires=10d",
            "auth.spec.foo.request.header-name=auth",
            "auth.spec.a.secret=newval",
    })
    class SpecTest {

        @Resource
        private AuthProperties properties;

        @Test
        void testUser() {
            Options user = properties.getSpec().get("user");
            assertEquals(defaultOptions.getSecret(), user.getSecret());
            assertEquals(Duration.ofDays(1), user.getExpires());
            assertEquals(Duration.ofDays(10), user.getMaxExpires());
            Options.Request request = user.getRequest();
            assertNotNull(request);
            assertEquals(defaultOptions.getRequest().getHeaderName(), request.getHeaderName());
            assertEquals(defaultOptions.getRequest().getHeaderPrefix(), request.getHeaderPrefix());
        }

        @Test
        void testFoo() {
            Options foo = properties.getSpec().get("foo");
            assertEquals(defaultOptions.getSecret(), foo.getSecret());
            assertEquals(defaultOptions.getExpires(), foo.getExpires());
            assertEquals(defaultOptions.getMaxExpires(), foo.getMaxExpires());
            Options.Request request = foo.getRequest();
            assertNotNull(request);
            assertEquals("auth", request.getHeaderName());
            assertEquals(defaultOptions.getRequest().getHeaderPrefix(), request.getHeaderPrefix());
        }

        @Test
        void testA() {
            Options a = properties.getSpec().get("a");
            assertEquals("newval", a.getSecret());
            assertEquals(defaultOptions.getExpires(), a.getExpires());
            assertEquals(defaultOptions.getMaxExpires(), a.getMaxExpires());
            Options.Request request = a.getRequest();
            assertNotNull(request);
            assertEquals(defaultOptions.getRequest().getHeaderName(), request.getHeaderName());
            assertEquals(defaultOptions.getRequest().getHeaderPrefix(), request.getHeaderPrefix());
        }
    }

}