package utilities;

import java.util.Collections;
import java.util.List;

import market.PowerBid;

public class MathFuns {
	
	// constructor vac�o
	public MathFuns(){}
	
	//
	// organizar un arreglo de ofertas de generaci�n horarias
	//
	public void sortPowerBids(List<PowerBid> powerBids)
	{
		Collections.sort(powerBids);
	}

}
