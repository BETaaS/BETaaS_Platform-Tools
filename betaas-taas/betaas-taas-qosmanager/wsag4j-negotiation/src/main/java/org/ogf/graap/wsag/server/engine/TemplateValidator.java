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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.logging.LogMessage;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag4j.types.configuration.SchemaImportType;
import org.ogf.graap.wsag4j.types.configuration.ValidatorConfigurationDocument;
import org.ogf.graap.wsag4j.types.configuration.ValidatorType;
import org.ogf.graap.wsag4j.types.engine.ConstraintAnnotationDocument;
import org.ogf.graap.wsag4j.types.engine.ConstraintAnnotationType;
import org.ogf.graap.wsag4j.types.engine.ItemCardinalityType;
import org.ogf.schemas.graap.wsAgreement.AgreementOfferDocument;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.AgreementType;
import org.ogf.schemas.graap.wsAgreement.OfferItemType;
import org.ogf.schemas.graap.wsAgreement.OfferItemType.ItemConstraint;
import org.ogf.schemas.graap.wsAgreement.TemplateDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationConstraintType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferItemType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType.Enum;

/**
 * The {@link TemplateValidator} implements the required methods to validate the compliance of an agreement
 * offer with respect to the creation constraints that are defined in the template that was used to create the
 * offer.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class TemplateValidator
{

    private static final String GENERATED_TYPE_NAME = "GeneratedConstraintValidationType";

    private static final String XML_SCHEMA_FILENAME = "/validator/XMLSchema.xml";

    private static final String WSAG_SCHEMA_FILENAME = "/validator/ws-agreement-xsd-types.xsd";

    private static final Logger LOG = Logger.getLogger( TemplateValidator.class );

    private final HashMap<String, Boolean[]> knownSchemaFormChoice = new HashMap<String, Boolean[]>();

    private XmlOptions options = null;

    private ValidatorType configuration;

    //
    // type system loader
    //
    private SchemaTypeLoader loader;

    /**
     * Command line invocation of the validator. For program parameters see info message.
     * 
     * @param args
     *            program arguments, see info
     */
    public static void main( String[] args )
    {
        if ( args.length != 3 )
        {
            LOG.info( "Usage: TemplateValidator agreement_template.xml agreement_offer.xml validator_config.xml" );
            return;
        }

        URL templateURL = System.class.getResource( args[0] );
        URL offerURL = System.class.getResource( args[1] );
        URL configURL = System.class.getResource( args[2] );

        if ( templateURL == null )
        {
            LOG.error( "Template does not exist..." );
            return;
        }
        if ( offerURL == null )
        {
            LOG.error( "Offer does not exist..." );
            return;
        }
        if ( configURL == null )
        {
            LOG.error( "Config file does not exist..." );
            return;
        }

        File templateFile;
        File offerFile;
        File configFile;

        try
        {
            templateFile = new File( templateURL.toURI() );
            offerFile = new File( offerURL.toURI() );
            configFile = new File( configURL.toURI() );
        }
        catch ( Exception ex )
        {
            LOG.error( "Error opening file. Reason: " + ex.getMessage() );
            return;
        }

        if ( !( templateFile.exists() && templateFile.isFile() ) )
        {
            LOG.error( LogMessage.getMessage( "Template file <{0}> does not exist or is a directory...",
                templateURL.toExternalForm() ) );
            return;
        }

        if ( !( offerFile.exists() && offerFile.isFile() ) )
        {
            LOG.error( LogMessage.getMessage( "Offer file <{0}> does not exist or is a directory...",
                offerURL.toExternalForm() ) );
            return;
        }

        if ( !( configFile.exists() && configFile.isFile() ) )
        {
            LOG.error( LogMessage.getMessage( "Config file <{0}> does not exist or is a directory...",
                offerURL.toExternalForm() ) );
            return;
        }

        AgreementTemplateType template;
        try
        {
            template = parseTemplate( templateFile );
        }
        catch ( Exception e )
        {
            LOG.error( "Could not load template file. Reason: " + e.getMessage() );
            return;
        }

        AgreementType offer;
        try
        {
            XmlObject parsedResult = XmlObject.Factory.parse( offerFile );

            if ( parsedResult instanceof AgreementOfferDocument )
            {
                offer = ( (AgreementOfferDocument) parsedResult ).getAgreementOffer();
            }
            else
            {
                Object[] filler =
                    new Object[] { ( parsedResult.schemaType().getName() != null ) ? parsedResult.schemaType()
                                                                                                 .getName()
                                    : parsedResult.schemaType().getDocumentElementName() };
                String message =
                    MessageFormat.format( "Offer file of type {0} is not a valid Agreement Offer Document. ",
                        filler );
                LOG.error( message );
                return;
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Could not load template file. Reason: " + e.getMessage() );
            return;
        }

        ValidatorType validatorConfig;
        try
        {
            XmlObject parsedResult = XmlObject.Factory.parse( configFile );

            if ( parsedResult instanceof ValidatorConfigurationDocument )
            {
                validatorConfig =
                    ( (ValidatorConfigurationDocument) parsedResult ).getValidatorConfiguration();
            }
            else
            {
                Object[] filler =
                    new Object[] { ( parsedResult.schemaType().getName() != null ) ? parsedResult.schemaType()
                                                                                                 .getName()
                                    : parsedResult.schemaType().getDocumentElementName() };

                String msgNoValidConfig =
                    "Config file of type {0} is not a valid Validator Config Document. ";
                String message = MessageFormat.format( msgNoValidConfig, filler );
                LOG.error( message );
                return;
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Could not load config file. Reason: " + e.getMessage() );
            return;
        }

        TemplateValidator validator = new TemplateValidator();
        validator.setConfiguration( validatorConfig );

        AgreementTemplateType offerTemplate = AgreementTemplateType.Factory.newInstance();
        offerTemplate.setAgreementId( offer.getAgreementId() );
        offerTemplate.setTemplateId( "" );
        offerTemplate.setName( offer.getName() );
        offerTemplate.setAgreementId( offer.getAgreementId() );
        offerTemplate.addNewContext().set( offer.getContext() );
        offerTemplate.addNewTerms().set( offer.getTerms() );
        offerTemplate.addNewCreationConstraints();

        AgreementOffer offerInstance = new AgreementOfferType( offerTemplate );

        boolean result = validator.validate( offerInstance, template );
        LOG.info( "Validation result: " + result );
    }

    /**
     * @param templateFile
     * @param template
     * @return
     * @throws Exception
     */
    private static AgreementTemplateType parseTemplate( File templateFile ) throws Exception
    {
        XmlObject parsedResult = XmlObject.Factory.parse( templateFile );

        if ( parsedResult instanceof TemplateDocument )
        {
            return ( (TemplateDocument) parsedResult ).getTemplate();
        }
        else
        {
            Object[] filler =
                new Object[] { ( parsedResult.schemaType().getName() != null ) ? parsedResult.schemaType()
                                                                                             .getName()
                                : parsedResult.schemaType().getDocumentElementName() };
            String message =
                MessageFormat.format(
                    "Template file of type {0} is not a valid Agreement Template Document. ", filler );
            throw new Exception( message );
        }
    }

    /**
     * 
     */
    public TemplateValidator()
    {

        //
        // configure global XmlBeans entity resolver and make sure that
        // the resolver is initialized correctly.
        //
        // TODO: if we use i.e. Xerces as parser for schema parsing,
        // we could omit the GlobalEntityResolver configuration
        //
        System.setProperty( "xmlbean.entityResolver", CatalogResolver.class.getName() );
        if ( ResolverUtil.getGlobalEntityResolver() == null )
        {
            String warn0 =
                "The XmlBeans global entity resolver is not set. "
                    + "This might cause problems in the WSAG4J offer validation process.";

            String warn1 =
                "Make sure that the 'xmlbean.entityResolver' system property is set to {0} "
                    + "before executing the first XmlObject.FACTORY.parse() operation.";

            LOG.warn( warn0 );
            LOG.warn( MessageFormat.format( warn1, new Object[] { CatalogResolver.class.getName() } ) );
        }

        //
        // initialize the XMLOptions
        //
        options = new XmlOptions();
        options.setLoadLineNumbers();
        options.setLoadLineNumbers( XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT );
        options.setLoadMessageDigest();
        options.setSavePrettyPrint();
        options.setSaveOuter();

        //
        // initialize the XML Resolver, essentially we must make sure
        // that the catalog resolver is set
        //
        options.setEntityResolver( new CatalogResolver() );

        //
        // initialize the validator configuration
        //
        configuration = ValidatorType.Factory.newInstance();
        configuration.addNewSchemaImports();
        configuration.getSchemaImports().addNewSchemaFilename().setStringValue( XML_SCHEMA_FILENAME );
        configuration.getSchemaImports().addNewSchemaFilename().setStringValue( WSAG_SCHEMA_FILENAME );
    }

    /**
     * Validates an agreement offer document against a template document.
     * 
     * @param offer
     *            the offer to validate
     * 
     * @param template
     *            the template containing the creation constraints
     * 
     * @return <code>true</code> if the offer is valid, otherwise <code>false</code>
     */
    public boolean validate( AgreementOfferDocument offer, TemplateDocument template )
    {
        return validate( offer, template, new StringBuffer() );
    }

    private boolean validate( AgreementOfferDocument offer, TemplateDocument template, StringBuffer error )
    {

        LOG.debug( "start agreement offer validation process" );
        LOG.debug( LogMessage.getMessage( "offer name: {0}", offer.getAgreementOffer().getName() ) );
        LOG.debug( LogMessage.getMessage( "template name: {0}, id: {1}", template.getTemplate().getName(),
            template.getTemplate().getTemplateId() ) );

        try
        {
            //
            // Parse the template and offer documents in order to make sure XMLBeans uses the
            // WSAG4J type systems during parsing. We need to make sure that our custom types
            // defined in the WSAG4J type system are used when selecting entities via XPath.
            //

            //
            // TODO: For better performance, parsing templates without line numbers
            // could be done using the getDom() method.
            //
            // template = (TemplateDocument)getWSAGLoader().parse(template.getDomNode(),
            // TemplateDocument.type, new
            // XmlOptions().setLoadLineNumbers());
            // offer = (AgreementOfferDocument) getWSAGLoader().parse(offer.getDomNode(),
            // AgreementOfferDocument.type,
            // new XmlOptions().setLoadLineNumbers());
            //
            try
            {
                XmlObject parsedTemplate =
                    getWSAGCompiledTypeLoader().parse( template.xmlText( options ), TemplateDocument.type,
                        new XmlOptions().setLoadLineNumbers() );

                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "Successfully parsed agreement template with WSAJ4J type system" );
                }

                XmlObject parsedOffer =
                    getWSAGCompiledTypeLoader().parse( offer.xmlText( options ), AgreementOfferDocument.type,
                        new XmlOptions().setLoadLineNumbers() );

                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "Successfully parsed agreement offer with WSAJ4J type system" );
                }

                template = (TemplateDocument) parsedTemplate;
                offer = (AgreementOfferDocument) parsedOffer;

                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "Successfully converted template/offer using WSAJ4J build in type system" );
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to parse the AgreementTemplateDocument or AgreementOfferDocument.", e );
                LOG.error( "Agreement offer validation failed." );
                return false;
            }

            //
            // trace the agreement template
            //
            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "Agreement template:\n" + template.xmlText( options ) );
            }

            //
            // validate the template, log errors (if any), and log validation result
            // if the template is not valid, return
            //
            boolean validTemplate = validate( template, error );

            LOG.debug( LogMessage.getMessage( "Template validation result: {0}", validTemplate ) );

            if ( !validTemplate )
            {
                LOG.error( "Agreement offer validation failed. The agreement template document is not valid." );
                return false;
            }

            //
            // trace the agreement offer
            //
            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "Agreement offer:\n" + offer.xmlText( options ) );
            }

            //
            // validate the offer, log errors (if any), and log validation result
            // if the offer is not valid, return
            //
            boolean validOffer = validate( offer, error );

            LOG.debug( LogMessage.getMessage( "Offer validation result: {0}", validOffer ) );

            if ( !validOffer )
            {
                LOG.error( "Agreement offer validation failed. The agreement offer document is not valid." );
                return false;
            }

            //
            // if agreement template and offer are valid, go on with the validation process
            //
            OfferItemType[] items = template.getTemplate().getCreationConstraints().getItemArray();
            for ( int i = 0; i < items.length; i++ )
            {
                if ( !validateConstraint( offer, items[i], error ) )
                {

                    return false;
                }
            }

            return true;

        }
        finally
        {
            LOG.debug( "Finished agreement offer validation process." );
        }
    }

    private boolean validate( NegotiationOfferDocument counterOfferDoc,
                              NegotiationOfferDocument parentOfferDoc, StringBuffer error )
    {
        NegotiationOfferType counterOffer = counterOfferDoc.getNegotiationOffer();
        NegotiationOfferType parentOffer = parentOfferDoc.getNegotiationOffer();

        boolean validationResult = true;

        //
        // check, if this is the correct (counter) offer combination
        //
        String offerId = parentOffer.getOfferId();
        String counterOfferId = counterOffer.getNegotiationOfferContext().getCounterOfferTo();

        if ( !offerId.equals( counterOfferId ) )
        {
            String msgText = "(Counter) Offer combination is not valid [''{0}'' <-> ''{1}''].";
            LOG.debug( LogMessage.getMessage( msgText, offerId, counterOfferId ) );

            validationResult = false;
        }

        LOG.debug( "start agreement offer validation process" );
        Enum parentCreator = parentOffer.getNegotiationOfferContext().getCreator();
        LOG.debug( LogMessage.getMessage( "offer creator: {0}", parentCreator ) );
        LOG.debug( LogMessage.getMessage( "counter offer name: {0}, id: {1}",
            counterOffer.getNegotiationOfferContext().getCounterOfferTo(), counterOffer.getOfferId() ) );

        try
        {
            try
            {

                XmlObject parsedCounterOffer = parseOffer( counterOfferDoc );
                LOG.trace( "Successfully parsed agreement template with WSAG4J type system." );

                XmlObject parsedOffer = parseOffer( parentOfferDoc );
                LOG.trace( "Successfully parsed agreement offer with WSAG4J type system" );

                counterOffer = ( (NegotiationOfferDocument) parsedCounterOffer ).getNegotiationOffer();
                parentOffer = ( (NegotiationOfferDocument) parsedOffer ).getNegotiationOffer();
                LOG.trace( "Successfully converted counter offer/offer using WSAJ4J build in type system" );
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to parse the NegotiationOfferType or AgreementOfferDocument.", e );
                LOG.error( "Agreement counter offer validation failed." );

                validationResult = false;
            }

            //
            // trace the agreement template
            //
            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "Agreement counter offer:\n" + counterOffer.xmlText( options ) );
            }

            //
            // validate the template, log errors (if any), and log validation result
            // if the template is not valid, return
            //
            boolean validCounterOffer = validate( counterOffer, error );

            LOG.debug( LogMessage.getMessage( "Counter offer validation result: {0}", validCounterOffer ) );

            if ( !validCounterOffer )
            {
                LOG.error( "Counter offer validation failed. The counter offer document is not valid." );
                validationResult = false;
            }

            //
            // trace the agreement offer
            //
            LOG.trace( LogMessage.getMessage( "Agreement offer:\n{0}", parentOffer.xmlText( options ) ) );

            //
            // validate the offer, log errors (if any), and log validation result
            // if the offer is not valid, return
            //
            boolean validOffer = validate( parentOffer, error );

            LOG.debug( LogMessage.getMessage( "Offer validation result: {0}", validOffer ) );

            if ( !validOffer )
            {
                validationResult = false;
                LOG.error( "Agreement offer validation failed. The agreement offer document is not valid." );
            }

            //
            // if agreement template and offer are valid, go on with the validation process
            //

            NegotiationOfferItemType[] items = parentOffer.getNegotiationConstraints().getItemArray();
            for ( int i = 0; i < items.length; i++ )
            {
                if ( !validateConstraint( counterOfferDoc, items[i], error ) )
                {
                    if ( items[i].getType() == NegotiationConstraintType.OPTIONAL )
                    {
                        if ( LOG.isInfoEnabled() )
                        {
                            LOG.info( "Validation of an optional term failed. Continue with the term validation." );
                        }
                        continue;
                    }

                    validationResult = false;
                    break;
                }
            }
        }
        finally
        {
            final String message = "Finished agreement offer validation process. Result is: {0}";
            LOG.debug( LogMessage.getMessage( message, validationResult ) );
        }

        return validationResult;
    }

    /**
     * @param offerDoc
     * @return
     * @throws Exception
     */
    private NegotiationOfferDocument parseOffer( NegotiationOfferDocument offerDoc ) throws Exception
    {
        //
        // Parse the template and offer documents in order to make sure XMLBeans uses the
        // WSAG4J type systems during parsing. We need to make sure that our custom types
        // defined in the WSAG4J type system are used when selecting entities via XPath.
        //

        //
        // TODO: For better performance, parsing templates without line numbers
        // could be done using the getDom() method.
        //
        // template = (TemplateDocument)getWSAGLoader().parse(template.getDomNode(), TemplateDocument.type,
        // new
        // XmlOptions().setLoadLineNumbers());
        // offer = (AgreementOfferDocument) getWSAGLoader().parse(offer.getDomNode(),
        // AgreementOfferDocument.type,
        // new XmlOptions().setLoadLineNumbers());
        //

        return (NegotiationOfferDocument) getWSAGCompiledTypeLoader().parse( offerDoc.xmlText( options ),
            NegotiationOfferDocument.type, new XmlOptions().setLoadLineNumbers() );
    }

    /**
     * Validates an agreement offer against a template.
     * 
     * @param offer
     *            the offer to validate
     * 
     * @param template
     *            the template containing the creation constraints
     * 
     * @param error
     *            contains the error message if the validation process failed
     * 
     * @return <code>true</code> if the offer is valid, otherwise <code>false</code>
     */
    public boolean validate( AgreementOffer offer, AgreementTemplateType template, StringBuffer error )
    {
        AgreementOfferDocument offerDoc = AgreementOfferDocument.Factory.newInstance( options );

        offerDoc.addNewAgreementOffer();
        offerDoc.getAgreementOffer().setName( offer.getName() );
        offerDoc.getAgreementOffer().setContext( offer.getContext() );
        offerDoc.getAgreementOffer().setTerms( offer.getTerms() );

        TemplateDocument templateDoc = TemplateDocument.Factory.newInstance();
        templateDoc.addNewTemplate().set( template );

        return validate( offerDoc, templateDoc, error );
    }

    /**
     * Validates an agreement offer against a template.
     * 
     * @param offer
     *            the offer to validate
     * 
     * @param template
     *            the template containing the creation constraints
     * 
     * @return <code>true</code> if the offer is valid, otherwise <code>false</code>
     */
    public boolean validate( AgreementOffer offer, AgreementTemplateType template )
    {
        return validate( offer, template, new StringBuffer() );
    }

    /**
     * Validates a negotiation offer against a template.
     * 
     * @param offer
     *            the offer to validate
     * 
     * @param template
     *            the template containing the creation constraints
     * 
     * @return <code>true</code> if the offer is valid, otherwise <code>false</code>
     */
    public boolean validate( NegotiationOfferType offer, AgreementTemplateType template )
    {
        AgreementOfferDocument offerDoc = AgreementOfferDocument.Factory.newInstance( options );

        offerDoc.addNewAgreementOffer();
        offerDoc.getAgreementOffer().setName( offer.getName() );
        offerDoc.getAgreementOffer().setContext( offer.getContext() );
        offerDoc.getAgreementOffer().setTerms( offer.getTerms() );

        TemplateDocument templateDoc = TemplateDocument.Factory.newInstance();
        templateDoc.addNewTemplate().set( template );

        return validate( offerDoc, templateDoc );
    }

    /**
     * Validates a negotiation counter offer against a negotiation offer.
     * 
     * @param offer
     *            the offer containing the negotiation constraints
     * 
     * @param counterOffer
     *            the counter offer to validate
     * 
     * @return <code>true</code> if the offer is valid, otherwise <code>false</code>
     */
    public boolean validate( NegotiationOfferType offer, NegotiationOfferType counterOffer )
    {
        NegotiationOfferDocument offerDoc = NegotiationOfferDocument.Factory.newInstance( options );
        offerDoc.addNewNegotiationOffer();
        offerDoc.getNegotiationOffer().setOfferId( offer.getOfferId() );
        offerDoc.getNegotiationOffer().setName( offer.getName() );
        offerDoc.getNegotiationOffer().setContext( offer.getContext() );
        offerDoc.getNegotiationOffer().setTerms( offer.getTerms() );
        offerDoc.getNegotiationOffer().setNegotiationConstraints( offer.getNegotiationConstraints() );
        offerDoc.getNegotiationOffer().setNegotiationOfferContext( offer.getNegotiationOfferContext() );

        NegotiationOfferDocument counterOfferDoc = NegotiationOfferDocument.Factory.newInstance( options );
        counterOfferDoc.addNewNegotiationOffer();
        counterOfferDoc.getNegotiationOffer().setOfferId( counterOffer.getOfferId() );
        counterOfferDoc.getNegotiationOffer().setName( counterOffer.getName() );
        counterOfferDoc.getNegotiationOffer().setContext( counterOffer.getContext() );
        counterOfferDoc.getNegotiationOffer().setTerms( counterOffer.getTerms() );
        counterOfferDoc.getNegotiationOffer().setNegotiationConstraints(
            counterOffer.getNegotiationConstraints() );
        counterOfferDoc.getNegotiationOffer().setNegotiationOfferContext(
            counterOffer.getNegotiationOfferContext() );

        return validate( offerDoc, counterOfferDoc, new StringBuffer() );
    }

    /**
     * Returns the validator configuration.
     * 
     * @return the validator configuration
     */
    public ValidatorType getConfiguration()
    {
        return configuration;
    }

    /**
     * Sets the validator configuration. The configuration contains a set of XML schemas that are used to
     * validate the XML documents (template, offer, couonter offer).
     * 
     * @param configuration
     *            the validator configuration
     */
    public void setConfiguration( ValidatorType configuration )
    {
        this.configuration = configuration;
    }

    private boolean validateConstraint( XmlObject target, OfferItemType item, StringBuffer error )
    {

        try
        {
            boolean result = true;

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "**************************************************************"
                    + "********************************************************************************" );
                LOG.trace( "validation of item constraint:\n" + item.xmlText( options ) );
                LOG.trace( "--------------------------------------------------------------"
                    + "--------------------------------------------------------------------------------" );
            }

            XmlObject[] items = target.selectPath( item.getLocation() );

            //
            // Check whether a cardinality of the expected selection result is specified
            // an whether the result fits to the specified selection constraints
            //
            result = checkItemCardinality( item, items );
            if ( !result )
            {
                return result;
            }

            //
            // create schema for type validation
            //
            HashMap<SchemaType, SchemaTypeLoader> schemaLoaderMap =
                new HashMap<SchemaType, SchemaTypeLoader>();
            HashMap<SchemaType, SchemaType> schemaTypeMap = new HashMap<SchemaType, SchemaType>();

            for ( int i = 0; i < items.length; i++ )
            {

                SchemaType sourcetype = getSourceType( items[i] );

                if ( !schemaTypeMap.containsKey( sourcetype ) )
                {

                    SchemaDocument schema = initializeSchema( sourcetype );

                    Schema generatedSchema =
                        createSchemaType( schema.getSchema(), sourcetype, item.getItemConstraint() );

                    if ( LOG.isTraceEnabled() )
                    {
                        LOG.trace( "generated schema for type [" + sourcetype.getName() + "]" );
                        LOG.trace( "--------------------------------------------------------------------" );
                        LOG.trace( "generated xml schema:\n" + generatedSchema.xmlText( options ) );
                    }

                    try
                    {
                        QName generatedTypeQName =
                            new QName( sourcetype.getName().getNamespaceURI(), GENERATED_TYPE_NAME, "wsag4j" );
                        SchemaTypeLoader schemaLoader = getLoader( generatedSchema );
                        SchemaType schemaType = schemaLoader.findType( generatedTypeQName );
                        schemaTypeMap.put( sourcetype, schemaType );
                        schemaLoaderMap.put( sourcetype, schemaLoader );
                    }
                    catch ( Exception e )
                    {
                        LOG.debug( LogMessage.getMessage(
                            "Failed to create schema for item constraint. Error: {0}", e.getMessage() ) );

                        LOG.debug( LogMessage.getMessage( "{0}", item.xmlText( options ) ) );

                        String message =
                            LogMessage.format(
                                "Failed to create schema for agreement offer validation. Error: {0}",
                                e.getMessage() );

                        LOG.error( message );
                        error.append( message );

                        return false;
                    }
                }
            }

            for ( int i = 0; i < items.length; i++ )
            {

                XmlOptions serializeOptions = new XmlOptions( options );
                serializeOptions.setSaveOuter();

                String serializedItem = items[i].xmlText( serializeOptions );

                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "restricted item:\n" + serializedItem );
                    LOG.trace( "document validation result: " + items[i].validate() );
                }

                try
                {
                    SchemaType sourceType = getSourceType( items[i] );

                    SchemaType restrictionType = schemaTypeMap.get( sourceType );

                    XmlOptions schemaOptions = new XmlOptions( options );
                    schemaOptions.setLoadReplaceDocumentElement( null );
                    schemaOptions.setDocumentType( restrictionType );

                    //
                    // TODO: For better performance, parsing templates without line numbers
                    // could be done using the getDom() method.
                    //
                    SchemaTypeLoader schemaLoader = schemaLoaderMap.get( sourceType );

                    XmlObject check = schemaLoader.parse( serializedItem, restrictionType, schemaOptions );
                    result = result && validate( check, error );

                }
                catch ( Exception e )
                {
                    LOG.error( "Could not parse target element: " + e.getMessage() );

                    result = false;
                    break;
                }
            }

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( MessageFormat.format( "Item constraint validation result: {0}",
                    new Object[] { ( result ) ? "successful" : "failed" } ) );
            }

            return result;
        }
        catch ( Exception ex )
        {
            LOG.error( "Failed to validate creation constraint: " + ex.getMessage() );
            return false;
        }
    }

    /**
     * @param sourcetype
     * @return
     */
    private SchemaDocument initializeSchema( SchemaType sourcetype )
    {
        SchemaDocument schema = SchemaDocument.Factory.newInstance();
        schema.addNewSchema();

        //
        // FIXME: add support for anonymous type validation
        //
        // TODO: sourcetype.getName().getNamespaceURI() does not work for anonymous
        // type declarations, such as:
        //
        // <element>
        // <complexType>
        // </complexType>
        // </element>
        //
        schema.getSchema().setTargetNamespace( sourcetype.getName().getNamespaceURI() );
        schema.getSchema().setVersion( "1.0" );

        schema.getSchema().setElementFormDefault( FormChoice.UNQUALIFIED );
        schema.getSchema().setAttributeFormDefault( FormChoice.UNQUALIFIED );

        String sourceNamespace = sourcetype.getName().getNamespaceURI();
        if ( knownSchemaFormChoice.containsKey( sourceNamespace ) )
        {
            Boolean[] efq = knownSchemaFormChoice.get( sourceNamespace );
            boolean isElementQualified = efq[0].booleanValue();
            boolean isAttributeQualified = efq[1].booleanValue();

            if ( isElementQualified )
            {
                schema.getSchema().setElementFormDefault( FormChoice.QUALIFIED );
            }
            if ( isAttributeQualified )
            {
                schema.getSchema().setAttributeFormDefault( FormChoice.QUALIFIED );
            }
        }
        return schema;
    }

    /**
     * @param item
     * @param items
     * @param annotation
     */
    private boolean checkItemCardinality( OfferItemType item, XmlObject[] items )
    {

        ConstraintAnnotationType annotation = ConstraintAnnotationType.Factory.newInstance();
        annotation.setMultiplicity( ItemCardinalityType.X_0_TO_N );
        XmlObject[] cadinalityDoc =
            item.selectChildren( ConstraintAnnotationDocument.type.getDocumentElementName() );
        if ( cadinalityDoc.length > 0 )
        {
            annotation = (ConstraintAnnotationType) cadinalityDoc[0];
        }

        switch ( annotation.getMultiplicity().intValue() )
        {
            case ItemCardinalityType.INT_X_0_TO_1:

                if ( items.length > 1 )
                {
                    LogMessage message =
                        LogMessage.getMessage( "Selected {0} elements for item constraint {1}, "
                            + "but constraint annotation specified multiplicity of 0..1", items.length,
                            item.getName() );
                    LOG.error( message );

                    return false;
                }
                return true;

            case ItemCardinalityType.INT_X_1:

                if ( items.length != 1 )
                {
                    LogMessage message =
                        LogMessage.getMessage( "Selected {0} elements for item constraint {1}, "
                            + "but constraint annotation specified multiplicity of 1", items.length,
                            item.getName() );
                    LOG.error( message );

                    return false;
                }
                return true;

            case ItemCardinalityType.INT_X_1_TO_N:

                if ( items.length == 0 )
                {
                    LogMessage message =
                        LogMessage.getMessage( "Selected 0 elements for item constraint {0}, "
                            + "but constraint annotation specified multiplicity of 1..N", item.getName() );
                    LOG.error( message );

                    return false;
                }
                return true;

            default:
                //
                // is satisfied anyway
                //
                return true;
        }
    }

    private boolean validate( XmlObject object, StringBuffer error )
    {
        ArrayList<XmlError> list = new ArrayList<XmlError>();

        XmlOptions voptions = new XmlOptions( options );
        voptions.setErrorListener( list );

        if ( !object.validate( voptions ) )
        {
            for ( int i = 0; i < list.size(); i++ )
            {
                if ( LOG.isDebugEnabled() )
                {
                    XmlError e = list.get( i );
                    String message =
                        MessageFormat.format( "Type validation error [line {0}]: {1}. Code: {2}",
                            e.getLine(), e.getMessage(), e.getErrorCode() );
                    error.append( message + "\n" );
                    LOG.debug( message );
                }
            }

            return false;
        }

        return true;
    }

    private SchemaType getSourceType( XmlObject item ) throws Exception
    {
        SchemaType sourcetype = item.schemaType().getPrimitiveType();

        if ( sourcetype == null )
        {
            sourcetype = item.schemaType();

            if ( sourcetype.isNoType() )
            {
                LOG.error( "No type information found for restricted item:" );
                LOG.error( item.xmlText( options ) );

                throw new Exception( "No type information found for item: " + item.xmlText() );
            }
        }

        return sourcetype;
    }

    private Schema createSchemaType( Schema schema, SchemaType type, ItemConstraint constraint )
    {

        Schema result = null;

        // We first check the type of the constraint. We can have a
        // typeDefParticle or a simpleRestrictionModel constraint
        if ( constraint.isSetAll() || constraint.isSetChoice() || constraint.isSetGroup()
            || constraint.isSetSequence() )
        {

            result = createTypeDefParticleSchema( schema, type, constraint );
        }
        else
        {
            result = createSimpleRestrictionModelSchema( schema, type, constraint );
        }

        return result;
    }

    private Schema createSimpleRestrictionModelSchema( Schema schema, SchemaType type,
                                                       ItemConstraint constraint )
    {
        QName typeName =
            new QName( "http://wsag4j.scai.fraunhofer.de/generated", "GeneratedConstraintValidationType",
                "wsag4j" );

        SimpleType simple = schema.addNewSimpleType();
        simple.setName( typeName.getLocalPart() );
        simple.addNewRestriction();

        if ( constraint.isSetSimpleType() )
        {
            simple.getRestriction().setSimpleType( constraint.getSimpleType() );
        }
        else
        {
            simple.getRestriction().setBase( type.getPrimitiveType().getName() );
        }

        simple.getRestriction().setLengthArray( constraint.getLengthArray() );

        simple.getRestriction().setMinInclusiveArray( constraint.getMinInclusiveArray() );
        simple.getRestriction().setMaxInclusiveArray( constraint.getMaxInclusiveArray() );

        simple.getRestriction().setMinExclusiveArray( constraint.getMinExclusiveArray() );
        simple.getRestriction().setMaxExclusiveArray( constraint.getMaxExclusiveArray() );

        simple.getRestriction().setEnumerationArray( constraint.getEnumerationArray() );

        simple.getRestriction().setLengthArray( constraint.getLengthArray() );
        simple.getRestriction().setMaxLengthArray( constraint.getMaxLengthArray() );
        simple.getRestriction().setMinLengthArray( constraint.getMinLengthArray() );

        simple.getRestriction().setFractionDigitsArray( constraint.getFractionDigitsArray() );

        simple.getRestriction().setPatternArray( constraint.getPatternArray() );
        simple.getRestriction().setTotalDigitsArray( constraint.getTotalDigitsArray() );
        simple.getRestriction().setWhiteSpaceArray( constraint.getWhiteSpaceArray() );

        return schema;
    }

    private Schema createTypeDefParticleSchema( Schema schema, SchemaType type, ItemConstraint constraint )
    {
        ComplexType complex = schema.addNewComplexType();
        complex.setName( GENERATED_TYPE_NAME );

        ComplexRestrictionType restriction = complex.addNewComplexContent().addNewRestriction();
        restriction.setBase( type.getName() );

        if ( constraint.isSetAll() )
        {
            restriction.setAll( constraint.getAll() );
        }
        if ( constraint.isSetChoice() )
        {
            restriction.setChoice( constraint.getChoice() );
        }
        if ( constraint.isSetSequence() )
        {
            restriction.setSequence( constraint.getSequence() );
        }
        if ( constraint.isSetGroup() )
        {
            restriction.setGroup( constraint.getGroup() );
        }

        return schema;
    }

    /**
     * This method mixes dynamic loaded type systems (defined by the validator configuration) with the
     * compiled WSAG4J-types type system. This means we can use it in order to validate agreement template and
     * offer documents, but we must not use it for constraint validation. (mixed type systems will produce
     * errors due to a XMLBeans bug)
     * 
     * @return mixed WSAG4J type system loader
     * @throws Exception
     */
    private SchemaTypeLoader getWSAGCompiledTypeLoader() throws Exception
    {
        SchemaTypeLoader dynamicLoader = getLoader();
        SchemaTypeLoader compiledLoader = AgreementTemplateType.type.getTypeSystem();
        SchemaTypeLoader compiledNegotiationLoader = NegotiationOfferDocument.type.getTypeSystem();
        SchemaTypeLoader compiledLoaderEngine = ConstraintAnnotationType.type.getTypeSystem();

        return XmlBeans.typeLoaderUnion( new SchemaTypeLoader[] { compiledLoader, compiledNegotiationLoader,
            compiledLoaderEngine, dynamicLoader } );
    }

    /**
     * Creates a type system loader based on the validator configuration. This loader is completely compiled
     * from XML schema files specified in the validator configuration.
     * 
     * @param schema
     *            an additional schema file to include in the type system loader
     * @return union of the validator type system loader and the loader of the provided schema file
     * @throws Exception
     */
    private synchronized SchemaTypeLoader getLoader( Schema schema ) throws Exception
    {

        //
        // First, we get the WSAG loader. This loader contains all global type systems
        // for the WSAG4J engine.
        //
        SchemaTypeLoader wsagLoader = getLoader();

        //
        // now we parse and compile the schema while using the global type system
        //
        SchemaDocument importSchema = SchemaDocument.Factory.parse( schema.getDomNode() );
        SchemaTypeSystem schemats =
            XmlBeans.compileXsd( new XmlObject[] { importSchema }, wsagLoader, options );

        //
        // the last step is to do a type loader union of our local schema and our global type systems
        //
        return XmlBeans.typeLoaderUnion( new SchemaTypeLoader[] { schemats, wsagLoader } );
    }

    /**
     * Creates a type system loader based on the validator configuration. This loader is completely compiled
     * from XML schema files specified in the validator configuration.
     * 
     * @return the validator type system loader
     * @throws Exception
     */
    private synchronized SchemaTypeLoader getLoader() throws Exception
    {
        //
        // If the WSAG4J Loader is not initialized, do the initialization
        // based on the validator configuration.
        //
        if ( loader == null )
        {

            // Remove for now, this would introduce system specific dependencies
            // XmlOptions parserOptions = new XmlOptions();
            // parserOptions.setLoadUseXMLReader( SAXParserFactory.newInstance().newSAXParser().getXMLReader()
            // );

            Vector<SchemaTypeSystem> wsag4jTypeSystems = new Vector<SchemaTypeSystem>();

            //
            // add the build in type system as initial type system
            //
            wsag4jTypeSystems.add( XmlBeans.getBuiltinTypeSystem() );

            //
            // for each explicitly referenced schema, create a new type system
            // and add the type system as a wsag4j type system
            //
            SchemaImportType imports = getConfiguration().getSchemaImports();
            if ( imports != null )
            {
                String[] schemaFilenames = imports.getSchemaFilenameArray();
                for ( int i = 0; i < schemaFilenames.length; i++ )
                {
                    try
                    {
                        InputStream resource =
                            TemplateValidator.class.getResourceAsStream( schemaFilenames[i] );
                        SchemaDocument importSchema = SchemaDocument.Factory.parse( resource );

                        if ( !knownSchemaFormChoice.containsKey( importSchema.getSchema()
                                                                             .getTargetNamespace() ) )
                        {
                            boolean isAttributeQualified = false;
                            boolean isElementQualified = false;

                            if ( importSchema.getSchema().isSetAttributeFormDefault() )
                            {
                                isAttributeQualified =
                                    importSchema.getSchema().getAttributeFormDefault() == FormChoice.QUALIFIED;
                            }

                            if ( importSchema.getSchema().isSetElementFormDefault() )
                            {
                                isElementQualified =
                                    importSchema.getSchema().getElementFormDefault() == FormChoice.QUALIFIED;
                            }

                            knownSchemaFormChoice.put(
                                importSchema.getSchema().getTargetNamespace(),
                                new Boolean[] { Boolean.valueOf( isElementQualified ),
                                    Boolean.valueOf( isAttributeQualified ) } );
                        }

                        SchemaTypeSystem schemats =
                            XmlBeans.compileXsd( new XmlObject[] { importSchema }, loader, options );
                        wsag4jTypeSystems.add( schemats );

                        LOG.debug( LogMessage.getMessage( "Loaded schema file {0}", schemaFilenames[i] ) );

                    }
                    catch ( Exception e )
                    {
                        LOG.error( LogMessage.getMessage( "Could not load imported schema {0}. Error: {1}",
                            schemaFilenames[i], e.getMessage() ) );
                        LOG.debug( e );
                        LOG.error( "Hint: check the order of the import schema entries in the wsag4j config file." );
                    }
                }
            }

            SchemaTypeLoader[] typeSystem =
                wsag4jTypeSystems.toArray( new SchemaTypeLoader[wsag4jTypeSystems.size()] );

            loader = XmlBeans.typeLoaderUnion( typeSystem );
        }

        return loader;
    }
}
