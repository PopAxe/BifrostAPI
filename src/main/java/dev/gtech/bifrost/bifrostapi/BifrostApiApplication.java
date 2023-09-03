package dev.gtech.bifrost.bifrostapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BifrostApiApplication extends SpringBootServletInitializer {

	private static final Map<String, Boolean> devEnvironmentVariables = Map.of(
		"VAULT_ENDPOINT", true,
		"VAULT_TOKEN", true
	);

	private static final Map<String, Boolean> prodEnvironmentVariables = Map.of(
		"VAULT_ROLEID", true,
		"VAULT_SECRETID", true
	);

	public static void main(String[] args) throws Exception {
		String activeProfile = System.getenv("STAGE");
		List<String> missingEnvVars = getMissingEnvVars(activeProfile, devEnvironmentVariables);

		if (missingEnvVars.size() > 0) {
			throw new Exception(String.format("Missing environment variables: %s", missingEnvVars.toString()));
		}

		new SpringApplicationBuilder(BifrostApiApplication.class)
			.profiles(activeProfile)
			.run(args);
	}

	private static List<String> getMissingEnvVars(String activeProfile, Map<String, Boolean> requiredArgsMap) {
		List<String> missingEnvVars = new ArrayList<>();

		if (!activeProfile.equals("dev")) {
			for (Entry<String, Boolean> entry : prodEnvironmentVariables.entrySet()) {
				boolean isRequired = entry.getValue();
				if (isRequired && StringUtils.isBlank(System.getenv(entry.getKey()))) {
					missingEnvVars.add(entry.getKey());
				}
			}
		} else {
			for (Entry<String, Boolean> entry : devEnvironmentVariables.entrySet()) {
				boolean isRequired = entry.getValue();
				if (isRequired && StringUtils.isBlank(System.getenv(entry.getKey()))) {
					missingEnvVars.add(entry.getKey());
				}
			}
		}

		return missingEnvVars;
	}

}
