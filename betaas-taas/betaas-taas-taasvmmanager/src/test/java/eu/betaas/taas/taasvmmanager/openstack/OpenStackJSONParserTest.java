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
import eu.betaas.taas.taasvmmanager.openstack.parser.OpenStackJSONParser;
import eu.betaas.taas.taasvmmanager.openstack.parser.OpenStackJSONParser;

public class OpenStackJSONParserTest {

    /******* Test XML responses *******/
    private static String AUTHORIZATIONRESPONSE =
    	"{"
      + "  \"access\": {"
      + "    \"token\": {"
      + "      \"issued_at\": \"2014-01-30T15:30:58.819584\","
      + "      \"expires\": \"2014-01-31T15:30:58Z\","
      + "      \"id\": \"aaaaa-bbbbb-ccccc-dddd\","
      + "      \"tenant\": {"
      + "        \"description\": null,"
      + "        \"enabled\": true,"
      + "        \"id\": \"fc394f2ab2df4114bde39905f800dc57\","
      + "        \"name\": \"demo\""
      + "      }"
      + "    },"
      + "    \"serviceCatalog\": ["
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"id\": \"2dad48f09e2a447a9bf852bcd93548ef\","
      + "            \"publicURL\": \"http://23.253.72.207:8774/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"compute\","
      + "        \"name\": \"nova\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:9696/\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:9696/\","
      + "            \"id\": \"97c526db8d7a4c88bbb8d68db1bdcdb8\","
      + "            \"publicURL\": \"http://23.253.72.207:9696/\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"network\","
      + "        \"name\": \"neutron\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"id\": \"93f86dfcbba143a39a33d0c2cd424870\","
      + "            \"publicURL\": \"http://23.253.72.207:8776/v2/fc394f2ab2df4114bde39905f800dc57\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"volumev2\","
      + "        \"name\": \"cinder\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8774/v3\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8774/v3\","
      + "            \"id\": \"3eb274b12b1d47b2abc536038d87339e\","
      + "            \"publicURL\": \"http://23.253.72.207:8774/v3\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"computev3\","
      + "        \"name\": \"nova\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:3333\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:3333\","
      + "            \"id\": \"957f1e54afc64d33a62099faa5e980a2\","
      + "            \"publicURL\": \"http://23.253.72.207:3333\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"s3\","
      + "        \"name\": \"s3\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:9292\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:9292\","
      + "            \"id\": \"27d5749f36864c7d96bebf84a5ec9767\","
      + "            \"publicURL\": \"http://23.253.72.207:9292\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"image\","
      + "        \"name\": \"glance\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\","
      + "            \"id\": \"37c83a2157f944f1972e74658aa0b139\","
      + "            \"publicURL\": \"http://23.253.72.207:8776/v1/fc394f2ab2df4114bde39905f800dc57\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"volume\","
      + "        \"name\": \"cinder\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8773/services/Admin\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8773/services/Cloud\","
      + "            \"id\": \"289b59289d6048e2912b327e5d3240ca\","
      + "            \"publicURL\": \"http://23.253.72.207:8773/services/Cloud\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"ec2\","
      + "        \"name\": \"ec2\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:8080\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:8080/v1/AUTH_fc394f2ab2df4114bde39905f800dc57\","
      + "            \"id\": \"16b76b5e5b7d48039a6e4cc3129545f3\","
      + "            \"publicURL\": \"http://23.253.72.207:8080/v1/AUTH_fc394f2ab2df4114bde39905f800dc57\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"object-store\","
      + "        \"name\": \"swift\""
      + "      },"
      + "      {"
      + "        \"endpoints\": ["
      + "          {"
      + "            \"adminURL\": \"http://23.253.72.207:35357/v2.0\","
      + "            \"region\": \"RegionOne\","
      + "            \"internalURL\": \"http://23.253.72.207:5000/v2.0\","
      + "            \"id\": \"26af053673df4ef3a2340c4239e21ea2\","
      + "            \"publicURL\": \"http://23.253.72.207:5000/v2.0\""
      + "          }"
      + "        ],"
      + "        \"endpoints_links\": [],"
      + "        \"type\": \"identity\","
      + "        \"name\": \"keystone\""
      + "      }"
      + "    ],"
      + "    \"user\": {"
      + "      \"username\": \"demo\","
      + "      \"roles_links\": [],"
      + "      \"id\": \"9a6590b2ab024747bc2167c4e064d00d\","
      + "      \"roles\": ["
      + "        {"
      + "          \"name\": \"Member\""
      + "        },"
      + "        {"
      + "          \"name\": \"anotherrole\""
      + "        }"
      + "      ],"
      + "      \"name\": \"demo\""
      + "    },"
      + "    \"metadata\": {"
      + "      \"is_admin\": 0,"
      + "      \"roles\": ["
      + "        \"7598ac3c634d4c3da4b9126a5f67ca2b\","
      + "        \"f95c0ab82d6045d9805033ee1fbc80d4\""
      + "      ]"
      + "    }"
      + "  }"
      + "}";
    
