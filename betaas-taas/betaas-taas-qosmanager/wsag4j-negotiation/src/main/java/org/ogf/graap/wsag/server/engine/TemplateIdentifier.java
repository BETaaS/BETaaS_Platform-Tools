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
package org.ogf.graap.wsag.server.engine;

import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;

/**
 * The template identifier is a unique identifier of a template in one agreement factory instance. It fact it
 * is a concatenation of the template name and id.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class TemplateIdentifier
{

    private String name;

    private String version;

    /**
     * Creates a new identifier for the given template.
     * 
     * @param template
     *            the agreement template
     */
    public TemplateIdentifier( AgreementTemplateType template )
    {
        this( template.getName(), template.getTemplateId() );
    }

    /**
     * Creates a new identifier for the given template name and version.
     * 
     * @param name
     *            the template name
     * 
     * @param version
     *            the template version (id)
     */
    public TemplateIdentifier( String name, String version )
    {
        this.name = ( name == null ) ? "" : name;
        this.version = ( version == null ) ? "" : version;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object compare )
    {
        if ( compare instanceof TemplateIdentifier )
        {
            TemplateIdentifier key = (TemplateIdentifier) compare;

            String templateName1 = key.getName();
            String templateVersion1 = key.getVersion();

            templateName1 = ( templateName1 == null ) ? "" : templateName1;
            templateVersion1 = ( templateVersion1 == null ) ? "" : templateVersion1;

            return templateName1.equals( name ) && templateVersion1.equals( version );
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return name.hashCode();
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

}
