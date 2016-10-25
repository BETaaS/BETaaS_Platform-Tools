package eu.betaas.taas.taasvmmanager.openstack;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import eu.betaas.taas.taasvmmanager.api.datamodel.InstanceType;
import eu.betaas.taas.taasvmmanager.occi.OCCIException;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Compute;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.ComputeState;
import eu.betaas.taas.taasvmmanager.occi.datamodel.InstanceTypeCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Network;
import eu.betaas.taas.taasvmmanager.occi.datamodel.NetworkCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.Storage;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageCollection;
import eu.betaas.taas.taasvmmanager.occi.datamodel.StorageType;
import eu.betaas.taas.taasvmmanager.occi.datamodel.User.Quota;

public class OpenStackParserTest {

    /******* Test XML responses *******/
    private static String AUTHORIZATIONRESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<access xmlns=\"http://docs.openstack.org/identity/api/v2.0\">"
      + "  <token issued_at=\"2014-01-30T15:49:11.054709\""
      + "    expires=\"2014-01-31T15:49:11Z\""
      + "    id=\"aaaaa-bbbbb-ccccc-dddd\">"
      + "    <tenant enabled=\"true\" name=\"demo\""
      + "      id=\"fc394f2ab2df4114bde39905f800dc57\"/>"
      + "  </token>"
      + "  <serviceCatalog>"
      + "    <service type=\"compute\" name=\"nova\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        internalURL=\"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        id=\"2dad48f09e2a447a9bf852bcd93548ef\""
      + "      />"
      + "    </service>"
      + "    <service type=\"network\" name=\"neutron\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:9696/\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:9696/\""
      + "        internalURL=\"http://23.253.72.207:9696/\""
      + "        id=\"97c526db8d7a4c88bbb8d68db1bdcdb8\""
      + "      />"
      + "    </service>"
      + "    <service type=\"volumev2\" name=\"cinder\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        internalURL=\"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "        id=\"93f86dfcbba143a39a33d0c2cd424870\""
      + "      />"
      + "    </service>"
      + "    <service type=\"computev3\" name=\"nova\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:8774/v3\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8774/v3\""
      + "        internalURL=\"http://23.253.72.207:8774/v3\""
      + "        id=\"3eb274b12b1d47b2abc536038d87339e\""
      + "      />"
      + "    </service>"
      + "    <service type=\"s3\" name=\"s3\">"
      + "      <endpoints_links/>"
      + "      <endpoint adminURL=\"http://23.253.72.207:3333\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:3333\""
      + "        internalURL=\"http://23.253.72.207:3333\""
      + "        id=\"957f1e54afc64d33a62099faa5e980a2\""
      + "      />"
      + "    </service>"
      + "    <service type=\"image\" name=\"glance\">"
      + "      <endpoints_links/>"
      + "      <endpoint adminURL=\"http://23.253.72.207:9292\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:9292\""
      + "        internalURL=\"http://23.253.72.207:9292\""
      + "        id=\"27d5749f36864c7d96bebf84a5ec9767\""
      + "      />"
      + "    </service>"
      + "    <service type=\"volume\" name=\"cinder\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\""
      + "        internalURL=\"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\""
      + "        id=\"37c83a2157f944f1972e74658aa0b139\""
      + "      />"
      + "    </service>"
      + "    <service type=\"ec2\" name=\"ec2\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:8773/services/Admin\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8773/services/Cloud\""
      + "        internalURL=\"http://23.253.72.207:8773/services/Cloud\""
      + "        id=\"289b59289d6048e2912b327e5d3240ca\""
      + "      />"
      + "    </service>"
      + "    <service type=\"object-store\" name=\"swift\">"
      + "      <endpoints_links/>"
      + "      <endpoint adminURL=\"http://23.253.72.207:8080\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:8080/v1/AUTH_fc394f2ab2df4114bde39905f800dc57\""
      + "        internalURL=\"http://23.253.72.207:8080/v1/AUTH_fc394f2ab2df4114bde39905f800dc57\""
      + "        id=\"16b76b5e5b7d48039a6e4cc3129545f3\""
      + "      />"
      + "    </service>"
      + "    <service type=\"identity\" name=\"keystone\">"
      + "      <endpoints_links/>"
      + "      <endpoint"
      + "        adminURL=\"http://23.253.72.207:35357/v2.0\""
      + "        region=\"RegionOne\""
      + "        publicURL=\"http://23.253.72.207:5000/v2.0\""
      + "        internalURL=\"http://23.253.72.207:5000/v2.0\""
      + "        id=\"26af053673df4ef3a2340c4239e21ea2\""
      + "      />"
      + "    </service>"
      + "  </serviceCatalog>"
      + "  <user username=\"demo\" id=\"9a6590b2ab024747bc2167c4e064d00d\""
      + "    name=\"demo\">"
      + "    <roles_links/>"
      + "    <role name=\"Member\"/>"
      + "    <role name=\"anotherrole\"/>"
      + "  </user>"
      + "  <metadata is_admin=\"0\">"
      + "    <roles>"
      + "      <role>7598ac3c634d4c3da4b9126a5f67ca2b</role>"
      + "      <role>f95c0ab82d6045d9805033ee1fbc80d4</role>"
      + "    </roles>"
      + "  </metadata>"
      + "</access>";
    
    private static String NETWORKCOLLECTIONRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<networks xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:provider=\"http://docs.openstack.org/ext/provider/api/v1.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:router=\"http://docs.openstack.org/ext/neutron/router/api/v1.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <network>"
      + "    <status>ACTIVE</status>"
      + "    <subnets>"
      + "      <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</subnet>"
      + "    </subnets>"
      + "    <name>private-network</name>"
      + "    <provider:physical_network xsi:nil=\"true\"/>"
      + "    <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "    <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "    <provider:network_type>local</provider:network_type>"
      + "    <router:external quantum:type=\"bool\">True</router:external>"
      + "    <shared quantum:type=\"bool\">True</shared>"
      + "    <id>d32019d3-bc6e-4319-9c1d-6722fc136a22</id>"
      + "    <provider:segmentation_id xsi:nil=\"true\"/>"
      + "  </network>"
      + "  <network>"
      + "    <status>ACTIVE</status>"
      + "    <subnets>"
      + "      <subnet>08eae331-0402-425a-923c-34f7cfe39c1b</subnet>"
      + "      <subnet>08eae331-0402-425a-923c-34f7cfe39c1c</subnet>"
      + "    </subnets>"
      + "    <name>private</name>"
      + "    <provider:physical_network xsi:nil=\"true\"/>"
      + "    <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "    <tenant_id>26a7980765d0414dbc1fc1f88cdb7e6e</tenant_id>"
      + "    <provider:network_type>local</provider:network_type>"
      + "    <router:external quantum:type=\"bool\">True</router:external>"
      + "    <shared quantum:type=\"bool\">True</shared>"
      + "    <id>db193ab3-96e3-4cb3-8fc5-05f4296d0324</id>"
      + "    <provider:segmentation_id xsi:nil=\"true\"/>"
      + "  </network>"
      + "</networks>";
    
    private static String NETWORKCOLLECTIONMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<networks xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:provider=\"http://docs.openstack.org/ext/provider/api/v1.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:router=\"http://docs.openstack.org/ext/neutron/router/api/v1.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <network>"
      + "    <status>ACTIVE</status>"
      + "    <subnets>"
      + "      <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</subnet>"
      + "    </subnets>"
      + "    <name>private-network</name>"
      + "    <provider:physical_network xsi:nil=\"true\"/>"
      + "    <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "    <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "    <provider:network_type>local</provider:network_type>"
      + "    <router:external quantum:type=\"bool\">True</router:external>"
      + "    <shared quantum:type=\"bool\">True</shared>"
      + "    <id>d32019d3-bc6e-4319-9c1d-6722fc136a22</id>"
      + "    <provider:segmentation_id xsi:nil=\"true\"/>"
      + "  </network>"
      + "  <network>"
      + "    <status>ACTIVE</status>"
      + "    <subnets>"
      + "      <subnet>08eae331-0402-425a-923c-34f7cfe39c1b</subnet>"
      + "    </subnets>"
      + "    <name>private</name>"
      + "    <provider:physical_network xsi:nil=\"true\"/>"
      + "    <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "    <tenant_id>26a7980765d0414dbc1fc1f88cdb7e6e</tenant_id>"
      + "    <provider:network_type>local</provider:network_type>"
      + "    <router:external quantum:type=\"bool\">True</router:external>"
      + "    <shared quantum:type=\"bool\">True</shared>"
      + "    <provider:segmentation_id xsi:nil=\"true\"/>"
      + "  </network>"
      + "</networks>";
    
    private static String NETWORKRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<network xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:provider=\"http://docs.openstack.org/ext/provider/api/v1.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:router=\"http://docs.openstack.org/ext/neutron/router/api/v1.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <status>ACTIVE</status>"
      + "  <subnets>"
      + "    <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</subnet>"
      + "    <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0c</subnet>"
      + "    <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0d</subnet>"
      + "  </subnets>"
      + "  <name>private-network</name>"
      + "  <provider:physical_network xsi:nil=\"true\"/>"
      + "  <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "  <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "  <provider:network_type>local</provider:network_type>"
      + "  <router:external quantum:type=\"bool\">True</router:external>"
      + "  <shared quantum:type=\"bool\">True</shared>"
      + "  <id>d32019d3-bc6e-4319-9c1d-6722fc136a22</id>"
      + "  <provider:segmentation_id xsi:nil=\"true\"/>"
      + "</network>";
    
    private static String NETWORKMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<network xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:provider=\"http://docs.openstack.org/ext/provider/api/v1.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:router=\"http://docs.openstack.org/ext/neutron/router/api/v1.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <status>ACTIVE</status>"
      + "  <subnets>"
      + "    <subnet>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</subnet>"
      + "  </subnets>"
      + "  <provider:physical_network xsi:nil=\"true\"/>"
      + "  <admin_state_up quantum:type=\"bool\">True</admin_state_up>"
      + "  <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "  <provider:network_type>local</provider:network_type>"
      + "  <router:external quantum:type=\"bool\">True</router:external>"
      + "  <shared quantum:type=\"bool\">True</shared>"
      + "  <provider:segmentation_id xsi:nil=\"true\"/>"
      + "</network>";
    
    private static String SUBNETCOLLECTIONRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<subnets xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <subnet>"
      + "    <name>private-subnet</name>"
      + "    <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
      + "    <network_id>db193ab3-96e3-4cb3-8fc5-05f4296d0324</network_id>"
      + "    <tenant_id>26a7980765d0414dbc1fc1f88cdb7e6e</tenant_id>"
      + "    <dns_nameservers quantum:type=\"list\"/>"
      + "    <allocation_pools>"
      + "      <allocation_pool>"
      + "        <start>10.0.0.2</start>"
      + "        <end>10.0.0.254</end>"
      + "      </allocation_pool>"
      + "    </allocation_pools>"
      + "    <host_routes quantum:type=\"list\"/>"
      + "    <ip_version quantum:type=\"long\">4</ip_version>"
      + "    <gateway_ip>10.0.0.1</gateway_ip>"
      + "    <cidr>10.0.0.0/24</cidr>"
      + "    <id>08eae331-0402-425a-923c-34f7cfe39c1b</id>"
      + "  </subnet>"
      + "  <subnet>"
      + "    <name>my_subnet</name>"
      + "    <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
      + "    <network_id>d32019d3-bc6e-4319-9c1d-6722fc136a22</network_id>"
      + "    <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "    <dns_nameservers quantum:type=\"list\"/>"
      + "    <allocation_pools>"
      + "      <allocation_pool>"
      + "        <start>192.0.0.2</start>"
      + "        <end>192.255.255.254</end>"
      + "      </allocation_pool>"
      + "    </allocation_pools>"
      + "    <host_routes quantum:type=\"list\"/>"
      + "    <ip_version quantum:type=\"long\">4</ip_version>"
      + "    <gateway_ip>192.0.0.1</gateway_ip>"
      + "    <cidr>192.0.0.0/8</cidr>"
      + "    <id>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</id>"
      + "  </subnet>"
      + "</subnets>";
    
    private static String SUBNETCOLLECTIONMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<subnets xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <subnet>"
      + "    <name>private-subnet</name>"
      + "    <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
      + "    <network_id>db193ab3-96e3-4cb3-8fc5-05f4296d0324</network_id>"
      + "    <tenant_id>26a7980765d0414dbc1fc1f88cdb7e6e</tenant_id>"
      + "    <dns_nameservers quantum:type=\"list\"/>"
      + "    <allocation_pools>"
      + "      <allocation_pool>"
      + "        <start>10.0.0.2</start>"
      + "        <end>10.0.0.254</end>"
      + "      </allocation_pool>"
      + "    </allocation_pools>"
      + "    <host_routes quantum:type=\"list\"/>"
      + "    <ip_version quantum:type=\"long\">4</ip_version>"
      + "    <gateway_ip>10.0.0.1</gateway_ip>"
      + "    <cidr>10.0.0.0/24</cidr>"
      + "    <id>08eae331-0402-425a-923c-34f7cfe39c1b</id>"
      + "  </subnet>"
      + "  <subnet>"
      + "    <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
      + "    <network_id>d32019d3-bc6e-4319-9c1d-6722fc136a22</network_id>"
      + "    <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "    <dns_nameservers quantum:type=\"list\"/>"
      + "    <allocation_pools>"
      + "      <allocation_pool>"
      + "        <start>192.0.0.2</start>"
      + "        <end>192.255.255.254</end>"
      + "      </allocation_pool>"
      + "    </allocation_pools>"
      + "    <host_routes quantum:type=\"list\"/>"
      + "    <ip_version quantum:type=\"long\">4</ip_version>"
      + "    <gateway_ip>192.0.0.1</gateway_ip>"
      + "    <cidr>192.0.0.0/8</cidr>"
      + "    <id>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</id>"
      + "  </subnet>"
      + "</subnets>";
    
    private static String SUBNETRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<subnet xmlns=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
      + "  <name>test_subnet_1</name>"
      + "  <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
      + "  <network_id>d32019d3-bc6e-4319-9c1d-6722fc136a22</network_id>"
      + "  <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
      + "  <dns_nameservers quantum:type=\"list\"/>"
      + "  <allocation_pools>"
      + "    <allocation_pool>"
      + "      <start>192.0.0.2</start>"
      + "      <end>192.255.255.254</end>"
      + "    </allocation_pool>"
      + "  </allocation_pools>"
      + "  <host_routes quantum:type=\"list\"/>"
      + "  <ip_version quantum:type=\"long\">4</ip_version>"
      + "  <gateway_ip>192.0.0.1</gateway_ip>"
      + "  <cidr>192.0.0.0/8</cidr>"
      + "  <id>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</id>"
      + "</subnet>";
    
    private static String SUBNETMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<subnet xmlns=\"http://openstack.org/quantum/api/v2.0\""
    + "  xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\""
    + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
    + "  <name>test_subnet_1</name>"
    + "  <enable_dhcp quantum:type=\"bool\">True</enable_dhcp>"
    + "  <tenant_id>4fd44f30292945e481c7b8a0c8908869</tenant_id>"
    + "  <network_id>d32019d3-bc6e-4319-9c1d-6722fc136a22</network_id>"
    + "  <dns_nameservers quantum:type=\"list\"/>"
    + "  <allocation_pools>"
    + "    <allocation_pool>"
    + "      <start>192.0.0.2</start>"
    + "      <end>192.255.255.254</end>"
    + "    </allocation_pool>"
    + "  </allocation_pools>"
    + "  <host_routes quantum:type=\"list\"/>"
    + "  <ip_version quantum:type=\"long\">4</ip_version>"
    + "  <gateway_ip>192.0.0.1</gateway_ip>"
    + "  <cidr>459.0.0.0/8</cidr>"
    + "  <id>54d6f61d-db07-451c-9ab3-b9609b6b6f0b</id>"
    + "</subnet>";
    
    private static String STORAGECOLLECTIONRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volumes xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/api/openstack-block-storage/2.0/content\">"
      + "  <volume name=\"vol-004\" id=\"45baf976-c20a-4894-a7c3-c94b7376bf55\">"
      + "    <attachments/>"
      + "    <metadata/>"
      + "  </volume>"
      + "  <volume name=\"vol-003\" id=\"5aa119a8-d25b-45a7-8d1b-88e127885635\">"
      + "    <attachments/>"
      + "    <metadata/>"
      + "  </volume>"
      + "</volumes>";
    
    private static String STORAGECOLLECTIONMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volumes xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/api/openstack-block-storage/2.0/content\">"
      + "  <volume name=\"vol-004\" id=\"45baf976-c20a-4894-a7c3-c94b7376bf55\">"
      + "    <attachments/>"
      + "    <metadata/>"
      + "  </volume>"
      + "  <volume name=\"vol-003\" id=\"5aa119a8-d25b-45a7-8d1b-88e127885635\">"
      + "    <attachments/>"
      + "    <metadata/>"
      + "  </volume>"
      + "</volumes>";
    
    private static String STORAGERESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volume"
      + "  xmlns:os-vol-image-meta=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Image_Metadata.html\""
      + "  xmlns:os-vol-tenant-attr=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Tenant_Attribute.html\""
      + "  xmlns:os-vol-host-attr=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Host_Attribute.html\""
      + "  xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/api/openstack-block-storage/2.0/content\""
      + "  status=\"available\" name=\"vol-003\" availability_zone=\"nova\""
      + "  bootable=\"false\" created_at=\"2013-02-25 02:40:21\""
      + "  description=\"This is yet, another volume.\" volume_type=\"None\""
      + "  source_volid=\"None\" snapshot_id=\"None\""
      + "  id=\"5aa119a8-d25b-45a7-8d1b-88e127885635\" size=\"1\""
      + "  os-vol-tenant-attr:tenant_id=\"0c2eba2c5af04d3f9e9d0d410b371fde\""
      + "  os-vol-host-attr:host=\"ip-10-168-107-25\">"
      + "  <attachments/>"
      + "  <metadata>"
      + "    <meta key=\"contents\">not junk</meta>"
      + "  </metadata>"
      + "</volume>";

