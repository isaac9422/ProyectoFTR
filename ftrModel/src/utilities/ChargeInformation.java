package utilities;

import java.io.FileInputStream;
import java.text.DecimalFormat;

import jxl.*;

public class ChargeInformation {
	
	DecimalFormat format = new DecimalFormat("###,##");
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of lower power limit for each generation unit
	* ------------------------------------------------------------------------------------------------------------------*/
	public double [] lowPowerLimit(int iteration, int units, Sheet sheet){
		double [] lowPowerLimit = new double[units];
		try {
			for (int i = 0; i < units; i++){
				lowPowerLimit[i] = Double.parseDouble(sheet.getCell(units - 2 + i,iteration -2 + 5).getContents()); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("lowPowerLimit ->"+e);
        }
        return lowPowerLimit;
	}
	
			
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of upper power limit for each generation unit
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public double [] upPowerLimit(int iteration, int units, Sheet sheet){
		double [] upPowerLimit = new double[units];
		try {
			for (int i = 0; i < units; i++){
				upPowerLimit[i] = Double.parseDouble(sheet.getCell(2*units - 2 + i,iteration -2 + 5).getContents()); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("upPowerLimit ->"+e);
        }
        return upPowerLimit;
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of generation cost for each generation unit
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public double [] generationCost(int iteration, int units, Sheet sheet){
		double [] generationCost = new double[units];
		try {
			for (int i = 0; i < units; i++){
				generationCost[i] = format.parse(sheet.getCell(3*units - 2 + i,iteration -2 + 5).getContents().toString()).doubleValue();
			}
		 }
        catch(Exception e)
        {
            System.out.println("generationCost ->"+e);
        }
        return generationCost;
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of power demand in each node
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public double [] powerDemand(int iteration, int nodes, Sheet sheet){
		double [] powerDemand = new double[nodes];
		try {
			for (int i = 0; i < nodes; i++){
				powerDemand[i] = Double.parseDouble(sheet.getCell(nodes - 1 + i,iteration -2 + 5).getContents()); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerDemand ->"+e);
        }
        return powerDemand;
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of power demand in each node
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public void powerDemandXls(int iteration, double [][] powerDemand, Sheet sheet){
		try {
			for (int i = 0; i < 24; i++){
				powerDemand[0][i] = format.parse(sheet.getCell(2 + i,iteration + 3).getContents().toString()).doubleValue(); 
				powerDemand[1][i] = format.parse(sheet.getCell(26 + i,iteration + 3).getContents().toString()).doubleValue();
				powerDemand[2][i] = format.parse(sheet.getCell(50 + i,iteration + 3).getContents().toString()).doubleValue(); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerDemandXls ->"+e);
        }   
	}
	
	//
	// Función para lee los datos de la demanda de energía por nodo y para cada hora [SIN GENERALIZAR]
	//
	
	public void powerDemandCsv(int iteration, double [][] powerDemand, double [][] demand){
		try {
			for (int i = 0; i < 24; i++){
				powerDemand[0][i] = demand[iteration][i+0];
				powerDemand[1][i] = demand[iteration][i+24];
				powerDemand[2][i] = demand[iteration][i+48];
				powerDemand[3][i] = demand[iteration][i+72];
				powerDemand[4][i] = demand[iteration][i+96];
				powerDemand[5][i] = demand[iteration][i+120];
				powerDemand[6][i] = demand[iteration][i+144];
				powerDemand[7][i] = demand[iteration][i+168];
				powerDemand[8][i] = demand[iteration][i+192];
				powerDemand[9][i] = demand[iteration][i+216];
				powerDemand[10][i] = demand[iteration][i+240];
				powerDemand[11][i] = demand[iteration][i+264];
				powerDemand[12][i] = demand[iteration][i+288];
				powerDemand[13][i] = demand[iteration][i+312];
				powerDemand[14][i] = demand[iteration][i+336];
				powerDemand[15][i] = demand[iteration][i+360];
				powerDemand[16][i] = demand[iteration][i+384];
				powerDemand[17][i] = demand[iteration][i+408];
				powerDemand[18][i] = demand[iteration][i+432];
				powerDemand[19][i] = demand[iteration][i+456];
				powerDemand[20][i] = demand[iteration][i+480];
				powerDemand[21][i] = demand[iteration][i+504];
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerDemandCsv ->"+e);
        }   
	}
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of power bids of each generation unit
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public void powerBidXls(int iteration, double [] powerBid, Sheet sheet){
		try {
			for (int i = 0; i < 24; i++){
				powerBid[i     ] = format.parse(sheet.getCell(3 + i,iteration + 3).getContents().toString()).doubleValue(); 
				powerBid[i + 24] = format.parse(sheet.getCell(28 + i,iteration + 3).getContents().toString()).doubleValue();
				powerBid[i + 48] = format.parse(sheet.getCell(53 + i,iteration + 3).getContents().toString()).doubleValue();
				powerBid[i + 72] = format.parse(sheet.getCell(78 + i,iteration + 3).getContents().toString()).doubleValue(); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerBidXls ->"+e);
        } 
	}
	
	//
	// Función para leer los datos de la oferta de energía por unidad y para cada hora del día
	//
	public void powerBidCsv(int iteration, double [][] powerBid, double [][] supply){
		try {
			for(int j = 0; j < Global.nUnits; j++){
				for (int i = 0; i < 24; i++){
					powerBid[j][i] = supply[iteration][i+25*j+1];
				}
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerBidCsv ->"+e);
        }   
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of power prices of each generation unit
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public void powerBidPriceXls(int iteration, double [] powerBidPrice, Sheet sheet){
		try {
			for (int i = 0; i < 24; i++){
				powerBidPrice[i     ] = format.parse(sheet.getCell(2,iteration + 3).getContents().toString()).doubleValue(); 
				powerBidPrice[i + 24] = format.parse(sheet.getCell(27,iteration + 3).getContents().toString()).doubleValue(); 
				powerBidPrice[i + 48] = format.parse(sheet.getCell(52,iteration + 3).getContents().toString()).doubleValue(); 
				powerBidPrice[i + 72] = format.parse(sheet.getCell(77,iteration + 3).getContents().toString()).doubleValue();  
			}
		 }
        catch(Exception e)
        {
            System.out.println("powerBidPriceXls ->"+e);
        } 
	}
	
	//
	// Función para leer los datos de la oferta de precio por unidad y para cada hora del día
	//
	public void powerBidPriceCsv(int iteration, double [][] powerBidPrice, double [][] supply){
		try {
			for(int j = 0; j < Global.nUnits; j++){
				for (int i = 0; i < 24; i++){
					powerBidPrice[j][i] = supply[iteration][25*j];
				}
			}
			
		 }
        catch(Exception e)
        {
            System.out.println("powerBidPriceCsv ->"+e);
        }   
	}
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of susceptance
	 *------------------------------------------------------------------------------------------------------------------*/
	 
	public double [] susceptance(int iteration, int lines, Sheet sheet){
		double [] susceptance = new double[lines];
		try {
			for (int i = 0; i < lines; i++){
				susceptance[i] = Double.parseDouble(sheet.getCell(lines - 1 + i,iteration -2 + 5).getContents()); 
			}
		 }
        catch(Exception e)
        {
            System.out.println("susceptance ->"+e);
        }
        return susceptance;
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of power limit
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public double [] powerLimit(int iteration, int lines, Sheet sheet){
		double [] powerLimit = new double[lines];
		try {
			for (int i = 0; i < lines; i++){
				powerLimit[i] = Double.parseDouble(sheet.getCell(2*lines -1 + i, iteration -2 + 5).getContents());
			}
		}
		catch(Exception e)
		{
		    System.out.println("powerLimit ->"+e);
		}	
		return powerLimit;
	}
	
	/*---------------------------------------------------------------------------------------------------------------------
	 * Read the data of expansion cost
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	public double [] expansionCost(int iteration, int lines, Sheet sheet){
		double [] expansionCost = new double[lines];
		try{
			for (int i = 0; i < lines; i++){
				expansionCost[i] = Double.parseDouble(sheet.getCell(3*lines -1 + i, iteration - 2 + 5).getContents());
			}			
		}
		catch(Exception e)
		{
		    System.out.println("expansionCost ->"+e);
		}
		return expansionCost;
	}
}
