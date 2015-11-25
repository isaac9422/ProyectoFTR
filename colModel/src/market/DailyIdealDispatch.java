package market;

//import agents.Operator;
//import jxl.write.WritableSheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument.Content;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.DateUtilities;
import org.jfree.ui.RefineryUtilities;

import utilities.Global;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import agents.Operator;
import ilog.concert.*;
import ilog.cplex.*;

public class DailyIdealDispatch {
	
	//
	// Funci�n para obtener la soluci�n del problema de optimizaci�n del despacho
	// Par�metros: array de variables, array de restricciones, strings para la primera, segunda y tercera variable 
	// de optimizaci�n (generaci�n, �ngulos de voltaje y demanda no atendida respectivamente), n�mero de iteraci�n,
	// n�mero de unidades de generaci�n, n�mero de nodos, n�mero de l�neas de transmisi�n, par�metro para las funciones de lectura
	// y escritura, vectores para el l�mte de generaci�n, las ofertas de potencia y energ�a, el costo de la demanda no atendida, la
	// demanda, la susceptancia, el limite de flujo de potencia, los flujos por las l�neas, los l�mites inferior y superior para los 
	// �ngulos de voltaje y la demanda no atendida, adem�s de par�metros para acceder a las funciones de Operador y ArrayFactory. 
	//
	public static Dispatch dispatch(IloNumVar[][] var, IloNumVar[][] varF, IloRange[][]  rng, IloRange[][]  rngF, String[] var1, String[] var2, String[] var3,
			int iteration, double [] lowPowerLimit, double [][] powerBid, 
			double [][] powerBidPrice, double [] unsDemandCost, double [][] powerDemandVector, double [] susceptance, 
			double [] powerLimit, double[] flows, double [] flowLb, double [] flowUb, double [] unservedLb, 
			double [] unservedUb, Operator operator, CsvWriter writer) throws NumberFormatException, IOException
	{
		try {
			// Create the modeler/solver object
	        IloCplex cplex = new IloCplex();
	        	        
	        //double [] generation = new double[Global.nUnits*24];
	        //double [] generation = new double[Global.nUnits*24];
	        
	        // Establecer los par�metros de la optimizaci�n [OPCIONAL]
	        //cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Auto);
	        //IloCplex.IntParam.
	        
	        //
	        // Obtenci�n de la soluci�n del despacho
	        //
	        populateByRow(cplex, var, rng, var1, var2, var3, lowPowerLimit,
	        		Global.factory.mat2vec(powerBid,Global.nUnits,24), Global.factory.mat2vec(powerBidPrice,Global.nUnits,24),
	        		unsDemandCost, powerDemandVector, susceptance, powerLimit, flowLb, flowUb, unservedLb, unservedUb);
	        
	        // Escribir el problema de optimizaci�n en un archivo .txt
	        cplex.exportModel(Global.directoryResults + "/idealDispatch.lp");
	        
	        // Si hay soluci�n entonces mostrar los resultados
	        if ( cplex.solve() ) {
	        	//
	        	// Resutados de la optimizaci�n
	        	//
	            operator.getIdealDispatch().setDispatchCost(cplex.getObjValue()); // valor de la funci�n objetivo	            
	            operator.getIdealDispatch().setGeneration(Global.factory.vec2mat(cplex.getValues(var[0]),Global.nUnits,24)); // generaci�n por unidad por hora
	            //operator.getIdealDispatch().setVoltajeAngles(Global.factory.vec2mat(cplex.getValues(var[1]),Global.nNodes,24)); // angulo de voltaje por nodo por hora
	            
	            //generation = cplex.getValues(var[0]);
	            
	            //operator.getIdealDispatch().setUnservedDemand(Global.factory.vec2mat(cplex.getValues(var[2]),Global.nNodes,24)); // demanda no atendida por nodo por hora
	            operator.getIdealDispatch().setUnservedDemand(Global.factory.vec2mat(cplex.getValues(var[1]),Global.nNodes,24)); // demanda no atendida por nodo por hora
	            //operator.getIdealDispatch().setNodalPrices(Global.factory.vec2mat(cplex.getDuals(rng[0]),Global.nNodes,24)); // valor de los precios duales por nodo por hora
	            
	            operator.getIdealDispatch().setNodalPrices(Global.factory.repMat(cplex.getDuals(rng[0]), Global.nNodes));
	            
	            //Global.rw.printVector(cplex.getDuals(rng[0]));
	            // Otras variabes obtenidas de la optimizaci�n
	            // double[] dj1    = cplex.getReducedCosts(var[0]);	// costos reducidos
	            // double[] dj2    = cplex.getReducedCosts(var[1]);	// costos reducidos
	            // double[] dj3    = cplex.getReducedCosts(var[2]);	// costos reducidos
	            // double[] slack1 = cplex.getSlacks(rng[0]); // slacks
	           
	            //
	            // Determinaci�n de los flujos de energ�a
	            //           
	            // Flujos
	            //		1			2			3			4			5			6			7			8			9			10			11
	            // flowCar1Car2 flowCar1Ant flowCar2Nord flowCar2Ven flowNordVen flowAntNord flowAntOri flowOriNord flowAntSur flowSurOri flowSurEcu
	            //
	            /*
	            flowCar1Car2 + flowCar1Ant 									= genCar1 - demCar1	------ CARIBE1 	 ------
	            flowCar1Car2 + flowCar2Nord + flowCar2Ven 					= genCar2 - demCar2 ------ CARIBE2 	 ------
	            flowCar1Ant  + flowAntNord  + flowAntOri  + flowAntSur		= genAnt  - demAnt	------ ANTIOQUIA ------
	            flowAntSur   + flowSurOri   + flowSurEcu 					= genSur  - demSur  ------ SUROCCIDE ------
	            flowCar2Nord + flowAntNord  + flowNordVen + flowOriNord		= genNord - demNord ------ NORDESTE  ------
	            flowAntOri   + flowOriNord  + flowSurOri					= genOri  - demOri  ------ ORIENTAL  ------
	            flowCar2Ven  + flowNordVen									= genVen  - demVen  ------ VENEZUELA ------
	            flowSurEcu													= genEcu  - demEcu  ------ ECUADOR   ------       
	            */
	            /*
	            double [][] A = {
    					{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
    					{1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
    					{0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0},
    					{1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0},
    					{0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0},
    					{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0},
    					{0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
    					{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}
    				};
	            
	             
	             
	            */
	            
	            
	            //
	            // Estructuraci�n del vector de flujos de potencia por las l�neas de transmisi�n
	            //
	            /*double[] y = cplex.getValues(var[1]);
	            for (int i = 0; i < 24; ++i) {
		              flows[i]		= susceptance[0]*y[i] - susceptance[0]*y[24 + i];
		              flows[24 + i]	= susceptance[1]*y[i] - susceptance[1]*y[48 + i];
		              flows[48 + i]	= susceptance[2]*y[24 + i] - susceptance[2]*y[48 + i];
	            }
	           */
	            //
	            // Resultados adicionales
	            //
	            //operator.getIdealDispatch().setFlows(Global.factory.vec2mat(flows,Global.nLines,24)); // flujos por las l�neas de transmisi�n para cada hora
	            //operator.getIdealDispatch().setPowerDemand(powerDemandVector); // guardar la demanda para cada hora
	            
	            //
	            // Imprimir los resultados en consola
	            //
	           //System.out.println("Generaci�n");
	           // Global.rw.printGeneration(operator.getIdealDispatch().getGeneration());
	            /*
	            System.out.println("�ngulos de voltaje");
	            Global.rw.printDoubleMatrix(operator.getIdealDispatch().getVoltageAngles());
	            System.out.println("Precios nodales");
	            Global.rw.printDoubleMatrix(operator.getIdealDispatch().getNodalPrices());
	            System.out.println("Flujos de potencia");
	            Global.rw.printDoubleMatrix(operator.getIdealDispatch().getFlows());
	            System.out.println("Demanda no atendida");
	            Global.rw.printDoubleMatrix(operator.getIdealDispatch().getUnservedDemand());
	            System.out.println("Costo del despacho\t " + operator.getIdealDispatch().getDispatchCost());     
	            */
	            //
	            // Escritura de los reultados en un archivo .csv
	            //
	            Global.rw.writeCsv(iteration, writer, operator.getIdealDispatch());
	            //Global.rw.writeCsv(writer, cplex.getDuals(rng[0]),24);
	           
	            /*
	            XYSeries seriesSpotPrice = new XYSeries(iteration+1, false, true);
	            for(int h = 0; h<24; h++){
	            	seriesSpotPrice.add(DateUtilities.createDate(2010, 1, 1, h+1, 0).getTime(),cplex.getDuals(rng[0])[h]);
	            	//series.add(h,cplex.getDuals(rng[0])[h]);
	            }
	            
	           Global.dataset.removeAllSeries();
	           Global.dataset.addSeries(seriesSpotPrice);
	           Global.stepGraphic.setDataset(Global.dataset);
	           
	           /*
	           XYSeries seriesGeneration = new XYSeries(iteration+1, false, true);
	           XYLineAndShapeRenderer rendererScatter = new XYLineAndShapeRenderer();
	           rendererScatter.setSeriesLinesVisible(0, false);
	           XYPlot plotScatter = Global.scatterChart.getXYPlot(); // Se obtiene una referencia a la gr�fica para futuras modificaciones
	           // change the auto tick unit selection to integer units only...
    	       final NumberAxis rangeAxis = (NumberAxis) plotScatter.getRangeAxis();
    	       rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	
	           
	           for(int unit = 0; unit < Global.nUnits; unit++)
	           {
		           for(int h = 0; h < 24; h++)
		           {
		        	   if(operator.getIdealDispatch().getGeneration()[unit][h] >= 50)
		        	   {
		        		   seriesGeneration.add(operator.getIdealDispatch().getGeneration()[unit][h],operator.getIdealDispatch().getGeneration()[unit][h]);
		        		   rendererScatter.setSeriesPaint(0, Color.red);
		        	   }
		        	   else 
		        	   {
		        		   seriesGeneration.add(operator.getIdealDispatch().getGeneration()[unit][h],operator.getIdealDispatch().getGeneration()[unit][h]);
		        		   rendererScatter.setSeriesPaint(0, Color.blue);
		        	   }
		        	   plotScatter.setRenderer(rendererScatter);
		           }
	           }
	           
	           Global.datasetScatter.addSeries(seriesGeneration);
	           Global.scatterGraphic.setDataset(Global.datasetScatter);
	           */
	           
	        }
	        else {
	        	System.out.println("El problema de optimizaci�n no tiene soluci�n");
	        }
	        
	        // Cerrar el bloque Cplex
	        cplex.end();
	        
	       /*
	        
	        IloCplex cplex1 = new IloCplex();
            populateByRow(cplex1, varF, rngF, var2, generation, powerDemandVector, flowLb, flowUb);
            
            // Escribir el problema de optimizaci�n en un archivo .txt
            cplex1.exportModel("idealDispatchFlows.lp");
            
	        // Si hay soluci�n entonces mostrar los resultados
	        if ( cplex1.solve() ) {
	        	operator.getIdealDispatch().setFlows(Global.factory.vec2mat(cplex1.getValues(varF[0]),Global.nLines,24)); // angulo de voltaje por nodo por hora
	        	System.out.println(2);
	        	System.out.println("Flujos de potencia");
	            Global.rw.printDoubleMatrix(operator.getIdealDispatch().getFlows());
	        }
	        
	        cplex1.end();*/
	     }
	     catch (IloException e) {
	        System.err.println("Ideal dispatch: concert exception '" + e + "' caught");
	     }
		
		// Retorna un objeto con los vectores de los resultados
		return operator.getIdealDispatch();
	}
	
