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

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * Resource loader that loads velocity resources from the classpath.
 * 
 * @author owaeld
 * 
 */
public class ClasspathResourceLoader extends ResourceLoader
{

    private static final Logger LOG = Logger.getLogger( ClasspathResourceLoader.class );

    /**
     * This is abstract in the base class, so we need it.
     * 
     * {@inheritDoc}
     */
    public void init( ExtendedProperties configuration )
    {
        LOG.debug( "ClasspathResourceLoader : initialization starting." );
        LOG.debug( "ClasspathResourceLoader : initialization complete." );
    }

    /**
     * Get an InputStream so that the Runtime can build a template with it.
     * 
     * @param name
     *            name of template to get
     * @return InputStream containing the template
     */
    public synchronized InputStream getResourceStream( String name )
    {
        InputStream result = null;

        if ( name == null || name.length() == 0 )
        {
            throw new ResourceNotFoundException( "No template name provided" );
        }

        try
        {
            result = ClasspathResourceLoader.class.getResourceAsStream( name );
        }
        catch ( Exception fnfe )
        {
            /*
             * log and convert to a general Velocity ResourceNotFoundException
             */
            throw new ResourceNotFoundException( fnfe.getMessage() );
        }

        return result;
    }

    /**
     * Defaults to return false.
     * 
     * {@inheritDoc}
     */
    public boolean isSourceModified( Resource resource )
    {
        return false;
    }

    /**
     * Defaults to return 0
     * 
     * {@inheritDoc}
     */
    public long getLastModified( Resource resource )
    {
        return 0;
    }

}
