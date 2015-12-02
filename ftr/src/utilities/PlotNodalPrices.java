package utilities;

import market.Dispatch;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import utilities.Global;

public class PlotNodalPrices implements Runnable{

	//
	Dispatch dispatch;
	XYSeriesCollection dataset;
	
	
	// constructor vac�o
	public PlotNodalPrices(){}
	
	// constructor: despacho, dataset, JFreeChar, GraphicsStepChart
	public PlotNodalPrices(Dispatch dispatch, XYSeriesCollection dataset){
		this.dispatch = dispatch;
		this.dataset = dataset;		
	}
	
	@Override
	public void run() {
		
		this.dataset.removeAllSeries();
        
		//XYSeries[] series = new XYSeries[Global.nNodes]; 
        //series.clear();
        for(int n = 0; n < Global.nNodes; n++)
        {
        	XYSeries series = new XYSeries(Global.nodesNames[n],false, true);
        	
            for(int h = 0; h<24; h++){
            	//series.add(DateUtilities.createDate(2010, 1, 1, h+1, 0).getTime(),operator.getIdealDispatch().getNodalPrices()[n][h]);
            	series.add(h+1,this.dispatch.getNodalPrices()[n][h]);
            }
            this.dataset.addSeries(series);
        }
        //System.out.println(Thread.currentThread().getName());
        //stepGraphic.setChart(lineChart);
        //stepGraphic = new GraphicsStepChart("Prueba", dataset, lineChart, "line");		
	}
}