package utilities;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.ImageIcon;

import market.PowerBid;
import market.PowerDemand;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

//import java.awt.Font;

//import javax.swing.JFrame;

//import org.jfree.chart.ChartPanel;

public class Global {
	
	//
	// Indices
	//
	public static int nContracts 			= 4;  	// número de contratos de generación
	public static int horizon				= 365; 	// número de períodos de simulación
	public static int nlags					= 30; 	// número de rezagos para promediación del precio [VERIFICAR]
	public static int[] lengthMonth 		= {31,28,31,30,31,30,31,31,30,31,30,31}; // longitud de cada uno de los meses
	public static int[] lengthMonthLeapYear = {31,29,31,30,31,30,31,31,30,31,30,31}; // longitud de cada uno de los meses en un año bisiesto
	//public static int[] ftrAuctionDate 		= {30,58,89,119,150,180,211,242,272,303,333,364,395,423,454,484,515,545,576,607,637,668,698,729}; // fechas de las subastas
	public static int[] ftrAuctionDate 		= {30,59,90,120,151,181,212,243,273,304,334,365}; // fechas de las subastas
	//public static int[] year				= {2010, 2011}; // años considerados
	public static int[] year				= {2012}; // años considerados
	public static ReadWrite rw				= new  ReadWrite();	// función para leer y escribir
	public static ArrayFactory factory 		= new ArrayFactory(); // función para modificar los arrays
	public static ChargeInformation charge 	= new ChargeInformation(); // funciones para cargar información
	public static double ftrPrecentPriceIncrement = 0.1; // porcentaje de incremento del precio del FTR
	
	public static double demandPanama = 240;
	public static double generationPanama = 17;
	
	
	//
	// directorios para accesoa la información
	//
	public static String directory 			= "F:/power_market_agents/Models/ftrModel/";
	public static String directoryResults 	= directory +  "results/";
	public static String directoryData 		= directory +  "data/";
	public static String directoryIcons 	= directory +  "icons/";
	
	//
	// rutas de acceso a los archivos de datos
	//
	public static String supplyS 	= directoryData + "supply.csv";			// archivo de ofertas
	public static String demandS 	= directoryData + "demand.csv";			// archivo de demandas
	public static String demandColS = directoryData + "demandCol.csv";		// archivo de demandas nacionales
	public static String demandIntS = directoryData + "demandInt.csv";		// archivo de demandas internacionales
	
	public static String supplyS2012 	= directoryData + "supply2012.csv";			// archivo de ofertas 2012
	public static String demandS2012 	= directoryData + "demand2012.csv";			// archivo de demandas 2012
	public static String demandColS2012 = directoryData + "demandCol2012.csv";		// archivo de demandas nacionales 2012
	public static String demandIntS2012 = directoryData + "demandInt2012.csv";		// archivo de demandas internacionales 2012
	
	public static String usageChargesS	= directoryData + "usageCharges.csv";	// archivo para la lectura de los cargos por uso
	
	//
	// rutas de escritura de los archivos de resultados
	//
	public static String idealS 	= directoryResults + "idealResults.csv";	// archivo para los resultados del despacho ideal
	public static String realS  	= directoryResults + "realResults.csv";		// archivo para los resultados del despacho real
	public static String contractsS = directoryResults + "contracts.csv";	// archivo para los resultados de los contratos
	public static String unitsS  	= directoryResults + "units.csv";	// archivo para los resultados de los contratos
	
	// archivo para los resultados de la liquidación del mercado de energía de los comercializadores
	public static String unSettlementRetailersS  	= directoryResults + "unSettlementRetailers.csv";	
	public static CsvWriter unSettlementRetailersWriter = new CsvWriter(Global.unSettlementRetailersS);
	
	public static String nodSettlementRetailersS  	= directoryResults + "nodSettlementRetailers.csv";	
	public static CsvWriter nodSettlementRetailersWriter = new CsvWriter(Global.nodSettlementRetailersS);
	
	public static String ftrSettlementRetailersS  	= directoryResults + "ftrSettlementRetailers.csv";	
	public static CsvWriter ftrSettlementRetailersWriter = new CsvWriter(Global.ftrSettlementRetailersS);
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// RESULTADOS DE INTERÉS
	public static String spotPrices  	= directoryResults + "z.spotPrices.csv";	
	public static CsvWriter spotPricesWriter = new CsvWriter(Global.spotPrices);
		
	public static String nodalPrices  	= directoryResults + "z.nodalPrices.csv";	
	public static CsvWriter nodalPricesWriter = new CsvWriter(Global.nodalPrices);
	
	public static String nodCongestionRents  	= directoryResults + "z.nodCongestionRents.csv";	
	public static CsvWriter nodCongestionRentsWriter = new CsvWriter(Global.nodCongestionRents);
	
	public static String ftrCongestionRents  	= directoryResults + "z.ftrCongestionRents.csv";	
	public static CsvWriter ftrCongestionRentsWriter = new CsvWriter(Global.ftrCongestionRents);
	
	public static String nodCongestionRentsFund  	= directoryResults + "z.nodCongestionRentsFund.csv";	
	public static CsvWriter nodCongestionRentsFundWriter = new CsvWriter(Global.nodCongestionRentsFund );
	
	public static String ftrCongestionRentsFund   	= directoryResults + "z.ftrCongestionRentsFund.csv";	
	public static CsvWriter ftrCongestionRentsFundWriter  = new CsvWriter(Global.ftrCongestionRentsFund );
	
	public static String auctionFeatures   	= directoryResults + "z.auctionFeatures.csv";	
	public static CsvWriter auctionFeaturesWriter  = new CsvWriter(Global.auctionFeatures );
	
	public static String ftrsFeatures   	= directoryResults + "z.ftrsFeatures.csv";	
	public static CsvWriter ftrsFeaturesWriter  = new CsvWriter(Global.ftrsFeatures );
	
	public static String organizedFtrsFeatures   	= directoryResults + "z.organizedFtrsFeatures.csv";	
	public static CsvWriter organizedFtrsFeaturesWriter  = new CsvWriter(Global.organizedFtrsFeatures );
	
		
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para los resultados de la liquidación del mercado de energía de las unidades de generación
	public static String unSettlementUnits  	= directoryResults + "unSettlementUnits.csv";	
	public static CsvWriter unSettlementUnitsWriter = new CsvWriter(Global.unSettlementUnits);
	
	public static String nodSettlementUnits  	= directoryResults + "nodSettlementUnits.csv";	
	public static CsvWriter nodSettlementUnitsWriter = new CsvWriter(Global.nodSettlementUnits);
	
	public static String ftrSettlementUnits  	= directoryResults + "ftrSettlementUnits.csv";	
	public static CsvWriter ftrSettlementUnitsWriter = new CsvWriter(Global.ftrSettlementUnits);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para los resultados de la liquidación del mercado de los transmisores
	public static String unSettlementTransmitters  	= directoryResults + "unSettlementTransmitters.csv";	
	public static CsvWriter unSettlementTransmittersWriter = new CsvWriter(Global.unSettlementTransmitters);
	
	public static String nodSettlementTransmitters  	= directoryResults + "nodSettlementTransmitters.csv";	
	public static CsvWriter nodSettlementTransmittersWriter = new CsvWriter(Global.nodSettlementTransmitters);
	
	public static String ftrSettlementTransmitters  	= directoryResults + "ftrSettlementTransmitters.csv";	
	public static CsvWriter ftrSettlementTransmittersWriter = new CsvWriter(Global.ftrSettlementTransmitters);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las compras en contratos PC para los comercializadores
	public static String unRetailersContractsEnergyPurchasesPCMWh  	= directoryResults + "unRetailersContractsEnergyPurchasesPCMWh.csv";	
	public static CsvWriter unRetailersContractsEnergyPurchasesPCMWhWriter = new CsvWriter(Global.unRetailersContractsEnergyPurchasesPCMWh);
	
	public static String nodRetailersContractsEnergyPurchasesPCMWh  	= directoryResults + "nodRetailersContractsEnergyPurchasesPCMWh.csv";	
	public static CsvWriter nodRetailersContractsEnergyPurchasesPCMWhWriter = new CsvWriter(Global.nodRetailersContractsEnergyPurchasesPCMWh);
	
