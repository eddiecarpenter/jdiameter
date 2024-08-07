/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, TeleStax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.jdiameter.common.api.app.s13;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;

@SuppressWarnings("all") //3rd party lib
public interface IS13MessageFactory {
    JMEIdentityCheckRequest createMEIdentityCheckRequest(Request request);

    JMEIdentityCheckAnswer createMEIdentityCheckAnswer(Answer answer);

    /**
     * Returns the Application-Id that this message factory is related to
     *
     * @return the Application-Id value
     */
    long getApplicationId();
}
