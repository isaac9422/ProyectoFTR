package agents;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.AbstractDocument.Content;

import org.jfree.chart.ChartPanel;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;



import utilities.ArrayFactory;
import utilities.ChargeInformation;
import market.DailyIdealDispatch;
import market.DailyIdealDispatchN;
import market.DailyRealDispatch;
import market.Dispatch;
import market.GenerationContract;
import market.GenerationUnit;
import utilities.Global;
import market.PowerBid;
import utilities.ReadWrite;
import market.TransmissionLine;
import jxl.Sheet;
import jxl.write.WritableSheet;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;

public class Operator {
		
	// variables dle mercado
	private List<Generator> generators = new ArrayList<Generator>(Global.nGencos);	// lista de generadores
	private List<Retailer> retailers = new ArrayList<Retailer>(Global.nRetailers);	// lista de comercializadores
	private List<Transmitter> transmitters = new ArrayList<Transmitter>(Global.nGridcos); 	// lista de transmisores
	private List<GenerationUnit> generationUnits = new ArrayList<GenerationUnit>(Global.nUnits); // lista of unidades de generación
	private List<TransmissionLine> transmissionLines = new ArrayList<TransmissionLine>(Global.nLines);	// lista de líneas de transmisión
	private List<GenerationContract> generationContracts = new ArrayList<GenerationContract>(Global.nContracts);   // lista de contratos de generación
	
	
	private List<PowerBid> DailyPowerBids = new ArrayList<PowerBid>(Global.nUnits);	// list of daily power bids
	
	// liquidación mercado
	private double[] energyDemand;	// demanda de energía total para cada hora
	private double[] idealGeneration;	// generación ideal total
	private double[] realGeneration; 	// generación real total
	private double[] positiveReconciliationMWh; // magnitud reconciliación positiva total para cada hora
	private double[] positiveReconciliationCOP; // valor reconciliación positiva total para cada hora
	private double[] negativeReconciliationMWh; // magnitud reconciliación negativa total para cada hora
	private double[] negativeReconciliationCOP; // valor reconciliación negativa total para cada hora
	private double[] constraintsCOP; // valor restricciones totales para cada hora
	private double referencePricePositiveReconciliation; // precio de referencia para las reconciliaciones positivas de la plantas térmicas
	private double[]  dailyPositiveReconciliationMWh; // magnitud reconciliación positiva total de un dia
	private double[][] usageChargesCOP_MWh; // cargos por uso del sistema de transmisión nacional
	private double[] settlementUsageChargesCOP; // liquidación cargos por uso del sistema de transmisión nacional por hora
	private double[] settlementUsageChargesRetailersCOP; // liquidación cargos por uso del sistema de transmisión nacional por hora comercializadores
	private double[] settlementUsageChargesGeneratorsCOP; // liquidación cargos por uso del sistema de transmisión nacional por hora generadores
	private double proportionUsageChargesDemand; // proporción de los cargos por uso liquidados a la demanda
	
	// variables para el despacho real
	public double[][] generationR = new double[Global.nUnits][24];		// hourly power generation by unit
	public double[][] generationCloneR = new double[Global.nUnits][24]; // clone of hourly power generation by unit
	public double[][] voltageAnglesR = new double[Global.nNodes][24]; // ángulos de voltage para cada nodo por hora
	private double[][] powerDemandR = new double[Global.nNodes][24];	// hourly power demand by retailer
	public double[][] powerDemandCloneR = new double[Global.nNodes][24];// clone of hourly power demand by retailer
	private double[][] nodalPricesR = new double[Global.nNodes][24];	// hourly nodal prices by node
	private double[][] powerFlowsR = new double[Global.nLines][24];		// hourly power flows by line
	private double[][] remainderCapacityR = new double[Global.nLines][24];		// hourly remainder capacity
	private double[][] unservedDemandR = new double[Global.nNodes][24];	// hourly unserved power demand by node
	private double dispatchCostR	= 0.0;								// daily dispatch cost
	
	// variables para el despacho ideal
	private double[][] generationI = new double[Global.nUnits][24];		// hourly power generation by unit
	public double[][] generationCloneI = new double[Global.nUnits][24];	// clone of hourly power generation by unit
	public double[][] voltageAnglesI = new double[Global.nNodes][24]; // ángulos de voltage para cada nodo por hora
	private double[][] powerDemandI = new double[Global.nNodes][24];	// hourly power demand by retailer
	public double[][] powerDemandCloneI = new double[Global.nNodes][24];// clone of hourly power demand by retailer
	private double[] spotPricesI = new double[24];	// precios spot horarios
	private double[][] nodalPricesI = new double[Global.nNodes][24];	// hourly nodal prices by node
	private double[][] powerFlowsI = new double[Global.nLines][24];		// hourly power flows by line
	private double[][] remainderCapacityI = new double[Global.nLines][24];		// hourly remainder capacity
	private double[][] unservedDemandI = new double[Global.nNodes][24];	// hourly unserved power demand by node
	private double dispatchCostI	= 0.0; 								// daily dispatch cost
	
	// despachos
	public Dispatch realDispatch	= new Dispatch(generationR, voltageAnglesR, powerDemandR, nodalPricesR, powerFlowsR, remainderCapacityR, unservedDemandR, dispatchCostR);		// real dispatch
	public Dispatch idealDispatch 	= new Dispatch(generationI, voltageAnglesI, powerDemandI, spotPricesI, nodalPricesI, powerFlowsI, remainderCapacityI, unservedDemandI, dispatchCostI);		// ideal dispatch
	
	// liquidación
	private List<GenerationContract> generationContractsPC = new ArrayList<GenerationContract>();   // lista de contratos de generación PC
	private List<GenerationContract> generationContractsPD = new ArrayList<GenerationContract>();   // lista de contratos de generación PD
	private List<List<GenerationContract>> dispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>();   // lista de contratos de generación PD despachados
	private List<List<GenerationContract>> sortDispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>();   // lista de contratos de generación PD despachados organizada
	
	
	// settlement the both nodal and uninodal market
	private double[][] contractSettlementGeneration = new double[Global.nContracts][24]; // settlement of contracts for generation companies
	private double[][] contractSettlementDemand 	= new double[Global.nContracts][24]; // settlement of contracts for retailers
	private double[][] poolGeneration				= new double[Global.nContracts][24];
	private double[][] poolDemand					= new double[Global.nRetailers][24];
	
	// settlement nodal market
	private double[][] nPoolPurSetGen = new double[Global.nContracts][24]; // settlement of power purchases of generation companies in the pool
	private double[][] nPoolSalSetGen = new double[Global.nUnits][24]; 	// settlement of power sales of generation companies in the pool
	private double[][] nPoolPurSetDem = new double[Global.nRetailers][24]; // settlement of power purchases of retailers in the pool
	private double[][] nPoolSalSetDem = new double[Global.nRetailers][24]; // settlement of power sales of retailers in the pool
	private double[][] nGridcosIncome = new double[Global.nGridcos][24]; // settlement of transmitters
	private double[][] transmissionSetByUnit; //= new double[Global.nUnits][24]; // congestion rents payed by the generators
	//private double[][] nCongesRentDem; // = new double[Global.nRetailers][24]; // congestion rents payed by the retailers
	private double[][] nCongestRents  = new double[Global.nContracts][24]; // total congestion rents
	private double nCongestRentsProp  = 0.5;
	
	private ArrayFactory factory = new ArrayFactory();
	
	// empty constructor
	public Operator(){}
	
	// constructor: generators 
	public Operator(List<Generator> generators, List<Retailer> retailers,
					List<Transmitter> transmitters, List<GenerationUnit> generationUnits,
					List<TransmissionLine> transmissionLines, List<GenerationContract> generationContracts ){
		this.generators 		= generators;
		this.retailers 			= retailers;
		this.transmitters 		= transmitters;
		this.generationUnits 	= generationUnits;
		this.transmissionLines 	= transmissionLines;
		this.generationContracts = generationContracts;			
	}
	
