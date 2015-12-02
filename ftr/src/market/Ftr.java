package market;

import utilities.Global;
import agents.Generator;
import agents.Retailer;

public class Ftr {
	
	// variables
	private int ftrID;			// identificador del FTR
	private Generator bidderG; 	// generador
	private Retailer bidderR; 	// comercializador
	public String bidder;		// ofertante
	public GenerationUnit generationUnit;				// unidad de generación en la oferta del generador
	public GenerationContract generationContract;		// contrato de generación en la oferta del generador
	public double[] ftrPower; 	// energía en el FTR
	public double[] ftrPrice;  	// precio en el FTR
	private double hourlyFtrPower; 	// energía en una hora en el FTR
	private double hourlyFtrPrice;  // precio en una hora en el FTR
	public Node ftrSourceNode;	// nodo de origen del FTR
	public Node ftrEndNode;		// nodo de destino del FTR
	private int	ftrInitialDate;	// fecha inicial del FTR
	private int ftrDuration;	// duración del FTR
	private int ftrFinalDate;	// fecha final del FTR
	
	// empty constructor
	public Ftr(){}
	
	// constructor: nombre ofertante [usado para verificar si se asignó o no FTRs en una hora determinada]
	public Ftr(String bidder){
		this.bidder = bidder;
	}
	
	// constructor 
	public Ftr(double ftrPower[], double[] ftrPrice, Node ftrSourceNode, Node ftrEndNode, int	ftrInitialDate, int ftrDuration){
		this.ftrPower = ftrPower;
		this.ftrPrice = ftrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
		this.ftrInitialDate = ftrInitialDate;
		this.ftrDuration = ftrDuration;
		this.ftrFinalDate = ftrInitialDate + ftrDuration;
	}
	
