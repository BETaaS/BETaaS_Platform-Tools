/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/


package eu.betaas.service.instancemanager.web.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.betaas.service.instancemanager.web.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAdminAddressResponseReturn_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "return");
    private final static QName _GetJoinedGWsResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getJoinedGWsResponse");
    private final static QName _DisjoinInstance_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "disjoinInstance");
    private final static QName _GetJoinedGWs_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getJoinedGWs");
    private final static QName _GetGWStarIDResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getGWStarIDResponse");
    private final static QName _GetGWStarID_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getGWStarID");
    private final static QName _GetDescriptionResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getDescriptionResponse");
    private final static QName _GetGWID_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getGWID");
    private final static QName _DisjoinInstanceResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "disjoinInstanceResponse");
    private final static QName _GetAdminAddressResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getAdminAddressResponse");
    private final static QName _GetDescription_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getDescription");
    private final static QName _GetInstanceIDResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getInstanceIDResponse");
    private final static QName _GetInstanceInfo_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getInstanceInfo");
    private final static QName _IsGWStar_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "isGWStar");
    private final static QName _GetGWIDResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getGWIDResponse");
    private final static QName _IsBackupStarResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "isBackupStarResponse");
    private final static QName _GetInstanceInfoResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getInstanceInfoResponse");
    private final static QName _JoinInstanceResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "joinInstanceResponse");
    private final static QName _RequestJoin_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "requestJoin");
    private final static QName _IsGWStarResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "isGWStarResponse");
    private final static QName _GetAdminAddress_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getAdminAddress");
    private final static QName _RequestJoinResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "requestJoinResponse");
    private final static QName _JoinInstance_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "joinInstance");
    private final static QName _RequestDisjoin_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "requestDisjoin");
    private final static QName _RequestDisjoinResponse_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "requestDisjoinResponse");
    private final static QName _GetInstanceID_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "getInstanceID");
    private final static QName _IsBackupStar_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "isBackupStar");
    private final static QName _JoinInstanceArg0_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "arg0");
    private final static QName _JoinInstanceArg1_QNAME = new QName("http://api.instancemanager.service.betaas.eu/", "arg1");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.betaas.service.instancemanager.web.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RequestJoinResponse }
     * 
     */
    public RequestJoinResponse createRequestJoinResponse() {
        return new RequestJoinResponse();
    }

    /**
     * Create an instance of {@link GetAdminAddress }
     * 
     */
    public GetAdminAddress createGetAdminAddress() {
        return new GetAdminAddress();
    }

    /**
     * Create an instance of {@link RequestDisjoin }
     * 
     */
    public RequestDisjoin createRequestDisjoin() {
        return new RequestDisjoin();
    }

    /**
     * Create an instance of {@link JoinInstance }
     * 
     */
    public JoinInstance createJoinInstance() {
        return new JoinInstance();
    }

    /**
     * Create an instance of {@link GetInstanceID }
     * 
     */
    public GetInstanceID createGetInstanceID() {
        return new GetInstanceID();
    }

    /**
     * Create an instance of {@link RequestDisjoinResponse }
     * 
     */
    public RequestDisjoinResponse createRequestDisjoinResponse() {
        return new RequestDisjoinResponse();
    }

    /**
     * Create an instance of {@link IsBackupStar }
     * 
     */
    public IsBackupStar createIsBackupStar() {
        return new IsBackupStar();
    }

    /**
     * Create an instance of {@link IsGWStarResponse }
     * 
     */
    public IsGWStarResponse createIsGWStarResponse() {
        return new IsGWStarResponse();
    }

    /**
     * Create an instance of {@link RequestJoin }
     * 
     */
    public RequestJoin createRequestJoin() {
        return new RequestJoin();
    }

    /**
     * Create an instance of {@link JoinInstanceResponse }
     * 
     */
    public JoinInstanceResponse createJoinInstanceResponse() {
        return new JoinInstanceResponse();
    }

    /**
     * Create an instance of {@link GetGWIDResponse }
     * 
     */
    public GetGWIDResponse createGetGWIDResponse() {
        return new GetGWIDResponse();
    }

    /**
     * Create an instance of {@link IsGWStar }
     * 
     */
    public IsGWStar createIsGWStar() {
        return new IsGWStar();
    }

    /**
     * Create an instance of {@link IsBackupStarResponse }
     * 
     */
    public IsBackupStarResponse createIsBackupStarResponse() {
        return new IsBackupStarResponse();
    }

    /**
     * Create an instance of {@link GetInstanceInfoResponse }
     * 
     */
    public GetInstanceInfoResponse createGetInstanceInfoResponse() {
        return new GetInstanceInfoResponse();
    }

    /**
     * Create an instance of {@link GetInstanceIDResponse }
     * 
     */
    public GetInstanceIDResponse createGetInstanceIDResponse() {
        return new GetInstanceIDResponse();
    }

    /**
     * Create an instance of {@link GetInstanceInfo }
     * 
     */
    public GetInstanceInfo createGetInstanceInfo() {
        return new GetInstanceInfo();
    }

    /**
     * Create an instance of {@link GetDescription }
     * 
     */
    public GetDescription createGetDescription() {
        return new GetDescription();
    }

    /**
     * Create an instance of {@link GetGWID }
     * 
     */
    public GetGWID createGetGWID() {
        return new GetGWID();
    }

    /**
     * Create an instance of {@link DisjoinInstanceResponse }
     * 
     */
    public DisjoinInstanceResponse createDisjoinInstanceResponse() {
        return new DisjoinInstanceResponse();
    }

    /**
     * Create an instance of {@link GetAdminAddressResponse }
     * 
     */
    public GetAdminAddressResponse createGetAdminAddressResponse() {
        return new GetAdminAddressResponse();
    }

    /**
     * Create an instance of {@link DisjoinInstance }
     * 
     */
    public DisjoinInstance createDisjoinInstance() {
        return new DisjoinInstance();
    }

    /**
     * Create an instance of {@link GetJoinedGWs }
     * 
     */
    public GetJoinedGWs createGetJoinedGWs() {
        return new GetJoinedGWs();
    }

    /**
     * Create an instance of {@link GetJoinedGWsResponse }
     * 
     */
    public GetJoinedGWsResponse createGetJoinedGWsResponse() {
        return new GetJoinedGWsResponse();
    }

    /**
     * Create an instance of {@link GetGWStarID }
     * 
     */
    public GetGWStarID createGetGWStarID() {
        return new GetGWStarID();
    }

    /**
     * Create an instance of {@link GetGWStarIDResponse }
     * 
     */
    public GetGWStarIDResponse createGetGWStarIDResponse() {
        return new GetGWStarIDResponse();
    }

    /**
     * Create an instance of {@link GetDescriptionResponse }
     * 
     */
    public GetDescriptionResponse createGetDescriptionResponse() {
        return new GetDescriptionResponse();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetAdminAddressResponse.class)
    public JAXBElement<String> createGetAdminAddressResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetAdminAddressResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJoinedGWsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getJoinedGWsResponse")
    public JAXBElement<GetJoinedGWsResponse> createGetJoinedGWsResponse(GetJoinedGWsResponse value) {
        return new JAXBElement<GetJoinedGWsResponse>(_GetJoinedGWsResponse_QNAME, GetJoinedGWsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisjoinInstance }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "disjoinInstance")
    public JAXBElement<DisjoinInstance> createDisjoinInstance(DisjoinInstance value) {
        return new JAXBElement<DisjoinInstance>(_DisjoinInstance_QNAME, DisjoinInstance.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJoinedGWs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getJoinedGWs")
    public JAXBElement<GetJoinedGWs> createGetJoinedGWs(GetJoinedGWs value) {
        return new JAXBElement<GetJoinedGWs>(_GetJoinedGWs_QNAME, GetJoinedGWs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWStarIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getGWStarIDResponse")
    public JAXBElement<GetGWStarIDResponse> createGetGWStarIDResponse(GetGWStarIDResponse value) {
        return new JAXBElement<GetGWStarIDResponse>(_GetGWStarIDResponse_QNAME, GetGWStarIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWStarID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getGWStarID")
    public JAXBElement<GetGWStarID> createGetGWStarID(GetGWStarID value) {
        return new JAXBElement<GetGWStarID>(_GetGWStarID_QNAME, GetGWStarID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDescriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getDescriptionResponse")
    public JAXBElement<GetDescriptionResponse> createGetDescriptionResponse(GetDescriptionResponse value) {
        return new JAXBElement<GetDescriptionResponse>(_GetDescriptionResponse_QNAME, GetDescriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getGWID")
    public JAXBElement<GetGWID> createGetGWID(GetGWID value) {
        return new JAXBElement<GetGWID>(_GetGWID_QNAME, GetGWID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisjoinInstanceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "disjoinInstanceResponse")
    public JAXBElement<DisjoinInstanceResponse> createDisjoinInstanceResponse(DisjoinInstanceResponse value) {
        return new JAXBElement<DisjoinInstanceResponse>(_DisjoinInstanceResponse_QNAME, DisjoinInstanceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAdminAddressResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getAdminAddressResponse")
    public JAXBElement<GetAdminAddressResponse> createGetAdminAddressResponse(GetAdminAddressResponse value) {
        return new JAXBElement<GetAdminAddressResponse>(_GetAdminAddressResponse_QNAME, GetAdminAddressResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getDescription")
    public JAXBElement<GetDescription> createGetDescription(GetDescription value) {
        return new JAXBElement<GetDescription>(_GetDescription_QNAME, GetDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInstanceIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getInstanceIDResponse")
    public JAXBElement<GetInstanceIDResponse> createGetInstanceIDResponse(GetInstanceIDResponse value) {
        return new JAXBElement<GetInstanceIDResponse>(_GetInstanceIDResponse_QNAME, GetInstanceIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInstanceInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getInstanceInfo")
    public JAXBElement<GetInstanceInfo> createGetInstanceInfo(GetInstanceInfo value) {
        return new JAXBElement<GetInstanceInfo>(_GetInstanceInfo_QNAME, GetInstanceInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsGWStar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "isGWStar")
    public JAXBElement<IsGWStar> createIsGWStar(IsGWStar value) {
        return new JAXBElement<IsGWStar>(_IsGWStar_QNAME, IsGWStar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getGWIDResponse")
    public JAXBElement<GetGWIDResponse> createGetGWIDResponse(GetGWIDResponse value) {
        return new JAXBElement<GetGWIDResponse>(_GetGWIDResponse_QNAME, GetGWIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsBackupStarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "isBackupStarResponse")
    public JAXBElement<IsBackupStarResponse> createIsBackupStarResponse(IsBackupStarResponse value) {
        return new JAXBElement<IsBackupStarResponse>(_IsBackupStarResponse_QNAME, IsBackupStarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInstanceInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getInstanceInfoResponse")
    public JAXBElement<GetInstanceInfoResponse> createGetInstanceInfoResponse(GetInstanceInfoResponse value) {
        return new JAXBElement<GetInstanceInfoResponse>(_GetInstanceInfoResponse_QNAME, GetInstanceInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JoinInstanceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "joinInstanceResponse")
    public JAXBElement<JoinInstanceResponse> createJoinInstanceResponse(JoinInstanceResponse value) {
        return new JAXBElement<JoinInstanceResponse>(_JoinInstanceResponse_QNAME, JoinInstanceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestJoin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "requestJoin")
    public JAXBElement<RequestJoin> createRequestJoin(RequestJoin value) {
        return new JAXBElement<RequestJoin>(_RequestJoin_QNAME, RequestJoin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsGWStarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "isGWStarResponse")
    public JAXBElement<IsGWStarResponse> createIsGWStarResponse(IsGWStarResponse value) {
        return new JAXBElement<IsGWStarResponse>(_IsGWStarResponse_QNAME, IsGWStarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAdminAddress }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getAdminAddress")
    public JAXBElement<GetAdminAddress> createGetAdminAddress(GetAdminAddress value) {
        return new JAXBElement<GetAdminAddress>(_GetAdminAddress_QNAME, GetAdminAddress.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestJoinResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "requestJoinResponse")
    public JAXBElement<RequestJoinResponse> createRequestJoinResponse(RequestJoinResponse value) {
        return new JAXBElement<RequestJoinResponse>(_RequestJoinResponse_QNAME, RequestJoinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JoinInstance }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "joinInstance")
    public JAXBElement<JoinInstance> createJoinInstance(JoinInstance value) {
        return new JAXBElement<JoinInstance>(_JoinInstance_QNAME, JoinInstance.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestDisjoin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "requestDisjoin")
    public JAXBElement<RequestDisjoin> createRequestDisjoin(RequestDisjoin value) {
        return new JAXBElement<RequestDisjoin>(_RequestDisjoin_QNAME, RequestDisjoin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestDisjoinResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "requestDisjoinResponse")
    public JAXBElement<RequestDisjoinResponse> createRequestDisjoinResponse(RequestDisjoinResponse value) {
        return new JAXBElement<RequestDisjoinResponse>(_RequestDisjoinResponse_QNAME, RequestDisjoinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetInstanceID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "getInstanceID")
    public JAXBElement<GetInstanceID> createGetInstanceID(GetInstanceID value) {
        return new JAXBElement<GetInstanceID>(_GetInstanceID_QNAME, GetInstanceID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsBackupStar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "isBackupStar")
    public JAXBElement<IsBackupStar> createIsBackupStar(IsBackupStar value) {
        return new JAXBElement<IsBackupStar>(_IsBackupStar_QNAME, IsBackupStar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "arg0", scope = JoinInstance.class)
    public JAXBElement<String> createJoinInstanceArg0(String value) {
        return new JAXBElement<String>(_JoinInstanceArg0_QNAME, String.class, JoinInstance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "arg1", scope = JoinInstance.class)
    public JAXBElement<String> createJoinInstanceArg1(String value) {
        return new JAXBElement<String>(_JoinInstanceArg1_QNAME, String.class, JoinInstance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetDescriptionResponse.class)
    public JAXBElement<String> createGetDescriptionResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetDescriptionResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetInstanceIDResponse.class)
    public JAXBElement<String> createGetInstanceIDResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetInstanceIDResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "arg0", scope = RequestJoin.class)
    public JAXBElement<String> createRequestJoinArg0(String value) {
        return new JAXBElement<String>(_JoinInstanceArg0_QNAME, String.class, RequestJoin.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetInstanceInfoResponse.class)
    public JAXBElement<String> createGetInstanceInfoResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetInstanceInfoResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetGWStarIDResponse.class)
    public JAXBElement<String> createGetGWStarIDResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetGWStarIDResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "arg0", scope = DisjoinInstance.class)
    public JAXBElement<String> createDisjoinInstanceArg0(String value) {
        return new JAXBElement<String>(_JoinInstanceArg0_QNAME, String.class, DisjoinInstance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "arg1", scope = DisjoinInstance.class)
    public JAXBElement<String> createDisjoinInstanceArg1(String value) {
        return new JAXBElement<String>(_JoinInstanceArg1_QNAME, String.class, DisjoinInstance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.instancemanager.service.betaas.eu/", name = "return", scope = GetGWIDResponse.class)
    public JAXBElement<String> createGetGWIDResponseReturn(String value) {
        return new JAXBElement<String>(_GetAdminAddressResponseReturn_QNAME, String.class, GetGWIDResponse.class, value);
    }

}
