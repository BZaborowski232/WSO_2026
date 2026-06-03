package pl.edu.pw.wso.algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.List;

public class MaxMinAllocator {

    public static void allocate(DatacenterBroker broker, List<Cloudlet> cloudlets, List<Vm> vms) {
        List<Cloudlet> unassigned = new ArrayList<>(cloudlets);
        double[] vmReadyTime = new double[vms.size()];

        System.out.println("Rozpoczynam mapowanie zadań algorytmem Max-Min...");

        while (!unassigned.isEmpty()) {
            Cloudlet bestCloudlet = null;
            Vm bestVm = null;
            double globalMaxCompletionTime = -1.0;

            // Krok 1: Dla każdego zadania szukamy maszyny, która wykona je najszybciej
            for (Cloudlet task : unassigned) {
                double minCompletionTimeForTask = Double.MAX_VALUE;
                Vm bestVmForTask = null;

                for (int i = 0; i < vms.size(); i++) {
                    Vm vm = vms.get(i);
                    double executionTime = task.getCloudletLength() / vm.getMips();
                    double completionTime = vmReadyTime[i] + executionTime;

                    if (completionTime < minCompletionTimeForTask) {
                        minCompletionTimeForTask = completionTime;
                        bestVmForTask = vm;
                    }
                }

                // Krok 2: Spośród wszystkich zadań wybieramy to, którego "minimalny czas zakończenia" jest NAJWIĘKSZY
                if (minCompletionTimeForTask > globalMaxCompletionTime) {
                    globalMaxCompletionTime = minCompletionTimeForTask;
                    bestCloudlet = task;
                    bestVm = bestVmForTask;
                }
            }

            // Krok 3: Przypisujemy zadanie do wybranej maszyny
            broker.bindCloudletToVm(bestCloudlet.getCloudletId(), bestVm.getId());
            int vmIndex = vms.indexOf(bestVm);
            vmReadyTime[vmIndex] += (bestCloudlet.getCloudletLength() / bestVm.getMips());
            unassigned.remove(bestCloudlet);
        }
        
        System.out.println("Zakończono mapowanie Max-Min.");
    }
}