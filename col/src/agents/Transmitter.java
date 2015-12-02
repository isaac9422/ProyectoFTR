package agents;

import java.util.List;

import market.TransmissionLine;

public class Transmitter {

	// datos generales
	private int transID;	//  identificador del transmisor
	private String transmitterName; // nombre del transmisor
	private int position; 	// posici�n del transmisor en la base de datos

	// datos mercado de energ�a
	private List<TransmissionLine> transmissionLines; // lista de l�neas de transmisi�n
	private double[] settlementUsageChargesCOP; // cargos por uso 
	private double proportionTransmissionSettlement; // proporci�n para la liquidaci�n de los ingresos por cargos por uso
		
	// constructor vac�o
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
		
	// constructor  con nombre y lista de l�neas de transmisi�n
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
	public List<TransmissionLine> getTransmissionLines(){ return this.transmissionLines; } // obtener la lista de l�neas de transmisi�n
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;}; // obtener los cargos por uso 
	public double getProportionTransmissionSettlement(){return this.proportionTransmissionSettlement;} // obtener la proporci�n de la liquidaci�n de cargos por uso
	
	// set methods
	public void setTransmitterName(String transmitterName){this.transmitterName = transmitterName; } // establecer el nombre del transmisor
	public void setTransmissionLines(List<TransmissionLine> transmissionLines){this.transmissionLines = transmissionLines; } // establecer la lista de l�neas de transmisi�n
	public void setSettlementUsageChargesCOP(double[] settlementUsageCharges){this.settlementUsageChargesCOP = settlementUsageCharges;}; // establecer los cargos por uso
	public void setProportionTransmissionSettlement(double proportionTransmissionSettlement){this.proportionTransmissionSettlement = proportionTransmissionSettlement;} // establecer la proporci�n de la liquidaci�n de cargos por uso
	
	// imprimir las caracter�sticas de cada transmisor
	public void printTransmitter(){
		System.out.println(this.transID + "\t" + this.transmitterName);
		for(int i = 0; i < this.transmissionLines.size(); i++)
			{
			System.out.println("\t" + this.transmissionLines.get(i).getSourceNode().getNodeName()
					+ "\t" + this.transmissionLines.get(i).getEndNode().getNodeName());
			}
	}
}
