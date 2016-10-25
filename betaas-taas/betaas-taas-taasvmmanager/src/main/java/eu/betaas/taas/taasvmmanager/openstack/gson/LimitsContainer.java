package eu.betaas.taas.taasvmmanager.openstack.gson;

public class LimitsContainer {
	public Limits limits;
	
	public LimitsContainer () {}
	
	public Limits getLimits() {
		return limits;
	}

	public void setLimits(Limits limits) {
		this.limits = limits;
	}

	public class Limits {
		private String[] rate;
		private Absolute absolute;
		
		public Limits() {}
		
		public String[] getRate() {
			return rate;
		}

		public void setRate(String[] rate) {
			this.rate = rate;
		}

		public Absolute getAbsolute() {
			return absolute;
		}

		public void setAbsolute(Absolute absolute) {
			this.absolute = absolute;
		}

		public class Absolute {
			private int totalSnapshotsUsed;
			private int maxTotalVolumeGigabytes;
			private int totalGigabytesUsed;
			private int maxTotalSnapshots;
			private int totalVolumesUsed;
			private int maxTotalVolumes;
			
			public Absolute () {}

			public int getTotalSnapshotsUsed() {
				return totalSnapshotsUsed;
			}

			public void setTotalSnapshotsUsed(int totalSnapshotsUsed) {
				this.totalSnapshotsUsed = totalSnapshotsUsed;
			}

			public int getMaxTotalVolumeGigabytes() {
				return maxTotalVolumeGigabytes;
			}

			public void setMaxTotalVolumeGigabytes(int maxTotalVolumeGigabytes) {
				this.maxTotalVolumeGigabytes = maxTotalVolumeGigabytes;
			}

			public int getTotalGigabytesUsed() {
				return totalGigabytesUsed;
			}

			public void setTotalGigabytesUsed(int totalGigabytesUsed) {
				this.totalGigabytesUsed = totalGigabytesUsed;
			}

			public int getMaxTotalSnapshots() {
				return maxTotalSnapshots;
			}

			public void setMaxTotalSnapshots(int maxTotalSnapshots) {
				this.maxTotalSnapshots = maxTotalSnapshots;
			}

			public int getTotalVolumesUsed() {
				return totalVolumesUsed;
			}

			public void setTotalVolumesUsed(int totalVolumesUsed) {
				this.totalVolumesUsed = totalVolumesUsed;
			}

			public int getMaxTotalVolumes() {
				return maxTotalVolumes;
			}

			public void setMaxTotalVolumes(int maxTotalVolumes) {
				this.maxTotalVolumes = maxTotalVolumes;
			}
		}
	}
}
