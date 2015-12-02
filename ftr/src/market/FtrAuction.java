package market;

import java.util.List;

import agents.Generator;
import agents.Retailer;

public class FtrAuction {
	
	// variables
	private int ftrAuctionIndex; 			// índice de la subasta
	private int ftrAuctionDate; 			// fecha de la subasta
	private List<Generator> generators; 	// list of generators to participate in the auction
	private List<Retailer> retailers; 		// list of retailers to participate in the auction
	private double[] auctionCapacity;		// transmission capacity to sell in the auction by hour
	private Ftr productForAuction;			// product to auction
	private double[] ftrDemand; 			// demanda de capacidad en FTRs para todas las horas
	private double ftrHourlyDemand; 		// demanda horaria de capacidad en FTRs
	private List<Ftr> ftrHourlyAssigns; 	// lista de FTRs asignados cada hora
	private List<List<Ftr>> ftrAssigns; 	// lista de FTRs asignados en toda la subasta
	private double[] ftrRemainingCapacity; 	// arreglo con las capacidades remanentes en cada hora
	private double[] ftrAuctionIncome; 		// arreglo con el ingreso de la subasta para cada hora
	private List<Ftr> ftrOrganized; 	    // lista de FTRs asignados presentados de forma organizada
	
	// empty constructor
	public FtrAuction(){}
	
	// constructor
	public FtrAuction(double[] auctionCapacity, List<Generator> generators, List<Retailer> retailers){
		this.auctionCapacity 	= auctionCapacity;
		this.generators    		= generators;
		this.retailers			= retailers;
	}
	
	// get methods
	public int getFtrAuctionIndex(){return this.ftrAuctionIndex;} // obtener el índice d ela subasta
	public int getFtrAuctionDate(){return this.ftrAuctionDate;} // obtener la fecha de la subasta
	public double[] getAuctionCapacity(){return this.auctionCapacity;} // get the auction capacity
	public Ftr gerProductForAuction(){return this.productForAuction;} // get the product for auction
	public double[] getFtrDemand(){return this.ftrDemand;} // obtener la demanda de capacidad en FTRs para todas las horas
	public double getFtrHourlyDemand(){return this.ftrHourlyDemand;} // obtener la demanda horaria de capacidad en FTRs
	public List<Ftr> getFtrHourlyAssigns(){return this.ftrHourlyAssigns;} // obtener la lista de FTRs asignados cada hora
	public List<List<Ftr>> getFtrAssigns(){return this.ftrAssigns;} // obtener la lista de FTRs asignados en toda la subasta
	public double[] getFtrRemainingCapacity(){return this.ftrRemainingCapacity;} // obtener el arreglo con las capacidades remanentes en cada hora
	public double[] getFtrAuctionIncome(){return this.ftrAuctionIncome;} // obtener el arreglo con el ingreso de la subasta para cada hora
	public List<Ftr> getFtrOrganized(){return this.ftrOrganized;} // obtener la lista de FTRs asignados presentados de manera organizada
	
	// set methods
	public void setFtrAuctionIndex(int ftrAuctionIndex){this.ftrAuctionIndex = ftrAuctionIndex;} // establecer el índice d ela subasta
	public void setFtrAuctionDate(int ftrAuctionDate){this.ftrAuctionDate = ftrAuctionDate;} // establecer la fecha de la subasta
	public void setAuctionCapacity(double[] auctionCapacity){this.auctionCapacity = auctionCapacity;} // set he auction capacity
	public void serProductForAuction(Ftr productForAuction){this.productForAuction = productForAuction;} // set he auction capacity
	public void setFtrDemand(double[] ftrDemand){this.ftrDemand = ftrDemand;} // establecer la demanda de capacidad en FTRs para todas las horas
	public void setFtrHourlyDemand(double ftrHourlyDemand){this.ftrHourlyDemand = ftrHourlyDemand;} // establecer la demanda horaria de capacidad en FTRs
	public void setFtrHourlyAssigns(List<Ftr> ftrHourlyAssigns){this.ftrHourlyAssigns = ftrHourlyAssigns;} // establecer la lista de FTRs asignados cada hora
	public void setFtrAssigns(List<List<Ftr>> ftrAssigns){this.ftrAssigns = ftrAssigns;} // establecer la lista de FTRs asignados en toda la subasta
	public void setFtrRemainingCapacity(double[] ftrRemainingCapacity){this.ftrRemainingCapacity = ftrRemainingCapacity;} // establecer el arreglo con las capacidades remanentes en cada hora
	public void setFtrAuctionIncome(double[] ftrAuctionIncome){this.ftrAuctionIncome = ftrAuctionIncome;} // establecer el arreglo con el ingreso de la subasta para cada hora
	public void setFtrOrganized(List<Ftr> ftrOrganized){this.ftrOrganized = ftrOrganized;} // establecer la lista de FTRs asignados presentados de manera organizada
	
	// define the participants of FTR auction
	
	
	

}
