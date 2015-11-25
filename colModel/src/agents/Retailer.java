package agents;

import java.util.ArrayList;
import java.util.List;

import market.GenerationContract;
import market.Node;
import market.PowerBid;

public class Retailer implements Comparable<Retailer> {

	// datos generales
	private int retId; 	// código del comercializador
	private String retailerName; // nombre del comercializador
	private String retailerCod;	// código del comercializador
	private Node demandNode; // nodo de demanda
	private int position; // posición del comercializador en la base de datos
	
	// datos mercado de energía
	private double[] energyDemand; // demanda de potencia para cada hora
	private List<GenerationContract> generationContractsPC; // lista de contratos pague lo contratado del comercializador
	private List<GenerationContract> generationContractsPD; // lista de contratos pague lo demandado del comercializador
	private List<GenerationContract> generationContracts; // lista de contratos del comercializador
	private List<List<GenerationContract>> sortGenerationContractsPD; // lista de contratos pague lo demandado organizados por precio
	private List<List<GenerationContract>> sortDispatchedGenerationContractsPD; // lista de contratos pague lo demandado espachados organizados por precio
	private double[] contractsProportion ; 	// proporción de energía en contratos
	private double[] contractEnergy; 			// energía contratada
	private double[] referenceDemand; 		// demanda de referencia para los contratos
	private double   referenceAverageDemand; // demanda de referencia promedio para los contratos
	private double[] referenceContractsPrice; // precio de referencia para los contratos
	
	// liquidación
	private double[] contractEnergyPurchasesPCMWh; // magnitud compras en contratos pague lo contratado
	private double[] contractEnergyPurchasesPCCOP; // valor compras en contratos pague lo contratado
	private double[] contractMaxEnergyPurchasesPDMWh; // máxima magnitud compras en contratos pague lo demandado
	private double[] contractEnergyPurchasesPDMWh; // magnitud compras en contratos pague lo demandado
	private double[] contractEnergyPurchasesPDCOP; // valor compras en contratos pague lo demandado
	private double[] PoolEnergyPurchasesMWh; // magnitud compras en bolsa
	private double[] PoolEnergyPurchasesCOP; // valor compras en bolsa
	private double[] PoolEnergySalesMWh; // magnitud ventas en bolsa
	private double[] PoolEnergySalesCOP; // valor ventas en bolsa
	private double[] constraintsCOP; // valor restricciones
	private double[] settlementUsageChargesCOP; // valor cargos por uso
	private double[] settlementEnergyMarket; // liquidación del mercado de energía
	
	
	// constructor vacío
	public Retailer(){}
	
	// constructor con nombre
	public Retailer(int retId, String retailerName){
		this.retId = retId;
		this.retailerName = retailerName;
	}
	
	// constructor con nombre y nodo de demanda
	public Retailer(int retID, String retailerName, Node demandNode, int position){
		this.retId = retID;
		this.retailerName 	= retailerName;
		this.demandNode		= demandNode;
		this.position 		= position; 
	}
		
	// constructor con nombre y nodo de demanda
	public Retailer(int retID, String retailerName, String retailerCod, Node demandNode, double[] contractsProportion,  double[] contractEnergy, 
			double[] referenceDemand, double   referenceAverageDemand, double[] referenceContractsPrice, int position){
		this.retId = retID;
		this.retailerName 	= retailerName;
		this.demandNode		= demandNode;
		this.retailerCod 		= retailerCod;
		this.contractsProportion = contractsProportion; 
		this.contractEnergy = contractEnergy;
		this.referenceDemand = referenceDemand;
		this.referenceAverageDemand = referenceAverageDemand;
		this.referenceContractsPrice = referenceContractsPrice;
		this.position 		= position; 
	}
		
	// constructor con nombre, nodo de demanda y contratos
	//public Retailer(int retID, String retailerName, Node demandNode, List<GenerationContract> generationContracts){
	//	this.retId = retID;
	//	this.retailerName 			= retailerName;
	//	this.demandNode				= demandNode;
	//	this.generationContracts 	= generationContracts;
	//}
	
