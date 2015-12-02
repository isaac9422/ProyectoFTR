package market;

//import agents.Operator;
//import jxl.write.WritableSheet;

import java.io.IOException;

import org.jfree.data.xy.XYSeries;
import org.jfree.date.DateUtilities;

import utilities.Global;

import com.csvreader.CsvWriter;

import agents.Operator;
import ilog.concert.*;
import ilog.cplex.*;

public class DailyIdealDispatchN {
	
	//
	// Función para obtener la solución del problema de optimización del despacho
	// Parámetros: array de variables, array de restricciones, strings para la primera, segunda y tercera variable 
	// de optimización (generación, ángulos de voltaje y demanda no atendida respectivamente), número de iteración,
	// número de unidades de generación, número de nodos, número de líneas de transmisión, parámetro para las funciones de lectura
	// y escritura, vectores para el límte de generación, las ofertas de potencia y energía, el costo de la demanda no atendida, la
	// demanda, la susceptancia, el limite de flujo de potencia, los flujos por las líneas, los límites inferior y superior para los 
	// ángulos de voltaje y la demanda no atendida, además de parámetros para acceder a las funciones de Operador y ArrayFactory. 
	//
	public static Dispatch dispatch(IloNumVar[][] var, IloRange[][]  rng, String[] var1, String[] var2, String[] var3,
			int iteration, double [] lowPowerLimit, double [][] powerBid, 
			double [][] powerBidPrice, double [] unsDemandCost, double [][] powerDemandVector, double [] susceptance, 
			double [] powerLimit, double [] angleLb, double [] angleUb, double [] unservedLb, 
			double [] unservedUb, Operator operator, CsvWriter writer) throws NumberFormatException, IOException
	{
		try {
			// create the modeler/solver object
	        IloCplex cplex = new IloCplex();
	        	            
	        // formulación del problema de optimización
	        populateByRow(cplex, var, rng, var1, var2, var3, lowPowerLimit,
	        		Global.factory.mat2vec(powerBid,Global.nUnits,24), Global.factory.mat2vec(powerBidPrice,Global.nUnits,24),
	        		unsDemandCost, powerDemandVector, susceptance, powerLimit, angleLb, angleUb, unservedLb, unservedUb);
	        
	        // escribir el problema de optimización en un archivo .txt
	        cplex.exportModel(Global.directoryResults + "idealDispatchN.lp");
	        
	        //
	        // si hay solución entonces estructurar los resultados
	        //
	        if ( cplex.solve() ) {
	        	
	        	//
	        	// resutados de la optimización
	        	//
	        	operator.getIdealDispatch().setDispatchCost(cplex.getObjValue()); // valor de la función objetivo	
	            operator.getIdealDispatch().setGeneration(Global.factory.vec2mat(cplex.getValues(var[0]),Global.nUnits,24)); // generación por unidad por hora       
	            operator.getIdealDispatch().setVoltajeAngles(Global.factory.vec2mat(cplex.getValues(var[1]),Global.nNodes,24)); // angulo de voltaje por nodo por hora
	            operator.getIdealDispatch().setUnservedDemand(Global.factory.vec2mat(cplex.getValues(var[2]),Global.nNodes,24)); // demanda no atendida por nodo por hora
	            operator.getIdealDispatch().setNodalPrices(Global.factory.vec2mat(cplex.getDuals(rng[0]),Global.nNodes,24)); // valor de los precios nodales por nodo por hora
	            operator.getIdealDispatch().setSpotPrices(operator.getIdealDispatch().getNodalPrices()[0]); // precios spot horarios
	            //
	            // otras variabes obtenidas de la optimización
	            //
	            // double[] dj1    = cplex.getReducedCosts(var[0]);	// costos reducidos
	            // double[] dj2    = cplex.getReducedCosts(var[1]);	// costos reducidos
	            // double[] dj3    = cplex.getReducedCosts(var[2]);	// costos reducidos
	            // double[] slack1 = cplex.getSlacks(rng[0]); 		// slacks
	           
	            //
	            // estructuración del vector de flujos de potencia por las líneas de transmisión
	            //
	            double[] flows = new double[24*Global.nLines];
	            double[] y = cplex.getValues(var[1]);
	            for (int i = 0; i < 24; ++i) {
		              /*flows[0 + i]		= susceptance[0]*y[0+i] - susceptance[0]*y[192 + i]; 		// 0	L1	0	ANTIOQUI	192	CQR
		              flows[24 + i]		= susceptance[1]*y[0+i] - susceptance[1]*y[384 + i];		// 1	L2	0	ANTIOQUI	384	SANCARLO
		              flows[48 + i]		= susceptance[2]*y[0+i] - susceptance[2]*y[288 + i];		// 2	L3	0	ANTIOQUI	288	MAGDAMED
		              flows[72 + i]		= susceptance[3]*y[24+i] - susceptance[3]*y[168 + i];		// 3	L4	24	ATLANTIC	168	CORDOSUC
		              flows[96 + i]		= susceptance[4]*y[48+i] - susceptance[4]*y[288 + i];		// 4	L5	48	BOGOTA		288	MAGDAMED
		              flows[120 + i]	= susceptance[5]*y[48+i] - susceptance[5]*y[312 + i];		// 5	L6	48	BOGOTA		312	META
		              flows[144 + i]	= susceptance[6]*y[72+i] - susceptance[6]*y[24 + i];		// 6	L7	72	BOLIVAR		24	ATLANTIC
		              flows[168 + i]	= susceptance[7]*y[72+i] - susceptance[7]*y[216 + i];		// 7	L8	72	BOLIVAR		216	GCM
		              flows[192 + i]	= susceptance[8]*y[96+i] - susceptance[8]*y[504 + i];		// 8	L9	96	CAUCANAR	504	ECUADOR
		              flows[216 + i]	= susceptance[9]*y[120+i] - susceptance[9]*y[288 + i];		// 9	L10	120	CERROMAT	288	MAGDAMED
		              flows[240 + i]	= susceptance[10]*y[120+i] - susceptance[10]*y[0 + i];		// 10	L11	120	CERROMAT	0	ANTIOQUI
		              flows[264 + i]	= susceptance[11]*y[144+i] - susceptance[11]*y[48 + i];		// 11	L12	144	CHIVOR		48	BOGOTA
		              flows[288 + i]	= susceptance[12]*y[144+i] - susceptance[12]*y[336 + i];	// 12	L13	144	CHIVOR		336	NORDESTE
		              flows[312 + i]	= susceptance[13]*y[168+i] - susceptance[13]*y[120 + i];	// 13	L14	168	CORDOSUC	120	CERROMAT
		              flows[336 + i]	= susceptance[14]*y[192+i] - susceptance[14]*y[408 + i];	// 14	L15	192	CQR			408	TOLIMA
		              flows[360 + i]	= susceptance[15]*y[192+i] - susceptance[15]*y[432 + i];	// 15	L16	192	CQR			432	VALLECAU
		              flows[384 + i]	= susceptance[16]*y[216+i] - susceptance[16]*y[24 + i];		// 16	L17	216	GCM			24	ATLANTIC
		              flows[408 + i]	= susceptance[17]*y[216+i] - susceptance[17]*y[336 + i];	// 17	L18	216	GCM			336	NORDESTE
		              flows[432 + i]	= susceptance[18]*y[216+i] - susceptance[18]*y[480 + i];	// 18	L19	216	GCM			480	CUATRICENTENARIO
		              flows[456 + i]	= susceptance[19]*y[240+i] - susceptance[19]*y[96 + i];		// 19	L20	240	HUILACAQ	96	CAUCANAR
		              flows[480 + i]	= susceptance[20]*y[240+i] - susceptance[20]*y[408 + i];	// 20	L21	240	HUILACAQ	408	TOLIMA
		              flows[504 + i]	= susceptance[21]*y[264+i] - susceptance[21]*y[48 + i];		// 21	L22	264	LAMIEL		48	BOGOTA
		              flows[528 + i]	= susceptance[22]*y[264+i] - susceptance[22]*y[408 + i];	// 22	L23	264	LAMIEL		408	TOLIMA
		              flows[552 + i]	= susceptance[23]*y[288+i] - susceptance[23]*y[384 + i];	// 23	L24	288	MAGDAMED	384	SANCARLO
		              flows[576 + i]	= susceptance[24]*y[288+i] - susceptance[24]*y[336 + i];	// 24	L25	288	MAGDAMED	336	NORDESTE
		              flows[600 + i]	= susceptance[25]*y[336+i] - susceptance[25]*y[456 + i];	// 25	L26	336	NORDESTE	456	COROZO
		              flows[624 + i]	= susceptance[26]*y[360+i] - susceptance[26]*y[48 + i];		// 26	L27	360	PAGUA		48	BOGOTA
		              flows[648 + i]	= susceptance[27]*y[384+i] - susceptance[27]*y[48 + i];		// 27	L28	384	SANCARLO	48	BOGOTA
		              flows[672 + i]	= susceptance[28]*y[384+i] - susceptance[28]*y[192 + i];	// 28	L29	384	SANCARLO	192	CQR
		              flows[696 + i]	= susceptance[29]*y[384+i] - susceptance[29]*y[432 + i];	// 29	L30	384	SANCARLO	432	VALLECAU
		              flows[720 + i]	= susceptance[30]*y[408+i] - susceptance[30]*y[48 + i];		// 30	L31	408	TOLIMA		48	BOGOTA
		              flows[744 + i]	= susceptance[31]*y[432+i] - susceptance[31]*y[96 + i];		// 31	L32	432	VALLECAU	96	CAUCANAR
		              */
		              flows[	0	+ i ]	=	susceptance[	0	] * y [	0	+ i ] -	susceptance[	0	] * y [	192	+ i ];	//	0	L1	0	ANTIOQUI	192	CQR
		              flows[	24	+ i ]	=	susceptance[	1	] * y [	0	+ i ] -	susceptance[	1	] * y [	192	+ i ];	//	1	L2	0	ANTIOQUI	192	CQR
		              flows[	48	+ i ]	=	susceptance[	2	] * y [	0	+ i ] -	susceptance[	2	] * y [	384	+ i ];	//	2	L3	0	ANTIOQUI	384	SANCARLO
		              flows[	72	+ i ]	=	susceptance[	3	] * y [	0	+ i ] -	susceptance[	3	] * y [	384	+ i ];	//	3	L4	0	ANTIOQUI	384	SANCARLO
		              flows[	96	+ i ]	=	susceptance[	4	] * y [	0	+ i ] -	susceptance[	4	] * y [	384	+ i ];	//	4	L5	0	ANTIOQUI	384	SANCARLO
		              flows[	120	+ i ]	=	susceptance[	5	] * y [	0	+ i ] -	susceptance[	5	] * y [	384	+ i ];	//	5	L6	0	ANTIOQUI	384	SANCARLO
		              flows[	144	+ i ]	=	susceptance[	6	] * y [	0	+ i ] -	susceptance[	6	] * y [	384	+ i ];	//	6	L7	0	ANTIOQUI	384	SANCARLO
		              flows[	168	+ i ]	=	susceptance[	7	] * y [	0	+ i ] -	susceptance[	7	] * y [	288	+ i ];	//	7	L8	0	ANTIOQUI	288	MAGDAMED
		              flows[	192	+ i ]	=	susceptance[	8	] * y [	0	+ i ] -	susceptance[	8	] * y [	288	+ i ];	//	8	L9	0	ANTIOQUI	288	MAGDAMED
		              flows[	216	+ i ]	=	susceptance[	9	] * y [	24	+ i ] -	susceptance[	9	] * y [	168	+ i ];	//	9	L10	24	ATLANTIC	168	CORDOSUC
		              flows[	240	+ i ]	=	susceptance[	10	] * y [	24	+ i ] -	susceptance[	10	] * y [	168	+ i ];	//	10	L11	24	ATLANTIC	168	CORDOSUC
		              flows[	264	+ i ]	=	susceptance[	11	] * y [	48	+ i ] -	susceptance[	11	] * y [	288	+ i ];	//	11	L12	48	BOGOTA	288	MAGDAMED
		              flows[	288	+ i ]	=	susceptance[	12	] * y [	48	+ i ] -	susceptance[	12	] * y [	288	+ i ];	//	12	L13	48	BOGOTA	288	MAGDAMED
		              flows[	312	+ i ]	=	susceptance[	13	] * y [	48	+ i ] -	susceptance[	13	] * y [	288	+ i ];	//	13	L14	48	BOGOTA	288	MAGDAMED
		              flows[	336	+ i ]	=	susceptance[	14	] * y [	48	+ i ] -	susceptance[	14	] * y [	312	+ i ];	//	14	L15	48	BOGOTA	312	META
		              flows[	360	+ i ]	=	susceptance[	15	] * y [	48	+ i ] -	susceptance[	15	] * y [	312	+ i ];	//	15	L16	48	BOGOTA	312	META
		              flows[	384	+ i ]	=	susceptance[	16	] * y [	72	+ i ] -	susceptance[	16	] * y [	24	+ i ];	//	16	L17	72	BOLIVAR	24	ATLANTIC
		              flows[	408	+ i ]	=	susceptance[	17	] * y [	72	+ i ] -	susceptance[	17	] * y [	24	+ i ];	//	17	L18	72	BOLIVAR	24	ATLANTIC
		              flows[	432	+ i ]	=	susceptance[	18	] * y [	72	+ i ] -	susceptance[	18	] * y [	24	+ i ];	//	18	L19	72	BOLIVAR	24	ATLANTIC
		              flows[	456	+ i ]	=	susceptance[	19	] * y [	72	+ i ] -	susceptance[	19	] * y [	216	+ i ];	//	19	L20	72	BOLIVAR	216	GCM
		              flows[	480	+ i ]	=	susceptance[	20	] * y [	96	+ i ] -	susceptance[	20	] * y [	504	+ i ];	//	20	L21	96	CAUCANAR	504	ECUADOR
		              flows[	504	+ i ]	=	susceptance[	21	] * y [	96	+ i ] -	susceptance[	21	] * y [	504	+ i ];	//	21	L22	96	CAUCANAR	504	ECUADOR
		              flows[	528	+ i ]	=	susceptance[	22	] * y [	96	+ i ] -	susceptance[	22	] * y [	504	+ i ];	//	22	L23	96	CAUCANAR	504	ECUADOR
		              flows[	552	+ i ]	=	susceptance[	23	] * y [	96	+ i ] -	susceptance[	23	] * y [	504	+ i ];	//	23	L24	96	CAUCANAR	504	ECUADOR
		              flows[	576	+ i ]	=	susceptance[	24	] * y [	120	+ i ] -	susceptance[	24	] * y [	288	+ i ];	//	24	L25	120	CERROMAT	288	MAGDAMED
		              flows[	600	+ i ]	=	susceptance[	25	] * y [	120	+ i ] -	susceptance[	25	] * y [	0	+ i ];	//	25	L26	120	CERROMAT	0	ANTIOQUI
		              flows[	624	+ i ]	=	susceptance[	26	] * y [	144	+ i ] -	susceptance[	26	] * y [	48	+ i ];	//	26	L27	144	CHIVOR	48	BOGOTA
		              flows[	648	+ i ]	=	susceptance[	27	] * y [	144	+ i ] -	susceptance[	27	] * y [	48	+ i ];	//	27	L28	144	CHIVOR	48	BOGOTA
		              flows[	672	+ i ]	=	susceptance[	28	] * y [	144	+ i ] -	susceptance[	28	] * y [	48	+ i ];	//	28	L29	144	CHIVOR	48	BOGOTA
		              flows[	696	+ i ]	=	susceptance[	29	] * y [	144	+ i ] -	susceptance[	29	] * y [	48	+ i ];	//	29	L30	144	CHIVOR	48	BOGOTA
		              flows[	720	+ i ]	=	susceptance[	30	] * y [	144	+ i ] -	susceptance[	30	] * y [	336	+ i ];	//	30	L31	144	CHIVOR	336	NORDESTE
		              flows[	744	+ i ]	=	susceptance[	31	] * y [	144	+ i ] -	susceptance[	31	] * y [	336	+ i ];	//	31	L32	144	CHIVOR	336	NORDESTE
		              flows[	768	+ i ]	=	susceptance[	32	] * y [	168	+ i ] -	susceptance[	32	] * y [	120	+ i ];	//	32	L33	168	CORDOSUC	120	CERROMAT
		              flows[	792	+ i ]	=	susceptance[	33	] * y [	168	+ i ] -	susceptance[	33	] * y [	120	+ i ];	//	33	L34	168	CORDOSUC	120	CERROMAT
		              flows[	816	+ i ]	=	susceptance[	34	] * y [	192	+ i ] -	susceptance[	34	] * y [	408	+ i ];	//	34	L35	192	CQR	408	TOLIMA
		              flows[	840	+ i ]	=	susceptance[	35	] * y [	192	+ i ] -	susceptance[	35	] * y [	408	+ i ];	//	35	L36	192	CQR	408	TOLIMA
		              flows[	864	+ i ]	=	susceptance[	36	] * y [	192	+ i ] -	susceptance[	36	] * y [	432	+ i ];	//	36	L37	192	CQR	432	VALLECAU
		              flows[	888	+ i ]	=	susceptance[	37	] * y [	192	+ i ] -	susceptance[	37	] * y [	432	+ i ];	//	37	L38	192	CQR	432	VALLECAU
		              flows[	912	+ i ]	=	susceptance[	38	] * y [	192	+ i ] -	susceptance[	38	] * y [	432	+ i ];	//	38	L39	192	CQR	432	VALLECAU
		              flows[	936	+ i ]	=	susceptance[	39	] * y [	192	+ i ] -	susceptance[	39	] * y [	432	+ i ];	//	39	L40	192	CQR	432	VALLECAU
		              flows[	960	+ i ]	=	susceptance[	40	] * y [	192	+ i ] -	susceptance[	40	] * y [	432	+ i ];	//	40	L41	192	CQR	432	VALLECAU
		              flows[	984	+ i ]	=	susceptance[	41	] * y [	216	+ i ] -	susceptance[	41	] * y [	24	+ i ];	//	41	L42	216	GCM	24	ATLANTIC
		              flows[	1008	+ i ]	=	susceptance[	42	] * y [	216	+ i ] -	susceptance[	42	] * y [	24	+ i ];	//	42	L43	216	GCM	24	ATLANTIC
		              flows[	1032	+ i ]	=	susceptance[	43	] * y [	216	+ i ] -	susceptance[	43	] * y [	24	+ i ];	//	43	L44	216	GCM	24	ATLANTIC
		              flows[	1056	+ i ]	=	susceptance[	44	] * y [	216	+ i ] -	susceptance[	44	] * y [	336	+ i ];	//	44	L45	216	GCM	336	NORDESTE
		              flows[	1080	+ i ]	=	susceptance[	45	] * y [	216	+ i ] -	susceptance[	45	] * y [	480	+ i ];	//	45	L46	216	GCM	480	CUATRICENTENARIO
		              flows[	1104	+ i ]	=	susceptance[	46	] * y [	240	+ i ] -	susceptance[	46	] * y [	96	+ i ];	//	46	L47	240	HUILACAQ	96	CAUCANAR
		              flows[	1128	+ i ]	=	susceptance[	47	] * y [	240	+ i ] -	susceptance[	47	] * y [	96	+ i ];	//	47	L48	240	HUILACAQ	96	CAUCANAR
		              flows[	1152	+ i ]	=	susceptance[	48	] * y [	240	+ i ] -	susceptance[	48	] * y [	96	+ i ];	//	48	L49	240	HUILACAQ	96	CAUCANAR
		              flows[	1176	+ i ]	=	susceptance[	49	] * y [	240	+ i ] -	susceptance[	49	] * y [	96	+ i ];	//	49	L50	240	HUILACAQ	96	CAUCANAR
		              flows[	1200	+ i ]	=	susceptance[	50	] * y [	240	+ i ] -	susceptance[	50	] * y [	408	+ i ];	//	50	L51	240	HUILACAQ	408	TOLIMA
		              flows[	1224	+ i ]	=	susceptance[	51	] * y [	264	+ i ] -	susceptance[	51	] * y [	48	+ i ];	//	51	L52	264	LAMIEL	48	BOGOTA
		              flows[	1248	+ i ]	=	susceptance[	52	] * y [	264	+ i ] -	susceptance[	52	] * y [	48	+ i ];	//	52	L53	264	LAMIEL	48	BOGOTA
		              flows[	1272	+ i ]	=	susceptance[	53	] * y [	264	+ i ] -	susceptance[	53	] * y [	408	+ i ];	//	53	L54	264	LAMIEL	408	TOLIMA
		              flows[	1296	+ i ]	=	susceptance[	54	] * y [	264	+ i ] -	susceptance[	54	] * y [	408	+ i ];	//	54	L55	264	LAMIEL	408	TOLIMA
		              flows[	1320	+ i ]	=	susceptance[	55	] * y [	288	+ i ] -	susceptance[	55	] * y [	384	+ i ];	//	55	L56	288	MAGDAMED	384	SANCARLO
		              flows[	1344	+ i ]	=	susceptance[	56	] * y [	288	+ i ] -	susceptance[	56	] * y [	384	+ i ];	//	56	L57	288	MAGDAMED	384	SANCARLO
		              flows[	1368	+ i ]	=	susceptance[	57	] * y [	288	+ i ] -	susceptance[	57	] * y [	336	+ i ];	//	57	L58	288	MAGDAMED	336	NORDESTE
		              flows[	1392	+ i ]	=	susceptance[	58	] * y [	288	+ i ] -	susceptance[	58	] * y [	336	+ i ];	//	58	L59	288	MAGDAMED	336	NORDESTE
		              flows[	1416	+ i ]	=	susceptance[	59	] * y [	288	+ i ] -	susceptance[	59	] * y [	336	+ i ];	//	59	L60	288	MAGDAMED	336	NORDESTE
		              flows[	1440	+ i ]	=	susceptance[	60	] * y [	288	+ i ] -	susceptance[	60	] * y [	336	+ i ];	//	60	L61	288	MAGDAMED	336	NORDESTE
		              flows[	1464	+ i ]	=	susceptance[	61	] * y [	336	+ i ] -	susceptance[	61	] * y [	456	+ i ];	//	61	L62	336	NORDESTE	456	COROZO
		              flows[	1488	+ i ]	=	susceptance[	62	] * y [	336	+ i ] -	susceptance[	62	] * y [	456	+ i ];	//	62	L63	336	NORDESTE	456	COROZO
		              flows[	1512	+ i ]	=	susceptance[	63	] * y [	360	+ i ] -	susceptance[	63	] * y [	48	+ i ];	//	63	L64	360	PAGUA	48	BOGOTA
		              flows[	1536	+ i ]	=	susceptance[	64	] * y [	360	+ i ] -	susceptance[	64	] * y [	48	+ i ];	//	64	L65	360	PAGUA	48	BOGOTA
		              flows[	1560	+ i ]	=	susceptance[	65	] * y [	360	+ i ] -	susceptance[	65	] * y [	48	+ i ];	//	65	L66	360	PAGUA	48	BOGOTA
		              flows[	1584	+ i ]	=	susceptance[	66	] * y [	360	+ i ] -	susceptance[	66	] * y [	48	+ i ];	//	66	L67	360	PAGUA	48	BOGOTA
		              flows[	1608	+ i ]	=	susceptance[	67	] * y [	384	+ i ] -	susceptance[	67	] * y [	48	+ i ];	//	67	L68	384	SANCARLO	48	BOGOTA
		              flows[	1632	+ i ]	=	susceptance[	68	] * y [	384	+ i ] -	susceptance[	68	] * y [	48	+ i ];	//	68	L69	384	SANCARLO	48	BOGOTA
		              flows[	1656	+ i ]	=	susceptance[	69	] * y [	384	+ i ] -	susceptance[	69	] * y [	192	+ i ];	//	69	L70	384	SANCARLO	192	CQR
		              flows[	1680	+ i ]	=	susceptance[	70	] * y [	384	+ i ] -	susceptance[	70	] * y [	192	+ i ];	//	70	L71	384	SANCARLO	192	CQR
		              flows[	1704	+ i ]	=	susceptance[	71	] * y [	384	+ i ] -	susceptance[	71	] * y [	432	+ i ];	//	71	L72	384	SANCARLO	432	VALLECAU
		              flows[	1728	+ i ]	=	susceptance[	72	] * y [	408	+ i ] -	susceptance[	72	] * y [	48	+ i ];	//	72	L73	408	TOLIMA	48	BOGOTA
		              flows[	1752	+ i ]	=	susceptance[	73	] * y [	408	+ i ] -	susceptance[	73	] * y [	48	+ i ];	//	73	L74	408	TOLIMA	48	BOGOTA
		              flows[	1776	+ i ]	=	susceptance[	74	] * y [	408	+ i ] -	susceptance[	74	] * y [	48	+ i ];	//	74	L75	408	TOLIMA	48	BOGOTA
		              flows[	1800	+ i ]	=	susceptance[	75	] * y [	408	+ i ] -	susceptance[	75	] * y [	48	+ i ];	//	75	L76	408	TOLIMA	48	BOGOTA
		              flows[	1824	+ i ]	=	susceptance[	76	] * y [	432	+ i ] -	susceptance[	76	] * y [	96	+ i ];	//	76	L77	432	VALLECAU	96	CAUCANAR
		              flows[	1848	+ i ]	=	susceptance[	77	] * y [	432	+ i ] -	susceptance[	77	] * y [	96	+ i ];	//	77	L78	432	VALLECAU	96	CAUCANAR
	            }
	            
	            //
	            // cálculo de la capacidad remanente
	            //
	            double [][] differences = new double[Global.nLines][24];
	            for (int i = 0; i < Global.nLines; i++)
	            {
	            	for (int h = 0; h < 24; h++)
	            	{
	            		differences[i][h] = powerLimit[i]-Math.abs(Global.factory.vec2mat(flows,Global.nLines,24)[i][h]);
	            	}
	            }
	            
	            //
	            // resultados adicionales
	            //
	            operator.getIdealDispatch().setFlows(Global.factory.vec2mat(flows,Global.nLines,24)); // flujos por las líneas de transmisión para cada hora
	            operator.getIdealDispatch().setEnergyDemand(powerDemandVector); 	// guardar la demanda para cada hora
	            operator.getIdealDispatch().setEnergyBid(powerBid); 		   		// guardar la oferta de energía
	            operator.getIdealDispatch().setEnergyBidPrice(powerBidPrice);		// guardar el precio de la oferta de energía
	            operator.getIdealDispatch().setRemainderCapacity(differences); 		// guardar la capacidad remanente
	            
	            //
	            // escritura de los resultados en un archivo .csv
	            //
	            Global.rw.writeCsv(iteration, writer, operator.getIdealDispatch());           
	        }
	        else {
	        	System.out.println("El problema de optimización no tiene solución");
	        }
	        
	        // Cerrar el bloque Cplex
	        cplex.end();
	        
	     }
	     catch (IloException e) {
	        System.err.println("Ideal dispatch: concert exception '" + e + "' caught");
	     }
		
		// Retorna un objeto con los vectores de los resultados
		return operator.getIdealDispatch();
	}
	
