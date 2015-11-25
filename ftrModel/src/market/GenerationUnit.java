package market;

import java.util.List;

import agents.Generator;

public class GenerationUnit {

	// datos generales
	private int unitID; 		// c�digo de identificaci�n
	private String unitName; 	// nombre de la unidad de generaci�n
	private Node unitNode;		// frontera comercial
	private int position;		// posici�n de la unidad en la base de datos		
	
	// datos t�cnicos
	private Generator generator; 		// administrador de la central de generaci�n
	private String unitType; 			// tipo de planta [hidr�ulica, t�rmica o cogeneraci�n]
	private double effectiveCapacity; 	// capacidad efectiva en MW
	private double technicalMinimum; 	// m�nimo t�cnico
	private double generationCost; 		// costo marginal de generaci�n
	//private double remainingCapacity; 	// capacidad remanente en MW [OPCIONAL]

	// datos mercado de energ�a
	private int haveContracts;			// [0,1], tiene o no contratos
	private List<GenerationContract> generationContracts;	// lista de contratos de generaci�n
	private List<GenerationContract> generationContractsPC;	// lista de contratos de generaci�n tipo pague lo contratado
	private List<GenerationContract> generationContractsPD;	// lista de contratos de generaci�n tipo pague lo demandado
	private double[] contractsProportion ; 	// proporci�n de energ�a en contratos
	private double[] contractEnergy; 			// energ�a contratada
	private double[] referenceContractsPrice; // precio de referencia para los contratos
	
	// liquidaci�n uninodal
	private double[] idealGeneration; 	// generaci�n en el depacho ideal
	private double[] realGeneration;	// generaci�n en el despacho real
	private double[] contractEnergySalesPCMWh; // magnitud ventas en contratos pague lo contratado
	private double[] contractEnergySalesPCCOP; // valor ventas en contratos pague lo contratado
	private double[] contractMaxEnergySalesPDMWh; // m�xima magnitud ventas en contratos pague lo demandado
	private double[] contractEnergySalesPDMWh; // magnitud ventas en contratos pague lo demandado
	private double[] contractEnergySalesPDCOP; // valor ventas en contratos pague lo demandado
	private double[] PoolEnergyPurchasesMWh; // magnitud compras en bolsa
	private double[] PoolEnergyPurchasesCOP; // valor compras en bolsa
	private double[] PoolEnergySalesMWh; // magnitud ventas en bolsa
	private double[] PoolEnergySalesCOP; // valor ventas en bolsa
	private double[] settlementEnergyMarket; // liquidaci�n del mercado de energ�a
	private double[] positiveReconciliationMWh; // magnitud reconciliaci�n positiva
	private double[] positiveReconciliationCOP; // valor reconciliaci�n positiva
	private double startStopPice; // precio de arranque y parada
	private double[] negativeReconciliationMWh; // magnitud reconciliaci�n negativa
	private double[] negativeReconciliationCOP; // valor reconciliaci�n negativa
	private double[] settlementUsageChargesCOP; // valor cargos por uso
	
	// liquidaci�n nodal
	private double[] nodContractEnergySalesPCMWh; // magnitud compras en contratos pague lo contratado en el mercado nodal
	private double[] nodContractEnergySalesPCCOP; // valor compras en contratos pague lo contratado en el mercado nodal
	private double[] nodContractMaxEnergySalesPDMWh; // m�xima magnitud compras en contratos pague lo demandado en el mercado nodal
	private double[] nodContractEnergySalesPDMWh; // magnitud compras en contratos pague lo demandado en el mercado nodal
	private double[] nodContractEnergySalesPDCOP; // valor compras en contratos pague lo demandado en el mercado nodal
	private double[] nodPoolEnergyPurchasesMWh; // magnitud compras en bolsa en el mercado nodal
	private double[] nodPoolEnergyPurchasesCOP; // valor compras en bolsa en el mercado nodal
	private double[] nodPoolEnergySalesMWh; // magnitud ventas en bolsa en el mercado nodal
	private double[] nodPoolEnergySalesCOP; // valor ventas en bolsa en el mercado nodal
	private double[] nodSettlementEnergyMarket; // liquidaci�n del mercado de energ�a en el mercado nodal
	
