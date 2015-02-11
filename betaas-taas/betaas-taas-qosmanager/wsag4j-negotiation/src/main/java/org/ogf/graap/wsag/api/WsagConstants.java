/* 
 * Copyright (c) 2005-2011, Fraunhofer-Gesellschaft
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

import javax.xml.namespace.QName;

/**
 * AgreementConstants
 * 
 * @author Oliver Waeldrich
 */
public class WsagConstants
{

    /**
     * default WSAG4J properties file name
     */
    public static final String WSAG4J_CONFIG_FILE = "/wsag4j.properties";

    /**
     * default WSAG4J client properties file name
     */
    public static final String WSAG4J_CLIENT_CONFIG_FILE = "/wsag4j-client.properties";

    /**
     * default WSAG4J WSRF engine configuration file name
     */
    public static final String WSAG4J_WSRF_ENGINE_CONFIG_FILE = "/wsrf-engine.config";

    /**
     * Application specific WSAG4J JAAS configuration file name.
     */
    public static final String WSAG4J_JAAS_CONFIG_FILE = "/jaas.config";

    /**
     * Default WSAG4J JAAS configuration file name packaged with client implementations.
     */
    public static final String WSAG4J_JAAS_CONFIG_FILE_DEFAULT = "/META-INF/wsag4j-jaas.config";

    /**
     * WSAG4J gateway URL system property key. Overwrites gateway URL in WSAG4J engine at runtime.
     */
    public static final String WSAG4J_GATEWAY_PROPERTY = "wsag4j.gateway.address";

    /**
     * WSAG4J constant that is used to lookup the factory configuration in the
     * <code>org.ogf.graap.wsag.server.api.IAgreementFactoryContext</code>.
     */
    public static final String WSAG4J_FACTORY_CONFIGURATION =
        "http://ogf.org/wsag4j/properties/factory-configuration";

    /*
     * WSAG related namespace and type declarations
     */

    /**
     * WS-Agreement namespace prefix
     */
    public static final String PREFIX = "wsag";

    /**
     * WS-Agreement namespace prefix declaration
     */
    public static final String WSAG_PREFIX_DECLARATION = "xmlns:" + PREFIX;

    /**
     * WS-Agreement namespace URI
     */
    public static final String NAMESPACE_URI = "http://schemas.ggf.org/graap/2007/03/ws-agreement";

    /**
     * WS-Agreement Agreement Factory QName
     */
    public static final QName WSAG_AGREEMENT_FACTORY_QNAME = new QName( NAMESPACE_URI, "AgreementFactory" );

    /**
     * WS-Agreement Agreement QName
     */
    public static final QName WSAG_AGREEMENT_QNAME = new QName( NAMESPACE_URI, "Agreement" );

    /**
     * WS-Agreement Agreement Acceptance QName
     */
    public static final QName WSAG_AGREEMENT_ACCEPTANCE_QNAME = new QName( NAMESPACE_URI,
        "AgreementAcceptance" );

    /**
     * WS-Agreement Create Agreement QName
     */
    public static final QName WSAG_CREATE_AGREEMENT_QNAME = new QName( NAMESPACE_URI, "CreateAgreement" );

    /**
     * WS-Agreement Create Agreement Input QName
     */
    public static final QName WSAG_CREATE_AGREEMENT_INPUT_QNAME = new QName( NAMESPACE_URI,
        "CreateAgreementInput" );

    /**
     * WS-Agreement Create Agreement WSA Action QName
     */
    public static final String WSAG_CREATE_AGREEMENT_ACTION = NAMESPACE_URI + "/CreateAgreementRequest";

    /**
     * WS-Agreement Create Pending Agreement QName
     */
    public static final QName WSAG_CREATE_PENDING_AGREEMENT_QNAME = new QName( NAMESPACE_URI,
        "CreatePendingAgreement" );

    /**
     * WS-Agreement Create Pending Agreement Input QName
     */
    public static final QName WSAG_CREATE_PENDING_AGREEMENT_INPUT_QNAME = new QName( NAMESPACE_URI,
        "CreatePendingAgreementInput" );

