/*
 * Copyright 2012-2025 the original author or authors.
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

import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.samples.petclinic.owner.VisitBooked;
import org.springframework.stereotype.Component;

/**
 * Listener of the {@code vet} module reacting to visits booked by the {@code owner} and
 * to Passage of Time events published by Spring Modulith Moments.
 * <p>
 * It demonstrates event-driven decoupling: the {@code owner} module does not know the
 * {@code vet} module. Communication happens only through the {@link VisitBooked} event,
 * which is part of the {@code owner} module public API.
 */
@Component
class VetEventListener {

	private final VetRoster vetRoster;

	VetEventListener(VetRoster vetRoster) {
		this.vetRoster = vetRoster;
	}

	@ApplicationModuleListener
	void on(VisitBooked event) {
		vetRoster.assignVet(event);
	}

	@EventListener
	void on(DayHasPassed event) {
		vetRoster.cleanupPastAssignments(event.getDate());
	}

}