	// liquidaci�n nodal con ftrs
	private double[] ftrContractEnergySalesPCMWh; // magnitud compras en contratos pague lo contratado en el mercado nodal con FTRs
	private double[] ftrContractEnergySalesPCCOP; // valor compras en contratos pague lo contratado en el mercado nodal con FTRs
	private double[] ftrContractMaxEnergySalesPDMWh; // m�xima magnitud compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrContractEnergySalesPDMWh; // magnitud compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrContractEnergySalesPDCOP; // valor compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrPoolEnergyPurchasesMWh; // magnitud compras en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergyPurchasesCOP; // valor compras en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergySalesMWh; // magnitud ventas en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergySalesCOP; // valor ventas en bolsa en el mercado nodal con FTRs
	private double[] ftrSettlementEnergyMarket; // liquidaci�n del mercado de energ�a en el mercado nodal con FTRs
		
	// constructor vac�o
	public GenerationUnit(){}
		
	// constructor para contrucci�n de contratos
	public GenerationUnit(int unitID, String unitName, Generator generator, String unitType, Node unitNode, 
			double effectiveCapacity, double technicalMinimum, double startStopPice, double[] contractsProportion, double[] contractEnergy, double[] referenceContractsPrice, int position){
		this.unitID = unitID;
		this.unitName = unitName;
		this.generator = generator; 
		this.unitType = unitType; 
		this.unitNode = unitNode;
		this.effectiveCapacity = effectiveCapacity;
		this.technicalMinimum = technicalMinimum;
		this.startStopPice = startStopPice;
		this.contractsProportion = contractsProportion;
		this.contractEnergy = contractEnergy;
		this.referenceContractsPrice = referenceContractsPrice;
		this.position = position; 
	}
	
	// get methods
	public int getUnitID(){return this.unitID;} // obtener el identificador de la unidad
	public String getUnitName(){return this.unitName;} // obtener el nombre de la unidad
	public Generator getGenerator(){ return this.generator;} // obtener el generador al cual pertenece
	public String getUnitType(){return this.unitType;} // obtener el tipo de la unidad
	public Node getNode(){return this.unitNode;} // obtener la forntera comercial de la unidad
	public double getEffectiveCapacity(){return this.effectiveCapacity;} // obtener la capacidad efectiva de la unidad
	public double getTechnicalMinimum(){return this.technicalMinimum;} // obtener el m�nimo t�cnico de la unidad
	public double getGenerationCost(){return this.generationCost;} // obtener el costo de generaci�n de la unidad
	public int getHaveContracts(){return this.haveContracts;} // saber si una unidad tiene o no contratos
	public List<GenerationContract> getGenerationContracts(){return this.generationContracts;} // obtener la lista de contratos de generaci�n de la unidad
	public List<GenerationContract> getGenerationContractsPC(){return this.generationContractsPC;} // obtener la lista de contratos de generaci�n tipo pague lo contratado
	public List<GenerationContract> getGenerationContractsPD(){return this.generationContractsPD;} // obtener la lista de contratos de generaci�n tipo pague lo demandado
	public double[] getContractsProportion(){return this.contractsProportion;} // obtener la proporci�n de la energ�a en contratos
	public double[] getContractEnergy(){return this.contractEnergy;} // obtener la energ�a contratada
	public double[] getReferenceContractsPrice(){return this.referenceContractsPrice;} // obtener el precio de referencia para los contratos
	public int getPosition(){return this.position;} // obtener la posici�n de la unidad en la base de datos
	
	public double[] getIdealGeneration(){return this.idealGeneration; } // obtener la generaci�n ideal
	public double[] getRealGeneration(){return this.realGeneration; } // obtener la genraci�n real
	
