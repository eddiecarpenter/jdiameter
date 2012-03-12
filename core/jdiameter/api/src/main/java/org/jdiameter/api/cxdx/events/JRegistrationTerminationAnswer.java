/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and individual contributors
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

package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Registration-Termination-Answer (RTA) command, indicated by the Command-Code field set to 304 and the �R� bit
 * cleared in the Command Flags field, is sent by a client in response to the Registration-Termination-Request command. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JRegistrationTerminationAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "RTA";
  public static final String _LONG_NAME = "Registration-Termination-Answer";

  public static final int code = 304;

}
