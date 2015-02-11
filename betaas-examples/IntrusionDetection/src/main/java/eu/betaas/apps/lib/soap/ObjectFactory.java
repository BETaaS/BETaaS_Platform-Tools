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


package eu.betaas.apps.lib.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.betaas.apps.lib.soap package. 
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

    private final static QName _GetTaskDataResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getTaskDataResponse");
    private final static QName _UnregisterResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "unregisterResponse");
    private final static QName _GetExtendedServiceDataResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getExtendedServiceDataResponse");
    private final static QName _GetThingServiceData_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getThingServiceData");
    private final static QName _Register_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "register");
    private final static QName _SetThingServiceData_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "setThingServiceData");
    private final static QName _RegisterResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "registerResponse");
    private final static QName _GetGWIdResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getGWIdResponse");
    private final static QName _GetApplicationServices_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getApplicationServices");
    private final static QName _UninstallApplicationResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "uninstallApplicationResponse");
    private final static QName _InstallApplication_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "installApplication");
    private final static QName _SetThingServiceDataResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "setThingServiceDataResponse");
    private final static QName _Unregister_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "unregister");
    private final static QName _GetTaskData_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getTaskData");
    private final static QName _GetGWId_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getGWId");
    private final static QName _InstallApplicationResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "installApplicationResponse");
    private final static QName _UninstallApplication_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "uninstallApplication");
    private final static QName _GetExtendedServiceData_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getExtendedServiceData");
    private final static QName _GetApplicationServicesResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getApplicationServicesResponse");
    private final static QName _GetThingServiceDataResponse_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "getThingServiceDataResponse");
    private final static QName _GetExtendedServiceDataResponseReturn_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "return");
    private final static QName _GetExtendedServiceDataArg0_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "arg0");
    private final static QName _GetExtendedServiceDataArg1_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "arg1");
    private final static QName _GetExtendedServiceDataArg2_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "arg2");
    private final static QName _SetThingServiceDataArg3_QNAME = new QName("http://api.servicemanager.service.betaas.eu/", "arg3");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.betaas.apps.lib.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UninstallApplication }
     * 
     */
    public UninstallApplication createUninstallApplication() {
        return new UninstallApplication();
    }

    /**
     * Create an instance of {@link InstallApplicationResponse }
     * 
     */
    public InstallApplicationResponse createInstallApplicationResponse() {
        return new InstallApplicationResponse();
    }

    /**
     * Create an instance of {@link GetTaskData }
     * 
     */
    public GetTaskData createGetTaskData() {
        return new GetTaskData();
    }

    /**
     * Create an instance of {@link GetGWId }
     * 
     */
    public GetGWId createGetGWId() {
        return new GetGWId();
    }

    /**
     * Create an instance of {@link GetExtendedServiceData }
     * 
     */
    public GetExtendedServiceData createGetExtendedServiceData() {
        return new GetExtendedServiceData();
    }

    /**
     * Create an instance of {@link GetApplicationServicesResponse }
     * 
     */
    public GetApplicationServicesResponse createGetApplicationServicesResponse() {
        return new GetApplicationServicesResponse();
    }

    /**
     * Create an instance of {@link GetThingServiceDataResponse }
     * 
     */
    public GetThingServiceDataResponse createGetThingServiceDataResponse() {
        return new GetThingServiceDataResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link GetThingServiceData }
     * 
     */
    public GetThingServiceData createGetThingServiceData() {
        return new GetThingServiceData();
    }

    /**
     * Create an instance of {@link SetThingServiceData }
     * 
     */
    public SetThingServiceData createSetThingServiceData() {
        return new SetThingServiceData();
    }

    /**
     * Create an instance of {@link UnregisterResponse }
     * 
     */
    public UnregisterResponse createUnregisterResponse() {
        return new UnregisterResponse();
    }

    /**
     * Create an instance of {@link GetTaskDataResponse }
     * 
     */
    public GetTaskDataResponse createGetTaskDataResponse() {
        return new GetTaskDataResponse();
    }

    /**
     * Create an instance of {@link GetExtendedServiceDataResponse }
     * 
     */
    public GetExtendedServiceDataResponse createGetExtendedServiceDataResponse() {
        return new GetExtendedServiceDataResponse();
    }

    /**
     * Create an instance of {@link InstallApplication }
     * 
     */
    public InstallApplication createInstallApplication() {
        return new InstallApplication();
    }

    /**
     * Create an instance of {@link Unregister }
     * 
     */
    public Unregister createUnregister() {
        return new Unregister();
    }

    /**
     * Create an instance of {@link SetThingServiceDataResponse }
     * 
     */
    public SetThingServiceDataResponse createSetThingServiceDataResponse() {
        return new SetThingServiceDataResponse();
    }

    /**
     * Create an instance of {@link GetApplicationServices }
     * 
     */
    public GetApplicationServices createGetApplicationServices() {
        return new GetApplicationServices();
    }

    /**
     * Create an instance of {@link GetGWIdResponse }
     * 
     */
    public GetGWIdResponse createGetGWIdResponse() {
        return new GetGWIdResponse();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link UninstallApplicationResponse }
     * 
     */
    public UninstallApplicationResponse createUninstallApplicationResponse() {
        return new UninstallApplicationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTaskDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getTaskDataResponse")
    public JAXBElement<GetTaskDataResponse> createGetTaskDataResponse(GetTaskDataResponse value) {
        return new JAXBElement<GetTaskDataResponse>(_GetTaskDataResponse_QNAME, GetTaskDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnregisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "unregisterResponse")
    public JAXBElement<UnregisterResponse> createUnregisterResponse(UnregisterResponse value) {
        return new JAXBElement<UnregisterResponse>(_UnregisterResponse_QNAME, UnregisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetExtendedServiceDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getExtendedServiceDataResponse")
    public JAXBElement<GetExtendedServiceDataResponse> createGetExtendedServiceDataResponse(GetExtendedServiceDataResponse value) {
        return new JAXBElement<GetExtendedServiceDataResponse>(_GetExtendedServiceDataResponse_QNAME, GetExtendedServiceDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetThingServiceData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getThingServiceData")
    public JAXBElement<GetThingServiceData> createGetThingServiceData(GetThingServiceData value) {
        return new JAXBElement<GetThingServiceData>(_GetThingServiceData_QNAME, GetThingServiceData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetThingServiceData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "setThingServiceData")
    public JAXBElement<SetThingServiceData> createSetThingServiceData(SetThingServiceData value) {
        return new JAXBElement<SetThingServiceData>(_SetThingServiceData_QNAME, SetThingServiceData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getGWIdResponse")
    public JAXBElement<GetGWIdResponse> createGetGWIdResponse(GetGWIdResponse value) {
        return new JAXBElement<GetGWIdResponse>(_GetGWIdResponse_QNAME, GetGWIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetApplicationServices }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getApplicationServices")
    public JAXBElement<GetApplicationServices> createGetApplicationServices(GetApplicationServices value) {
        return new JAXBElement<GetApplicationServices>(_GetApplicationServices_QNAME, GetApplicationServices.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UninstallApplicationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "uninstallApplicationResponse")
    public JAXBElement<UninstallApplicationResponse> createUninstallApplicationResponse(UninstallApplicationResponse value) {
        return new JAXBElement<UninstallApplicationResponse>(_UninstallApplicationResponse_QNAME, UninstallApplicationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstallApplication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "installApplication")
    public JAXBElement<InstallApplication> createInstallApplication(InstallApplication value) {
        return new JAXBElement<InstallApplication>(_InstallApplication_QNAME, InstallApplication.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetThingServiceDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "setThingServiceDataResponse")
    public JAXBElement<SetThingServiceDataResponse> createSetThingServiceDataResponse(SetThingServiceDataResponse value) {
        return new JAXBElement<SetThingServiceDataResponse>(_SetThingServiceDataResponse_QNAME, SetThingServiceDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Unregister }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "unregister")
    public JAXBElement<Unregister> createUnregister(Unregister value) {
        return new JAXBElement<Unregister>(_Unregister_QNAME, Unregister.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTaskData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getTaskData")
    public JAXBElement<GetTaskData> createGetTaskData(GetTaskData value) {
        return new JAXBElement<GetTaskData>(_GetTaskData_QNAME, GetTaskData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGWId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getGWId")
    public JAXBElement<GetGWId> createGetGWId(GetGWId value) {
        return new JAXBElement<GetGWId>(_GetGWId_QNAME, GetGWId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstallApplicationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "installApplicationResponse")
    public JAXBElement<InstallApplicationResponse> createInstallApplicationResponse(InstallApplicationResponse value) {
        return new JAXBElement<InstallApplicationResponse>(_InstallApplicationResponse_QNAME, InstallApplicationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UninstallApplication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "uninstallApplication")
    public JAXBElement<UninstallApplication> createUninstallApplication(UninstallApplication value) {
        return new JAXBElement<UninstallApplication>(_UninstallApplication_QNAME, UninstallApplication.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetExtendedServiceData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getExtendedServiceData")
    public JAXBElement<GetExtendedServiceData> createGetExtendedServiceData(GetExtendedServiceData value) {
        return new JAXBElement<GetExtendedServiceData>(_GetExtendedServiceData_QNAME, GetExtendedServiceData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetApplicationServicesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getApplicationServicesResponse")
    public JAXBElement<GetApplicationServicesResponse> createGetApplicationServicesResponse(GetApplicationServicesResponse value) {
        return new JAXBElement<GetApplicationServicesResponse>(_GetApplicationServicesResponse_QNAME, GetApplicationServicesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetThingServiceDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "getThingServiceDataResponse")
    public JAXBElement<GetThingServiceDataResponse> createGetThingServiceDataResponse(GetThingServiceDataResponse value) {
        return new JAXBElement<GetThingServiceDataResponse>(_GetThingServiceDataResponse_QNAME, GetThingServiceDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = GetExtendedServiceDataResponse.class)
    public JAXBElement<String> createGetExtendedServiceDataResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, GetExtendedServiceDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = InstallApplicationResponse.class)
    public JAXBElement<String> createInstallApplicationResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, InstallApplicationResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = GetExtendedServiceData.class)
    public JAXBElement<String> createGetExtendedServiceDataArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, GetExtendedServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = GetExtendedServiceData.class)
    public JAXBElement<String> createGetExtendedServiceDataArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, GetExtendedServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg2", scope = GetExtendedServiceData.class)
    public JAXBElement<String> createGetExtendedServiceDataArg2(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg2_QNAME, String.class, GetExtendedServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = GetThingServiceDataResponse.class)
    public JAXBElement<String> createGetThingServiceDataResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, GetThingServiceDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = GetTaskData.class)
    public JAXBElement<String> createGetTaskDataArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, GetTaskData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = GetTaskData.class)
    public JAXBElement<String> createGetTaskDataArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, GetTaskData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = GetGWIdResponse.class)
    public JAXBElement<String> createGetGWIdResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, GetGWIdResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = Unregister.class)
    public JAXBElement<String> createUnregisterArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, Unregister.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = Unregister.class)
    public JAXBElement<String> createUnregisterArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, Unregister.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg2", scope = Unregister.class)
    public JAXBElement<String> createUnregisterArg2(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg2_QNAME, String.class, Unregister.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = GetTaskDataResponse.class)
    public JAXBElement<String> createGetTaskDataResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, GetTaskDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = UninstallApplication.class)
    public JAXBElement<String> createUninstallApplicationArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, UninstallApplication.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = UninstallApplication.class)
    public JAXBElement<String> createUninstallApplicationArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, UninstallApplication.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = Register.class)
    public JAXBElement<String> createRegisterArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, Register.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = Register.class)
    public JAXBElement<String> createRegisterArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, Register.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg2", scope = Register.class)
    public JAXBElement<String> createRegisterArg2(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg2_QNAME, String.class, Register.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = InstallApplication.class)
    public JAXBElement<String> createInstallApplicationArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, InstallApplication.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = SetThingServiceData.class)
    public JAXBElement<String> createSetThingServiceDataArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, SetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = SetThingServiceData.class)
    public JAXBElement<String> createSetThingServiceDataArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, SetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg2", scope = SetThingServiceData.class)
    public JAXBElement<String> createSetThingServiceDataArg2(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg2_QNAME, String.class, SetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg3", scope = SetThingServiceData.class)
    public JAXBElement<String> createSetThingServiceDataArg3(String value) {
        return new JAXBElement<String>(_SetThingServiceDataArg3_QNAME, String.class, SetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "return", scope = GetApplicationServicesResponse.class)
    public JAXBElement<String> createGetApplicationServicesResponseReturn(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataResponseReturn_QNAME, String.class, GetApplicationServicesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = GetThingServiceData.class)
    public JAXBElement<String> createGetThingServiceDataArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, GetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg1", scope = GetThingServiceData.class)
    public JAXBElement<String> createGetThingServiceDataArg1(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg1_QNAME, String.class, GetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg2", scope = GetThingServiceData.class)
    public JAXBElement<String> createGetThingServiceDataArg2(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg2_QNAME, String.class, GetThingServiceData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.servicemanager.service.betaas.eu/", name = "arg0", scope = GetApplicationServices.class)
    public JAXBElement<String> createGetApplicationServicesArg0(String value) {
        return new JAXBElement<String>(_GetExtendedServiceDataArg0_QNAME, String.class, GetApplicationServices.class, value);
    }

}
