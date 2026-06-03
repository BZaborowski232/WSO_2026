package pl.edu.pw.wso.algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

import java.util.ArrayList;
import java.util.List;

public class MinMinAllocator {

    public static void allocate(DatacenterBroker broker, List<Cloudlet> cloudlets, List<Vm> vms) {
        List<Cloudlet> unassigned = new ArrayList<>(cloudlets);
        
        // Tablica przechowująca czas, w którym dana VM zwolni się po wykonaniu przypisanych jej zadań
        double[] vmReadyTime = new double[vms.size()];

        System.out.println("Rozpoczynam mapowanie zadań algorytmem Min-Min...");

        while (!unassigned.isEmpty()) {
            Cloudlet bestCloudlet = null;
            Vm bestVm = null;
            double globalMinCompletionTime = Double.MAX_VALUE;

            // Krok 1: Dla każdego nieprzypisanego zadania szukamy maszyny, która wykona je najszybciej
            for (Cloudlet task : unassigned) {
                double minCompletionTimeForTask = Double.MAX_VALUE;
                Vm bestVmForTask = null;

                for (int i = 0; i < vms.size(); i++) {
                    Vm vm = vms.get(i);
                    // Szacowany czas wykonania = długość zadania / moc obliczeniowa (MIPS) maszyny
                    double executionTime = task.getCloudletLength() / vm.getMips();
                    // Czas zakończenia = czas, kiedy maszyna będzie gotowa + czas wykonania
                    double completionTime = vmReadyTime[i] + executionTime;

                    if (completionTime < minCompletionTimeForTask) {
                        minCompletionTimeForTask = completionTime;
                        bestVmForTask = vm;
                    }
                }

                // Krok 2: Spośród wszystkich zadań wybieramy to, którego "minimalny czas zakończenia" jest najmniejszy
                if (minCompletionTimeForTask < globalMinCompletionTime) {
                    globalMinCompletionTime = minCompletionTimeForTask;
                    bestCloudlet = task;
                    bestVm = bestVmForTask;
                }
            }

            // Krok 3: Przypisujemy wybrane zadanie do znalezionej maszyny
            broker.bindCloudletToVm(bestCloudlet.getCloudletId(), bestVm.getId());

            // Aktualizujemy czas gotowości tej maszyny o czas dodanego zadania
            int vmIndex = vms.indexOf(bestVm);
            vmReadyTime[vmIndex] += (bestCloudlet.getCloudletLength() / bestVm.getMips());

            // Usuwamy zadanie z puli do przydzielenia
            unassigned.remove(bestCloudlet);
        }
        
        System.out.println("Zakończono mapowanie Min-Min.");
    }
}