package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Tests to verify the modular structure and generate documentation for the modules.
 *
 */
class ModularityTests {

	ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);

	@Test
	void verifiesModularStructure() {
		modules.verify();
	}

	@Test
	void writeDocumentation() {
		new Documenter(modules).writeDocumentation();
	}

}