    private static String IMAGECOLLECTIONRESPONSE =
        "{"
      + "  \"images\": ["
      + "    {"
      + "      \"status\": \"active\","
      + "      \"name\": \"cirros-0.3.2-x86_64-disk\","
      + "      \"tags\": [],"
      + "      \"container_format\": \"bare\","
      + "      \"created_at\": \"2014-11-07T17:07:06Z\","
      + "      \"disk_format\": \"qcow2\","
      + "      \"updated_at\": \"2014-11-07T17:19:09Z\","
      + "      \"visibility\": \"public\","
      + "      \"self\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "      \"min_disk\": 0,"
      + "      \"protected\": false,"
      + "      \"id\": \"1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "      \"file\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27/file\","
      + "      \"checksum\": \"64d7c1cd2b6f60c92c14662941cb7913\","
      + "      \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "      \"size\": 13167616,"
      + "      \"min_ram\": 0,"
      + "      \"schema\": \"/v2/schemas/image\""
      + "    },"
      + "    {"
      + "      \"status\": \"active\","
      + "      \"name\": \"F17-x86_64-cfntools\","
      + "      \"tags\": [],"
      + "      \"container_format\": \"bare\","
      + "      \"created_at\": \"2014-10-30T08:23:39Z\","
      + "      \"disk_format\": \"qcow2\","
      + "      \"updated_at\": \"2014-11-03T16:40:10Z\","
      + "      \"visibility\": \"public\","
      + "      \"self\": \"/v2/images/781b3762-9469-4cec-b58d-3349e5de4e9c\","
      + "      \"min_disk\": 0,"
      + "      \"protected\": false,"
      + "      \"id\": \"781b3762-9469-4cec-b58d-3349e5de4e9c\","
      + "      \"file\": \"/v2/images/781b3762-9469-4cec-b58d-3349e5de4e9c/file\","
      + "      \"checksum\": \"afab0f79bac770d61d24b4d0560b5f70\","
      + "      \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "      \"size\": 476704768,"
      + "      \"min_ram\": 0,"
      + "      \"schema\": \"/v2/schemas/image\""
      + "    }"
      + "  ],"
      + "  \"schema\": \"/v2/schemas/images\","
      + "  \"first\": \"/v2/images\""
      + "}";
            
    private static String IMAGECOLLECTIONMALFORMEDRESPONSE =
        "{"
      + "  \"images\": ["
      + "    {"
      + "      \"status\": \"active\","
      + "      \"name\": \"cirros-0.3.2-x86_64-disk\","
      + "      \"tags\": [],"
      + "      \"container_format\": \"bare\","
      + "      \"created_at\": \"2014-11-07T17:07:06Z\","
      + "      \"disk_format\": \"qcow2\","
      + "      \"updated_at\": \"2014-11-07T17:19:09Z\","
      + "      \"visibility\": \"public\","
      + "      \"self\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "      \"min_disk\": 0,"
      + "      \"protected\": false,"
      + "      \"id\": \"1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "      \"file\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27/file\","
      + "      \"checksum\": \"64d7c1cd2b6f60c92c14662941cb7913\","
      + "      \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "      \"min_ram\": 0,"
      + "      \"schema\": \"/v2/schemas/image\""
      + "    },"
      + "    {"
      + "      \"status\": \"active\","
      + "      \"tags\": [],"
      + "      \"container_format\": \"bare\","
      + "      \"created_at\": \"2014-10-30T08:23:39Z\","
      + "      \"disk_format\": \"qcow2\","
      + "      \"updated_at\": \"2014-11-03T16:40:10Z\","
      + "      \"visibility\": \"public\","
      + "      \"self\": \"/v2/images/781b3762-9469-4cec-b58d-3349e5de4e9c\","
      + "      \"min_disk\": 0,"
      + "      \"protected\": false,"
      + "      \"id\": \"781b3762-9469-4cec-b58d-3349e5de4e9c\","
      + "      \"file\": \"/v2/images/781b3762-9469-4cec-b58d-3349e5de4e9c/file\","
      + "      \"checksum\": \"afab0f79bac770d61d24b4d0560b5f70\","
      + "      \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "      \"size\": 476704768,"
      + "      \"min_ram\": 0,"
      + "      \"schema\": \"/v2/schemas/image\""
      + "    }"
      + "  ],"
      + "  \"schema\": \"/v2/schemas/images\","
      + "  \"first\": \"/v2/images\""
      + "}";
    
    private static String IMAGERESPONSE =
        "{"
      + "  \"status\": \"active\","
      + "  \"name\": \"cirros-0.3.2-x86_64-disk\","
      + "  \"tags\": [],"
      + "  \"container_format\": \"bare\","
      + "  \"created_at\": \"2014-05-05T17:15:10Z\","
      + "  \"disk_format\": \"qcow2\","
      + "  \"updated_at\": \"2014-05-05T17:15:11Z\","
      + "  \"visibility\": \"public\","
      + "  \"self\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "  \"min_disk\": 0,"
      + "  \"protected\": false,"
      + "  \"id\": \"1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "  \"file\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27/file\","
      + "  \"checksum\": \"64d7c1cd2b6f60c92c14662941cb7913\","
      + "  \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "  \"size\": 13167616,"
      + "  \"min_ram\": 0,"
      + "  \"schema\": \"/v2/schemas/image\""
      + "}";
    
    private static String IMAGEMALFORMEDRESPONSE =
    	"{"
      + "  \"status\": \"active\","
      + "  \"name\": \"cirros-0.3.2-x86_64-disk\","
      + "  \"tags\": [],"
      + "  \"container_format\": \"bare\","
      + "  \"created_at\": \"2014-05-05T17:15:10Z\","
      + "  \"disk_format\": \"qcow2\","
      + "  \"updated_at\": \"2014-05-05T17:15:11Z\","
      + "  \"visibility\": \"public\","
      + "  \"self\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27\","
      + "  \"min_disk\": 0,"
      + "  \"protected\": false,"
      + "  \"file\": \"/v2/images/1bea47ed-f6a9-463b-b423-14b9cca9ad27/file\","
      + "  \"checksum\": \"64d7c1cd2b6f60c92c14662941cb7913\","
      + "  \"owner\": \"5ef70662f8b34079a6eddb8da9d75fe8\","
      + "  \"size\": 13167616,"
      + "  \"min_ram\": 0,"
      + "  \"schema\": \"/v2/schemas/image\""
      + "}";
    