	public static String ftrRetailersContractsEnergyPurchasesPCMWh  	= directoryResults + "ftrRetailersContractsEnergyPurchasesPCMWh.csv";	
	public static CsvWriter ftrRetailersContractsEnergyPurchasesPCMWhWriter = new CsvWriter(Global.ftrRetailersContractsEnergyPurchasesPCMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las compras en contratos PC para los comercializadores
	public static String unRetailersContractsEnergyPurchasesPCCOP  	= directoryResults + "unRetailersContractsEnergyPurchasesPCCOP.csv";	
	public static CsvWriter unRetailersContractsEnergyPurchasesPCCOPWriter = new CsvWriter(Global.unRetailersContractsEnergyPurchasesPCCOP);
	
	public static String nodRetailersContractsEnergyPurchasesPCCOP  	= directoryResults + "nodRetailersContractsEnergyPurchasesPCCOP.csv";	
	public static CsvWriter nodRetailersContractsEnergyPurchasesPCCOPWriter = new CsvWriter(Global.nodRetailersContractsEnergyPurchasesPCCOP);
	
	public static String ftrRetailersContractsEnergyPurchasesPCCOP  	= directoryResults + "ftrRetailersContractsEnergyPurchasesPCCOP.csv";	
	public static CsvWriter ftrRetailersContractsEnergyPurchasesPCCOPWriter = new CsvWriter(Global.ftrRetailersContractsEnergyPurchasesPCCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las compras en contratos PD para los comercializadores
	public static String unRetailersContractsEnergyPurchasesPDMWh  	= directoryResults + "unRetailersContractsEnergyPurchasesPDMWh.csv";	
	public static CsvWriter unRetailersContractsEnergyPurchasesPDMWhWriter = new CsvWriter(Global.unRetailersContractsEnergyPurchasesPDMWh);
	
	public static String nodRetailersContractsEnergyPurchasesPDMWh  	= directoryResults + "nodRetailersContractsEnergyPurchasesPDMWh.csv";	
	public static CsvWriter nodRetailersContractsEnergyPurchasesPDMWhWriter = new CsvWriter(Global.nodRetailersContractsEnergyPurchasesPDMWh);
	
	public static String ftrRetailersContractsEnergyPurchasesPDMWh  	= directoryResults + "ftrRetailersContractsEnergyPurchasesPDMWh.csv";	
	public static CsvWriter ftrRetailersContractsEnergyPurchasesPDMWhWriter = new CsvWriter(Global.ftrRetailersContractsEnergyPurchasesPDMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las compras en contratos PD para los comercializadores
	public static String unRetailersContractsEnergyPurchasesPDCOP  	= directoryResults + "unRetailersContractsEnergyPurchasesPDCOP.csv";	
	public static CsvWriter unRetailersContractsEnergyPurchasesPDCOPWriter = new CsvWriter(Global.unRetailersContractsEnergyPurchasesPDCOP);
	
	public static String nodRetailersContractsEnergyPurchasesPDCOP  	= directoryResults + "nodRetailersContractsEnergyPurchasesPDCOP.csv";	
	public static CsvWriter nodRetailersContractsEnergyPurchasesPDCOPWriter = new CsvWriter(Global.nodRetailersContractsEnergyPurchasesPDCOP);
	
	public static String ftrRetailersContractsEnergyPurchasesPDCOP  	= directoryResults + "ftrRetailersContractsEnergyPurchasesPDCOP.csv";	
	public static CsvWriter ftrRetailersContractsEnergyPurchasesPDCOPWriter = new CsvWriter(Global.ftrRetailersContractsEnergyPurchasesPDCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las compras en bolsa para los comercializadores
	public static String unRetailersPoolEnergyPurchasesMWh  	= directoryResults + "unRetailersPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter unRetailersPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.unRetailersPoolEnergyPurchasesMWh);
	
	public static String nodRetailersPoolEnergyPurchasesMWh  	= directoryResults + "nodRetailersPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter nodRetailersPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.nodRetailersPoolEnergyPurchasesMWh);
	
	public static String ftrRetailersPoolEnergyPurchasesMWh  	= directoryResults + "ftrRetailersPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter ftrRetailersPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.ftrRetailersPoolEnergyPurchasesMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las compras en bolsa para los comercializadores
	public static String unRetailersPoolEnergyPurchasesCOP  	= directoryResults + "unRetailersPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter unRetailersPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.unRetailersPoolEnergyPurchasesCOP);
	
	public static String nodRetailersPoolEnergyPurchasesCOP  	= directoryResults + "nodRetailersPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter nodRetailersPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.nodRetailersPoolEnergyPurchasesCOP);
	
	public static String ftrRetailersPoolEnergyPurchasesCOP  	= directoryResults + "ftrRetailersPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter ftrRetailersPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.ftrRetailersPoolEnergyPurchasesCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------	
	// archivo para la magnitud de las ventas en bolsa para los comercializadores
	public static String unRetailersPoolEnergySalesMWh  	= directoryResults + "unRetailersPoolEnergySalesMWh.csv";	
	public static CsvWriter unRetailersPoolEnergySalesMWhWriter = new CsvWriter(Global.unRetailersPoolEnergySalesMWh);
	
	public static String nodRetailersPoolEnergySalesMWh  	= directoryResults + "nodRetailersPoolEnergySalesMWh.csv";	
	public static CsvWriter nodRetailersPoolEnergySalesMWhWriter = new CsvWriter(Global.nodRetailersPoolEnergySalesMWh);
	
	public static String ftrRetailersPoolEnergySalesMWh  	= directoryResults + "ftrRetailersPoolEnergySalesMWh.csv";	
	public static CsvWriter ftrRetailersPoolEnergySalesMWhWriter = new CsvWriter(Global.ftrRetailersPoolEnergySalesMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las ventas en bolsa para los comercializadores
	public static String unRetailersPoolEnergySalesCOP  	= directoryResults + "unRetailersPoolEnergySalesCOP.csv";	
	public static CsvWriter unRetailersPoolEnergySalesCOPWriter = new CsvWriter(Global.unRetailersPoolEnergySalesCOP);
	
	public static String nodRetailersPoolEnergySalesCOP  	= directoryResults + "nodRetailersPoolEnergySalesCOP.csv";	
	public static CsvWriter nodRetailersPoolEnergySalesCOPWriter = new CsvWriter(Global.nodRetailersPoolEnergySalesCOP);
	
	public static String ftrRetailersPoolEnergySalesCOP  	= directoryResults + "ftrRetailersPoolEnergySalesCOP.csv";	
	public static CsvWriter ftrRetailersPoolEnergySalesCOPWriter = new CsvWriter(Global.ftrRetailersPoolEnergySalesCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las rectricciones pagadas por los comercializadores
	public static String unRetailersConstraintsCOP  	= directoryResults + "unRetailersConstraintsCOP.csv";	
	public static CsvWriter unRetailersConstraintsCOPPWriter = new CsvWriter(Global.unRetailersConstraintsCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de los cargos por uso liquidados a los comercializadores
	public static String unRetailersSettlementUsageChargesCOP  	= directoryResults + "unRetailersSettlementUsageChargesCOP.csv";	
	public static CsvWriter unRetailersSettlementUsageChargesCOPWriter = new CsvWriter(Global.unRetailersSettlementUsageChargesCOP);
	
	public static String nodRetailersSettlementUsageChargesCOP  	= directoryResults + "nodRetailersSettlementUsageChargesCOP.csv";	
	public static CsvWriter nodRetailersSettlementUsageChargesCOPWriter = new CsvWriter(Global.nodRetailersSettlementUsageChargesCOP);
	
	public static String nodRetailersSettlementComplementaryChargesCOP  	= directoryResults + "nodRetailersSettlementComplementaryChargesCOP.csv";	
	public static CsvWriter nodRetailersSettlementComplementaryChargesCOPWriter = new CsvWriter(Global.nodRetailersSettlementComplementaryChargesCOP);
	
	public static String nodRetailersSettlementCongestionRentsCOP  	= directoryResults + "nodRetailersSettlementCongestionRentsCOP.csv";	
	public static CsvWriter nodRetailersSettlementCongestionRentsCOPWriter = new CsvWriter(Global.nodRetailersSettlementCongestionRentsCOP);
	
	public static String ftrRetailersSettlementUsageChargesCOP  	= directoryResults + "ftrRetailersSettlementUsageChargesCOP.csv";	
	public static CsvWriter ftrRetailersSettlementUsageChargesCOPWriter = new CsvWriter(Global.ftrRetailersSettlementUsageChargesCOP);
	
	public static String ftrRetailersSettlementComplementaryChargesCOP  	= directoryResults + "ftrRetailersSettlementComplementaryChargesCOP.csv";	
	public static CsvWriter ftrRetailersSettlementComplementaryChargesCOPWriter = new CsvWriter(Global.ftrRetailersSettlementComplementaryChargesCOP);
	
	public static String ftrRetailersSettlementCongestionRentsCOP  	= directoryResults + "ftrRetailersSettlementCongestionRentsCOP.csv";	
	public static CsvWriter ftrRetailersSettlementCongestionRentsCOPWriter = new CsvWriter(Global.ftrRetailersSettlementCongestionRentsCOP);
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la liquidación total para los comercializadores
	public static String unRetailersSettlementEnergyMarketCOP  	= directoryResults + "unRetailersSettlementEnergyMarketCOP.csv";	
	public static CsvWriter unRetailersSettlementEnergyMarketCOPWriter = new CsvWriter(Global.unRetailersSettlementEnergyMarketCOP);
	
	public static String nodRetailersSettlementEnergyMarketCOP  	= directoryResults + "nodRetailersSettlementEnergyMarketCOP.csv";	
	public static CsvWriter nodRetailersSettlementEnergyMarketCOPWriter = new CsvWriter(Global.nodRetailersSettlementEnergyMarketCOP);
	
	public static String ftrRetailersSettlementEnergyMarketCOP  	= directoryResults + "ftrRetailersSettlementEnergyMarketCOP.csv";	
	public static CsvWriter ftrRetailersSettlementEnergyMarketCOPWriter = new CsvWriter(Global.ftrRetailersSettlementEnergyMarketCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
		
	///////////////////////////////////////////////////
	public static String newLines  	= directoryResults + "z. newLines.csv";	
	public static CsvWriter newLinesWriter = new CsvWriter(Global.newLines);
	//////////////////////////////////////////////
	
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------	
	// archivo para la magnitud de las ventas en contratos PC para las unidades de generación
	public static String unGenerationUnitsContractsEnergySalesPCMWh  	= directoryResults + "unGenerationUnitsContractsEnergySalesPCMWh.csv";	
	public static CsvWriter unGenerationUnitsContractsEnergySalesPCMWhWriter = new CsvWriter(Global.unGenerationUnitsContractsEnergySalesPCMWh);
	
	public static String nodGenerationUnitsContractsEnergySalesPCMWh  	= directoryResults + "nodGenerationUnitsContractsEnergySalesPCMWh.csv";	
	public static CsvWriter nodGenerationUnitsContractsEnergySalesPCMWhWriter = new CsvWriter(Global.nodGenerationUnitsContractsEnergySalesPCMWh);
	
	public static String ftrGenerationUnitsContractsEnergySalesPCMWh  	= directoryResults + "ftrGenerationUnitsContractsEnergySalesPCMWh.csv";	
	public static CsvWriter ftrGenerationUnitsContractsEnergySalesPCMWhWriter = new CsvWriter(Global.ftrGenerationUnitsContractsEnergySalesPCMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las ventas en contratos PC para las unidades de generación
	public static String unGenerationUnitsContractsEnergySalesPCCOP  	= directoryResults + "unGenerationUnitsContractsEnergySalesPCCOP.csv";	
	public static CsvWriter unGenerationUnitsContractsEnergySalesPCCOPWriter = new CsvWriter(Global.unGenerationUnitsContractsEnergySalesPCCOP);
	
	public static String nodGenerationUnitsContractsEnergySalesPCCOP  	= directoryResults + "nodGenerationUnitsContractsEnergySalesPCCOP.csv";	
	public static CsvWriter nodGenerationUnitsContractsEnergySalesPCCOPWriter = new CsvWriter(Global.nodGenerationUnitsContractsEnergySalesPCCOP);
	
	public static String ftrGenerationUnitsContractsEnergySalesPCCOP  	= directoryResults + "ftrGenerationUnitsContractsEnergySalesPCCOP.csv";	
	public static CsvWriter ftrGenerationUnitsContractsEnergySalesPCCOPWriter = new CsvWriter(Global.ftrGenerationUnitsContractsEnergySalesPCCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las ventas en contratos PD para las unidades de generación
	public static String unGenerationUnitsContractsEnergySalesPDMWh  	= directoryResults + "unGenerationUnitsContractsEnergySalesPDMWh.csv";	
	public static CsvWriter unGenerationUnitsContractsEnergySalesPDMWhWriter = new CsvWriter(Global.unGenerationUnitsContractsEnergySalesPDMWh);
	
	public static String nodGenerationUnitsContractsEnergySalesPDMWh  	= directoryResults + "nodGenerationUnitsContractsEnergySalesPDMWh.csv";	
	public static CsvWriter nodGenerationUnitsContractsEnergySalesPDMWhWriter = new CsvWriter(Global.nodGenerationUnitsContractsEnergySalesPDMWh);
	
	public static String ftrGenerationUnitsContractsEnergySalesPDMWh  	= directoryResults + "ftrGenerationUnitsContractsEnergySalesPDMWh.csv";	
	public static CsvWriter ftrGenerationUnitsContractsEnergySalesPDMWhWriter = new CsvWriter(Global.ftrGenerationUnitsContractsEnergySalesPDMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las ventas en contratos PD para las unidades de generación
	public static String unGenerationUnitsContractsEnergySalesPDCOP  	= directoryResults + "unGenerationUnitsContractsEnergySalesPDCOP.csv";	
	public static CsvWriter unGenerationUnitsContractsEnergySalesPDCOPWriter = new CsvWriter(Global.unGenerationUnitsContractsEnergySalesPDCOP);
	
	public static String nodGenerationUnitsContractsEnergySalesPDCOP  	= directoryResults + "nodGenerationUnitsContractsEnergySalesPDCOP.csv";	
	public static CsvWriter nodGenerationUnitsContractsEnergySalesPDCOPWriter = new CsvWriter(Global.nodGenerationUnitsContractsEnergySalesPDCOP);
	
	public static String ftrGenerationUnitsContractsEnergySalesPDCOP  	= directoryResults + "ftrGenerationUnitsContractsEnergySalesPDCOP.csv";	
	public static CsvWriter ftrGenerationUnitsContractsEnergySalesPDCOPWriter = new CsvWriter(Global.ftrGenerationUnitsContractsEnergySalesPDCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las ventas en bolsa para las unidades de generación
	public static String unGenerationUnitsPoolEnergyPurchasesMWh  	= directoryResults + "unGenerationUnitsPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter unGenerationUnitsPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.unGenerationUnitsPoolEnergyPurchasesMWh);
	
	public static String nodGenerationUnitsPoolEnergyPurchasesMWh  	= directoryResults + "nodGenerationUnitsPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter nodGenerationUnitsPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.nodGenerationUnitsPoolEnergyPurchasesMWh);
	
	public static String ftrGenerationUnitsPoolEnergyPurchasesMWh  	= directoryResults + "ftrGenerationUnitsPoolEnergyPurchasesMWh.csv";	
	public static CsvWriter ftrGenerationUnitsPoolEnergyPurchasesMWhWriter = new CsvWriter(Global.ftrGenerationUnitsPoolEnergyPurchasesMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
		
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las compras en bolsa para las unidades de generación
	public static String unGenerationUnitsPoolEnergyPurchasesCOP  	= directoryResults + "unGenerationUnitsPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter unGenerationUnitsPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.unGenerationUnitsPoolEnergyPurchasesCOP);
	
	public static String nodGenerationUnitsPoolEnergyPurchasesCOP  	= directoryResults + "nodGenerationUnitsPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter nodGenerationUnitsPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.nodGenerationUnitsPoolEnergyPurchasesCOP);
	
	public static String ftrGenerationUnitsPoolEnergyPurchasesCOP  	= directoryResults + "ftrGenerationUnitsPoolEnergyPurchasesCOP.csv";	
	public static CsvWriter ftrGenerationUnitsPoolEnergyPurchasesCOPWriter = new CsvWriter(Global.ftrGenerationUnitsPoolEnergyPurchasesCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las ventas en bolsa para las unidades de generación
	public static String unGenerationUnitsPoolEnergySalesMWh  	= directoryResults + "unGenerationUnitsPoolEnergySalesMWh.csv";	
	public static CsvWriter unGenerationUnitsPoolEnergySalesMWhWriter = new CsvWriter(Global.unGenerationUnitsPoolEnergySalesMWh);
	
	public static String nodGenerationUnitsPoolEnergySalesMWh  	= directoryResults + "nodGenerationUnitsPoolEnergySalesMWh.csv";	
	public static CsvWriter nodGenerationUnitsPoolEnergySalesMWhWriter = new CsvWriter(Global.nodGenerationUnitsPoolEnergySalesMWh);
	
	public static String ftrGenerationUnitsPoolEnergySalesMWh  	= directoryResults + "ftrGenerationUnitsPoolEnergySalesMWh.csv";	
	public static CsvWriter ftrGenerationUnitsPoolEnergySalesMWhWriter = new CsvWriter(Global.ftrGenerationUnitsPoolEnergySalesMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las ventas en bolsa para las unidades de generación
	public static String unGenerationUnitsPoolEnergySalesCOP  	= directoryResults + "unGenerationUnitsPoolEnergySalesCOP.csv";	
	public static CsvWriter unGenerationUnitsPoolEnergySalesCOPWriter = new CsvWriter(Global.unGenerationUnitsPoolEnergySalesCOP);
	
	public static String nodGenerationUnitsPoolEnergySalesCOP  	= directoryResults + "nodGenerationUnitsPoolEnergySalesCOP.csv";	
	public static CsvWriter nodGenerationUnitsPoolEnergySalesCOPWriter = new CsvWriter(Global.nodGenerationUnitsPoolEnergySalesCOP);
	
	public static String ftrGenerationUnitsPoolEnergySalesCOP  	= directoryResults + "ftrGenerationUnitsPoolEnergySalesCOP.csv";	
	public static CsvWriter ftrGenerationUnitsPoolEnergySalesCOPWriter = new CsvWriter(Global.ftrGenerationUnitsPoolEnergySalesCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las reconciliaciones positivas para las unidades de generación
	public static String unGenerationUnitsPositiveReconciliationMWh  	= directoryResults + "unGenerationUnitsPositiveReconciliationMWh.csv";	
	public static CsvWriter unGenerationUnitsPositiveReconciliationMWhWriter = new CsvWriter(Global.unGenerationUnitsPositiveReconciliationMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las reconciliaciones positivas para las unidades de generación
	public static String unGenerationUnitsPositiveReconciliationCOP  	= directoryResults + "unGenerationUnitsPositiveReconciliationCOP.csv";	
	public static CsvWriter unGenerationUnitsPositiveReconciliationCOPWriter = new CsvWriter(Global.unGenerationUnitsPositiveReconciliationCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la magnitud de las reconciliaciones negativas para las unidades de generación
	public static String unGenerationUnitsNegativeReconciliationMWh  	= directoryResults + "unGenerationUnitsNegativeReconciliationMWh.csv";	
	public static CsvWriter unGenerationUnitsNegativeReconciliationMWhWriter = new CsvWriter(Global.unGenerationUnitsNegativeReconciliationMWh);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de las reconciliaciones negativas para las unidades de generación
	public static String unGenerationUnitsNegativeReconciliationCOP  	= directoryResults + "unGenerationUnitsNegativeReconciliationCOP.csv";	
	public static CsvWriter unGenerationUnitsNegativeReconciliationCOPWriter = new CsvWriter(Global.unGenerationUnitsNegativeReconciliationCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de los cargos por uso liquidados a las unidades de generación
	public static String unGenerationUnitsSettlementUsageChargesCOP  	= directoryResults + "unGenerationUnitsSettlementUsageChargesCOP.csv";	
	public static CsvWriter unGenerationUnitsSettlementUsageChargesCOPWriter = new CsvWriter(Global.unGenerationUnitsSettlementUsageChargesCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para la liquidación total para las unidades de generación
	public static String unGenerationUnitsSettlementEnergyMarketCOP  	= directoryResults + "unGenerationUnitsSettlementEnergyMarketCOP.csv";	
	public static CsvWriter unGenerationUnitsSettlementEnergyMarketCOPWriter = new CsvWriter(Global.unGenerationUnitsSettlementEnergyMarketCOP);
	
	public static String nodGenerationUnitsSettlementEnergyMarketCOP  	= directoryResults + "nodGenerationUnitsSettlementEnergyMarketCOP.csv";	
	public static CsvWriter nodGenerationUnitsSettlementEnergyMarketCOPWriter = new CsvWriter(Global.nodGenerationUnitsSettlementEnergyMarketCOP);
	
	public static String ftrGenerationUnitsSettlementEnergyMarketCOP  	= directoryResults + "ftrGenerationUnitsSettlementEnergyMarketCOP.csv";	
	public static CsvWriter ftrGenerationUnitsSettlementEnergyMarketCOPWriter = new CsvWriter(Global.ftrGenerationUnitsSettlementEnergyMarketCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------
	// archivo para el valor de los cargos por uso pagados a los transmisores
	public static String unTransmittersSettlementUsageChargesCOP  	= directoryResults + "unTransmittersSettlementUsageChargesCOP.csv";	
	public static CsvWriter unTransmittersSettlementUsageChargesCOPWriter = new CsvWriter(Global.unTransmittersSettlementUsageChargesCOP);
	
	public static String nodTransmittersSettlementUsageChargesCOP  	= directoryResults + "nodTransmittersSettlementUsageChargesCOP.csv";	
	public static CsvWriter nodTransmittersSettlementUsageChargesCOPWriter = new CsvWriter(Global.nodTransmittersSettlementUsageChargesCOP);
	
	public static String nodTransmittersSettlementComplementaryChargesCOP  	= directoryResults + "nodTransmittersSettlementComplementaryChargesCOP.csv";	
	public static CsvWriter nodTransmittersSettlementComplementaryChargesCOPWriter = new CsvWriter(Global.nodTransmittersSettlementComplementaryChargesCOP);
	
	public static String nodTransmittersSettlementCongestionRentsCOP  	= directoryResults + "nodTransmittersSettlementCongestionRentsCOP.csv";	
	public static CsvWriter nodTransmittersSettlementCongestionRentsCOPWriter = new CsvWriter(Global.nodTransmittersSettlementCongestionRentsCOP);
	
	public static String ftrTransmittersSettlementUsageChargesCOP  	= directoryResults + "ftrTransmittersSettlementUsageChargesCOP.csv";	
	public static CsvWriter ftrTransmittersSettlementUsageChargesCOPWriter = new CsvWriter(Global.ftrTransmittersSettlementUsageChargesCOP);
	
	public static String ftrTransmittersSettlementComplementaryChargesCOP  	= directoryResults + "ftrTransmittersSettlementComplementaryChargesCOP.csv";	
	public static CsvWriter ftrTransmittersSettlementComplementaryChargesCOPWriter = new CsvWriter(Global.ftrTransmittersSettlementComplementaryChargesCOP);
	
	public static String ftrTransmittersSettlementCongestionRentsCOP  	= directoryResults + "ftrTransmittersSettlementCongestionRentsCOP.csv";	
	public static CsvWriter ftrTransmittersSettlementCongestionRentsCOPWriter = new CsvWriter(Global.ftrTransmittersSettlementCongestionRentsCOP);
	// -------------------------------------------------------------------------------------------------------------------------------------------------
			
	public static String resultsS  	= directoryResults + "results.csv";			// archivo para los resultados del despacho real en formato consola
	public static CsvWriter resultsWriter = new CsvWriter(Global.resultsS);		// archivo para escribir los resultados [funciona como consola de java]
	
	public static Dimension dimPricesChart 	= new Dimension(890,300);
	public static Dimension dimFlowChart 	= new Dimension(500,671);
	public static Dimension dimGenTable 	= new Dimension(890,300);
	public static Dimension dimCongTable 	= new Dimension(890,300);
	
	//public static XYSeriesCollection dataset = new XYSeriesCollection();
	//public static XYSeriesCollection datasetScatter = new XYSeriesCollection();
	
	//public static JFreeChart stepChart = ChartFactory.createXYStepChart("Spot price","Time [hours]","Price [$/Mwh]",dataset,PlotOrientation.VERTICAL,true,true,false); 
	//public static JFreeChart lineChart = ChartFactory.createXYLineChart("Spot price","Time [hours]","Price [$/Mwh]",dataset,PlotOrientation.VERTICAL,true,true,false);
	//public static JFreeChart scatterChart = ChartFactory.createScatterPlot("Dispatch","Si/No","Generation [Mwh]",datasetScatter,PlotOrientation.VERTICAL,true,true,false);
	
	//public static GraphicsStepChart stepGraphic = new GraphicsStepChart("Prueba", dataset, stepChart, "step");  // generación de gráficos
	//public static Graphics stepGraphic = new Graphics("Prueba", dataset, lineChart, "line",dimNodalChart);  // generación de gráficos
	//public static GraphicsScatterPlot scatterGraphic = new GraphicsScatterPlot("Prueba", datasetScatter, scatterChart);  // generación de gráficos
		
	public static ImageIcon fondoIcon = new ImageIcon(directoryIcons + "map.png");
	public static Image fondo = fondoIcon.getImage();
	
	public static ImageIcon fondoIcon1 = new ImageIcon(directoryIcons + "map1.png");
	public static Image fondo1 = fondoIcon1.getImage();
	
	public static ImageIcon fondoIcon2 = new ImageIcon(directoryIcons + "map2.png");
	public static Image fondo2 = fondoIcon2.getImage();
	
	public static ImageIcon fondoIcon3 = new ImageIcon(directoryIcons + "map3.png");
	public static Image fondo3 = fondoIcon3.getImage();
		
	public static ImageIcon nodooIcon = new ImageIcon(directoryIcons + "node.png");
	public static Image imgNode = nodooIcon.getImage();
	
    // formatos
	public static DecimalFormat decimalFormatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH)); // formato: decimales
	public static DateFormat dateFormatter = DateFormat.getDateInstance();	// formato: fechas
	
	// fuentes
	public static Font labelFont = new Font("Arial", Font.BOLD, 10); // fuente labels 
	
	//
	// energy market
	//
	public static double contractPriceRange = 0.10;
	public static double powerBidPriceRange = 0.10;
	public static double[][] data = {
		{	98953.61,	78.00	}	,
		{	221412	,	202.00	}	,
		{	134329	,	60.00	}	,
		{	98953.61,	420.00	}	,
		{	98953.61,	800.00	}	,
		{	166568	,	78.00	}	,
		{	0.00	,	19.90	}	,
		{	184329	,	45.00	}	,
		{	212906	,	250.00	}	,
		{	98953.61,	2400.00	}	,
		{	143074	,	352.00	}	,
		{	222906	,	88.00	}	,
		{	222906	,	17.00	}		
 }; 
	public static int contractStartDate = 0;
	public static int contractFinalDate = 735;
	public static double contractPowerLowerLimit = 20.0;
	public static double referencePricePositiveReconciliation = 195470.00;	// precio de referencia para la reconciliación positiva de las plantas térmicas
	public static double proportionUsageChargesDemand = 1.0;	// proporción cargos por uso liquidados a la demanda
	public static double ftrOperatorMinimumPrice = 0.1; 		// precio mínimo ofertado por el operador para un FTR
	public static double proportionComplementaryCharge = 0.95; 	// proporción para el cálculo del cargo complementario de transmisión en le mercado nodal
	public static double proportionAuctionCapacity = 0.2; 		// proporción para el cálculo de la capacidad de transmisión a subastar
	public static int ftrDuration = 30; 						// duración estándar de los FTRs
	public static double proportionCongestionRentsTransmitters = 0.2; // proporción de las rentas para los transmisores
	
	public static double [][] cordXYFlows = 
		{  {0.285	,
			0.285	,
			0.330	,
			0.331	,
			0.332	,
			0.333	,
			0.334	,
			0.360	,
			0.361	,
			0.335	,
			0.336	,
			0.420	,
			0.421	,
			0.422	,
			0.450	,
			0.451	,
			0.335	,
			0.336	,
			0.337	,
			0.390	,
			0.125	,
			0.126	,
			0.127	,
			0.128	,
			0.355	,
			0.295	,
			0.465	,
			0.466	,
			0.467	,
			0.468	,
			0.515	,
			0.516	,
			0.300	,
			0.301	,
			0.290	,
			0.291	,
			0.240	,
			0.241	,
			0.242	,
			0.243	,
			0.244	,
			0.415	,
			0.416	,
			0.417	,
			0.495	,
			0.520	,
			0.235	,
			0.236	,
			0.237	,
			0.238	,
			0.310	,
			0.395	,
			0.396	,
			0.340	,
			0.341	,
			0.390	,
			0.391	,
			0.470	,
			0.471	,
			0.472	,
			0.473	,
			0.575	,
			0.576	,
			0.415	,
			0.416	,
			0.417	,
			0.418	,
			0.390	,
			0.391	,
			0.315	,
			0.316	,
			0.285	,
			0.365	,
			0.366	,
			0.367	,
			0.368	,
			0.185	,
			0.186	},
		 {		0.615	,
				0.615	,
				0.645	,
				0.646	,
				0.647	,
				0.648	,
				0.649	,
				0.670	,
				0.671	,
				0.900	,
				0.901	,
				0.620	,
				0.621	,
				0.622	,
				0.520	,
				0.521	,
				0.930	,
				0.931	,
				0.932	,
				0.905	,
				0.340	,
				0.341	,
				0.342	,
				0.343	,
				0.735	,
				0.725	,
				0.565	,
				0.566	,
				0.567	,
				0.568	,
				0.650	,
				0.651	,
				0.810	,
				0.811	,
				0.540	,
				0.541	,
				0.535	,
				0.536	,
				0.537	,
				0.538	,
				0.539	,
				0.945	,
				0.946	,
				0.947	,
				0.825	,
				0.920	,
				0.405	,
				0.406	,
				0.407	,
				0.408	,
				0.465	,
				0.590	,
				0.591	,
				0.565	,
				0.566	,
				0.655	,
				0.656	,
				0.705	,
				0.706	,
				0.707	,
				0.708	,
				0.755	,
				0.756	,
				0.545	,
				0.546	,
				0.547	,
				0.548	,
				0.595	,
				0.596	,
				0.600	,
				0.601	,
				0.565	,
				0.535	,
				0.536	,
				0.537	,
				0.538	,
				0.445	,
				0.446	}
		};
	
	
	public static double [][] cordXYnodes = 
		{{0.30,	0.36,	0.42,	0.31,	0.16,	0.29,	0.51,	0.31,	0.27,	0.47,	0.31,	0.37,	0.42,	0.48,	0.52,	0.41,	0.36,	0.31,	0.21,	0.63,	0.57,	0.09},
		 {0.66,	0.97,	0.56,	0.89,	0.39,	0.79,	0.57,	0.83,	0.57,	0.92,	0.42,	0.62,	0.68,	0.48,	0.73,	0.53,	0.63,	0.51,	0.50,	0.78,	0.92,	0.29}
		};
	
	/*public static double [][] cordXYNodesLines =
		{{0.30,	0.30,	0.30,	0.36,	0.42,	0.42,	0.31,	0.31,	0.16,	0.29,	0.29,	0.51,	0.51,	0.31,	0.27,	0.27,	0.47,	0.47,	0.47,	0.31,	0.31,	0.37,	0.37,	0.42,	0.42,	0.52,	0.41,	0.36,	0.36,	0.36,	0.31,	0.21},
		 {0.66,	0.66,	0.66,	0.97,	0.56,	0.56,	0.89,	0.89,	0.39,	0.79,	0.79,	0.57,	0.57,	0.83,	0.57,	0.57,	0.92,	0.92,	0.92,	0.42,	0.42,	0.62,	0.62,	0.68,	0.68,	0.73,	0.53,	0.63,	0.63,	0.63,	0.51,	0.50},
		 {0.27, 0.36, 	0.42,	0.31,   0.42,	0.48,	0.36,	0.47,	0.09,	0.42,	0.30,	0.42,	0.52,	0.29,	0.31,	0.21,	0.36,	0.52,	0.57,	0.16,	0.31,	0.42,	0.31,	0.36,	0.52,	0.63,	0.42,	0.42,	0.27,	0.21,	0.42,	0.16},
		 {0.57, 0.63,	0.68,	0.83,	0.68,	0.48,	0.97,	0.92,	0.29,	0.68,	0.66,	0.56,	0.73,	0.79,	0.51,	0.50,	0.97,	0.73,	0.92,	0.39,	0.51,	0.56,	0.51,	0.63,	0.73,	0.78,	0.56,	0.56,	0.57,	0.50,	0.56,	0.39}
		};*/
	
	public static double [][] cordXYNodesLines =
		{{	0.30, 0.30,	0.30, 0.30, 0.30, 0.30, 0.30, 0.30, 0.30, 0.36,
			0.36, 0.42, 0.42, 0.42, 0.42, 0.42, 0.31, 0.31, 0.31, 0.31,
			0.16, 0.16, 0.16, 0.16, 0.29, 0.29, 0.51, 0.51, 0.51, 0.51,
			0.51, 0.51, 0.31, 0.31, 0.27, 0.27, 0.27, 0.27, 0.27, 0.27,
			0.27, 0.47, 0.47, 0.47, 0.47, 0.47, 0.31, 0.31, 0.31, 0.31,
			0.31, 0.37, 0.37, 0.37, 0.37, 0.42, 0.42, 0.42, 0.42, 0.42,
			0.42, 0.52, 0.52, 0.41, 0.41, 0.41, 0.41, 0.36, 0.36, 0.36,
			0.36, 0.36, 0.31, 0.31, 0.31, 0.31, 0.21, 0.21  			},
		 {  0.66, 0.66, 0.66, 0.66, 0.66, 0.66, 0.66, 0.66, 0.66, 0.97,
			0.97, 0.56, 0.56, 0.56, 0.56, 0.56, 0.89, 0.89, 0.89, 0.89,
			0.39, 0.39, 0.39, 0.39, 0.79, 0.79, 0.57, 0.57, 0.57, 0.57,
			0.57, 0.57, 0.83, 0.83, 0.57, 0.57, 0.57, 0.57, 0.57, 0.57,
			0.57, 0.92, 0.92, 0.92, 0.92, 0.92, 0.42, 0.42, 0.42, 0.42,
			0.42, 0.62, 0.62, 0.62, 0.62, 0.68, 0.68, 0.68, 0.68, 0.68,
			0.68, 0.73, 0.73, 0.53, 0.53, 0.53, 0.53, 0.63, 0.63, 0.63,
			0.63, 0.63, 0.51, 0.51, 0.51, 0.51, 0.50, 0.50				},
		 {  0.27, 0.27, 0.36, 0.36, 0.36, 0.36, 0.36, 0.42, 0.42, 0.31, 
			0.31, 0.42, 0.42, 0.42, 0.48, 0.48, 0.36, 0.36, 0.36, 0.47, 
			0.09, 0.09, 0.09, 0.09, 0.42, 0.30, 0.42, 0.42, 0.42, 0.42, 
			0.52, 0.52, 0.29, 0.29, 0.31, 0.31, 0.21, 0.21, 0.21, 0.21, 
			0.21, 0.36, 0.36, 0.36, 0.52, 0.57, 0.16, 0.16, 0.16, 0.16, 
			0.31, 0.42, 0.42, 0.31, 0.31, 0.36, 0.36, 0.52, 0.52, 0.52,
			0.52, 0.63, 0.63, 0.42, 0.42, 0.42, 0.42, 0.42, 0.42, 0.27,
			0.27, 0.21, 0.42, 0.42, 0.42, 0.42, 0.10, 0.10	 			},
		 {	0.57, 0.57, 0.63, 0.63, 0.63, 0.63, 0.63, 0.68, 0.68, 0.83,
			0.83, 0.68, 0.69, 0.70, 0.48, 0.48, 0.97, 0.97, 0.97, 0.92,
			0.29, 0.29, 0.29, 0.29, 0.68, 0.66, 0.56, 0.56, 0.56, 0.56,
			0.73, 0.73, 0.79, 0.79, 0.51, 0.51, 0.50, 0.50, 0.50, 0.50,
			0.50, 0.97, 0.97, 0.97, 0.73, 0.92, 0.39, 0.39, 0.39, 0.39,
			0.51, 0.56, 0.56, 0.51, 0.51, 0.63, 0.63, 0.73, 0.73, 0.73,
			0.73, 0.78, 0.78, 0.56, 0.56, 0.56, 0.56, 0.56, 0.56, 0.57,
			0.57, 0.50, 0.56, 0.56, 0.56, 0.56, 0.39, 0.40				}
		};
	
	public static String[] columnNames = {"RECURSO", "DISPONIBILIDAD", "GEN IDEAL", "GEN REAL","DESVIACIÓN"};
	
	public static String[] gensAntioqui = 
		{
			"GUATAPE"		,	
			"GUATRON"		,	
			"JAGUAS"		,	
			"LATASAJERA"	,	
			"MAGUAFRE"		,
			"MANTIOQ1"		,	
			"MCALDERAS"		,	
			"MCARUQUIA"		,	
			"MCASCADA1"		,	
			"MGUANAQUITA"	,
			"MSANTARITA"	,	
			"MSANTIAGO"		,	
			"PLAYAS"		,	
			"PORCE2"		,	
			"PORCE3"		,
			"PORCE3P"		,	
			"RPIEDRAS"	
		};
	
	public static String[] gensAtlantic = 
		{
			"BARRANQ3"	,	
			"BARRANQ4"	,	
			"FLORES1"	,	
			"FLORES21"	,	
			"FLORES3"	,
			"FLORESIVB"	,	
			"TEBSA"
		};
	
	public static String[] gensBogota = 
		{
			"GUAVIO"		,	
			"MBOGOTA1"		,	
			"MCUNDINAMARCA"	,	
			"MSANTANA"		,	
			"ZIPAEMG2"		,
			"ZIPAEMG3"		,	
			"ZIPAEMG4"		,	
			"ZIPAEMG5"
		};
	
	public static String[] gensBolivar =
		{
			"CTGEMG1"	,	
			"CTGEMG2"	,
			"CTGEMG3"	,
			"PROELEC1"	,
			"PROELEC2"	,
			"TCANDEL1"	,
			"TCANDEL2"
		};
	
	public static String[] gensCaucanar =
		{
			"COINCAUCA"	,
			"FLORIDA2"	,
			"MCAUCAN1"	,
			"MCAUCAN2"	,
			"MRIOMAYO"
		};
	
	public static String[] gensCerromat =
		{
			"URRA"
		};
	
	public static String[] gensChivor =
		{
			"CHIVOR"
		};
	
	public static String[] gensCorozo =
		{
			"COROZO1"
		};
	
	public static String[] gensCQR =
		{
			"DORADA1"	,
			"ESMERALDA"	,
			"INSULA"	,
			"MBELMONTE"	,
			"MCQR1"		,
			"MELBOSQUE"	,
			"MNLIBARE"	,
			"SANFRANCISCO"
		};
		
	public static String[] gensCuatricentenario =
		{
			"VENEZUE1"	
		};
	
	public static String[] gensEcuador =
		{
			"ECUADOR11"	,
			"ECUADOR12"	,
			"ECUADOR13"	,
			"ECUADOR14"	,
			"ECUADOR21"	,
			"ECUADOR22"	,
			"ECUADOR23"	,
			"ECUADOR24"	
		};
		
	public static String[] gensGCM =
		{
			"GUAJIR11"	,
			"GUAJIR21"	,
			"MJEPIRAC"	
		};
	
	public static String[] gensHuilacaq =
		{
			"BETANIA"	,
			"MHUILAQ1"	
		};
	
	public static String[] gensLamiel =
		{
			"MIEL1"
		};
	
	public static String[] gensMagdamed =
		{
			"TCENTRO1"	,
			"TSIERRA"
		};
		
	public static String[] gensNordeste =
		{
			"MCIMARR1"	,
			"MERILEC1"	,
			"MMORRO1"	,
			"MMORRO2"	,
			"MNORDE1"	,
			"MYOPAL1"	,
			"PAIPA1"	,
			"PAIPA2"	,
			"PAIPA3"	,
			"PAIPA4"	,
			"PALENQ3"	,
			"TASAJER1"	,
			"TYOPAL2"
		};
		
	public static String[] gensPagua =
		{
			"PAGUA"	
		};
	
	public static String[] gensSancarlo =
		{		
			"SANCARLOS"
		};
	
	public static String[] gensTolima =
		{		
			"MCURRUCU"	,
			"MPRADO4"	,
			"MTOLIMA1"	,
			"PRADO"		,
			"TPIEDRAS"
		};
	
	public static String[] gensVallecau =
		{		
			"ALBAN"			,
			"CALIMA1"		,
			"CSANCARLOS"	,
			"CVALLEC1"		,
			"MEMCALI"		,
			"MTULUA"		,
			"MVALLEC1"		,
			"M_AMAIME"		,
			"M_PROVIDEN"	,
			"SALVAJINA"		,
			"TEMCALI"		,
			"TVALLE"	
		};


	//public static MathFunctions MathFun = new MathFunctions(); // variables to use several math process
	//public static Graphs graphs = new Graphs(); 
	//public static ChartPanel panel = null; // = new ChartPanel(chart);
	//public static JFrame frame = new JFrame("Graphs");
	//public static Font labelFont = new Font("Arial", Font.BOLD, 16);
	//public static Font tickFont = new Font("Arial", Font.BOLD, 12);
	//public static Font titleFont = new Font("Arial", Font.BOLD, 18);
	
	
	//
	// Nombres de los generadores a ser usados para crear los generadores 
	// que participan en el mercado de energía
	//
	/*public static String[] generatorsCods = {
		"EPSG",	"GECG",	"ENDG",	"CHVG",	"DLRG",	"ESSG",	"CVAG",	"CHCG",	"ECUG",	"FRSG",
		"GNCG",	"EPMG",	"ISGG",	"ADCG",	"EEPG",	"LCSG",	"CDNG",	"CTMG",	"EECG",	"FACG",
		"ERCG",	"EGCG",	"CIVG",	"HLAG",	"GEEG",	"CETG",	"TYPG",	"HIMG",	"PRLG",	"TRMG",
		"TCDG", "TEMG", "TRPG", "EMUG"};*/
	
	public static String[] generatorsCods = {
		"EPSG",	"GECG",	"ENDG",	"CHVG",	"DLRG",	"ESSG",	"CVAG",	"CHCG",	"ECUG",	"FRSG",
		"GNCG",	"EPMG",	"ISGG",	"ADCG",	"EEPG",	"LCSG",	"CDNG",	"CTMG",	"EECG",	"FACG",
		"ERCG",	"EGCG",	"CIVG",	"HLAG",	"GEEG",	"CETG",	"TYPG",	"HIMG",	"PRLG",	"TRMG",
		"TCDG", "TEMG", "TRPG", "EMUG",	
		"POLG",
		"HIDG",
		"HMVG",
		"ENAG",
		"ITUG",
		"TENG",
		"PANG",
		"PUTG"
		};
		
	

	public static int nGencos = generatorsCods.length; 	// número de compañías de generación
	
	/*public static String[] generatorsNames = {
	"EMPRESA DE ENERGÍA DEL PACÍFICO S.A. E.S.P.",						"GENERADORA Y COMERCIALIZADORA DE ENERGÍA DEL CARIBE S.A. ESP",		
	"EMGESA S.A E.S.P",													"AES CHIVOR & CÍA S.C.A. E.S.P.",									
	"DICELER S.A. E.S.P.",												"ELECTRIFICADORA DE SANTANDER S.A",									
	"CVALLEC1",															"CENTRAL HIDROELÉCTRICA DE CALDAS S.A. E.S.P.",						
	"ECUADOR",															"TERMOFLORES S.A. E.S.P",											
	"COMPAÑÍA DE GENERACIÓN DEL CAUCA S.A. E.S.P.",						"EMPRESAS PÚBLICAS DE MEDELLÍN E.S.P.",								
	"ISAGEN S.A. E.S.P.",												"AGUAS DE LA CABAÑA S.A. E.S.P.",									
	"EMPRESA DE ENERGÍA DE PEREIRA S.A E.S.P.",							"PRESTADORA DE SERVICIOS PÚBLICOS LA CASCADA S.A. E.S.P.",			
	"CENTRALES ELÉCTRICAS DE NARIÑO S.A. E.S.P.",						"CENTRAL TERMOELECTRICA EL MORRO 2 S.A.S. E.S.P.",					
	"EMPRESA DE ENERGÍA DE CUNDINAMARCA S.A. E.S.P.",					"ENERMONT S.A . E.S.P.",											
	"ENERGÍA RENOVABLE DE COLOMBIA S.A E.S.P.",							"EMPRESA DE GENERACIÓN DE CALI S.A. E.S.P.",						
	"CELSIA S.A E.S.P.",												"ELECTRIFICADORA DEL HUILA S.A. E.S.P.",							
	"GENERAMOS ENERGÍA S.A. E.S.P.",									"COMPAÑÍA DE ELECTRICIDAD DE TULUÁ S.A. E.S.P.",					
	"TERMOYOPAL GENERACION 2 S.A.S E.S.P.",								"GESTIÓN ENERGÉTICA S.A. E.S.P.",									
	"PROELECTRICA & CIA S.C.A. E.S.P.",									"TERMOTASAJERO S.A E.S.P.",											
	"TERMOCANDELARIA S.C.A. E.S.P.",									"TERMOEMCALI I S.A E.S.P.",											
	"TERMOPIEDRAS S.A. E.S.P.",											"EMPRESA URRÁ S.A. E.S.P."
	};*/
	
	public static String[] generatorsNames = {
		"EMPRESA DE ENERGÍA DEL PACÍFICO S.A. E.S.P.",						"GENERADORA Y COMERCIALIZADORA DE ENERGÍA DEL CARIBE S.A. ESP",		
		"EMGESA S.A E.S.P",													"AES CHIVOR & CÍA S.C.A. E.S.P.",									
		"DICELER S.A. E.S.P.",												"ELECTRIFICADORA DE SANTANDER S.A",									
		"CVALLEC1",															"CENTRAL HIDROELÉCTRICA DE CALDAS S.A. E.S.P.",						
		"ECUADOR",															"TERMOFLORES S.A. E.S.P",											
		"COMPAÑÍA DE GENERACIÓN DEL CAUCA S.A. E.S.P.",						"EMPRESAS PÚBLICAS DE MEDELLÍN E.S.P.",								
		"ISAGEN S.A. E.S.P.",												"AGUAS DE LA CABAÑA S.A. E.S.P.",									
		"EMPRESA DE ENERGÍA DE PEREIRA S.A E.S.P.",							"PRESTADORA DE SERVICIOS PÚBLICOS LA CASCADA S.A. E.S.P.",			
		"CENTRALES ELÉCTRICAS DE NARIÑO S.A. E.S.P.",						"CENTRAL TERMOELECTRICA EL MORRO 2 S.A.S. E.S.P.",					
		"EMPRESA DE ENERGÍA DE CUNDINAMARCA S.A. E.S.P.",					"ENERMONT S.A . E.S.P.",											
		"ENERGÍA RENOVABLE DE COLOMBIA S.A E.S.P.",							"EMPRESA DE GENERACIÓN DE CALI S.A. E.S.P.",						
		"CELSIA S.A E.S.P.",												"ELECTRIFICADORA DEL HUILA S.A. E.S.P.",							
		"GENERAMOS ENERGÍA S.A. E.S.P.",									"COMPAÑÍA DE ELECTRICIDAD DE TULUÁ S.A. E.S.P.",					
		"TERMOYOPAL GENERACION 2 S.A.S E.S.P.",								"GESTIÓN ENERGÉTICA S.A. E.S.P.",									
		"PROELECTRICA & CIA S.C.A. E.S.P.",									"TERMOTASAJERO S.A E.S.P.",											
		"TERMOCANDELARIA S.C.A. E.S.P.",									"TERMOEMCALI I S.A E.S.P.",											
		"TERMOPIEDRAS S.A. E.S.P.",											"EMPRESA URRÁ S.A. E.S.P.",
		"GRUPO POLIOBRAS", 
		"HIDRALPOR",
		"HMV INGENIEROS LTDA",
		"ENERGIA DE LOS ANDES",
		"HIDROELÉCTRICA PESCADERO ITUANGO S.A. E.S.P.",
		"TERMONORTE",	
		"PANAMA",	
		"ENERPUTUMAYO"
		};

	//
	// Nombres de las unidades de generación a ser usados para crear las 
	// unidades de generación que participan en el despacho
	//
	/*public static String[] generationUnitsNames = {
		"ALBAN",		"BARRANQ3",		"BARRANQ4",		"BETANIA",		"CALIMA1",		"CHIVOR",		"COINCAUCA",		"COROZO1",		"CSANCARLOS",		"CTGEMG1",		
		"CTGEMG2",		"CTGEMG3",		"CVALLEC1",		"DORADA1",		"ECUADOR11",	"ECUADOR12",	"ECUADOR13",		"ECUADOR14",	"ECUADOR21",		"ECUADOR22",
		"ECUADOR23",	"ECUADOR24",	"ESMERALDA",	"FLORES1",		"FLORES21",		"FLORES3",		"FLORESIVB",		"FLORIDA2",		"GUAJIR11",			"GUAJIR21",
		"GUATAPE",		"GUATRON",		"GUAVIO",		"INSULA",		"JAGUAS",		"LATASAJERA",	"MAGUAFRE",			"MANTIOQ1",		"MBELMONTE",		"MBOGOTA1",
		"MCALDERAS",	"MCARUQUIA",	"MCASCADA1",	"MCAUCAN1",		"MCAUCAN2",		"MCIMARR1",		"MCQR1",			"MCUNDINAMARCA","MCURRUCU",			"MELBOSQUE",
		"MEMCALI",		"MERILEC1",		"MGUANAQUITA",	"MHUILAQ1",		"MIEL1",		"MJEPIRAC",		"MMORRO1",			"MMORRO2",		"MNLIBARE",			"MNORDE1",
		"MPRADO4",		"MRIOMAYO",		"MSANTANA",		"MSANTARITA",	"MSANTIAGO",	"MTOLIMA1",		"MTULUA",			"MVALLEC1",		"MYOPAL1",			"M_AMAIME",
		"M_PROVIDEN",	"PAGUA",		"PAIPA1",		"PAIPA2",		"PAIPA3",		"PAIPA4",		"PALENQ3",			"PLAYAS",		"PORCE2",			"PORCE3",
		"PORCE3P",		"PRADO",		"PROELEC1",		"PROELEC2",		"RPIEDRAS",		"SALVAJINA",	"SANCARLOS",		"SANFRANCISCO",	"TASAJER1",			"TCANDEL1",
		"TCANDEL2",		"TCENTRO1",		"TEBSA",		"TEMCALI",		"TPIEDRAS",		"TSIERRA",		"TVALLE",			"TYOPAL2",		"URRA",				"VENEZUE1",
		"ZIPAEMG2",		"ZIPAEMG3",		"ZIPAEMG4",		"ZIPAEMG5"};*/	
	
	public static String[] generationUnitsNames = {
		"ALBAN"	,
		"BARRANQ3"	,
		"BARRANQ4"	,
		"BETANIA"	,
		"CALIMA1"	,
		"CHIVOR"	,
		"COINCAUCA"	,
		"COROZO1"	,
		"CSANCARLOS"	,
		"CTGEMG1"	,
		"CTGEMG2"	,
		"CTGEMG3"	,
		"CVALLEC1"	,
		"DORADA1"	,
		"ECUADOR11"	,
		"ECUADOR12"	,
		"ECUADOR13"	,
		"ECUADOR14"	,
		"ECUADOR21"	,
		"ECUADOR22"	,
		"ECUADOR23"	,
		"ECUADOR24"	,
		"ESMERALDA"	,
		"FLORESIV"	,
		"FLORES1"	,
		"FLORIDA2"	,
		"GUAJIR11"	,
		"GUAJIR21"	,
		"GUATAPE"	,
		"GUATRON"	,
		"GUAVIO"	,
		"INSULA"	,
		"JAGUAS"	,
		"LATASAJERA"	,
		"M_AMAIME"	,
		"M_PROVIDEN"	,
		"MAGUAFRE"	,
		"MALTOTULUA1"	,
		"MANTIOQ1"	,
		"MBARROSO1"	,
		"MBELMONTE"	,
		"MBOGOTA1"	,
		"MCALDERAS"	,
		"MCARUQUIA"	,
		"MCASCADA1"	,
		"MCAUCAN1"	,
		"MCAUCAN2"	,
		"MCIMARR1"	,
		"MCQR1"	,
		"MCUNDINAMARCA"	,
		"MCURRUCU"	,
		"ELBOSQUE"	,
		"MEMCALI"	,
		"MERILEC1"	,
		"MGUANAQUITA"	,
		"MHUILAQ1"	,
		"MIEL1"	,
		"MJEPIRAC"	,
		"MMONTAÑITAS"	,
		"MMORRO1"	,
		"MMORRO2"	,
		"MNLIBARE"	,
		"MNORDE1"	,
		"MPRADO4"	,
		"MRIOMAYO"	,
		"MSANFRANCISC"	,
		"MSANTANA"	,
		"MSANTARITA"	,
		"MSANTIAGO"	,
		"MTOLIMA1"	,
		"MTULUA"	,
		"MVALLEC1"	,
		"MYOPAL1"	,
		"PAGUA"	,
		"PAIPA1"	,
		"PAIPA2"	,
		"PAIPA3"	,
		"PAIPA4"	,
		"PLAYAS"	,
		"PORCE2"	,
		"PORCE3"	,
		"PRADO"	,
		"PROELEC1"	,
		"PROELEC2"	,
		"RPIEDRAS"	,
		"SALVAJINA"	,
		"SANCARLOS"	,
		"SANFRANCISCO"	,
		"TASAJER1"	,
		"TCANDEL1"	,
		"TCANDEL2"	,
		"TCENTRO1"	,
		"TEBSA"	,
		"TEMCALI"	,
		"TPIEDRAS"	,
		"TSIERRA"	,
		"TVALLE"	,
		"TYOPAL2"	,
		"URRA"	,
		"VENEZUE1"	,
		"ZIPAEMG2"	,
		"ZIPAEMG3"	,
		"ZIPAEMG4"	,
		"ZIPAEMG5"	,
		"AMOYA"	,
		"TERMOCOL"	,
		"CUCUANA"	,
		"ELQUIMBO"	,
		"SOGAMOSO"	,
		"CARLOSLLERAS"	,
		"POPAL"	,
		"RIOAMBEIMA"	,
		"GECELCA32"	,
		"ITUANGO"	,
		"PORVENIRII"	,
		"TERMONORTE"	,
		"PANAMA"		
		};		
	
	public static int nUnits = generationUnitsNames.length; 	// número de unidades de generación
	
	/*public static String[] generationUnits = {
	"unitALBAN"		,	"unitBARRANQ3"	,	"unitBARRANQ4"	,	"unitBETANIA"	,	"unitCALIMA1"	,	"unitCHIVOR"	,	"unitCOINCAUCA"	,	"unitCOROZO1"		,
	"unitCSANCARLOS",	"unitCTGEMG1"	,	"unitCTGEMG2"	,	"unitCTGEMG3"	,	"unitCVALLEC1"	,	"unitDORADA1"	,	"unitECUADOR11"	,	"unitECUADOR12"		,
	"unitECUADOR13"	,	"unitECUADOR14"	,	"unitECUADOR21"	,	"unitECUADOR22"	,	"unitECUADOR23"	,	"unitECUADOR24"	,	"unitESMERALDA"	,	"unitFLORES1"		,
	"unitFLORES21"	,	"unitFLORES3"	,	"unitFLORESIVB"	,	"unitFLORIDA2"	,	"unitGUAJIR11"	,	"unitGUAJIR21"	,	"unitGUATAPE"	,	"unitGUATRON"		,
	"unitGUAVIO"	,	"unitINSULA"	,	"unitJAGUAS"	,	"unitLATASAJERA",	"unitMAGUAFRE"	,	"unitMANTIOQ1"	,	"unitMBELMONTE"	,	"unitMBOGOTA1"		,
	"unitMCALDERAS"	,	"unitMCARUQUIA"	,	"unitMCASCADA1"	,	"unitMCAUCAN1"	,	"unitMCAUCAN2"	,	"unitMCIMARR1"	,	"unitMCQR1"		,	"unitMCUNDINAMARCA"	,
	"unitMCURRUCU"	,	"unitMELBOSQUE"	,	"unitMEMCALI"	,	"unitMERILEC1"	,	"unitMGUANAQUITA",	"unitMHUILAQ1"	,	"unitMIEL1"		,	"unitMJEPIRAC"		,
	"unitMMORRO1"	,	"unitMMORRO2"	,	"unitMNLIBARE"	,	"unitMNORDE1"	,	"unitMPRADO4"	,	"unitMRIOMAYO"	,	"unitMSANTANA"	,	"unitMSANTARITA"	,
	"unitMSANTIAGO"	,	"unitMTOLIMA1"	,	"unitMTULUA"	,	"unitMVALLEC1"	,	"unitMYOPAL1"	,	"unitM_AMAIME"	,	"unitM_PROVIDEN",	"unitPAGUA"			,
	"unitPAIPA1"	,	"unitPAIPA2"	,	"unitPAIPA3"	,	"unitPAIPA4"	,	"unitPALENQ3"	,	"unitPLAYAS"	,	"unitPORCE2"	,	"unitPORCE3"		,
	"unitPORCE3P"	,	"unitPRADO"		,	"unitPROELEC1"	,	"unitPROELEC2"	,	"unitRPIEDRAS"	,	"unitSALVAJINA"	,	"unitSANCARLOS"	,	"unitSANFRANCISCO"	,
	"unitTASAJER1"	,	"unitTCANDEL1"	,	"unitTCANDEL2"	,	"unitTCENTRO1"	,	"unitTEBSA"		,	"unitTEMCALI"	,	"unitTPIEDRAS"	,	"unitTSIERRA"		,
	"unitTVALLE"	,	"unitTYOPAL2"	,	"unitURRA"		,	"unitVENEZUE1"	,	"unitZIPAEMG2"	,	"unitZIPAEMG3"	,	"unitZIPAEMG4"	,	"unitZIPAEMG5"
	};*/
	
	public static String[] generationUnits = {
	"unitALBAN"	,
	"unitBARRANQ3"	,
	"unitBARRANQ4"	,
	"unitBETANIA"	,
	"unitCALIMA1"	,
	"unitCHIVOR"	,
	"unitCOINCAUCA"	,
	"unitCOROZO1"	,
	"unitCSANCARLOS"	,
	"unitCTGEMG1"	,
	"unitCTGEMG2"	,
	"unitCTGEMG3"	,
	"unitCVALLEC1"	,
	"unitDORADA1"	,
	"unitECUADOR11"	,
	"unitECUADOR12"	,
	"unitECUADOR13"	,
	"unitECUADOR14"	,
	"unitECUADOR21"	,
	"unitECUADOR22"	,
	"unitECUADOR23"	,
	"unitECUADOR24"	,
	"unitESMERALDA"	,
	"unitFLORESIV"	,
	"unitFLORES1"	,
	"unitFLORIDA2"	,
	"unitGUAJIR11"	,
	"unitGUAJIR21"	,
	"unitGUATAPE"	,
	"unitGUATRON"	,
	"unitGUAVIO"	,
	"unitINSULA"	,
	"unitJAGUAS"	,
	"unitLATASAJERA"	,
	"unitM_AMAIME"	,
	"unitM_PROVIDEN"	,
	"unitMAGUAFRE"	,
	"unitMALTOTULUA1"	,
	"unitMANTIOQ1"	,
	"unitMBARROSO1"	,
	"unitMBELMONTE"	,
	"unitMBOGOTA1"	,
	"unitMCALDERAS"	,
	"unitMCARUQUIA"	,
	"unitMCASCADA1"	,
	"unitMCAUCAN1"	,
	"unitMCAUCAN2"	,
	"unitMCIMARR1"	,
	"unitMCQR1"	,
	"unitMCUNDINAMARCA"	,
	"unitMCURRUCU"	,
	"unitMELBOSQUE"	,
	"unitMEMCALI"	,
	"unitMERILEC1"	,
	"unitMGUANAQUITA"	,
	"unitMHUILAQ1"	,
	"unitMIEL1"	,
	"unitMJEPIRAC"	,
	"unitMMONTAÑITAS	"	,
	"unitMMORRO1"	,
	"unitMMORRO2"	,
	"unitMNLIBARE"	,
	"unitMNORDE1"	,
	"unitMPRADO4"	,
	"unitMRIOMAYO"	,
	"unitMSANFRANCISC"	,
	"unitMSANTANA"	,
	"unitMSANTARITA"	,
	"unitMSANTIAGO"	,
	"unitMTOLIMA1"	,
	"unitMTULUA"	,
	"unitMVALLEC1"	,
	"unitMYOPAL1"	,
	"unitPAGUA"	,
	"unitPAIPA1"	,
	"unitPAIPA2"	,
	"unitPAIPA3"	,
	"unitPAIPA4"	,
	"unitPLAYAS"	,
	"unitPORCE2"	,
	"unitPORCE3"	,
	"unitPRADO"	,
	"unitPROELEC1"	,
	"unitPROELEC2"	,
	"unitRPIEDRAS"	,
	"unitSALVAJINA"	,
	"unitSANCARLOS"	,
	"unitSANFRANCISCO"	,
	"unitTASAJER1"	,
	"unitTCANDEL1"	,
	"unitTCANDEL2"	,
	"unitTCENTRO1"	,
	"unitTEBSA"	,
	"unitTEMCALI"	,
	"unitTPIEDRAS"	,
	"unitTSIERRA"	,
	"unitTVALLE"	,
	"unitTYOPAL2"	,
	"unitURRA"	,
	"unitVENEZUE1"	,
	"unitZIPAEMG2"	,
	"unitZIPAEMG3"	,
	"unitZIPAEMG4"	,
	"unitZIPAEMG5"	,
	"unitAMOYA"	,
	"unitTERMOCOL"	,
	"unitCUCUANA"	,
	"unitELQUIMBO"	,
	"unitSOGAMOSO"	,
	"unitCARLOSLLERAS"	,
	"unitPOPAL"	,
	"unitRIOAMBEIMA"	,
	"unitGECELCA32"	,
	"unitITUANGO"	,
	"unitPORVENIRII"	,
	"unitTERMONORTE"	,
	"unitPANAMA"
	};

	//
	// código de los comercializadores
	//
	/*public static String[] retailersCods = {
		"EPMC"	,	"CCOC"	,	"ENDC"	,	"CCOC"	,	"EMIC"	,	"GECC"	,	"ISGC"	,	"GECC"	,	"ISGC"	,	"GECC"	,
		"HLAC"	,	"ISGC"	,	"EPMC"	,	"EMSC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ESSC"	,
		"ISGC"	,	"ECUC"	
		};*/
	
	public static String[] retailersCods = {
		"EPMC"	,	"CCOC"	,	"ENDC"	,	"CCOC"	,	"EMIC"	,	"GECC"	,	"ISGC"	,	"GECC"	,	"ISGC"	,	"GECC"	,
		"HLAC"	,	"ISGC"	,	"EPMC"	,	"EMSC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ESSC"	,
		"ISGC"	,	"ECUC"	,	"PANC"	
		};

	//
	// nombres de los comercializadores
	//
	/*public static String[] retailersNames = {
		"EMPRESAS PUBLICAS DE MEDELLIN E.S.P."	,
		"ENERGIA EMPRESARIAL DE LA COSTA S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ENERGIA EMPRESARIAL DE LA COSTA S.A. E.S.P."	,
		"EMPRESAS MUNICIPALES DE CALI E.I.C.E. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ELECTRIFICADORA DEL HUILA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMPRESAS PUBLICAS DE MEDELLIN E.S.P."	,
		"ELECTRIFICADORA DEL META S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"ELECTRIFICADORA DE SANTANDER S.A."	,
		"ISAGEN S.A. E.S.P."	,
		"ECUADOR"
		};*/
	
	public static String[] retailersNames= {
		"EMPRESAS PUBLICAS DE MEDELLIN E.S.P."	,
		"ENERGIA EMPRESARIAL DE LA COSTA S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ENERGIA EMPRESARIAL DE LA COSTA S.A. E.S.P."	,
		"EMPRESAS MUNICIPALES DE CALI E.I.C.E. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"GENERADORA Y COMERCIALIZADORA DE ENERGIA DEL CARIBE S.A. E.S.P."	,
		"ELECTRIFICADORA DEL HUILA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMPRESAS PUBLICAS DE MEDELLIN E.S.P."	,
		"ELECTRIFICADORA DEL META S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"EMGESA S.A. E.S.P."	,
		"ISAGEN S.A. E.S.P."	,
		"ELECTRIFICADORA DE SANTANDER S.A."	,
		"ISAGEN S.A. E.S.P."	,
		"ECUADOR",
		"PANAMA"
		};
	
	public static int nRetailers = retailersNames.length; 	// número de unidades de generación
	
	/*public static String[] retailers = {
	"retANTIOQUI"	, 	"retATLANTIC"	, 	"retBOGOTA"		, 	"retBOLIVAR"	,	"retCAUCANAR"	,	"retCERROMAT"	,	"retCHIVOR"		,	"retCORDOSUC"	,	"retCQR"		,	"retGCM"	,
	"retHUILACAQ"	,	"retLAMIEL"		,	"retMAGDAMED"	,	"retMETA"		,	"retNORDESTE"	,	"retPAGUA"		,	"retSANCARLO"	,	"retTOLIMA"		,	"retVALLECAU"	,	"retCOROZO"	,
	"retCUATRICENTENARIO"	,	"retECUADOR220"
	};*/
	
	public static String[] retailers = {
		"retANTIOQUI"	, 	"retATLANTIC"	, 	"retBOGOTA"		, 	"retBOLIVAR"	,	"retCAUCANAR"	,	"retCERROMAT"	,	"retCHIVOR"		,	"retCORDOSUC"	,	"retCQR"		,	"retGCM"	,
		"retHUILACAQ"	,	"retLAMIEL"		,	"retMAGDAMED"	,	"retMETA"		,	"retNORDESTE"	,	"retPAGUA"		,	"retSANCARLO"	,	"retTOLIMA"		,	"retVALLECAU"	,	"retCOROZO"	,
		"retCUATRICENTENARIO"	,	"retECUADOR220", 	"retPANAMA"
		};
	
	//
	// Nombres de cada uno de los nodos de demanda nacional
	//
	public static String[] nalnodesNames = {
			"ANTIOQUI",	"ATLANTIC",	"BOGOTA",	"BOLIVAR",	"CAUCANAR", "CERROMAT",	"CHIVOR",	"CORDOSUC",	"CQR",	   "GCM",
			"HUILACAQ",	"LAMIEL",	"MAGDAMED", "META",		"NORDESTE", "PAGUA",	"SANCARLO",	"TOLIMA",   "VALLECAU"};
	public static int nnalNodes = nalnodesNames.length; // número de nodos nacionales (fronteras comerciales)
	
	//
	// Nombres de cada uno de los nodos de demanda internacional.
	//
	//public static String[] intnodesNames = { "COROZO", "CUATRICENTENARIO", "ECUADOR220", "PANAMA" };
	public static String[] intnodesNames = { "COROZO", "CUATRICENTENARIO", "ECUADOR220", "PANAMA" };
	public static int nintNodes = intnodesNames.length; // número de nodos internacionales(fronteras comerciales)
	
	//
	// Nombres de cada uno de los nodos del sistema, los cuales representan las áreas operativas del mismo.
	//
	/*public static String[] nodesNames = {
		"ANTIOQUI",	"ATLANTIC",	"BOGOTA",	"BOLIVAR",	"CAUCANAR", "CERROMAT",	"CHIVOR",	"CORDOSUC",	"CQR",	   "GCM",
		"HUILACAQ",	"LAMIEL",	"MAGDAMED", "META",		"NORDESTE", "PAGUA",	"SANCARLO",	"TOLIMA",   "VALLECAU","COROZO", 
		"CUATRICENTENARIO", "ECUADOR220" };*/
	
	public static String[] nodesNames = {
		"ANTIOQUI",	"ATLANTIC",	"BOGOTA",	"BOLIVAR",	"CAUCANAR", "CERROMAT",	"CHIVOR",	"CORDOSUC",	"CQR",	   "GCM",
		"HUILACAQ",	"LAMIEL",	"MAGDAMED", "META",		"NORDESTE", "PAGUA",	"SANCARLO",	"TOLIMA",   "VALLECAU","COROZO", 
		"CUATRICENTENARIO", "ECUADOR220", "PANAMA" };
	public static int nNodes = nodesNames.length; // número total de nodos del sistema (fronteras comerciales)
		
	//
	// Nombre de las líneas de transmisión
	//	
	/*public static String[] linesNames = {
		"ANTIOQUI-CQR",			"ANTIOQUI-SANCARLO",	"ANTIOQUI-MAGDAMED",	"ATLANTIC-CORDOSUC",		"BOGOTA-MAGDAMED",
		"BOGOTA-META",			"BOLIVAR-ATLANTIC",		"BOLIVAR-GCM",			"CAUCANAR-ECUADOR",			"CERROMAT-MAGDAMED",
		"CERROMAT-ANTIOQUI",	"CHIVOR-BOGOTA",		"CHIVOR-NORDESTE",		"CORDOSUC-CERROMAT",		"CQR-TOLIMA",
		"CQR-VALLECAU",			"GCM-ATLANTIC",			"GCM-NORDESTE",			"GCM-CUATRICENTENARIO",		"HUILACAQ-CAUCANAR",
		"HUILACAQ-TOLIMA",		"LAMIEL-BOGOTA",		"LAMIEL-TOLIMA",		"MAGDAMED-SANCARLO",		"MAGDAMED-NORDESTE",
		"NORDESTE-COROZO",		"PAGUA-BOGOTA",			"SANCARLO-BOGOTA",		"SANCARLO-CQR",				"SANCARLO-VALLECAU",
		"TOLIMA-BOGOTA",		"VALLECAU-CAUCANAR"};
		*/
		
	/*public static String[] linesNames = {	
		"ANCON SUR (ISA) - ESMERALDA (ISA) 1 230 kV"	,	"ANCON SUR (ISA) - ESMERALDA 2 230 kV"		,	"ANCON SUR ISA - SAN CARLOS 1 230 kV"	,
		"ANCON SUR ISA - SAN CARLOS 2 230 kV"			,	"GUATAPE - SAN CARLOS 1 230 kV"				,	"GUATAPE - SAN CARLOS 2 230 kV"	,
		"PORCE III - SAN CARLOS 1 500 kV"				,	"PLAYAS - PRIMAVERA 1 230 kV"				,	"MALENA - PRIMAVERA 1 230 kV"	,
		"SABANALARGA - CHINU 1 500 kV"					,	"SABANALARGA - CHINU 2 500 kV"				,	"BACATA - PRIMAVERA 1 500 kV"	,
		"LA SIERRA (ANTIOQUIA) - PURNIO 1 230 kV"		,	"LA SIERRA (ANTIOQUIA) - PURNIO 2 230 kV"	,	"LA REFORMA - TUNAL 1 230 kV"	,
		"GUAVIO - LA REFORMA 1 230 kV"					,	"BOLIVAR (CARTAGENA) - SABANALARGA 1 220 kV",	"BOLIVAR (CARTAGENA) - SABANALARGA 2 220 kV"	,
		"SABANALARGA - TERNERA 2 220 kV"				,	"BOLIVAR (CARTAGENA) - EL COPEY 1 500 kV"	,	"JAMONDINO - POMASQUI (ECUADOR) 1 230 kV"	,
		"JAMONDINO - POMASQUI (ECUADOR) 2 230 kV"		,	"JAMONDINO - POMASQUI (ECUADOR) 3 230 kV"	,	"JAMONDINO - POMASQUI (ECUADOR) 4 230 kV"	,
		"CERROMATOSO - PRIMAVERA 1 500 kV"				,	"CERROMATOSO - PORCE III 1 500 kV"			,	"CHIVOR - TORCA 1 230 kV"	,
		"CHIVOR - TORCA 2 230 Kv"						,	"GUAVIO - CHIVOR 1 230 kV"					,	"GUAVIO - CHIVOR 2 230 kV"	,
		"CHIVOR - SOCHAGOTA 1 230 kV"					,	"CHIVOR - SOCHAGOTA 2 230 kV"				,	"CHINU - CERROMATOSO 1 500 kV"	,
		"CHINU - CERROMATOSO 2 500 kV"					,	"SAN FELIPE - ESMERALDA 1 230 kV"			,	"LA ENEA - SAN FELIPE 1 230 kV"	,
		"ESMERALDA - YUMBO 2 230 kV"					,	"ESMERALDA - YUMBO 3 230 kV"				,	"ESMERALDA - LA VIRGINIA 1 230 kV"	,
		"ESMERALDA - LA VIRGINIA 2 230 kV"				,	"LA VIRGINIA - LA HERMOSA 1 230 kV"			,	"FUNDACION - SABANALARGA 1 220 kV"	,
		"FUNDACION - SABANALARGA 2 220 kV"				,	"FUNDACION - SABANALARGA 3 220 kV"			,	"EL COPEY - OCANA 1 500 kV"	,
		"CUESTECITAS - CUATRICENTENARIO 1 230 kV"		,	"BETANIA - SAN BERNARDINO 1 230 kV"			,	"BETANIA - SAN BERNARDINO 2 230 kV"	,
		"BETANIA - JAMONDINO 1 230 kV"					,	"ALTAMIRA - MOCOA (JUNIN) 1 230 kV"			,	"BETANIA - MIROLINDO 1 230 kV"	,
		"MIEL I - PURNIO 1 230 kV"						,	"MIEL I - PURNIO 2 230 kV"					,	"MIEL I - SAN FELIPE 1 230 kV"	,
		"MIEL I - SAN FELIPE 2 230 kV"					,	"PRIMAVERA - SAN CARLOS 1 500 kV"			,	"LA SIERRA - SAN CARLOS 1 230 Kv"	,
		"OCANA - PRIMAVERA 1 500 kV"					,	"PRIMAVERA - COMUNEROS 1 230 kV"			,	"PRIMAVERA - COMUNEROS 2 230 kV"	,
		"GUATIGUARA - PRIMAVERA 1 230 kV"				,	"SAN MATEO (CUCUTA) - COROZO 1 230 kV"		,	"SAN MATEO (CUCUTA) - COROZO 2 230 kV"	,
		"LA GUACA - LA MESA 1 230 kV"					,	"LA GUACA - LA MESA 2 230 kV"				,	"PARAISO - SAN MATEO EEB 1 230 kV"	,
		"CIRCO - PARAISO 1 230 kV"						,	"SAN CARLOS - PURNIO 1 230 kV"				,	"SAN CARLOS - PURNIO 2 230 kV"	,
		"SAN CARLOS - ESMERALDA 1 230 kV"				,	"SAN CARLOS - ESMERALDA 2 230 kV"			,	"SAN CARLOS - LA VIRGINIA 1 500 kV"	,
		"MIROLINDO - LA MESA 1 230 kV"					,	"MIROLINDO - LA MESA 2 230 kV"				,	"SAN FELIPE - LA MESA 1 230 kV"	,
		"SAN FELIPE - LA 	MESA 2 230 kV"				,	"JUANCHITO (220 KV) - PAEZ 1 230 kV"		,	"YUMBO - SAN	BERNARDINO 1 230 kV"
	};*/
	
	public static String[] linesNames = {	
		"ANCON SUR (ISA) - ESMERALDA (ISA) 1 230 kV"	,	"ANCON SUR (ISA) - ESMERALDA 2 230 kV"		,	"ANCON SUR ISA - SAN CARLOS 1 230 kV"	,
		"ANCON SUR ISA - SAN CARLOS 2 230 kV"			,	"GUATAPE - SAN CARLOS 1 230 kV"				,	"GUATAPE - SAN CARLOS 2 230 kV"	,
		"PORCE III - SAN CARLOS 1 500 kV"				,	"PLAYAS - PRIMAVERA 1 230 kV"				,	"MALENA - PRIMAVERA 1 230 kV"	,
		"SABANALARGA - CHINU 1 500 kV"					,	"SABANALARGA - CHINU 2 500 kV"				,	"BACATA - PRIMAVERA 1 500 kV"	,
		"LA SIERRA (ANTIOQUIA) - PURNIO 1 230 kV"		,	"LA SIERRA (ANTIOQUIA) - PURNIO 2 230 kV"	,	"LA REFORMA - TUNAL 1 230 kV"	,
		"GUAVIO - LA REFORMA 1 230 kV"					,	"BOLIVAR (CARTAGENA) - SABANALARGA 1 220 kV",	"BOLIVAR (CARTAGENA) - SABANALARGA 2 220 kV"	,
		"SABANALARGA - TERNERA 2 220 kV"				,	"BOLIVAR (CARTAGENA) - EL COPEY 1 500 kV"	,	"JAMONDINO - POMASQUI (ECUADOR) 1 230 kV"	,
		"JAMONDINO - POMASQUI (ECUADOR) 2 230 kV"		,	"JAMONDINO - POMASQUI (ECUADOR) 3 230 kV"	,	"JAMONDINO - POMASQUI (ECUADOR) 4 230 kV"	,
		"CERROMATOSO - PRIMAVERA 1 500 kV"				,	"CERROMATOSO - PORCE III 1 500 kV"			,	"CHIVOR - TORCA 1 230 kV"	,
		"CHIVOR - TORCA 2 230 Kv"						,	"GUAVIO - CHIVOR 1 230 kV"					,	"GUAVIO - CHIVOR 2 230 kV"	,
		"CHIVOR - SOCHAGOTA 1 230 kV"					,	"CHIVOR - SOCHAGOTA 2 230 kV"				,	"CHINU - CERROMATOSO 1 500 kV"	,
		"CHINU - CERROMATOSO 2 500 kV"					,	"SAN FELIPE - ESMERALDA 1 230 kV"			,	"LA ENEA - SAN FELIPE 1 230 kV"	,
		"ESMERALDA - YUMBO 2 230 kV"					,	"ESMERALDA - YUMBO 3 230 kV"				,	"ESMERALDA - LA VIRGINIA 1 230 kV"	,
		"ESMERALDA - LA VIRGINIA 2 230 kV"				,	"LA VIRGINIA - LA HERMOSA 1 230 kV"			,	"FUNDACION - SABANALARGA 1 220 kV"	,
		"FUNDACION - SABANALARGA 2 220 kV"				,	"FUNDACION - SABANALARGA 3 220 kV"			,	"EL COPEY - OCANA 1 500 kV"	,
		"CUESTECITAS - CUATRICENTENARIO 1 230 kV"		,	"BETANIA - SAN BERNARDINO 1 230 kV"			,	"BETANIA - SAN BERNARDINO 2 230 kV"	,
		"QUIMBO - JAMONDINO 1 230 kV"					,	"ALTAMIRA - MOCOA (JUNIN) 1 230 kV"			,	"BETANIA - MIROLINDO 1 230 kV"	,
		"MIEL I - PURNIO 1 230 kV"						,	"MIEL I - PURNIO 2 230 kV"					,	"MIEL I - SAN FELIPE 1 230 kV"	,
		"MIEL I - SAN FELIPE 2 230 kV"					,	"PRIMAVERA - SAN CARLOS 1 500 kV"			,	"LA SIERRA - SAN CARLOS 1 230 Kv"	,
		"SOGAMOSO - PRIMAVERA 1 500 kV"					,	"PRIMAVERA - COMUNEROS 1 230 kV"			,	"PRIMAVERA - COMUNEROS 2 230 kV"	,
		"GUATIGUARA - PRIMAVERA 1 230 kV"				,	"SAN MATEO (CUCUTA) - COROZO 1 230 kV"		,	"SAN MATEO (CUCUTA) - COROZO 2 230 kV"	,
		"LA GUACA - LA MESA 1 230 kV"					,	"LA GUACA - LA MESA 2 230 kV"				,	"PARAISO - SAN MATEO EEB 1 230 kV"	,
		"CIRCO - PARAISO 1 230 kV"						,	"SAN CARLOS - PURNIO 1 230 kV"				,	"SAN CARLOS - PURNIO 2 230 kV"	,
		"SAN CARLOS - ESMERALDA 1 230 kV"				,	"SAN CARLOS - ESMERALDA 2 230 kV"			,	"SAN CARLOS - LA VIRGINIA 1 500 kV"	,
		"MIROLINDO - LA MESA 1 230 kV"					,	"MIROLINDO - LA MESA 2 230 kV"				,	"SAN FELIPE - LA MESA 1 230 kV"	,
		"SAN FELIPE - LA 	MESA 2 230 kV"				,	"JUANCHITO (220 KV) - PAEZ 1 230 kV"		,	"YUMBO - SAN	BERNARDINO 1 230 kV",
		"ITUANGO - PRIMAVERA 500 kV"	,
		"ITUANGO - CERROMATOSO 500 kV"	,
		"CERROMATOSO - COPEY 500 kV"	,
		"PRIMAVERA - NORTE 500 kV"	,
		"OCCIDENTE - SAN MARCOS 500 kV"	,
		"LA VIRGINIA - NUEVA ESPERANZA 500 kV"	,
		"ALFEREZ - JAMONDINO 500 kV"	,
		"JAMONDINO - ECUADOR 500 kV"	,
		"COLOMBIA - PANAMA HVDC"	,
		"GUAVIO - SURIA 230 kV"	,
		"SURIA -TUNAL 230 kV"	,
		"MONTERIA-URABA 220 kV"	,
		"QUIMBO - ALFEREZ 1 230 kV"	,
		"QUIMBO - ALFEREZ 2 230 kV"	,
		"CHIVOR II - NORTE 1 230 kV"	,
		"CHIVOR II - NORTE 2 230 kV"	,

	};


	public static int nLines = linesNames.length;	// número de nodos (fronteras coemrciales)
	
	/*
	public static String[] linesCod = {
		"L1",	"L2",	"L3",	"L4",	"L5",	"L6",	"L7",	"L8",	"L9",	"L10",
		"L11", 	"L12",	"L13",	"L14",	"L15",	"L16",	"L17",	"L18",	"L19",	"L20",
		"L21",	"L22",	"L23",	"L24",	"L25",	"L26",	"L27",	"L28",	"L29",	"L30",
		"L31",	"L32"	
		};*/
	
	public static String[] linesCod = {
	"L1	"	,	"L2"	,	"L3"	,	"L4"	,	"L5"	,	"L6"	,	"L7"	,	"L8"	,	"L9"	,	"L10"	,
	"L11"	,	"L12"	,	"L13"	,	"L14"	,	"L15"	,	"L16"	,	"L17"	,	"L18"	,	"L19"	,	"L20"	,
	"L21"	,	"L22"	,	"L23"	,	"L24"	,	"L25"	,	"L26"	,	"L27"	,	"L28"	,	"L29"	,	"L30"	,
	"L31"	,	"L32"	,	"L33"	,	"L34"	,	"L35"	,	"L36"	,	"L37"	,	"L38"	,	"L39"	,	"L40"	,
	"L41"	,	"L42"	,	"L43"	,	"L44"	,	"L45"	,	"L46"	,	"L47"	,	"L48"	,	"L49"	,	"L50"	,
	"L51"	,	"L52"	,	"L53"	,	"L54"	,	"L55"	,	"L56"	,	"L57"	,	"L58"	,	"L59"	,	"L60"	,
	"L61"	,	"L62"	,	"L63"	,	"L64"	,	"L65"	,	"L66"	,	"L67"	,	"L68"	,	"L69"	,	"L70"	,
	"L71"	,	"L72"	,	"L73"	,	"L74"	,	"L75"	,	"L76"	,	"L77"	,	"L78",
	"L79"	,
	"L80"	,
	"L81"	,
	"L82"	,
	"L83"	,
	"L84"	,
	"L85"	,
	"L86"	,
	"L87"	,
	"L88"	,
	"L89"	,
	"L90"	,
	"L91"	,
	"L92"	,
	"L93"	,
	"L94"	,

	};
	
	//
	// Nombre de los transmisores
	//
	public static String[] gridcosNames = {"CENS", "EEB", "ISA", "TRANSELCA"};
	public static int nGridcos = gridcosNames.length;	// número de compañías de transmisión
		
/*
	//
	// Función para organizar las ofertas de los generadores apra el despacho horario de electricidad.
	// El despacho considera las ofertas definitivas reportadas por XM, y no se tienen en cuenta los
	// precios de arranque y parada. 
	//
	public static void organizerPowerBids(){
		//
		// Carga de los datos con las ofertas
		//
		// Vector dinámico para almacenar las ofertas diarias de los generadores
		Vector<PowerBid> supply = new Vector<PowerBid>();
		
		// String para guardar el nombre del archivo con los datos de las ofertas
		String supplyCsv = "";
				
		// Archivo para escribir las ofertas
		CsvWriter supplyWriter = new CsvWriter(supplyS);
		
		try{
			// Ciclo para recorrer los años
			for(int year = 2010; year <=2011; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonth[month]; day++){
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader supplyReader = new CsvReader(supplyCsv);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las ofertas de los generadores
						supply = Global.rw.readCsvSupplyVector(supplyReader, generationUnitsNames);
						
						// Se escriben las ofertas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerBids(supplyWriter, supply);
					}
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerPowerBids ->"+e);
	    }
		// Cerrar el archivo creado
		supplyWriter.close();
	}
	
	
	//
	// Función para organizar las demandas de energía. Las demandas son horarias para cada uno de los nodos
	// del sistema. Dichos nodos corresponden a las áreas operativas. 
	//
	public static void organizerNationalDemands(){
		//
		// Carga de los datos las demandas
		//
		// Vector dinámico para almacenar las demandas horarias por nodo
		Vector<PowerDemand> demand = new Vector<PowerDemand>();
		
		// String para guardar el nombre del archivo con los datos de las demandas
		String demandCsv = "";
		
		// Archivo para escribir las demandas
		CsvWriter demandWriter = new CsvWriter(demandColS);
				
		try{
			// Ciclo para recorrer los años
			for(int year = 2010; year <=2011; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonth[month]; day++){
					
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){	
								demandCsv 	= directoryData + "demand/"+year+"/dDEM0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								demandCsv 	= directoryData + "demand/"+year+"/dDEM0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								demandCsv 	= directoryData + "demand/"+year+"/dDEM"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								demandCsv 	= directoryData + "demand/"+year+"/dDEM"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader demandReader = new CsvReader(demandCsv);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las demandas
						demand = Global.rw.readCsvDemandVector(demandReader, nalnodesNames);
						
						// Se escriben las demandas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerDemands(demandWriter, demand);
					}	
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerNationalDemands ->"+e);
	    }
		// Cerrar el archivo creado
		demandWriter.close();
	}
	
	//
	// Función para organizar las demandas internacionales de energía. Las demandas son horarias 
	// para cada uno de los nodos del sistema. Dichos nodos corresponden a las áreas operativas. 
	//
	public static void organizerInternationalDemands(){
		
		//
		// Carga de los datos las demandas internacionales
		//
		// Vector dinámico para almacenar las demandas internacionales
		Vector<PowerDemand> intdemand = new Vector<PowerDemand>();
		
		// String para guardar el nombre del archivo con los datos de las demandas
		String intdemandCsv = "";
		
		// Archivo para escribir las demandas
		CsvWriter intdemandWriter = new CsvWriter(demandIntS);
				
		try{
			// Ciclo para recorrer los años
			for(int year = 2010; year <=2011; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonth[month]; day++){
					
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){	
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader intdemandReader = new CsvReader(intdemandCsv);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las demandas
						intdemand = Global.rw.readCsvDemandVector(intdemandReader, intnodesNames);
						
						// Se escriben las demandas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerDemands(intdemandWriter, intdemand);
					}	
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerInternationalDemands ->"+e);
	    }
		// Cerrar el archivo creado
		intdemandWriter.close();
	}
	
	//
	// Función para organizar las demandas de energía. A partir de los archivos de demandas
	// nacionales e internacionales se crea un archivo que contenga ambo, en el cual al conjunto 
	// de demandas nacioanles se les agrega las internacionales como columnas a la derecha. 
	//
	public static void organizerDemands(){
		
		//
		// Organización del archivo de demandas
		//
		// Archivo para escribir las demandas
		CsvWriter demandWriter = new CsvWriter(demandS);
				
		try{
			// Archivos para leer las demandas nacionales e internacionales
			CsvReader naldemandReader  = new CsvReader(demandColS);
			CsvReader intdemandReader  = new CsvReader(demandIntS);
			
			int row = 0;
			while (naldemandReader.readRecord() && intdemandReader.readRecord())
			{
				// escritura de los datos de demanda nacional
				for(int col = 0; col < naldemandReader.getColumnCount(); col++)
				{
					demandWriter.write(naldemandReader.get(col));
				}
				
				// escritura de los datos de demanda internacional
				for(int col = 0; col < intdemandReader.getColumnCount(); col++)
				{
					demandWriter.write(intdemandReader.get(col));
				}
				
				demandWriter.endRecord(); // cambio de línea
			}
			// Cerrar los archivos
			naldemandReader.close();
			intdemandReader.close();
		}
		catch(Exception e)
	    {
	        System.out.println("organizerDemands ->"+e);
	    }
		// Cerrar el archivo
		demandWriter.close();
	}
	
	
	*/
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void organizerPowerBids2012(){
		//
		// Carga de los datos con las ofertas
		//
		// Vector dinámico para almacenar las ofertas diarias de los generadores
		Vector<PowerBid> supply = new Vector<PowerBid>();
		
		// String para guardar el nombre del archivo con los datos de las ofertas
		String supplyCsv = "";
				
		// Archivo para escribir las ofertas
		CsvWriter supplyWriter = new CsvWriter(supplyS2012);
		
		// Arreglo con los nombres de todas las unidades 
		//Vector<String> names = new Vector<String>();
		
		try{
			// Ciclo para recorrer los años
			for(int year = 2012; year < 2013; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonthLeapYear[month]; day++){
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								supplyCsv 	= directoryData + "supply/"+year+"/dOFE"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader supplyReader = new CsvReader(supplyCsv);
						
						// determinar los diferentes nombres de las unidades de generación						
						//Global.rw.readCsvGenNames(supplyReader, names, year, month, day);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las ofertas de los generadores
						supply = Global.rw.readCsvSupplyVector2012(supplyReader, generationUnitsNames, data);
						
						// Se escriben las ofertas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerBids(supplyWriter, supply);
					}
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerPowerBids2012 ->"+e);
	    }
		// Cerrar el archivo creado
		supplyWriter.close();
	}
	
	
	//
	// Función para organizar las demandas de energía. Las demandas son horarias para cada uno de los nodos
	// del sistema. Dichos nodos corresponden a las áreas operativas. 
	//
	public static void organizerNationalDemands2012(){
		//
		// Carga de los datos las demandas
		//
		// Vector dinámico para almacenar las demandas horarias por nodo
		Vector<PowerDemand> demand = new Vector<PowerDemand>();
		
		// String para guardar el nombre del archivo con los datos de las demandas
		String demandCsv = "";
		
		// Archivo para escribir las demandas
		CsvWriter demandWriter = new CsvWriter(demandColS2012);
				
		try{
			// Ciclo para recorrer los años
			for(int year = 2012; year < 2013; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonthLeapYear[month]; day++){
					
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){	
								demandCsv 	= directoryData + "demand/"+year+"/dDEM0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								demandCsv 	= directoryData + "demand/"+year+"/dDEM0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								demandCsv 	= directoryData + "demand/"+year+"/dDEM"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								demandCsv 	= directoryData + "demand/"+year+"/dDEM"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader demandReader = new CsvReader(demandCsv);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las demandas
						demand = Global.rw.readCsvDemandVector(demandReader, nalnodesNames);
						
						// Se escriben las demandas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerDemands(demandWriter, demand);
					}	
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerNationalDemands2012 ->"+e);
	    }
		// Cerrar el archivo creado
		demandWriter.close();
	}
	
	//
	// Función para organizar las demandas internacionales de energía. Las demandas son horarias 
	// para cada uno de los nodos del sistema. Dichos nodos corresponden a las áreas operativas. 
	//
	public static void organizerInternationalDemands2012(){
		
		//
		// Carga de los datos las demandas internacionales
		//
		// Vector dinámico para almacenar las demandas internacionales
		Vector<PowerDemand> intdemand = new Vector<PowerDemand>();
		
		// String para guardar el nombre del archivo con los datos de las demandas
		String intdemandCsv = "";
		
		// Archivo para escribir las demandas
		CsvWriter intdemandWriter = new CsvWriter(demandIntS2012);
				
		try{
			// Ciclo para recorrer los años
			for(int year = 2012; year < 2013; year++){
			
				// Ciclo para recorrer los archivos por mes
				for (int month = 0; month < 12; month++){
					
					// Ciclo para recorrer los archivos por día
					for(int day = 0; day < lengthMonthLeapYear[month]; day++){
					
						//
						// Control para hacer coincidir el nombre de los archivos con la ruta dada en el String supplyCsv
						//
						if(month+1 < 10){
							if(day+1 < 10){	
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN0"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN0"+(month+1)+(day+1)+".txt";
							}
						}
						else {
							if(day+1 < 10){
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN"+(month+1)+"0"+(day+1)+".txt";	
							}
							else
							{
								intdemandCsv 	= directoryData + "demand/"+year+"/dDIN"+(month+1)+(day+1)+".txt";
							}
						}
						// Con la ruta dada se crea un CsvReader para leer el archivo
						CsvReader intdemandReader = new CsvReader(intdemandCsv);
						
						// Control del progreso de la lectura
						System.out.println("año\t"+year+"\tmes\t"+(month+1)+"\tday\t"+(day+1));
						
						// En el vector dinámico se almacenan las demandas
						intdemand = Global.rw.readCsvDemandVector2020(intdemandReader, intnodesNames);
						
						// Se escriben las demandas en un archivo .csv. Cada día en una unica fila. 
						Global.rw.writeCsvPowerDemands(intdemandWriter, intdemand);
					}	
				}
			}
		}
		catch(Exception e)
	    {
	        System.out.println("organizerInternationalDemands2012 ->"+e);
	    }
		// Cerrar el archivo creado
		intdemandWriter.close();
	}
	
	//
	// Función para organizar las demandas de energía. A partir de los archivos de demandas
	// nacionales e internacionales se crea un archivo que contenga ambo, en el cual al conjunto 
	// de demandas nacioanles se les agrega las internacionales como columnas a la derecha. 
	//
	public static void organizerDemands2012(){
		
		//
		// Organización del archivo de demandas
		//
		// Archivo para escribir las demandas
		CsvWriter demandWriter = new CsvWriter(demandS2012);
				
		try{
			// Archivos para leer las demandas nacionales e internacionales
			CsvReader naldemandReader  = new CsvReader(demandColS2012);
			CsvReader intdemandReader  = new CsvReader(demandIntS2012);
			
			int row = 0;
			while (naldemandReader.readRecord() && intdemandReader.readRecord())
			{
				// escritura de los datos de demanda nacional
				for(int col = 0; col < naldemandReader.getColumnCount(); col++)
				{
					demandWriter.write(naldemandReader.get(col));
				}
				
				// escritura de los datos de demanda internacional
				for(int col = 0; col < intdemandReader.getColumnCount(); col++)
				{
					demandWriter.write(intdemandReader.get(col));
				}
				
				demandWriter.endRecord(); // cambio de línea
			}
			// Cerrar los archivos
			naldemandReader.close();
			intdemandReader.close();
		}
		catch(Exception e)
	    {
	        System.out.println("organizerDemands2012 ->"+e);
	    }
		// Cerrar el archivo
		demandWriter.close();
	}
	
	
	
	//
	// Menú principal para la jecución del modelo
	//
	public static void mainMenu() throws IOException
	{
		Scanner input = new Scanner( System.in );
		int option; // opción de simulación
		System.out.println("---------------------------------------------------------------");
		System.out.println("			Ejecución menú principal						   ");
		System.out.println("---------------------------------------------------------------");
		System.out.println(" Opciones: ");
		System.out.println("	1.- Organizar los datos de ofertas.");
		System.out.println("	2.- Organizar los datos de demandas nacionales.");
		System.out.println("	3.- Organizar los datos de demandas internacionales.");
		System.out.println("	4.- Organizar los datos de demandas.");
		System.out.println("	5.- Ejecutar modelo FTRs.");
		System.out.println("	6.- Generar gráficoss.");
		System.out.println("	7.- Interfaz gráfica.");
		System.out.println("	8.- Salir.");
		option = input.nextInt();
		do
		{
			switch(option)
			{
				case 1:
				{
					//Global.organizerPowerBids(); // organiza el archivo de ofertas por central de generación para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 2:
				{
					//Global.organizerNationalDemands(); // organiza el archivo de demandas nacionales para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 3:
				{
					//Global.organizerInternationalDemands(); // organiza el archivo de demandas internacionales para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 4:
				{
					//Global.organizerDemands(); // organiza un archivo de demandas por nodo para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 5:
				{    
					//FtrMain um = new FtrMain(); // ejecución del modelo FTRs
					//um.start();
					secondMenu(); // llamada al manú interno
					break;
				}
				case 6:
				{    
					//Graphics stateChart = new Graphics("Prueba"); // generación de gráficos
					//stateChart.setVisible(true);
					secondMenu(); // llamada al manú interno
					break;
				} 
				case 7:
				{    
					Interfaz interfaz = new Interfaz("Prueba"); // interfaz gráfica
					secondMenu(); // llamada al manú interno
					break;
				} 
				case 8:
				{
					System.exit(0);
					break;
				}
				default :
					System.out.println("Opción incorrecta");
					secondMenu();
					break;			    	
			}
				
		} while (option != 8);
		input.close();
	}
	
	public static void secondMenu() throws IOException
	{
		Scanner input = new Scanner( System.in );
		int option; // opción de simulación
		System.out.println("---------------------------------------------------------------");
		System.out.println("			Ejecución menú interno							   ");
		System.out.println("---------------------------------------------------------------");
		System.out.println(" Opciones: ");
		System.out.println("	1.- Volver a menu principal.");
		System.out.println("	2.- Salir.");
		option = input.nextInt();
		do
		{
			switch(option)
			{
				case 1:
				{
					mainMenu(); // llamada al manú interno
					break;
				}  			           
				case 2:
				{
					System.exit(0); // salir
					break;
				}
				default : 
					System.out.println("Opción incorrecta");
					secondMenu();
					break;			    	
			}
				
		} while (option != 2);	
		
		input.close();
	}
}
