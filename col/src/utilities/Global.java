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
	public static int nContracts 	= 4;  	// número de contratos de generación
	public static int horizon		= 1; 	// número de períodos de simulación
	public static int nlags			= 30; 	// número de rezagos para promediación del precio [VERIFICAR]
	public static int[] lengthMonth = {31,28,31,30,31,30,31,31,30,31,30,31}; // longitud de cada uno de los meses
	public static int[] year		= {2010, 2011}; // años considerados
	public static ReadWrite rw		= new  ReadWrite();	// función para leer y escribir
	public static ArrayFactory factory = new ArrayFactory(); // función para modificar los arrays
	public static ChargeInformation charge = new ChargeInformation(); // funciones para cargar información
	
	
	//
	// directorios para accesoa la información
	//
	public static String directory 			= "F:/FinalModels/Models/colModel/"; //"D:/Cristian/FinalModels/Models/ftrModel/";  // "F:/FinalModels/Models/ftrModel/";
	public static String directoryResults 	= directory +  "results/"; // "D:/Cristian/FinalModels/Models/ftrModel/results/";  // "F:/FinalModels/Models/ftrModel/results/";
	public static String directoryData 		= directory +  "data/"; // "D:/Cristian/FinalModels/Models/ftrModel/data/";  // "F:/FinalModels/Models/ftrModel/data/";
	public static String directoryIcons 	= directory +  "icons/"; // "D:/Cristian/FinalModels/Models/ftrModel/icons/";  // "F:/FinalModels/Models/ftrModel/icons/";
	
	//
	// rutas de acceso a los archivos de datos
	//
	public static String supplyS 	= directoryData + "supply.csv";			// archivo de ofertas
	public static String demandS 	= directoryData + "demand.csv";			// archivo de demandas
	public static String demandColS = directoryData + "demandCol.csv";		// archivo de demandas nacionales
	public static String demandIntS = directoryData + "demandInt.csv";		// archivo de demandas internacionales
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
	
	// archivo para los resultados de la liquidación del mercado de energía de las unidades de generación
	public static String unSettlementUnits  	= directoryResults + "unSettlementUnits.csv";	
	public static CsvWriter unSettlementUnitsWriter = new CsvWriter(Global.unSettlementUnits);
	
	// archivo para los resultados de la liquidación del mercado de energía de las unidades de generación
	public static String unSettlementTransmitters  	= directoryResults + "unSettlementTransmitters.csv";	
	public static CsvWriter unSettlementTransmittersWriter = new CsvWriter(Global.unSettlementTransmitters);
		
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
	public static int contractStartDate = 0;
	public static int contractFinalDate = 735;
	public static double contractPowerLowerLimit = 0.0;
	public static double referencePricePositiveReconciliation = 195470.00;	// precio de referencia para la reconciliación positiva de las plantas térmicas
	public static double proportionUsageChargesDemand = 1.0;					// proporción cargos por uso liquidados a la demanda
	
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
	
	public static String[] columnNames = {"RECURSO", "DISPONIBILIDAD", "GEN IDEAL", "GEN REAL","RECONCILIACIÓN"};
	
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
	public static String[] generatorsCods = {
		"EPSG",	"GECG",	"ENDG",	"CHVG",	"DLRG",	"ESSG",	"CVAG",	"CHCG",	"ECUG",	"FRSG",
		"GNCG",	"EPMG",	"ISGG",	"ADCG",	"EEPG",	"LCSG",	"CDNG",	"CTMG",	"EECG",	"FACG",
		"ERCG",	"EGCG",	"CIVG",	"HLAG",	"GEEG",	"CETG",	"TYPG",	"HIMG",	"PRLG",	"TRMG",
		"TCDG", "TEMG", "TRPG", "EMUG"};
	
	public static int nGencos = generatorsCods.length; 	// número de compañías de generación
	
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
	"TERMOPIEDRAS S.A. E.S.P.",											"EMPRESA URRÁ S.A. E.S.P."
	};

	//
	// Nombres de las unidades de generación a ser usados para crear las 
	// unidades de generación que participan en el despacho
	//
	public static String[] generationUnitsNames = {
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
		"ZIPAEMG2",		"ZIPAEMG3",		"ZIPAEMG4",		"ZIPAEMG5"};		
	
	public static int nUnits = generationUnitsNames.length; 	// número de unidades de generación
	
	public static String[] generationUnits = {
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
	};

	//
	// código de los comercializadores
	//
	public static String[] retailersCods = {
		"EPMC"	,	"CCOC"	,	"ENDC"	,	"CCOC"	,	"EMIC"	,	"GECC"	,	"ISGC"	,	"GECC"	,	"ISGC"	,	"GECC"	,
		"HLAC"	,	"ISGC"	,	"EPMC"	,	"EMSC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ENDC"	,	"ISGC"	,	"ESSC"	,
		"ISGC"	,	"ECUC"	
		};

	//
	// nombres de los comercializadores
	//
	public static String[] retailersNames = {
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
		};
	
	public static int nRetailers = retailersNames.length; 	// número de unidades de generación
	
	public static String[] retailers = {
	"retANTIOQUI"	, 	"retATLANTIC"	, 	"retBOGOTA"		, 	"retBOLIVAR"	,	"retCAUCANAR"	,	"retCERROMAT"	,	"retCHIVOR"		,	"retCORDOSUC"	,	"retCQR"		,	"retGCM"	,
	"retHUILACAQ"	,	"retLAMIEL"		,	"retMAGDAMED"	,	"retMETA"		,	"retNORDESTE"	,	"retPAGUA"		,	"retSANCARLO"	,	"retTOLIMA"		,	"retVALLECAU"	,	"retCOROZO"	,
	"retCUATRICENTENARIO"	,	"retECUADOR220"
	};
	
	//
	// Nombres de cada uno de los nodos de demanda nacional
	//
	public static String[] nalnodesNames = {
			"ANTIOQUI",	"ATLANTIC",	"BOGOTA",	"BOLIVAR",	"CAUCANAR", "CERROMAT",	"CHIVOR",	"CORDOSUC",	"CQR",	   "GCM",
			"HUILACAQ",	"LAMIEL",	"MAGDAMED", "META",		"NORDESTE", "PAGUA",	"SANCARLO",	"TOLIMA",   "VALLECAU" };
	public static int nnalNodes = nalnodesNames.length; // número de nodos nacionales (fronteras comerciales)
	
	//
	// Nombres de cada uno de los nodos de demanda internacional.
	//
	public static String[] intnodesNames = { "COROZO", "CUATRICENTENARIO", "ECUADOR220" };
	public static int nintNodes = intnodesNames.length; // número de nodos internacionales(fronteras comerciales)
	
	//
	// Nombres de cada uno de los nodos del sistema, los cuales representan las áreas operativas del mismo.
	//
	public static String[] nodesNames = {
		"ANTIOQUI",	"ATLANTIC",	"BOGOTA",	"BOLIVAR",	"CAUCANAR", "CERROMAT",	"CHIVOR",	"CORDOSUC",	"CQR",	   "GCM",
		"HUILACAQ",	"LAMIEL",	"MAGDAMED", "META",		"NORDESTE", "PAGUA",	"SANCARLO",	"TOLIMA",   "VALLECAU","COROZO", 
		"CUATRICENTENARIO", "ECUADOR220" };
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
	"L71"	,	"L72"	,	"L73"	,	"L74"	,	"L75"	,	"L76"	,	"L77"	,	"L78"		
	};
	
	//
	// Nombre de los transmisores
	//
	public static String[] gridcosNames = {"CENS", "EEB", "ISA", "TRANSELCA"};
	public static int nGridcos = gridcosNames.length;	// número de compañías de transmisión
		

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
					Global.organizerPowerBids(); // organiza el archivo de ofertas por central de generación para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 2:
				{
					Global.organizerNationalDemands(); // organiza el archivo de demandas nacionales para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 3:
				{
					Global.organizerInternationalDemands(); // organiza el archivo de demandas internacionales para todo el horizonte de tiempo considerado
					secondMenu(); // llamada al manú interno
					break;
				}
				case 4:
				{
					Global.organizerDemands(); // organiza un archivo de demandas por nodo para todo el horizonte de tiempo considerado
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