	// Get methods
	public int getRetailerId(){return this.retId;} // obtener el identificador del comercializador
	public String getRetailerName(){return this.retailerName;}	// obtener el nombre del comercializador
	public String getRetailerCod(){return this.retailerCod;}	// obtener el código del comercializador
	public Node getDemandNode(){ return this.demandNode;}	// obtener el nodo de demanda del comercializador
	public double[] getEnergyDemand(){return this.energyDemand;} // obtener la demanda de potencia para cada hora
	public List<GenerationContract> getGenerationContracts(){return this.generationContracts;} // obtener la lista de contratos de generación del comercializador
	public List<GenerationContract> getGenerationContractsPC(){return this.generationContractsPC;} // obtener la lista de contratos de generación pague lo contratado del comercializador
	public List<GenerationContract> getGenerationContractsPD(){return this.generationContractsPD;} // obtener la lista de contratos de generación pague lo demandado del comercializador
	public List<List<GenerationContract>> getSortGenerationContractsPD(){return this.sortGenerationContractsPD;} // obtener la lista organizada de contratos PD
	public List<List<GenerationContract>> getSortDispatchedGenerationContractsPD(){return this.sortDispatchedGenerationContractsPD;} // obtener la lista organizada de contratos PD despachados
	public double[] getContractsProportion(){return this.contractsProportion;} // obtener la proporción de la energía en contratos
	public double[] getContractEnergy(){return this.contractEnergy;} // obtener la energía contratada
	public double[] getReferenceDemand(){return this.referenceDemand;} // obtener la demanda de referencia para los contratos
	public double[] getReferenceContractsPrice(){return this.referenceContractsPrice;} // obtener el precio de referencia para los contratos
	