	//
	// Funci�n para definir la estructura del problema de optimizaci�n
	//
	static void populateByRow(IloMPModeler model, IloNumVar[][] var, IloRange[][] rng, String[] var1, String[] var2, String[] var3,
			double [] lowPowerLimit, double [] powerBid, double [] powerBidPrice, double [] unsDemandCost, double [][] powerDemandVector, 
			double [] susceptance, double [] powerLimit, double [] flowLb, double [] flowUb, double [] unservedLb, double [] unservedUb) throws IloException {
		
		// Variables  --------------------------------------------------------------------------------
		IloNumVar[] generation  = model.numVarArray(24*Global.nUnits, lowPowerLimit, powerBid, var1); 	// progrmaci�n de la generaci�n
		//IloNumVar[] angle       = model.numVarArray(24*Global.nNodes, angleLb, angleUb, var2); 			// �ngulos de voltaje
		IloNumVar[] unserved    = model.numVarArray(24*Global.nNodes, unservedLb, unservedUb, var3); 	// demanda no atendida
		//IloNumVar[] flows       = model.numVarArray(24*Global.nLines, flowLb, flowUb, var2); 	// demanda no atendida
		//var[0] = generation; 	// generaci�n
		//var[1] = angle; 		// �ngulos de voltaje
		//var[2] = unserved; 		// demanda no atendida
		
		var[0] = generation; 	// generaci�n
		//var[1] = flows; 		// flujos
		var[1] = unserved; 		// demanda no atendida
		 
		//
		// Funci�n objetivo
		//
		// Minimizar el costo de generaci�n
		//model.addMinimize(model.scalProd(generation, powerBidPrice)); 
		
		// Minimizar el costo de generaci�n y el costo por demanda no atendida
		model.addMinimize(model.sum(model.scalProd(generation, powerBidPrice), model.scalProd(unserved, unsDemandCost))); 
		
		//
		// Restricciones  --------------------------------------------------------------------------------
		//
		/*
		 * Unidades de generaci�n por nodo
		 
		Caribe1			URRA1		URRA2		URRA3		URRA4																																											
		Caribe2			FLORES1		FLORES21	FLORES3		BARRANQ3	BARRANQ4	
						GUAJIR11	GUAJIR21	TEBSA		MELBOSQUE	PROELEC1
						PROELEC2	TCANDEL1	TCANDEL2 	MJEPIRACHI																																										
		Nordeste		MERILEC1--	PALENQ3--	MNORDE1--   MMORRO1--	MMORRO2--	
						PAIPA1		PAIPA2		PAIPA3		PAIPA4		TCENTRO_CC-	
						TYOPAL2		MYOPAL1																																		
		Antioquia-Choc�	GUATAPE1	GUATAPE2	GUATAPE3	GUATAPE4	GUATAPE5	
						GUATAPE6	GUATAPE7	GUATAPE8	GUADAL31	GUADAL32	
						GUADAL33	GUADAL34	GUADAL35	GUADAL36	GUADAL41	
						GUADAL42	GUADAL43	TRONERA1	TRONERA2	LATASAJ1	
						LATASAJ2	LATASAJ3	PLAYAS1		PLAYAS2		PLAYAS3	
						PORCE21		PORCE22		PORCE23		TSIERRA--	MANTIOQ1	
						JAGUAS1		JAGUAS2		SANCARL1	SANCARL2	SANCARL3	
						SANCARL4	SANCARL5	SANCARL6	SANCARL7	SANCARL8	
						MCALDERAS1-	MCALDERAS2-	TASAJER1																																																	
		Oriental		CHIVOR1		CHIVOR2		CHIVOR3		CHIVOR4		CHIVOR5	
						CHIVOR6		CHIVOR7		CHIVOR8		MCUNDINAM1-	GUAVIO1	
						GUAVIO2		GUAVIO3		GUAVIO4		GUAVIO5		LAGUACA1
						LAGUACA2	LAGUACA3	PARAISO1	PARAISO2	PARAISO3
						ZIPAEMG2--	ZIPAEMG3--	ZIPAEMG4--	ZIPAEMG5-- 	MBOGOTA1													
		Suroccidental	DORADA1		ESMERAL1	ESMERAL2	INSULA1		INSULA2	
						INSULA3		MCQR1		SANFRAN1	SANFRAN2	SANFRAN3
						MIEL11		MIEL12		MIEL13		MTULUA		MEMCALI--
						ALTANCHI1	ALTANCHI2	ALTANCHI3	BAJANCHI1	BAJANCHI2	
						BAJANCHI3	BAJANCHI4	CALIMA11	CALIMA12	CALIMA13	
						CALIMA14	SALVAJI1	SALVAJI2	SALVAJI3	TVALLE1	
						TVALLE2		MVALLEC1	TEMCALI		MCAUCAN1	MRIOMAYO1	
						MRIOMAYO2	MRIOMAYO3	BETANIA1	BETANIA2	BETANIA3	
						FLORID21--	FLORID22--	MCAUCAN2	PRADO1		PRADO2
						PRADO3		MPRADO4--	MTOLIMA1	MHUILAQ1																																					
		Ecuador																																																								
		Venezuela		COROZO1																																										
						VENEZUE1	
		
		//
		// Faltantes: 	MAGUAFRE	MCIMARR1	M_PROVIDEN	CTGEMG1		CTGEMG2	
		// 				CTGEMG3		MSANTANA	RPIEDRAS	MCASCADA1	TPIEDRAS1
		//
		*/
		
		//
		// Balanace de oferta y demanda CARIBE1
		//
		// GP_URRA1 + GP_URRA2 + GP_URRA3 + GP_URRA4 + UD_CARIBE1 
		// + (SUS[CAR1-CAR2] + SUS[ANT-CAR1]) * VA_CARIBE1
		// - SUS[CAR1-CAR2] * VA_CARIBE2
		// - SUS[ANT-CAR1] * VA_ANTIOQUIA
		// >= DEM[CARIBE1]
			
		for (int i = 0; i < 24; i++){
			IloNumExpr[] supply = {
					generation[0+i],
					generation[24+i],
					generation[48+i],
					generation[72+i],
					generation[96+i],
					generation[120+i],
					generation[144+i],
					generation[168+i],
					generation[192+i],
					generation[216+i],
					generation[240+i],
					generation[264+i],
					generation[288+i],
					generation[312+i],
					generation[336+i],
					generation[360+i],
					generation[384+i],
					generation[408+i],
					generation[432+i],
					generation[456+i], 
					generation[480+i], 
					generation[504+i], 
					generation[528+i], 
					generation[552+i], 
					generation[576+i],
					generation[600+i], 
					generation[624+i], 
					generation[648+i], 
					generation[672+i], 
					generation[696+i], 
					generation[720+i], 
					generation[744+i], 
					generation[768+i], 
					generation[792+i], 
					generation[816+i], 
					generation[840+i], 
					generation[864+i], 
					generation[864+i], 
					generation[888+i], 
					generation[912+i], 
					generation[936+i], 
					generation[960+i], 
					generation[984+i], 
					generation[1008+i], 
					generation[1032+i], 
					generation[1056+i], 
					generation[1080+i],
					generation[1104+i],
					generation[1128+i],
					generation[1152+i],
					generation[1176+i],
					generation[1200+i],
					generation[1224+i],
					generation[1248+i],
					generation[1272+i],
					generation[1296+i],
					generation[1320+i],
					generation[1344+i], 
					generation[1368+i], 
					generation[1392+i], 
					generation[1416+i], 
					generation[1440+i], 
					generation[1464+i], 
					generation[1488+i], 
					generation[1512+i],
					generation[1536+i],
					generation[1560+i],
					generation[1584+i],
					generation[1608+i], 
					generation[1632+i], 
					generation[1656+i], 
					generation[1680+i],
					generation[1704+i],
					generation[1728+i], 
					generation[1752+i], 
					generation[1776+i], 
					generation[1800+i], 
					generation[1824+i], 	
					generation[1848+i], 
					generation[1872+i], 
					generation[1896+i], 
					generation[1920+i], 	
					generation[1944+i], 	
					generation[1968+i], 
					generation[1992+i], 
					generation[2016+i],   	 
					generation[2040+i], 
					generation[2064+i], 
					generation[2088+i], 
					generation[2112+i], 	
					generation[2136+i], 
					generation[2160+i], 
					generation[2184+i], 
					generation[2208+i],  	
					generation[2232+i], 
					generation[2256+i], 
					generation[2280+i], 
					generation[2304+i], 	
					generation[2328+i], 
					generation[2352+i], 
					generation[2376+i], 
					generation[2400+i],  
					generation[2424+i], 
					generation[2448+i], 
					generation[2472+i],
					unserved[0+i],	
					unserved[24+i],
					unserved[48+i],	
					unserved[72+i],	
					unserved[96+i],	
					unserved[120+i],	
					unserved[144+i],
					unserved[168+i],
					unserved[192+i],
					unserved[216+i],
					unserved[240+i],
					unserved[264+i],
					unserved[288+i],
					unserved[312+i],
					unserved[336+i],
					unserved[360+i],
					unserved[384+i],
					unserved[408+i],
					unserved[432+i],
					unserved[456+i],
					unserved[480+i],
					unserved[504+i]
													
			};
			
			double demand = 
					powerDemandVector[0][i] +
					powerDemandVector[1][i] +
					powerDemandVector[2][i] +
					powerDemandVector[3][i] +
					powerDemandVector[4][i] +
					powerDemandVector[5][i] +
					powerDemandVector[6][i] +
					powerDemandVector[7][i] +
					powerDemandVector[8][i] +
					powerDemandVector[9][i] +
					powerDemandVector[10][i] +
					powerDemandVector[11][i] +
					powerDemandVector[12][i] +
					powerDemandVector[13][i] +
					powerDemandVector[14][i] +
					powerDemandVector[15][i] +
					powerDemandVector[16][i] +
					powerDemandVector[17][i] +
					powerDemandVector[18][i] +
					powerDemandVector[19][i] +
					powerDemandVector[20][i] +
					powerDemandVector[21][i];
			
			rng[0][0+i] = model.addGe(
					model.sum(supply),
					demand,"HORA"+i);
			
		}
		/*
		//double [][] b = new double [Global.nNodes][24];
        //double [] generation = cplex.getValues(var[0]);
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] balCar1 =  	
				{
					generation[456+i], // GP_URRA1			
					generation[480+i], // GP_URRA2
					generation[504+i], // GP_URRA3
					generation[528+i]  // GP_URRA4
				};
			
			IloNumExpr[] balCar2 =  	
				{
					generation[3168+i], // GP_FLORES1
					generation[3192+i], // GP_FLORES21
					generation[3216+i], // GP_FLORES3
					generation[3240+i], // GP_BARRANQ3
					generation[3264+i], // GP_BARRANQ4
					generation[3288+i], // GP_GUAJIR11
					generation[3312+i], // GP_GUAJIR21
					generation[3336+i], // GP_TEBSA
					generation[3072+i], // GP_MELBOSQUE
					generation[2424+i], // GP_PROELEC1
					generation[2448+i], // GP_PROELEC2
					generation[3000+i], // GP_MJEPIRACHI
					generation[3648+i], // GP_TCANDEL1
					generation[3672+i] // GP_TCANDEL2
				};
			
			IloNumExpr[] balAnt =  	
				{
					generation[3720+i], // GP_TASAJER1
					generation[888+i], // GP_GUATAPE1
					generation[912+i], // GP_GUATAPE2
					generation[936+i], // GP_GUATAPE3
					generation[960+i], // GP_GUATAPE4
					generation[984+i], // GP_GUATAPE5
					generation[1008+i], // GP_GUATAPE6
					generation[1032+i], // GP_GUATAPE7
					generation[1056+i], // GP_GUATAPE8
					generation[1080+i], // GP_GUADAL31
					generation[1104+i], // GP_GUADAL32
					generation[1128+i], // GP_GUADAL33
					generation[1152+i], // GP_GUADAL34
					generation[1176+i], // GP_GUADAL35
					generation[1200+i], // GP_GUADAL36
					generation[1224+i], // GP_GUADAL41
					generation[1248+i], // GP_GUADAL42
					generation[1272+i], // GP_GUADAL43
					generation[1296+i], // GP_TRONERA1
					generation[1320+i], // GP_TRONERA2
					generation[1344+i], // GP_LATASAJ1
					generation[1368+i], // GP_LATASAJ2
					generation[1392+i], // GP_LATASAJ3
					generation[1416+i], // GP_PLAYAS1
					generation[1440+i], // GP_PLAYAS2
					generation[1464+i], // GP_PLAYAS3
					generation[1488+i], // GP_PORCE21
					generation[1512+i], // GP_PORCE22
					generation[1536+i], // GP_PORCE23
					generation[2952+i], // GP_TSIERRA
					generation[2976+i], // GP_MANTIOQ1
					generation[2064+i], // GP_JAGUAS1
					generation[2088+i], // GP_JAGUAS2
					generation[2184+i], // GP_SANCARL1
					generation[2208+i], // GP_SANCARL2
					generation[2232+i], // GP_SANCARL3
					generation[2256+i], // GP_SANCARL4
					generation[2280+i], // GP_SANCARL5
					generation[2304+i], // GP_SANCARL6
					generation[2328+i], // GP_SANCARL7
					generation[2352+i], // GP_SANCARL8
					generation[2376+i], // GP_MCALDERAS1
					generation[2400+i] // GP_MCALDERAS2
				};
			
			IloNumExpr[] balSur =  	
				{
					generation[2544+i], // GP_DORADA1
					generation[72+i],   // GP_ESMERAL1
					generation[96+i],   // GP_ESMERAL2
					generation[120+i],  // GP_INSULA1
					generation[144+i],  // GP_INSULA2
					generation[168+i],  // GP_INSULA3
					generation[2568+i], // GP_MCQR1
					generation[192+i],  // GP_SANFRAN1
					generation[216+i],  // GP_SANFRAN2
					generation[240+i],  // GP_SANFRAN3
					generation[2112+i], // GP_MIEL11
					generation[2136+i], // GP_MIEL12
					generation[2160+i], // GP_MIEL13
					generation[2520+i], // GP_MTULUA
					generation[2712+i], // GP_MEMCALI
					generation[1560+i], // GP_ALTANCHI1
					generation[1584+i], // GP_ALTANCHI2	
					generation[1608+i], // GP_ALTANCHI3
					generation[1632+i], // GP_BAJANCHI1
					generation[1656+i], // GP_BAJANCHI2
					generation[1680+i], // GP_BAJANCHI3	
					generation[1704+i], // GP_BAJANCHI4	
					generation[1728+i], // GP_CALIMA11
					generation[1752+i], // GP_CALIMA12
					generation[1776+i], // GP_CALIMA13  	 
					generation[1800+i], // GP_CALIMA14
					generation[1896+i], // GP_SALVAJI1
					generation[1920+i], // GP_SALVAJI2
					generation[1944+i], // GP_SALVAJI3	
					generation[3384+i], // GP_MTOLIMA1
					generation[3528+i], // GP_MHUILAQ1
					generation[1968+i], // GP_TVALLE1
					generation[1992+i], // GP_TVALLE2 	
					generation[3048+i], // GP_MVALLEC1
					generation[3696+i], // GP_TEMCALI
					generation[1824+i], // GP_PRADO1
					generation[1848+i], // GP_PRADO2	
					generation[1872+i], // GP_PRADO3
					generation[3024+i], // GP_MPRADO4
					generation[2496+i], // GP_MCAUCAN1
					generation[0+i],    // GP_MRIOMAYO1 
					generation[24+i],   // GP_MRIOMAYO2
					generation[48+i],   // GP_MRIOMAYO3
					generation[552+i],  // GP_BETANIA1
					generation[576+i],  // GP_BETANIA2 	
					generation[600+i],  // GP_BETANIA3
					generation[2016+i], // GP_FLORID21
					generation[2040+i], // GP_FLORID22
					generation[3360+i]  // GP_MCAUCAN2
				};
			
			IloNumExpr[] balNord =  	
				{
					generation[3624+i], // GP_MERILEC1
					generation[3120+i], // GP_PALENQ3
					generation[3144+i], // GP_MNORDE1
					generation[2616+i], // GP_MMORRO1
					generation[2640+i], // GP_MMORRO2
					generation[3432+i], // GP_PAIPA1
					generation[3456+i], // GP_PAIPA2
					generation[3480+i], // GP_PAIPA3
					generation[3504+i], // GP_PAIPA4
					generation[3552+i], // GP_TCENTRO_CC
					generation[3768+i], // GP_TYOPAL2
					generation[3792+i] // GP_MYOPAL1
				};
			
			IloNumExpr[] balOri =  	
				{
					generation[264+i], // GP_CHIVOR1
					generation[288+i], // GP_CHIVOR2
					generation[312+i], // GP_CHIVOR3
					generation[336+i], // GP_CHIVOR4
					generation[360+i], // GP_CHIVOR5
					generation[384+i], // GP_CHIVOR6
					generation[408+i], // GP_CHIVOR7
					generation[432+i], // GP_CHIVOR8
					generation[2688+i], // GP_MCUNDINAM1
					generation[624+i], // GP_GUAVIO1
					generation[648+i], // GP_GUAVIO2
					generation[672+i], // GP_GUAVIO3
					generation[696+i], // GP_GUAVIO4
					generation[720+i], // GP_GUAVIO5
					generation[744+i], // GP_LAGUACA1
					generation[768+i], // GP_LAGUACA2
					generation[792+i], // GP_LAGUACA3 
					generation[816+i], // GP_PARAISO1
					generation[840+i], // GP_PARAISO2
					generation[864+i], // GP_PARAISO3
					generation[2808+i],// GP_ZIPAEMG2 	
					generation[2832+i],// GP_ZIPAEMG3
					generation[2856+i],// GP_ZIPAEMG4
					generation[2880+i],// GP_ZIPAEMG5
					generation[2904+i] // GP_MBOGOTA1
				};
			
			IloNumExpr[] balVen =  	
				{
					generation[3096+i], // GP_COROZO1
					generation[3576+i] // GP_VENEZUE1
				};
			
						
			//  1    	   2		 3         4        5       6        7       8       9      10       11
			//  0    	   24		 48        72       96     120       144     168     192    216      240
			//CAR1-CAR2 ANT-CAR1 NORD-CAR2 CAR2-VEN NORD-VEN ANT-NORD ANT-ORI ANT-SUR SUR-ORI ORI-NORD SUR-ECU
			
			rng[1][0+i] = model.addEq(
						  		model.sum(flows[0+i], 	// CAR1-CAR2
						  				  flows[24+i],  // ANT-CAR1 
						  				  model.sum(balCar1)),	// suma oferta en CAR1
						  		powerDemandVector[0][i],	// demanda en CAR1
						  		"C_CAR1_"+i); 	
			
			rng[1][24+i] = model.addEq(
						  		model.sum(flows[0+i], 	// CAR1-CAR2
						  				  flows[48+i],  // NORD-CAR2
						  				  flows[72+i],  // CAR2-VEN
						  				  model.sum(balCar2)),	// suma oferta en CAR2
						  		powerDemandVector[1][i],	// demanda en CAR2
						  		"C_CAR2_"+i); 
			
			rng[1][48+i] = model.addEq(
						  		model.sum(flows[24+i], 	 // ANT-CAR1
						  				  flows[120+i],  // ANT-NORD
						  				  flows[144+i],  // ANT-ORI
						  				  flows[168+i],  // ANT-SUR
						  				  model.sum(balAnt)),	// suma oferta en ANT
						  		powerDemandVector[2][i],	// demanda en ANT
						  		"C_ANT_"+i);  
			
			rng[1][72+i] = model.addEq(
						  		model.sum(flows[168+i],  // ANT-SUR
						  				  flows[192+i],  // SUR-ORI
						  				  flows[240+i],  // SUR-ECU
						  				  model.sum(balSur)),	// suma oferta en SUR
						  		powerDemandVector[3][i],	// demanda en SUR
						  		"C_SUR_"+i);
			
			rng[1][96+i] = model.addEq(
						  		model.sum(flows[48+i],   // NORD-CAR2
						  				  flows[96+i],   // NORD-VEN
						  				  flows[120+i],  // ANT-NORD
						  				  flows[216+i],  //ORI-NORD
						  				  model.sum(balNord)),	// suma oferta en NORD
						  		powerDemandVector[4][i],	// demanda en NORD
						  		"C_NORD_"+i);
			
			rng[1][120+i] = model.addEq(
						  		model.sum(flows[144+i],  // ANT-ORI
						  				  flows[192+i],  // SUR-ORI
						  				  flows[216+i],  // ORI-NORD
						  				  model.sum(balOri)),	// suma oferta en ORI
						  		powerDemandVector[5][i],	// demanda en ORI
						  		"C_ORI_"+i);
			
			rng[1][144+i] = model.addEq(
						  		model.sum(flows[72+i],  // CAR2-VEN
						  				  flows[96+i],  // NORD-VEN
						  				  model.sum(balVen)),	// suma oferta en VEN
						  		powerDemandVector[6][i],	// demanda en VEN
						  		"C_VEN_"+i);
			
			rng[1][168+i] = model.addEq(
						  		flows[240+i],  // SUR-ECU
						  		powerDemandVector[7][i],	// demanda en VEN
						  		"C_ECU_"+i);		
		}
				
		/*
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[456+i], // GP_URRA1
					generation[480+i], // GP_URRA2
					generation[504+i], // GP_URRA3
					generation[528+i], // GP_URRA4
					unserved[0+i], 	   // UD_CARIBE1
					model.prod(susceptance[0] + susceptance[1], angle[0+i]), // (SUS[CAR1-CAR2] + SUS[ANT-CAR1]) * VA_CARIBE1
					model.prod(-susceptance[0], angle[24+i]), // SUS[CAR1-CAR2] * VA_CARIBE2
					model.prod(-susceptance[1], angle[48+i])}; // SUS[ANT-CAR1] * VA_ANTIOQUIA
							
			rng[0][0+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[0][i], "C_CARIBE1_"+i);  //  DEM[CARIBE1]
		}
		
		//
		// Balanace de oferta y demanda CARIBE2
		//
		// 		GP_FLORES1 		+ 	GP_FLORES21 	+ 	GP_FLORES3 		+ 	GP_BARRANQ3 
		// + 	GP_BARRANQ4 	+ 	GP_GUAJIR11 	+ 	GP_GUAJIR21 	+ 	GP_TEBSA
		// + 	GP_MELBOSQUE 	+ 	GP_PROELEC1 	+ 	GP_PROELEC2 	+ 	GP_MJEPIRACHI 
		// + 	GP_TCANDEL1 	+ 	GP_TCANDEL2
		// + 	UD_CARIBE2 
		// + 	(SUS[CAR1-CAR2]	+ SUS[NORD-CAR2] + SUS[CAR2-VEN]) * VA_CARIBE2
		// - 	SUS[CAR1-CAR2]	* VA_CARIBE1
		// - 	SUS[NORD-CAR2]	* VA_NORDESTE
		// - 	SUS[CAR2-VEN]	* VA_VENEZUELA
		// >= 	DEM[CARIBE2]
		
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[3168+i], // GP_FLORES1
					generation[3192+i], // GP_FLORES21
					generation[3216+i], // GP_FLORES3
					generation[3240+i], // GP_BARRANQ3
					generation[3264+i], // GP_BARRANQ4
					generation[3288+i], // GP_GUAJIR11
					generation[3312+i], // GP_GUAJIR21
					generation[3336+i], // GP_TEBSA
					generation[3072+i], // GP_MELBOSQUE
					generation[2424+i], // GP_PROELEC1
					generation[2448+i], // GP_PROELEC2
					generation[3000+i], // GP_MJEPIRACHI
					generation[3648+i], // GP_TCANDEL1
					generation[3672+i], // GP_TCANDEL2
					unserved[24+i],		// UD_CARIBE2
					model.prod(susceptance[0] + susceptance[2] + susceptance[3], angle[24+i]), // (SUS[CAR1-CAR2]	+ SUS[NORD-CAR2] + SUS[CAR2-VEN]) * VA_CARIBE2
					model.prod(-susceptance[0], angle[0+i]), 	// SUS[CAR1-CAR2]	* VA_CARIBE1
					model.prod(-susceptance[2], angle[96+i]), 	// SUS[NORD-CAR2]	* VA_NORDESTE
					model.prod(-susceptance[3], angle[144+i])}; // SUS[CAR2-VEN]	* VA_VENEZUELA
			
			rng[0][24+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[1][i], "C_CARIBE2_"+i); 			//  DEM[CARIBE2]
		}
		
		//
		// Balanace de oferta y demanda ANTIOQUIA
		//
		// 		GP_TASAJER1		+ 	GP_GUATAPE1 	+ 	GP_GUATAPE2 	+ 	GP_GUATAPE3 
		// + 	GP_GUATAPE4 	+ 	GP_GUATAPE5 	+ 	GP_GUATAPE6 	+ 	GP_GUATAPE7
		// + 	GP_GUATAPE8 	+ 	GP_GUADAL31 	+ 	GP_GUADAL32 	+ 	GP_GUADAL33 
		// + 	GP_GUADAL34 	+ 	GP_GUADAL35 	+ 	GP_GUADAL36 	+ 	GP_GUADAL41 
		// + 	GP_GUADAL42 	+ 	GP_GUADAL43 	+	GP_TRONERA1 	+	GP_TRONERA2 
		// +	GP_LATASAJ1		+ 	GP_LATASAJ2 	+	GP_LATASAJ3 	+	GP_PLAYAS1 
		// +	GP_PLAYAS2 		+	GP_PLAYAS3 		+ 	GP_PORCE21 		+  	GP_PORCE22 
		// +	GP_PORCE23		+ 	GP_TSIERRA 		+	GP_MANTIOQ1 	+	GP_JAGUAS1
		// + 	GP_JAGUAS2 		+ 	GP_SANCARL1 	+ 	GP_SANCARL2 	+	GP_SANCARL3 
		// +	GP_SANCARL4 	+ 	GP_SANCARL5 	+ 	GP_SANCARL6 	+ 	SANCARL7
		// + 	GP_SANCARL8 	+ 	GP_MCALDERAS1 	+ 	GP_MCALDERAS2
		// + 	UD_ANTIOQUIA  
		// + 	(SUS[ANT-CAR1]	+ SUS[ANT-NORD] + SUS[ANT-ORI] + SUS[ANT-SUR]) * VA_ANTIOQUIA
		// - 	SUS[ANT-CAR1]	* VA_CARIBE1
		// - 	SUS[ANT-NORD]	* VA_NORDESTE
		// - 	SUS[ANT-ORI]	* VA_ORIENTAL
		// - 	SUS[ANT-SUR]	* VA_SUROCCIDENTAL
		// >= 	DEM[ANTIOQUIA]
		
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[3720+i], // GP_TASAJER1
					generation[888+i], // GP_GUATAPE1
					generation[912+i], // GP_GUATAPE2
					generation[936+i], // GP_GUATAPE3
					generation[960+i], // GP_GUATAPE4
					generation[984+i], // GP_GUATAPE5
					generation[1008+i], // GP_GUATAPE6
					generation[1032+i], // GP_GUATAPE7
					generation[1056+i], // GP_GUATAPE8
					generation[1080+i], // GP_GUADAL31
					generation[1104+i], // GP_GUADAL32
					generation[1128+i], // GP_GUADAL33
					generation[1152+i], // GP_GUADAL34
					generation[1176+i], // GP_GUADAL35
					generation[1200+i], // GP_GUADAL36
					generation[1224+i], // GP_GUADAL41
					generation[1248+i], // GP_GUADAL42
					generation[1272+i], // GP_GUADAL43
					generation[1296+i], // GP_TRONERA1
					generation[1320+i], // GP_TRONERA2
					generation[1344+i], // GP_LATASAJ1
					generation[1368+i], // GP_LATASAJ2
					generation[1392+i], // GP_LATASAJ3
					generation[1416+i], // GP_PLAYAS1
					generation[1440+i], // GP_PLAYAS2
					generation[1464+i], // GP_PLAYAS3
					generation[1488+i], // GP_PORCE21
					generation[1512+i], // GP_PORCE22
					generation[1536+i], // GP_PORCE23
					generation[2952+i], // GP_TSIERRA
					generation[2976+i], // GP_MANTIOQ1
					generation[2064+i], // GP_JAGUAS1
					generation[2088+i], // GP_JAGUAS2
					generation[2184+i], // GP_SANCARL1
					generation[2208+i], // GP_SANCARL2
					generation[2232+i], // GP_SANCARL3
					generation[2256+i], // GP_SANCARL4
					generation[2280+i], // GP_SANCARL5
					generation[2304+i], // GP_SANCARL6
					generation[2328+i], // GP_SANCARL7
					generation[2352+i], // GP_SANCARL8
					generation[2376+i], // GP_MCALDERAS1
					generation[2400+i], // GP_MCALDERAS2
					unserved[48+i],		// UD_ANTIOQUIA 
					model.prod(susceptance[1] + susceptance[5] + susceptance[6] + susceptance[7], angle[48+i]), // (SUS[ANT-CAR1]	+ SUS[ANT-NORD] + SUS[ANT-ORI] + SUS[ANT-SUR]) * VA_ANTIOQUIA
					model.prod(-susceptance[1], angle[0+i]), 	// SUS[ANT-CAR1] * VA_CARIBE1
					model.prod(-susceptance[5], angle[96+i]), 	// SUS[ANT-NORD] * VA_NORDESTE
					model.prod(-susceptance[6], angle[120+i]),  // SUS[ANT-ORI]	* VA_ORIENTAL
					model.prod(-susceptance[7], angle[72+i])}; // SUS[ANT-SUR]	* VA_SUROCCIDENTAL
			
			rng[0][48+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[2][i], "C_ANTIOQUIA_"+i); //  DEM[ANTIOQUIA]
		}
		
		
		//
		// Balanace de oferta y demanda SUROCCIDENTAL
		//
		// 		GP_DORADA1		+ 	GP_ESMERAL1 	+ 	GP_ESMERAL2 	+ 	GP_INSULA1 
		// + 	GP_INSULA2	 	+ 	GP_INSULA3	 	+ 	GP_MCQR1	 	+ 	GP_SANFRAN1
		// + 	GP_SANFRAN2 	+ 	GP_SANFRAN3 	+ 	GP_MIEL11 		+ 	GP_MIEL12 
		// + 	GP_MIEL13	 	+ 	GP_MTULUA	 	+ 	GP_MEMCALI	 	+ 	GP_ALTANCHI1 
		// + 	GP_ALTANCHI2 	+ 	GP_ALTANCHI3 	+	GP_BAJANCHI1 	+	GP_BAJANCHI2 
		// +	GP_BAJANCHI3	+ 	GP_BAJANCHI4 	+	GP_CALIMA11 	+	GP_CALIMA12 
		// +	GP_CALIMA13 	+	GP_CALIMA14 	+ 	GP_SALVAJI1		+  	GP_SALVAJI2 
		// +	GP_SALVAJI3		+ 	GP_MTOLIMA1		+	GP_MHUILAQ1 	+	GP_TVALLE1
		// + 	GP_TVALLE2 		+ 	GP_MVALLEC1 	+ 	GP_TEMCALI 		+	GP_PRADO1 
		// +	GP_PRADO2	 	+ 	GP_PRADO3	 	+ 	GP_MPRADO4	 	+ 	GP_MCAUCAN1
		// + 	GP_MRIOMAYO1 	+ 	GP_MRIOMAYO2 	+ 	GP_MRIOMAYO3	+	GP_BETANIA1
		// +	GP_BETANIA2 	+	GP_BETANIA3 	+	GP_FLORID21 	+	GP_FLORID22 
		// +	GP_MCAUCAN2
		// + 	UD_SUROCCIDENTAL  
		// + 	(SUS[ANT-SUR]	+ SUS[SUR-ORI] + SUS[SUR-ECU]) * VA_SUROCCIDENTAL
		// - 	SUS[ANT-SUR]	* VA_ANTIOQUIA
		// - 	SUS[SUR-ORI]	* VA_ORIENTAL
		// - 	SUS[SUR-ECU]	* VA_ECUADOR
		// >= 	DEM[SUROCCIDENTAL]
		
		for (int i = 0; i < 24; i++){
			
			IloNumExpr[] expr = {
					generation[2544+i], // GP_DORADA1
					generation[72+i], // GP_ESMERAL1
					generation[96+i], // GP_ESMERAL2
					generation[120+i], // GP_INSULA1
					generation[144+i], // GP_INSULA2
					generation[168+i], // GP_INSULA3
					generation[2568+i], // GP_MCQR1
					generation[192+i], // GP_SANFRAN1
					generation[216+i], // GP_SANFRAN2
					generation[240+i], // GP_SANFRAN3
					generation[2112+i], // GP_MIEL11
					generation[2136+i], // GP_MIEL12
					generation[2160+i], // GP_MIEL13
					generation[2520+i], // GP_MTULUA
					generation[2712+i], // GP_MEMCALI
					generation[1560+i], // GP_ALTANCHI1
					generation[1584+i], // GP_ALTANCHI2	
					generation[1608+i], // GP_ALTANCHI3
					generation[1632+i], // GP_BAJANCHI1
					generation[1656+i], // GP_BAJANCHI2
					generation[1680+i], // GP_BAJANCHI3	
					generation[1704+i], // GP_BAJANCHI4	
					generation[1728+i], // GP_CALIMA11
					generation[1752+i], // GP_CALIMA12
					generation[1776+i], // GP_CALIMA13  	 
					generation[1800+i], // GP_CALIMA14
					generation[1896+i], // GP_SALVAJI1
					generation[1920+i], // GP_SALVAJI2
					generation[1944+i], // GP_SALVAJI3	
					generation[3384+i], // GP_MTOLIMA1
					generation[3528+i], // GP_MHUILAQ1
					generation[1968+i], // GP_TVALLE1
					generation[1992+i], // GP_TVALLE2 	
					generation[3048+i], // GP_MVALLEC1
					generation[3696+i], // GP_TEMCALI
					generation[1824+i], // GP_PRADO1
					generation[1848+i], // GP_PRADO2	
					generation[1872+i], // GP_PRADO3
					generation[3024+i], // GP_MPRADO4
					generation[2496+i], // GP_MCAUCAN1
					generation[0+i], // GP_MRIOMAYO1 
					generation[24+i], // GP_MRIOMAYO2
					generation[48+i], // GP_MRIOMAYO3
					generation[552+i], // GP_BETANIA1
					generation[576+i], // GP_BETANIA2 	
					generation[600+i], // GP_BETANIA3
					generation[2016+i], // GP_FLORID21
					generation[2040+i], // GP_FLORID22
					generation[3360+i], // GP_MCAUCAN2
					unserved[72+i],		// UD_SUROCCIDENTAL 
					model.prod(susceptance[7] + susceptance[8] + susceptance[10], angle[72+i]), // (SUS[ANT-SUR]	+ SUS[SUR-ORI] + SUS[SUR-ECU]) * VA_SUROCCIDENTAL
					model.prod(-susceptance[7], angle[48+i]), 	// SUS[ANT-SUR]	* VA_ANTIOQUIA
					model.prod(-susceptance[8], angle[120+i]), 	// SUS[SUR-ORI]	* VA_ORIENTAL
					model.prod(-susceptance[10], angle[168+i])};  // SUS[SUR-ECU]	* VA_ECUADOR
			
			rng[0][72+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[3][i], "C_SUROCCIDENTAL_"+i); //  DEM[SUROCCIDENTAL]
		}
		
		//
		// Balanace de oferta y demanda NORDESTE
		//
		// 		GP_MERILEC1		+ 	GP_PALENQ3	 	+ 	GP_MNORDE1	 	+ 	GP_MMORRO1 
		// + 	GP_MMORRO2	 	+ 	GP_PAIPA1	 	+ 	GP_PAIPA2	 	+ 	GP_PAIPA3
		// + 	GP_PAIPA4		+ 	GP_TCENTRO_CC 	+ 	GP_TYOPAL2 		+ 	GP_MYOPAL1 
		// + 	UD_NORDESTE  
		// + 	(SUS[NORD-CAR2]	+ SUS[NORD-VEN] + SUS[ANT-NORD] + SUS[ORI-NORD]) * VA_NORDESTE
		// - 	SUS[NORD-CAR2]	* VA_CARIBE2
		// - 	SUS[NORD-VEN]	* VA_VENEZUELA
		// - 	SUS[ANT-NORD]	* VA_ANTIOQUIA
		// - 	SUS[ORI-NORD]	* VA_ORIENTAL
		// >= 	DEM[NORDESTE]
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[3624+i], // GP_MERILEC1
					generation[3120+i], // GP_PALENQ3
					generation[3144+i], // GP_MNORDE1
					generation[2616+i], // GP_MMORRO1
					generation[2640+i], // GP_MMORRO2
					generation[3432+i], // GP_PAIPA1
					generation[3456+i], // GP_PAIPA2
					generation[3480+i], // GP_PAIPA3
					generation[3504+i], // GP_PAIPA4
					generation[3552+i], // GP_TCENTRO_CC
					generation[3768+i], // GP_TYOPAL2
					generation[3792+i], // GP_MYOPAL1
					unserved[96+i],	// UD_NORDESTE
					model.prod(susceptance[2] + susceptance[4] + susceptance[5]  + susceptance[9], angle[96+i]), // (SUS[NORD-CAR2]	+ SUS[NORD-VEN] + SUS[ANT-NORD] + SUS[ORI-NORD]) * VA_NORDESTE
					model.prod(-susceptance[2], angle[24+i]), 	// SUS[NORD-CAR2]	* VA_CARIBE2
					model.prod(-susceptance[4], angle[144+i]), 	// SUS[NORD-VEN]	* VA_VENEZUELA
					model.prod(-susceptance[5], angle[48+i]), 	// SUS[ANT-NORD]	* VA_ANTIOQUIA
					model.prod(-susceptance[9], angle[120+i])}; // SUS[ORI-NORD]	* VA_ORIENTAL
			
			rng[0][96+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[4][i], "C_NORDESTE_"+i); 			//  DEM[NORDESTE]
		}
		
		//
		// Balanace de oferta y demanda ORIENTAL
		//
		// 		GP_CHIVOR1		+ 	GP_CHIVOR2	 	+ 	GP_CHIVOR3	 	+ 	GP_CHIVOR4 
		// + 	GP_CHIVOR5	 	+ 	GP_CHIVOR6	 	+ 	GP_CHIVOR7	 	+ 	GP_CHIVOR8
		// + 	GP_MCUNDINAM1	+ 	GP_GUAVIO1 		+ 	GP_GUAVIO2 		+ 	GP_GUAVIO3 
		// +	GP_GUAVIO4 		+	GP_GUAVIO5 		+ 	GP_LAGUACA1 	+	GP_LAGUACA2 
		// +	GP_LAGUACA3 	+	GP_PARAISO1 	+	GP_PARAISO2 	+	GP_PARAISO3 
		// +	GP_ZIPAEMG2 	+	GP_ZIPAEMG3 	+	GP_ZIPAEMG4 	+	GP_ZIPAEMG5
		// +	GP_MBOGOTA1
		// + 	UD_ORIENTAL  
		// + 	(SUS[ANT-ORI]	+ SUS[SUR-ORI] + SUS[ORI-NORD]) * VA_ORIENTAL
		// - 	SUS[ANT-ORI]	* VA_ANTIOQUIA
		// - 	SUS[SUR-ORI]	* VA_SUROCCIDENTAL
		// - 	SUS[ORI-NORD]	* VA_NORDESTE
		// >= 	DEM[ORIENTAL]
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[264+i], // GP_CHIVOR1
					generation[288+i], // GP_CHIVOR2
					generation[312+i], // GP_CHIVOR3
					generation[336+i], // GP_CHIVOR4
					generation[360+i], // GP_CHIVOR5
					generation[384+i], // GP_CHIVOR6
					generation[408+i], // GP_CHIVOR7
					generation[432+i], // GP_CHIVOR8
					generation[2688+i], // GP_MCUNDINAM1
					generation[624+i], // GP_GUAVIO1
					generation[648+i], // GP_GUAVIO2
					generation[672+i], // GP_GUAVIO3
					generation[696+i], // GP_GUAVIO4
					generation[720+i], // GP_GUAVIO5
					generation[744+i], // GP_LAGUACA1
					generation[768+i], // GP_LAGUACA2
					generation[792+i], // GP_LAGUACA3 
					generation[816+i], // GP_PARAISO1
					generation[840+i], // GP_PARAISO2
					generation[864+i], // GP_PARAISO3
					generation[2808+i], // GP_ZIPAEMG2 	
					generation[2832+i], // GP_ZIPAEMG3
					generation[2856+i], // GP_ZIPAEMG4
					generation[2880+i], // GP_ZIPAEMG5
					generation[2904+i], // GP_MBOGOTA1
					unserved[120+i],	// UD_ORIENTAL
					model.prod(susceptance[6] + susceptance[8] + susceptance[9], angle[120+i]), // (SUS[ANT-ORI]	+ SUS[SUR-ORI] + SUS[ORI-NORD]) * VA_ORIENTAL
					model.prod(-susceptance[6], angle[48+i]), 	// SUS[ANT-ORI]		* VA_ANTIOQUIA
					model.prod(-susceptance[8], angle[72+i]), 	// SUS[SUR-ORI]		* VA_SUROCCIDENTAL
					model.prod(-susceptance[9], angle[96+i])}; // SUS[ORI-NORD]	* VA_NORDESTE
			
			rng[0][120+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[5][i], "C_ORIENTAL_"+i); 			//  DEM[ORIENTAL]
		}
		
		//
		// Balanace de oferta y demanda VENEZUELA
		//
		// 		GP_COROZO1		+ 	GP_VENEZUE1
		// + 	UD_VENEZUELA  
		// + 	(SUS[CAR2-VEN]	+ SUS[NORD-VEN]) * VA_VENEZUELA
		// - 	SUS[CAR2-VEN]	* VA_CARIBE2
		// - 	SUS[NORD-VEN]	* VA_NORDESTE
		// >= 	DEM[VENEZUELA]
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					generation[3096+i], // GP_COROZO1
					generation[3576+i], // GP_VENEZUE1
					unserved[144+i],	// UD_VENEZUELA
					model.prod(susceptance[3] + susceptance[4], angle[144+i]), // (SUS[CAR2-VEN]	+ SUS[NORD-VEN]) * VA_VENEZUELA
					model.prod(-susceptance[3], angle[24+i]),  // SUS[CAR2-VEN]  * VA_CARIBE2
					model.prod(-susceptance[4], angle[96+i])}; // SUS[NORD-VEN]	 * VA_NORDESTE
			
			rng[0][144+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[6][i], "C_VENEZUELA_"+i); 	//  DEM[VENEZUELA]
		}
				
		//
		// Balanace de oferta y demanda ECUADOR
		//
		// 		
		// + 	UD_ECUADOR  
		// + 	(SUS[SUR-ECU])  * VA_ECUADOR
		// - 	SUS[SUR-ECU]	* VA_SUROCCIDENTAL
		// >= 	DEM[ECUADOR]
		
		for (int i = 0; i < 24; i++){
			IloNumExpr[] expr = {
					unserved[168+i],	// UD_ECUADOR
					model.prod(susceptance[10], angle[168+i]), // (SUS[SUR-ECU])  * VA_ECUADOR
					model.prod(-susceptance[10], angle[72+i])}; // SUS[SUR-ECU]	* VA_SUROCCIDENTAL
			
			rng[0][168+i] = model.addGe(
					model.sum(expr), 	
					powerDemandVector[7][i], "C_ECUADOR_"+i); 	//  DEM[ECUADOR]
		}
		
		/*
		for (int i = 0; i < 24; i++){
			rng[0][i] = model.addGe(
					model.sum(
							model.sum(
							generation[3168+i], // GP_FLORES1
							generation[3192+i], // GP_FLORES21
							generation[3216+i], // GP_FLORES3
							generation[3240+i]  // GP_BARRANQ3
							),
							model.sum(
							generation[3264+i], // GP_BARRANQ4
							generation[3288+i], // GP_GUAJIR11
							generation[3312+i], // GP_GUAJIR21
							generation[3336+i]  // GP_TEBSA
							),
							model.sum(
							generation[3072+i], // GP_MELBOSQUE
							generation[2424+i], // GP_PROELEC1
							generation[2448+i], // GP_PROELEC2
							generation[3000+i]  // GP_MJEPIRACHI
							),
							model.sum(
							generation[3648+i], // GP_TCANDEL1
							generation[3672+i],  // GP_TCANDEL2
							unserved[0+i]),
							model.prod(susceptance[0] + susceptance[2] + susceptance[3], angle[24+i]), // (SUS[CAR1-CAR2]	+ SUS[NORD-CAR2] + SUS[CAR2-VEN]) * VA_CARIBE2
							model.prod(-susceptance[0], angle[0+i]), 	// SUS[CAR1-CAR2]	* VA_CARIBE1
							model.prod(-susceptance[2], angle[96+i]), 	// SUS[NORD-CAR2]	* VA_NORDESTE
							model.prod(-susceptance[3], angle[144+i])), 	// SUS[CAR2-VEN]	* VA_VENEZUELA
							powerDemandVector[0][i], "c1_1"); 			//  DEM[CARIBE2]
		}
		*/
		
		/*
		
        rng[0][1] = model.addGe(model.sum(generation[1], generation[25], unserved[1],
				model.prod(susceptance[0] + susceptance[1], angle[1]), 
				model.prod(-susceptance[0], angle[25]), 
				model.prod(-susceptance[1], angle[49])), 
				powerDemandVector[0][1], "c1_2");
        rng[0][2] = model.addGe(model.sum(generation[2], generation[26], unserved[2],
				model.prod(susceptance[0] + susceptance[1], angle[2]), 
				model.prod(-susceptance[0], angle[26]), 
				model.prod(-susceptance[1], angle[50])), 
				powerDemandVector[0][2], "c1_3");
        rng[0][3] = model.addGe(model.sum(generation[3], generation[27], unserved[3],
				model.prod(susceptance[0] + susceptance[1], angle[3]), 
				model.prod(-susceptance[0], angle[27]), 
				model.prod(-susceptance[1], angle[51])), 
				powerDemandVector[0][3], "c1_4");
        rng[0][4] = model.addGe(model.sum(generation[4], generation[28], unserved[4],
                model.prod(susceptance[0] + susceptance[1], angle[4]), 
                model.prod(-susceptance[0], angle[28]), 
                model.prod(-susceptance[1], angle[52])), 
                powerDemandVector[0][4], "c1_5");
        rng[0][5] = model.addGe(model.sum(generation[5], generation[29], unserved[5],
                model.prod(susceptance[0] + susceptance[1], angle[5]), 
                model.prod(-susceptance[0], angle[29]), 
                model.prod(-susceptance[1], angle[53])), 
                powerDemandVector[0][5], "c1_6");
        rng[0][6] = model.addGe(model.sum(generation[6], generation[30], unserved[6],
                model.prod(susceptance[0] + susceptance[1], angle[6]), 
                model.prod(-susceptance[0], angle[30]), 
                model.prod(-susceptance[1], angle[54])), 
                powerDemandVector[0][6], "c1_7");
        rng[0][7] = model.addGe(model.sum(generation[7], generation[31], unserved[7],
                model.prod(susceptance[0] + susceptance[1], angle[7]), 
                model.prod(-susceptance[0], angle[31]), 
                model.prod(-susceptance[1], angle[55])), 
                powerDemandVector[0][7], "c1_8");
        rng[0][8] = model.addGe(model.sum(generation[8], generation[32], unserved[8],
                model.prod(susceptance[0] + susceptance[1], angle[8]), 
                model.prod(-susceptance[0], angle[32]), 
                model.prod(-susceptance[1], angle[56])), 
                powerDemandVector[0][8], "c1_9");
        rng[0][9] = model.addGe(model.sum(generation[9], generation[33], unserved[9],
                model.prod(susceptance[0] + susceptance[1], angle[9]), 
                model.prod(-susceptance[0], angle[33]), 
                model.prod(-susceptance[1], angle[57])), 
                powerDemandVector[0][9], "c1_10");
        rng[0][10] = model.addGe(model.sum(generation[10], generation[34], unserved[10],
                model.prod(susceptance[0] + susceptance[1], angle[10]), 
                model.prod(-susceptance[0], angle[34]), 
                model.prod(-susceptance[1], angle[58])), 
                powerDemandVector[0][10], "c1_11");
        rng[0][11] = model.addGe(model.sum(generation[11], generation[35], unserved[11], 
                model.prod(susceptance[0] + susceptance[1], angle[11]), 
                model.prod(-susceptance[0], angle[35]), 
                model.prod(-susceptance[1], angle[59])), 
                powerDemandVector[0][11], "c1_12");
        rng[0][12] = model.addGe(model.sum(generation[12], generation[36], unserved[12],
                model.prod(susceptance[0] + susceptance[1], angle[12]), 
                model.prod(-susceptance[0], angle[36]), 
                model.prod(-susceptance[1], angle[60])), 
                powerDemandVector[0][12], "c1_13");
        rng[0][13] = model.addGe(model.sum(generation[13], generation[37], unserved[13],
                model.prod(susceptance[0] + susceptance[1], angle[13]), 
                model.prod(-susceptance[0], angle[37]), 
                model.prod(-susceptance[1], angle[61])), 
                powerDemandVector[0][13], "c1_14");
        rng[0][14] = model.addGe(model.sum(generation[14], generation[38], unserved[14],
                model.prod(susceptance[0] + susceptance[1], angle[14]), 
                model.prod(-susceptance[0], angle[38]), 
                model.prod(-susceptance[1], angle[62])), 
                powerDemandVector[0][14], "c1_15");
        rng[0][15] = model.addGe(model.sum(generation[15], generation[39], unserved[15],
                model.prod(susceptance[0] + susceptance[1], angle[15]), 
                model.prod(-susceptance[0], angle[39]), 
                model.prod(-susceptance[1], angle[63])), 
                powerDemandVector[0][15], "c1_16");
        rng[0][16] = model.addGe(model.sum(generation[16], generation[40], unserved[16],
                model.prod(susceptance[0] + susceptance[1], angle[16]), 
                model.prod(-susceptance[0], angle[40]), 
                model.prod(-susceptance[1], angle[64])), 
                powerDemandVector[0][16], "c1_17");
        rng[0][17] = model.addGe(model.sum(generation[17], generation[41], unserved[17],
                model.prod(susceptance[0] + susceptance[1], angle[17]), 
                model.prod(-susceptance[0], angle[41]), 
                model.prod(-susceptance[1], angle[65])), 
                powerDemandVector[0][17], "c1_18");
        rng[0][18] = model.addGe(model.sum(generation[18], generation[42], unserved[18],
                model.prod(susceptance[0] + susceptance[1], angle[18]), 
                model.prod(-susceptance[0], angle[42]), 
                model.prod(-susceptance[1], angle[66])), 
                powerDemandVector[0][18], "c1_19");
        rng[0][19] = model.addGe(model.sum(generation[19], generation[43], unserved[19],
                model.prod(susceptance[0] + susceptance[1], angle[19]), 
                model.prod(-susceptance[0], angle[43]), 
                model.prod(-susceptance[1], angle[67])), 
                powerDemandVector[0][19], "c1_20");
        rng[0][20] = model.addGe(model.sum(generation[20], generation[44], unserved[20],
                model.prod(susceptance[0] + susceptance[1], angle[20]), 
                model.prod(-susceptance[0], angle[44]), 
                model.prod(-susceptance[1], angle[68])), 
                powerDemandVector[0][20], "c1_21");
        rng[0][21] = model.addGe(model.sum(generation[21], generation[45], unserved[21],
                model.prod(susceptance[0] + susceptance[1], angle[21]), 
                model.prod(-susceptance[0], angle[45]), 
                model.prod(-susceptance[1], angle[69])), 
                powerDemandVector[0][21], "c1_22");
        rng[0][22] = model.addGe(model.sum(generation[22], generation[46], unserved[22],
                model.prod(susceptance[0] + susceptance[1], angle[22]), 
                model.prod(-susceptance[0], angle[46]), 
                model.prod(-susceptance[1], angle[70])), 
                powerDemandVector[0][22], "c1_23");
        rng[0][23] = model.addGe(model.sum(generation[23], generation[47], unserved[23],
                model.prod(susceptance[0] + susceptance[1], angle[23]), 
                model.prod(-susceptance[0], angle[47]), 
                model.prod(-susceptance[1], angle[71])), 
                powerDemandVector[0][23], "c1_24");
             
        
        rng[0][24] = model.addGe(model.sum(generation[48], unserved[24],
        		model.prod(-susceptance[0], angle[0]), 
        		model.prod(susceptance[0] + susceptance[2], angle[24]), 
        		model.prod(-susceptance[2], angle[48])),
        		powerDemandVector[1][0], "c2_1");
        rng[0][25] = model.addGe(model.sum(generation[49], unserved[25],
				model.prod(-susceptance[0], angle[1]), 
				model.prod(susceptance[0] + susceptance[2], angle[25]), 
				model.prod(-susceptance[2], angle[49])),
				powerDemandVector[1][1], "c2_2");
        rng[0][26] = model.addGe(model.sum(generation[50], unserved[26],
				model.prod(-susceptance[0], angle[2]), 
				model.prod(susceptance[0] + susceptance[2], angle[26]), 
				model.prod(-susceptance[2], angle[50])),
				powerDemandVector[1][2], "c2_3");
        rng[0][27] = model.addGe(model.sum(generation[51], unserved[27],
				model.prod(-susceptance[0], angle[3]), 
				model.prod(susceptance[0] + susceptance[2], angle[27]), 
				model.prod(-susceptance[2], angle[51])),
				powerDemandVector[1][3], "c2_4");
        rng[0][28] = model.addGe(model.sum(generation[52], unserved[28],
				model.prod(-susceptance[0], angle[4]), 
				model.prod(susceptance[0] + susceptance[2], angle[28]), 
				model.prod(-susceptance[2], angle[52])),
				powerDemandVector[1][4], "c2_5");
        rng[0][29] = model.addGe(model.sum(generation[53], unserved[29],
				model.prod(-susceptance[0], angle[5]), 
				model.prod(susceptance[0] + susceptance[2], angle[29]), 
				model.prod(-susceptance[2], angle[53])),
				powerDemandVector[1][5], "c2_6");
        rng[0][30] = model.addGe(model.sum(generation[54], unserved[30],
				model.prod(-susceptance[0], angle[6]), 
				model.prod(susceptance[0] + susceptance[2], angle[30]), 
				model.prod(-susceptance[2], angle[54])),
				powerDemandVector[1][6], "c2_7");
        rng[0][31] = model.addGe(model.sum(generation[55], unserved[31],
				model.prod(-susceptance[0], angle[7]), 
				model.prod(susceptance[0] + susceptance[2], angle[31]), 
				model.prod(-susceptance[2], angle[55])),
				powerDemandVector[1][7], "c2_8");
        rng[0][32] = model.addGe(model.sum(generation[56], unserved[32],
				model.prod(-susceptance[0], angle[8]), 
				model.prod(susceptance[0] + susceptance[2], angle[32]), 
				model.prod(-susceptance[2], angle[56])),
				powerDemandVector[1][8], "c2_9");
        rng[0][33] = model.addGe(model.sum(generation[57], unserved[33],
				model.prod(-susceptance[0], angle[9]), 
				model.prod(susceptance[0] + susceptance[2], angle[33]), 
				model.prod(-susceptance[2], angle[57])),
				powerDemandVector[1][9], "c2_10");
        rng[0][34] = model.addGe(model.sum(generation[58], unserved[34],
				model.prod(-susceptance[0], angle[10]), 
				model.prod(susceptance[0] + susceptance[2], angle[34]), 
				model.prod(-susceptance[2], angle[58])),
				powerDemandVector[1][10], "c2_11");
        rng[0][35] = model.addGe(model.sum(generation[59], unserved[35],
				model.prod(-susceptance[0], angle[11]), 
				model.prod(susceptance[0] + susceptance[2], angle[35]), 
				model.prod(-susceptance[2], angle[59])),
				powerDemandVector[1][11], "c2_12");
        rng[0][36] = model.addGe(model.sum(generation[60], unserved[36],
				model.prod(-susceptance[0], angle[12]), 
				model.prod(susceptance[0] + susceptance[2], angle[36]), 
				model.prod(-susceptance[2], angle[60])),
				powerDemandVector[1][12], "c2_13");
        rng[0][37] = model.addGe(model.sum(generation[61], unserved[37],
				model.prod(-susceptance[0], angle[13]), 
				model.prod(susceptance[0] + susceptance[2], angle[37]), 
				model.prod(-susceptance[2], angle[61])),
				powerDemandVector[1][13], "c2_14");
        rng[0][38] = model.addGe(model.sum(generation[62], unserved[38],
				model.prod(-susceptance[0], angle[14]), 
				model.prod(susceptance[0] + susceptance[2], angle[38]), 
				model.prod(-susceptance[2], angle[62])),
				powerDemandVector[1][14], "c2_15");
        rng[0][39] = model.addGe(model.sum(generation[63], unserved[39],
				model.prod(-susceptance[0], angle[15]), 
				model.prod(susceptance[0] + susceptance[2], angle[39]), 
				model.prod(-susceptance[2], angle[63])),
				powerDemandVector[1][15], "c2_16");
        rng[0][40] = model.addGe(model.sum(generation[64], unserved[40],
				model.prod(-susceptance[0], angle[16]), 
				model.prod(susceptance[0] + susceptance[2], angle[40]), 
				model.prod(-susceptance[2], angle[64])),
				powerDemandVector[1][16], "c2_17");
        rng[0][41] = model.addGe(model.sum(generation[65], unserved[41],
				model.prod(-susceptance[0], angle[17]), 
				model.prod(susceptance[0] + susceptance[2], angle[41]), 
				model.prod(-susceptance[2], angle[65])),
				powerDemandVector[1][17], "c2_18");
        rng[0][42] = model.addGe(model.sum(generation[66], unserved[42],
				model.prod(-susceptance[0], angle[18]), 
				model.prod(susceptance[0] + susceptance[2], angle[42]), 
				model.prod(-susceptance[2], angle[66])),
				powerDemandVector[1][18], "c2_19");
        rng[0][43] = model.addGe(model.sum(generation[67], unserved[43],
				model.prod(-susceptance[0], angle[19]), 
				model.prod(susceptance[0] + susceptance[2], angle[43]), 
				model.prod(-susceptance[2], angle[67])),
				powerDemandVector[1][19], "c2_20");
        rng[0][44] = model.addGe(model.sum(generation[68], unserved[44],
				model.prod(-susceptance[0], angle[20]), 
				model.prod(susceptance[0] + susceptance[2], angle[44]), 
				model.prod(-susceptance[2], angle[68])),
				powerDemandVector[1][20], "c2_21");
        rng[0][45] = model.addGe(model.sum(generation[69], unserved[45],
				model.prod(-susceptance[0], angle[21]), 
				model.prod(susceptance[0] + susceptance[2], angle[45]), 
				model.prod(-susceptance[2], angle[69])),
				powerDemandVector[1][21], "c2_22");
        rng[0][46] = model.addGe(model.sum(generation[70], unserved[46],
				model.prod(-susceptance[0], angle[22]), 
				model.prod(susceptance[0] + susceptance[2], angle[46]), 
				model.prod(-susceptance[2], angle[70])),
				powerDemandVector[1][22], "c2_23");
        rng[0][47] = model.addGe(model.sum(generation[71], unserved[47],
				model.prod(-susceptance[0], angle[23]), 
				model.prod(susceptance[0] + susceptance[2], angle[47]), 
				model.prod(-susceptance[2], angle[71])),
				powerDemandVector[1][23], "c2_24"); 
        
        
        rng[0][48] = model.addGe(model.sum(generation[72], unserved[48], 
	            model.prod(-susceptance[1], angle[0]), 
	            model.prod(-susceptance[2], angle[24]), 
	            model.prod(susceptance[1] + susceptance[2], angle[48])), 
	            powerDemandVector[2][0], "c3_1");
        rng[0][49] = model.addGe(model.sum(generation[73], unserved[49],
                model.prod(-susceptance[1], angle[1]), 
                model.prod(-susceptance[2], angle[25]), 
                model.prod(susceptance[1] + susceptance[2], angle[49])), 
                powerDemandVector[2][1], "c3_2");
        rng[0][50] = model.addGe(model.sum(generation[74], unserved[50],
                model.prod(-susceptance[1], angle[2]), 
                model.prod(-susceptance[2], angle[26]), 
                model.prod(susceptance[1] + susceptance[2], angle[50])), 
                powerDemandVector[2][2], "c3_3");
        rng[0][51] = model.addGe(model.sum(generation[75], unserved[51],
                model.prod(-susceptance[1], angle[3]), 
                model.prod(-susceptance[2], angle[27]), 
                model.prod(susceptance[1] + susceptance[2], angle[51])), 
                powerDemandVector[2][3], "c3_4");
        rng[0][52] = model.addGe(model.sum(generation[76], unserved[52],
                model.prod(-susceptance[1], angle[4]), 
                model.prod(-susceptance[2], angle[28]), 
                model.prod(susceptance[1] + susceptance[2], angle[52])), 
                powerDemandVector[2][4], "c3_5");
        rng[0][53] = model.addGe(model.sum(generation[77], unserved[53],
                model.prod(-susceptance[1], angle[5]), 
                model.prod(-susceptance[2], angle[29]), 
                model.prod(susceptance[1] + susceptance[2], angle[53])), 
                powerDemandVector[2][5], "c3_6");
        rng[0][54] = model.addGe(model.sum(generation[78], unserved[54],
                model.prod(-susceptance[1], angle[6]), 
                model.prod(-susceptance[2], angle[30]), 
                model.prod(susceptance[1] + susceptance[2], angle[54])), 
                powerDemandVector[2][6], "c3_7");
        rng[0][55] = model.addGe(model.sum(generation[79], unserved[55],
                model.prod(-susceptance[1], angle[7]), 
                model.prod(-susceptance[2], angle[31]), 
                model.prod(susceptance[1] + susceptance[2], angle[55])), 
                powerDemandVector[2][7], "c3_8");
        rng[0][56] = model.addGe(model.sum(generation[80], unserved[56],
                model.prod(-susceptance[1], angle[8]), 
                model.prod(-susceptance[2], angle[32]), 
                model.prod(susceptance[1] + susceptance[2], angle[56])), 
                powerDemandVector[2][8], "c3_9");
        rng[0][57] = model.addGe(model.sum(generation[81], unserved[57],
                model.prod(-susceptance[1], angle[9]), 
                model.prod(-susceptance[2], angle[33]), 
                model.prod(susceptance[1] + susceptance[2], angle[57])), 
                powerDemandVector[2][9], "c3_10");
        rng[0][58] = model.addGe(model.sum(generation[82], unserved[58],
                model.prod(-susceptance[1], angle[10]), 
                model.prod(-susceptance[2], angle[34]), 
                model.prod(susceptance[1] + susceptance[2], angle[58])), 
                powerDemandVector[2][10], "c3_11");
        rng[0][59] = model.addGe(model.sum(generation[83], unserved[59],
                model.prod(-susceptance[1], angle[11]), 
                model.prod(-susceptance[2], angle[35]), 
                model.prod(susceptance[1] + susceptance[2], angle[59])), 
                powerDemandVector[2][11], "c3_12");
        rng[0][60] = model.addGe(model.sum(generation[84], unserved[60],
                model.prod(-susceptance[1], angle[12]), 
                model.prod(-susceptance[2], angle[36]), 
                model.prod(susceptance[1] + susceptance[2], angle[60])), 
                powerDemandVector[2][12], "c3_13");
        rng[0][61] = model.addGe(model.sum(generation[85], unserved[61],
                model.prod(-susceptance[1], angle[13]), 
                model.prod(-susceptance[2], angle[37]), 
                model.prod(susceptance[1] + susceptance[2], angle[61])), 
                powerDemandVector[2][13], "c3_14");
        rng[0][62] = model.addGe(model.sum(generation[86], unserved[62],
                model.prod(-susceptance[1], angle[14]), 
                model.prod(-susceptance[2], angle[38]), 
                model.prod(susceptance[1] + susceptance[2], angle[62])), 
                powerDemandVector[2][14], "c3_15");
        rng[0][63] = model.addGe(model.sum(generation[87], unserved[63],
                model.prod(-susceptance[1], angle[15]), 
                model.prod(-susceptance[2], angle[39]), 
                model.prod(susceptance[1] + susceptance[2], angle[63])), 
                powerDemandVector[2][15], "c3_16");
        rng[0][64] = model.addGe(model.sum(generation[88], unserved[64],
                model.prod(-susceptance[1], angle[16]), 
                model.prod(-susceptance[2], angle[40]), 
                model.prod(susceptance[1] + susceptance[2], angle[64])), 
                powerDemandVector[2][16], "c3_17");
        rng[0][65] = model.addGe(model.sum(generation[89], unserved[65],
                model.prod(-susceptance[1], angle[17]), 
                model.prod(-susceptance[2], angle[41]), 
                model.prod(susceptance[1] + susceptance[2], angle[65])), 
                powerDemandVector[2][17], "c3_18");
        rng[0][66] = model.addGe(model.sum(generation[90], unserved[66],
                model.prod(-susceptance[1], angle[18]), 
                model.prod(-susceptance[2], angle[42]), 
                model.prod(susceptance[1] + susceptance[2], angle[66])), 
                powerDemandVector[2][18], "c3_19");
        rng[0][67] = model.addGe(model.sum(generation[91], unserved[67],
                model.prod(-susceptance[1], angle[19]), 
                model.prod(-susceptance[2], angle[43]), 
                model.prod(susceptance[1] + susceptance[2], angle[67])), 
                powerDemandVector[2][19], "c3_20");
        rng[0][68] = model.addGe(model.sum(generation[92], unserved[68],
                model.prod(-susceptance[1], angle[20]), 
                model.prod(-susceptance[2], angle[44]), 
                model.prod(susceptance[1] + susceptance[2], angle[68])), 
                powerDemandVector[2][20], "c3_21");
        rng[0][69] = model.addGe(model.sum(generation[93], unserved[69],
                model.prod(-susceptance[1], angle[21]), 
                model.prod(-susceptance[2], angle[45]), 
                model.prod(susceptance[1] + susceptance[2], angle[69])), 
                powerDemandVector[2][21], "c3_22");
        rng[0][70] = model.addGe(model.sum(generation[94], unserved[70],
                model.prod(-susceptance[1], angle[22]), 
                model.prod(-susceptance[2], angle[46]), 
                model.prod(susceptance[1] + susceptance[2], angle[70])), 
                powerDemandVector[2][22], "c3_23");
        rng[0][71] = model.addGe(model.sum(generation[95], unserved[71],
                model.prod(-susceptance[1], angle[23]), 
                model.prod(-susceptance[2], angle[47]), 
                model.prod(susceptance[1] + susceptance[2], angle[71])), 
                powerDemandVector[2][23], "c3_23");
                
         */
	}
	
