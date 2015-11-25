package utilities;

import java.util.Collections;
import java.util.List;

import market.PowerBid;

public class MathFuns {
	
	// constructor vacío
	public MathFuns(){}
	
	//
	// organizar un arreglo de ofertas de generación horarias
	//
	public void sortPowerBids(List<PowerBid> powerBids)
	{
		Collections.sort(powerBids);
	}

}
