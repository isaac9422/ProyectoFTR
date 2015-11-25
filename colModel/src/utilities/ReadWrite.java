package utilities;

import agents.Generator;
import agents.Operator;
import agents.Retailer;
import agents.Transmitter;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import market.Dispatch;
import market.GenerationContract;
import market.GenerationUnit;
import market.Node;
import market.PowerBid;
import market.PowerDemand;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


public class ReadWrite {
  
	WritableCellFormat numberFormat = new WritableCellFormat(new NumberFormat("#0.00"));
	
    //
    // Función para imprimir una matriz tipo double en consola
    //
    public void printDoubleMatrix(double [][] data)
    {
    	for(int column=0;column < data[0].length;column++){
        		System.out.print(data[0][column] + "\t   ");	
            }
    	System.out.println();
    	for(int row=1;row < data.length;row++){
        	for(int column=0;column < data[0].length;column++){
        		System.out.print(data[row][column] + "\t\t");	
            }
        System.out.println();
        }
    }
    
    //
    // Función para imprimir una matriz tipo string en consola
    //
    public void printStringMatrix(String [][] data)
    {
    	for(int column=0;column < data[0].length;column++){
        		System.out.print(data[0][column] + "\t   ");	
            }
    	System.out.println();
    	for(int row=1;row < data.length;row++){
        	for(int column=0;column < data[0].length;column++){
        		System.out.print(data[row][column] + "\t\t");	
            }
        System.out.println();
        }
    }
    
    //
    // Función para imprimir un vector tipo string en consola
    //
    public void printStringVector(String [] data)
    {
    	for(int column=0;column < data.length;column++){
        		System.out.print(data[column] + "\t   ");	
            }
    	System.out.println();
    }
    
    //
    // Función para imprimir un vector tipo double en consola
    //
    public void printVector(double [] vector)
    {
    	for(int column=0;column < vector.length;column++){
        		System.out.print(vector[column] + "\t   ");	
        }
    	System.out.println();
    }
    
    
    //
    // Función para leer datos de un archivo .csv y almacenarlos en una matriz tipo double
    //
    public double[][] readCsv(CsvReader reader, int nRows, int nCols) throws NumberFormatException, IOException
    {
    	double data[][] = new double[nRows][nCols];
    	try 
    	{
    		int row=0;
    		while (reader.readRecord())
    		{
    			for(int column=0;  column < nCols;column++){
		        		data[row][column] = Double.parseDouble(reader.get(column));	
    			}  	
    			row++;
    		}
        }
    	 catch (IOException e) 
    	 {
    		 e.printStackTrace();
    	}
    	
    	reader.close();
    	return data;
    }
    
    //
    // Función para leer datos de un archivo .csv y almacenarlos en una matriz tipo double. Se eliminan los String de los archivos
    //
    public double[][] readCsv(CsvReader reader, int nRows, int nCols, int[] positions) throws NumberFormatException, IOException
    {
    	double data[][] = new double[nRows][nCols-positions.length];
    	try 
    	{
    		int row=0;
    		int column = 0;
    		boolean bool = false;
    		while (reader.readRecord())
    		{
    			for(int j = 0;  j < nCols; j++){
    				for(int i = 0; i < positions.length; i++){
    					if(j == positions[i] ){
    						bool = true;
    		        		break;
    					}
    					else
    					{
    						bool = false;
    					}
    				}
    				if(bool == false){
    					data[row][column] = Double.parseDouble(reader.get(j));
    					column = column + 1;
    				}
    			}
    			column = 0;
    			row++;
    		}
        }
    	 catch (IOException e) 
    	 {
    		 e.printStackTrace();
    	}
    	
    	reader.close();
    	return data;
    }
    
