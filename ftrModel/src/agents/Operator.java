package agents;

import java.awt.Color;
import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.AbstractDocument.Content;

import org.jfree.chart.ChartPanel;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;



import utilities.ArrayFactory;
import utilities.ChargeInformation;
import utilities.MathFuns;
import market.DailyIdealDispatch;
import market.DailyIdealDispatchN;
import market.DailyRealDispatch;
import market.Dispatch;
import market.Ftr;
import market.FtrAuction;
import market.GenerationContract;
import market.GenerationUnit;
import market.Node;
import market.PowerDemand;
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
	private List<Node> nodes = new ArrayList<Node>(Global.nNodes); 					// lista de nodos
	private List<Generator> generators = new ArrayList<Generator>(Global.nGencos);	// lista de generadores
	private List<Retailer> retailers = new ArrayList<Retailer>(Global.nRetailers);	// lista de comercializadores
	private List<Transmitter> transmitters = new ArrayList<Transmitter>(Global.nGridcos); 	// lista de transmisores
	private List<GenerationUnit> generationUnits = new ArrayList<GenerationUnit>(Global.nUnits); // lista of unidades de generación
	private List<TransmissionLine> transmissionLines = new ArrayList<TransmissionLine>(Global.nLines);	// lista de líneas de transmisión
	private List<GenerationContract> generationContracts = new ArrayList<GenerationContract>(Global.nContracts);   // lista de contratos de generación
	private List<PowerBid> DailyPowerBids = new ArrayList<PowerBid>(Global.nUnits);	// list of daily power bids
	
	// liquidación mercado
	private double[] energyDemand;								// demanda de energía total para cada hora
	private double[] servedDemand;								// demanda de energía total efectívamente atendida para cada hora
	private double[] unservedDemand;							// demanda de energía total no atendida para cada hora
	private double[] idealGeneration;							// generación ideal total
	private double[] realGeneration; 							// generación real total
	
	// liquidación mercado uninodal
	private double[] retailersContractEnergyPurchasesPCMWh;		// magnitud compras totales en contratos PC de los comercializadores
	private double[] retailersContractEnergyPurchasesPCCOP;		// valor compras totales en contratos PC de los comercializadores
	private double[] retailersContractEnergyPurchasesPDMWh;		// magnitud compras totales en contratos PD de los comercializadores
	private double[] retailersContractEnergyPurchasesPDCOP;		// valor compras totales en contratos PD de los comercializadores
	private double[] retailersPoolEnergyPurchasesMWh;			// magnitud compras en bolsa de los comercializadores
	private double[] retailersPoolEnergyPurchasesCOP;			// valor compras en bolsa de los comercializadores
	private double[] retailersPoolEnergySalesMWh;				// magnitud ventas totales en bolsa de los comercializadores
	private double[] retailersPoolEnergySalesCOP;				// valor ventas totales en bolsa de los comercializadores
	private double[] retailersSetttlementEnergyMarketCOP;		// valor de la liquidación total de los comercializadores
	private double[] generationUnitsContractEnergySalesPCMWh;	// magnitud ventas totales en contratos PC de las unidades de generación
	private double[] generationUnitsContractEnergySalesPCCOP;	// valor ventas totales en contratos PC de las unidades de generación en
	private double[] generationUnitsContractEnergySalesPDMWh;	// magnitud ventas totales en contratos PD de las unidades de generación
	private double[] generationUnitsContractEnergySalesPDCOP;	// valor ventas totales en contratos PD de las unidades de generación
	private double[] generationUnitsPoolEnergyPurchasesMWh;		// magnitud compras en bolsa de las unidades de generación
	private double[] generationUnitsPoolEnergyPurchasesCOP;		// valor compras en bolsa de las unidades de generación
	private double[] generationUnitsPoolEnergySalesMWh;			// magnitud ventas totales en bolsa de las unidades de generación
	private double[] generationUnitsPoolEnergySalesCOP;			// valor ventas totales en bolsa de las unidades de generación
	private double[] generationUnitsSetttlementEnergyMarketCOP;	// valor de la liquidación total de las unidades de generación
	private double[] positiveReconciliationMWh; 				// magnitud reconciliación positiva total para cada hora
	private double[] positiveReconciliationCOP; 				// valor reconciliación positiva total para cada hora
	private double[] negativeReconciliationMWh; 				// magnitud reconciliación negativa total para cada hora
	private double[] negativeReconciliationCOP; 				// valor reconciliación negativa total para cada hora
	private double[] constraintsCOP; 							// valor restricciones totales para cada hora
	private double referencePricePositiveReconciliation; 		// precio de referencia para las reconciliaciones positivas de la plantas térmicas
	private double[]  dailyPositiveReconciliationMWh; 			// magnitud reconciliación positiva total de un dia
	private double[][] usageChargesCOP_MWh; 					// cargos por uso del sistema de transmisión nacional
	private double[] settlementUsageChargesCOP; 				// liquidación cargos por uso del sistema de transmisión nacional por hora
	private double[] settlementUsageChargesRetailersCOP; 		// liquidación cargos por uso del sistema de transmisión nacional por hora comercializadores
	private double[] settlementUsageChargesGeneratorsCOP;		// liquidación cargos por uso del sistema de transmisión nacional por hora generadores
	private double proportionUsageChargesDemand; 				// proporción de los cargos por uso liquidados a la demanda
	
	// liquidación mercado nodal
	private double[] nodRetailersContractEnergyPurchasesPCMWh;		// magnitud compras totales en contratos PC de los comercializadores en el mercado nodal
	private double[] nodRetailersContractEnergyPurchasesPCCOP;		// valor compras totales en contratos PC de los comercializadores en el mercado nodal
	private double[] nodRetailersContractEnergyPurchasesPDMWh;		// magnitud compras totales en contratos PD de los comercializadores en el mercado nodal
	private double[] nodRetailersContractEnergyPurchasesPDCOP;		// valor compras totales en contratos PD de los comercializadores en el mercado nodal
	private double[] nodRetailersPoolEnergyPurchasesMWh;			// magnitud compras en bolsa de los comercializadores en el mercado nodal
	private double[] nodRetailersPoolEnergyPurchasesCOP;			// valor compras en bolsa de los comercializadores en el mercado nodal
	private double[] nodRetailersPoolEnergySalesMWh;				// magnitud ventas totales en bolsa de los comercializadores en el mercado nodal
	private double[] nodRetailersPoolEnergySalesCOP;				// valor ventas totales en bolsa de los comercializadores en el mercado nodal
	private double[] nodRetailersCongestionRentsCOP; 				// valor de las rentas por congestión a cargo de los comercializadores en el mercado nodal
	private double[] nodRetailersComplementaryChargesCOP; 			// valor de los cargos complementarios a cargo de los comercializadores en el mercado nodal
	private double[] nodRetailersUsageChargesCOP; 					// valor de los cargos por uso a cargo de los comercializadores en el mercado nodal
	private double[] nodRetailersSetttlementEnergyMarketCOP;		// valor de la liquidación total de los comercializadores en el mercado nodal
	private double[] nodGenerationUnitsContractEnergySalesPCMWh;	// magnitud ventas totales en contratos PC de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsContractEnergySalesPCCOP;	// valor ventas totales en contratos PC de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsContractEnergySalesPDMWh;	// magnitud ventas totales en contratos PD de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsContractEnergySalesPDCOP;	// valor ventas totales en contratos PD de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsPoolEnergyPurchasesMWh;		// magnitud compras en bolsa de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsPoolEnergyPurchasesCOP;		// valor compras en bolsa de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsPoolEnergySalesMWh;			// magnitud ventas totales en bolsa de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsPoolEnergySalesCOP;			// valor ventas totales en bolsa de las unidades de generación en el mercado nodal
	private double[] nodGenerationUnitsSetttlementEnergyMarketCOP;	// valor de la liquidación total de las unidades de generación en el mercado nodal
	private double[] nodTransmitterCongestionRentsCOP; 				// valor total de las rentas por congestión por hora
	private double[] nodTransmittersSettlementUsageChargesCOP;		// valor total de los cargos por uso en el mercado nodal
	private double[] nodTransmittersSettlementComplementaryChargesCOP;		// valor total de los cargos complementarios en el mercado nodal
	private double[] nodCongestionRentsCOP; 						// valor total de las rentas por congestión del mercado nodal
	private double[] nodCongestionRentsFundCOP; 					// valor total del fondo creado con las rentas por congestión del mercado nodal
		
	// variable mercado nodal con ftrs
	private List<double[]> historicalNodalPrices; 			// precios nodales históricos
	private List<double[]> historicalEnergyDemand; 			// energía de demanda histórica
	private List<double[]> historicalHourlyEnergyDemand; 	// energía de demanda histórica para cada hora
	private int auctionIndex; 							// índice de una subasta
	private FtrAuction ftrAuction;							// subasta de FTRs
	private double[] ftrAuctionCapacity;					// capacidad de transmisión a subastar
	private double ftrHourlyReservePrice; 					// precio de reserva del operador para los FTRs de una hora dada
	private double[] ftrReservePrice; 						// precio de reserva del operador para los FTRs en todas las horas
	private double[] ftrProductPrice; 						// precio actualizado del operador para los FTRs en todas las horas
	private Ftr ftrAuctionedProduct; 							// producto a ser subastado en cada hora: FTR horario
	private List<Ftr> ftrBids; 									// arreglo de ofertas de FTRs	
	private double[] ftrSettlementFtrsIncomeCOP; 					// valor total de los ingresos por los FTRs
	private double[] ftrRetailersContractEnergyPurchasesPCMWh;		// magnitud compras totales en contratos PC de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersContractEnergyPurchasesPCCOP;		// valor compras totales en contratos PC de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersContractEnergyPurchasesPDMWh;		// magnitud compras totales en contratos PD de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersContractEnergyPurchasesPDCOP;		// valor compras totales en contratos PD de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersPoolEnergyPurchasesMWh;			// magnitud compras en bolsa de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersPoolEnergyPurchasesCOP;			// valor compras en bolsa de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersPoolEnergySalesMWh;				// magnitud ventas totales en bolsa de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersPoolEnergySalesCOP;				// valor ventas totales en bolsa de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersCongestionRentsCOP; 				// valor de las rentas por congestión a cargo de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersComplementaryChargesCOP; 			// valor de los cargos complementarios a cargo de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersUsageChargesCOP; 					// valor de los cargos por uso a cargo de los comercializadores en el mercado nodal con ftrs
	private double[] ftrRetailersSetttlementEnergyMarketCOP;		// valor de la liquidación total de los comercializadores en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsContractEnergySalesPCMWh;	// magnitud ventas totales en contratos PC de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsContractEnergySalesPCCOP;	// valor ventas totales en contratos PC de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsContractEnergySalesPDMWh;	// magnitud ventas totales en contratos PD de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsContractEnergySalesPDCOP;	// valor ventas totales en contratos PD de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsPoolEnergyPurchasesMWh;		// magnitud compras en bolsa de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsPoolEnergyPurchasesCOP;		// valor compras en bolsa de las unidades de generación en el mercado nodall con ftrs
	private double[] ftrGenerationUnitsPoolEnergySalesMWh;			// magnitud ventas totales en bolsa de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsPoolEnergySalesCOP;			// valor ventas totales en bolsa de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrGenerationUnitsSetttlementEnergyMarketCOP;	// valor de la liquidación total de las unidades de generación en el mercado nodal con ftrs
	private double[] ftrTransmitterCongestionRentsCOP; 				// valor total de las rentas por congestión por hora en el mercado nodal con ftrs
	private double[] ftrTransmittersSettlementUsageChargesCOP;		// valor total de los cargos por uso en el mercado nodall con ftrs
	private double[] ftrTransmittersSettlementComplementaryChargesCOP; // valor total de los cargos complementarios en el mercado nodal con ftrs
	private double[] ftrCongestionRentsCOP; 						// valor total de las rentas por congestión del mercado nodal
	private double[] ftrCongestionRentsFundCOP; 					// valor total del fondo creado con las rentas por congestión del mercado nodal
	private double[] ftrRetailersSettlementFtrsIncomeCOP; 			// valor total de los ingresoso de los comercializadores por los FTRs adquiridos
	private double[] ftrRemainingCongestionRentsCOP; 				// valor de las rentas por congestión remanetes después de liquidar los FTRs
	
	// variables para el despacho real
	private double[][] generationR = new double[Global.nUnits][24];			// hourly power generation by unit
	private double[][] generationCloneR = new double[Global.nUnits][24]; 	// clone of hourly power generation by unit
	private double[][] voltageAnglesR = new double[Global.nNodes][24]; 		// ángulos de voltage para cada nodo por hora
	private double[][] powerDemandR = new double[Global.nNodes][24];		// hourly power demand by retailer
	private double[][] powerDemandCloneR = new double[Global.nNodes][24];	// clone of hourly power demand by retailer
	private double[][] nodalPricesR = new double[Global.nNodes][24];		// hourly nodal prices by node
	private double[][] powerFlowsR = new double[Global.nLines][24];			// hourly power flows by line
	private double[][] remainderCapacityR = new double[Global.nLines][24];	// hourly remainder capacity
	private double[][] unservedDemandR = new double[Global.nNodes][24];		// hourly unserved power demand by node
	private double dispatchCostR	= 0.0;									// daily dispatch cost
	
	// variables para el despacho ideal
	private double[][] generationI = new double[Global.nUnits][24];			// hourly power generation by unit
	public double[][] generationCloneI = new double[Global.nUnits][24];		// clone of hourly power generation by unit
	public double[][] voltageAnglesI = new double[Global.nNodes][24]; 		// ángulos de voltage para cada nodo por hora
	private double[][] powerDemandI = new double[Global.nNodes][24];		// hourly power demand by retailer
	public double[][] powerDemandCloneI = new double[Global.nNodes][24];	// clone of hourly power demand by retailer
	private double[] spotPricesI = new double[24];							// precios spot horarios
	private double[][] nodalPricesI = new double[Global.nNodes][24];		// hourly nodal prices by node
	private double[][] powerFlowsI = new double[Global.nLines][24];			// hourly power flows by line
	private double[][] remainderCapacityI = new double[Global.nLines][24];	// hourly remainder capacity
	private double[][] unservedDemandI = new double[Global.nNodes][24];		// hourly unserved power demand by node
	private double dispatchCostI	= 0.0; 									// daily dispatch cost
	
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
	public List<Node> getNodes(){return this.nodes; } // obtenerla lista de nodos
	public List<GenerationUnit> getGenerationUnits(){return this.generationUnits; } // obtener la lista de unidades de generación
	public List<Generator> getGenerators() {return this.generators;} // obtener las lista de generadores
	public List<Retailer> getRetailers() {return this.retailers;} // obtener la lista de comercializadores
	public List<Transmitter> getTransmitters() {return this.transmitters;} // obtener la lista de transmisores
	public List<TransmissionLine> getTransmissionLines() {return this.transmissionLines;} // obtener la lista de líneas de transmissión
	public Dispatch getRealDispatch(){return this.realDispatch; } // obtener el despacho real
	public Dispatch getIdealDispatch(){return this.idealDispatch; } // obtener el despacho ideal
	public List<GenerationContract> getGenerationContracts() {return this.generationContracts;} // obtener la lista de contratos de generación
	public List<GenerationContract> getGenerationContractsPC() {return this.generationContractsPC;} // obtener los contratos de generación PC
	public List<GenerationContract> getGenerationContractsPD() {return this.generationContractsPD;} // obtener los contratos de generación PD
	public List<List<GenerationContract>> getDispatchedGenerationContractsPD() {return this.dispatchedGenerationContractsPD;} // obtener los contratos PD que han sido despachados
	public List<List<GenerationContract>> getSortDispatchedGenerationContractsPD() {return this.sortDispatchedGenerationContractsPD;} // obtener los contratos PD organizados que han sido despachados
	public double getReferencePricePositiveReconciliation() { return this.referencePricePositiveReconciliation;} // obtener precio de referencia para las reconciliaciones positivas de la plantas térmicas
	public double[][] getUsageCharges(){return this.usageChargesCOP_MWh;} // obtener los cargos por uso del sistema de transmisión
	public double getProportionUsageChargesDemand(){ return this.proportionUsageChargesDemand;} // obtener proporción de los cargos por uso liquidados a la demanda
	public double[] getSettlementUsageChargesRetailersCOP(){ return this.settlementUsageChargesRetailersCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora comercializadores
	public double[] getSettlementUsageChargesGeneratorsCOP(){ return this.settlementUsageChargesGeneratorsCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora generadores
	public List<double[]> getHistoricalNodalPrices(){return this.historicalNodalPrices;} // obtener el valor de los precios históricos
	public List<double[]> getHistoricalEnergyDemand(){return this.historicalEnergyDemand;} // obtener el valor de la demanda histórica de energía
	public List<double[]> getHistoricalHourlyEnergyDemand(){return this.historicalHourlyEnergyDemand;} // obtener el valor de la demanda histórica de energía por hora
	public FtrAuction getFtrAuction(){return this.ftrAuction;} // obtener la subasta de FTRs
	public double[] getFtrAuctionCapacity(){return this.ftrAuctionCapacity;} // obtener la capacidad de transmisión a subastar
	public double[] getFtrReservePrice(){return this.ftrReservePrice;} // obtener el precio de reserva del operador para los FTRs en todas las horas
	public double getFtrHourlyReservePrice(){return this.ftrHourlyReservePrice;} // obtener el precio de reserva del operador para los FTRs en una hora determinada
	public double[] getFtrProductPrice(){return this.ftrProductPrice;} // obtener el precio actualizado del operador para los FTRs en todas las horas
	public List<Ftr> getFtrBids(){return this.ftrBids;} //  obtener el arreglo de ofertas de FTRs
	public Ftr getFtrAuctionedProduct(){return this.ftrAuctionedProduct;} // obtener los detalles del FTR subastado en cada hora
	public int getAuctionIndex(){return this.auctionIndex;} // obtener el índice de la subasta
	public double[] getNodCongestionRentsCOP(){return this.nodCongestionRentsCOP;} // obtener el valor total de las rentas por congestión
	public double[] getNodCongestionRentsFundCOP(){return this.nodCongestionRentsFundCOP;} // obtener el valor total del fondo de rentas por congestión
	public double[] getFtrCongestionRentsCOP(){return this.ftrCongestionRentsCOP;} // obtener el valor total de las rentas por congestión
	public double[] getFtrCongestionRentsFundCOP(){return this.ftrCongestionRentsFundCOP;} // obtener el valor total del fondo de rentas por congestión
	
	// set methods
	public void setNodes(List<Node> nodes){this.nodes = nodes; } // establcer lista de nodos
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
	public void setHistoricalNodalPrices(List<double[]> historicalNodalPrices){ this.historicalNodalPrices = historicalNodalPrices;} // establecer el valor de los precios históricos
	public void setHistoricalEnergyDemand(List<double[]> historicalEnergyDemand){ this.historicalEnergyDemand = historicalEnergyDemand;} // establecer el valor de la demanda histórica de energía
	public void setHistoricalHourlyEnergyDemand(List<double[]> historicalHourlyEnergyDemand){ this.historicalHourlyEnergyDemand = historicalHourlyEnergyDemand;} // establecer el valor de la demanda histórica de energía cada hora
	public void setFtrAuction(FtrAuction ftrAuction){this.ftrAuction = ftrAuction;} // establecer la subasta de FTRs
	public void setFtrReservePrice(double[] ftrReservePrice){this.ftrReservePrice = ftrReservePrice;} // establecer el precio de reserva del operador para los FTRs en todas las horas
	public void setFtrHourlyReservePrice(double ftrHourlyReservePrice){this.ftrHourlyReservePrice = ftrHourlyReservePrice;} // establecer el precio de reserva del operador para los FTRs en una hora determinada
	public void setFtrProductPrice(double[] ftrProductPrice){this.ftrProductPrice = ftrProductPrice;} // establecer el precio actualizado del operador para los FTRs en todas las horas
	public void setFtrBids(List<Ftr> ftrBids){this.ftrBids = ftrBids;} //  establecer el arreglo de ofertas de FTRs
	public void setFtrAuctionedProduct(Ftr ftrAuctionedProduct){this.ftrAuctionedProduct = ftrAuctionedProduct;} // establecer los detalles del FTR subastado en cada hora
	public void setFtrAuctionCapacity(double[] ftrAuctionCapacity){this.ftrAuctionCapacity = ftrAuctionCapacity;} // establecer la capacidad de transmisión a subastar
	public void setAuctionIndex(int auctionIndex){this.auctionIndex = auctionIndex;} // establecer el índice de la subasta
	public void setNodCongestionRentsCOP(double[] nodCongestionRentsCOP){this.nodCongestionRentsCOP = nodCongestionRentsCOP;} // establecer el valor total de las rentas por congestión
	public void setNodCongestionRentsFundCOP(double[] nodCongestionRentsFundCOP){this.nodCongestionRentsFundCOP = nodCongestionRentsFundCOP;} // establecer el valor total del fondo de rentas por congestión
	public void setFtrCongestionRentsCOP(double[] ftrCongestionRentsCOP){this.ftrCongestionRentsCOP = ftrCongestionRentsCOP;} // establecer el valor total de las rentas por congestión
	public void setFtrCongestionRentsFundCOP(double[] ftrCongestionRentsFundCOP){this.ftrCongestionRentsFundCOP = ftrCongestionRentsFundCOP;} // establecer el valor total del fondo de rentas por congestión
		
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
	// obtener y establecer la demanda de energía efectívamente atendida para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getServedDemand(){return this.servedDemand;} // obtener la demanda total efectívamente atendida del mercado para cada hora
	public void setServedDemand() {
		
		// variable auxiliar
		double[] servedDemand = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// demanda de energía efectívamente atendida para cada hora
				servedDemand[h] = servedDemand[h] + this.retailers.get(ret).getServedDemand()[h];
			}
		}
		this.servedDemand = servedDemand;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la demanda de energía no atendida para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getUnservedDemand(){return this.unservedDemand;} // obtener la demanda total no atendida del mercado para cada hora
	public void setUnservedDemand() {
		
		// variable auxiliar
		double[] unservedDemand = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// demanda de energía no atendida para cada hora
				unservedDemand[h] = unservedDemand[h] + this.retailers.get(ret).getUnservedDemand()[h];
			}
		}
		this.unservedDemand = unservedDemand;
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
	
	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°____________________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|											|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	FUNCIONES AUXILIARES MERCADO UNINODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|____________________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersContractEnergyPurchasesPCMWh(){return this.retailersContractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	public void setRetailersContractEnergyPurchasesPCMWh(){
		
		// variable auxiliar
		double[] retailersContractEnergyPurchasesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
				retailersContractEnergyPurchasesPCMWh[h] = retailersContractEnergyPurchasesPCMWh[h] + this.retailers.get(ret).getContractEnergyPurchasesPCMWh()[h];
			}
		}
		this.retailersContractEnergyPurchasesPCMWh = retailersContractEnergyPurchasesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersContractEnergyPurchasesPCCOP(){return this.retailersContractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos PC en cada hora de todos los comercializadores
	public void setRetailersContractEnergyPurchasesPCCOP(){
		
		// variable auxiliar
		double[] retailersContractEnergyPurchasesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
				retailersContractEnergyPurchasesPCCOP[h] = retailersContractEnergyPurchasesPCCOP[h] + this.retailers.get(ret).getContractEnergyPurchasesPCCOP()[h];
			}
		}
		this.retailersContractEnergyPurchasesPCCOP = retailersContractEnergyPurchasesPCCOP;
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersContractEnergyPurchasesPDMWh(){return this.retailersContractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	public void setRetailersContractEnergyPurchasesPDMWh(){
		
		// variable auxiliar
		double[] retailersContractEnergyPurchasesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
				retailersContractEnergyPurchasesPDMWh[h] = retailersContractEnergyPurchasesPDMWh[h] + this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh()[h];
			}
		}
		this.retailersContractEnergyPurchasesPDMWh = retailersContractEnergyPurchasesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersContractEnergyPurchasesPDCOP(){return this.retailersContractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos PD en cada hora de todos los comercializadores
	public void setRetailersContractEnergyPurchasesPDCOP(){
		
		// variable auxiliar
		double[] retailersContractEnergyPurchasesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
				retailersContractEnergyPurchasesPDCOP[h] = retailersContractEnergyPurchasesPDCOP[h] + this.retailers.get(ret).getContractEnergyPurchasesPDCOP()[h];
			}
		}
		this.retailersContractEnergyPurchasesPDCOP = retailersContractEnergyPurchasesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersPoolEnergyPurchasesMWh(){return this.retailersPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	public void setRetailersPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] retailersPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras bolsa en cada hora de todos los comercializadores
				retailersPoolEnergyPurchasesMWh[h] = retailersPoolEnergyPurchasesMWh[h] + this.retailers.get(ret).getPoolEnergyPurchasesMWh()[h];
			}
		}
		this.retailersPoolEnergyPurchasesMWh = retailersPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersPoolEnergyPurchasesCOP(){return this.retailersPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en cada hora de todos los comercializadores
	public void setRetailersPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] retailersPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras bolsa en cada hora de todos los comercializadores
				retailersPoolEnergyPurchasesCOP[h] = retailersPoolEnergyPurchasesCOP[h] + this.retailers.get(ret).getPoolEnergyPurchasesCOP()[h];
			}
		}
		this.retailersPoolEnergyPurchasesCOP = retailersPoolEnergyPurchasesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersPoolEnergySalesMWh(){return this.retailersPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	public void setRetailersPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] retailersPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las ventas bolsa en cada hora de todos los comercializadores
				retailersPoolEnergySalesMWh[h] = retailersPoolEnergySalesMWh[h] + this.retailers.get(ret).getPoolEnergySalesMWh()[h];
			}
		}
		this.retailersPoolEnergySalesMWh = retailersPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersPoolEnergySalesCOP(){return this.retailersPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en cada hora de todos los comercializadores
	public void setRetailersPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] retailersPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las ventas bolsa en cada hora de todos los comercializadores
				retailersPoolEnergySalesCOP[h] = retailersPoolEnergySalesCOP[h] + this.retailers.get(ret).getPoolEnergySalesCOP()[h];
			}
		}
		this.retailersPoolEnergySalesCOP = retailersPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las liquidación total de los comercializadores para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getRetailersSetttlementEnergyMarketCOP(){return this.retailersSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de los comercializadores para cada hora
	public void setRetailersSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] retailersSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las liquidación total de los comercializadores para cada hora
				retailersSetttlementEnergyMarketCOP[h] = retailersSetttlementEnergyMarketCOP[h] + this.retailers.get(ret).getSettlementEnergyMarket()[h];
			}
		}
		this.retailersSetttlementEnergyMarketCOP = retailersSetttlementEnergyMarketCOP;
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsContractEnergySalesPCMWh(){return this.generationUnitsContractEnergySalesPCMWh;} // obtener la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setGenerationUnitsContractEnergySalesPCMWh(){
		
		// variable auxiliar
		double[] generationUnitsContractEnergySalesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				generationUnitsContractEnergySalesPCMWh[h] = generationUnitsContractEnergySalesPCMWh[h] + this.generationUnits.get(unit).getContractEnergySalesPCMWh()[h];
			}
		}
		this.generationUnitsContractEnergySalesPCMWh = generationUnitsContractEnergySalesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsContractEnergySalesPCCOP(){return this.generationUnitsContractEnergySalesPCCOP;} // obtener el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setGenerationUnitsContractEnergySalesPCCOP(){
		
		// variable auxiliar
		double[] generationUnitsContractEnergySalesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				generationUnitsContractEnergySalesPCCOP[h] = generationUnitsContractEnergySalesPCCOP[h] + this.generationUnits.get(unit).getContractEnergySalesPCCOP()[h];
			}
		}
		this.generationUnitsContractEnergySalesPCCOP = generationUnitsContractEnergySalesPCCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsContractEnergySalesPDMWh(){return this.generationUnitsContractEnergySalesPDMWh;} // obtener la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setGenerationUnitsContractEnergySalesPDMWh(){
		
		// variable auxiliar
		double[] generationUnitsContractEnergySalesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				generationUnitsContractEnergySalesPDMWh[h] = generationUnitsContractEnergySalesPDMWh[h] + this.generationUnits.get(unit).getContractEnergySalesPDMWh()[h];
			}
		}
		this.generationUnitsContractEnergySalesPDMWh = generationUnitsContractEnergySalesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsContractEnergySalesPDCOP(){return this.generationUnitsContractEnergySalesPDCOP;} // obtener el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setGenerationUnitsContractEnergySalesPDCOP(){
		
		// variable auxiliar
		double[] generationUnitsContractEnergySalesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				generationUnitsContractEnergySalesPDCOP[h] = generationUnitsContractEnergySalesPDCOP[h] + this.generationUnits.get(unit).getContractEnergySalesPDCOP()[h];
			}
		}
		this.generationUnitsContractEnergySalesPDCOP = generationUnitsContractEnergySalesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsPoolEnergySalesMWh(){return this.generationUnitsPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setGenerationUnitsPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] generationUnitsPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
				generationUnitsPoolEnergySalesMWh[h] = generationUnitsPoolEnergySalesMWh[h] + this.generationUnits.get(unit).getPoolEnergySalesMWh()[h];
			}
		}
		this.generationUnitsPoolEnergySalesMWh = generationUnitsPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsPoolEnergySalesCOP(){return this.generationUnitsPoolEnergySalesCOP;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setGenerationUnitsPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] generationUnitsPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
				generationUnitsPoolEnergySalesCOP[h] = generationUnitsPoolEnergySalesCOP[h] + this.generationUnits.get(unit).getPoolEnergySalesCOP()[h];
			}
		}
		this.generationUnitsPoolEnergySalesCOP = generationUnitsPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsPoolEnergyPurchasesMWh(){return this.generationUnitsPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setGenerationUnitsPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] generationUnitsPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				generationUnitsPoolEnergyPurchasesMWh[h] = generationUnitsPoolEnergyPurchasesMWh[h] + this.generationUnits.get(unit).getPoolEnergyPurchasesMWh()[h];
			}
		}
		this.generationUnitsPoolEnergyPurchasesMWh = generationUnitsPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsPoolEnergyPurchasesCOP(){return this.generationUnitsPoolEnergyPurchasesCOP;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setGenerationUnitsPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] generationUnitsPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				generationUnitsPoolEnergyPurchasesCOP[h] = generationUnitsPoolEnergyPurchasesCOP[h] + this.generationUnits.get(unit).getPoolEnergyPurchasesCOP()[h];
			}
		}
		this.generationUnitsPoolEnergyPurchasesCOP = generationUnitsPoolEnergyPurchasesCOP;
	}

	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// obtener y establecer el valor de las liquidación total de las unidades de generación para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getGenerationUnitsSetttlementEnergyMarketCOP(){return this.generationUnitsSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de las unidades de generación para cada hora
	public void setGenerationUnitsSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] generationUnitsSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las liquidación total de las unidades de generación para cada hora
				generationUnitsSetttlementEnergyMarketCOP[h] = generationUnitsSetttlementEnergyMarketCOP[h] + this.generationUnits.get(unit).getSettlementEnergyMarket()[h];
			}
		}
		this.generationUnitsSetttlementEnergyMarketCOP = generationUnitsSetttlementEnergyMarketCOP;
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
				settlementUsageChargesCOP[h] = settlementUsageChargesCOP[h] + this.getTransmitters().get(gridco).getSettlementUsageChargesCOP()[h];
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
	// obtener y establecer el valor de las reconciliaciones negativas totales diarias
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
	
	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°______________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|										|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	FUNCIONES AUXILIARES MERCADO NODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|______________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: obtener y establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersContractEnergyPurchasesPCMWh(){return this.nodRetailersContractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	public void setNodRetailersContractEnergyPurchasesPCMWh(){
		
		// variable auxiliar
		double[] nodRetailersContractEnergyPurchasesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
				nodRetailersContractEnergyPurchasesPCMWh[h] = nodRetailersContractEnergyPurchasesPCMWh[h] + this.getRetailers().get(ret).getNodContractEnergyPurchasesPCMWh()[h];
			}
		}
		this.nodRetailersContractEnergyPurchasesPCMWh = nodRetailersContractEnergyPurchasesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersContractEnergyPurchasesPCCOP(){return this.nodRetailersContractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos PC en cada hora de todos los comercializadores
	public void setNodRetailersContractEnergyPurchasesPCCOP(){
		
		// variable auxiliar
		double[] nodRetailersContractEnergyPurchasesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
				nodRetailersContractEnergyPurchasesPCCOP[h] = nodRetailersContractEnergyPurchasesPCCOP[h] + this.getRetailers().get(ret).getNodContractEnergyPurchasesPCCOP()[h];
			}
		}
		this.nodRetailersContractEnergyPurchasesPCCOP = nodRetailersContractEnergyPurchasesPCCOP;
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersContractEnergyPurchasesPDMWh(){return this.nodRetailersContractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	public void setNodRetailersContractEnergyPurchasesPDMWh(){
		
		// variable auxiliar
		double[] nodRetailersContractEnergyPurchasesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
				nodRetailersContractEnergyPurchasesPDMWh[h] = nodRetailersContractEnergyPurchasesPDMWh[h] + this.getRetailers().get(ret).getNodContractEnergyPurchasesPDMWh()[h];
			}
		}
		this.nodRetailersContractEnergyPurchasesPDMWh = nodRetailersContractEnergyPurchasesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersContractEnergyPurchasesPDCOP(){return this.nodRetailersContractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos PD en cada hora de todos los comercializadores
	public void setNodRetailersContractEnergyPurchasesPDCOP(){
		
		// variable auxiliar
		double[] nodRetailersContractEnergyPurchasesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
				nodRetailersContractEnergyPurchasesPDCOP[h] = nodRetailersContractEnergyPurchasesPDCOP[h] + this.getRetailers().get(ret).getNodContractEnergyPurchasesPDCOP()[h];
			}
		}
		this.nodRetailersContractEnergyPurchasesPDCOP = nodRetailersContractEnergyPurchasesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersPoolEnergyPurchasesMWh(){return this.nodRetailersPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	public void setNodRetailersPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] nodRetailersPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras bolsa en cada hora de todos los comercializadores
				nodRetailersPoolEnergyPurchasesMWh[h] = nodRetailersPoolEnergyPurchasesMWh[h] + this.getRetailers().get(ret).getNodPoolEnergyPurchasesMWh()[h];
			}
		}
		this.nodRetailersPoolEnergyPurchasesMWh = nodRetailersPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersPoolEnergyPurchasesCOP(){return this.nodRetailersPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en cada hora de todos los comercializadores
	public void setNodRetailersPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] nodRetailersPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras bolsa en cada hora de todos los comercializadores
				nodRetailersPoolEnergyPurchasesCOP[h] = nodRetailersPoolEnergyPurchasesCOP[h] + this.getRetailers().get(ret).getNodPoolEnergyPurchasesCOP()[h];
			}
		}
		this.nodRetailersPoolEnergyPurchasesCOP = nodRetailersPoolEnergyPurchasesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersPoolEnergySalesMWh(){return this.nodRetailersPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	public void setNodRetailersPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] nodRetailersPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las ventas bolsa en cada hora de todos los comercializadores
				nodRetailersPoolEnergySalesMWh[h] = nodRetailersPoolEnergySalesMWh[h] + this.getRetailers().get(ret).getNodPoolEnergySalesMWh()[h];
			}
		}
		this.nodRetailersPoolEnergySalesMWh = nodRetailersPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersPoolEnergySalesCOP(){return this.nodRetailersPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en cada hora de todos los comercializadores
	public void setNodRetailersPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] nodRetailersPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las ventas bolsa en cada hora de todos los comercializadores
				nodRetailersPoolEnergySalesCOP[h] = nodRetailersPoolEnergySalesCOP[h] + this.getRetailers().get(ret).getNodPoolEnergySalesCOP()[h];
			}
		}
		this.nodRetailersPoolEnergySalesCOP = nodRetailersPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las liquidación total de los comercializadores para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersSetttlementEnergyMarketCOP(){return this.nodRetailersSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de los comercializadores para cada hora
	public void setNodRetailersSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] nodRetailersSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las liquidación total de los comercializadores para cada hora
				nodRetailersSetttlementEnergyMarketCOP[h] = nodRetailersSetttlementEnergyMarketCOP[h] + this.getRetailers().get(ret).getNodSettlementEnergyMarket()[h];
			}
		}
		this.nodRetailersSetttlementEnergyMarketCOP = nodRetailersSetttlementEnergyMarketCOP;
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsContractEnergySalesPCMWh(){return this.nodGenerationUnitsContractEnergySalesPCMWh;} // obtener la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsContractEnergySalesPCMWh(){
		
		// variable auxiliar
		double[] nodGenerationUnitsContractEnergySalesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				nodGenerationUnitsContractEnergySalesPCMWh[h] = nodGenerationUnitsContractEnergySalesPCMWh[h] + this.getGenerationUnits().get(unit).getNodContractEnergySalesPCMWh()[h];
			}
		}
		this.nodGenerationUnitsContractEnergySalesPCMWh = nodGenerationUnitsContractEnergySalesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsContractEnergySalesPCCOP(){return this.nodGenerationUnitsContractEnergySalesPCCOP;} // obtener el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsContractEnergySalesPCCOP(){
		
		// variable auxiliar
		double[] nodGenerationUnitsContractEnergySalesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				nodGenerationUnitsContractEnergySalesPCCOP[h] = nodGenerationUnitsContractEnergySalesPCCOP[h] + this.getGenerationUnits().get(unit).getNodContractEnergySalesPCCOP()[h];
			}
		}
		this.nodGenerationUnitsContractEnergySalesPCCOP = nodGenerationUnitsContractEnergySalesPCCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsContractEnergySalesPDMWh(){return this.nodGenerationUnitsContractEnergySalesPDMWh;} // obtener la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsContractEnergySalesPDMWh(){
		
		// variable auxiliar
		double[] nodGenerationUnitsContractEnergySalesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				nodGenerationUnitsContractEnergySalesPDMWh[h] = nodGenerationUnitsContractEnergySalesPDMWh[h] + this.getGenerationUnits().get(unit).getNodContractEnergySalesPDMWh()[h];
			}
		}
		this.nodGenerationUnitsContractEnergySalesPDMWh = nodGenerationUnitsContractEnergySalesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsContractEnergySalesPDCOP(){return this.nodGenerationUnitsContractEnergySalesPDCOP;} // obtener el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsContractEnergySalesPDCOP(){
		
		// variable auxiliar
		double[] nodGenerationUnitsContractEnergySalesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				nodGenerationUnitsContractEnergySalesPDCOP[h] = nodGenerationUnitsContractEnergySalesPDCOP[h] + this.getGenerationUnits().get(unit).getNodContractEnergySalesPDCOP()[h];
			}
		}
		this.nodGenerationUnitsContractEnergySalesPDCOP = nodGenerationUnitsContractEnergySalesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsPoolEnergySalesMWh(){return this.nodGenerationUnitsPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] nodGenerationUnitsPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
				nodGenerationUnitsPoolEnergySalesMWh[h] = nodGenerationUnitsPoolEnergySalesMWh[h] + this.getGenerationUnits().get(unit).getNodPoolEnergySalesMWh()[h];
			}
		}
		this.nodGenerationUnitsPoolEnergySalesMWh = nodGenerationUnitsPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsPoolEnergySalesCOP(){return this.nodGenerationUnitsPoolEnergySalesCOP;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] nodGenerationUnitsPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
				nodGenerationUnitsPoolEnergySalesCOP[h] = nodGenerationUnitsPoolEnergySalesCOP[h] + this.getGenerationUnits().get(unit).getNodPoolEnergySalesCOP()[h];
			}
		}
		this.nodGenerationUnitsPoolEnergySalesCOP = nodGenerationUnitsPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsPoolEnergyPurchasesMWh(){return this.nodGenerationUnitsPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] nodGenerationUnitsPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				nodGenerationUnitsPoolEnergyPurchasesMWh[h] = nodGenerationUnitsPoolEnergyPurchasesMWh[h] + this.getGenerationUnits().get(unit).getNodPoolEnergyPurchasesMWh()[h];
			}
		}
		this.nodGenerationUnitsPoolEnergyPurchasesMWh = nodGenerationUnitsPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsPoolEnergyPurchasesCOP(){return this.nodGenerationUnitsPoolEnergyPurchasesCOP;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setNodGenerationUnitsPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] nodGenerationUnitsPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				nodGenerationUnitsPoolEnergyPurchasesCOP[h] = nodGenerationUnitsPoolEnergyPurchasesCOP[h] + this.getGenerationUnits().get(unit).getNodPoolEnergyPurchasesCOP()[h];
			}
		}
		this.nodGenerationUnitsPoolEnergyPurchasesCOP = nodGenerationUnitsPoolEnergyPurchasesCOP;
	}

	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de las liquidación total de las unidades de generación para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodGenerationUnitsSetttlementEnergyMarketCOP(){return this.nodGenerationUnitsSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de las unidades de generación para cada hora
	public void setNodGenerationUnitsSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] nodGenerationUnitsSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las liquidación total de las unidades de generación para cada hora
				nodGenerationUnitsSetttlementEnergyMarketCOP[h] = nodGenerationUnitsSetttlementEnergyMarketCOP[h] + this.getGenerationUnits().get(unit).getNodSettlementEnergyMarket()[h];
			}
		}
		this.nodGenerationUnitsSetttlementEnergyMarketCOP = nodGenerationUnitsSetttlementEnergyMarketCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de cargos por uso del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodTransmittersSettlementUsageChargesCOP(){return this.nodTransmittersSettlementUsageChargesCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora
	public void setNodTransmittersSettlementUsageChargesCOP(){
		// variable auxiliar
		double[] nodTransmittersSettlementUsageChargesCOP = new double[24];

		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación cargos por uso del sistema de transmisión nacional por hora
				nodTransmittersSettlementUsageChargesCOP[h] = nodTransmittersSettlementUsageChargesCOP[h] + this.getTransmitters().get(gridco).getNodSettlementUsageChargesCOP()[h];
			}
		}
		this.nodTransmittersSettlementUsageChargesCOP = nodTransmittersSettlementUsageChargesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de cargos complementarios del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodTransmittersSettlementComplementaryChargesCOP(){return this.nodTransmittersSettlementComplementaryChargesCOP;} // obtener liquidación cargos complementarios del sistema de transmisión nacional por hora
	public void setNodTransmittersSettlementComplementaryChargesCOP(){
		// variable auxiliar
		double[] nodTransmittersSettlementComplementaryChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación cargos complementarios del sistema de transmisión nacional por hora
				nodTransmittersSettlementComplementaryChargesCOP[h] = nodTransmittersSettlementComplementaryChargesCOP[h] + this.getTransmitters().get(gridco).getNodSettlementComplementaryChargesCOP()[h];
			}
		}
		this.nodTransmittersSettlementComplementaryChargesCOP = nodTransmittersSettlementComplementaryChargesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de las rentas por congestión del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodTransmitterCongestionRentsCOP(){return this.nodTransmitterCongestionRentsCOP;} // obtener el valor de las rentas por congestión por hora
	public void setNodTransmitterCongestionRentsCOP(){	// establecer el valor de las rentas por congestión por hora
		// variable auxiliar
		double[] nodTransmitterCongestionRentsCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación rentas por congestión del sistema de transmisión nacional por hora
				nodTransmitterCongestionRentsCOP[h] = nodTransmitterCongestionRentsCOP[h] + this.getTransmitters().get(gridco).getNodSettlementCongestionCOP()[h];
			}
		}
		this.nodTransmitterCongestionRentsCOP = nodTransmitterCongestionRentsCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de las rentas por congestión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersCongestionRentsCOP(){return this.nodRetailersCongestionRentsCOP;} // obtener el valor de las rentas por congestión por hora
	public void setNodRetailersCongestionRentsCOP(){	// establecer el valor de las rentas por congestión por hora
		// variable auxiliar
		double[] nodRetailersCongestionRentsCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación rentas por congestión a los comercializadores
				nodRetailersCongestionRentsCOP[h] = nodRetailersCongestionRentsCOP[h] + this.getRetailers().get(ret).getNodSettlementCongestionCOP()[h];
			}
		}
		this.nodRetailersCongestionRentsCOP = nodRetailersCongestionRentsCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de los cargos complementarios de transmisión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersComplementaryChargesCOP(){return this.nodRetailersComplementaryChargesCOP;} // obtener el valor de los cargos complementarios de transmisión a los comercializadores
	public void setNodRetailersComplementaryChargesCOP(){	// establecer el valor de los cargos complementarios de transmisión a los comercializadores
		// variable auxiliar
		double[] nodRetailersComplementaryChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación de los cargos complementarios de transmisión a los comercializadores por hora
				nodRetailersComplementaryChargesCOP[h] = nodRetailersComplementaryChargesCOP[h] + this.getRetailers().get(ret).getNodSettlementComplementaryChargesCOP()[h];
			}
		}
		this.nodRetailersComplementaryChargesCOP = nodRetailersComplementaryChargesCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: obtener y establecer el valor de la liquidación de los cargos por uso de transmisión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getNodRetailersUsageChargesCOP(){return this.nodRetailersUsageChargesCOP;} // obtener el valor de los cargos por uso de transmisión a los comercializadores
	public void setNodRetailersUsageChargesCOP(){	// establecer el valor de los cargos por uso de transmisión a los comercializadores
		// variable auxiliar
		double[] nodRetailersUsageChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación de los cargos por uso de transmisión a los comercializadores
				nodRetailersUsageChargesCOP[h] = nodRetailersUsageChargesCOP[h] + this.getRetailers().get(ret).getNodSettlementUsageChargesCOP()[h];
			}
		}
		this.nodRetailersUsageChargesCOP = nodRetailersUsageChargesCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  mercado nodal: función para establecer los elementos comunes en la liquidación del mercado uninodal y multinodal
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void setNodSettlementEqualElemets(){
		
		// unidades de generación
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPCMWh(this.getGenerationUnits().get(unit).getContractEnergySalesPCMWh().clone());
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPCCOP(this.getGenerationUnits().get(unit).getContractEnergySalesPCCOP().clone());
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPDMWh(this.getGenerationUnits().get(unit).getContractEnergySalesPDMWh().clone());
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPDCOP(this.getGenerationUnits().get(unit).getContractEnergySalesPDCOP().clone());
		}
		
		// comercializadores
		for(int ret = 0; ret < Global.nRetailers; ret++) 
		{
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPCMWh(this.getRetailers().get(ret).getContractEnergyPurchasesPCMWh().clone());
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPCCOP(this.getRetailers().get(ret).getContractEnergyPurchasesPCCOP().clone());
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPDMWh(this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone());
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPDCOP(this.getRetailers().get(ret).getContractEnergyPurchasesPDCOP().clone());
		}
	}
	
	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°_____________________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|												|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	FUNCIONES AUXILIARES MERCADO NODAL CON FTRS	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|_____________________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: obtener y establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersContractEnergyPurchasesPCMWh(){return this.ftrRetailersContractEnergyPurchasesPCMWh;} // obtener la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
	public void setFtrRetailersContractEnergyPurchasesPCMWh(){
		
		// variable auxiliar
		double[] ftrRetailersContractEnergyPurchasesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PC en cada hora de todos los comercializadores
				ftrRetailersContractEnergyPurchasesPCMWh[h] = ftrRetailersContractEnergyPurchasesPCMWh[h] + this.getRetailers().get(ret).getFtrContractEnergyPurchasesPCMWh()[h];
			}
		}
		this.ftrRetailersContractEnergyPurchasesPCMWh = ftrRetailersContractEnergyPurchasesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersContractEnergyPurchasesPCCOP(){return this.ftrRetailersContractEnergyPurchasesPCCOP;} // obtener el valor de las compras en contratos PC en cada hora de todos los comercializadores
	public void setFtrRetailersContractEnergyPurchasesPCCOP(){
		
		// variable auxiliar
		double[] ftrRetailersContractEnergyPurchasesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PC en cada hora de todos los comercializadores
				ftrRetailersContractEnergyPurchasesPCCOP[h] = ftrRetailersContractEnergyPurchasesPCCOP[h] + this.getRetailers().get(ret).getFtrContractEnergyPurchasesPCCOP()[h];
			}
		}
		this.ftrRetailersContractEnergyPurchasesPCCOP = ftrRetailersContractEnergyPurchasesPCCOP;
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersContractEnergyPurchasesPDMWh(){return this.ftrRetailersContractEnergyPurchasesPDMWh;} // obtener la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
	public void setFtrRetailersContractEnergyPurchasesPDMWh(){
		
		// variable auxiliar
		double[] ftrRetailersContractEnergyPurchasesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras en contratos PD en cada hora de todos los comercializadores
				ftrRetailersContractEnergyPurchasesPDMWh[h] = ftrRetailersContractEnergyPurchasesPDMWh[h] + this.getRetailers().get(ret).getFtrContractEnergyPurchasesPDMWh()[h];
			}
		}
		this.ftrRetailersContractEnergyPurchasesPDMWh = ftrRetailersContractEnergyPurchasesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersContractEnergyPurchasesPDCOP(){return this.ftrRetailersContractEnergyPurchasesPDCOP;} // obtener el valor de las compras en contratos PD en cada hora de todos los comercializadores
	public void setFtrRetailersContractEnergyPurchasesPDCOP(){
		
		// variable auxiliar
		double[] ftrRetailersContractEnergyPurchasesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras en contratos PD en cada hora de todos los comercializadores
				ftrRetailersContractEnergyPurchasesPDCOP[h] = ftrRetailersContractEnergyPurchasesPDCOP[h] + this.getRetailers().get(ret).getFtrContractEnergyPurchasesPDCOP()[h];
			}
		}
		this.ftrRetailersContractEnergyPurchasesPDCOP = ftrRetailersContractEnergyPurchasesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersPoolEnergyPurchasesMWh(){return this.ftrRetailersPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todos los comercializadores
	public void setFtrRetailersPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] ftrRetailersPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las compras bolsa en cada hora de todos los comercializadores
				ftrRetailersPoolEnergyPurchasesMWh[h] = ftrRetailersPoolEnergyPurchasesMWh[h] + this.getRetailers().get(ret).getFtrPoolEnergyPurchasesMWh()[h];
			}
		}
		this.ftrRetailersPoolEnergyPurchasesMWh = ftrRetailersPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las compras en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersPoolEnergyPurchasesCOP(){return this.ftrRetailersPoolEnergyPurchasesCOP;} // obtener el valor de las compras en bolsa en cada hora de todos los comercializadores
	public void setFtrRetailersPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] ftrRetailersPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las compras bolsa en cada hora de todos los comercializadores
				ftrRetailersPoolEnergyPurchasesCOP[h] = ftrRetailersPoolEnergyPurchasesCOP[h] + this.getRetailers().get(ret).getFtrPoolEnergyPurchasesCOP()[h];
			}
		}
		this.ftrRetailersPoolEnergyPurchasesCOP = ftrRetailersPoolEnergyPurchasesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersPoolEnergySalesMWh(){return this.ftrRetailersPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todos los comercializadores
	public void setFtrRetailersPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] ftrRetailersPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer la magnitud de las ventas bolsa en cada hora de todos los comercializadores
				ftrRetailersPoolEnergySalesMWh[h] = ftrRetailersPoolEnergySalesMWh[h] + this.getRetailers().get(ret).getFtrPoolEnergySalesMWh()[h];
			}
		}
		this.ftrRetailersPoolEnergySalesMWh = ftrRetailersPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las ventas en bolsa en cada hora de todos los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersPoolEnergySalesCOP(){return this.ftrRetailersPoolEnergySalesCOP;} // obtener el valor de las ventas en bolsa en cada hora de todos los comercializadores
	public void setFtrRetailersPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] ftrRetailersPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las ventas bolsa en cada hora de todos los comercializadores
				ftrRetailersPoolEnergySalesCOP[h] = ftrRetailersPoolEnergySalesCOP[h] + this.getRetailers().get(ret).getFtrPoolEnergySalesCOP()[h];
			}
		}
		this.ftrRetailersPoolEnergySalesCOP = ftrRetailersPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las liquidación total de los comercializadores para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersSetttlementEnergyMarketCOP(){return this.ftrRetailersSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de los comercializadores para cada hora
	public void setFtrRetailersSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] ftrRetailersSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// establecer el valor de las liquidación total de los comercializadores para cada hora
				ftrRetailersSetttlementEnergyMarketCOP[h] = ftrRetailersSetttlementEnergyMarketCOP[h] + this.getRetailers().get(ret).getFtrSettlementEnergyMarket()[h];
			}
		}
		this.ftrRetailersSetttlementEnergyMarketCOP = ftrRetailersSetttlementEnergyMarketCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsContractEnergySalesPCMWh(){return this.ftrGenerationUnitsContractEnergySalesPCMWh;} // obtener la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsContractEnergySalesPCMWh(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsContractEnergySalesPCMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				ftrGenerationUnitsContractEnergySalesPCMWh[h] = ftrGenerationUnitsContractEnergySalesPCMWh[h] + this.getGenerationUnits().get(unit).getFtrContractEnergySalesPCMWh()[h];
			}
		}
		this.ftrGenerationUnitsContractEnergySalesPCMWh = ftrGenerationUnitsContractEnergySalesPCMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsContractEnergySalesPCCOP(){return this.ftrGenerationUnitsContractEnergySalesPCCOP;} // obtener el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsContractEnergySalesPCCOP(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsContractEnergySalesPCCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				ftrGenerationUnitsContractEnergySalesPCCOP[h] = ftrGenerationUnitsContractEnergySalesPCCOP[h] + this.getGenerationUnits().get(unit).getFtrContractEnergySalesPCCOP()[h];
			}
		}
		this.ftrGenerationUnitsContractEnergySalesPCCOP = ftrGenerationUnitsContractEnergySalesPCCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsContractEnergySalesPDMWh(){return this.ftrGenerationUnitsContractEnergySalesPDMWh;} // obtener la magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsContractEnergySalesPDMWh(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsContractEnergySalesPDMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
				ftrGenerationUnitsContractEnergySalesPDMWh[h] = ftrGenerationUnitsContractEnergySalesPDMWh[h] + this.getGenerationUnits().get(unit).getFtrContractEnergySalesPDMWh()[h];
			}
		}
		this.ftrGenerationUnitsContractEnergySalesPDMWh = ftrGenerationUnitsContractEnergySalesPDMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsContractEnergySalesPDCOP(){return this.ftrGenerationUnitsContractEnergySalesPDCOP;} // obtener el valor de las ventas en contratos PD en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsContractEnergySalesPDCOP(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsContractEnergySalesPDCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en contratos PC en cada hora de todas las unidades de generación
				ftrGenerationUnitsContractEnergySalesPDCOP[h] = ftrGenerationUnitsContractEnergySalesPDCOP[h] + this.getGenerationUnits().get(unit).getFtrContractEnergySalesPDCOP()[h];
			}
		}
		this.ftrGenerationUnitsContractEnergySalesPDCOP = ftrGenerationUnitsContractEnergySalesPDCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsPoolEnergySalesMWh(){return this.ftrGenerationUnitsPoolEnergySalesMWh;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsPoolEnergySalesMWh(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsPoolEnergySalesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
				ftrGenerationUnitsPoolEnergySalesMWh[h] = ftrGenerationUnitsPoolEnergySalesMWh[h] + this.getGenerationUnits().get(unit).getFtrPoolEnergySalesMWh()[h];
			}
		}
		this.ftrGenerationUnitsPoolEnergySalesMWh = ftrGenerationUnitsPoolEnergySalesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsPoolEnergySalesCOP(){return this.ftrGenerationUnitsPoolEnergySalesCOP;} // obtener la magnitud de las ventas en bolsa en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsPoolEnergySalesCOP(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsPoolEnergySalesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las ventas en bolsa en cada hora de todas las unidades de generación
				ftrGenerationUnitsPoolEnergySalesCOP[h] = ftrGenerationUnitsPoolEnergySalesCOP[h] + this.getGenerationUnits().get(unit).getFtrPoolEnergySalesCOP()[h];
			}
		}
		this.ftrGenerationUnitsPoolEnergySalesCOP = ftrGenerationUnitsPoolEnergySalesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsPoolEnergyPurchasesMWh(){return this.ftrGenerationUnitsPoolEnergyPurchasesMWh;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsPoolEnergyPurchasesMWh(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsPoolEnergyPurchasesMWh = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				ftrGenerationUnitsPoolEnergyPurchasesMWh[h] = ftrGenerationUnitsPoolEnergyPurchasesMWh[h] + this.getGenerationUnits().get(unit).getFtrPoolEnergyPurchasesMWh()[h];
			}
		}
		this.ftrGenerationUnitsPoolEnergyPurchasesMWh = ftrGenerationUnitsPoolEnergyPurchasesMWh;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las compras en bolsa en cada hora de todas las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsPoolEnergyPurchasesCOP(){return this.ftrGenerationUnitsPoolEnergyPurchasesCOP;} // obtener la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
	public void setFtrGenerationUnitsPoolEnergyPurchasesCOP(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsPoolEnergyPurchasesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer la magnitud de las compras en bolsa en cada hora de todas las unidades de generación
				ftrGenerationUnitsPoolEnergyPurchasesCOP[h] = ftrGenerationUnitsPoolEnergyPurchasesCOP[h] + this.getGenerationUnits().get(unit).getFtrPoolEnergyPurchasesCOP()[h];
			}
		}
		this.ftrGenerationUnitsPoolEnergyPurchasesCOP = ftrGenerationUnitsPoolEnergyPurchasesCOP;
	}

	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de las liquidación total de las unidades de generación para cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrGenerationUnitsSetttlementEnergyMarketCOP(){return this.ftrGenerationUnitsSetttlementEnergyMarketCOP;} // obtener el valor de las liquidación total de las unidades de generación para cada hora
	public void setFtrGenerationUnitsSetttlementEnergyMarketCOP(){
		
		// variable auxiliar
		double[] ftrGenerationUnitsSetttlementEnergyMarketCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int unit = 0; unit < Global.nUnits; unit++)
			{
				// establecer el valor de las liquidación total de las unidades de generación para cada hora
				ftrGenerationUnitsSetttlementEnergyMarketCOP[h] = ftrGenerationUnitsSetttlementEnergyMarketCOP[h] + this.getGenerationUnits().get(unit).getFtrSettlementEnergyMarket()[h];
			}
		}
		this.ftrGenerationUnitsSetttlementEnergyMarketCOP = ftrGenerationUnitsSetttlementEnergyMarketCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de cargos por uso del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrTransmittersSettlementUsageChargesCOP(){return this.ftrTransmittersSettlementUsageChargesCOP;} // obtener liquidación cargos por uso del sistema de transmisión nacional por hora
	public void setFtrTransmittersSettlementUsageChargesCOP(){
		// variable auxiliar
		double[] ftrTransmittersSettlementUsageChargesCOP = new double[24];
		
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación cargos por uso del sistema de transmisión nacional por hora
				ftrTransmittersSettlementUsageChargesCOP[h] = ftrTransmittersSettlementUsageChargesCOP[h] + this.getTransmitters().get(gridco).getFtrSettlementUsageChargesCOP()[h];
			}
		}
		this.ftrTransmittersSettlementUsageChargesCOP = ftrTransmittersSettlementUsageChargesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de las rentas por congestión del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrTransmitterCongestionRentsCOP(){return this.ftrTransmitterCongestionRentsCOP;} // obtener el valor de las rentas por congestión por hora
	public void setFtrTransmitterCongestionRentsCOP(){	// establecer el valor de las rentas por congestión por hora
		// variable auxiliar
		double[] ftrTransmitterCongestionRentsCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación rentas por congestión del sistema de transmisión nacional por hora
				ftrTransmitterCongestionRentsCOP[h] = ftrTransmitterCongestionRentsCOP[h] + this.getTransmitters().get(gridco).getFtrSettlementCongestionCOP()[h];
			}
		}
		this.ftrTransmitterCongestionRentsCOP = ftrTransmitterCongestionRentsCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de las rentas por congestión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersCongestionRentsCOP(){return this.ftrRetailersCongestionRentsCOP;} // obtener el valor de las rentas por congestión por hora
	public void setFtrRetailersCongestionRentsCOP(){	// establecer el valor de las rentas por congestión por hora
		// variable auxiliar
		double[] ftrRetailersCongestionRentsCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación rentas por congestión a los comercializadores
				ftrRetailersCongestionRentsCOP[h] = ftrRetailersCongestionRentsCOP[h] + this.getRetailers().get(ret).getFtrSettlementCongestionCOP()[h];
			}
		}
		this.ftrRetailersCongestionRentsCOP = ftrRetailersCongestionRentsCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de los cargos complementarios de transmisión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersComplementaryChargesCOP(){return this.ftrRetailersComplementaryChargesCOP;} // obtener el valor de los cargos complementarios de transmisión a los comercializadores
	public void setFtrRetailersComplementaryChargesCOP(){	// establecer el valor de los cargos complementarios de transmisión a los comercializadores
		// variable auxiliar
		double[] ftrRetailersComplementaryChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación de los cargos complementarios de transmisión a los comercializadores por hora
				ftrRetailersComplementaryChargesCOP[h] = ftrRetailersComplementaryChargesCOP[h] + this.getRetailers().get(ret).getFtrSettlementComplementaryChargesCOP()[h];
			}
		}
		this.ftrRetailersComplementaryChargesCOP = ftrRetailersComplementaryChargesCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de los cargos por uso de transmisión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersUsageChargesCOP(){return this.ftrRetailersUsageChargesCOP;} // obtener el valor de los cargos por uso de transmisión a los comercializadores
	public void setFtrRetailersUsageChargesCOP(){	// establecer el valor de los cargos por uso de transmisión a los comercializadores
		// variable auxiliar
		double[] ftrRetailersUsageChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación de los cargos por uso de transmisión a los comercializadores
				ftrRetailersUsageChargesCOP[h] = ftrRetailersUsageChargesCOP[h] + this.getRetailers().get(ret).getFtrSettlementUsageChargesCOP()[h];
			}
		}
		this.ftrRetailersUsageChargesCOP = ftrRetailersUsageChargesCOP;
	} 	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: obtener y establecer el valor de la liquidación de cargos complementarios del sistema de transmisión nacional
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrTransmittersSettlementComplementaryChargesCOP(){return this.ftrTransmittersSettlementComplementaryChargesCOP;} // obtener liquidación cargos complementarios del sistema de transmisión nacional por hora
	public void setFtrTransmittersSettlementComplementaryChargesCOP(){
		// variable auxiliar
		double[] ftrTransmittersSettlementComplementaryChargesCOP = new double[24];
		for(int h = 0; h < 24; h++)
		{
			for(int gridco = 0; gridco < Global.nGridcos; gridco++)
			{
				// liquidación cargos complementarios del sistema de transmisión nacional por hora
				ftrTransmittersSettlementComplementaryChargesCOP[h] = ftrTransmittersSettlementComplementaryChargesCOP[h] + this.getTransmitters().get(gridco).getFtrSettlementComplementaryChargesCOP()[h];
			}
		}
		this.ftrTransmittersSettlementComplementaryChargesCOP = ftrTransmittersSettlementComplementaryChargesCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: obtener y establecer los ingresos totales por FTRs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrIncomeSettlementCOP(){return this.ftrSettlementFtrsIncomeCOP;} // obtener liquidación total de los ingresos por FTRs por hora
	public void setFtrIncomeSettlementCOP()
	{
		// variables auxiliares
		double[] ftrsIncomeSettlementCOP = new double[24];	// valor ingresos por los ftrs
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación total de ingresos por FTRs en la hora h
				ftrsIncomeSettlementCOP[h] = ftrsIncomeSettlementCOP[h] + this.getRetailers().get(ret).getFtrSettlementCOP()[h];
			}
		}
		this.ftrSettlementFtrsIncomeCOP = ftrsIncomeSettlementCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: obtener y establecer los ingresos totales de los compradores por los FTRs asignados
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRetailersIncomeSettlementCOP(){return this.ftrRetailersSettlementFtrsIncomeCOP;} // obtener liquidación total de los ingresos por FTRs por hora
	public void setFtrRetailersIncomeSettlementCOP()
	{
		// variables auxiliares
		double[] ftrsRetailersIncomeSettlementCOP = new double[24];	// valor ingresos por los ftrs
		
		for(int h = 0; h < 24; h++)
		{
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// liquidación total de ingresos por FTRs en la hora h
				ftrsRetailersIncomeSettlementCOP[h] = ftrsRetailersIncomeSettlementCOP[h] + this.getRetailers().get(ret).getFtrIncomeSettlementCOP()[h];
			}
		}
		this.ftrRetailersSettlementFtrsIncomeCOP = ftrsRetailersIncomeSettlementCOP;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: obtener y establecer los ingresos totales de los compradores por los FTRs asignados
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double[] getFtrRemainingCongestionRentsCOP(){return this.ftrRemainingCongestionRentsCOP;} // obtener liquidación total de los ingresos por FTRs por hora
	public void setFtrRemainingCongestionRentsCOP()
	{
		// variables auxiliares
		double[] ftrRemainingCongestionRentsCOP = new double[24];	// valor ingresos por los ftrs
		
		for(int h = 0; h < 24; h++)
		{
			// rentas por congestión remanentes después de liquidar FTRs
			ftrRemainingCongestionRentsCOP[h] = Math.abs(this.getFtrCongestionRentsCOP()[h]
					- this.getFtrRetailersIncomeSettlementCOP()[h]);
		}
		this.ftrRemainingCongestionRentsCOP = ftrRemainingCongestionRentsCOP;
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	//  ftrs: función para establecer los elementos comunes en la liquidación del mercado uninodal y multinodal con FTRs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void setFtrSetSettlementEqualElemets(){
		
		// unidades de generación
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPCMWh(this.getGenerationUnits().get(unit).getContractEnergySalesPCMWh().clone());
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPCCOP(this.getGenerationUnits().get(unit).getContractEnergySalesPCCOP().clone());
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPDMWh(this.getGenerationUnits().get(unit).getContractEnergySalesPDMWh().clone());
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPDCOP(this.getGenerationUnits().get(unit).getContractEnergySalesPDCOP().clone());
		}
		
		// comercializadores
		for(int ret = 0; ret < Global.nRetailers; ret++) 
		{
			this.getRetailers().get(ret).setFtrContractEnergyPurchasesPCMWh(this.getRetailers().get(ret).getContractEnergyPurchasesPCMWh().clone());
			this.getRetailers().get(ret).setFtrContractEnergyPurchasesPCCOP(this.getRetailers().get(ret).getContractEnergyPurchasesPCCOP().clone());
			this.getRetailers().get(ret).setFtrContractEnergyPurchasesPDMWh(this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone());
			this.getRetailers().get(ret).setFtrContractEnergyPurchasesPDCOP(this.getRetailers().get(ret).getContractEnergyPurchasesPDCOP().clone());
		}
	}
		

	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°______________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|						|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	FUNCIONES GENERALES	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|______________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */	
	
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
				this.getGenerationContractsPC().add(generationContracts.get(contract));
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
				this.getGenerationContractsPD().add(generationContracts.get(contract));
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
				contractsVector.addAll(this.getRetailers().get(ret).getGenerationContractsPD());
				
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
			this.getRetailers().get(ret).setSortGenerationContractsPD(contractsMatrix);
			//System.out.println(this.retailers.get(ret).getRetailerName() + "\t" + this.retailers.get(ret).getDemandNode().getNodeName());
			//Global.rw.printArraySortContracts(this.retailers.get(ret).getSortGenerationContractsPD());
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para organizar los contratos PD despachados según el precio del mismo en cada hora 
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
			contractsVector.addAll(this.getDispatchedGenerationContractsPD().get(h));
			
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
			this.getGenerationUnits().get(unit).setIdealGeneration(this.getIdealDispatch().getGeneration()[unit]);
			this.getGenerationUnits().get(unit).setRealGeneration(this.getRealDispatch().getGeneration()[unit]);
		}
			
		// demanda, demanda no atendia, y demanda real a cada comercializador
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			this.getRetailers().get(ret).setEnergyDemand(this.getIdealDispatch().getEnergyDemand()[ret]);
			this.getRetailers().get(ret).setServedDemand(this.getRealDispatch().getServedDemand()[ret]);
			this.getRetailers().get(ret).setUnservedDemand(this.getRealDispatch().getUnservedDemand()[ret]);
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
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPC().size(); contract++)
				{
					energyGenerationPC[h] = energyGenerationPC[h] + this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			}
			this.getGenerationUnits().get(unit).setContractEnergySalesPCMWh(energyGenerationPC);
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
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
				{
					energyDemandPC[h] = energyDemandPC[h] + this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			}
			this.getRetailers().get(ret).setContractEnergyPurchasesPCMWh(energyDemandPC);
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
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); contract++)
				{
					// máxima generación comprometida en contratos PD
					maxEnergyGenerationPD[h] = maxEnergyGenerationPD[h] + this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPower()[h];
				}
			}
			//
			// máxima generación comprometida en contratos PD
			//
			this.getGenerationUnits().get(unit).setContractMaxEnergySalesPDMWh(maxEnergyGenerationPD);
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
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPD().size(); contract++)
				{
					// máxima demanda comprometida en contratos PD
					maxEnergyDemandPD[h] = maxEnergyDemandPD[h] + this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getContractPower()[h];
				}
			}
			//
			// máxima demanda comprometida en contratos PD
			//
			this.getRetailers().get(ret).setContractMaxEnergyPurchasesPDMWh(maxEnergyDemandPD);
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
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone();
		}
		
		System.out.println("\n------------------------------ uninodal ----> unidades de generación: ventas en contratos PD 	--------------------------------\n");
		
		// para cada unidad de generación		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// incicialización de las variables auxiliares
			energyGenerationPD = new double[24];
			energyDemandPD = new double[24];
			
			for(int h = 0; h < 24; h++)
			{				
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); contract++)
				{
					if (this.getDispatchedGenerationContractsPD().get(h).contains(this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract)))
					{	
						// demanda a atender en contratos PD para el comercializador ret asociado al contrato
						int ret = this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractBuyerId();
						energyDemandPD[h] = energyDemandPD[h] + Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h], 
								this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPower()[h]);
						
						// actualización de la demanda remanente a atender en contratos PD para el comercializador ret en la hora h
						remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] 
								- Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h], 
										this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPower()[h]);
					}
				}
				// la demanda a atender en contratos pague lo demandado es el mínimo entre la demanda contratada 
				// en este tipo de contratos y el máximo entre cero y la diferencia entre la demanda real 
				// y la demanda atendida en contratos pague lo contratado 
				energyGenerationPD[h] = Math.min(this.getGenerationUnits().get(unit).getContractMaxEnergySalesPDMWh()[h], energyDemandPD[h]);
			}
			//
			// generación real para atender contratos PD
			//
			this.getGenerationUnits().get(unit).setContractEnergySalesPDMWh(energyGenerationPD);
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(energyGenerationPD);
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
		
		System.out.println("\n------------------------------ uninodal ----> comercializadores: compras en contratos PD 	--------------------------------\n");
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización de las variables auxiliares
			energyDemandPD = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// la demanda a atender en contratos pague lo demandado es el mínimo entre la demanda contratada 
				// en este tipo de contratos y el máximo entre cero y la diferencia entre la demanda real 
				// y la demanda atendida en contratos pague lo contratado 
				energyDemandPD[h] = Math.min(this.getRetailers().get(ret).getContractMaxEnergyPurchasesPDMWh()[h],
											Math.max(0,
													this.getRetailers().get(ret).getServedDemand()[h] - this.getRetailers().get(ret).getContractEnergyPurchasesPCMWh()[h]));
			}
			// 
			// demanda real atendida en contratos PD en la hora h
			//
			this.getRetailers().get(ret).setContractEnergyPurchasesPDMWh(energyDemandPD);
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerCod() + "\t");
			Global.rw.printVector(energyDemandPD);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// función para calcular la cantidad de energía negociada en bolsa por cada comercializador y para cada período de despacho
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	/*public void retailersEnergyPurchasesMWh()
	{
		double[] energyPurchases;
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			energyPurchases = new double[24];
			for(int h = 0; h < 24; h++)
			{
				energyPurchases[h] = this.getRetailers().get(ret).getServedDemand()[h] 
										- this.getRetailers().get(ret).getContractEnergyPurchasesPCMWh()[h]
										- this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh()[h]		;
			}
			this.getRetailers().get(ret).setPoolEnergyPurchasesMWh(energyPurchases);
		}
	}*/
	
	/* °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°______________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|								|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	FUNCIONES MERCADO UNINODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|______________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	 * °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	 */	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación del mercado de energía para los comercializadores
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
		
		System.out.println("\n------------------------------ uninodal ----> comercializadores: liquidación inicial del mercado 	--------------------------------\n");
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone();
		
			// inicialización de las varibles auxiliares
			remainingEnergyDemand 			= this.getRetailers().get(ret).getServedDemand().clone();
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
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
				{
					// cálculo del egreso en el contrato PC contract para la hora h
					settlementContractsPC[h] =  settlementContractsPC[h] + 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
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
					settlementPoolEnergySales[h] = this.getIdealDispatch().getSpotPrices()[h] * poolEnergySales[h];
				}
				//
				// si el comercializador posee contratos PD
				//
				hourlyDispatchedGenerationContractsPD = new ArrayList<GenerationContract>();
				for(int contract = 0; contract < this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).size(); contract++)
				{
					// cálculo de la demanda a liquidar en el contrato PD contact en la hora h
					double demandPD = Math.max(0,
							Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h],
							this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPower()[h]));

					// cálculo del egreso en el contrato PD contract para la hora h
					settlementContractsPD[h] = settlementContractsPD[h] 
							+ this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPrice()[h]
							* demandPD; 
										
					// actualización de la demanda de energía remanente para la hora h
					remainingEnergyDemand[h] = remainingEnergyDemand[h] - demandPD;	
					remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] - demandPD;
					
					//
					// actualización energía despachada en contratos PD
					//
					// comerializador
					this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).setDispatchedContractPowerPD(demandPD, h);
					//
					// unidad de generación
					//
					int unit = this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getGenerationUnitId();
					for(int i = 0; i < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); i++)
					{
						if(this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).getContractId() == this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractId())
						{
							this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).setDispatchedContractPowerPD(demandPD, h);
						}
					}
					
					if(demandPD > 0)
					{
						// si el contrato se necesita para atender la demanda entonces el contrato PD se guarda como despachado
						hourlyDispatchedGenerationContractsPD.add(this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract));
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
					settlementPoolEnergyPurchses[h] = this.getIdealDispatch().getSpotPrices()[h] * poolEnergyPurchases[h];
				}
				
				//
				// liquidación general del comercializador
				//
				settlementEnergyMarket[h] = settlementContractsPC[h] + settlementContractsPD[h] 
					+ settlementPoolEnergyPurchses[h] - settlementPoolEnergySales[h]; 
			}
			this.getRetailers().get(ret).setContractEnergyPurchasesPCCOP(settlementContractsPC);
			this.getRetailers().get(ret).setContractEnergyPurchasesPDCOP(settlementContractsPD);
			this.getRetailers().get(ret).setPoolEnergySalesMWh(poolEnergySales);
			this.getRetailers().get(ret).setPoolEnergySalesCOP(settlementPoolEnergySales);
			this.getRetailers().get(ret).setPoolEnergyPurchasesMWh(poolEnergyPurchases);
			this.getRetailers().get(ret).setPoolEnergyPurchasesCOP(settlementPoolEnergyPurchses);
			this.getRetailers().get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerCod() + "\t");
			Global.rw.printVector(settlementEnergyMarket);
			
		}
		// establece los contratos PD despachados para la hora h de todos los comercializadores
		this.setDispatchedGenerationContractsPD(dispatchedGenerationContractsPD);
		//Global.rw.printArrayDispatchContracts(this.getDispatchedGenerationContractsPD());
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación del mercado de energía para las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] remainingIdealGeneration; 				// generación ideal remanente
		double[] settlementContractsPC; 				// ingresos en contratos pague lo contratado
		double[] settlementContractsPD; 				// ingresos en contratos pague lo demandado
		double[] poolEnergyPurchases; 					// compras en bolsa
		double[] settlementPoolEnergyPurchses; 			// egresos por compras en bolsa
		double[] poolEnergySales; 						// ventas en bolsa
		double[] settlementPoolEnergySales; 			// ingresos por ventas en bolsa
		double[] settlementEnergyMarket; 				// ingresos totales de la unidad de generación
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone();
		}
		
		System.out.println("\n------------------------------ uninodal ----> unidades de generación: liquidación inicial del mercado 	--------------------------------\n");
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			remainingIdealGeneration 		= this.getGenerationUnits().get(unit).getIdealGeneration().clone();
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
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPC().size(); contract++)
				{
					// calculo del ingreso en el contrato PC contract para la hora h
					settlementContractsPC[h] =  settlementContractsPC[h] + 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
					
					// actualización de la generación ideal de energía remanente para la hora h
					remainingIdealGeneration[h] = remainingIdealGeneration[h] - 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			
				//
				// si la unidad de generación posee contratos PD
				//
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); contract++)
				{
					// generación a liquidar en el contrato PD contract en la hora h
					double energyPD = this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
					
					// cálculo del ingreso en el contrato PD contract para la hora h
					settlementContractsPD[h] = settlementContractsPD[h] 
							+ this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPrice()[h]
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
					settlementPoolEnergyPurchses[h] = this.getIdealDispatch().getSpotPrices()[h] * poolEnergyPurchases[h];
				}
				else
				{
					// la cantidad vendida en bolsa es la generación remanente 
					poolEnergySales[h] = remainingIdealGeneration[h];
					
					// cálculo del ingreso por ventas en bolsa
					settlementPoolEnergySales[h] = this.getIdealDispatch().getSpotPrices()[h] * poolEnergySales[h];
				}
					
				// liquidación general del comercializador
				settlementEnergyMarket[h] = settlementContractsPC[h] + settlementContractsPD[h] 
						- settlementPoolEnergyPurchses[h] + settlementPoolEnergySales[h];
			}
			this.getGenerationUnits().get(unit).setContractEnergySalesPCCOP(settlementContractsPC);
			this.getGenerationUnits().get(unit).setContractEnergySalesPDCOP(settlementContractsPD);
			this.getGenerationUnits().get(unit).setPoolEnergySalesMWh(poolEnergySales);
			this.getGenerationUnits().get(unit).setPoolEnergySalesCOP(settlementPoolEnergySales);
			this.getGenerationUnits().get(unit).setPoolEnergyPurchasesMWh(poolEnergyPurchases);
			this.getGenerationUnits().get(unit).setPoolEnergyPurchasesCOP(settlementPoolEnergyPurchses);
			this.getGenerationUnits().get(unit).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(settlementEnergyMarket);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación reconciliaciones para las unidades de generación
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
		
		System.out.println("\n-------------------------------- uninodal ----> unidades de generación: liquidación después de reconciliaciones 	--------------------------------\n");
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			idealGeneration 		= this.getGenerationUnits().get(unit).getIdealGeneration().clone();
			realGeneration 			= this.getGenerationUnits().get(unit).getRealGeneration().clone();
			settlementEnergyMarket  = this.getGenerationUnits().get(unit).getSettlementEnergyMarket().clone();
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
					if(this.getGenerationUnits().get(unit).getUnitType().equals("	T	"))
					{							
						// plantas térmicas
						positiveReconciliationCOP[h] = positiveReconciliationMWh[h] * 
								Math.min(this.getReferencePricePositiveReconciliation(), 
										this.getIdealDispatch().getEnergyBidPrice()[unit][h] + this.getGenerationUnits().get(unit).getStartStopPice()/this.getDailyPositiveReconciliationMWh()[unit]);
						
						System.out.println("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t" + h + "\t" + this.getReferencePricePositiveReconciliation() 
										+ "\t" + (this.getIdealDispatch().getEnergyBidPrice()[unit][h] + this.getGenerationUnits().get(unit).getStartStopPice()/this.getDailyPositiveReconciliationMWh()[unit])
										+ "\t" + positiveReconciliationMWh[h] + "\t" + positiveReconciliationCOP[h]);
					}
					else
					{
						// plantas hidráulicas [ asumiendo NEM < NPV ]
						positiveReconciliationCOP[h] = positiveReconciliationMWh[h] * this.getIdealDispatch().getSpotPrices()[h]; 
						
						System.out.println("\t" + this.getGenerationUnits().get(unit).getUnitName()+ "\t" + h + "\t" + positiveReconciliationMWh[h]
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
			this.getGenerationUnits().get(unit).setPositiveReconciliationMWh(positiveReconciliationMWh); // establcer magnitud reconciliación positiva
			this.getGenerationUnits().get(unit).setPositiveReconciliationCOP(positiveReconciliationCOP); // establcer valor reconciliación positiva
			this.getGenerationUnits().get(unit).setNegativeReconciliationMWh(negativeReconciliationMWh); // establcer magnitud reconciliación negativa
			this.getGenerationUnits().get(unit).setNegativeReconciliationCOP(negativeReconciliationCOP); // establcer valor reconciliación negativa
			this.getGenerationUnits().get(unit).setSettlementEnergyMarket(settlementEnergyMarket);		// establecer nuevo valor de la liquidación
			
			// imprimir resultados
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(settlementEnergyMarket);
		}
	}
		
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación restricciones para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void retailersConstraintsSettlement()
	{
		// variables auxiliares
		double[] constraintsCOP; 			// valor restricciones
		double[] settlementEnergyMarket; 	// valor liquidación parcial del mercado	
		
		System.out.println("\n-------------------------------- uninodal ----> comercializadores: restricciones 	--------------------------------\n");
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización de las varibles auxiliares
			constraintsCOP = new double[24];
			settlementEnergyMarket = this.getRetailers().get(ret).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				constraintsCOP[h] = this.getConstraintsCOP()[h] * (this.getRetailers().get(ret).getServedDemand()[h]/this.getServedDemand()[h]); 
				settlementEnergyMarket[h] = settlementEnergyMarket[h] + constraintsCOP[h];
			}
			this.getRetailers().get(ret).setConstraintsCOP(constraintsCOP);
			this.getRetailers().get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(constraintsCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación cargos por uso para los transmisores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void transmittersUsageChargesSettlement(int iteration)
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		
		System.out.println("\n-------------------------------- uninodal ----> transmisores: cargos por uso 	--------------------------------\n");
		
		for( int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para el transmisor gridco
				settlementUsageChargesCOP[h] = this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
						* this.getUsageCharges()[iteration][h]* this.getServedDemand()[h];
			}
			this.getTransmitters().get(gridco).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación cargos por uso para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void retailersUsageChargesSettlementCOP()
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] settlementEnergyMarket; 		// valor liquidación parcial del mercado
		
		System.out.println("\n-------------------------------- uninodal ----> comercializadores: cargos por uso 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			settlementEnergyMarket = this.getRetailers().get(ret).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para el comercializador ret
				settlementUsageChargesCOP[h] = this.getSettlementUsageChargesRetailersCOP()[h] * (this.getRetailers().get(ret).getServedDemand()[h]/this.getServedDemand()[h]);
				settlementEnergyMarket[h] = settlementEnergyMarket[h] + settlementUsageChargesCOP[h];
			}
			this.getRetailers().get(ret).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			this.getRetailers().get(ret).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// uninodal: liquidación cargos por uso para los generadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void generationUnitsUsageChargesSettlementCOP()
	{
		// variables auxiliares
		double[] settlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] settlementEnergyMarket; 		// valor liquidación parcial del mercado
		
		System.out.println("\n-------------------------------- uninodal ----> generadores: cargos por uso 	--------------------------------\n");
		
		for( int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización variables auxiliares
			settlementUsageChargesCOP = new double[24];
			settlementEnergyMarket = this.getGenerationUnits().get(unit).getSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación cargos por uso para cada hora h para la unidad de generación unit
				settlementUsageChargesCOP[h] = this.getSettlementUsageChargesGeneratorsCOP()[h] * (this.getGenerationUnits().get(unit).getRealGeneration()[h]/this.getRealGeneration()[h]);
				settlementEnergyMarket[h] = settlementEnergyMarket[h] - settlementUsageChargesCOP[h];
			}
			this.getGenerationUnits().get(unit).setSettlementUsageChargesCOP(settlementUsageChargesCOP);
			this.getGenerationUnits().get(unit).setSettlementEnergyMarket(settlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(settlementUsageChargesCOP);
		}
	}
	
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°__________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|					|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	MERCADO NODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|__________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	// 
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación del mercado de energía para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void nodRetailersEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] nodRemainingEnergyDemand; 			// demanda remanente
		double[] nodSettlementContractsPC; 			// egresos en contratos pague lo contratado
		double[] nodSettlementContractsPD; 			// egresos en contratos pague lo demandado
		double[] nodPoolEnergyPurchases; 			// compras en bolsa
		double[] nodSettlementPoolEnergyPurchses; 	// egresos por compras en bolsa
		double[] nodPoolEnergySales; 				// ventas en bolsa
		double[] nodSettlementPoolEnergySales; 		// ingresos por ventas en bolsa
		double[] nodSettlementEnergyMarket; 		// egresos totales del comercializador
		/*
		List<List<GenerationContract>> dispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>(); 		// contratos de generación despachados en el día
		List<GenerationContract> hourlyDispatchedGenerationContractsPD; 	// contratos de generación despachados en cada hora
		
		for(int i = 0; i < 24; i++)
		{
			dispatchedGenerationContractsPD.add(new ArrayList<GenerationContract>());
		}*/
		
		// demanda remanente para contratos PD cada comercializador
		double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];
		
		System.out.println("\n------------------------------ nod ----> comercializadores: liquidación inicial mercado de energía 	--------------------------------\n");
		
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{		
			// inicialización de las varibles auxiliares
			nodRemainingEnergyDemand 		= this.getRetailers().get(ret).getServedDemand().clone();
			nodSettlementContractsPC 		= new double[24];
			nodSettlementContractsPD 		= new double[24];
			nodPoolEnergyPurchases 			= new double[24];
			nodSettlementPoolEnergyPurchses = new double[24];
			nodPoolEnergySales 				= new double[24];
			nodSettlementPoolEnergySales 	= new double[24];
			nodSettlementEnergyMarket 		= new double[24];
			remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getNodContractEnergyPurchasesPDMWh().clone();
			//remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getContractEnergyPurchasesPDMWh().clone();
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si el comercializador posee contratos PC
				//
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
				{
					// cálculo del egreso en el contrato PC contract para la hora h
					nodSettlementContractsPC[h] =  nodSettlementContractsPC[h] + 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				
					// actualización de la demanda de energía remanente para la hora h
					nodRemainingEnergyDemand[h] = nodRemainingEnergyDemand[h] - 
						this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
				}			
				//
				// si la demanda contratada en contratos PC es mayor que la demanda real entonces hay ventas en bolsa
				//
				if(nodRemainingEnergyDemand[h] <= 0)
				{
					// la cantidad vendida en bolsa es es la diferencia entre la demanda contratada 
					// en contratos PC y la demanda real
					nodPoolEnergySales[h] = Math.abs(nodRemainingEnergyDemand[h]);
					
					// cálculo del ingreso por ventas en bolsa
					nodSettlementPoolEnergySales[h] = 
							this.getRealDispatch().getNodalPrices()[this.getRetailers().get(ret).getDemandNode().getNodeId()][h] 
									* nodPoolEnergySales[h];
				}
				//
				// si el comercializador posee contratos PD
				//
				//hourlyDispatchedGenerationContractsPD = new ArrayList<GenerationContract>();
				for(int contract = 0; contract < this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).size(); contract++)
				{
					// cálculo de la demanda a liquidar en el contrato PD contact en la hora h
					double demandPD = Math.max(0,
							Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h],
							this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPower()[h]));

					// cálculo del egreso en el contrato PD contract para la hora h
					nodSettlementContractsPD[h] = nodSettlementContractsPD[h] 
							+ this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPrice()[h]
							* demandPD; 
										
					// actualización de la demanda de energía remanente para la hora h
					nodRemainingEnergyDemand[h] = nodRemainingEnergyDemand[h] - demandPD;	
					remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] - demandPD;
					/*
					//
					// actualización energía despachada en contratos PD
					//
					// comercializador
					this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).setDispatchedContractPowerPD(demandPD, h);
					//
					// unidad de generación
					//
					int unit = this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getGenerationUnitId();
					for(int i = 0; i < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); i++)
					{
						if(this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).getContractId() == this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractId())
						{
							this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).setDispatchedContractPowerPD(demandPD, h);
						}
					}
					
					if(demandPD > 0)
					{
						// si el contrato se necesita para atender la demanda entonces el contrato PD se guarda como despachado
						hourlyDispatchedGenerationContractsPD.add(this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract));
					}*/
				}
				// agregar los contratos despachados para la hora h que pertenecen al comercializador ret
				//dispatchedGenerationContractsPD.get(h).addAll(hourlyDispatchedGenerationContractsPD);
							
				//
				// si después de atender la demanda con contratos PC y PD aun hay demanda remanente por atender entonces
				// el comercializador tienen que comprar energía en la bolsa
				//
				if(nodRemainingEnergyDemand[h] > 0)
				{					
					// la cantidad comprada en bolsa es la demanda remanente equivalente a la demanda real menos la
					// demanda atendida en contratos PC y PD
					nodPoolEnergyPurchases[h] = nodRemainingEnergyDemand[h];
					
					// cálculo del egreso por compras en bolsa
					nodSettlementPoolEnergyPurchses[h] = 
							this.getRealDispatch().getNodalPrices()[this.getRetailers().get(ret).getDemandNode().getNodeId()][h] 
									* nodPoolEnergyPurchases[h];
				}
				
				//
				// liquidación general del comercializador
				//
				nodSettlementEnergyMarket[h] = nodSettlementContractsPC[h] + nodSettlementContractsPD[h] 
					+ nodSettlementPoolEnergyPurchses[h] - nodSettlementPoolEnergySales[h]; 
			}
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPCCOP(nodSettlementContractsPC);
			this.getRetailers().get(ret).setNodContractEnergyPurchasesPDCOP(nodSettlementContractsPD);
			this.getRetailers().get(ret).setNodPoolEnergySalesMWh(nodPoolEnergySales);
			this.getRetailers().get(ret).setNodPoolEnergySalesCOP(nodSettlementPoolEnergySales);
			this.getRetailers().get(ret).setNodPoolEnergyPurchasesMWh(nodPoolEnergyPurchases);
			this.getRetailers().get(ret).setNodPoolEnergyPurchasesCOP(nodSettlementPoolEnergyPurchses);
			this.getRetailers().get(ret).setNodSettlementEnergyMarket(nodSettlementEnergyMarket);
			
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerCod()+ "\t");
			Global.rw.printVector(nodSettlementEnergyMarket);
		}
		// establece los despachos contratados para la hora h de todos los comercializadores
		//this.setDispatchedGenerationContractsPD(dispatchedGenerationContractsPD);
		//Global.rw.printArrayDispatchContracts(this.getDispatchedGenerationContractsPD());
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación del mercado de energía para las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodGenerationUnitsEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] nodRemainingRealGeneration; 				// generación real remanente
		double[] nodSettlementContractsPC; 					// ingresos en contratos pague lo contratado
		double[] nodSettlementContractsPD; 					// ingresos en contratos pague lo demandado
		double[] nodPoolEnergyPurchases; 					// compras en bolsa
		double[] nodSettlementPoolEnergyPurchses; 			// egresos por compras en bolsa
		double[] nodPoolEnergySales; 						// ventas en bolsa
		double[] nodSettlementPoolEnergySales; 				// ingresos por ventas en bolsa
		double[] nodSettlementEnergyMarket; 				// ingresos totales de la unidad de generación
		
		System.out.println("\n------------------------------ nod ----> generadores: liquidación inicial mercado de energía 	--------------------------------\n");
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			nodRemainingRealGeneration 			= this.getGenerationUnits().get(unit).getRealGeneration().clone();
			nodSettlementContractsPC 			= new double[24];
			nodSettlementContractsPD 			= new double[24];
			nodPoolEnergyPurchases 				= new double[24];
			nodSettlementPoolEnergyPurchses 	= new double[24];
			nodPoolEnergySales 					= new double[24];
			nodSettlementPoolEnergySales 		= new double[24];
			nodSettlementEnergyMarket 			= new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si la unidad de generación posee contratos PC
				//
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPC().size(); contract++)
				{
					// calculo del ingreso en el contrato PC contract para la hora h
					nodSettlementContractsPC[h] =  nodSettlementContractsPC[h] + 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
					
					// actualización de la generación ideal de energía remanente para la hora h
					nodRemainingRealGeneration[h] = nodRemainingRealGeneration[h] - 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			
				//
				// si la unidad de generación posee contratos PD
				//
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); contract++)
				{
					// generación a liquidar en el contrato PD contract en la hora h
					double energyPD = this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
					
					// cálculo del ingreso en el contrato PD contract para la hora h
					nodSettlementContractsPD[h] = nodSettlementContractsPD[h] 
							+ this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPrice()[h]
							* energyPD; 
					
					// actualización de generación ideal remanente para la hora h
					nodRemainingRealGeneration[h] = nodRemainingRealGeneration[h] - energyPD;
				}
				
				//
				// si después de cumplir con los compromisos en contratos PC y PD hay exceso o faltante de generación ideal, 
				// entonces la unidad de generación vende o compra en bolsa respectívamente
				//
				if(nodRemainingRealGeneration[h] <= 0)
				{
					// la cantidad comprada en bolsa es la generación remanente 
					nodPoolEnergyPurchases[h] = Math.abs(nodRemainingRealGeneration[h]);
					
					// cálculo del egreso por compras en bolsa
					nodSettlementPoolEnergyPurchses[h] = 
							this.getRealDispatch().getNodalPrices()[this.getGenerationUnits().get(unit).getNode().getNodeId()][h] 
									* nodPoolEnergyPurchases[h];
				}
				else
				{
					// la cantidad vendida en bolsa es la generación remanente 
					nodPoolEnergySales[h] = nodRemainingRealGeneration[h];
					
					// cálculo del ingreso por ventas en bolsa
					nodSettlementPoolEnergySales[h] = 
							this.getRealDispatch().getNodalPrices()[this.getGenerationUnits().get(unit).getNode().getNodeId()][h] 
									* nodPoolEnergySales[h];
				}
					
				// liquidación general de la unidad de generación
				nodSettlementEnergyMarket[h] = nodSettlementContractsPC[h] + nodSettlementContractsPD[h] 
						- nodSettlementPoolEnergyPurchses[h] + nodSettlementPoolEnergySales[h];
			}
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPCCOP(nodSettlementContractsPC);
			this.getGenerationUnits().get(unit).setNodContractEnergySalesPDCOP(nodSettlementContractsPD);
			this.getGenerationUnits().get(unit).setNodPoolEnergySalesMWh(nodPoolEnergySales);
			this.getGenerationUnits().get(unit).setNodPoolEnergySalesCOP(nodSettlementPoolEnergySales);
			this.getGenerationUnits().get(unit).setNodPoolEnergyPurchasesMWh(nodPoolEnergyPurchases);
			this.getGenerationUnits().get(unit).setNodPoolEnergyPurchasesCOP(nodSettlementPoolEnergyPurchses);
			this.getGenerationUnits().get(unit).setNodSettlementEnergyMarket(nodSettlementEnergyMarket);
			
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(nodSettlementEnergyMarket);
		}
	}	

	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de la diferencia entre pagos por energía de los comercializadores y los ingresos de los generadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodDifferenceSettlementCOP5()
	{
		// variables auxiliares
		double[] nodCongestionRents = new double[24]; 	// valor de la diferencia por hora
		
		for(int h = 0; h < 24; h++)
		{
			// inicialización de las varibles auxiliares
			nodCongestionRents[h] 	= Math.max(0,
					Math.abs(this.getNodRetailersSetttlementEnergyMarketCOP()[h]
							- this.getNodGenerationUnitsSetttlementEnergyMarketCOP()[h]));
		}
		
		this.setNodCongestionRentsCOP(nodCongestionRents);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de los cargos complementarios para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodTransmittersComplementaryChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] nodTransmittersComplementaryChargesSettlementCOP;			// valor cargos complementarios por hora para cada transmisor
		
		System.out.println("\n------------------------------ nod ----> transmisores: cargos complementarios 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			nodTransmittersComplementaryChargesSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				nodTransmittersComplementaryChargesSettlementCOP[h] = 
						Global.proportionComplementaryCharge*this.getTransmitters().get(gridco).getSettlementUsageChargesCOP()[h];
			}
			// establecer el valor de los cargos complementarios a cada transmisor
			this.getTransmitters().get(gridco).setNodSettlementComplementaryChargesCOP(nodTransmittersComplementaryChargesSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(nodTransmittersComplementaryChargesSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de las rentas por congestión para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodTransmittersCongestionSettlementCOP()
	{
		// variables auxiliares
		double[] nodTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		
		System.out.println("\n------------------------------ nod ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			nodTransmittersCongestionSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				for(int line = 0; line < Global.nLines; line++)
				{
					if(this.getTransmitters().get(gridco).equals(this.getTransmissionLines().get(line).getLineOwner()))
					{
						nodTransmittersCongestionSettlementCOP[h] = nodTransmittersCongestionSettlementCOP[h] + 
								Math.abs(this.getRealDispatch().getFlows()[this.getTransmissionLines().get(line).getLineID()][h])
								*(this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getEndNode().getNodeId()][h]
								- this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getSourceNode().getNodeId()][h]);
					
					}
				}
			}
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setNodSettlementCongestionCOP(nodTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(nodTransmittersCongestionSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de las rentas por congestión para cada transmisor [ALTERNATIVA]
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodTransmittersCongestionSettlementCOP1()
	{
		// variables auxiliares
		double[] nodTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		double[] nodCongestionRentsCOP = this.getNodRetailersCongestionRentsCOP().clone(); 	// rentas por congestión recolectadas de los comercializadores
		
		System.out.println("\n------------------------------ nod ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			nodTransmittersCongestionSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// rentas por congestión en proporción a la participación del sistema de transmisión nacional
				nodTransmittersCongestionSettlementCOP[h] = 
						this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
						* nodCongestionRentsCOP[h];
			}
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setNodSettlementCongestionCOP(nodTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(nodTransmittersCongestionSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de las rentas por congestión para cada transmisor [ALTERNATIVA]
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodTransmittersCongestionSettlementCOP5()
	{
		// variables auxiliares
		double[] nodTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		double[] nodCongestionRentsCOP = 
				Global.factory.productEscalarVector(
						this.getNodCongestionRentsCOP().clone(),
						Global.proportionCongestionRentsTransmitters); 	// rentas por congestión recolectadas de los comercializadores
		
		System.out.println("\n------------------------------ nod ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			nodTransmittersCongestionSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				// rentas por congestión en proporción a la participación del sistema de transmisión nacional
				nodTransmittersCongestionSettlementCOP[h] = 
						this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
						* nodCongestionRentsCOP[h];
			}
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setNodSettlementCongestionCOP(nodTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(nodTransmittersCongestionSettlementCOP);
		}
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: cálculo de los cargos por uso para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodTransmittersUsageChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] nodTransmittersUsageChargesSettlementCOP;			// valor cargos por uso por hora para cada transmisor
		
		System.out.println("\n------------------------------ nod ----> transmisores: cargos por uso 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			nodTransmittersUsageChargesSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				nodTransmittersUsageChargesSettlementCOP[h] = 
						this.getTransmitters().get(gridco).getNodSettlementComplementaryChargesCOP()[h] +
						this.getTransmitters().get(gridco).getNodSettlementCongestionCOP()[h];

			}
			// establecer el valor de los cargos por uso a cada transmisor
			this.getTransmitters().get(gridco).setNodSettlementUsageChargesCOP(nodTransmittersUsageChargesSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(nodTransmittersUsageChargesSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación cargos complementarios a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodRetailersComplementaryChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] nodSettlementComplementaryChargesCOP; 	// valor liquidación cargos complementarios	
		double[] nodSettlementEnergyMarket; 			// valor liquidación parcial del mercado
		
		System.out.println("\n------------------------------ nod ----> comercializadores: cargos complementarios 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			nodSettlementComplementaryChargesCOP = new double[24];
			nodSettlementEnergyMarket = this.getRetailers().get(ret).getNodSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				nodSettlementComplementaryChargesCOP[h] = this.getNodTransmittersSettlementComplementaryChargesCOP()[h]
						*(this.getRetailers().get(ret).getServedDemand()[h]/this.getServedDemand()[h]);
				
				// actualización de la liquidación total del mercado
				nodSettlementEnergyMarket[h] = nodSettlementEnergyMarket[h] + nodSettlementComplementaryChargesCOP[h];
			}
			this.getRetailers().get(ret).setNodSettlementComplementaryChargesCOP(nodSettlementComplementaryChargesCOP);
			this.getRetailers().get(ret).setNodSettlementEnergyMarket(nodSettlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(nodSettlementComplementaryChargesCOP);
			//Global.rw.printVector(nodSettlementEnergyMarket);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación rentas por congestión a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodRetailersCongestionSettlementCOP()
	{
		// variables auxiliares
		double[] nodSettlementCongestionCOP; 	// valor liquidación rentas por congestión
		double[] nodRemainingEnergyDemand; 				// magnitud de la demanda de energía de cada comercializador
		
		System.out.println("\n------------------------------ nod ----> comercializadores: rentas por congestión 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			nodSettlementCongestionCOP = new double[24];
			nodRemainingEnergyDemand = this.getRetailers().get(ret).getServedDemand().clone();
			
			for(int h = 0; h < 24; h++)
			{
				// liquidación rentas por congestión correspondientes a contratos PC para cada hora h para el comercializador ret
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
				{
					nodSettlementCongestionCOP[h] = nodSettlementCongestionCOP[h]
							+ Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
									nodRemainingEnergyDemand[h])									
									* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getWithdrawalNodeId()][h]
											- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getSourceNodeId()][h]));
					
					// actualización del valor de la remanda remanente tras el contrato PC 
					nodRemainingEnergyDemand[h] = nodRemainingEnergyDemand[h] - 
							Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h], nodRemainingEnergyDemand[h]);
				}
				// liquidación rentas por congestión correspondientes a contratos PD para cada hora h para el comercializador ret
				for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPD().size(); contract++)
				{
					nodSettlementCongestionCOP[h] = nodSettlementCongestionCOP[h]
							+ this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h]
									* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getWithdrawalNodeId()][h]
											- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getSourceNodeId()][h]));
					
					// actualización del valor de la remanda remanente tras el contrato PD 
					nodRemainingEnergyDemand[h] = nodRemainingEnergyDemand[h] - this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
				}
			}
			this.getRetailers().get(ret).setNodSettlementCongestionCOP(nodSettlementCongestionCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(nodSettlementCongestionCOP);
			//Global.rw.printVector(nodRemainingEnergyDemand);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación cargos por uso a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodRetailersUsageChargesSettlementCOP()
	{
		// variables auxiliares
		double[] nodSettlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] nodSettlementEnergyMarket; 	// valor liquidación parcial del mercado
		
		System.out.println("\n------------------------------ nod ----> comercializadores: cargos por uso 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			nodSettlementUsageChargesCOP = new double[24];
			nodSettlementEnergyMarket = this.getRetailers().get(ret).getNodSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				nodSettlementUsageChargesCOP[h] = 
						this.getRetailers().get(ret).getNodSettlementComplementaryChargesCOP()[h] 
								+ this.getRetailers().get(ret).getNodSettlementCongestionCOP()[h];
				
				// actualización de la liquidación total del mercado
				nodSettlementEnergyMarket[h] = nodSettlementEnergyMarket[h] + nodSettlementUsageChargesCOP[h];
			}
			this.getRetailers().get(ret).setNodSettlementUsageChargesCOP(nodSettlementUsageChargesCOP);
			this.getRetailers().get(ret).setNodSettlementEnergyMarket(nodSettlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(nodSettlementUsageChargesCOP);
			//Global.rw.printVector(nodSettlementEnergyMarket);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// mercado nodal: liquidación cargos por uso a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void nodRetailersUsageChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] nodSettlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		//double[] nodSettlementEnergyMarket; 	// valor liquidación parcial del mercado
		
		System.out.println("\n------------------------------ nod ----> comercializadores: cargos por uso 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			nodSettlementUsageChargesCOP = new double[24];
			//nodSettlementEnergyMarket = this.getRetailers().get(ret).getNodSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				nodSettlementUsageChargesCOP[h] = this.getNodTransmittersSettlementUsageChargesCOP()[h]
						* (this.getRetailers().get(ret).getServedDemand()[h]/this.getServedDemand()[h]);
				
				// actualización de la liquidación total del mercado
				//nodSettlementEnergyMarket[h] = nodSettlementEnergyMarket[h] + nodSettlementUsageChargesCOP[h];
			}
			this.getRetailers().get(ret).setNodSettlementUsageChargesCOP(nodSettlementUsageChargesCOP);
			//this.getRetailers().get(ret).setNodSettlementEnergyMarket(nodSettlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(nodSettlementUsageChargesCOP);
			//Global.rw.printVector(nodSettlementEnergyMarket);
		}
	}
		
		
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°_________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|							|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	MERCADO NODAL CON FTRS	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|_________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
	// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° 
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: definir la capacidad de transmisión a subastar
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void setFtrAuctionCapacity(){
		// variables auxiliares
		double[] ftrAuctionCapacity = new double[24];								// capacidad a subastar = media de la demanda histórica
		double[] hourlyEnergyDemand; 												// demanda 
		List<double[]> historicalHourlyEnergyDemand = new ArrayList<double[]>();	// demanda histórica horaria
		historicalHourlyEnergyDemand.addAll(this.getHistoricalHourlyEnergyDemand());
		
		try{			
			for(int h = 0; h < 24; h++)
			{
				hourlyEnergyDemand = new double[historicalHourlyEnergyDemand.size()];
				for(int i = 0; i < historicalHourlyEnergyDemand.size(); i++)
				{
					hourlyEnergyDemand[i] = historicalHourlyEnergyDemand.get(i)[h];
				}
				ftrAuctionCapacity[h] = MathFuns.Max(hourlyEnergyDemand);
			}				
			this.setFtrAuctionCapacity(ftrAuctionCapacity);
		}
		catch (Exception e) {
			System.out.println("operator: setFtrAuctionCapacity ->"+e);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de la diferencia entre pagos por energía de los comercializadores y los ingresos de los generadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrDifferenceSettlementCOP5()
	{
		// variables auxiliares
		double[] ftrCongestionRents = new double[24]; 	// valor de la diferencia por hora
		
		for(int h = 0; h < 24; h++)
		{
			// inicialización de las varibles auxiliares
			ftrCongestionRents[h] 	= Math.max(0,
					Math.abs(this.getFtrRetailersSetttlementEnergyMarketCOP()[h]
							- this.getFtrGenerationUnitsSetttlementEnergyMarketCOP()[h]));
		}
		
		this.setFtrCongestionRentsCOP(ftrCongestionRents);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// 
	// ftrs: función para calcular el precio de reserva del generador para los FTRs en todas las horas
	//
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	public double[] calculateFtrReservePrice(int iteration){
		// variables auxiliares
		double[] nodalPriceDifferences; 			// diferencias de precios nodales
		double[] ftrHourlyReservePriceForContracts; // precio de reserva
		double[] ftrReservePrice = new double[24];	// precio de reserva por hora
		double cumSumPriceDifferences;
		try{
			for(int h = 0; h < 24; h++)
			{
				ftrHourlyReservePriceForContracts = new double[this.getGenerationContracts().size()];
				for(int contract = 0; contract < this.getGenerationContracts().size(); contract++)
				{				
					// inicialización del vector de diferencias de precios nodales
					nodalPriceDifferences = new double[Global.nlags];
					cumSumPriceDifferences = 0.0;
					int k = 0;
					for(int i = 0; i < Global.nlags; i++)
					{
						// cálculo del vector de diferencias de precios nodales
						nodalPriceDifferences[i] = 
								this.getHistoricalNodalPrices().get(iteration - i)[this.getGenerationContracts().get(contract).getWithdrawalNodeId()*24 + h] 
								- this.getHistoricalNodalPrices().get(iteration - i)[this.getGenerationContracts().get(contract).getSourceNodeId()*24 + h];
						if(nodalPriceDifferences[i] > 0)
						{
							cumSumPriceDifferences = cumSumPriceDifferences + nodalPriceDifferences[i];
							k = k + 1; 
						}
					}
					// ftrHourlyReservePriceForContracts es igual a la media de las pasadas
					// Global.nlags diferencias de precios nodales entre los nodos de destino y origen en cada contrato 
					ftrHourlyReservePriceForContracts[contract] = cumSumPriceDifferences/k;
				}
				// ftrReservePrice en la hora h es igual a la media de los precios de reserva calculados para cada contrato en dicha hora
				ftrReservePrice[h] = MathFuns.Mean(ftrHourlyReservePriceForContracts);
				
			}
			this.setFtrReservePrice(ftrReservePrice);
		}
		catch(Exception e)
		{
            System.out.println("operator: calculateFtrReservePrice ->"+e);
        }
		return this.getFtrReservePrice();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// 
	// ftrs: función para calcular el precio de reserva del generador para los FTRs en todas las horas
	//
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	public List<List<Ftr>> defineFtrs(int iteration){
		// variables auxiliares
		List<List<Ftr>> listFtrs = new ArrayList<List<Ftr>>(24); // lista de todos los FTRs
		List<Ftr> ftrs; 				// lista de ftrs para una hora dada
		double[] priceDifferences;		// diferencia de precios por hora 
		double[] ftrReservePrice; 		// precio de reserva por hora
		try{
			ftrReservePrice = new double[24];
			for(int h = 0; h < 24; h++)
			{
				ftrs = new ArrayList<Ftr>();
				for(int i = 0; i < this.getNodes().size()-1; i++)
				{	
					for(int j = i+1; j < this.getNodes().size(); j++)
					{
						double nodalPriceDifference = this.getRealDispatch().getNodalPrices()[j][h] - this.getRealDispatch().getNodalPrices()[i][h];
						if(nodalPriceDifference >= 0.0)
						{
							ftrs.add(new Ftr("Operador", 0.0,nodalPriceDifference, 
									this.getNodes().get(i), this.getNodes().get(j), iteration + 1, 30));
						}
						else
						{
							ftrs.add(new Ftr("Operador", 0.0,Math.abs(nodalPriceDifference), 
									this.getNodes().get(j), this.getNodes().get(i), iteration + 1, 30));
						}	
					}
				}
				// cálculo del precio de reserva a partir de las diferencias de precios nodales en cada hora
				priceDifferences = new double[ftrs.size()];
				for(int i = 0; i < ftrs.size(); i++)
				{
					priceDifferences[i] = ftrs.get(i).getHourlyFtrPrice();
				}
				// precio de reserva par ala hora h
				ftrReservePrice[h] = MathFuns.MinNonZero(priceDifferences);
				
				// adición de la lista de FTRs en la hora h a la lista general de FTRs
				listFtrs.add(ftrs);
			}
			this.setFtrReservePrice(ftrReservePrice);
		}
		catch(Exception e)
		{
            System.out.println("operator: defineFtrs ->"+e);
        }
		return listFtrs;
	}
		
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: función para determinar el precio de reserva del operador para los FTRs de una hora dada
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public double calculateHourlyFtrReservePrice(int iteration, int h, int round, List<Ftr> ftrBids){
		try{
			// si es la primera ronda, el precio de reserva para los FTRs de la hora hour, es el 
			// calculado como el promedio de los precios de reserva de los contratos para dicha hora
			if (round == 0){
				this.setFtrHourlyReservePrice(this.getFtrReservePrice()[h]);
			}
			else {
				// sino, el precio de reserva se actualiza mediante un factor de incremento
				double[] bidPrices= new double[ftrBids.size()];
				for(int i = 0; i < ftrBids.size(); i++)
				{
					bidPrices[i] = ftrBids.get(i).getHourlyFtrPrice();
				}
				double minBidPrice = MathFuns.Min(bidPrices); 
				//double ftrNewPrice = this.getFtrProductPrice()[h]  + Global.ftrPrecentPriceIncrement*this.getFtrProductPrice()[h];
				this.setFtrHourlyReservePrice(minBidPrice + Global.ftrPrecentPriceIncrement*minBidPrice);
				this.getFtrProductPrice()[h] = this.getFtrHourlyReservePrice();
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: calculateFtrReservePrice ->"+e);
        }
		// retorna el nuevo precio de reserva del operador para los FTRs de la hora hour
		return this.getFtrHourlyReservePrice();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: función para determinar la demanda total horaria de capacidad en FTRs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public double ftrCalculateHourlyDemand(List<Ftr> ftrBids){
		// variables auxiliares
		double ftrHourlyDemand = 0.0;
		try{
			if(ftrBids.isEmpty())
			{
				ftrHourlyDemand = 0.0; 
			}
			else
			{
				for(int i = 0; i < ftrBids.size(); i++)
				{
					ftrHourlyDemand = ftrHourlyDemand + ftrBids.get(i).getHourlyFtrPower();
				}
			}
			
		}
		catch(Exception e)
		{
            System.out.println("operator: ftrCalculateHourlyDemand ->"+e);
        }
		// retorna la demanda total en FTRs
		return ftrHourlyDemand ;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: lista de FTRs asignados en cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrHourlyAssigns(int h, List<Ftr> ftrBids, double ftrHourlyAcutionCapacity, double ftrHourlyAuctionPrice, 
			int ftrInitialDate, int ftrDuration){
		// variables auxiliares
		List<Ftr>  ftrHourlyAssigns = new ArrayList<Ftr>();
		//List<List<Ftr>>  ftrAssigns = new ArrayList<List<Ftr>>();
		try{
			// se copian los ftrs que persisten en la ronda final de la subasta
			ftrHourlyAssigns.addAll(ftrBids);
			for(int ftr = 0; ftr < ftrBids.size(); ftr++){
				// se determina la capacidad asignada en cada FTR
				ftrHourlyAssigns.get(ftr).setHourlyFtrPower(Math.min(ftrHourlyAssigns.get(ftr).getHourlyFtrPower(),
															ftrHourlyAcutionCapacity));
				// el precio del FTR asignado es el precio de cierre de la subasta del FTR en dicha hora
				ftrHourlyAssigns.get(ftr).setHourlyFtrPrice(ftrHourlyAuctionPrice);
				// definicón de la fecha y la duración del FTR
				ftrHourlyAssigns.get(ftr).setFtrInitialDate(ftrInitialDate);
				ftrHourlyAssigns.get(ftr).setFtrDuration(ftrDuration);
				
				// actualización de la cpacidad remanente para ser asignada en los demás FTRs de la hora h
				ftrHourlyAcutionCapacity = ftrHourlyAcutionCapacity -
											Math.min(ftrHourlyAssigns.get(ftr).getHourlyFtrPower(),
													 ftrHourlyAcutionCapacity);	
			}
			// adición de los FTRs asignados en la hora h a la lista de FTRs asignados en toda la subasta
			this.getFtrAuction().setFtrHourlyAssigns(ftrHourlyAssigns);
			if(ftrHourlyAssigns.isEmpty())
			{
				this.getFtrAuction().getFtrAssigns().add(h, new ArrayList<Ftr>());
			}else
			{
				this.getFtrAuction().getFtrAssigns().add(h,ftrHourlyAssigns);
			}
			System.out.println(this.getFtrAuction().getFtrAssigns().get(h).size());
		}
		catch (Exception e) {
			System.out.println("operator: ftrHourlyAssigns ->"+e);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: lista de FTRs asignados en cada hora
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrHourlyAssigns5(int h, List<Ftr> ftrBids, double ftrHourlyAcutionCapacity, double ftrHourlyAuctionPrice, 
			int ftrInitialDate, int ftrDuration){
		// variables auxiliares
		List<Ftr>  ftrHourlyAssigns = new ArrayList<Ftr>();
		//List<List<Ftr>>  ftrAssigns = new ArrayList<List<Ftr>>();
		try{
			// se copian los ftrs que persisten en la ronda final de la subasta
			ftrHourlyAssigns.addAll(ftrBids);
			for(int ftr = 0; ftr < ftrBids.size(); ftr++){
				// se determina la capacidad asignada en cada FTR
				ftrHourlyAssigns.get(ftr).setHourlyFtrPower(Math.min(ftrHourlyAssigns.get(ftr).getHourlyFtrPower(),
															ftrHourlyAcutionCapacity));
				// el precio del FTR asignado es el precio de cierre de la subasta del FTR en dicha hora
				//ftrHourlyAssigns.get(ftr).setHourlyFtrPrice(ftrHourlyAuctionPrice);
				// definicón de la fecha y la duración del FTR
				ftrHourlyAssigns.get(ftr).setFtrInitialDate(ftrInitialDate);
				ftrHourlyAssigns.get(ftr).setFtrDuration(ftrDuration);
				
				// actualización de la cpacidad remanente para ser asignada en los demás FTRs de la hora h
				ftrHourlyAcutionCapacity = ftrHourlyAcutionCapacity -
											Math.min(ftrHourlyAssigns.get(ftr).getHourlyFtrPower(),
													 ftrHourlyAcutionCapacity);	
			}
			// adición de los FTRs asignados en la hora h a la lista de FTRs asignados en toda la subasta
			this.getFtrAuction().setFtrHourlyAssigns(ftrHourlyAssigns);
			if(ftrHourlyAssigns.isEmpty())
			{
				this.getFtrAuction().getFtrAssigns().add(h, new ArrayList<Ftr>());
			}else
			{
				this.getFtrAuction().getFtrAssigns().add(h,ftrHourlyAssigns);
			}
			System.out.println(this.getFtrAuction().getFtrAssigns().get(h).size());
		}
		catch (Exception e) {
			System.out.println("operator: ftrHourlyAssigns ->"+e);
		}
	}	
		
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: función para organizar los ftrs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrFtrOrganizer(int h, List<Ftr> organizedFtrs, List<Ftr> ftrBids){
		// variables auxiliares
		//List<Ftr>  ftrHourlyAssigns = new ArrayList<Ftr>();
		//List<List<Ftr>>  ftrAssigns = new ArrayList<List<Ftr>>();
		
		try{
			if(!ftrBids.isEmpty())
			{
				System.out.println(1);
				if(organizedFtrs.isEmpty())
				{
					System.out.println(2);
					for(int i = 0; i < ftrBids.size(); i++)
					{
						System.out.println(3);
						Ftr newftr= new Ftr(new Retailer(), new GenerationContract(), new double[24],new double[24],new Node(), new Node());
						System.out.println(4);
						newftr.setBidderR(ftrBids.get(i).getBidderR());
						System.out.println(5);
						newftr.setGenerationContract(ftrBids.get(i).getGenerationContract());
						System.out.println(6);
						newftr.setFtrPower(ftrBids.get(i).getHourlyFtrPower(),h);
						System.out.println(7);
						newftr.setFtrPower(ftrBids.get(i).getHourlyFtrPrice(),h);
						newftr.setFtrSourceNode(ftrBids.get(i).getFtrSourceNode());
						newftr.setFtrEndNode(ftrBids.get(i).getFtrEndNode());
						organizedFtrs.add(newftr);
						System.out.println(8);
					}
				}
				else
				{
					for(int i = 0; i < ftrBids.size(); i++)
					{
						boolean equal = false;
						for (int ftr = 0; ftr < organizedFtrs.size(); ftr++)
						{
							if(ftrBids.get(i).getGenerationContract().getContractId() == organizedFtrs.get(ftr).getGenerationContract().getContractId())
							{
								equal = true;
								organizedFtrs.get(ftr).getFtrPower()[h] = ftrBids.get(i).getHourlyFtrPower();
								organizedFtrs.get(ftr).getFtrPrice()[h] = ftrBids.get(i).getHourlyFtrPrice();
							}
						}
						if(equal == false)
						{
							Ftr newftr= new Ftr(new Retailer(), new GenerationContract(), new double[24],new double[24],new Node(), new Node());
							newftr.setBidderR(ftrBids.get(i).getBidderR());
							newftr.setGenerationContract(ftrBids.get(i).getGenerationContract());
							newftr.setFtrPower(ftrBids.get(i).getHourlyFtrPower(),h);
							newftr.setFtrPower(ftrBids.get(i).getHourlyFtrPrice(),h);
							newftr.setFtrSourceNode(ftrBids.get(i).getFtrSourceNode());
							newftr.setFtrEndNode(ftrBids.get(i).getFtrEndNode());
							organizedFtrs.add(newftr);
						}
					}
				}
			}			
		}
		catch (Exception e) {
			System.out.println("operator: ftrFtrOrganizer ->"+e);
		
		}
	}
			
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: capacidad remanente tras los FTRs asignados
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double ftrRemainingCapacity(int h){
		// variables auxiliares
		double ftrRemainingCapacity = 0.0;
		try{
			ftrRemainingCapacity = this.getFtrAuctionedProduct().getHourlyFtrPower() - this.getFtrAuction().getFtrDemand()[h];
		}
		catch (Exception e) {
			System.out.println("operator: ftrRemainingCapacity ->"+e);
		}
		// retornar la capacidad remanente para FTRs en la hora h
		return ftrRemainingCapacity;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: clasificación de los FTRs asignados por cada comercializador
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrRetailersAssignedRights(List<List<Ftr>> ftrAssigns){
		// variables auxiliares
		List<List<Ftr>> ftrAssignedRights; // ftrs asignados en toda la subasta
		List<Ftr> ftrHourlyAssignedRights;			// ftrs asignados en cada hora
		try{
			for(int ret = 0; ret < this.getRetailers().size(); ret++)
			{
				// inicialización variable auxiliar
				ftrAssignedRights = new ArrayList<List<Ftr>>();
				
				for(int h = 0; h < 24; h++)
				{
					// inicialización variable auxiliar
					ftrHourlyAssignedRights = new ArrayList<Ftr>();
					
					// si en la hora h no se asignaron FTRs
					if(ftrAssigns.get(h).isEmpty())
					{
						ftrHourlyAssignedRights.add(new Ftr());
					}
					else
					{						
						for(int ftr = 0; ftr < ftrAssigns.get(h).size(); ftr++)
						{
							// se dermina cuales de los FTRs asignados corresponden al comercializador ret
							if(ftrAssigns.get(h).get(ftr).getBidderR().equals(this.getRetailers().get(ret)))
							{
								ftrHourlyAssignedRights.add(ftrAssigns.get(h).get(ftr));
							}
						}
						// si el comercializador ret no tiene FTRs
						if(ftrHourlyAssignedRights.isEmpty())
						{
							ftrHourlyAssignedRights.add(new Ftr());
						}
					}
					// adición de la lista de FTRs para la hora h al comercializador ret
					ftrAssignedRights.add(h,ftrHourlyAssignedRights);
				}
				// establecer los FTRs asignados a cada comercializador
				this.getRetailers().get(ret).setFtrAssignedRights(ftrAssignedRights);
			}
		}
		catch (Exception e) {
			System.out.println("operator: ftrRetailersAssignedRights ->"+e);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: ingreso por los FTRs asignados en una hora determinada
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public double ftrHourlyAuctionIncome(int h, List<Ftr> ftrHourlyAssigns){
		// variables auxiliares
		double ftrHourlyAuctionIncome = 0.0;
		double[][] ftrIncome = new double[ftrHourlyAssigns.size()][24];
		try{
			// calculo del ingreso por cada FTR
			for(int ftr = 0; ftr < ftrHourlyAssigns.size(); ftr++){
				ftrIncome[ftr][h] = ftrHourlyAssigns.get(ftr).getHourlyFtrPower() * 
						ftrHourlyAssigns.get(ftr).getHourlyFtrPrice() *
						ftrHourlyAssigns.get(ftr).getFtrDuration();
				ftrHourlyAuctionIncome = ftrHourlyAuctionIncome + ftrIncome[ftr][h];
			}
		}
		catch (Exception e) {
			System.out.println("operator: ftrHourlyAuctionIncome ->"+e);
		}
		// retorna el ingreso por los FTRs asignados en la hora h
		return ftrHourlyAuctionIncome;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftr: liquidación del mercado de energía para los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void ftrRetailersEnergyMarketSettlement()
	{
		try{
			// variables auxiliares
			double[] ftrRemainingEnergyDemand; 		// demanda remanente
			double[] ftrSettlementContractsPC; 		// egresos en contratos pague lo contratado
			double[] ftrSettlementContractsPD; 		// egresos en contratos pague lo demandado
			double[] ftrPoolEnergyPurchases; 			// compras en bolsa
			double[] ftrSettlementPoolEnergyPurchses; 	// egresos por compras en bolsa
			double[] ftrPoolEnergySales; 				// ventas en bolsa
			double[] ftrSettlementPoolEnergySales; 	// ingresos por ventas en bolsa
			double[] ftrSettlementEnergyMarket; 		// egresos totales del comercializador
			/*
			List<List<GenerationContract>> dispatchedGenerationContractsPD = new ArrayList<List<GenerationContract>>(); 		// contratos de generación despachados en el día
			List<GenerationContract> hourlyDispatchedGenerationContractsPD; 	// contratos de generación despachados en cada hora
			
			for(int i = 0; i < 24; i++)
			{
				dispatchedGenerationContractsPD.add(new ArrayList<GenerationContract>());
			}
			*/
			// demanda remanente para contratos PD cada comercializador
			double[][] remainingRetailerEnergyPurchasesPDMWh = new double[Global.nRetailers][24];
			
			System.out.println("\n------------------------------ ftrs ----> comercializadores: liquidación inicial mercado de energía 	--------------------------------\n");
			
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{		
				// inicialización de las varibles auxiliares
				ftrRemainingEnergyDemand 		= this.getRetailers().get(ret).getServedDemand().clone();
				ftrSettlementContractsPC 		= new double[24];
				ftrSettlementContractsPD 		= new double[24];
				ftrPoolEnergyPurchases 			= new double[24];
				ftrSettlementPoolEnergyPurchses	= new double[24];
				ftrPoolEnergySales 				= new double[24];
				ftrSettlementPoolEnergySales 	= new double[24];
				ftrSettlementEnergyMarket 		= new double[24];
				remainingRetailerEnergyPurchasesPDMWh[ret] = this.getRetailers().get(ret).getFtrContractEnergyPurchasesPDMWh().clone();
				
				for(int h = 0; h < 24; h++)
				{
					//
					// si el comercializador posee contratos PC
					//
					for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
					{
						// cálculo del egreso en el contrato PC contract para la hora h
						ftrSettlementContractsPC[h] =  ftrSettlementContractsPC[h] + 
							this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
							this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
					
						// actualización de la demanda de energía remanente para la hora h
						ftrRemainingEnergyDemand[h] = ftrRemainingEnergyDemand[h] - 
							this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h];
					}			
					//
					// si la demanda contratada en contratos PC es mayor que la demanda real entonces hay ventas en bolsa
					//
					if(ftrRemainingEnergyDemand[h] <= 0)
					{
						// la cantidad vendida en bolsa es es la diferencia entre la demanda contratada 
						// en contratos PC y la demanda real
						ftrPoolEnergySales[h] = Math.abs(ftrRemainingEnergyDemand[h]);
						
						// cálculo del ingreso por ventas en bolsa
						ftrSettlementPoolEnergySales[h] = 
								this.getRealDispatch().getNodalPrices()[this.getRetailers().get(ret).getDemandNode().getNodeId()][h] 
										* ftrPoolEnergySales[h];
					}
					//
					// si el comercializador posee contratos PD
					//
					//hourlyDispatchedGenerationContractsPD = new ArrayList<GenerationContract>();
					for(int contract = 0; contract < this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).size(); contract++)
					{
						// cálculo de la demanda a liquidar en el contrato PD contact en la hora h
						double demandPD = Math.max(0,
								Math.min(remainingRetailerEnergyPurchasesPDMWh[ret][h],
								this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPower()[h]));
	
						// cálculo del egreso en el contrato PD contract para la hora h
						ftrSettlementContractsPD[h] = ftrSettlementContractsPD[h] 
								+ this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractPrice()[h]
								* demandPD; 
											
						// actualización de la demanda de energía remanente para la hora h
						ftrRemainingEnergyDemand[h] = ftrRemainingEnergyDemand[h] - demandPD;	
						remainingRetailerEnergyPurchasesPDMWh[ret][h] = remainingRetailerEnergyPurchasesPDMWh[ret][h] - demandPD;
						
						/*
						//
						// actualización energía despachada en contratos PD
						//
						// comerializador
						this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).setDispatchedContractPowerPD(demandPD, h);
						//
						// unidad de generación
						//
						int unit = this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getGenerationUnitId();
						for(int i = 0; i < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); i++)
						{
							if(this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).getContractId() == this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract).getContractId())
							{
								this.getGenerationUnits().get(unit).getGenerationContractsPD().get(i).setDispatchedContractPowerPD(demandPD, h);
							}
						}
						
						if(demandPD > 0)
						{
							// si el contrato se necesita para atender la demanda entonces el contrato PD se guarda como despachado
							hourlyDispatchedGenerationContractsPD.add(this.getRetailers().get(ret).getSortGenerationContractsPD().get(h).get(contract));
						}*/
					}
					// agregar los contratos despachados para la hora h que pertenecen al comercializador ret
					//dispatchedGenerationContractsPD.get(h).addAll(hourlyDispatchedGenerationContractsPD);
								
					//
					// si después de atender la demanda con contratos PC y PD aun hay demanda remanente por atender entonces
					// el comercializador tienen que comprar energía en la bolsa
					//
					if(ftrRemainingEnergyDemand[h] > 0)
					{					
						// la cantidad comprada en bolsa es la demanda remanente equivalente a la demanda real menos la
						// demanda atendida en contratos PC y PD
						ftrPoolEnergyPurchases[h] = ftrRemainingEnergyDemand[h];
						
						// cálculo del egreso por compras en bolsa
						ftrSettlementPoolEnergyPurchses[h] = 
								this.getRealDispatch().getNodalPrices()[this.getRetailers().get(ret).getDemandNode().getNodeId()][h] 
										* ftrPoolEnergyPurchases[h];
					}
					
					//
					// liquidación general del comercializador
					//
					ftrSettlementEnergyMarket[h] = ftrSettlementContractsPC[h] + ftrSettlementContractsPD[h] 
						+ ftrSettlementPoolEnergyPurchses[h] - ftrSettlementPoolEnergySales[h]; 
				}
				this.getRetailers().get(ret).setFtrContractEnergyPurchasesPCCOP(ftrSettlementContractsPC);
				this.getRetailers().get(ret).setFtrContractEnergyPurchasesPDCOP(ftrSettlementContractsPD);
				this.getRetailers().get(ret).setFtrPoolEnergySalesMWh(ftrPoolEnergySales);
				this.getRetailers().get(ret).setFtrPoolEnergySalesCOP(ftrSettlementPoolEnergySales);
				this.getRetailers().get(ret).setFtrPoolEnergyPurchasesMWh(ftrPoolEnergyPurchases);
				this.getRetailers().get(ret).setFtrPoolEnergyPurchasesCOP(ftrSettlementPoolEnergyPurchses);
				this.getRetailers().get(ret).setFtrSettlementEnergyMarket(ftrSettlementEnergyMarket);
				
				System.out.print("\t" + this.getRetailers().get(ret).getRetailerCod()+ "\t");
				Global.rw.printVector(ftrSettlementEnergyMarket);
			}
			// establece los despachos contratados para la hora h de todos los comercializadores
			//this.setDispatchedGenerationContractsPD(dispatchedGenerationContractsPD);
			//Global.rw.printArrayDispatchContracts(this.getDispatchedGenerationContractsPD());
		}
		catch (Exception e) {
			System.out.println("operator: ftrRetailersEnergyMarketSettlement ->"+e);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: función para calcular la liquidación de los FTRs asignados a cada comercializador
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void ftrRetailersFtrSettlementCOP(int iteration, int auctionIndex){
		// variables auxiliares
		List<List<Ftr>> ftrAssignedRights;
		double[] ftrFtrSettlementCOP;
		double[] ftrSettlementEnergyMarket; 			// valor liquidación total del mercado
		try{
			System.out.println("\n------------------------------ ftrs ----> comercializadores: pago por FTRs 	--------------------------------\n");
			
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// inicialización variables auxiliares
				ftrFtrSettlementCOP 		= new double[24];				
				ftrSettlementEnergyMarket 	= this.getRetailers().get(ret).getFtrSettlementEnergyMarket().clone();
				
				// si ya se realizaron subastas
				if(auctionIndex >= 1)
				{
					// lista de FTRs asignados
					ftrAssignedRights = new ArrayList<List<Ftr>>();
					ftrAssignedRights.addAll(this.getRetailers().get(ret).getFtrAssignedRights());
					
					if(iteration > Global.ftrAuctionDate[auctionIndex-1]
							&& iteration <= Global.ftrAuctionDate[auctionIndex])
					{
						// para cada hora
						for(int h = 0; h < 24; h++)
						{
							for(int ftr = 0; ftr < ftrAssignedRights.get(h).size(); ftr++)	
							{
								// cálculo de la liquidación en cada hora
								ftrFtrSettlementCOP[h] = ftrFtrSettlementCOP[h] + 
										(ftrAssignedRights.get(h).get(ftr).getHourlyFtrPower()
												* ftrAssignedRights.get(h).get(ftr).getHourlyFtrPrice());			
							}
							ftrSettlementEnergyMarket[h] = ftrSettlementEnergyMarket[h] + ftrFtrSettlementCOP[h];
						}
					}
				}
				// establecer la liquidación de los FTRs a cada comercializador
				this.getRetailers().get(ret).setFtrSettlementCOP(ftrFtrSettlementCOP);
				
				// actualización de la liquidación del mercado de cada comercializador
				this.getRetailers().get(ret).setFtrSettlementEnergyMarket(ftrSettlementEnergyMarket);
				
				// imprimir resultados
				System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
				Global.rw.printVector(ftrFtrSettlementCOP);
			}			
		}
		catch(Exception e)
		{
            System.out.println("operator: ftrRetailersFtrSettlementCOP ->"+e);
        }
	}	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de los ingresos de cada comercializador por los FTRs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrRetailersFtrIncomeSettlementCOP(int iteration, int auctionIndex)
	{
		// variables auxiliares
		double[] ftrRetailersFtrIncomeSettlementCOP; 	// valor ingresos por FTRs
		double[] ftrSettlementEnergyMarket; 			// valor liquidación total del mercado
		
		try{		
			System.out.println("\n------------------------------ ftr ----> comercializadores: ingresos por FTRs 	--------------------------------\n");
			
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// inicialización variables auxiliares
				ftrRetailersFtrIncomeSettlementCOP = new double[24];
				ftrSettlementEnergyMarket = this.getRetailers().get(ret).getFtrSettlementEnergyMarket().clone();
				
				// si ya se realizaron subastas
				if(auctionIndex >= 1)
				{
					if(iteration > Global.ftrAuctionDate[auctionIndex-1]
							&& iteration <= Global.ftrAuctionDate[auctionIndex])
					{
						for(int h = 0; h < 24; h++)
						{
							// lista de FTRs asignados en la hora h
							List<Ftr> ftrHourlyFtrs = new ArrayList<Ftr>();
							ftrHourlyFtrs.addAll(this.getRetailers().get(ret).getFtrAssignedRights().get(h));
							
							if(ftrHourlyFtrs.get(0).getBidderR() != null)
							{
								for(int ftr = 0; ftr < ftrHourlyFtrs.size(); ftr++)
								{
									// cálculo de los ingresos / egresos por los FTRs adquiridos
									ftrRetailersFtrIncomeSettlementCOP[h] = ftrRetailersFtrIncomeSettlementCOP[h]
											+ ftrHourlyFtrs.get(ftr).getHourlyFtrPower()
											* (this.getRealDispatch().getNodalPrices()[ftrHourlyFtrs.get(ftr).getFtrEndNode().getNodeId()][h]
													- this.getRealDispatch().getNodalPrices()[ftrHourlyFtrs.get(ftr).getFtrSourceNode().getNodeId()][h]);
								}
							}
							ftrSettlementEnergyMarket[h] = ftrSettlementEnergyMarket[h] - ftrRetailersFtrIncomeSettlementCOP[h];
						}
					}
				}
				// establecer el valor de los ingresos por FTRs a cada comercializador
				this.getRetailers().get(ret).setFtrIncomeSettlementCOP(ftrRetailersFtrIncomeSettlementCOP);
				
				// actualización de la liquidación del mercado de cada comercializador
				this.getRetailers().get(ret).setFtrSettlementEnergyMarket(ftrSettlementEnergyMarket);
				
				// imprimir resultados
				System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
				Global.rw.printVector(ftrRetailersFtrIncomeSettlementCOP);
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: ftrRetailersFtrIncomeSettlementCOP ->"+e);
        }
	}
			
	// --------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: función para liquidar las rentas por congestión de los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void ftrRetailersCongestionRentsCOP(int iteration, int auctionIndex){
		// variables auxiliares
		double[] ftrSettlementCongestionRentsCOP;
		double[] ftrRemainingServedDemand; 			// demanda atendida de cada comercializador
		try{
			System.out.println("\n------------------------------ ftrs ----> comercializadores: rentas por congestión	--------------------------------\n");
			
			for(int ret = 0; ret < Global.nRetailers; ret++)
			{
				// inicialización variables auxiliares
				ftrSettlementCongestionRentsCOP = new double[24];
				ftrRemainingServedDemand = this.getRetailers().get(ret).getServedDemand().clone();
				if(auctionIndex >= 1)
				{
					if(iteration > Global.ftrAuctionDate[auctionIndex-1]
							&& iteration <= Global.ftrAuctionDate[auctionIndex])
					{
						for(int h = 0; h < 24; h++)
						{
							// lista de ftrs del comercializador en la hora h
							List<Ftr> ftrHourlyFtrs = new ArrayList<Ftr>();
							ftrHourlyFtrs.addAll(this.getRetailers().get(ret).getFtrAssignedRights().get(h));

							// si el comercializador no tiene ftrs
							if(ftrHourlyFtrs.get(0).getBidderR() == null)
							{
								// liquidación rentas por congestión correspondientes a contratos PC para cada hora h para el comercializador ret
								for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
								{	
									// actualización de las rentas por congestión
									ftrSettlementCongestionRentsCOP[h]= ftrSettlementCongestionRentsCOP[h]
											+ Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
													ftrRemainingServedDemand[h])
													* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getWithdrawalNodeId()][h]
															- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getSourceNodeId()][h]));
									
									// actualización de la demanda remanente
									ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h] 
											- Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
													ftrRemainingServedDemand[h]);	
								}
								
								// liquidación rentas por congestión correspondientes a contratos PD para cada hora h para el comercializador ret
								for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPD().size(); contract++)
								{
									// actualización de las rentas por congestión
									ftrSettlementCongestionRentsCOP[h] = ftrSettlementCongestionRentsCOP[h]
											+ this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h]
													* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getWithdrawalNodeId()][h]
															- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getSourceNodeId()][h]));
									
									// actualización de la demanda remanente
									ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h]
											- this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
								}
							}
							else
							{
								// liquidación rentas por congestión correspondientes a contratos PC para cada hora h para el comercializador ret
								for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
								{
									// booleano que indica si un contrato estaba o no asociado a FTRs
									boolean contractFtr = false;
									
									// para cada ftr
									for(int ftr = 0; ftr < ftrHourlyFtrs.size(); ftr++)
									{
										if(ftrHourlyFtrs.get(ftr).getGenerationContract().equals(this.getRetailers().get(ret).getGenerationContractsPC().get(contract)))
										{
											// actualizar el valor del booleano
											contractFtr = true;
											
											// demanda no cubierta con ftrs
											double uncoveredPower = 
													this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h]
													- ftrHourlyFtrs.get(ftr).getHourlyFtrPower();
											
											// actualización de las rentas por congestión
											ftrSettlementCongestionRentsCOP[h]= ftrSettlementCongestionRentsCOP[h]
													+ Math.min(uncoveredPower,
															ftrRemainingServedDemand[h])
															* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getWithdrawalNodeId()][h]
																	- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getSourceNodeId()][h]));
											
											// actualización de la demanda remanente
											ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h] 
													- Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
															ftrRemainingServedDemand[h]);
											
											// sólo hay un ftr por cada contrato
											break;
										}
									}
									
									// si el contrato no estuvo asociado a ningún ftr
									if(contractFtr == false)
									{
										// actualización de las rentas por congestión
										ftrSettlementCongestionRentsCOP[h]= ftrSettlementCongestionRentsCOP[h]
												+ Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
														ftrRemainingServedDemand[h])
														* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getWithdrawalNodeId()][h]
																- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getSourceNodeId()][h]));
										
										// actualización de la demanda remanente
										ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h] 
												- Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
														ftrRemainingServedDemand[h]);											
									}
								}
								
								// liquidación rentas por congestión correspondientes a contratos PD para cada hora h para el comercializador ret
								for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPD().size(); contract++)
								{
									// booleano que indica si un contrato estaba o no asociado a FTRs
									boolean contractFtr = false;
									
									// para cada ftr
									for(int ftr = 0; ftr < ftrHourlyFtrs.size(); ftr++)
									{
										if(ftrHourlyFtrs.get(ftr).getGenerationContract().equals(this.getRetailers().get(ret).getGenerationContractsPD().get(contract)))
										{
											// actualizar el valor del booleano
											contractFtr = true;
											
											// demanda no cubierta con ftrs
											double uncoveredPower = 
													this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h]
													- ftrHourlyFtrs.get(ftr).getHourlyFtrPower();
											
											// actualización de las rentas por congestión
											ftrSettlementCongestionRentsCOP[h]= ftrSettlementCongestionRentsCOP[h]
													+ Math.min(uncoveredPower,
															ftrRemainingServedDemand[h])
															* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getWithdrawalNodeId()][h]
																	- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getSourceNodeId()][h]));
											
											// actualización de la demanda remanente
											ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h] 
													- Math.min(this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h],
															ftrRemainingServedDemand[h]);
											
											// sólo hay un ftr por cada contrato
											break;
										}										
									}
									
									// si el contrato no estuvo asociado a ningún ftr
									if(contractFtr == false)
									{							
										// actualización de las rentas por congestión
										ftrSettlementCongestionRentsCOP[h] = ftrSettlementCongestionRentsCOP[h]
												+ this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h]
														* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getWithdrawalNodeId()][h]
																- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getSourceNodeId()][h]));
										
										// actualización de la demanda remanente
										ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h]
												- this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
									}
								}								
							}
						}
					}
				}
				else
				{
					for(int h = 0; h < 24; h++)
					{
						// liquidación rentas por congestión correspondientes a contratos PC para cada hora h para el comercializador ret
						for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPC().size(); contract++)
						{
							// actualización de las rentas por congestión
							ftrSettlementCongestionRentsCOP[h] = ftrSettlementCongestionRentsCOP[h]
									+ Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
											ftrRemainingServedDemand[h])
											* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getWithdrawalNodeId()][h]
													- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPC().get(contract).getSourceNodeId()][h]));
							
							// actualización de la demanda remanente
							ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h] 
									- Math.min(this.getRetailers().get(ret).getGenerationContractsPC().get(contract).getContractPower()[h],
											ftrRemainingServedDemand[h]);
						}
						// liquidación rentas por congestión correspondientes a contratos PD para cada hora h para el comercializador ret
						for(int contract = 0; contract < this.getRetailers().get(ret).getGenerationContractsPD().size(); contract++)
						{
							// actualización de las rentas por congestión
							ftrSettlementCongestionRentsCOP[h] = ftrSettlementCongestionRentsCOP[h]
									+ this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h]
											* Math.max(0,(this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getWithdrawalNodeId()][h]
													- this.getRealDispatch().getNodalPrices()[this.getGenerationContractsPD().get(contract).getSourceNodeId()][h]));
							
							// actualización de la demanda remanente
							ftrRemainingServedDemand[h] = ftrRemainingServedDemand[h]
									- this.getRetailers().get(ret).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
						}
					}
				}
				// establecer las rentas por congestión a cada comercializador
				this.getRetailers().get(ret).setFtrSettlementCongestionCOP(ftrSettlementCongestionRentsCOP);
				
				// imprimir resultados
				System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
				Global.rw.printVector(ftrSettlementCongestionRentsCOP);
				//Global.rw.printVector(ftrRemainingServedDemand);
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: ftrRetailersCongestionRentsCOP ->"+e);
        }
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: liquidación cargos complementarios a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrRetailersComplementaryChargesSettlementCOP()
	{
		// variables auxiliares
		double[] ftrSettlementComplementaryChargesCOP; 	// valor liquidación cargos complementarios	
		
		System.out.println("\n------------------------------ ftr ----> comercializadores: cargos complementarios 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			ftrSettlementComplementaryChargesCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				ftrSettlementComplementaryChargesCOP[h] = this.getFtrTransmittersSettlementComplementaryChargesCOP()[h]
						*(this.retailers.get(ret).getServedDemand()[h]/this.getServedDemand()[h]);
			}
			// establecer los cargos complementarios a cada comercializador
			this.getRetailers().get(ret).setFtrSettlementComplementaryChargesCOP(ftrSettlementComplementaryChargesCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(ftrSettlementComplementaryChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: liquidación cargos complementarios a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrRetailersComplementaryChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] ftrSettlementComplementaryChargesCOP; 	// valor liquidación cargos complementarios	
		double[] ftrEnergyMarketSettlement;  // liquidación parcial del mercado de energía 
		System.out.println("\n------------------------------ ftr ----> comercializadores: cargos complementarios 	--------------------------------\n");
		
		for( int ret = 0; ret < Global.nRetailers; ret++)
		{
			// inicialización variables auxiliares
			ftrSettlementComplementaryChargesCOP = new double[24];
			ftrEnergyMarketSettlement = this.getRetailers().get(ret).getFtrSettlementEnergyMarket().clone();
			
			for(int h = 0; h < 24; h++)
			{
				ftrSettlementComplementaryChargesCOP[h] = this.getFtrTransmittersSettlementComplementaryChargesCOP()[h]
						*(this.getRetailers().get(ret).getServedDemand()[h]/this.getServedDemand()[h]);
				
				// actualización de la liquidación del mercado
				ftrEnergyMarketSettlement[h] = ftrEnergyMarketSettlement[h] + ftrSettlementComplementaryChargesCOP[h];
			}
			// establecer los cargos complementarios a cada comercializador
			this.getRetailers().get(ret).setFtrSettlementComplementaryChargesCOP(ftrSettlementComplementaryChargesCOP);
			
			// actualizar la liquidación del mercado
			this.getRetailers().get(ret).setFtrSettlementEnergyMarket(ftrEnergyMarketSettlement);
			
			// imprimir resultados
			System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
			Global.rw.printVector(ftrSettlementComplementaryChargesCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: liquidación cargos por uso a los comercializadores
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrRetailersUsageChargesSettlementCOP(int iteration, int auctionIndex)
	{
		// variables auxiliares
		double[] ftrSettlementUsageChargesCOP; 	// valor liquidación cargos por uso	
		double[] ftrSettlementEnergyMarket; 	// valor liquidación parcial del mercado
		
		try{
			System.out.println("\n------------------------------ ftr ----> comercializadores: cargos por uso 	--------------------------------\n");
			
			for( int ret = 0; ret < Global.nRetailers; ret++)
			{
				// inicialización variables auxiliares
				ftrSettlementUsageChargesCOP = new double[24];
				ftrSettlementEnergyMarket = this.getRetailers().get(ret).getFtrSettlementEnergyMarket().clone();
				
				// si ya se realizaron subastas
				if(auctionIndex >= 1)
				{
					if(iteration > Global.ftrAuctionDate[auctionIndex-1]
							&& iteration <= Global.ftrAuctionDate[auctionIndex])
					{
						for(int h = 0; h < 24; h++)
						{
							ftrSettlementUsageChargesCOP[h] = 
									this.getRetailers().get(ret).getFtrSettlementComplementaryChargesCOP()[h] 
											+ this.getRetailers().get(ret).getFtrSettlementCongestionCOP()[h]
													+ this.getRetailers().get(ret).getFtrSettlementCOP()[h];
							
							// actualización de la liquidación total del mercado
							ftrSettlementEnergyMarket[h] = ftrSettlementEnergyMarket[h] + ftrSettlementUsageChargesCOP[h];
						}
					}
				}
				else // si aun no hay subastas
				{
					for(int h = 0; h < 24; h++)
					{
						ftrSettlementUsageChargesCOP[h] = 
								this.getRetailers().get(ret).getFtrSettlementComplementaryChargesCOP()[h] 
										+ this.getRetailers().get(ret).getFtrSettlementCongestionCOP()[h];
						
						// actualización de la liquidación total del mercado
						ftrSettlementEnergyMarket[h] = ftrSettlementEnergyMarket[h] + ftrSettlementUsageChargesCOP[h];
					}
				}
				// establece el valor de los cargos por uso para cada comercializador
				this.getRetailers().get(ret).setFtrSettlementUsageChargesCOP(ftrSettlementUsageChargesCOP);
				// actualizar la liquidación del mercado
				this.getRetailers().get(ret).setFtrSettlementEnergyMarket(ftrSettlementEnergyMarket);
				
				// imprimir resultados
				System.out.print("\t" + this.getRetailers().get(ret).getRetailerName() + "\t");
				Global.rw.printVector(ftrSettlementUsageChargesCOP);
				//Global.rw.printVector(ftrSettlementEnergyMarket);
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: ftrRetailersUsageChargesSettlementCOP ->"+e);
        }
	}				
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de los cargos complementarios para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersComplementaryChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] ftrTransmittersComplementaryChargesSettlementCOP;			// valor cargos complementarios por hora para cada transmisor
		
		System.out.println("\n------------------------------ ftr ----> transmisores: cargos complementarios 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersComplementaryChargesSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				ftrTransmittersComplementaryChargesSettlementCOP[h] = 
						Global.proportionComplementaryCharge*this.getTransmitters().get(gridco).getSettlementUsageChargesCOP()[h];
			}
			// establecer el valor de los cargos complementarios a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementComplementaryChargesCOP(ftrTransmittersComplementaryChargesSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersComplementaryChargesSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de las rentas por congestión para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersCongestionSettlementCOP()
	{
		// variables auxiliares
		double[] ftrTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		
		System.out.println("\n------------------------------ ftr ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersCongestionSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				for(int line = 0; line < Global.nLines; line++)
				{
					if(this.getTransmitters().get(gridco).equals(this.getTransmissionLines().get(line).getLineOwner()))
					{
						ftrTransmittersCongestionSettlementCOP[h] = ftrTransmittersCongestionSettlementCOP[h] + 
								Math.abs(Math.abs(this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getEndNode().getNodeId()][h]
											- this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getSourceNode().getNodeId()][h])
										* this.getRealDispatch().getFlows()[this.getTransmissionLines().get(line).getLineID()][h]);
					}
				}
			}
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementCongestionCOP(ftrTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersCongestionSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de las rentas por congestión para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersCongestionSettlementCOP5(int iteration, int auctionIndex)
	{
		// variables auxiliares
		double[] ftrTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		double[] ftrRemainingCongestionRentsCOP = 					// rentas por congestión remanentes
				Global.factory.productEscalarVector(
						this.getFtrRemainingCongestionRentsCOP().clone(),
						Global.proportionCongestionRentsTransmitters); 
		double[] ftrCongestionRentsCOP = 
				Global.factory.productEscalarVector(
						this.getFtrCongestionRentsCOP().clone(),
						Global.proportionCongestionRentsTransmitters); 	// rentas por congestión recolectadas de los comercializadores
				
		System.out.println("\n------------------------------ ftr ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersCongestionSettlementCOP = new double[24];
			
			// si ya se realizaron subastas
			if(auctionIndex >= 1)
			{
				if(iteration > Global.ftrAuctionDate[auctionIndex-1]
										&& iteration <= Global.ftrAuctionDate[auctionIndex])
				{
					for(int h = 0; h < 24; h++)
					{
						// rentas por congestión en proporción a la participación del sistema de transmisión nacional
						ftrTransmittersCongestionSettlementCOP[h] = 
								this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
								* ftrRemainingCongestionRentsCOP[h];
					}
				}
			}
			else
			{
				for(int h = 0; h < 24; h++)
				{
					// rentas por congestión en proporción a la participación del sistema de transmisión nacional
					ftrTransmittersCongestionSettlementCOP[h] = 
							this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
							* ftrCongestionRentsCOP[h];
				}
			}
			
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementCongestionCOP(ftrTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersCongestionSettlementCOP);
		}
	}	

	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de las rentas por congestión para cada transmisor [ALTERNATIVA]
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersCongestionSettlementCOP1()
	{
		// variables auxiliares
		double[] ftrTransmittersCongestionSettlementCOP;			// valor rentas por congestión por hora para cada transmisor
		double[] ftrCongestionRents = this.getFtrRetailersCongestionRentsCOP().clone(); // valor de las rentas por congestión recolectadas de los comecializadores
		
		System.out.println("\n------------------------------ ftr ----> transmisores: rentas por congestión 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersCongestionSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				ftrTransmittersCongestionSettlementCOP[h] = 
						this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
							* ftrCongestionRents[h];
			}
			// establecer el valor de las rentas por congestión a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementCongestionCOP(ftrTransmittersCongestionSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersCongestionSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de los ingresos de los trasnmisores provenientes de los FTRs vendidos en la subasta
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersFtrAuctionIncomeCOP(int iteration, int auctionIndex)
	{
		// variables auxiliares
		double[] ftrAuctionIncomeCOP;	// valor ingresos de los transmisores por los FTRs
		
		System.out.println("\n------------------------------ ftr ----> transmisores: ingresos por FTRs 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrAuctionIncomeCOP = new double[24];
			
			// si ya se realizaron subastas
			if(auctionIndex >= 1)
			{				
				if(iteration > Global.ftrAuctionDate[auctionIndex-1]
										&& iteration <= Global.ftrAuctionDate[auctionIndex])
				{
					for(int h = 0; h < 24; h++)
					{
						ftrAuctionIncomeCOP[h] = 
								this.getTransmitters().get(gridco).getProportionTransmissionSettlement()
								* this.getFtrIncomeSettlementCOP()[h];
					}
				}
			}
			// establecer el valor de los ingresos por FTRs
			this.getTransmitters().get(gridco).setFtrAuctionIncomeCOP(ftrAuctionIncomeCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrAuctionIncomeCOP);
		}
	}
		
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de los cargos por uso para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersUsageChargesSettlementCOP(int iteration, int auctionIndex)
	{
		// variables auxiliares
		double[] ftrTransmittersUsageChargesSettlementCOP; 	// valor cargos por uso por hora para cada transmisor
		
		System.out.println("\n------------------------------ ftr ----> transmisores: cargos por uso 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersUsageChargesSettlementCOP = new double[24];
			
			// si ya se realizaron subastas
			if(auctionIndex >= 1)
			{
				if(iteration > Global.ftrAuctionDate[auctionIndex-1]
										&& iteration <= Global.ftrAuctionDate[auctionIndex])
				{
					for(int h = 0; h < 24; h++)
					{
						ftrTransmittersUsageChargesSettlementCOP[h] = 
								this.getTransmitters().get(gridco).getFtrSettlementComplementaryChargesCOP()[h] +
								//this.getTransmitters().get(gridco).getFtrSettlementCongestionCOP()[h] + 
								this.getTransmitters().get(gridco).getFtrAuctionIncomeCOP()[h];
					}
				}
			}
			else // si no se han desarrollado subastas
			{
				for(int h = 0; h < 24; h++)
				{
					ftrTransmittersUsageChargesSettlementCOP[h] = 
							this.getTransmitters().get(gridco).getFtrSettlementComplementaryChargesCOP()[h] +
							this.getTransmitters().get(gridco).getFtrSettlementCongestionCOP()[h];
				}				
			}
			// establecer el valor de los cargos por uso a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementUsageChargesCOP(ftrTransmittersUsageChargesSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersUsageChargesSettlementCOP);
		}
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: cálculo de los cargos por uso para cada transmisor
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrTransmittersUsageChargesSettlementCOP5()
	{
		// variables auxiliares
		double[] ftrTransmittersUsageChargesSettlementCOP; 	// valor cargos por uso por hora para cada transmisor
		
		System.out.println("\n------------------------------ ftr ----> transmisores: cargos por uso 	--------------------------------\n");
		
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			// inicialización variables auxiliares
			ftrTransmittersUsageChargesSettlementCOP = new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				ftrTransmittersUsageChargesSettlementCOP[h] = 
						this.getTransmitters().get(gridco).getFtrSettlementComplementaryChargesCOP()[h] +
						this.getTransmitters().get(gridco).getFtrSettlementCongestionCOP()[h];
			}				
			// establecer el valor de los cargos por uso a cada transmisor
			this.getTransmitters().get(gridco).setFtrSettlementUsageChargesCOP(ftrTransmittersUsageChargesSettlementCOP);
			
			// imprimir resultados
			System.out.print("\t" + this.getTransmitters().get(gridco).getTransmitterName() + "\t");
			Global.rw.printVector(ftrTransmittersUsageChargesSettlementCOP);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: liquidación del mercado de energía para las unidades de generación
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrGenerationUnitsEnergyMarketSettlement()
	{
		// variables auxiliares
		double[] ftrRemainingRealGeneration; 				// generación real remanente
		double[] ftrSettlementContractsPC; 					// ingresos en contratos pague lo contratado
		double[] ftrSettlementContractsPD; 					// ingresos en contratos pague lo demandado
		double[] ftrPoolEnergyPurchases; 					// compras en bolsa
		double[] ftrSettlementPoolEnergyPurchses; 			// egresos por compras en bolsa
		double[] ftrPoolEnergySales; 						// ventas en bolsa
		double[] ftrSettlementPoolEnergySales; 				// ingresos por ventas en bolsa
		double[] ftrSettlementEnergyMarket; 				// ingresos totales de la unidad de generación
		
		System.out.println("\n------------------------------ ftr ----> generadores: liquidación inicial mercado de energía 	--------------------------------\n");
		
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			// inicialización de las varibles auxiliares
			ftrRemainingRealGeneration 			= this.getGenerationUnits().get(unit).getRealGeneration().clone();
			ftrSettlementContractsPC 			= new double[24];
			ftrSettlementContractsPD 			= new double[24];
			ftrPoolEnergyPurchases 				= new double[24];
			ftrSettlementPoolEnergyPurchses 	= new double[24];
			ftrPoolEnergySales 					= new double[24];
			ftrSettlementPoolEnergySales 		= new double[24];
			ftrSettlementEnergyMarket 			= new double[24];
			
			for(int h = 0; h < 24; h++)
			{
				//
				// si la unidad de generación posee contratos PC
				//
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPC().size(); contract++)
				{
					// calculo del ingreso en el contrato PC contract para la hora h
					ftrSettlementContractsPC[h] =  ftrSettlementContractsPC[h] + 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPrice()[h] * 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
					
					// actualización de la generación ideal de energía remanente para la hora h
					ftrRemainingRealGeneration[h] = ftrRemainingRealGeneration[h] - 
							this.getGenerationUnits().get(unit).getGenerationContractsPC().get(contract).getContractPower()[h];
				}
			
				//
				// si la unidad de generación posee contratos PD
				//
				for(int contract = 0; contract < this.getGenerationUnits().get(unit).getGenerationContractsPD().size(); contract++)
				{
					// generación a liquidar en el contrato PD contract en la hora h
					double energyPD = this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getDispatchedContractPowerPD()[h];
					
					// cálculo del ingreso en el contrato PD contract para la hora h
					ftrSettlementContractsPD[h] = ftrSettlementContractsPD[h] 
							+ this.getGenerationUnits().get(unit).getGenerationContractsPD().get(contract).getContractPrice()[h]
							* energyPD; 
					
					// actualización de generación ideal remanente para la hora h
					ftrRemainingRealGeneration[h] = ftrRemainingRealGeneration[h] - energyPD;
				}
				
				//
				// si después de cumplir con los compromisos en contratos PC y PD hay exceso o faltante de generación ideal, 
				// entonces la unidad de generación vende o compra en bolsa respectívamente
				//
				if(ftrRemainingRealGeneration[h] <= 0)
				{
					// la cantidad comprada en bolsa es la generación remanente 
					ftrPoolEnergyPurchases[h] = Math.abs(ftrRemainingRealGeneration[h]);
					
					// cálculo del egreso por compras en bolsa
					ftrSettlementPoolEnergyPurchses[h] = 
							this.getRealDispatch().getNodalPrices()[this.getGenerationUnits().get(unit).getNode().getNodeId()][h] 
									* ftrPoolEnergyPurchases[h];
				}
				else
				{
					// la cantidad vendida en bolsa es la generación remanente 
					ftrPoolEnergySales[h] = ftrRemainingRealGeneration[h];
					
					// cálculo del ingreso por ventas en bolsa
					ftrSettlementPoolEnergySales[h] = 
							this.getRealDispatch().getNodalPrices()[this.getGenerationUnits().get(unit).getNode().getNodeId()][h] 
									* ftrPoolEnergySales[h];
				}
					
				// liquidación general de la unidad de generación
				ftrSettlementEnergyMarket[h] = ftrSettlementContractsPC[h] + ftrSettlementContractsPD[h] 
						- ftrSettlementPoolEnergyPurchses[h] + ftrSettlementPoolEnergySales[h];
			}
			// establcer los valores de las variables
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPCCOP(ftrSettlementContractsPC);
			this.getGenerationUnits().get(unit).setFtrContractEnergySalesPDCOP(ftrSettlementContractsPD);
			this.getGenerationUnits().get(unit).setFtrPoolEnergySalesMWh(ftrPoolEnergySales);
			this.getGenerationUnits().get(unit).setFtrPoolEnergySalesCOP(ftrSettlementPoolEnergySales);
			this.getGenerationUnits().get(unit).setFtrPoolEnergyPurchasesMWh(ftrPoolEnergyPurchases);
			this.getGenerationUnits().get(unit).setFtrPoolEnergyPurchasesCOP(ftrSettlementPoolEnergyPurchses);
			this.getGenerationUnits().get(unit).setFtrSettlementEnergyMarket(ftrSettlementEnergyMarket);
			
			// imprimir resultados
			System.out.print("\t" + this.getGenerationUnits().get(unit).getUnitName() + "\t");
			Global.rw.printVector(ftrSettlementEnergyMarket);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ftrs: lliquidación rentas por nuevos FTRs
	//
	//---------------------------------------------------------------------------------------------------------------------------------------
	public void ftrInversionSettlement()
	{
		String[] newLines = {"L79", "L80", "L81", "L82", "L83", "L84", "L85", "L86", "L87", "L88", "L89", "L90", "L91", "L92", "L93", "L94"};
		Vector<PowerDemand> vector = new Vector<PowerDemand>();
		PowerDemand powerDemand;
		try
		{
			for (int newline = 0; newline < newLines.length; newline++)
			{
				for(int line = 0; line < this.getTransmissionLines().size(); line ++)
				{
					if(this.getTransmissionLines().get(line).getLineCod().equals(newLines[newline]))
					{
						powerDemand = new PowerDemand(); 
						powerDemand.setNode(this.getTransmissionLines().get(line).getLineCod());
						for(int j = 0; j < 24; j++)
						{
							double congestion = 
									Math.max(0.0, (this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getEndNode().getNodeId()][j] 
											 - this.getRealDispatch().getNodalPrices()[this.getTransmissionLines().get(line).getSourceNode().getNodeId()][j]))
									* this.getTransmissionLines().get(line).getPowerFlowLimit();
											
							powerDemand.setPowerDemand(j, congestion);
						}
						vector.add(powerDemand);
						break;					
					}
				}
			}
			Global.rw.writeCsvPowerDemands(Global.newLinesWriter, vector);
		}
		catch(IOException e) 
   	 	{
	   		 e.printStackTrace();
	   		System.out.println("ftrInversionSettlement ->"+e);
	   	}
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
			
			// escenario alto
			//powerDemandVector = Global.factory.productEscalarMatrix(powerDemandVector, 1.36);	
			// escenario medio
			//powerDemandVector = Global.factory.productEscalarMatrix(powerDemandVector, 1.312);	
			// escenario bajo
			//powerDemandVector = Global.factory.productEscalarMatrix(powerDemandVector, 1.272);		
			
			// solución del despacho idal
			this.idealDispatch = DailyIdealDispatchN.dispatch(varIN, rngIN, powergen, anglevol, unserved, iteration,  
				lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, powerDemandVector, susceptance, powerLimit,  
				angleLb, angleUb, unservedLb, unservedUb, this, idealWriter);
			
			// solución del despacho real
			this.realDispatch = DailyRealDispatch.dispatch(varR, rngR, powergen, anglevol, unserved, iteration,  
					lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, powerDemandVector, susceptance, powerLimit,  
					angleLb, angleUb, unservedLb, unservedUb, this, realWriter);
			
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|								|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	RESULTADOS GLOBALES			|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
				
			// establecer los resultados del despacho
			this.setDispatchResults(); 	// resultados a generadores y comercializadores
			this.setIdealGeneration();	// establecer generación ideal total
			this.setRealGeneration();	// establecer generación real total
			this.setEnergyDemand();		// demanda total por cada hora
			this.setServedDemand();		// demanda atendida por cada hora
			this.setUnservedDemand();	// demanda no atendida por cada hora
			
			// liquidación contratos y bolsa comercializadores
			this.retailersEnergyPurchasesPCMWh();				// magnitud compras de energía en contratos PC comercializadores
			this.retailersEnergyPurchasesPDMWh();				// magnitud compras de energía en contratos PD comercializadores
			this.retailersEnergyMarketSettlement();				// liquidación de energía en contratos y en bolsa comercializadores
			
			// liquidación contratos y bolsa generadores
			this.generationUnitsEnergySalesPCMWh();				// magnitud ventas de energía en contratos PC generadores
			this.generationUnitsEnergySalesPDMWh();				// magnitud ventas de energía en contratos PD generadores
			this.generationUnitsEnergyMarketSettlement();		// liquidación de energía en contratos y en bolsa generadores
			this.setGenerationUnitsSetttlementEnergyMarketCOP();	// liquidación total comercializadores
			
			// establecer valores para el mercado
			this.setRetailersContractEnergyPurchasesPCMWh();	// magnitud de las compras en contratos PC en cada hora de todos los comercializadores
			this.setRetailersContractEnergyPurchasesPDMWh();	// magnitud de las compras en contratos PD en cada hora de todos los comercializadores
			this.setGenerationUnitsContractEnergySalesPCMWh();	// magnitud de las ventas en contratos PC en cada hora de todas las unidades de generación
			this.setGenerationUnitsContractEnergySalesPDMWh();  // magnitud de las ventas en contratos PD en cada hora de todas las unidades de generación
			
			this.setRetailersContractEnergyPurchasesPCCOP();	// valor de las compras en contratos PC en cada hora de todos los comercializadores
			this.setRetailersContractEnergyPurchasesPDCOP();	// valor de las compras en contratos PD en cada hora de todos los comercializadores
			this.setGenerationUnitsContractEnergySalesPCCOP();  // valor de las compras en contratos PC en cada hora de todas las unidades de generación
			this.setGenerationUnitsContractEnergySalesPDCOP();	// valor de las compras en contratos PD en cada hora de todas las unidades de generación
			
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|								|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	RESULTADOS MERCADO UNINODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
				
			// liquidación reconciliaciones
			this.setDailyPositiveReconciliationMWh();			// magnitud generaciones fuera de mérito diarias
			this.generationUnitsReconciliationsSettlement();	// unidades de generación: reconciliaciones
			
			// valores para el mercado
			this.setPositiveReconciliationMWh();				// magnitud de las reconciliaciones positivas
			this.setPositiveReconciliationCOP();				// valor de las reconciliaciones positivas
			this.setNegativeReconciliationMWh();				// magnitud de las reconciliaciones negativas
			this.setNegativeReconciliationCOP();				// valor de las reconciliaciones negativas
			this.setConstraintsCOP();							// vaor de las restricciones totales
			
			// liquidación restricciones a comercializadores
			this.retailersConstraintsSettlement();				// comercializadores: restricciones
			
			// liquidación transmisión
			this.transmittersUsageChargesSettlement(iteration); // liquidación de los cargos por uso para cada transmisor
			
			// valores para el mercado
			this.setSettlementUsageChargesCOP();				// liquidación de los cargos por uso totales en el mercado, por comercializador y por generador
			
			// liquidación cargos por uso comercializadores y unidades de generación
			this.retailersUsageChargesSettlementCOP();			// liquidación de los cargos por uso para cada comercializador
			this.generationUnitsUsageChargesSettlementCOP();	// liquidación de los cargos por uso para cada generador
			
			// escritura de resultados en csv
			Global.rw.writeCsvSettlementRetailers(Global.unSettlementRetailersWriter, this.retailers, iteration);			// escritura liquidación comercializadores
			Global.rw.writeCsvSettlementUnits(Global.unSettlementUnitsWriter, this.generationUnits, iteration);				// escritura liquidación unidades de generación
			Global.rw.writeCsvSettlementTransmitters(Global.unSettlementTransmittersWriter, this.transmitters, iteration);	// escritura liquidación transmisores

			// valores para el mercado
			this.setRetailersPoolEnergyPurchasesMWh();			// magnitud de las compras en bolsa en cada hora de todos los comercializadores
			this.setRetailersPoolEnergyPurchasesCOP();			// valor de las compras en bolsa en cada hora de todos los comercializadores
			this.setRetailersPoolEnergySalesMWh();				// magnitud de las ventas en bolsa en cada hora de todos los comercializadores
			this.setRetailersPoolEnergySalesCOP();				// valor de las ventas en bolsa en cada hora de todos los comercializadores
			this.setRetailersSetttlementEnergyMarketCOP();		// valor de la liquidación total en cada hora de todos los comercializadores
			
			this.setGenerationUnitsPoolEnergyPurchasesMWh();	// magnitud de las compras en bolsa en cada hora de todas las unidades degeneración
			this.setGenerationUnitsPoolEnergyPurchasesCOP();	// valor de las compras en bolsa en cada hora de de todas las unidades degeneración
			this.setGenerationUnitsPoolEnergySalesMWh();		// magnitud de las ventas en bolsa en cada hora de todas las unidades degeneración
			this.setGenerationUnitsPoolEnergySalesCOP();		// valor de las ventas en bolsa en cada hora de todas las unidades degeneración
			this.setGenerationUnitsSetttlementEnergyMarketCOP();// valor de la liquidación total en cada hora de todas las unidades degeneración
			
			//
			// escritura de resultados: comercializadores
			//
			Global.rw.writeCsvRetailersContractEnergyPurchasesPCMWh(Global.unRetailersContractsEnergyPurchasesPCMWhWriter, this.retailers, this.getRetailersContractEnergyPurchasesPCMWh(), iteration);
			Global.rw.writeCsvRetailersContractEnergyPurchasesPCCOP(Global.unRetailersContractsEnergyPurchasesPCCOPWriter, this.retailers, this.getRetailersContractEnergyPurchasesPCCOP(), iteration);
			Global.rw.writeCsvRetailersContractEnergyPurchasesPDMWh(Global.unRetailersContractsEnergyPurchasesPDMWhWriter, this.retailers, this.getRetailersContractEnergyPurchasesPDMWh(), iteration);
			Global.rw.writeCsvRetailersContractEnergyPurchasesPDCOP(Global.unRetailersContractsEnergyPurchasesPDCOPWriter, this.retailers, this.getRetailersContractEnergyPurchasesPDCOP(), iteration);
			Global.rw.writeCsvRetailersPoolEnergyPurchasesMWh(Global.unRetailersPoolEnergyPurchasesMWhWriter, this.retailers, this.getRetailersPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvRetailersPoolEnergyPurchasesCOP(Global.unRetailersPoolEnergyPurchasesCOPWriter, this.retailers, this.getRetailersPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvRetailersPoolEnergySalesMWh(Global.unRetailersPoolEnergySalesMWhWriter, this.retailers, this.getRetailersPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvRetailersPoolEnergySalesCOP(Global.unRetailersPoolEnergySalesCOPWriter, this.retailers, this.getRetailersPoolEnergySalesCOP(), iteration);
			Global.rw.writeCsvConstraintsCOP(Global.unRetailersConstraintsCOPPWriter, this.retailers, this.getConstraintsCOP(), iteration);
			Global.rw.writeCsvSettlementUsageChargesRetailersCOP(Global.unRetailersSettlementUsageChargesCOPWriter, this.retailers, this.getSettlementUsageChargesRetailersCOP(), iteration);
			Global.rw.writeCsvRetailersSetttlementEnergyMarketCOP(Global.unRetailersSettlementEnergyMarketCOPWriter, this.retailers, this.getRetailersSetttlementEnergyMarketCOP(), iteration);
	
			//
			// escritura de resultados: unidades de generación
			//
			Global.rw.writeCsvGenerationUnitsContractEnergySalesPCMWh(Global.unGenerationUnitsContractsEnergySalesPCMWhWriter, this.generationUnits, this.getGenerationUnitsContractEnergySalesPCMWh(), iteration);
			Global.rw.writeCsvGenerationUnitsContractEnergySalesPCCOP(Global.unGenerationUnitsContractsEnergySalesPCCOPWriter, this.generationUnits, this.getGenerationUnitsContractEnergySalesPCCOP(), iteration);
			Global.rw.writeCsvGenerationUnitsContractEnergySalesPDMWh(Global.unGenerationUnitsContractsEnergySalesPDMWhWriter, this.generationUnits, this.getGenerationUnitsContractEnergySalesPDMWh(), iteration);
			Global.rw.writeCsvGenerationUnitsContractEnergySalesPDCOP(Global.unGenerationUnitsContractsEnergySalesPDCOPWriter, this.generationUnits, this.getGenerationUnitsContractEnergySalesPDCOP(), iteration);
			Global.rw.writeCsvGenerationUnitsPoolEnergyPurchasesMWh(Global.unGenerationUnitsPoolEnergyPurchasesMWhWriter, this.generationUnits, this.getGenerationUnitsPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvGenerationUnitsPoolEnergyPurchasesCOP(Global.unGenerationUnitsPoolEnergyPurchasesCOPWriter, this.generationUnits, this.getGenerationUnitsPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvGenerationUnitsPoolEnergySalesMWh(Global.unGenerationUnitsPoolEnergySalesMWhWriter, this.generationUnits, this.getGenerationUnitsPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvGenerationUnitsPoolEnergySalesCOP(Global.unGenerationUnitsPoolEnergySalesCOPWriter, this.generationUnits, this.getGenerationUnitsPoolEnergySalesCOP(), iteration);
			Global.rw.writeCsvPositiveReconciliationMWh(Global.unGenerationUnitsPositiveReconciliationMWhWriter, this.generationUnits, this.getPositiveReconciliationMWh(), iteration);
			Global.rw.writeCsvPositiveReconciliationCOP(Global.unGenerationUnitsPositiveReconciliationCOPWriter, this.generationUnits, this.getPositiveReconciliationCOP(), iteration);
			Global.rw.writeCsvNegativeReconciliationMWh(Global.unGenerationUnitsNegativeReconciliationMWhWriter, this.generationUnits, this.getNegativeReconciliationMWh(), iteration);
			Global.rw.writeCsvNegativeReconciliationCOP(Global.unGenerationUnitsNegativeReconciliationCOPWriter, this.generationUnits, this.getNegativeReconciliationCOP(), iteration);
			Global.rw.writeCsvSettlementUsageChargesGeneratorsCOP(Global.unGenerationUnitsSettlementUsageChargesCOPWriter, this.generationUnits, this.getSettlementUsageChargesCOP(), iteration);
			Global.rw.writeCsvGenerationUnitsSetttlementEnergyMarketCOP(Global.unGenerationUnitsSettlementEnergyMarketCOPWriter, this.generationUnits, this.getGenerationUnitsSetttlementEnergyMarketCOP(), iteration);	

			//
			// Transmisores
			//
			Global.rw.writeCsvSettlementUsageChargesCOP(Global.unTransmittersSettlementUsageChargesCOPWriter, this.transmitters, this.settlementUsageChargesCOP, iteration);
			
			
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°____________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|									|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	RESULTADOS MERCADO MULTINODAL	|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|____________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°

			//
			// cálculo de la liquidación comercializadores, unidades de generación y transmisores
			//
			this.setNodSettlementEqualElemets(); 						// establecer elementos comunes entre la liquidación uninodal y multinodal
			this.nodRetailersEnergyMarketSettlement();					// liquidación energía comercializadores
			this.nodGenerationUnitsEnergyMarketSettlement();			// liquidación total unidades de generación
			
			this.setNodRetailersSetttlementEnergyMarketCOP();			// liquidación total comercializadores
			this.setNodGenerationUnitsSetttlementEnergyMarketCOP();		// liquidación total comercializadores
			this.nodDifferenceSettlementCOP5(); 						// diferencia entra pagos comercializadores y generadores
			this.nodTransmittersCongestionSettlementCOP5();				// liquidación de las rentas por congestión para los transmisores
			this.setNodCongestionRentsFundCOP(							// fondo de rentas por congestión
					Global.factory.productEscalarVector(
							this.getNodCongestionRentsCOP(), 
							(1-Global.proportionCongestionRentsTransmitters)));
			
			this.nodTransmittersComplementaryChargesSettlementCOP5();	// liquidación de los cargos complementarios para los transmisores
			this.setNodTransmittersSettlementComplementaryChargesCOP(); // valor de los cargos complementarios de transmisión para el mercado
			this.nodRetailersComplementaryChargesSettlementCOP5(); 		// liquidación de los cargos complementarios a los comercializadores
			
			//this.nodRetailersCongestionSettlementCOP();				// liquidación de las rentas por congestión a los comercializadores
			//this.setNodRetailersCongestionRentsCOP(); 				// valor total de las rentas por congestión para los comercializadores
						
			this.nodTransmittersUsageChargesSettlementCOP5();			// liquidación de los cargos por uso para los transmisores
			this.setNodTransmittersSettlementUsageChargesCOP(); 		// valor de los cargos por uso para los transmisores
			this.nodRetailersUsageChargesSettlementCOP5();				// liquidación transmisión y energía comercializadores 
			
			//
			// valores para el mercado: comercializadores
			//
			this.setNodRetailersContractEnergyPurchasesPCMWh();		// magnitud compras en contratos PC
			this.setNodRetailersContractEnergyPurchasesPCCOP();		// valor compras en contratos PC
			this.setNodRetailersContractEnergyPurchasesPDMWh();		// magnitud compras en contratos PD
			this.setNodRetailersContractEnergyPurchasesPDCOP();		// valor compras en contratos PD
			this.setNodRetailersPoolEnergyPurchasesMWh();			// magnitud compras en bolsa
			this.setNodRetailersPoolEnergyPurchasesCOP();			// valor compras en bolsa
			this.setNodRetailersPoolEnergySalesMWh();				// magnitud ventas en bolsa
			this.setNodRetailersPoolEnergySalesCOP();				// valor ventas en bolsa
			this.setNodRetailersSetttlementEnergyMarketCOP();		// liquidación total comercializadores

			//
			// valores para el mercado: unidades de generación
			//
			this.setNodGenerationUnitsContractEnergySalesPCMWh();	// magnitud compras en contratos PC
			this.setNodGenerationUnitsContractEnergySalesPCCOP();	// valor compras en contratos PC
			this.setNodGenerationUnitsContractEnergySalesPDMWh();	// magnitud compras en contratos PD
			this.setNodGenerationUnitsContractEnergySalesPDCOP();	// valor compras en contratos PD
			this.setNodGenerationUnitsPoolEnergyPurchasesMWh();		// magnitud compras en bolsa
			this.setNodGenerationUnitsPoolEnergyPurchasesCOP();		// valor compras en bolsa
			this.setNodGenerationUnitsPoolEnergySalesMWh();			// magnitud ventas en bolsa
			this.setNodGenerationUnitsPoolEnergySalesCOP();			// valor ventas en bolsa
			this.setNodGenerationUnitsSetttlementEnergyMarketCOP();	// liquidación total comercializadores
			
			//
			// valores para el mercado: transmisión
			// 			
			this.setNodTransmitterCongestionRentsCOP(); 				// valor total de la congestión para los transmisores
			//this.setNodRetailersCongestionRentsCOP();					// valor total de la congestión para los comercializadores
			this.setNodRetailersComplementaryChargesCOP(); 				// valor total de los cargos complemetarios para los comercializadores
			this.setNodRetailersUsageChargesCOP(); 						// valor total de los cargos por uso para los comercializadores
			
			//
			// escritura de resultados: comercializadores
			//
			Global.rw.writeCsvNodRetailersContractEnergyPurchasesPCMWh(Global.nodRetailersContractsEnergyPurchasesPCMWhWriter, this.retailers, this.getNodRetailersContractEnergyPurchasesPCMWh(), iteration);
			Global.rw.writeCsvNodRetailersContractEnergyPurchasesPCCOP(Global.nodRetailersContractsEnergyPurchasesPCCOPWriter, this.retailers, this.getNodRetailersContractEnergyPurchasesPCCOP(), iteration);
			Global.rw.writeCsvNodRetailersContractEnergyPurchasesPDMWh(Global.nodRetailersContractsEnergyPurchasesPDMWhWriter, this.retailers, this.getNodRetailersContractEnergyPurchasesPDMWh(), iteration);
			Global.rw.writeCsvNodRetailersContractEnergyPurchasesPDCOP(Global.nodRetailersContractsEnergyPurchasesPDCOPWriter, this.retailers, this.getNodRetailersContractEnergyPurchasesPDCOP(), iteration);
			Global.rw.writeCsvNodRetailersPoolEnergyPurchasesMWh(Global.nodRetailersPoolEnergyPurchasesMWhWriter, this.retailers, this.getNodRetailersPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvNodRetailersPoolEnergyPurchasesCOP(Global.nodRetailersPoolEnergyPurchasesCOPWriter, this.retailers, this.getNodRetailersPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvNodRetailersPoolEnergySalesMWh(Global.nodRetailersPoolEnergySalesMWhWriter, this.retailers, this.getNodRetailersPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvNodRetailersPoolEnergySalesCOP(Global.nodRetailersPoolEnergySalesCOPWriter, this.retailers, this.getNodRetailersPoolEnergySalesCOP(), iteration);
			//Global.rw.writeCsvNodRetailersCongestionRentsCOP(Global.nodRetailersSettlementCongestionRentsCOPWriter, this.retailers, this.getNodRetailersCongestionRentsCOP(), iteration);
			Global.rw.writeCsvNodRetailersComplementaryChargesCOP(Global.nodRetailersSettlementComplementaryChargesCOPWriter, this.retailers, this.getNodRetailersComplementaryChargesCOP(), iteration);
			Global.rw.writeCsvNodRetailersUsageChargesCOP(Global.nodRetailersSettlementUsageChargesCOPWriter, this.retailers, this.getNodRetailersUsageChargesCOP(), iteration);
			Global.rw.writeCsvNodRetailersSetttlementEnergyMarketCOP(Global.nodRetailersSettlementEnergyMarketCOPWriter, this.retailers, this.getNodRetailersSetttlementEnergyMarketCOP(), iteration);
			//
			// escritura de resultados: unidades de generación
			//
			Global.rw.writeCsvNodGenerationUnitsContractEnergySalesPCMWh(Global.nodGenerationUnitsContractsEnergySalesPCMWhWriter, this.generationUnits, this.getNodGenerationUnitsContractEnergySalesPCMWh(), iteration);
			Global.rw.writeCsvNodGenerationUnitsContractEnergySalesPCCOP(Global.nodGenerationUnitsContractsEnergySalesPCCOPWriter, this.generationUnits, this.getNodGenerationUnitsContractEnergySalesPCCOP(), iteration);
			Global.rw.writeCsvNodGenerationUnitsContractEnergySalesPDMWh(Global.nodGenerationUnitsContractsEnergySalesPDMWhWriter, this.generationUnits, this.getNodGenerationUnitsContractEnergySalesPDMWh(), iteration);
			Global.rw.writeCsvNodGenerationUnitsContractEnergySalesPDCOP(Global.nodGenerationUnitsContractsEnergySalesPDCOPWriter, this.generationUnits, this.getNodGenerationUnitsContractEnergySalesPDCOP(), iteration);
			Global.rw.writeCsvNodGenerationUnitsPoolEnergyPurchasesMWh(Global.nodGenerationUnitsPoolEnergyPurchasesMWhWriter, this.generationUnits, this.getNodGenerationUnitsPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvNodGenerationUnitsPoolEnergyPurchasesCOP(Global.nodGenerationUnitsPoolEnergyPurchasesCOPWriter, this.generationUnits, this.getNodGenerationUnitsPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvNodGenerationUnitsPoolEnergySalesMWh(Global.nodGenerationUnitsPoolEnergySalesMWhWriter, this.generationUnits, this.getNodGenerationUnitsPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvNodGenerationUnitsPoolEnergySalesCOP(Global.nodGenerationUnitsPoolEnergySalesCOPWriter, this.generationUnits, this.getNodGenerationUnitsPoolEnergySalesCOP(), iteration);
			Global.rw.writeCsvNodGenerationUnitsSetttlementEnergyMarketCOP(Global.nodGenerationUnitsSettlementEnergyMarketCOPWriter, this.generationUnits, this.getNodGenerationUnitsSetttlementEnergyMarketCOP(), iteration);	
	
			//
			// escritura de resultados: transmisión
			//
			Global.rw.writeCsvNodTransmittersSettlementCongestionCOP(Global.nodTransmittersSettlementCongestionRentsCOPWriter, this.transmitters, this.getNodTransmitterCongestionRentsCOP(), iteration);
			Global.rw.writeCsvNodTransmittersSettlementComplementaryChargesCOP(Global.nodTransmittersSettlementComplementaryChargesCOPWriter, this.transmitters, this.getNodTransmittersSettlementComplementaryChargesCOP(), iteration);
			Global.rw.writeCsvNodTransmittersSettlementUsageChargesCOP(Global.nodTransmittersSettlementUsageChargesCOPWriter, this.transmitters, this.getNodTransmittersSettlementUsageChargesCOP(), iteration);
				
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°_____________________________________________°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|												|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|	RESULTADOS MERCADO MULTINODAL CON FTRS		|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°|_____________________________________________|°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			// °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°
			//
			
			// actualización del arreglo de precios nodales históricos
			this.getHistoricalNodalPrices().add(Global.factory.mat2vec(this.realDispatch.getNodalPrices(),Global.nNodes,24));
			//System.out.println("\nprecios nodales\t" + (iteration + 1) + "\t" + this.historicalNodalPrices.size());
			//Global.rw.printList(this.getHistoricalNodalPrices());
			
			// actualización del arreglo de demanda de energía histórica
			this.getHistoricalEnergyDemand().add(Global.factory.mat2vec(this.realDispatch.getEnergyDemand(), Global.nNodes, 24));
			//System.out.println("\ndemanda de energía\t" + (iteration + 1) + "\t" + this.historicalEnergyDemand.size());
			//Global.rw.printList(this.getHistoricalEnergyDemand());
			
			// actualización del arreglo de demanda de energía histórica por hora
			this.getHistoricalHourlyEnergyDemand().add(this.getEnergyDemand());
			//System.out.println("\ndemanda de energía horaria\t" + (iteration + 1) + "\t" + this.historicalHourlyEnergyDemand.size());
			//Global.rw.printList(this.getHistoricalHourlyEnergyDemand());
			
			this.setFtrSetSettlementEqualElemets();				// establecer elementos comunes entre la liquidación uninodal y multinodal con ftrs
			this.ftrGenerationUnitsEnergyMarketSettlement();	// liquidación total unidades de generación
			this.ftrRetailersEnergyMarketSettlement();			// liquidación inicial mercaod de energía comercializadores
			
			this.setFtrRetailersSetttlementEnergyMarketCOP();			// liquidación total comercializadores
			this.setFtrGenerationUnitsSetttlementEnergyMarketCOP();		// liquidación total unidades de generación
			this.ftrDifferenceSettlementCOP5(); 						// diferencia entra pagos comercializadores y generadores
			
			this.ftrRetailersFtrSettlementCOP(iteration, this.getAuctionIndex());
			this.ftrRetailersFtrIncomeSettlementCOP(iteration, this.getAuctionIndex());
			this.setFtrRetailersIncomeSettlementCOP(); 		// valor total de los ingresos de los comercializadores por los FTRs
			this.setFtrRemainingCongestionRentsCOP();		// rentas por congestión después de liquidar los FTRs asignados
			
			this.ftrTransmittersComplementaryChargesSettlementCOP5();	// liquidación de los cargos complementarios para los transmisores
			this.setFtrTransmittersSettlementComplementaryChargesCOP(); // valor de los cargos complementarios de transmisión para el mercado
			this.ftrRetailersComplementaryChargesSettlementCOP5(); 		// liquidación de los cargos complementarios a los comercializadores
			
			this.ftrTransmittersCongestionSettlementCOP5(iteration, this.getAuctionIndex());				// liquidación de las rentas por congestión para los transmisores
			this.ftrTransmittersUsageChargesSettlementCOP5();
			this.setFtrCongestionRentsFundCOP(							// fondo de rentas por congestión
					Global.factory.productEscalarVector(
							this.getNodCongestionRentsCOP(), 
							(1-Global.proportionCongestionRentsTransmitters)));
			
			
			//this.ftrRetailersCongestionRentsCOP(iteration, this.getAuctionIndex());
			//this.ftrTransmittersComplementaryChargesSettlementCOP();
			//this.setFtrTransmittersSettlementComplementaryChargesCOP();
			//this.ftrRetailersComplementaryChargesSettlementCOP();
			//this.ftrRetailersUsageChargesSettlementCOP(iteration, this.getAuctionIndex());
			
			//this.setFtrRetailersCongestionRentsCOP(); 					// valor total de las rentas por congestión para los comercializadores
			//this.ftrTransmittersCongestionSettlementCOP1();				// valor total de las rentas por congestión para los transmisores
			this.setFtrIncomeSettlementCOP();							// pagos totales por FTRs
			//this.ftrTransmittersFtrAuctionIncomeCOP(iteration, this.getAuctionIndex()); // cálculo de los ingresos de los trasnmisores provenientes de los FTRs vendidos en la subasta
			//this.ftrTransmittersUsageChargesSettlementCOP(iteration, this.getAuctionIndex());
			
			//
			// valores para el mercado: comercializadores
			//
			this.setFtrRetailersContractEnergyPurchasesPCMWh();		// magnitud compras en contratos PC
			this.setFtrRetailersContractEnergyPurchasesPCCOP();		// valor compras en contratos PC
			this.setFtrRetailersContractEnergyPurchasesPDMWh();		// magnitud compras en contratos PD
			this.setFtrRetailersContractEnergyPurchasesPDCOP();		// valor compras en contratos PD
			this.setFtrRetailersPoolEnergyPurchasesMWh();			// magnitud compras en bolsa
			this.setFtrRetailersPoolEnergyPurchasesCOP();			// valor compras en bolsa
			this.setFtrRetailersPoolEnergySalesMWh();				// magnitud ventas en bolsa
			this.setFtrRetailersPoolEnergySalesCOP();				// valor ventas en bolsa
			this.setFtrRetailersSetttlementEnergyMarketCOP();		// liquidación total comercializadores
			
			//
			// valores para el mercado: unidades de generación
			//
			this.setFtrGenerationUnitsContractEnergySalesPCMWh();	// magnitud compras en contratos PC
			this.setFtrGenerationUnitsContractEnergySalesPCCOP();	// valor compras en contratos PC
			this.setFtrGenerationUnitsContractEnergySalesPDMWh();	// magnitud compras en contratos PD
			this.setFtrGenerationUnitsContractEnergySalesPDCOP();	// valor compras en contratos PD
			this.setFtrGenerationUnitsPoolEnergyPurchasesMWh();		// magnitud compras en bolsa
			this.setFtrGenerationUnitsPoolEnergyPurchasesCOP();		// valor compras en bolsa
			this.setFtrGenerationUnitsPoolEnergySalesMWh();			// magnitud ventas en bolsa
			this.setFtrGenerationUnitsPoolEnergySalesCOP();			// valor ventas en bolsa
			this.setFtrGenerationUnitsSetttlementEnergyMarketCOP();	// liquidación total comercializadores
			
			//
			// valores para el mercado: transmisión
			// 
			this.setFtrTransmittersSettlementUsageChargesCOP(); 		// valor de los cargos por uso para los transmisores
			this.setFtrTransmitterCongestionRentsCOP(); 				// valor total de la congestión para los transmisores
			this.setFtrRetailersComplementaryChargesCOP(); 				// valor total de los cargos complemetarios para los comercializadores
			//this.setFtrRetailersCongestionRentsCOP(); 					// valor total de las rentas por congestión para los comercializadores
			//this.setFtrRetailersUsageChargesCOP(); 						// valor total de los cargos por uso para los comercializadores
			
			//
			// escritura de resultados: comercializadores
			//
			Global.rw.writeCsvFtrRetailersContractEnergyPurchasesPCMWh(Global.ftrRetailersContractsEnergyPurchasesPCMWhWriter, this.retailers, this.getFtrRetailersContractEnergyPurchasesPCMWh(), iteration);
			Global.rw.writeCsvFtrRetailersContractEnergyPurchasesPCCOP(Global.ftrRetailersContractsEnergyPurchasesPCCOPWriter, this.retailers, this.getFtrRetailersContractEnergyPurchasesPCCOP(), iteration);
			Global.rw.writeCsvFtrRetailersContractEnergyPurchasesPDMWh(Global.ftrRetailersContractsEnergyPurchasesPDMWhWriter, this.retailers, this.getFtrRetailersContractEnergyPurchasesPDMWh(), iteration);
			Global.rw.writeCsvFtrRetailersContractEnergyPurchasesPDCOP(Global.ftrRetailersContractsEnergyPurchasesPDCOPWriter, this.retailers, this.getFtrRetailersContractEnergyPurchasesPDCOP(), iteration);
			Global.rw.writeCsvFtrRetailersPoolEnergyPurchasesMWh(Global.ftrRetailersPoolEnergyPurchasesMWhWriter, this.retailers, this.getFtrRetailersPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvFtrRetailersPoolEnergyPurchasesCOP(Global.ftrRetailersPoolEnergyPurchasesCOPWriter, this.retailers, this.getFtrRetailersPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvFtrRetailersPoolEnergySalesMWh(Global.ftrRetailersPoolEnergySalesMWhWriter, this.retailers, this.getFtrRetailersPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvFtrRetailersPoolEnergySalesCOP(Global.ftrRetailersPoolEnergySalesCOPWriter, this.retailers, this.getFtrRetailersPoolEnergySalesCOP(), iteration);
			//Global.rw.writeCsvFtrRetailersCongestionRentsCOP(Global.ftrRetailersSettlementCongestionRentsCOPWriter, this.retailers, this.getFtrRetailersCongestionRentsCOP(), iteration);
			Global.rw.writeCsvFtrRetailersComplementaryChargesCOP(Global.ftrRetailersSettlementComplementaryChargesCOPWriter, this.retailers, this.getFtrRetailersComplementaryChargesCOP(), iteration);
			//Global.rw.writeCsvFtrRetailersUsageChargesCOP(Global.ftrRetailersSettlementUsageChargesCOPWriter, this.retailers, this.getFtrRetailersUsageChargesCOP(), iteration);
			Global.rw.writeCsvFtrRetailersSetttlementEnergyMarketCOP(Global.ftrRetailersSettlementEnergyMarketCOPWriter, this.retailers, this.getFtrRetailersSetttlementEnergyMarketCOP(), iteration);
			//
			// escritura de resultados: unidades de generación
			//
			Global.rw.writeCsvFtrGenerationUnitsContractEnergySalesPCMWh(Global.ftrGenerationUnitsContractsEnergySalesPCMWhWriter, this.generationUnits, this.getFtrGenerationUnitsContractEnergySalesPCMWh(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsContractEnergySalesPCCOP(Global.ftrGenerationUnitsContractsEnergySalesPCCOPWriter, this.generationUnits, this.getFtrGenerationUnitsContractEnergySalesPCCOP(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsContractEnergySalesPDMWh(Global.ftrGenerationUnitsContractsEnergySalesPDMWhWriter, this.generationUnits, this.getFtrGenerationUnitsContractEnergySalesPDMWh(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsContractEnergySalesPDCOP(Global.ftrGenerationUnitsContractsEnergySalesPDCOPWriter, this.generationUnits, this.getFtrGenerationUnitsContractEnergySalesPDCOP(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsPoolEnergyPurchasesMWh(Global.ftrGenerationUnitsPoolEnergyPurchasesMWhWriter, this.generationUnits, this.getFtrGenerationUnitsPoolEnergyPurchasesMWh(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsPoolEnergyPurchasesCOP(Global.ftrGenerationUnitsPoolEnergyPurchasesCOPWriter, this.generationUnits, this.getFtrGenerationUnitsPoolEnergyPurchasesCOP(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsPoolEnergySalesMWh(Global.ftrGenerationUnitsPoolEnergySalesMWhWriter, this.generationUnits, this.getFtrGenerationUnitsPoolEnergySalesMWh(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsPoolEnergySalesCOP(Global.ftrGenerationUnitsPoolEnergySalesCOPWriter, this.generationUnits, this.getFtrGenerationUnitsPoolEnergySalesCOP(), iteration);
			Global.rw.writeCsvFtrGenerationUnitsSetttlementEnergyMarketCOP(Global.ftrGenerationUnitsSettlementEnergyMarketCOPWriter, this.generationUnits, this.getFtrGenerationUnitsSetttlementEnergyMarketCOP(), iteration);	
			
			//
			// escritura de resultados: transmisión
			//
			Global.rw.writeCsvFtrTransmittersSettlementCongestionCOP(Global.ftrTransmittersSettlementCongestionRentsCOPWriter, this.transmitters, this.getFtrTransmitterCongestionRentsCOP(), iteration);
			Global.rw.writeCsvFtrTransmittersSettlementComplementaryChargesCOP(Global.ftrTransmittersSettlementComplementaryChargesCOPWriter, this.transmitters, this.getFtrTransmittersSettlementComplementaryChargesCOP(), iteration);
			Global.rw.writeCsvFtrTransmittersSettlementUsageChargesCOP(Global.ftrTransmittersSettlementUsageChargesCOPWriter, this.transmitters, this.getFtrTransmittersSettlementUsageChargesCOP(), iteration);
			
			
			//--------------------------------------------------------------------------------------------------------------------------------------
			// escritura de resultados globales
			Global.rw.writeCsvSpotPrices(Global.spotPricesWriter, this.getIdealDispatch().getSpotPrices(), iteration);
			Global.rw.writeCsvNodalPrices(Global.nodalPricesWriter, Global.factory.mat2vec(this.getRealDispatch().getNodalPrices(), Global.nNodes, 24), iteration);
			
			// mercado nodal
			Global.rw.writeCsvCongestionRents(Global.nodCongestionRentsWriter, this.getNodCongestionRentsCOP(), iteration);
			Global.rw.writeCsvCongestionRentsFund(Global.nodCongestionRentsFundWriter, this.getNodCongestionRentsFundCOP(), iteration);
			
			// mercado nodal con ftrs
			Global.rw.writeCsvCongestionRents(Global.ftrCongestionRentsWriter, this.getFtrCongestionRentsCOP(), iteration);
			Global.rw.writeCsvCongestionRentsFund(Global.ftrCongestionRentsFundWriter, this.getFtrCongestionRentsFundCOP(), iteration);		
			//--------------------------------------------------------------------------------------------------------------------------------------
			
			
			/////////////////////////////////////////////////////////////
			this.ftrInversionSettlement();
			/////////////////////////////////////////////////////////////
			
			if(MathFuns.IsIn(iteration,Global.ftrAuctionDate) && iteration != Global.ftrAuctionDate[Global.ftrAuctionDate.length-1])
			{
				// actualizamos el índice de la subasta
				this.setAuctionIndex(this.getAuctionIndex() + 1);
				
				// marca inicio de la subasta
				System.out.println("\n\n----------------------------------------- inicio de la subasta" + "\t ----------------------------------" + (this.auctionIndex));
				
				// crear la subasta
				this.setFtrAuction(new FtrAuction());
				this.getFtrAuction().setFtrAuctionIndex(this.getAuctionIndex());
				this.getFtrAuction().setFtrAuctionDate(iteration);
				this.getFtrAuction().setFtrDemand(new double[24]);
				this.getFtrAuction().setFtrHourlyAssigns(new ArrayList<Ftr>());
				this.getFtrAuction().setFtrAssigns(new ArrayList<List<Ftr>>(24));
				this.getFtrAuction().setFtrRemainingCapacity(new double[24]);
				this.getFtrAuction().setFtrAuctionIncome(new double[24]);
				this.getFtrAuction().setFtrOrganized(new ArrayList<Ftr>());
				
				// el operador define la capacidad de transmisión a subastar
				this.setFtrAuctionCapacity();
				this.getFtrAuction().setAuctionCapacity(this.getFtrAuctionCapacity());
				System.out.println("\ncapacidad de transmisión a subastar:" );
				Global.rw.printVector(this.getFtrAuctionCapacity());
				
				// los generadores determinan su precio de reserva en cada contrato
				for(int gen = 0; gen < Global.nGencos; gen++){
					this.generators.get(gen).calculateFtrReservePrice(iteration, this);
				}
				System.out.println("\nprecios de reserva de los generadores:" );
				for(int gen = 0; gen < Global.nGencos; gen++){
					System.out.println(this.generators.get(gen).getGeneratorCod());
					Global.rw.printDoubleMatrix(this.generators.get(gen).getFtrReservePrice());
				}
				
				// los comercializadores determinan su precio de reserva en cada contrato
				for(int ret = 0; ret < Global.nRetailers; ret++){
					this.retailers.get(ret).calculateFtrReservePrice(iteration, this);
				}
				System.out.println("\nprecios de reserva de los comercializadores:" );
				for(int ret = 0; ret < Global.nRetailers; ret++){
					System.out.println(this.retailers.get(ret).getRetailerCod());
					Global.rw.printDoubleMatrix(this.retailers.get(ret).getFtrReservePrice());
				}
				
				// el operador determina su precio de reserva para los FTRs en todas las horas
				//this.calculateFtrReservePrice(iteration);
				//System.out.println("\n\nprecios de reserva del operador:" );
				//Global.rw.printVector(this.getFtrReservePrice());
				
				// determinación del universo de FTRs
				System.out.println("\nproducto para subastar:" );
				List<List<Ftr>> listFtrs = this.defineFtrs(iteration);
				Global.rw.printListFtrs(listFtrs);
				
				// actualización del precio del producto a subastar
				this.setFtrProductPrice(this.getFtrReservePrice());
		
				// inicio de las rondas de subasta
				int round = 0;
				for(int h = 0; h < 24; h++)
				{
					System.out.println("\n----------"+ "\t" + "hora" + "\t" + (h + 1) + "\t" + "round" + "\t" + (round + 1) + "\t" + "-----------");
					
					// el operador define el producto a subastar en cada hora
					this.calculateHourlyFtrReservePrice(iteration, h, round, null);	// precio del producto
					this.ftrAuctionedProduct = new Ftr("Operator", Global.proportionAuctionCapacity*this.getFtrAuctionCapacity()[h],
									this.getFtrHourlyReservePrice(), iteration + 1,Global.ftrDuration);
					
					// imprimir el producto a subastar
					System.out.println("producto para subastar:");
					this.ftrAuctionedProduct.printFtrProduct(this.ftrAuctionedProduct);
					
					// arreglo para las ofertas de generadores y comercializadores
					this.setFtrBids(new ArrayList<Ftr>());
					
					// generadores hacen sus ofertas
					/*System.out.println("generadores: ofertas de ftrs");
					for(int gen = 0; gen < Global.nGencos; gen++){
						List<Ftr> ftrs = this.generators.get(gen).ftrSetHourlyBids(iteration, h, round, this);
						for(int bid = 0; bid < ftrs.size(); bid++){
							this.getFtrBids().add(ftrs.get(bid));
							ftrs.get(bid).printHourlyFtrContract();
						}
					}*/
					
					// comercializadores hacen sus ofertas
					System.out.println("comercializadores: ofertas de ftrs");
					for(int ret = 0; ret < Global.nRetailers; ret++){
						List<Ftr> ftrs = this.retailers.get(ret).ftrSetHourlyBids(iteration, h, round, this);
						for(int bid = 0; bid < ftrs.size(); bid++){
							this.getFtrBids().add(ftrs.get(bid));
							ftrs.get(bid).printHourlyFtrContract();
						}
					}
					
					// calcular la demanda total de capacidad en los FTRs
					this.getFtrAuction().getFtrDemand()[h] = this.ftrCalculateHourlyDemand(this.getFtrBids());
					System.out.println("demanda total de capacidad");
					Global.rw.printVector(this.getFtrAuction().getFtrDemand());
					
					
					// mientras la demanda de capacidad en FTRs sea mayor que la oferta definida por el operador, la subasta continua
					while( this.getFtrAuction().getFtrDemand()[h] > this.ftrAuctionedProduct.getHourlyFtrPower()){
						
						// informe del nuevo estado de la subasta
						System.out.println("FTR demand > Ftr supply: new round");
						
						round = round + 1;
						System.out.println("\n----------"+ "\t" + "hora" + "\t" + (h + 1) + "\t" + "round" + "\t" + (round + 1) + "\t" + "-----------");
						
						// el operador incrementa el precio del producto a subastar en cada hora
						this.calculateHourlyFtrReservePrice(iteration, h, round, this.getFtrBids());	// precio del producto
						this.ftrAuctionedProduct.setHourlyFtrPrice(this.ftrHourlyReservePrice);
											
						// imprimir el nuevo producto para subastar
						System.out.println("nuevo producto a subastar: precio de reserva modificado" );
						this.ftrAuctionedProduct.printFtrProduct(this.ftrAuctionedProduct);
						
						// se vacía el vector de ofertas
						this.getFtrBids().clear();
						
						// generadores realizan nuevas ofertas
						/*for(int gen = 0; gen < Global.nGencos; gen++){
							List<Ftr> ftrs = this.generators.get(gen).ftrSetHourlyBids(iteration, h, round, this);
							for(int bid = 0; bid < ftrs.size(); bid++){
								this.getFtrBids().add(ftrs.get(bid));
								ftrs.get(bid).printHourlyFtrContract();
							}
						}*/
						
						// comercializadores realizan nuevas ofertas
						for(int ret = 0; ret < Global.nRetailers; ret++){
							List<Ftr> ftrs = this.retailers.get(ret).ftrSetHourlyBids(iteration, h, round, this);
							for(int bid = 0; bid < ftrs.size(); bid++){
								this.getFtrBids().add(ftrs.get(bid));
								ftrs.get(bid).printHourlyFtrContract();
							}
						}
												
						// recalcular la demanda total de capacidad en los FTRs
						this.getFtrAuction().getFtrDemand()[h] = this.ftrCalculateHourlyDemand(this.getFtrBids());
						System.out.println("demanda total de capacidad");
						Global.rw.printVector(this.getFtrAuction().getFtrDemand());
					}
					
					// información sobre el estado de la subasta
					System.out.println("FTR demand < Ftr supply: auction ends");
					
					// asignación de los FTRs a los generadores y comercializadores. 
					this.ftrHourlyAssigns5(h, this.getFtrBids(), 
							this.ftrAuctionedProduct.getHourlyFtrPower(), 
							this.ftrAuctionedProduct.getHourlyFtrPrice(),
							this.ftrAuctionedProduct.getFtrInitialDate(),
							this.ftrAuctionedProduct.getFtrDuration());
					
					// se organizan los FTRs para darles una estructura diaria
					this.ftrFtrOrganizer(h, this.getFtrAuction().getFtrOrganized(),this.getFtrBids());
										
					// imprimir la lista de FTRs asignados a generadores y comercializadores en la hora h
					System.out.println("FTRs asignados en la hora:\t" + (h+1));
					for(int ftr = 0; ftr < this.getFtrAuction().getFtrHourlyAssigns().size(); ftr++){
						this.getFtrAuction().getFtrHourlyAssigns().get(ftr).printAssignedHourlyFtr();	
					}
					
					// capacidad remanente
					this.getFtrAuction().getFtrRemainingCapacity()[h] = this.ftrRemainingCapacity(h);
					System.out.println("capacidad remanente:\t" + Global.decimalFormatter.format(this.getFtrAuction().getFtrRemainingCapacity()[h]));
					
					// reiniciar el valor de las rondas para las demás horas
					round = 0;
					
					// cálculo del ingreso en la subasta para la hora h
					this.getFtrAuction().getFtrAuctionIncome()[h]  = this.ftrHourlyAuctionIncome(h, this.getFtrAuction().getFtrHourlyAssigns()); 
				}		
				// imprimir los ingresos recolectados en la subasta en cada hora
				System.out.println("ingreso en la subasta de FTRs");
				Global.rw.printVector(this.getFtrAuction().getFtrAuctionIncome());
				
				// clasifica los FTRs asignados en la subasta según el comercializador que los adquirió
				this.ftrRetailersAssignedRights(this.getFtrAuction().getFtrAssigns());
				
				// cantidad de FTRs asignados en la subasta
				System.out.println("-----------------\t" + this.getFtrAuction().getFtrOrganized().size());
				Global.rw.printOrganizedFtrs(this.getFtrAuction().getFtrOrganized());
				//----------------------------------------------------------------------------------------------------------------------------
				// características de la subasta
				Global.rw.writeCsvAuctionFeatures(Global.auctionFeaturesWriter, this.getFtrAuction(), iteration);
				
				// características de los ftrs asignados
				Global.rw.writeCsvAsignedFtrsFeatures(Global.ftrsFeaturesWriter, this.getFtrAuction(), iteration);
				
				// características de los ftrs asignados
				Global.rw.writeCsvOrganizedFtrsFeatures(Global.organizedFtrsFeaturesWriter, this.getFtrAuction(), iteration);				
				//----------------------------------------------------------------------------------------------------------------------------
			}
			
			System.out.println("siiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
			
		} catch (Exception e) {
			System.out.println("management ->"+e);
		}
	}
}