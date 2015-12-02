package utilities;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class TableFormat extends DefaultTableCellRenderer
{

   	private static final long serialVersionUID = 1L;
	
	// cosntructor vacío
	public TableFormat(){}
		
    @Override
    public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column )
    {     
        setBackground(Color.white);//color de fondo
        table.setForeground(Color.black);//color de texto
        
        // 
        if(column == 0)
        {
        	for(int i = 0; i < Global.gensAntioqui.length; i++)
        	{
	        	if( value.equals(Global.gensAntioqui[i]) )
	            {
	                setBackground(Color.green);
	            } 
        	}
        	for(int i = 0; i < Global.gensAtlantic.length; i++)
        	{
	        	if( value.equals(Global.gensAtlantic[i]) )
	            {
	                setBackground(Color.yellow);
	            } 
        	}
        	for(int i = 0; i < Global.gensBogota.length; i++)
        	{
	        	if( value.equals(Global.gensBogota[i]) )
	            {
	                setBackground(Color.red);
	            } 
        	}
        	for(int i = 0; i < Global.gensBolivar.length; i++)
        	{
	        	if( value.equals(Global.gensBolivar[i]) )
	            {
	                setBackground(Color.blue);
	            } 
        	}
        	for(int i = 0; i < Global.gensCaucanar.length; i++)
        	{
	        	if( value.equals(Global.gensCaucanar[i]) )
	            {
	                setBackground(Color.cyan);
	            } 
        	}
        	for(int i = 0; i < Global.gensCerromat.length; i++)
        	{
	        	if( value.equals(Global.gensCerromat[i]) )
	            {
	                setBackground(Color.darkGray);
	            } 
        	}
        	for(int i = 0; i < Global.gensChivor.length; i++)
        	{
	        	if( value.equals(Global.gensChivor[i]) )
	            {
	                setBackground(Color.magenta);
	            } 
        	}
        	for(int i = 0; i < Global.gensCorozo.length; i++)
        	{
	        	if( value.equals(Global.gensCorozo[i]) )
	            {
	                setBackground(Color.orange);
	            } 
        	}
        	for(int i = 0; i < Global.gensCQR.length; i++)
        	{
	        	if( value.equals(Global.gensCQR[i]) )
	            {
	                setBackground(Color.gray);
	            } 
        	}
        	for(int i = 0; i < Global.gensCuatricentenario.length; i++)
        	{
	        	if( value.equals(Global.gensCuatricentenario[i]) )
	            {
	                setBackground(Color.pink);
	            } 
        	}
        	for(int i = 0; i < Global.gensEcuador.length; i++)
        	{
	        	if( value.equals(Global.gensEcuador[i]) )
	            {
	                setBackground(Color.lightGray);
	            } 
        	}
        	for(int i = 0; i < Global.gensGCM.length; i++)
        	{
	        	if( value.equals(Global.gensGCM[i]) )
	            {
	                setBackground(new Color(0.1F,0.2F,0.3F));
	            } 
        	}
        	for(int i = 0; i < Global.gensHuilacaq.length; i++)
        	{
	        	if( value.equals(Global.gensHuilacaq[i]) )
	            {
	                setBackground(new Color(0.1F,0.3F,0.4F));
	            } 
        	}
        	for(int i = 0; i < Global.gensLamiel.length; i++)
        	{
	        	if( value.equals(Global.gensLamiel[i]) )
	            {
	                setBackground(new Color(0.1F,0.5F,0.6F));
	            } 
        	}
        	for(int i = 0; i < Global.gensMagdamed.length; i++)
        	{
	        	if( value.equals(Global.gensMagdamed[i]) )
	            {
	                setBackground(new Color(0.1F,0.7F,0.8F));
	            } 
        	}
        	for(int i = 0; i < Global.gensNordeste.length; i++)
        	{
	        	if( value.equals(Global.gensNordeste[i]) )
	            {
	                setBackground(new Color(0.1F,0.9F,1.0F));
	            } 
        	}
        	for(int i = 0; i < Global.gensPagua.length; i++)
        	{
	        	if( value.equals(Global.gensPagua[i]) )
	            {
	                setBackground(new Color(0.3F,0.2F,0.3F));
	            } 
        	}
        	for(int i = 0; i < Global.gensSancarlo.length; i++)
        	{
	        	if( value.equals(Global.gensSancarlo[i]) )
	            {
	                setBackground(new Color(0.5F,0.2F,0.3F));
	            } 
        	}
        	for(int i = 0; i < Global.gensTolima.length; i++)
        	{
	        	if( value.equals(Global.gensTolima[i]) )
	            {
	                setBackground(new Color(0.7F,0.2F,0.3F));
	            } 
        	}
        	for(int i = 0; i < Global.gensVallecau.length; i++)
        	{
	        	if( value.equals(Global.gensVallecau[i]) )
	            {
	                setBackground(new Color(0.9F,0.2F,0.3F));
	            } 
        	}	
        }
        else if (column == 4)
        {
        	// si la gen ideal es diferente de la generación real 
        	if( Double.parseDouble((String)value) != 0.0 )
            {
                setBackground(Color.red);
            }        	
        }
        
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        return this;
    }
 }

