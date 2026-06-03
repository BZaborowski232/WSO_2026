package pl.edu.pw.wso.experiments;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import pl.edu.pw.wso.infrastructure.DatacenterFactory;
import pl.edu.pw.wso.infrastructure.TaskFactory;
import pl.edu.pw.wso.utils.CsvExporter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class BaselineExperiment {

    public static void main(String[] args) {
        String variant = (args.length > 0) ? args[0] : "A";
        int taskCount = (args.length > 1) ? Integer.parseInt(args[1]) : 500;

        System.out.println("Rozpoczynam Baseline (FCFS) | Wariant: " + variant + " | Zadania: " + taskCount);

        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            List<PowerHost> hostList = variant.equals("B") ? DatacenterFactory.createWariantB() : DatacenterFactory.createWariantA();
            int vmCount = variant.equals("B") ? 40 : 20;

            Datacenter datacenter = createDatacenter("Datacenter_" + variant, hostList);
            DatacenterBroker broker = new DatacenterBroker("Broker_FCFS");
            
            List<Vm> vmlist = DatacenterFactory.createVms(broker.getId(), vmCount);
            broker.submitVmList(vmlist);
            List<Cloudlet> cloudletList = TaskFactory.createVideoTasks(broker.getId(), taskCount);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            CsvExporter.exportCloudletsToCsv(newList, "DATA/wyniki_FCFS_" + variant + "_" + taskCount + ".csv");
            System.out.println("Zakończono.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static Datacenter createDatacenter(String name, List<PowerHost> hostList) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // Wykorzystujemy VmAllocationPolicySimple - prostą politykę alokacji VM do Hostów
        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 1.0);
    }

    private static DatacenterBroker createBroker(String name) throws Exception {
        return new DatacenterBroker(name);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        String indent = "    ";
        System.out.println();
        System.out.println("========== WYNIKI SYMULACJI ==========");
        System.out.println("Task ID" + indent + "STATUS" + indent + "Datacenter ID" + indent + "VM ID" + indent + "Czas (Makespan)" + indent + "Start" + indent + "Koniec");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");
                System.out.println(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getActualCPUTime())
                        + indent + indent + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}