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

/**
 * Base class for WSAG4J exceptions.
 * 
 * @author Oliver Waeldrich
 */
public abstract class WSAgreementException extends Exception
{

    /**
     * HTTP error code for application errors. This code identifies the overall error class. The
     * exception-specific error code identifies the concrete exception.
     */
    private static final int APPLICATION_ERROR_CLASS = 500;

    /**
     * Error code for the {@link AgreementFactoryException}
     */
    public static final int AGREEMENT_FACTORY_ERROR = 1001;

    /**
     * Error code for the {@link CreationConstraintsViolationException}
     */
    public static final int AGREEMENT_CONSTRAINT_VALIDATION_ERROR = 1002;

    /**
     * Error code for the {@link AgreementCreationException}
     */
    public static final int AGREEMENT_CREATION_ERROR = 1003;

    /**
     * Error code for the {@link NegotiationFactoryException}
     */
    public static final int NEGOTIATION_INSTANTIATION_ERROR = 2001;

    /**
     * Error code for the {@link NegotiationException}
     */
    public static final int NEGOTIATION_ERROR = 2101;

    /**
     * Error code for the {@link ValidationException}
     */
    public static final int NEGOTIATION_VALIDATION_ERROR = 2102;

    /**
     * Error code for the {@link ResourceUnknownException}
     */
    public static final int RESOURCE_UNKNOWN_ERROR = 9001;

    /**
     * Error code for the {@link ResourceUnavailableException}
     */
    public static final int RESOURCE_UNAVAILABLE_ERROR = 9002;

    private static final long serialVersionUID = 1L;

    /**
     * default constructor
     */
    public WSAgreementException()
    {
        super();
    }

    /**
     * Constructs the exception with the given message.
     * 
     * @param message
     *            the exception message
     */
    public WSAgreementException( String message )
    {
        super( message );
    }

    /**
     * Constructs the exception with the given message and initializes the exception cause.
     * 
     * @param message
     *            the exception message
     * @param cause
     *            the exception cause
     */
    public WSAgreementException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Constructs the exception and initializes the exception cause.
     * 
     * @param cause
     *            the exception cause
     */
    public WSAgreementException( Throwable cause )
    {
        super( cause );
    }

    /**
     * Returns a unique error code for the specific Exception;
     * 
     * @return the exception specific error code
     */
    public abstract int getErrorCode();

    /**
     * Maps an application error to an error class. This error class reflects a HTTP error code. By default
     * this method returns 500 as error class indicating an application error. Exceptions may overwrite this
     * method in order to provide a more appropriate error code.
     * 
     * @return the code of the error class
     */
    public int getErrorClass()
    {
        return APPLICATION_ERROR_CLASS;
    }
}
