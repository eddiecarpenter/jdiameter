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

package org.jdiameter.server.impl.app.rf;

import static org.jdiameter.common.api.app.rf.ServerRfSessionState.IDLE;
import static org.jdiameter.common.api.app.rf.ServerRfSessionState.OPEN;

import java.io.Serializable;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rf.ServerRfSession;
import org.jdiameter.api.rf.ServerRfSessionListener;
import org.jdiameter.api.rf.events.RfAccountingAnswer;
import org.jdiameter.api.rf.events.RfAccountingRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.rf.IServerRfActionContext;
import org.jdiameter.common.api.app.rf.ServerRfSessionState;
import org.jdiameter.common.impl.app.rf.AppRfSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server Accounting session implementation
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@SuppressWarnings("all") //3rd party lib
public class ServerRfSessionImpl extends AppRfSessionImpl
        implements EventListener<Request, Answer>, ServerRfSession, NetworkReqListener {
    //FIXME: verify this FSM
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ServerRfSessionImpl.class);

    // Session State Handling ---------------------------------------------------

    // Factories and Listeners --------------------------------------------------
    protected transient IServerRfActionContext context;
    protected transient ServerRfSessionListener listener;

    // Ts Timer -----------------------------------------------------------------
    protected static final String TIMER_NAME_TS = "TS";
    protected IServerRfSessionData sessionData;

    // Constructors -------------------------------------------------------------
    public ServerRfSessionImpl(IServerRfSessionData sessionData, ISessionFactory sessionFactory,
            ServerRfSessionListener serverSessionListener,
            IServerRfActionContext serverContextListener, StateChangeListener<AppSession> stLst, long tsTimeout,
            boolean stateless) {
        // TODO Auto-generated constructor stub
        super(sessionFactory, sessionData);
        this.listener = serverSessionListener;
        this.context = serverContextListener;
        this.sessionData = sessionData;
        this.sessionData.setTsTimeout(tsTimeout);
        this.sessionData.setStateless(stateless);
        super.addStateChangeNotification(stLst);
    }

    @Override
    public void sendAccountAnswer(RfAccountingAnswer accountAnswer)
            throws InternalException, IllegalStateException, RouteException, OverloadException {
        try {
            session.send(accountAnswer.getMessage());
            /*
             * TODO: Need to notify state change...
             * if (isStateless() && isValid()) {
             * session.release();
             * }
             */
        } catch (IllegalDiameterStateException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isStateless() {
        return this.sessionData.isStateless();
    }

    @SuppressWarnings("unchecked")
    protected void setState(IAppSessionState newState) {
        IAppSessionState oldState = this.sessionData.getServerRfSessionState();
        this.sessionData.setServerRfSessionState((ServerRfSessionState) newState);

        for (StateChangeListener i : stateListeners) {
            i.stateChanged(this, (Enum) oldState, (Enum) newState);
        }
    }

    @Override
    public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
        return isStateless() ? handleEventForStatelessMode(event) : handleEventForStatefulMode(event);
    }

    public boolean handleEventForStatelessMode(StateEvent event) throws InternalException, OverloadException {
        try {
            //this will handle RTRs as well, no need to alter.
            final ServerRfSessionState state = this.sessionData.getServerRfSessionState();
            switch (state) {
                case IDLE: {
                    switch ((Event.Type) event.getType()) {
                        case RECEIVED_START_RECORD:
                            // Current State: IDLE
                            // Event: Accounting start request received, and successfully processed.
                            // Action: Send accounting start answer
                            // New State: IDLE
                            if (listener != null) {
                                try {
                                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                }
                            }
                            // TODO: This is unnecessary state change: setState(IDLE);
                            break;
                        case RECEIVED_EVENT_RECORD:
                            // Current State: IDLE
                            // Event: Accounting event request received, and successfully processed.
                            // Action: Send accounting event answer
                            // New State: IDLE
                            if (listener != null) {
                                try {
                                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                }
                            }
                            // FIXME: it is required, so we know it ends up again in IDLE!
                            setState(IDLE);
                            break;
                        case RECEIVED_INTERIM_RECORD:
                            // Current State: IDLE
                            // Event: Interim record received, and successfully processed.
                            // Action: Send accounting interim answer
                            // New State: IDLE
                            if (listener != null) {
                                try {
                                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                }
                            }
                            // TODO: This is unnecessary state change: setState(IDLE);
                            break;
                        case RECEIVED_STOP_RECORD:
                            // Current State: IDLE
                            // Event: Accounting stop request received, and successfully processed
                            // Action: Send accounting stop answer
                            // New State: IDLE
                            if (listener != null) {
                                try {
                                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                }
                            }
                            // TODO: This is unnecessary state change: setState(IDLE);
                            break;
                        default:
                            throw new IllegalStateException("Current state " + state + " action " + event.getType());
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Can not process event", e);
            return false;
        } finally {
            // TODO: Since setState was removed, we are now using this to terminate. Correct?
            // We can't release here, answer needs to be sent through. done at send.
            // release();
        }
        return true;
    }

    public boolean handleEventForStatefulMode(StateEvent event) throws InternalException, OverloadException {
        try {
            if (((RfAccountingRequest) event.getData()).getMessage().isReTransmitted()) {
                try {
                    setState(OPEN);
                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                    // FIXME: should we do this before passing to lst?
                    cancelTsTimer();
                    startTsTimer();
                    if (context != null) {
                        context.sessionTimerStarted(this, null);
                    }
                } catch (Exception e) {
                    logger.debug("Can not handle event", e);
                    setState(IDLE);
                }
                return true;
            } else {
                final ServerRfSessionState state = this.sessionData.getServerRfSessionState();
                switch (state) {
                    case IDLE: {
                        switch ((Event.Type) event.getType()) {
                            case RECEIVED_START_RECORD:
                                // Current State: IDLE
                                // Event: Accounting start request received, and successfully processed.
                                // Action: Send accounting start answer, Start Ts
                                // New State: OPEN
                                setState(OPEN);
                                if (listener != null) {
                                    try {
                                        listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                        cancelTsTimer();
                                        startTsTimer();
                                        if (context != null) {
                                            context.sessionTimerStarted(this, null);
                                        }
                                    } catch (Exception e) {
                                        logger.debug("Can not handle event", e);
                                        setState(IDLE);
                                    }
                                }
                                break;
                            case RECEIVED_EVENT_RECORD:
                                // Current State: IDLE
                                // Event: Accounting event request received, and
                                // successfully processed.
                                // Action: Send accounting event answer
                                // New State: IDLE
                                if (listener != null) {
                                    try {
                                        listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                    } catch (Exception e) {
                                        logger.debug("Can not handle event", e);
                                    }
                                }
                                break;
                        }
                        break;
                    }
                    case OPEN: {
                        switch ((Event.Type) event.getType()) {
                            case RECEIVED_INTERIM_RECORD:
                                // Current State: OPEN
                                // Event: Interim record received, and successfully
                                // processed.
                                // Action: Send accounting interim answer, Restart Ts
                                // New State: OPEN
                                try {
                                    listener.doRfAccountingRequestEvent(this, (RfAccountingRequest) event.getData());
                                    cancelTsTimer();
                                    startTsTimer();
                                    if (context != null) {
                                        context.sessionTimerStarted(this, null);
                                    }
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                    setState(IDLE);
                                }
                                break;
                            case RECEIVED_STOP_RECORD:
                                // Current State: OPEN
                                // Event: Accounting stop request received, and
                                // successfully
                                // processed
                                // Action: Send accounting stop answer, Stop Ts
                                // New State: IDLE
                                setState(IDLE);
                                try {
                                    listener.doRfAccountingRequestEvent(this,
                                            (RfAccountingRequest) event.getData());
                                    cancelTsTimer();
                                    if (context != null) {
                                        context.sessionTimerCanceled(this, null);
                                    }
                                } catch (Exception e) {
                                    logger.debug("Can not handle event", e);
                                    setState(IDLE);
                                }
                                break;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Can not process event", e);
            return false;
        }
        return true;
    }

    private void startTsTimer() {
        //    return scheduler.schedule(new Runnable() {
        //      public void run() {
        //        logger.debug("Ts timer expired");
        //        if (context != null) {
        //          try {
        //            context.sessionTimeoutElapses(ServerRfSessionImpl.this);
        //          }
        //          catch (InternalException e) {
        //            logger.debug("Failure on processing expired Ts", e);
        //          }
        //        }
        //        setState(IDLE);
        //      }
        //    }, tsTimeout, TimeUnit.MILLISECONDS);
        try {
            sendAndStateLock.lock();
            if (sessionData.getTsTimeout() > 0) {
                this.sessionData.setTsTimerId(
                        super.timerFacility.schedule(getSessionId(), TIMER_NAME_TS, this.sessionData.getTsTimeout()));
            }
        } finally {
            sendAndStateLock.unlock();
        }
    }

    private void cancelTsTimer() {
        try {
            sendAndStateLock.lock();
            final Serializable tsTimerId = this.sessionData.getTsTimerId();
            if (tsTimerId != null) {
                super.timerFacility.cancel(tsTimerId);
                this.sessionData.setTsTimerId(null);
            }
        } finally {
            sendAndStateLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
     */
    @Override
    public void onTimer(String timerName) {
        if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
            checkIdleAppSession();
        } else if (timerName.equals(TIMER_NAME_TS)) {
            if (context != null) {
                try {
                    context.sessionTimeoutElapses(ServerRfSessionImpl.this);
                } catch (InternalException e) {
                    logger.debug("Failure on processing expired Ts", e);
                }
            }
            setState(IDLE);
        } else {
            logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
        }
    }

    protected Answer createStopAnswer(Request request) {
        Answer answer = request.createAnswer(ResultCode.SUCCESS);
        answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 4);
        answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
        return answer;
    }

    protected Answer createInterimAnswer(Request request) {
        Answer answer = request.createAnswer(ResultCode.SUCCESS);
        answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
        answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
        return answer;
    }

    protected Answer createEventAnswer(Request request) {
        Answer answer = request.createAnswer(ResultCode.SUCCESS);
        answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 2);
        answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
        return answer;
    }

    protected Answer createStartAnswer(Request request) {
        Answer answer = request.createAnswer(ResultCode.SUCCESS);
        answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 1);
        answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
        return answer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getState(Class<E> eClass) {
        return eClass == ServerRfSessionState.class ? (E) this.sessionData.getServerRfSessionState() : null;
    }

    @Override
    public Answer processRequest(Request request) {
        if (request.getCommandCode() == RfAccountingRequest.code) {
            try {
                sendAndStateLock.lock();
                handleEvent(new Event(createAccountRequest(request)));
            } catch (Exception e) {
                logger.debug("Can not handle event", e);
            } finally {
                sendAndStateLock.unlock();
            }
        } else {
            try {
                listener.doOtherEvent(this, createAccountRequest(request), null);
            } catch (Exception e) {
                logger.debug("Can not handle event", e);
            }
        }
        return null;
    }

    @Override
    public void receivedSuccessMessage(Request request, Answer answer) {
        if (request.getCommandCode() == RfAccountingRequest.code) {
            try {
                sendAndStateLock.lock();
                handleEvent(new Event(createAccountRequest(request)));
            } catch (Exception e) {
                logger.debug("Can not handle event", e);
            } finally {
                sendAndStateLock.unlock();
            }

            try {
                listener.doRfAccountingRequestEvent(this, createAccountRequest(request));
            } catch (Exception e) {
                logger.debug("Can not handle event", e);
            }
        } else {
            try {
                listener.doOtherEvent(this, createAccountRequest(request), createAccountAnswer(answer));
            } catch (Exception e) {
                logger.debug("Can not handle event", e);
            }
        }
    }

    @Override
    public void timeoutExpired(Request request) {
        // FIXME: alexandre: We don't do anything here... are we even getting this on server?
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
     */
    @Override
    public boolean isReplicable() {
        return true;
    }

    @Override
    public void release() {
        if (isValid()) {
            try {
                sendAndStateLock.lock();
                //TODO: cancel timer?
                super.release();
            } catch (Exception e) {
                logger.debug("Failed to release session", e);
            } finally {
                sendAndStateLock.unlock();
            }
        } else {
            logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
        }
    }

}
