package dev.gtech.bifrost.bifrostapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BifrostApiApplication {

	private static final Map<String, Boolean> environmentVariables = Map.of(
		"CLIENT_ID", true,
		"CLIENT_SECRET", true,
		"ISSUER_URI", true,
		"KEYPAIR_ID", true,
		"MONGO_USERNAME", true,
		"MONGO_PASSWORD", true,
		"MONGO_HOST", true,
		"MONGO_DB", true
	);

	public static void main(String[] args) throws Exception {
		List<String> missingEnvVars = new ArrayList<>();
		for (Entry<String, Boolean> entry : environmentVariables.entrySet()) {
			boolean isRequired = entry.getValue();
			if (isRequired && StringUtils.isBlank(System.getenv(entry.getKey()))) {
				missingEnvVars.add(entry.getKey());
			}
		}

		if (missingEnvVars.size() > 0) {
			throw new Exception(String.format("Missing environment variables: %s", missingEnvVars.toString()));
		}
		SpringApplication.run(BifrostApiApplication.class, args);
	}

}
