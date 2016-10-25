package eu.betaas.taas.taasvmmanager.libvirt;

public class LibVirtTemplates {

	private static String KERNEL_ENTRY  = "<kernel>%s</kernel>";
	private static String CMDLINE_ENTRY = "<cmdline>%s</cmdline>";
	private static String DTB_ENTRY     = "<dtb>%s</dtb> ";
	
	private static String TEMPLATE =
			"<domain type='kvm'>" +
				"<name>%s</name>" +
				"<uuid>%s</uuid>" +
				"<memory>%s</memory>" +
				"<vcpu>%s</vcpu>" +
				"<os>" +
					"<type arch='%s' machine='%s'>hvm</type>" +
					"<boot dev='hd'/>" +
					"%s" +
					"%s" +
					"%s" +
				"</os>" +
				"<clock offset='utc'/>" +
				"<on_poweroff>destroy</on_poweroff>" +
				"<on_reboot>restart</on_reboot>" +
				"<on_crash>destroy</on_crash>" +
				"<devices>" +
					"<emulator>/usr/bin/kvm</emulator>" +
					"<disk type='file' device='disk'>" +
						"<source file='%s'/>" +
						"<driver name='qemu' type='raw'/>" +
						"<target dev='hda' bus='ide'/>" +
						"<alias name='ide0-0-0'/>" +
						"<address type='drive' controller='0' bus='0' unit='0'/>" +
					"</disk>" +
					"<controller type='ide' index='0'>" +
						"<alias name='ide0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x1'/>" +
					"</controller>" +
					"<interface type='network'>" +
						"<mac address='52:54:00:6a:84:e9'/>" +
						"<source network='default'/>" +
						"<target dev='vnet0'/>" +
						"<alias name='net0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x03' function='0x0'/>" +
					"</interface>" +
					"<input type='mouse' bus='ps2'/>" +
					"<graphics type='vnc' port='5900' autoport='yes'/>" +
					"<video>" +
						"<model type='cirrus' vram='9216' heads='1'/>" +
						"<alias name='video0'/>" +
						"<address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>" +
					"</video>" +
				"</devices>" +
			"</domain>";
	
	public static String getTemplate(String vmName,   String vmUuid,
	                                   String vmMemory, String vmCpu,
	                                   String arch,     String machine,
	                                   String kernel,   String cmdline,
	                                   String dtb,      String vmImage) {
		String kernelEntry =
				(kernel == null || kernel.equals(""))?
						"" : String.format(KERNEL_ENTRY, kernel);
		String cmdlineEntry =
				(cmdline == null || cmdline.equals(""))?
						"" : String.format(CMDLINE_ENTRY, kernel);
		String dtbEntry = (dtb == null || dtb.equals(""))?
				"" : String.format(DTB_ENTRY, kernel);
		
		return String.format(
				TEMPLATE, vmName, vmUuid, vmMemory, vmCpu, arch, machine,
				kernelEntry, cmdlineEntry, dtbEntry, vmImage);
	}
}
