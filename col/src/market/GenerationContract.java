package market;

import agents.Generator;
import agents.Retailer;

public class GenerationContract{

	//Datos generales
	private int contractId;	// identificador del contrato
	private Generator contractSeller; // vendedor en el contrato
	private int contractSellerId; // identificador del vendedor en el contrato
	private GenerationUnit generationUnit; // unidad de generación en el contrato
	private int generationUnitId; // identificador de la unidad de generación en el contrato
	private Retailer contractBuyer;	// comprador en el contrato
	private int contractBuyerId; // identificador del comprador del contrato
	private double[] contractPower	= new double[24]; // potencia en el contrato
	private double[] contractPrice 	= new double[24]; // precio del contrato
	private Node sourceNode;	// nodo de origen de la potencia contratada
	private int sourceNodeId; // identificador del nodo de origen de la potencia contratada
	private Node withdrawalNode; // nodo de destino de la potencia contratada
	private int withdrawalNodeId; // identificador del nodo de destino de la potencia contratada
	private int contractStartDate;	// fecha de inicio del contrato
	private int contractFinalDate; // duración del contrato
	
	
	// variable energy market
	private double hourlyContractPrice; 
	private double hourlyContractPower;
	private int contractHour;
	private String contractType; 
	
	// contratos pague lo demandado
	private double[] dispatchedContractEnergyPD; // potencia en el contrato PD que fue despachada
	
	// Constructor vacío
	public GenerationContract(){}
	
	// Constructor con identificador, vendedor, unidad de generación, comprador, potencia, precio, nodo origen,
	// nodo destino, fecha de inicio, duración
	public GenerationContract(int contractId, int contractSellerId, Generator contractSeller, int generationUnitId, GenerationUnit generationUnit, 
			int contractBuyerId, Retailer contractBuyer, double[] contractPower, double[] contractPrice, int sourceNodeId, Node sourceNode, 
			int withdrawalNodeId, Node withdrawalNode,	int contractStartDate, int contractFinalDate){
		this.contractId 		= contractId;
		this.contractSellerId 	= contractSellerId;
		this.contractSeller		= contractSeller;
		this.generationUnitId 	= generationUnitId;
		this.generationUnit		= generationUnit;
		this.contractBuyerId 	= contractBuyerId;
		this.contractBuyer		= contractBuyer;
		this.contractPower		= contractPower;
		this.contractPrice 		= contractPrice;
		this.sourceNodeId 		= sourceNodeId;
		this.sourceNode			= sourceNode;
		this.withdrawalNodeId 	= withdrawalNodeId;
		this.withdrawalNode 	= withdrawalNode;
		this.contractStartDate 	= contractStartDate;
		this.contractFinalDate 	= contractFinalDate;
	}
	
	//
	// Constructor para la letura de la información de los contratos desde un archivo .csv
	//
	public GenerationContract(int contractId, int contractSellerId, Generator contractSeller, GenerationUnit generationUnit, 
			Retailer contractBuyer, double[] contractPower, double[] contractPrice, Node sourceNode, 
			Node withdrawalNode,	int contractStartDate, int contractFinalDate){
		this.contractId 		= contractId;
		this.contractSeller		= contractSeller;
		this.generationUnit		= generationUnit;
		this.contractBuyer		= contractBuyer;
		this.contractPower		= contractPower;
		this.contractPrice 		= contractPrice;
		this.sourceNode			= sourceNode;
		this.withdrawalNode 	= withdrawalNode;
		this.contractStartDate 	= contractStartDate;
		this.contractFinalDate 	= contractFinalDate;
	}
	
	// Constructor horario con identificador, vendedor, unidad de generación, comprador, potencia, precio, nodo origen,
	// nodo destino, fecha de inicio, duración
	public GenerationContract(GenerationUnit generationUnit, Retailer contractBuyer, double hourlyContractPower, double hourlyContractPrice, int contractHour){
		this.generationUnit		= generationUnit;
		this.contractBuyer		= contractBuyer;
		this.hourlyContractPower= hourlyContractPower;
		this.hourlyContractPrice= hourlyContractPrice;
		this.contractHour 		= contractHour;
	}
	
	// Get methods
	public int getContractId(){return this.contractId; } // obtener el identificador
	public Generator getContractSeller(){return this.contractSeller;} // obtener el vendedor del contrato
	public GenerationUnit getGenerationUnit(){return this.generationUnit;} //obtener la unidad de generación
	public Retailer getContractBuyer(){return this.contractBuyer;}	// obtener el comprador
	public double[] getContractPower(){return this.contractPower;}// obtener  la potencia contratada
	public double[] getContractPrice(){return this.contractPrice;} // obtener el precio del contrato para cada hora 
	public Node getSourceNode(){return this.sourceNode;}	// obtener el nodo de origen
	public Node getWithdrawalNode(){return this.withdrawalNode;}	// obtener el nodo de destino
	public int getContractStartDate(){return this.contractStartDate;} // obtener la fecha de inicio del contrato
	public int getContractFinalDate(){return this.contractFinalDate;} // obtener la duración del contrato
	public double getHourlyContractPower(){ return this.hourlyContractPower;} // obtener la potencia contratada en un contrato horario
	public double getHourlyContractPrice(){ return this.hourlyContractPrice;} // obtener el precio en un contrato horario
	public int getContractHour(){return this.contractHour;} // obtener la hora del contrato horario
	public String getContractType(){return this.contractType;} // obtener el tipo del contrato
	public int getContractSellerId(){return this.contractSellerId;} // obtener el identificador del vendedor del contrato
	public int getGenerationUnitId(){return this.generationUnitId;} // obtener el identificador de la unidad de generación del contrato
	public int getContractBuyerId(){return this.contractBuyerId;} // obtener el identificador del comprador del contrato
	public int getSourceNodeId(){return this.sourceNodeId;} // obtener el identificador del nodo de oferta
	public int getWithdrawalNodeId(){return this.withdrawalNodeId;} // obtener el identificador del nodo de demanda
	
