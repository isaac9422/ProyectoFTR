function plots_com_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot)

    unRetailersSettlementEnergyMarketCOP = importdata('unRetailersSettlementEnergyMarketCOP.csv', ',');
    nodRetailersSettlementEnergyMarketCOP = importdata('nodRetailersSettlementEnergyMarketCOP.csv',',');
    ftrRetailersSettlementEnergyMarketCOP = importdata('ftrRetailersSettlementEnergyMarketCOP.csv',',');

    for i = 1:24
        figure(i)
        plot(unRetailersSettlementEnergyMarketCOP.data(:,i), '*r', 'LineWidth',1.0);
        hold on
        plot(ftrRetailersSettlementEnergyMarketCOP.data(:,i), '-', 'Color',[0.6,0.6,0.6], 'LineWidth',1.0);
        plot(nodRetailersSettlementEnergyMarketCOP.data(:,i), '.k', 'LineWidth',1.0);
        title('Liquidación total comercializadores', 'FontSize', 12, 'FontWeight','Bold');
        legend('M. uninodal','M. nodal','M. ftrs','Location','Best');
        ylabel('Liquidación (COP)', 'FontSize', 12, 'FontWeight','Bold');
        %xlabel('Fecha','FontSize', 12, 'FontWeight','Bold');
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
        saveas(fig,strcat('plot liq com hora-',num2str(i)),'pdf');
    end
    close all
end
