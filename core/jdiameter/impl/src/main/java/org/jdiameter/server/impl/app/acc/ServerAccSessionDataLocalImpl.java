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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   JBoss, Home of Professional Open Source
 *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
 *   by the @authors tag. See the copyright.txt in the distribution for a
 *   full listing of individual contributors.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.server.impl.app.acc;

import java.io.Serializable;

import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;

/**
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
@SuppressWarnings("all") //3rd party lib
public class ServerAccSessionDataLocalImpl extends AppSessionDataLocalImpl implements IServerAccSessionData {

    protected ServerAccSessionState state = ServerAccSessionState.IDLE;
    protected boolean stateles = true;
    protected long tsTimeout = NON_INITIALIZED;
    protected Serializable tsTimerId;

    /**
     *
     */
    public ServerAccSessionDataLocalImpl() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#
     * setServerAccSessionState
     * (org.jdiameter.common.api.app.acc.ServerAccSessionState)
     */
    @Override
    public void setServerAccSessionState(ServerAccSessionState value) {
        this.state = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#
     * getServerAccSessionState()
     */
    @Override
    public ServerAccSessionState getServerAccSessionState() {
        return this.state;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setStateles(boolean
     * )
     */
    @Override
    public void setStateless(boolean value) {
        this.stateles = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.server.impl.app.acc.IServerAccSessionData#isStateles()
     */
    @Override
    public boolean isStateless() {
        return this.stateles;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setTsTimeout(
     * long)
     */
    @Override
    public void setTsTimeout(long value) {
        this.tsTimeout = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jdiameter.server.impl.app.acc.IServerAccSessionData#getTsTimeout()
     */
    @Override
    public long getTsTimeout() {
        return this.tsTimeout;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jdiameter.server.impl.app.acc.IServerAccSessionData#setTsTimerId(
     * java.io.Serializable)
     */
    @Override
    public void setTsTimerId(Serializable value) {
        this.tsTimerId = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jdiameter.server.impl.app.acc.IServerAccSessionData#getTsTimerId()
     */
    @Override
    public Serializable getTsTimerId() {
        return this.tsTimerId;
    }

}
