package market;

import agents.Transmitter;

public class TransmissionLine {

	// Datos generales
	private int lineID;	// identificador
	private String lineCod; // código de la línea en la base de datos
	private String lineName; // nombre de la línea [nodo origen - nodo destino]
	private int position; // posición de la línea en la base de datos
	
	// Datos técnicos
	private Transmitter lineOwner; // propietario de la línea
	private Node sourceNode;	// nodo de origen
	private Node endNode; // nodo de destino
	private double susceptance;	// sisceptancia
	private double powerFlowLimit; // límite de flujo de potencia
	
	// Constructor vacío
	public TransmissionLine(){}
	
	// Constructor con identificador, propietario, nodo origen, nodo destino, 
	// susceptancia y límite de flujo de potencia
	public TransmissionLine(int lineID, Transmitter lineOwner, Node sourceNode, Node endNode, String lineName, 
			double powerFlowLimit, double susceptance, String lineCod, int position){
		this.lineID	= lineID;
		this.lineOwner = lineOwner;
		this.sourceNode	= sourceNode;
		this.endNode = endNode;
		this.lineName = lineName;
		this.powerFlowLimit = powerFlowLimit;
		this.susceptance = susceptance;
		this.lineCod = lineCod;
		this.position = position;
	}
	
	// Get methods
	public int getLineID(){return this.lineID; }	// obtener el identificador
	public Transmitter getLineOwner(){return this.lineOwner; }	// obtener el propietario de la línea
	public Node getSourceNode(){return this.sourceNode; }	// obtener el nodo de origen
	public Node getEndNode(){return this.endNode; } // obtener el nodo de destino
	public double getSusceptance(){return this.susceptance; } // obtener la suscaptancia
	public double getPowerFlowLimit(){return this.powerFlowLimit; } // obtener el límite de flujo de potencia
	public String getLineCod(){return this.lineCod; } // obtener el código de la línea 
	
	// Set methods
	public void setLineID(int lineID){this.lineID = lineID; }	// obtener el identificador
	public void setLineOwner(Transmitter lineOwner){this.lineOwner = lineOwner; }	// obtener el propietario de la línea
	public void setSourceNode(Node sourceNode){this.sourceNode = sourceNode; }	// obtener el nodo de origen
	public void setEndNode(Node endNode){this.endNode = endNode; } // obtener el nodo de destino
	public void setSusceptance(double susceptance){this.susceptance = susceptance; } // obtener la suscaptancia
	public void setPowerFlowLimit(double powerFlowLimit){this.powerFlowLimit = powerFlowLimit; } // obtener el límite de flujo de potencia
	
	// imprimir las características de cada línea de transmisión
	public void printLine(){
		System.out.println(this.lineID + "\t" + this.lineOwner.getTransmitterName() + "\t" + this.sourceNode.getNodeName()
				+ "\t" + this.endNode.getNodeName() + "\t" + this.lineName + "\t" + this.powerFlowLimit  + "\t" + this.susceptance
				+ "\t" + this.lineCod + "\t" + this.position);
	}
}