    private static String TENANTSRESPONSE =
    	"{"
      + "  \"tenants\": ["
      + "    {"
      + "      \"id\": \"1234\","
      + "      \"name\": \"ACME Corp\","
      + "      \"description\": \"A description ...\","
      + "      \"enabled\": true"
      + "    },"
      + "    {"
      + "      \"id\": \"3456\","
      + "      \"name\": \"Iron Works\","
      + "      \"description\": \"A description ...\","
      + "      \"enabled\": true"
      + "    }"
      + "  ],"
      + "  \"tenants_links\": []"
      + "}";
    
    private static String NETWORKCOLLECTIONRESPONSE =
    	"{"
      + "  \"networks\": ["
      + "    {"
      + "      \"status\": \"ACTIVE\","
      + "      \"subnets\": ["
      + "        \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "      ],"
      + "      \"name\": \"private-network\","
      + "      \"provider:physical_network\": null,"
      + "      \"admin_state_up\": true,"
      + "      \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "      \"provider:network_type\": \"local\","
      + "      \"router:external\": true,"
      + "      \"shared\": true,"
      + "      \"id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "      \"provider:segmentation_id\": null"
      + "    },"
      + "    {"
      + "      \"status\": \"ACTIVE\","
      + "      \"subnets\": ["
      + "        \"08eae331-0402-425a-923c-34f7cfe39c1b\","
      + "        \"08eae331-0402-425a-923c-34f7cfe39c1c\""
      + "      ],"
      + "      \"name\": \"private\","
      + "      \"provider:physical_network\": null,"
      + "      \"admin_state_up\": true,"
      + "      \"tenant_id\": \"26a7980765d0414dbc1fc1f88cdb7e6e\","
      + "      \"provider:network_type\": \"local\","
      + "      \"router:external\": true,"
      + "      \"shared\": true,"
      + "      \"id\": \"db193ab3-96e3-4cb3-8fc5-05f4296d0324\","
      + "      \"provider:segmentation_id\": null"
      + "    }"
      + "  ]"
      + "}";
    
    private static String NETWORKCOLLECTIONMALFORMEDRESPONSE =
   		"{"
      + "  \"networks\": ["
      + "    {"
      + "      \"status\": \"ACTIVE\","
      + "      \"subnets\": ["
      + "        \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "      ],"
      + "      \"name\": \"private-network\","
      + "      \"provider:physical_network\": null,"
      + "      \"admin_state_up\": true,"
      + "      \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "      \"provider:network_type\": \"local\","
      + "      \"router:external\": true,"
      + "      \"shared\": true,"
      + "      \"id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "      \"provider:segmentation_id\": null"
      + "    },"
      + "    {"
      + "      \"status\": \"ACTIVE\","
      + "      \"subnets\": ["
      + "        \"08eae331-0402-425a-923c-34f7cfe39c1b\""
      + "      ],"
      + "      \"name\": \"private\","
      + "      \"provider:physical_network\": null,"
      + "      \"admin_state_up\": true,"
      + "      \"tenant_id\": \"26a7980765d0414dbc1fc1f88cdb7e6e\","
      + "      \"provider:network_type\": \"local\","
      + "      \"router:external\": true,"
      + "      \"shared\": true,"
      + "      \"provider:segmentation_id\": null"
      + "    }"
      + "  ]"
      + "}";
    
    private static String NETWORKRESPONSE =
   		"{"
      + "  \"network\": {"
      + "    \"status\": \"ACTIVE\","
      + "    \"subnets\": ["
      + "      \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "    ],"
      + "    \"name\": \"private-network\","
      + "    \"provider:physical_network\": null,"
      + "    \"admin_state_up\": true,"
      + "    \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "    \"provider:network_type\": \"local\","
      + "    \"router:external\": true,"
      + "    \"shared\": true,"
      + "    \"id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "    \"provider:segmentation_id\": null"
      + "  }"
      + "}";
    
    private static String NETWORKMALFORMEDRESPONSE =
   		"{"
      + "  \"network\": {"
      + "    \"status\": \"ACTIVE\","
      + "    \"subnets\": ["
      + "      \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "    ],"
      + "    \"provider:physical_network\": null,"
      + "    \"admin_state_up\": true,"
      + "    \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "    \"provider:network_type\": \"local\","
      + "    \"router:external\": true,"
      + "    \"shared\": true,"
      + "    \"id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "    \"provider:segmentation_id\": null"
      + "  }"
      + "}";
    
    private static String SUBNETCOLLECTIONRESPONSE =
   		"{"
      + "  \"subnets\": ["
      + "    {"
      + "      \"name\": \"private-subnet\","
      + "      \"enable_dhcp\": true,"
      + "      \"network_id\": \"db193ab3-96e3-4cb3-8fc5-05f4296d0324\","
      + "      \"tenant_id\": \"26a7980765d0414dbc1fc1f88cdb7e6e\","
      + "      \"dns_nameservers\": [],"
      + "      \"allocation_pools\": ["
      + "        {"
      + "          \"start\": \"10.0.0.2\","
      + "          \"end\": \"10.0.0.254\""
      + "        }"
      + "      ],"
      + "      \"host_routes\": [],"
      + "      \"ip_version\": 4,"
      + "      \"gateway_ip\": \"10.0.0.1\","
      + "      \"cidr\": \"10.0.0.0/24\","
      + "      \"id\": \"08eae331-0402-425a-923c-34f7cfe39c1b\""
      + "    },"
      + "    {"
      + "      \"name\": \"my_subnet\","
      + "      \"enable_dhcp\": true,"
      + "      \"network_id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "      \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "      \"dns_nameservers\": [],"
      + "      \"allocation_pools\": ["
      + "        {"
      + "          \"start\": \"192.0.0.2\","
      + "          \"end\": \"192.255.255.254\""
      + "        }"
      + "      ],"
      + "      \"host_routes\": [],"
      + "      \"ip_version\": 4,"
      + "      \"gateway_ip\": \"192.0.0.1\","
      + "      \"cidr\": \"192.0.0.0/8\","
      + "      \"id\": \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String SUBNETCOLLECTIONMALFORMEDRESPONSE =
   		"{"
      + "  \"subnets\": ["
      + "    {"
      + "      \"name\": \"private-subnet\","
      + "      \"enable_dhcp\": true,"
      + "      \"network_id\": \"db193ab3-96e3-4cb3-8fc5-05f4296d0324\","
      + "      \"tenant_id\": \"26a7980765d0414dbc1fc1f88cdb7e6e\","
      + "      \"dns_nameservers\": [],"
      + "      \"allocation_pools\": ["
      + "        {"
      + "          \"start\": \"10.0.0.2\","
      + "          \"end\": \"10.0.0.254\""
      + "        }"
      + "      ],"
      + "      \"host_routes\": [],"
      + "      \"ip_version\": 4,"
      + "      \"gateway_ip\": \"10.0.0.1\","
      + "      \"cidr\": \"10.0.0.0/24\","
      + "      \"id\": \"08eae331-0402-425a-923c-34f7cfe39c1b\""
      + "    },"
      + "    {"
      + "      \"enable_dhcp\": true,"
      + "      \"network_id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "      \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "      \"dns_nameservers\": [],"
      + "      \"allocation_pools\": ["
      + "        {"
      + "          \"start\": \"192.0.0.2\","
      + "          \"end\": \"192.255.255.254\""
      + "        }"
      + "      ],"
      + "      \"host_routes\": [],"
      + "      \"ip_version\": 4,"
      + "      \"gateway_ip\": \"192.0.0.1\","
      + "      \"cidr\": \"192.0.0.0/8\","
      + "      \"id\": \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String SUBNETRESPONSE =
   		"{"
      + "  \"subnet\": {"
      + "    \"name\": \"test_subnet_1\","
      + "    \"enable_dhcp\": true,"
      + "    \"network_id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "    \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "    \"dns_nameservers\": [],"
      + "    \"allocation_pools\": ["
      + "      {"
      + "        \"start\": \"192.0.0.2\","
      + "        \"end\": \"192.255.255.254\""
      + "      }"
      + "    ],"
      + "    \"host_routes\": [],"
      + "    \"ip_version\": 4,"
      + "    \"gateway_ip\": \"192.0.0.1\","
      + "    \"cidr\": \"192.0.0.0/8\","
      + "    \"id\": \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "  }"
      + "}";
    
    private static String SUBNETMALFORMEDRESPONSE =
   		"{"
      + "  \"subnet\": {"
      + "    \"name\": \"my_subnet\","
      + "    \"enable_dhcp\": true,"
      + "    \"network_id\": \"d32019d3-bc6e-4319-9c1d-6722fc136a22\","
      + "    \"tenant_id\": \"4fd44f30292945e481c7b8a0c8908869\","
      + "    \"dns_nameservers\": [],"
      + "    \"allocation_pools\": ["
      + "      {"
      + "        \"start\": \"192.0.0.2\","
      + "        \"end\": \"192.255.255.254\""
      + "      }"
      + "    ],"
      + "    \"host_routes\": [],"
      + "    \"ip_version\": 4,"
      + "    \"gateway_ip\": \"192.0.0.1\","
      + "    \"id\": \"54d6f61d-db07-451c-9ab3-b9609b6b6f0b\""
      + "  }"
      + "}";
    
    private static String STORAGECOLLECTIONRESPONSE =
   		"{"
      + "  \"volumes\": ["
      + "    {"
      + "      \"id\": \"521752a6-acf6-4b2d-bc7a-119f9148cd8c\","
      + "      \"display_name\": \"vol-004\","
      + "      \"display_description\": \"Another volume.\","
      + "      \"size\": 30,"
      + "      \"volume_type\": \"289da7f8-6440-407c-9fb4-7db01ec49164\","
      + "      \"metadata\": {"
      + "        \"contents\": \"junk\""
      + "      },"
      + "      \"availability_zone\": \"us-east1\","
      + "      \"snapshot_id\": null,"
      + "      \"attachments\": [],"
      + "      \"created_at\": \"2012-02-14T20:53:07Z\""
      + "    },"
      + "    {"
      + "      \"id\": \"76b8950a-8594-4e5b-8dce-0dfa9c696358\","
      + "      \"display_name\": \"vol-003\","
      + "      \"display_description\": \"Yet another volume.\","
      + "      \"size\": 25,"
      + "      \"volume_type\": \"96c3bda7-c82a-4f50-be73-ca7621794835\","
      + "      \"metadata\": {},"
      + "      \"availability_zone\": \"us-east2\","
      + "      \"snapshot_id\": null,"
      + "      \"attachments\": [],"
      + "      \"created_at\": \"2012-03-15T19:10:03Z\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String STORAGECOLLECTIONMALFORMEDRESPONSE =
    	"{"
      + "  \"volumes\": ["
      + "    {"
      + "      \"id\": \"521752a6-acf6-4b2d-bc7a-119f9148cd8c\","
      + "      \"display_name\": \"vol-001\","
      + "      \"display_description\": \"Another volume.\","
      + "      \"size\": 30,"
      + "      \"volume_type\": \"289da7f8-6440-407c-9fb4-7db01ec49164\","
      + "      \"metadata\": {"
      + "        \"contents\": \"junk\""
      + "      },"
      + "      \"availability_zone\": \"us-east1\","
      + "      \"snapshot_id\": null,"
      + "      \"attachments\": [],"
      + "      \"created_at\": \"2012-02-14T20:53:07Z\""
      + "    },"
      + "    {"
      + "      \"display_name\": \"vol-002\","
      + "      \"display_description\": \"Yet another volume.\","
      + "      \"size\": 25,"
      + "      \"volume_type\": \"96c3bda7-c82a-4f50-be73-ca7621794835\","
      + "      \"metadata\": {},"
      + "      \"availability_zone\": \"us-east2\","
      + "      \"snapshot_id\": null,"
      + "      \"attachments\": [],"
      + "      \"created_at\": \"2012-03-15T19:10:03Z\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String STORAGERESPONSE =
   		"{"
      + "  \"volume\": {"
      + "    \"id\": \"521752a6-acf6-4b2d-bc7a-119f9148cd8c\","
      + "    \"display_name\": \"vol-001\","
      + "    \"display_description\": \"Another volume.\","
      + "    \"size\": 30,"
      + "    \"volume_type\": \"289da7f8-6440-407c-9fb4-7db01ec49164\","
      + "    \"metadata\": {"
      + "      \"contents\": \"junk\""
      + "    },"
      + "    \"availability_zone\": \"us-east1\","
      + "    \"bootable\": \"false\","
      + "    \"snapshot_id\": null,"
      + "    \"attachments\": [],"
      + "    \"created_at\": \"2012-02-14T20:53:07Z\""
      + "  }"
      + "}";

    private static String STORAGEMALFORMEDRESPONSE =
   		"{"
      + "  \"volume\": {"
      + "    \"id\": \"521752a6-acf6-4b2d-bc7a-119f9148cd8c\","
      + "    \"display_name\": \"vol-001\","
      + "    \"display_description\": \"Another volume.\","
      + "    \"volume_type\": \"289da7f8-6440-407c-9fb4-7db01ec49164\","
      + "    \"metadata\": {"
      + "      \"contents\": \"junk\""
      + "    },"
      + "    \"availability_zone\": \"us-east1\","
      + "    \"bootable\": \"false\","
      + "    \"snapshot_id\": null,"
      + "    \"attachments\": [],"
      + "    \"created_at\": \"2012-02-14T20:53:07Z\""
      + "  }"
      + "}";
    
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
    
    private static String COMPUTECOLLECTIONRESPONSE =
    	"{"
      + "  \"servers\": ["
      + "    {"
      + "      \"id\": \"616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"new-server-test1\""
      + "    },"
      + "    {"
      + "      \"id\": \"616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"new-server-test2\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String COMPUTECOLLECTIONMALFORMEDRESPONSE =
    	"{"
      + "  \"servers\": ["
      + "    {"
      + "      \"id\": \"616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd19\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"new-server-test1\""
      + "    },"
      + "    {"
      + "      \"id\": \"616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/servers/616fb98f-46ca-475e-917e-2563e5a8cd20\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    }"
      + "  ]"
      + "}";
    
    private static String COMPUTERESPONSE =
   		"{"
      + "  \"server\": {"
      + "    \"accessIPv4\": \"\","
      + "    \"accessIPv6\": \"\","
      + "    \"addresses\": {"
      + "      \"private\": ["
      + "        {"
      + "          \"addr\": \"192.168.0.3\","
      + "          \"version\": 4"
      + "        }"
      + "      ]"
      + "    },"
      + "    \"created\": \"2012-08-20T21:11:09Z\","
      + "    \"flavor\": {"
      + "      \"id\": \"1\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    },"
      + "    \"hostId\": \"65201c14a29663e06d0748e561207d998b343e1d164bfa0aafa9c45d\","
      + "    \"id\": \"893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "    \"image\": {"
      + "      \"id\": \"70a599e0-31e7-49b7-b260-868f441e862b\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/images/70a599e0-31e7-49b7-b260-868f441e862b\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    },"
      + "    \"links\": ["
      + "      {"
      + "        \"href\": \"http://openstack.example.com/v2/openstack/servers/893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "        \"rel\": \"self\""
      + "      },"
      + "      {"
      + "        \"href\": \"http://openstack.example.com/openstack/servers/893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "        \"rel\": \"bookmark\""
      + "      }"
      + "    ],"
      + "    \"metadata\": {"
      + "      \"My Server Name\": \"Apache1\""
      + "    },"
      + "    \"name\": \"new-server-test\","
      + "    \"progress\": 0,"
      + "    \"status\": \"ACTIVE\","
      + "    \"tenant_id\": \"openstack\","
      + "    \"updated\": \"2012-08-20T21:11:09Z\","
      + "    \"user_id\": \"fake\""
      + "  }"
      + "}";
    
    private static String COMPUTEMALFORMEDRESPONSE =
   		"{"
      + "  \"server\": {"
      + "    \"accessIPv4\": \"\","
      + "    \"accessIPv6\": \"\","
      + "    \"addresses\": {"
      + "      \"private\": ["
      + "        {"
      + "          \"addr\": \"192.168.0.3\","
      + "          \"version\": 4"
      + "        }"
      + "      ]"
      + "    },"
      + "    \"created\": \"2012-08-20T21:11:09Z\","
      + "    \"flavor\": {"
      + "      \"id\": \"1\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    },"
      + "    \"hostId\": \"65201c14a29663e06d0748e561207d998b343e1d164bfa0aafa9c45d\","
      + "    \"id\": \"893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "    \"image\": {"
      + "      \"id\": \"70a599e0-31e7-49b7-b260-868f441e862b\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/images/70a599e0-31e7-49b7-b260-868f441e862b\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    },"
      + "    \"links\": ["
      + "      {"
      + "        \"href\": \"http://openstack.example.com/v2/openstack/servers/893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "        \"rel\": \"self\""
      + "      },"
      + "      {"
      + "        \"href\": \"http://openstack.example.com/openstack/servers/893c7791-f1df-4c3d-8383-3caae9656c62\","
      + "        \"rel\": \"bookmark\""
      + "      }"
      + "    ],"
      + "    \"metadata\": {"
      + "      \"My Server Name\": \"Apache1\""
      + "    },"
      + "    \"name\": \"new-server-test\","
      + "    \"progress\": 0,"
      + "    \"tenant_id\": \"openstack\","
      + "    \"updated\": \"2012-08-20T21:11:09Z\","
      + "    \"user_id\": \"fake\""
      + "  }"
      + "}";
    
    private static String VOLUMEATTACHMENTRESPONSE =
   		"{"
      + "  \"volumeAttachments\": ["
      + "    {"
      + "      \"device\": \"/dev/sdd\","
      + "      \"id\": \"a26887c6-c47b-4654-abb5-dfadf7d3f803\","
      + "      \"serverId\": \"4d8c3732-a248-40ed-bebc-539a6ffd25c0\","
      + "      \"volumeId\": \"a26887c6-c47b-4654-abb5-dfadf7d3f803\""
      + "    },"
      + "    {"
      + "      \"device\": \"/dev/sdc\","
      + "      \"id\": \"a26887c6-c47b-4654-abb5-dfadf7d3f804\","
      + "      \"serverId\": \"4d8c3732-a248-40ed-bebc-539a6ffd25c0\","
      + "      \"volumeId\": \"a26887c6-c47b-4654-abb5-dfadf7d3f804\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String VOLUMEATTACHMENTMALFORMEDRESPONSE =
   		"{"
      + "  \"volumeAttachments\": ["
      + "    {"
      + "      \"device\": \"/dev/sdd\","
      + "      \"id\": \"a26887c6-c47b-4654-abb5-dfadf7d3f803\","
      + "      \"serverId\": \"4d8c3732-a248-40ed-bebc-539a6ffd25c0\""
      + "    },"
      + "    {"
      + "      \"device\": \"/dev/sdc\","
      + "      \"id\": \"a26887c6-c47b-4654-abb5-dfadf7d3f804\","
      + "      \"serverId\": \"4d8c3732-a248-40ed-bebc-539a6ffd25c0\","
      + "      \"volumeId\": \"a26887c6-c47b-4654-abb5-dfadf7d3f804\""
      + "    }"
      + "  ]"
      + "}";
      
    private static String VIRTUALINTERFACESRESPONSE = 
   		"{"
      + "  \"interfaceAttachments\": ["
      + "    {"
      + "      \"port_state\": \"ACTIVE\","
      + "      \"fixed_ips\": ["
      + "        {"
      + "          \"subnet_id\": \"a26887c6-c47b-4654-abb5-dfadf7d3f803\","
      + "          \"ip_address\": \"192.168.1.3\""
      + "        }"
      + "      ],"
      + "      \"net_id\": \"3cb9bc59-5699-4588-a4b1-b87f96708bc6\","
      + "      \"port_id\": \"ce531f90-199f-48c0-816c-13e38010b442\","
      + "      \"mac_addr\": \"fa:16:3e:4c:2c:30\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String VIRTUALINTERFACESMALFORMEDRESPONSE = 
   		"{"
      + "  \"interfaceAttachments\": ["
      + "    {"
      + "      \"port_state\": \"ACTIVE\","
      + "      \"fixed_ips\": ["
      + "        {"
      + "          \"subnet_id\": \"f8a6e8f8-c2ec-497c-9f23-da9616de54ef\","
      + "          \"ip_address\": \"192.168.1.3\""
      + "        }"
      + "      ],"
      + "      \"net_id\": \"3cb9bc59-5699-4588-a4b1-b87f96708bc6\","
      + "      \"port_id\": \"ce531f90-199f-48c0-816c-13e38010b442\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String INSTANCETYPECOLLECTIONRESPONSE =
   		"{"
      + "  \"flavors\": ["
      + "    {"
      + "      \"id\": \"1\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/1\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.tiny\""
      + "    },"
      + "    {"
      + "      \"id\": \"2\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/2\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/2\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.small\""
      + "    },"
      + "    {"
      + "      \"id\": \"3\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/3\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/3\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.medium\""
      + "    },"
      + "    {"
      + "      \"id\": \"4\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/4\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/4\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.large\""
      + "    },"
      + "    {"
      + "      \"id\": \"5\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/5\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/5\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.xlarge\""
      + "    }"
      + "  ]"
      + "}";
    
    private static String INSTANCETYPECOLLECTIONMALFORMEDRESPONSE =
   		"{"
      + "  \"flavors\": ["
      + "    {"
      + "      \"id\": \"1\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/1\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.tiny\""
      + "    },"
      + "    {"
      + "      \"id\": \"2\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/2\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/2\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.small\""
      + "    },"
      + "    {"
      + "      \"id\": \"3\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/3\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/3\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.medium\""
      + "    },"
      + "    {"
      + "      \"id\": \"4\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/4\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/4\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ],"
      + "      \"name\": \"m1.large\""
      + "    },"
      + "    {"
      + "      \"id\": \"5\","
      + "      \"links\": ["
      + "        {"
      + "          \"href\": \"http://openstack.example.com/v2/openstack/flavors/5\","
      + "          \"rel\": \"self\""
      + "        },"
      + "        {"
      + "          \"href\": \"http://openstack.example.com/openstack/flavors/5\","
      + "          \"rel\": \"bookmark\""
      + "        }"
      + "      ]"
      + "    }"
      + "  ]"
      + "}";
    
    private static String INSTANCETYPERESPONSE =
   		"{"
      + "  \"flavor\": {"
      + "    \"disk\": 1,"
      + "    \"id\": \"1\","
      + "    \"links\": ["
      + "      {"
      + "        \"href\": \"http://openstack.example.com/v2/openstack/flavors/1\","
      + "        \"rel\": \"self\""
      + "      },"
      + "      {"
      + "        \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "        \"rel\": \"bookmark\""
      + "      }"
      + "    ],"
      + "    \"name\": \"m1.tiny\","
      + "    \"ram\": 512,"
      + "    \"vcpus\": 1"
      + "  }"
      + "}";
    
    private static String INSTANCETYPEMALFORMEDRESPONSE =
    	"{"
      + "  \"flavor\": {"
      + "    \"disk\": 1,"
      + "    \"id\": \"1\","
      + "    \"links\": ["
      + "      {"
      + "        \"href\": \"http://openstack.example.com/v2/openstack/flavors/1\","
      + "        \"rel\": \"self\""
      + "      },"
      + "      {"
      + "        \"href\": \"http://openstack.example.com/openstack/flavors/1\","
      + "        \"rel\": \"bookmark\""
      + "      }"
      + "    ],"
      + "    \"name\": \"m1.tiny\","
      + "    \"ram\": 512"
      + "  }"
      + "}";

    
    private static String COMPUTEQUOTARESPONSE = 
   		"{"
      + "  \"quota_set\": {"
      + "    \"cores\": 20,"
      + "    \"fixed_ips\": -1,"
      + "    \"floating_ips\": 10,"
      + "    \"id\": \"fake_tenant\","
      + "    \"injected_file_content_bytes\": 10240,"
      + "    \"injected_file_path_bytes\": 255,"
      + "    \"injected_files\": 5,"
      + "    \"instances\": 10,"
      + "    \"key_pairs\": 100,"
      + "    \"metadata_items\": 128,"
      + "    \"ram\": 51200,"
      + "    \"security_group_rules\": 20,"
      + "    \"security_groups\": 10,"
      + "    \"server_group_members\": 10,"
      + "    \"server_groups\": 10"
      + "  }"
      + "}";
      
    private static String COMPUTEQUOTAMALFORMEDRESPONSE = 
    	"{"
      + "  \"quota_set\": {"
      + "    \"fixed_ips\": -1,"
      + "    \"floating_ips\": 10,"
      + "    \"id\": \"fake_tenant\","
      + "    \"injected_file_content_bytes\": 10240,"
      + "    \"injected_file_path_bytes\": 255,"
      + "    \"injected_files\": 5,"
      + "    \"instances\": 10,"
      + "    \"key_pairs\": 100,"
      + "    \"metadata_items\": 128,"
      + "    \"ram\": 51200,"
      + "    \"security_group_rules\": 20,"
      + "    \"security_groups\": 10,"
      + "    \"server_group_members\": 10,"
      + "    \"server_groups\": 10"
      + "  }"
      + "}";
    
    private static String STORAGEQUOTARESPONSE = 
   		"{"
      + "  \"limits\": {"
      + "    \"rate\": [],"
      + "    \"absolute\": {"
      + "      \"totalSnapshotsUsed\": 0,"
      + "      \"maxTotalVolumeGigabytes\": 1000,"
      + "      \"totalGigabytesUsed\": 0,"
      + "      \"maxTotalSnapshots\": 10,"
      + "      \"totalVolumesUsed\": 0,"
      + "      \"maxTotalVolumes\": 10"
      + "    }"
      + "  }"
      + "}";
    
    private static String STORAGEQUOTAMALFORMEDRESPONSE = 
   		"{"
      + "  \"limits\": {"
      + "    \"rate\": [],"
      + "    \"absolute\": {"
      + "      \"totalSnapshotsUsed\": 0,"
      + "      \"maxTotalVolumeGigabytes\": 1000,"
      + "      \"totalGigabytesUsed\": 0,"
      + "      \"maxTotalSnapshots\": 10,"
      + "      \"totalVolumesUsed\": 0"
      + "    }"
      + "  }"
      + "}";
    
    @Test
    public void testParseAuthorizationResponse() {
        String[] results;
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
			results = parser.parseAuthorizationResponse(AUTHORIZATIONRESPONSE);
			
			assertEquals(6, results.length);
	        assertEquals("aaaaa-bbbbb-ccccc-dddd", results[0]);
	        assertEquals("demo", results[1]);
	        assertEquals("9a6590b2ab024747bc2167c4e064d00d", results[2]);
	        assertEquals(
	                "http://23.253.72.207:8774",
	                results[3]);
	        assertEquals(
	                "http://23.253.72.207:8776",
	                results[4]);
	        assertEquals("http://23.253.72.207:9696/", results[5]);
		} catch (OCCIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }

    @Test
    public void testParseTenantsResponse() {
    	String result;
    	OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            result =
                parser.parseTenantsResponse(TENANTSRESPONSE, "ACME Corp");
            
            assertEquals("1234", result);
            result =
                    parser.parseTenantsResponse(TENANTSRESPONSE, "ACME Corpus");
            fail();
        } catch (OCCIException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testParseNetworkCollectionResponse() {
    	String[][] results;
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseCreatedNetworkResponse(NETWORKRESPONSE);
            
            assertEquals(2, results.length);
            assertEquals("d32019d3-bc6e-4319-9c1d-6722fc136a22", results[0]);
            assertEquals("private-network", results[1]);
            
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseStorageCollectionResponse(STORAGECOLLECTIONRESPONSE);
            
            assertEquals(2, results.getStorage().size());
            assertEquals("521752a6-acf6-4b2d-bc7a-119f9148cd8c", 
                    results.getStorage().get(0).getHref());
            assertEquals("76b8950a-8594-4e5b-8dce-0dfa9c696358", 
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseStorageResponse(STORAGERESPONSE);
            
            assertEquals("Another volume.",
            		results.getDescription());
            assertEquals("521752a6-acf6-4b2d-bc7a-119f9148cd8c",
            		results.getHref());
            assertEquals("vol-001", results.getName());
            assertEquals("30", results.getSize());
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseComputeCollectionResponse(COMPUTECOLLECTIONRESPONSE);
            
            assertEquals(2, results.getCompute().size());
            assertEquals("616fb98f-46ca-475e-917e-2563e5a8cd19", 
                    results.getCompute().get(0).getHref());
            assertEquals("new-server-test1", 
                    results.getCompute().get(0).getName());
            assertEquals("616fb98f-46ca-475e-917e-2563e5a8cd20", 
                    results.getCompute().get(1).getHref());
            assertEquals("new-server-test2", 
                    results.getCompute().get(1).getName());
            
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseComputeResponse(COMPUTERESPONSE);
            
            assertEquals("893c7791-f1df-4c3d-8383-3caae9656c62",
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
        try {
            results =
                parser.parseVirtualInterfacesResponse(VIRTUALINTERFACESRESPONSE);
            
            assertEquals(1, results.size());
            assertEquals("a26887c6-c47b-4654-abb5-dfadf7d3f803",
            	results.get(0).getNetwork().getHref());
            assertEquals("fa:16:3e:4c:2c:30", results.get(0).getMac());
            
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
        OpenStackJSONParser parser = new OpenStackJSONParser();
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
    	OpenStackJSONParser parser = new OpenStackJSONParser();
    	
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
    	OpenStackJSONParser parser = new OpenStackJSONParser();
    	
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
