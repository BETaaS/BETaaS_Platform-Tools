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
package org.ogf.graap.wsag.api;

import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ConstraintSectionType;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;

/**
 * The AgreementTemplate interface implements the required methods to access the properties of an agreement
 * template. Agreement templates are used to create new {@link AgreementOffer} instances.
 * 
 * @see AgreementOffer
 * @author Oliver Waeldrich
 */
public interface AgreementTemplate
{

    /**
     * @return the template name
     */
    String getName();

    /**
     * @param name
     *            the template name to set
     */
    void setName( String name );

    /**
     * @return the agreement id
     */
    String getAgreementId();

    /**
     * @param id
     *            the agreement id to set
     */
    void setAgreementId( String id );

    /**
     * @return the template id
     */
    String getTemplateId();

    /**
     * @param id
     *            the template id to set
     */
    void setTemplateId( String id );

    /**
     * @return the agreement context
     */
    AgreementContextType getContext();

    /**
     * @param context
     *            the context to set
     */
    void setContext( AgreementContextType context );

    /**
     * @return the agreement terms
     */
    TermTreeType getTerms();

    /**
     * @param terms
     *            the terms to set
     */
    void setTerms( TermTreeType terms );

    /**
     * @return the template creation constraints
     */
    ConstraintSectionType getCreationConstraints();

    /**
     * @param constraints
     *            the creation constraints to set
     */
    void setCreationConstraints( ConstraintSectionType constraints );

    /**
     * @return the XML representation of the template
     */
    AgreementTemplateType getXMLObject();
}
