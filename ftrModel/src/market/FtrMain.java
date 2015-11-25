package market;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.DateUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.RefineryUtilities;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import utilities.Global;
import utilities.Graphics;
import utilities.Interfaz;
import utilities.MathFuns;
import utilities.PlotNodalPrices;
import utilities.PlotSpotPrices;
import utilities.TableFormat;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import agents.Generator;
import agents.Operator;
import agents.Retailer;
import agents.Transmitter;

// Cplex
import ilog.concert.*;

@SuppressWarnings("serial")
public class FtrMain  extends JFrame implements Runnable{

	Operator operator = new Operator();
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	// variables del mercado
	private List<Node> nodes = new ArrayList<Node>(Global.nGencos);	// lista de nodos
	private List<Generator> generators = new ArrayList<Generator>(Global.nGencos);	// lista de generadores
	private List<Retailer> retailers = new ArrayList<Retailer>(Global.nRetailers);	// lista de comercializadores
	private List<Transmitter> transmitters = new ArrayList<Transmitter>(Global.nGridcos); 	// lista de transmisores
	private List<GenerationUnit> generationUnits = new ArrayList<GenerationUnit>(Global.nUnits); // list of unidades de generación
	private List<TransmissionLine> transmissionLines = new ArrayList<TransmissionLine>(Global.nLines);	// lista de líneas de transmisión
	private List<GenerationContract> contracts = new ArrayList<GenerationContract>(Global.nContracts); // lista de contratos de generación
	private double[][] usageCharges; // cargos por uso del sistema de transmisión
	private List<double[]> historicalNodalPrices = new ArrayList<double[]>(); // lista de precios nodales históricos por nodo
	private List<double[]> historicalEnergyDemand = new ArrayList<double[]>(); // lista de demanda de energía histórica
	private List<double[]> historicalHourlyEnergyDemand = new ArrayList<double[]>(); // lista de demanda de energía histórica por hora
	
	//
	// datos estáticos
	//
	private double referencePricePositiveReconciliation = Global.referencePricePositiveReconciliation;
	private int auctionIndex = 0;
	
	//
	// nodos
	//
	Node	nodeANTIOQUI			;	Node	nodeATLANTIC	;	Node	nodeBOGOTA		;	Node	nodeBOLIVAR		;	Node	nodeCAUCANAR	;
	Node	nodeCERROMAT			;	Node	nodeCHIVOR		;	Node	nodeCORDOSUC	; 	Node	nodeCQR			; 	Node	nodeGCM			;
	Node	nodeHUILACAQ			; 	Node	nodeLAMIEL		;   Node	nodeMAGDAMED	;   Node	nodeMETA		;   Node	nodeNORDESTE	;
	Node	nodePAGUA				;	Node	nodeSANCARLO	;	Node	nodeTOLIMA		;	Node	nodeVALLECAU	;	Node	nodeCOROZO		;
	Node	nodeCUATRICENTENARIO	;	Node	nodeECUADOR220	;	
	
	Node 	nodePANAMA;
	
	//
	// generadores
	//
	Generator	genEPSG	;	Generator	genGECG	;	Generator	genENDG	;	Generator	genCHVG	;	Generator	genDLRG	;	Generator	genESSG	;
	Generator	genCVAG	;	Generator	genCHCG	;	Generator	genECUG	;	Generator	genFRSG	;	Generator	genGNCG	;	Generator	genEPMG	;	
	Generator	genISGG	;	Generator	genADCG	;	Generator	genEEPG	;	Generator	genLCSG	;	Generator	genCDNG	;	Generator	genCTMG	;	
	Generator	genEECG	;	Generator	genFACG	;	Generator	genERCG	;	Generator	genEGCG	;	Generator	genCIVG	;	Generator	genHLAG	;	
	Generator	genGEEG	;	Generator	genCETG	;	Generator	genTYPG	;	Generator	genHIMG	;	Generator	genPRLG	;	Generator	genTRMG	;
	Generator	genTCDG	;	Generator	genTEMG	;	Generator	genTRPG	;	Generator	genEMUG	;	
	
	Generator	genPOLG;
	Generator	genHIDG;
	Generator	genHMVG;
	Generator	genENAG;
	Generator	genITUG;
	Generator	genTENG;
	Generator	genPANG;
	Generator   genPUTG;
	
	//
	// unidades de generación
	//
	
	/*
	// 2012
	GenerationUnit	unitALBAN		;	GenerationUnit	unitBARRANQ3		;	GenerationUnit	unitBARRANQ4		;	GenerationUnit	unitBETANIA			;	GenerationUnit	unitCALIMA1		;
	GenerationUnit	unitCHIVOR		;	GenerationUnit	unitCOINCAUCA		;	GenerationUnit	unitCOROZO1			;	GenerationUnit	unitCSANCARLOS		;	GenerationUnit	unitCTGEMG1		;
	GenerationUnit	unitCTGEMG2		;	GenerationUnit	unitCTGEMG3			;	GenerationUnit	unitCVALLEC1		;	GenerationUnit	unitDORADA1			;	GenerationUnit	unitECUADOR11	;
	GenerationUnit	unitECUADOR12	;	GenerationUnit	unitECUADOR13		;	GenerationUnit	unitECUADOR14		;	GenerationUnit	unitECUADOR21		;	GenerationUnit	unitECUADOR22	;
	GenerationUnit	unitECUADOR23	;	GenerationUnit	unitECUADOR24		;	GenerationUnit	unitESMERALDA		;	GenerationUnit	unitFLORES1			;	GenerationUnit	unitFLORES21	;		
	GenerationUnit	unitFLORES3		;	GenerationUnit	unitFLORESIVB		;	GenerationUnit	unitFLORIDA2		;	GenerationUnit	unitGUAJIR11		;	GenerationUnit	unitGUAJIR21	;
	GenerationUnit	unitGUATAPE		;	GenerationUnit	unitGUATRON			; 	GenerationUnit	unitGUAVIO			;	GenerationUnit	unitINSULA			;	GenerationUnit	unitJAGUAS		;		
	GenerationUnit	unitLATASAJERA	;	GenerationUnit	unitMAGUAFRE		;	GenerationUnit	unitMANTIOQ1		;	GenerationUnit	unitMBELMONTE		;	GenerationUnit	unitMBOGOTA1	;
	GenerationUnit	unitMCALDERAS	;	GenerationUnit	unitMCARUQUIA		;	GenerationUnit	unitMCASCADA1		;	GenerationUnit	unitMCAUCAN1		;	GenerationUnit	unitMCAUCAN2	;	
	GenerationUnit	unitMCIMARR1	; 	GenerationUnit	unitMCQR1			;	GenerationUnit	unitMCUNDINAMARCA	;	GenerationUnit	unitMCURRUCU		;	GenerationUnit	unitMELBOSQUE	;
	GenerationUnit	unitMEMCALI		;	GenerationUnit	unitMERILEC1		; 	GenerationUnit	unitMGUANAQUITA		;	GenerationUnit	unitMHUILAQ1		; 	GenerationUnit	unitMIEL1		;	
	GenerationUnit	unitMJEPIRAC	; 	GenerationUnit	unitMMORRO1			;	GenerationUnit	unitMMORRO2			; 	GenerationUnit	unitMNLIBARE		;	GenerationUnit	unitMNORDE1		;
	GenerationUnit	unitMPRADO4		;	GenerationUnit	unitMRIOMAYO		; 	GenerationUnit	unitMSANTANA	    ;	GenerationUnit	unitMSANTARITA		;	GenerationUnit	unitMSANTIAGO	;		
	GenerationUnit	unitMTOLIMA1	;	GenerationUnit	unitMTULUA	     	;	GenerationUnit	unitMVALLEC1		;	GenerationUnit	unitMYOPAL1			;	GenerationUnit	unitM_AMAIME	;
	GenerationUnit	unitM_PROVIDEN	;	GenerationUnit	unitPAGUA			;	GenerationUnit	unitPAIPA1			;	GenerationUnit	unitPAIPA2			;	GenerationUnit	unitPAIPA3		;		
	GenerationUnit	unitPAIPA4		;	GenerationUnit	unitPALENQ3			;	GenerationUnit	unitPLAYAS			;	GenerationUnit	unitPORCE2			;	GenerationUnit	unitPORCE3		;
	GenerationUnit	unitPORCE3P		;	GenerationUnit	unitPRADO			;	GenerationUnit	unitPROELEC1		;	GenerationUnit	unitPROELEC2		;	GenerationUnit	unitRPIEDRAS	;		
	GenerationUnit	unitSALVAJINA	;	GenerationUnit	unitSANCARLOS		;	GenerationUnit	unitSANFRANCISCO	;	GenerationUnit	unitTASAJER1		;	GenerationUnit	unitTCANDEL1	;
	GenerationUnit	unitTCANDEL2	;	GenerationUnit	unitTCENTRO1		;	GenerationUnit	unitTEBSA			;	GenerationUnit	unitTEMCALI			;	GenerationUnit	unitTPIEDRAS	;		
	GenerationUnit	unitTSIERRA		;	GenerationUnit	unitTVALLE			;	GenerationUnit	unitTYOPAL2			;	GenerationUnit	unitURRA			;	GenerationUnit	unitVENEZUE1	;
	GenerationUnit	unitZIPAEMG2	;	GenerationUnit	unitZIPAEMG3		;	GenerationUnit	unitZIPAEMG4		;	GenerationUnit	unitZIPAEMG5		;
	*/
	
	// 2020
	GenerationUnit	unitALBAN	;
	GenerationUnit	unitBARRANQ3	;
	GenerationUnit	unitBARRANQ4	;
	GenerationUnit	unitBETANIA	;
	GenerationUnit	unitCALIMA1	;
	GenerationUnit	unitCHIVOR	;
	GenerationUnit	unitCOINCAUCA	;
	GenerationUnit	unitCOROZO1	;
	GenerationUnit	unitCSANCARLOS	;
	GenerationUnit	unitCTGEMG1	;
	GenerationUnit	unitCTGEMG2	;
	GenerationUnit	unitCTGEMG3	;
	GenerationUnit	unitCVALLEC1	;
	GenerationUnit	unitDORADA1	;
	GenerationUnit	unitECUADOR11	;
	GenerationUnit	unitECUADOR12	;
	GenerationUnit	unitECUADOR13	;
	GenerationUnit	unitECUADOR14	;
	GenerationUnit	unitECUADOR21	;
	GenerationUnit	unitECUADOR22	;
	GenerationUnit	unitECUADOR23	;
	GenerationUnit	unitECUADOR24	;
	GenerationUnit	unitESMERALDA	;
	GenerationUnit	unitFLORESIV	;
	GenerationUnit	unitFLORES1	;
	GenerationUnit	unitFLORIDA2	;
	GenerationUnit	unitGUAJIR11	;
	GenerationUnit	unitGUAJIR21	;
	GenerationUnit	unitGUATAPE	;
	GenerationUnit	unitGUATRON	;
	GenerationUnit	unitGUAVIO	;
	GenerationUnit	unitINSULA	;
	GenerationUnit	unitJAGUAS	;
	GenerationUnit	unitLATASAJERA	;
	GenerationUnit	unitM_AMAIME	;
	GenerationUnit	unitM_PROVIDEN	;
	GenerationUnit	unitMAGUAFRE	;
	GenerationUnit	unitMALTOTULUA1	;
	GenerationUnit	unitMANTIOQ1	;
	GenerationUnit	unitMBARROSO1	;
	GenerationUnit	unitMBELMONTE	;
	GenerationUnit	unitMBOGOTA1	;
	GenerationUnit	unitMCALDERAS	;
	GenerationUnit	unitMCARUQUIA	;
	GenerationUnit	unitMCASCADA1	;
	GenerationUnit	unitMCAUCAN1	;
	GenerationUnit	unitMCAUCAN2	;
	GenerationUnit	unitMCIMARR1	;
	GenerationUnit	unitMCQR1	;
	GenerationUnit	unitMCUNDINAMARCA	;
	GenerationUnit	unitMCURRUCU	;
	GenerationUnit	unitMELBOSQUE	;
	GenerationUnit	unitMEMCALI	;
	GenerationUnit	unitMERILEC1	;
	GenerationUnit	unitMGUANAQUITA	;
	GenerationUnit	unitMHUILAQ1	;
	GenerationUnit	unitMIEL1	;
	GenerationUnit	unitMJEPIRAC	;
	GenerationUnit	unitMMONTAÑITAS	;
	GenerationUnit	unitMMORRO1	;
	GenerationUnit	unitMMORRO2	;
	GenerationUnit	unitMNLIBARE	;
	GenerationUnit	unitMNORDE1	;
	GenerationUnit	unitMPRADO4	;
	GenerationUnit	unitMRIOMAYO	;
	GenerationUnit	unitMSANFRANCISC	;
	GenerationUnit	unitMSANTANA	;
	GenerationUnit	unitMSANTARITA	;
	GenerationUnit	unitMSANTIAGO	;
	GenerationUnit	unitMTOLIMA1	;
	GenerationUnit	unitMTULUA	;
	GenerationUnit	unitMVALLEC1	;
	GenerationUnit	unitMYOPAL1	;
	GenerationUnit	unitPAGUA	;
	GenerationUnit	unitPAIPA1	;
	GenerationUnit	unitPAIPA2	;
	GenerationUnit	unitPAIPA3	;
	GenerationUnit	unitPAIPA4	;
	GenerationUnit	unitPLAYAS	;
	GenerationUnit	unitPORCE2	;
	GenerationUnit	unitPORCE3	;
	GenerationUnit	unitPRADO	;
	GenerationUnit	unitPROELEC1	;
	GenerationUnit	unitPROELEC2	;
	GenerationUnit	unitRPIEDRAS	;
	GenerationUnit	unitSALVAJINA	;
	GenerationUnit	unitSANCARLOS	;
	GenerationUnit	unitSANFRANCISCO	;
	GenerationUnit	unitTASAJER1	;
	GenerationUnit	unitTCANDEL1	;
	GenerationUnit	unitTCANDEL2	;
	GenerationUnit	unitTCENTRO1	;
	GenerationUnit	unitTEBSA	;
	GenerationUnit	unitTEMCALI	;
	GenerationUnit	unitTPIEDRAS	;
	GenerationUnit	unitTSIERRA	;
	GenerationUnit	unitTVALLE	;
	GenerationUnit	unitTYOPAL2	;
	GenerationUnit	unitURRA	;
	GenerationUnit	unitVENEZUE1	;
	GenerationUnit	unitZIPAEMG2	;
	GenerationUnit	unitZIPAEMG3	;
	GenerationUnit	unitZIPAEMG4	;
	GenerationUnit	unitZIPAEMG5	;
	GenerationUnit	unitAMOYA	;
	GenerationUnit	unitTERMOCOL	;
	GenerationUnit	unitCUCUANA	;
	GenerationUnit	unitELQUIMBO	;
	GenerationUnit	unitSOGAMOSO	;
	GenerationUnit	unitCARLOSLLERAS	;
	GenerationUnit	unitPOPAL	;
	GenerationUnit	unitRIOAMBEIMA	;
	GenerationUnit	unitGECELCA32	;
	GenerationUnit	unitITUANGO	;
	GenerationUnit	unitPORVENIRII	;
	GenerationUnit	unitTERMONORTE	;
	GenerationUnit	unitPANAMA	;

	//
	// comercializadores
	//
	Retailer	retANTIOQUI			;	Retailer	retATLANTIC		; 	Retailer	retBOGOTA	; 	Retailer	retBOLIVAR	; 	Retailer	retCAUCANAR	; 	
	Retailer	retCERROMAT			; 	Retailer	retCHIVOR		; 	Retailer	retCORDOSUC	; 	Retailer	retCQR	    ; 	Retailer	retGCM		; 	
	Retailer	retHUILACAQ			; 	Retailer	retLAMIEL		;	Retailer	retMAGDAMED	; 	Retailer	retMETA		; 	Retailer	retNORDESTE	; 	
	Retailer	retPAGUA			;	Retailer	retSANCARLO		; 	Retailer	retTOLIMA	; 	Retailer	retVALLECAU	; 	Retailer	retCOROZO	;
	Retailer	retCUATRICENTENARIO	; 	Retailer	retECUADOR220	;	
	
	Retailer 	retPANAMA;
	
	//
	// comercializadores
	//
	Transmitter	transCENS	;
	Transmitter	transEEB	;
	Transmitter	transISA	;
	Transmitter	transTRANSELCA	;
	
	//
	// líneas de transmisión
	//	
	TransmissionLine	lineANTCQR1	; 	TransmissionLine	lineANTCQR2	; 	TransmissionLine	lineANTSCA1	; 	TransmissionLine	lineANTSCA2	; 	TransmissionLine	lineANTSCA3	; 	
	TransmissionLine	lineANTSCA4	; 	TransmissionLine	lineANTSCA5	; 	TransmissionLine	lineANTMAG1	; 	TransmissionLine	lineANTMAG2	; 	TransmissionLine	lineATLCDS1	; 	
	TransmissionLine	lineATLCDS2	; 	TransmissionLine	lineBOGMAG1	;   TransmissionLine	lineBOGMAG2	;  	TransmissionLine	lineBOGMAG3	; 	TransmissionLine	lineBOGMET1	; 	
	TransmissionLine	lineBOGMET2	;  	TransmissionLine	lineBOLATL1	; 	TransmissionLine	lineBOLATL2	; 	TransmissionLine	lineBOLATL3	; 	TransmissionLine	lineBOLGCM1	;
	TransmissionLine	lineCAUECU1	; 	TransmissionLine	lineCAUECU2	; 	TransmissionLine	lineCAUECU3	; 	TransmissionLine	lineCAUECU4	; 	TransmissionLine	lineCERMAG1	; 	
	TransmissionLine	lineCERANT1	; 	TransmissionLine	lineCHIBOG1	; 	TransmissionLine	lineCHIBOG2	;	TransmissionLine	lineCHIBOG3	; 	TransmissionLine	lineCHIBOG4	; 	
	TransmissionLine	lineCHINOR1	; 	TransmissionLine	lineCHINOR2	;	TransmissionLine	lineCDSCER1	; 	TransmissionLine	lineCDSCER2	; 	TransmissionLine	lineCQRTOL1	; 	
	TransmissionLine	lineCQRTOL2	;	TransmissionLine	lineCQRVAL1	; 	TransmissionLine	lineCQRVAL2	; 	TransmissionLine	lineCQRVAL3	; 	TransmissionLine	lineCQRVAL4	;
	TransmissionLine	lineCQRVAL5	; 	TransmissionLine	lineGCMATL1	; 	TransmissionLine	lineGCMATL2	; 	TransmissionLine	lineGCMATL3	;	TransmissionLine	lineGCMNOR1	; 	
	TransmissionLine	lineGCMCUA1	; 	TransmissionLine	lineHUICAU1	; 	TransmissionLine	lineHUICAU2	;	TransmissionLine	lineHUICAU3	; 	TransmissionLine	lineHUICAU4	; 	
	TransmissionLine	lineHUITOL1	; 	TransmissionLine	lineLAMBOG1	;	TransmissionLine	lineLAMBOG2	; 	TransmissionLine	lineLAMTOL1	; 	TransmissionLine	lineLAMTOL2	;
	TransmissionLine	lineMAGSCA1	;	TransmissionLine	lineMAGSCA2	; 	TransmissionLine	lineMAGNOR1	; 	TransmissionLine	lineMAGNOR2	; 	TransmissionLine	lineMAGNOR3	;
	TransmissionLine	lineMAGNOR4	; 	TransmissionLine	lineNORCOR1	; 	TransmissionLine	lineNORCOR2	; 	TransmissionLine	linePAGBOG1	;	TransmissionLine	linePAGBOG2	;
	TransmissionLine	linePAGBOG3	; 	TransmissionLine	linePAGBOG4	; 	TransmissionLine	lineSANBOG1	;	TransmissionLine	lineSANBOG2	; 	TransmissionLine	lineSANCQR1	;
	TransmissionLine	lineSANCQR2	; 	TransmissionLine	lineSANVAL1	;	TransmissionLine	lineTOLBOG1	; 	TransmissionLine	lineTOLBOG2	; 	TransmissionLine	lineTOLBOG3	; 	
	TransmissionLine	lineTOLBOG4	;	TransmissionLine	lineVALCAU1	; 	TransmissionLine	lineVALCAU2	; 
	
	TransmissionLine	lineANTMAG3;
	TransmissionLine	lineANTCERR1;
	TransmissionLine	lineCERRGCM1;
	TransmissionLine	lineMAGBOG1;
	TransmissionLine	lineANTVALL1;
	TransmissionLine	lineVALLBOG1;
	TransmissionLine	lineVALLCAU3;
	TransmissionLine	lineCAUECU5;
	TransmissionLine	lineCERRPAN1;
	TransmissionLine	lineBOGMET3;
	TransmissionLine	lineBOGMET4;
	TransmissionLine	lineCORDCERR2;
	TransmissionLine	lineHUIVALL1;
	TransmissionLine	lineHUIVALL2;
	TransmissionLine	lineCHIBOG5;
	TransmissionLine	lineCHIBOG6;

	
	// archivos para escribir los resultados
	CsvWriter idealWriter = new CsvWriter(Global.idealS); 	// archivo para escribir los resultados del despacho ideal
	CsvWriter realWriter = new CsvWriter(Global.realS);		// archivo para escribir los resultados del despacho real
	CsvWriter contractsWriter = new CsvWriter(Global.contractsS); // archivo para escribir los resultados de la negociación de contratos
	CsvWriter unitsWriter = new CsvWriter(Global.unitsS); // archivo para escribir los resultados de la negociación de contratos
	
	// archivos para leer datos
	CsvReader contractsReader; 	// archivo con la información de los contratos generados
	
	//
	// definición de la lista de contratos del mercado
	//
	public void contractListDefine()
	{
		List<GenerationContract> genContracts; // auxiliar para almacenar temporalmente los detalles de los contratos
		List<GenerationContract> genContractsPC; // auxiliar para almacenar temporalmente los detalles de los contratos pague lo contratado
		List<GenerationContract> genContractsPD; // auxiliar para almacenar temporalmente los detalles de los contratos pague lo demandado
		double[] dispatchedEnergyPD;	// auxiliar para completar los contratos con la energía despachada en contratos PD
		try
		{
			contractsReader = new CsvReader(Global.contractsS);
			this.contracts = Global.rw.readCsvContracts(this.contractsReader, this.generators, this.retailers, this. generationUnits, this.nodes);
			
			// inicialización de la energía despachada en contratos PD
			for (int i = 0; i < this.contracts.size(); i++)
			{
				dispatchedEnergyPD = new double[24];
				this.contracts.get(i).setDispatchedContractPowerPD(dispatchedEnergyPD);
			}
			
			//
			// contratos por unidad de generación
			//
			for(int unit = 0; unit < this.generationUnits.size(); unit++)
			{
				// inicialización de las variables auxiliares
				genContracts = new ArrayList<GenerationContract>();
				genContractsPC = new ArrayList<GenerationContract>();
				genContractsPD = new ArrayList<GenerationContract>();
				
				for(int contract = 0; contract < this.contracts.size(); contract++)
				{
					if(this.generationUnits.get(unit) == this.contracts.get(contract).getGenerationUnit())
					{
						// añadir el contrado a la lista de contratos d ela unidad
						genContracts.add(this.contracts.get(contract));
						
						// si el contrato es del tipo pague lo contratado
						if(this.contracts.get(contract).getContractType().equals("PC"))
						{
							genContractsPC.add(this.contracts.get(contract));
						}
						else // si el contrato es del tipo pague lo demandado
						{
							genContractsPD.add(this.contracts.get(contract));
						}
						for (int h = 0; h < 24; h++)
						{
							// actualización de la energía contratada
							this.generationUnits.get(unit).getContractEnergy()[h] = this.generationUnits.get(unit).getContractEnergy()[h]
									+ this.contracts.get(contract).getContractPower()[h];
						}
					}
				}
				// establecer el listado de contratos de cada unidad de generación
				this.generationUnits.get(unit).setGenerationContracts(genContracts);		// lista general
				this.generationUnits.get(unit).setGenerationContractsPC(genContractsPC);	// lista pague lo contratado
				this.generationUnits.get(unit).setGenerationContractsPD(genContractsPD);	// lista pague lo demandado
			}
			
			//
			// contratos por generador
			//
			for(int gen = 0; gen < this.generators.size(); gen++)
			{
				// inicialización de la variable auxiliar
				genContracts = new ArrayList<GenerationContract>();
				
				for(int contract = 0; contract < this.contracts.size(); contract++)
				{
					if(this.generators.get(gen) == this.contracts.get(contract).getContractSeller())
					{
						genContracts.add(this.contracts.get(contract));
					}
				}
				// establecer el listado de contratos de cada generador
				this.generators.get(gen).setGenerationContracts(genContracts);
			}
			
			//
			// contratos por comercializador
			//
			for(int ret = 0; ret < this.retailers.size(); ret++)
			{
				// inicialización de la variable auxiliar
				genContracts = new ArrayList<GenerationContract>();
				genContractsPC = new ArrayList<GenerationContract>();
				genContractsPD = new ArrayList<GenerationContract>();
				
				for(int contract = 0; contract < this.contracts.size(); contract++)
				{
					if(this.retailers.get(ret) == this.contracts.get(contract).getContractBuyer())
					{
						// añadir el contrato a la lista de contratos del comercializador
						genContracts.add(this.contracts.get(contract));
						
						// si el contrato es del tipo pague lo contratado
						if(this.contracts.get(contract).getContractType().equals("PC"))
						{
							genContractsPC.add(this.contracts.get(contract));
						}
						else // si el contrato es del tipo pague lo demandado
						{
							genContractsPD.add(this.contracts.get(contract));
						}
						
						for (int h = 0; h < 24; h++)
						{
							// actualización de la energía contratada
							this.retailers.get(ret).getContractEnergy()[h] = this.retailers.get(ret).getContractEnergy()[h]
									+ this.contracts.get(contract).getContractPower()[h];
						}
					}
				}
				// establecer el listado de contratos de cada comercializador
				this.retailers.get(ret).setGenerationContracts(genContracts);		// lista general
				this.retailers.get(ret).setGenerationContractsPC(genContractsPC);	// lista pague lo contratado
				this.retailers.get(ret).setGenerationContractsPD(genContractsPD);	// lista pague lo demandado
				
			}
		}
		catch(IOException e) 
   	 	{
	   		 e.printStackTrace();
	   		System.out.println("contractListDefine ->"+e);
	   	}	
	}
		
	// constructor vacío
	public FtrMain(){}
	
	//
	// inicialización de los elementos dle mercado
	//
	public void initializeNodes()
	{
		//
		// nodos
		//
		
		/*
		// 2010
		nodeANTIOQUI	=	new	Node(	0	,	Global.nodesNames[	0	]	,	0	);		
		nodeATLANTIC	=	new	Node(	1	,	Global.nodesNames[	1	]	,	24	);		
		nodeBOGOTA		=	new	Node(	2	,	Global.nodesNames[	2	]	,	48	);		
		nodeBOLIVAR		=	new	Node(	3	,	Global.nodesNames[	3	]	,	72	);		
		nodeCAUCANAR	=	new	Node(	4	,	Global.nodesNames[	4	]	,	96	);		
		nodeCERROMAT	=	new	Node(	5	,	Global.nodesNames[	5	]	,	120	);		
		nodeCHIVOR		=	new	Node(	6	,	Global.nodesNames[	6	]	,	144	);		
		nodeCORDOSUC	=	new	Node(	7	,	Global.nodesNames[	7	]	,	168	);		
		nodeCQR			=	new	Node(	8	,	Global.nodesNames[	8	]	,	192	);		
		nodeGCM			=	new	Node(	9	,	Global.nodesNames[	9	]	,	216	);		
		nodeHUILACAQ	=	new	Node(	10	,	Global.nodesNames[	10	]	,	240	);		
		nodeLAMIEL		=	new	Node(	11	,	Global.nodesNames[	11	]	,	264	);		
		nodeMAGDAMED	=	new	Node(	12	,	Global.nodesNames[	12	]	,	288	);		
		nodeMETA		=	new	Node(	13	,	Global.nodesNames[	13	]	,	312	);		
		nodeNORDESTE	=	new	Node(	14	,	Global.nodesNames[	14	]	,	336	);		
		nodePAGUA		=	new	Node(	15	,	Global.nodesNames[	15	]	,	360	);		
		nodeSANCARLO	=	new	Node(	16	,	Global.nodesNames[	16	]	,	384	);		
		nodeTOLIMA		=	new	Node(	17	,	Global.nodesNames[	17	]	,	408	);		
		nodeVALLECAU	=	new	Node(	18	,	Global.nodesNames[	18	]	,	432	);		
		nodeCOROZO		=	new	Node(	19	,	Global.nodesNames[	19	]	,	456	);		
		nodeCUATRICENTENARIO=new Node(	20	,	Global.nodesNames[	20	]	,	480	);		
		nodeECUADOR220	=	new	Node(	21	,	Global.nodesNames[	21	]	,	504	);		
		*/
		
		// 2020
		nodeANTIOQUI	=	new	Node(	0	,	Global.nodesNames[	0	]	,	0	);
		nodeATLANTIC	=	new	Node(	1	,	Global.nodesNames[	1	]	,	24	);
		nodeBOGOTA	=	new	Node(	2	,	Global.nodesNames[	2	]	,	48	);
		nodeBOLIVAR	=	new	Node(	3	,	Global.nodesNames[	3	]	,	72	);
		nodeCAUCANAR	=	new	Node(	4	,	Global.nodesNames[	4	]	,	96	);
		nodeCERROMAT	=	new	Node(	5	,	Global.nodesNames[	5	]	,	120	);
		nodeCHIVOR	=	new	Node(	6	,	Global.nodesNames[	6	]	,	144	);
		nodeCORDOSUC	=	new	Node(	7	,	Global.nodesNames[	7	]	,	168	);
		nodeCQR	=	new	Node(	8	,	Global.nodesNames[	8	]	,	192	);
		nodeGCM	=	new	Node(	9	,	Global.nodesNames[	9	]	,	216	);
		nodeHUILACAQ	=	new	Node(	10	,	Global.nodesNames[	10	]	,	240	);
		nodeLAMIEL	=	new	Node(	11	,	Global.nodesNames[	11	]	,	264	);
		nodeMAGDAMED	=	new	Node(	12	,	Global.nodesNames[	12	]	,	288	);
		nodeMETA	=	new	Node(	13	,	Global.nodesNames[	13	]	,	312	);
		nodeNORDESTE	=	new	Node(	14	,	Global.nodesNames[	14	]	,	336	);
		nodePAGUA	=	new	Node(	15	,	Global.nodesNames[	15	]	,	360	);
		nodeSANCARLO	=	new	Node(	16	,	Global.nodesNames[	16	]	,	384	);
		nodeTOLIMA	=	new	Node(	17	,	Global.nodesNames[	17	]	,	408	);
		nodeVALLECAU	=	new	Node(	18	,	Global.nodesNames[	18	]	,	432	);
		nodeCOROZO	=	new	Node(	19	,	Global.nodesNames[	19	]	,	456	);
		nodeCUATRICENTENARIO	=	new	Node(	20	,	Global.nodesNames[	20	]	,	480	);
		nodeECUADOR220	=	new	Node(	21	,	Global.nodesNames[	21	]	,	504	);
		nodePANAMA	=	new	Node(	22	,	Global.nodesNames[	22	]	,	528	);
	}
	
