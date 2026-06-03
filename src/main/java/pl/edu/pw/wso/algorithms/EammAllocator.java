package pl.edu.pw.wso.algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.List;

public class EammAllocator {

    // Dodajemy parametry weightTime oraz weightLoad (wagi muszą sumować się do 1.0)
    public static void allocate(DatacenterBroker broker, List<Cloudlet> cloudlets, List<Vm> vms, double weightTime, double weightLoad) {
        List<Cloudlet> unassigned = new ArrayList<>(cloudlets);
        double[] vmReadyTime = new double[vms.size()];
        int[] vmTaskCount = new int[vms.size()]; // Śledzimy liczbę przypisanych zadań do oceny obciążenia

        System.out.println("Rozpoczynam mapowanie EAMM (Waga Czasu: " + weightTime + ", Waga Obciążenia/Energii: " + weightLoad + ")...");

        while (!unassigned.isEmpty()) {
            Cloudlet bestCloudlet = null;
            Vm bestVm = null;
            double globalMinCost = Double.MAX_VALUE;

            for (Cloudlet task : unassigned) {
                double minCostForTask = Double.MAX_VALUE;
                Vm bestVmForTask = null;

                for (int i = 0; i < vms.size(); i++) {
                    Vm vm = vms.get(i);
                    double executionTime = task.getCloudletLength() / vm.getMips();
                    double completionTime = vmReadyTime[i] + executionTime;

                    // AUTORSKA FUNKCJA KOSZTU EAMM:
                    // 1. Składnik czasu: standardowy przewidywany czas zakończenia zadania.
                    // 2. Składnik energii/obciążenia: prosta heurystyka nakładająca "karę" za długą kolejkę zadań na maszynie.
                    // Im więcej zadań maszyna ma przypisanych, tym wyższy koszt przydzielenia kolejnego,
                    // co wymusza bardziej zrównoważony rozkład pracy i chroni najszybsze hosty przed przeciążeniem.
                    double loadPenalty = vmTaskCount[i] * executionTime;
                    double cost = (weightTime * completionTime) + (weightLoad * loadPenalty);

                    if (cost < minCostForTask) {
                        minCostForTask = cost;
                        bestVmForTask = vm;
                    }
                }

                if (minCostForTask < globalMinCost) {
                    globalMinCost = minCostForTask;
                    bestCloudlet = task;
                    bestVm = bestVmForTask;
                }
            }

            // Przypisanie zadania i aktualizacja stanu maszyny
            broker.bindCloudletToVm(bestCloudlet.getCloudletId(), bestVm.getId());
            int vmIndex = vms.indexOf(bestVm);
            vmReadyTime[vmIndex] += (bestCloudlet.getCloudletLength() / bestVm.getMips());
            vmTaskCount[vmIndex]++; // Zwiększamy licznik zadań na VM
            
            unassigned.remove(bestCloudlet);
        }
        
        System.out.println("Zakończono mapowanie EAMM.");
    }
}