	// constructor: structure for the bidding process 
	public Ftr(Generator bidderG, double ftrPower[], double[] ftrPrice, Node ftrSourceNode, Node ftrEndNode){
		this.bidderG = bidderG;
		this.ftrPower = ftrPower;
		this.ftrPrice = ftrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	
	// constructor: structure for the bidding process 
	public Ftr(Retailer bidderR, double ftrPower[], double[] ftrPrice, Node ftrSourceNode, Node ftrEndNode){
		this.bidderR = bidderR;
		this.ftrPower = ftrPower;
		this.ftrPrice = ftrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
		}
	
	// constructor: structure for the bidding process 
	public Ftr(String bidder, double ftrPower[], double[] ftrPrice, Node ftrSourceNode, Node ftrEndNode){
		this.bidder = bidder;
		this.ftrPower = ftrPower;
		this.ftrPrice = ftrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	/*
	// constructor: structure for the bidding process 
	public Ftr(String bidder, double hourlyFtrPower, double hourlyFtrPrice, int ftrSourceNode, int ftrEndNode){
		this.bidder = bidder;
		this.hourlyFtrPower = hourlyFtrPower;
		this.hourlyFtrPrice = hourlyFtrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	*/
	// constructor: structure for the bidding process 
	public Ftr(String bidder, double hourlyFtrPower, double hourlyFtrPrice, Node ftrSourceNode, Node ftrEndNode, int ftrInitialDate, int ftrDuration){
		this.bidder = bidder;
		this.hourlyFtrPower = hourlyFtrPower;
		this.hourlyFtrPrice = hourlyFtrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
		this.ftrInitialDate = ftrInitialDate;
		this.ftrDuration = ftrDuration;
		this.ftrFinalDate = ftrInitialDate + ftrDuration;
	}
	
	// constructor: for the operator to realize the FTR auction
	public Ftr(String bidder, double hourlyFtrPower, Node ftrSourceNode, Node ftrEndNode, int	ftrInitialDate, int ftrDuration){
		this.bidder = bidder;
		this.hourlyFtrPower = hourlyFtrPower;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
		this.ftrInitialDate = ftrInitialDate;
		this.ftrDuration = ftrDuration;
		this.ftrFinalDate = ftrInitialDate + ftrDuration;
	}
	
	// constructor: structure for the bidding process: generators and retailers (original) 
	public Ftr(String bidder, double hourlyFtrPower, Node ftrSourceNode, Node ftrEndNode){
		this.bidder = bidder;
		this.hourlyFtrPower = hourlyFtrPower;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	
	// constructor: structure for the bidding process: generators  and retailers (modified)
	public Ftr(String bidder, GenerationContract generationContract, double hourlyFtrPower, Node ftrSourceNode, Node ftrEndNode){
		this.bidder = bidder;
		this.generationContract = generationContract;
		this.hourlyFtrPower = hourlyFtrPower;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	
	// constructor: structure for the bidding process: generators  and retailers (modified)
	public Ftr(String bidder, GenerationContract generationContract, double hourlyFtrPower, 
			double hourlyFtrPrice, Node ftrSourceNode, Node ftrEndNode){
		this.bidder = bidder;
		this.generationContract = generationContract;
		this.hourlyFtrPower = hourlyFtrPower;
		this.hourlyFtrPrice = hourlyFtrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	
	// constructor: structure for the bidding process: generators  and retailers (modified)
	public Ftr(Retailer bidderR, GenerationContract generationContract, double hourlyFtrPower, 
			double hourlyFtrPrice, Node ftrSourceNode, Node ftrEndNode){
		this.bidderR = bidderR;
		this.generationContract = generationContract;
		this.hourlyFtrPower = hourlyFtrPower;
		this.hourlyFtrPrice = hourlyFtrPrice;
		this.ftrSourceNode = ftrSourceNode;
		this.ftrEndNode = ftrEndNode;
	}
	
	// constructor: structure for the bidding process: generators  and retailers (modified)
		public Ftr(Retailer bidderR, GenerationContract generationContract, double[] ftrPower, 
				double[] ftrPrice, Node ftrSourceNode, Node ftrEndNode){
			this.bidderR = bidderR;
			this.generationContract = generationContract;
			this.ftrPower = ftrPower;
			this.ftrPrice = ftrPrice;
			this.ftrSourceNode = ftrSourceNode;
			this.ftrEndNode = ftrEndNode;
		}
	
	// constructor: structure to define the product for auction
	public Ftr(String bidder, double hourlyFtrPower, double hourlyFtrPrice, int	ftrInitialDate, int ftrDuration){
		this.bidder = bidder;
		this.hourlyFtrPower = hourlyFtrPower;
		this.hourlyFtrPrice = hourlyFtrPrice;
		this.ftrInitialDate = ftrInitialDate;
		this.ftrDuration = ftrDuration;
		this.ftrFinalDate = ftrInitialDate + ftrDuration;
	}
			
	// set methods
	public void setFtrID(int ftrID){ this.ftrID = ftrID; } // set ftrID
	public void setBidderG(Generator bidderG){ this.bidderG = bidderG; } // set bidderG
	public void setBidderR(Retailer bidderR){ this.bidderR = bidderR; } // set bidderR
	public void setBidder(String bidder){ this.bidder = bidder; } // set bidder
	public void setGenerationUnit(GenerationUnit generationUnit){ this.generationUnit = generationUnit;} // set generation unit
	public void setFtrPower(double ftrPower, int h){ this.ftrPower[h] = ftrPower; } // set FTR power
	public void setFtrPrice(double ftrPrice, int h){ this.ftrPrice[h] = ftrPrice; } // set FTR power
	public void setFtrPower(double[] ftrPower){ this.ftrPower = ftrPower; } // set FTR power
	public void setFtrPrice(double[] ftrPrice){ this.ftrPrice = ftrPrice; } // set FTR power
	public void setHourlyFtrPower(double hourlyFtrPower){this.hourlyFtrPower = hourlyFtrPower; } // set hourly FTR power
	public void setHourlyFtrPrice(double hourlyFtrPrice){this.hourlyFtrPrice = hourlyFtrPrice; }  // set hourly price of the FTR
	public void setFtrSourceNode(Node ftrSourceNode){ this.ftrSourceNode = ftrSourceNode; } // set FTR source node
	public void setFtrEndNode(Node ftrEndNode){ this.ftrEndNode = ftrEndNode; } // set FTR end node
	public void setFtrInitialDate(int ftrInitialDate){ this.ftrInitialDate = ftrInitialDate; } // set FTR initial date
	public void setFtrDuration(int ftrDuration){ this.ftrDuration = ftrDuration; } // set FTR duration
	public void setFtrFinalDate(int	ftrInitialDate, int ftrDuration){ this.ftrFinalDate = ftrInitialDate + ftrDuration; } // set FTR final date
	public void setGenerationContract(GenerationContract generationContract){ this.generationContract = generationContract;} // set generation contract
	
	// get methods
	public int getFtrID(){ return this.ftrID; } // get ftrID
	public Generator getBidderG(){ return this.bidderG; } // get bidderG
	public Retailer getBidderR(){ return this.bidderR; } // get bidderR
	public String getBidder(){ return this.bidder; } // get bidder
	public GenerationUnit getGenerationUnit(){ return this.generationUnit;} // get generation unit
	public GenerationContract getGenerationContract(){ return this.generationContract;} // get generation contract
	public double[] getFtrPower(){ return this.ftrPower; } // get FTR power
	public double[] getFtrPrice(){return this.ftrPrice; }  // get price of the FTR
	public double getHourlyFtrPower(){return this.hourlyFtrPower; } // get hourly FTR power
	public double getHourlyFtrPrice(){return this.hourlyFtrPrice; } // get hourly price of the FTR
	public Node getFtrSourceNode(){ return this.ftrSourceNode; } // get FTR source node
	public Node getFtrEndNode(){ return this.ftrEndNode; } // get FTR end node
	public int getFtrInitialDate(){ return this.ftrInitialDate; } // get FTR initial date
	public int getFtrDuration(){ return this.ftrDuration; } // get FTR duration
	public int getFtrFinalDate(){ return this.ftrFinalDate;} // get FTR final date
		
	// print FTR
	public void printFtr(){
		System.out.print(this.getBidder()+ "\t");
		for(int hour = 0; hour < 24; hour++){
			System.out.print(Global.decimalFormatter.format(this.ftrPower[hour])+ "\t");
		}
		for(int hour = 0; hour < 24; hour++){
			System.out.print(Global.decimalFormatter.format(this.ftrPrice[hour])+ "\t");
		}
		System.out.println(this.getFtrSourceNode().getNodeName() + "\t" + this.getFtrEndNode().getNodeName());
	}
	
	// print FTR
	public void printHourlyFtr(){
		System.out.println("\t\t" + this.getBidder()+ "\t" + Global.decimalFormatter.format(this.getHourlyFtrPower()) + "\t"
						 + Global.decimalFormatter.format(this.getHourlyFtrPrice()) + "\t" + this.getFtrSourceNode().getNodeName() 
						 + "\t" + this.getFtrEndNode().getNodeName() + "\t" + this.getFtrInitialDate()
						 + "\t" + this.getFtrDuration());
	}
	
	// print FTR
	public void printHourlyFtrContract(){
		System.out.println("\t\t" + this.getBidderR().getRetailerName() + "\t" + this.getGenerationContract().getContractId() 
						 + "\t" + Global.decimalFormatter.format(this.getHourlyFtrPower()) + "\t"
						 + Global.decimalFormatter.format(this.getHourlyFtrPrice()) 
						 + "\t" + this.getFtrSourceNode().getNodeName() 
						 + "\t" + this.getFtrEndNode().getNodeName() + "\t" + this.getFtrInitialDate()
						 + "\t" + this.getFtrDuration());
	}
	
	// print FTR
	public void printHourlyFtr1(Ftr ftr){
		System.out.println(ftr.getBidder()+ "\t" + ftr.getHourlyFtrPower() + "\t" + "\t" + ftr.getFtrSourceNode() + "\t" + ftr.getFtrEndNode());
	}
	
	// print assigned hourly FTR
	public void printAssignedHourlyFtr(){
		System.out.println(this.getBidderR().getRetailerName() + "\t" + this.generationContract.getContractId() + "\t" 
							+ Global.decimalFormatter.format(this.getHourlyFtrPower()) + "\t" 
							+ Global.decimalFormatter.format(this.getHourlyFtrPrice()) + "\t" + this.getFtrSourceNode().getNodeName() + "\t" 
							+ this.getFtrEndNode().getNodeName() + "\t" + this.getFtrInitialDate() + "\t" + this.getFtrDuration());
	}
	
	// print FTR
	public void printFtrProduct(Ftr ftr){
		System.out.println(ftr.getBidder()+ "\t" + Global.decimalFormatter.format(ftr.getHourlyFtrPower()) + "\t"
							 + Global.decimalFormatter.format(ftr.getHourlyFtrPrice()) + "\t" + ftr.getFtrInitialDate() + "\t" + ftr.getFtrDuration());
	}
}