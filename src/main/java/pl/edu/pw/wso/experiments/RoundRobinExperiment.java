package pl.edu.pw.wso.experiments;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import pl.edu.pw.wso.algorithms.RoundRobinAllocator;
import pl.edu.pw.wso.infrastructure.DatacenterFactory;
import pl.edu.pw.wso.infrastructure.TaskFactory;
import pl.edu.pw.wso.utils.CsvExporter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class RoundRobinExperiment {

    public static void main(String[] args) {
        String variant = (args.length > 0) ? args[0] : "A";
        int taskCount = (args.length > 1) ? Integer.parseInt(args[1]) : 500;

        System.out.println("Rozpoczynam Round Robin | Wariant: " + variant + " | Zadania: " + taskCount);

        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            List<PowerHost> hostList = variant.equals("B") ? DatacenterFactory.createWariantB() : DatacenterFactory.createWariantA();
            int vmCount = variant.equals("B") ? 40 : 20;

            Datacenter datacenter = createDatacenter("Datacenter_" + variant, hostList);
            DatacenterBroker broker = new DatacenterBroker("Broker_RoundRobin");
            
            List<Vm> vmlist = DatacenterFactory.createVms(broker.getId(), vmCount);
            broker.submitVmList(vmlist);
            List<Cloudlet> cloudletList = TaskFactory.createVideoTasks(broker.getId(), taskCount);
            broker.submitCloudletList(cloudletList);

            // Algorytm Round Robin
            RoundRobinAllocator.allocate(broker, cloudletList, vmlist);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            CsvExporter.exportCloudletsToCsv(newList, "DATA/wyniki_RoundRobin_" + variant + "_" + taskCount + ".csv");
            System.out.println("Zakończono.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static Datacenter createDatacenter(String name, List<PowerHost> hostList) throws Exception {
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 1.0);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        System.out.println("\n========== WYNIKI SYMULACJI ==========");
        System.out.println("Task ID    STATUS    Datacenter ID    VM ID    Czas (Makespan)    Start    Koniec");
        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet cloudlet : list) {
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.println("    " + cloudlet.getCloudletId() + "        SUCCESS        " 
                        + cloudlet.getResourceId() + "            " + cloudlet.getVmId()
                        + "        " + dft.format(cloudlet.getActualCPUTime())
                        + "            " + dft.format(cloudlet.getExecStartTime())
                        + "        " + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}