    /**
     * WS-Agreement Create Pending Agreement WSA Action QName
     */
    public static final String WSAG_CREATE_PENDING_AGREEMENT_ACTION = NAMESPACE_URI
        + "/CreatePendingAgreementRequest";

    /**
     * WS-Agreement Terminate Agreement QName
     */
    public static final QName WSAG_TERMINATE_AGREEMENT_QNAME = new QName( NAMESPACE_URI, "Terminate" );

    /**
     * WS-Agreement Terminate Agreement Input QName
     */
    public static final QName WSAG_TERMINATE_AGREEMENT_INPUT_QNAME = new QName( NAMESPACE_URI,
        "TerminateInput" );

    /**
     * WS-Agreement Terminate Agreement WSA Action QName
     */
    public static final String WSAG_TERMINATE_AGREEMENT_ACTION = NAMESPACE_URI + "/TerminateRequest";

    /**
     * WS-Agreement Accept Agreement QName
     */
    public static final QName WSAG_ACCEPT_AGREEMENT_QNAME = new QName( NAMESPACE_URI, "AcceptAgreement" );

    /**
     * WS-Agreement Accept Agreement Input QName
     */
    public static final QName WSAG_ACCEPT_AGREEMENT_INPUT_QNAME = new QName( NAMESPACE_URI,
        "AcceptAgreementInput" );

    /**
     * WS-Agreement Accept Agreement WSA Action QName
     */
    public static final String WSAG_ACCEPT_AGREEMENT_ACTION = NAMESPACE_URI + "/AcceptAgreementRequest";

    /**
     * WS-Agreement Reject Agreement QName
     */
    public static final QName WSAG_REJECT_AGREEMENT_QNAME = new QName( NAMESPACE_URI, "RejectAgreement" );

    /**
     * WS-Agreement Reject Agreement Input QName
     */
    public static final QName WSAG_REJECT_AGREEMENT_INPUT_QNAME = new QName( NAMESPACE_URI,
        "RejectAgreementInput" );

    /**
     * WS-Agreement Reject Agreement WSA Action QName
     */
    public static final String WSAG_REJECT_AGREEMENT_ACTION = NAMESPACE_URI + "/RejectAgreementRequest";

    /**
     * WS-Agreement Continuing Fault QName
     */
    public static final QName WSAG_CONTINUING_FAULT_QNAME = new QName( NAMESPACE_URI, "ContinuingFault" );

    /**
     * WS-Agreement Agreement Factory Resource Properties QNames
     */
    public static final QName[] WSAG_AGREEMENT_FACTORY_PROPERTIES = new QName[] { new QName( NAMESPACE_URI,
        "Template", PREFIX ) };

    /**
     * WS-Agreement Agreement Resource Properties QNames
     */
    public static final QName[] WSAG_AGREEMENT_PROPERTIES = new QName[] {
        new QName( NAMESPACE_URI, "Name", PREFIX ), new QName( NAMESPACE_URI, "AgreementId", PREFIX ),
        new QName( NAMESPACE_URI, "Context", PREFIX ), new QName( NAMESPACE_URI, "Terms", "wsag" ),
        new QName( NAMESPACE_URI, "AgreementState", PREFIX ),
        new QName( NAMESPACE_URI, "GuaranteeTermState", PREFIX ),
        new QName( NAMESPACE_URI, "ServiceTermState", PREFIX ) };

    /**
     * Negotiation Namespace URI
     * 
     * @deprecated
     */
    @Deprecated
    public static final String NEGOTIATION_NAMESPACE_URI =
        "http://schemas.ggf.org/graap/2008/12/ws-agreement-negotiation";

    /**
     * Negotiation QName
     * 
     * @deprecated
     */
    @Deprecated
    public static final QName WSAG_NEGOTIATE_QNAME = new QName( NEGOTIATION_NAMESPACE_URI, "Negotiate" );

    /**
     * Negotiation Input QName
     * 
     * @deprecated
     */
    @Deprecated
    public static final QName WSAG_NEGOTIATE_INPUT_QNAME = new QName( NEGOTIATION_NAMESPACE_URI,
        "NegotiateInput" );

