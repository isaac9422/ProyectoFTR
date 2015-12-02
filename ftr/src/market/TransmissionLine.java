package market;

import agents.Transmitter;

public class TransmissionLine {

	// Datos generales
	private int lineID;	// identificador
	private String lineCod; // c�digo de la l�nea en la base de datos
	private String lineName; // nombre de la l�nea [nodo origen - nodo destino]
	private int position; // posici�n de la l�nea en la base de datos
	
	// Datos t�cnicos
	private Transmitter lineOwner; // propietario de la l�nea
	private Node sourceNode;	// nodo de origen
	private Node endNode; // nodo de destino
	private double susceptance;	// sisceptancia
	private double powerFlowLimit; // l�mite de flujo de potencia
	
	// Constructor vac�o
	public TransmissionLine(){}
	
	// Constructor con identificador, propietario, nodo origen, nodo destino, 
	// susceptancia y l�mite de flujo de potencia
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
	public Transmitter getLineOwner(){return this.lineOwner; }	// obtener el propietario de la l�nea
	public Node getSourceNode(){return this.sourceNode; }	// obtener el nodo de origen
	public Node getEndNode(){return this.endNode; } // obtener el nodo de destino
	public double getSusceptance(){return this.susceptance; } // obtener la suscaptancia
	public double getPowerFlowLimit(){return this.powerFlowLimit; } // obtener el l�mite de flujo de potencia
	public String getLineCod(){return this.lineCod; } // obtener el c�digo de la l�nea 
	
	// Set methods
	public void setLineID(int lineID){this.lineID = lineID; }	// obtener el identificador
	public void setLineOwner(Transmitter lineOwner){this.lineOwner = lineOwner; }	// obtener el propietario de la l�nea
	public void setSourceNode(Node sourceNode){this.sourceNode = sourceNode; }	// obtener el nodo de origen
	public void setEndNode(Node endNode){this.endNode = endNode; } // obtener el nodo de destino
	public void setSusceptance(double susceptance){this.susceptance = susceptance; } // obtener la suscaptancia
	public void setPowerFlowLimit(double powerFlowLimit){this.powerFlowLimit = powerFlowLimit; } // obtener el l�mite de flujo de potencia
	
	// imprimir las caracter�sticas de cada l�nea de transmisi�n
	public void printLine(){
		System.out.println(this.lineID + "\t" + this.lineOwner.getTransmitterName() + "\t" + this.sourceNode.getNodeName()
				+ "\t" + this.endNode.getNodeName() + "\t" + this.lineName + "\t" + this.powerFlowLimit  + "\t" + this.susceptance
				+ "\t" + this.lineCod + "\t" + this.position);
	}
}