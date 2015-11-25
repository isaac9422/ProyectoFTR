package market;

public class PowerDemand {
	
	// Atributos
	private String node; 			// nodo de demanda
	private double[] powerDemand = new double[24];	// demanda horaria

	// Constructor vacío
	public PowerDemand(){}
	
	// Constructor con nodo y demanda
	public PowerDemand(String node, double[] powerDemand){
		this.node = node;
		this.powerDemand = powerDemand; 
	}
	
	// Get methods
	public String getNode(){return this.node;} // obtener el nombre del nodo
	public double[] getPowerDemand(){return this.powerDemand;} // obtener la demanda de energía
	public double getPowerDemand(int h){return this.powerDemand[h];} // obtener la demanda de energía para la hora h
	
	// Set methods
	public void setNode(String node){this.node = node;} // establecer el nombre del nodo
	public void setPowerDemand(double[] powerDemand){this.powerDemand = powerDemand;} // establecer la demanda de energía
	public void setPowerDemand(int h, double powerDemand){this.powerDemand[h] = powerDemand;} // obtener la demanda de energía para la hora h
	
	// Imprimir demanda
	public void printPowerDemand(){
		System.out.print(this.node +"\t");
		for(int i = 0; i < 24; i++){
			System.out.print(this.powerDemand[i]+"\t");
		}
		System.out.print("\n");
	}
	
}
