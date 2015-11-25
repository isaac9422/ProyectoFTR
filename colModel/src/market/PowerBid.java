package market;

import agents.Generator;

public class PowerBid implements Comparable<PowerBid>{
	
	// Datos
	private String generatorName; // nombre del generador
	private double bidPrice; // precio ofertado
	private double[] bidPower = new double[24]; // potencia ofertada para las 24 horas del día
	
	// mercado de energía
	private GenerationUnit generationUnit;
	private double hourlyBidPower; // generación ofertada para una hora
	
	// Constructor vacío
	public PowerBid(){}
	
	// Constructor con bidPrice y bidPower
	public PowerBid(double bidPrice, double[] bidPower){
		this.bidPrice = bidPrice;
		this.bidPower = bidPower;
	}
	
	// Constructor con generatorName, bidPrice y bidPower
	public PowerBid(String generatorName, double bidPrice, double[] bidPower){
		this.generatorName = generatorName; 
		this.bidPrice = bidPrice;
		this.bidPower = bidPower;
	}
	
	// Constructor con generatorName, bidPrice y hourlyBidPower
	public PowerBid(GenerationUnit generationUnit, double bidPrice, double hourlyBidPower){
		this.generationUnit = generationUnit; 
		this.bidPrice = bidPrice;
		this.hourlyBidPower = hourlyBidPower;
	}

	// Get methods
	public String getGeneratorName(){return this.generatorName;} // pbtener el nombre del generador
	public double getBidPrice(){return this.bidPrice;} // obtener el precio de oferta	
	public double getBidPowerOne(int hour){ return this.bidPower[hour];} // obtener la potencia ofertada para una hora determinada
	public double[] getBidPower(){ return this.bidPower;} // obtener la potencia ofertada para cada hora
	public double getHourlyBidPower(){ return this.hourlyBidPower;} // obtener la potencia ofertada para una hora determinada
	public GenerationUnit getGenerationUnit(){return this.generationUnit;} // obtener la unidad de generación que realiza la oferta
	
	// Set methods
	public void setGeneratorName(String generatorName){this.generatorName = generatorName;} // establecer el nombre del generador
	public void setBidPrice(double bidPrice){this.bidPrice = bidPrice;} // establecer el precio de oferta		
	public void setBidPower(int hour, double bidPower){ this.bidPower[hour] = bidPower;} // establecer el la potencia ofertada en una hora determinada
	public void setBidPower(double[] bidPower){this.bidPower = bidPower;} // establecer la potencia ofertada para las 24 horas
	public void setHourlyBidPower(double hourlyBidPower){this.hourlyBidPower = hourlyBidPower;} // establecer la potencia ofertada para una hora determinada
	public void getGenerationUnit(GenerationUnit generationUnit){this.generationUnit = generationUnit;} // establoecer la unidad de generación que realiza la oferta
	
	// Imprimir una oferta
	public void printPowerBid(){
		System.out.print(this.generatorName +"\t"+this.bidPrice+"\t");
		for(int i = 0; i < 24; i++){
			System.out.print(this.bidPower[i]+"\t");
		}
		System.out.print("\n");
	}

	@Override
	public int compareTo(PowerBid powerBid) {
		if (this.bidPrice > powerBid.bidPrice) {   
			return 1;   
		} else if (this.bidPrice < powerBid.bidPrice) {
			return -1;   
		} else {   
		    return 0;   
		}
	}
}
