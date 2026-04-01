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
import java.util.Optional;

import org.springframework.data.repository.Repository;

/**
 * Repository class for <code>VisitAssignment</code> domain objects.
 */
interface VisitAssignmentRepository extends Repository<VisitAssignment, Integer> {

	Optional<VisitAssignment> findByVisitId(int visitId);

	VisitAssignment save(VisitAssignment assignment);

	long countByVetAndVisitDateGreaterThanEqual(Vet vet, LocalDate date);

	long deleteByVisitDateBefore(LocalDate date);

	/**
	 * Returns the next upcoming assignment for a given vet (visit date >= today).
	 */
	Optional<VisitAssignment> findFirstByVetAndVisitDateGreaterThanEqualOrderByVisitDate(Vet vet, LocalDate date);

}
