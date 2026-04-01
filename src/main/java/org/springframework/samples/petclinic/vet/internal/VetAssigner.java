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
 * Domain component of the {@code vet} module handling automatic vet assignment.
 * <p>
 * When a {@link VisitBooked} event is received, this component picks the least-loaded
 * veterinarian (fewest upcoming assignments) and persists the association in the
 * {@code visit_assignments} table.
 * <p>
 * It also supports daily cleanup of past assignments triggered by the
 * {@link org.springframework.modulith.moments.DayHasPassed} Passage of Time event: once a
 * visit date has passed the corresponding row is deleted so the load-balancing counts
 * stay accurate.
 */
@Service
class VetAssigner {

	private static final Logger log = LoggerFactory.getLogger(VetAssigner.class);

	private final VetRepository vets;

	private final VisitAssignmentRepository visitAssignments;

	VetAssigner(VetRepository vets, VisitAssignmentRepository visitAssignments) {
		this.vets = vets;
		this.visitAssignments = visitAssignments;
	}

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

	@Transactional
	void cleanupPastAssignments(LocalDate today) {
		long deleted = visitAssignments.deleteByVisitDateBefore(today);
		if (deleted > 0) {
			log.info("Cleaned up {} past visit assignment(s) (before {})", deleted, today);
		}
	}

}
