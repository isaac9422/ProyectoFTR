function plots_prices(xlimu,numberOfXTickMarks,xTickString,rot,nnodos,nodesNames)
    
    nodalPrices = importdata('z.nodalPrices.csv', ',');
    spotPrices = importdata('z.spotPrices.csv',',');
    
    nodalPrices2010 = mean(nodalPrices(1:365,:));
    nodalPrices2010 = vec2mat(nodalPrices2010,24);
    nodalPrices2011 = mean(nodalPrices(366:730,:));
    nodalPrices2011 = vec2mat(nodalPrices2011,24);
    
    spotPrices2010 = mean(spotPrices(1:365,:));
    spotPrices2011 = mean(spotPrices(366:730,:));
    
    %figure(1)
    set(gcf,'Position',[0 0 1280 720]);
    set(gcf,'PaperSize',[12.8 7.2]);
    set(gca,'Position',[0 0 1280 720]);
    for i = 1:nnodos
        if(i == 21)
            subplot(6,4,22)
        else if(i == 22)
                subplot(6,4,23)
            else
                subplot(6,4,i)
            end
        end
         ylim1 = [0.9*min([min(spotPrices2010),min(spotPrices2011),min(nodalPrices2010(i,:)),min(nodalPrices2011(i,:))]), ...
             1.1*max([max(spotPrices2010),max(spotPrices2011),max(nodalPrices2010(i,:)),max(nodalPrices2011(i,:))])];
         plot (spotPrices2010, '.r', 'LineWidth',1.0);
         hold on
         plot (spotPrices2011, '*b', 'LineWidth',1.0);
         plot (nodalPrices2010(i,:), '-k', 'LineWidth',1.0);
         plot (nodalPrices2011(i,:), '-','Color',[0.6,0.6,0.6],'LineWidth',1.0);
         hold off
         title(nodesNames(i), 'FontSize', 8, 'FontWeight','Bold');
         xlabel('Hora','FontSize', 8, 'FontWeight','Bold');
         ylabel('Precio ($/MWh)', 'FontSize', 8, 'FontWeight','Bold');
         xlim(xlimu);
         ylim(ylim1);
         %set(gca,'Position',[0 0 1200 1000]);
         
         %set(gcf,'PaperSize',[50 25]);
         %set(gca,'xtick',[]);

%         plot(unTransmittersSettlementUsageChargesCOP.data(:,i), '-k', 'LineWidth',1.0);
%         hold on
%         plot(nodTransmittersSettlementUsageChargesCOP.data(:,i), '-r', 'LineWidth',1.0);
%         plot(ftrTransmittersSettlementUsageChargesCOP.data(:,i), '-b', 'LineWidth',1.0);
%         title('Liquidación total de la transmisión', 'FontSize', 12, 'FontWeight','Bold');
%         legend('M. uninodal','M. nodal','M. ftrs','Location','Best');
%         %xlabel('Fecha','FontSize', 12, 'FontWeight','Bold');
%         ylabel('Liquidación (COP)', 'FontSize', 12, 'FontWeight','Bold');
%         set(gcf,'PaperPosition',[0 0 10 5]);
%         set(gcf,'PaperSize',[10 5]);
%         xlim(xlimu);
%         xl = xlim();
%         xTickLocations = linspace(xl(1), xl(2), numberOfXTickMarks);
%         set(gca,'XTick', xTickLocations);
%         b = get(gca,'XTick');
%         set(gca,'XTickLabel', xTickString);
%         a = get(gca,'XTickLabel');
%         set(gca,'XTickLabel',[]);
%         c = get(gca,'YTick');
%         text(b,repmat(c(1)-.6*(c(2)-c(1)),numberOfXTickMarks,1),a,'HorizontalAlignment','center','rotation',rot,'FontSize', 10, 'FontWeight','Bold');
%         set(gca,'FontWeight','bold','FontSize',10);
%         hold off       
% 
%         fig = figure(i);
%         saveas(fig,strcat('plot tra liquidación hora-',num2str(i)),'jpg');
    end
    legend('Precio spot 2010 (MC)','Precio spot 2011 (MC)','Precio nodal 2010 (MP)','Precio nodal 2011 (MP)','Location','Best');
    %set(gca,'PaperSize',[1200 1000]);
    
    %fig = figure(1);  
    %saveas(gca,'plot nodal prices','pdf');
    
    % Reset the bottom subplot to have xticks
    %set(gca,'xtickMode', 'auto')
end