    /**
     * Negotiation WSA Action QName
     * 
     * @deprecated
     */
    @Deprecated
    public static final String WSAG_NEGOTIATE_ACTION = NEGOTIATION_NAMESPACE_URI + "/NegotiateRequest";

    //
    // Negotiation constants
    //

    /**
     * WS-Agreement Negotiation Namespace URI
     */
    public static final String WSAG_NEGOTIATION_NAMESPACE_URI =
        "http://schemas.ogf.org/graap/2009/11/ws-agreement-negotiation";

    /**
     * WS-Agreement Negotiation Namespace Prefix
     */
    public static final String WSAG_NEGOTIATION_PREFIX = "wsag-neg";

    /**
     * WS-Agreement Negotiation Namespace Prefix Declaration
     */
    public static final String WSAG_NEGOTIATION_PREFIX_DECLARATION = "xmlns:" + WSAG_NEGOTIATION_PREFIX;

    /**
     * WS-Agreement Negotiation QName
     */
    public static final QName WSAG_NEGOTIATION_QNAME = new QName( WSAG_NEGOTIATION_NAMESPACE_URI,
        "Negotiation" );

    /**
     * WS-Agreement Negotiation Resource Properties QNames
     */
    public static final QName[] WSAG_NEGOTIATION_PROPERTIES = new QName[] {
        new QName( WSAG_NEGOTIATION_NAMESPACE_URI, "NegotiationContext", WSAG_NEGOTIATION_PREFIX ),
        new QName( WSAG_NEGOTIATION_NAMESPACE_URI, "NegotiableTemplate", WSAG_NEGOTIATION_PREFIX ),
        new QName( WSAG_NEGOTIATION_NAMESPACE_URI, "NegotiationOffer", WSAG_NEGOTIATION_PREFIX ), };

    /**
     * WS-Agreement Negotiation Factory InitiateNegotiation QName
     */
    public static final QName WSAG_INITIATE_NEGOTIATION_QNAME = new QName( WSAG_NEGOTIATION_NAMESPACE_URI,
        "InitiateNegotiation" );

    /**
     * WS-Agreement Negotiation Factory InitiateNegotiation Input QName
     */
    public static final QName WSAG_INITIATE_NEGOTIATION_INPUT_QNAME = new QName(
        WSAG_NEGOTIATION_NAMESPACE_URI, "InitiateNegotiationInput" );

    /**
     * WS-Agreement Negotiation Factory InitiateNegotiation WSA Action QName
     */
    public static final String WSAG_INITIATE_NEGOTIATION_ACTION = WSAG_NEGOTIATION_NAMESPACE_URI
        + "/InitiateNegotiationRequest";

    /**
     * WS-Agreement Negotiation Negotiate QName
     */
    public static final QName WSAG_NEGOTIATION_NEGOTIATE_QNAME = new QName( WSAG_NEGOTIATION_NAMESPACE_URI,
        "Negotiate" );

    /**
     * WS-Agreement Negotiation Negotiate Input QName
     */
    public static final QName WSAG_NEGOTIATION_NEGOTIATE_INPUT_QNAME = new QName(
        WSAG_NEGOTIATION_NAMESPACE_URI, "NegotiateInput" );

    /**
     * WS-Agreement Negotiation Negotiate WSA Action QName
     */
    public static final String WSAG_NEGOTIATION_NEGOTIATE_ACTION = WSAG_NEGOTIATION_NAMESPACE_URI
        + "/NegotiateRequest";

    /**
     * WS-Agreement Negotiation Advertise QName
     */
    public static final QName WSAG_NEGOTIATION_ADVERTISE_QNAME = new QName( WSAG_NEGOTIATION_NAMESPACE_URI,
        "Advertise" );

    /**
     * WS-Agreement Negotiation Advertise Input QName
     */
    public static final QName WSAG_NEGOTIATION_ADVERTISE_INPUT_QNAME = new QName(
        WSAG_NEGOTIATION_NAMESPACE_URI, "AdvertiseInput" );

