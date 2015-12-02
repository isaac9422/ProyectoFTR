package agents;

import java.util.ArrayList;
import java.util.List;

import utilities.Global;
import utilities.MathFuns;

import market.Ftr;
import market.GenerationContract;
import market.GenerationUnit;
import market.PowerBid;

public class Generator {
	
	// datos generales
	private int genId; 				// identificador del generador
	private String codXM;			// c�digo generador en XM
	private String generatorName; 	// nombre del generador
	private int position; 			// posici�n del generador en la base de datos
	private List<GenerationUnit> generationUnits = new ArrayList<GenerationUnit>(); // lista de unidades de generaci�n
		
	// datos mercado de energ�a
	private PowerBid[] powerBids; // lista de ofertas de potencias
	private List<GenerationContract> generationContracts; // lista de contratos de generaci�n
	
	// mercado nodal con ftrs
	private double[][] ftrReservePrice; // precio de reserva para la subasta de FTRs
	private Ftr[] ftrHourlyBids; 		// arreglo de ofertas horarias de FTRs, una por cada contrato 
	private double[] ftrHourlyBidPower; // arreglo de ofertas de energ�a en cada FTR
	private double ftrRiskAversion; 	// proporci�n de aversi�n al riesgo
	
	// constructor vac�o
	public Generator(){}
	
	// constructor con nombre
	public Generator(int genId, String codXM, String generatorName, double ftrRiskAversion, int position){
		this.genId = genId;
		this.codXM = codXM;
		this.generatorName = generatorName;
		this.ftrRiskAversion = ftrRiskAversion;
		this.position = position; 
	}
	
	// Constructor con nombre
	public Generator(String generatorName){
		this.generatorName = generatorName;
	}
	
	// Constructor con nombre, unidades y contratos
	public Generator(String generatorName, List<GenerationUnit> generationUnits, ArrayList<GenerationContract> generationContracts){
		this.generatorName = generatorName;
		this.generationUnits = generationUnits;
		this.generationContracts = generationContracts;
	}
	
	// Get methods
	public int getGeneratorId(){return this.genId;} // obtener el identificador del generador
	public String getGeneratorName(){return this.generatorName;} // obtener el nombre del generador
	public String getGeneratorCod(){return this.codXM;} // obtener el c�digo del generador
	public List<GenerationUnit> getGenerationUnits(){return this.generationUnits;} // obtener la lista de unidades de generaci�n
	public List<GenerationContract> getGenerationContracts(){return this.generationContracts;} // obtener la lista de contratos de generaci�n
	public PowerBid[] getPowerBids(){return this.powerBids;} // obtener la lista de ofertas de generaci�n
	public double[][] getFtrReservePrice(){return this.ftrReservePrice;} // obtener los precios de reserva por contrato y por hora para la subasta de FTRs
	public double getFtrRiskAversion(){return this.ftrRiskAversion;} // obtener la proporci�n de aversi�n al riesgo en el mercado de FTRs
	
	// Set method
	public void setGeneratorId(int genId){this.genId = genId;} // establecer el identificador del generador
	public void setGeneratorName(String generatorName){this.generatorName = generatorName;} // establecer el nombre del generador
	public void setGeneratorCod(String codXM){this.codXM = codXM;} // establecer el c�digo del generador
	public void setGenerationUnits(List<GenerationUnit> generationUnits){this.generationUnits = generationUnits;} // establecer la lista de unidades de generaci�n
	public void setGenerationContracts(List<GenerationContract> generationContracts){this.generationContracts = generationContracts;} // establecer la lista de contratos de generaci�n
	public void setPowerBids(PowerBid[] powerBids){this.powerBids = powerBids;} // establecer la lista de ofertas de generaci�n
	public void setFtrReservePrice(double[][] ftrReservePrice){this.ftrReservePrice = ftrReservePrice;} // establecer los precios de reserva por contrato y por hora para la subasta de FTRs
	public void setFtrRiskAversion(double ftrRiskAversion){this.ftrRiskAversion = ftrRiskAversion;} // establecer la proporci�n de aversi�n al riesgo en el mercado de FTRs
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// funci�n para calcular el precio de reserva del generador
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	public double[][] calculateFtrReservePrice(int iteration, Operator operator){
		// variables auxiliares
		double[] nodalPriceDifferences; // diferencias de precios nodales
		double[][] ftrReservePrice; // precio de reserva
		try{
			// si el generador tiene contratos
			if(this.getGenerationContracts().isEmpty() != true)
			{
				ftrReservePrice = new double[this.getGenerationContracts().size()][24];
				for(int contract = 0; contract < this.getGenerationContracts().size(); contract++)
				{
					for(int hour = 0; hour < 24; hour++)
					{
						// inicializaci�n del vector de diferencias de precios nodales
						nodalPriceDifferences = new double[Global.nlags];
						for(int i = 0; i < Global.nlags; i++)
						{
							// c�lculo del vector de diferencias de precios nodales
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
            System.out.println("generator: calculateFtrReservePrice ->"+e);
        }
		return this.ftrReservePrice;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// funci�n para construir una oferta horaria por FTRs en la subasta de FTRs
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------
	public List<Ftr> ftrSetHourlyBids(int iteration, int h, int round, Operator operator){
		// variables auxiliares
		List<Ftr> ftrHourlyBids = new ArrayList<Ftr>(); 			// lista de ofertas horarias por FTRs
		List<Double> ftrHourlyBidPower = new ArrayList<Double>(); 	// lista de ofertas horarias de energ�a en los FTRs
		try{
			int ftrIndex = 0; 
			for(int contract = 0; contract < this.getGenerationContracts().size(); contract++)
			{
				// solo se oferta por los FTRs que tengan un valor mayor que cero
				if(operator.getFtrProductPrice()[h] > Global.ftrOperatorMinimumPrice)
				{
					// s�lo se oferta en los contratos que tenga una generaci�n comprometida mayor que cero
					if(this.getGenerationContracts().get(contract).getContractPower()[h] > 0.0)
					{
						if(operator.getFtrProductPrice()[h] <= this.getFtrReservePrice()[contract][h])
						{
							// c�lculo de la energ�a en el FTr segun la proporci�n de aversi�n al riesgo
							ftrHourlyBidPower.add(ftrIndex, this.getFtrRiskAversion() * this.getGenerationContracts().get(contract).getContractPower()[h]);
							
							// construcci�n de un FTR horario
							ftrHourlyBids.add(	new Ftr(this.getGeneratorName(),
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
	        System.out.println("generador: ftrSetHourlyBids ->"+e);
	    }
		return ftrHourlyBids;
	}
		
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// funci�n para imprimir las caracter�sticas de cada generador
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void printGenerator(){
		System.out.print(this.genId + "\t" + this.codXM + "\t" + this.generatorName + "\t" + this.position + "\t");
		for(int i = 0; i < this.generationUnits.size(); i++)
		{
			System.out.print(this.generationUnits.get(i).getUnitName() + "\t");
		}
		System.out.println();
	}
}