    //
    // Función para escribir resultados en un archivo .csv a partir de una matriz tipo double
    //
    public void writeCsv(CsvWriter writer, double[][] data, int nRows, int nCols) throws NumberFormatException, IOException
    {
    	try 
    	{
	    	for(int row=0; row < nRows; row++)
			{
				for(int column=0;  column < nCols;column++){
					writer.write(String.valueOf(data[row][column]));
				} 
				writer.endRecord();
			}
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    //
    // Función para escribir resultados en un archivo .csv a partir de una matriz tipo String
    //
    public void writeCsv(CsvWriter writer, String[][] data, int nRows, int nCols) throws NumberFormatException, IOException
    {
    	try 
    	{
	    	for(int row=0; row < nRows; row++)
			{
				for(int column=0;  column < nCols;column++){
					writer.write(data[row][column]);
				} 
				writer.endRecord();
			}
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }

    //
    // Función para escribir resultados en un archivo .csv a partir de un vector tipo double
    //
    public void writeCsv(CsvWriter writer, double[] data, int nRows) throws NumberFormatException, IOException
    {
    	try 
    	{
	    	for(int row=0; row < nRows; row++)
			{
				writer.write(String.valueOf(data[row]));	
			}
	    	writer.endRecord();
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
        
    //
    // Función para escribir resultados en un archivo .csv a partir de un vector tipo String
    //
    public void writeCsv(CsvWriter writer, String[] data) throws NumberFormatException, IOException
    {
    	try 
    	{
    		writer.writeRecord(data);	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    //
    // Función para escribir resultados en un archivo .csv a partir de un vector dinámico de strings[]
    //
    public void writeCsv(CsvWriter writer, Vector<String []> vector) throws NumberFormatException, IOException
    {
    	try 
    	{
    		for (int i = 0; i < vector.size(); i++){
    			for (int j = 0; j < vector.get(0).length; j++){
    				writer.write(vector.get(i)[j]);
    			}
    		}
    		// Cambiar de línea
	    	writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    //
    // Función para escribir resultados en un archivo .csv a partir de un vector dinámico de strings
    //
    public void writeCsvStrings(CsvWriter writer, Vector<String> vector) throws NumberFormatException, IOException
    {
    	try 
    	{
    		for (int i = 0; i < vector.size(); i++){
    			writer.write(vector.get(i));
    			
    			// Cambiar de línea
    	    	writer.endRecord();	
    		}    		
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    //
    // Función para escribir resultados en un archivo .csv a partir de un vector dinámico que contiene las ofertas de energía
    //
    public void writeCsvPowerBids(CsvWriter writer, Vector<PowerBid> vector) throws NumberFormatException, IOException
    {
    	try 
    	{
    		for (int i = 0; i < vector.size(); i++){
    			// escribir el nombre del generador
    			writer.write(vector.get(i).getGeneratorName());
    			// escribir el precio de oferta
    			writer.write(String.valueOf(vector.get(i).getBidPrice()));
    			// escribir la disponibilidad declarada para cada hora
    			for (int j = 0; j < 24; j++)
    			{
    				writer.write(String.valueOf(vector.get(i).getBidPowerOne(j)));
    			}
    		}
    		// Cambiar de línea
	    	writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    /* ---------------------------------------------------------------------------------------------------------------
     * Función para escribir resultados del despacho
     * ---------------------------------------------------------------------------------------------------------------*/
    public void writeCsv(int iteration, CsvWriter writer, Dispatch dispatch) throws NumberFormatException, IOException
    {
    	try 
    	{
    		if(iteration == 0)
    		{
    			writer.endRecord();
    			writer.endRecord();
    		}
    		// Escribir la programación de la generación
	    	for(int row=0; row < dispatch.getGeneration().length; row++)
			{
	    		for(int column=0; column < dispatch.getGeneration()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getGeneration()[row][column]));
	    			
	    		}
			}
	    	// Escribir los ángulos de voltaje
	    	for(int row=0; row < dispatch.getVoltageAngles().length; row++)
			{
	    		for(int column=0; column < dispatch.getVoltageAngles()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getVoltageAngles()[row][column]));
	    			
	    		}
			}
	    	
	    	// Escribir la demanda
	    	for(int row=0; row < dispatch.getEnergyDemand().length; row++)
			{
	    		for(int column=0; column < dispatch.getEnergyDemand()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getEnergyDemand()[row][column]));
	    			
	    		}
			}
	    	
	    	// Escribir los precios nodales
	    	for(int row=0; row < dispatch.getNodalPrices().length; row++)
			{
	    		for(int column=0; column < dispatch.getNodalPrices()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getNodalPrices()[row][column]));
	    			
	    		}
			}
	    	
	    	// Escribir los flujos de potencia
	    	for(int row=0; row < dispatch.getFlows().length; row++)
			{
	    		for(int column=0; column < dispatch.getFlows()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getFlows()[row][column]));
	    			
	    		}
			}
	    	
	    	// Escribir la demanda no atendida
	    	for(int row=0; row < dispatch.getUnservedDemand().length; row++)
			{
	    		for(int column=0; column < dispatch.getUnservedDemand()[1].length; column++)
	    		{
	    			writer.write(String.valueOf(dispatch.getUnservedDemand()[row][column]));
	    			
	    		}
			}
	    	
	    	// Escribir el costo del despacho diario
	    	writer.write(String.valueOf(dispatch.getDispatchCost()));
	    	
	    	// Cambiar de línea
	    	writer.endRecord();
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   	 	}	
    }
    
    /* ---------------------------------------------------------------------------------------------------------------
     * Función para leer datos de un archivo .csv y almacenarlos en una matriz tipo string
     * ---------------------------------------------------------------------------------------------------------------*/
    public String[][] readCsvString(CsvReader reader, int nRows, int nCols) throws NumberFormatException, IOException
    {
    	String data[][] = new String[nRows][nCols];
    	try 
    	{
    		int row=0;
    		while (reader.readRecord())
    		{
    			for(int column=0;  column < nCols;column++){
		        		data[row][column] = reader.get(column);	
    			}
    			row++;
    		}
        }
    	 catch (IOException e) 
    	 {
    		 e.printStackTrace();
    	}  	
    	reader.close();
    	return data;
    }
    
     //
     // Función para leer datos de un archivo .csv y almacenarlos en una matriz tipo string
     // 
	public String[] readCsvStringVec(CsvReader reader, int nRows, int nCols) throws NumberFormatException, IOException
    {
    	String data[][] = new String[nRows][nCols];
    	try 
    	{
    		int row=0;
    		while (reader.readRecord())
    		{
    			for(int column=0;  column < nCols;column++){
		        		data[row][column] = reader.get(column);	
    			}  	
    			row++;
    		}
        }
    	 catch (IOException e) 
    	 {
    		 e.printStackTrace();
    	}
    	// cerrar el archivo
    	reader.close();
    	
    	// la matriz de Strings se transforma en un vector fila 
    	return Global.factory.mat2vec(data,nRows,nCols);
    }
	
	//
    // Función para leer datos de un archivo .csv con las ofertas diarias de los generadores y almacenarlos 
	// en un arreglo dinámico de PowerBids
    //
	public Vector<PowerBid> readCsvSupplyVector(CsvReader reader, String[] namesPlants) throws NumberFormatException, IOException
    {
		// vector de PowerBids para almacenar las ofertas de los generadores para cada día
    	Vector<PowerBid> vector = new Vector<PowerBid>();
    	
    	// vector de strings para almacenar la información del archivo   	 
    	Vector<String[]> vector1 = new Vector<String[]>();
    
    	try 
    	{
    		//
    		// mientras un registro no contenga ciertas entradas, el registro será guardado en el vector dinámico de Strings
    		//
    		while (reader.readRecord())
    		{
    				// llenar un vector dinámico de Strings con los registros de los archivos
    	    		vector1.addElement(reader.getValues());
    		}
    		
    		//
    		// gestionar el problema de nombres de unidades de generación
    		//
    		for(int i = 0; i < vector1.size(); i++)
			{
    			if(vector1.get(i)[0].equals("FLORES IVB") || vector1.get(i)[0].equals("FLORES IV"))
    			{
    				vector1.get(i)[0] = "FLORESIVB";
    				break;
    			}
			}
    		
    		//
    		// bloque para formar las ofertas de los generadores cuyos registros están organizados, es decir
    		// donde el nombre para la oferta con el precio es igual al nombre para la oferta de disponibilidad.
    		//   		
    		for (int gen = 0; gen < Global.nUnits; gen++ )
			{ 
    			// valos booleano apra verificar si un recurso de generación tiene o no oferta
    			boolean found = false;
    			
    			// nueva oferta por cada generador
    			PowerBid powerBid = new PowerBid();
    			
    			// recorre el vector dinámico de strings con las ofertas
    			for(int i = 0; i < vector1.size(); i++)
    			{
    				if(namesPlants[gen].equals(vector1.get(i)[0]))
					{  	
						// se hace found = true para indicar que se encontró la oferta del generador
						found = true;
						
						// a la oferta se le asigna el nombre del generador 
						powerBid.setGeneratorName(namesPlants[gen]);
						
						// si el registro que se está leyendo es la oferta de precio
						if(vector1.get(i)[1].equals("P"))
						{
							// a la oferta se le agrega el precio ofertado
							powerBid.setBidPrice(Double.parseDouble(vector1.get(i)[2]));
						}
						// si el registro que se está leyendo es la oferta de disponibilidad
						else if(vector1.get(i)[1].equals("D"))
						{
							// se leen los valores de la disponibilidad para cada hora y se agregan a la oferta
							for(int j = 0; j < 24; j++)
							{
								powerBid.setBidPower(j, Double.parseDouble(vector1.get(i)[j+2]));
							}
						}
					}
    			}
    			// si no se encontró el generador dentro del archivo entonces se 
    			// crea una oferta con su nombre y con precio y disponibilidad cero
    			if(found == false)
				{
						powerBid.setGeneratorName(namesPlants[gen]);
						powerBid.setBidPrice(0.0);
						powerBid.setBidPower(Global.factory.repVec(0.0, 24));
				}
    			
    			// guardar en el vector dinámico de PowerBids la oferta de cada generador 
    			vector.addElement(powerBid);	
			}
        }
    	catch (IOException e) 
    	{
    		 e.printStackTrace();
    		 
    	}
    	// oferta total de energía por hora por día
    	totalPowerBid(vector);
    	
    	// cierra el archivo
    	reader.close();
    	
    	// retorna el vector dinámico de PowerBids
    	return vector;
    }  
	
	//
	// Función para buscar un String dentro de un vector dinámico de Strings y devolver un valor double asociado
	//
	public double searchInVector(Vector<String[]> vector, String name)
    {
		// valor a devolver
		double value = 0.0;
		
		// se recorre el vector dinámico
		for(int i = 0; i < vector.size() - 1; i++){
			if(vector.get(i)[0].equals(name)){
				// si se encuentra el String buscado, se almacena el valor double relacionado y se termina el ciclo
				value = Double.parseDouble(vector.get(i+1)[2]);
				break;
			}
		}
		// retorno dle valor
		return value;
    }
	
	//
	// Función para determinar la oferta total de energía por hora para cada día
	//
	public double[] totalPowerBid(Vector<PowerBid> vector)
    {
		// valor a devolver
		double total[] = new double[24];
		
		// ciclo para las horas
		for(int i = 0; i < 24; i++){
			// se recorre el vector dinámico
			for(int j = 0; j < vector.size(); j++){
				// acumula la oferta de energía por hora
				total[i] = total[i] + vector.get(j).getBidPowerOne(i);
			}
		}
		// imprimir el vector de ofertas totales por hora
		printVector(total);
		
		// retorno del valor
		return total;
    }
	
	//
	// Función para leer los datos de las demandas horarias de energía. La demanda se leen para cada día y se organizan en un
	// archivo .csv, donde cada fila representa un día. 
	//
	public Vector<PowerDemand> readCsvDemandVector(CsvReader reader, String[] nodes) throws NumberFormatException, IOException
    {
		// vector de PowerDemands para almacenar las demandas por nodo para cada día
    	Vector<PowerDemand> vector = new Vector<PowerDemand>();
    	  	
    	// vector de strings para almacenar la información del archivo   	 
    	Vector<String[]> vector1 = new Vector<String[]>();
    	
    	// Instance da PowerDemand para almacenar la demanda de cada nodo
    	PowerDemand powerDemand;
    	try 
    	{
    		// mientras haya filas en el archivo
    		while (reader.readRecord())
    		{
    			// llenar un vector dinámico de Strings con los registros de los archivos
    	    	vector1.addElement(reader.getValues());
    		}
    	
    		//
    		// organización de los datos de la demanda por nodo
    		//
    		for (int node = 0; node < nodes.length; node++ )
			{ 
    			// valor booleano para verificar si un nodo tiene demanda o no
    			boolean found = false;
    			
    			// guarda el nombre del nodo y la demanda horaria
    			powerDemand = new PowerDemand(); 
    			
    			// recorre el vector dinámico de strings con los nodos
    			for(int i = 0; i < vector1.size(); i++)
    			{
    				
    				if(nodes[node].equals(vector1.get(i)[0]))
    				{
    					found = true;
    					powerDemand.setNode(nodes[node]);

    					for(int j = 0; j < 24; j++)
    					{
    						powerDemand.setPowerDemand(j, Double.parseDouble(vector1.get(i)[j+1]));
    					}	
    				}
				}
    			// si no se encontró el nodo dentro del archivo entonces se 
    			// crea una demanda de energía con el nombre del nodo y  demanda cero
    			if(found == false)
				{
    					powerDemand.setNode(nodes[node]);
    					powerDemand.setPowerDemand(Global.factory.repVec(0.0, 24));
				}
    			
    			// guardar en el vector dinámico de powerDemands las demandas de cada noso 
    			vector.addElement(powerDemand);
    		}		
    	}
    	catch (IOException e) 
    	{
    		 e.printStackTrace();
    		 System.out.println("readCsvDemandVector ->"+e);
    	}
    	
    	// demanda total de energía por hora por día
    	totalDemand(vector);
    	
    	// cierra el archivo
    	reader.close();
    	
    	// retorna el vector dinámico de PowerBids
    	return vector;
    } 
	
	//
	// Función para determinar la oferta total de energía por hora para cada día
	//
	public double[] totalDemand(Vector<PowerDemand> vector)
    {
		// valor a devolver
		double total[] = new double[24];
		
		// ciclo para las horas
		for(int i = 0; i < 24; i++){
			// se recorre el vector dinámico
			for(int j = 0; j < vector.size(); j++){
				// acumula la demanda de energía por hora
				total[i] = total[i] + vector.get(j).getPowerDemand(i);
			}
		}
		// imprimir el vector de demandas totales de energía
		printVector(total);
		
		// retorno del valor
		return total;
    }
	
	//
    // Función para escribir resultados en un archivo .csv a partir de un vector dinámico que contiene las demandas de energía
    //
    public void writeCsvPowerDemands(CsvWriter writer, Vector<PowerDemand> vector) throws NumberFormatException, IOException
    {
    	try 
    	{
    		for (int i = 0; i < vector.size(); i++){
    			// escribir el nombre del nodo
    			writer.write(vector.get(i).getNode());
    			// escribir la demanda declarada para cada hora
    			for (int j = 0; j < 24; j++)
    			{
    				writer.write(String.valueOf(vector.get(i).getPowerDemand(j)));
    			}
    		}
    		// Cambiar de línea
	    	writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvPowerDemands ->"+e);
   	 	}	
    }
    
    //
	// Función para leer los datos de las demandas horarias de energía. La demanda se leen para cada día y se organizan en un
	// archivo .csv, donde cada fila representa un día. 
	//
	public Vector<PowerDemand> readCsvIntDemandVector(CsvReader reader, String[] nodes) throws NumberFormatException, IOException
    {
		// vector de PowerDemands para almacenar las demandas para cada día repitiendo nodo
    	Vector<PowerDemand> vector = new Vector<PowerDemand>();
    	
    	// vector de PowerDemands para almacenar las demandas para cada día sin repetir nodo
    	Vector<PowerDemand> vectorDef = new Vector<PowerDemand>();
    	
    	// vector de strings para almacenar la información del archivo   	 
    	Vector<String[]> vector1 = new Vector<String[]>();
    	
    	// demanda para almacenar la demanda de cada nodo
    	PowerDemand powerDemand;
    	try 
    	{
    		// mientras haya ilas en el archivo
    		while (reader.readRecord())
    		{
    			// llenar un vector dinámico de Strings con los registros de los archivos
    	    	vector1.addElement(reader.getValues());
    		}
    	
    		//
    		// organización de los datos de la demanda. Dado que sólo se manejan algunos nodos entonces
    		// los registros serán agrupados según el nodo al cual correspondan
    		//
    		boolean found = false;
    		for(int i = 0; i < vector1.size(); i++)
			{
    			// guarda el nombre del nodo y la demanda horaria
    			powerDemand = new PowerDemand(); 
    			
    			// Un if para cada registro
				if(vector1.get(i)[0].equals("COROZO"))
				{
					found = true;
					powerDemand.setNode("VENEZUELA");

					for(int j = 0; j < 24; j++)
					{
						powerDemand.setPowerDemand(j, Double.parseDouble(vector1.get(i)[j+1]));
					}	
				}
				else if(vector1.get(i)[0].equals("CUATRICENTENARIO"))
				{
					found = true;
					powerDemand.setNode("VENEZUELA");
					for(int j = 0; j < 24; j++)
					{
						powerDemand.setPowerDemand(j, Double.parseDouble(vector1.get(i)[j+1]));
					}
				}		
				//
				// si encontró el registro, lo organiza y lo guarda en el vector dinámico de PowerDemands
				//
				if(found == true)
				{
					vector.addElement(powerDemand);
				}
				// reestablece el valor de found en false para la siguiente iteración del ciclo
				found = false;
			}
    		System.out.println(1);
    		
    		double[] sum;
    		for(int i = 0; i < nodes.length; i++){
    			sum = new double[24];
    			for(int j = 0; j < vector.size(); j++){
    				if(vector.get(j).getNode().equals(nodes[i])){
    					for(int h = 0; h < 24; h++){
        					sum[h] = sum[h] + vector.get(j).getPowerDemand(h);    						
    					}
    				}
    			}
    			vectorDef.addElement(new PowerDemand(nodes[i], sum));
    		}
    		
    	}
    	catch (IOException e) 
    	{
    		 e.printStackTrace();
    		 System.out.println("readCsvIntDemandVector ->"+e);
    	}
    	
    	// demanda total de energía por hora por día
    	totalDemand(vector);
    	
    	// cierra el archivo
    	reader.close();
    	
    	// retorna el vector dinámico de PowerBids
    	return vectorDef;
    } 
	
	//
	// Lee la primera columna de un archivo de ofertas y almacena los nombres de los generadores
	//
	public Vector<String> readCsvGenNames(CsvReader reader, Vector<String> names, int year, int month, int day) throws NumberFormatException, IOException
    {
		Set<String> s; // variable auxiliar que ayuda a eliminar los elementos repetidos del un Vector de Strings
		boolean equal; // variable auxilair para determinar si hay registros nuevos o no
    	try 
    	{
    		// Si nos esncontramos el en primer día del año 2010
    		if(year == Global.year[0] && month == 0 && day == 0)
    		{
    			while (reader.readRecord())
        		{
        			names.add(reader.get(0));	
        		}
    			// Eliminar los elementos duplicados
    			s = new LinkedHashSet<String>(names);
    			names.clear();
    			names.addAll(s);
    		}
    		else
    		{
    			while (reader.readRecord())
        		{	
    				equal = false;
    				for(int i = 0; i < names.size(); i++)
    				{
    					if(reader.get(0).equals("FLORES IVB") || reader.get(0).equals("FLORES IV"))
    					{
    						equal = true;
        					break;
        				}
    					// si el nombre de la central que se está leyendo ya está dentro del Vector de nombres
    					else if(reader.get(0).equals(names.get(i)))
    					{
    						equal = true;
    						break;
    					}
    				}
    				// si el registro que se está leyendo es nuevo se lo adiciona al Vector de nombres
    				if(equal == false)
					{
    					names.add(reader.get(0));
    					System.out.println(reader.get(0)+"\t"+year+"/"+ (month+1)+"/"+(day+1));
					}
        		}
    		}
    			
        }
    	 catch (IOException e) 
    	 {
    		 e.printStackTrace();
    		 System.out.println("readCsvGenNames ->"+e);
    	}
    	// cerrar el archivo
    	reader.close();
    	
    	return names;
    }
	
	//
    // Función para imprimir la generación en consola
    //
    public void printGeneration(double [][] data)
    {
    	for(int row = 0; row < data.length; row++){
    		System.out.print(Global.generationUnitsNames[row] + "\t\t");
        	for(int column = 0; column < data[0].length; column++){
        		System.out.print(data[row][column] + "\t\t");	
            }
        System.out.println();
        }
    }
    
    //
    // Función para imprimir los precios nodales en consola
    //
    public void printNodalPrices(double [][] data)
    {
    	for(int row= 0; row < data.length; row++){
    		System.out.print(Global.nodesNames[row] + "\t\t");
        	for(int column = 0; column < data[0].length; column++){
        		System.out.print(data[row][column] + "\t\t");	
            }
        System.out.println();
        }
    }
    
    
    //
    // Función para imprimir los flujos en consola
    //
    public void printFlows(double [][] data)
    {
    	for(int row= 0; row < data.length; row++){
    		System.out.print(Global.linesNames[row] + "\t\t");
        	for(int column = 0; column < data[0].length; column++){
        		System.out.print(data[row][column] + "\t\t");	
            }
        System.out.println();
        }
    }
    
    //
    // Función para imprimir resultados en un archivo.csv
    //
    public void printResultsCsv(int iteration, Dispatch dispatch, CsvWriter writer)  throws NumberFormatException, IOException
    {
		writer.write("----------------------------------------------   ITERACIÓN:\t" + (iteration +1) + "\t----------------------------------------------");
    	writer.endRecord();
    	writer.endRecord();	
    	writer.write("Generación");
    	writer.endRecord();
    	writer.endRecord();
    	for(int i = 0; i < Global.nUnits; i++)
    	{
    		writer.write(Global.generationUnitsNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getGeneration()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();
		writer.write("Ángulos de voltaje\n");
		writer.endRecord();	
		writer.endRecord();
		for(int i = 0; i < Global.nNodes; i++)
    	{
			writer.write(Global.nodesNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getVoltageAngles()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();	
		writer.write("Precios nodales\n");
		writer.endRecord();	
		writer.endRecord();
		for(int i = 0; i < Global.nNodes; i++)
    	{
			writer.write(Global.nodesNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getNodalPrices()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();	
		writer.write("Flujos de potencia\n");
		writer.endRecord();	
		writer.endRecord();	
		for(int i = 0; i < Global.nLines; i++)
    	{
			writer.write(Global.linesNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getFlows()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();	
		writer.write("Capacidad remanente\n");
		writer.endRecord();	
		writer.endRecord();	
		for(int i = 0; i < Global.nLines; i++)
    	{
			writer.write(Global.linesNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getRemainderCapacity()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();	
		writer.write("Demanda no atendida\n");
		writer.endRecord();	
		writer.endRecord();
		for(int i = 0; i < Global.nNodes; i++)
    	{
			writer.write(Global.nodesNames[i]);
    		for(int h = 0; h < 24; h++)
    		{
    			writer.write(String.valueOf(dispatch.getUnservedDemand()[i][h]));
    		}
    		writer.endRecord();	
    	}
		writer.endRecord();	
		writer.endRecord();	
		writer.write("Costo del despacho");
		writer.write(String.valueOf(dispatch.getDispatchCost()));
		writer.endRecord();	
		writer.endRecord();	    	
    }
    
    //
    // Función para escribir en un archivo .csv el listado de contratos bilaterales
    //
    public void writeCsvContracts(CsvWriter writer, GenerationContract contract) throws NumberFormatException, IOException
    {
    	try 
    	{
			// escribir el nombre del nodo
    		writer.write(String.valueOf(contract.getContractSellerId()));
			writer.write(contract.getContractSeller().getGeneratorName());
			writer.write(String.valueOf(contract.getGenerationUnitId()));
			writer.write(contract.getGenerationUnit().getUnitName());
			writer.write(String.valueOf(contract.getContractBuyerId()));
			writer.write(contract.getContractBuyer().getRetailerName());
			writer.write(contract.getContractType());
			writer.write(String.valueOf(contract.getSourceNodeId()));
			writer.write(contract.getSourceNode().getNodeName());
			writer.write(String.valueOf(contract.getWithdrawalNodeId()));
			writer.write(contract.getWithdrawalNode().getNodeName());
			writer.write(String.valueOf(contract.getContractStartDate()));
			writer.write(String.valueOf(contract.getContractFinalDate()));
    	
			// escribir la energía contratada para cada hora
			for (int j = 0; j < 24; j++)
			{
				writer.write(String.valueOf(Global.decimalFormatter.format(contract.getContractPower()[j])));
			}
			// escribir el precio del contrato para cada hora
			for (int j = 0; j < 24; j++)
			{
				writer.write(String.valueOf(Global.decimalFormatter.format(contract.getContractPrice()[j])));
			}
	    	writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvContracts ->"+e);
   	 	}	
    }
    
    //
    // Función para leer de un archivo .csv el listado de contratos bilaterales
    //
    public List<GenerationContract> readCsvContracts(CsvReader reader, List<Generator> generators, List<Retailer> retailers, 
    		List<GenerationUnit> generationUnits, List<Node> nodes) throws NumberFormatException, IOException
    {
    	List<GenerationContract> contracts = new ArrayList<GenerationContract>();
    	GenerationContract contract;
    	double[] power; //= new double[24];
    	double[] price; //= new double[24];
    	
    	try 
    	{
    		int row = 0;
    		while (reader.readRecord())
    		{
    			contract = new GenerationContract();
    			power = new double[24];
    			price = new double[24];
    			
    			contract.setContractId(row);													// identificador del contrato
    			contract.setContractSellerId(Integer.parseInt(reader.get(0)));					// identificador vendedor
    			contract.setContractSeller(generators.get(contract.getContractSellerId()));		// vendedor	
    			contract.setGenerationUnitId(Integer.parseInt(reader.get(2)));					// unidad de generación
    			contract.setGenerationUnit(generationUnits.get(contract.getGenerationUnitId()));// identificador 
    			contract.setContractBuyerId(Integer.parseInt(reader.get(4)));					// comprador
    			contract.setContractBuyer(retailers.get(contract.getContractBuyerId()));		// identificador comprador
    			contract.setContractType(reader.get(6));										// tipo de contrato
    			contract.setSourceNodeId(Integer.parseInt(reader.get(7)));				// identificador nodo de origen
    			contract.setSourceNode(nodes.get(contract.getSourceNodeId()));			// nodo de origen
    			contract.setWithdrawalNodeId(Integer.parseInt(reader.get(9)));			// identificador nodo de destino
    			contract.setWithdrawalNode(nodes.get(contract.getWithdrawalNodeId()));	// nodo de destino
    			contract.setContractStartDate(Integer.parseInt(reader.get(11)));		// fecha inicio
    			contract.setContractFinalDate(Integer.parseInt(reader.get(12)));		// fecha final
    			
    			//
    			// energía y precio para cada hora
    			//
    			for (int j = 0; j < 24; j++)
    			{
    				power[j] = Double.parseDouble(reader.get(13 + j));
    				price[j] = Double.parseDouble(reader.get(13 + 24 + j));
    			}
    			contract.setContractPower(power);		// potencia contratada
    			contract.setContractPrice(price);		// precio del contrato
    			
    			// adición del contrato construido a la lista de contratos
    			contracts.add(contract);
    			
    			// siguiente línea del archivo
    			row = row + 1;
			}
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("readCsvContracts ->"+e);
   	 	}
    	return contracts;
    }
    
    //
    // Función para escribir en un archivo .csv el listado de unidades de generación
    //
    public void writeCsvGenerationUnits(CsvWriter writer, List<GenerationUnit> generationUnits) throws NumberFormatException, IOException
    {
    	try 
    	{
    		for (int unit = 0; unit < generationUnits.size(); unit++)
    		{
    			writer.write(String.valueOf(generationUnits.get(unit).getUnitID()));
				writer.write(generationUnits.get(unit).getUnitName());				
				writer.write(generationUnits.get(unit).getGenerator().getGeneratorName());
				writer.write(generationUnits.get(unit).getNode().getNodeName());
				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getEffectiveCapacity())));
				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getTechnicalMinimum())));
				
				// proporción en contratos
				for (int j = 0; j < 24; j++)
				{
					writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractsProportion()[j])));
				}
				
				// energía contratada
				for (int j = 0; j < 24; j++)
				{
					writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractEnergy()[j])));
				}
				
				// precio de referencia
				for (int j = 0; j < 24; j++)
				{
					writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getReferenceContractsPrice()[j])));
				}
				writer.write(String.valueOf(generationUnits.get(unit).getPosition()));
	
				// cambio de línea
		    	writer.endRecord();	
    		}
    		writer.close();
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvGenerationUnits ->"+e);
   	 	}	
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //
    // función para escribir la liquidación de los comercializadores en un archivo .csv
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvSettlementRetailers(CsvWriter writer, List<Retailer> retailers, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{
    		writer.write("----------------------------------------------   ITERACIÓN:\t" + (iteration +1) + "\t----------------------------------------------");
    		
    		// cambio de línea
	    	writer.endRecord();	
	    	
    		//
    		// liquidación comercializador
    		//
    		for(int ret = 0; ret < retailers.size(); ret++)
    		{
    			writer.write(String.valueOf(retailers.get(ret).getRetailerName())); // nombre
    			writer.write(String.valueOf(retailers.get(ret).getDemandNode().getNodeName())); // nodo que atiende
    			
    			// demanda de energía
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getEnergyDemand()[h])));
				}
    			
    			// magnitud compras en contratos PC
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getContractEnergyPurchasesPCMWh()[h])));
				}
    			
    			// valor compras en contratos PC
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getContractEnergyPurchasesPCCOP()[h])));
				}
    			
    			// máxima magnitud compras en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getContractMaxEnergyPurchasesPDMWh()[h])));
				}
    			
    			// magnitud compras en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getContractEnergyPurchasesPDMWh()[h])));
				}
    			
    			// valor compras en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getContractEnergyPurchasesPDCOP()[h])));
				}
    			
    			// magnitud compras en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getPoolEnergyPurchasesMWh()[h])));
				}
    			
    			// valor compras en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getPoolEnergyPurchasesCOP()[h])));
				}
    			
    			// magnitud ventas en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getPoolEnergySalesMWh()[h])));
				}
    			
    			// valor ventas en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getPoolEnergySalesCOP()[h])));
				}
    			
    			// valor restricciones
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getConstraintsCOP()[h])));
				}
    			
    			// valor liquidación cargos por uso
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getSettlementUsageChargesCOP()[h])));
				}
    			
    			// valor liquidación total
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getSettlementEnergyMarket()[h])));
				}
    			
    			// cambio de línea
		    	writer.endRecord();	
    		}
    		// cambio de línea para formatos
    		writer.endRecord();	
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvSettlementRetailers ->"+e);
   	 	}
    }
    
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //
    // función para escribir la liquidación de las unidades de generación en un archivo .csv
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvSettlementUnits(CsvWriter writer, List<GenerationUnit> generationUnits, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{	    	
    		writer.write("----------------------------------------------   ITERACIÓN:\t" + (iteration +1) + "\t----------------------------------------------");
    		
    		// cambio de línea
	    	writer.endRecord();	
	    	
    		//
    		// liquidación unidades de generación
    		//
    		for(int unit = 0; unit < generationUnits.size(); unit++)
    		{
    			writer.write(String.valueOf(generationUnits.get(unit).getUnitName())); // nombre
    			
    			// generación ideal de energía
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getIdealGeneration()[h])));
				}
    			
    			// generación real de energía
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getRealGeneration()[h])));
				}
    			
    			// magnitud ventas en contratos PC
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractEnergySalesPCMWh()[h])));
				}
    			
    			// valor ventas en contratos PC
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractEnergySalesPCCOP()[h])));
				}
    			
    			// máxima magnitud ventas en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractMaxEnergySalesPDMWh()[h])));
				}
    			
    			// magnitud ventas en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractEnergySalesPDMWh()[h])));
				}
    			
    			// valor ventas en contratos PD
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getContractEnergySalesPDCOP()[h])));
				}
    			
    			// magnitud compras en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPoolEnergyPurchasesMWh()[h])));
				}
    			
    			// valor compras en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPoolEnergyPurchasesCOP()[h])));
				}
    			
    			// magnitud ventas en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPoolEnergySalesMWh()[h])));
				}
    			
    			// valor ventas en bolsa
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPoolEnergySalesCOP()[h])));
				}
    			
    			// magnitud reconciliaciones positivas
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPositiveReconciliationMWh()[h])));
				}
    			
    			// valor reconciliaciones positivas
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getPositiveReconciliationCOP()[h])));
				}
    			
    			// magnitud reconciliaciones negativas
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getNegativeReconciliationMWh()[h])));
				}
    			
    			// valor reconciliaciones negativas
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getNegativeReconciliationCOP()[h])));
				}
    			
    			// valor liquidación cargos por uso
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getSettlementUsageChargesCOP()[h])));
				}
    			    			
    			// valor liquidación total
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(generationUnits.get(unit).getSettlementEnergyMarket()[h])));
				}
    			
    			// cambio de línea
		    	writer.endRecord();	
    		}
    		// cambio de línea para formatos
    		writer.endRecord();	
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvSettlementUnits ->"+e);
   	 	}
    }
    
  //---------------------------------------------------------------------------------------------------------------------------------------
    //
    // función para escribir la liquidación de las unidades de generación en un archivo .csv
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvSettlementTransmitters(CsvWriter writer, List<Transmitter> transmitters, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{	    	
    		writer.write("----------------------------------------------   ITERACIÓN:\t" + (iteration +1) + "\t----------------------------------------------");
    		
    		// cambio de línea
	    	writer.endRecord();	
	    	
    		//
    		// liquidación unidades de generación
    		//
    		for(int gridco = 0; gridco < transmitters.size(); gridco++)
    		{
    			writer.write(String.valueOf(transmitters.get(gridco).getTransmitterName())); // nombre
    			
    			// valor liquidación cargos por uso
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(transmitters.get(gridco).getSettlementUsageChargesCOP()[h])));
				}
    			
    			// cambio de línea
		    	writer.endRecord();	
    		}
    		// cambio de línea para formatos
    		writer.endRecord();	
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvSettlementTransmitters ->"+e);
   	 	}
    }
    //
    // función para imprimir en consola un arreglo de contratos organizados pore precio
    //
    public void printArraySortContracts(List<List<GenerationContract>> sortContracts)
    {
    	for(int i = 0; i < sortContracts.size(); i++)
    	{
    		for(int j = 0; j < sortContracts.get(i).size(); j++)
    		{
    			System.out.print("\t" + sortContracts.get(i).get(j).getContractPrice()[i]);
    		}
    		System.out.println();
    	}
    }
    
    //
    // función para imprimir en consola los contratos despachados en cada hora
    //
    public void printArrayDispatchContracts(List<List<GenerationContract>> dispachedContracts)
    {
    	for(int i = 0; i < dispachedContracts.size(); i++)
    	{
    		System.out.println(i);
    		for(int j = 0; j < dispachedContracts.get(i).size(); j++)
    		{
    			System.out.println("\t" + dispachedContracts.get(i).get(j).getContractId()+ "\t" + dispachedContracts.get(i).get(j).getContractSeller().getGeneratorName()
    					+ "\t" + dispachedContracts.get(i).get(j).getGenerationUnit().getUnitName()
    					+ "\t" + dispachedContracts.get(i).get(j).getContractBuyer().getRetailerName() + "\t" + dispachedContracts.get(i).get(j).getWithdrawalNode().getNodeName()
    					+ "\t" + dispachedContracts.get(i).get(j).getContractPrice()[i]);
    		}
    		System.out.println();
    	}
    }
}