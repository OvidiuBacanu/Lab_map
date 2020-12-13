package socialnetwork.ui;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UtilizatorService;

import java.util.Scanner;

public class UI {
    private UI_Admin ui_admin;
    private UI_Utilizator ui_utilizator;
    Scanner in=new Scanner(System.in);

    public UI(UI_Admin ui_admin, UI_Utilizator ui_utilizator) {
        this.ui_admin = ui_admin;
        this.ui_utilizator = ui_utilizator;
    }

    public void show_meniu(){
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.println("x. Exit");
    }

    public void run(){
        while (true){
            String cmd;
            show_meniu();
            System.out.print("\nDati comanda: ");
            cmd=in.nextLine();
            if(cmd.equals("1"))
                ui_admin.run();
            if(cmd.equals("2"))
                ui_utilizator.run();
            if(cmd.equals("x"))
                break;
        }
    }
}