	static void populateByRow(IloMPModeler model, IloNumVar[][] var, IloRange[][] rng, String[] var1, 
			double [] generation, double [][] powerDemandVector, double [] flowLb, double [] flowUb) throws IloException {
		
		// Variables  --------------------------------------------------------------------------------
		IloNumVar[] flows       = model.numVarArray(24*Global.nLines, flowLb, flowUb, var1); 	// demanda no atendida
		var[0] = flows; 		// flujos 
		
		//
		// Funci�n objetivo
		//
		// Minimizar el costo de generaci�n
		// model.addMinimize(model.scalProd(generation, generationCost)); 
		
		// Minimizar el costo de generaci�n y el costo por demanda no atendida
		//model.addMinimize(model.sum(flows)); 
		model.addMinimize();
		
		//
		// Restricciones  --------------------------------------------------------------------------------
		//
		
		double [][] b = new double [Global.nNodes][24];
              
        for (int i = 0; i < 24; ++i) {
        	b[0][i] = 	generation[456+i] + // GP_URRA1			
						generation[480+i] + // GP_URRA2
						generation[504+i] + // GP_URRA3
						generation[528+i] -  // GP_URRA4
		            	powerDemandVector[0][i] ;
							            	
        	b[1][i] =	generation[3168+i] + // GP_FLORES1
						generation[3192+i] + // GP_FLORES21
						generation[3216+i] + // GP_FLORES3
						generation[3240+i] + // GP_BARRANQ3
						generation[3264+i] + // GP_BARRANQ4
						generation[3288+i] + // GP_GUAJIR11
						generation[3312+i] + // GP_GUAJIR21
						generation[3336+i] + // GP_TEBSA
						generation[3072+i] + // GP_MELBOSQUE
						generation[2424+i] + // GP_PROELEC1
						generation[2448+i] + // GP_PROELEC2
						generation[3000+i] + // GP_MJEPIRACHI
						generation[3648+i] + // GP_TCANDEL1
						generation[3672+i] - // GP_TCANDEL2
						powerDemandVector[1][i] ;
			
        	
        	b[2][i] = 	generation[3720+i] + // GP_TASAJER1
						generation[888+i]  + // GP_GUATAPE1
						generation[912+i]  + // GP_GUATAPE2
						generation[936+i]  + // GP_GUATAPE3
						generation[960+i]  + // GP_GUATAPE4
						generation[984+i]  + // GP_GUATAPE5
						generation[1008+i] + // GP_GUATAPE6
						generation[1032+i] + // GP_GUATAPE7
						generation[1056+i] + // GP_GUATAPE8
						generation[1080+i] + // GP_GUADAL31
						generation[1104+i] + // GP_GUADAL32
						generation[1128+i] + // GP_GUADAL33
						generation[1152+i] + // GP_GUADAL34
						generation[1176+i] + // GP_GUADAL35
						generation[1200+i] + // GP_GUADAL36
						generation[1224+i] + // GP_GUADAL41
						generation[1248+i] + // GP_GUADAL42
						generation[1272+i] + // GP_GUADAL43
						generation[1296+i] + // GP_TRONERA1
						generation[1320+i] + // GP_TRONERA2
						generation[1344+i] + // GP_LATASAJ1
						generation[1368+i] + // GP_LATASAJ2
						generation[1392+i] + // GP_LATASAJ3
						generation[1416+i] + // GP_PLAYAS1
						generation[1440+i] + // GP_PLAYAS2
						generation[1464+i] + // GP_PLAYAS3
						generation[1488+i] + // GP_PORCE21
						generation[1512+i] + // GP_PORCE22
						generation[1536+i] + // GP_PORCE23
						generation[2952+i] + // GP_TSIERRA
						generation[2976+i] + // GP_MANTIOQ1
						generation[2064+i] + // GP_JAGUAS1
						generation[2088+i] + // GP_JAGUAS2
						generation[2184+i] + // GP_SANCARL1
						generation[2208+i] + // GP_SANCARL2
						generation[2232+i] + // GP_SANCARL3
						generation[2256+i] + // GP_SANCARL4
						generation[2280+i] + // GP_SANCARL5
						generation[2304+i] + // GP_SANCARL6
						generation[2328+i] + // GP_SANCARL7
						generation[2352+i] + // GP_SANCARL8
						generation[2376+i] + // GP_MCALDERAS1
						generation[2400+i] - // GP_MCALDERAS2
		            	powerDemandVector[2][i] ;
						
						
        	b[3][i] =	generation[2544+i] + // GP_DORADA1
						generation[72+i]   + // GP_ESMERAL1
						generation[96+i]   + // GP_ESMERAL2
						generation[120+i]  + // GP_INSULA1
						generation[144+i]  + // GP_INSULA2
						generation[168+i]  + // GP_INSULA3
						generation[2568+i] + // GP_MCQR1
						generation[192+i]  + // GP_SANFRAN1
						generation[216+i]  + // GP_SANFRAN2
						generation[240+i]  + // GP_SANFRAN3
						generation[2112+i] + // GP_MIEL11
						generation[2136+i] + // GP_MIEL12
						generation[2160+i] + // GP_MIEL13
						generation[2520+i] + // GP_MTULUA
						generation[2712+i] + // GP_MEMCALI
						generation[1560+i] + // GP_ALTANCHI1
						generation[1584+i] + // GP_ALTANCHI2	
						generation[1608+i] + // GP_ALTANCHI3
						generation[1632+i] + // GP_BAJANCHI1
						generation[1656+i] + // GP_BAJANCHI2
						generation[1680+i] + // GP_BAJANCHI3	
						generation[1704+i] + // GP_BAJANCHI4	
						generation[1728+i] + // GP_CALIMA11
						generation[1752+i] + // GP_CALIMA12
						generation[1776+i] + // GP_CALIMA13  	 
						generation[1800+i] + // GP_CALIMA14
						generation[1896+i] + // GP_SALVAJI1
						generation[1920+i] + // GP_SALVAJI2
						generation[1944+i] + // GP_SALVAJI3	
						generation[3384+i] + // GP_MTOLIMA1
						generation[3528+i] + // GP_MHUILAQ1
						generation[1968+i] + // GP_TVALLE1
						generation[1992+i] + // GP_TVALLE2 	
						generation[3048+i] + // GP_MVALLEC1
						generation[3696+i] + // GP_TEMCALI
						generation[1824+i] + // GP_PRADO1
						generation[1848+i] + // GP_PRADO2	
						generation[1872+i] + // GP_PRADO3
						generation[3024+i] + // GP_MPRADO4
						generation[2496+i] + // GP_MCAUCAN1
						generation[0+i]    + // GP_MRIOMAYO1 
						generation[24+i]   + // GP_MRIOMAYO2
						generation[48+i]   + // GP_MRIOMAYO3
						generation[552+i]  + // GP_BETANIA1
						generation[576+i]  + // GP_BETANIA2 	
						generation[600+i]  + // GP_BETANIA3
						generation[2016+i] + // GP_FLORID21
						generation[2040+i] + // GP_FLORID22
						generation[3360+i] - // GP_MCAUCAN2
        				powerDemandVector[3][i] ;			
																					
			b[4][i] =	generation[3624+i] + // GP_MERILEC1
						generation[3120+i] + // GP_PALENQ3
						generation[3144+i] + // GP_MNORDE1
						generation[2616+i] + // GP_MMORRO1
						generation[2640+i] + // GP_MMORRO2
						generation[3432+i] + // GP_PAIPA1
						generation[3456+i] + // GP_PAIPA2
						generation[3480+i] + // GP_PAIPA3
						generation[3504+i] + // GP_PAIPA4
						generation[3552+i] + // GP_TCENTRO_CC
						generation[3768+i] + // GP_TYOPAL2
						generation[3792+i] - // GP_MYOPAL1
						powerDemandVector[4][i] ;
														
			b[5][i] =	generation[264+i]  + // GP_CHIVOR1
						generation[288+i]  + // GP_CHIVOR2
						generation[312+i]  + // GP_CHIVOR3
						generation[336+i]  + // GP_CHIVOR4
						generation[360+i]  + // GP_CHIVOR5
						generation[384+i]  + // GP_CHIVOR6
						generation[408+i]  + // GP_CHIVOR7
						generation[432+i]  + // GP_CHIVOR8
						generation[2688+i] + // GP_MCUNDINAM1
						generation[624+i]  + // GP_GUAVIO1
						generation[648+i]  + // GP_GUAVIO2
						generation[672+i]  + // GP_GUAVIO3
						generation[696+i]  + // GP_GUAVIO4
						generation[720+i]  + // GP_GUAVIO5
						generation[744+i]  + // GP_LAGUACA1
						generation[768+i]  + // GP_LAGUACA2
						generation[792+i]  + // GP_LAGUACA3 
						generation[816+i]  + // GP_PARAISO1
						generation[840+i]  + // GP_PARAISO2
						generation[864+i]  + // GP_PARAISO3
						generation[2808+i] + // GP_ZIPAEMG2 	
						generation[2832+i] + // GP_ZIPAEMG3
						generation[2856+i] + // GP_ZIPAEMG4
						generation[2880+i] + // GP_ZIPAEMG5
						generation[2904+i] - // GP_MBOGOTA1
						powerDemandVector[5][i] ;
														
			b[6][i] =	generation[3096+i] + // GP_COROZO1
						generation[3576+i] - // GP_VENEZUE1
						powerDemandVector[6][i] ;
														
			b[7][i] =	0.0 -
						powerDemandVector[7][i];;
			
			//    1    	   2		 3         4        5       6        7       8       9      10       11
			//    0    	   24		 48        72       96     120       144     168     192    216      240
			// CAR1-CAR2 ANT-CAR1 NORD-CAR2 CAR2-VEN NORD-VEN ANT-NORD ANT-ORI ANT-SUR SUR-ORI ORI-NORD SUR-ECU
			// CAR1_CAR2 ANT_CAR1 NORD_CAR2 CAR2_VEN NORD_VEN ANT_NORD ANT_ORI ANT_SUR SUR_ORI ORI_NORD SUR_ECU
						
			rng[0][0+i] = model.addEq(
						  		model.sum(flows[0+i], 	 // CAR1-CAR2
						  				  flows[24+i]),  // ANT-CAR1 
						  		b[0][i],	// demanda en CAR1
						  		"C_CAR1_"+i); 	
			
			rng[0][24+i] = model.addEq(
						  		model.sum(flows[0+i], 	// CAR1-CAR2
						  				  flows[48+i],  // NORD-CAR2
						  				  flows[72+i]), // CAR2-VEN
						  		b[1][i],	// demanda en CAR2
						  		"C_CAR2_"+i); 
			
			rng[0][48+i] = model.addEq(
						  		model.sum(flows[24+i], 	 // ANT-CAR1
						  				  flows[120+i],  // ANT-NORD
						  				  flows[144+i],  // ANT-ORI
						  				  flows[168+i]), // ANT-SUR
						  		b[2][i],	// demanda en ANT
						  		"C_ANT_"+i);  
			
			rng[0][72+i] = model.addEq(
						  		model.sum(flows[168+i],  // ANT-SUR
						  				  flows[192+i],  // SUR-ORI
						  				  flows[240+i]), // SUR-ECU
						  		b[3][i],	// demanda en SUR
						  		"C_SUR_"+i);
			
			rng[0][96+i] = model.addEq(
						  		model.sum(flows[48+i],   // NORD-CAR2
						  				  flows[96+i],   // NORD-VEN
						  				  flows[120+i],  // ANT-NORD
						  				  flows[216+i]), // ORI-NORD
						  		b[4][i],	// demanda en NORD
						  		"C_NORD_"+i);
			
			rng[0][120+i] = model.addEq(
						  		model.sum(flows[144+i],  // ANT-ORI
						  				  flows[192+i],  // SUR-ORI
						  				  flows[216+i]), // ORI-NORD
						  		b[5][i],	// demanda en ORI
						  		"C_ORI_"+i);
			
			rng[0][144+i] = model.addEq(
						  		model.sum(flows[72+i],  // CAR2-VEN
						  				  flows[96+i]), // NORD-VEN
						  		b[6][i],	// demanda en VEN
						  		"C_VEN_"+i);
			
			rng[0][168+i] = model.addEq(
						  		flows[240+i],  // SUR-ECU
						  		b[7][i],	// demanda en VEN
						  		"C_ECU_"+i);	
        }
	}
}