	public void initializeGenerators()
	{
		//
		// generadores
		//
		
		/*
		// 2012
		genEPSG	=	new	Generator(	0	,	Global.generatorsCods[	0	]	,	Global.generatorsNames[	0	]	,	0.8	,	0	);
		genGECG	=	new	Generator(	1	,	Global.generatorsCods[	1	]	,	Global.generatorsNames[	1	]	,	0.8	,	24	);
		genENDG	=	new	Generator(	2	,	Global.generatorsCods[	2	]	,	Global.generatorsNames[	2	]	,	0.8	,	48	);
		genCHVG	=	new	Generator(	3	,	Global.generatorsCods[	3	]	,	Global.generatorsNames[	3	]	,	0.8	,	72	);
		genDLRG	=	new	Generator(	4	,	Global.generatorsCods[	4	]	,	Global.generatorsNames[	4	]	,	0.8	,	96	);
		genESSG	=	new	Generator(	5	,	Global.generatorsCods[	5	]	,	Global.generatorsNames[	5	]	,	0.8	,	120	);
		genCVAG	=	new	Generator(	6	,	Global.generatorsCods[	6	]	,	Global.generatorsNames[	6	]	,	0.8	,	144	);
		genCHCG	=	new	Generator(	7	,	Global.generatorsCods[	7	]	,	Global.generatorsNames[	7	]	,	0.8	,	168	);
		genECUG	=	new	Generator(	8	,	Global.generatorsCods[	8	]	,	Global.generatorsNames[	8	]	,	0.8	,	192	);
		genFRSG	=	new	Generator(	9	,	Global.generatorsCods[	9	]	,	Global.generatorsNames[	9	]	,	0.8	,	216	);
		genGNCG	=	new	Generator(	10	,	Global.generatorsCods[	10	]	,	Global.generatorsNames[	10	]	,	0.8	,	240	);
		genEPMG	=	new	Generator(	11	,	Global.generatorsCods[	11	]	,	Global.generatorsNames[	11	]	,	0.8	,	264	);
		genISGG	=	new	Generator(	12	,	Global.generatorsCods[	12	]	,	Global.generatorsNames[	12	]	,	0.8	,	288	);
		genADCG	=	new	Generator(	13	,	Global.generatorsCods[	13	]	,	Global.generatorsNames[	13	]	,	0.8	,	312	);
		genEEPG	=	new	Generator(	14	,	Global.generatorsCods[	14	]	,	Global.generatorsNames[	14	]	,	0.8	,	336	);
		genLCSG	=	new	Generator(	15	,	Global.generatorsCods[	15	]	,	Global.generatorsNames[	15	]	,	0.8	,	360	);
		genCDNG	=	new	Generator(	16	,	Global.generatorsCods[	16	]	,	Global.generatorsNames[	16	]	,	0.8	,	384	);
		genCTMG	=	new	Generator(	17	,	Global.generatorsCods[	17	]	,	Global.generatorsNames[	17	]	,	0.8	,	408	);
		genEECG	=	new	Generator(	18	,	Global.generatorsCods[	18	]	,	Global.generatorsNames[	18	]	,	0.8	,	432	);
		genFACG	=	new	Generator(	19	,	Global.generatorsCods[	19	]	,	Global.generatorsNames[	19	]	,	0.8	,	456	);
		genERCG	=	new	Generator(	20	,	Global.generatorsCods[	20	]	,	Global.generatorsNames[	20	]	,	0.8	,	480	);
		genEGCG	=	new	Generator(	21	,	Global.generatorsCods[	21	]	,	Global.generatorsNames[	21	]	,	0.8	,	504	);
		genCIVG	=	new	Generator(	22	,	Global.generatorsCods[	22	]	,	Global.generatorsNames[	22	]	,	0.8	,	528	);
		genHLAG	=	new	Generator(	23	,	Global.generatorsCods[	23	]	,	Global.generatorsNames[	23	]	,	0.8	,	552	);
		genGEEG	=	new	Generator(	24	,	Global.generatorsCods[	24	]	,	Global.generatorsNames[	24	]	,	0.8	,	576	);
		genCETG	=	new	Generator(	25	,	Global.generatorsCods[	25	]	,	Global.generatorsNames[	25	]	,	0.8	,	600	);
		genTYPG	=	new	Generator(	26	,	Global.generatorsCods[	26	]	,	Global.generatorsNames[	26	]	,	0.8	,	624	);
		genHIMG	=	new	Generator(	27	,	Global.generatorsCods[	27	]	,	Global.generatorsNames[	27	]	,	0.8	,	648	);
		genPRLG	=	new	Generator(	28	,	Global.generatorsCods[	28	]	,	Global.generatorsNames[	28	]	,	0.8	,	672	);
		genTRMG	=	new	Generator(	29	,	Global.generatorsCods[	29	]	,	Global.generatorsNames[	29	]	,	0.8	,	696	);
		genTCDG	=	new	Generator(	30	,	Global.generatorsCods[	30	]	,	Global.generatorsNames[	30	]	,	0.8	,	720	);
		genTEMG	=	new	Generator(	31	,	Global.generatorsCods[	31	]	,	Global.generatorsNames[	31	]	,	0.8	,	744	);
		genTRPG	=	new	Generator(	32	,	Global.generatorsCods[	32	]	,	Global.generatorsNames[	32	]	,	0.8	,	768	);
		genEMUG	=	new	Generator(	33	,	Global.generatorsCods[	33	]	,	Global.generatorsNames[	33	]	,	0.8	,	792	);
		*/
		
		// 2020
		genEPSG	=	new	Generator(	0	,	Global.generatorsCods[	0	]	,	Global.generatorsNames[	0	]	,	0.8	,	0	);
		genGECG	=	new	Generator(	1	,	Global.generatorsCods[	1	]	,	Global.generatorsNames[	1	]	,	0.8	,	24	);
		genENDG	=	new	Generator(	2	,	Global.generatorsCods[	2	]	,	Global.generatorsNames[	2	]	,	0.8	,	48	);
		genCHVG	=	new	Generator(	3	,	Global.generatorsCods[	3	]	,	Global.generatorsNames[	3	]	,	0.8	,	72	);
		genDLRG	=	new	Generator(	4	,	Global.generatorsCods[	4	]	,	Global.generatorsNames[	4	]	,	0.8	,	96	);
		genESSG	=	new	Generator(	5	,	Global.generatorsCods[	5	]	,	Global.generatorsNames[	5	]	,	0.8	,	120	);
		genCVAG	=	new	Generator(	6	,	Global.generatorsCods[	6	]	,	Global.generatorsNames[	6	]	,	0.8	,	144	);
		genCHCG	=	new	Generator(	7	,	Global.generatorsCods[	7	]	,	Global.generatorsNames[	7	]	,	0.8	,	168	);
		genECUG	=	new	Generator(	8	,	Global.generatorsCods[	8	]	,	Global.generatorsNames[	8	]	,	0.8	,	192	);
		genFRSG	=	new	Generator(	9	,	Global.generatorsCods[	9	]	,	Global.generatorsNames[	9	]	,	0.8	,	216	);
		genGNCG	=	new	Generator(	10	,	Global.generatorsCods[	10	]	,	Global.generatorsNames[	10	]	,	0.8	,	240	);
		genEPMG	=	new	Generator(	11	,	Global.generatorsCods[	11	]	,	Global.generatorsNames[	11	]	,	0.8	,	264	);
		genISGG	=	new	Generator(	12	,	Global.generatorsCods[	12	]	,	Global.generatorsNames[	12	]	,	0.8	,	288	);
		genADCG	=	new	Generator(	13	,	Global.generatorsCods[	13	]	,	Global.generatorsNames[	13	]	,	0.8	,	312	);
		genEEPG	=	new	Generator(	14	,	Global.generatorsCods[	14	]	,	Global.generatorsNames[	14	]	,	0.8	,	336	);
		genLCSG	=	new	Generator(	15	,	Global.generatorsCods[	15	]	,	Global.generatorsNames[	15	]	,	0.8	,	360	);
		genCDNG	=	new	Generator(	16	,	Global.generatorsCods[	16	]	,	Global.generatorsNames[	16	]	,	0.8	,	384	);
		genCTMG	=	new	Generator(	17	,	Global.generatorsCods[	17	]	,	Global.generatorsNames[	17	]	,	0.8	,	408	);
		genEECG	=	new	Generator(	18	,	Global.generatorsCods[	18	]	,	Global.generatorsNames[	18	]	,	0.8	,	432	);
		genFACG	=	new	Generator(	19	,	Global.generatorsCods[	19	]	,	Global.generatorsNames[	19	]	,	0.8	,	456	);
		genERCG	=	new	Generator(	20	,	Global.generatorsCods[	20	]	,	Global.generatorsNames[	20	]	,	0.8	,	480	);
		genEGCG	=	new	Generator(	21	,	Global.generatorsCods[	21	]	,	Global.generatorsNames[	21	]	,	0.8	,	504	);
		genCIVG	=	new	Generator(	22	,	Global.generatorsCods[	22	]	,	Global.generatorsNames[	22	]	,	0.8	,	528	);
		genHLAG	=	new	Generator(	23	,	Global.generatorsCods[	23	]	,	Global.generatorsNames[	23	]	,	0.8	,	552	);
		genGEEG	=	new	Generator(	24	,	Global.generatorsCods[	24	]	,	Global.generatorsNames[	24	]	,	0.8	,	576	);
		genCETG	=	new	Generator(	25	,	Global.generatorsCods[	25	]	,	Global.generatorsNames[	25	]	,	0.8	,	600	);
		genTYPG	=	new	Generator(	26	,	Global.generatorsCods[	26	]	,	Global.generatorsNames[	26	]	,	0.8	,	624	);
		genHIMG	=	new	Generator(	27	,	Global.generatorsCods[	27	]	,	Global.generatorsNames[	27	]	,	0.8	,	648	);
		genPRLG	=	new	Generator(	28	,	Global.generatorsCods[	28	]	,	Global.generatorsNames[	28	]	,	0.8	,	672	);
		genTRMG	=	new	Generator(	29	,	Global.generatorsCods[	29	]	,	Global.generatorsNames[	29	]	,	0.8	,	696	);
		genTCDG	=	new	Generator(	30	,	Global.generatorsCods[	30	]	,	Global.generatorsNames[	30	]	,	0.8	,	720	);
		genTEMG	=	new	Generator(	31	,	Global.generatorsCods[	31	]	,	Global.generatorsNames[	31	]	,	0.8	,	744	);
		genTRPG	=	new	Generator(	32	,	Global.generatorsCods[	32	]	,	Global.generatorsNames[	32	]	,	0.8	,	768	);
		genEMUG	=	new	Generator(	33	,	Global.generatorsCods[	33	]	,	Global.generatorsNames[	33	]	,	0.8	,	792	);
		genPOLG	=	new	Generator(	34	,	Global.generatorsCods[	34	]	,	Global.generatorsNames[	34	]	,	0.8	,	816	);
		genHIDG	=	new	Generator(	35	,	Global.generatorsCods[	35	]	,	Global.generatorsNames[	35	]	,	0.8	,	840	);
		genHMVG	=	new	Generator(	36	,	Global.generatorsCods[	36	]	,	Global.generatorsNames[	36	]	,	0.8	,	864	);
		genENAG	=	new	Generator(	37	,	Global.generatorsCods[	37	]	,	Global.generatorsNames[	37	]	,	0.8	,	888	);
		genITUG	=	new	Generator(	38	,	Global.generatorsCods[	38	]	,	Global.generatorsNames[	38	]	,	0.8	,	912	);
		genTENG	=	new	Generator(	39	,	Global.generatorsCods[	39	]	,	Global.generatorsNames[	39	]	,	0.8	,	936	);
		genPANG	=	new	Generator(	40	,	Global.generatorsCods[	40	]	,	Global.generatorsNames[	40	]	,	0.8	,	960	);
		genPUTG	=	new	Generator(	41	,	Global.generatorsCods[	41	]	,	Global.generatorsNames[	41	]	,	0.8	,	984	);


	}
	
