package agents;

import java.util.List;

import market.TransmissionLine;

public class Transmitter {

	// datos generales
	private int transID;	//  identificador del transmisor
	private String transmitterName; // nombre del transmisor
	private int position; 	// posición del transmisor en la base de datos

	// datos mercado de energía
	private List<TransmissionLine> transmissionLines; // lista de líneas de transmisión
	private double[] settlementUsageChargesCOP; // cargos por uso 
	private double proportionTransmissionSettlement; // proporción para la liquidación de los ingresos por cargos por uso
		
	// constructor vacío
	public Transmitter(){}
	
	// constructor  con nombre
	public Transmitter(String transmitterName){
		this.transmitterName = transmitterName;
	}
		
	// constructor  con nombre
		public Transmitter(int transID, String transmitterName, int position){
			this.transID = transID;
			this.transmitterName = transmitterName;
			this.position = position; 
		}
		
	// constructor  con nombre y lista de líneas de transmisión
	public Transmitter(int transID, String transmitterName, List<TransmissionLine> transmissionLines, double proportionTransmissionSettlement, 
			int position){
		this.transID = transID;
		this.transmitterName 	= transmitterName;
		this.transmissionLines 	= transmissionLines;
		this.proportionTransmissionSettlement = proportionTransmissionSettlement;
		this.position = position;
	}
	
	// get methods
	public String getTransmitterName(){return this.transmitterName; } // obtener el nombre del transmisor
	public List<TransmissionLine> getTransmissionLines(){ return this.transmissionLines; } // obtener la lista de líneas de transmisión
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;}; // obtener los cargos por uso 
	public double getProportionTransmissionSettlement(){return this.proportionTransmissionSettlement;} // obtener la proporción de la liquidación de cargos por uso
	
	// set methods
	public void setTransmitterName(String transmitterName){this.transmitterName = transmitterName; } // establecer el nombre del transmisor
	public void setTransmissionLines(List<TransmissionLine> transmissionLines){this.transmissionLines = transmissionLines; } // establecer la lista de líneas de transmisión
	public void setSettlementUsageChargesCOP(double[] settlementUsageCharges){this.settlementUsageChargesCOP = settlementUsageCharges;}; // establecer los cargos por uso
	public void setProportionTransmissionSettlement(double proportionTransmissionSettlement){this.proportionTransmissionSettlement = proportionTransmissionSettlement;} // establecer la proporción de la liquidación de cargos por uso
	
	// imprimir las características de cada transmisor
	public void printTransmitter(){
		System.out.println(this.transID + "\t" + this.transmitterName);
		for(int i = 0; i < this.transmissionLines.size(); i++)
			{
			System.out.println("\t" + this.transmissionLines.get(i).getSourceNode().getNodeName()
					+ "\t" + this.transmissionLines.get(i).getEndNode().getNodeName());
			}
	}
}
