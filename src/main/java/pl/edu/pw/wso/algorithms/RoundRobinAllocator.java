package pl.edu.pw.wso.algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

public class RoundRobinAllocator {

    public static void allocate(DatacenterBroker broker, List<Cloudlet> cloudlets, List<Vm> vms) {
        System.out.println("Rozpoczynam mapowanie zadań algorytmem Round Robin...");
        
        int vmIndex = 0;
        int vmCount = vms.size();

        for (Cloudlet task : cloudlets) {
            Vm vm = vms.get(vmIndex);
            
            // Przypisanie zadania do maszyny
            broker.bindCloudletToVm(task.getCloudletId(), vm.getId());
            
            // Przejście do następnej maszyny (gdy dojdzie do końca listy, wraca do 0)
            vmIndex = (vmIndex + 1) % vmCount;
        }
        
        System.out.println("Zakończono mapowanie Round Robin.");
    }
}