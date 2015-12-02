package market;

public class Dispatch {

	private double[][] generation; 			// programación de la generación
	private double[][] voltageAngles; 		// ángulos de voltaje
	private double[][] energyDemand; 		// demanda de energía
	private double[][] energyBid; 			// oferta de energía
	private double[][] energyBidPrice; 		// precio de oferta de energía [ se puede corregir dado que es un precio por día]
	private double[][] flows; 				// flujos de energía
	private double[][] remainderCapacity; 	// capacidad de transmisión remanente
	private double[][] unservedDemand; 		// demanda no atendida
	private double[][] servedDemand; 		// demanda efectivamente atendida
	private double dispatchCost; 			// costo del despacho
	
	// despacho ideal
	private double[] spotPrices; // precio spot para cada hora
	
	// despacho real
	private double[][] nodalPrices; // precios nodales
	
	// Constructor vacío
	public Dispatch(){}
	
	// Constructor deapcaho real
	public Dispatch(double[][] generation,double[][] voltageAngles, double[][] powerDemand, double[][] nodalPrices, 
			double[][] flows,  double[][] remainderCapacity, double[][] unservedDemand, double dispatchCost ){
		this.generation			= generation;
		this.voltageAngles  	= voltageAngles; 
		this.energyDemand		= powerDemand;
		this.nodalPrices		= nodalPrices;
		this.flows				= flows;
		this.remainderCapacity 	= remainderCapacity;
		this.unservedDemand		= unservedDemand;
		this.dispatchCost		= dispatchCost;
	}
	
	// Constructor deapcaho ideal
	public Dispatch(double[][] generation,double[][] voltageAngles, double[][] powerDemand, double[] spotPrices, double[][] nodalPrices, 
			double[][] flows,  double[][] remainderCapacity, double[][] unservedDemand, double dispatchCost ){
		this.generation			= generation;
		this.voltageAngles  	= voltageAngles; 
		this.energyDemand		= powerDemand;
		this.spotPrices			= spotPrices;
		this.nodalPrices		= nodalPrices;
		this.flows				= flows;
		this.remainderCapacity 	= remainderCapacity;
		this.unservedDemand		= unservedDemand;
		this.dispatchCost		= dispatchCost;
	}
	
	// get methods
	public double[][] getGeneration(){return this.generation; } // obtener la programación de generación
	public double[][] getVoltageAngles(){return this.voltageAngles;} // obtener los ángulos de voltaje 
	public double[][] getEnergyDemand(){return this.energyDemand; } // obtener la demanda de energía
	public double[][] getEnergyBid(){return this.energyBid; } // obtener la oferta de energía
	public double[][] getEnergyBidPrice(){return this.energyBidPrice; } // obtener el precio de la oferta de energía
	public double[] getSpotPrices(){return this.spotPrices;} // obtener los precios spot horarios
	public double[][] getNodalPrices(){return this.nodalPrices; } // obtener los precios nodales
	public double[][] getFlows(){return this.flows;} // obtener los flujos de energía
	public double[][] getRemainderCapacity(){return this.remainderCapacity;} // obtener la capacidad de transmisión remanente
	public double[][] getServedDemand(){return this.servedDemand;} // obtener la demanda efectívamente atendida
	public double[][] getUnservedDemand(){return this.unservedDemand;} // obtener la demanda no atendida
	public double getDispatchCost(){return this.dispatchCost;} // obtener el costo del despacho
	
	// set methods
	public void setGeneration(double[][] generation){this.generation = generation; } // establecer la programación de generación
	public void setVoltajeAngles(double[][] voltageAngles){this.voltageAngles = voltageAngles;} // establecer los ángulos de voltaje
	public void setEnergyDemand(double[][] energyDemand){this.energyDemand = energyDemand; } // establecer la demanda de potencia
	public void setEnergyBid(double[][] energyBid){this.energyBid = energyBid; } // establecer la oferta de energía
	public void setEnergyBidPrice(double[][] energyBidPrice){this.energyBidPrice = energyBidPrice; } // establecer el precio de la oferta de energía
	public void setSpotPrices(double[] spotPrices){this.spotPrices = spotPrices;} // establecer los precios spot horarios
	public void setNodalPrices(double[][] nodalPrices){this.nodalPrices = nodalPrices; } // establecer los precios nodales
	public void setFlows(double[][] flows){this.flows = flows;}	// establecer los flujos de potencia
	public void setRemainderCapacity(double[][] remainderCapacity){this.remainderCapacity = remainderCapacity;}	// establecer la capacidad remanate
	public void setServedDemand(double[][] servedDemand){this.servedDemand = servedDemand;}	// establecer la demanda efectívamente atendida
	public void setUnservedDemand(double[][] unservedDemand){this.unservedDemand = unservedDemand;}	// establecer la demanda no atendida
	public void setDispatchCost(double dispatchCost){this.dispatchCost = dispatchCost;}	// establecer el costo del despacho

}
