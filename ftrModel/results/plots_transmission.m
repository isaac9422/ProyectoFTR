function plots_transmission(xlimu,numberOfXTickMarks,xTickString,rot)
    
    unTransmittersSettlementUsageChargesCOP = importdata('unTransmittersSettlementUsageChargesCOP.csv', ',');
    nodTransmittersSettlementUsageChargesCOP = importdata('nodTransmittersSettlementUsageChargesCOP.csv',',');
    ftrTransmittersSettlementUsageChargesCOP = importdata('ftrTransmittersSettlementUsageChargesCOP.csv',',');
    
%     unsum2010 = sum(sum(unTransmittersSettlementUsageChargesCOP.data(1:365,:)))
%     unsum2011 = sum(sum(unTransmittersSettlementUsageChargesCOP.data(366:730,:)))
%     nodsum2010 = sum(sum(nodTransmittersSettlementUsageChargesCOP.data(1:365,:)))
%     nodsum2011 = sum(sum(nodTransmittersSettlementUsageChargesCOP.data(366:730,:)))
%     ftrsum2010 = sum(sum(ftrTransmittersSettlementUsageChargesCOP.data(1:365,:)))
%     ftrum2011 = sum(sum(ftrTransmittersSettlementUsageChargesCOP.data(366:730,:)))
   
    unTransmittersSettlementUsageChargesCOP = mean(unTransmittersSettlementUsageChargesCOP.data,2);
    nodTransmittersSettlementUsageChargesCOP = mean(nodTransmittersSettlementUsageChargesCOP.data,2);
    ftrTransmittersSettlementUsageChargesCOP = mean(ftrTransmittersSettlementUsageChargesCOP.data,2);
    
    %figure(1)
    plot(unTransmittersSettlementUsageChargesCOP, '*r', 'LineWidth',1.0);
    hold on  
    plot(nodTransmittersSettlementUsageChargesCOP, '.k', 'LineWidth',1.0);
    plot(ftrTransmittersSettlementUsageChargesCOP, '-', 'Color',[0.6,0.6,0.6], 'LineWidth',1.0);
    
    %title('Liquidación total generadores', 'FontSize', 12, 'FontWeight','Bold');
    legend('M. uninodal','M. nodal','M. ftrs','Location','Best');
    %xlabel('Fecha','FontSize', 10, 'FontWeight','Bold');
    ylabel('Liquidación (COP)', 'FontSize', 10, 'FontWeight','Bold');
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
    saveas(gcf,'z. plot liquidación transmisión','pdf');
    
    %close all
end