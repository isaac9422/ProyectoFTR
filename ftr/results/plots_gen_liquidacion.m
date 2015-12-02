function plots_gen_liquidacion(xlimu,numberOfXTickMarks,xTickString,rot)

    unGenerationUnitsSettlementEnergyMarketCOP = importdata('unGenerationUnitsSettlementEnergyMarketCOP.csv', ',');
    nodGenerationUnitsSettlementEnergyMarketCOP = importdata('nodGenerationUnitsSettlementEnergyMarketCOP.csv',',');
    ftrGenerationUnitsSettlementEnergyMarketCOP = importdata('ftrGenerationUnitsSettlementEnergyMarketCOP.csv',',');
    
    unGenerationUnitsSettlementEnergyMarketCOP = mean(unGenerationUnitsSettlementEnergyMarketCOP.data,2);
    nodGenerationUnitsSettlementEnergyMarketCOP = mean(nodGenerationUnitsSettlementEnergyMarketCOP.data,2);
    ftrGenerationUnitsSettlementEnergyMarketCOP = mean(ftrGenerationUnitsSettlementEnergyMarketCOP.data,2);
    
    %figure(1)
    plot(unGenerationUnitsSettlementEnergyMarketCOP, '*r', 'LineWidth',1.0);
    hold on  
    plot(nodGenerationUnitsSettlementEnergyMarketCOP, '.k', 'LineWidth',1.0);
    plot(ftrGenerationUnitsSettlementEnergyMarketCOP, '-', 'Color',[0.6,0.6,0.6], 'LineWidth',1.0);
    
    %title('Liquidaci�n total generadores', 'FontSize', 12, 'FontWeight','Bold');
    legend('M. uninodal','M. nodal','M. ftrs','Location','Best');
    %xlabel('Fecha','FontSize', 10, 'FontWeight','Bold');
    ylabel('Liquidaci�n (COP)', 'FontSize', 10, 'FontWeight','Bold');
    set(gcf,'PaperPosition',[0 0 10 5]);
    set(gcf,'PaperSize',[10 5]);
    xlim(xlimu);
    xl = xlim();
    xTickLocations = linspace(xl(1), xl(2), numberOfXTickMarks);
    set(gca,'XTick', xTickLocations);
    b = get(gca,'XTick');
    set(gca,'XTickLabel', xTickString);
    a = get(gca,'XTickLabel');
    set(gca,'XTickLabel',[]);
    c = get(gca,'YTick');
    text(b,repmat(c(1)-.4*(c(2)-c(1)),numberOfXTickMarks,1),a,'HorizontalAlignment','center','rotation',rot,'FontSize', 10, 'FontWeight','Bold');
    set(gca,'FontWeight','bold','FontSize',10);
    hold off       

    %fig = figure(1);
    saveas(gcf,'z. plot liquidaci�n generaci�n','pdf');
    
    %close all
end