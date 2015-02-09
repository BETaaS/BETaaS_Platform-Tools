package eu.betaas.taas.securitymanager.requirements.helper;

public class Ids implements Comparable<Ids>{

	private int id;
	private double score;
	
	public Ids(){}
	
	public Ids(double score, int id){
		this.id = id;
		this.score = score;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public int compareTo(Ids scoreIds) {
		double compareScore = ((Ids) scoreIds).getScore();
		
		return (int) (compareScore*1000 - this.score*1000);
	}
}