    private static String STORAGEMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volume"
      + "  xmlns:os-vol-image-meta=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Image_Metadata.html\""
      + "  xmlns:os-vol-tenant-attr=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Tenant_Attribute.html\""
      + "  xmlns:os-vol-host-attr=\"http://docs.openstack.org/openstack-block-storage/2.0/content/Volume_Host_Attribute.html\""
      + "  xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/api/openstack-block-storage/2.0/content\""
      + "  status=\"available\" name=\"vol-003\" availability_zone=\"nova\""
      + "  bootable=\"false\" created_at=\"2013-02-25 02:40:21\""
      + "  description=\"This is yet, another volume.\" volume_type=\"None\""
      + "  source_volid=\"None\" snapshot_id=\"None\""
      + "  id=\"5aa119a8-d25b-45a7-8d1b-88e127885635\""
      + "  os-vol-tenant-attr:tenant_id=\"0c2eba2c5af04d3f9e9d0d410b371fde\""
      + "  os-vol-host-attr:host=\"ip-10-168-107-25\">"
      + "  <attachments/>"
      + "  <metadata>"
      + "    <meta key=\"contents\">not junk</meta>"
      + "  </metadata>"
      + "</volume>";
    
    private static String COMPUTECOLLECTIONRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<servers xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <server name=\"new-server-test\" id=\"b626796d-d585-4874-b178-78c65289bba4\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/servers/b626796d-d585-4874-b178-78c65289bba4\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/servers/b626796d-d585-4874-b178-78c65289bba4\" rel=\"bookmark\"/>"
      + "  </server>"
      + "</servers>";
    
    private static String COMPUTECOLLECTIONMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<servers xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <server id=\"b626796d-d585-4874-b178-78c65289bba4\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/servers/b626796d-d585-4874-b178-78c65289bba4\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/servers/b626796d-d585-4874-b178-78c65289bba4\" rel=\"bookmark\"/>"
      + "  </server>"
      + "</servers>";
    
    private static String COMPUTERESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<server xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\" status=\"ACTIVE\" updated=\"2012-08-20T21:11:10Z\" hostId=\"1746536de20daadad89a6fab8d6968b1214b0ba9fb37b29e7098e0b9\" name=\"new-server-test\" created=\"2012-08-20T21:11:10Z\" userId=\"fake\" tenantId=\"openstack\" accessIPv4=\"\" accessIPv6=\"\" progress=\"0\" id=\"3f9f7d18-aaf3-4703-b368-ea9b4d609c95\">"
      + "  <image id=\"70a599e0-31e7-49b7-b260-868f441e862b\">"
      + "    <atom:link href=\"http://openstack.example.com/openstack/images/70a599e0-31e7-49b7-b260-868f441e862b\" rel=\"bookmark\"/>"
      + "  </image>"
      + "  <flavor id=\"1\">"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <metadata>"
      + "    <meta key=\"My Server Name\">Apache1</meta>"
      + "  </metadata>"
      + "  <addresses>"
      + "    <network id=\"private\">"
      + "      <ip version=\"4\" addr=\"192.168.0.3\"/>"
      + "    </network>"
      + "  </addresses>"
      + "  <atom:link href=\"http://openstack.example.com/v2/openstack/servers/3f9f7d18-aaf3-4703-b368-ea9b4d609c95\" rel=\"self\"/>"
      + "  <atom:link href=\"http://openstack.example.com/openstack/servers/3f9f7d18-aaf3-4703-b368-ea9b4d609c95\" rel=\"bookmark\"/>"
      + "</server>";
    
    private static String COMPUTEMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<server xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\" status=\"BOOH\" updated=\"2012-08-20T21:11:10Z\" hostId=\"1746536de20daadad89a6fab8d6968b1214b0ba9fb37b29e7098e0b9\" name=\"new-server-test\" created=\"2012-08-20T21:11:10Z\" userId=\"fake\" tenantId=\"openstack\" accessIPv4=\"\" accessIPv6=\"\" progress=\"0\" id=\"3f9f7d18-aaf3-4703-b368-ea9b4d609c95\">"
      + "  <image id=\"70a599e0-31e7-49b7-b260-868f441e862b\">"
      + "    <atom:link href=\"http://openstack.example.com/openstack/images/70a599e0-31e7-49b7-b260-868f441e862b\" rel=\"bookmark\"/>"
      + "  </image>"
      + "  <flavor id=\"1\">"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <metadata>"
      + "    <meta key=\"My Server Name\">Apache1</meta>"
      + "  </metadata>"
      + "  <addresses>"
      + "    <network id=\"private\">"
      + "      <ip version=\"4\" addr=\"192.168.0.3\"/>"
      + "    </network>"
      + "  </addresses>"
      + "  <atom:link href=\"http://openstack.example.com/v2/openstack/servers/3f9f7d18-aaf3-4703-b368-ea9b4d609c95\" rel=\"self\"/>"
      + "  <atom:link href=\"http://openstack.example.com/openstack/servers/3f9f7d18-aaf3-4703-b368-ea9b4d609c95\" rel=\"bookmark\"/>"
      + "</server>";
    
    private static String VOLUMEATTACHMENTRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volumeAttachments>"
      + "  <volumeAttachment device=\"/dev/sdd\" serverId=\"4335bab6-6086-4247-8274-8b8b048edaaa\" id=\"a26887c6-c47b-4654-abb5-dfadf7d3f803\" volumeId=\"a26887c6-c47b-4654-abb5-dfadf7d3f803\"/>"
      + "  <volumeAttachment device=\"/dev/sdc\" serverId=\"4335bab6-6086-4247-8274-8b8b048edaaa\" id=\"a26887c6-c47b-4654-abb5-dfadf7d3f804\" volumeId=\"a26887c6-c47b-4654-abb5-dfadf7d3f804\"/>"
      + "</volumeAttachments>";
    
    private static String VOLUMEATTACHMENTMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<volumeAttachments>"
      + "  <volumeAttachment device=\"/dev/sdd\" serverId=\"4335bab6-6086-4247-8274-8b8b048edaaa\" id=\"a26887c6-c47b-4654-abb5-dfadf7d3f803\" volumeId=\"a26887c6-c47b-4654-abb5-dfadf7d3f803\"/>"
      + "  <volumeAttachment serverId=\"4335bab6-6086-4247-8274-8b8b048edaaa\" id=\"a26887c6-c47b-4654-abb5-dfadf7d3f804\" volumeId=\"a26887c6-c47b-4654-abb5-dfadf7d3f804\"/>"
      + "</volumeAttachments>";
      
    private static String VIRTUALINTERFACESRESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<virtual_interfaces"
      + "  xmlns:OS-EXT-VIF-NET=\"http://docs.openstack.org/compute/ext/extended-virtual-interfaces-net/api/v1.1\""
      + "  xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <virtual_interface id=\"94edf7aa-565a-469a-8f45-656b4acf8229\""
      + "    mac_address=\"fa:16:3e:7d:31:9a\""
      + "    OS-EXT-VIF-NET:net_id=\"94edf7aa-565a-469a-8f45-656b4acf8230\"/>"
      + "</virtual_interfaces>";
    
    private static String VIRTUALINTERFACESMALFORMEDRESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<virtual_interfaces"
      + "  xmlns:OS-EXT-VIF-NET=\"http://docs.openstack.org/compute/ext/extended-virtual-interfaces-net/api/v1.1\""
      + "  xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <virtual_interface id=\"94edf7aa-565a-469a-8f45-656b4acf8229\""
      + "    OS-EXT-VIF-NET:net_id=\"94edf7aa-565a-469a-8f45-656b4acf8230\"/>"
      + "</virtual_interfaces>";
    
    private static String INSTANCETYPECOLLECTIONRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<flavors xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <flavor name=\"m1.tiny\" id=\"1\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/1\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.small\" id=\"2\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/2\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/2\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.medium\" id=\"3\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/3\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/3\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.large\" id=\"4\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/4\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/4\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.xlarge\" id=\"5\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/5\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/5\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "</flavors>";
    
    private static String INSTANCETYPECOLLECTIONMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<flavors xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
      + "  <flavor name=\"m1.tiny\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/1\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.small\" id=\"2\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/2\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/2\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.medium\" id=\"3\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/3\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/3\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.large\" id=\"4\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/4\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/4\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "  <flavor name=\"m1.xlarge\" id=\"5\">"
      + "    <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/5\" rel=\"self\"/>"
      + "    <atom:link href=\"http://openstack.example.com/openstack/flavors/5\" rel=\"bookmark\"/>"
      + "  </flavor>"
      + "</flavors>";
    
    private static String INSTANCETYPERESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<flavor xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\" disk=\"1\" vcpus=\"1\" ram=\"512\" name=\"m1.tiny\" id=\"1\">"
      + "  <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/1\" rel=\"self\"/>"
      + "  <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "</flavor>";
    
    private static String INSTANCETYPEMALFORMEDRESPONSE =
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<flavor xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\" vcpus=\"1\" ram=\"512\" name=\"m1.tiny\" id=\"1\">"
      + "  <atom:link href=\"http://openstack.example.com/v2/openstack/flavors/1\" rel=\"self\"/>"
      + "  <atom:link href=\"http://openstack.example.com/openstack/flavors/1\" rel=\"bookmark\"/>"
      + "</flavor>";
    
    private static String COMPUTEQUOTARESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<quota_set id=\"fake_tenant\">"
      + "  <cores>20</cores>"
      + "  <fixed_ips>-1</fixed_ips>"
      + "  <floating_ips>10</floating_ips>"
      + "  <injected_file_content_bytes>10240</injected_file_content_bytes>"
      + "  <injected_file_path_bytes>255</injected_file_path_bytes>"
      + "  <injected_files>5</injected_files>"
      + "  <instances>10</instances>"
      + "  <key_pairs>100</key_pairs>"
      + "  <metadata_items>128</metadata_items>"
      + "  <ram>51200</ram>"
      + "  <security_group_rules>20</security_group_rules>"
      + "  <security_groups>10</security_groups>"
      + "  <server_groups>10</server_groups>"
      + "  <server_group_members>10</server_group_members>"
      + "</quota_set>";
      
    private static String COMPUTEQUOTAMALFORMEDRESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<quota_set id=\"fake_tenant\">"
      + "  <cores>20</cores>"
      + "  <fixed_ips>-1</fixed_ips>"
      + "  <floating_ips>10</floating_ips>"
      + "  <injected_file_content_bytes>10240</injected_file_content_bytes>"
      + "  <injected_file_path_bytes>255</injected_file_path_bytes>"
      + "  <injected_files>5</injected_files>"
      + "  <instances>10</instances>"
      + "  <key_pairs>100</key_pairs>"
      + "  <metadata_items>128</metadata_items>"
      + "  <security_group_rules>20</security_group_rules>"
      + "  <security_groups>10</security_groups>"
      + "  <server_groups>10</server_groups>"
      + "  <server_group_members>10</server_group_members>"
      + "</quota_set>";
    
    private static String STORAGEQUOTARESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<limits xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/common/api/v1.0\">"
      + "  <rates/>"
      + "  <absolute>"
      + "    <limit name=\"totalSnapshotsUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalVolumeGigabytes\" value=\"1000\"/>"
      + "    <limit name=\"totalGigabytesUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalSnapshots\" value=\"10\"/>"
      + "    <limit name=\"totalVolumesUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalVolumes\" value=\"10\"/>"
      + "  </absolute>"
      + "</limits>";
    
    private static String STORAGEQUOTAMALFORMEDRESPONSE = 
        "<?xml version='1.0' encoding='UTF-8'?>"
      + "<limits xmlns:atom=\"http://www.w3.org/2005/Atom\""
      + "  xmlns=\"http://docs.openstack.org/common/api/v1.0\">"
      + "  <rates/>"
      + "  <absolute>"
      + "    <limit name=\"totalSnapshotsUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalVolumeGigabytes\" value=\"1000\"/>"
      + "    <limit name=\"totalGigabytesUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalSnapshots\" value=\"10\"/>"
      + "    <limit name=\"totalVolumesUsed\" value=\"0\"/>"
      + "    <limit name=\"maxTotalVolumes\" value=\"10\"/>"
      + "  </absolute>"
      + "</limits>";
    
    @Test
    public void testParseAuthorizationResponse() {
        String[] results;
        OpenStackParser parser = new OpenStackParser();
        results = parser.parseAuthorizationResponse(AUTHORIZATIONRESPONSE);
        
        assertEquals(6, results.length);
        assertEquals("aaaaa-bbbbb-ccccc-dddd", results[0]);
        assertEquals("demo", results[1]);
        assertEquals("9a6590b2ab024747bc2167c4e064d00d", results[2]);
        assertEquals(
                "http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57",
                results[3]);
        assertEquals(
                "http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57",
                results[4]);
        assertEquals("http://23.253.72.207:9696/", results[5]);
    }

