function plots_auction_features
    
    xlimu = [1 23];
    rot = 90;
    xTickLocations = xlimu(1):1:xlimu(2);
    numberOfXTickMarks = length(xTickLocations);
    xTickString = {'ene-10', 'feb-10', 'mar-10', 'abr-10', 'may-10', 'jun-10', 'jul-10', 'ago-10', 'sep-10', 'oct-10', 'nov-10', 'dic-10',... 
               'ene-11', 'feb-11', 'mar-11', 'abr-11', 'may-11', 'jun-11', 'jul-11', 'ago-11', 'sep-11', 'oct-11', 'nov-11'};
    
    auctionFeatures = importdata('z.auctionFeatures.csv', ',');
    
    numFtrs = auctionFeatures(:,2);
    auctionIncome = sum(auctionFeatures(:,26:size(auctionFeatures,2)),2);
    x = (1:1:23);
    
    subplot(2,2,1:2)
    bar(x,numFtrs,'b');
    set(gca,'XTickLabel',[]);
    hold on
    plotyy(NaN,NaN,x,auctionIncome)
    
    set(gca,'XTickLabel',[]);
    hold off
    xl = xlim();
    xTickLocations = linspace(xl(1), xl(2),numberOfXTickMarks);
    set(gca,'XTick', xTickLocations);
    b = get(gca,'XTick');
    set(gca,'XTickLabel', xTickString);
    a = get(gca,'XTickLabel');
    set(gca,'XTickLabel',[]);
    c = get(gca,'YTick');
    text(b,repmat(c(1)-.4*(c(2)-c(1)),numberOfXTickMarks,1),a,'HorizontalAlignment','center','rotation',rot,'FontSize', 10, 'FontWeight','Bold');
    set(gca,'FontWeight','bold','FontSize',10);
    hold off 
    
    subplot(2,2,3)
    plot(x,auctionIncome)
    
    subplot(2,2,4)
    plot(x,auctionIncome)    
end