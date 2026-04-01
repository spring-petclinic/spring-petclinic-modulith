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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.petclinic.owner.VisitBooked;
import org.springframework.samples.petclinic.owner.domain.Owner;
import org.springframework.samples.petclinic.owner.domain.OwnerRepository;
import org.springframework.samples.petclinic.owner.domain.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service of the {@code owner} module handling visit booking.
 * <p>
 * After persistence, it publishes a {@link VisitBooked} event so other modules can react
 * without direct dependencies on this module.
 */
@Service
public class VisitScheduler {

	private final OwnerRepository owners;

	private final ApplicationEventPublisher eventPublisher;

	public VisitScheduler(OwnerRepository owners, ApplicationEventPublisher eventPublisher) {
		this.owners = owners;
		this.eventPublisher = eventPublisher;
	}

	@Transactional
	public void bookVisit(Owner owner, Integer petId, Visit visit) {
		Owner managedOwner = owners.findById(owner.getId()).orElseThrow();
		managedOwner.addVisit(petId, visit);
		owners.flush();
		eventPublisher.publishEvent(new VisitBooked(visit.getId(), petId, visit.getDate()));
	}

}