	//
	// Función para definir la estructura del problema de optimización
	//
	static void populateByRow(IloMPModeler model, IloNumVar[][] var, IloRange[][] rng, String[] var1, String[] var2, String[] var3,
			double [] lowPowerLimit, double [] powerBid, double [] powerBidPrice, double [] unsDemandCost, double [][] powerDemandVector, 
			double [] susceptance, double [] powerLimit, double [] angleLb, double [] angleUb, double [] unservedLb, double [] unservedUb) throws IloException {
		
		//
		// control: si el mínimo técnico es mayor que la oferta entonces el mínimo técnico se hace cero
		//
		for (int i = 0; i < 24*Global.nUnits; i++)
		{
			if(lowPowerLimit[i] > powerBid[i])
			{
				lowPowerLimit[i] = 0;
			}
		}
		
		// Variables  --------------------------------------------------------------------------------
		IloNumVar[] generation  = model.numVarArray(24*Global.nUnits, lowPowerLimit, powerBid, var1); 	// progrmación de la generación
		IloNumVar[] angle       = model.numVarArray(24*Global.nNodes, angleLb, angleUb, var2); 			// ángulos de voltaje
		IloNumVar[] unserved    = model.numVarArray(24*Global.nNodes, unservedLb, unservedUb, var3); 	// demanda no atendida
		var[0] = generation; 	// generación
		var[1] = angle; 		// ángulos de voltaje
		var[2] = unserved; 		// demanda no atendida
		
		//
		// Función objetivo
		//
		// Minimizar el costo de generación y el costo por demanda no atendida
		model.addMinimize(model.sum(model.scalProd(generation, powerBidPrice), model.scalProd(unserved, unsDemandCost))); 
		
		//
		// Restricciones  --------------------------------------------------------------------------------
		//
		
		/* Balance oferta y demanda ANTIOQUI
		31	GUATAPE		ANTIOQUI	720
		32	GUATRON		ANTIOQUI	744
		35	JAGUAS		ANTIOQUI	816
		36	LATASAJERA	ANTIOQUI	840
		37	MAGUAFRE	ANTIOQUI	864
		38	MANTIOQ1	ANTIOQUI	888
		41	MCALDERAS	ANTIOQUI	960
		42	MCARUQUIA	ANTIOQUI	984
		43	MCASCADA1	ANTIOQUI	1008
		53	MGUANAQUITA	ANTIOQUI	1248
		64	MSANTARITA	ANTIOQUI	1512
		65	MSANTIAGO	ANTIOQUI	1536
		78	PLAYAS		ANTIOQUI	1848
		79	PORCE2		ANTIOQUI	1872
		80	PORCE3		ANTIOQUI	1896
		81	PORCE3P		ANTIOQUI	1920
		85	RPIEDRAS	ANTIOQUI	2016
		
		
		0	L1	0	ANTIOQUI	192	CQR	ISA	230.00	3.465	0
		1	L2	0	ANTIOQUI	192	CQR	ISA	230.00	3.464	24
		2	L3	0	ANTIOQUI	384	SANCARLO	ISA	223.33	3.421	48
		3	L4	0	ANTIOQUI	384	SANCARLO	ISA	223.33	3.421	72
		4	L5	0	ANTIOQUI	384	SANCARLO	ISA	265.42	3.535	96
		5	L6	0	ANTIOQUI	384	SANCARLO	ISA	265.42	3.535	120
		6	L7	0	ANTIOQUI	384	SANCARLO	ISA	952.50	5.138	144
		7	L8	0	ANTIOQUI	288	MAGDAMED	ISA	225.86	3.5	168
		8	L9	0	ANTIOQUI	288	MAGDAMED	ISA	176.64	3.276	192
		25	L26	120	CERROMAT	0	ANTIOQUI	ISA	952.50	5.134	600
		 */
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[720+i], 
					generation[744+i], 
					generation[816+i], 
					generation[840+i], 
					generation[864+i], 
					generation[888+i], 
					generation[960+i], 
					generation[984+i], 
					generation[1008+i],
					generation[1248+i],
					generation[1512+i],
					generation[1536+i],
					generation[1848+i],
					generation[1872+i],
					generation[1896+i],
					generation[1920+i],
					generation[2016+i],					
					unserved[0+i],
					// (SUS[ANT-CQR] + SUS[ANT-SCA] + SUS[ANT-MAG] + SUS[CER-ANT]) * VA_ANTIOQUI
					model.prod(susceptance[0] + susceptance[1] + susceptance[2] + susceptance[3]
							 + susceptance[4] + susceptance[5] + susceptance[6] + susceptance[7]
							 +  susceptance[8] + susceptance[25], angle[0+i]),
					model.prod(-susceptance[0], angle[192+i]), 	// SUS[ANT-CQR]	* VA_CQR
					model.prod(-susceptance[1], angle[192+i]), 	// SUS[ANT-CQR]	* VA_CQR
					model.prod(-susceptance[2], angle[384+i]),	// SUS[ANT-SCA]	* VA_SANCARLO
					model.prod(-susceptance[3], angle[384+i]),	// SUS[ANT-SCA]	* VA_SANCARLO
					model.prod(-susceptance[4], angle[384+i]),	// SUS[ANT-SCA]	* VA_SANCARLO
					model.prod(-susceptance[5], angle[384+i]),	// SUS[ANT-SCA]	* VA_SANCARLO
					model.prod(-susceptance[6], angle[384+i]),	// SUS[ANT-SCA]	* VA_SANCARLO
					model.prod(-susceptance[7], angle[288+i]), 	// SUS[ANT-MAG]	* VA_MAGDAMED
					model.prod(-susceptance[8], angle[288+i]), 	// SUS[ANT-MAG]	* VA_MAGDAMED
					model.prod(-susceptance[25], angle[120+i])};// SUS[CER-ANT]	* VA_CERROMAT 	
			
