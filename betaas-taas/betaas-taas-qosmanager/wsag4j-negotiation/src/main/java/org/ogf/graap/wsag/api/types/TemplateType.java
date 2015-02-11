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
package org.ogf.graap.wsag.api.types;

import org.ogf.graap.wsag.api.AgreementTemplate;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementRoleType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ConstraintSectionType;
import org.ogf.schemas.graap.wsAgreement.TemplateDocument;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;

/**
 * AgreementTemplateType
 * 
 * @author Oliver Waeldrich
 */
public class TemplateType extends WSAGXmlType implements AgreementTemplate
{
    AgreementTemplateType template = AgreementTemplateType.Factory.newInstance();

    /**
     * Default constructor.
     */
    public TemplateType()
    {
        initialize();
    }

    /**
     * Creates a new template type based on a template XML document.
     * 
     * @param template
     *            the agreement template
     */
    public TemplateType( AgreementTemplateType template )
    {
        //
        // make sure that template is not a document fragment
        //
        TemplateDocument templateDoc = TemplateDocument.Factory.newInstance();
        templateDoc.addNewTemplate().set( template.copy() );
        this.template = templateDoc.getTemplate();
    }

    /**
     * {@inheritDoc}
     */
    public boolean validate()
    {
        return validate( template );
    }

    private void initialize()
    {
        AgreementContextType context = template.addNewContext();
        context.setServiceProvider( AgreementRoleType.AGREEMENT_RESPONDER );

        template.addNewTerms();
        template.addNewCreationConstraints();
    }

    /**
     * {@inheritDoc}
     */
    public String getAgreementId()
    {
        return template.getAgreementId();
    }

    /**
     * {@inheritDoc}
     */
    public AgreementContextType getContext()
    {
        return template.getContext();
    }

    /**
     * {@inheritDoc}
     */
    public ConstraintSectionType getCreationConstraints()
    {
        return template.getCreationConstraints();
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return template.getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getTemplateId()
    {
        return template.getTemplateId();
    }

    /**
     * {@inheritDoc}
     */
    public TermTreeType getTerms()
    {
        return template.getTerms();
    }

    /**
     * {@inheritDoc}
     */
    public void setAgreementId( String arg0 )
    {
        template.setAgreementId( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public void setContext( AgreementContextType arg0 )
    {
        template.setContext( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public void setCreationConstraints( ConstraintSectionType arg0 )
    {
        template.setCreationConstraints( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public void setName( String arg0 )
    {
        template.setName( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public void setTemplateId( String arg0 )
    {
        template.setTemplateId( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public void setTerms( TermTreeType arg0 )
    {
        template.setTerms( arg0 );
    }

    /**
     * {@inheritDoc}
     */
    public AgreementTemplateType getXMLObject()
    {
        return template;
    }

}
