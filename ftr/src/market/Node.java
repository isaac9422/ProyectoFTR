package market;

public class Node {
	
	private int nodeId;			// identificador del nodo
	private String nodeName; 	// nombre del nodo
	private int position;		// posición (orden) en base de datos
	
	// constructor vacío
	public Node(){};
	
	// constructor: nodeID, nodeName
	public Node(int nodeId, String nodeName, int position){
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.position = position;
	}
	
	// get methods
	public int getNodeId(){ return this.nodeId;} // obtener el identificador del nodo
	public String getNodeName(){ return this.nodeName;} // obtener el nombre del nodo
	
	// set methods
	public void setNodeId(int nodeId){ this.nodeId = nodeId;} // establecer el identificador del nodo
	public void setNodeName(String nodeName){ this.nodeName = nodeName;} // establecer el nombre del nodo
	
	// imprimir las características de cada nodo
	public void printNode(){
		System.out.println(this.nodeId + "\t" + this.nodeName + "\t" + this.position);
	}
}
