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
package org.ogf.graap.wsag.server.actions.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.server.actions.AbstractGetTemplateAction;
import org.ogf.graap.wsag.server.actions.ActionInitializationException;
import org.ogf.graap.wsag4j.types.configuration.FileTemplateConfigurationDocument;
import org.ogf.graap.wsag4j.types.configuration.FileTemplateConfigurationType;
import org.ogf.graap.wsag4j.types.configuration.ImplementationConfigurationType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;

/**
 * Implementation of an {@link org.ogf.graap.wsag.server.actions.IGetTemplateAction} that dynamically creates
 * an agreement template using Velocity Template engine. Agreement templates are generated based on the
 * specified template file when the action is initialized ({@link #initialize()}). Subsequent calls to the
 * {@link #getTemplate()} method return the agreement template that was created during the action
 * initialization process.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class VelocityAgreementTemplateAction extends AbstractGetTemplateAction
{

    private static final Logger LOG = Logger.getLogger( VelocityAgreementTemplateAction.class );

    private String templateFilename = "";

    private Map<String, Object> templateParameter = new HashMap<String, Object>();

    private AgreementTemplateType template;

    /**
     * <!-- begin-UML-doc --> Initializes this action. During the action initialization process the specified
     * template file is loaded via the application {@link ClassLoader} and processed as Velocity template. The
     * resulting agreement template is stored and retrieved via the {@link #getTemplate()} method. <!--
     * end-UML-doc -->
     * 
     * {@inheritDoc}
     * 
     * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
     */
    public void initialize() throws ActionInitializationException
    {
        // begin-user-code
        try
        {
            ImplementationConfigurationType handlerConfig = getHandlerContext().getHandlerConfiguration();

            XmlObject[] children =
                handlerConfig.selectChildren( FileTemplateConfigurationDocument.type.getDocumentElementName() );
            if ( children.length != 1 )
            {
                Object[] filler =
                    new Object[] { getClass().getName(),
                        FileTemplateConfigurationDocument.type.getDocumentElementName().toString(),
                        Integer.toString( children.length ) };

                String msgInvalidConfig =
                    "Invalid configuration for action {0}. Expected one configuration section of type {1}, "
                        + "but found {2}";
                String message = MessageFormat.format( msgInvalidConfig, filler );
                throw new ActionInitializationException( message );
            }
            /*System.out.println(((SimpleValue)children[0]).getStringValue());*/

            /* GIACOMO EDITED */
            FileTemplateConfigurationType config= FileTemplateConfigurationType.Factory.newInstance();
            config.setFilename(((SimpleValue)children[0]).getStringValue().trim());
            
            setTemplateFilename( config.getFilename() );

            FileTemplate fileTemplate = new FileTemplate( getTemplateFilename() );

            Map<String, ?> parameter = getTemplateParameter();
            Iterator<String> keys = parameter.keySet().iterator();
            while ( keys.hasNext() )
            {
                try
                {
                    String key = (String) keys.next();
                    fileTemplate.addParameter( key, parameter.get( key ) );
                }
                catch ( ClassCastException e )
                {
                    LOG.error( "Invalid template parameter. Key value must be of type String." );
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            fileTemplate.processTemplate( out );
            ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );

            template =
                AgreementTemplateType.Factory.parse( in,
                                                     new XmlOptions().setLoadReplaceDocumentElement( null ) );
        }
        catch ( Exception e )
        {
            Object[] filler = new Object[] { e.getMessage() };
            String message = MessageFormat.format( "Error creating template. Reason: {0}", filler );
            LOG.error( message );
            throw new ActionInitializationException( message );
        }
        // end-user-code
    }

    /**
     * Hook for custom implementations for providing a set of parameters that are can be accessed by the
     * Velocity template. Implementations return a map with parameters in order to dynamically include them in
     * the template.
     * 
     * @return the template parameters used for creating the template
     * 
     * @see AbstractGetTemplateAction#getHandlerContext()
     * @see org.ogf.graap.wsag.server.actions.IActionHandlerContext#getFactoryContext()
     */
    protected Map<String, ?> getTemplateParameter()
    {
        return templateParameter;
    }

    /**
     * Returns the processed template.
     * 
     * {@inheritDoc}
     */
    public AgreementTemplateType getTemplate()
    {
        return template;
    }

    /**
     * Returns the file name of the template to process.
     * 
     * @return the templateFilename
     */
    public String getTemplateFilename()
    {
        // begin-user-code
        return templateFilename;
        // end-user-code
    }

    /**
     * Sets the file name of the template to process.
     * 
     * @param templateFilename
     *            the templateFilename to set
     */
    public void setTemplateFilename( String templateFilename )
    {
        // begin-user-code
        this.templateFilename = templateFilename;
        // end-user-code
    }

}