    /**
     * WS-Agreement Negotiation Advertise WSA Action QName
     */
    public static final String WSAG_NEGOTIATION_ADVERTISE_ACTION = WSAG_NEGOTIATION_NAMESPACE_URI
        + "/AdvertiseRequest";

    /**
     * WS-Agreement Negotiation Terminate Negotiation QName
     */
    public static final QName WSAG_NEGOTIATION_TERMINATE_QNAME = new QName( WSAG_NEGOTIATION_NAMESPACE_URI,
        "Terminate" );

    /**
     * WS-Agreement Negotiation Terminate Negotiation Input QName
     */
    public static final QName WSAG_NEGOTIATION_TERMINATE_INPUT_QNAME = new QName(
        WSAG_NEGOTIATION_NAMESPACE_URI, "TerminateInput" );

    /**
     * WS-Agreement Negotiation Terminate Negotiation WSA Action QName
     */
    public static final String WSAG_NEGOTIATION_TERMINATE_ACTION = WSAG_NEGOTIATION_NAMESPACE_URI
        + "/TerminateRequest";

    /**
     * WS-Agreement Creation Constraints Resource Property QName
     */
    public static final QName CREATION_CONSTRAINT_ELEMENT_QNAME = new QName( WsagConstants.NAMESPACE_URI,
        "CreationConstraints" );

    /*
     * WSRF related namespace and type declarations
     */

    /**
     * WSRF Namespace URI
     */
    public static final String WSRF_RPW_NAMESPACE_URI = "http://docs.oasis-open.org/wsrf/rpw-2";

    /**
     * WSRF GetResourceProperty WSA Action QName
     */
    public static final String WSRF_GET_RESOURCE_PROPERTY_ACTION = WSRF_RPW_NAMESPACE_URI
        + "/GetResourceProperty/GetResourcePropertyRequest";

    /**
     * WSRF GetResourceProperty QName
     */
    public static final QName WSRF_GET_RESOURCE_PROPERTY_QNAME = new QName( WSRF_RPW_NAMESPACE_URI,
        "GetResourceProperty" );

    /*
     * WSDM related namespace and type declarations
     */

    /**
     * WSDM MUWS Namespace Prefix
     */
    public static final String WSDM_MUWS_PREFIX = "muws1";

    /**
     * WSDM MUWS Namespace Prefix Declaration
     */
    public static final String WSDM_MUWS_PREFIX_DECLARATION = "xmlns:" + WSDM_MUWS_PREFIX;

    /**
     * WSDM MUWS Namespace URI
     */
    public static final String WSDM_MUWS_NAMESPACE_URI = "http://docs.oasis-open.org/wsdm/muws1-2.xsd";

    /*
     * WSAG4J service URIs
     */

    /**
     * WSAG4J Agreement Factory Service Path
     */
    public static final String AGREEMENT_FACTORY_SERVICE_URI = "/services/AgreementFactory";

    /**
     * WSAG4J Agreement Factory Registry Service Path
     */
    public static final String AGREEMENT_FACTORY_REGISTRY_SERVICE_URI =
        "/services/AgreementFactoryServiceGroup";

    /**
     * WSAG4J Agreement Service Path
     */
    public static final String AGREEMENT_SERVICE_URI = "/services/Agreement";

    /**
     * WSAG4J Agreement Registry Service Path
     */
    public static final String AGREEMENT_REGISTRY_SERVICE_URI = "/services/AgreementServiceGroup";

    /**
     * WSAG4J Namespace URI
     */
    public static final String WSAG4J_NAMESPACE = "http://schemas.scai.fraunhofer.de/wsag4j";

    /**
     * WSAG4J Metadata Exchange Namespace URI
     */
    public static final String WSAG4J_MEX_DIALECT = WSAG4J_NAMESPACE + "/mex";

    /**
     * WSAG4J WSDM Resource Id QName
     */
    public static final QName WSAG4J_RESOURCE_ID_QNAME = new QName( WSAG4J_NAMESPACE, "ResourceId" );

}
