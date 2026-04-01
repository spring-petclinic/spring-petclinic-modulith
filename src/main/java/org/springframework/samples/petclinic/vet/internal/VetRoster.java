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
package org.springframework.samples.petclinic.vet.internal;

import java.time.LocalDate;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.owner.VisitBooked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain service of the {@code vet} module managing the veterinarian roster and automatic
 * vet assignment.
 * <p>
 * When a {@link VisitBooked} event is received, this roster picks the least-loaded
 * veterinarian (fewest upcoming assignments) and persists the association in the
 * {@code visit_assignments} table.
 * <p>
 * It also supports daily cleanup of past assignments triggered by the
 * {@link org.springframework.modulith.moments.DayHasPassed} Passage of Time event: once a
 * visit date has passed the corresponding row is deleted so the load-balancing counts
 * stay accurate.
 */
@Service
class VetRoster {

	private static final Logger log = LoggerFactory.getLogger(VetRoster.class);

	private final VetRepository vets;

	private final VisitAssignmentRepository visitAssignments;

	VetRoster(VetRepository vets, VisitAssignmentRepository visitAssignments) {
		this.vets = vets;
		this.visitAssignments = visitAssignments;
	}

	/**
	 * Assigns the least-loaded veterinarian to the visit described by the given event.
	 * Only <em>upcoming</em> assignments (visit date &ge; today) are considered when
	 * computing the workload.
	 * @param event the published {@link VisitBooked} event
	 * @throws IllegalStateException if no vet is registered in the system
	 */
	@Transactional
	void assignVet(VisitBooked event) {
		LocalDate visitDate = event.date();
		Vet assignedVet = vets.findAll()
			.stream()
			.min(Comparator
				.comparingLong(vet -> visitAssignments.countByVetAndVisitDateGreaterThanEqual(vet, visitDate)))
			.orElseThrow(() -> new IllegalStateException("No veterinarian available for assignment"));

		visitAssignments.save(new VisitAssignment(event.visitId(), assignedVet, visitDate));

		log.info("Visit {} (pet {}, {}) assigned to Dr. {} {}", event.visitId(), event.petId(), visitDate,
				assignedVet.getFirstName(), assignedVet.getLastName());
	}

	/**
	 * Removes all visit assignments whose visit date is strictly before {@code today}.
	 * Intended to be called daily via a {@code DayHasPassed} event listener.
	 * @param today the current date (as provided by the Passage of Time event)
	 */
	@Transactional
	void cleanupPastAssignments(LocalDate today) {
		long deleted = visitAssignments.deleteByVisitDateBefore(today);
		if (deleted > 0) {
			log.info("Cleaned up {} past visit assignment(s) (before {})", deleted, today);
		}
	}

}