			rng[0][0+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[0][i], "C_ANTIOQUI_"+i); 			//  DEM[CARIBE2]
		}
		
		
		/* Balance oferta y demanda ATLANTIC
		2	BARRANQ3	ATLANTIC	24
		3	BARRANQ4	ATLANTIC	48
		24	FLORES1		ATLANTIC	552
		25	FLORES21	ATLANTIC	576
		26	FLORES3		ATLANTIC	600
		27	FLORESIVB	ATLANTIC	624
		93	TEBSA		ATLANTIC	2208
		
		9	L10	24	ATLANTIC	168	CORDOSUC	ISA	1,250.00	4.927	216
		10	L11	24	ATLANTIC	168	CORDOSUC	ISA	1,187.50	5.14	240
		16	L17	72	BOLIVAR	24	ATLANTIC	TRANSELCA	138.60	3.347	384
		17	L18	72	BOLIVAR	24	ATLANTIC	ISA	204.38	3.361	408
		18	L19	72	BOLIVAR	24	ATLANTIC	TRANSELCA	138.60	3.319	432
		41	L42	216	GCM	24	ATLANTIC	TRANSELCA	144.32	3.162	984
		42	L43	216	GCM	24	ATLANTIC	TRANSELCA	213.62	3.543	1008
		43	L44	216	GCM	24	ATLANTIC	TRANSELCA	186.56	3.543	1032
		 */
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[24+i], 
					generation[48+i], 
					generation[552+i], 
					generation[576+i], 
					generation[600+i],
					generation[624+i],
					generation[2208+i],					
					unserved[24+i],
					// (SUS[ATL-CDS] + SUS[BOL-ATL] + SUS[GCM-ATL]) * VA_ATLANTIC
					model.prod(susceptance[9] + susceptance[10]  + susceptance[16] + susceptance[17]
							+ susceptance[18] + susceptance[41]  + susceptance[42] + susceptance[43], angle[24+i]),
					model.prod(-susceptance[9], angle[168+i]),	// SUS[ATL-CDS]	* VA_CORDOSUC
					model.prod(-susceptance[10], angle[168+i]),	// SUS[ATL-CDS]	* VA_CORDOSUC
					model.prod(-susceptance[16], angle[72+i]),	// SUS[BOL-ATL] * VA_BOLIVAR
					model.prod(-susceptance[17], angle[72+i]),	// SUS[BOL-ATL] * VA_BOLIVAR
					model.prod(-susceptance[18], angle[72+i]),	// SUS[BOL-ATL] * VA_BOLIVAR
					model.prod(-susceptance[41], angle[216+i]), // SUS[GCM-ATL] * VA_GCM
					model.prod(-susceptance[42], angle[216+i]), // SUS[GCM-ATL] * VA_GCM
					model.prod(-susceptance[43], angle[216+i])}; // SUS[GCM-ATL] * VA_GCM	
			
			rng[0][24+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[1][i], "C_ATLANTIC_"+i); 
		}
		
		
		/* Balance oferta y demanda BOGOTÁ
		33	GUAVIO			BOGOTA	768
		40	MBOGOTA1		BOGOTA	936
		48	MCUNDINAMARCA	BOGOTA	1128
		63	MSANTANA		BOGOTA	1488
		101	ZIPAEMG2		BOGOTA	2400
		102	ZIPAEMG3		BOGOTA	2424
		103	ZIPAEMG4		BOGOTA	2448
		104	ZIPAEMG5		BOGOTA	2472
		
		11	L12	48	BOGOTA	288	MAGDAMED	ISA	952.50	4.831	264
		12	L13	48	BOGOTA	288	MAGDAMED	ISA	226.32	3.468	288
		13	L14	48	BOGOTA	288	MAGDAMED	ISA	226.32	3.468	312
		14	L15	48	BOGOTA	312	META	EEB	220.80	4.561	336
		15	L16	48	BOGOTA	312	META	EEB	331.20	4.538	360
		26	L27	144	CHIVOR	48	BOGOTA	ISA	220.80	3.419	624
		27	L28	144	CHIVOR	48	BOGOTA	ISA	220.80	3.419	648
		28	L29	144	CHIVOR	48	BOGOTA	ISA	234.83	3.442	672
		29	L30	144	CHIVOR	48	BOGOTA	ISA	234.83	3.442	696
		51	L52	264	LAMIEL	48	BOGOTA	ISA	227.24	3.466	1224
		52	L53	264	LAMIEL	48	BOGOTA	ISA	227.24	3.466	1248
		63	L64	360	PAGUA	48	BOGOTA	EEB	257.60	3.479	1512
		64	L65	360	PAGUA	48	BOGOTA	EEB	257.60	3.479	1536
		65	L66	360	PAGUA	48	BOGOTA	EEB	220.80	3.471	1560
		66	L67	360	PAGUA	48	BOGOTA	EEB	220.80	3.476	1584
		67	L68	384	SANCARLO	48	BOGOTA	ISA	222.64	3.487	1608
		68	L69	384	SANCARLO	48	BOGOTA	ISA	222.64	3.487	1632
		72	L73	408	TOLIMA	48	BOGOTA	ISA	222.18	3.469	1728
		73	L74	408	TOLIMA	48	BOGOTA	ISA	222.18	3.469	1752
		74	L75	408	TOLIMA	48	BOGOTA	ISA	218.96	3.463	1776
		75	L76	408	TOLIMA	48	BOGOTA	ISA	218.96	3.463	1800
		 */
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[768+i],
					generation[936+i], 
					generation[1128+i], 
					generation[1488+i], 
					generation[2400+i], 
					generation[2424+i], 
					generation[2448+i], 
					generation[2472+i], 
					unserved[48+i],
					// (SUS[BOG-MAG] + SUS[BOG-MET] + SUS[CHI-BOG] + SUS[LAM-BOG] + SUS[PAG-BOG] + SUS[SCA-BOG] + SUS[TOL-BOG]) * VA_BOGOTA
					model.prod(susceptance[11] + susceptance[12] + susceptance[13] + susceptance[14] + susceptance[15] + susceptance[26] + susceptance[27]
							+ susceptance[28] + susceptance[29] + susceptance[51] + susceptance[52] + susceptance[63] + susceptance[64] + susceptance[65]
							+ susceptance[66] + susceptance[67] + susceptance[68] + susceptance[72] + susceptance[73] + susceptance[74] + susceptance[75], angle[48+i]),
					model.prod(-susceptance[11], angle[288+i]),	// SUS[BOG-MAG] * VA_MAGDAMED
					model.prod(-susceptance[12], angle[288+i]),	// SUS[BOG-MAG] * VA_MAGDAMED
					model.prod(-susceptance[13], angle[288+i]),	// SUS[BOG-MAG] * VA_MAGDAMED
					model.prod(-susceptance[14], angle[312+i]),	// SUS[BOG-MET] * VA_META
					model.prod(-susceptance[15], angle[312+i]),	// SUS[BOG-MET] * VA_META
					model.prod(-susceptance[26], angle[144+i]),	// SUS[CHI-BOG] * VA_CHIVOR 
					model.prod(-susceptance[27], angle[144+i]),	// SUS[CHI-BOG] * VA_CHIVOR 
					model.prod(-susceptance[28], angle[144+i]),	// SUS[CHI-BOG] * VA_CHIVOR 
					model.prod(-susceptance[29], angle[144+i]),	// SUS[CHI-BOG] * VA_CHIVOR 
					model.prod(-susceptance[51], angle[264+i]),	// SUS[LAM-BOG] * VA_LAMIEL
					model.prod(-susceptance[52], angle[264+i]),	// SUS[LAM-BOG] * VA_LAMIEL
					model.prod(-susceptance[63], angle[360+i]),	// SUS[PAG-BOG] * VA_PAGUA
					model.prod(-susceptance[64], angle[360+i]),	// SUS[PAG-BOG] * VA_PAGUA
					model.prod(-susceptance[65], angle[360+i]),	// SUS[PAG-BOG] * VA_PAGUA
					model.prod(-susceptance[66], angle[360+i]),	// SUS[PAG-BOG] * VA_PAGUA
					model.prod(-susceptance[67], angle[384+i]),	// SUS[SCA-BOG] * VA_SANCARLO
					model.prod(-susceptance[68], angle[384+i]),	// SUS[SCA-BOG] * VA_SANCARLO
					model.prod(-susceptance[72], angle[408+i]),	// SUS[TOL-BOG] * VA_TOLIMA
					model.prod(-susceptance[73], angle[408+i]),	// SUS[TOL-BOG] * VA_TOLIMA
					model.prod(-susceptance[74], angle[408+i]),	// SUS[TOL-BOG] * VA_TOLIMA
					model.prod(-susceptance[75], angle[408+i])};// SUS[TOL-BOG] * VA_TOLIMA
			
			rng[0][48+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[2][i], "C_BOGOTA_"+i); 
		}
		
		
		/* Balance oferta y demanda BOLIVAR
		10	CTGEMG1		BOLIVAR	216
		11	CTGEMG2		BOLIVAR	240
		12	CTGEMG3		BOLIVAR	264
		83	PROELEC1	BOLIVAR	1968
		84	PROELEC2	BOLIVAR	1992
		90	TCANDEL1	BOLIVAR	2136
		91	TCANDEL2	BOLIVAR	2160
		
		16	L17	72	BOLIVAR	24	ATLANTIC	TRANSELCA	138.60	3.347	384
		17	L18	72	BOLIVAR	24	ATLANTIC	ISA	204.38	3.361	408
		18	L19	72	BOLIVAR	24	ATLANTIC	TRANSELCA	138.60	3.319	432
		19	L20	72	BOLIVAR	216	GCM	ISA	952.50	4.914	456
		*/
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[216+i], 
					generation[240+i], 
					generation[264+i], 
					generation[1968+i], 
					generation[1992+i], 
					generation[2136+i], 
					generation[2160+i], 
					unserved[72+i],
					// (SUS[BOL-ATL] + SUS[BOL-GCM]) * VA_BOLIVAR
					model.prod(susceptance[16] + susceptance[17] + susceptance[18] + susceptance[19], angle[72+i]),
					model.prod(-susceptance[16], angle[24+i]),	// SUS[BOL-ATL] * VA_ATLANTIC
					model.prod(-susceptance[17], angle[24+i]),	// SUS[BOL-ATL] * VA_ATLANTIC
					model.prod(-susceptance[18], angle[24+i]),	// SUS[BOL-ATL] * VA_ATLANTIC
					model.prod(-susceptance[19], angle[216+i])};// SUS[BOL-GCM] * VA_GCM
			
			rng[0][72+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[3][i], "C_BOLIVAR_"+i); 
		}
		
		
		/* Balance oferta y demanda CAUCANAR
		7	COINCAUCA	CAUCANAR	144
		28	FLORIDA2	CAUCANAR	648
		44	MCAUCAN1	CAUCANAR	1032
		45	MCAUCAN2	CAUCANAR	1056
		62	MRIOMAYO	CAUCANAR	1464
		
		20	L21	96	CAUCANAR	504	ECUADOR	ISA	242.19	3.452	480
		21	L22	96	CAUCANAR	504	ECUADOR	ISA	242.19	3.452	504
		22	L23	96	CAUCANAR	504	ECUADOR	EEB	244.26	3.472	528
		23	L24	96	CAUCANAR	504	ECUADOR	EEB	244.26	3.472	552
		46	L47	240	HUILACAQ	96	CAUCANAR	ISA	184.00	3.619	1104
		47	L48	240	HUILACAQ	96	CAUCANAR	ISA	184.00	3.619	1128
		48	L49	240	HUILACAQ	96	CAUCANAR	EEB	239.20	4.664	1152
		49	L50	240	HUILACAQ	96	CAUCANAR	EEB	239.20	4.718	1176
		76	L77	432	VALLECAU	96	CAUCANAR	ISA	230.69	3.489	1824
		77	L78	432	VALLECAU	96	CAUCANAR	ISA	230.69	3.511	1848
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[144+i], 
					generation[648+i], 
					generation[1032+i], 
					generation[1056+i], 
					generation[1464+i], 
					unserved[96+i],
					// (SUS[CAU-ECU] + SUS[HUI-CAU]+ SUS[VAL-CAU]) * VA_CAUCANAR
					model.prod(susceptance[20] + susceptance[21]  + susceptance[22]
						+ susceptance[23] + susceptance[46]  + susceptance[47]
						+ susceptance[48] + susceptance[49]  + susceptance[76]
						+ susceptance[77], angle[96+i]),
					model.prod(-susceptance[20], angle[504+i]),		// SUS[CAU-ECU] * VA_ECUADOR
					model.prod(-susceptance[21], angle[504+i]),		// SUS[CAU-ECU] * VA_ECUADOR
					model.prod(-susceptance[22], angle[504+i]),		// SUS[CAU-ECU] * VA_ECUADOR
					model.prod(-susceptance[23], angle[504+i]),		// SUS[CAU-ECU] * VA_ECUADOR
					model.prod(-susceptance[46], angle[240+i]),		// SUS[HUI-CAU] * VA_HUILACAQ
					model.prod(-susceptance[47], angle[240+i]),		// SUS[HUI-CAU] * VA_HUILACAQ
					model.prod(-susceptance[48], angle[240+i]),		// SUS[HUI-CAU] * VA_HUILACAQ
					model.prod(-susceptance[49], angle[240+i]),		// SUS[HUI-CAU] * VA_HUILACAQ
					model.prod(-susceptance[76], angle[432+i]),		// SUS[VAL-CAU] * VA_VALLECAU
					model.prod(-susceptance[77], angle[432+i])};	// SUS[VAL-CAU] * VA_VALLECAU
			
			rng[0][96+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[4][i], "C_CAUCANAR_"+i); 
		}
		
		/* Balance oferta y demanda CERROMAT
		99	URRA	CERROMAT	2352
		
		24	L25	120	CERROMAT	288	MAGDAMED	ISA	1,250.00	5.004	576
		25	L26	120	CERROMAT	0	ANTIOQUI	ISA	952.50	5.134	600
		32	L33	168	CORDOSUC	336	CERROMAT	ISA	1,190.00	4.927	768
		33	L34	168	CORDOSUC	120	CERROMAT	ISA	1,250.00	5.101	792
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[2352+i], 
					unserved[120+i],
					// (SUS[CER-MAG] + SUS[CER-ANT]+ SUS[CDS-CER]) * VA_CERROMAT
					model.prod(susceptance[24] + susceptance[25] + susceptance[32] + susceptance[33], angle[120+i]),
					model.prod(-susceptance[24], angle[288+i]),		// SUS[CER-MAG] * VA_MAGDAMED
					model.prod(-susceptance[25], angle[0+i]),		// SUS[CER-ANT] * VA_ANTIOQUI
					model.prod(-susceptance[32], angle[168+i]),		// SUS[CDS-CER] * VA_CORDOSUC
					model.prod(-susceptance[33], angle[168+i])};	// SUS[CDS-CER] * VA_CORDOSUC
			
			rng[0][120+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[5][i], "C_CERROMAT_"+i); 
		}
		
		
		/* Balance oferta y demanda CHIVOR
		6	CHIVOR		CHIVOR	120
		
		26	L27	144	CHIVOR	48	BOGOTA	ISA	220.80	3.419	624
		27	L28	144	CHIVOR	48	BOGOTA	ISA	220.80	3.419	648
		28	L29	144	CHIVOR	48	BOGOTA	ISA	234.83	3.442	672
		29	L30	144	CHIVOR	48	BOGOTA	ISA	234.83	3.442	696
		30	L31	144	CHIVOR	336	NORDESTE	ISA	220.80	3.421	720
		31	L32	144	CHIVOR	336	NORDESTE	ISA	220.80	3.421	744
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[120+i],
					unserved[144+i],
					// (SUS[CHI-BOG] + SUS[CHI-NOR]) * VA_CHIVOR
					model.prod(susceptance[26] + susceptance[27] + susceptance[28] + susceptance[29]
							+ susceptance[30] + susceptance[31], angle[144+i]),
					model.prod(-susceptance[26], angle[48+i]),		// SUS[CHI-BOG] * VA_BOGOTA
					model.prod(-susceptance[27], angle[48+i]),		// SUS[CHI-BOG] * VA_BOGOTA
					model.prod(-susceptance[28], angle[48+i]),		// SUS[CHI-BOG] * VA_BOGOTA
					model.prod(-susceptance[29], angle[48+i]),		// SUS[CHI-BOG] * VA_BOGOTA
					model.prod(-susceptance[30], angle[336+i]),		// SUS[CHI-NOR] * VA_NORDESTE
					model.prod(-susceptance[31], angle[336+i])};	// SUS[CHI-NOR] * VA_NORDESTE
			
			rng[0][144+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[6][i], "C_CHIVOR_"+i); 
		}
		
		
		/* Balance oferta y demanda CORDOSUC
		No hay oferta
		
		32	L33	168	CORDOSUC	120	CERROMAT	ISA	1,190.00	4.927	768
		33	L34	168	CORDOSUC	120	CERROMAT	ISA	1,250.00	5.101	792
		9	L10	24	ATLANTIC	168	CORDOSUC	ISA	1,250.00	4.927	216
		10	L11	24	ATLANTIC	168	CORDOSUC	ISA	1,187.50	5.14	240
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					unserved[168+i],
					// (SUS[CDS-CER] + SUS[ATL-CDS]) * VA_CORDOSUC
					model.prod(susceptance[32] + susceptance[33] + susceptance[9] + susceptance[10], angle[168+i]),
					model.prod(-susceptance[32], angle[120+i]),	// SUS[CDS-CER]
					model.prod(-susceptance[33], angle[120+i]),	// SUS[CDS-CER]
					model.prod(-susceptance[9], angle[24+i]), 	// SUS[ATL-CDS]
					model.prod(-susceptance[10], angle[24+i])};	// SUS[ATL-CDS]
			
			rng[0][168+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[7][i], "C_CORDOSUC_"+i); 
		}
		
		
		/* Balance oferta y demanda CQR
		14	DORADA1			CQR	312
		23	ESMERALDA		CQR	528
		34	INSULA			CQR	792
		39	MBELMONTE		CQR	912
		47	MCQR1			CQR	1104
		50	MELBOSQUE		CQR	1176
		59	MNLIBARE		CQR	1392
		88	SANFRANCISCO	CQR	2088
		
		34	L35	192	CQR	408	TOLIMA	ISA	206.08	3.485	816
		35	L36	192	CQR	408	TOLIMA	ISA	184.00	3.494	840
		36	L37	192	CQR	432	VALLECAU	ISA	230.00	3.488	864
		37	L38	192	CQR	432	VALLECAU	ISA	230.00	3.488	888
		38	L39	192	CQR	432	VALLECAU	ISA	226.55	3.459	912
		39	L40	192	CQR	432	VALLECAU	ISA	226.55	3.459	936
		40	L41	192	CQR	432	VALLECAU	ISA	230.00	3.432	960
		0	L1	0	ANTIOQUI	192	CQR	ISA	230.00	3.465	0
		1	L2	0	ANTIOQUI	192	CQR	ISA	230.00	3.464	24
		69	L70	384	SANCARLO	192	CQR	ISA	223.10	3.441	1656
		70	L71	384	SANCARLO	192	CQR	ISA	223.10	3.441	1680
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[312+i],
					generation[528+i],
					generation[792+i],
					generation[912+i],
					generation[1104+i],
					generation[1176+i],
					generation[1392+i],
					generation[2088+i],
					unserved[192+i],
					// (SUS[CQR-TOL] + SUS[CQR-VAL] + SUS[ANT-CQR] + SUS[SCA-CQR]) * VA_CQR
					model.prod(susceptance[34] + susceptance[35] + susceptance[36] + susceptance[37]
							+ susceptance[38] + susceptance[39] + susceptance[40] + susceptance[0]
							+ susceptance[1] + susceptance[69] + susceptance[70], angle[192+i]),
					model.prod(-susceptance[34], angle[408+i]),		// SUS[CQR-TOL] * VA_TOLIMA
					model.prod(-susceptance[35], angle[408+i]),		// SUS[CQR-TOL] * VA_TOLIMA
					model.prod(-susceptance[36], angle[432+i]),		// SUS[CQR-VAL] * VA_VALLECAU
					model.prod(-susceptance[37], angle[432+i]),		// SUS[CQR-VAL] * VA_VALLECAU
					model.prod(-susceptance[38], angle[432+i]),		// SUS[CQR-VAL] * VA_VALLECAU
					model.prod(-susceptance[39], angle[432+i]),		// SUS[CQR-VAL] * VA_VALLECAU
					model.prod(-susceptance[40], angle[432+i]),		// SUS[CQR-VAL] * VA_VALLECAU
					model.prod(-susceptance[0], angle[0+i]),		// SUS[ANT-CQR] * VA_ANTIOQUI
					model.prod(-susceptance[1], angle[0+i]),		// SUS[ANT-CQR] * VA_ANTIOQUI
					model.prod(-susceptance[69], angle[384+i]), 	// SUS[SCA-CQR] * VA_SANCARLO
					model.prod(-susceptance[70], angle[384+i])};	// SUS[SCA-CQR] * VA_SANCARLO
			
			rng[0][192+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[8][i], "C_CQR_"+i); 
		}
		
		
		/* Balance oferta y demanda GCM
		29	GUAJIR11	GCM	672
		30	GUAJIR21	GCM	696
		56	MJEPIRAC	GCM	1320
		
		41	L42	216	GCM	24	ATLANTIC	TRANSELCA	144.32	3.162	984
		42	L43	216	GCM	24	ATLANTIC	TRANSELCA	213.62	3.543	1008
		43	L44	216	GCM	24	ATLANTIC	TRANSELCA	186.56	3.543	1032
		44	L45	216	GCM	336	NORDESTE	ISA	952.50	4.914	1056
		45	L46	216	GCM	480	CUATRICENTENARIO	ISA	202.86	3.296	1080
		19	L20	72	BOLIVAR	216	GCM	ISA	952.50	4.914	456
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[672+i],
					generation[696+i],
					generation[1320+i],
					unserved[216+i],
					// (SUS[GCM-ATL] + SUS[GCM-NOR] + SUS[GCM-CUA] + SUS[BOL-GCM]) * VA_GCM
					model.prod(susceptance[41] + susceptance[42]  + susceptance[43]  + susceptance[44]
							+ susceptance[45]  + susceptance[19], angle[216+i]),
					model.prod(-susceptance[41], angle[24+i]),	// SUS[GCM-ATL] * VA_ATLANTIC
					model.prod(-susceptance[42], angle[24+i]),	// SUS[GCM-ATL] * VA_ATLANTIC
					model.prod(-susceptance[43], angle[24+i]),	// SUS[GCM-ATL] * VA_ATLANTIC
					model.prod(-susceptance[44], angle[336+i]),	// SUS[GCM-NOR] * VA_NORDESTE
					model.prod(-susceptance[45], angle[480+i]),	// SUS[GCM-CUA] * VA_CUATRICENTENARIO
					model.prod(-susceptance[19], angle[72+i])};	// SUS[BOL-GCM] * VA_BOLIVAR
			
			rng[0][216+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[9][i], "C_GCM_"+i); 
		}
		
		
		/* Balance oferta y demanda HUILACAQ
		4	BETANIA		HUILACAQ	72
		54	MHUILAQ1	HUILACAQ	1272
		
		46	L47	240	HUILACAQ	96	CAUCANAR	ISA	184.00	3.619	1104
		47	L48	240	HUILACAQ	96	CAUCANAR	ISA	184.00	3.619	1128
		48	L49	240	HUILACAQ	96	CAUCANAR	EEB	239.20	4.664	1152
		49	L50	240	HUILACAQ	96	CAUCANAR	EEB	239.20	4.718	1176
		50	L51	240	HUILACAQ	408	TOLIMA	ISA	205.85	3.41	1200
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[72+i],
					generation[1272+i],
					unserved[240+i],
					// (SUS[HUI-CAU] + SUS[HUI-TOL]) * VA_HUILACAQ
					model.prod(susceptance[46] + susceptance[47] + susceptance[48] + susceptance[49] 
							+ susceptance[50], angle[240+i]),
					model.prod(-susceptance[46], angle[96+i]),		// SUS[HUI-CAU] * VA_CAUCANAR
					model.prod(-susceptance[47], angle[96+i]),		// SUS[HUI-CAU] * VA_CAUCANAR
					model.prod(-susceptance[48], angle[96+i]),		// SUS[HUI-CAU] * VA_CAUCANAR
					model.prod(-susceptance[49], angle[96+i]),		// SUS[HUI-CAU] * VA_CAUCANAR
					model.prod(-susceptance[50], angle[408+i])};	// SUS[HUI-TOL] * VA_TOLIMA
			
			rng[0][240+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[10][i], "C_HUILACAQ_"+i); 
		}
		
		
		/* Balance oferta y demanda LAMIEL
		55	MIEL1	LAMIEL	1296
		
		51	L52	264	LAMIEL	48	BOGOTA	ISA	227.24	3.466	1224
		52	L53	264	LAMIEL	48	BOGOTA	ISA	227.24	3.466	1248
		53	L54	264	LAMIEL	408	TOLIMA	ISA	229.08	3.468	1272
		54	L55	264	LAMIEL	408	TOLIMA	ISA	229.08	3.468	1296
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[1296+i],
					unserved[264+i],
					// (SUS[MIE-BOG] + SUS[MIE-TOL]) * VA_LAMIEL
					model.prod(susceptance[51] + susceptance[52] + susceptance[53] + susceptance[54], angle[264+i]),
					model.prod(-susceptance[51], angle[48+i]),		// SUS[MIE-BOG] * VA_BOGOTA
					model.prod(-susceptance[52], angle[48+i]),		// SUS[MIE-BOG] * VA_BOGOTA
					model.prod(-susceptance[53], angle[408+i]),		// SUS[MIE-TOL] * VA_TOLIMA
					model.prod(-susceptance[54], angle[408+i])};	// SUS[MIE-TOL] * VA_TOLIMA
			
			rng[0][264+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[11][i], "C_LAMIEL_"+i); 
		}
		
		
		/* Balance oferta y demanda MAGNAMED
		92	TCENTRO1	MAGNAMED	2184
		96	TSIERRA		MAGNAMED	2280
		
		55	L56	288	MAGDAMED	384	SANCARLO	ISA	1,250.00	5.018	1320
		56	L57	288	MAGDAMED	384	SANCARLO	ISA	186.76	3.329	1344
		57	L58	288	MAGDAMED	336	NORDESTE	ISA	952.50	4.911	1368
		58	L59	288	MAGDAMED	336	NORDESTE	ISA	186.30	3.331	1392
		59	L60	288	MAGDAMED	336	NORDESTE	ISA	186.07	3.209	1416
		60	L61	288	MAGDAMED	336	NORDESTE	ISA	225.17	3.535	1440
		7	L8	0	ANTIOQUI	288	MAGDAMED	ISA	225.86	3.5	168
		8	L9	0	ANTIOQUI	288	MAGDAMED	ISA	176.64	3.276	192
		11	L12	48	BOGOTA	288	MAGDAMED	ISA	952.50	4.831	264
		12	L13	48	BOGOTA	288	MAGDAMED	ISA	226.32	3.468	288
		13	L14	48	BOGOTA	288	MAGDAMED	ISA	226.32	3.468	312
		24	L25	120	CERROMAT	288	MAGDAMED	ISA	1,250.00	5.004	576
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[2184+i],
					generation[2280+i],
					unserved[288+i],
					// (SUS[MAG-SCA] + SUS[MAG-NOR] + SUS[ANT-MAG] + SUS[BOG-MAG] + SUS[CER-MAG]) * VA_MAGNAMED
					model.prod(susceptance[55] + susceptance[56] + susceptance[57] + susceptance[58] + susceptance[59]
							+ susceptance[60] + susceptance[7] + susceptance[8] + susceptance[11] + susceptance[12]
							+ susceptance[13] + susceptance[24], angle[288+i]),
					model.prod(-susceptance[55], angle[384+i]),	// SUS[MAG-SCA] * VA_SANCARLO
					model.prod(-susceptance[56], angle[384+i]),	// SUS[MAG-SCA] * VA_SANCARLO
					model.prod(-susceptance[57], angle[336+i]),	// SUS[MAG-NOR] * VA_NORDESTE
					model.prod(-susceptance[58], angle[336+i]),	// SUS[MAG-NOR] * VA_NORDESTE
					model.prod(-susceptance[59], angle[336+i]),	// SUS[MAG-NOR] * VA_NORDESTE
					model.prod(-susceptance[60], angle[336+i]),	// SUS[MAG-NOR] * VA_NORDESTE
					model.prod(-susceptance[7], angle[0+i]),	// SUS[ANT-MAG] * VA_ANTIOQUI
					model.prod(-susceptance[8], angle[0+i]),	// SUS[ANT-MAG] * VA_ANTIOQUI
					model.prod(-susceptance[11], angle[48+i]),	// SUS[BOG-MAG] * VA_BOGOTA
					model.prod(-susceptance[12], angle[48+i]),	// SUS[BOG-MAG] * VA_BOGOTA
					model.prod(-susceptance[13], angle[48+i]),	// SUS[BOG-MAG] * VA_BOGOTA
					model.prod(-susceptance[24], angle[120+i])};	// SUS[CER-MAG] * VA_CERROMAT
			
			rng[0][288+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[12][i], "C_MAGNAMED_"+i); 
		}
		
		
		/* Balance oferta y demanda META
		No tiene oferta
		
		14	L15	48	BOGOTA	312	META	EEB	220.80	4.561	336
		15	L16	48	BOGOTA	312	META	EEB	331.20	4.538	360
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					unserved[312+i],
					// (SUS[BOG-MET]) * VA_META
					model.prod(susceptance[14] + susceptance[15], angle[312+i]),
					model.prod(-susceptance[14], angle[48+i]),	// SUS[BOG-MET] * VA_BOGOTA
					model.prod(-susceptance[15], angle[48+i])};	// SUS[BOG-MET] * VA_BOGOTA
			
			rng[0][312+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[13][i], "C_META_"+i); 
		}
		
		
		/* Balance oferta y demanda NORDESTE
		46	MCIMARR1	NORDESTE	1080
		52	MERILEC1	NORDESTE	1224
		57	MMORRO1		NORDESTE	1344
		58	MMORRO2		NORDESTE	1368
		60	MNORDE1		NORDESTE	1416
		69	MYOPAL1		NORDESTE	1632
		73	PAIPA1		NORDESTE	1728
		74	PAIPA2		NORDESTE	1752
		75	PAIPA3		NORDESTE	1776
		76	PAIPA4		NORDESTE	1800
		77	PALENQ3		NORDESTE	1824
		89	TASAJER1	NORDESTE	2112
		98	TYOPAL2		NORDESTE	2328
		
		61	L62	336	NORDESTE	456	COROZO	CENS	173.19	4.512	1464
		62	L63	336	NORDESTE	456	COROZO	CENS	173.19	4.512	1488
		30	L31	144	CHIVOR	336	NORDESTE	ISA	220.80	3.421	720
		31	L32	144	CHIVOR	336	NORDESTE	ISA	220.80	3.421	744
		44	L45	216	GCM	336	NORDESTE	ISA	952.50	4.914	1056
		57	L58	288	MAGDAMED	336	NORDESTE	ISA	952.50	4.911	1368
		58	L59	288	MAGDAMED	336	NORDESTE	ISA	186.30	3.331	1392
		59	L60	288	MAGDAMED	336	NORDESTE	ISA	186.07	3.209	1416
		60	L61	288	MAGDAMED	336	NORDESTE	ISA	225.17	3.535	1440
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[1080+i],
					generation[1224+i],
					generation[1344+i],
					generation[1368+i],
					generation[1416+i],
					generation[1632+i],
					generation[1728+i],
					generation[1752+i],
					generation[1776+i],
					generation[1800+i],
					generation[1824+i],
					generation[2112+i],
					generation[2328+i],
					unserved[336+i],
					// (SUS[NOR-COR] + SUS[CHI-NOR] + SUS[GCM-NOR] + SUS[MAG-NOR]) * VA_NORDESTE
					model.prod(susceptance[61] + susceptance[62] + susceptance[30] + susceptance[31]
							+ susceptance[44] + susceptance[57] + susceptance[58] + susceptance[59]
							+ susceptance[60], angle[336+i]),
					model.prod(-susceptance[61], angle[456+i]),		// SUS[NOR-COR] * VA_COROZO
					model.prod(-susceptance[62], angle[456+i]),		// SUS[NOR-COR] * VA_COROZO
					model.prod(-susceptance[30], angle[144+i]),		// SUS[CHI-NOR] * VA_CHIVOR
					model.prod(-susceptance[31], angle[144+i]),		// SUS[CHI-NOR] * VA_CHIVOR
					model.prod(-susceptance[44], angle[216+i]),		// SUS[GCM-NOR] * VA_GCM
					model.prod(-susceptance[57], angle[288+i]),		// SUS[MAG-NOR] * VA_MAGDAMED
					model.prod(-susceptance[58], angle[288+i]),		// SUS[MAG-NOR] * VA_MAGDAMED
					model.prod(-susceptance[59], angle[288+i]),		// SUS[MAG-NOR] * VA_MAGDAMED
					model.prod(-susceptance[60], angle[288+i])};	// SUS[MAG-NOR] * VA_MAGDAMED
			
			rng[0][336+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[14][i], "C_NORDESTE_"+i); 
		}
		
		
		/* Balance oferta y demanda PAGUA
		72	PAGUA	PAGUA	1704
		
		63	L64	360	PAGUA	48	BOGOTA	EEB	257.60	3.479	1512
		64	L65	360	PAGUA	48	BOGOTA	EEB	257.60	3.479	1536
		65	L66	360	PAGUA	48	BOGOTA	EEB	220.80	3.471	1560
		66	L67	360	PAGUA	48	BOGOTA	EEB	220.80	3.476	1584
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[1704+i],
					unserved[360+i],
					// (SUS[PAG-BOG]) * VA_PAGUA
					model.prod(susceptance[63] + susceptance[64] + susceptance[65] + susceptance[66], angle[360+i]),
					model.prod(-susceptance[63], angle[48+i]), 	// SUS[PAG-BOG] * VA_BOGOTA
					model.prod(-susceptance[64], angle[48+i]),	// SUS[PAG-BOG] * VA_BOGOTA
					model.prod(-susceptance[65], angle[48+i]),	// SUS[PAG-BOG] * VA_BOGOTA
					model.prod(-susceptance[66], angle[48+i])};	// SUS[PAG-BOG] * VA_BOGOTA
			
			rng[0][360+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[15][i], "C_PAGUA_"+i); 
		}
		
		
		/* Balance oferta y demanda SANCARLO
		87	SANCARLOS	SANCARLO	2064
		
		67	L68	384	SANCARLO	48	BOGOTA	ISA	222.64	3.487	1608
		68	L69	384	SANCARLO	48	BOGOTA	ISA	222.64	3.487	1632
		69	L70	384	SANCARLO	192	CQR	ISA	223.10	3.441	1656
		70	L71	384	SANCARLO	192	CQR	ISA	223.10	3.441	1680
		71	L72	384	SANCARLO	432	VALLECAU	ISA	1,000.00	5.224	1704
		2	L3	0	ANTIOQUI	384	SANCARLO	ISA	223.33	3.421	48
		3	L4	0	ANTIOQUI	384	SANCARLO	ISA	223.33	3.421	72
		4	L5	0	ANTIOQUI	384	SANCARLO	ISA	265.42	3.535	96
		5	L6	0	ANTIOQUI	384	SANCARLO	ISA	265.42	3.535	120
		6	L7	0	ANTIOQUI	384	SANCARLO	ISA	952.50	5.138	144
		55	L56	288	MAGDAMED	384	SANCARLO	ISA	1,250.00	5.018	1320
		56	L57	288	MAGDAMED	384	SANCARLO	ISA	186.76	3.329	1344
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[2064+i],
					unserved[384+i],
					// (SUS[SCA-BOG] + SUS[SCA-CQR] + SUS[SCA-VAL] + SUS[ANT-SCA] + SUS[MAG-SCA]) * VA_SANCARLO
					model.prod(susceptance[67] + susceptance[68] + susceptance[69] + susceptance[70] + susceptance[71]
							+ susceptance[2] + susceptance[3] + susceptance[4] + susceptance[5] + susceptance[6]
							+ susceptance[55] + susceptance[56], angle[384+i]),
					model.prod(-susceptance[67], angle[48+i]),		// SUS[SCA-BOG] * VA_BOGOTA
					model.prod(-susceptance[68], angle[48+i]),		// SUS[SCA-BOG] * VA_BOGOTA
					model.prod(-susceptance[69], angle[192+i]),		// SUS[SCA-CQR] * VA_CQR
					model.prod(-susceptance[70], angle[192+i]),		// SUS[SCA-CQR] * VA_CQR
					model.prod(-susceptance[71], angle[432+i]),		// SUS[SCA-VAL] * VA_VALLECAU
					model.prod(-susceptance[2], angle[0+i]),		// SUS[ANT-SCA] * VA_ANTIOQUI
					model.prod(-susceptance[3], angle[0+i]),		// SUS[ANT-SCA] * VA_ANTIOQUI
					model.prod(-susceptance[4], angle[0+i]),		// SUS[ANT-SCA] * VA_ANTIOQUI
					model.prod(-susceptance[5], angle[0+i]),		// SUS[ANT-SCA] * VA_ANTIOQUI
					model.prod(-susceptance[6], angle[0+i]),		// SUS[ANT-SCA] * VA_ANTIOQUI
					model.prod(-susceptance[55], angle[288+i]),		// SUS[MAG-SCA] * VA_MAGDAMED
					model.prod(-susceptance[56], angle[288+i])};	// SUS[MAG-SCA] * VA_MAGDAMED
			
			rng[0][384+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[16][i], "C_SANCARLO_"+i); 
		}
		
		
		/* Balance oferta y demanda TOLIMA
		49	MCURRUCU	TOLIMA	1152
		61	MPRADO4		TOLIMA	1440
		66	MTOLIMA1	TOLIMA	1560
		82	PRADO		TOLIMA	1944
		95	TPIEDRAS	TOLIMA	2256
		
		72	L73	408	TOLIMA	48	BOGOTA	ISA	222.18	3.469	1728
		73	L74	408	TOLIMA	48	BOGOTA	ISA	222.18	3.469	1752
		74	L75	408	TOLIMA	48	BOGOTA	ISA	218.96	3.463	1776
		75	L76	408	TOLIMA	48	BOGOTA	ISA	218.96	3.463	1800
		34	L35	192	CQR	408	TOLIMA	ISA	206.08	3.485	816
		35	L36	192	CQR	408	TOLIMA	ISA	184.00	3.494	840
		50	L51	240	HUILACAQ	408	TOLIMA	ISA	205.85	3.41	1200
		53	L54	264	LAMIEL	408	TOLIMA	ISA	229.08	3.468	1272
		54	L55	264	LAMIEL	408	TOLIMA	ISA	229.08	3.468	1296
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[1152+i],
					generation[1440+i],
					generation[1560+i],
					generation[1944+i],
					generation[2256+i],
					unserved[408+i],
					// (SUS[TOL-BOG] + SUS[CQR-TOL] + SUS[HUI-TOL] + SUS[MIE-TOL]) * VA_TOLIMA
					model.prod(susceptance[72] + susceptance[73] + susceptance[74] + susceptance[75]
							+ susceptance[34] + susceptance[35] + susceptance[50] + susceptance[53]
							+ susceptance[54], angle[408+i]),
					model.prod(-susceptance[72], angle[48+i]),		// SUS[TOL-BOG] * VA_BOGOTA
					model.prod(-susceptance[73], angle[48+i]),		// SUS[TOL-BOG] * VA_BOGOTA
					model.prod(-susceptance[74], angle[48+i]),		// SUS[TOL-BOG] * VA_BOGOTA
					model.prod(-susceptance[75], angle[48+i]),		// SUS[TOL-BOG] * VA_BOGOTA
					model.prod(-susceptance[34], angle[192+i]),		// SUS[CQR-TOL] * VA_CQR
					model.prod(-susceptance[35], angle[192+i]),		// SUS[CQR-TOL] * VA_CQR
					model.prod(-susceptance[50], angle[240+i]),		// SUS[HUI-TOL] * VA_HUILACAQ
					model.prod(-susceptance[53], angle[264+i]),		// SUS[MIE-TOL] * VA_LAMIEL
					model.prod(-susceptance[54], angle[264+i])};	// SUS[MIE-TOL] * VA_LAMIEL
			
			rng[0][408+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[17][i], "C_TOLIMA_"+i); 
		}
		
		
		/* Balance oferta y demanda VALLECAU
		1	ALBAN		VALLECAU	0
		5	CALIMA1		VALLECAU	96
		9	CSANCARLOS	VALLECAU	192
		13	CVALLEC1	VALLECAU	288
		51	MEMCALI		VALLECAU	1200
		67	MTULUA		VALLECAU	1584
		68	MVALLEC1	VALLECAU	1608
		70	M_AMAIME	VALLECAU	1656
		71	M_PROVIDEN	VALLECAU	1680
		86	SALVAJINA	VALLECAU	2040
		94	TEMCALI		VALLECAU	2232
		97	TVALLE		VALLECAU	2304
		
		76	L77	432	VALLECAU	96	CAUCANAR	ISA	230.69	3.489	1824
		77	L78	432	VALLECAU	96	CAUCANAR	ISA	230.69	3.511	1848
		36	L37	192	CQR	432	VALLECAU	ISA	230.00	3.488	864
		37	L38	192	CQR	432	VALLECAU	ISA	230.00	3.488	888
		38	L39	192	CQR	432	VALLECAU	ISA	226.55	3.459	912
		39	L40	192	CQR	432	VALLECAU	ISA	226.55	3.459	936
		40	L41	192	CQR	432	VALLECAU	ISA	230.00	3.432	960
		71	L72	384	SANCARLO	432	VALLECAU	ISA	1,000.00	5.224	1704
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[0+i],
					generation[96+i],
					generation[192+i],
					generation[288+i],
					generation[1200+i],
					generation[1584+i],
					generation[1608+i],
					generation[1656+i],
					generation[1680+i],
					generation[2040+i],
					generation[2232+i],
					generation[2304+i],
					unserved[432+i],
					// (SUS[VAL-CAU] + SUS[CQR-VAL] + SUS[SCA-VAL]) * VA_VALLECAU
					model.prod(susceptance[76] + susceptance[77] + susceptance[36]
							+ susceptance[37] + susceptance[38] + susceptance[39]
							+ susceptance[40] + susceptance[71], angle[432+i]),
					model.prod(-susceptance[76], angle[96+i]),	// SUS[VAL-CAU] * VA_CAUCANAR
					model.prod(-susceptance[77], angle[96+i]),	// SUS[VAL-CAU] * VA_CAUCANAR
					model.prod(-susceptance[36], angle[192+i]),	// SUS[CQR-VAL] * VA_CQR
					model.prod(-susceptance[37], angle[192+i]),	// SUS[CQR-VAL] * VA_CQR
					model.prod(-susceptance[38], angle[192+i]),	// SUS[CQR-VAL] * VA_CQR
					model.prod(-susceptance[39], angle[192+i]),	// SUS[CQR-VAL] * VA_CQR
					model.prod(-susceptance[40], angle[192+i]),	// SUS[CQR-VAL] * VA_CQR
					model.prod(-susceptance[71], angle[384+i])};	// SUS[SCA-VAL] * VA_SANCARLO
			
			rng[0][432+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[18][i], "C_VALLECAU_"+i); 
		}
		
		
		/* Balance oferta y demanda COROZO
		8	COROZO1	COROZO	168
		
		61	L62	336	NORDESTE	456	COROZO	CENS	173.19	4.512	1464
		62	L63	336	NORDESTE	456	COROZO	CENS	173.19	4.512	1488
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[168+i],
					unserved[456+i],
					// (SUS[NOR-COR]) * VA_COROZO
					model.prod(susceptance[61] + susceptance[62], angle[456+i]),
					model.prod(-susceptance[61], angle[336+i]),		// SUS[VAL-CAU] * VA_NORDESTE
					model.prod(-susceptance[62], angle[336+i])};	// SUS[VAL-CAU] * VA_NORDESTE
			
			rng[0][456+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[19][i], "C_COROZO_"+i); 
		}
		
		
		/* Balance oferta y demanda CUATRICENTENARIO
		100	VENEZUE1	CUATRICENTENARIO	2376
		
		45	L46	216	GCM	480	CUATRICENTENARIO	ISA	202.86	3.296	1080
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[2376+i],
					unserved[480+i],
					// (SUS[GCM-CUA]) * VA_CUATRICENTENARIO
					model.prod(susceptance[45], angle[480+i]),
					model.prod(-susceptance[45], angle[216+i])};	// SUS[GCM-CUA] * VA_GCM
			
			rng[0][480+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[20][i], "C_CUATRICENTENARIO_"+i); 
		}
		
		
		/* Balance oferta y demanda ECUADOR220
		15	ECUADOR11	ECUADOR220	336
		16	ECUADOR12	ECUADOR220	360
		17	ECUADOR13	ECUADOR220	384
		18	ECUADOR14	ECUADOR220	408
		19	ECUADOR21	ECUADOR220	432
		20	ECUADOR22	ECUADOR220	456
		21	ECUADOR23	ECUADOR220	480
		22	ECUADOR24	ECUADOR220	504
		
		20	L21	96	CAUCANAR	504	ECUADOR	ISA	242.19	3.452	480
		21	L22	96	CAUCANAR	504	ECUADOR	ISA	242.19	3.452	504
		22	L23	96	CAUCANAR	504	ECUADOR	EEB	244.26	3.472	528
		23	L24	96	CAUCANAR	504	ECUADOR	EEB	244.26	3.472	552
		*/
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[336+i],
					generation[360+i],
					generation[384+i],
					generation[408+i],
					generation[432+i],
					generation[456+i],
					generation[480+i],
					generation[504+i],
					unserved[504+i],
					// (SUS[CAU-ECU]) * VA_ECUADOR220
					model.prod(susceptance[20] + susceptance[21] + susceptance[22] + susceptance[23], angle[504+i]),
					model.prod(-susceptance[20], angle[96+i]),	// SUS[CAU-ECU] * VA_CAUCANAR
					model.prod(-susceptance[21], angle[96+i]),	// SUS[CAU-ECU] * VA_CAUCANAR
					model.prod(-susceptance[22], angle[96+i]),	// SUS[CAU-ECU] * VA_CAUCANAR
					model.prod(-susceptance[23], angle[96+i])};	// SUS[CAU-ECU] * VA_CAUCANAR
			
			rng[0][504+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[21][i], "C_ECUADOR220_"+i); 
		}
		
	}
}