package com.apptware.interview.singleton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The code tests whether the {@link com.apptware.interview.singleton.Singleton}
 * class strictly enforces the singleton pattern. By using reflection to access
 * the private constructor, it attempts to create a second instance of the
 * singleton. The assertion at the end verifies whether both instances are
 * indeed the same, based on their hash codes. If the assertion fails, it
 * indicates a failure in the Singleton pattern implementation.
 *
 * <p>
 * The candidate is expected **NOT** to modify the test case but the
 * corresponding class for which the test case is written.
 */
class SingletonTest {

	@Test
	@SneakyThrows
	void testSingleton() throws Exception {
		Singleton instance1 = Singleton.getInstance();

		assertThatThrownBy(() -> {
			Constructor<?>[] constructors = Singleton.class.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				constructor.setAccessible(true);
				try {
					constructor.newInstance();
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					assertThat(cause).isInstanceOf(IllegalStateException.class)
							.hasMessageContaining("Singleton instance already created");
					throw e;
				} catch (Exception e) {
					throw e;
				}
			}
		}).isInstanceOf(InvocationTargetException.class);
		Singleton instance3 = Singleton.getInstance();
		assertThat(instance1).isSameAs(instance3);
		Assertions.assertThat(instance1.hashCode()).isEqualTo(instance3.hashCode());
	}
}
