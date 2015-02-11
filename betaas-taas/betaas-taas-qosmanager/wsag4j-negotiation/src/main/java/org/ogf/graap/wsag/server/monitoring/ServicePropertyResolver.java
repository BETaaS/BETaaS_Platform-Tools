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
package org.ogf.graap.wsag.server.monitoring;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.ServicePropertiesType;
import org.ogf.schemas.graap.wsAgreement.VariableType;

/**
 * Resolves a set of service properties from a given agreement properties document and converts the resolved
 * properties in the correct type using the specified service property metric.
 * 
 * @author owaeld
 */
public class ServicePropertyResolver
{
    private static final Logger LOG = Logger.getLogger( ServicePropertyResolver.class );

    private final ServicePropertiesType properties;

    private final AgreementPropertiesType agreementProperties;

    private final ConvertUtilsBean convertor = new ConvertUtilsBean();

    private final Map<String, Class<?>> metrics = new HashMap<String, Class<?>>();

    /**
     * Constructs a new resolver.
     * 
     * @param properties
     *            the service properties to resolve
     * 
     * @param agreementProperties
     *            the agreement properties document to resolve the service properties from
     */
    public ServicePropertyResolver( ServicePropertiesType properties,
                                    AgreementPropertiesType agreementProperties )
    {
        this.properties = properties;
        this.agreementProperties = agreementProperties;

        metrics.put( "string", String.class );
        metrics.put( "integer", Integer.class );
        metrics.put( "short", Short.class );
        metrics.put( "long", Long.class );
        metrics.put( "double", Double.class );
        metrics.put( "float", Float.class );
        metrics.put( "date", Date.class );
        metrics.put( "boolean", Boolean.class );

        convertor.register( new IntegerConverter(), Integer.class );
        convertor.register( new IntegerConverter(), Integer.TYPE );
        convertor.register( new ShortConverter(), Short.class );
        convertor.register( new ShortConverter(), Short.TYPE );
        convertor.register( new LongConverter(), Long.class );
        convertor.register( new LongConverter(), Long.TYPE );
        convertor.register( new DoubleConverter(), Double.class );
        convertor.register( new DoubleConverter(), Double.TYPE );
        convertor.register( new FloatConverter(), Float.class );
        convertor.register( new FloatConverter(), Float.TYPE );
        convertor.register( new BooleanConverter(), Boolean.class );
        convertor.register( new BooleanConverter(), Boolean.TYPE );
    }

    /**
     * Resolves the service properties from the agreement properties document.
     * 
     * @return the resolved service properties
     */
    public Map<String, Object> resolveServiceProperties()
    {

        Map<String, Object> variableMap = new HashMap<String, Object>();

        VariableType[] variables = properties.getVariableSet().getVariableArray();
        for ( int j = 0; j < variables.length; j++ )
        {

            String xpath = variables[j].getLocation();
            String variableName = variables[j].getName();
            String metric = variables[j].getMetric();
            Object variableValue = "";

            try
            {
                XmlObject[] values = agreementProperties.selectPath( xpath );
                XmlObject selected = values[0];
                variableValue = convertValue( selected, metric );
            }
            catch ( Exception ex )
            {
                variableValue = null;

                String msgResolveError =
                    "Could not resolve variable {0} for [{1}:{2}]. Variable will not be used for guarantee evaluation.";

                String error =
                    MessageFormat.format( msgResolveError,
                        new Object[] { variableName, properties.getServiceName(), properties.getName() } );
                LOG.warn( error );
                continue;
            }

            // and put data in the variable map
            if ( !variableMap.containsKey( variableName ) )
            {

                variableMap.put( variableName, variableValue );

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( LogMessage.getMessage( "Added variable [Name: {0}][Value: {1}]", variableName,
                        variableValue ) );
                }
            }
            else
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( LogMessage.getMessage(
                        "Variable [Name: {0}] already in variable map. Ignore this instance.", variableName ) );
                }
            }
        }
        return variableMap;
    }

    private Object convertValue( XmlObject in, String metric ) throws Exception
    {
        //
        // first convert the value according to the metric specification
        //
        Class<?> target = String.class;
        if ( metrics.containsKey( metric ) )
        {
            target = metrics.get( metric );
        }
        else
        {
            LOG.warn( MessageFormat.format(
                "No convertor configured for metric ''{0}''. Value is treated as string.",
                new Object[] { metric } ) );
        }

        Object result = null;
        String strValue = XmlString.Factory.parse( in.getDomNode() ).getStringValue();

        if ( target == Date.class )
        {
            XmlDateTime dt = XmlDateTime.Factory.parse( in.getDomNode() );
            result = dt.getCalendarValue().getTime();
        }
        else
        {
            try
            {
                result = convertor.convert( strValue, target );
            }
            catch ( Exception e )
            {
                String msgConversionError =
                    "Failed to convert metric with commons beanutils. Start conversion based on schema definition.";
                LOG.debug( msgConversionError, e );
            }
        }

        if ( result != null )
        {
            return result;
        }

        //
        // now try to convert values based on the schema definition
        //
        if ( in instanceof XmlDateTime )
        {
            return ( (XmlDateTime) in ).getCalendarValue().getTime();
        }

        if ( in instanceof XmlDouble )
        {
            return new Double( ( (XmlDouble) in ).getDoubleValue() );
        }

        //
        // if still not succeeded return the string
        //
        LOG.warn( MessageFormat.format(
            "Failed to convert value {0} into metric {1}. Value will be treated as string.", new Object[] {
                strValue, metric } ) );
        return strValue;
    }
}
