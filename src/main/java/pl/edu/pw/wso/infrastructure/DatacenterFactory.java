package pl.edu.pw.wso.infrastructure;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;

import java.util.ArrayList;
import java.util.List;

public class DatacenterFactory {

    public static List<PowerHost> createWariantA() {
        List<PowerHost> hostList = new ArrayList<>();

        // H1: 8 PEs, 10000 MIPS, 32 GB RAM, niski pobór (np. max 150W, 30% w spoczynku)
        hostList.add(createHost(1, 8, 10000, 32768, 150, 0.3));

        // H2: 8 PEs, 12000 MIPS, 32 GB RAM, niski pobór
        hostList.add(createHost(2, 8, 12000, 32768, 150, 0.3));

        // H3: 16 PEs, 15000 MIPS, 64 GB RAM, średni pobór (np. max 250W, 30% w spoczynku)
        hostList.add(createHost(3, 16, 15000, 65536, 250, 0.3));

        // H4: 16 PEs, 18000 MIPS, 64 GB RAM, wysoki pobór (np. max 350W, 30% w spoczynku)
        hostList.add(createHost(4, 16, 18000, 65536, 350, 0.3));

        // H5: 32 PEs, 25000 MIPS, 128 GB RAM, bardzo wysoki pobór (np. max 500W, 30% w spoczynku)
        hostList.add(createHost(5, 32, 25000, 131072, 500, 0.3));

        return hostList;
    }

    public static List<PowerHost> createWariantB() {
        List<PowerHost> hostList = new ArrayList<>();

        // Pierwsze 5 maszyn identyczne jak w Wariancie A
        hostList.add(createHost(1, 8, 10000, 32768, 150, 0.3));
        hostList.add(createHost(2, 8, 12000, 32768, 150, 0.3));
        hostList.add(createHost(3, 16, 15000, 65536, 250, 0.3));
        hostList.add(createHost(4, 16, 18000, 65536, 350, 0.3));
        hostList.add(createHost(5, 32, 25000, 131072, 500, 0.3));

        // Nowe hosty dla Wariantu B o określonych parametrach CPU, MIPS i RAM
        hostList.add(createHost(6, 8, 11000, 32768, 150, 0.3));
        hostList.add(createHost(7, 16, 16000, 65536, 250, 0.3));
        hostList.add(createHost(8, 16, 20000, 65536, 350, 0.3));
        hostList.add(createHost(9, 32, 28000, 131072, 500, 0.3));
        hostList.add(createHost(10, 32, 30000, 131072, 500, 0.3));

        return hostList;
    }

    public static List<Vm> createVms(int brokerId, int vmCount) {
        List<Vm> vmList = new ArrayList<>();
        // Parametry VM do kalibracji w miarę postępu prac nad zadaniami wideo
        int mips = 1000;
        long size = 10000; // 10 GB
        int ram = 2048; // 2 GB
        long bw = 1000;
        int pesNumber = 4; // 4 vCPU na maszynę wirtualną
        String vmm = "Xen";

        // Tworzenie zdefiniowanej liczby maszyn wirtualnych
        for (int i = 0; i < vmCount; i++) {
            vmList.add(new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared()));
        }
        return vmList;
    }

    private static PowerHost createHost(int id, int pesNumber, int mips, int ramMB, double maxPower, double staticPowerPercent) {
        List<Pe> peList = new ArrayList<>();
        // Tworzenie rdzeni (PEs) dla danego hosta
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mips)));
        }

        long storage = 1000000; // 1 TB pamięci masowej na hosta
        int bw = 10000;         // Przepustowość 10 Gbit/s

        return new PowerHost(
                id,
                new RamProvisionerSimple(ramMB),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList),
                new PowerModelLinear(maxPower, staticPowerPercent)
        );
    }
}