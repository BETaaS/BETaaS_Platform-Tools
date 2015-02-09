/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package org.ogf.graap.wsag.api.exceptions;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.xmlbeans.XmlString;
import org.oasisOpen.docs.wsrf.bf2.BaseFaultDocument;
import org.oasisOpen.docs.wsrf.bf2.BaseFaultType;
import org.oasisOpen.docs.wsrf.bf2.BaseFaultType.Description;
import org.ogf.graap.wsag.api.WsagConstants;

/**
 * NegotiationException
 * 
 * @author Oliver Waeldrich
 */
public class NegotiationException extends WSAgreementException
{
    /**
     * Default language identifier in base fault.
     */
    public static final String LOCAL_EN = "en";

    /**
     * Definition of main error message.
     */
    private static final String DESCRIPTION_MESSAGE = "{0}: {1}";

    /**
     * Definition of cause error message.
     */
    private static final String CAUSE_MESSAGE = "Caused by {0}: {1}";

    /**
     * Definition of stack trace error message.
     */
    private static final String STACKTRACE_MESSAGE = "\tat {0}";

    /**
     * exception timestamp
     */
    private final Calendar timestamp = new GregorianCalendar();

    private static final long serialVersionUID = 1L;

    /**
     * default constructor
     */
    public NegotiationException()
    {
        super();
    }

    /**
     * @param message
     *            the exception message
     * @param cause
     *            the exception cause
     */
    public NegotiationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * @param message
     *            the exception message
     */
    public NegotiationException( String message )
    {
        super( message );
    }

    /**
     * @param cause
     *            the exception cause
     */
    public NegotiationException( Throwable cause )
    {
        super( cause );
    }

    /**
     * @return the base fault document representing this exception.
     */
    public BaseFaultType getBaseFault()
    {
        BaseFaultDocument baseFaultDocument = BaseFaultDocument.Factory.newInstance();
        BaseFaultType baseFault = baseFaultDocument.addNewBaseFault();

        baseFault.addNewErrorCode();
        baseFault.getErrorCode().setDialect( WsagConstants.WSAG4J_NAMESPACE );
        baseFault.getErrorCode().set( XmlString.Factory.newValue( Integer.toString( getErrorCode() ) ) );

        baseFault.setTimestamp( timestamp );

        Description description = baseFault.addNewDescription();
        description.setLang( LOCAL_EN );
        description.setStringValue( MessageFormat.format( DESCRIPTION_MESSAGE, new Object[] {
            this.getClass().getName(), getMessage() } ) );

        StackTraceElement[] stackTrace = getStackTrace();
        for ( int i = 0; i < stackTrace.length; i++ )
        {
            description = baseFault.addNewDescription();
            description.setLang( LOCAL_EN );
            description.setStringValue( MessageFormat.format( STACKTRACE_MESSAGE,
                new Object[] { stackTrace[i] } ) );
        }

        if ( getCause() != null )
        {
            description = baseFault.addNewDescription();
            description.setLang( LOCAL_EN );
            description.setStringValue( MessageFormat.format( CAUSE_MESSAGE, new Object[] {
                getCause().getClass().getName(), getCause().getMessage() } ) );
        }

        return baseFault;
    }

    /**
     * Specific exceptions must override this method.
     * 
     * @return the base fault error code for this exception
     */
    @Override
    public int getErrorCode()
    {
        return NEGOTIATION_ERROR;
    }
}
