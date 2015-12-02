function plots

    % variables auxiliares para formato de las gráficas
%     nodesNames = {'ANTIOQUI',           'ATLANTIC',	'BOGOTA',	'BOLIVAR',	...
%                     'CAUCANAR',         'CERROMAT',	'CHIVOR',	'CORDOSUC',	...
%                     'CQR',              'GCM',      'HUILACAQ',	'LAMIEL',	...
%                     'MAGDAMED',         'META',		'NORDESTE', 'PAGUA',	...
%                     'SANCARLO',         'TOLIMA',   'VALLECAU', 'COROZO',   ...
%                     'CUATRICENTENARIO', 'ECUADOR220'};
                
    nodesNames = {'ANTIOQUI',           'ATLANTIC',	'BOGOTA',	'BOLIVAR',	...
                    'CAUCANAR',         'CERROMAT',	'CHIVOR',	'CORDOSUC',	...
                    'CQR',              'GCM',      'HUILACAQ',	'LAMIEL',	...
                    'MAGDAMED',         'META',		'NORDESTE', 'PAGUA',	...
                    'SANCARLO',         'TOLIMA',   'VALLECAU', 'COROZO',   ...
                    'CUATRICENTENARIO', 'ECUADOR220', 'PANAMA'};    
    xlimu = [1 366];
    xlimu1 = [1 24];
    rot = 90;
    nnodos = 22;
    [num txt]= xlsread('plot_dates.xls');
    xTickLocations = xlimu(1):30:xlimu(2);
    numberOfXTickMarks = length(xTickLocations);
    xTickString = {''};
    for k = 1 : length(xTickLocations);
        xTickString(k) = txt(xTickLocations(k));
    end
    xTickString = datenum(xTickString,'dd/mm/yyyy');
    xTickString = datestr(xTickString,'mm/yy');

    % menú
    disp('Menú gráficas');
    disp('1. gráfico liquidación total mercado uninodal');
    disp('2. gráfico liquidación total mercado nodal');
    disp('3. gráfico liquidación total mercado nodal con ftrs');
    disp('4. liquidación trasnmisión mercado uninodal vs nodal vs nodal con ftrs');
    disp('5. liquidación generación mercado uninodal vs nodal vs nodal con ftrs');
    disp('6. liquidación comercialización mercado uninodal vs nodal vs nodal con ftrs');
    disp('7. todos');
    disp('8. precios nodales vs spot');
    disp('9. características de las subastas');
    disp('10. liquidación generación');
    disp('11. liquidación transmisión');
    disp('12. liquidación congestión');
    disp('13. salir');
  
    % lectura de opción
    option = input('Ingrese su opción:');

    switch option
        case 1
            plots_uni(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 2
            plots_nod(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 3
            plots_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 4
            plots_trans_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 5
            plots_gen_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 6
            plots_com_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2; 
        case 7
            plots_uni(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_nod(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_trans_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_gen_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_com_uni_nod_ftr(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 8
            plots_prices(xlimu1,numberOfXTickMarks,xTickString,rot,nnodos,nodesNames);
            plots_menu_2;
        case 9            
            plots_auction_features(xlimu,numberOfXTickMarks,xTickString,rot,nnodos,nodesNames);
            plots_menu_2;
        case 10            
            plots_gen_liquidacion(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 11            
            plots_transmission(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 12
            plots_congestion(xlimu,numberOfXTickMarks,xTickString,rot);
            plots_menu_2;
        case 13            
            return;
        otherwise
            disp('opción incorrecta');
            plots_menu_2;
    end
end