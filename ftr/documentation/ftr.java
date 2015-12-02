	//---------------------------------------------------------------------------------------------------------------------------------------
    //
    // ftrs: función para escribir la magnitud de las compras en contratos PC de todos los comercializadores
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvFtrRetailersContractEnergyPurchasesPCMWh(CsvWriter writer, List<Retailer> retailers, double[] FtrRetailersContractEnergyPurchasesPCMWh, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{	    	
    		if(iteration == 0)
    		{
    			// cambio de línea
    			writer.endRecord();	
    			writer.endRecord();
    		}
	    	
    		//
    		// magnitud compras en contratos PC para cada comercializador
    		//
    		for(int ret = 0; ret < retailers.size(); ret++)
    		{
    			writer.write(String.valueOf(retailers.get(ret).getRetailerCod())); // nombre
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getFtrContractEnergyPurchasesPCMWh()[h])));
				}
    		}
    		
    		//
    		// magnitud compras en contratos PC totales
    		//
    		writer.write("MARKET");
    		for (int h = 0; h < 24; h++)
			{
    			writer.write(String.valueOf(Global.decimalFormatter.format(FtrRetailersContractEnergyPurchasesPCMWh[h])));
			}
    		
    		// cambio de línea para formatos
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvFtrRetailersContractEnergyPurchasesPCMWh ->"+e);
   	 	}
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //
    // ftrs: función para escribir el valor de las compras en contratos PC de todos los comercializadores
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvFtrRetailersContractEnergyPurchasesPCCOP(CsvWriter writer, List<Retailer> retailers, double[] FtrRetailersContractEnergyPurchasesPCCOP, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{	    	
    		if(iteration == 0)
    		{
    			// cambio de línea
    			writer.endRecord();	
    			writer.endRecord();
    		}
	    	
    		//
    		// valor compras en contratos PC por comercializador
    		//
    		for(int ret = 0; ret < retailers.size(); ret++)
    		{
    			writer.write(String.valueOf(retailers.get(ret).getRetailerCod())); // nombre
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getFtrContractEnergyPurchasesPCCOP()[h])));
				}
    		}
    		
    		//
    		// valor compras en contratos PC totales
    		//
    		writer.write("MARKET");
    		for (int h = 0; h < 24; h++)
			{
    			writer.write(String.valueOf(Global.decimalFormatter.format(FtrRetailersContractEnergyPurchasesPCCOP[h])));
			}
    		
    		// cambio de línea para formatos
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvFtrRetailersContractEnergyPurchasesPCCOP ->"+e);
   	 	}
    }
    
  //---------------------------------------------------------------------------------------------------------------------------------------
    //
    // ftrs: función para escribir la magnitud de las compras en contratos PD de todos los comercializadores
    //
    //---------------------------------------------------------------------------------------------------------------------------------------
    public void writeCsvFtrRetailersContractEnergyPurchasesPDMWh(CsvWriter writer, List<Retailer> retailers, double[] FtrRetailersContractEnergyPurchasesPDMWh, int iteration) throws NumberFormatException, IOException
    {
    	try 
    	{	    	
    		if(iteration == 0)
    		{
    			// cambio de línea
    			writer.endRecord();	
    			writer.endRecord();
    		}
	    	
    		//
    		// magnitud compras en contratos PD por comercializador
    		//
    		for(int ret = 0; ret < retailers.size(); ret++)
    		{
    			writer.write(String.valueOf(retailers.get(ret).getRetailerCod())); // nombre
    			for (int h = 0; h < 24; h++)
				{
    				writer.write(String.valueOf(Global.decimalFormatter.format(retailers.get(ret).getFtrContractEnergyPurchasesPDMWh()[h])));
				}
    		}
    		
    		//
    		// magnitud compras en contratos PD totales
    		//
    		writer.write("MARKET");
    		for (int h = 0; h < 24; h++)
			{
    			writer.write(String.valueOf(Global.decimalFormatter.format(FtrRetailersContractEnergyPurchasesPDMWh[h])));
			}
    		
    		// cambio de línea para formatos
    		writer.endRecord();	
    	}
    	catch (IOException e) 
   	 	{
   		 e.printStackTrace();
   		System.out.println("writeCsvFtrRetailersContractEnergyPurchasesPDMWh ->"+e);
   	 	}
    }
    
	--------------------------------------------------------------------------------------
	--------------------------------------------------------------------------------------
	--------------------------------------------------------------------------------------
	--------------------------------------------------------------------------------------
	--------------------------------------------------------------------------------------
	/*
	
	//public void setPowerDemand(double[][] powerDemand){this.powerDemand = powerDemand;} // set the hourly power demand by node
	//public void setHistoricalNodalPrices( List<double[][]> historicalNodalPrices){this.historicalNodalPrices = historicalNodalPrices;}
		
	// get the cost of generation of all generation units
	public double[] getGenerationCosts(){
		double [] generationCosts = new double[Global.nUnits];
		for (int i = 0; i < Global.nUnits; i++){
			generationCosts[i] = this.generationUnits.get(i).getGenerationCost();
		}
		return generationCosts;
	}
	
	// get the susceptance of all generation units
	public double[] getSusceptances(){
		double [] susceptances = new double[Global.nLines];
		for (int i = 0; i < Global.nLines; i++){
			susceptances[i] = this.transmissionLines.get(i).getSusceptance();
		}
		return susceptances;
	}
	
	// get the power flow limit of all generation units
	public double[] getPowerFlowLimits(){
		double [] powerFlowLimit = new double[this.transmissionLines.size()];
		for (int i = 0; i < this.transmissionLines.size(); i++){
			powerFlowLimit[i] = this.transmissionLines.get(i).getPowerFlowLimit();
		}
		return powerFlowLimit;
	}
	
	
	// set real generation to each generation unit
	public void setGeneration2Units(double[][] generation){
		for (int unit = 0; unit < this.generationUnits.size(); unit++){
			this.generationUnits.get(unit).setRealGeneration(generation[unit]);
		}
	}
	
	
	// set real power demand to each retailer
	public void setPowerDemand2Retailers(double[][] energyDemand) {
		for (int ret = 0; ret < this.retailers.size(); ret++){
			this.retailers.get(ret).setEnergyDemand(energyDemand[ret]);
		}
	}
	
	// set real nodal prices
	public void setRealNodalPrices(double[] nodalPrices) {
		this.nodalPricesR = factory.vec2mat(nodalPrices,Global.nNodes,24);
	}*/
	
	/*// print the list of generators
		public void printGenerators(){
			for(int gen = 0; gen < this.generators.size(); gen++){
				for (int bid = 0; bid < this.generators.get(gen).getPowerBids().size(); bid++){
					System.out.println(generators.get(gen).getGeneratorName() + "\t" 
							+ generators.get(gen).getPowerBids().get(bid).getBidPrice() + "\t"
							+ generators.get(gen).getPowerBids().get(bid).getBidPower());	
				}	
			}
		}
	
	// contract generation settlement
	public void contractGenerationSettlement(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// settlement the generation contracts
						this.contractSettlementGeneration[contract][hour] =
								this.generationContracts.get(contract).getContractPrice()[hour] *
								Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
										this.generationContracts.get(contract).getContractPower()[hour]);

						
						// settlement the generation contracts
						/*this.contractSettlementGeneration[contract][hour] =
								this.generationContracts.get(contract).getContractPrice() * 
								Math.min(this.generationUnits.get(unit).getRealGeneration()[hour],
										Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
												this.generationContracts.get(contract).getContractPower()));
						
						// reduce the missing value of programmed generation
						this.generationUnits.get(unit).getRealGeneration()[hour] =
								this.generationUnits.get(unit).getRealGeneration()[hour] -
								Math.min(this.generationUnits.get(unit).getRealGeneration()[hour],
										Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
												this.generationContracts.get(contract).getContractPower()));
						*/
		/*				// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] -
								Math.min(this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour],
										this.generationContracts.get(contract).getContractPower()[hour]);
								
					}
				}
			}
		}
		this.setGeneration2Units(this.realDispatch.getGenerationClone());
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	//  calculate the pool purchases for each generation unit in each hour
	public void poolPurchasesSettlementGeneration(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// settlement the power pool
						this.poolGeneration[contract][hour] = 
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] -
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of generation
						this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] =
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
					}
					
					// calculate the pool purchases for each generation unit in each hour
					if(this.poolGeneration[contract][hour] < 0){
						// nodal market
						this.nPoolPurSetGen[contract][hour] = 
								Math.abs(this.poolGeneration[contract][hour]) * 
								this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode() - 1][hour];
						// uninodal market
						/*this.unPoolPurSetGen[contract][hour] = 
								Math.abs(this.poolGeneration[contract][hour]) * 
								this.idealDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode() - 1][hour];        
						*/
