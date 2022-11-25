package com.jiangtj.utils.authspringstarter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created At 2021/3/30.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthConfiguration.class)
class AuthPropertiesTest {

    @Nested
    class DefaultEnv {

        @Resource
        private AuthProperties properties;

        @Test
        void testOptions() {
            Options def = properties.getDef();
            assertNotNull(def);
            assertNull(def.getSecret());
            assertEquals(Duration.ofMinutes(5), def.getExpires());
            assertEquals(Duration.ofMinutes(30), def.getMaxExpires());
            assertEquals("Authorization", def.getHeaderName());
            assertEquals("Bearer ", def.getHeaderPrefix());
            assertNotNull(properties.getSpec());
            int size = properties.getSpec().size();
            assertEquals(0 ,size);
        }
    }

    @Nested
    @TestPropertySource(properties = {
        "auth.def.secret=vtsjytbfgdfsrgsgtrhrseveewvtsrbrwevc2evtsresgerfcwedefrebtrtred",
    })
    class CustomKey {

        @Resource
        private AuthProperties properties;

        @Test
        void testOptions() {
            Options def = properties.getDef();
            assertNotNull(def);
            assertEquals("vtsjytbfgdfsrgsgtrhrseveewvtsrbrwevc2evtsresgerfcwedefrebtrtred", def.getSecret());
            assertEquals(Duration.ofMinutes(5), def.getExpires());
            assertEquals(Duration.ofMinutes(30), def.getMaxExpires());
            assertEquals("Authorization", def.getHeaderName());
            assertEquals("Bearer ", def.getHeaderPrefix());
            assertNotNull(properties.getSpec());
            int size = properties.getSpec().size();
            assertEquals(0 ,size);
        }
    }

    @Nested
    @TestPropertySource(properties = {
        "auth.def.secret=unuse",
        "auth.spec.user.secret=vtsjytbfgdfsrgsgtrhrseveedswvtsrbrwevc2evtsresgerfcdefrebtrtred",
        "auth.spec.user.expires=1d",
        "auth.spec.user.max-expires=10d",
        "auth.spec.foo.header-name=auth",
        "auth.spec.a.secret=newval",
    })
    class SpecTest {

        @Resource
        private AuthProperties properties;

        @Test
        void testUser() {
            Options user = properties.getSpec().get("user");
            assertEquals("vtsjytbfgdfsrgsgtrhrseveedswvtsrbrwevc2evtsresgerfcdefrebtrtred", user.getSecret());
            assertEquals(Duration.ofDays(1), user.getExpires());
            assertEquals(Duration.ofDays(10), user.getMaxExpires());
            assertNull(user.getHeaderName());
            assertNull(user.getHeaderPrefix());
        }

        @Test
        void testFoo() {
            Options foo = properties.getSpec().get("foo");
            assertNull(foo.getSecret());
            assertNull(foo.getExpires());
            assertNull(foo.getMaxExpires());
            assertEquals("auth", foo.getHeaderName());
            assertNull(foo.getHeaderPrefix());
        }

        @Test
        void testA() {
            Options a = properties.getSpec().get("a");
            assertEquals("newval", a.getSecret());
            assertNull(a.getExpires());
            assertNull(a.getMaxExpires());
            assertNull(a.getHeaderName());
            assertNull(a.getHeaderPrefix());
        }
    }

}