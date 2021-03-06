package utilities;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class GraphicsScatterPlot extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// elementos d ela interfaz	
	private XYDataset dataset; 	// conjunto de datos
	private JFreeChart chart; 	// gr�fico
		
	// constructor con el t�tulo de la pantalla 
	public GraphicsScatterPlot(String title) {
        super(title);
    }
	
	// constructor vac�o 
	public GraphicsScatterPlot() {}
	
	// constructor para una gr�fica tipo StepChart
	public GraphicsScatterPlot(final String title, XYDataset dataset, JFreeChart chart) 
	{
        super(title);
 		
 		// tipo de gr�fico
 		ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
  		setContentPane(chartPanel);
    }
		
	// set methods
	public void setDataset(XYDataset dataset){this.dataset =  dataset; }
	public void setChar(JFreeChart chart){this.chart =  chart; }
	
	//
	//formato de un gr�fico tipo StepChart
	//
	private void stepChartTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white); 		// chart background        
        final XYPlot plot = chart.getXYPlot(); 		// se obtiene una referencia a la gr�fica para futuras modificaciones
        plot.setBackgroundPaint(Color.white);		// background del �rea del gr�fico
        plot.setDomainGridlinePaint(Color.black); 	// color cuadricula eje x
        plot.setRangeGridlinePaint(Color.black);	// color cuadricula eje y

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	       
	}
	
	//
	// Formato de un gr�fico tipo LineChart
	//
	private void lineChartTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white); // chart background        
        final XYPlot plot = chart.getXYPlot(); // Se obtiene una referencia a la gr�fica para futuras modificaciones
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        
        // permite cambiar la presentaci�n de las lineas que representan las series de tiempo
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	       
	}
}
