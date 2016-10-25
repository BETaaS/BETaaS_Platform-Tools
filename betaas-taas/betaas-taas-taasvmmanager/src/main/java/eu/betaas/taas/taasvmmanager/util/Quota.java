package eu.betaas.taas.taasvmmanager.util;

public class Quota {
	public enum QuotaLocalization {LOCAL, REMOTE}; 
	
	private QuotaLocalization localization;
	private double vCpu;
	private double memory;
	private double disk;
	
	public Quota(QuotaLocalization localization, double vCpu, double memory, double disk) {
		this.localization = localization;
		this.vCpu = vCpu;
		this.memory = memory;
		this.disk = disk;
	}
	
	public QuotaLocalization getLocalization() {
		return localization;
	}

	public double getvCpu() {
		return vCpu;
	}

	public void setvCpu(double vCpu) {
		this.vCpu = vCpu;
	}

	public double getMemory() {
		return memory;
	}

	public void setMemory(double memory) {
		this.memory = memory;
	}

	public double getDisk() {
		return disk;
	}

	public void setDisk(double disk) {
		this.disk = disk;
	}

	public void setLocalization(QuotaLocalization localization) {
		this.localization = localization;
	}
	
	public Quota clone() {
		return new Quota(this.localization, this.vCpu, this.memory, this.disk);
	}
}
