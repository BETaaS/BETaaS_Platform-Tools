/* 
 * Copyright (c) 2005-2011, Fraunhofer-Gesellschaft
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
 * ResourceUnknownException
 * 
 * @author Oliver Waeldrich
 */
public class ResourceUnknownException extends WSAgreementException
{

    private static final long serialVersionUID = 1L;

    /**
     * The error class code for unknown resources.
     */
    private static final int RESOURCE_UNKNOWN_ERROR_CLASS = 404;

    /**
     * default constructor
     */
    public ResourceUnknownException()
    {
        super();
    }

    /**
     * @param message
     *            the exception message
     */
    public ResourceUnknownException( String message )
    {
        super( message );
        // setErrorCode();
    }

    /**
     * @param message
     *            the exception message
     * @param cause
     *            the exception cause
     */
    public ResourceUnknownException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * @param cause
     *            the exception cause
     */
    public ResourceUnknownException( Throwable cause )
    {
        super( cause );
    }

    @Override
    public int getErrorCode()
    {
        return RESOURCE_UNKNOWN_ERROR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.exceptions.WSAgreementException#getErrorClass()
     */
    @Override
    public int getErrorClass()
    {
        return RESOURCE_UNKNOWN_ERROR_CLASS;
    }
}
