package agents;

import java.util.ArrayList;
import java.util.List;

import utilities.Global;
import utilities.MathFuns;

import market.Ftr;
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
	private double[] energyDemand; 		// demanda de energía para cada hora
	private double[] servedDemand; 		// demanda de energía efectivamente atendida
	private double[] unservedDemand; 	// demanda de energía no atendida
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
	
	// liquidación uninodal
	private double[] contractEnergyPurchasesPCMWh; // magnitud compras en contratos pague lo contratado
	private double[] contractEnergyPurchasesPCCOP; // valor compras en contratos pague lo contratado
	private double[] contractMaxEnergyPurchasesPDMWh; // máxima magnitud compras en contratos pague lo demandado
	private double[] contractEnergyPurchasesPDMWh; // magnitud compras en contratos pague lo demandado
	private double[] contractEnergyPurchasesPDCOP; // valor compras en contratos pague lo demandado
	private double[] poolEnergyPurchasesMWh; // magnitud compras en bolsa
	private double[] poolEnergyPurchasesCOP; // valor compras en bolsa
	private double[] poolEnergySalesMWh; // magnitud ventas en bolsa
	private double[] poolEnergySalesCOP; // valor ventas en bolsa
	private double[] constraintsCOP; // valor restricciones
	private double[] settlementUsageChargesCOP; // valor cargos por uso
	private double[] settlementEnergyMarket; // liquidación del mercado de energía
	
	// liquidación nodal
	private double[] nodContractEnergyPurchasesPCMWh; // magnitud compras en contratos pague lo contratado en el mercado nodal
	private double[] nodContractEnergyPurchasesPCCOP; // valor compras en contratos pague lo contratado en el mercado nodal
	private double[] nodContractMaxEnergyPurchasesPDMWh; // máxima magnitud compras en contratos pague lo demandado en el mercado nodal
	private double[] nodContractEnergyPurchasesPDMWh; // magnitud compras en contratos pague lo demandado en el mercado nodal
	private double[] nodContractEnergyPurchasesPDCOP; // valor compras en contratos pague lo demandado en el mercado nodal
	private double[] nodPoolEnergyPurchasesMWh; // magnitud compras en bolsa en el mercado nodal
	private double[] nodPoolEnergyPurchasesCOP; // valor compras en bolsa en el mercado nodal
	private double[] nodPoolEnergySalesMWh; // magnitud ventas en bolsa en el mercado nodal
	private double[] nodPoolEnergySalesCOP; // valor ventas en bolsa en el mercado nodal
	private double[] nodSettlementEnergyMarket; // liquidación del mercado de energía en el mercado nodal
	private double[] nodSettlementCongestionCOP; // valor rentas por congestión
	private double[] nodSettlementUsageChargesCOP; // valor cargos por uso
	private double[] nodSettlementComplementaryChargesCOP; // cargos complementarios mercado nodal
	
	// mercado nodal con ftrs
	private double[][] ftrReservePrice; // precio de reserva para la subasta de FTRs
	private double ftrRiskAversion; 	// proporción de aversión al riesgo
	private List<List<Ftr>> ftrAssignedRights; // lista de Ftrs asignados en la subasta
	private double[] ftrContractEnergyPurchasesPCMWh; // magnitud compras en contratos pague lo contratado en el mercado nodal con FTRs
	private double[] ftrContractEnergyPurchasesPCCOP; // valor compras en contratos pague lo contratado en el mercado nodal con FTRs
	private double[] ftrContractMaxEnergyPurchasesPDMWh; // máxima magnitud compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrContractEnergyPurchasesPDMWh; // magnitud compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrContractEnergyPurchasesPDCOP; // valor compras en contratos pague lo demandado en el mercado nodal con FTRs
	private double[] ftrPoolEnergyPurchasesMWh; // magnitud compras en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergyPurchasesCOP; // valor compras en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergySalesMWh; // magnitud ventas en bolsa en el mercado nodal con FTRs
	private double[] ftrPoolEnergySalesCOP; // valor ventas en bolsa en el mercado nodal con FTRs
	private double[] ftrSettlementEnergyMarket; // liquidación del mercado de energía en el mercado nodal con FTRs
	private double[] ftrSettlementCongestionCOP; // valor rentas por congestión en el mercado nodal con FTRs
	private double[] ftrSettlementUsageChargesCOP; // valor cargos por uso en el mercado nodal con FTRs
	private double[] ftrSettlementComplementaryChargesCOP; // cargos complementarios mercado nodal con FTRs
	private double[] ftrFtrSettlementCOP; // pagos por los FTRs asignados en la subasta
	private double[] ftrFtrIncomeSettlementCOP; // ingresos/egresos por los FTRs adquiridos en la subasta
		
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
			double[] referenceDemand, double   referenceAverageDemand, double[] referenceContractsPrice, double ftrRiskAversion, int position){
		this.retId = retID;
		this.retailerName 	= retailerName;
		this.demandNode		= demandNode;
		this.retailerCod 		= retailerCod;
		this.contractsProportion = contractsProportion; 
		this.contractEnergy = contractEnergy;
		this.referenceDemand = referenceDemand;
		this.referenceAverageDemand = referenceAverageDemand;
		this.referenceContractsPrice = referenceContractsPrice;
		this.ftrRiskAversion = ftrRiskAversion;
		this.position = position; 
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
	public double[] getServedDemand(){return this.servedDemand;} 	// obtener la demanda de energía efectívamente atendida para las 24 horas del día
	public double[] getUnservedDemand(){ return this.unservedDemand;} 	// obtener la demanda de energía no atendida para las 24 horas del día
	public List<GenerationContract> getGenerationContracts(){return this.generationContracts;} // obtener la lista de contratos de generación del comercializador
	public List<GenerationContract> getGenerationContractsPC(){return this.generationContractsPC;} // obtener la lista de contratos de generación pague lo contratado del comercializador
	public List<GenerationContract> getGenerationContractsPD(){return this.generationContractsPD;} // obtener la lista de contratos de generación pague lo demandado del comercializador
	public List<List<GenerationContract>> getSortGenerationContractsPD(){return this.sortGenerationContractsPD;} // obtener la lista organizada de contratos PD
	public List<List<GenerationContract>> getSortDispatchedGenerationContractsPD(){return this.sortDispatchedGenerationContractsPD;} // obtener la lista organizada de contratos PD despachados
	public double[] getContractsProportion(){return this.contractsProportion;} // obtener la proporción de la energía en contratos
	public double[] getContractEnergy(){return this.contractEnergy;} // obtener la energía contratada
	public double[] getReferenceDemand(){return this.referenceDemand;} // obtener la demanda de referencia para los contratos
	public double[] getReferenceContractsPrice(){return this.referenceContractsPrice;} // obtener el precio de referencia para los contratos
	
	//---------------------------------------------------------------------------------- MERCADO UNINODAL --------------------------------------------------------------------------------------//
	public double[] getContractEnergyPurchasesPCMWh(){return this.contractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado
	public double[] getContractEnergyPurchasesPCCOP(){return this.contractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado
	public double[] getContractMaxEnergyPurchasesPDMWh(){return this.contractMaxEnergyPurchasesPDMWh;} // obtener la máxima magnitud de las compras en contratos pague lo demandado
	public double[] getContractEnergyPurchasesPDMWh(){return this.contractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado
	public double[] getContractEnergyPurchasesPDCOP(){return this.contractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado
	public double[] getPoolEnergyPurchasesMWh(){return this.poolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa
	public double[] getPoolEnergyPurchasesCOP(){return this.poolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa
	public double[] getPoolEnergySalesMWh(){return this.poolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa
	public double[] getPoolEnergySalesCOP(){return this.poolEnergySalesCOP;} // obtener el valor de las ventas en bolsa
	public double[] getConstraintsCOP(){return this.constraintsCOP;} // obtener el valor de las restricciones
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;} // obtener la liquidación de los cargos por uso
	public double[] getSettlementEnergyMarket(){return this.settlementEnergyMarket;} // obtener las liquidación del mercado de energía
	
	//--------------------------------------------------------------------------------- MERCADO NODAL --------------------------------------------------------------------------------------------//
	public double[] getNodContractEnergyPurchasesPCMWh(){return this.nodContractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado en el mercado nodal
	public double[] getNodContractEnergyPurchasesPCCOP(){return this.nodContractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado en el mercado nodal
	public double[] getNodContractMaxEnergyPurchasesPDMWh(){return this.nodContractMaxEnergyPurchasesPDMWh;} // obtener la máxima magnitud de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodContractEnergyPurchasesPDMWh(){return this.nodContractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodContractEnergyPurchasesPDCOP(){return this.nodContractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado en el mercado nodal
	public double[] getNodPoolEnergyPurchasesMWh(){return this.nodPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en el mercado nodal
	public double[] getNodPoolEnergyPurchasesCOP(){return this.nodPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en el mercado nodal
	public double[] getNodPoolEnergySalesMWh(){return this.nodPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en el mercado nodal
	public double[] getNodPoolEnergySalesCOP(){return this.nodPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en el mercado nodal
	public double[] getNodSettlementEnergyMarket(){return this.nodSettlementEnergyMarket;} // obtener las liquidación del mercado de energía en el mercado nodal
	public double[] getNodSettlementCongestionCOP(){return this.nodSettlementCongestionCOP;} // obtener la liquidación de las rentas por congestión
	public double[] getNodSettlementUsageChargesCOP(){return this.nodSettlementUsageChargesCOP;} // obtener la liquidación de los cargos por uso 
	public double[] getNodSettlementComplementaryChargesCOP(){return this.nodSettlementComplementaryChargesCOP;} // obtener la liquidación de los cargos complementarios 
		
	//--------------------------------------------------------------------------------- MERCADO NODAL CON FTRs --------------------------------------------------------------------------------------------//
	public double[][] getFtrReservePrice(){return this.ftrReservePrice;} // obtener los precios de reserva por contrato y por hora para la subasta de FTRs
	public double getFtrRiskAversion(){return this.ftrRiskAversion;} // obtener la proporción de aversión al riesgo en el mercado de FTRs
	public List<List<Ftr>> getFtrAssignedRights(){return this.ftrAssignedRights;} // obtener la lista de FTRs asignados en la subasta
	public double[] getFtrContractEnergyPurchasesPCMWh(){return this.ftrContractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos pague lo contratado en el mercado nodal con FTRs
	public double[] getFtrContractEnergyPurchasesPCCOP(){return this.ftrContractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos pague lo contratado en el mercado nodal con FTRs
	public double[] getFtrContractMaxEnergyPurchasesPDMWh(){return this.ftrContractMaxEnergyPurchasesPDMWh;} // obtener la máxima magnitud de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrContractEnergyPurchasesPDMWh(){return this.ftrContractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrContractEnergyPurchasesPDCOP(){return this.ftrContractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public double[] getFtrPoolEnergyPurchasesMWh(){return this.ftrPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergyPurchasesCOP(){return this.ftrPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergySalesMWh(){return this.ftrPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en el mercado nodal con FTRs
	public double[] getFtrPoolEnergySalesCOP(){return this.ftrPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en el mercado nodal con FTRs
	public double[] getFtrSettlementEnergyMarket(){return this.ftrSettlementEnergyMarket;} // obtener las liquidación del mercado de energía en el mercado nodal con FTRs
	public double[] getFtrSettlementCongestionCOP(){return this.ftrSettlementCongestionCOP;} // obtener la liquidación de las rentas por congestión en el mercado nodal con FTRs
	public double[] getFtrSettlementUsageChargesCOP(){return this.ftrSettlementUsageChargesCOP;} // obtener la liquidación de los cargos por uso en el mercado nodal con FTRs
	public double[] getFtrSettlementComplementaryChargesCOP(){return this.ftrSettlementComplementaryChargesCOP;} // obtener la liquidación de los cargos complementarios en el mercado nodal con FTRs
	public double[] getFtrSettlementCOP(){return this.ftrFtrSettlementCOP;} // obtener el valor de los pagos por los FTRs asignados en la subasta
	public double[] getFtrIncomeSettlementCOP(){return this.ftrFtrIncomeSettlementCOP;} // obtener el valor de ingresos/egresos por los FTRs asignados en la subasta
	
	// Set methods
	public void setRetailerName(String retailerName){this.retailerName  = retailerName;} // establecer el nombre del comercializador
	public void setRetailerCod(String retailerCod){this.retailerCod  = retailerCod;} // establecer el código del comercializador
	public void setDemandNode(Node demandNode){ this.demandNode  = demandNode;}	// establecer el nodo de demanda del comercializador
	public void setEnergyDemand(double[] energyDemand){this.energyDemand = energyDemand;} 	// establecer la demanda de energía para las 24 horas del día
	public void setServedDemand(double[] servedDemand){this.servedDemand = servedDemand;} 	// establecer la demanda de energía efectívamente atendida para las 24 horas del día
	public void setUnservedDemand(double[] unservedDemand){this.unservedDemand = unservedDemand;} 	// establecer la demanda de energía no atendida para las 24 horas del día
	
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
	
	//---------------------------------------------------------------------------------- MERCADO UNINODAL --------------------------------------------------------------------------------------//
	public void setContractEnergyPurchasesPCMWh(double[] contractEnergyPurchasesPCMWh){this.contractEnergyPurchasesPCMWh = contractEnergyPurchasesPCMWh;} // establecer la magnitud de las compras en en contratos pague lo contratado
	public void setContractEnergyPurchasesPCCOP(double[] contractEnergyPurchasesPCCOP){this.contractEnergyPurchasesPCCOP = contractEnergyPurchasesPCCOP;} // establecer el valor de las compras en contratos pague lo contratado
	public void setContractMaxEnergyPurchasesPDMWh(double[] contractMaxEnergyPurchasesPDMWh){this.contractMaxEnergyPurchasesPDMWh = contractMaxEnergyPurchasesPDMWh;} // establecer la máxima magnitud de las compras en contratos pague lo demandado
	public void setContractEnergyPurchasesPDMWh(double[] contractEnergyPurchasesPDMWh){this.contractEnergyPurchasesPDMWh = contractEnergyPurchasesPDMWh;} // establecer la maginitud de las compras en en contratos pague lo demandado
	public void setContractEnergyPurchasesPDCOP(double[] contractEnergyPurchasesPDCOP){this.contractEnergyPurchasesPDCOP = contractEnergyPurchasesPDCOP;} // establecer el valor de las compras en contratos pague lo demandado
	public void setPoolEnergyPurchasesMWh(double[] PoolEnergyPurchasesMWh){this.poolEnergyPurchasesMWh = PoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa
	public void setPoolEnergyPurchasesCOP(double[] PoolEnergyPurchasesCOP){this.poolEnergyPurchasesCOP = PoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa
	public void setPoolEnergySalesMWh(double[] PoolEnergySalesMWh){this.poolEnergySalesMWh = PoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa
	public void setPoolEnergySalesCOP(double[] PoolEnergySalesCOP){this.poolEnergySalesCOP = PoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa
	public void setConstraintsCOP(double[] constraintsCOP){this.constraintsCOP = constraintsCOP;} // establecer el valor de las restricciones
	public void setSettlementUsageChargesCOP(double[] settlementUsageChargesCOP){this.settlementUsageChargesCOP = settlementUsageChargesCOP;} // establecer la liquidación de los cargos por uso
	public void setSettlementEnergyMarket(double[] settlementEnergyMarket){this.settlementEnergyMarket = settlementEnergyMarket;} // establecer la liquidación dle mercado de energía
	
	//--------------------------------------------------------------------------------- MERCADO NODAL --------------------------------------------------------------------------------------------//
	public void setNodContractEnergyPurchasesPCMWh(double[] contractEnergyPurchasesPCMWh){this.nodContractEnergyPurchasesPCMWh = contractEnergyPurchasesPCMWh;} // establecer la magnitud de las compras en en contratos pague lo contratado en el mercado nodal
	public void setNodContractEnergyPurchasesPCCOP(double[] contractEnergyPurchasesPCCOP){this.nodContractEnergyPurchasesPCCOP = contractEnergyPurchasesPCCOP;} // establecer el valor de las compras en contratos pague lo contratado en el mercado nodal
	public void setNodContractMaxEnergyPurchasesPDMWh(double[] contractMaxEnergyPurchasesPDMWh){this.nodContractMaxEnergyPurchasesPDMWh = contractMaxEnergyPurchasesPDMWh;} // establecer la máxima magnitud de las compras en contratos pague lo demandado en el mercado nodal
	public void setNodContractEnergyPurchasesPDMWh(double[] contractEnergyPurchasesPDMWh){this.nodContractEnergyPurchasesPDMWh = contractEnergyPurchasesPDMWh;} // establecer la maginitud de las compras en en contratos pague lo demandado en el mercado nodal
	public void setNodContractEnergyPurchasesPDCOP(double[] contractEnergyPurchasesPDCOP){this.nodContractEnergyPurchasesPDCOP = contractEnergyPurchasesPDCOP;} // establecer el valor de las compras en contratos pague lo demandado en el mercado nodal
	public void setNodPoolEnergyPurchasesMWh(double[] PoolEnergyPurchasesMWh){this.nodPoolEnergyPurchasesMWh = PoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa en el mercado nodal
	public void setNodPoolEnergyPurchasesCOP(double[] PoolEnergyPurchasesCOP){this.nodPoolEnergyPurchasesCOP = PoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa en el mercado nodal
	public void setNodPoolEnergySalesMWh(double[] PoolEnergySalesMWh){this.nodPoolEnergySalesMWh = PoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa en el mercado nodal
	public void setNodPoolEnergySalesCOP(double[] PoolEnergySalesCOP){this.nodPoolEnergySalesCOP = PoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa en el mercado nodal
	public void setNodSettlementEnergyMarket(double[] settlementEnergyMarket){this.nodSettlementEnergyMarket = settlementEnergyMarket;} // establecer la liquidación dle mercado de energía en el mercado nodal
	public void setNodSettlementCongestionCOP(double[] nodSettlementCongestionCOP){this.nodSettlementCongestionCOP = nodSettlementCongestionCOP;} // establecer la liquidación de las rentas por congestión
	public void setNodSettlementUsageChargesCOP(double[] nodSettlementUsageChargesCOP){this.nodSettlementUsageChargesCOP = nodSettlementUsageChargesCOP;} // establecer la liquidación de los cargos por uso 
	public void setNodSettlementComplementaryChargesCOP(double[] nodSettlementComplementaryChargesCOP){this.nodSettlementComplementaryChargesCOP = nodSettlementComplementaryChargesCOP;} // establecer la liquidación de los cargos complementarios 
	
	//--------------------------------------------------------------------------------- MERCADO NODAL CON FTRs --------------------------------------------------------------------------------------------//
	public void setFtrReservePrice(double[][] ftrReservePrice){this.ftrReservePrice = ftrReservePrice;} // establecer los precios de reserva por contrato y por hora para la subasta de FTRs
	public void setFtrRiskAversion(double ftrRiskAversion){this.ftrRiskAversion = ftrRiskAversion;} // establecer la proporción de aversión al riesgo en el mercado de FTRs
	public void setFtrAssignedRights(List<List<Ftr>> ftrAssignedRights){this.ftrAssignedRights = ftrAssignedRights;} // establecer la lista de FTRs asignados en la subasta
	public void setFtrContractEnergyPurchasesPCMWh(double[] contractEnergyPurchasesPCMWh){this.ftrContractEnergyPurchasesPCMWh = contractEnergyPurchasesPCMWh;} // establecer la magnitud de las compras en en contratos pague lo contratado en el mercado nodal con FTRs
	public void setFtrContractEnergyPurchasesPCCOP(double[] contractEnergyPurchasesPCCOP){this.ftrContractEnergyPurchasesPCCOP = contractEnergyPurchasesPCCOP;} // establecer el valor de las compras en contratos pague lo contratado en el mercado nodal con FTRs
	public void setFtrContractMaxEnergyPurchasesPDMWh(double[] contractMaxEnergyPurchasesPDMWh){this.ftrContractMaxEnergyPurchasesPDMWh = contractMaxEnergyPurchasesPDMWh;} // establecer la máxima magnitud de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrContractEnergyPurchasesPDMWh(double[] contractEnergyPurchasesPDMWh){this.ftrContractEnergyPurchasesPDMWh = contractEnergyPurchasesPDMWh;} // establecer la maginitud de las compras en en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrContractEnergyPurchasesPDCOP(double[] contractEnergyPurchasesPDCOP){this.ftrContractEnergyPurchasesPDCOP = contractEnergyPurchasesPDCOP;} // establecer el valor de las compras en contratos pague lo demandado en el mercado nodal con FTRs
	public void setFtrPoolEnergyPurchasesMWh(double[] PoolEnergyPurchasesMWh){this.ftrPoolEnergyPurchasesMWh = PoolEnergyPurchasesMWh;} // establecer la magnitud de las compras en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergyPurchasesCOP(double[] PoolEnergyPurchasesCOP){this.ftrPoolEnergyPurchasesCOP = PoolEnergyPurchasesCOP;} // establecer el valor de las compras en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergySalesMWh(double[] PoolEnergySalesMWh){this.ftrPoolEnergySalesMWh = PoolEnergySalesMWh;} // establecer la magnitud de las ventas en bolsa en el mercado nodal con FTRs
	public void setFtrPoolEnergySalesCOP(double[] PoolEnergySalesCOP){this.ftrPoolEnergySalesCOP = PoolEnergySalesCOP;} // establecer el valor de las ventas en bolsa en el mercado nodal con FTRs
	public void setFtrSettlementEnergyMarket(double[] ftrSettlementEnergyMarket){this.ftrSettlementEnergyMarket = ftrSettlementEnergyMarket;} // establecer la liquidación dle mercado de energía en el mercado nodal con FTRs
	public void setFtrSettlementCongestionCOP(double[] ftrSettlementCongestionCOP){this.ftrSettlementCongestionCOP = ftrSettlementCongestionCOP;} // establecer la liquidación de las rentas por congestión en el mercado nodal con FTRs
	public void setFtrSettlementUsageChargesCOP(double[] ftrSettlementUsageChargesCOP){this.ftrSettlementUsageChargesCOP = ftrSettlementUsageChargesCOP;} // establecer la liquidación de los cargos por uso en el mercado nodal con FTRs
	public void setFtrSettlementComplementaryChargesCOP(double[] ftrSettlementComplementaryChargesCOP){this.ftrSettlementComplementaryChargesCOP = ftrSettlementComplementaryChargesCOP;} // establecer la liquidación de los cargos complementarios en el mercado nodal con FTRs 
	public void setFtrSettlementCOP(double[] ftrFtrSettlementCOP){this.ftrFtrSettlementCOP = ftrFtrSettlementCOP;} // establecer el valor de los pagos por los FTRs asignados en la subasta
	public void setFtrIncomeSettlementCOP(double[] ftrFtrIncomeSettlementCOP){this.ftrFtrIncomeSettlementCOP = ftrFtrIncomeSettlementCOP;} // establecer el valor de ingresos/egresos por los FTRs asignados en la subasta
	
	///---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// función para calcular el precio de reserva del comercializador
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public double[][] calculateFtrReservePrice(int iteration, Operator operator){
		// variables auxiliares
		double[] nodalPriceDifferences; // diferencias de precios nodales
		double[][] ftrReservePrice; 	// precio de reserva
		try{
			if(this.getGenerationContracts().isEmpty() != true)
			{
				ftrReservePrice = new double[this.getGenerationContracts().size()][24];
				for(int contract = 0; contract < this.getGenerationContracts().size(); contract++)
				{
					for(int hour = 0; hour < 24; hour++)
					{
						// inicialización del vector de diferencias de precios nodales
						nodalPriceDifferences = new double[Global.nlags];
						for(int i = 0; i < Global.nlags; i++)
						{
							// cálculo del vector de diferencias de precios nodales
							nodalPriceDifferences[i] = 
									operator.getHistoricalNodalPrices().get(iteration - i)[this.getGenerationContracts().get(contract).getWithdrawalNodeId()*24 + hour] 
									- operator.getHistoricalNodalPrices().get(iteration - i)[this.getGenerationContracts().get(contract).getSourceNodeId()*24 + hour];
						
						}
						// ftrReservePrice es igual a la media de las pasadas
						// Global.nlags diferencias de precios nodales entre los nodos de destino y origen en cada contrato 
						ftrReservePrice[contract][hour] = MathFuns.Mean(nodalPriceDifferences);
					}
				}
			}
			else {
				ftrReservePrice = new double[1][24];
			}
			this.ftrReservePrice = ftrReservePrice;
		}
		catch(Exception e)
		{
            System.out.println("comercializador: calculateFtrReservePrice ->"+e);
        }
		return this.ftrReservePrice;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// función para construir una oferta horaria por FTRs en la subasta de FTRs
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	public List<Ftr> ftrSetHourlyBids(int iteration, int h, int round, Operator operator){
		// variables auxiliares
		List<Ftr> ftrHourlyBids = new ArrayList<Ftr>(); 			// lista de ofertas horarias por FTRs
		List<Double> ftrHourlyBidPower = new ArrayList<Double>(); 	// lista de ofertas horarias de energía en los FTRs
		try{
			int ftrIndex = 0; 
			for(int contract = 0; contract < this.getGenerationContracts().size(); contract++)
			{
				// solo se oferta por los FTRs que tengan un valor mayor que cero
				if(operator.getFtrProductPrice()[h] > Global.ftrOperatorMinimumPrice)
				{
					// sólo se oferta en los contratos que tenga una generación comprometida mayor que cero
					if(this.getGenerationContracts().get(contract).getContractPower()[h] > 0.0)
					{
						if(operator.getFtrProductPrice()[h] <= this.getFtrReservePrice()[contract][h])
						{
							// cálculo de la energía en el FTR segun la proporción de aversión al riesgo
							ftrHourlyBidPower.add(ftrIndex, Math.ceil(this.getFtrRiskAversion() * this.getGenerationContracts().get(contract).getContractPower()[h]));
							
							// construcción de un FTR horario
							ftrHourlyBids.add(	new Ftr(this,
												this.getGenerationContracts().get(contract),
												ftrHourlyBidPower.get(ftrIndex),
												this.getFtrReservePrice()[contract][h],
												this.getGenerationContracts().get(contract).getSourceNode(),
												this.getGenerationContracts().get(contract).getWithdrawalNode()));
							ftrIndex = ftrIndex + 1; 							
						}
					}
				}
			}
		}
		catch(Exception e)
		{
	        System.out.println("comercializador: ftrSetHourlyBids ->"+e);
	    }
		return ftrHourlyBids;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// imprimir las características de cada comercializador
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