    @Test
    public void testParseNetworkCollectionResponse() {
    	String[][] results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseNetworkCollectionResponse(NETWORKCOLLECTIONRESPONSE);
            
            assertEquals(2, results.length);
            assertEquals(3, results[0].length);
            assertEquals(4, results[1].length);
            assertEquals("d32019d3-bc6e-4319-9c1d-6722fc136a22", results[0][0]);
            assertEquals("private-network", results[0][1]);
            assertEquals("54d6f61d-db07-451c-9ab3-b9609b6b6f0b", results[0][2]);
            assertEquals("db193ab3-96e3-4cb3-8fc5-05f4296d0324", results[1][0]);
            assertEquals("private", results[1][1]);
            assertEquals("08eae331-0402-425a-923c-34f7cfe39c1b", results[1][2]);
            assertEquals("08eae331-0402-425a-923c-34f7cfe39c1c", results[1][3]);
            
            results =
                parser.parseNetworkCollectionResponse(
                		NETWORKCOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseCreatedNetworkResponse() {
    	String[] results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseCreatedNetworkResponse(NETWORKRESPONSE);
            
            assertEquals(2, results.length);
            assertEquals("d32019d3-bc6e-4319-9c1d-6722fc136a22", results[0]);
            assertEquals("ACTIVE", results[1]);
            
            results =
                parser.parseCreatedNetworkResponse(
                		NETWORKMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseSubnetCollectionResponse() {
        NetworkCollection results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseSubnetCollectionResponse(SUBNETCOLLECTIONRESPONSE);
            
            assertEquals(2, results.getNetwork().size());
            assertEquals("08eae331-0402-425a-923c-34f7cfe39c1b", 
                    results.getNetwork().get(0).getHref());
            assertEquals("54d6f61d-db07-451c-9ab3-b9609b6b6f0b", 
                    results.getNetwork().get(1).getHref());
            assertEquals("private-subnet", 
                    results.getNetwork().get(0).getName());
            assertEquals("my_subnet", 
                    results.getNetwork().get(1).getName());
            
            results =
                parser.parseSubnetCollectionResponse(
                        SUBNETCOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseSubnetResponse() {
    	Network results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseSubnetResponse(SUBNETRESPONSE);
            
            assertEquals("192.0.0.0/8", results.getAddress());
            assertEquals("54d6f61d-db07-451c-9ab3-b9609b6b6f0b",
            		results.getHref());
            assertEquals("test_subnet_1", results.getName());
            assertEquals("C", results.getSize());
            
            results =
                parser.parseSubnetResponse(
                        SUBNETMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseStorageCollectionResponse() {
    	StorageCollection results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseStorageCollectionResponse(STORAGECOLLECTIONRESPONSE);
            
            assertEquals(2, results.getStorage().size());
            assertEquals("45baf976-c20a-4894-a7c3-c94b7376bf55", 
                    results.getStorage().get(0).getHref());
            assertEquals("5aa119a8-d25b-45a7-8d1b-88e127885635", 
                    results.getStorage().get(1).getHref());
            assertEquals("vol-004", 
                    results.getStorage().get(0).getName());
            assertEquals("vol-003", 
                    results.getStorage().get(1).getName());
            
            results =
                parser.parseStorageCollectionResponse(
                		STORAGECOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseStorageResponse() {
    	Storage results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseStorageResponse(STORAGERESPONSE);
            
            assertEquals("This is yet, another volume.",
            		results.getDescription());
            assertEquals("5aa119a8-d25b-45a7-8d1b-88e127885635",
            		results.getHref());
            assertEquals("vol-003", results.getName());
            assertEquals("1", results.getSize());
            assertEquals(StorageType.DATABLOCK, results.getType());
            
            results =
                parser.parseStorageResponse(
                		STORAGEMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseImageCollectionResponse() {
    	StorageCollection results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseImageCollectionResponse(IMAGECOLLECTIONRESPONSE);
            
            assertEquals(2, results.getStorage().size());
            assertEquals("1bea47ed-f6a9-463b-b423-14b9cca9ad27", 
                    results.getStorage().get(0).getHref());
            assertEquals("781b3762-9469-4cec-b58d-3349e5de4e9c", 
                    results.getStorage().get(1).getHref());
            assertEquals("cirros-0.3.2-x86_64-disk", 
                    results.getStorage().get(0).getName());
            assertEquals("F17-x86_64-cfntools", 
                    results.getStorage().get(1).getName());
            
            results =
                parser.parseImageCollectionResponse(
                		IMAGECOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testParseImageResponse() {
    	Storage results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseImageResponse(IMAGERESPONSE);
            
            assertEquals("1bea47ed-f6a9-463b-b423-14b9cca9ad27",
            		results.getHref());
            assertEquals("cirros-0.3.2-x86_64-disk", results.getName());
            assertEquals("13167616", results.getSize());
            assertEquals(StorageType.OS, results.getType());
            
            results =
                parser.parseImageResponse(IMAGEMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testParseComputeCollectionResponse() {
    	ComputeCollection results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseComputeCollectionResponse(COMPUTECOLLECTIONRESPONSE);
            
            assertEquals(1, results.getCompute().size());
            assertEquals("b626796d-d585-4874-b178-78c65289bba4", 
                    results.getCompute().get(0).getHref());
            assertEquals("new-server-test", 
                    results.getCompute().get(0).getName());
            
            results =
                parser.parseComputeCollectionResponse(
                		COMPUTECOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseComputeResponse() {
        Compute results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseComputeResponse(COMPUTERESPONSE);
            
            assertEquals("3f9f7d18-aaf3-4703-b368-ea9b4d609c95",
            	results.getHref());
            assertEquals("new-server-test", results.getName());
            assertEquals(ComputeState.ACTIVE, results.getState());
            assertEquals("1", results.getInstanceType());
            
            results =
                parser.parseComputeResponse(
                		COMPUTEMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseAttachedStoragesResponse() {
        List<Compute.Disk> results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseAttachedStoragesResponse(VOLUMEATTACHMENTRESPONSE);
            
            assertEquals(2, results.size());
            assertEquals("a26887c6-c47b-4654-abb5-dfadf7d3f803",
            	results.get(0).getStorage().getHref());
            assertEquals("a26887c6-c47b-4654-abb5-dfadf7d3f804",
            	results.get(1).getStorage().getHref());
            assertEquals("/dev/sdd", results.get(0).getTarget());
            assertEquals("/dev/sdc", results.get(1).getTarget());
            
            results =
                parser.parseAttachedStoragesResponse(
                		VOLUMEATTACHMENTMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseVirtualInterfacesResponse() {
        List<Compute.Nic> results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseVirtualInterfacesResponse(VIRTUALINTERFACESRESPONSE);
            
            assertEquals(1, results.size());
            assertEquals("a26887c6-c47b-4654-abb5-dfadf7d3f803",
            	results.get(0).getNetwork().getHref());
            assertEquals("fa:16:3e:7d:31:9a", results.get(0).getMac());
            
            results =
                parser.parseVirtualInterfacesResponse(
                		VIRTUALINTERFACESMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseInstanceTypeCollectionResponse() {
    	InstanceTypeCollection results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results =
                parser.parseInstanceTypeCollectionResponse(
                		INSTANCETYPECOLLECTIONRESPONSE);
            
            assertEquals(5, results.getInstanceType().size());
            assertEquals("1", results.getInstanceType().get(0).getHref());
            assertEquals("m1.tiny", results.getInstanceType().get(0).getName());
            assertEquals("2", results.getInstanceType().get(1).getHref());
            assertEquals(
            		"m1.small", results.getInstanceType().get(1).getName());
            assertEquals("3", results.getInstanceType().get(2).getHref());
            assertEquals(
            		"m1.medium", results.getInstanceType().get(2).getName());
            assertEquals("4", results.getInstanceType().get(3).getHref());
            assertEquals(
            		"m1.large", results.getInstanceType().get(3).getName());
            assertEquals("5", results.getInstanceType().get(4).getHref());
            assertEquals(
            		"m1.xlarge", results.getInstanceType().get(4).getName());
            
            results =
                parser.parseInstanceTypeCollectionResponse(
                		INSTANCETYPECOLLECTIONMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseInstanceTypeResponse() {
    	InstanceType results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results = parser.parseInstanceTypeResponse(INSTANCETYPERESPONSE);
            
            assertEquals(1, results.getCpu());
            assertEquals("1", results.getHref());
            assertEquals(512, results.getMemory());
            assertEquals("m1.tiny", results.getName());
            
            results =
            	parser.parseInstanceTypeResponse(INSTANCETYPEMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseComputeQuotaResponse() {
        Quota results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results = parser.parseComputeQuotaResponse(COMPUTEQUOTARESPONSE);
            
            assertEquals(20, results.getCpu());
            assertEquals(51200, results.getMemory());
            assertEquals(10, results.getNumVms());
            
            results =
            	parser.parseComputeQuotaResponse(COMPUTEQUOTAMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParseStorageQuotaResponse() {
        int results;
        OpenStackParser parser = new OpenStackParser();
        try {
            results = parser.parseStorageQuotaResponse(STORAGEQUOTARESPONSE);
            
            assertEquals(10000, results);
            
            results =
            	parser.parseStorageQuotaResponse(STORAGEQUOTAMALFORMEDRESPONSE);
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testFromOpenStackComputeState() {
    	OpenStackParser parser = new OpenStackParser();
    	
        try {
        	assertEquals(ComputeState.ACTIVE,
                parser.fromOpenStackComputeState("ACTIVE"));
        	assertEquals(ComputeState.PENDING,
                parser.fromOpenStackComputeState("BUILDING"));
			assertEquals(ComputeState.DONE,
                parser.fromOpenStackComputeState("DELETED"));
			assertEquals(ComputeState.FAILED,
                parser.fromOpenStackComputeState("ERROR"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("HARD_REBOOT"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("PASSWORD"));
			assertEquals(ComputeState.SUSPENDED,
                parser.fromOpenStackComputeState("PAUSED"));
			assertEquals(ComputeState.REBOOT,
                parser.fromOpenStackComputeState("REBOOT"));
			assertEquals(ComputeState.PENDING,
                parser.fromOpenStackComputeState("REBUILD"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("RESCUED"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("RESIZED"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("REVERT_RESIZE"));
			assertEquals(ComputeState.SHUTDOWN,
                parser.fromOpenStackComputeState("SHUTOFF"));
			assertEquals(ComputeState.DONE,
                parser.fromOpenStackComputeState("SOFT_DELETED"));
			assertEquals(ComputeState.STOPPED,
                parser.fromOpenStackComputeState("STOPPED"));
			assertEquals(ComputeState.SUSPENDED,
                parser.fromOpenStackComputeState("SUSPENDED"));
			assertEquals(ComputeState.FAILED,
                parser.fromOpenStackComputeState("UNKNOWN"));
			assertEquals(ComputeState.RESET,
                parser.fromOpenStackComputeState("VERIFY_RESIZE"));
			parser.fromOpenStackComputeState("NOT EXISTING ONE");
	        fail();
		} catch (OCCIException e) {
			assertTrue(true);
		}
    }

    @Test
    public void testToOpenStackComputeState() {
    	OpenStackParser parser = new OpenStackParser();
    	
        try {
        	assertEquals("ACTIVE",
                parser.toOpenStackComputeState(ComputeState.ACTIVE));
        	assertEquals("DELETED",
                parser.toOpenStackComputeState(ComputeState.CANCEL));
        	assertEquals("DELETED",
                parser.toOpenStackComputeState(ComputeState.DONE));
        	assertEquals("ERROR",
                parser.toOpenStackComputeState(ComputeState.FAILED));
        	assertEquals("PAUSED",
                parser.toOpenStackComputeState(ComputeState.HOLD));
        	assertEquals("BUILDING",
                parser.toOpenStackComputeState(ComputeState.INIT));
        	assertEquals("BUILDING",
                parser.toOpenStackComputeState(ComputeState.PENDING));
        	assertEquals("REBOOT",
                parser.toOpenStackComputeState(ComputeState.REBOOT));
        	assertEquals("REBUILD",
                parser.toOpenStackComputeState(ComputeState.RESET));
        	assertEquals("ACTIVE",
                parser.toOpenStackComputeState(ComputeState.RESUME));
        	assertEquals("SHUTOFF",
                parser.toOpenStackComputeState(ComputeState.SHUTDOWN));
        	assertEquals("STOPPED",
                parser.toOpenStackComputeState(ComputeState.STOPPED));
        	assertEquals("SUSPENDED",
                parser.toOpenStackComputeState(ComputeState.SUSPENDED));
		} catch (OCCIException e) {
			fail();
		}
    }

}