	//---------------------------------------------------------------------------------- MERCADO UNINODAL --------------------------------------------------------------------------------------//
	public double[] getContractEnergySalesPCMWh(){return this.contractEnergySalesPCMWh;} // obtener la magnitud de las ventas en contratos pague lo contratado
	public double[] getContractEnergySalesPCCOP(){return this.contractEnergySalesPCCOP;} // obtener el valor de las ventas en contratos pague lo contratado
	public double[] getContractMaxEnergySalesPDMWh(){return this.contractMaxEnergySalesPDMWh;} // obtener la m�xima magnitud de las ventas en contratos pague lo demandado
	public double[] getContractEnergySalesPDMWh(){return this.contractEnergySalesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado
	public double[] getContractEnergySalesPDCOP(){return this.contractEnergySalesPDCOP;} // obtener el valor de las ventas en contratos pague lo demandado
	public double[] getPoolEnergyPurchasesMWh(){return this.PoolEnergyPurchasesMWh;} // obtener la magnitud de las ventas en bolsa
	public double[] getPoolEnergyPurchasesCOP(){return this.PoolEnergyPurchasesCOP;} // obtener el valor de las ventas en bolsa
	public double[] getPoolEnergySalesMWh(){return this.PoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa
	public double[] getPoolEnergySalesCOP(){return this.PoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa
	public double[] getSettlementEnergyMarket(){return this.settlementEnergyMarket;} // obtener las liquidaci�n del mercado de energ�a
	public double[] getPositiveReconciliationMWh(){return this.positiveReconciliationMWh;} // obtener la magnitud de la reconciliaci�n positiva
	public double[] getPositiveReconciliationCOP(){return this.positiveReconciliationCOP;} // obtener el valor de la reconciliaci�n positiva
	public double getStartStopPice(){return this.startStopPice;} // obtener el precio de arranque y parada
	public double[] getNegativeReconciliationMWh(){return this.negativeReconciliationMWh;} // obtener la magnitud de la reconciliaci�n negativa
	public double[] getNegativeReconciliationCOP(){return this.negativeReconciliationCOP;} // obtener el valor de la reconciliaci�n negativa
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;} // obtener la liquidaci�n de los cargos por uso
	
	//--------------------------------------------------------------------------------- MERCADO NODAL --------------------------------------------------------------------------------------------//
	public double[] getNodContractEnergySalesPCMWh(){return this.nodContractEnergySalesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado en el mercado nodal
	public double[] getNodContractEnergySalesPCCOP(){return this.nodContractEnergySalesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado en el mercado nodal
	public double[] getNodContractMaxEnergySalesPDMWh(){return this.nodContractMaxEnergySalesPDMWh;} // obtener la m�xima magnitud de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodContractEnergySalesPDMWh(){return this.nodContractEnergySalesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodContractEnergySalesPDCOP(){return this.nodContractEnergySalesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodPoolEnergyPurchasesMWh(){return this.nodPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en el mercado nodal
	public double[] getNodPoolEnergyPurchasesCOP(){return this.nodPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en el mercado nodal
	public double[] getNodPoolEnergySalesMWh(){return this.nodPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en el mercado nodal
	public double[] getNodPoolEnergySalesCOP(){return this.nodPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en el mercado nodal
	public double[] getNodSettlementEnergyMarket(){return this.nodSettlementEnergyMarket;} // obtener las liquidaci�n del mercado de energ�a en el mercado nodal
	
	//----------------------------------------------------------------------------- MERCADO NODAL CON FTRS--------------------------------------------------------------------------------------------//
	public double[] getFtrContractEnergySalesPCMWh(){return this.ftrContractEnergySalesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado en el mercado nodal con FTRs
	public double[] getFtrContractEnergySalesPCCOP(){return this.ftrContractEnergySalesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado en el mercado nodal con FTRs
	public double[] getFtrContractMaxEnergySalesPDMWh(){return this.ftrContractMaxEnergySalesPDMWh;} // obtener la m�xima magnitud de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrContractEnergySalesPDMWh(){return this.ftrContractEnergySalesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrContractEnergySalesPDCOP(){return this.ftrContractEnergySalesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrPoolEnergyPurchasesMWh(){return this.ftrPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergyPurchasesCOP(){return this.ftrPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergySalesMWh(){return this.ftrPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergySalesCOP(){return this.ftrPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en el mercado nodal con FTRs
	public double[] getFtrSettlementEnergyMarket(){return this.ftrSettlementEnergyMarket;} // obtener las liquidaci�n del mercado de energ�a en el mercado nodal con FTRs
		
	// set methods
	public void setUnitID(int unitID){this.unitID = unitID;} // establecer el identificador de la unidad
	public void setUnitName(String unitName){this.unitName = unitName;} // establecer el nombre de la unidad
	public void setGenerator(Generator generator){this.generator = generator;} // establecer el generador al cual pertenece
	public void setUnitType(String unitType){this.unitType = unitType;} // establecer el tipo de la unidad
	public void setNode(Node unitNode){this.unitNode = unitNode;} // establecer la forntera comercial de la unidad
	public void setEffectiveCapacity(double effectiveCapacity){this.effectiveCapacity = effectiveCapacity;} // establecer la capacidad efectiva de la unidad
	public void setGenerationCost(double generationCost){this.generationCost = generationCost;} // establecer el costo de generaci�n de la unidad
	public void setHaveContracts(int haveContracts){this.haveContracts = haveContracts;} // saber si una unidad tiene o no contratos
	public void setGenerationContracts(List<GenerationContract> generationContracts){this.generationContracts = generationContracts;} // obtener la lista de contratos de generaci�n de la unidad
	public void setGenerationContractsPC(List<GenerationContract> generationContractsPC){this.generationContractsPC = generationContractsPC;} // establecer la lista de contratos de generaci�n tipo pague lo contratado
	public void setGenerationContractsPD(List<GenerationContract> generationContractsPD){this.generationContractsPD = generationContractsPD;} // establecer la lista de contratos de generaci�n tipo pague lo demandado
	public void setContractsProportion(double[] contractsProportion){ this.contractsProportion = contractsProportion;} // establecer la proporci�n de la energ�a en contratos
	public void setContractEnergy(double[] contractEnergy){this.contractEnergy = contractEnergy;} // establecer la energ�a contratada
	public void setReferenceContractsPrice(double[] referenceContractsPrice){this.referenceContractsPrice = referenceContractsPrice;} // establecer el precio de referencia para los contratos
		
	public void setIdealGeneration(double[] idealGeneration){this.idealGeneration = idealGeneration; } // establecer la generaci�n ideal
	public void setRealGeneration(double[] realGeneration){this.realGeneration = realGeneration; } // establecer la generaci�n real
	
	//---------------------------------------------------------------------------------- MERCADO UNINODAL --------------------------------------------------------------------------------------//
	public void setContractEnergySalesPCMWh(double[] contractEnergySalesPCMWh){this.contractEnergySalesPCMWh = contractEnergySalesPCMWh;} // establecer la magnitud de las ventas en en contratos pague lo contratado
	public void setContractEnergySalesPCCOP(double[] contractEnergySalesPCCOP){this.contractEnergySalesPCCOP = contractEnergySalesPCCOP;} // establecer el valor de las ventas en contratos pague lo contratado
	public void setContractMaxEnergySalesPDMWh(double[] contractMaxEnergySalesPDMWh){this.contractMaxEnergySalesPDMWh = contractMaxEnergySalesPDMWh;} // establecer la m�xima magnitud de las ventas en contratos pague lo demandado
	public void setContractEnergySalesPDMWh(double[] contractEnergySalesPDMWh){this.contractEnergySalesPDMWh = contractEnergySalesPDMWh;} // establecer la maginitud de las ventas en en contratos pague lo demandado
	public void setContractEnergySalesPDCOP(double[] contractEnergySalesPDCOP){this.contractEnergySalesPDCOP = contractEnergySalesPDCOP;} // establecer el valor de las ventas en contratos pague lo demandado
	public void setPoolEnergyPurchasesMWh(double[] PoolEnergyPurchasesMWh){this.PoolEnergyPurchasesMWh = PoolEnergyPurchasesMWh;} // establecer la magnitud de las ventas en bolsa
	public void setPoolEnergyPurchasesCOP(double[] PoolEnergyPurchasesCOP){this.PoolEnergyPurchasesCOP = PoolEnergyPurchasesCOP;} // establecer el valor de las ventas en bolsa
	public void setPoolEnergySalesMWh(double[] PoolEnergySalesMWh){this.PoolEnergySalesMWh = PoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa
	public void setPoolEnergySalesCOP(double[] PoolEnergySalesCOP){this.PoolEnergySalesCOP = PoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa
	public void setSettlementEnergyMarket(double[] settlementEnergyMarket){this.settlementEnergyMarket = settlementEnergyMarket;} // establecer la liquidaci�n dle mercado de energ�a
	public void setPositiveReconciliationMWh(double[] positiveReconciliationMWh){this.positiveReconciliationMWh = positiveReconciliationMWh;} // establecer la magnitud de la reconciliaci�n positiva
	public void setPositiveReconciliationCOP(double[] positiveReconciliationCOP){this.positiveReconciliationCOP = positiveReconciliationCOP;} // establecer el valor de la reconciliaci�n positiva
	public void setStartStopPice(double startStopPice){this.startStopPice = startStopPice;} // establecer el precio de arranque y parada
	public void setNegativeReconciliationMWh(double[] negativeReconciliationMWh){this.negativeReconciliationMWh = negativeReconciliationMWh;} // establecer la magnitud de la reconciliaci�n negativa
	public void setNegativeReconciliationCOP(double[] negativeReconciliationCOP){this.negativeReconciliationCOP = negativeReconciliationCOP;} // establecer el valor de la reconciliaci�n negativa
	public void setSettlementUsageChargesCOP(double[] settlementUsageChargesCOP){this.settlementUsageChargesCOP = settlementUsageChargesCOP;} // establecer la liquidaci�n de los cargos por uso
	
	//--------------------------------------------------------------------------------- MERCADO NODAL --------------------------------------------------------------------------------------------//
	public void setNodContractEnergySalesPCMWh(double[] nodContractEnergySalesPCMWh){this.nodContractEnergySalesPCMWh = nodContractEnergySalesPCMWh;} // establecer la magnitud de las ventas en en contratos pague lo contratado en el mercado nodal
	public void setNodContractEnergySalesPCCOP(double[] nodContractEnergySalesPCCOP){this.nodContractEnergySalesPCCOP = nodContractEnergySalesPCCOP;} // establecer el valor de las ventas en contratos pague lo contratado en el mercado nodal
	public void setNodContractMaxEnergySalesPDMWh(double[] nodContractMaxEnergySalesPDMWh){this.nodContractMaxEnergySalesPDMWh = nodContractMaxEnergySalesPDMWh;} // establecer la m�xima magnitud de las ventas en contratos pague lo demandado en el mercado nodal
	public void setNodContractEnergySalesPDMWh(double[] nodContractEnergySalesPDMWh){this.nodContractEnergySalesPDMWh = nodContractEnergySalesPDMWh;} // establecer la maginitud de las ventas en en contratos pague lo demandado en el mercado nodal
	public void setNodContractEnergySalesPDCOP(double[] nodContractEnergySalesPDCOP){this.nodContractEnergySalesPDCOP = nodContractEnergySalesPDCOP;} // establecer el valor de las ventas en contratos pague lo demandado en el mercado nodal
	public void setNodPoolEnergyPurchasesMWh(double[] nodPoolEnergyPurchasesMWh){this.nodPoolEnergyPurchasesMWh = nodPoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa en el mercado nodal
	public void setNodPoolEnergyPurchasesCOP(double[] nodPoolEnergyPurchasesCOP){this.nodPoolEnergyPurchasesCOP = nodPoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa en el mercado nodal
	public void setNodPoolEnergySalesMWh(double[] nodPoolEnergySalesMWh){this.nodPoolEnergySalesMWh = nodPoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa en el mercado nodal
	public void setNodPoolEnergySalesCOP(double[] nodPoolEnergySalesCOP){this.nodPoolEnergySalesCOP = nodPoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa en el mercado nodal
	public void setNodSettlementEnergyMarket(double[] nodSettlementEnergyMarket){this.nodSettlementEnergyMarket = nodSettlementEnergyMarket;} // establecer la liquidaci�n dle mercado de energ�a en el mercado nodal
	
	//--------------------------------------------------------------------------------- MERCADO NODAL --------------------------------------------------------------------------------------------//
	public void setFtrContractEnergySalesPCMWh(double[] ftrContractEnergySalesPCMWh){this.ftrContractEnergySalesPCMWh = ftrContractEnergySalesPCMWh;} // establecer la magnitud de las ventas en en contratos pague lo contratado en el mercado nodal con FTRs
	public void setFtrContractEnergySalesPCCOP(double[] ftrContractEnergySalesPCCOP){this.ftrContractEnergySalesPCCOP = ftrContractEnergySalesPCCOP;} // establecer el valor de las ventas en contratos pague lo contratado en el mercado nodal con FTRs
	public void setFtrContractMaxEnergySalesPDMWh(double[] ftrContractMaxEnergySalesPDMWh){this.ftrContractMaxEnergySalesPDMWh = ftrContractMaxEnergySalesPDMWh;} // establecer la m�xima magnitud de las ventas en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrContractEnergySalesPDMWh(double[] ftrContractEnergySalesPDMWh){this.ftrContractEnergySalesPDMWh = ftrContractEnergySalesPDMWh;} // establecer la maginitud de las ventas en en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrContractEnergySalesPDCOP(double[] ftrContractEnergySalesPDCOP){this.ftrContractEnergySalesPDCOP = ftrContractEnergySalesPDCOP;} // establecer el valor de las ventas en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrPoolEnergyPurchasesMWh(double[] ftrPoolEnergyPurchasesMWh){this.ftrPoolEnergyPurchasesMWh = ftrPoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergyPurchasesCOP(double[] ftrPoolEnergyPurchasesCOP){this.ftrPoolEnergyPurchasesCOP = ftrPoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergySalesMWh(double[] ftrPoolEnergySalesMWh){this.ftrPoolEnergySalesMWh = ftrPoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergySalesCOP(double[] ftrPoolEnergySalesCOP){this.ftrPoolEnergySalesCOP = ftrPoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa en el mercado nodal con FTRs
	public void setFtrSettlementEnergyMarket(double[] ftrSettlementEnergyMarket){this.ftrSettlementEnergyMarket = ftrSettlementEnergyMarket;} // establecer la liquidaci�n dle mercado de energ�a en el mercado nodal con FTRs
		
	// imprimir las caracter�sticas de cada unidad de generaci�n
	public void printUnit(){
		System.out.print(this.unitID + "\t" + this.unitName + "\t" + this.generator.getGeneratorName()
				+ "\t" + this.unitType + "\t" + this.unitNode.getNodeName() + "\t" + this.effectiveCapacity + "\t" + this.technicalMinimum);
		for (int i = 0; i< 24; i++)
		{
			System.out.print("\t" + this.contractEnergy[i]);
		}
		System.out.println("\t" + this.position);
	}
}


/*// constructor con id, nombre, nodo, capacidad efectiva, m�nimo t�cnico, costo de generaci�n
public GenerationUnit(int unitID, String unitName, Node unitNode, double effectiveCapacity, 
		double technicalMinimum, double generationCost){
	this.unitID = unitID;
	this.unitName = unitName;
	this.unitNode = unitNode;
	this.effectiveCapacity = effectiveCapacity;
	this.technicalMinimum = technicalMinimum; 
	this.generationCost = generationCost;
}

// constructor con id, nombre, generador, nodo, capacidad efectiva, m�nimo t�cnico, posici�n
public GenerationUnit(int unitID, String unitName, Generator generator, Node unitNode, 
		double effectiveCapacity, double technicalMinimum, int position){
	this.unitID = unitID;
	this.unitName = unitName;
	this.generator = generator; 
	this.unitNode = unitNode;
	this.effectiveCapacity = effectiveCapacity;
	this.technicalMinimum = technicalMinimum;
	this.position = position; 
}

// constructor con id, nombre, nodo, capacidad efectiva, costo de generaci�n, haveContracts, contratos
	public GenerationUnit(int unitID, String unitName, Node unitNode, double effectiveCapacity, double generationCost, int haveContracts, List<GenerationContract> generationContracts){
		this.unitID = unitID;
		this.unitName = unitName;
		this.unitNode = unitNode;
		this.effectiveCapacity = effectiveCapacity;
		this.generationCost = generationCost;
		this.haveContracts = haveContracts;
		this.generationContracts = generationContracts;
	}
*
*/


//constructor con nombre, nodo
	//public GenerationUnit(String unitName, Node unitNode)
	//{
	//	this.unitName = unitName;
	//	this.unitNode = unitNode;
	//	}
		
	// constructor con nombre
	//public GenerationUnit(String unitName){
	//	this.unitName = unitName;
	//	}
		