	// get methods
	public List<GenerationUnit> getGenerationUnits(){return this.generationUnits; } // obtener la lista de unidades de generación
	public List<Generator> getGenerators() {return this.generators;} // obtener las lista de generadores
	public List<Retailer> getRetailers() {return this.retailers;} // obtener la lista de comercializadores
	public List<Transmitter> getTransmitters() {return this.transmitters;} // obtener la lista de transmisores
	public List<TransmissionLine> getTransmissionLines() {return this.transmissionLines;} // obtener la lista de líneas de transmissión
	public Dispatch getRealDispatch(){return this.realDispatch; } // obtener el despacho real
	public Dispatch getIdealDispatch(){return this.idealDispatch; } // obtener el despacho ideal
	public List<GenerationContract> getGeneraionContracts() {return this.generationContracts;} // obtener la lista de contratos de generación
	public List<GenerationContract> getGeneraionContractsPC() {return this.generationContractsPC;} // obtener los contratos de generación PC
	public List<GenerationContract> getGeneraionContractsPD() {return this.generationContractsPD;} // obtener los contratos de generación PD
	public List<List<GenerationContract>> getDispatchedGenerationContractsPD() {return this.dispatchedGenerationContractsPD;} // obtener los contratos PD que han sido despachados
	public List<List<GenerationContract>> getSortDispatchedGenerationContractsPD() {return this.sortDispatchedGenerationContractsPD;} // obtener los contratos PD organizados que han sido despachados
	public double getReferencePricePositiveReconciliation() { return this.referencePricePositiveReconciliation;} // obtener precio de referencia para las reconciliaciones positivas de la plantas térmicas
	public double[][] getUsageCharges(){return this.usageChargesCOP_MWh;} // obtener los cargos por uso del sistema de transmisión
	public double getProportionUsageChargesDemand(){ return this.proportionUsageChargesDemand;} // obtener proporción de los cargos por uso liquidados a la demanda
	public double[] getSettlementUsageChargesRetailersCOP(){ return this.settlementUsageChargesRetailersCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora comercializadores
	public double[] getSettlementUsageChargesGeneratorsCOP(){ return this.settlementUsageChargesGeneratorsCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora generadores
	
	// set methods
	public void setGenerationUnits(List<GenerationUnit> generationUnits){ this.generationUnits = generationUnits; } // establcer la lista de unidades de generación
	public void setGenerators(List<Generator> generators) {this.generators = generators;} // establcer las lista de generadores
	public void setRetailers(List<Retailer> retailers) {this.retailers = retailers;} // establcer la lista de comercializadores
	public void setTransmitters(List<Transmitter> transmitters) { this.transmitters = transmitters;} // establecer la lista de transmisores
	public void setTransmissionLines(List<TransmissionLine> transmissionLines) {this.transmissionLines = transmissionLines;} // establecer la lista de líneas de transmissión
	public void setRealDispatch(Dispatch realDispatch){this.realDispatch = realDispatch; } // establecer el despacho real
	public void setIdealDispatch(Dispatch idealDispatch){ this.idealDispatch = idealDispatch; } // establecer el despacho ideal
	public void setGenerationContracts(List<GenerationContract> generationContracts) {this.generationContracts = generationContracts;} // establecer la lista de contratos de generación
	public void setDispatchedGenerationContractsPD(List<List<GenerationContract>> dispatchedGenerationContractsPD) {this.dispatchedGenerationContractsPD = dispatchedGenerationContractsPD;} // establecer la lista de contratos PD que han sido despachados
	public void setSortDispatchedGenerationContractsPD(List<List<GenerationContract>> sortDispatchedGenerationContractsPD) {this.sortDispatchedGenerationContractsPD = sortDispatchedGenerationContractsPD;} // establecer la lista de contratos PD organizados que han sido despachados
	public void setReferencePricePositiveReconciliation(double referencePricePositiveReconciliation) {this.referencePricePositiveReconciliation = referencePricePositiveReconciliation;} // establecer precio de referencia para las reconciliaciones positivas de la plantas térmicas
	public void setUsageCharges(double[][] usageCharges){this.usageChargesCOP_MWh = usageCharges;} // establecer los cargos por uso del sistema de transmisión
	public void setProportionUsageChargesDemand(double proportionUsageChargesDemand){this.proportionUsageChargesDemand = proportionUsageChargesDemand;} // establecer proporción de los cargos por uso liquidados a la demanda
	public void setSettlementUsageChargesRetailersCOP(double[] settlementUsageChargesRetailersCOP){ this.settlementUsageChargesRetailersCOP = settlementUsageChargesRetailersCOP;} // establecer liquidación cargos por uso del sistema de transmisión nacional por hora comercializadores
	public void setSettlementUsageChargesGeneratorsCOP(double[] settlementUsageChargesGeneratorsCOP){ this.settlementUsageChargesGeneratorsCOP = settlementUsageChargesGeneratorsCOP;} // establecer liquidación cargos por uso del sistema de transmisión nacional por hora generadores
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la demanda de energía para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getEnergyDemand(){return this.energyDemand;} // obtener la demanda total del mercado para cada hora
	public void setEnergyDemand() {
		
		// variable auxiliar
		double[] demand = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// demanda de energía para cada hora
				demand[h] = demand[h] + this.retailers.get(ret).getEnergyDemand()[h];
			}
		}
		this.energyDemand = demand;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la generación idela total para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getIdealGeneration(){return this.idealGeneration;} // obtener la generación ideal total para cada hora
	public void setIdealGeneration() {
		
		// variable auxiliar
		double[] idealGeneration = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nUnits; ret++)
			{
				// generación ideal total para cada hora
				idealGeneration[h] = idealGeneration[h] + this.generationUnits.get(ret).getIdealGeneration()[h];
			}
		}
		this.idealGeneration = idealGeneration;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la generación real total para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRealGeneration(){return this.realGeneration;} // obtener la generación real total para cada hora
	public void setRealGeneration() {
		
		// variable auxiliar
		double[] realGeneration = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nUnits; ret++)
			{
				// generación real total para cada hora
				realGeneration[h] = realGeneration[h] + this.generationUnits.get(ret).getRealGeneration()[h];
			}
		}
		this.realGeneration = realGeneration;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las reconciliaciones positivas totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getPositiveReconciliationMWh(){return this.positiveReconciliationMWh;} // obtener la magnitud de las reconciliaciones positivas totales
	public void setPositiveReconciliationMWh() {
		
		// variable auxiliar
		double[] positiveReconciliationMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// magnitud reconciliaciones positivas totales para cada hora
				positiveReconciliationMWh[h] = positiveReconciliationMWh[h] + this.generationUnits.get(unit).getPositiveReconciliationMWh()[h];
			}
		}
		this.positiveReconciliationMWh = positiveReconciliationMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las reconciliaciones positivas totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getPositiveReconciliationCOP(){return this.positiveReconciliationCOP;} // obtener el valor de las reconciliaciones positivas totales
	public void setPositiveReconciliationCOP() {
		
		// variable auxiliar
		double[] positiveReconciliationCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// valor reconciliaciones positivas totales para cada hora
				positiveReconciliationCOP[h] = positiveReconciliationCOP[h] + this.generationUnits.get(unit).getPositiveReconciliationCOP()[h];
			}
		}
		this.positiveReconciliationCOP = positiveReconciliationCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las reconciliaciones negativas totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNegativeReconciliationMWh(){return this.negativeReconciliationMWh;} // obtener la magnitud de las reconciliaciones negativas totales
	public void setNegativeReconciliationMWh() {
		
		// variable auxiliar
		double[] negativeReconciliationMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// magnitud reconciliaciones negativas totales para cada hora
				negativeReconciliationMWh[h] = negativeReconciliationMWh[h] + this.generationUnits.get(unit).getNegativeReconciliationMWh()[h];
			}
		}
		this.negativeReconciliationMWh = negativeReconciliationMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las reconciliaciones negativas totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNegativeReconciliationCOP(){return this.negativeReconciliationCOP;} // obtener el valor de las reconciliaciones negativas totales
	public void setNegativeReconciliationCOP() {
		
		// variable auxiliar
		double[] negativeReconciliationCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// valor reconciliaciones negativas totales para cada hora
				negativeReconciliationCOP[h] = negativeReconciliationCOP[h] + this.generationUnits.get(unit).getNegativeReconciliationCOP()[h];
			}
		}
		this.negativeReconciliationCOP = negativeReconciliationCOP;
	}
		
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las restricciones totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getConstraintsCOP(){return this.constraintsCOP;} // obtener el valor de las restricciones totales
	public void setConstraintsCOP() {
		
		// variable auxiliar
		double[] constraintsCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			// valor restricciones totales para cada hora
			constraintsCOP[h] = Math.max(0,this.getPositiveReconciliationCOP()[h] - this.getNegativeReconciliationCOP()[h]);
		}
		this.constraintsCOP = constraintsCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de la liquidación de cargos por uso del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getSettlementUsageChargesCOP(){return this.settlementUsageChargesCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora
	public void setSettlementUsageChargesCOP(){
		
		// variable auxiliar
		double[] settlementUsageChargesCOP = new double[24];
		double[] settlementUsageChargesRetailersCOP = new double[24];
		double[] settlementUsageChargesGeneratorsCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación cargos por uso del sistema de transmisión nacional por hora
				settlementUsageChargesCOP[h] = settlementUsageChargesCOP[h] + this.transmitters.get(gridco).getSettlementUsageChargesCOP()[h];
			}
			// división de los cargos por uso liquidados a la oferta y a la demanda
			settlementUsageChargesGeneratorsCOP[h] = (1 - this.getProportionUsageChargesDemand())*settlementUsageChargesCOP[h]; 
			settlementUsageChargesRetailersCOP[h] = this.getProportionUsageChargesDemand()*settlementUsageChargesCOP[h];
		}
		this.settlementUsageChargesCOP = settlementUsageChargesCOP;
		this.settlementUsageChargesGeneratorsCOP = settlementUsageChargesGeneratorsCOP;
		this.settlementUsageChargesRetailersCOP = settlementUsageChargesRetailersCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las reconciliaciones negativas totales
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getDailyPositiveReconciliationMWh(){return this.dailyPositiveReconciliationMWh;} // obtener la magnitud de las reconciliaciones positivas diarias
	public void setDailyPositiveReconciliationMWh() {
		
		// variable auxiliar
		double[] idealGeneration;
		double[] realGeneration;
		double[] positiveReconciliationMWh;
		double[] dailyPositiveReconciliationMWh = new double[Global.nUnits];
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
		// inicialización de las varibles auxiliares
			idealGeneration 			= this.generationUnits.get(unit).getIdealGeneration().clone();
			realGeneration 				= this.generationUnits.get(unit).getRealGeneration().clone();
			positiveReconciliationMWh 	= new double[24];
			
			// magnitud de las reconciliaciones positivas
			for(int h = 0; h < 24; h++)
			{
				positiveReconciliationMWh[h] = Math.max(0, realGeneration[h] - idealGeneration[h]);			
			}
			
			// magnitud reconciliaiones positivas diarias por unidad de generación
			for(int h = 0; h < 24; h++)
			{
				dailyPositiveReconciliationMWh[unit] = dailyPositiveReconciliationMWh[unit] + positiveReconciliationMWh[h];
			}
		}
		this.dailyPositiveReconciliationMWh = dailyPositiveReconciliationMWh;
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// establecer la lista de contratos de generación PC
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void setGenerationContractsPC(List<GenerationContract> generationContracts) { 
		for(int contract = 0; contract < generationContracts.size(); contract++)
		{
			if(generationContracts.get(contract).getContractType().equals("PC"))
			{
				this.generationContractsPC.add(generationContracts.get(contract));
			}
		}
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// establecer la lista de contratos de generación PD
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void setGenerationContractsPD(List<GenerationContract> generationContracts) { 
		for(int contract = 0; contract < generationContracts.size(); contract++)
		{
			if(generationContracts.get(contract).getContractType().equals("PD"))
			{
				this.generationContractsPD.add(generationContracts.get(contract));
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para organizar los contratos PD de cada comercializador según el precio del mismo en cada hora 
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void sortContratsByHourlyPrice()
	{
		List<List<GenerationContract>> contractsMatrix;
		List<GenerationContract> contractsVector;
		GenerationContract contractAux;
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			contractsMatrix = new ArrayList<List<GenerationContract>>();			
			for(int h = 0; h < 24; h++)
			{
				contractsVector = new ArrayList<GenerationContract>();
				contractsVector.addAll(this.retailers.get(ret).getGenerationContractsPD());
				
				for(int i = 0; i < contractsVector.size() - 1; i++)
				{
					for(int contract = 0; contract < contractsVector.size() - 1; contract++)
					{
						if(contractsVector.get(contract).getContractPrice()[h]
								> contractsVector.get(contract + 1).getContractPrice()[h])
						{
							contractAux = contractsVector.get(contract);
							contractsVector.set(contract, contractsVector.get(contract + 1));
							contractsVector.set(contract + 1, contractAux);
						}
					}	
				}
				contractsMatrix.add(contractsVector);
			}
			this.retailers.get(ret).setSortGenerationContractsPD(contractsMatrix);
			//System.out.println(this.retailers.get(ret).getRetailerName() + "\t" + this.retailers.get(ret).getDemandNode().getNodeName());
			//Global.rw.printArraySortContracts(this.retailers.get(ret).getSortGenerationContractsPD());
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para organizar los contratos PD de cada comercializador según el precio del mismo en cada hora 
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void sortDispatchedContratsByHourlyPrice()
	{
		List<List<GenerationContract>> contractsMatrix;
		List<GenerationContract> contractsVector;
		GenerationContract contractAux;
		
		contractsMatrix = new ArrayList<List<GenerationContract>>();			
		for(int h = 0; h < 24; h++)
		{
			contractsVector = new ArrayList<GenerationContract>();
			contractsVector.addAll(this.dispatchedGenerationContractsPD.get(h));
			
			for(int i = 0; i < contractsVector.size() - 1; i++)
			{
				for(int contract = 0; contract < contractsVector.size() - 1; contract++)
				{
					if(contractsVector.get(contract).getContractPrice()[h]
							> contractsVector.get(contract + 1).getContractPrice()[h])
					{
						contractAux = contractsVector.get(contract);
						contractsVector.set(contract, contractsVector.get(contract + 1));
						contractsVector.set(contract + 1, contractAux);
					}
				}	
			}
			contractsMatrix.add(contractsVector);
		}
		this.setSortDispatchedGenerationContractsPD(contractsMatrix);
	}
	
	
	
	//public void setPowerDemand(double[][] powerDemand){this.powerDemand = powerDemand;} // set the hourly power demand by node
	//public void setHistoricalNodalPrices( List<double[][]> historicalNodalPrices){this.historicalNodalPrices = historicalNodalPrices;}
		
	// get the cost of generation of all generation units
	public double[] getGenerationCosts(){
		double [] generationCosts = new double[Global.nUnits];
		for (int i = 0; i < Global.nUnits; i++){
			generationCosts[i] = this.generationUnits.get(i).getGenerationCost();
		}
		return generationCosts;
	}
	
	// get the susceptance of all generation units
	public double[] getSusceptances(){
		double [] susceptances = new double[Global.nLines];
		for (int i = 0; i < Global.nLines; i++){
			susceptances[i] = this.transmissionLines.get(i).getSusceptance();
		}
		return susceptances;
	}
	
	// get the power flow limit of all generation units
	public double[] getPowerFlowLimits(){
		double [] powerFlowLimit = new double[this.transmissionLines.size()];
		for (int i = 0; i < this.transmissionLines.size(); i++){
			powerFlowLimit[i] = this.transmissionLines.get(i).getPowerFlowLimit();
		}
		return powerFlowLimit;
	}
	
	
	// set real generation to each generation unit
	/*public void setGeneration2Units(double[][] generation){
		for (int unit = 0; unit < this.generationUnits.size(); unit++){
			this.generationUnits.get(unit).setRealGeneration(generation[unit]);
		}
	}*/
	
	
	// set real power demand to each retailer
	public void setPowerDemand2Retailers(double[][] energyDemand) {
		for (int ret = 0; ret < this.retailers.size(); ret++){
			this.retailers.get(ret).setEnergyDemand(energyDemand[ret]);
		}
	}
	
	// set real nodal prices
	public void setRealNodalPrices(double[] nodalPrices) {
		this.nodalPricesR = factory.vec2mat(nodalPrices,Global.nNodes,24);
	}
	
	/*// print the list of generators
		public void printGenerators(){
			for(int gen = 0; gen < this.generators.size(); gen++){
				for (int bid = 0; bid < this.generators.get(gen).getPowerBids().size(); bid++){
					System.out.println(generators.get(gen).getGeneratorName() + "\t" 
							+ generators.get(gen).getPowerBids().get(bid).getBidPrice() + "\t"
							+ generators.get(gen).getPowerBids().get(bid).getBidPower());	
				}	
			}
		}
	
	// contract generation settlement
	public void contractGenerationSettlement(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// settlement the generation contracts
						this.contractSettlementGeneration[contract][hour] =
								this.generationContracts.get(contract).getContractPrice()[hour] *
								Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
										this.generationContracts.get(contract).getContractPower()[hour]);

						
						// settlement the generation contracts
						/*this.contractSettlementGeneration[contract][hour] =
								this.generationContracts.get(contract).getContractPrice() * 
								Math.min(this.generationUnits.get(unit).getRealGeneration()[hour],
										Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
												this.generationContracts.get(contract).getContractPower()));
						
						// reduce the missing value of programmed generation
						this.generationUnits.get(unit).getRealGeneration()[hour] =
								this.generationUnits.get(unit).getRealGeneration()[hour] -
								Math.min(this.generationUnits.get(unit).getRealGeneration()[hour],
										Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
												this.generationContracts.get(contract).getContractPower()));
						*/
		/*				// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] -
								Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
										this.generationContracts.get(contract).getContractPower()[hour]);
								
					}
				}
			}
		}
		this.setGeneration2Units(this.realDispatch.getGenerationClone());
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	//  calculate the pool purchases for each generation unit in each hour
	public void poolPurchasesSettlementGeneration(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// settlement the power pool
						this.poolGeneration[contract][hour] = 
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] -
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of generation
						this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] =
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
					}
					
					// calculate the pool purchases for each generation unit in each hour
					if(this.poolGeneration[contract][hour] < 0){
						// nodal market
						this.nPoolPurSetGen[contract][hour] = 
								Math.abs(this.poolGeneration[contract][hour]) * 
								this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode() - 1][hour];
						// uninodal market
						/*this.unPoolPurSetGen[contract][hour] = 
								Math.abs(this.poolGeneration[contract][hour]) * 
								this.idealDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode() - 1][hour];        
						*/
/*
					}
				}	
			}
		}
		this.setGeneration2Units(this.realDispatch.getGeneration());
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// calculate the pool sales for each generation unit in each hour
	public void poolSalesSettlementGeneration(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// nodal market
						this.nPoolSalSetGen[unit][hour] = 
								this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
								Math.max(0, 
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
						
						// uninodal market
						/*this.unPoolSalSetGen[unit][hour] = 
								this.idealDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
								Math.max(0,
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
											Math.min(this.generationContracts.get(contract).getContractPower(),
												 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
						*/						
						/*// reduce the missing value of generation
						this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] =
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
					}
				}
				if(this.generationUnits.get(unit).getHaveContracts() == 0){
					
					// nodal market
					this.nPoolSalSetGen[unit][hour] = 
							this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
							this.generationUnits.get(unit).getRealGeneration()[hour];
					
					/* uninodal market
					this.unPoolSalSetGen[unit][hour] = 
							this.idealDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
							this.generationUnits.get(unit).getRealGeneration()[hour];
							*//*
				}
			}
		}
		this.setGeneration2Units(this.generationR);
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// demand settlement: contracts and both pool purchases an sales
	public void demandSettlement(){
		for(int ret = 0; ret < this.retailers.size(); ret++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
						
						// settlement the generation contracts
						this.contractSettlementDemand[contract][hour] = 
								this.generationContracts.get(contract).getContractPrice()[hour] *
								Math.min(this.retailers.get(ret).getPowerDemand()[hour],
								this.generationContracts.get(contract).getContractPower()[hour]);
						
						// reduce the missing value of power demand
						this.retailers.get(ret).getPowerDemand()[hour] =
								this.retailers.get(ret).getPowerDemand()[hour] -
								Math.min(this.retailers.get(ret).getPowerDemand()[hour],
								this.generationContracts.get(contract).getContractPower()[hour]);
					}
				}
				
				// settlement the power pool
				this.poolDemand[ret][hour] = this.retailers.get(ret).getPowerDemand()[hour];

				// calculate the pool sales for each retailer in each hour
				if(this.poolDemand[ret][hour] < 0){
					
					// nodal market
					this.nPoolSalSetDem[ret][hour] = 
							Math.abs(this.poolDemand[ret][hour]) * 
							this.realDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
					
					/*/// uninodal market
					/*this.unPoolSalSetDem[ret][hour] = 
							Math.abs(this.poolDemand[ret][hour]) * 
							this.idealDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
							*/        
				/*
				}
				// calculate the pool purchases for each retailer in each hour
				else {
					// nodal market
					this.nPoolPurSetDem[ret][hour] = 
							this.poolDemand[ret][hour] * 
							this.realDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
					
					/*/// uninodal market
					/*this.unPoolPurSetDem[ret][hour] = 
							this.poolDemand[ret][hour] * 
							this.idealDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
							*/ 
				/*
				}
			}
		}
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// transmission settlement
	public void transmissionSettlement(){
		for(int gridco = 0; gridco < Global.nGridcos; gridco++){
			for(int hour = 0; hour < 24; hour++){
				for(int line = 0; line < Global.nLines; line++){
					if(this.transmitters.get(gridco) == this.transmissionLines.get(line).getLineOwner())
						// nodal market
						this.nGridcosIncome[gridco][hour] = 
								Math.abs((this.realDispatch.getNodalPrices()[this.transmissionLines.get(line).getEndNode()-1][hour] - 
										this.realDispatch.getNodalPrices()[this.transmissionLines.get(line).getSourceNode()-1][hour]) *
										this.realDispatch.getFlows()[line][hour]);
						
						
					/*/// uninodal market
						/*this.unGridcosIncome[gridco][hour] = 
							Math.abs((this.idealDispatch.getNodalPrices()[this.transmissionLines.get(line).getEndNode()-1][hour] - 
									this.idealDispatch.getNodalPrices()[this.transmissionLines.get(line).getSourceNode()-1][hour]) *
									this.idealDispatch.getFlows()[line][hour]);
									*//*
				}	
			}
		}
	}
		
	// congestion rents
	public void congestionRents(){
		for(int contract = 0; contract < Global.nContracts; contract++){
			for(int hour = 0; hour < 24; hour++){
				this.nCongestRents[contract][hour] =  
						(this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
						this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
						Math.min(this.generationContracts.get(contract).getContractPower()[hour],
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
				
				this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
						Math.min(this.generationContracts.get(contract).getContractPower()[hour],
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// congestion rents payed by the generation units:  model without FTRs
	public double[][] nFinalSetCongestionRentsGenerators(){
		this.nTransmissionSetByUnit = new double[Global.nUnits][24];
		try{
			for(int unit = 0; unit < Global.nUnits; unit++){
				this.nTransmissionSetUnits[unit] = new CongestRentUnit();
				for(int hour = 0; hour < 24; hour++){
					for(int contract = 0; contract < Global.nContracts; contract++){
						if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
							this.nTransmissionSetByUnit[unit][hour] =
									this.nTransmissionSetByUnit[unit][hour] +
									(1 - this.nCongestRentsProp) *
									((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
									this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
							
							this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							
							/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
									this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower(),
											Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
													*//*
						}
					}
				}
				this.nTransmissionSetUnits[unit].setCongestRentGen(this.nTransmissionSetByUnit[unit]);
				this.nTransmissionSetUnits[unit].setGenerationUnit(this.generationUnits.get(unit));
				//this.congestRentUnits[unit].printCongestRentGen();
			}
		}
		catch (Exception e) {
			System.out.println("operator: nFinalSetCongestionRentsGenerators ->"+e);
		}
		return this.nTransmissionSetByUnit;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator:  model without FTRs
	public double[][] nTansmissionSettlementGen(){
		// array to save the transmission settlement by generator
		this.nTransmissionSetByGen = new double[Global.nGencos][24];
		try{
			// determine the congestion rents by generator
			for(int gen = 0; gen < Global.nGencos; gen++){
				this.nTransmissionSetGens[gen] = new CongestRentGen();
				for(int hour = 0; hour < 24; hour++){
					for(int unit1 = 0; unit1 < this.generators.get(gen).getGenerationUnits().size(); unit1++){
						for(int unit = 0; unit < Global.nUnits; unit++){
							if(this.generators.get(gen).getGenerationUnits().get(unit1) == this.nTransmissionSetUnits[unit].getGenerationUnit())
							this.nTransmissionSetByGen[gen][hour] = nTransmissionSetByGen[gen][hour] + this.nTransmissionSetUnits[unit].getCongestRentGen()[hour];
						}
					}
				}
				this.nTransmissionSetGens[gen].setGenerator(this.generators.get(gen));
				this.nTransmissionSetGens[gen].setCongestRentGen(this.nTransmissionSetByGen[gen]);
				//this.transmissionSetGens[gen].printCongestRentGen();
			}			
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.nTransmissionSetByGen;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator: model without FTRs
	public double[][] nTransmissionSettlementDem(){
		this.nTransmissionSetByRet = new double[Global.nRetailers][24];
		try{
			for(int ret = 0; ret < Global.nRetailers; ret++){
				this.nTransmissionSetRets[ret] = new CongestRentRet();
				for(int hour = 0; hour < 24; hour++){
					for(int contract = 0; contract < Global.nContracts; contract++){
						if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
							this.nTransmissionSetByRet[ret][hour] =
									this.nTransmissionSetByRet[ret][hour] +
									this.nCongestRentsProp *
									((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
									this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
							
							this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							
							/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
									this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower(),
											Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
													*//*
						}
					}
				}
				this.nTransmissionSetRets[ret].setCongestRentRet(this.nTransmissionSetByUnit[ret]);
				this.nTransmissionSetRets[ret].setRetailer(this.retailers.get(ret));
				//this.congestRentUnits[unit].printCongestRentGen();
			}
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.nTransmissionSetByRet;
	}
	
	/* FTRS sort by price
	public List<List<Ftr>> ftrsSortByPrice(List<Ftr> ftrs){
		List<Ftr> auxFtrList = new ArrayList<Ftr>();
		Ftr[][] vec = new Ftr[2][3];
		String auxBidder = "";
		double auxPrice = 0.0;
		double auxPower = 0.0;
		int auxSourceNode = 0;
		int auxEndNode = 0;
		try {
			for(int hour = 0; hour < 24; hour++){
				auxFtrList.clear();
				for(int i = 0; i < ftrs.size(); i++){
					Ftr auxFtr1 = new Ftr(ftrs.get(i).getBidder(),
										  ftrs.get(i).getFtrPrice()[hour],
										  ftrs.get(i).getFtrPower()[hour], 
										  ftrs.get(i).getFtrSourceNode(),
										  ftrs.get(i).getFtrEndNode());
					/*this.auxFtr.setBidder(ftrs.get(i).getBidder());
					this.auxFtr.setHourlyFtrPrice(ftrs.get(i).getFtrPrice()[hour]);
					this.auxFtr.setHourlyFtrPower(ftrs.get(i).getFtrPower()[hour]);
					this.auxFtr.setFtrSourceNode(ftrs.get(i).getFtrSourceNode());
					this.auxFtr.setFtrEndNode(ftrs.get(i).getFtrEndNode());*/
					/*
					auxFtrList.add(auxFtr1);
					//break;
					vec[0][0] = auxFtr1;
					vec[0][1] = auxFtr1;					
				}
				this.listFtrs.add(auxFtrList);	
			}
			vec[0][0].printHourlyFtr(vec[0][0]);
			vec[0][1].printHourlyFtr(vec[0][1]);
			System.gc();
			for(int i = 0; i < listFtrs.size(); i++){
				for(int j = 0; j < listFtrs.get(i).size(); j++){
					for(int k = 0; k < listFtrs.get(i).size() - 1; k++){
						if(listFtrs.get(i).get(k).getHourlyFtrPrice() < listFtrs.get(i).get(k+1).getHourlyFtrPrice()){
							
							// change the bidder name
							auxBidder = listFtrs.get(i).get(k).getBidder();
							listFtrs.get(i).get(k).setBidder(listFtrs.get(i).get(k+1).getBidder());
							listFtrs.get(i).get(k+1).setBidder(auxBidder);
							
							// change the price
							auxPrice = listFtrs.get(i).get(k).getHourlyFtrPrice();
							listFtrs.get(i).get(k).setHourlyFtrPrice(listFtrs.get(i).get(k+1).getHourlyFtrPrice());
							listFtrs.get(i).get(k+1).setHourlyFtrPrice(auxPrice);
							
							// change the power
							auxPower = listFtrs.get(i).get(k).getHourlyFtrPower();
							listFtrs.get(i).get(k).setHourlyFtrPower(listFtrs.get(i).get(k+1).getHourlyFtrPower());
							listFtrs.get(i).get(k+1).setHourlyFtrPower(auxPower);
							
							// change the source node
							auxSourceNode = listFtrs.get(i).get(k).getFtrSourceNode();
							listFtrs.get(i).get(k).setFtrSourceNode(listFtrs.get(i).get(k+1).getFtrSourceNode());
							listFtrs.get(i).get(k+1).setFtrSourceNode(auxSourceNode);
							
							// change the end node
							auxEndNode = listFtrs.get(i).get(k).getFtrEndNode();
							listFtrs.get(i).get(k).setFtrEndNode(listFtrs.get(i).get(k+1).getFtrEndNode());
							listFtrs.get(i).get(k+1).setFtrEndNode(auxEndNode);
						}
						listFtrs.add(ftrs);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("ftrsSortByPrice ->"+e);
		}
		return listFtrs;
	}
	*/
	/*
	// FTRS sort by price
	public Ftr[][] ftrsSort(Ftr[] ftrs){
		try {
			// pass to daily FTRS to hourly FTRs
			for(int hour = 0; hour < 24; hour++){
				for(int i = 0; i < ftrs.length; i++){
					Ftr auxFtr1 = new Ftr(ftrs[i].getBidder(),
							ftrs[i].getFtrPower()[hour],
							ftrs[i].getFtrPrice()[hour],
							ftrs[i].getFtrSourceNode(),
							ftrs[i].getFtrEndNode());
					this.hourlyFtrBids[i][hour] = auxFtr1;					
				}
			}
			// call to garbage collector
			System.gc();
			
			// sort the array of hourly bids of FTRs
			for(int i = 0; i < this.hourlyFtrBids[1].length; i++){
				for(int j = 0; j < this.hourlyFtrBids.length; j++){
					for(int k = 0; k < this.hourlyFtrBids.length - 1; k++){
						if(this.hourlyFtrBids[k][i].getHourlyFtrPrice() < this.hourlyFtrBids[k+1][i].getHourlyFtrPrice()){
							// sort
							auxFtr = this.hourlyFtrBids[k][i];
							this.hourlyFtrBids[k][i] = this.hourlyFtrBids[k+1][i];
							this.hourlyFtrBids[k+1][i] = auxFtr;
						}
					}	
				}
			}
		} catch (Exception e) {
			System.out.println("ftrsSort ->"+e);
		}
		return hourlyFtrBids;
	}
	
	// assign the FTRs to generators and retailers
	public void ftrsAssign(Ftr[][] hourlyFtrBids, double capacityToAuctioning){
		for(int i = 0; i < this.hourlyFtrBids[0].length; i++){
			for(int j = 0; j < this.hourlyFtrBids.length; j++){
				if(this.hourlyFtrBids[j][i].getHourlyFtrPower() <= this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i]){
					this.assignHourlyFtrBids[j][i] = hourlyFtrBids[j][i];
					
					// actualize the remaining capacity for auction
					this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] = 
							this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] -
							this.hourlyFtrBids[j][i].getHourlyFtrPower();
				}
				else{
					this.assignHourlyFtrBids[j][i] = hourlyFtrBids[j][i];
					this.assignHourlyFtrBids[j][i].setHourlyFtrPower(Math.min(this.hourlyFtrBids[j][i].getHourlyFtrPower(), 
																			  this.transmissionMarket.getFtrAuction().getAuctionCapacity()[i]));
					this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] = 
							Math.max(0,  
									this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] -
									this.hourlyFtrBids[j][i].getHourlyFtrPower());
				}
				// actualize the values of initial date and duration of the FTR
				this.assignHourlyFtrBids[j][i].setFtrInitialDate(this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrInitialDate());
				this.assignHourlyFtrBids[j][i].setFtrDuration(this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrDuration());
			
			}
		}
	}
	
	// calculate the hourly demand for FTRs
	public double calculateHourlyFtrDemand(Ftr[] ftrs){
		try{
			this.hourlyFtrsDemand = 0.0;
			for(int i = 0; i< this.ftrBids.length; i++){
				this.hourlyFtrsDemand = this.hourlyFtrsDemand + this.ftrBids[i].getHourlyFtrPower();
			}	
				
		}
		catch (Exception e) {
			System.out.println("calculateHourlyFtrDemand ->"+e);
		}
		return this.hourlyFtrsDemand;
	}
		
	//----------------------------------------------------------------------------------------------------------------------------
	// determine the reserve price of the operator
	public double calculateFtrReservePrice(int iteration, int hour, int round){
		double reservePrice = 0.0;
		try{
			if (round == 0){	
				for(int contract = 0; contract < Global.nContracts; contract++){
					for(int i = 0; i < Global.nlags; i++){
						this.auxNodalPricesDif[i] = this.historicalNodalPrices.get(this.generationContracts.get(contract).getWithdrawalNode()-1)[iteration- i][hour] - 
								this.historicalNodalPrices.get(this.generationContracts.get(contract).getSourceNode()-1)[iteration - i][hour];
					}
					// ftrReservePrice is equal to the mean of last Global.nlags nodal price differences between the withdrawal node and source node in each contract 
					this.auxReservePriceFrtAuction[contract] = Global.MathFun.Mean(this.auxNodalPricesDif);
				}
				reservePrice = Global.MathFun.Mean(auxReservePriceFrtAuction);
				//this.productPrice = Global.MathFun.Mean(auxReservePriceFrtAuction);
			}
			else {
				//reservePrice = 3.0;
				reservePrice = this.productPrice  + this.percentPriceIncrement*this.productPrice;
				//this.productPrice  = this.productPrice  + this.percentPriceIncrement*this.productPrice ;
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: calculateFtrReservePrice ->"+e);
        }
		//return this.productPrice ;
		return reservePrice;
	}
	
	// FALTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	// organizar el calculo de la capacidad a subastar como el promedio de las demandas
	//---------------------------------------------------------------------------------------------------------------------------------------
	// define the transmission capacity to auction
	public double[] ftrAuctionCapacity(){
		double[] auxAuctionCapacity = new double[24];
		try{
			for(int hour = 0; hour < 24; hour++){
				auxAuctionCapacity[hour] = 410;
			}
		}
		catch (Exception e) {
			System.out.println("operator: ftrAuctionCapacity ->"+e);
		}
		return auxAuctionCapacity;
	}
	
	
	// define the product to auction
	public Ftr productToAuction(String operator, double power, double price, int initialDate, int duration){
		Ftr productToAuction = new Ftr(operator,power,price,initialDate,duration);
		return productToAuction;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// assign the hourly bids of FTRS
	public Ftr[] hourlyAssignFtrs(int hour, Ftr[] ftrs, double hourlyFtrAcutionCapacity, double hourlyFtrPrice,
			int ftrInitialDate, int ftrDuration){
		try{
			this.hourlyAssignFtrs = ftrs;
			for(int ftr = 0; ftr < ftrs.length; ftr++){
				this.hourlyAssignFtrs[ftr].setHourlyFtrPower(Math.min(this.hourlyAssignFtrs[ftr].getHourlyFtrPower(),
															hourlyFtrAcutionCapacity));
				/*if(ftrs[ftr].getGenerationUnit() != null){
					this.hourlyAssignFtrs[ftr].setGenerationUnit(ftrs[ftr].getGenerationUnit());
				}*//*
				this.hourlyAssignFtrs[ftr].setHourlyFtrPrice(hourlyFtrPrice);
				this.hourlyAssignFtrs[ftr].setFtrInitialDate(ftrInitialDate);
				this.hourlyAssignFtrs[ftr].setFtrDuration(ftrDuration);
				
				hourlyFtrAcutionCapacity = hourlyFtrAcutionCapacity -
											Math.min(this.hourlyAssignFtrs[ftr].getHourlyFtrPower(),
													 hourlyFtrAcutionCapacity);
				
				this.hourlyAssignFtrsAuction[ftr][hour] = this.hourlyAssignFtrs[ftr];
			}
		}
		catch (Exception e) {
			System.out.println("operator: hourlyAssignFtrs ->"+e);
		}
		return this.hourlyAssignFtrs;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// auction income by each assigned FTR by each hour
	public double[] hourlyAuctionIncome(int hour, Ftr[] ftrs){
		this.hourlyAuctionIncome[hour] = 0.0;
		try{
			for(int ftr = 0; ftr < ftrs.length; ftr++){
				this.auctionIncome[ftr][hour] = ftrs[ftr].getHourlyFtrPower() * 
												ftrs[ftr].getHourlyFtrPrice() *
												ftrs[ftr].getFtrDuration();
				this.hourlyAuctionIncome[hour] = this.hourlyAuctionIncome[hour] + this.auctionIncome[ftr][hour];
			}
		}
		catch (Exception e) {
			System.out.println("operator: hourlyAuctionIncome ->"+e);
		}
		return this.hourlyAuctionIncome;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// settlement of auction income with transmitters
	public double[][] auctionIncomeTransmitters(int iteration, double[] hourlyAuctionIncome, double[][] flows){
		double[] sumFlows = new double[24];
		this.hourlyAuctionIncomeTransmitters = this.nGridcosIncome;
		try{
			if(this.auctionIndex >= 1){
				if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 1]));
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyAuctionIncomeTransmitters[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyAuctionIncome[hour]/30);
						}
					}
				}
				// control of auction date 
				else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 2]));
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyAuctionIncomeTransmitters[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyAuctionIncome[hour]/30);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("operator: auctionIncomeTransmitters ->"+e);
		}
		return this.hourlyAuctionIncomeTransmitters;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator
	public double[][] tansmissionSettlementGen(int iteration){
		// array to save the transmission settlement by generator
		this.transmissionSetByGen = new double[Global.nGencos][24];
		try{
			// determine the congestion rents by generator
			for(int gen = 0; gen < Global.nGencos; gen++){
				this.transmissionSetGens[gen] = new CongestRentGen();
				for(int hour = 0; hour < 24; hour++){
					for(int unit1 = 0; unit1 < this.generators.get(gen).getGenerationUnits().size(); unit1++){
						for(int unit = 0; unit < Global.nUnits; unit++){
							if(this.generators.get(gen).getGenerationUnits().get(unit1) == this.transmissionSetUnits[unit].getGenerationUnit())
							this.transmissionSetByGen[gen][hour] = transmissionSetByGen[gen][hour] + this.transmissionSetUnits[unit].getCongestRentGen()[hour];
						}
					}
				}
				this.transmissionSetGens[gen].setGenerator(this.generators.get(gen));
				this.transmissionSetGens[gen].setCongestRentGen(this.transmissionSetByGen[gen]);
				//this.transmissionSetGens[gen].printCongestRentGen();
			}			
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.transmissionSetByGen;
	}
			
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by unit
	public double[][] finalSetCongestionRentsGenerators(int iteration, Ftr[][] hourlyAssignFtrsAuction){
		try{
			// array to save the transmission settlement by generation unit
			this.transmissionSetByUnit = new double[Global.nUnits][24];
			for(int unit = 0; unit < Global.nUnits; unit++){
				this.transmissionSetUnits[unit] = new CongestRentUnit();
				if(this.auctionIndex >= 1){
					if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContracts().size(); contract++){
								for(int ftr = 0; ftr < Global.nContracts; ftr++){
									if(this.generationUnits.get(unit).getGenerationContracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){							
										this.transmissionSetByUnit[unit][hour] =
												this.transmissionSetByUnit[unit][hour] +
												
												// payments by congestion rents - power in FTRs
												(1 - this.nCongestRentsProp) *
												((this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour]);
										
										this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour],
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContracts().size(); contract++){
								for(int ftr = 0; ftr < Global.nContracts; ftr++){
									if(this.generationUnits.get(unit).getGenerationContracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){							
										this.transmissionSetByUnit[unit][hour] =
												this.transmissionSetByUnit[unit][hour] +
												(1 - this.nCongestRentsProp) *
												((this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour]);
										
										this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour],
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]);
									}
								}							
							}
						}
					}
					else {
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < Global.nContracts; contract++){
								if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
							
									this.transmissionSetByUnit[unit][hour] =
											this.transmissionSetByUnit[unit][hour] +
											(1 - this.nCongestRentsProp) *
											((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
											this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
									
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
								}
							}
						}
					}
				}
				else {
					for(int hour = 0; hour < 24; hour++){
						for(int contract = 0; contract < Global.nContracts; contract++){
							if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
								this.transmissionSetByUnit[unit][hour] =
										this.transmissionSetByUnit[unit][hour] +
										(1 - this.nCongestRentsProp) *
										((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
										this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
								
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
										this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							}
						}
					}
				}
				this.transmissionSetUnits[unit].setCongestRentGen(this.transmissionSetByUnit[unit]);
				this.transmissionSetUnits[unit].setGenerationUnit(this.generationUnits.get(unit));
				//this.transmissionSetUnits[unit].printCongestRentUnit();
			}
		}
		catch (Exception e) {
			System.out.println("operator: finalSetCongestionRentsGenerators ->"+e);
		}
		return this.transmissionSetByUnit;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by retailer
	public double[][] transmissionSettlementDem(int iteration, Ftr[][] hourlyAssignFtrsAuction){
		try{
			// array to save the transmission settlement by retailer
			this.transmissionSetByRet = new double[Global.nRetailers][24];
			for(int ret = 0; ret < Global.nRetailers; ret++){
				this.transmissionSetRets[ret] = new CongestRentRet();
				if(this.auctionIndex >= 1){
					if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.retailers.get(ret).getGenerationConstracts().size(); contract++){
								for(int ftr = Global.nContracts; ftr < 2*Global.nContracts; ftr++){
									if(this.retailers.get(ret).getGenerationConstracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){
										this.transmissionSetByRet[ret][hour] =
												this.transmissionSetByRet[ret][hour] +
												
												// payments by congestion rents - power in FTRs
												this.nCongestRentsProp *
												((this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour]);
										
										this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour],
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.retailers.get(ret).getGenerationConstracts().size(); contract++){
								for(int ftr = Global.nContracts; ftr < 2*Global.nContracts; ftr++){
									if(this.retailers.get(ret).getGenerationConstracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){
										this.transmissionSetByRet[ret][hour] =
												this.transmissionSetByRet[ret][hour] +
												
												// payments by congestion rents - power in FTRs
												this.nCongestRentsProp *
												((this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour]);
										
										this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour],
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else {
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < Global.nContracts; contract++){
								if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
									this.transmissionSetByRet[ret][hour] =
											this.transmissionSetByRet[ret][hour] +
											this.nCongestRentsProp *
											((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
											this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
									
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
															this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
									
									/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
											this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower(),
													Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
															this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
															*//*
								}
							}
						}
					}
				}
				else {
					for(int hour = 0; hour < 24; hour++){
						for(int contract = 0; contract < Global.nContracts; contract++){
							if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
								this.transmissionSetByRet[ret][hour] =
										this.transmissionSetByRet[ret][hour] +
										this.nCongestRentsProp *
										((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
										this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
								
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
										this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
														this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
								
								/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower(),
												Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
														this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
														*//*
							}
						}
					}
				}
				this.transmissionSetRets[ret].setCongestRentRet(this.transmissionSetByRet[ret]);
				this.transmissionSetRets[ret].setRetailer(this.retailers.get(ret));
				//this.transmissionSetUnits[unit].printCongestRentUnit();
			}
		}
		catch (Exception e) {
			System.out.println("operator: transmissionSettlementDem ->"+e);
		}
		return this.transmissionSetByRet;
	}

	// transmission settlement: auction income + congestion rents
	public double[][] finalTransmissionSettlement(int iteration, double [][] flows, double[][] settlmentGenration, double[][] setllementDemand)
	{
		this.hourlyTransmissionSettlement = this.nGridcosIncome;
		
		double[] generationPayments = new double[24];
		double[] demandPayments = new double[24];		
		double[] sumFlows = new double[24];
		
		try{
			if(this.auctionIndex >= 1){
				if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 1]));
					
					// total power flow in the system
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					
					// sum by hour of generation and demand payments
					generationPayments = Global.MathFun.SumByColumns(settlmentGenration);
					demandPayments = Global.MathFun.SumByColumns(setllementDemand);
					
					// income total for transmission
					for(int hour = 0; hour < 24; hour++){
						this.hourlyTransmissionIncome[hour] = generationPayments[hour] + demandPayments[hour];
					}
					
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyTransmissionSettlement[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyTransmissionIncome[hour]);
						}
					}
				}
				// control of auction date 
				else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 2]));

					// total power flow in the system
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					
					// sum by hour of generation and demand payments
					generationPayments = Global.MathFun.SumByColumns(settlmentGenration);
					demandPayments = Global.MathFun.SumByColumns(setllementDemand);
					
					// income total for transmission
					for(int hour = 0; hour < 24; hour++){
						this.hourlyTransmissionIncome[hour] = generationPayments[hour] + demandPayments[hour];
					}
					
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyTransmissionSettlement[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyTransmissionIncome[hour]);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("operator: transmissionSettlement ->"+e);
		}
		return this.hourlyTransmissionSettlement;
	}*/
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para entregar los resultados del despacho a cada agente
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void setDispatchResults()
	{
		// generación ideal y real a cada unidad
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			this.generationUnits.get(unit).setIdealGeneration(this.getIdealDispatch().getGeneration()[unit]);
			this.generationUnits.get(unit).setRealGeneration(this.getRealDispatch().getGeneration()[unit]);
		}
			
		// demanda a cada comercializador
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			this.retailers.get(ret).setEnergyDemand(this.idealDispatch.getEnergyDemand()[ret]);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la magnitud de las ventas en contratos PC de cada unidad de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsEnergySalesPCMWh()
	{
		double[] energyGenerationPC;
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			energyGenerationPC = new double[24];
			for(int h = 0; h < 24; h++)
			{
				for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContractsPC().size(); contract++)
				{
					energyGenerationPC[h] = energyGenerationPC[h] + this.generationUnits.get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			}
			this.generationUnits.get(unit).setContractEnergySalesPCMWh(energyGenerationPC);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la magnitud de las compras en contratos PC de cada comercializador
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void retailersEnergyPurchasesPCMWh()
	{
		double[] energyDemandPC;
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			energyDemandPC = new double[24];
			for(int h = 0; h < 24; h++)
			{
				for(int contract = 0; contract < this.retailers.get(ret).getGenerationContractsPC().size(); contract++)
				{
					energyDemandPC[h] = energyDemandPC[h] + this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			}
			this.retailers.get(ret).setContractEnergyPurchasesPCMWh(energyDemandPC);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la máxima magnitud de las ventas en contratos PD de cada unidad de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsMaxEnergySalesPDMWh()
	{
		// variables auxiliares
		double[] maxEnergyGenerationPD; // máxima generación comprometida en contratos PD en cada período de tiempo
		
		// para cada unidad de generación		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// incicialización de las variables auxiliares
			maxEnergyGenerationPD = new double[24];
			for(int h = 0; h < 24; h++)
			{
				// calcular la energía comprometida en contratos PD
				for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContractsPD().size(); contract++)
				{
					// máxima generación comprometida en contratos PD
					maxEnergyGenerationPD[h] = maxEnergyGenerationPD[h] + this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getContractPower()[h];
				}
			}
			//
			// máxima generación comprometida en contratos PD
			//
			this.generationUnits.get(unit).setContractMaxEnergySalesPDMWh(maxEnergyGenerationPD);
		}				
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la máxima magnitud de las compras en contratos PD de cada comercializador
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void retailersMaxEnergyPurchasesPDMWh()
	{
		// variables auxiliares
		double[] maxEnergyDemandPD; // máxima demanda comprometida en contratos PD en cada período de tiempo
		
		// para cada comercializador		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			// incicialización de las variables auxiliares
			maxEnergyDemandPD = new double[24];
			for(int h = 0; h < 24; h++)
			{
				// calcular la demanda comprometida en contratos PD
				for(int contract = 0; contract < this.retailers.get(ret).getGenerationContractsPD().size(); contract++)
				{
					// máxima demanda comprometida en contratos PD
					maxEnergyDemandPD[h] = maxEnergyDemandPD[h] + this.retailers.get(ret).getGenerationContractsPD().get(contract).getContractPower()[h];
				}
			}
			//
			// máxima demanda comprometida en contratos PD
			//
			this.retailers.get(ret).setContractMaxEnergyPurchasesPDMWh(maxEnergyDemandPD);
		}		
	}
		
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la magnitud de las ventas en contratos PD de cada unidad de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void generationUnitsEnergySalesPDMWh()
	{
		// variables auxiliares
		double[] energyGenerationPD; 	// generación real comprometida en contratos PD en cada período de tiempo
		double[] energyDemandPD;		// demanda real a atender en contratos PD en cada período de tiempo
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.retailers.get(ret).getContractEnergyPurchasesPDMWh().clone();
		}
				
		// para cada unidad de generación		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// incicialización de las variables auxiliares
			energyGenerationPD = new double[24];
			energyDemandPD = new double[24];
			
			for(int h = 0; h < 24; h++)
			{				
				for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContractsPD().size(); contract++)
				{
					if (this.dispatchedGenerationContractsPD.get(h).contains(this.generationUnits.get(unit).getGenerationContractsPD().get(contract)))
					{	
						// demanda a atender en contratos PD para el comercializador ret asociado al contrato
						int ret = this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getContractBuyerId();
						energyDemandPD[h] = energyDemandPD[h] + Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h], 
								this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getContractPower()[h]);
						
						// actualización de la demanda remanente a atender en contratos PD para el comercializador ret en la hora h
						remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] 
								- Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h], 
										this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getContractPower()[h]);
					}
				}
				// la demanda a atender en contratos pague lo demandado es el mínimo entre la demanda contratada 
				// en este tipo de contratos y el máximo entre cero y la diferencia entre la demanda real 
				// y la demanda atendida en contratos pague lo contratado 
				energyGenerationPD[h] = Math.min(this.generationUnits.get(unit).getContractMaxEnergySalesPDMWh()[h], energyDemandPD[h]);
			}
			//
			// generación real para atender contratos PD
			//
			this.generationUnits.get(unit).setContractEnergySalesPDMWh(energyGenerationPD);
		}	
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la magnitud de las compras en contratos PD de cada comercializador
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void retailersEnergyPurchasesPDMWh()
	{
		// variables auxiliares
		double[] energyDemandPD;		// demanda real atendida en contratos PD
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización de las variables auxiliares
			energyDemandPD = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// la demanda a atender en contratos pague lo demandado es el mínimo entre la demanda contratada 
				// en este tipo de contratos y el máximo entre cero y la diferencia entre la demanda real 
				// y la demanda atendida en contratos pague lo contratado 
				energyDemandPD[h] = Math.min(this.retailers.get(ret).getContractMaxEnergyPurchasesPDMWh()[h],
											Math.max(0,
													this.retailers.get(ret).getEnergyDemand()[h] - this.retailers.get(ret).getContractEnergyPurchasesPCMWh()[h]));
			}
			// 
			// demanda real atendida en contratos PD en la hora h
			//
			this.retailers.get(ret).setContractEnergyPurchasesPDMWh(energyDemandPD);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la cantidad de energía negociada en bolsa por cada comercializador y para cada período de despacho
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void retailersEnergyPurchasesMWh()
	{
		double[] energyPurchases;
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			energyPurchases = new double[24];
			for(int h = 0; h < 24; h++)
			{
				energyPurchases[h] = this.retailers.get(ret).getEnergyDemand()[h] 
										- this.retailers.get(ret).getContractEnergyPurchasesPCMWh()[h]
										- this.retailers.get(ret).getContractEnergyPurchasesPDMWh()[h]		;
			}
			this.retailers.get(ret).setPoolEnergyPurchasesMWh(energyPurchases);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación del mercado de energía para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void retailersEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] remainingEnergyDemand; 			// demanda remanente
		double[] settlementContractsPC; 			// egresos en contratos pague lo contratado
		double[] settlementContractsPD; 			// egresos en contratos pague lo demandado
		double[] poolEnergyPurchases; 				// compras en bolsa
		double[] settlementPoolEnergyPurchses; 		// egresos por compras en bolsa
		double[] poolEnergySales; 					// ventas en bolsa
		double[] settlementPoolEnergySales; 		// ingresos por ventas en bolsa
		double[] settlementEnergyMarket; 			// egresos totales del comercializador
		
		List<List<GenerationContract>> dispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>(); 		// contratos de generación despachados en el día
		List<GenerationContract> hourlyDispatchedGenerationContractsPD; 	// contratos de generación despachados en cada hora
		
		for(int i = 0; i < 24; i++)
		{
			dispatchedGenerationContractsPD.add(new ArrayList<GenerationContract>());
		}
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.retailers.get(ret).getContractEnergyPurchasesPDMWh().clone();
		
			// inicialización de las varibles auxiliares
			remainingEnergyDemand 			= this.retailers.get(ret).getEnergyDemand().clone();
			settlementContractsPC 			= new double[24];
			settlementContractsPD 			= new double[24];
			poolEnergyPurchases 			= new double[24];
			settlementPoolEnergyPurchses 	= new double[24];
			poolEnergySales 				= new double[24];
			settlementPoolEnergySales 		= new double[24];
			settlementEnergyMarket 			= new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si el comercializador posee contratos PC
				//
				for(int contract = 0; contract < this.retailers.get(ret).getGenerationContractsPC().size(); contract++)
				{
					// cálculo del egreso en el contrato PC contract para la hora h
					settlementContractsPC[h] =  settlementContractsPC[h] + 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				}			
				//
				// si la demanda contratada en contratos PC es mayor que la demanda real entonces hay ventas en bolsa
				//
				if(remainingEnergyDemand[h] <= 0)
				{
					// la cantidad vendida en bolsa es es la diferencia entre la demanda contratada 
					// en contratos PC y la demanda real
					poolEnergySales[h] = Math.abs(remainingEnergyDemand[h]);
					
					// cálculo del ingreso por ventas en bolsa
					settlementPoolEnergySales[h] = this.idealDispatch.getSpotPrices()[h] * poolEnergySales[h];
				}
				//
				// si el comercializador posee contratos PD
				//
				hourlyDispatchedGenerationContractsPD = new ArrayList<GenerationContract>();
				for(int contract = 0; contract < this.retailers.get(ret).getSortGenerationContractsPD().get(h).size(); contract++)
				{
					// cálculo de la demanda a liquidar en el contrato PD contact en la hora h
					double demandPD = Math.max(0,
							Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h],
							this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPower()[h]));

					// cálculo del egreso en el contrato PD contract para la hora h
					settlementContractsPD[h] = settlementContractsPD[h] 
							+ this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPrice()[h]
							* demandPD; 
										
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - demandPD;	
					remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] - demandPD;
					
					//
					// actualización energía despachada en contratos PD
					//
					// comerializador
					this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).setDispatchedContractPowerPD(demandPD, h);
					//
					// unidad de generación
					//
					int unit = this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getGenerationUnitId();
					for(int i = 0; i < this.generationUnits.get(unit).getGenerationContractsPD().size(); i++)
					{
						if(this.generationUnits.get(unit).getGenerationContractsPD().get(i).getContractId() == this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractId())
						{
							this.generationUnits.get(unit).getGenerationContractsPD().get(i).setDispatchedContractPowerPD(demandPD, h);
						}
					}
					
					if(demandPD > 0)
					{
						// si el contrato se necesita para atender la demanda entonces el contrato PD se guarda como despachado
						hourlyDispatchedGenerationContractsPD.add(this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract));
					}
				}
				// agregar los contratos despachados para la hora h que pertenecen al comercializador ret
				dispatchedGenerationContractsPD.get(h).addAll(hourlyDispatchedGenerationContractsPD);
							
				//
				// si después de atender la demanda con contratos PC y PD aun hay demanda remanente por atender entonces
				// el comercializador tienen que comprar energía en la bolsa
				//
				if(remainingEnergyDemand[h] > 0)
				{					
					// la cantidad comprada en bolsa es la demanda remanente equivalente a la demanda real menos la
					// demanda atendida en contratos PC y PD
					poolEnergyPurchases[h] = remainingEnergyDemand[h];
					
					// cálculo del egreso por compras en bolsa
					settlementPoolEnergyPurchses[h] = this.idealDispatch.getSpotPrices()[h] * poolEnergyPurchases[h];
				}
				
				//
				// liquidación general del comercializador
				//
				settlementEnergyMarket[h] = settlementContractsPC[h] + settlementContractsPD[h] 
					+ settlementPoolEnergyPurchses[h] - settlementPoolEnergySales[h]; 
			}
			this.retailers.get(ret).setContractEnergyPurchasesPCCOP(settlementContractsPC);
			this.retailers.get(ret).setContractEnergyPurchasesPDCOP(settlementContractsPD);
			this.retailers.get(ret).setPoolEnergySalesMWh(poolEnergySales);
			this.retailers.get(ret).setPoolEnergySalesCOP(settlementPoolEnergySales);
			this.retailers.get(ret).setPoolEnergyPurchasesMWh(poolEnergyPurchases);
			this.retailers.get(ret).setPoolEnergyPurchasesCOP(settlementPoolEnergyPurchses);
			this.retailers.get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
		}
		// establece los despachos contratados para la hora h de todos los comercializadores
		this.setDispatchedGenerationContractsPD(dispatchedGenerationContractsPD);
		//Global.rw.printArrayDispatchContracts(this.getDispatchedGenerationContractsPD());
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación del mercado de energía para las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] remainingIdealGeneration; 				// generación ideal remanente
		double[] settlementContractsPC; 				// egresos en contratos pague lo contratado
		double[] settlementContractsPD; 				// egresos en contratos pague lo demandado
		double[] poolEnergyPurchases; 					// compras en bolsa
		double[] settlementPoolEnergyPurchses; 			// egresos por compras en bolsa
		double[] poolEnergySales; 						// ventas en bolsa
		double[] settlementPoolEnergySales; 			// ingresos por ventas en bolsa
		double[] settlementEnergyMarket; 				// egresos totales de la unidad de generación
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.retailers.get(ret).getContractEnergyPurchasesPDMWh().clone();
		}
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			remainingIdealGeneration 		= this.generationUnits.get(unit).getIdealGeneration().clone();
			settlementContractsPC 			= new double[24];
			settlementContractsPD 			= new double[24];
			poolEnergyPurchases 			= new double[24];
			settlementPoolEnergyPurchses 	= new double[24];
			poolEnergySales 				= new double[24];
			settlementPoolEnergySales 		= new double[24];
			settlementEnergyMarket 			= new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si la unidad de generación posee contratos PC
				//
				for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContractsPC().size(); contract++)
				{
					// calculo del ingreso en el contrato PC contract para la hora h
					settlementContractsPC[h] =  settlementContractsPC[h] + 
							this.generationUnits.get(unit).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
							this.generationUnits.get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
					
					// actualización de la generación ideal de energía remanente para la hora h
					remainingIdealGeneration[h] = remainingIdealGeneration[h] - 
							this.generationUnits.get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			
				//
				// si la unidad de generación posee contratos PD
				//
				for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContractsPD().size(); contract++)
				{
					// generación a liquidar en el contrato PD contract en la hora h
					double energyPD = this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD(h);
					
					// cálculo del ingreso en el contrato PD contract para la hora h
					settlementContractsPD[h] = settlementContractsPD[h] 
							+ this.generationUnits.get(unit).getGenerationContractsPD().get(contract).getContractPrice()[h]
							* energyPD; 
					
					// actualización de generación ideal remanente para la hora h
					remainingIdealGeneration[h] = remainingIdealGeneration[h] - energyPD;
				}
				
				//
				// si después de cumplir con los compromisos en contratos PC y PD hay exceso o faltante de generación ideal, 
				// entonces la unidad de generación vende o compra en bolsa respectívamente
				//
				if(remainingIdealGeneration[h] <= 0)
				{
					// la cantidad comprada en bolsa es la generación remanente 
					poolEnergyPurchases[h] = Math.abs(remainingIdealGeneration[h]);
					
					// cálculo del egreso por compras en bolsa
					settlementPoolEnergyPurchses[h] = this.idealDispatch.getSpotPrices()[h] * poolEnergyPurchases[h];
				}
				else
				{
					// la cantidad vendida en bolsa es la generación remanente 
					poolEnergySales[h] = remainingIdealGeneration[h];
					
					// cálculo del ingreso por ventas en bolsa
					settlementPoolEnergySales[h] = this.idealDispatch.getSpotPrices()[h] * poolEnergySales[h];
				}
					
				// liquidación general del comercializador
				settlementEnergyMarket[h] = settlementContractsPC[h] + settlementContractsPD[h] 
						- settlementPoolEnergyPurchses[h] + settlementPoolEnergySales[h];
			}
			this.generationUnits.get(unit).setContractEnergySalesPCCOP(settlementContractsPC);
			this.generationUnits.get(unit).setContractEnergySalesPDCOP(settlementContractsPD);
			this.generationUnits.get(unit).setPoolEnergySalesMWh(poolEnergySales);
			this.generationUnits.get(unit).setPoolEnergySalesCOP(settlementPoolEnergySales);
			this.generationUnits.get(unit).setPoolEnergyPurchasesMWh(poolEnergyPurchases);
			this.generationUnits.get(unit).setPoolEnergyPurchasesCOP(settlementPoolEnergyPurchses);
			this.generationUnits.get(unit).setSettlementEnergyMarket(settlementEnergyMarket);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación reconciliaciones para las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsReconciliationsSettlement()
	{
		// variables auxiliares
		double[] idealGeneration; 				// generación ideal
		double[] realGeneration; 				// generación real
		double[] reconciliationMWh; 			// magnitud reconciliación
		double[] positiveReconciliationMWh;		// magnitud reconciliación positiva
		double[] positiveReconciliationCOP;		// valor reconciliación positiva
		double[] negativeReconciliationMWh;		// magnitud reconciliación negativa
		double[] negativeReconciliationCOP;		// valor reconciliación negativa
		double[] settlementEnergyMarket; 		// valor liquidación parcial del mercado		
		
		System.out.println("\n-------------------------------- 	Reconciliaciones 	--------------------------------\n");
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			idealGeneration 		= this.generationUnits.get(unit).getIdealGeneration().clone();
			realGeneration 			= this.generationUnits.get(unit).getRealGeneration().clone();
			settlementEnergyMarket  = this.generationUnits.get(unit).getSettlementEnergyMarket().clone();
			reconciliationMWh 		= new double[24];
			positiveReconciliationMWh = new double[24];
			positiveReconciliationCOP = new double[24];
			negativeReconciliationMWh = new double[24];
			negativeReconciliationCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// magnitud de la reconciliación
				reconciliationMWh[h] = realGeneration[h] - idealGeneration[h];
				//
				// tipo de reconciliación
				//
				if(reconciliationMWh[h] > 0)
				{
					// magnitud reconciliación positiva
					positiveReconciliationMWh[h] = reconciliationMWh[h];
					
					// [ VERIFICAR [[[[[ las plantas menores son depachadas de manera obligatoria a toda su capacidad por 
					// lo que no participan en las reconciliaciones ]]]]]
					//if(this.generationUnits.get(unit).getEffectiveCapacity() >= 20)
					//{
					// valor reconciliación positiva por tipo de recurso
					if(this.generationUnits.get(unit).getUnitType().equals("	T	"))
					{							
						// plantas térmicas
						positiveReconciliationCOP[h] = positiveReconciliationMWh[h] * 
								Math.min(this.getReferencePricePositiveReconciliation(), 
										this.getIdealDispatch().getEnergyBidPrice()[unit][h] + this.generationUnits.get(unit).getStartStopPice()/this.getDailyPositiveReconciliationMWh()[unit]);
						
						System.out.println("\t" + this.generationUnits.get(unit).getUnitName() + "\t" + h + "\t" + this.getReferencePricePositiveReconciliation() 
										+ "\t" + (this.getIdealDispatch().getEnergyBidPrice()[unit][h] + this.generationUnits.get(unit).getStartStopPice()/this.getDailyPositiveReconciliationMWh()[unit])
										+ "\t" + positiveReconciliationMWh[h] + "\t" + positiveReconciliationCOP[h]);
					}
					else
					{
						// plantas hidráulicas [ asumiendo NEM < NPV ]
						positiveReconciliationCOP[h] = positiveReconciliationMWh[h] * this.getIdealDispatch().getSpotPrices()[h]; 
						System.out.println("\t" + this.generationUnits.get(unit).getUnitName()+ "\t" + h + "\t" + positiveReconciliationMWh[h]
								+ "\t" + this.getIdealDispatch().getSpotPrices()[h] + "\t" + positiveReconciliationCOP[h]); 
					}
					
					// actualización de la liquidación de la unidad de generación
					settlementEnergyMarket[h] = settlementEnergyMarket[h] + positiveReconciliationCOP[h];
					//}
				}
				else
				{
					// magnitud reconciliación negativa
					negativeReconciliationMWh[h] = Math.abs(reconciliationMWh[h]);
					
					// valor reconciliación negativa
					negativeReconciliationCOP[h] = negativeReconciliationMWh[h] * this.getIdealDispatch().getSpotPrices()[h]; 
					
					// actualización de la liquidación de la unidad de generación
					settlementEnergyMarket[h] = settlementEnergyMarket[h] - negativeReconciliationCOP[h];
					
					//System.out.println("\t" + this.generationUnits.get(unit).getUnitName()+ "\t" + h + "\t" + negativeReconciliationMWh[h]
					//		+ "\t" + this.getIdealDispatch().getSpotPrices()[h] + "\t" + negativeReconciliationCOP[h]); 
				}				
			}
			this.generationUnits.get(unit).setPositiveReconciliationMWh(positiveReconciliationMWh); // establcer magnitud reconciliación positiva
			this.generationUnits.get(unit).setPositiveReconciliationCOP(positiveReconciliationCOP); // establcer valor reconciliación positiva
			this.generationUnits.get(unit).setNegativeReconciliationMWh(negativeReconciliationMWh); // establcer magnitud reconciliación negativa
			this.generationUnits.get(unit).setNegativeReconciliationCOP(negativeReconciliationCOP); // establcer valor reconciliación negativa
			this.generationUnits.get(unit).setSettlementEnergyMarket(settlementEnergyMarket);		// establecer nuevo valor de la liquidación
		}
	}
		
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación restricciones para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void retailersConstraintsSettlement()
	{
		// variables auxiliares
		double[] constraintsCOP; 			// valor restricciones
		double[] settlementEnergyMarket; 	// valor liquidación parcial del mercado	
		
		System.out.println("\n-------------------------------- 	Restricciones 	--------------------------------\n");
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización de las varibles auxiliares
			constraintsCOP = new double[24];
			settlementEnergyMarket = this.retailers.get(ret).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				constraintsCOP[h] = this.getConstraintsCOP()[h] * (this.retailers.get(ret).getEnergyDemand()[h]/this.getEnergyDemand()[h]); 
				settlementEnergyMarket[h] = settlementEnergyMarket[h] + constraintsCOP[h];
			}
			this.retailers.get(ret).setConstraintsCOP(constraintsCOP);
			this.retailers.get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.retailers.get(ret).getRetailerName() + "\t");
			Global.rw.printVector(constraintsCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación cargos por uso para los transmisores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void transmittersUsageChargesSettlement(int iteration)
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		
		System.out.println("\n-------------------------------- 	Transmisores: cargos por uso 	--------------------------------\n");
		
		for( int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para el transmisor gridco
				settlementUsageChargesCOP[h] = this.transmitters.get(gridco).getProportionTransmissionSettlement()
						* this.getUsageCharges()[iteration][h]* this.getEnergyDemand()[h];
			}
			this.transmitters.get(gridco).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.transmitters.get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación cargos por uso para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void retailersUsageChargesSettlementCOP()
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] settlementEnergyMarket; 		// valor liquidación parcial del mercado
		
		System.out.println("\n-------------------------------- 	Comercializadores: cargos por uso 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			settlementEnergyMarket = this.retailers.get(ret).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para el comercializador ret
				settlementUsageChargesCOP[h] = this.getSettlementUsageChargesRetailersCOP()[h] * (this.retailers.get(ret).getEnergyDemand()[h]/this.getEnergyDemand()[h]);
				settlementEnergyMarket[h] = settlementEnergyMarket[h] + settlementUsageChargesCOP[h];
			}
			this.retailers.get(ret).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			this.retailers.get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.retailers.get(ret).getRetailerName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// liquidación cargos por uso para los generadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsUsageChargesSettlementCOP()
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] settlementEnergyMarket; 		// valor liquidación parcial del mercado
		
		System.out.println("\n-------------------------------- 	Generadores: cargos por uso 	--------------------------------\n");
		
		for( int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			settlementEnergyMarket = this.generationUnits.get(unit).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para la unidad de generación unit
				settlementUsageChargesCOP[h] = this.getSettlementUsageChargesGeneratorsCOP()[h] * (this.generationUnits.get(unit).getIdealGeneration()[h]/this.getIdealGeneration()[h]);
				settlementEnergyMarket[h] = settlementEnergyMarket[h] - settlementUsageChargesCOP[h];
			}
			this.generationUnits.get(unit).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			this.generationUnits.get(unit).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.generationUnits.get(unit).getUnitName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°__________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|					|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	MERCADO NODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|__________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// MERCADO NODAL: liquidación del mercado de energía para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void nodRetailersEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] remainingEnergyDemand; 			// demanda remanente
		double[] settlementContractsPC; 			// egresos en contratos pague lo contratado
		double[] settlementContractsPD; 			// egresos en contratos pague lo demandado
		double[] poolEnergyPurchases; 				// compras en bolsa
		double[] settlementPoolEnergyPurchses; 		// egresos por compras en bolsa
		double[] poolEnergySales; 					// ventas en bolsa
		double[] settlementPoolEnergySales; 		// ingresos por ventas en bolsa
		double[] settlementEnergyMarket; 			// egresos totales del comercializador
		
		List<List<GenerationContract>> dispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>(); 		// contratos de generación despachados en el día
		List<GenerationContract> hourlyDispatchedGenerationContractsPD; 	// contratos de generación despachados en cada hora
		
		for(int i = 0; i < 24; i++)
		{
			dispatchedGenerationContractsPD.add(new ArrayList<GenerationContract>());
		}
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.retailers.get(ret).getContractEnergyPurchasesPDMWh().clone();
		
			// inicialización de las varibles auxiliares
			remainingEnergyDemand 			= this.retailers.get(ret).getEnergyDemand().clone();
			settlementContractsPC 			= new double[24];
			settlementContractsPD 			= new double[24];
			poolEnergyPurchases 			= new double[24];
			settlementPoolEnergyPurchses 	= new double[24];
			poolEnergySales 				= new double[24];
			settlementPoolEnergySales 		= new double[24];
			settlementEnergyMarket 			= new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si el comercializador posee contratos PC
				//
				for(int contract = 0; contract < this.retailers.get(ret).getGenerationContractsPC().size(); contract++)
				{
					// cálculo del egreso en el contrato PC contract para la hora h
					settlementContractsPC[h] =  settlementContractsPC[h] + 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - 
						this.retailers.get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				}			
				//
				// si la demanda contratada en contratos PC es mayor que la demanda real entonces hay ventas en bolsa
				//
				if(remainingEnergyDemand[h] <= 0)
				{
					// la cantidad vendida en bolsa es es la diferencia entre la demanda contratada 
					// en contratos PC y la demanda real
					poolEnergySales[h] = Math.abs(remainingEnergyDemand[h]);
					
					// cálculo del ingreso por ventas en bolsa
					settlementPoolEnergySales[h] = 
							this.getRealDispatch().getNodalPrices()[this.retailers.get(ret).getDemandNode().getNodeId()][h] 
									* poolEnergySales[h];
				}
				//
				// si el comercializador posee contratos PD
				//
				hourlyDispatchedGenerationContractsPD = new ArrayList<GenerationContract>();
				for(int contract = 0; contract < this.retailers.get(ret).getSortGenerationContractsPD().get(h).size(); contract++)
				{
					// cálculo de la demanda a liquidar en el contrato PD contact en la hora h
					double demandPD = Math.max(0,
							Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h],
							this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPower()[h]));

					// cálculo del egreso en el contrato PD contract para la hora h
					settlementContractsPD[h] = settlementContractsPD[h] 
							+ this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPrice()[h]
							* demandPD; 
										
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - demandPD;	
					remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] - demandPD;
					
					//
					// actualización energía despachada en contratos PD
					//
					// comerializador
					this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).setDispatchedContractPowerPD(demandPD, h);
					//
					// unidad de generación
					//
					int unit = this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getGenerationUnitId();
					for(int i = 0; i < this.generationUnits.get(unit).getGenerationContractsPD().size(); i++)
					{
						if(this.generationUnits.get(unit).getGenerationContractsPD().get(i).getContractId() == this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractId())
						{
							this.generationUnits.get(unit).getGenerationContractsPD().get(i).setDispatchedContractPowerPD(demandPD, h);
						}
					}
					
					if(demandPD > 0)
					{
						// si el contrato se necesita para atender la demanda entonces el contrato PD se guarda como despachado
						hourlyDispatchedGenerationContractsPD.add(this.retailers.get(ret).getSortGenerationContractsPD().get(h).get(contract));
					}
				}
				// agregar los contratos despachados para la hora h que pertenecen al comercializador ret
				dispatchedGenerationContractsPD.get(h).addAll(hourlyDispatchedGenerationContractsPD);
							
				//
				// si después de atender la demanda con contratos PC y PD aun hay demanda remanente por atender entonces
				// el comercializador tienen que comprar energía en la bolsa
				//
				if(remainingEnergyDemand[h] > 0)
				{					
					// la cantidad comprada en bolsa es la demanda remanente equivalente a la demanda real menos la
					// demanda atendida en contratos PC y PD
					poolEnergyPurchases[h] = remainingEnergyDemand[h];
					
					// cálculo del egreso por compras en bolsa
					settlementPoolEnergyPurchses[h] = 
							this.getRealDispatch().getNodalPrices()[this.retailers.get(ret).getDemandNode().getNodeId()][h] 
									* poolEnergyPurchases[h];
				}
				
				//
				// liquidación general del comercializador
				//
				settlementEnergyMarket[h] = settlementContractsPC[h] + settlementContractsPD[h] 
					+ settlementPoolEnergyPurchses[h] - settlementPoolEnergySales[h]; 
			}
			this.retailers.get(ret).setContractEnergyPurchasesPCCOP(settlementContractsPC);
			this.retailers.get(ret).setContractEnergyPurchasesPDCOP(settlementContractsPD);
			this.retailers.get(ret).setPoolEnergySalesMWh(poolEnergySales);
			this.retailers.get(ret).setPoolEnergySalesCOP(settlementPoolEnergySales);
			this.retailers.get(ret).setPoolEnergyPurchasesMWh(poolEnergyPurchases);
			this.retailers.get(ret).setPoolEnergyPurchasesCOP(settlementPoolEnergyPurchses);
			this.retailers.get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
		}
		// establece los despachos contratados para la hora h de todos los comercializadores
		this.setDispatchedGenerationContractsPD(dispatchedGenerationContractsPD);
		//Global.rw.printArrayDispatchContracts(this.getDispatchedGenerationContractsPD());
	}
	

	//---------------------------------------------------------------------------------------------------------------------------------------
	// Función con las actividades del operador:
	// 1.) Despacho ideal y real.
	// 2.) Liquidación del mercado de energía
	// 3.) Desarrollo de la subasta de FTRs
	// 4.) Liquidación del mercado de transmisión
	//
	public void management(int iteration, IloNumVar[][] varI, IloNumVar[][] varIN, IloNumVar[][] varR, IloRange[][] rngI,
			IloRange[][] rngIN, IloRange[][] rngR, String[] powergen, String[] anglevol, String[] unserved, double[] lowPowerLimit, double[][] dailyPowerBid,
			double[][] powerBidPrice, double[] unsDemandCost, double [][] powerDemandVector, double[] susceptance, 
			double[] powerLimit, double[] angleLb, double[] angleUb, double[] unservedLb,
			double[] unservedUb, double[][] demand, double[][] supply,
			CsvWriter idealWriter, CsvWriter realWriter){
		try {
			// set the power bids (price and quantity) for all generation units
			//for(int gen = 0; gen < Global.nGencos; gen++){
			//	this.generators.get(gen).sendSupplyBids(iteration, sheetSupply);
			//}		
						
			// Read information from excel
			Global.charge.powerDemandCsv	(iteration, powerDemandVector, demand); // obtener el valor de la demanda por nodo por hora
			Global.charge.powerBidCsv		(iteration, dailyPowerBid, supply);		// obtener el valor de la oferta de energía por unidad de generación por hora
			Global.charge.powerBidPriceCsv	(iteration, powerBidPrice, supply);   	// obtener el precio ofertado por unidad de generación por día
			
			//powerDemandVector = Global.factory.productoPuntoEscalar(powerDemandVector, 1.2);		
			
			// solución del despacho idal
			this.idealDispatch = DailyIdealDispatchN.dispatch(varIN, rngIN, powergen, anglevol, unserved, iteration,  
				lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, powerDemandVector, susceptance, powerLimit,  
				angleLb, angleUb, unservedLb, unservedUb, this, idealWriter);
			
			// solución del despacho real
			this.realDispatch = DailyRealDispatch.dispatch(varR, rngR, powergen, anglevol, unserved, iteration,  
					lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, powerDemandVector, susceptance, powerLimit,  
					angleLb, angleUb, unservedLb, unservedUb, this, realWriter);
			
			// establcer los resultados del despacho
			this.setDispatchResults(); 	// resultados a generadores y comercializadores
			this.setIdealGeneration();	// establecer generación ideal total
			this.setRealGeneration();	// establecer generación real total
			
			// liquidación contratos y bolsa comercializadores
			this.retailersEnergyPurchasesPCMWh();				// magnitud compras de energía en contratos PC comercializadores
			this.retailersEnergyPurchasesPDMWh();				// magnitud compras de energía en contratos PD comercializadores
			this.retailersEnergyMarketSettlement();				// liquidación de energía en contratos y en bolsa comercializadores
			
			// liquidación contratos y bolsa generadores
			this.generationUnitsEnergySalesPCMWh();				// magnitud ventas de energía en contratos PC generadores
			this.generationUnitsEnergySalesPDMWh();				// magnitud ventas de energía en contratos PD generadores
			this.generationUnitsEnergyMarketSettlement();		// liquidación de energía en contratos y en bolsa generadores
			
			// liquidación reconciliaciones
			this.setDailyPositiveReconciliationMWh();			// magnitud generaciones fuera de mérito diarias
			this.generationUnitsReconciliationsSettlement();	// unidades de generación: reconciliaciones
			this.setPositiveReconciliationMWh();				// magnitud de las reconciliaciones positivas
			this.setPositiveReconciliationCOP();				// valor de las reconciliaciones positivas
			this.setNegativeReconciliationMWh();				// magnitud de las reconciliaciones negativas
			this.setNegativeReconciliationCOP();				// valor de las reconciliaciones negativas
			this.setEnergyDemand();								// demanda total por cada hora
			this.setConstraintsCOP();							// vaor de las restricciones por comercializador
			this.retailersConstraintsSettlement();				// comercializadores: restricciones
			
			// liquidación transmisión
			this.transmittersUsageChargesSettlement(iteration); // liquidación de los cargos por uso para cada transmisor
			this.setSettlementUsageChargesCOP();				// liquidación de los cargos por uso totales en el mercado, por comercializador y por generador
			this.retailersUsageChargesSettlementCOP();			// liquidación de los cargos por uso para cada comercializador
			this.generationUnitsUsageChargesSettlementCOP();	// liquidación de los cargos por uso para cada generador
			
			// escritura de resultados en csv
			Global.rw.writeCsvSettlementRetailers(Global.unSettlementRetailersWriter, this.retailers, iteration);			// escritura liquidación comercializadores
			Global.rw.writeCsvSettlementUnits(Global.unSettlementUnitsWriter, this.generationUnits, iteration);				// escritura liquidación unidades de generación
			Global.rw.writeCsvSettlementTransmitters(Global.unSettlementTransmittersWriter, this.transmitters, iteration);	// escritura liquidación transmisores
			
			
			/*
			this.retailersEnergySalesPCMWh();
			this.retailersEnergySalesPDMWh();
			this.retailersEnergyPurchasesMWh();
			
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				System.out.println(this.retailers.get(ret).getRetailerName());
				System.out.print("demand\t");
				Global.rw.printVector(this.retailers.get(ret).getEnergyDemand());
				System.out.print("PCMWh\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPCMWh());
				System.out.print("PDMWh\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPDMWh());
				System.out.print("Purchases MWh\t");
				Global.rw.printVector(this.retailers.get(ret).getPoolEnergyPurchasesMWh());	
			}
			System.out.println();
			System.out.println();
			System.out.println();
			*/
			
			
			
			/*
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				System.out.println(this.retailers.get(ret).getRetailerName());
				System.out.print("demand\t");
				Global.rw.printVector(this.retailers.get(ret).getEnergyDemand());
				System.out.print("PCMWh\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPCMWh());
				System.out.print("PCCOP\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPCCOP());
				System.out.print("PDMWh\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPDMWh());
				System.out.print("PDCOP\t");
				Global.rw.printVector(this.retailers.get(ret).getContractEnergyPurchasesPDCOP());
				System.out.print("Purchases MWh\t");
				Global.rw.printVector(this.retailers.get(ret).getPoolEnergyPurchasesMWh());
				System.out.print("Purchases COP\t");
				Global.rw.printVector(this.retailers.get(ret).getPoolEnergyPurchasesCOP());
				System.out.print("Sales MWh\t");
				Global.rw.printVector(this.retailers.get(ret).getPoolEnergySalesMWh());
				System.out.print("Sales COP\t");
				Global.rw.printVector(this.retailers.get(ret).getPoolEnergySalesCOP());
				System.out.print("Settlement\t");
				Global.rw.printVector(this.retailers.get(ret).getSettlementEnergyMarket());
			}
			*/
			
			/*this.realDispatch = DailyRealDispatch.dispatch(varR, rngR, powergen, anglevol, unserved, iteration, units, nodes, lines, data, 
	    			lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, flows, powerDemandVector, susceptance, powerLimit, 
	    			angleLb, angleUb, unservedLb, unservedUb, sheetPgR, sheetPrR, sheetFlR, sheetDeR, this, factory);
					
			data.writeSettlement(iteration, this.contractSettlementGeneration, this.nPoolPurSetGen,
					this.nPoolSalSetGen, this.contractSettlementDemand, this.nPoolPurSetDem,
					this.nGridcosIncome, this.nTransmissionSetByUnit, this.transmissionSetByRet, 
					sheetConG, sheetPurGN, sheetSalGN, sheetConD, sheetPurDN, sheetTraSN,
					sheetConGN, sheetConDN);
			*/
			System.out.println("siiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
			
		} catch (Exception e) {
			System.out.println("management ->"+e);
		}
	}
}