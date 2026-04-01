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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_assignments")
class VisitAssignment {

	@Id
	@Column(name = "visit_id", updatable = false)
	private int visitId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vet_id", nullable = false)
	private Vet vet;

	@Column(name = "visit_date", nullable = false, updatable = false)
	private LocalDate visitDate;

	protected VisitAssignment() {
		// Default constructor for JPA
	}

	VisitAssignment(int visitId, Vet vet, LocalDate visitDate) {
		this.visitId = visitId;
		this.vet = vet;
		this.visitDate = visitDate;
	}

	int getVisitId() {
		return visitId;
	}

	Vet getVet() {
		return vet;
	}

	LocalDate getVisitDate() {
		return visitDate;
	}

}
