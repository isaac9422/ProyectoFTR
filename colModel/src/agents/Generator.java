package agents;

import java.util.ArrayList;
import java.util.List;

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
	
	// constructor vac�o
	public Generator(){}
	
	// constructor con nombre
	public Generator(int genId, String codXM, String generatorName, int position){
		this.genId = genId;
		this.codXM = codXM;
		this.generatorName = generatorName;
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
	
	// Set method
	public void gsetGeneratorId(int genId){this.genId = genId;} // establecer el identificador del generador
	public void setGeneratorName(String generatorName){this.generatorName = generatorName;} // establecer el nombre del generador
	public void setGeneratorCod(String codXM){this.codXM = codXM;} // establecer el c�digo del generador
	public void setGenerationUnits(List<GenerationUnit> generationUnits){this.generationUnits = generationUnits;} // establecer la lista de unidades de generaci�n
	public void setGenerationContracts(List<GenerationContract> generationContracts){this.generationContracts = generationContracts;} // establecer la lista de contratos de generaci�n
	public void setPowerBids(PowerBid[] powerBids){this.powerBids = powerBids;} // establecer la lista de ofertas de generaci�n
	
	// imprimir las caracter�sticas de cada generador
	public void printGenerator(){
		System.out.print(this.genId + "\t" + this.codXM + "\t" + this.generatorName + "\t" + this.position + "\t");
		for(int i = 0; i < this.generationUnits.size(); i++)
		{
			System.out.print(this.generationUnits.get(i).getUnitName() + "\t");
		}
		System.out.println();
	}
}
