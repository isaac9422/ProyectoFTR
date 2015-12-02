function plots_ftr(xlimu,numberOfXTickMarks,xTickString,rot)

    ftrGenerationUnitsSettlementEnergyMarketCOP = importdata('ftrGenerationUnitsSettlementEnergyMarketCOP.csv', ',');
    ftrRetailersSettlementEnergyMarketCOP = importdata('ftrRetailersSettlementEnergyMarketCOP.csv',',');
    ftrTransmittersSettlementUsageChargesCOP = importdata('ftrTransmittersSettlementUsageChargesCOP.csv',',');

    for i = 1:24
        figure(i)
        plot(ftrGenerationUnitsSettlementEnergyMarketCOP.data(:,i), '-r', 'LineWidth',1.0);
        hold on
        plot(ftrTransmittersSettlementUsageChargesCOP.data(:,i), '-', 'Color',[0.6,0.6,0.6], 'LineWidth',1.0);
        plot(ftrRetailersSettlementEnergyMarketCOP.data(:,i), '-k', 'LineWidth',1.0);
        title('Liquidación total del mercado nodal con ftrs', 'FontSize', 12, 'FontWeight','Bold');
        legend('Generadores','Comercializadores','Transmisores','Location','Best');
        %xlabel('Fecha','FontSize', 12, 'FontWeight','Bold');
        ylabel('Liquidación (COP)', 'FontSize', 12, 'FontWeight','Bold');
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
        text(b,repmat(c(1)-.6*(c(2)-c(1)),numberOfXTickMarks,1),a,'HorizontalAlignment','center','rotation',rot,'FontSize', 10, 'FontWeight','Bold');
        set(gca,'FontWeight','bold','FontSize',10);
        hold off       

        fig = figure(i);
        saveas(fig,strcat('plot ftr liquidación hora-',num2str(i)),'pdf');
    end
    close all
end
