// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.negotiation;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

/**
* Activator class for the WSAG4J negotiator into the TaaS layer
**/

public class NegotiationActivator {
	private static Logger LOG = Logger.getLogger("betaas");

    public static IBigDataDatabaseService service;
    
    public static BundleContext context;
    
    private NegotiationManager nm;
    
    public void start() throws Exception {
		LOG.debug("WSAG4J Neogtiation started");
		
        LOG.debug("Got database service: "+ service);
        
        // Create the manager
        nm = new NegotiationManager(context, service);
        
	}

	public void stop() throws Exception {
		// On stop we need to shutdown the engine
		nm.stop();
		LOG.debug("WSAG4J Neogtiation stopped");
	}
	public void setService(IBigDataDatabaseService service) throws SQLException {
		// TODO Auto-generated method stub
		this.service = service;
		//this.setConnection(service.getConnection());
	}
	public void setContext(BundleContext context) {
		// TODO Auto-generated method stub
		this.context = context;
		//this.setConnection(service.getConnection());
	}
}
