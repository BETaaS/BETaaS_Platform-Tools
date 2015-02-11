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
package org.ogf.graap.wsag.server.persistence;

/**
 * <p>
 * Specification of an agreement home implementation. Implementations are used to retrieve (list and find)
 * agreements and to remove existing agreements.
 * </p>
 * 
 * <p>
 * A global instance of the agreement home implementations stored in the
 * {@link org.ogf.graap.wsag.server.engine.WsagEngine} instance. It can be used to access all agreements known to
 * the wsag4j-instance.
 * </p>
 * 
 * <p>
 * The default implementation of an agreement home is
 * {@link org.ogf.graap.wsag.server.persistence.impl.DatabaseAgreementHome}.
 * </p>
 * 
 * @author Oliver Waeldrich
 * 
 */
public interface IAgreementHome
{

    /**
     * Finds an agreement with a given ID.
     * 
     * @param agreementId
     *            the ID of the agreement
     * 
     * @return the agreement
     * 
     * @throws PersistedResourceException
     *             indicates an exception while loading the agreement from the database
     */
    PersistentAgreement find( String agreementId ) throws PersistedResourceException;

    /**
     * Lists all agreements.
     * 
     * @return List of all agreements.
     * 
     * @throws Exception
     *             indicates an exception while loading the agreements from the database
     */
    PersistentAgreement[] list() throws Exception;

    /**
     * Lists all agreements for one specific agreement factory.
     * 
     * @param agreementFactoryId
     *            the id of the factory for which the agreements should be listed
     * 
     * @return a list of all agreements for a specified agreement factory
     * 
     * @throws Exception
     *             indicates an exception while loading the agreements from the database
     * 
     * @deprecated the agreement home is implemented by a specific agreement factory instance
     */
    PersistentAgreement[] list( String agreementFactoryId ) throws Exception;

    /**
     * Removes the agreement with the given id.
     * 
     * @param agreementId
     *            the ID of the agreement, which should be removed
     * 
     * @throws PersistedResourceException
     *             indicates an exception while removing the agreement from the database
     */
    void remove( String agreementId ) throws PersistedResourceException;
}
