package org.springframework.samples.petclinic.vet.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisabledInAotMode
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @TestPropertySource("/application-postgres.properties")
class VetRepositoryTests {

	@Autowired
	protected VetRepository vets;

	@Test
	void shouldFindVets() {
		Collection<Vet> vets = this.vets.findAll();

		Vet vet = vets.stream().filter(v -> v.getId() == 3).findFirst().orElseThrow();

		assertThat(vet.getLastName()).isEqualTo("Douglas");
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}

}