	public double[] getContractEnergyPurchasesPCMWh(){return this.contractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado
	public double[] getContractEnergyPurchasesPCCOP(){return this.contractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado
	public double[] getContractMaxEnergyPurchasesPDMWh(){return this.contractMaxEnergyPurchasesPDMWh;} // obtener la máxima magnitud de las compras en contratos pague lo demandado
	public double[] getContractEnergyPurchasesPDMWh(){return this.contractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado
	public double[] getContractEnergyPurchasesPDCOP(){return this.contractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado
	public double[] getPoolEnergyPurchasesMWh(){return this.PoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa
	public double[] getPoolEnergyPurchasesCOP(){return this.PoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa
	public double[] getPoolEnergySalesMWh(){return this.PoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa
	public double[] getPoolEnergySalesCOP(){return this.PoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa
	public double[] getConstraintsCOP(){return this.constraintsCOP;} // obtener el valor de las restricciones
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;} // obtener la liquidación de los cargos por uso
	public double[] getSettlementEnergyMarket(){return this.settlementEnergyMarket;} // obtener las liquidación del mercado de energía
	
	// Set methods
	public void setRetailerName(String retailerName){this.retailerName  = retailerName;} // establecer el nombre del comercializador
	public void setRetailerCod(String retailerCod){this.retailerCod  = retailerCod;} // establecer el código del comercializador
	public void setDemandNode(Node demandNode){ this.demandNode  = demandNode;}	// establecer el nodo de demanda del comercializador
	public void setEnergyDemand(double[] energyDemand){this.energyDemand = energyDemand;} 	// establecer la demanda de potencia para las 24 horas del día
	
	public void setGenerationContracts(List<GenerationContract> generationContracts){this.generationContracts = generationContracts;} // establecer la lista de contratos de generación del comercializador
	public void setGenerationContractsPC(List<GenerationContract> generationContractsPC){this.generationContractsPC = generationContractsPC;} // establecer la lista de contratos de generación pague lo contratado del comercializador
	public void setGenerationContractsPD(List<GenerationContract> generationContractsPD){this.generationContractsPD = generationContractsPD;} // establecer la lista de contratos de generación pague lo demandado del comercializador
	public void setSortGenerationContractsPD(List<List<GenerationContract>> sortGenerationContractsP){this.sortGenerationContractsPD = sortGenerationContractsP;} // establecer la lista organizada de contratos PD
	public void setSortDispatchedGenerationContractsPD(List<List<GenerationContract>> sortDispatchedGenerationContractsPD){this.sortDispatchedGenerationContractsPD = sortDispatchedGenerationContractsPD;} // establecer la lista organizada de contratos PD despachados
	
	public void setContractsProportion(double[] contractsProportion){ this.contractsProportion = contractsProportion;} // establecer la proporción de la energía en contratos
	public void setContractEnergy(double[] contractEnergy){this.contractEnergy = contractEnergy;} // establecer la energía contratada
	public void setReferenceDemand(double[] referenceDemand){this.referenceDemand = referenceDemand;} // establecer la demanda de referencia para los contratos
	public void setReferenceContractsPrice(double[] referenceContractsPrice){this.referenceContractsPrice = referenceContractsPrice;} // establecer el precio de referencia para los contratos
	public void serRetailerId(int retId){this.retId = retId;} // establecer el identificador del comercializador
	
	public void setContractEnergyPurchasesPCMWh(double[] contractEnergyPurchasesPCMWh){this.contractEnergyPurchasesPCMWh = contractEnergyPurchasesPCMWh;} // establecer la magnitud de las compras en en contratos pague lo contratado
	public void setContractEnergyPurchasesPCCOP(double[] contractEnergyPurchasesPCCOP){this.contractEnergyPurchasesPCCOP = contractEnergyPurchasesPCCOP;} // establecer el valor de las compras en contratos pague lo contratado
	public void setContractMaxEnergyPurchasesPDMWh(double[] contractMaxEnergyPurchasesPDMWh){this.contractMaxEnergyPurchasesPDMWh = contractMaxEnergyPurchasesPDMWh;} // establecer la máxima magnitud de las compras en contratos pague lo demandado
	public void setContractEnergyPurchasesPDMWh(double[] contractEnergyPurchasesPDMWh){this.contractEnergyPurchasesPDMWh = contractEnergyPurchasesPDMWh;} // establecer la maginitud de las compras en en contratos pague lo demandado
	public void setContractEnergyPurchasesPDCOP(double[] contractEnergyPurchasesPDCOP){this.contractEnergyPurchasesPDCOP = contractEnergyPurchasesPDCOP;} // establecer el valor de las compras en contratos pague lo demandado
	public void setPoolEnergyPurchasesMWh(double[] PoolEnergyPurchasesMWh){this.PoolEnergyPurchasesMWh = PoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa
	public void setPoolEnergyPurchasesCOP(double[] PoolEnergyPurchasesCOP){this.PoolEnergyPurchasesCOP = PoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa
	public void setPoolEnergySalesMWh(double[] PoolEnergySalesMWh){this.PoolEnergySalesMWh = PoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa
	public void setPoolEnergySalesCOP(double[] PoolEnergySalesCOP){this.PoolEnergySalesCOP = PoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa
	public void setConstraintsCOP(double[] constraintsCOP){this.constraintsCOP = constraintsCOP;} // establecer el valor de las restricciones
	public void setSettlementUsageChargesCOP(double[] settlementUsageChargesCOP){this.settlementUsageChargesCOP = settlementUsageChargesCOP;} // establecer la liquidación de los cargos por uso
	public void setSettlementEnergyMarket(double[] settlementEnergyMarket){this.settlementEnergyMarket = settlementEnergyMarket;} // establecer la liquidación dle mercado de energía

	// imprimir las características de cada comercializador
	public void printRetailer(){
		System.out.println(this.retId + "\t" + this.retailerName + "\t" + this.demandNode.getNodeName() + "\t"+ this.position + "\t");
	}
	
	// ordenar retailers de mayor a menor demanda de referencia
	@Override
	public int compareTo(Retailer retailer) {
		if (this.referenceAverageDemand > retailer.referenceAverageDemand) {   
			return -1;   
		} else if (this.referenceAverageDemand < retailer.referenceAverageDemand) {
			return 1;   
		} else {   
		    return 0;   
		}
	}
}
