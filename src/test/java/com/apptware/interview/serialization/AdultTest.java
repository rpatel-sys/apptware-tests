package com.apptware.interview.serialization;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This test class has a validation for
 * {@link com.apptware.interview.serialization.Adult}. The first test tests the
 * validation using a constructor by creating an illegal entity and expecting an
 * error message for the same. The second test has a same purpose using
 * serialization.
 *
 * <p>
 * The candidate is expected to modify the test case and the corresponding class
 * for which the test case is written so that the appropriate exception is
 * thrown with appropriate messages.
 */
public class AdultTest {
	private static class AdultDeserializer extends JsonDeserializer<Adult> {
		@Override
		public Adult deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);

			String firstName = node.get("firstName").asText();
			String lastName = node.get("lastName").asText();
			int age = node.get("age").asInt();

			if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
				throw new IllegalArgumentException("Firstname or Lastname CANNOT be blank.");
			}

			if (Objects.isNull(age) || age < 18) {
				throw new IllegalArgumentException("Inappropriate Age for an Adult.");
			}

			return new Adult(firstName, lastName, age);
		}

	}

	@Test
	void testConstructorValidation() {
		Assertions.assertThatThrownBy(() -> new Adult("", "", 18)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Firstname or Lastname CANNOT be blank.");
		Assertions.assertThatThrownBy(() -> new Adult("Firstname", "Lastname", 17))
				.isInstanceOf(IllegalArgumentException.class)
				// Changes expected ----->
				.hasMessage("Inappropriate Age for an Adult.");
		// <----- Changes expected

		String json1 = """
				{
				  "firstName": "",
				  "lastName": "",
				  "age": 18
				}
				""";

		String json2 = """
				{
				  "firstName": "Firstname",
				  "lastName": "Lastname",
				  "age": 17
				}
				""";

		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Adult.class, new AdultDeserializer());
		objectMapper.registerModule(module);
		Assertions.assertThatThrownBy(() -> {
			Adult adult = objectMapper.readValue(json1, Adult.class);
			System.out.println(adult);
		})
				// Changes expected ----->
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Firstname or Lastname CANNOT be blank.");
		// <----- Changes expected
		Assertions.assertThatThrownBy(() -> {
			Adult adult = objectMapper.readValue(json2, Adult.class);
			System.out.println(adult);
		})
				// Changes expected ----->
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Inappropriate Age for an Adult.");
		// <----- Changes expected
	}
}
