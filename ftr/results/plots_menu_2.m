% menu
disp('Menu interno');
disp('1. volver al menu principal');
disp('2. salir');

option = input('Ingrese su opci�n:');

switch option
    case 1
        plots;
    case 2
        break;
    otherwise
        disp('opci�n incorrecta');
        plots_menu_2;
end