	public double[] getDispatchedContractPowerPD(){return this.dispatchedContractEnergyPD;}// obtener la potencia contratada en contratos PD que fue despachada
	public double getDispatchedContractPowerPD(int h){return this.dispatchedContractEnergyPD[h];}// obtener la potencia contratada en contratos PD que fue despachada en la hora h
	
	// Set methods
	public void setContractId(int contractId){this.contractId = contractId; } // establecer el identificador
	public void setContractSeller(Generator contractSeller){this.contractSeller = contractSeller;} // establecer el vendedor del contrato
	public void setGenerationUnit(GenerationUnit generationUnit){this.generationUnit = generationUnit;} //establecer la unidad de generación
	public void setContractBuyer(Retailer contractBuyer){this.contractBuyer = contractBuyer;} // establecer el comprador
	public void setContractPower(double[] contractPower){this.contractPower = contractPower;} // establecer  la potencia contratada
	public void setContractPrice(double[] contractPrice){this.contractPrice = contractPrice;} // establecer el precio del contrato para cada hora 
	public void setSourceNode(Node sourceNode){this.sourceNode = sourceNode;} // establecer el nodo de origen
	public void setWithdrawalNode(Node withdrawalNode){this.withdrawalNode = withdrawalNode;} // establecer el nodo de destino
	public void setContractStartDate(int contractStartDate){this.contractStartDate = contractStartDate;} // establecer la fecha de inicio del contrato
	public void setContractFinalDate(int contractFinalDate){this.contractFinalDate = contractFinalDate;} // establecer la duración del contrato
	public void setHourlyContractPower(double hourlyContractPower){this.hourlyContractPower = hourlyContractPower;} // establecer la potencia contratada en un contrato horario
	public void setHourlyContractPrice(double hourlyContractPrice){this.hourlyContractPrice = hourlyContractPrice;} // establecer el precio en un contrato horario
	public void setContractHour(int contractHour){this.contractHour = contractHour;} // establecer la hora del contrato horario
	public void setContractType(String contractType){this.contractType = contractType;} // establecer el tipo del contrato
	public void setContractSellerId(int contractSellerId){this.contractSellerId = contractSellerId;} // establecer el identificador del vendedor del contrato
	public void setGenerationUnitId(int generationUnitId){this.generationUnitId = generationUnitId;} // establecer el identificador de la unidad de generación del contrato
	public void setContractBuyerId(int contractBuyerId){this.contractBuyerId = contractBuyerId;} // establecer el identificador del comprador del contrato
	public void setSourceNodeId(int sourceNodeId){this.sourceNodeId = sourceNodeId;} // establecer el identificador del nodo de oferta
	public void setWithdrawalNodeId(int withdrawalNodeId){this.withdrawalNodeId = withdrawalNodeId;} // establecer el identificador del nodo de demanda
	
	public void setDispatchedContractPowerPD(double[] dispatchedContractPower){this.dispatchedContractEnergyPD = dispatchedContractPower;} // establecer  la potencia contratada en contratos PD que fue despachada
	public void setDispatchedContractPowerPD(double dispatchedContractPower, int h){this.dispatchedContractEnergyPD[h] = dispatchedContractPower;} // establecer  la potencia contratada en contratos PD que fue despachada en la hora h
	
	// Print the information of generation unit
	public void printContract(){
		System.out.print(this.contractId + "\t" + this.contractSellerId + "\t" + this.contractSeller.getGeneratorName() 
				+ "\t" + this.generationUnitId + "\t" + this.generationUnit.getUnitName() 
				+ "\t" + this.contractBuyerId + "\t" + this.contractBuyer.getRetailerName() 
				+ "\t" + this.contractType + "\t" + this.sourceNodeId + "\t" + this.sourceNode.getNodeName() 
				+ "\t" + this.withdrawalNodeId + "\t" + this.withdrawalNode.getNodeName() 
				+ "\t" + this.contractStartDate  + "\t" + this.contractFinalDate);
		for (int i = 0; i < 24; i++)
		{
		  System.out.print("\t" + this.contractPower[i]  + "\t");
		}
		for (int i = 0; i < 24; i++)
		{
		  System.out.print("\t" + this.contractPrice[i]  + "\t");
		} 
		System.out.println();
	}
}
