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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.xmlbeans.XmlDateTime;
import org.ogf.graap.wsag.api.logging.LogMessage;

/**
 * Default implementation to load files from the classpath an process these files as Velocity templates.
 * Additional parameters can be specified, which can be used in the Velocity macros defined in the file.
 * 
 * @author owaeld
 * 
 */
public class FileTemplate
{

    private static final Logger LOG = Logger.getLogger( FileTemplate.class );

    /**
     * Velocity properties filename. Applications may provide a custom properties file in order to overwrite
     * default configuration. If this file does not exist in the classpath the default velocity configuration
     * is used.
     */
    public static final String VELOCITY_PROPERTIES_FILE = "/wsag4j-velocity.properties";

    /**
     * Default velocity properties filename.
     */
    public static final String VELOCITY_PROPERTIES_FILE_DEFAULT = "/wsag4j-velocity.properties.default";

    private VelocityContext context;

    private Template template;
    
    

    static
    {
        Properties properties = new Properties();

        try
        {
            //
            // try to load user provided velocity properties if available in the classpath, otherwise use
            // default properties
            //
            InputStream in = FileTemplate.class.getResourceAsStream( VELOCITY_PROPERTIES_FILE );
            if ( in == null )
            {
                in = FileTemplate.class.getResourceAsStream( VELOCITY_PROPERTIES_FILE_DEFAULT );
            }
            properties.load( in );
            
            //
            // set Velocity log4j logger name
            //
            properties.setProperty( "runtime.log.logsystem.log4j.logger", FileTemplate.class.getName() );

        }
        catch ( Exception e )
        {
            String message = "Failed to load velocity properties file [{0}]. Reason: {1}";
            LOG.error( LogMessage.getMessage( message, VELOCITY_PROPERTIES_FILE, e.getMessage() ) );
            LOG.error( "Resource loockup in WSAG4J classpath disabled." );
            LOG.debug( e );
        }

        try
        {   
            Velocity.init( properties );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to initialize velocity template engine.", e );
            LOG.error( "Resource loockup in WSAG4J classpath disabled." );
        }

    }

    /**
     * Creates a new file template for the given file name. The file with the specified name is resolved from
     * the classpath.
     * 
     * @param filename
     *            the file name
     */
    public FileTemplate( String filename )
    {
        LOG.debug("Read");
        
        try
        {
            this.context = new VelocityContext();
            
            String currDate = XmlDateTime.Factory.newValue( new Date() ).getStringValue();
            addParameter( "currentTime", currDate );

            //
            // avoid escaping all the XPath expression
            //
            addParameter( "this", "$this" );
            
            
            template = Velocity.getTemplate( filename );
            
        }
        catch ( ResourceNotFoundException e )
        {
            LOG.error( MessageFormat.format( "error loading template file [{0}]", filename ) );
            LOG.error( e.getMessage() );
        }
        catch (ParseErrorException e)
        {
        	LOG.error( MessageFormat.format( "error parsing template file [{0}]", filename ) );
            LOG.error( e.getMessage() );
        }
        catch ( Exception e )
        {
            LOG.error( MessageFormat.format( "error processing template file [{0}]", filename ) );
            LOG.error( e.getMessage() );
        }
    }

    /**
     * Adds a new parameter to the Velocity context. Parameters can be accessed in the template by its key.
     * 
     * @param key
     *            the parameter name
     * 
     * @param value
     *            the parameter value
     */
    public void addParameter( String key, Object value )
    {
        context.put( key, value );
    }

    /**
     * Processes the file template as Velocity template.
     * 
     * @param out
     *            the {@link OutputStream} where the result is written to
     */
    public void processTemplate( OutputStream out )
    {
        try
        {
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out ) );

            if ( template != null )
            {
                template.merge( context, writer );
            }

            writer.flush();
            writer.close();
        }
        catch ( Exception ex )
        {
            LOG.error( "error processing output for template:" );
            LOG.error( ex.getMessage() );
        }
    }
}
