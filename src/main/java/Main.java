/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author deepa
 */
import version.control.Service;

public class Main {

    public static void main(String[] args) {
        final String REPOPATHS_LOCATION = "D:\\vcs\\repos.vcs";
        Service service = Service.createServiceInstance(REPOPATHS_LOCATION);
    }

}