	public void initializeGenerationUnits()
	{
		//
		// unidades de generación
		//	
		/*
		GenerationUnit	unitALBAN		=	new	GenerationUnit(	0	,	Global.generationUnitsNames[	0	]	,	genEPSG	,	nodeVALLECAU	,	429.00	,	30.00	,	0	);
		GenerationUnit	unitBARRANQ3	=	new	GenerationUnit(	1	,	Global.generationUnitsNames[	1	]	,	genGECG	,	nodeATLANTIC	,	60.00	,	0.00	,	24	);
		GenerationUnit	unitBARRANQ4	=	new	GenerationUnit(	2	,	Global.generationUnitsNames[	2	]	,	genGECG	,	nodeATLANTIC	,	60.00	,	0.00	,	48	);
		GenerationUnit	unitBETANIA		=	new	GenerationUnit(	3	,	Global.generationUnitsNames[	3	]	,	genENDG	,	nodeHUILACAQ	,	540.00	,	60.00	,	72	);
		GenerationUnit	unitCALIMA1		=	new	GenerationUnit(	4	,	Global.generationUnitsNames[	4	]	,	genEPSG	,	nodeVALLECAU	,	132.00	,	16.00	,	96	);
		GenerationUnit	unitCHIVOR		=	new	GenerationUnit(	5	,	Global.generationUnitsNames[	5	]	,	genCHVG	,	nodeCHIVOR	,	1000.00	,	10.00	,	120	);
		GenerationUnit	unitCOINCAUCA	=	new	GenerationUnit(	6	,	Global.generationUnitsNames[	6	]	,	genDLRG	,	nodeCAUCANAR	,	3.50	,	0.00	,	144	);
		GenerationUnit	unitCOROZO1		=	new	GenerationUnit(	7	,	Global.generationUnitsNames[	7	]	,	genESSG	,	nodeCOROZO	,	55.00	,	0.00	,	168	);
		GenerationUnit	unitCSANCARLOS	=	new	GenerationUnit(	8	,	Global.generationUnitsNames[	8	]	,	genEPSG	,	nodeVALLECAU	,	2.00	,	0.00	,	192	);
		GenerationUnit	unitCTGEMG1		=	new	GenerationUnit(	9	,	Global.generationUnitsNames[	9	]	,	genENDG	,	nodeBOLIVAR	,	61.00	,	0.00	,	216	);
		GenerationUnit	unitCTGEMG2		=	new	GenerationUnit(	10	,	Global.generationUnitsNames[	10	]	,	genENDG	,	nodeBOLIVAR	,	60.00	,	0.00	,	240	);
		GenerationUnit	unitCTGEMG3		=	new	GenerationUnit(	11	,	Global.generationUnitsNames[	11	]	,	genENDG	,	nodeBOLIVAR	,	66.00	,	0.00	,	264	);
		GenerationUnit	unitCVALLEC1	=	new	GenerationUnit(	12	,	Global.generationUnitsNames[	12	]	,	genCVAG	,	nodeVALLECAU	,	31.90	,	0.00	,	288	);
		GenerationUnit	unitDORADA1		=	new	GenerationUnit(	13	,	Global.generationUnitsNames[	13	]	,	genCHCG	,	nodeCQR	,	51.00	,	0.00	,	312	);
		GenerationUnit	unitECUADOR11	=	new	GenerationUnit(	14	,	Global.generationUnitsNames[	14	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	336	);
		GenerationUnit	unitECUADOR12	=	new	GenerationUnit(	15	,	Global.generationUnitsNames[	15	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	360	);
		GenerationUnit	unitECUADOR13	=	new	GenerationUnit(	16	,	Global.generationUnitsNames[	16	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	384	);
		GenerationUnit	unitECUADOR14	=	new	GenerationUnit(	17	,	Global.generationUnitsNames[	17	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	408	);
		GenerationUnit	unitECUADOR21	=	new	GenerationUnit(	18	,	Global.generationUnitsNames[	18	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	432	);
		GenerationUnit	unitECUADOR22	=	new	GenerationUnit(	19	,	Global.generationUnitsNames[	19	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	456	);
		GenerationUnit	unitECUADOR23	=	new	GenerationUnit(	20	,	Global.generationUnitsNames[	20	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	480	);
		GenerationUnit	unitECUADOR24	=	new	GenerationUnit(	21	,	Global.generationUnitsNames[	21	]	,	genECUG	,	nodeECUADOR220	,	0.00	,	0.00	,	504	);
		GenerationUnit	unitESMERALDA	=	new	GenerationUnit(	22	,	Global.generationUnitsNames[	22	]	,	genCHCG	,	nodeCQR	,	30.00	,	0.00	,	528	);
		GenerationUnit	unitFLORES1		=	new	GenerationUnit(	23	,	Global.generationUnitsNames[	23	]	,	genFRSG	,	nodeATLANTIC	,	160.00	,	0.00	,	552	);
		GenerationUnit	unitFLORES21	=	new	GenerationUnit(	24	,	Global.generationUnitsNames[	24	]	,	genFRSG	,	nodeATLANTIC	,	112.00	,	0.00	,	576	);
		GenerationUnit	unitFLORES3		=	new	GenerationUnit(	25	,	Global.generationUnitsNames[	25	]	,	genFRSG	,	nodeATLANTIC	,	169.00	,	0.00	,	600	);
		GenerationUnit	unitFLORESIVB	=	new	GenerationUnit(	26	,	Global.generationUnitsNames[	26	]	,	genFRSG	,	nodeATLANTIC	,	450.00	,	100.00	,	624	);
		GenerationUnit	unitFLORIDA2	=	new	GenerationUnit(	27	,	Global.generationUnitsNames[	27	]	,	genGNCG	,	nodeCAUCANAR	,	19.90	,	0.00	,	648	);
		GenerationUnit	unitGUAJIR11	=	new	GenerationUnit(	28	,	Global.generationUnitsNames[	28	]	,	genGECG	,	nodeGCM	,	145.00	,	0.00	,	672	);
		GenerationUnit	unitGUAJIR21	=	new	GenerationUnit(	29	,	Global.generationUnitsNames[	29	]	,	genGECG	,	nodeGCM	,	151.00	,	0.00	,	696	);
		GenerationUnit	unitGUATAPE		=	new	GenerationUnit(	30	,	Global.generationUnitsNames[	30	]	,	genEPMG	,	nodeANTIOQUI	,	560.00	,	0.00	,	720	);
		GenerationUnit	unitGUATRON		=	new	GenerationUnit(	31	,	Global.generationUnitsNames[	31	]	,	genEPMG	,	nodeANTIOQUI	,	512.00	,	35.00	,	744	);
		GenerationUnit	unitGUAVIO		=	new	GenerationUnit(	32	,	Global.generationUnitsNames[	32	]	,	genENDG	,	nodeBOGOTA	,	1200.00	,	60.00	,	768	);
		GenerationUnit	unitINSULA		=	new	GenerationUnit(	33	,	Global.generationUnitsNames[	33	]	,	genCHCG	,	nodeCQR	,	19.90	,	0.00	,	792	);
		GenerationUnit	unitJAGUAS		=	new	GenerationUnit(	34	,	Global.generationUnitsNames[	34	]	,	genISGG	,	nodeANTIOQUI	,	170.00	,	15.00	,	816	);
		GenerationUnit	unitLATASAJERA	=	new	GenerationUnit(	35	,	Global.generationUnitsNames[	35	]	,	genEPMG	,	nodeANTIOQUI	,	306.00	,	0.00	,	840	);
		GenerationUnit	unitMAGUAFRE	=	new	GenerationUnit(	36	,	Global.generationUnitsNames[	36	]	,	genADCG	,	nodeANTIOQUI	,	7.05	,	0.00	,	864	);
		GenerationUnit	unitMANTIOQ1	=	new	GenerationUnit(	37	,	Global.generationUnitsNames[	37	]	,	genEPMG	,	nodeANTIOQUI	,	134.27	,	0.00	,	888	);
		GenerationUnit	unitMBELMONTE	=	new	GenerationUnit(	38	,	Global.generationUnitsNames[	38	]	,	genEEPG	,	nodeCQR	,	3.40	,	0.00	,	912	);
		GenerationUnit	unitMBOGOTA1	=	new	GenerationUnit(	39	,	Global.generationUnitsNames[	39	]	,	genENDG	,	nodeBOGOTA	,	115.00	,	0.00	,	936	);
		GenerationUnit	unitMCALDERAS	=	new	GenerationUnit(	40	,	Global.generationUnitsNames[	40	]	,	genISGG	,	nodeANTIOQUI	,	19.90	,	0.00	,	960	);
		GenerationUnit	unitMCARUQUIA	=	new	GenerationUnit(	41	,	Global.generationUnitsNames[	41	]	,	genLCSG	,	nodeANTIOQUI	,	9.50	,	0.00	,	984	);
		GenerationUnit	unitMCASCADA1	=	new	GenerationUnit(	42	,	Global.generationUnitsNames[	42	]	,	genLCSG	,	nodeANTIOQUI	,	2.30	,	0.00	,	1008	);
		GenerationUnit	unitMCAUCAN1	=	new	GenerationUnit(	43	,	Global.generationUnitsNames[	43	]	,	genCDNG	,	nodeCAUCANAR	,	7.33	,	0.00	,	1032	);
		GenerationUnit	unitMCAUCAN2	=	new	GenerationUnit(	44	,	Global.generationUnitsNames[	44	]	,	genGNCG	,	nodeCAUCANAR	,	13.77	,	0.00	,	1056	);
		GenerationUnit	unitMCIMARR1	=	new	GenerationUnit(	45	,	Global.generationUnitsNames[	45	]	,	genCTMG	,	nodeNORDESTE	,	19.90	,	0.00	,	1080	);
		GenerationUnit	unitMCQR1		=	new	GenerationUnit(	46	,	Global.generationUnitsNames[	46	]	,	genCHCG	,	nodeCQR	,	7.60	,	0.00	,	1104	);
		GenerationUnit	unitMCUNDINAMARCA=	new	GenerationUnit(	47	,	Global.generationUnitsNames[	47	]	,	genEECG	,	nodeBOGOTA	,	15.60	,	0.00	,	1128	);
		GenerationUnit	unitMCURRUCU	=	new	GenerationUnit(	48	,	Global.generationUnitsNames[	48	]	,	genFACG	,	nodeTOLIMA	,	1.25	,	0.00	,	1152	);
		GenerationUnit	unitMELBOSQUE	=	new	GenerationUnit(	49	,	Global.generationUnitsNames[	49	]	,	genERCG	,	nodeCQR	,	2.28	,	0.00	,	1176	);
		GenerationUnit	unitMEMCALI		=	new	GenerationUnit(	50	,	Global.generationUnitsNames[	50	]	,	genEGCG	,	nodeVALLECAU	,	0.00	,	0.00	,	1200	);
		GenerationUnit	unitMERILEC1	=	new	GenerationUnit(	51	,	Global.generationUnitsNames[	51	]	,	genCIVG	,	nodeNORDESTE	,	167.00	,	0.00	,	1224	);
		GenerationUnit	unitMGUANAQUITA	=	new	GenerationUnit(	52	,	Global.generationUnitsNames[	52	]	,	genLCSG	,	nodeANTIOQUI	,	9.50	,	0.00	,	1248	);
		GenerationUnit	unitMHUILAQ1	=	new	GenerationUnit(	53	,	Global.generationUnitsNames[	53	]	,	genHLAG	,	nodeHUILACAQ	,	2.69	,	0.00	,	1272	);
		GenerationUnit	unitMIEL1		=	new	GenerationUnit(	54	,	Global.generationUnitsNames[	54	]	,	genISGG	,	nodeLAMIEL	,	396.00	,	20.00	,	1296	);
		GenerationUnit	unitMJEPIRAC	=	new	GenerationUnit(	55	,	Global.generationUnitsNames[	55	]	,	genEPMG	,	nodeGCM	,	18.42	,	0.00	,	1320	);
		GenerationUnit	unitMMORRO1		=	new	GenerationUnit(	56	,	Global.generationUnitsNames[	56	]	,	genCTMG	,	nodeNORDESTE	,	19.90	,	0.00	,	1344	);
		GenerationUnit	unitMMORRO2		=	new	GenerationUnit(	57	,	Global.generationUnitsNames[	57	]	,	genCTMG	,	nodeNORDESTE	,	19.90	,	0.00	,	1368	);
		GenerationUnit	unitMNLIBARE	=	new	GenerationUnit(	58	,	Global.generationUnitsNames[	58	]	,	genEEPG	,	nodeCQR	,	5.10	,	0.00	,	1392	);
		GenerationUnit	unitMNORDE1		=	new	GenerationUnit(	59	,	Global.generationUnitsNames[	59	]	,	genESSG	,	nodeNORDESTE	,	20.95	,	0.00	,	1416	);
		GenerationUnit	unitMPRADO4		=	new	GenerationUnit(	60	,	Global.generationUnitsNames[	60	]	,	genEPSG	,	nodeTOLIMA	,	5.00	,	0.00	,	1440	);
		GenerationUnit	unitMRIOMAYO	=	new	GenerationUnit(	61	,	Global.generationUnitsNames[	61	]	,	genCDNG	,	nodeCAUCANAR	,	19.80	,	0.00	,	1464	);
		GenerationUnit	unitMSANTANA	=	new	GenerationUnit(	62	,	Global.generationUnitsNames[	62	]	,	genENDG	,	nodeBOGOTA	,	8.00	,	0.00	,	1488	);
		GenerationUnit	unitMSANTARITA	=	new	GenerationUnit(	63	,	Global.generationUnitsNames[	63	]	,	genFACG	,	nodeANTIOQUI	,	1.30	,	0.00	,	1512	);
		GenerationUnit	unitMSANTIAGO	=	new	GenerationUnit(	64	,	Global.generationUnitsNames[	64	]	,	genGEEG	,	nodeANTIOQUI	,	2.80	,	0.00	,	1536	);
		GenerationUnit	unitMTOLIMA1	=	new	GenerationUnit(	65	,	Global.generationUnitsNames[	65	]	,	genGNCG	,	nodeTOLIMA	,	8.40	,	0.00	,	1560	);
		GenerationUnit	unitMTULUA		=	new	GenerationUnit(	66	,	Global.generationUnitsNames[	66	]	,	genCETG	,	nodeVALLECAU	,	14.19	,	0.00	,	1584	);
		GenerationUnit	unitMVALLEC1	=	new	GenerationUnit(	67	,	Global.generationUnitsNames[	67	]	,	genEPSG	,	nodeVALLECAU	,	8.50	,	0.00	,	1608	);
		GenerationUnit	unitMYOPAL1		=	new	GenerationUnit(	68	,	Global.generationUnitsNames[	68	]	,	genTYPG	,	nodeNORDESTE	,	0.00	,	0.00	,	1632	);
		GenerationUnit	unitM_AMAIME	=	new	GenerationUnit(	69	,	Global.generationUnitsNames[	69	]	,	genEPSG	,	nodeVALLECAU	,	19.90	,	0.00	,	1656	);
		GenerationUnit	unitM_PROVIDEN	=	new	GenerationUnit(	70	,	Global.generationUnitsNames[	70	]	,	genDLRG	,	nodeVALLECAU	,	19.90	,	0.00	,	1680	);
		GenerationUnit	unitPAGUA		=	new	GenerationUnit(	71	,	Global.generationUnitsNames[	71	]	,	genENDG	,	nodePAGUA	,	600.00	,	74.00	,	1704	);
		GenerationUnit	unitPAIPA1		=	new	GenerationUnit(	72	,	Global.generationUnitsNames[	72	]	,	genHIMG	,	nodeNORDESTE	,	31.00	,	0.00	,	1728	);
		GenerationUnit	unitPAIPA2		=	new	GenerationUnit(	73	,	Global.generationUnitsNames[	73	]	,	genHIMG	,	nodeNORDESTE	,	70.00	,	0.00	,	1752	);
		GenerationUnit	unitPAIPA3		=	new	GenerationUnit(	74	,	Global.generationUnitsNames[	74	]	,	genHIMG	,	nodeNORDESTE	,	70.00	,	0.00	,	1776	);
		GenerationUnit	unitPAIPA4		=	new	GenerationUnit(	75	,	Global.generationUnitsNames[	75	]	,	genHIMG	,	nodeNORDESTE	,	150.00	,	0.00	,	1800	);
		GenerationUnit	unitPALENQ3		=	new	GenerationUnit(	76	,	Global.generationUnitsNames[	76	]	,	genESSG	,	nodeNORDESTE	,	13.61	,	0.00	,	1824	);
		GenerationUnit	unitPLAYAS		=	new	GenerationUnit(	77	,	Global.generationUnitsNames[	77	]	,	genEPMG	,	nodeANTIOQUI	,	201.00	,	55.00	,	1848	);
		GenerationUnit	unitPORCE2		=	new	GenerationUnit(	78	,	Global.generationUnitsNames[	78	]	,	genEPMG	,	nodeANTIOQUI	,	405.00	,	75.00	,	1872	);
		GenerationUnit	unitPORCE3		=	new	GenerationUnit(	79	,	Global.generationUnitsNames[	79	]	,	genEPMG	,	nodeANTIOQUI	,	660.00	,	125.00	,	1896	);
		GenerationUnit	unitPORCE3P		=	new	GenerationUnit(	80	,	Global.generationUnitsNames[	80	]	,	genEPMG	,	nodeANTIOQUI	,	660.00	,	125.00	,	1920	);
		GenerationUnit	unitPRADO		=	new	GenerationUnit(	81	,	Global.generationUnitsNames[	81	]	,	genEPSG	,	nodeTOLIMA	,	46.00	,	8.00	,	1944	);
		GenerationUnit	unitPROELEC1	=	new	GenerationUnit(	82	,	Global.generationUnitsNames[	82	]	,	genPRLG	,	nodeBOLIVAR	,	45.00	,	0.00	,	1968	);
		GenerationUnit	unitPROELEC2	=	new	GenerationUnit(	83	,	Global.generationUnitsNames[	83	]	,	genPRLG	,	nodeBOLIVAR	,	45.00	,	0.00	,	1992	);
		GenerationUnit	unitRPIEDRAS	=	new	GenerationUnit(	84	,	Global.generationUnitsNames[	84	]	,	genCIVG	,	nodeANTIOQUI	,	19.90	,	0.00	,	2016	);
		GenerationUnit	unitSALVAJINA	=	new	GenerationUnit(	85	,	Global.generationUnitsNames[	85	]	,	genEPSG	,	nodeVALLECAU	,	285.00	,	0.00	,	2040	);
		GenerationUnit	unitSANCARLOS	=	new	GenerationUnit(	86	,	Global.generationUnitsNames[	86	]	,	genISGG	,	nodeSANCARLO	,	1240.00	,	10.00	,	2064	);
		GenerationUnit	unitSANFRANCISCO=	new	GenerationUnit(	87	,	Global.generationUnitsNames[	87	]	,	genCHCG	,	nodeCQR	,	135.00	,	12.00	,	2088	);
		GenerationUnit	unitTASAJER1	=	new	GenerationUnit(	88	,	Global.generationUnitsNames[	88	]	,	genTRMG	,	nodeNORDESTE	,	155.00	,	0.00	,	2112	);
		GenerationUnit	unitTCANDEL1	=	new	GenerationUnit(	89	,	Global.generationUnitsNames[	89	]	,	genTCDG	,	nodeBOLIVAR	,	157.00	,	0.00	,	2136	);
		GenerationUnit	unitTCANDEL2	=	new	GenerationUnit(	90	,	Global.generationUnitsNames[	90	]	,	genTCDG	,	nodeBOLIVAR	,	157.00	,	0.00	,	2160	);
		GenerationUnit	unitTCENTRO1	=	new	GenerationUnit(	91	,	Global.generationUnitsNames[	91	]	,	genISGG	,	nodeMAGDAMED	,	276.00	,	0.00	,	2184	);
		GenerationUnit	unitTEBSA		=	new	GenerationUnit(	92	,	Global.generationUnitsNames[	92	]	,	genGECG	,	nodeATLANTIC	,	791.00	,	0.00	,	2208	);
		GenerationUnit	unitTEMCALI		=	new	GenerationUnit(	93	,	Global.generationUnitsNames[	93	]	,	genTEMG	,	nodeVALLECAU	,	229.00	,	0.00	,	2232	);
		GenerationUnit	unitTPIEDRAS	=	new	GenerationUnit(	94	,	Global.generationUnitsNames[	94	]	,	genTRPG	,	nodeTOLIMA	,	3.75	,	0.00	,	2256	);
		GenerationUnit	unitTSIERRA		=	new	GenerationUnit(	95	,	Global.generationUnitsNames[	95	]	,	genEPMG	,	nodeMAGDAMED	,	460.00	,	0.00	,	2280	);
		GenerationUnit	unitTVALLE		=	new	GenerationUnit(	96	,	Global.generationUnitsNames[	96	]	,	genEPSG	,	nodeVALLECAU	,	205.00	,	0.00	,	2304	);
		GenerationUnit	unitTYOPAL2		=	new	GenerationUnit(	97	,	Global.generationUnitsNames[	97	]	,	genTYPG	,	nodeNORDESTE	,	30.00	,	0.00	,	2328	);
		GenerationUnit	unitURRA		=	new	GenerationUnit(	98	,	Global.generationUnitsNames[	98	]	,	genEMUG	,	nodeCERROMAT	,	338.00	,	63.00	,	2352	);
		GenerationUnit	unitVENEZUE1	=	new	GenerationUnit(	99	,	Global.generationUnitsNames[	99	]	,	genISGG	,	nodeCUATRICENTENARIO	,	150.00	,	0.00	,	2376	);
		GenerationUnit	unitZIPAEMG2	=	new	GenerationUnit(	100	,	Global.generationUnitsNames[	100	]	,	genENDG	,	nodeBOGOTA	,	34.00	,	0.00	,	2400	);
		GenerationUnit	unitZIPAEMG3	=	new	GenerationUnit(	101	,	Global.generationUnitsNames[	101	]	,	genENDG	,	nodeBOGOTA	,	63.00	,	0.00	,	2424	);
		GenerationUnit	unitZIPAEMG4	=	new	GenerationUnit(	102	,	Global.generationUnitsNames[	102	]	,	genENDG	,	nodeBOGOTA	,	64.00	,	0.00	,	2448	);
		GenerationUnit	unitZIPAEMG5	=	new	GenerationUnit(	103	,	Global.generationUnitsNames[	103	]	,	genENDG	,	nodeBOGOTA	,	64.00	,	0.00	,	2472	);
		*/
		
		/*
		// 2012
		unitALBAN	=	new	GenerationUnit(	0	,	Global.generationUnitsNames[	0	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	429.00	,	30.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	,	98953.61781	}	,	0	);
		unitBARRANQ3	=	new	GenerationUnit(	1	,	Global.generationUnitsNames[	1	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	60.00	,	0.00	,	37069992.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	,	407249.8904	}	,	24	);
		unitBARRANQ4	=	new	GenerationUnit(	2	,	Global.generationUnitsNames[	2	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	60.00	,	0.00	,	37069992.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	,	408126.4781	}	,	48	);
		unitBETANIA	=	new	GenerationUnit(	3	,	Global.generationUnitsNames[	3	]	,	genENDG	,	"	H	"	,	nodeHUILACAQ	,	540.00	,	60.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	,	138232.6603	}	,	72	);
		unitCALIMA1	=	new	GenerationUnit(	4	,	Global.generationUnitsNames[	4	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	132.00	,	16.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	,	315035.3671	}	,	96	);
		unitCHIVOR	=	new	GenerationUnit(	5	,	Global.generationUnitsNames[	5	]	,	genCHVG	,	"	H	"	,	nodeCHIVOR	,	1000.00	,	10.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	,	170110.1384	}	,	120	);
		unitCOINCAUCA	=	new	GenerationUnit(	6	,	Global.generationUnitsNames[	6	]	,	genDLRG	,	"	C	"	,	nodeCAUCANAR	,	3.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	,	714410	}	,	144	);
		unitCOROZO1	=	new	GenerationUnit(	7	,	Global.generationUnitsNames[	7	]	,	genESSG	,	"	E	"	,	nodeCOROZO	,	55.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	,	420000	}	,	168	);
		unitCSANCARLOS	=	new	GenerationUnit(	8	,	Global.generationUnitsNames[	8	]	,	genEPSG	,	"	C	"	,	nodeVALLECAU	,	2.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	,	533307	}	,	192	);
		unitCTGEMG1	=	new	GenerationUnit(	9	,	Global.generationUnitsNames[	9	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	61.00	,	0.00	,	30035123.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	,	472659.9	}	,	216	);
		unitCTGEMG2	=	new	GenerationUnit(	10	,	Global.generationUnitsNames[	10	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	60.00	,	0.00	,	30035123.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	,	467387.1699	}	,	240	);
		unitCTGEMG3	=	new	GenerationUnit(	11	,	Global.generationUnitsNames[	11	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	66.00	,	0.00	,	30388533.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	,	443602.1521	}	,	264	);
		unitCVALLEC1	=	new	GenerationUnit(	12	,	Global.generationUnitsNames[	12	]	,	genCVAG	,	"	C	"	,	nodeVALLECAU	,	31.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	,	791950	}	,	288	);
		unitDORADA1	=	new	GenerationUnit(	13	,	Global.generationUnitsNames[	13	]	,	genCHCG	,	"	T	"	,	nodeCQR	,	51.00	,	0.00	,	14842073.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	,	234533.2151	}	,	312	);
		unitECUADOR11	=	new	GenerationUnit(	14	,	Global.generationUnitsNames[	14	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	,	154620.9096	}	,	336	);
		unitECUADOR12	=	new	GenerationUnit(	15	,	Global.generationUnitsNames[	15	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	,	786106	}	,	360	);
		unitECUADOR13	=	new	GenerationUnit(	16	,	Global.generationUnitsNames[	16	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	,	865694	}	,	384	);
		unitECUADOR14	=	new	GenerationUnit(	17	,	Global.generationUnitsNames[	17	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	,	137108	}	,	408	);
		unitECUADOR21	=	new	GenerationUnit(	18	,	Global.generationUnitsNames[	18	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	,	154538.7986	}	,	432	);
		unitECUADOR22	=	new	GenerationUnit(	19	,	Global.generationUnitsNames[	19	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	,	707397	}	,	456	);
		unitECUADOR23	=	new	GenerationUnit(	20	,	Global.generationUnitsNames[	20	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	,	97127	}	,	480	);
		unitECUADOR24	=	new	GenerationUnit(	21	,	Global.generationUnitsNames[	21	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	,	92665	}	,	504	);
		unitESMERALDA	=	new	GenerationUnit(	22	,	Global.generationUnitsNames[	22	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	30.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	,	398374	}	,	528	);
		unitFLORES1	=	new	GenerationUnit(	23	,	Global.generationUnitsNames[	23	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	160.00	,	0.00	,	30181065.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	,	158476.6055	}	,	552	);
		unitFLORES21	=	new	GenerationUnit(	24	,	Global.generationUnitsNames[	24	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	112.00	,	0.00	,	22276979.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	,	152919.0712	}	,	576	);
		unitFLORES3	=	new	GenerationUnit(	25	,	Global.generationUnitsNames[	25	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	169.00	,	0.00	,	29465280.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	,	96739.78493	}	,	600	);
		unitFLORESIVB	=	new	GenerationUnit(	26	,	Global.generationUnitsNames[	26	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	450.00	,	100.00	,	58145114.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	,	332500	}	,	624	);
		unitFLORIDA2	=	new	GenerationUnit(	27	,	Global.generationUnitsNames[	27	]	,	genGNCG	,	"	H	"	,	nodeCAUCANAR	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	,	781785	}	,	648	);
		unitGUAJIR11	=	new	GenerationUnit(	28	,	Global.generationUnitsNames[	28	]	,	genGECG	,	"	T	"	,	nodeGCM	,	145.00	,	0.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	,	188499.0479	}	,	672	);
		unitGUAJIR21	=	new	GenerationUnit(	29	,	Global.generationUnitsNames[	29	]	,	genGECG	,	"	T	"	,	nodeGCM	,	151.00	,	0.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	,	181160.6726	}	,	696	);
		unitGUATAPE	=	new	GenerationUnit(	30	,	Global.generationUnitsNames[	30	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	560.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	,	70438.50548	}	,	720	);
		unitGUATRON	=	new	GenerationUnit(	31	,	Global.generationUnitsNames[	31	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	512.00	,	35.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	,	83775.32055	}	,	744	);
		unitGUAVIO	=	new	GenerationUnit(	32	,	Global.generationUnitsNames[	32	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	1200.00	,	60.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	,	103928.0932	}	,	768	);
		unitINSULA	=	new	GenerationUnit(	33	,	Global.generationUnitsNames[	33	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	,	647284	}	,	792	);
		unitJAGUAS	=	new	GenerationUnit(	34	,	Global.generationUnitsNames[	34	]	,	genISGG	,	"	H	"	,	nodeANTIOQUI	,	170.00	,	15.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	,	79668.97534	}	,	816	);
		unitLATASAJERA	=	new	GenerationUnit(	35	,	Global.generationUnitsNames[	35	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	306.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	,	63663.95342	}	,	840	);
		unitMAGUAFRE	=	new	GenerationUnit(	36	,	Global.generationUnitsNames[	36	]	,	genADCG	,	"	H	"	,	nodeANTIOQUI	,	7.05	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	,	461362	}	,	864	);
		unitMANTIOQ1	=	new	GenerationUnit(	37	,	Global.generationUnitsNames[	37	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	134.27	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	,	296625	}	,	888	);
		unitMBELMONTE	=	new	GenerationUnit(	38	,	Global.generationUnitsNames[	38	]	,	genEEPG	,	"	H	"	,	nodeCQR	,	3.40	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	,	515567	}	,	912	);
		unitMBOGOTA1	=	new	GenerationUnit(	39	,	Global.generationUnitsNames[	39	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	115.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	,	250857	}	,	936	);
		unitMCALDERAS	=	new	GenerationUnit(	40	,	Global.generationUnitsNames[	40	]	,	genISGG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	,	218742	}	,	960	);
		unitMCARUQUIA	=	new	GenerationUnit(	41	,	Global.generationUnitsNames[	41	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	9.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	,	455506	}	,	984	);
		unitMCASCADA1	=	new	GenerationUnit(	42	,	Global.generationUnitsNames[	42	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	2.30	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	,	539591	}	,	1008	);
		unitMCAUCAN1	=	new	GenerationUnit(	43	,	Global.generationUnitsNames[	43	]	,	genCDNG	,	"	H	"	,	nodeCAUCANAR	,	7.33	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	,	255202	}	,	1032	);
		unitMCAUCAN2	=	new	GenerationUnit(	44	,	Global.generationUnitsNames[	44	]	,	genGNCG	,	"	H	"	,	nodeCAUCANAR	,	13.77	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	,	464322	}	,	1056	);
		unitMCIMARR1	=	new	GenerationUnit(	45	,	Global.generationUnitsNames[	45	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	,	151447	}	,	1080	);
		unitMCQR1	=	new	GenerationUnit(	46	,	Global.generationUnitsNames[	46	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	7.60	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	,	227895	}	,	1104	);
		unitMCUNDINAMARCA	=	new	GenerationUnit(	47	,	Global.generationUnitsNames[	47	]	,	genEECG	,	"	H	"	,	nodeBOGOTA	,	15.60	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	,	282000	}	,	1128	);
		unitMCURRUCU	=	new	GenerationUnit(	48	,	Global.generationUnitsNames[	48	]	,	genFACG	,	"	H	"	,	nodeTOLIMA	,	1.25	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	,	127417	}	,	1152	);
		unitMELBOSQUE	=	new	GenerationUnit(	49	,	Global.generationUnitsNames[	49	]	,	genERCG	,	"	H	"	,	nodeCQR	,	2.28	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	,	810225	}	,	1176	);
		unitMEMCALI	=	new	GenerationUnit(	50	,	Global.generationUnitsNames[	50	]	,	genEGCG	,	"	H	"	,	nodeVALLECAU	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	,	374250	}	,	1200	);
		unitMERILEC1	=	new	GenerationUnit(	51	,	Global.generationUnitsNames[	51	]	,	genCIVG	,	"	T	"	,	nodeNORDESTE	,	167.00	,	0.00	,	52487028.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	,	282924.5274	}	,	1224	);
		unitMGUANAQUITA	=	new	GenerationUnit(	52	,	Global.generationUnitsNames[	52	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	9.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	,	61030	}	,	1248	);
		unitMHUILAQ1	=	new	GenerationUnit(	53	,	Global.generationUnitsNames[	53	]	,	genHLAG	,	"	H	"	,	nodeHUILACAQ	,	2.69	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	,	372766	}	,	1272	);
		unitMIEL1	=	new	GenerationUnit(	54	,	Global.generationUnitsNames[	54	]	,	genISGG	,	"	H	"	,	nodeLAMIEL	,	396.00	,	20.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	,	117349.2685	}	,	1296	);
		unitMJEPIRAC	=	new	GenerationUnit(	55	,	Global.generationUnitsNames[	55	]	,	genEPMG	,	"	E	"	,	nodeGCM	,	18.42	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	,	623758	}	,	1320	);
		unitMMORRO1	=	new	GenerationUnit(	56	,	Global.generationUnitsNames[	56	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	,	495314	}	,	1344	);
		unitMMORRO2	=	new	GenerationUnit(	57	,	Global.generationUnitsNames[	57	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	,	628819	}	,	1368	);
		unitMNLIBARE	=	new	GenerationUnit(	58	,	Global.generationUnitsNames[	58	]	,	genEEPG	,	"	H	"	,	nodeCQR	,	5.10	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	,	555638	}	,	1392	);
		unitMNORDE1	=	new	GenerationUnit(	59	,	Global.generationUnitsNames[	59	]	,	genESSG	,	"	H	"	,	nodeNORDESTE	,	20.95	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	,	412658	}	,	1416	);
		unitMPRADO4	=	new	GenerationUnit(	60	,	Global.generationUnitsNames[	60	]	,	genEPSG	,	"	H	"	,	nodeTOLIMA	,	5.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	,	681119	}	,	1440	);
		unitMRIOMAYO	=	new	GenerationUnit(	61	,	Global.generationUnitsNames[	61	]	,	genCDNG	,	"	H	"	,	nodeCAUCANAR	,	19.80	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	,	392559	}	,	1464	);
		unitMSANTANA	=	new	GenerationUnit(	62	,	Global.generationUnitsNames[	62	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	8.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	,	542553	}	,	1488	);
		unitMSANTARITA	=	new	GenerationUnit(	63	,	Global.generationUnitsNames[	63	]	,	genFACG	,	"	H	"	,	nodeANTIOQUI	,	1.30	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	,	667177	}	,	1512	);
		unitMSANTIAGO	=	new	GenerationUnit(	64	,	Global.generationUnitsNames[	64	]	,	genGEEG	,	"	H	"	,	nodeANTIOQUI	,	2.80	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	,	56969	}	,	1536	);
		unitMTOLIMA1	=	new	GenerationUnit(	65	,	Global.generationUnitsNames[	65	]	,	genGNCG	,	"	H	"	,	nodeTOLIMA	,	8.40	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	,	431333	}	,	1560	);
		unitMTULUA	=	new	GenerationUnit(	66	,	Global.generationUnitsNames[	66	]	,	genCETG	,	"	H	"	,	nodeVALLECAU	,	14.19	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	,	445221	}	,	1584	);
		unitMVALLEC1	=	new	GenerationUnit(	67	,	Global.generationUnitsNames[	67	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	8.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	,	348859	}	,	1608	);
		unitMYOPAL1	=	new	GenerationUnit(	68	,	Global.generationUnitsNames[	68	]	,	genTYPG	,	"	H	"	,	nodeNORDESTE	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	,	559690	}	,	1632	);
		unitM_AMAIME	=	new	GenerationUnit(	69	,	Global.generationUnitsNames[	69	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	,	228909	}	,	1656	);
		unitM_PROVIDEN	=	new	GenerationUnit(	70	,	Global.generationUnitsNames[	70	]	,	genDLRG	,	"	C	"	,	nodeVALLECAU	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	,	216506	}	,	1680	);
		unitPAGUA	=	new	GenerationUnit(	71	,	Global.generationUnitsNames[	71	]	,	genENDG	,	"	H	"	,	nodePAGUA	,	600.00	,	74.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	,	72156.91507	}	,	1704	);
		unitPAIPA1	=	new	GenerationUnit(	72	,	Global.generationUnitsNames[	72	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	31.00	,	0.00	,	58068650.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	,	112998.9589	}	,	1728	);
		unitPAIPA2	=	new	GenerationUnit(	73	,	Global.generationUnitsNames[	73	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	70.00	,	0.00	,	73094794.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	,	138317.4767	}	,	1752	);
		unitPAIPA3	=	new	GenerationUnit(	74	,	Global.generationUnitsNames[	74	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	70.00	,	0.00	,	73094794.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	,	127661.3068	}	,	1776	);
		unitPAIPA4	=	new	GenerationUnit(	75	,	Global.generationUnitsNames[	75	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	150.00	,	0.00	,	54914904.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	,	105072.8699	}	,	1800	);
		unitPALENQ3	=	new	GenerationUnit(	76	,	Global.generationUnitsNames[	76	]	,	genESSG	,	"	T	"	,	nodeNORDESTE	,	13.61	,	0.00	,	4883421.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	,	810657.626	}	,	1824	);
		unitPLAYAS	=	new	GenerationUnit(	77	,	Global.generationUnitsNames[	77	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	201.00	,	55.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	,	58479.3726	}	,	1848	);
		unitPORCE2	=	new	GenerationUnit(	78	,	Global.generationUnitsNames[	78	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	405.00	,	75.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	,	82651.5726	}	,	1872	);
		unitPORCE3	=	new	GenerationUnit(	79	,	Global.generationUnitsNames[	79	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	660.00	,	125.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	,	17576.61507	}	,	1896	);
		unitPORCE3P	=	new	GenerationUnit(	80	,	Global.generationUnitsNames[	80	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	660.00	,	125.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	,	847738	}	,	1920	);
		unitPRADO	=	new	GenerationUnit(	81	,	Global.generationUnitsNames[	81	]	,	genEPSG	,	"	H	"	,	nodeTOLIMA	,	46.00	,	8.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	,	212067.5753	}	,	1944	);
		unitPROELEC1	=	new	GenerationUnit(	82	,	Global.generationUnitsNames[	82	]	,	genPRLG	,	"	T	"	,	nodeBOLIVAR	,	45.00	,	0.00	,	19451005.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	}	,	1968	);
		unitPROELEC2	=	new	GenerationUnit(	83	,	Global.generationUnitsNames[	83	]	,	genPRLG	,	"	T	"	,	nodeBOLIVAR	,	45.00	,	0.00	,	19451005.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	,	212480.6603	}	,	1992	);
		unitRPIEDRAS	=	new	GenerationUnit(	84	,	Global.generationUnitsNames[	84	]	,	genCIVG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	,	157128	}	,	2016	);
		unitSALVAJINA	=	new	GenerationUnit(	85	,	Global.generationUnitsNames[	85	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	285.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	,	421421.7123	}	,	2040	);
		unitSANCARLOS	=	new	GenerationUnit(	86	,	Global.generationUnitsNames[	86	]	,	genISGG	,	"	H	"	,	nodeSANCARLO	,	1240.00	,	10.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	,	72520.26986	}	,	2064	);
		unitSANFRANCISCO	=	new	GenerationUnit(	87	,	Global.generationUnitsNames[	87	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	135.00	,	12.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	,	743936	}	,	2088	);
		unitTASAJER1	=	new	GenerationUnit(	88	,	Global.generationUnitsNames[	88	]	,	genTRMG	,	"	T	"	,	nodeNORDESTE	,	155.00	,	0.00	,	76784777.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	,	145290.726	}	,	2112	);
		unitTCANDEL1	=	new	GenerationUnit(	89	,	Global.generationUnitsNames[	89	]	,	genTCDG	,	"	T	"	,	nodeBOLIVAR	,	157.00	,	0.00	,	44997246.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	,	853043.7562	}	,	2136	);
		unitTCANDEL2	=	new	GenerationUnit(	90	,	Global.generationUnitsNames[	90	]	,	genTCDG	,	"	T	"	,	nodeBOLIVAR	,	157.00	,	0.00	,	45613487.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	,	874925.3616	}	,	2160	);
		unitTCENTRO1	=	new	GenerationUnit(	91	,	Global.generationUnitsNames[	91	]	,	genISGG	,	"	T	"	,	nodeMAGDAMED	,	276.00	,	0.00	,	54291847.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	,	194513.1932	}	,	2184	);
		unitTEBSA	=	new	GenerationUnit(	92	,	Global.generationUnitsNames[	92	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	791.00	,	0.00	,	48573839.59	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	,	156383.5397	}	,	2208	);
		unitTEMCALI	=	new	GenerationUnit(	93	,	Global.generationUnitsNames[	93	]	,	genTEMG	,	"	T	"	,	nodeVALLECAU	,	229.00	,	0.00	,	116027209.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	,	541487.5438	}	,	2232	);
		unitTPIEDRAS	=	new	GenerationUnit(	94	,	Global.generationUnitsNames[	94	]	,	genTRPG	,	"	T	"	,	nodeTOLIMA	,	3.75	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	,	856984	}	,	2256	);
		unitTSIERRA	=	new	GenerationUnit(	95	,	Global.generationUnitsNames[	95	]	,	genEPMG	,	"	T	"	,	nodeMAGDAMED	,	460.00	,	0.00	,	119947583.72	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	,	201046.7438	}	,	2280	);
		unitTVALLE	=	new	GenerationUnit(	96	,	Global.generationUnitsNames[	96	]	,	genEPSG	,	"	T	"	,	nodeVALLECAU	,	205.00	,	0.00	,	125507262.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	,	164463.1082	}	,	2304	);
		unitTYOPAL2	=	new	GenerationUnit(	97	,	Global.generationUnitsNames[	97	]	,	genTYPG	,	"	T	"	,	nodeNORDESTE	,	30.00	,	0.00	,	4480695.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	,	56210.27397	}	,	2328	);
		unitURRA	=	new	GenerationUnit(	98	,	Global.generationUnitsNames[	98	]	,	genEMUG	,	"	H	"	,	nodeCERROMAT	,	338.00	,	63.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	,	94389.7726	}	,	2352	);
		unitVENEZUE1	=	new	GenerationUnit(	99	,	Global.generationUnitsNames[	99	]	,	genISGG	,	"	E	"	,	nodeCUATRICENTENARIO	,	150.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	,	241810.8671	}	,	2376	);
		unitZIPAEMG2	=	new	GenerationUnit(	100	,	Global.generationUnitsNames[	100	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	34.00	,	0.00	,	49430948.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	,	143747.9452	}	,	2400	);
		unitZIPAEMG3	=	new	GenerationUnit(	101	,	Global.generationUnitsNames[	101	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	63.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	,	127930.4548	}	,	2424	);
		unitZIPAEMG4	=	new	GenerationUnit(	102	,	Global.generationUnitsNames[	102	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	64.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	,	126526.4849	}	,	2448	);
		unitZIPAEMG5	=	new	GenerationUnit(	103	,	Global.generationUnitsNames[	103	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	64.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	,	129027.4808	}	,	2472	);
		*/
		
		// 2020
		unitALBAN	=	new	GenerationUnit(	0	,	Global.generationUnitsNames[	0	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	429.00	,	30.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	}	,	0	);
		unitBARRANQ3	=	new	GenerationUnit(	1	,	Global.generationUnitsNames[	1	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	60.00	,	0.00	,	37069992.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	,	407249.89	}	,	24	);
		unitBARRANQ4	=	new	GenerationUnit(	2	,	Global.generationUnitsNames[	2	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	60.00	,	0.00	,	37069992.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	,	408126.48	}	,	48	);
		unitBETANIA	=	new	GenerationUnit(	3	,	Global.generationUnitsNames[	3	]	,	genENDG	,	"	H	"	,	nodeHUILACAQ	,	540.00	,	60.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	,	138232.66	}	,	72	);
		unitCALIMA1	=	new	GenerationUnit(	4	,	Global.generationUnitsNames[	4	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	132.00	,	16.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	,	315035.37	}	,	96	);
		unitCHIVOR	=	new	GenerationUnit(	5	,	Global.generationUnitsNames[	5	]	,	genCHVG	,	"	H	"	,	nodeCHIVOR	,	1000.00	,	10.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	,	170110.14	}	,	120	);
		unitCOINCAUCA	=	new	GenerationUnit(	6	,	Global.generationUnitsNames[	6	]	,	genDLRG	,	"	C	"	,	nodeCAUCANAR	,	3.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	,	714410.00	}	,	144	);
		unitCOROZO1	=	new	GenerationUnit(	7	,	Global.generationUnitsNames[	7	]	,	genESSG	,	"	E	"	,	nodeCOROZO	,	55.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	,	420000.00	}	,	168	);
		unitCSANCARLOS	=	new	GenerationUnit(	8	,	Global.generationUnitsNames[	8	]	,	genEPSG	,	"	C	"	,	nodeVALLECAU	,	2.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	,	533307.00	}	,	192	);
		unitCTGEMG1	=	new	GenerationUnit(	9	,	Global.generationUnitsNames[	9	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	61.00	,	0.00	,	30035123.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	,	472659.90	}	,	216	);
		unitCTGEMG2	=	new	GenerationUnit(	10	,	Global.generationUnitsNames[	10	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	60.00	,	0.00	,	30035123.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	,	467387.17	}	,	240	);
		unitCTGEMG3	=	new	GenerationUnit(	11	,	Global.generationUnitsNames[	11	]	,	genENDG	,	"	T	"	,	nodeBOLIVAR	,	66.00	,	0.00	,	30388533.67	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	,	443602.15	}	,	264	);
		unitCVALLEC1	=	new	GenerationUnit(	12	,	Global.generationUnitsNames[	12	]	,	genCVAG	,	"	C	"	,	nodeVALLECAU	,	31.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	,	791950.00	}	,	288	);
		unitDORADA1	=	new	GenerationUnit(	13	,	Global.generationUnitsNames[	13	]	,	genCHCG	,	"	T	"	,	nodeCQR	,	51.00	,	0.00	,	14842073.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	,	234533.22	}	,	312	);
		unitECUADOR11	=	new	GenerationUnit(	14	,	Global.generationUnitsNames[	14	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	,	154620.91	}	,	336	);
		unitECUADOR12	=	new	GenerationUnit(	15	,	Global.generationUnitsNames[	15	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	,	786106.00	}	,	360	);
		unitECUADOR13	=	new	GenerationUnit(	16	,	Global.generationUnitsNames[	16	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	,	865694.00	}	,	384	);
		unitECUADOR14	=	new	GenerationUnit(	17	,	Global.generationUnitsNames[	17	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	,	137108.00	}	,	408	);
		unitECUADOR21	=	new	GenerationUnit(	18	,	Global.generationUnitsNames[	18	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	,	154538.80	}	,	432	);
		unitECUADOR22	=	new	GenerationUnit(	19	,	Global.generationUnitsNames[	19	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	,	707397.00	}	,	456	);
		unitECUADOR23	=	new	GenerationUnit(	20	,	Global.generationUnitsNames[	20	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	,	97127.00	}	,	480	);
		unitECUADOR24	=	new	GenerationUnit(	21	,	Global.generationUnitsNames[	21	]	,	genECUG	,	"	E	"	,	nodeECUADOR220	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	,	92665.00	}	,	504	);
		unitESMERALDA	=	new	GenerationUnit(	22	,	Global.generationUnitsNames[	22	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	30.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	,	398374.00	}	,	528	);
		unitFLORESIV	=	new	GenerationUnit(	23	,	Global.generationUnitsNames[	23	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	450.00	,	100.00	,	58145114.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	,	332500.00	}	,	552	);
		unitFLORES1	=	new	GenerationUnit(	24	,	Global.generationUnitsNames[	24	]	,	genFRSG	,	"	T	"	,	nodeATLANTIC	,	160.00	,	0.00	,	30181065.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	,	158476.61	}	,	576	);
		unitFLORIDA2	=	new	GenerationUnit(	25	,	Global.generationUnitsNames[	25	]	,	genGNCG	,	"	H	"	,	nodeCAUCANAR	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	,	781785.00	}	,	600	);
		unitGUAJIR11	=	new	GenerationUnit(	26	,	Global.generationUnitsNames[	26	]	,	genGECG	,	"	T	"	,	nodeGCM	,	145.00	,	0.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	,	188499.05	}	,	624	);
		unitGUAJIR21	=	new	GenerationUnit(	27	,	Global.generationUnitsNames[	27	]	,	genGECG	,	"	T	"	,	nodeGCM	,	151.00	,	0.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	,	181160.67	}	,	648	);
		unitGUATAPE	=	new	GenerationUnit(	28	,	Global.generationUnitsNames[	28	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	560.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	,	70438.51	}	,	672	);
		unitGUATRON	=	new	GenerationUnit(	29	,	Global.generationUnitsNames[	29	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	512.00	,	35.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	,	83775.32	}	,	696	);
		unitGUAVIO	=	new	GenerationUnit(	30	,	Global.generationUnitsNames[	30	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	1200.00	,	60.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	,	103928.09	}	,	720	);
		unitINSULA	=	new	GenerationUnit(	31	,	Global.generationUnitsNames[	31	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	,	647284.00	}	,	744	);
		unitJAGUAS	=	new	GenerationUnit(	32	,	Global.generationUnitsNames[	32	]	,	genISGG	,	"	H	"	,	nodeANTIOQUI	,	170.00	,	15.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	,	79668.98	}	,	768	);
		unitLATASAJERA	=	new	GenerationUnit(	33	,	Global.generationUnitsNames[	33	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	306.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	,	63663.95	}	,	792	);
		unitM_AMAIME	=	new	GenerationUnit(	34	,	Global.generationUnitsNames[	34	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	,	228909.00	}	,	816	);
		unitM_PROVIDEN	=	new	GenerationUnit(	35	,	Global.generationUnitsNames[	35	]	,	genDLRG	,	"	C	"	,	nodeVALLECAU	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	,	216506.00	}	,	840	);
		unitMAGUAFRE	=	new	GenerationUnit(	36	,	Global.generationUnitsNames[	36	]	,	genADCG	,	"	H	"	,	nodeANTIOQUI	,	7.05	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	}	,	864	);
		unitMALTOTULUA1	=	new	GenerationUnit(	37	,	Global.generationUnitsNames[	37	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	,	461362.00	}	,	888	);
		unitMANTIOQ1	=	new	GenerationUnit(	38	,	Global.generationUnitsNames[	38	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	134.27	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	,	296625.00	}	,	912	);
		unitMBARROSO1	=	new	GenerationUnit(	39	,	Global.generationUnitsNames[	39	]	,	genHMVG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	1.00	,	1.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	181160.67	,	181161.67	,	181162.67	,	181163.67	,	181164.67	,	181165.67	,	181166.67	,	181167.67	,	181168.67	,	181169.67	,	181170.67	,	181171.67	,	181172.67	,	181173.67	,	181174.67	,	181175.67	,	181176.67	,	181177.67	,	181178.67	,	181179.67	,	181180.67	,	181181.67	,	181182.67	,	181183.67	}	,	936	);
		unitMBELMONTE	=	new	GenerationUnit(	40	,	Global.generationUnitsNames[	40	]	,	genEEPG	,	"	H	"	,	nodeCQR	,	3.40	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	,	515567.00	}	,	960	);
		unitMBOGOTA1	=	new	GenerationUnit(	41	,	Global.generationUnitsNames[	41	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	115.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	,	250857.00	}	,	984	);
		unitMCALDERAS	=	new	GenerationUnit(	42	,	Global.generationUnitsNames[	42	]	,	genISGG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	,	218742.00	}	,	1008	);
		unitMCARUQUIA	=	new	GenerationUnit(	43	,	Global.generationUnitsNames[	43	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	9.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	,	455506.00	}	,	1032	);
		unitMCASCADA1	=	new	GenerationUnit(	44	,	Global.generationUnitsNames[	44	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	2.30	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	,	539591.00	}	,	1056	);
		unitMCAUCAN1	=	new	GenerationUnit(	45	,	Global.generationUnitsNames[	45	]	,	genCDNG	,	"	H	"	,	nodeCAUCANAR	,	7.33	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	,	255202.00	}	,	1080	);
		unitMCAUCAN2	=	new	GenerationUnit(	46	,	Global.generationUnitsNames[	46	]	,	genGNCG	,	"	H	"	,	nodeCAUCANAR	,	13.77	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	,	464322.00	}	,	1104	);
		unitMCIMARR1	=	new	GenerationUnit(	47	,	Global.generationUnitsNames[	47	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	,	151447.00	}	,	1128	);
		unitMCQR1	=	new	GenerationUnit(	48	,	Global.generationUnitsNames[	48	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	7.60	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	,	227895.00	}	,	1152	);
		unitMCUNDINAMARCA	=	new	GenerationUnit(	49	,	Global.generationUnitsNames[	49	]	,	genEECG	,	"	H	"	,	nodeBOGOTA	,	15.60	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	,	282000.00	}	,	1176	);
		unitMCURRUCU	=	new	GenerationUnit(	50	,	Global.generationUnitsNames[	50	]	,	genFACG	,	"	H	"	,	nodeTOLIMA	,	1.25	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	,	127417.00	}	,	1200	);
		unitMELBOSQUE	=	new	GenerationUnit(	51	,	Global.generationUnitsNames[	51	]	,	genERCG	,	"	H	"	,	nodeCQR	,	2.28	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	,	810225.00	}	,	1224	);
		unitMEMCALI	=	new	GenerationUnit(	52	,	Global.generationUnitsNames[	52	]	,	genEGCG	,	"	H	"	,	nodeVALLECAU	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	,	374250.00	}	,	1248	);
		unitMERILEC1	=	new	GenerationUnit(	53	,	Global.generationUnitsNames[	53	]	,	genCIVG	,	"	T	"	,	nodeNORDESTE	,	167.00	,	0.00	,	52487028.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	,	282924.53	}	,	1272	);
		unitMGUANAQUITA	=	new	GenerationUnit(	54	,	Global.generationUnitsNames[	54	]	,	genLCSG	,	"	H	"	,	nodeANTIOQUI	,	9.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	,	61030.00	}	,	1296	);
		unitMHUILAQ1	=	new	GenerationUnit(	55	,	Global.generationUnitsNames[	55	]	,	genHLAG	,	"	H	"	,	nodeHUILACAQ	,	2.69	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	,	372766.00	}	,	1320	);
		unitMIEL1	=	new	GenerationUnit(	56	,	Global.generationUnitsNames[	56	]	,	genISGG	,	"	H	"	,	nodeLAMIEL	,	396.00	,	20.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	,	117349.27	}	,	1344	);
		unitMJEPIRAC	=	new	GenerationUnit(	57	,	Global.generationUnitsNames[	57	]	,	genEPMG	,	"	E	"	,	nodeGCM	,	18.42	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	,	623758.00	}	,	1368	);
		unitMMONTAÑITAS	=	new	GenerationUnit(	58	,	Global.generationUnitsNames[	58	]	,	genCIVG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	181160.67	,	181161.67	,	181162.67	,	181163.67	,	181164.67	,	181165.67	,	181166.67	,	181167.67	,	181168.67	,	181169.67	,	181170.67	,	181171.67	,	181172.67	,	181173.67	,	181174.67	,	181175.67	,	181176.67	,	181177.67	,	181178.67	,	181179.67	,	181180.67	,	181181.67	,	181182.67	,	181183.67	}	,	1392	);
		unitMMORRO1	=	new	GenerationUnit(	59	,	Global.generationUnitsNames[	59	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	,	495314.00	}	,	1416	);
		unitMMORRO2	=	new	GenerationUnit(	60	,	Global.generationUnitsNames[	60	]	,	genCTMG	,	"	T	"	,	nodeNORDESTE	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	,	628819.00	}	,	1440	);
		unitMNLIBARE	=	new	GenerationUnit(	61	,	Global.generationUnitsNames[	61	]	,	genEEPG	,	"	H	"	,	nodeCQR	,	5.10	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	,	555638.00	}	,	1464	);
		unitMNORDE1	=	new	GenerationUnit(	62	,	Global.generationUnitsNames[	62	]	,	genESSG	,	"	H	"	,	nodeNORDESTE	,	20.95	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	,	412658.00	}	,	1488	);
		unitMPRADO4	=	new	GenerationUnit(	63	,	Global.generationUnitsNames[	63	]	,	genEPSG	,	"	H	"	,	nodeTOLIMA	,	5.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	,	681119.00	}	,	1512	);
		unitMRIOMAYO	=	new	GenerationUnit(	64	,	Global.generationUnitsNames[	64	]	,	genCDNG	,	"	H	"	,	nodeCAUCANAR	,	19.80	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	,	392559.00	}	,	1536	);
		unitMSANFRANCISC	=	new	GenerationUnit(	65	,	Global.generationUnitsNames[	65	]	,	genPUTG	,	"	H	"	,	nodeCAUCANAR	,	0.47	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	181160.67	,	181161.67	,	181162.67	,	181163.67	,	181164.67	,	181165.67	,	181166.67	,	181167.67	,	181168.67	,	181169.67	,	181170.67	,	181171.67	,	181172.67	,	181173.67	,	181174.67	,	181175.67	,	181176.67	,	181177.67	,	181178.67	,	181179.67	,	181180.67	,	181181.67	,	181182.67	,	181183.67	}	,	1560	);
		unitMSANTANA	=	new	GenerationUnit(	66	,	Global.generationUnitsNames[	66	]	,	genENDG	,	"	H	"	,	nodeBOGOTA	,	8.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	,	542553.00	}	,	1584	);
		unitMSANTARITA	=	new	GenerationUnit(	67	,	Global.generationUnitsNames[	67	]	,	genFACG	,	"	H	"	,	nodeANTIOQUI	,	1.30	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	,	667177.00	}	,	1608	);
		unitMSANTIAGO	=	new	GenerationUnit(	68	,	Global.generationUnitsNames[	68	]	,	genGEEG	,	"	H	"	,	nodeANTIOQUI	,	2.80	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	,	56969.00	}	,	1632	);
		unitMTOLIMA1	=	new	GenerationUnit(	69	,	Global.generationUnitsNames[	69	]	,	genGNCG	,	"	H	"	,	nodeTOLIMA	,	8.40	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	,	431333.00	}	,	1656	);
		unitMTULUA	=	new	GenerationUnit(	70	,	Global.generationUnitsNames[	70	]	,	genCETG	,	"	H	"	,	nodeVALLECAU	,	14.19	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	,	445221.00	}	,	1680	);
		unitMVALLEC1	=	new	GenerationUnit(	71	,	Global.generationUnitsNames[	71	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	8.50	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	,	348859.00	}	,	1704	);
		unitMYOPAL1	=	new	GenerationUnit(	72	,	Global.generationUnitsNames[	72	]	,	genTYPG	,	"	H	"	,	nodeNORDESTE	,	0.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	,	559690.00	}	,	1728	);
		unitPAGUA	=	new	GenerationUnit(	73	,	Global.generationUnitsNames[	73	]	,	genENDG	,	"	H	"	,	nodePAGUA	,	600.00	,	74.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	,	72156.92	}	,	1752	);
		unitPAIPA1	=	new	GenerationUnit(	74	,	Global.generationUnitsNames[	74	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	31.00	,	0.00	,	58068650.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	,	112998.96	}	,	1776	);
		unitPAIPA2	=	new	GenerationUnit(	75	,	Global.generationUnitsNames[	75	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	70.00	,	0.00	,	73094794.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	,	138317.48	}	,	1800	);
		unitPAIPA3	=	new	GenerationUnit(	76	,	Global.generationUnitsNames[	76	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	70.00	,	0.00	,	73094794.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	,	127661.31	}	,	1824	);
		unitPAIPA4	=	new	GenerationUnit(	77	,	Global.generationUnitsNames[	77	]	,	genHIMG	,	"	T	"	,	nodeNORDESTE	,	150.00	,	0.00	,	54914904.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	,	105072.87	}	,	1848	);
		unitPLAYAS	=	new	GenerationUnit(	78	,	Global.generationUnitsNames[	78	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	201.00	,	55.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	,	58479.37	}	,	1872	);
		unitPORCE2	=	new	GenerationUnit(	79	,	Global.generationUnitsNames[	79	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	405.00	,	75.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	,	82651.57	}	,	1896	);
		unitPORCE3	=	new	GenerationUnit(	80	,	Global.generationUnitsNames[	80	]	,	genEPMG	,	"	H	"	,	nodeANTIOQUI	,	660.00	,	125.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	,	17576.62	}	,	1920	);
		unitPRADO	=	new	GenerationUnit(	81	,	Global.generationUnitsNames[	81	]	,	genEPSG	,	"	H	"	,	nodeTOLIMA	,	46.00	,	8.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	,	212067.58	}	,	1944	);
		unitPROELEC1	=	new	GenerationUnit(	82	,	Global.generationUnitsNames[	82	]	,	genPRLG	,	"	T	"	,	nodeBOLIVAR	,	45.00	,	0.00	,	19451005.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	}	,	1968	);
		unitPROELEC2	=	new	GenerationUnit(	83	,	Global.generationUnitsNames[	83	]	,	genPRLG	,	"	T	"	,	nodeBOLIVAR	,	45.00	,	0.00	,	19451005.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	,	212480.66	}	,	1992	);
		unitRPIEDRAS	=	new	GenerationUnit(	84	,	Global.generationUnitsNames[	84	]	,	genCIVG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	,	157128.00	}	,	2016	);
		unitSALVAJINA	=	new	GenerationUnit(	85	,	Global.generationUnitsNames[	85	]	,	genEPSG	,	"	H	"	,	nodeVALLECAU	,	285.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	,	421421.71	}	,	2040	);
		unitSANCARLOS	=	new	GenerationUnit(	86	,	Global.generationUnitsNames[	86	]	,	genISGG	,	"	H	"	,	nodeSANCARLO	,	1240.00	,	10.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	,	72520.27	}	,	2064	);
		unitSANFRANCISCO	=	new	GenerationUnit(	87	,	Global.generationUnitsNames[	87	]	,	genCHCG	,	"	H	"	,	nodeCQR	,	135.00	,	12.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	,	743936.00	}	,	2088	);
		unitTASAJER1	=	new	GenerationUnit(	88	,	Global.generationUnitsNames[	88	]	,	genTRMG	,	"	T	"	,	nodeNORDESTE	,	155.00	,	0.00	,	76784777.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	,	145290.73	}	,	2112	);
		unitTCANDEL1	=	new	GenerationUnit(	89	,	Global.generationUnitsNames[	89	]	,	genTCDG	,	"	T	"	,	nodeBOLIVAR	,	157.00	,	0.00	,	44997246.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	,	853043.76	}	,	2136	);
		unitTCANDEL2	=	new	GenerationUnit(	90	,	Global.generationUnitsNames[	90	]	,	genTCDG	,	"	T	"	,	nodeBOLIVAR	,	157.00	,	0.00	,	45613487.75	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	,	874925.36	}	,	2160	);
		unitTCENTRO1	=	new	GenerationUnit(	91	,	Global.generationUnitsNames[	91	]	,	genISGG	,	"	T	"	,	nodeMAGDAMED	,	276.00	,	0.00	,	54291847.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	,	194513.19	}	,	2184	);
		unitTEBSA	=	new	GenerationUnit(	92	,	Global.generationUnitsNames[	92	]	,	genGECG	,	"	T	"	,	nodeATLANTIC	,	791.00	,	0.00	,	48573839.59	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	,	156383.54	}	,	2208	);
		unitTEMCALI	=	new	GenerationUnit(	93	,	Global.generationUnitsNames[	93	]	,	genTEMG	,	"	T	"	,	nodeVALLECAU	,	229.00	,	0.00	,	116027209.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	,	541487.54	}	,	2232	);
		unitTPIEDRAS	=	new	GenerationUnit(	94	,	Global.generationUnitsNames[	94	]	,	genTRPG	,	"	T	"	,	nodeTOLIMA	,	3.75	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	,	856984.00	}	,	2256	);
		unitTSIERRA	=	new	GenerationUnit(	95	,	Global.generationUnitsNames[	95	]	,	genEPMG	,	"	T	"	,	nodeMAGDAMED	,	460.00	,	0.00	,	119947583.72	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	,	201046.74	}	,	2280	);
		unitTVALLE	=	new	GenerationUnit(	96	,	Global.generationUnitsNames[	96	]	,	genEPSG	,	"	T	"	,	nodeVALLECAU	,	205.00	,	0.00	,	125507262.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	,	164463.11	}	,	2304	);
		unitTYOPAL2	=	new	GenerationUnit(	97	,	Global.generationUnitsNames[	97	]	,	genTYPG	,	"	T	"	,	nodeNORDESTE	,	30.00	,	0.00	,	4480695.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	,	56210.27	}	,	2328	);
		unitURRA	=	new	GenerationUnit(	98	,	Global.generationUnitsNames[	98	]	,	genEMUG	,	"	H	"	,	nodeCERROMAT	,	338.00	,	63.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	,	94389.77	}	,	2352	);
		unitVENEZUE1	=	new	GenerationUnit(	99	,	Global.generationUnitsNames[	99	]	,	genISGG	,	"	E	"	,	nodeCUATRICENTENARIO	,	150.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	,	241810.87	}	,	2376	);
		unitZIPAEMG2	=	new	GenerationUnit(	100	,	Global.generationUnitsNames[	100	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	34.00	,	0.00	,	49430948.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	,	143747.95	}	,	2400	);
		unitZIPAEMG3	=	new	GenerationUnit(	101	,	Global.generationUnitsNames[	101	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	63.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	,	127930.45	}	,	2424	);
		unitZIPAEMG4	=	new	GenerationUnit(	102	,	Global.generationUnitsNames[	102	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	64.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	,	126526.48	}	,	2448	);
		unitZIPAEMG5	=	new	GenerationUnit(	103	,	Global.generationUnitsNames[	103	]	,	genENDG	,	"	T	"	,	nodeBOGOTA	,	64.00	,	0.00	,	54848325.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	,	129027.48	}	,	2472	);
		unitAMOYA	=	new	GenerationUnit(	104	,	Global.generationUnitsNames[	104	]	,	genISGG	,	"	H	"	,	nodeTOLIMA	,	78.00	,	1.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	}	,	2496	);
		unitTERMOCOL	=	new	GenerationUnit(	105	,	Global.generationUnitsNames[	105	]	,	genPOLG	,	"	T	"	,	nodeGCM	,	202.00	,	2.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	,	221412.00	}	,	2520	);
		unitCUCUANA	=	new	GenerationUnit(	106	,	Global.generationUnitsNames[	106	]	,	genEPSG	,	"	H	"	,	nodeTOLIMA	,	60.00	,	3.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	,	134329.00	}	,	2544	);
		unitELQUIMBO	=	new	GenerationUnit(	107	,	Global.generationUnitsNames[	107	]	,	genENDG	,	"	H	"	,	nodeHUILACAQ	,	420.00	,	4.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	}	,	2568	);
		unitSOGAMOSO	=	new	GenerationUnit(	108	,	Global.generationUnitsNames[	108	]	,	genISGG	,	"	H	"	,	nodeNORDESTE	,	800.00	,	5.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	}	,	2592	);
		unitCARLOSLLERAS	=	new	GenerationUnit(	109	,	Global.generationUnitsNames[	109	]	,	genHIDG	,	"	H	"	,	nodeANTIOQUI	,	78.00	,	6.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	}	,	2616	);
		unitPOPAL	=	new	GenerationUnit(	110	,	Global.generationUnitsNames[	110	]	,	genHMVG	,	"	H	"	,	nodeANTIOQUI	,	19.90	,	7.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	,	166568.00	}	,	2640	);
		unitRIOAMBEIMA	=	new	GenerationUnit(	111	,	Global.generationUnitsNames[	111	]	,	genENAG	,	"	H	"	,	nodeTOLIMA	,	45.00	,	8.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	,	184329.00	}	,	2664	);
		unitGECELCA32	=	new	GenerationUnit(	112	,	Global.generationUnitsNames[	112	]	,	genGECG	,	"	T	"	,	nodeCERROMAT	,	250.00	,	9.00	,	19451005.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	,	212906.00	}	,	2688	);
		unitITUANGO	=	new	GenerationUnit(	113	,	Global.generationUnitsNames[	113	]	,	genITUG	,	"	H	"	,	nodeANTIOQUI	,	2400.00	,	10.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	,	98953.62	}	,	2712	);
		unitPORVENIRII	=	new	GenerationUnit(	114	,	Global.generationUnitsNames[	114	]	,	genCIVG	,	"	H	"	,	nodeSANCARLO	,	352.00	,	11.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	,	143074.00	}	,	2736	);
		unitTERMONORTE	=	new	GenerationUnit(	115	,	Global.generationUnitsNames[	115	]	,	genTENG	,	"	T	"	,	nodeGCM	,	88.00	,	12.00	,	40160853.50	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	}	,	2760	);
		unitPANAMA	=	new	GenerationUnit(	116	,	Global.generationUnitsNames[	116	]	,	genPANG	,	"	E	"	,	nodePANAMA	,	17.00	,	0.00	,	0.00	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	,	222906.00	}	,	2784	);
	}
	
	public void initializeRetailers()
	{
		//
		// comercializadores
		//
		/*
		retANTIOQUI	=	new	Retailer(	0	,	Global.retailersNames[	0	]	,	nodeANTIOQUI	,	0	);
		retATLANTIC	=	new	Retailer(	1	,	Global.retailersNames[	1	]	,	nodeATLANTIC	,	24	);
		retBOGOTA	=	new	Retailer(	2	,	Global.retailersNames[	2	]	,	nodeBOGOTA	,	48	);
		retBOLIVAR	=	new	Retailer(	3	,	Global.retailersNames[	3	]	,	nodeBOLIVAR	,	72	);
		retCAUCANAR	=	new	Retailer(	4	,	Global.retailersNames[	4	]	,	nodeCAUCANAR	,	96	);
		retCERROMAT	=	new	Retailer(	5	,	Global.retailersNames[	5	]	,	nodeCERROMAT	,	120	);
		retCHIVOR	=	new	Retailer(	6	,	Global.retailersNames[	6	]	,	nodeCHIVOR	,	144	);
		retCORDOSUC	=	new	Retailer(	7	,	Global.retailersNames[	7	]	,	nodeCORDOSUC	,	168	);
		retCQR	=	new	Retailer(	8	,	Global.retailersNames[	8	]	,	nodeCQR	,	192	);
		retGCM	=	new	Retailer(	9	,	Global.retailersNames[	9	]	,	nodeGCM	,	216	);
		retHUILACAQ	=	new	Retailer(	10	,	Global.retailersNames[	10	]	,	nodeHUILACAQ	,	240	);
		retLAMIEL	=	new	Retailer(	11	,	Global.retailersNames[	11	]	,	nodeLAMIEL	,	264	);
		retMAGDAMED	=	new	Retailer(	12	,	Global.retailersNames[	12	]	,	nodeMAGDAMED	,	288	);
		retMETA	=	new	Retailer(	13	,	Global.retailersNames[	13	]	,	nodeMETA	,	312	);
		retNORDESTE	=	new	Retailer(	14	,	Global.retailersNames[	14	]	,	nodeNORDESTE	,	336	);
		retPAGUA	=	new	Retailer(	15	,	Global.retailersNames[	15	]	,	nodePAGUA	,	360	);
		retSANCARLO	=	new	Retailer(	16	,	Global.retailersNames[	16	]	,	nodeSANCARLO	,	384	);
		retTOLIMA	=	new	Retailer(	17	,	Global.retailersNames[	17	]	,	nodeTOLIMA	,	408	);
		retVALLECAU	=	new	Retailer(	18	,	Global.retailersNames[	18	]	,	nodeVALLECAU	,	432	);
		retCOROZO	=	new	Retailer(	19	,	Global.retailersNames[	19	]	,	nodeCOROZO	,	456	);
		retCUATRICENTENARIO	=	new	Retailer(	20	,	Global.retailersNames[	20	]	,	nodeCUATRICENTENARIO	,	480	);
		retECUADOR220	=	new	Retailer(	21	,	Global.retailersNames[	21	]	,	nodeECUADOR220	,	504	);
		*/
		
		/*
		// 2012
		retANTIOQUI	=	new	Retailer(	0	,	Global.retailersNames[	0	]	,	Global.retailersCods[	0	]	,	nodeANTIOQUI	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	589.7534	,	558.9274	,	545.7370	,	553.0863	,	608.1356	,	711.1411	,	781.3233	,	841.4055	,	902.5110	,	947.9767	,	990.5589	,	1017.3822	,	999.5712	,	962.5959	,	940.2438	,	926.6740	,	925.5534	,	952.9288	,	1155.0301	,	1153.3575	,	1090.4753	,	896.9685	,	767.8384	,	655.7123	}	,	853.12	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	0	);
		retATLANTIC	=	new	Retailer(	1	,	Global.retailersNames[	1	]	,	Global.retailersCods[	1	]	,	nodeATLANTIC	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	441.7562	,	426.5507	,	415.0658	,	407.5753	,	406.7192	,	409.9288	,	399.7425	,	419.9521	,	458.1945	,	475.9658	,	488.6932	,	496.2370	,	482.3123	,	487.3699	,	499.5301	,	499.7644	,	493.2164	,	489.7247	,	542.8342	,	547.0151	,	544.5014	,	530.7425	,	506.5274	,	470.7014	}	,	472.53	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	24	);
		retBOGOTA	=	new	Retailer(	2	,	Global.retailersNames[	2	]	,	Global.retailersCods[	2	]	,	nodeBOGOTA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	1127.8973	,	1066.9877	,	1038.9986	,	1037.3877	,	1120.1342	,	1323.0425	,	1408.0808	,	1502.1781	,	1630.5014	,	1707.8808	,	1763.3014	,	1800.2452	,	1767.4493	,	1723.9877	,	1702.8342	,	1687.7027	,	1670.1849	,	1685.8548	,	2010.7918	,	2026.8425	,	1991.8753	,	1737.1260	,	1479.8466	,	1255.5904	}	,	1552.78	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	48	);
		retBOLIVAR	=	new	Retailer(	3	,	Global.retailersNames[	3	]	,	Global.retailersCods[	3	]	,	nodeBOLIVAR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	310.3767	,	301.0219	,	294.3534	,	288.9082	,	286.3740	,	286.0767	,	274.5151	,	279.5658	,	293.9959	,	303.2740	,	311.0836	,	316.6973	,	314.1123	,	316.9014	,	321.9589	,	321.4603	,	318.3616	,	319.8863	,	363.0890	,	373.2973	,	370.5000	,	361.7219	,	347.7507	,	326.8137	}	,	316.75	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	72	);
		retCAUCANAR	=	new	Retailer(	4	,	Global.retailersNames[	4	]	,	Global.retailersCods[	4	]	,	nodeCAUCANAR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	116.4753	,	109.4082	,	107.2425	,	109.6219	,	120.0863	,	153.7288	,	185.0959	,	186.8548	,	191.2918	,	196.0822	,	203.0945	,	206.8699	,	192.4178	,	179.8082	,	180.0205	,	184.4795	,	191.4438	,	207.8301	,	289.2616	,	297.1589	,	269.7411	,	217.0466	,	169.3027	,	132.7712	}	,	183.21	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	96	);
		retCERROMAT	=	new	Retailer(	5	,	Global.retailersNames[	5	]	,	Global.retailersCods[	5	]	,	nodeCERROMAT	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	264.2151	,	259.7384	,	257.2082	,	256.3260	,	257.8767	,	261.4301	,	254.4644	,	256.8077	,	262.0070	,	263.3184	,	265.4903	,	268.6195	,	268.2428	,	269.4785	,	272.4070	,	274.2330	,	273.6139	,	278.5726	,	328.2959	,	336.9014	,	333.0178	,	305.0123	,	289.5740	,	272.4740	}	,	276.22	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	120	);
		retCHIVOR	=	new	Retailer(	6	,	Global.retailersNames[	6	]	,	Global.retailersCods[	6	]	,	nodeCHIVOR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	144	);
		retCORDOSUC	=	new	Retailer(	7	,	Global.retailersNames[	7	]	,	Global.retailersCods[	7	]	,	nodeCORDOSUC	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	185.1986	,	179.0781	,	174.3781	,	171.0822	,	171.8178	,	177.8342	,	165.3986	,	161.2882	,	169.6054	,	172.8528	,	178.9028	,	184.8545	,	179.7079	,	180.0023	,	187.4218	,	189.3725	,	186.6546	,	187.8890	,	239.2699	,	253.6781	,	248.6767	,	237.7096	,	217.7384	,	198.1534	}	,	191.61	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	168	);
		retCQR	=	new	Retailer(	8	,	Global.retailersNames[	8	]	,	Global.retailersCods[	8	]	,	nodeCQR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	239.2795	,	225.8260	,	218.3110	,	217.9192	,	227.9616	,	259.8164	,	280.7055	,	298.0904	,	318.3096	,	334.8712	,	353.0315	,	365.1288	,	351.8178	,	342.6384	,	340.1904	,	337.8315	,	339.0616	,	355.2548	,	451.9548	,	473.1493	,	447.8219	,	384.9699	,	325.2562	,	270.4671	}	,	323.32	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	192	);
		retGCM	=	new	Retailer(	9	,	Global.retailersNames[	9	]	,	Global.retailersCods[	9	]	,	nodeGCM	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	373.7137	,	360.6521	,	351.7219	,	346.6616	,	344.4425	,	339.5589	,	318.9644	,	319.9110	,	332.2205	,	342.6603	,	358.6603	,	374.0370	,	368.7589	,	377.5658	,	390.2781	,	390.7753	,	382.0904	,	387.5301	,	461.6384	,	477.4863	,	481.4548	,	465.0740	,	441.7973	,	397.1685	}	,	382.70	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	216	);
		retHUILACAQ	=	new	Retailer(	10	,	Global.retailersNames[	10	]	,	Global.retailersCods[	10	]	,	nodeHUILACAQ	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	94.0123	,	89.8849	,	87.1603	,	85.6329	,	86.8438	,	94.1945	,	98.8904	,	107.5781	,	117.3000	,	124.4942	,	131.2183	,	136.5141	,	131.0255	,	129.5530	,	134.2957	,	136.0726	,	135.4014	,	136.0548	,	181.1712	,	187.1890	,	178.3356	,	152.2603	,	127.1164	,	105.5932	}	,	124.49	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	240	);
		retLAMIEL	=	new	Retailer(	11	,	Global.retailersNames[	11	]	,	Global.retailersCods[	11	]	,	nodeLAMIEL	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	264	);
		retMAGDAMED	=	new	Retailer(	12	,	Global.retailersNames[	12	]	,	Global.retailersCods[	12	]	,	nodeMAGDAMED	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	288	);
		retMETA	=	new	Retailer(	13	,	Global.retailersNames[	13	]	,	Global.retailersCods[	13	]	,	nodeMETA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	89.0849	,	84.7000	,	82.1740	,	81.5397	,	81.9644	,	87.7068	,	87.1575	,	95.5356	,	108.8630	,	117.2890	,	124.1877	,	128.6301	,	126.6699	,	125.1123	,	126.2384	,	125.6493	,	124.1589	,	125.4959	,	153.0726	,	154.0918	,	150.2589	,	133.9479	,	115.8164	,	98.3562	}	,	113.65	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	312	);
		retNORDESTE	=	new	Retailer(	14	,	Global.retailersNames[	14	]	,	Global.retailersCods[	14	]	,	nodeNORDESTE	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	661.4521	,	645.1890	,	633.5767	,	627.6589	,	639.0575	,	668.2288	,	672.7712	,	710.3438	,	761.0795	,	790.8918	,	818.7822	,	840.5658	,	811.4671	,	817.7808	,	834.2479	,	834.2795	,	830.5534	,	858.4041	,	996.1082	,	999.3425	,	963.6932	,	875.5192	,	796.8205	,	696.9918	}	,	782.70	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	336	);
		retPAGUA	=	new	Retailer(	15	,	Global.retailersNames[	15	]	,	Global.retailersCods[	15	]	,	nodePAGUA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	360	);
		retSANCARLO	=	new	Retailer(	16	,	Global.retailersNames[	16	]	,	Global.retailersCods[	16	]	,	nodeSANCARLO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	60.8712	,	62.4808	,	62.2219	,	63.0288	,	64.3973	,	64.5890	,	63.9808	,	65.3452	,	66.0466	,	65.7671	,	65.2096	,	65.8781	,	66.2425	,	66.7384	,	67.0027	,	67.4740	,	67.6315	,	69.4945	,	78.2918	,	75.8836	,	74.1603	,	67.5548	,	64.4685	,	62.8151	}	,	66.57	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	384	);
		retTOLIMA	=	new	Retailer(	17	,	Global.retailersNames[	17	]	,	Global.retailersCods[	17	]	,	nodeTOLIMA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	131.2164	,	126.4658	,	123.5603	,	122.5014	,	124.9918	,	131.1082	,	132.7082	,	141.8452	,	151.3466	,	160.4866	,	167.0789	,	172.1612	,	168.0444	,	166.9210	,	168.3974	,	168.3274	,	167.9890	,	174.2712	,	210.6260	,	218.3288	,	208.6767	,	185.1055	,	161.0315	,	141.4781	}	,	159.36	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	408	);
		retVALLECAU	=	new	Retailer(	18	,	Global.retailersNames[	18	]	,	Global.retailersCods[	18	]	,	nodeVALLECAU	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	567.4849	,	538.8808	,	524.5260	,	518.8644	,	530.5932	,	569.6000	,	589.4534	,	643.1712	,	713.9562	,	761.8918	,	807.1151	,	838.0589	,	817.8384	,	804.9986	,	806.3616	,	803.5356	,	797.5808	,	791.4808	,	937.4384	,	952.6123	,	918.5973	,	820.2575	,	728.9603	,	630.3767	}	,	725.57	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	432	);
		retCOROZO	=	new	Retailer(	19	,	Global.retailersNames[	19	]	,	Global.retailersCods[	19	]	,	nodeCOROZO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	5.6986	,	7.8014	,	8.6918	,	18.0205	,	18.2603	,	18.1575	,	17.9658	,	17.9658	,	18.0137	,	21.8082	,	21.7534	,	21.6986	,	21.7534	,	22.1096	,	22.0685	,	0.1233	}	,	10.91	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	456	);
		retCUATRICENTENARIO	=	new	Retailer(	20	,	Global.retailersNames[	20	]	,	Global.retailersCods[	20	]	,	nodeCUATRICENTENARIO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.3699	,	3.1096	,	8.8767	,	13.8767	,	14.4110	,	14.3973	,	14.4452	,	14.5137	,	14.1027	,	17.2671	,	17.3767	,	17.3767	,	17.4932	,	17.3425	,	17.5274	,	1.5068	}	,	8.59	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	480	);
		retECUADOR220	=	new	Retailer(	21	,	Global.retailersNames[	21	]	,	Global.retailersCods[	21	]	,	nodeECUADOR220	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	153.8425	,	154.9137	,	149.6288	,	147.2534	,	145.3493	,	131.8123	,	114.6658	,	116.8164	,	120.5370	,	123.0918	,	125.4890	,	128.5534	,	126.3438	,	132.7027	,	136.9589	,	132.5123	,	116.7438	,	83.7329	,	78.2411	,	72.6904	,	69.8795	,	88.9986	,	117.9890	,	153.0589	}	,	121.74	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	504	);
		*/
		
		//2020
		retANTIOQUI	=	new	Retailer(	0	,	Global.retailersNames[	0	]	,	Global.retailersCods[	0	]	,	nodeANTIOQUI	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	589.7534	,	558.9274	,	545.7370	,	553.0863	,	608.1356	,	711.1411	,	781.3233	,	841.4055	,	902.5110	,	947.9767	,	990.5589	,	1017.3822	,	999.5712	,	962.5959	,	940.2438	,	926.6740	,	925.5534	,	952.9288	,	1155.0301	,	1153.3575	,	1090.4753	,	896.9685	,	767.8384	,	655.7123	}	,	853.12	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	0	);
		retATLANTIC	=	new	Retailer(	1	,	Global.retailersNames[	1	]	,	Global.retailersCods[	1	]	,	nodeATLANTIC	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	441.7562	,	426.5507	,	415.0658	,	407.5753	,	406.7192	,	409.9288	,	399.7425	,	419.9521	,	458.1945	,	475.9658	,	488.6932	,	496.2370	,	482.3123	,	487.3699	,	499.5301	,	499.7644	,	493.2164	,	489.7247	,	542.8342	,	547.0151	,	544.5014	,	530.7425	,	506.5274	,	470.7014	}	,	472.53	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	24	);
		retBOGOTA	=	new	Retailer(	2	,	Global.retailersNames[	2	]	,	Global.retailersCods[	2	]	,	nodeBOGOTA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	1127.8973	,	1066.9877	,	1038.9986	,	1037.3877	,	1120.1342	,	1323.0425	,	1408.0808	,	1502.1781	,	1630.5014	,	1707.8808	,	1763.3014	,	1800.2452	,	1767.4493	,	1723.9877	,	1702.8342	,	1687.7027	,	1670.1849	,	1685.8548	,	2010.7918	,	2026.8425	,	1991.8753	,	1737.1260	,	1479.8466	,	1255.5904	}	,	1552.78	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	48	);
		retBOLIVAR	=	new	Retailer(	3	,	Global.retailersNames[	3	]	,	Global.retailersCods[	3	]	,	nodeBOLIVAR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	310.3767	,	301.0219	,	294.3534	,	288.9082	,	286.3740	,	286.0767	,	274.5151	,	279.5658	,	293.9959	,	303.2740	,	311.0836	,	316.6973	,	314.1123	,	316.9014	,	321.9589	,	321.4603	,	318.3616	,	319.8863	,	363.0890	,	373.2973	,	370.5000	,	361.7219	,	347.7507	,	326.8137	}	,	316.75	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	72	);
		retCAUCANAR	=	new	Retailer(	4	,	Global.retailersNames[	4	]	,	Global.retailersCods[	4	]	,	nodeCAUCANAR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	116.4753	,	109.4082	,	107.2425	,	109.6219	,	120.0863	,	153.7288	,	185.0959	,	186.8548	,	191.2918	,	196.0822	,	203.0945	,	206.8699	,	192.4178	,	179.8082	,	180.0205	,	184.4795	,	191.4438	,	207.8301	,	289.2616	,	297.1589	,	269.7411	,	217.0466	,	169.3027	,	132.7712	}	,	183.21	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	96	);
		retCERROMAT	=	new	Retailer(	5	,	Global.retailersNames[	5	]	,	Global.retailersCods[	5	]	,	nodeCERROMAT	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	264.2151	,	259.7384	,	257.2082	,	256.3260	,	257.8767	,	261.4301	,	254.4644	,	256.8077	,	262.0070	,	263.3184	,	265.4903	,	268.6195	,	268.2428	,	269.4785	,	272.4070	,	274.2330	,	273.6139	,	278.5726	,	328.2959	,	336.9014	,	333.0178	,	305.0123	,	289.5740	,	272.4740	}	,	276.22	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	120	);
		retCHIVOR	=	new	Retailer(	6	,	Global.retailersNames[	6	]	,	Global.retailersCods[	6	]	,	nodeCHIVOR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	144	);
		retCORDOSUC	=	new	Retailer(	7	,	Global.retailersNames[	7	]	,	Global.retailersCods[	7	]	,	nodeCORDOSUC	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	185.1986	,	179.0781	,	174.3781	,	171.0822	,	171.8178	,	177.8342	,	165.3986	,	161.2882	,	169.6054	,	172.8528	,	178.9028	,	184.8545	,	179.7079	,	180.0023	,	187.4218	,	189.3725	,	186.6546	,	187.8890	,	239.2699	,	253.6781	,	248.6767	,	237.7096	,	217.7384	,	198.1534	}	,	191.61	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	168	);
		retCQR	=	new	Retailer(	8	,	Global.retailersNames[	8	]	,	Global.retailersCods[	8	]	,	nodeCQR	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	239.2795	,	225.8260	,	218.3110	,	217.9192	,	227.9616	,	259.8164	,	280.7055	,	298.0904	,	318.3096	,	334.8712	,	353.0315	,	365.1288	,	351.8178	,	342.6384	,	340.1904	,	337.8315	,	339.0616	,	355.2548	,	451.9548	,	473.1493	,	447.8219	,	384.9699	,	325.2562	,	270.4671	}	,	323.32	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	192	);
		retGCM	=	new	Retailer(	9	,	Global.retailersNames[	9	]	,	Global.retailersCods[	9	]	,	nodeGCM	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	373.7137	,	360.6521	,	351.7219	,	346.6616	,	344.4425	,	339.5589	,	318.9644	,	319.9110	,	332.2205	,	342.6603	,	358.6603	,	374.0370	,	368.7589	,	377.5658	,	390.2781	,	390.7753	,	382.0904	,	387.5301	,	461.6384	,	477.4863	,	481.4548	,	465.0740	,	441.7973	,	397.1685	}	,	382.70	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	216	);
		retHUILACAQ	=	new	Retailer(	10	,	Global.retailersNames[	10	]	,	Global.retailersCods[	10	]	,	nodeHUILACAQ	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	94.0123	,	89.8849	,	87.1603	,	85.6329	,	86.8438	,	94.1945	,	98.8904	,	107.5781	,	117.3000	,	124.4942	,	131.2183	,	136.5141	,	131.0255	,	129.5530	,	134.2957	,	136.0726	,	135.4014	,	136.0548	,	181.1712	,	187.1890	,	178.3356	,	152.2603	,	127.1164	,	105.5932	}	,	124.49	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	240	);
		retLAMIEL	=	new	Retailer(	11	,	Global.retailersNames[	11	]	,	Global.retailersCods[	11	]	,	nodeLAMIEL	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	264	);
		retMAGDAMED	=	new	Retailer(	12	,	Global.retailersNames[	12	]	,	Global.retailersCods[	12	]	,	nodeMAGDAMED	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	288	);
		retMETA	=	new	Retailer(	13	,	Global.retailersNames[	13	]	,	Global.retailersCods[	13	]	,	nodeMETA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	89.0849	,	84.7000	,	82.1740	,	81.5397	,	81.9644	,	87.7068	,	87.1575	,	95.5356	,	108.8630	,	117.2890	,	124.1877	,	128.6301	,	126.6699	,	125.1123	,	126.2384	,	125.6493	,	124.1589	,	125.4959	,	153.0726	,	154.0918	,	150.2589	,	133.9479	,	115.8164	,	98.3562	}	,	113.65	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	312	);
		retNORDESTE	=	new	Retailer(	14	,	Global.retailersNames[	14	]	,	Global.retailersCods[	14	]	,	nodeNORDESTE	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	661.4521	,	645.1890	,	633.5767	,	627.6589	,	639.0575	,	668.2288	,	672.7712	,	710.3438	,	761.0795	,	790.8918	,	818.7822	,	840.5658	,	811.4671	,	817.7808	,	834.2479	,	834.2795	,	830.5534	,	858.4041	,	996.1082	,	999.3425	,	963.6932	,	875.5192	,	796.8205	,	696.9918	}	,	782.70	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	336	);
		retPAGUA	=	new	Retailer(	15	,	Global.retailersNames[	15	]	,	Global.retailersCods[	15	]	,	nodePAGUA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	}	,	0.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	360	);
		retSANCARLO	=	new	Retailer(	16	,	Global.retailersNames[	16	]	,	Global.retailersCods[	16	]	,	nodeSANCARLO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	60.8712	,	62.4808	,	62.2219	,	63.0288	,	64.3973	,	64.5890	,	63.9808	,	65.3452	,	66.0466	,	65.7671	,	65.2096	,	65.8781	,	66.2425	,	66.7384	,	67.0027	,	67.4740	,	67.6315	,	69.4945	,	78.2918	,	75.8836	,	74.1603	,	67.5548	,	64.4685	,	62.8151	}	,	66.57	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	384	);
		retTOLIMA	=	new	Retailer(	17	,	Global.retailersNames[	17	]	,	Global.retailersCods[	17	]	,	nodeTOLIMA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	131.2164	,	126.4658	,	123.5603	,	122.5014	,	124.9918	,	131.1082	,	132.7082	,	141.8452	,	151.3466	,	160.4866	,	167.0789	,	172.1612	,	168.0444	,	166.9210	,	168.3974	,	168.3274	,	167.9890	,	174.2712	,	210.6260	,	218.3288	,	208.6767	,	185.1055	,	161.0315	,	141.4781	}	,	159.36	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	408	);
		retVALLECAU	=	new	Retailer(	18	,	Global.retailersNames[	18	]	,	Global.retailersCods[	18	]	,	nodeVALLECAU	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	567.4849	,	538.8808	,	524.5260	,	518.8644	,	530.5932	,	569.6000	,	589.4534	,	643.1712	,	713.9562	,	761.8918	,	807.1151	,	838.0589	,	817.8384	,	804.9986	,	806.3616	,	803.5356	,	797.5808	,	791.4808	,	937.4384	,	952.6123	,	918.5973	,	820.2575	,	728.9603	,	630.3767	}	,	725.57	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	432	);
		retCOROZO	=	new	Retailer(	19	,	Global.retailersNames[	19	]	,	Global.retailersCods[	19	]	,	nodeCOROZO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	0.0000	,	5.6986	,	7.8014	,	8.6918	,	18.0205	,	18.2603	,	18.1575	,	17.9658	,	17.9658	,	18.0137	,	21.8082	,	21.7534	,	21.6986	,	21.7534	,	22.1096	,	22.0685	,	0.1233	}	,	10.91	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	456	);
		retCUATRICENTENARIO	=	new	Retailer(	20	,	Global.retailersNames[	20	]	,	Global.retailersCods[	20	]	,	nodeCUATRICENTENARIO	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.2740	,	0.3699	,	3.1096	,	8.8767	,	13.8767	,	14.4110	,	14.3973	,	14.4452	,	14.5137	,	14.1027	,	17.2671	,	17.3767	,	17.3767	,	17.4932	,	17.3425	,	17.5274	,	1.5068	}	,	8.59	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	480	);
		retECUADOR220	=	new	Retailer(	21	,	Global.retailersNames[	21	]	,	Global.retailersCods[	21	]	,	nodeECUADOR220	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	153.8425	,	154.9137	,	149.6288	,	147.2534	,	145.3493	,	131.8123	,	114.6658	,	116.8164	,	120.5370	,	123.0918	,	125.4890	,	128.5534	,	126.3438	,	132.7027	,	136.9589	,	132.5123	,	116.7438	,	83.7329	,	78.2411	,	72.6904	,	69.8795	,	88.9986	,	117.9890	,	153.0589	}	,	121.74	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	504	);
		retPANAMA	=	new	Retailer(	22	,	Global.retailersNames[	22	]	,	Global.retailersCods[	22	]	,	nodePANAMA	,	new double[]	{	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	,	0.80	}	,	new double[]	{	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	}	,	new double[]	{	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	,	600.00	}	,	600.00	,	new double[]	{	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	,	120000.00	}	,	0.80	,	528	);	
	}
	
	public void initializeTransmitters()
	{

		//
		// transmisores
		//
		
		/*
		// 2012
		transCENS	=	new	Transmitter(	0	,	Global.gridcosNames[	0	]	,	new ArrayList<TransmissionLine>()	,	0.0167	,	0	);
		transEEB	=	new	Transmitter(	1	,	Global.gridcosNames[	1	]	,	new ArrayList<TransmissionLine>()	,	0.1193	,	24	);
		transISA	=	new	Transmitter(	2	,	Global.gridcosNames[	2	]	,	new ArrayList<TransmissionLine>()	,	0.8226	,	48	);
		transTRANSELCA	=	new	Transmitter(	3	,	Global.gridcosNames[	3	]	,	new ArrayList<TransmissionLine>()	,	0.0414	,	72	);
		*/
		
		// 2020
		transCENS	=	new	Transmitter(	0	,	Global.gridcosNames[	0	]	,	new ArrayList<TransmissionLine>()	,	0.0093	,	0	);
		transEEB	=	new	Transmitter(	1	,	Global.gridcosNames[	1	]	,	new ArrayList<TransmissionLine>()	,	0.0938	,	24	);
		transISA	=	new	Transmitter(	2	,	Global.gridcosNames[	2	]	,	new ArrayList<TransmissionLine>()	,	0.8680	,	48	);
		transTRANSELCA	=	new	Transmitter(	3	,	Global.gridcosNames[	3	]	,	new ArrayList<TransmissionLine>()	,	0.0289	,	72	);
	}
	
	public void initializeTransmissionLines()
	{
	
		//
		// líneas de transmisión
		//
		/*
		TransmissionLine	lineANTCQR	=	new	TransmissionLine(	0	,	transISA	,	nodeANTIOQUI	,	nodeCQR	,	Global.linesNames[	0	]	,	460.00	,	6.93	,	Global.linesCod[	0	]	,	0	);
		TransmissionLine	lineANTSCA	=	new	TransmissionLine(	1	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	1	]	,	1930.00	,	19.05	,	Global.linesCod[	1	]	,	24	);
		TransmissionLine	lineANTMAG	=	new	TransmissionLine(	2	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	2	]	,	402.50	,	6.78	,	Global.linesCod[	2	]	,	48	);
		TransmissionLine	lineATLCDS	=	new	TransmissionLine(	3	,	transISA	,	nodeATLANTIC	,	nodeCORDOSUC	,	Global.linesNames[	3	]	,	2437.50	,	10.07	,	Global.linesCod[	3	]	,	72	);
		TransmissionLine	lineBOGMAG	=	new	TransmissionLine(	4	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	4	]	,	1405.14	,	11.77	,	Global.linesCod[	4	]	,	96	);
		TransmissionLine	lineBOGMET	=	new	TransmissionLine(	5	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	5	]	,	552.00	,	9.10	,	Global.linesCod[	5	]	,	120	);
		TransmissionLine	lineBOLATL	=	new	TransmissionLine(	6	,	transISA_TRANSELCA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	6	]	,	481.58	,	10.03	,	Global.linesCod[	6	]	,	144	);
		TransmissionLine	lineBOLGCM	=	new	TransmissionLine(	7	,	transISA	,	nodeBOLIVAR	,	nodeGCM	,	Global.linesNames[	7	]	,	952.50	,	4.91	,	Global.linesCod[	7	]	,	168	);
		TransmissionLine	lineCAUECU	=	new	TransmissionLine(	8	,	transISA_EEB	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	8	]	,	972.90	,	13.85	,	Global.linesCod[	8	]	,	192	);
		TransmissionLine	lineCERMAG	=	new	TransmissionLine(	9	,	transISA	,	nodeCERROMAT	,	nodeMAGDAMED	,	Global.linesNames[	9	]	,	1250.00	,	5.00	,	Global.linesCod[	9	]	,	216	);
		TransmissionLine	lineCERANT	=	new	TransmissionLine(	10	,	transISA	,	nodeCERROMAT	,	nodeANTIOQUI	,	Global.linesNames[	10	]	,	952.50	,	5.13	,	Global.linesCod[	10	]	,	240	);
		TransmissionLine	lineCHIBOG	=	new	TransmissionLine(	11	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	11	]	,	911.26	,	13.72	,	Global.linesCod[	11	]	,	264	);
		TransmissionLine	lineCHINOR	=	new	TransmissionLine(	12	,	transISA	,	nodeCHIVOR	,	nodeNORDESTE	,	Global.linesNames[	12	]	,	441.60	,	6.84	,	Global.linesCod[	12	]	,	288	);
		TransmissionLine	lineCDSCER	=	new	TransmissionLine(	13	,	transISA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	13	]	,	2440.00	,	10.03	,	Global.linesCod[	13	]	,	312	);
		TransmissionLine	lineCQRTOL	=	new	TransmissionLine(	14	,	transISA	,	nodeCQR	,	nodeTOLIMA	,	Global.linesNames[	14	]	,	390.08	,	6.98	,	Global.linesCod[	14	]	,	336	);
		TransmissionLine	lineCQRVAL	=	new	TransmissionLine(	15	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	15	]	,	1143.10	,	17.33	,	Global.linesCod[	15	]	,	360	);
		TransmissionLine	lineGCMATL	=	new	TransmissionLine(	16	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	16	]	,	544.50	,	10.25	,	Global.linesCod[	16	]	,	384	);
		TransmissionLine	lineGCMNOR	=	new	TransmissionLine(	17	,	transISA	,	nodeGCM	,	nodeNORDESTE	,	Global.linesNames[	17	]	,	952.50	,	4.91	,	Global.linesCod[	17	]	,	408	);
		TransmissionLine	lineGCMCUA	=	new	TransmissionLine(	18	,	transISA	,	nodeGCM	,	nodeCUATRICENTENARIO	,	Global.linesNames[	18	]	,	202.86	,	3.30	,	Global.linesCod[	18	]	,	432	);
		TransmissionLine	lineHUICAU	=	new	TransmissionLine(	19	,	transISA_EEB	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	19	]	,	846.40	,	16.62	,	Global.linesCod[	19	]	,	456	);
		TransmissionLine	lineHUITOL	=	new	TransmissionLine(	20	,	transISA	,	nodeHUILACAQ	,	nodeTOLIMA	,	Global.linesNames[	20	]	,	205.85	,	3.41	,	Global.linesCod[	20	]	,	480	);
		TransmissionLine	lineLAMBOG	=	new	TransmissionLine(	21	,	transISA	,	nodeLAMIEL	,	nodeBOGOTA	,	Global.linesNames[	21	]	,	454.48	,	6.93	,	Global.linesCod[	21	]	,	504	);
		TransmissionLine	lineLAMTOL	=	new	TransmissionLine(	22	,	transISA	,	nodeLAMIEL	,	nodeTOLIMA	,	Global.linesNames[	22	]	,	458.16	,	6.94	,	Global.linesCod[	22	]	,	528	);
		TransmissionLine	lineMAGSCA	=	new	TransmissionLine(	23	,	transISA	,	nodeMAGDAMED	,	nodeSANCARLO	,	Global.linesNames[	23	]	,	1436.76	,	8.35	,	Global.linesCod[	23	]	,	552	);
		TransmissionLine	lineMAGNOR	=	new	TransmissionLine(	24	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	24	]	,	1550.04	,	14.99	,	Global.linesCod[	24	]	,	576	);
		TransmissionLine	lineNORCOR	=	new	TransmissionLine(	25	,	transCENS	,	nodeNORDESTE	,	nodeCOROZO	,	Global.linesNames[	25	]	,	346.38	,	9.02	,	Global.linesCod[	25	]	,	600	);
		TransmissionLine	linePAGBOG	=	new	TransmissionLine(	26	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	26	]	,	956.80	,	13.91	,	Global.linesCod[	26	]	,	624	);
		TransmissionLine	lineSANBOG	=	new	TransmissionLine(	27	,	transISA	,	nodeSANCARLO	,	nodeBOGOTA	,	Global.linesNames[	27	]	,	445.28	,	6.97	,	Global.linesCod[	27	]	,	648	);
		TransmissionLine	lineSANCQR	=	new	TransmissionLine(	28	,	transISA	,	nodeSANCARLO	,	nodeCQR	,	Global.linesNames[	28	]	,	446.20	,	6.88	,	Global.linesCod[	28	]	,	672	);
		TransmissionLine	lineSANVAL	=	new	TransmissionLine(	29	,	transISA	,	nodeSANCARLO	,	nodeVALLECAU	,	Global.linesNames[	29	]	,	1000.00	,	5.22	,	Global.linesCod[	29	]	,	696	);
		TransmissionLine	lineTOLBOG	=	new	TransmissionLine(	30	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	30	]	,	882.28	,	13.86	,	Global.linesCod[	30	]	,	720	);
		TransmissionLine	lineVALCAU	=	new	TransmissionLine(	31	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	31	]	,	461.38	,	7.00	,	Global.linesCod[	31	]	,	744	);
		 */
		
		/* 2012
		lineANTCQR1	=	new	TransmissionLine(	0	,	transISA	,	nodeANTIOQUI	,	nodeCQR	,	Global.linesNames[	0	]	,	230.00	,	2.063131834	,	Global.linesCod[	0	]	,	0	);
		lineANTCQR2	=	new	TransmissionLine(	1	,	transISA	,	nodeANTIOQUI	,	nodeCQR	,	Global.linesNames[	1	]	,	230.00	,	2.063131834	,	Global.linesCod[	1	]	,	24	);
		lineANTSCA1	=	new	TransmissionLine(	2	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	2	]	,	223.33	,	2.00080032	,	Global.linesCod[	2	]	,	48	);
		lineANTSCA2	=	new	TransmissionLine(	3	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	3	]	,	223.33	,	2.00080032	,	Global.linesCod[	3	]	,	72	);
		lineANTSCA3	=	new	TransmissionLine(	4	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	4	]	,	265.42	,	2.06185567	,	Global.linesCod[	4	]	,	96	);
		lineANTSCA4	=	new	TransmissionLine(	5	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	5	]	,	265.42	,	2.061430633	,	Global.linesCod[	5	]	,	120	);
		lineANTSCA5	=	new	TransmissionLine(	6	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	6	]	,	952.50	,	3.034901366	,	Global.linesCod[	6	]	,	144	);
		lineANTMAG1	=	new	TransmissionLine(	7	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	7	]	,	225.86	,	2.094679514	,	Global.linesCod[	7	]	,	168	);
		lineANTMAG2	=	new	TransmissionLine(	8	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	8	]	,	176.64	,	1.898974554	,	Global.linesCod[	8	]	,	192	);
		lineATLCDS1	=	new	TransmissionLine(	9	,	transISA	,	nodeATLANTIC	,	nodeCORDOSUC	,	Global.linesNames[	9	]	,	1250.00	,	2.988643156	,	Global.linesCod[	9	]	,	216	);
		lineATLCDS2	=	new	TransmissionLine(	10	,	transISA	,	nodeATLANTIC	,	nodeCORDOSUC	,	Global.linesNames[	10	]	,	1187.50	,	3.06936771	,	Global.linesCod[	10	]	,	240	);
		lineBOGMAG1	=	new	TransmissionLine(	11	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	11	]	,	952.50	,	3.021148036	,	Global.linesCod[	11	]	,	264	);
		lineBOGMAG2	=	new	TransmissionLine(	12	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	12	]	,	226.32	,	2.0733983	,	Global.linesCod[	12	]	,	288	);
		lineBOGMAG3	=	new	TransmissionLine(	13	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	13	]	,	226.32	,	2.0733983	,	Global.linesCod[	13	]	,	312	);
		lineBOGMET1	=	new	TransmissionLine(	14	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	14	]	,	220.80	,	2.674511902	,	Global.linesCod[	14	]	,	336	);
		lineBOGMET2	=	new	TransmissionLine(	15	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	15	]	,	331.20	,	2.688172043	,	Global.linesCod[	15	]	,	360	);
		lineBOLATL1	=	new	TransmissionLine(	16	,	transTRANSELCA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	16	]	,	138.60	,	1.893939394	,	Global.linesCod[	16	]	,	384	);
		lineBOLATL2	=	new	TransmissionLine(	17	,	transISA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	17	]	,	204.38	,	2.008032129	,	Global.linesCod[	17	]	,	408	);
		lineBOLATL3	=	new	TransmissionLine(	18	,	transTRANSELCA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	18	]	,	138.60	,	1.885369532	,	Global.linesCod[	18	]	,	432	);
		lineBOLGCM1	=	new	TransmissionLine(	19	,	transISA	,	nodeBOLIVAR	,	nodeGCM	,	Global.linesNames[	19	]	,	952.50	,	2.992220227	,	Global.linesCod[	19	]	,	456	);
		lineCAUECU1	=	new	TransmissionLine(	20	,	transISA	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	20	]	,	242.19	,	2.06185567	,	Global.linesCod[	20	]	,	480	);
		lineCAUECU2	=	new	TransmissionLine(	21	,	transISA	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	21	]	,	242.19	,	2.06185567	,	Global.linesCod[	21	]	,	504	);
		lineCAUECU3	=	new	TransmissionLine(	22	,	transEEB	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	22	]	,	244.26	,	2.047502048	,	Global.linesCod[	22	]	,	528	);
		lineCAUECU4	=	new	TransmissionLine(	23	,	transEEB	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	23	]	,	244.26	,	2.047502048	,	Global.linesCod[	23	]	,	552	);
		lineCERMAG1	=	new	TransmissionLine(	24	,	transISA	,	nodeCERROMAT	,	nodeMAGDAMED	,	Global.linesNames[	24	]	,	1250.00	,	3.098853424	,	Global.linesCod[	24	]	,	576	);
		lineCERANT1	=	new	TransmissionLine(	25	,	transISA	,	nodeCERROMAT	,	nodeANTIOQUI	,	Global.linesNames[	25	]	,	952.50	,	3.03030303	,	Global.linesCod[	25	]	,	600	);
		lineCHIBOG1	=	new	TransmissionLine(	26	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	26	]	,	220.80	,	1.99920032	,	Global.linesCod[	26	]	,	624	);
		lineCHIBOG2	=	new	TransmissionLine(	27	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	27	]	,	220.80	,	1.99920032	,	Global.linesCod[	27	]	,	648	);
		lineCHIBOG3	=	new	TransmissionLine(	28	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	28	]	,	234.83	,	2.006420546	,	Global.linesCod[	28	]	,	672	);
		lineCHIBOG4	=	new	TransmissionLine(	29	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	29	]	,	234.83	,	2.006420546	,	Global.linesCod[	29	]	,	696	);
		lineCHINOR1	=	new	TransmissionLine(	30	,	transISA	,	nodeCHIVOR	,	nodeNORDESTE	,	Global.linesNames[	30	]	,	220.80	,	2.062706271	,	Global.linesCod[	30	]	,	720	);
		lineCHINOR2	=	new	TransmissionLine(	31	,	transISA	,	nodeCHIVOR	,	nodeNORDESTE	,	Global.linesNames[	31	]	,	220.80	,	2.062706271	,	Global.linesCod[	31	]	,	744	);
		lineCDSCER1	=	new	TransmissionLine(	32	,	transISA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	32	]	,	1190.00	,	2.988643156	,	Global.linesCod[	32	]	,	768	);
		lineCDSCER2	=	new	TransmissionLine(	33	,	transISA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	33	]	,	1250.00	,	3.092145949	,	Global.linesCod[	33	]	,	792	);
		lineCQRTOL1	=	new	TransmissionLine(	34	,	transISA	,	nodeCQR	,	nodeTOLIMA	,	Global.linesNames[	34	]	,	206.08	,	2.037074761	,	Global.linesCod[	34	]	,	816	);
		lineCQRTOL2	=	new	TransmissionLine(	35	,	transISA	,	nodeCQR	,	nodeTOLIMA	,	Global.linesNames[	35	]	,	184.00	,	2.041649653	,	Global.linesCod[	35	]	,	840	);
		lineCQRVAL1	=	new	TransmissionLine(	36	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	36	]	,	230.00	,	2.165439584	,	Global.linesCod[	36	]	,	864	);
		lineCQRVAL2	=	new	TransmissionLine(	37	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	37	]	,	230.00	,	2.165439584	,	Global.linesCod[	37	]	,	888	);
		lineCQRVAL3	=	new	TransmissionLine(	38	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	38	]	,	226.55	,	2.022244692	,	Global.linesCod[	38	]	,	912	);
		lineCQRVAL4	=	new	TransmissionLine(	39	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	39	]	,	226.55	,	2.022244692	,	Global.linesCod[	39	]	,	936	);
		lineCQRVAL5	=	new	TransmissionLine(	40	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	40	]	,	230.00	,	2.07641196	,	Global.linesCod[	40	]	,	960	);
		lineGCMATL1	=	new	TransmissionLine(	41	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	41	]	,	144.32	,	1.906577693	,	Global.linesCod[	41	]	,	984	);
		lineGCMATL2	=	new	TransmissionLine(	42	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	42	]	,	213.62	,	2.085505735	,	Global.linesCod[	42	]	,	1008	);
		lineGCMATL3	=	new	TransmissionLine(	43	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	43	]	,	186.56	,	2.078137988	,	Global.linesCod[	43	]	,	1032	);
		lineGCMNOR1	=	new	TransmissionLine(	44	,	transISA	,	nodeGCM	,	nodeNORDESTE	,	Global.linesNames[	44	]	,	952.50	,	2.808988764	,	Global.linesCod[	44	]	,	1056	);
		lineGCMCUA1	=	new	TransmissionLine(	45	,	transISA	,	nodeGCM	,	nodeCUATRICENTENARIO	,	Global.linesNames[	45	]	,	202.86	,	1.902949572	,	Global.linesCod[	45	]	,	1080	);
		lineHUICAU1	=	new	TransmissionLine(	46	,	transISA	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	46	]	,	184.00	,	2.134927412	,	Global.linesCod[	46	]	,	1104	);
		lineHUICAU2	=	new	TransmissionLine(	47	,	transISA	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	47	]	,	184.00	,	2.134927412	,	Global.linesCod[	47	]	,	1128	);
		lineHUICAU3	=	new	TransmissionLine(	48	,	transEEB	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	48	]	,	239.20	,	2.75558005	,	Global.linesCod[	48	]	,	1152	);
		lineHUICAU4	=	new	TransmissionLine(	49	,	transEEB	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	49	]	,	239.20	,	2.784739627	,	Global.linesCod[	49	]	,	1176	);
		lineHUITOL1	=	new	TransmissionLine(	50	,	transISA	,	nodeHUILACAQ	,	nodeTOLIMA	,	Global.linesNames[	50	]	,	205.85	,	1.950458358	,	Global.linesCod[	50	]	,	1200	);
		lineLAMBOG1	=	new	TransmissionLine(	51	,	transISA	,	nodeLAMIEL	,	nodeBOGOTA	,	Global.linesNames[	51	]	,	227.24	,	2.073828287	,	Global.linesCod[	51	]	,	1224	);
		lineLAMBOG2	=	new	TransmissionLine(	52	,	transISA	,	nodeLAMIEL	,	nodeBOGOTA	,	Global.linesNames[	52	]	,	227.24	,	2.073828287	,	Global.linesCod[	52	]	,	1248	);
		lineLAMTOL1	=	new	TransmissionLine(	53	,	transISA	,	nodeLAMIEL	,	nodeTOLIMA	,	Global.linesNames[	53	]	,	229.08	,	2.073828287	,	Global.linesCod[	53	]	,	1272	);
		lineLAMTOL2	=	new	TransmissionLine(	54	,	transISA	,	nodeLAMIEL	,	nodeTOLIMA	,	Global.linesNames[	54	]	,	229.08	,	2.073828287	,	Global.linesCod[	54	]	,	1296	);
		lineMAGSCA1	=	new	TransmissionLine(	55	,	transISA	,	nodeMAGDAMED	,	nodeSANCARLO	,	Global.linesNames[	55	]	,	1250.00	,	3.078817734	,	Global.linesCod[	55	]	,	1320	);
		lineMAGSCA2	=	new	TransmissionLine(	56	,	transISA	,	nodeMAGDAMED	,	nodeSANCARLO	,	Global.linesNames[	56	]	,	186.76	,	1.948937829	,	Global.linesCod[	56	]	,	1344	);
		lineMAGNOR1	=	new	TransmissionLine(	57	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	57	]	,	952.50	,	2.994011976	,	Global.linesCod[	57	]	,	1368	);
		lineMAGNOR2	=	new	TransmissionLine(	58	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	58	]	,	186.30	,	1.813236627	,	Global.linesCod[	58	]	,	1392	);
		lineMAGNOR3	=	new	TransmissionLine(	59	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	59	]	,	186.07	,	1.864628007	,	Global.linesCod[	59	]	,	1416	);
		lineMAGNOR4	=	new	TransmissionLine(	60	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	60	]	,	225.17	,	2.098195552	,	Global.linesCod[	60	]	,	1440	);
		lineNORCOR1	=	new	TransmissionLine(	61	,	transCENS	,	nodeNORDESTE	,	nodeCOROZO	,	Global.linesNames[	61	]	,	173.19	,	2.38492726	,	Global.linesCod[	61	]	,	1464	);
		lineNORCOR2	=	new	TransmissionLine(	62	,	transCENS	,	nodeNORDESTE	,	nodeCOROZO	,	Global.linesNames[	62	]	,	173.19	,	2.38492726	,	Global.linesCod[	62	]	,	1488	);
		linePAGBOG1	=	new	TransmissionLine(	63	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	63	]	,	257.60	,	2.082899396	,	Global.linesCod[	63	]	,	1512	);
		linePAGBOG2	=	new	TransmissionLine(	64	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	64	]	,	257.60	,	2.082899396	,	Global.linesCod[	64	]	,	1536	);
		linePAGBOG3	=	new	TransmissionLine(	65	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	65	]	,	220.80	,	2.086811352	,	Global.linesCod[	65	]	,	1560	);
		linePAGBOG4	=	new	TransmissionLine(	66	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	66	]	,	220.80	,	2.082032063	,	Global.linesCod[	66	]	,	1584	);
		lineSANBOG1	=	new	TransmissionLine(	67	,	transISA	,	nodeSANCARLO	,	nodeBOGOTA	,	Global.linesNames[	67	]	,	222.64	,	2.085505735	,	Global.linesCod[	67	]	,	1608	);
		lineSANBOG2	=	new	TransmissionLine(	68	,	transISA	,	nodeSANCARLO	,	nodeBOGOTA	,	Global.linesNames[	68	]	,	222.64	,	2.085505735	,	Global.linesCod[	68	]	,	1632	);
		lineSANCQR1	=	new	TransmissionLine(	69	,	transISA	,	nodeSANCARLO	,	nodeCQR	,	Global.linesNames[	69	]	,	223.10	,	2.01247736	,	Global.linesCod[	69	]	,	1656	);
		lineSANCQR2	=	new	TransmissionLine(	70	,	transISA	,	nodeSANCARLO	,	nodeCQR	,	Global.linesNames[	70	]	,	223.10	,	2.01247736	,	Global.linesCod[	70	]	,	1680	);
		lineSANVAL1	=	new	TransmissionLine(	71	,	transISA	,	nodeSANCARLO	,	nodeVALLECAU	,	Global.linesNames[	71	]	,	1000.00	,	3.169572108	,	Global.linesCod[	71	]	,	1704	);
		lineTOLBOG1	=	new	TransmissionLine(	72	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	72	]	,	222.18	,	2.071680133	,	Global.linesCod[	72	]	,	1728	);
		lineTOLBOG2	=	new	TransmissionLine(	73	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	73	]	,	222.18	,	2.071680133	,	Global.linesCod[	73	]	,	1752	);
		lineTOLBOG3	=	new	TransmissionLine(	74	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	74	]	,	218.96	,	2.025931929	,	Global.linesCod[	74	]	,	1776	);
		lineTOLBOG4	=	new	TransmissionLine(	75	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	75	]	,	218.96	,	2.025931929	,	Global.linesCod[	75	]	,	1800	);
		lineVALCAU1	=	new	TransmissionLine(	76	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	76	]	,	230.69	,	2.093364036	,	Global.linesCod[	76	]	,	1824	);
		lineVALCAU2	=	new	TransmissionLine(	77	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	77	]	,	230.69	,	2.103049422	,	Global.linesCod[	77	]	,	1848	);
		*/
		
		//2020
		lineANTCQR1	=	new	TransmissionLine(	0	,	transISA	,	nodeANTIOQUI	,	nodeCQR	,	Global.linesNames[	0	]	,	230.00	,	2.0631	,	Global.linesCod[	0	]	,	0	);
		lineANTCQR2	=	new	TransmissionLine(	1	,	transISA	,	nodeANTIOQUI	,	nodeCQR	,	Global.linesNames[	1	]	,	230.00	,	2.0631	,	Global.linesCod[	1	]	,	24	);
		lineANTSCA1	=	new	TransmissionLine(	2	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	2	]	,	223.33	,	2.0008	,	Global.linesCod[	2	]	,	48	);
		lineANTSCA2	=	new	TransmissionLine(	3	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	3	]	,	223.33	,	2.0008	,	Global.linesCod[	3	]	,	72	);
		lineANTSCA3	=	new	TransmissionLine(	4	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	4	]	,	265.42	,	2.0619	,	Global.linesCod[	4	]	,	96	);
		lineANTSCA4	=	new	TransmissionLine(	5	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	5	]	,	265.42	,	2.0614	,	Global.linesCod[	5	]	,	120	);
		lineANTSCA5	=	new	TransmissionLine(	6	,	transISA	,	nodeANTIOQUI	,	nodeSANCARLO	,	Global.linesNames[	6	]	,	952.50	,	3.0349	,	Global.linesCod[	6	]	,	144	);
		lineANTMAG1	=	new	TransmissionLine(	7	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	7	]	,	225.86	,	2.0947	,	Global.linesCod[	7	]	,	168	);
		lineANTMAG2	=	new	TransmissionLine(	8	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	8	]	,	176.64	,	1.8990	,	Global.linesCod[	8	]	,	192	);
		lineATLCDS1	=	new	TransmissionLine(	9	,	transISA	,	nodeATLANTIC	,	nodeCORDOSUC	,	Global.linesNames[	9	]	,	1250.00	,	2.9886	,	Global.linesCod[	9	]	,	216	);
		lineATLCDS2	=	new	TransmissionLine(	10	,	transISA	,	nodeATLANTIC	,	nodeCORDOSUC	,	Global.linesNames[	10	]	,	1187.50	,	3.0694	,	Global.linesCod[	10	]	,	240	);
		lineBOGMAG1	=	new	TransmissionLine(	11	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	11	]	,	952.50	,	3.0211	,	Global.linesCod[	11	]	,	264	);
		lineBOGMAG2	=	new	TransmissionLine(	12	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	12	]	,	226.32	,	2.0734	,	Global.linesCod[	12	]	,	288	);
		lineBOGMAG3	=	new	TransmissionLine(	13	,	transISA	,	nodeBOGOTA	,	nodeMAGDAMED	,	Global.linesNames[	13	]	,	226.32	,	2.0734	,	Global.linesCod[	13	]	,	312	);
		lineBOGMET1	=	new	TransmissionLine(	14	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	14	]	,	220.80	,	2.6745	,	Global.linesCod[	14	]	,	336	);
		lineBOGMET2	=	new	TransmissionLine(	15	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	15	]	,	331.20	,	2.6882	,	Global.linesCod[	15	]	,	360	);
		lineBOLATL1	=	new	TransmissionLine(	16	,	transTRANSELCA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	16	]	,	138.60	,	1.8939	,	Global.linesCod[	16	]	,	384	);
		lineBOLATL2	=	new	TransmissionLine(	17	,	transISA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	17	]	,	204.38	,	2.0080	,	Global.linesCod[	17	]	,	408	);
		lineBOLATL3	=	new	TransmissionLine(	18	,	transTRANSELCA	,	nodeBOLIVAR	,	nodeATLANTIC	,	Global.linesNames[	18	]	,	138.60	,	1.8854	,	Global.linesCod[	18	]	,	432	);
		lineBOLGCM1	=	new	TransmissionLine(	19	,	transISA	,	nodeBOLIVAR	,	nodeGCM	,	Global.linesNames[	19	]	,	952.50	,	2.9922	,	Global.linesCod[	19	]	,	456	);
		lineCAUECU1	=	new	TransmissionLine(	20	,	transISA	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	20	]	,	242.19	,	2.0619	,	Global.linesCod[	20	]	,	480	);
		lineCAUECU2	=	new	TransmissionLine(	21	,	transISA	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	21	]	,	242.19	,	2.0619	,	Global.linesCod[	21	]	,	504	);
		lineCAUECU3	=	new	TransmissionLine(	22	,	transEEB	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	22	]	,	244.26	,	2.0475	,	Global.linesCod[	22	]	,	528	);
		lineCAUECU4	=	new	TransmissionLine(	23	,	transEEB	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	23	]	,	244.26	,	2.0475	,	Global.linesCod[	23	]	,	552	);
		lineCERMAG1	=	new	TransmissionLine(	24	,	transISA	,	nodeCERROMAT	,	nodeMAGDAMED	,	Global.linesNames[	24	]	,	1250.00	,	3.0989	,	Global.linesCod[	24	]	,	576	);
		lineCERANT1	=	new	TransmissionLine(	25	,	transISA	,	nodeCERROMAT	,	nodeANTIOQUI	,	Global.linesNames[	25	]	,	952.50	,	3.0303	,	Global.linesCod[	25	]	,	600	);
		lineCHIBOG1	=	new	TransmissionLine(	26	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	26	]	,	220.80	,	1.9992	,	Global.linesCod[	26	]	,	624	);
		lineCHIBOG2	=	new	TransmissionLine(	27	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	27	]	,	220.80	,	1.9992	,	Global.linesCod[	27	]	,	648	);
		lineCHIBOG3	=	new	TransmissionLine(	28	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	28	]	,	234.83	,	2.0064	,	Global.linesCod[	28	]	,	672	);
		lineCHIBOG4	=	new	TransmissionLine(	29	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	29	]	,	234.83	,	2.0064	,	Global.linesCod[	29	]	,	696	);
		lineCHINOR1	=	new	TransmissionLine(	30	,	transISA	,	nodeCHIVOR	,	nodeNORDESTE	,	Global.linesNames[	30	]	,	220.80	,	2.0627	,	Global.linesCod[	30	]	,	720	);
		lineCHINOR2	=	new	TransmissionLine(	31	,	transISA	,	nodeCHIVOR	,	nodeNORDESTE	,	Global.linesNames[	31	]	,	220.80	,	2.0627	,	Global.linesCod[	31	]	,	744	);
		lineCDSCER1	=	new	TransmissionLine(	32	,	transISA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	32	]	,	1190.00	,	2.9886	,	Global.linesCod[	32	]	,	768	);
		lineCDSCER2	=	new	TransmissionLine(	33	,	transISA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	33	]	,	1250.00	,	3.0921	,	Global.linesCod[	33	]	,	792	);
		lineCQRTOL1	=	new	TransmissionLine(	34	,	transISA	,	nodeCQR	,	nodeTOLIMA	,	Global.linesNames[	34	]	,	206.08	,	2.0371	,	Global.linesCod[	34	]	,	816	);
		lineCQRTOL2	=	new	TransmissionLine(	35	,	transISA	,	nodeCQR	,	nodeTOLIMA	,	Global.linesNames[	35	]	,	184.00	,	2.0416	,	Global.linesCod[	35	]	,	840	);
		lineCQRVAL1	=	new	TransmissionLine(	36	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	36	]	,	230.00	,	2.1654	,	Global.linesCod[	36	]	,	864	);
		lineCQRVAL2	=	new	TransmissionLine(	37	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	37	]	,	230.00	,	2.1654	,	Global.linesCod[	37	]	,	888	);
		lineCQRVAL3	=	new	TransmissionLine(	38	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	38	]	,	226.55	,	2.0222	,	Global.linesCod[	38	]	,	912	);
		lineCQRVAL4	=	new	TransmissionLine(	39	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	39	]	,	226.55	,	2.0222	,	Global.linesCod[	39	]	,	936	);
		lineCQRVAL5	=	new	TransmissionLine(	40	,	transISA	,	nodeCQR	,	nodeVALLECAU	,	Global.linesNames[	40	]	,	230.00	,	2.0764	,	Global.linesCod[	40	]	,	960	);
		lineGCMATL1	=	new	TransmissionLine(	41	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	41	]	,	144.32	,	1.9066	,	Global.linesCod[	41	]	,	984	);
		lineGCMATL2	=	new	TransmissionLine(	42	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	42	]	,	213.62	,	2.0855	,	Global.linesCod[	42	]	,	1008	);
		lineGCMATL3	=	new	TransmissionLine(	43	,	transTRANSELCA	,	nodeGCM	,	nodeATLANTIC	,	Global.linesNames[	43	]	,	186.56	,	2.0781	,	Global.linesCod[	43	]	,	1032	);
		lineGCMNOR1	=	new	TransmissionLine(	44	,	transISA	,	nodeGCM	,	nodeNORDESTE	,	Global.linesNames[	44	]	,	952.50	,	2.8090	,	Global.linesCod[	44	]	,	1056	);
		lineGCMCUA1	=	new	TransmissionLine(	45	,	transISA	,	nodeGCM	,	nodeCUATRICENTENARIO	,	Global.linesNames[	45	]	,	202.86	,	1.9029	,	Global.linesCod[	45	]	,	1080	);
		lineHUICAU1	=	new	TransmissionLine(	46	,	transISA	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	46	]	,	184.00	,	2.1349	,	Global.linesCod[	46	]	,	1104	);
		lineHUICAU2	=	new	TransmissionLine(	47	,	transISA	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	47	]	,	184.00	,	2.1349	,	Global.linesCod[	47	]	,	1128	);
		lineHUICAU3	=	new	TransmissionLine(	48	,	transEEB	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	48	]	,	239.20	,	2.7556	,	Global.linesCod[	48	]	,	1152	);
		lineHUICAU4	=	new	TransmissionLine(	49	,	transEEB	,	nodeHUILACAQ	,	nodeCAUCANAR	,	Global.linesNames[	49	]	,	239.20	,	2.7847	,	Global.linesCod[	49	]	,	1176	);
		lineHUITOL1	=	new	TransmissionLine(	50	,	transISA	,	nodeHUILACAQ	,	nodeTOLIMA	,	Global.linesNames[	50	]	,	205.85	,	1.9505	,	Global.linesCod[	50	]	,	1200	);
		lineLAMBOG1	=	new	TransmissionLine(	51	,	transISA	,	nodeLAMIEL	,	nodeBOGOTA	,	Global.linesNames[	51	]	,	227.24	,	2.0738	,	Global.linesCod[	51	]	,	1224	);
		lineLAMBOG2	=	new	TransmissionLine(	52	,	transISA	,	nodeLAMIEL	,	nodeBOGOTA	,	Global.linesNames[	52	]	,	227.24	,	2.0738	,	Global.linesCod[	52	]	,	1248	);
		lineLAMTOL1	=	new	TransmissionLine(	53	,	transISA	,	nodeLAMIEL	,	nodeTOLIMA	,	Global.linesNames[	53	]	,	229.08	,	2.0738	,	Global.linesCod[	53	]	,	1272	);
		lineLAMTOL2	=	new	TransmissionLine(	54	,	transISA	,	nodeLAMIEL	,	nodeTOLIMA	,	Global.linesNames[	54	]	,	229.08	,	2.0738	,	Global.linesCod[	54	]	,	1296	);
		lineMAGSCA1	=	new	TransmissionLine(	55	,	transISA	,	nodeMAGDAMED	,	nodeSANCARLO	,	Global.linesNames[	55	]	,	1250.00	,	3.0788	,	Global.linesCod[	55	]	,	1320	);
		lineMAGSCA2	=	new	TransmissionLine(	56	,	transISA	,	nodeMAGDAMED	,	nodeSANCARLO	,	Global.linesNames[	56	]	,	186.76	,	1.9489	,	Global.linesCod[	56	]	,	1344	);
		lineMAGNOR1	=	new	TransmissionLine(	57	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	57	]	,	952.50	,	2.9940	,	Global.linesCod[	57	]	,	1368	);
		lineMAGNOR2	=	new	TransmissionLine(	58	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	58	]	,	186.30	,	1.8132	,	Global.linesCod[	58	]	,	1392	);
		lineMAGNOR3	=	new	TransmissionLine(	59	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	59	]	,	186.07	,	1.8646	,	Global.linesCod[	59	]	,	1416	);
		lineMAGNOR4	=	new	TransmissionLine(	60	,	transISA	,	nodeMAGDAMED	,	nodeNORDESTE	,	Global.linesNames[	60	]	,	225.17	,	2.0982	,	Global.linesCod[	60	]	,	1440	);
		lineNORCOR1	=	new	TransmissionLine(	61	,	transCENS	,	nodeNORDESTE	,	nodeCOROZO	,	Global.linesNames[	61	]	,	173.19	,	2.3849	,	Global.linesCod[	61	]	,	1464	);
		lineNORCOR2	=	new	TransmissionLine(	62	,	transCENS	,	nodeNORDESTE	,	nodeCOROZO	,	Global.linesNames[	62	]	,	173.19	,	2.3849	,	Global.linesCod[	62	]	,	1488	);
		linePAGBOG1	=	new	TransmissionLine(	63	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	63	]	,	257.60	,	2.0829	,	Global.linesCod[	63	]	,	1512	);
		linePAGBOG2	=	new	TransmissionLine(	64	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	64	]	,	257.60	,	2.0829	,	Global.linesCod[	64	]	,	1536	);
		linePAGBOG3	=	new	TransmissionLine(	65	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	65	]	,	220.80	,	2.0868	,	Global.linesCod[	65	]	,	1560	);
		linePAGBOG4	=	new	TransmissionLine(	66	,	transEEB	,	nodePAGUA	,	nodeBOGOTA	,	Global.linesNames[	66	]	,	220.80	,	2.0820	,	Global.linesCod[	66	]	,	1584	);
		lineSANBOG1	=	new	TransmissionLine(	67	,	transISA	,	nodeSANCARLO	,	nodeBOGOTA	,	Global.linesNames[	67	]	,	222.64	,	2.0855	,	Global.linesCod[	67	]	,	1608	);
		lineSANBOG2	=	new	TransmissionLine(	68	,	transISA	,	nodeSANCARLO	,	nodeBOGOTA	,	Global.linesNames[	68	]	,	222.64	,	2.0855	,	Global.linesCod[	68	]	,	1632	);
		lineSANCQR1	=	new	TransmissionLine(	69	,	transISA	,	nodeSANCARLO	,	nodeCQR	,	Global.linesNames[	69	]	,	223.10	,	2.0125	,	Global.linesCod[	69	]	,	1656	);
		lineSANCQR2	=	new	TransmissionLine(	70	,	transISA	,	nodeSANCARLO	,	nodeCQR	,	Global.linesNames[	70	]	,	223.10	,	2.0125	,	Global.linesCod[	70	]	,	1680	);
		lineSANVAL1	=	new	TransmissionLine(	71	,	transISA	,	nodeSANCARLO	,	nodeVALLECAU	,	Global.linesNames[	71	]	,	1000.00	,	3.1696	,	Global.linesCod[	71	]	,	1704	);
		lineTOLBOG1	=	new	TransmissionLine(	72	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	72	]	,	222.18	,	2.0717	,	Global.linesCod[	72	]	,	1728	);
		lineTOLBOG2	=	new	TransmissionLine(	73	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	73	]	,	222.18	,	2.0717	,	Global.linesCod[	73	]	,	1752	);
		lineTOLBOG3	=	new	TransmissionLine(	74	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	74	]	,	218.96	,	2.0259	,	Global.linesCod[	74	]	,	1776	);
		lineTOLBOG4	=	new	TransmissionLine(	75	,	transISA	,	nodeTOLIMA	,	nodeBOGOTA	,	Global.linesNames[	75	]	,	218.96	,	2.0259	,	Global.linesCod[	75	]	,	1800	);
		lineVALCAU1	=	new	TransmissionLine(	76	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	76	]	,	230.69	,	2.0934	,	Global.linesCod[	76	]	,	1824	);
		lineVALCAU2	=	new	TransmissionLine(	77	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	77	]	,	230.69	,	2.1030	,	Global.linesCod[	77	]	,	1848	);
		lineANTMAG3	=	new	TransmissionLine(	78	,	transISA	,	nodeANTIOQUI	,	nodeMAGDAMED	,	Global.linesNames[	78	]	,	1000.00	,	3.1696	,	Global.linesCod[	78	]	,	1872	);
		lineANTCERR1	=	new	TransmissionLine(	79	,	transISA	,	nodeANTIOQUI	,	nodeCERROMAT	,	Global.linesNames[	79	]	,	1000.00	,	3.1696	,	Global.linesCod[	79	]	,	1896	);
		lineCERRGCM1	=	new	TransmissionLine(	80	,	transISA	,	nodeCERROMAT	,	nodeGCM	,	Global.linesNames[	80	]	,	1000.00	,	3.1696	,	Global.linesCod[	80	]	,	1920	);
		lineMAGBOG1	=	new	TransmissionLine(	81	,	transISA	,	nodeMAGDAMED	,	nodeBOGOTA	,	Global.linesNames[	81	]	,	1000.00	,	3.1696	,	Global.linesCod[	81	]	,	1944	);
		lineANTVALL1	=	new	TransmissionLine(	82	,	transISA	,	nodeANTIOQUI	,	nodeVALLECAU	,	Global.linesNames[	82	]	,	1000.00	,	3.1696	,	Global.linesCod[	82	]	,	1968	);
		lineVALLBOG1	=	new	TransmissionLine(	83	,	transISA	,	nodeVALLECAU	,	nodeBOGOTA	,	Global.linesNames[	83	]	,	1000.00	,	3.1696	,	Global.linesCod[	83	]	,	1992	);
		lineVALLCAU3	=	new	TransmissionLine(	84	,	transISA	,	nodeVALLECAU	,	nodeCAUCANAR	,	Global.linesNames[	84	]	,	1000.00	,	3.1696	,	Global.linesCod[	84	]	,	2016	);
		lineCAUECU5	=	new	TransmissionLine(	85	,	transISA	,	nodeCAUCANAR	,	nodeECUADOR220	,	Global.linesNames[	85	]	,	1000.00	,	3.1696	,	Global.linesCod[	85	]	,	2040	);
		lineCERRPAN1	=	new	TransmissionLine(	86	,	transISA	,	nodeCERROMAT	,	nodePANAMA	,	Global.linesNames[	86	]	,	600.00	,	3.0303	,	Global.linesCod[	86	]	,	2064	);
		lineBOGMET3	=	new	TransmissionLine(	87	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	87	]	,	222.18	,	2.0717	,	Global.linesCod[	87	]	,	2088	);
		lineBOGMET4	=	new	TransmissionLine(	88	,	transEEB	,	nodeBOGOTA	,	nodeMETA	,	Global.linesNames[	88	]	,	222.18	,	2.0717	,	Global.linesCod[	88	]	,	2112	);
		lineCORDCERR2	=	new	TransmissionLine(	89	,	transTRANSELCA	,	nodeCORDOSUC	,	nodeCERROMAT	,	Global.linesNames[	89	]	,	222.18	,	2.0717	,	Global.linesCod[	89	]	,	2136	);
		lineHUIVALL1	=	new	TransmissionLine(	90	,	transEEB	,	nodeHUILACAQ	,	nodeVALLECAU	,	Global.linesNames[	90	]	,	222.18	,	2.0717	,	Global.linesCod[	90	]	,	2160	);
		lineHUIVALL2	=	new	TransmissionLine(	91	,	transEEB	,	nodeHUILACAQ	,	nodeVALLECAU	,	Global.linesNames[	91	]	,	222.18	,	2.0717	,	Global.linesCod[	91	]	,	2184	);
		lineCHIBOG5	=	new	TransmissionLine(	92	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	92	]	,	222.18	,	2.0717	,	Global.linesCod[	92	]	,	2208	);
		lineCHIBOG6	=	new	TransmissionLine(	93	,	transISA	,	nodeCHIVOR	,	nodeBOGOTA	,	Global.linesNames[	93	]	,	222.18	,	2.0717	,	Global.linesCod[	93	]	,	2232	);
	}
	
	//
	// definición de los elementos del mercado
	//
	public void marketElements()
	{
		this.initializeNodes();
		this.initializeGenerators();
		this.initializeGenerationUnits();
		this.initializeRetailers();
		this.initializeTransmitters();
		this.initializeTransmissionLines();
	}
	
	//
	// definición del mercado
	//
	public void marketDefine() throws NumberFormatException, IOException{
		//
		// lista de nodos del sistema
		//
		nodes.add(	nodeANTIOQUI	);	nodes.add(	nodeATLANTIC	);	nodes.add(	nodeBOGOTA		);	nodes.add(	nodeBOLIVAR		);	nodes.add(	nodeCAUCANAR	);			
		nodes.add(	nodeCERROMAT	);  nodes.add(	nodeCHIVOR		);	nodes.add(	nodeCORDOSUC	);	nodes.add(	nodeCQR			);	nodes.add(	nodeGCM			);			
		nodes.add(	nodeHUILACAQ	);	nodes.add(	nodeLAMIEL		);	nodes.add(	nodeMAGDAMED	);	nodes.add(	nodeMETA		);	nodes.add(	nodeNORDESTE	);
		nodes.add(	nodePAGUA		);	nodes.add(	nodeSANCARLO	);	nodes.add(	nodeTOLIMA		); 	nodes.add(	nodeVALLECAU	);	nodes.add(	nodeCOROZO		);			
		nodes.add(nodeCUATRICENTENARIO);nodes.add(	nodeECUADOR220	);
		
		nodes.add(nodePANAMA);

		//
		// lista de generadores del sistema
		//
		
		/*
		// 2012
		generators.add(	genEPSG	);	generators.add(	genGECG	);	generators.add(	genENDG	);	generators.add(	genCHVG	);	generators.add(	genDLRG	);
		generators.add(	genESSG	);	generators.add(	genCVAG	);	generators.add(	genCHCG	);	generators.add(	genECUG	);	generators.add(	genFRSG	);
		generators.add(	genGNCG	);	generators.add(	genEPMG	);	generators.add(	genISGG	);	generators.add(	genADCG	);	generators.add(	genEEPG	);
		generators.add(	genLCSG	);	generators.add(	genCDNG	);	generators.add(	genCTMG	);	generators.add(	genEECG	);	generators.add(	genFACG	);
		generators.add(	genERCG	);	generators.add(	genEGCG	);	generators.add(	genCIVG	);	generators.add(	genHLAG	);	generators.add(	genGEEG	);
		generators.add(	genCETG	);	generators.add(	genTYPG	);	generators.add(	genHIMG	);	generators.add(	genPRLG	);	generators.add(	genTRMG	);
		generators.add(	genTCDG	);	generators.add(	genTEMG	);	generators.add(	genTRPG	);	generators.add(	genEMUG	);	
		*/
		
		// 2020
		generators.add(	genEPSG	);
		generators.add(	genGECG	);
		generators.add(	genENDG	);
		generators.add(	genCHVG	);
		generators.add(	genDLRG	);
		generators.add(	genESSG	);
		generators.add(	genCVAG	);
		generators.add(	genCHCG	);
		generators.add(	genECUG	);
		generators.add(	genFRSG	);
		generators.add(	genGNCG	);
		generators.add(	genEPMG	);
		generators.add(	genISGG	);
		generators.add(	genADCG	);
		generators.add(	genEEPG	);
		generators.add(	genLCSG	);
		generators.add(	genCDNG	);
		generators.add(	genCTMG	);
		generators.add(	genEECG	);
		generators.add(	genFACG	);
		generators.add(	genERCG	);
		generators.add(	genEGCG	);
		generators.add(	genCIVG	);
		generators.add(	genHLAG	);
		generators.add(	genGEEG	);
		generators.add(	genCETG	);
		generators.add(	genTYPG	);
		generators.add(	genHIMG	);
		generators.add(	genPRLG	);
		generators.add(	genTRMG	);
		generators.add(	genTCDG	);
		generators.add(	genTEMG	);
		generators.add(	genTRPG	);
		generators.add(	genEMUG	);
		generators.add(	genPOLG	);
		generators.add(	genHIDG	);
		generators.add(	genHMVG	);
		generators.add(	genENAG	);
		generators.add(	genITUG	);
		generators.add(	genTENG	);
		generators.add(	genPANG	);
		generators.add(	genPUTG	);		

		//
		// lista de unidades de generación del sistema
		//
		
		/*
		// 2012
		generationUnits.add(	unitALBAN		);	generationUnits.add(	unitBARRANQ3	);	generationUnits.add(	unitBARRANQ4	);	generationUnits.add(	unitBETANIA		);	generationUnits.add(	unitCALIMA1		);
		generationUnits.add(	unitCHIVOR		);	generationUnits.add(	unitCOINCAUCA	);	generationUnits.add(	unitCOROZO1		);	generationUnits.add(	unitCSANCARLOS	);	generationUnits.add(	unitCTGEMG1		);	
		generationUnits.add(	unitCTGEMG2		);	generationUnits.add(	unitCTGEMG3		);	generationUnits.add(	unitCVALLEC1	);	generationUnits.add(	unitDORADA1		);	generationUnits.add(	unitECUADOR11	);
		generationUnits.add(	unitECUADOR12	);	generationUnits.add(	unitECUADOR13	);	generationUnits.add(	unitECUADOR14	);	generationUnits.add(	unitECUADOR21	);	generationUnits.add(	unitECUADOR22	);	
		generationUnits.add(	unitECUADOR23	);	generationUnits.add(	unitECUADOR24	);	generationUnits.add(	unitESMERALDA	);	generationUnits.add(	unitFLORES1		);	generationUnits.add(	unitFLORES21	);
		generationUnits.add(	unitFLORES3		);	generationUnits.add(	unitFLORESIVB	);	generationUnits.add(	unitFLORIDA2	);	generationUnits.add(	unitGUAJIR11	);	generationUnits.add(	unitGUAJIR21	);
		generationUnits.add(	unitGUATAPE		);	generationUnits.add(	unitGUATRON		);	generationUnits.add(	unitGUAVIO		);	generationUnits.add(	unitINSULA		);	generationUnits.add(	unitJAGUAS		);
		generationUnits.add(	unitLATASAJERA	);	generationUnits.add(	unitMAGUAFRE	);	generationUnits.add(	unitMANTIOQ1	);	generationUnits.add(	unitMBELMONTE	);	generationUnits.add(	unitMBOGOTA1	);
		generationUnits.add(	unitMCALDERAS	);	generationUnits.add(	unitMCARUQUIA	);	generationUnits.add(	unitMCASCADA1	);	generationUnits.add(	unitMCAUCAN1	);	generationUnits.add(	unitMCAUCAN2	);
		generationUnits.add(	unitMCIMARR1	);	generationUnits.add(	unitMCQR1		);	generationUnits.add(	unitMCUNDINAMARCA);	generationUnits.add(	unitMCURRUCU	);	generationUnits.add(	unitMELBOSQUE	);
		generationUnits.add(	unitMEMCALI		);	generationUnits.add(	unitMERILEC1	);	generationUnits.add(	unitMGUANAQUITA	);	generationUnits.add(	unitMHUILAQ1	);	generationUnits.add(	unitMIEL1		);
		generationUnits.add(	unitMJEPIRAC	);	generationUnits.add(	unitMMORRO1		);	generationUnits.add(	unitMMORRO2		);	generationUnits.add(	unitMNLIBARE	);	generationUnits.add(	unitMNORDE1		);
		generationUnits.add(	unitMPRADO4		);	generationUnits.add(	unitMRIOMAYO	);	generationUnits.add(	unitMSANTANA	);	generationUnits.add(	unitMSANTARITA	);	generationUnits.add(	unitMSANTIAGO	);
		generationUnits.add(	unitMTOLIMA1	);	generationUnits.add(	unitMTULUA		);	generationUnits.add(	unitMVALLEC1	);	generationUnits.add(	unitMYOPAL1		);	generationUnits.add(	unitM_AMAIME	);
		generationUnits.add(	unitM_PROVIDEN	);	generationUnits.add(	unitPAGUA		);	generationUnits.add(	unitPAIPA1		);	generationUnits.add(	unitPAIPA2		);	generationUnits.add(	unitPAIPA3		);
		generationUnits.add(	unitPAIPA4		);	generationUnits.add(	unitPALENQ3		);	generationUnits.add(	unitPLAYAS		);	generationUnits.add(	unitPORCE2		);	generationUnits.add(	unitPORCE3		);
		generationUnits.add(	unitPORCE3P		);	generationUnits.add(	unitPRADO		);	generationUnits.add(	unitPROELEC1	);	generationUnits.add(	unitPROELEC2	);	generationUnits.add(	unitRPIEDRAS	);
		generationUnits.add(	unitSALVAJINA	);	generationUnits.add(	unitSANCARLOS	);	generationUnits.add(	unitSANFRANCISCO);	generationUnits.add(	unitTASAJER1	);	generationUnits.add(	unitTCANDEL1	);
		generationUnits.add(	unitTCANDEL2	);	generationUnits.add(	unitTCENTRO1	);	generationUnits.add(	unitTEBSA		);	generationUnits.add(	unitTEMCALI		);	generationUnits.add(	unitTPIEDRAS	);
		generationUnits.add(	unitTSIERRA		);	generationUnits.add(	unitTVALLE		);	generationUnits.add(	unitTYOPAL2		);	generationUnits.add(	unitURRA		);	generationUnits.add(	unitVENEZUE1	);
		generationUnits.add(	unitZIPAEMG2	);	generationUnits.add(	unitZIPAEMG3	);	generationUnits.add(	unitZIPAEMG4	);	generationUnits.add(	unitZIPAEMG5	);
		*/
		
		// 2020
		generationUnits.add(	unitALBAN	);
		generationUnits.add(	unitBARRANQ3	);
		generationUnits.add(	unitBARRANQ4	);
		generationUnits.add(	unitBETANIA	);
		generationUnits.add(	unitCALIMA1	);
		generationUnits.add(	unitCHIVOR	);
		generationUnits.add(	unitCOINCAUCA	);
		generationUnits.add(	unitCOROZO1	);
		generationUnits.add(	unitCSANCARLOS	);
		generationUnits.add(	unitCTGEMG1	);
		generationUnits.add(	unitCTGEMG2	);
		generationUnits.add(	unitCTGEMG3	);
		generationUnits.add(	unitCVALLEC1	);
		generationUnits.add(	unitDORADA1	);
		generationUnits.add(	unitECUADOR11	);
		generationUnits.add(	unitECUADOR12	);
		generationUnits.add(	unitECUADOR13	);
		generationUnits.add(	unitECUADOR14	);
		generationUnits.add(	unitECUADOR21	);
		generationUnits.add(	unitECUADOR22	);
		generationUnits.add(	unitECUADOR23	);
		generationUnits.add(	unitECUADOR24	);
		generationUnits.add(	unitESMERALDA	);
		generationUnits.add(	unitFLORESIV	);
		generationUnits.add(	unitFLORES1	);
		generationUnits.add(	unitFLORIDA2	);
		generationUnits.add(	unitGUAJIR11	);
		generationUnits.add(	unitGUAJIR21	);
		generationUnits.add(	unitGUATAPE	);
		generationUnits.add(	unitGUATRON	);
		generationUnits.add(	unitGUAVIO	);
		generationUnits.add(	unitINSULA	);
		generationUnits.add(	unitJAGUAS	);
		generationUnits.add(	unitLATASAJERA	);
		generationUnits.add(	unitM_AMAIME	);
		generationUnits.add(	unitM_PROVIDEN	);
		generationUnits.add(	unitMAGUAFRE	);
		generationUnits.add(	unitMALTOTULUA1	);
		generationUnits.add(	unitMANTIOQ1	);
		generationUnits.add(	unitMBARROSO1	);
		generationUnits.add(	unitMBELMONTE	);
		generationUnits.add(	unitMBOGOTA1	);
		generationUnits.add(	unitMCALDERAS	);
		generationUnits.add(	unitMCARUQUIA	);
		generationUnits.add(	unitMCASCADA1	);
		generationUnits.add(	unitMCAUCAN1	);
		generationUnits.add(	unitMCAUCAN2	);
		generationUnits.add(	unitMCIMARR1	);
		generationUnits.add(	unitMCQR1	);
		generationUnits.add(	unitMCUNDINAMARCA	);
		generationUnits.add(	unitMCURRUCU	);
		generationUnits.add(	unitMELBOSQUE	);
		generationUnits.add(	unitMEMCALI	);
		generationUnits.add(	unitMERILEC1	);
		generationUnits.add(	unitMGUANAQUITA	);
		generationUnits.add(	unitMHUILAQ1	);
		generationUnits.add(	unitMIEL1	);
		generationUnits.add(	unitMJEPIRAC	);
		generationUnits.add(	unitMMONTAÑITAS	);
		generationUnits.add(	unitMMORRO1	);
		generationUnits.add(	unitMMORRO2	);
		generationUnits.add(	unitMNLIBARE	);
		generationUnits.add(	unitMNORDE1	);
		generationUnits.add(	unitMPRADO4	);
		generationUnits.add(	unitMRIOMAYO	);
		generationUnits.add(	unitMSANFRANCISC	);
		generationUnits.add(	unitMSANTANA	);
		generationUnits.add(	unitMSANTARITA	);
		generationUnits.add(	unitMSANTIAGO	);
		generationUnits.add(	unitMTOLIMA1	);
		generationUnits.add(	unitMTULUA	);
		generationUnits.add(	unitMVALLEC1	);
		generationUnits.add(	unitMYOPAL1	);
		generationUnits.add(	unitPAGUA	);
		generationUnits.add(	unitPAIPA1	);
		generationUnits.add(	unitPAIPA2	);
		generationUnits.add(	unitPAIPA3	);
		generationUnits.add(	unitPAIPA4	);
		generationUnits.add(	unitPLAYAS	);
		generationUnits.add(	unitPORCE2	);
		generationUnits.add(	unitPORCE3	);
		generationUnits.add(	unitPRADO	);
		generationUnits.add(	unitPROELEC1	);
		generationUnits.add(	unitPROELEC2	);
		generationUnits.add(	unitRPIEDRAS	);
		generationUnits.add(	unitSALVAJINA	);
		generationUnits.add(	unitSANCARLOS	);
		generationUnits.add(	unitSANFRANCISCO	);
		generationUnits.add(	unitTASAJER1	);
		generationUnits.add(	unitTCANDEL1	);
		generationUnits.add(	unitTCANDEL2	);
		generationUnits.add(	unitTCENTRO1	);
		generationUnits.add(	unitTEBSA	);
		generationUnits.add(	unitTEMCALI	);
		generationUnits.add(	unitTPIEDRAS	);
		generationUnits.add(	unitTSIERRA	);
		generationUnits.add(	unitTVALLE	);
		generationUnits.add(	unitTYOPAL2	);
		generationUnits.add(	unitURRA	);
		generationUnits.add(	unitVENEZUE1	);
		generationUnits.add(	unitZIPAEMG2	);
		generationUnits.add(	unitZIPAEMG3	);
		generationUnits.add(	unitZIPAEMG4	);
		generationUnits.add(	unitZIPAEMG5	);
		generationUnits.add(	unitAMOYA	);
		generationUnits.add(	unitTERMOCOL	);
		generationUnits.add(	unitCUCUANA	);
		generationUnits.add(	unitELQUIMBO	);
		generationUnits.add(	unitSOGAMOSO	);
		generationUnits.add(	unitCARLOSLLERAS	);
		generationUnits.add(	unitPOPAL	);
		generationUnits.add(	unitRIOAMBEIMA	);
		generationUnits.add(	unitGECELCA32	);
		generationUnits.add(	unitITUANGO	);
		generationUnits.add(	unitPORVENIRII	);
		generationUnits.add(	unitTERMONORTE	);
		generationUnits.add(	unitPANAMA	);

		//
		// lista de comercializadores 
		//		
		retailers.add(	retANTIOQUI	);	retailers.add(	retATLANTIC	);	retailers.add(	retBOGOTA	);	retailers.add(	retBOLIVAR	);	retailers.add(	retCAUCANAR	);
		retailers.add(	retCERROMAT	);	retailers.add(	retCHIVOR	);	retailers.add(	retCORDOSUC	);	retailers.add(	retCQR		);	retailers.add(	retGCM	);
		retailers.add(	retHUILACAQ	);	retailers.add(	retLAMIEL	);	retailers.add(	retMAGDAMED	);	retailers.add(	retMETA		);	retailers.add(	retNORDESTE	);
		retailers.add(	retPAGUA	);	retailers.add(	retSANCARLO	);	retailers.add(	retTOLIMA	);	retailers.add(	retVALLECAU	);	retailers.add(	retCOROZO	);
		retailers.add(	retCUATRICENTENARIO	);	retailers.add(	retECUADOR220	);
		
		retailers.add(retPANAMA);

		
		//
		// lista de transmisores del sistema
		//
		transmitters.add(	transCENS	);			transmitters.add(	transEEB		);
		transmitters.add(	transISA	);			transmitters.add(	transTRANSELCA	);

		
		//
		// lista de líneas de transmisión del sistema
		//
		/*transmissionLines.add(	lineANTCQR	);	transmissionLines.add(	lineANTSCA	);	transmissionLines.add(	lineANTMAG	);	transmissionLines.add(	lineATLCDS	);	transmissionLines.add(	lineBOGMAG	);	
		transmissionLines.add(	lineBOGMET	);	transmissionLines.add(	lineBOLATL	);	transmissionLines.add(	lineBOLGCM	);	transmissionLines.add(	lineCAUECU	);	transmissionLines.add(	lineCERMAG	);	
		transmissionLines.add(	lineCERANT	);	transmissionLines.add(	lineCHIBOG	);	transmissionLines.add(	lineCHINOR	);	transmissionLines.add(	lineCDSCER	);	transmissionLines.add(	lineCQRTOL	);	
		transmissionLines.add(	lineCQRVAL	);	transmissionLines.add(	lineGCMATL	);	transmissionLines.add(	lineGCMNOR	);	transmissionLines.add(	lineGCMCUA	);	transmissionLines.add(	lineHUICAU	);	
		transmissionLines.add(	lineHUITOL	);	transmissionLines.add(	lineLAMBOG	);	transmissionLines.add(	lineLAMTOL	);	transmissionLines.add(	lineMAGSCA	);	transmissionLines.add(	lineMAGNOR	);	
		transmissionLines.add(	lineNORCOR	);	transmissionLines.add(	linePAGBOG	);	transmissionLines.add(	lineSANBOG	);	transmissionLines.add(	lineSANCQR	);	transmissionLines.add(	lineSANVAL	);	
		transmissionLines.add(	lineTOLBOG	);	transmissionLines.add(	lineVALCAU	);
		*/
		
		/*
		// 2012
		transmissionLines.add(	lineANTCQR1	); transmissionLines.add(	lineANTCQR2	); transmissionLines.add(	lineANTSCA1	); transmissionLines.add(	lineANTSCA2	); transmissionLines.add(	lineANTSCA3	); 
		transmissionLines.add(	lineANTSCA4	); transmissionLines.add(	lineANTSCA5	); transmissionLines.add(	lineANTMAG1	); transmissionLines.add(	lineANTMAG2	); transmissionLines.add(	lineATLCDS1	);
		transmissionLines.add(	lineATLCDS2	); transmissionLines.add(	lineBOGMAG1	); transmissionLines.add(	lineBOGMAG2	); transmissionLines.add(	lineBOGMAG3	); transmissionLines.add(	lineBOGMET1	);
		transmissionLines.add(	lineBOGMET2	); transmissionLines.add(	lineBOLATL1	); transmissionLines.add(	lineBOLATL2	); transmissionLines.add(	lineBOLATL3	); transmissionLines.add(	lineBOLGCM1	);
		transmissionLines.add(	lineCAUECU1	); transmissionLines.add(	lineCAUECU2	); transmissionLines.add(	lineCAUECU3	); transmissionLines.add(	lineCAUECU4	); transmissionLines.add(	lineCERMAG1	);
		transmissionLines.add(	lineCERANT1	); transmissionLines.add(	lineCHIBOG1	); transmissionLines.add(	lineCHIBOG2	); transmissionLines.add(	lineCHIBOG3	); transmissionLines.add(	lineCHIBOG4	);
		transmissionLines.add(	lineCHINOR1	); transmissionLines.add(	lineCHINOR2	); transmissionLines.add(	lineCDSCER1	); transmissionLines.add(	lineCDSCER2	); transmissionLines.add(	lineCQRTOL1	);
		transmissionLines.add(	lineCQRTOL2	); transmissionLines.add(	lineCQRVAL1	); transmissionLines.add(	lineCQRVAL2	); transmissionLines.add(	lineCQRVAL3	); transmissionLines.add(	lineCQRVAL4	);
		transmissionLines.add(	lineCQRVAL5	); transmissionLines.add(	lineGCMATL1	); transmissionLines.add(	lineGCMATL2	); transmissionLines.add(	lineGCMATL3	); transmissionLines.add(	lineGCMNOR1	);
		transmissionLines.add(	lineGCMCUA1	); transmissionLines.add(	lineHUICAU1	); transmissionLines.add(	lineHUICAU2	); transmissionLines.add(	lineHUICAU3	); transmissionLines.add(	lineHUICAU4	);
		transmissionLines.add(	lineHUITOL1	); transmissionLines.add(	lineLAMBOG1	); transmissionLines.add(	lineLAMBOG2	); transmissionLines.add(	lineLAMTOL1	); transmissionLines.add(	lineLAMTOL2	);
		transmissionLines.add(	lineMAGSCA1	); transmissionLines.add(	lineMAGSCA2	); transmissionLines.add(	lineMAGNOR1	); transmissionLines.add(	lineMAGNOR2	); transmissionLines.add(	lineMAGNOR3	);
		transmissionLines.add(	lineMAGNOR4	); transmissionLines.add(	lineNORCOR1	); transmissionLines.add(	lineNORCOR2	); transmissionLines.add(	linePAGBOG1	); transmissionLines.add(	linePAGBOG2	);
		transmissionLines.add(	linePAGBOG3	); transmissionLines.add(	linePAGBOG4	); transmissionLines.add(	lineSANBOG1	); transmissionLines.add(	lineSANBOG2	); transmissionLines.add(	lineSANCQR1	);
		transmissionLines.add(	lineSANCQR2	); transmissionLines.add(	lineSANVAL1	); transmissionLines.add(	lineTOLBOG1	); transmissionLines.add(	lineTOLBOG2	); transmissionLines.add(	lineTOLBOG3	);
		transmissionLines.add(	lineTOLBOG4	); transmissionLines.add(	lineVALCAU1	); transmissionLines.add(	lineVALCAU2	); 
		*/
		
		// 2020
		transmissionLines.add(	lineANTCQR1	);
		transmissionLines.add(	lineANTCQR2	);
		transmissionLines.add(	lineANTSCA1	);
		transmissionLines.add(	lineANTSCA2	);
		transmissionLines.add(	lineANTSCA3	);
		transmissionLines.add(	lineANTSCA4	);
		transmissionLines.add(	lineANTSCA5	);
		transmissionLines.add(	lineANTMAG1	);
		transmissionLines.add(	lineANTMAG2	);
		transmissionLines.add(	lineATLCDS1	);
		transmissionLines.add(	lineATLCDS2	);
		transmissionLines.add(	lineBOGMAG1	);
		transmissionLines.add(	lineBOGMAG2	);
		transmissionLines.add(	lineBOGMAG3	);
		transmissionLines.add(	lineBOGMET1	);
		transmissionLines.add(	lineBOGMET2	);
		transmissionLines.add(	lineBOLATL1	);
		transmissionLines.add(	lineBOLATL2	);
		transmissionLines.add(	lineBOLATL3	);
		transmissionLines.add(	lineBOLGCM1	);
		transmissionLines.add(	lineCAUECU1	);
		transmissionLines.add(	lineCAUECU2	);
		transmissionLines.add(	lineCAUECU3	);
		transmissionLines.add(	lineCAUECU4	);
		transmissionLines.add(	lineCERMAG1	);
		transmissionLines.add(	lineCERANT1	);
		transmissionLines.add(	lineCHIBOG1	);
		transmissionLines.add(	lineCHIBOG2	);
		transmissionLines.add(	lineCHIBOG3	);
		transmissionLines.add(	lineCHIBOG4	);
		transmissionLines.add(	lineCHINOR1	);
		transmissionLines.add(	lineCHINOR2	);
		transmissionLines.add(	lineCDSCER1	);
		transmissionLines.add(	lineCDSCER2	);
		transmissionLines.add(	lineCQRTOL1	);
		transmissionLines.add(	lineCQRTOL2	);
		transmissionLines.add(	lineCQRVAL1	);
		transmissionLines.add(	lineCQRVAL2	);
		transmissionLines.add(	lineCQRVAL3	);
		transmissionLines.add(	lineCQRVAL4	);
		transmissionLines.add(	lineCQRVAL5	);
		transmissionLines.add(	lineGCMATL1	);
		transmissionLines.add(	lineGCMATL2	);
		transmissionLines.add(	lineGCMATL3	);
		transmissionLines.add(	lineGCMNOR1	);
		transmissionLines.add(	lineGCMCUA1	);
		transmissionLines.add(	lineHUICAU1	);
		transmissionLines.add(	lineHUICAU2	);
		transmissionLines.add(	lineHUICAU3	);
		transmissionLines.add(	lineHUICAU4	);
		transmissionLines.add(	lineHUITOL1	);
		transmissionLines.add(	lineLAMBOG1	);
		transmissionLines.add(	lineLAMBOG2	);
		transmissionLines.add(	lineLAMTOL1	);
		transmissionLines.add(	lineLAMTOL2	);
		transmissionLines.add(	lineMAGSCA1	);
		transmissionLines.add(	lineMAGSCA2	);
		transmissionLines.add(	lineMAGNOR1	);
		transmissionLines.add(	lineMAGNOR2	);
		transmissionLines.add(	lineMAGNOR3	);
		transmissionLines.add(	lineMAGNOR4	);
		transmissionLines.add(	lineNORCOR1	);
		transmissionLines.add(	lineNORCOR2	);
		transmissionLines.add(	linePAGBOG1	);
		transmissionLines.add(	linePAGBOG2	);
		transmissionLines.add(	linePAGBOG3	);
		transmissionLines.add(	linePAGBOG4	);
		transmissionLines.add(	lineSANBOG1	);
		transmissionLines.add(	lineSANBOG2	);
		transmissionLines.add(	lineSANCQR1	);
		transmissionLines.add(	lineSANCQR2	);
		transmissionLines.add(	lineSANVAL1	);
		transmissionLines.add(	lineTOLBOG1	);
		transmissionLines.add(	lineTOLBOG2	);
		transmissionLines.add(	lineTOLBOG3	);
		transmissionLines.add(	lineTOLBOG4	);
		transmissionLines.add(	lineVALCAU1	);
		transmissionLines.add(	lineVALCAU2	);
		transmissionLines.add(	lineANTMAG3	);
		transmissionLines.add(	lineANTCERR1	);
		transmissionLines.add(	lineCERRGCM1	);
		transmissionLines.add(	lineMAGBOG1	);
		transmissionLines.add(	lineANTVALL1	);
		transmissionLines.add(	lineVALLBOG1	);
		transmissionLines.add(	lineVALLCAU3	);
		transmissionLines.add(	lineCAUECU5	);
		transmissionLines.add(	lineCERRPAN1	);
		transmissionLines.add(	lineBOGMET3	);
		transmissionLines.add(	lineBOGMET4	);
		transmissionLines.add(	lineCORDCERR2	);
		transmissionLines.add(	lineHUIVALL1	);
		transmissionLines.add(	lineHUIVALL2	);
		transmissionLines.add(	lineCHIBOG5	);
		transmissionLines.add(	lineCHIBOG6	);

		
		//
		// establecer la lista de unidades de generación por cada generador
		//		
		for (int i = 0; i < Global.nGencos; i++)
		{
			for (int j = 0; j < Global.nUnits; j++)
			{
				if(this.generators.get(i).getGeneratorName().equals(this.generationUnits.get(j).getGenerator().getGeneratorName()))
				{
					this.generators.get(i).getGenerationUnits().add(this.generationUnits.get(j));
				}
			}
		}
		
		//
		// establecer la lista de líneas de transmisión por cada transmisor
		//
		for (int i = 0; i < Global.nGridcos; i++)
		{
			for (int j = 0; j < Global.nLines; j++)
			{
				if(this.transmitters.get(i).getTransmitterName().equals(this.transmissionLines.get(j).getLineOwner().getTransmitterName()))
				{
					this.transmitters.get(i).getTransmissionLines().add(this.transmissionLines.get(j));
				}
			}
		}		
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// cargos por uso de la red de transmisión
	//
	//-----------------------------------------------------------------------------------------------------------------------------//		
	public void setUsageCharges() throws NumberFormatException, IOException{
		// archivo para la lectura de los datos
		CsvReader usageChargesReader = new CsvReader(Global.usageChargesS);
		this.usageCharges = Global.rw.readCsv(usageChargesReader, 730, 24); // matriz con los datos de los cargos por uso
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// límites para los ángulos de fase
	//
	
	// límite inferior para el ángulo de voltaje en cada nodo
	double[] angleLb	= { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,
			-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE
	};
	
	// límite superior para el ángulo de voltaje en cada nodo
	double[] angleUb    = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,
			Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE
	};
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// límites los flujos en el despaho ideal
	//
	
	// límite inferior para los flujos en el despacho ideal
	//double[] flowLb	= Global.factory.repVec(-Double.MAX_VALUE, 24*Global.nLines);
			
	// límite superior para los flujos en el despacho ideal
	//double[] flowUb = Global.factory.repVec(Double.MAX_VALUE, 24*Global.nLines);
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// límites para la demanda no atendida
	//
	
	// límite inferior para la demanda no atendida en cada nodo
	double[] unservedLb	= Global.factory.repVec(0.0, 24*Global.nNodes);
	
	// límite superior para la demanda no atendida en cada nodo
	double[] unservedUb = Global.factory.repVec(Double.MAX_VALUE, 24*Global.nNodes);
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// límites para la potencia generada por cada unidad
	//
	
	// límite inferior para la potencia generada por cada unidad
	double [] lowPowerLimit = //Global.factory.repVec(0.0, 24*Global.nUnits);/*
	{	
			30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,	30.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,
			16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,	16.00	,
			10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,	100.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,	35.00	,
			60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,	60.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,	15.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,	20.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,	74.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,	55.00	,
			75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,	75.00	,
			125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,
			125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,	125.00	,
			8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,	8.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,	10.00	,
			12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,	12.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,	63.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,
			0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	,	0.00	
	};
		
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// Costo de la demanda no atendida en cada nodo
	double [] unsDemandCost = Global.factory.repVec(650000.0, 24*Global.nNodes);
	
	// Susceptancia de cada línea
	//double [] susceptance = {200,200,200,200,200,200,200,200,200,200,200};
	//double [] susceptance = {5,5,5,5,5,5,5,5,5,5,5};
	/*double [] susceptance = {6.93,	19.05, 	6.78,	10.07,	11.77,	9.10,	10.03,	4.91,	13.85,	5.00,
							 5.13,	13.72,	6.84,	10.03,	6.98,	17.33,	10.25,	4.91,	3.30,	16.62,
							 3.41,	6.93,	6.94,	8.35,	14.99,	9.02,	13.91,	6.97,	6.88,	5.22,
							 13.86,	7.00	};*/
	/*
	double [] susceptance = {3.465, 3.464, 3.421, 3.421, 3.535, 3.535, 5.138, 3.500, 3.276, 4.927,
							5.140, 4.831, 3.468, 3.468, 4.561, 4.538, 3.347, 3.361, 3.319, 4.914,
							3.452, 3.452, 3.472, 3.472, 5.004, 5.134, 3.419, 3.419, 3.442, 3.442,
							3.421, 3.421, 4.927, 5.101, 3.485, 3.494, 3.488, 3.488, 3.459, 3.459,
							3.432, 3.162, 3.543, 3.543, 4.914, 3.296, 3.619, 3.619, 4.664, 4.718,
							3.410, 3.466, 3.466, 3.468, 3.468, 5.018, 3.329, 4.911, 3.331, 3.209,
							3.535, 4.512, 4.512, 3.479, 3.479, 3.471, 3.476, 3.487, 3.487, 3.441,
							3.441, 5.224, 3.469, 3.469, 3.463, 3.463, 3.489, 3.511};
							*/
	double [] susceptance = {2.0631	,
			2.0631	,
			2.0008	,
			2.0008	,
			2.0619	,
			2.0614	,
			3.0349	,
			2.0947	,
			1.8990	,
			2.9886	,
			3.0694	,
			3.0211	,
			2.0734	,
			2.0734	,
			2.6745	,
			2.6882	,
			1.8939	,
			2.0080	,
			1.8854	,
			2.9922	,
			2.0619	,
			2.0619	,
			2.0475	,
			2.0475	,
			3.0989	,
			3.0303	,
			1.9992	,
			1.9992	,
			2.0064	,
			2.0064	,
			2.0627	,
			2.0627	,
			2.9886	,
			3.0921	,
			2.0371	,
			2.0416	,
			2.1654	,
			2.1654	,
			2.0222	,
			2.0222	,
			2.0764	,
			1.9066	,
			2.0855	,
			2.0781	,
			2.8090	,
			1.9029	,
			2.1349	,
			2.1349	,
			2.7556	,
			2.7847	,
			1.9505	,
			2.0738	,
			2.0738	,
			2.0738	,
			2.0738	,
			3.0788	,
			1.9489	,
			2.9940	,
			1.8132	,
			1.8646	,
			2.0982	,
			2.3849	,
			2.3849	,
			2.0829	,
			2.0829	,
			2.0868	,
			2.0820	,
			2.0855	,
			2.0855	,
			2.0125	,
			2.0125	,
			3.1696	,
			2.0717	,
			2.0717	,
			2.0259	,
			2.0259	,
			2.0934	,
			2.1030	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.1696	,
			3.0303	,
			2.0717	,
			2.0717	,
			2.0717	,
			2.0717	,
			2.0717	,
			2.0717	,
			2.0717};
	
	// Límite del flujo de potencia para cada línea
	/*double [] powerLimit = {460.00,	1930.00,	402.50,	2437.50,	1405.14,	552.00,		481.58,	952.50,	972.90,	1250.00,
							952.50,	911.26,		441.60,	2440.00,	390.08,		1143.10,	544.50,	952.50,	202.86,	846.40,
							205.85,	454.48,		458.16,	1436.76,	1550.04,	346.38,		956.80,	445.28,	446.20,	1000.00,
							882.28,	461.38	};*/
							
	double [] powerLimit = {230.00	,
			230.00	,
			223.33	,
			223.33	,
			265.42	,
			265.42	,
			952.50	,
			225.86	,
			176.64	,
			1250.00	,
			1187.50	,
			952.50	,
			226.32	,
			226.32	,
			220.80	,
			331.20	,
			138.60	,
			204.38	,
			138.60	,
			952.50	,
			242.19	,
			242.19	,
			244.26	,
			244.26	,
			1250.00	,
			952.50	,
			220.80	,
			220.80	,
			234.83	,
			234.83	,
			220.80	,
			220.80	,
			1190.00	,
			1250.00	,
			206.08	,
			184.00	,
			230.00	,
			230.00	,
			226.55	,
			226.55	,
			230.00	,
			144.32	,
			213.62	,
			186.56	,
			952.50	,
			202.86	,
			184.00	,
			184.00	,
			239.20	,
			239.20	,
			205.85	,
			227.24	,
			227.24	,
			229.08	,
			229.08	,
			1250.00	,
			186.76	,
			952.50	,
			186.30	,
			186.07	,
			225.17	,
			173.19	,
			173.19	,
			257.60	,
			257.60	,
			220.80	,
			220.80	,
			222.64	,
			222.64	,
			223.10	,
			223.10	,
			1000.00	,
			222.18	,
			222.18	,
			218.96	,
			218.96	,
			230.69	,
			230.69	,
			1000.00	,
			1000.00	,
			1000.00	,
			1000.00	,
			1000.00	,
			1000.00	,
			1000.00	,
			1000.00	,
			600.00	,
			222.18	,
			222.18	,
			222.18	,
			222.18	,
			222.18	,
			222.18	,
			222.18	};


	
	//double [] susceptance 			= operator.getSusceptances();		// susceptances of all lines
	//double [] powerLimit 			= operator.getPowerFlowLimits();	// power flows limits of all lines
	
	double [][] powerDemandVector   = new double[Global.nNodes][24];	// matrix of demand by hour by node
	double [][] dailyPowerBid		= new double[Global.nUnits][24];	// daily offer of price all generators
	double [][] powerBidPrice		= new double[Global.nUnits][24];	// daily offer of power of all generators for each hour
	
	// result of dispatch process
	//double [] generation	 = new double[24*Global.nUnits];	// hourly programmed generation
	//double [] nodalPrices	 = new double[24*Global.nNodes];
	//double [] unservedDemand = new double[24*Global.nNodes];	// hourly unserved demand
	//double [] flows 		 = new double[24*Global.nLines];	// flow of power by line by hour
	//double dispatchCost		= 0.0;						// cost value of daily dispatch
	//double [][] dispatch = {generation,nodalPrices,unservedDemand,flows,dispatchCost};
			
	//double [] unitCosts 		= {7.5,6.0,14.0,10.0}; 
	//double [] unitCosts 		= operator.getGenerationCosts();
	//double [][] unitCostMatrix 	= Global.factory.repMat(unitCosts,4,24);
	//double [] generationCost 	= Global.factory.mat2vec(unitCostMatrix,4,24);
	
	//data.printVector(generationCost);
	
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// strings para las variables para la generación
	//
	String[] powergen = new String[24*Global.nUnits];
	
	/*public void powergenDefine(){
		int j = 0; 
		for(int i = 0; i < Global.nUnits; i++){
			for(int h = 0; h < 24; h++){
				powergen[j] = "GP_"+Global.generationUnitsNames[i]+"_"+h;
				j = j + 1;
			}	
		}
	}
	*/
	public void powergenDefine(){
		int j = 0; 
		for(int i = 0; i < Global.nUnits; i++){
			for(int h = 0; h < 24; h++){
				powergen[j] = "GP_"+Global.generationUnitsNames[i]+"_"+h;
				j = j + 1;
			}	
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// strings para las variables para el ángulo de voltaje
	//
	String[] anglevol = new String[24*Global.nNodes];
	
	public void anglevolDefine(){
		int j = 0; 
		for(int i = 0; i < Global.nNodes; i++){
			for(int h = 0; h < 24; h++){
				anglevol[j] = "VA_"+Global.nodesNames[i]+"_"+h;
				j = j +1;
			}	
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// strings para las variables para los flujos
	//
	String[] flowsPot = new String[24*Global.nLines];
	
	public void flowsPotDefine(){
		int j = 0; 
		for(int i = 0; i < Global.nLines; i++){
			for(int h = 0; h < 24; h++){
				flowsPot[j] = "FL_"+Global.linesNames[i]+"_"+h;
				j = j +1;
			}	
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// strings para las variables para la demanda no atendida
	//
	String[] unserved = new String[24*Global.nNodes];
	
	public void unservedDefine(){
		int j = 0; 
		for(int i = 0; i < Global.nNodes; i++){
			for(int h = 0; h < 24; h++){
				unserved[j] = "UD_"+Global.nodesNames[i]+"_"+h;
				j = j +1;
			}	
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// variables para los despachos
	//
	IloNumVar[][] varI = new IloNumVar[2][]; // despacho ideal
	IloNumVar[][] varR = new IloNumVar[3][]; // despacho real
	IloNumVar[][] varIN = new IloNumVar[3][]; // despacho ideal flujos
	
	// matrices para guardar las restricciones de cada despacho
	IloRange[][]  rngIN = new IloRange[1][];
	public void rngINDefine(){ rngIN[0] = new IloRange[24*Global.nNodes];}
	
	IloRange[][]  rngI = new IloRange[1][];
	public void rngIDefine(){rngI[0] = new IloRange[24];}
	
	IloRange[][]  rngR = new IloRange[2][];
	public void rngRDefine(){rngR[0] = new IloRange[24*Global.nNodes];  rngR[1] = new IloRange[2*24*Global.nLines];}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// organización del arreglo de demandas
	//
	CsvReader demandReader;	// archivo con los datos de la demanda
	int[] posDem = new int[Global.nNodes];	// posisiones a ignorar del archivo de demandas
	double [][] demand = new double[366][Global.nNodes*25]; // arreglo para almacenar las demandas
	
	//
	// estructura las demandas para todo el horizonte de tiempo considerado
	//
	public void demandDefine(){
		try
		{
			demandReader = new CsvReader(Global.demandS2012);
			for (int i = 0; i < Global.nNodes; i++){
				posDem[i] = i*25;
			}
			demand = Global.rw.readCsv(demandReader, 366, Global.nNodes*25, posDem); // matriz con los datos de demanda por nodo por hora
			
			// escenario alto
			demand = Global.factory.productEscalarMatrix(demand,1.36);
			// escenario medio
			// demand = Global.factory.productEscalarMatrix(demand,1.312);
			// escenario bajo
			//demand = Global.factory.productEscalarMatrix(demand,1.272);
		}
		catch(IOException e) 
   	 	{
	   		 e.printStackTrace();
	   		System.out.println("demandDefine ->"+e);
	   	}	
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------//
	//
	// organización del arreglo de ofertas
	//
	CsvReader supplyReader;	// archivo con los datos de la oferta
	int[] posSup = new int[Global.nUnits]; // posisiones a ignorar del archivo de ofertas
	double [][] supply = new double[366][Global.nUnits*26];	// arreglo para almacenar las ofertas
	
	//
	// estructura las ofertas para todo el horizonte de tiempo considerado
	//
	public void supplyDefine(){
		try
		{
			supplyReader = new CsvReader(Global.supplyS2012);
			for (int i = 0; i < Global.nUnits; i++){
				posSup[i] = i*26;
			}
			supply = Global.rw.readCsv(supplyReader, 366, Global.nUnits*26, posSup); // matriz con los datos de oferta por planta por hora
		}
		catch(IOException e) 
   	 	{
	   		 e.printStackTrace();
	   		System.out.println("supplyDefine ->"+e);
	   	}	
	}
	//-----------------------------------------------------------------------------------------------------------------------------//
	
	// ofertas de disponibilidad para cada día
	double [][] dailySupply = new double [Global.nUnits][24]; 
	
	//
	// estructura las ofertas para cada día
	//
	public void dalilyPowerBids()
	{
		for (int i = 0; i < Global.nUnits; i++)
		{
			for (int h = 0; h < 24; h++)
			{
				this.dailySupply[i][h] = this.supply[this.iteration][1+i*25+h];
			}
		}
	}
	
	// generación total horaria de energía
	double [] totalSupply = new double[24];
	
	//
	// calcula la generación total horaria de energía para un día determinado
	//
	public void totalSupplyCalculate(){
		double [] totalSupply  = new double[24];
		for(int h = 0; h < 24; h++){
			for(int i = 0; i < Global.nUnits; i++){
				totalSupply[h] = totalSupply[h] + this.operator.getRealDispatch().getGeneration()[i][h];
			}
		}
		this.totalSupply = totalSupply;
	}
	
	// demanda total horaria de energía 
	double [] totalDemand = new double[24];
	
	//
	// calcula la demanda total de energía para cada hora del día 
	//
	public void totalDemandCalculate(){
		double [] totalDemand  = new double[24];
		for(int h = 0; h < 24; h++){
			for(int i = 0; i < Global.nNodes; i++){
				totalDemand[h] = totalDemand[h] + this.powerDemandVector[i][h];
			}
		}
		this.totalDemand = totalDemand;
	}
	
	//
	// definición de un conjunto de contratos de generación
	//
	public void contractsDefine() throws NumberFormatException, IOException{
		// variables apra generar aleatorios
		Random random = new Random(); 	// auxiliar para sacar valores aleatorios
		double randomContractType; 		// aleatorio para definir el tipo del contrato
		double randomGeneratorPrice;	// aleatorio para definir el precio ofertado por el generador
		
		// variables para almacenar resultados temporales
		List<PowerBid> powerBids;				// lista de ofertas de generadores para dada hora
		List<GenerationContract> contracts;		// lista de contratos de generación horarios
		//List<GenerationContract> retContracts;  	// lista de contratos de generación consolidados
		GenerationContract generationContract;	// contrato consolidado por cada generador
		double[] contractPower;					// vector de cantidades contratadas
		double[] contractPrice;					// vector de precios de contrato
		
		// ordenamiento de los comercializadores por demanda de referencia promedio
		List<Retailer> orderRetailers = new ArrayList<Retailer>();	// variable para hacer una copia local de comercializadores para ser ordenada
		orderRetailers.addAll(this.retailers); 						// vacia los comercializadores en la capia local
		Collections.sort(orderRetailers);							// ordenamiento usando como criterio el nivel de demanda
		
		// para cada comercializador
		for(int ret = 0; ret < orderRetailers.size(); ret++)
		{
			// inicialización lista de contratos horarios
			contracts = new ArrayList<GenerationContract>();
			
			// inicialización lista de contratos consolidados
			//retContracts = new ArrayList<GenerationContract>();
						
			// para cada hora del día
			for(int h = 0; h < 24; h++)
			{
				// lista de ofertas de precios y cantidad para contratar
				powerBids = new ArrayList<PowerBid>();
				
				// para cada unidad de generación
				for(int unit = 0; unit < this.generationUnits.size(); unit++)
				{
					// si la capacidad efectiva de la unidad es mayor que un nivel dado
					if(this.generationUnits.get(unit).getEffectiveCapacity() >= Global.contractPowerLowerLimit)
					{
						// si la unidad de generación aun tiene capacidad de generación disponible
						if(this.generationUnits.get(unit).getContractEnergy()[h] 
									< this.generationUnits.get(unit).getContractsProportion()[h] * this.generationUnits.get(unit).getEffectiveCapacity())
						{
							// la unidad de generación define un precio para el contrato
							randomGeneratorPrice = random.nextDouble() *(((1 + Global.contractPriceRange) * this.generationUnits.get(unit).getReferenceContractsPrice()[h]) + 1)
												   + (1 - Global.contractPriceRange) * this.generationUnits.get(unit).getReferenceContractsPrice()[h];
							
							// la unidad de generación define la oferta horaria. La capacidad ofertada es la capacidad remanente después de contratos anteriores
							powerBids.add(new PowerBid(this.generationUnits.get(unit),
														randomGeneratorPrice, 
														this.generationUnits.get(unit).getContractsProportion()[h] * this.generationUnits.get(unit).getEffectiveCapacity()
															- this.generationUnits.get(unit).getContractEnergy()[h]));
						}
					}
				}
			
				// ordenar las ofertas de menor a mayor precio
				Collections.sort(powerBids);
								
				// formación de contratos horarios
				for (int bid = 0; bid < powerBids.size(); bid++)
				{
					// si el comercializador aun requiere contratar para alcanzar su cuota de demanda contratada
					if (orderRetailers.get(ret).getContractEnergy()[h] 
							< orderRetailers.get(ret).getContractsProportion()[h] * orderRetailers.get(ret).getReferenceDemand()[h])
					{
						// formación del contrato horario par ala hora "h" con la unidad de generación que estableció la oferta, el comercializador, la cantidad 
						// a contratar y el precio para dicha hora. Se almacena también la hora para la cual se realizó el contrato para poder consolidar un contrato general
						// por generador. La energía a contratar es el mínimo entre la generación ofertada y la cantidad de energía que le falta al comercializador
						// para cumplir su cuota de contratación. 
						contracts.add(new GenerationContract(powerBids.get(bid).getGenerationUnit(),
								orderRetailers.get(ret),
								Math.min(powerBids.get(bid).getHourlyBidPower(),
										orderRetailers.get(ret).getContractsProportion()[h] * orderRetailers.get(ret).getReferenceDemand()[h] - orderRetailers.get(ret).getContractEnergy()[h]),
								powerBids.get(bid).getBidPrice(),
								h));
						
						// actualización de la energía contratada para el generador. A lo que se tiene contratado con otros comercializadores
						// se le suma lo que se acaba de contratar. 
						for(int unit = 0; unit < this.generationUnits.size(); unit++)
						{ 
							if(this.generationUnits.get(unit).equals(powerBids.get(bid).getGenerationUnit()))
							{
								this.generationUnits.get(unit).getContractEnergy()[h] = powerBids.get(bid).getGenerationUnit().getContractEnergy()[h]
																						+ contracts.get(contracts.size()-1).getHourlyContractPower();
							}
						}
												
						// actualización de la energía contratada para el comercializador. A lo que se tiene contratado con otros generadores
						// se le suma lo que se acaba de contratar. 
						orderRetailers.get(ret).getContractEnergy()[h] = orderRetailers.get(ret).getContractEnergy()[h]
								+ contracts.get(contracts.size()-1).getHourlyContractPower();  						 
					}
					else
						break; // si ya cumplió la cuota de contratación entonces no busca establecer nuevos contratos
				}
			}
			
			// organización de los contratos horarios segun la unidad de generación para consolida un contrato
			// por varios períodos
			for (int i = 0; i < contracts.size(); i++)
			{
				// inicialización de la variablle para almacenar los detalles del contrato
				generationContract = new GenerationContract();
				
				// primeros dealles del contrato
				generationContract.setContractSellerId(contracts.get(i).getGenerationUnit().getGenerator().getGeneratorId()); // identificador del vendedor
				generationContract.setContractSeller(contracts.get(i).getGenerationUnit().getGenerator()); 	// vendedor del contrato
				generationContract.setGenerationUnitId(contracts.get(i).getGenerationUnit().getUnitID()); 	// identificador de la unidad
				generationContract.setGenerationUnit(contracts.get(i).getGenerationUnit()); 				// unidad de generación comprometida en el contrato
				generationContract.setContractBuyerId(orderRetailers.get(ret).getRetailerId());				// identificador del comprador
				generationContract.setContractBuyer(orderRetailers.get(ret)); 								// comprador del contrato
				generationContract.setSourceNodeId(contracts.get(i).getGenerationUnit().getNode().getNodeId()); // identificador del nodo de origen
				generationContract.setSourceNode(contracts.get(i).getGenerationUnit().getNode()); 			// nodo de origen
				generationContract.setWithdrawalNodeId(orderRetailers.get(ret).getDemandNode().getNodeId());// identificador del nodo de destino
				generationContract.setWithdrawalNode(orderRetailers.get(ret).getDemandNode()); 				// nodo de destino
				generationContract.setContractStartDate(Global.contractStartDate);							// fecha inicial del contrato
				generationContract.setContractFinalDate(Global.contractFinalDate);							// fecha final del contrato
				
				// definir el tipo del contrato
				randomContractType = random.nextDouble(); // aleatorio entre 0 y 1
				if(randomContractType < 0.5) 
					generationContract.setContractType("PC"); // pague lo contratado
				else 
					generationContract.setContractType("PD"); // pague lo demandado
				
				// inicialización de varibles para almaenar temporalemente los precios y las cantidades estableceidas en cada contrato
				contractPower = new double[24];
				contractPrice = new double[24];
			
				contractPower[contracts.get(i).getContractHour()] = contracts.get(i).getHourlyContractPower();
				contractPrice[contracts.get(i).getContractHour()] = contracts.get(i).getHourlyContractPrice();
				
				// los vectores de precios y cantidades se complementan con los datos de oferta de precio y cantidad de los demás
				// contratos horarios establecidos con la misma unidad de generación. 				//
				for (int j = i+1; j < contracts.size(); j++)
				{
					if(contracts.get(i).getGenerationUnit() == contracts.get(j).getGenerationUnit())
					{
						// actualización de precio y cantidades en las horas correspondientes
						contractPower[contracts.get(j).getContractHour()] = contracts.get(j).getHourlyContractPower();
						contractPrice[contracts.get(j).getContractHour()] = contracts.get(j).getHourlyContractPrice();
						
						// una vez analizado el contrato horario se elimina. 
						contracts.remove(j);
						j = j-1;
					}
				}
				
				// consolidacíon del contrato agregando los vectores de precios y cantidades
				generationContract.setContractPower(contractPower);		// energía contratada								
				generationContract.setContractPrice(contractPrice);		// precio del contrato	
				
				System.out.println("Contrato entre: \t" + generationContract.getContractSeller().getGeneratorCod() + "\t y \t" + generationContract.getContractBuyer().getRetailerCod());
				// aguegar el contrato a la lista de contratos del comercializador
				//retContracts.add(generationContract);
				
				// escribir cada contrato consolidado en un archivo .csv
				Global.rw.writeCsvContracts(this.contractsWriter, generationContract);	
				
			}
			// completar la definición de los retailers con los contratos definidos
			//this.retailers.get(ret).setGenerationContracts(retContracts);
						
		}
		Global.rw.writeCsvGenerationUnits(unitsWriter, this.generationUnits);
		
		// cerrar el archivo en el que se escibieron los contatos
		this.contractsWriter.close();
	}
	
	
	//
	// imprime en consola la información del mercado
	//		
	public void printMarketData(){
		
		// imprimir los datos de los nodos
		System.out.println("\nNodos\n");
		for(int node = 0; node < Global.nNodes; node++)
		{
			nodes.get(node).printNode();
		}
							
		// imprimir los datos de los generadores
		System.out.println("\nGeneradores\n");
		for(int gen = 0; gen < Global.nGencos; gen++)
		{
			generators.get(gen).printGenerator();
		}
		
		// imprimir los datos de las unidades de generación
		System.out.println("\nUnidades de generación\n");
		for(int unit = 0; unit < Global.nUnits; unit++)
		{
			generationUnits.get(unit).printUnit();
		}		
		
		// imprimir los datos de los comercializadores
		System.out.println("\nComercializadores\n");
		for(int ret = 0; ret < Global.nRetailers; ret++)
		{
			retailers.get(ret).printRetailer();
		}
		
		// imprimir los datos de los transmisores
		System.out.println("\nTransmisores\n");
		for(int gridco = 0; gridco < Global.nGridcos; gridco++)
		{
			transmitters.get(gridco).printTransmitter();
		}
		
		// imprimir los datos de las líneas de transmisión
		System.out.println("\nLíneas de transmisión\n");
		for(int line = 0; line < Global.nLines; line++)
		{
			transmissionLines.get(line).printLine();
		}
		
		// imprimir los datos de los contratos de generación
		System.out.println("\nContratos de generación\n");
		for(int contract = 0; contract < this.contracts.size(); contract++)
		{
			this.contracts.get(contract).printContract();
		}
	}
	
	//  despacho ideal: variables para el gráfico de precios spot
	XYSeriesCollection idealDatasetPrices = new XYSeriesCollection();
	JFreeChart idealPriceChart = ChartFactory.createXYLineChart("Ideal dispatch: spot prices","Time [hours]","Price [$/MWh]",idealDatasetPrices,PlotOrientation.VERTICAL,true,true,false);
	Graphics idealPriceGraphic = new Graphics("Ideal dispatch: spot prices", idealDatasetPrices, idealPriceChart, "line", Global.dimPricesChart);
		
	// despacho real: variables para el gráfico de precios nodales
	XYSeriesCollection realDatasetNodal = new XYSeriesCollection();
	JFreeChart realPriceChart = ChartFactory.createXYLineChart("Real dispatch: nodal prices","Time [hours]","Price [$/MWh]",realDatasetNodal,PlotOrientation.VERTICAL,true,true,false);
	Graphics realPriceGraphic = new Graphics("Real dispatch: nodal prices", realDatasetNodal, realPriceChart, "line", Global.dimPricesChart);

	// varaibles para el gráfico de flujos de potencia
	XYSeriesCollection datasetFlows = new XYSeriesCollection();
	JFreeChart flowsChart = ChartFactory.createXYLineChart("Real dispatch: power flows","","",datasetFlows,PlotOrientation.VERTICAL,true,true,false);
	Graphics graphicFlows = new Graphics("Real dispatch: power flows", datasetFlows, flowsChart, "flow", Global.dimFlowChart);
	
	// despacho real: variables para el gráfico de flujos de potencia alternativo
	XYSeriesCollection realDatasetFlows = new XYSeriesCollection();
	JFreeChart realFlowsChart = ChartFactory.createXYLineChart("Real dispatch: power flows","","",realDatasetFlows,PlotOrientation.VERTICAL,true,true,false);
	Graphics realFlowsGraphic = new Graphics("Real dispatch: power flows", realDatasetFlows, realFlowsChart, "flow", Global.dimFlowChart);
	
	// variables para la tabla de generación
	TableFormat tableRenderer = new TableFormat();
	DefaultTableModel tableModel= new DefaultTableModel();
	JTable tableGeneration = new JTable();
	Graphics graphicTable = new Graphics("Generation", tableGeneration, Global.dimGenTable);
	
	// varaibles para la tabla de congestión
	TableFormat congestionTableRenderer = new TableFormat();
	DefaultTableModel congestionTableModel= new DefaultTableModel();
	JTable tableCongestion = new JTable();
	Graphics graphicTableCongestion = new Graphics("Congestión", tableCongestion, Global.dimCongTable);
	
	
	int iteration = 0;								
							
	@Override
	public void run() {

		//
		// entrega de información del mercado al operador
		//
		//this.operator.setGenerationUnits(this.generationUnits);
		//this.operator.setGenerators(this.generators);
		//this.operator.setRetailers(this.retailers);
		//this.operator.setTransmitters(this.transmitters);
		//this.operator.setTransmissionLines(this.transmissionLines);
		
		System.out.println("\n-------------------------\t" + this.iteration + "\t-------------------------" );
		
		this.powerLimit =  Global.factory.productEscalarVector(powerLimit, 1.0);
		
		// para cada iteración en el tiempo de simulación						
		this.operator.management(this.iteration, varI, varIN, varR, rngI, rngIN, rngR, powergen, anglevol, 
				unserved, lowPowerLimit, dailyPowerBid, powerBidPrice, unsDemandCost, powerDemandVector, susceptance,
				powerLimit, angleLb, angleUb, unservedLb, unservedUb, demand, supply, idealWriter, realWriter);	
		
		this.totalSupplyCalculate();
		this.totalDemandCalculate();
		Global.rw.printVector(this.totalSupply);
		Global.rw.printVector(this.totalDemand);
		
		//
		// deapacho ideal: gráfico precios
		//
		idealPriceGraphic.setTitle("Ideal dispatch: spot prices	" + String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime())));
		idealPriceGraphic.pack();
		idealPriceGraphic.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(idealPriceGraphic, 1, 0.0);
		idealPriceGraphic.setVisible(true);
		
		//
		// despacho real: gráfico precios nodales
		//
		realPriceGraphic.setTitle("Real dispatch: nodal prices	" + String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime())));
		realPriceGraphic.pack();
		realPriceGraphic.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(realPriceGraphic, 1, 0.47);
		realPriceGraphic.setVisible(true);
		
		//
		// gráfico flujos
		//
		/*
		XYSeries series = new XYSeries("Flows",false, true);
		series.add(0.0, 0.0);
		series.add(1.0, 1.0);
		this.datasetFlows.removeAllSeries();
		this.datasetFlows.addSeries(series);
		XYPlot plot = this.flowsChart.getXYPlot(); 
		plot.clearAnnotations();
		XYImageAnnotation xyImage = new XYImageAnnotation(0.5,0.5,Global.fondo);
		plot.addAnnotation(xyImage);
		for (int i = 0; i < Global.nLines; i++){
			XYTextAnnotation xy = new XYTextAnnotation(String.valueOf(Global.decimalFormatter.format(this.operator.getRealDispatch().getFlows()[i][0])),Global.cordXYFlows[0][i],Global.cordXYFlows[1][i]);
			xy.setBackgroundPaint(Color.white);
			xy.setFont(Global.labelFont);
			plot.addAnnotation(xy);
		}
		graphicFlows.pack();
		graphicFlows.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(graphicFlows, 0.0, 0.0);
		graphicFlows.setVisible(true);
		*//*
		//
		// despacho real: gráfico flujos alternativo
		//
		this.realDatasetFlows.removeAllSeries();
		XYPlot plotFlows = this.realFlowsChart.getXYPlot(); 
		plotFlows.setBackgroundImage(Global.fondo3);
		plotFlows.setBackgroundImageAlignment(Align.CENTER);
		plotFlows.clearAnnotations();
		for (int i = 0; i < Global.nNodes; i++){
			XYImageAnnotation xyImgNode = new XYImageAnnotation(Global.cordXYnodes[0][i],Global.cordXYnodes[1][i], Global.imgNode);
			plotFlows.addAnnotation(xyImgNode);
		}
		for (int i = 0; i < Global.nLines; i++){
			XYLineAnnotation xyLines = new XYLineAnnotation(Global.cordXYNodesLines[0][i],Global.cordXYNodesLines[1][i],Global.cordXYNodesLines[2][i],Global.cordXYNodesLines[3][i]);
			
			XYTextAnnotation xy = new XYTextAnnotation(String.valueOf(Global.decimalFormatter.format(this.operator.getRealDispatch().getFlows()[i][0])),Global.cordXYFlows[0][i],Global.cordXYFlows[1][i]);
			xy.setBackgroundPaint(Color.white);
			xy.setFont(Global.labelFont);
	
			plotFlows.addAnnotation(xyLines);
			plotFlows.addAnnotation(xy);
		}
		realFlowsGraphic.setTitle("Real dispatch: flows	" + String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime())));
		realFlowsGraphic.pack();
		realFlowsGraphic.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(realFlowsGraphic, 0.20, 0.0);
		realFlowsGraphic.setVisible(true);
		*/
		/*
		//
		// tabla generación
		//
		for (int i = 0; i < Global.nUnits; i++)
		{
			this.dalilyPowerBids(); // organiza las ofertas para cada día
			this.tableModel.setValueAt(Global.decimalFormatter.format(this.dailySupply[i][0]), i, 1);									// llena la columna de disponibilidada
			this.tableModel.setValueAt(Global.decimalFormatter.format(this.operator.getIdealDispatch().getGeneration()[i][0]), i, 2);  	// llena la columna de generación ideal
			this.tableModel.setValueAt(Global.decimalFormatter.format(this.operator.getRealDispatch().getGeneration()[i][0]), i, 3);   	// llena la columna de generación real
			this.tableModel.setValueAt(Global.decimalFormatter.format(this.operator.getIdealDispatch().getGeneration()[i][0] -
															   this.operator.getRealDispatch().getGeneration()[i][0]), i, 4);			// llena la columna de desviación
			
			// ayuda a mirar las desviaciones de la generación real respecto a la generación ideal
			this.tableRenderer.getTableCellRendererComponent(this.tableGeneration, this.tableModel.getValueAt(i, 4), false, false, i, 4);			
		}
		graphicTable.setTitle("Generation	" + String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime())));
		graphicTable.pack();
		graphicTable.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(graphicTable, 1, 0.95);
		graphicTable.setVisible(true);
		*/
		
		//
		// tabla congestión
		//
		Object[] row = new Object[26];
		boolean congestion = false;
		for (int i = 0; i < Global.nLines; i++)
		{
			congestion = false;
			for (int h = 0; h < 24; h++)
			{
				//if (this.operator.getRealDispatch().getRemainderCapacity()[i][h] <= 0.1*this.powerLimit[i])
				if (this.operator.getRealDispatch().getRemainderCapacity()[i][h] == 0.0)
				{
					congestion = true;
					
					row[h+2] = Global.decimalFormatter.format(this.operator.getRealDispatch().getRemainderCapacity()[i][h]);
					//this.congestionTableModel.adsetValueAt(Global.lengthMonth[i], this.congestionTableModel.getRowCount(), 0);
					//this.congestionTableModel.setValueAt(Global.decimalFormatter.format(this.operator.getRealDispatch().getRemainderCapacity()[i][h]), i, h+1);
				}
				/*else 
				{
					this.congestionTableModel.setValueAt("", i, h+1);
				}*/
			}
			if(congestion == true)
			{
				row[0] = Global.linesNames[i];
				row[1] = String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime()));
				congestionTableModel.addRow(row);
			}
			/*else 
			{
				this.congestionTableModel.setValueAt("", i, h+1);
			}*/
		}
		graphicTableCongestion.setTitle("Congestión	" + String.valueOf(Global.dateFormatter.format(DateUtilities.createDate(2010, 1, this.iteration + 1, 1, 0).getTime())));
		graphicTableCongestion.pack();
		graphicTableCongestion.setForeground(Color.black);
		RefineryUtilities.positionFrameOnScreen(graphicTableCongestion, 0, 0.95);
		graphicTableCongestion.setVisible(true);
		
	}
	
	//
	// Menú principal para la ejecución del modelo
	//
	@SuppressWarnings("static-access")
	public void mainMenu() throws IOException, InterruptedException
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
		System.out.println("	5.- Definir contratos de generación.");
		System.out.println("	6.- Visualizar información el mercado.");
		System.out.println("	7.- Ejecutar modelo FTRs.");
		System.out.println("	8.- Generar gráficoss.");
		System.out.println("	9.- Interfaz gráfica.");
		System.out.println("	10.- Organizar los datos de ofertas 2012.");
		System.out.println("	11.- Organizar los datos de demandas nacionales 2012.");
		System.out.println("	12.- Organizar los datos de demandas internacionales 2012.");
		System.out.println("	13.- Organizar los datos de demandas 2012.");
		System.out.println("	14.- Determinar si hay unidades de generación nuevas 2012..");
		System.out.println("	15.- Salir.");
		option = input.nextInt();
		do
		{
			switch(option)
			{
				case 1:
				{
					//Global.organizerPowerBids(); // organiza el archivo de ofertas por central de generación para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 2:
				{
					//Global.organizerNationalDemands(); // organiza el archivo de demandas nacionales para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 3:
				{
					//Global.organizerInternationalDemands(); // organiza el archivo de demandas internacionales para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 4:
				{
					//Global.organizerDemands(); // organiza un archivo de demandas por nodo para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al menú interno
					break;
				}
				case 5:
				{
					//
					// definir un conjunto de contratos de generación
					//
					this.contractsDefine();
					//
					// llamada al manú interno
					//
					this.secondMenu();
					break;
				}				
				case 6:
				{
					//String[] args = new String[]{"F:/FinalModels/Models/ftrModel/ftrModel.rs/",
					//		"C:/RepastSimphony-2.0/eclipse/plugins/repast.simphony.runtime_2.0.1/"};
					//String[] args1 = new String[]{"F:/FinalModels/Models/ftrModel/ftrModel.rs/"};
					
					// lo correcto
					//String[] args1 = new String[]{"D:/Cristian/FinalModels/Models/ftrModel/ftrModel.rs/",
					//"C:/RepastSimphony-2.0/eclipse/plugins/repast.simphony.runtime_2.0.1/"};
					//repast.simphony.runtime.RepastMain.main(args1);	// Code to call the agent system
					
					//
					// establecer la listas de contratos de generación
					//
					this.contractListDefine();
					//
					// establecer la matriz de cargos por uso del sistema de transmisión
					//
					this.setUsageCharges();
					//
					// imprimir la información del mercado
					//
					this.printMarketData();
					//
					// llamada al manú interno
					//
					this.secondMenu(); 		
					break;
				}
				case 7:
				{    
					//
					// definición de las variables para los despachos
					//		
					this.powergenDefine()	;
					this.anglevolDefine()	;
					this.flowsPotDefine()	;
					this.unservedDefine()	;
					this.rngINDefine()		;
					this.rngIDefine()		;
					this.rngRDefine()		;
					this.demandDefine()		;
					this.supplyDefine()		;	
					
					// entrega información al operador
					this.operator.setNodes(this.nodes); 							// establecer la lista de nodos
					this.operator.setGenerators(this.generators);					// establecer la lista de generadores
					this.operator.setGenerationUnits(this.generationUnits);			// establecer la lista de unidades de generación
					this.operator.setRetailers(this.retailers);						// establecer la lista de comercializadores
					this.operator.setGenerationContracts(this.contracts);			// establecer la lista de contratos
					this.operator.setGenerationContractsPC(this.contracts);			// establecer la lista de contratos PC
					this.operator.setGenerationContractsPD(this.contracts);			// establecer la lista de contratos	PD				
					this.operator.setTransmitters(this.transmitters);				// establecer la lista de transmisores
					this.operator.setTransmissionLines(this.transmissionLines);		// establecer la lista de líneas de transmisión
					this.operator.sortContratsByHourlyPrice();						// orden de los contratos PD por precio para cada hora y cada comercializador
					this.operator.generationUnitsMaxEnergySalesPDMWh();				// máxima generación en contratos PD por unidad de generación
					this.operator.retailersMaxEnergyPurchasesPDMWh();				// máxima demanda en contratos PD por comercializador
					this.operator.setReferencePricePositiveReconciliation(this.referencePricePositiveReconciliation);	// establecer precio de referencia reconciliación positiva de plantas térmicas
					this.operator.setUsageCharges(this.usageCharges);				// establecer los cargos por uso del sistema de transmisión nacional
					this.operator.setProportionUsageChargesDemand(Global.proportionUsageChargesDemand); 	// establecer proporción de los cargos por uso liquidados a la demanda
					this.operator.setHistoricalNodalPrices(this.historicalNodalPrices); // inicializar los precios nodales históricos 
					this.operator.setHistoricalEnergyDemand(this.historicalEnergyDemand); // inicializar la lista de demanda de energía histórica
					this.operator.setHistoricalHourlyEnergyDemand(this.historicalHourlyEnergyDemand); // inicializar la lista de demanda de energía histórica por hora
					this.operator.setAuctionIndex(this.auctionIndex); // inicializar el índice de la subasta
					//
					// tabla generación
					//
					Object[][] datos =  new Object[Global.nUnits][Global.columnNames.length];
					this.tableModel.setDataVector(datos, Global.columnNames);
					//for (int i = 0; i < Global.nUnits; i++) {this.tableModel.setValueAt(Global.generationUnitsNames[i], i, 0);}
					for (int i = 0; i < Global.nUnits; i++) {this.tableModel.setValueAt(Global.generationUnitsNames[i], i, 0);}
					this.tableGeneration.setModel(this.tableModel);
					
					// columnas de la tabla
					TableColumnModel m = tableGeneration.getColumnModel();
					
					// formato columna: recurso
					m.getColumn(0).setCellRenderer(this.tableRenderer);
					for(int i = 0; i < Global.nUnits; i++)	{this.tableRenderer.getTableCellRendererComponent(this.tableGeneration, this.tableModel.getValueAt(i, 0), false, false, i, 0);}
					
					// formato columna: desviación
					m.getColumn(4).setCellRenderer(this.tableRenderer);
					
					//
					// tabla congestión
					//
					String[] columnNamesCongestion = {"LÍNEA","FECHA","H1","H2","H3","H4","H5","H6","H7","H8","H9","H10","H11","H12","H13","H14","H15","H16","H17","H18","H19","H20","H21","H22","H23","H24"};
					Object[][] datosCongestion =  new Object[0][columnNamesCongestion.length];
					this.congestionTableModel.setDataVector(datosCongestion, columnNamesCongestion);
					//for (int i = 0; i < Global.nLines; i++) {this.congestionTableModel.setValueAt(Global.linesNames[i], i, 0);}
					this.tableCongestion.setModel(this.congestionTableModel);
					
					// Para cada iteración en el tiempo de simulación
					for (this.iteration = 0; this.iteration < Global.horizon; this.iteration++){
						
						// thread para ejecutar los despachos cada día
						Thread t = new Thread(this);
						t.start();
						try 
						{
							t.join();
							
						} catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
						
						// despacho ideal: thread para graficar los precios
						PlotSpotPrices idealPlotPrices = new PlotSpotPrices(this.operator.getIdealDispatch(), this.idealDatasetPrices);
						Thread idealPlotSpot = new Thread(idealPlotPrices);
						idealPlotSpot.start();
						
						// despacho real: thread para graficar los precios nodales
						PlotNodalPrices realPlotPrices = new PlotNodalPrices(this.operator.getRealDispatch(), this.realDatasetNodal);
						Thread realPlotNodal = new Thread(realPlotPrices);
						realPlotNodal.start();
						try 
						{
							idealPlotSpot.join();
							realPlotNodal.join();
							
							if(MathFuns.IsIn(iteration,Global.ftrAuctionDate))
							{
							idealPlotSpot.sleep(5*100);
							realPlotNodal.sleep(5*100);
							}
							else
							{
								idealPlotSpot.sleep(8*10);
								realPlotNodal.sleep(8*10);
							}
							
						} catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
						
						// hace que la lectura de datos del thread del la gráfica, espere hasta que se haya llenado la información
						//Thread.sleep(5*100);
					}
					Global.unSettlementRetailersWriter.close();
					Global.unSettlementUnitsWriter.close();
					Global.unSettlementTransmittersWriter.close();
					Global.resultsWriter.close();
					
					Global.unRetailersContractsEnergyPurchasesPCMWhWriter.close();
					Global.unRetailersContractsEnergyPurchasesPCCOPWriter.close();
					Global.unRetailersContractsEnergyPurchasesPDMWhWriter.close();
					Global.unRetailersContractsEnergyPurchasesPDCOPWriter.close();
					Global.unRetailersPoolEnergyPurchasesMWhWriter.close();
					Global.unRetailersPoolEnergyPurchasesCOPWriter.close();
					Global.unRetailersPoolEnergySalesMWhWriter.close();
					Global.unRetailersPoolEnergySalesCOPWriter.close();
					Global.unRetailersConstraintsCOPPWriter.close();
					Global.unRetailersSettlementUsageChargesCOPWriter.close();
					Global.unRetailersSettlementEnergyMarketCOPWriter.close();
					
					Global.unGenerationUnitsContractsEnergySalesPCMWhWriter.close();
					Global.unGenerationUnitsContractsEnergySalesPCCOPWriter.close();
					Global.unGenerationUnitsContractsEnergySalesPDMWhWriter.close();
					Global.unGenerationUnitsContractsEnergySalesPDCOPWriter.close();
					Global.unGenerationUnitsPoolEnergyPurchasesMWhWriter.close();
					Global.unGenerationUnitsPoolEnergyPurchasesCOPWriter.close();
					Global.unGenerationUnitsPoolEnergySalesMWhWriter.close();
					Global.unGenerationUnitsPoolEnergySalesCOPWriter.close();
					Global.unGenerationUnitsPositiveReconciliationMWhWriter.close();
					Global.unGenerationUnitsPositiveReconciliationCOPWriter.close();
					Global.unGenerationUnitsNegativeReconciliationMWhWriter.close();
					Global.unGenerationUnitsNegativeReconciliationCOPWriter.close();
					Global.unGenerationUnitsSettlementUsageChargesCOPWriter.close();
					Global.unGenerationUnitsSettlementEnergyMarketCOPWriter.close();
					
					Global.unTransmittersSettlementUsageChargesCOPWriter.close();
					
					Global.nodRetailersContractsEnergyPurchasesPCMWhWriter.close();
					Global.nodRetailersContractsEnergyPurchasesPCCOPWriter.close();
					Global.nodRetailersContractsEnergyPurchasesPDMWhWriter.close();
					Global.nodRetailersContractsEnergyPurchasesPDCOPWriter.close();
					Global.nodRetailersPoolEnergyPurchasesMWhWriter.close();
					Global.nodRetailersPoolEnergyPurchasesCOPWriter.close();
					Global.nodRetailersPoolEnergySalesMWhWriter.close();
					Global.nodRetailersPoolEnergySalesCOPWriter.close();
					Global.nodRetailersSettlementCongestionRentsCOPWriter.close();
					Global.nodRetailersSettlementComplementaryChargesCOPWriter.close();
					Global.nodRetailersSettlementUsageChargesCOPWriter.close();
					Global.nodRetailersSettlementEnergyMarketCOPWriter.close();
					
					Global.nodGenerationUnitsContractsEnergySalesPCMWhWriter.close();
					Global.nodGenerationUnitsContractsEnergySalesPCCOPWriter.close();
					Global.nodGenerationUnitsContractsEnergySalesPDMWhWriter.close();
					Global.nodGenerationUnitsContractsEnergySalesPDCOPWriter.close();
					Global.nodGenerationUnitsPoolEnergyPurchasesMWhWriter.close();
					Global.nodGenerationUnitsPoolEnergyPurchasesCOPWriter.close();
					Global.nodGenerationUnitsPoolEnergySalesMWhWriter.close();
					Global.nodGenerationUnitsPoolEnergySalesCOPWriter.close();
					Global.nodGenerationUnitsSettlementEnergyMarketCOPWriter.close();
										
					Global.nodTransmittersSettlementCongestionRentsCOPWriter.close();
					Global.nodTransmittersSettlementComplementaryChargesCOPWriter.close();
					Global.nodTransmittersSettlementUsageChargesCOPWriter.close();
					
					Global.ftrRetailersContractsEnergyPurchasesPCMWhWriter.close();
					Global.ftrRetailersContractsEnergyPurchasesPCCOPWriter.close();
					Global.ftrRetailersContractsEnergyPurchasesPDMWhWriter.close();
					Global.ftrRetailersContractsEnergyPurchasesPDCOPWriter.close();
					Global.ftrRetailersPoolEnergyPurchasesMWhWriter.close();
					Global.ftrRetailersPoolEnergyPurchasesCOPWriter.close();
					Global.ftrRetailersPoolEnergySalesMWhWriter.close();
					Global.ftrRetailersPoolEnergySalesCOPWriter.close();
					Global.ftrRetailersSettlementCongestionRentsCOPWriter.close();
					Global.ftrRetailersSettlementComplementaryChargesCOPWriter.close();
					Global.ftrRetailersSettlementUsageChargesCOPWriter.close();
					Global.ftrRetailersSettlementEnergyMarketCOPWriter.close();
					
					Global.ftrGenerationUnitsContractsEnergySalesPCMWhWriter.close();
					Global.ftrGenerationUnitsContractsEnergySalesPCCOPWriter.close();
					Global.ftrGenerationUnitsContractsEnergySalesPDMWhWriter.close();
					Global.ftrGenerationUnitsContractsEnergySalesPDCOPWriter.close();
					Global.ftrGenerationUnitsPoolEnergyPurchasesMWhWriter.close();
					Global.ftrGenerationUnitsPoolEnergyPurchasesCOPWriter.close();
					Global.ftrGenerationUnitsPoolEnergySalesMWhWriter.close();
					Global.ftrGenerationUnitsPoolEnergySalesCOPWriter.close();
					Global.ftrGenerationUnitsSettlementEnergyMarketCOPWriter.close();
					
					Global.ftrTransmittersSettlementCongestionRentsCOPWriter.close();
					Global.ftrTransmittersSettlementComplementaryChargesCOPWriter.close();
					Global.ftrTransmittersSettlementUsageChargesCOPWriter.close();
					
					Global.spotPricesWriter.close();
					Global.nodalPricesWriter.close();
					Global.nodCongestionRentsWriter.close();
					Global.ftrCongestionRentsWriter.close();
					Global.nodCongestionRentsFundWriter.close();
					Global.ftrCongestionRentsFundWriter.close();
					Global.auctionFeaturesWriter.close();
					Global.ftrsFeaturesWriter.close();
					Global.organizedFtrsFeaturesWriter.close();
					
					Global.newLinesWriter.close();
					
					//Runtime.getRuntime().exec(Global.directory + "results/plots.m"); 
					
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 8:
				{    
					//Graphics stateChart = new Graphics("Prueba"); // generación de gráficos
					//stateChart.setVisible(true);
					this.secondMenu(); // llamada al manú interno
					break;
				} 
				case 9:
				{    
					Interfaz interfaz = new Interfaz("Prueba"); // interfaz gráfica
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 10:
				{
					Global.organizerPowerBids2012(); // organiza el archivo de ofertas por central de generación para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 11:
				{
					Global.organizerNationalDemands2012(); // organiza el archivo de demandas nacionales para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 12:
				{
					Global.organizerInternationalDemands2012(); // organiza el archivo de demandas internacionales para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 13:
				{
					Global.organizerDemands2012(); // organiza un archivo de demandas por nodo para todo el horizonte de tiempo considerado
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 14:
				{
					//Global.rw.readCsvGenNames(CsvReader reader, Vector<String> names, int year, int month, int day); // determinar los diferentes nombres de las unidades de generación
					this.secondMenu(); // llamada al manú interno
					break;
				}
				case 15:
				{
					System.exit(0);
					break;
				}
				default :
					System.out.println("Opción incorrecta");
					this.secondMenu();
					break;			    	
			}
				
		} while (option != 10);
		input.close();
	}
	
	public void secondMenu() throws IOException, InterruptedException
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
					this.mainMenu(); // llamada al manú interno
					break;
				}  			           
				case 2:
				{
					System.exit(0); // salir
					break;
				}
				default : 
					System.out.println("Opción incorrecta");
					this.secondMenu();
					break;			    	
			}
				
		} while (option != 2);	
		
		input.close();
	}

	/**
	 * @param argsftr.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public synchronized static void main(String[] args) throws IOException, InterruptedException {
		
		FtrMain ftr = new FtrMain();
		
		//
		// definición de los elementos del mercado
		//
		ftr.marketElements();
		ftr.marketDefine();
				
		//
		// menu de ejecución
		//
		ftr.mainMenu();	
	}
}



/*SwingUtilities.invokeLater(new Runnable()
{
     @Override public void run() {
    	 label.setText(new java.util.Date().toString());
     }
});
label.repaint();
panelCenter.add(label, BorderLayout.CENTER);
panelCenter.repaint();
interfaz.add(panelCenter, BorderLayout.CENTER);
interfaz.repaint();

//panelCenter.add(label, BorderLayout.CENTER);
//panelCenter.validate();
//interfaz.add(panelCenter, BorderLayout.CENTER);

System.out.println("Debería graficar:   " + (iteration + 1));	

// serie a graficar
XYSeries series = new XYSeries(iteration+1, false, true);
for(int h = 0; h<24; h++)
{
series.add(DateUtilities.createDate(2010, 1, 1, h+1, 0).getTime(),operator.idealDispatch.getNodalPrices()[0][h]);
//series.add(h,cplex.getDuals(rng[0])[h]);
}

// definición del conjunto de datos
XYSeriesCollection dataset = new XYSeriesCollection();
dataset.addSeries(series);
JFreeChart stepChart = ChartFactory.createXYStepChart("Spot price","Time [hours]","Price [$/MWh]",dataset,PlotOrientation.VERTICAL,true,true,false); 

// panel para gráficas
ChartPanel chartPanel = new ChartPanel(stepChart);
chartPanel.repaint();
chartPanel.setLayout(new BorderLayout());
chartPanel.setBackground(Color.red);
chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));         

// panel para agregar las gráficas dentro de la interfaz
panelCenter.setLayout(new BorderLayout());
panelCenter.add(chartPanel, BorderLayout.CENTER);
panelCenter.repaint();
interfaz.add(panelCenter, BorderLayout.CENTER);

// panel este
JPanel east = new JPanel();
east.setLayout(new BorderLayout());
JLabel label = new JLabel(String.valueOf(iteration+1));
east.add(label, BorderLayout.CENTER);
interfaz.add(east, BorderLayout.EAST);

SwingUtilities.updateComponentTreeUI(interfaz);
interfaz.validate();


System.out.println("Debería graficar:   " + (iteration + 1));	
*/	   

/*
Generator genco1 = new Generator("GENCO1");
Generator genco2 = new Generator("GENCO2");
Generator genco3 = new Generator("GENCO3");
//Generator gen4 = new Generator("GENCO4");
//Generator gen5 = new Generator("GENCO5");
//Generator gen6 = new Generator("GENCO6");
//Generator gen7 = new Generator("GENCO7");
//Generator gen8 = new Generator("GENCO8");

// Comercializadores
Retailer retailco1 = new Retailer("RETAILER1",1);
Retailer retailco2 = new Retailer("RETAILER2",2);
Retailer retailco3 = new Retailer("RETAILER3",3);
//Retailer ret4 = new Retailer("RETAILER4");
//Retailer ret5 = new Retailer("RETAILER5");
//Retailer ret6 = new Retailer("RETAILER6");
//Retailer ret7 = new Retailer("RETAILER7");
//Retailer ret8 = new Retailer("RETAILER8");
	
// Transmisores
Transmitter gridco1 = new Transmitter("GRIDCO1");
Transmitter gridco2 = new Transmitter("GRIDCO2");
Transmitter gridco3 = new Transmitter("GRIDCO3");
//Transmitter trans4 = new Transmitter("GRIDCO4");
//Transmitter trans5 = new Transmitter("GRIDCO5");
//Transmitter trans6 = new Transmitter("GRIDCO6");
//Transmitter trans7 = new Transmitter("GRIDCO7");
//Transmitter trans8 = new Transmitter("GRIDCO8");
/*
// Unidades de generación
// Constructor con id, nombre, nodo, capacidad efectiva, costo de generación
GenerationUnit unit1 = new GenerationUnit(1,"U1",1,140,7.5);
GenerationUnit unit2 = new GenerationUnit(2,"U2",1,285,6.0);
GenerationUnit unit3 = new GenerationUnit(3,"U3",2,90,14.0);
GenerationUnit unit4 = new GenerationUnit(4,"U4",3,85,10.0);

// Líneas de transmisión
// Constructor con identificador, propietario, nodo origen, nodo destino, 
// susceptancia y límite de flujo de potencia
TransmissionLine line1 = new TransmissionLine(1,gridco1,1,2,5,126);	// nodo 1 - nodo 2
TransmissionLine line2 = new TransmissionLine(2,gridco2,1,3,5,250);	// nodo 1 - nodo 3
TransmissionLine line3 = new TransmissionLine(3,gridco3,2,3,10,130); // nodo 2 - nodo 3

// generation contracts
GenerationContract contractG2U2R1 = new GenerationContract("C1",genco2,unit2,retailco1,
		new double[]{50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0,50.0},
		new double[]{4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,},
		1,1,1,365);
GenerationContract contractG2U2R3 = new GenerationContract("C2",genco2,unit2,retailco3,
		new double[]{160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0,160.0},
		new double[]{5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0},
		1,3,1,365);
GenerationContract contractG2U2R2 = new GenerationContract("C3",genco2,unit2,retailco2,
		new double[]{60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0,60.0},
		new double[]{5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5,5.5},
		1,2,1,365);
GenerationContract contractG1U1R3 = new GenerationContract("C4",genco1,unit1,retailco3,
		new double[]{120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0,120.0},
		new double[]{6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5,6.5},
		1,3,1,365);
		*/






/*
public void contractsDefine() throws NumberFormatException, IOException{

//
// archivo para guardar la información de los contratos
//


Random random = new Random();
int randomGenSelection;

double randomGeneratorPrice;

double randomRetailerPrice;

List<PowerBid> powerBids;
List<GenerationContract> contracts;
GenerationContract generationContract; 

for(int ret = 0; ret < this.retailers.size(); ret++)
{
	// lista de contratos horarios
	contracts = new ArrayList<GenerationContract>();
	
	for(int h = 0; h < 24; h++)
	{
		// lista de ofertas de precios y cantidad para contratar
		powerBids = new ArrayList<PowerBid>();
		
		//while (this.retailers.get(ret).getContractEnergy()[h] 
		//		<= this.retailers.get(ret).getContractsProportion()[h] * this.retailers.get(ret).getReferenceDemand()[h])
		//{
			// aleatorio para definir con que generador se negocia el contrato
			//randomGenSelection = random.nextInt()*(Global.nUnits);
			
			// for para rrecorrer las undades de generación
			for(int unit = 0; unit < this.generationUnits.size(); unit++)
			{
				// if para determinar la unidad de generación con la que posiblemente se establezca un contrato de generación
				//if(randomGenSelection == this.generationUnits.get(unit).getUnitID())
				//{
					// si la unidad de generación aun tiene capacidad disponible
					if(this.generationUnits.get(unit).getContractEnergy()[h] 
							<= this.generationUnits.get(unit).getContractsProportion()[h] * this.generationUnits.get(unit).getEffectiveCapacity())
					{
						// la unidad de generación define un precio para el contrato
						randomGeneratorPrice = 
								random.nextDouble()
								*(((1 + Global.contractPriceRange) * this.generationUnits.get(unit).getReferenceContractsPrice()[h]) + 1)
								+ (1 - Global.contractPriceRange) * this.generationUnits.get(unit).getReferenceContractsPrice()[h];
						
						// la unidad de generación define un precio para el contrato
						
						powerBids.add(new PowerBid(this.generationUnits.get(unit),
													randomGeneratorPrice, 
													this.generationUnits.get(unit).getContractsProportion()[h] * this.generationUnits.get(unit).getEffectiveCapacity()
														- this.generationUnits.get(unit).getContractEnergy()[h]));
						/*
						randomRetailerPrice  = 
								random.nextDouble()
								* (((1 + Global.contractPriceRange) * this.retailers.get(ret).getReferenceContractsPrice()[h]) + 1)
								+ (1 - Global.contractPriceRange) * this.retailers.get(ret).getReferenceContractsPrice()[h];
								
						if(randomRetailerPrice <= randomGeneratorPrice)
						{
							
						}
						*//*
					}					
				//}
			}
			// ordenar las ofertas de menor a mayor precio
			Collections.sort(powerBids);
			
			// formación de contratos
			for (int bid = 0; bid < powerBids.size(); bid++)
			{
				if (this.retailers.get(ret).getContractEnergy()[h] 
						<= this.retailers.get(ret).getContractsProportion()[h] * this.retailers.get(ret).getReferenceDemand()[h])
				{
					contracts.add(new GenerationContract(powerBids.get(bid).getGenerationUnit(),
							this.retailers.get(ret),
							Math.min(powerBids.get(bid).getHourlyBidPower(),
									this.retailers.get(ret).getContractsProportion()[h] * this.retailers.get(ret).getReferenceDemand()[h]
									- this.retailers.get(ret).getContractEnergy()[h]),
							powerBids.get(bid).getBidPrice(),
							h));
					
					// actualización de la energía contratada para el generador
					powerBids.get(bid).getGenerationUnit().getContractEnergy()[h] = powerBids.get(bid).getGenerationUnit().getContractEnergy()[h]
							- contracts.get(contracts.size()-1).getHourlyContractPower(); 
							
							//Math.min(powerBids.get(bid).getHourlyBidPower(),
								//	   this.retailers.get(ret).getContractsProportion()[h] * this.retailers.get(ret).getReferenceDemand()[h]
									//   - this.retailers.get(ret).getContractEnergy()[h]);
							
					// actualización d ela energía contratada para el comercializador
					this.retailers.get(ret).getContractEnergy()[h] = this.retailers.get(ret).getContractEnergy()[h]
							- contracts.get(contracts.size()-1).getHourlyContractPower();  
							//- Math.min(powerBids.get(bid).getHourlyBidPower(),
								//	   this.retailers.get(ret).getContractsProportion()[h] * this.retailers.get(ret).getReferenceDemand()[h]
								//	   - this.retailers.get(ret).getContractEnergy()[h]);
							 
				}
				else
					break;
			}
			
			
		//}
		
		
	}
	
	int ncontracts = contracts.size();
	for (int i = 0; i < ncontracts; i++)
	{
		
		generationContract = new GenerationContract();
		generationContract.setContractSeller(contracts.get(i).getGenerationUnit().getGenerator()); 	// vendedor del contrato
		generationContract.setGenerationUnit(contracts.get(i).getGenerationUnit()); 				// unidad de generación comprometida en el contrato
		generationContract.setContractBuyer(this.retailers.get(ret)); 								// comprador del contrato
		generationContract.setSourceNode(contracts.get(i).getGenerationUnit().getNode()); 			// nodo de origen
		generationContract.setWithdrawalNode(this.retailers.get(ret).getDemandNode()); 				// nodo de destino
		generationContract.setContractStartDate(Global.contractStartDate);							// fecha inicial del contrato
		generationContract.setContractFinalDate(Global.contractFinalDate);							// fecha final del contrato
		
		double[] contractPower = new double[24];
		double[] contractPrice = new double[24];
		
		contractPower[contracts.get(i).getContractHour()] = contracts.get(i).getHourlyContractPower();
		contractPrice[contracts.get(i).getContractHour()] = contracts.get(i).getHourlyContractPrice();
		
		
		for (int j = 1; j < ncontracts; j++)
		{
			if(contracts.get(i).getGenerationUnit() == contracts.get(j).getGenerationUnit())
			{
				contractPower[contracts.get(j).getContractHour()] = contracts.get(j).getHourlyContractPower();
				contractPrice[contracts.get(j).getContractHour()] = contracts.get(j).getHourlyContractPrice();
				
				contracts.remove(j);
				ncontracts = ncontracts - 1;
			}
		}
		
		generationContract.setContractPower(contractPower);		// energía contratada								
		generationContract.setContractPrice(contractPrice);		// precio del contrato	
		
		System.out.println("Retailer:\t"+ this.retailers.get(ret).getRetailerName());
		generationContract.printContract();
		
		Global.rw.writeCsvContracts(this.contractsWriter, generationContract);
		
		//this.retailers.get(ret).getGenerationContracts().add(generationContract);
	}
				
}
this.contractsWriter.close();
}
*/