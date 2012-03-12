/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.api.app;

import org.jdiameter.api.OverloadException;
import org.jdiameter.api.InternalException;

/**
 * The StateMachine lets you organize event handling,
 * if the order of the events are important to you.
 * 
 * @version 1.5.1 Final
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface StateMachine {

    /**
     * Add a new state change listener
     * @param listener a reference to the listener that will get information about state changes.
     */
    void addStateChangeNotification(StateChangeListener listener);

    /**
     * Remove a state change listener
     * @param listener a reference to the listener that will get information about state changes.
     */
    void removeStateChangeNotification(StateChangeListener listener);    

    /**
     * Handle an event in the current state.
     * @param event processing event
     * @return true if staterocessed
     * @throws OverloadException if queue of state mashine is full
     * @throws InternalException if FSM has internal error
     */
    boolean handleEvent(StateEvent event) throws InternalException, OverloadException;

    /**
     * Get the current state
     * @param stateType type of state
     * @return current state
     */
    <E> E getState(Class<E> stateType);  
}
