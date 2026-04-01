/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.samples.petclinic.owner.VisitBooked;
import org.springframework.samples.petclinic.owner.domain.Owner;
import org.springframework.samples.petclinic.owner.domain.OwnerRepository;
import org.springframework.samples.petclinic.owner.domain.Pet;
import org.springframework.samples.petclinic.owner.domain.Visit;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Integration tests for the {@code owner} module using {@link ApplicationModuleTest}.
 * <p>
 * Unlike {@code @SpringBootTest}, only the {@code owner} module is bootstrapped. Startup
 * logs should contain: <pre>
 *   Bootstrapping @ApplicationModuleTest for owner in mode STANDALONE
 *   Re-configuring auto-configuration and entity scan packages to: owner.internal
 * </pre>
 */
@ApplicationModuleTest
class VisitSchedulerTests {

	@Autowired
	OwnerRepository owners;

	@Autowired
	VisitScheduler visitScheduler;

	@Test
	void bookVisitShouldPublishVisitBookedEvent(Scenario scenario) {
		// Given
		Owner owner = owners.findById(1).orElseThrow();
		Pet pet = owner.getPets().iterator().next();
		Visit visit = new Visit();
		visit.setDescription("Annual checkup");

		// When / Then
		scenario.stimulate(() -> visitScheduler.bookVisit(owner, pet.getId(), visit))
			.andWaitForEventOfType(VisitBooked.class)
			.matching(event -> event.petId() == pet.getId())
			.toArriveAndVerify(event -> then(event.petId()).isEqualTo(pet.getId()));
	}

}
