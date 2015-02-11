// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.negotiation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.server.engine.EngineInstantiationException;
import org.ogf.graap.wsag.server.engine.WsagEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.exceptions.NegotiationFactoryException;
import org.ogf.graap.wsag.samples.Sample1CreateAgreementAction;
import org.ogf.graap.wsag.samples.SampleAgreement;
//import org.ogf.graap.wsag.client.api.local.LocalAgreementFactoryClient;
import org.ogf.graap.wsag.server.actions.INegotiationAction;
//import org.ogf.graap.wsag.server.actions.Sample1NegotiateAgreementAction;
//import org.ogf.graap.wsag.server.actions.Sample2NegotiateAgreementAction;
import org.ogf.graap.wsag.server.actions.impl.AgreementFactoryAction;
import org.ogf.graap.wsag.server.actions.impl.NegotiationUnsupportedAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationConstraintSectionType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;
import org.ogf.graap.wsag4j.types.configuration.WSAG4JEngineConfigurationDocument;
import org.ogf.graap.wsag4j.types.configuration.WSAG4JEngineConfigurationType;
import org.ogf.graap.wsag.api.Agreement;
//import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
//import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionType;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.server.engine.EngineInstantiationException;
import org.ogf.graap.wsag.server.engine.WsagEngine;
import org.ogf.graap.wsag.server.persistence.impl.PersistentAgreementContainer;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

import org.ogf.graap.wsag.server.persistence.EmfRegistry;

import eu.betaas.taas.bigdatamanager.database.service.IBigDataDatabaseService;

public class NegotiationManager implements NegotiationInterface{

private static Logger LOG = Logger.getLogger("betaas");
	
    private static final Map<String, Object> DEFAULT_ENVIRONMENT = new HashMap<String, Object>();
    
    private static final XmlObject[] DEFAULT_CRITICAL_EXT = new XmlObject[0];

    private static final XmlObject[] DEFAULT_NON_CRITICAL_EXT = new XmlObject[0];	

    public static IBigDataDatabaseService service;
    
    public static BundleContext context;
    
    private ServiceRegistration mInternalServReg;
    
    private WsagEngine engine;
    
    AgreementFactory genericAgreementFactory;
	
    public NegotiationManager(BundleContext c,
			IBigDataDatabaseService s) throws Exception {
    	context = c;
    	service = s;
    	
    	// Initialization
    	
        // Set the entity manager received from the 
        EntityManager em = service.getEntityManager(); 
        EmfRegistry.setEntityManager(em); 
        
        // Create the engine and retrieve the factory
        genericAgreementFactory = getEngine(context);
        
        // Register public interfaces
		LOG.info("Registering internal Negotiation services");
		
		// Selfish, I register myself
		mInternalServReg = context.registerService(NegotiationInterface.class.getName(), 
												   this, 
									               null);
	}
    
    public void stop() throws Exception {
    	// Shutdown the engine
    	engine.shutdown();
    }
    
    // Public interface: get template
    public String getTemplate(String name){ 
    	AgreementTemplateType agreementTemplate = getTemplateWithRequiredTerm( genericAgreementFactory, name ); // "BETaaS-Template"
    
    	String template= agreementTemplate.toString();
    	
    	LOG.info("Template required");
    	
    	return template;
    }
    
    // Public interface: send the offer and get the agreement
    public String sendOffer(String offer)  {
    	
    	try{
    	
	    	// Parse offer
	    	AgreementTemplateType agreementTemplate = AgreementTemplateType.Factory.parse(offer);
	        
	    	//invoke negotiation (generate negotiation offer type, receive counter offers, etc.)
	        AgreementOffer off = new AgreementOfferType(agreementTemplate);
	        
	        // Create agreement
	        Agreement agreement = genericAgreementFactory.createAgreement(off, DEFAULT_ENVIRONMENT);
	        
	        LOG.info("Agreement Created, id: " + agreement.getAgreementId() + " state: " + agreement.getState());
	        
	        return agreement.getAgreementId();
    	} 
    	catch(Exception e){
    		LOG.error(e.getMessage());
    	}
    	return null;
    }

	// Load Engine configuration from file
    private WSAG4JEngineConfigurationType loadEngineConfiguration(BundleContext context) throws Exception
    {
        WSAG4JEngineConfigurationType wsag4JEngineConfigurationType = null;
        final String configFile = "wsag4j-engine.config";

            InputStream in = getClass().getResourceAsStream( configFile );
              
            WSAG4JEngineConfigurationDocument engineConfigurationDocumentn = WSAG4JEngineConfigurationDocument.Factory.parse(in);
            
            wsag4JEngineConfigurationType = engineConfigurationDocumentn.getWSAG4JEngineConfiguration();
            
        return wsag4JEngineConfigurationType;
    }
    
    // Create the Engine
    private AgreementFactory getEngine( BundleContext arg0 ) throws Exception
    {
        // initialize engine
        WSAG4JEngineConfigurationType wsag4JConfiguration = loadEngineConfiguration(arg0);
        LOG.debug( "Engine configuration loaded." );

        
        try
        {
            engine = WsagEngine.getInstance( wsag4JConfiguration );
            if(engine==null)
            	LOG.error( "failed to instantiate engine" );
        }
        catch ( EngineInstantiationException e )
        {
        	LOG.error( "failed to instantiate engine" );

            // call above raises an error, so we will never reach this point
            return null;
        }

        try
        {
        	// We'll be using only one factory
            return engine.list()[0];
        }
        catch ( Exception e )
        {
        	LOG.error( "failed to retrieve agreement factory" );

            // call above raises an error, so we will never reach this point
            return null;
        }
    }
    
    // Retrieve template given the template name
    private AgreementTemplateType getTemplateWithRequiredTerm( AgreementFactory factory, String templateName )
    {
    	
    	AgreementTemplateType[] templates = factory.getTemplates();
    	
        AgreementTemplateType agreementTemplateType = null;
        //System.out.println(templates.length);
        for( int i = 0; i < templates.length; i++ ){
        	//System.out.println("TEMPLATE " + templates[i].getName());
        	if( templates[i].getName().equals(templateName)){
        		agreementTemplateType = templates[i];
        		break;
        	}
        }

        return agreementTemplateType;
    }
	
}