/*
					}
				}	
			}
		}
		this.setGeneration2Units(this.realDispatch.getGeneration());
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// calculate the pool sales for each generation unit in each hour
	public void poolSalesSettlementGeneration(){
		for(int unit = 0; unit < this.generationUnits.size(); unit++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
						// nodal market
						this.nPoolSalSetGen[unit][hour] = 
								this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
								Math.max(0, 
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
						
						// uninodal market
						/*this.unPoolSalSetGen[unit][hour] = 
								this.idealDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
								Math.max(0,
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
											Math.min(this.generationContracts.get(contract).getContractPower(),
												 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
						*/						
						/*// reduce the missing value of generation
						this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] =
								this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
						
						// reduce the missing value of power demand
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] =
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] - 
								Math.min(this.generationContracts.get(contract).getContractPower()[hour],
										 this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
					}
				}
				if(this.generationUnits.get(unit).getHaveContracts() == 0){
					
					// nodal market
					this.nPoolSalSetGen[unit][hour] = 
							this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
							this.generationUnits.get(unit).getRealGeneration()[hour];
					
					/* uninodal market
					this.unPoolSalSetGen[unit][hour] = 
							this.idealDispatch.getNodalPrices()[this.generationUnits.get(unit).getNode() - 1][hour] *
							this.generationUnits.get(unit).getRealGeneration()[hour];
							*//*
				}
			}
		}
		this.setGeneration2Units(this.generationR);
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// demand settlement: contracts and both pool purchases an sales
	public void demandSettlement(){
		for(int ret = 0; ret < this.retailers.size(); ret++){
			for (int hour = 0; hour < 24; hour ++){
				for(int contract = 0; contract < this.generationContracts.size(); contract++){
					if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
						
						// settlement the generation contracts
						this.contractSettlementDemand[contract][hour] = 
								this.generationContracts.get(contract).getContractPrice()[hour] *
								Math.min(this.retailers.get(ret).getPowerDemand()[hour],
								this.generationContracts.get(contract).getContractPower()[hour]);
						
						// reduce the missing value of power demand
						this.retailers.get(ret).getPowerDemand()[hour] =
								this.retailers.get(ret).getPowerDemand()[hour] -
								Math.min(this.retailers.get(ret).getPowerDemand()[hour],
								this.generationContracts.get(contract).getContractPower()[hour]);
					}
				}
				
				// settlement the power pool
				this.poolDemand[ret][hour] = this.retailers.get(ret).getPowerDemand()[hour];

				// calculate the pool sales for each retailer in each hour
				if(this.poolDemand[ret][hour] < 0){
					
					// nodal market
					this.nPoolSalSetDem[ret][hour] = 
							Math.abs(this.poolDemand[ret][hour]) * 
							this.realDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
					
					/*/// uninodal market
					/*this.unPoolSalSetDem[ret][hour] = 
							Math.abs(this.poolDemand[ret][hour]) * 
							this.idealDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
							*/        
				/*
				}
				// calculate the pool purchases for each retailer in each hour
				else {
					// nodal market
					this.nPoolPurSetDem[ret][hour] = 
							this.poolDemand[ret][hour] * 
							this.realDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
					
					/*/// uninodal market
					/*this.unPoolPurSetDem[ret][hour] = 
							this.poolDemand[ret][hour] * 
							this.idealDispatch.getNodalPrices()[this.retailers.get(ret).getDemandNode() - 1][hour];
							*/ 
				/*
				}
			}
		}
		this.setPowerDemand2Retailers(this.realDispatch.getPowerDemandClone());
	}
	
	// transmission settlement
	public void transmissionSettlement(){
		for(int gridco = 0; gridco < Global.nGridcos; gridco++){
			for(int hour = 0; hour < 24; hour++){
				for(int line = 0; line < Global.nLines; line++){
					if(this.transmitters.get(gridco) == this.transmissionLines.get(line).getLineOwner())
						// nodal market
						this.nGridcosIncome[gridco][hour] = 
								Math.abs((this.realDispatch.getNodalPrices()[this.transmissionLines.get(line).getEndNode()-1][hour] - 
										this.realDispatch.getNodalPrices()[this.transmissionLines.get(line).getSourceNode()-1][hour]) *
										this.realDispatch.getFlows()[line][hour]);
						
						
					/*/// uninodal market
						/*this.unGridcosIncome[gridco][hour] = 
							Math.abs((this.idealDispatch.getNodalPrices()[this.transmissionLines.get(line).getEndNode()-1][hour] - 
									this.idealDispatch.getNodalPrices()[this.transmissionLines.get(line).getSourceNode()-1][hour]) *
									this.idealDispatch.getFlows()[line][hour]);
									*//*
				}	
			}
		}
	}
		
	// congestion rents
	public void congestionRents(){
		for(int contract = 0; contract < Global.nContracts; contract++){
			for(int hour = 0; hour < 24; hour++){
				this.nCongestRents[contract][hour] =  
						(this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
						this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
						Math.min(this.generationContracts.get(contract).getContractPower()[hour],
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
				
				this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
						this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
						Math.min(this.generationContracts.get(contract).getContractPower()[hour],
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// congestion rents payed by the generation units:  model without FTRs
	public double[][] nFinalSetCongestionRentsGenerators(){
		this.nTransmissionSetByUnit = new double[Global.nUnits][24];
		try{
			for(int unit = 0; unit < Global.nUnits; unit++){
				this.nTransmissionSetUnits[unit] = new CongestRentUnit();
				for(int hour = 0; hour < 24; hour++){
					for(int contract = 0; contract < Global.nContracts; contract++){
						if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
							this.nTransmissionSetByUnit[unit][hour] =
									this.nTransmissionSetByUnit[unit][hour] +
									(1 - this.nCongestRentsProp) *
									((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
									this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
							
							this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							
							/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
									this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower(),
											Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
													*//*
						}
					}
				}
				this.nTransmissionSetUnits[unit].setCongestRentGen(this.nTransmissionSetByUnit[unit]);
				this.nTransmissionSetUnits[unit].setGenerationUnit(this.generationUnits.get(unit));
				//this.congestRentUnits[unit].printCongestRentGen();
			}
		}
		catch (Exception e) {
			System.out.println("operator: nFinalSetCongestionRentsGenerators ->"+e);
		}
		return this.nTransmissionSetByUnit;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator:  model without FTRs
	public double[][] nTansmissionSettlementGen(){
		// array to save the transmission settlement by generator
		this.nTransmissionSetByGen = new double[Global.nGencos][24];
		try{
			// determine the congestion rents by generator
			for(int gen = 0; gen < Global.nGencos; gen++){
				this.nTransmissionSetGens[gen] = new CongestRentGen();
				for(int hour = 0; hour < 24; hour++){
					for(int unit1 = 0; unit1 < this.generators.get(gen).getGenerationUnits().size(); unit1++){
						for(int unit = 0; unit < Global.nUnits; unit++){
							if(this.generators.get(gen).getGenerationUnits().get(unit1) == this.nTransmissionSetUnits[unit].getGenerationUnit())
							this.nTransmissionSetByGen[gen][hour] = nTransmissionSetByGen[gen][hour] + this.nTransmissionSetUnits[unit].getCongestRentGen()[hour];
						}
					}
				}
				this.nTransmissionSetGens[gen].setGenerator(this.generators.get(gen));
				this.nTransmissionSetGens[gen].setCongestRentGen(this.nTransmissionSetByGen[gen]);
				//this.transmissionSetGens[gen].printCongestRentGen();
			}			
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.nTransmissionSetByGen;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator: model without FTRs
	public double[][] nTransmissionSettlementDem(){
		this.nTransmissionSetByRet = new double[Global.nRetailers][24];
		try{
			for(int ret = 0; ret < Global.nRetailers; ret++){
				this.nTransmissionSetRets[ret] = new CongestRentRet();
				for(int hour = 0; hour < 24; hour++){
					for(int contract = 0; contract < Global.nContracts; contract++){
						if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
							this.nTransmissionSetByRet[ret][hour] =
									this.nTransmissionSetByRet[ret][hour] +
									this.nCongestRentsProp *
									((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
									this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
							
							this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							
							/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
									this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
									Math.min(this.generationContracts.get(contract).getContractPower(),
											Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
													*//*
						}
					}
				}
				this.nTransmissionSetRets[ret].setCongestRentRet(this.nTransmissionSetByUnit[ret]);
				this.nTransmissionSetRets[ret].setRetailer(this.retailers.get(ret));
				//this.congestRentUnits[unit].printCongestRentGen();
			}
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.nTransmissionSetByRet;
	}
	
	/* FTRS sort by price
	public List<List<Ftr>> ftrsSortByPrice(List<Ftr> ftrs){
		List<Ftr> auxFtrList = new ArrayList<Ftr>();
		Ftr[][] vec = new Ftr[2][3];
		String auxBidder = "";
		double auxPrice = 0.0;
		double auxPower = 0.0;
		int auxSourceNode = 0;
		int auxEndNode = 0;
		try {
			for(int hour = 0; hour < 24; hour++){
				auxFtrList.clear();
				for(int i = 0; i < ftrs.size(); i++){
					Ftr auxFtr1 = new Ftr(ftrs.get(i).getBidder(),
										  ftrs.get(i).getFtrPrice()[hour],
										  ftrs.get(i).getFtrPower()[hour], 
										  ftrs.get(i).getFtrSourceNode(),
										  ftrs.get(i).getFtrEndNode());
					/*this.auxFtr.setBidder(ftrs.get(i).getBidder());
					this.auxFtr.setHourlyFtrPrice(ftrs.get(i).getFtrPrice()[hour]);
					this.auxFtr.setHourlyFtrPower(ftrs.get(i).getFtrPower()[hour]);
					this.auxFtr.setFtrSourceNode(ftrs.get(i).getFtrSourceNode());
					this.auxFtr.setFtrEndNode(ftrs.get(i).getFtrEndNode());*/
					/*
					auxFtrList.add(auxFtr1);
					//break;
					vec[0][0] = auxFtr1;
					vec[0][1] = auxFtr1;					
				}
				this.listFtrs.add(auxFtrList);	
			}
			vec[0][0].printHourlyFtr(vec[0][0]);
			vec[0][1].printHourlyFtr(vec[0][1]);
			System.gc();
			for(int i = 0; i < listFtrs.size(); i++){
				for(int j = 0; j < listFtrs.get(i).size(); j++){
					for(int k = 0; k < listFtrs.get(i).size() - 1; k++){
						if(listFtrs.get(i).get(k).getHourlyFtrPrice() < listFtrs.get(i).get(k+1).getHourlyFtrPrice()){
							
							// change the bidder name
							auxBidder = listFtrs.get(i).get(k).getBidder();
							listFtrs.get(i).get(k).setBidder(listFtrs.get(i).get(k+1).getBidder());
							listFtrs.get(i).get(k+1).setBidder(auxBidder);
							
							// change the price
							auxPrice = listFtrs.get(i).get(k).getHourlyFtrPrice();
							listFtrs.get(i).get(k).setHourlyFtrPrice(listFtrs.get(i).get(k+1).getHourlyFtrPrice());
							listFtrs.get(i).get(k+1).setHourlyFtrPrice(auxPrice);
							
							// change the power
							auxPower = listFtrs.get(i).get(k).getHourlyFtrPower();
							listFtrs.get(i).get(k).setHourlyFtrPower(listFtrs.get(i).get(k+1).getHourlyFtrPower());
							listFtrs.get(i).get(k+1).setHourlyFtrPower(auxPower);
							
							// change the source node
							auxSourceNode = listFtrs.get(i).get(k).getFtrSourceNode();
							listFtrs.get(i).get(k).setFtrSourceNode(listFtrs.get(i).get(k+1).getFtrSourceNode());
							listFtrs.get(i).get(k+1).setFtrSourceNode(auxSourceNode);
							
							// change the end node
							auxEndNode = listFtrs.get(i).get(k).getFtrEndNode();
							listFtrs.get(i).get(k).setFtrEndNode(listFtrs.get(i).get(k+1).getFtrEndNode());
							listFtrs.get(i).get(k+1).setFtrEndNode(auxEndNode);
						}
						listFtrs.add(ftrs);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("ftrsSortByPrice ->"+e);
		}
		return listFtrs;
	}
	*/
	/*
	// FTRS sort by price
	public Ftr[][] ftrsSort(Ftr[] ftrs){
		try {
			// pass to daily FTRS to hourly FTRs
			for(int hour = 0; hour < 24; hour++){
				for(int i = 0; i < ftrs.length; i++){
					Ftr auxFtr1 = new Ftr(ftrs[i].getBidder(),
							ftrs[i].getFtrPower()[hour],
							ftrs[i].getFtrPrice()[hour],
							ftrs[i].getFtrSourceNode(),
							ftrs[i].getFtrEndNode());
					this.hourlyFtrBids[i][hour] = auxFtr1;					
				}
			}
			// call to garbage collector
			System.gc();
			
			// sort the array of hourly bids of FTRs
			for(int i = 0; i < this.hourlyFtrBids[1].length; i++){
				for(int j = 0; j < this.hourlyFtrBids.length; j++){
					for(int k = 0; k < this.hourlyFtrBids.length - 1; k++){
						if(this.hourlyFtrBids[k][i].getHourlyFtrPrice() < this.hourlyFtrBids[k+1][i].getHourlyFtrPrice()){
							// sort
							auxFtr = this.hourlyFtrBids[k][i];
							this.hourlyFtrBids[k][i] = this.hourlyFtrBids[k+1][i];
							this.hourlyFtrBids[k+1][i] = auxFtr;
						}
					}	
				}
			}
		} catch (Exception e) {
			System.out.println("ftrsSort ->"+e);
		}
		return hourlyFtrBids;
	}
	
	// assign the FTRs to generators and retailers
	public void ftrsAssign(Ftr[][] hourlyFtrBids, double capacityToAuctioning){
		for(int i = 0; i < this.hourlyFtrBids[0].length; i++){
			for(int j = 0; j < this.hourlyFtrBids.length; j++){
				if(this.hourlyFtrBids[j][i].getHourlyFtrPower() <= this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i]){
					this.assignHourlyFtrBids[j][i] = hourlyFtrBids[j][i];
					
					// actualize the remaining capacity for auction
					this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] = 
							this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] -
							this.hourlyFtrBids[j][i].getHourlyFtrPower();
				}
				else{
					this.assignHourlyFtrBids[j][i] = hourlyFtrBids[j][i];
					this.assignHourlyFtrBids[j][i].setHourlyFtrPower(Math.min(this.hourlyFtrBids[j][i].getHourlyFtrPower(), 
																			  this.transmissionMarket.getFtrAuction().getAuctionCapacity()[i]));
					this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] = 
							Math.max(0,  
									this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrPower()[i] -
									this.hourlyFtrBids[j][i].getHourlyFtrPower());
				}
				// actualize the values of initial date and duration of the FTR
				this.assignHourlyFtrBids[j][i].setFtrInitialDate(this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrInitialDate());
				this.assignHourlyFtrBids[j][i].setFtrDuration(this.transmissionMarket.getFtrAuction().gerProductForAuction().getFtrDuration());
			
			}
		}
	}
	
	// calculate the hourly demand for FTRs
	public double calculateHourlyFtrDemand(Ftr[] ftrs){
		try{
			this.hourlyFtrsDemand = 0.0;
			for(int i = 0; i< this.ftrBids.length; i++){
				this.hourlyFtrsDemand = this.hourlyFtrsDemand + this.ftrBids[i].getHourlyFtrPower();
			}	
				
		}
		catch (Exception e) {
			System.out.println("calculateHourlyFtrDemand ->"+e);
		}
		return this.hourlyFtrsDemand;
	}
		
	//----------------------------------------------------------------------------------------------------------------------------
	// determine the reserve price of the operator
	public double calculateFtrReservePrice(int iteration, int hour, int round){
		double reservePrice = 0.0;
		try{
			if (round == 0){	
				for(int contract = 0; contract < Global.nContracts; contract++){
					for(int i = 0; i < Global.nlags; i++){
						this.auxNodalPricesDif[i] = this.historicalNodalPrices.get(this.generationContracts.get(contract).getWithdrawalNode()-1)[iteration- i][hour] - 
								this.historicalNodalPrices.get(this.generationContracts.get(contract).getSourceNode()-1)[iteration - i][hour];
					}
					// ftrReservePrice is equal to the mean of last Global.nlags nodal price differences between the withdrawal node and source node in each contract 
					this.auxReservePriceFrtAuction[contract] = Global.MathFun.Mean(this.auxNodalPricesDif);
				}
				reservePrice = Global.MathFun.Mean(auxReservePriceFrtAuction);
				//this.productPrice = Global.MathFun.Mean(auxReservePriceFrtAuction);
			}
			else {
				//reservePrice = 3.0;
				reservePrice = this.productPrice  + this.percentPriceIncrement*this.productPrice;
				//this.productPrice  = this.productPrice  + this.percentPriceIncrement*this.productPrice ;
			}
		}
		catch(Exception e)
		{
            System.out.println("operator: calculateFtrReservePrice ->"+e);
        }
		//return this.productPrice ;
		return reservePrice;
	}
	
	// FALTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	// organizar el calculo de la capacidad a subastar como el promedio de las demandas
	//---------------------------------------------------------------------------------------------------------------------------------------
	// define the transmission capacity to auction
	public double[] ftrAuctionCapacity(){
		double[] auxAuctionCapacity = new double[24];
		try{
			for(int hour = 0; hour < 24; hour++){
				auxAuctionCapacity[hour] = 410;
			}
		}
		catch (Exception e) {
			System.out.println("operator: ftrAuctionCapacity ->"+e);
		}
		return auxAuctionCapacity;
	}
	
	
	// define the product to auction
	public Ftr productToAuction(String operator, double power, double price, int initialDate, int duration){
		Ftr productToAuction = new Ftr(operator,power,price,initialDate,duration);
		return productToAuction;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// assign the hourly bids of FTRS
	public Ftr[] hourlyAssignFtrs(int hour, Ftr[] ftrs, double hourlyFtrAcutionCapacity, double hourlyFtrPrice,
			int ftrInitialDate, int ftrDuration){
		try{
			this.hourlyAssignFtrs = ftrs;
			for(int ftr = 0; ftr < ftrs.length; ftr++){
				this.hourlyAssignFtrs[ftr].setHourlyFtrPower(Math.min(this.hourlyAssignFtrs[ftr].getHourlyFtrPower(),
															hourlyFtrAcutionCapacity));
				/*if(ftrs[ftr].getGenerationUnit() != null){
					this.hourlyAssignFtrs[ftr].setGenerationUnit(ftrs[ftr].getGenerationUnit());
				}*//*
				this.hourlyAssignFtrs[ftr].setHourlyFtrPrice(hourlyFtrPrice);
				this.hourlyAssignFtrs[ftr].setFtrInitialDate(ftrInitialDate);
				this.hourlyAssignFtrs[ftr].setFtrDuration(ftrDuration);
				
				hourlyFtrAcutionCapacity = hourlyFtrAcutionCapacity -
											Math.min(this.hourlyAssignFtrs[ftr].getHourlyFtrPower(),
													 hourlyFtrAcutionCapacity);
				
				this.hourlyAssignFtrsAuction[ftr][hour] = this.hourlyAssignFtrs[ftr];
			}
		}
		catch (Exception e) {
			System.out.println("operator: hourlyAssignFtrs ->"+e);
		}
		return this.hourlyAssignFtrs;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// auction income by each assigned FTR by each hour
	public double[] hourlyAuctionIncome(int hour, Ftr[] ftrs){
		this.hourlyAuctionIncome[hour] = 0.0;
		try{
			for(int ftr = 0; ftr < ftrs.length; ftr++){
				this.auctionIncome[ftr][hour] = ftrs[ftr].getHourlyFtrPower() * 
												ftrs[ftr].getHourlyFtrPrice() *
												ftrs[ftr].getFtrDuration();
				this.hourlyAuctionIncome[hour] = this.hourlyAuctionIncome[hour] + this.auctionIncome[ftr][hour];
			}
		}
		catch (Exception e) {
			System.out.println("operator: hourlyAuctionIncome ->"+e);
		}
		return this.hourlyAuctionIncome;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// settlement of auction income with transmitters
	public double[][] auctionIncomeTransmitters(int iteration, double[] hourlyAuctionIncome, double[][] flows){
		double[] sumFlows = new double[24];
		this.hourlyAuctionIncomeTransmitters = this.nGridcosIncome;
		try{
			if(this.auctionIndex >= 1){
				if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 1]));
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyAuctionIncomeTransmitters[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyAuctionIncome[hour]/30);
						}
					}
				}
				// control of auction date 
				else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 2]));
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyAuctionIncomeTransmitters[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyAuctionIncome[hour]/30);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("operator: auctionIncomeTransmitters ->"+e);
		}
		return this.hourlyAuctionIncomeTransmitters;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by generator
	public double[][] tansmissionSettlementGen(int iteration){
		// array to save the transmission settlement by generator
		this.transmissionSetByGen = new double[Global.nGencos][24];
		try{
			// determine the congestion rents by generator
			for(int gen = 0; gen < Global.nGencos; gen++){
				this.transmissionSetGens[gen] = new CongestRentGen();
				for(int hour = 0; hour < 24; hour++){
					for(int unit1 = 0; unit1 < this.generators.get(gen).getGenerationUnits().size(); unit1++){
						for(int unit = 0; unit < Global.nUnits; unit++){
							if(this.generators.get(gen).getGenerationUnits().get(unit1) == this.transmissionSetUnits[unit].getGenerationUnit())
							this.transmissionSetByGen[gen][hour] = transmissionSetByGen[gen][hour] + this.transmissionSetUnits[unit].getCongestRentGen()[hour];
						}
					}
				}
				this.transmissionSetGens[gen].setGenerator(this.generators.get(gen));
				this.transmissionSetGens[gen].setCongestRentGen(this.transmissionSetByGen[gen]);
				//this.transmissionSetGens[gen].printCongestRentGen();
			}			
		}
		catch (Exception e) {
			System.out.println("operator: tansmissionSettlementGen ->"+e);
		}
		return this.transmissionSetByGen;
	}
			
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by unit
	public double[][] finalSetCongestionRentsGenerators(int iteration, Ftr[][] hourlyAssignFtrsAuction){
		try{
			// array to save the transmission settlement by generation unit
			this.transmissionSetByUnit = new double[Global.nUnits][24];
			for(int unit = 0; unit < Global.nUnits; unit++){
				this.transmissionSetUnits[unit] = new CongestRentUnit();
				if(this.auctionIndex >= 1){
					if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContracts().size(); contract++){
								for(int ftr = 0; ftr < Global.nContracts; ftr++){
									if(this.generationUnits.get(unit).getGenerationContracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){							
										this.transmissionSetByUnit[unit][hour] =
												this.transmissionSetByUnit[unit][hour] +
												
												// payments by congestion rents - power in FTRs
												(1 - this.nCongestRentsProp) *
												((this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour]);
										
										this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour],
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.generationUnits.get(unit).getGenerationContracts().size(); contract++){
								for(int ftr = 0; ftr < Global.nContracts; ftr++){
									if(this.generationUnits.get(unit).getGenerationContracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){							
										this.transmissionSetByUnit[unit][hour] =
												this.transmissionSetByUnit[unit][hour] +
												(1 - this.nCongestRentsProp) *
												((this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.generationUnits.get(unit).getGenerationContracts().get(contract).getSourceNode()-1][hour]);
										
										this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractPower()[hour],
														this.generationUnits.get(unit).getGenerationContracts().get(contract).getContractBuyer().getPowerDemand()[hour]);
									}
								}							
							}
						}
					}
					else {
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < Global.nContracts; contract++){
								if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
							
									this.transmissionSetByUnit[unit][hour] =
											this.transmissionSetByUnit[unit][hour] +
											(1 - this.nCongestRentsProp) *
											((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
											this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
									
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
								}
							}
						}
					}
				}
				else {
					for(int hour = 0; hour < 24; hour++){
						for(int contract = 0; contract < Global.nContracts; contract++){
							if(this.generationUnits.get(unit) == this.generationContracts.get(contract).getGenerationUnit()){
						
								this.transmissionSetByUnit[unit][hour] =
										this.transmissionSetByUnit[unit][hour] +
										(1 - this.nCongestRentsProp) *
										((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
										this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
								
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
										this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
							}
						}
					}
				}
				this.transmissionSetUnits[unit].setCongestRentGen(this.transmissionSetByUnit[unit]);
				this.transmissionSetUnits[unit].setGenerationUnit(this.generationUnits.get(unit));
				//this.transmissionSetUnits[unit].printCongestRentUnit();
			}
		}
		catch (Exception e) {
			System.out.println("operator: finalSetCongestionRentsGenerators ->"+e);
		}
		return this.transmissionSetByUnit;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	// transmission settlement by retailer
	public double[][] transmissionSettlementDem(int iteration, Ftr[][] hourlyAssignFtrsAuction){
		try{
			// array to save the transmission settlement by retailer
			this.transmissionSetByRet = new double[Global.nRetailers][24];
			for(int ret = 0; ret < Global.nRetailers; ret++){
				this.transmissionSetRets[ret] = new CongestRentRet();
				if(this.auctionIndex >= 1){
					if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.retailers.get(ret).getGenerationConstracts().size(); contract++){
								for(int ftr = Global.nContracts; ftr < 2*Global.nContracts; ftr++){
									if(this.retailers.get(ret).getGenerationConstracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){
										this.transmissionSetByRet[ret][hour] =
												this.transmissionSetByRet[ret][hour] +
												
												// payments by congestion rents - power in FTRs
												this.nCongestRentsProp *
												((this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour]);
										
										this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour],
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < this.retailers.get(ret).getGenerationConstracts().size(); contract++){
								for(int ftr = Global.nContracts; ftr < 2*Global.nContracts; ftr++){
									if(this.retailers.get(ret).getGenerationConstracts().get(contract) == hourlyAssignFtrsAuction[ftr][hour].getGenerationContract()){
										this.transmissionSetByRet[ret][hour] =
												this.transmissionSetByRet[ret][hour] +
												
												// payments by congestion rents - power in FTRs
												this.nCongestRentsProp *
												((this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
												this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour] ) * 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour] - 
														hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower(),
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]))
												
												// payments by FTR
												+ hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() * hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPrice()
												
												// payments received by FTR
												- hourlyAssignFtrsAuction[ftr][hour].getHourlyFtrPower() *
														(this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getWithdrawalNode()-1][hour] -
														this.realDispatch.getNodalPrices()[this.retailers.get(ret).getGenerationConstracts().get(contract).getSourceNode()-1][hour]);
										
										this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour] = 
												this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]- 
												Math.min(this.retailers.get(ret).getGenerationConstracts().get(contract).getContractPower()[hour],
														this.retailers.get(ret).getGenerationConstracts().get(contract).getContractBuyer().getPowerDemand()[hour]);	
									}
								}							
							}
						}
					}
					else {
						for(int hour = 0; hour < 24; hour++){
							for(int contract = 0; contract < Global.nContracts; contract++){
								if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
									this.transmissionSetByRet[ret][hour] =
											this.transmissionSetByRet[ret][hour] +
											this.nCongestRentsProp *
											((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
											this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
													this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
									
									this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
											this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower()[hour],
															this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
									
									/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
											this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
											Math.min(this.generationContracts.get(contract).getContractPower(),
													Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
															this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
															*//*
								}
							}
						}
					}
				}
				else {
					for(int hour = 0; hour < 24; hour++){
						for(int contract = 0; contract < Global.nContracts; contract++){
							if(this.retailers.get(ret) == this.generationContracts.get(contract).getContractBuyer()){
								this.transmissionSetByRet[ret][hour] =
										this.transmissionSetByRet[ret][hour] +
										this.nCongestRentsProp *
										((this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getWithdrawalNode()-1][hour] -
										this.realDispatch.getNodalPrices()[this.generationContracts.get(contract).getSourceNode()-1][hour] ) * 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
												this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
								
								this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour] = 
										this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower()[hour],
														this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]);
								
								/*this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour] = 
										this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour]- 
										Math.min(this.generationContracts.get(contract).getContractPower(),
												Math.min(this.generationContracts.get(contract).getGenerationUnit().getRealGeneration()[hour],
														this.generationContracts.get(contract).getContractBuyer().getPowerDemand()[hour]));
														*//*
							}
						}
					}
				}
				this.transmissionSetRets[ret].setCongestRentRet(this.transmissionSetByRet[ret]);
				this.transmissionSetRets[ret].setRetailer(this.retailers.get(ret));
				//this.transmissionSetUnits[unit].printCongestRentUnit();
			}
		}
		catch (Exception e) {
			System.out.println("operator: transmissionSettlementDem ->"+e);
		}
		return this.transmissionSetByRet;
	}

	// transmission settlement: auction income + congestion rents
	public double[][] finalTransmissionSettlement(int iteration, double [][] flows, double[][] settlmentGenration, double[][] setllementDemand)
	{
		this.hourlyTransmissionSettlement = this.nGridcosIncome;
		
		double[] generationPayments = new double[24];
		double[] demandPayments = new double[24];		
		double[] sumFlows = new double[24];
		
		try{
			if(this.auctionIndex >= 1){
				if(iteration > this.ftrAuctionDate[this.auctionIndex - 1] && iteration <= this.ftrAuctionDate[this.auctionIndex - 1] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 1]));
					
					// total power flow in the system
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					
					// sum by hour of generation and demand payments
					generationPayments = Global.MathFun.SumByColumns(settlmentGenration);
					demandPayments = Global.MathFun.SumByColumns(setllementDemand);
					
					// income total for transmission
					for(int hour = 0; hour < 24; hour++){
						this.hourlyTransmissionIncome[hour] = generationPayments[hour] + demandPayments[hour];
					}
					
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyTransmissionSettlement[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyTransmissionIncome[hour]);
						}
					}
				}
				// control of auction date 
				else if (this.auctionIndex >= 2 && iteration <= this.ftrAuctionDate[this.auctionIndex - 2] + 30){
					this.hourlyAuctionIncomeTransmitters = new double[Global.nLines][24];
					System.out.println("auction\t" + this.auctionIndex + "\tsettlement period\t" + (iteration - this.ftrAuctionDate[this.auctionIndex - 2]));

					// total power flow in the system
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							sumFlows[hour] = sumFlows[hour] + Math.abs(flows[line][hour]);
						}
					}
					
					// sum by hour of generation and demand payments
					generationPayments = Global.MathFun.SumByColumns(settlmentGenration);
					demandPayments = Global.MathFun.SumByColumns(setllementDemand);
					
					// income total for transmission
					for(int hour = 0; hour < 24; hour++){
						this.hourlyTransmissionIncome[hour] = generationPayments[hour] + demandPayments[hour];
					}
					
					for(int line = 0; line < Global.nLines; line++){
						for(int hour = 0; hour < 24; hour++){
							this.hourlyTransmissionSettlement[line][hour] = (Math.abs(flows[line][hour])/sumFlows[hour])*
																			   (hourlyTransmissionIncome[hour]);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("operator: transmissionSettlement ->"+e);
		}
		return this.hourlyTransmissionSettlement;
	}*/
	
	// si ya se realizaron subastas
	if(auctionIndex >= 1)
	{
	
	if(iteration > Global.ftrAuctionDate[auctionIndex-1]
							&& iteration <= Global.ftrAuctionDate[auctionIndex])
	{