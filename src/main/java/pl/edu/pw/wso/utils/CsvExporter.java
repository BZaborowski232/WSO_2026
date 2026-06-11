package pl.edu.pw.wso.utils;

import org.cloudbus.cloudsim.Cloudlet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class CsvExporter {

    public static void exportCloudletsToCsv(List<Cloudlet> list, String filePath) {
        System.out.println("Eksportowanie wyników do pliku: " + filePath);
        
        // Wymuszamy kropkę jako separator dziesiętny (ułatwia to import w Pythonie/Pandas)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat dft = new DecimalFormat("###.##", symbols);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Nagłówek CSV
            writer.println("TaskID,Status,DatacenterID,VmID,Makespan,StartTime,FinishTime");

            for (Cloudlet cloudlet : list) {
                if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                    writer.printf("%d,SUCCESS,%d,%d,%s,%s,%s%n",
                            cloudlet.getCloudletId(),
                            cloudlet.getResourceId(),
                            cloudlet.getVmId(),
                            dft.format(cloudlet.getActualCPUTime()),
                            dft.format(cloudlet.getExecStartTime()),
                            dft.format(cloudlet.getFinishTime())
                    );
                }
            }
            System.out.println("Eksport zakończony sukcesem.");
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu do pliku CSV: " + e.getMessage());
        }
    }
    public static void exportEnergyToCsv(String algorithm, String variant, int tasks, double energyKWh, String filePath) {
            System.out.println("Eksportowanie zużycia energii do pliku: " + filePath);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat dft = new DecimalFormat("###.####", symbols);

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("Algorytm,Wariant,LiczbaZadań,Energia_kWh");
                writer.printf(Locale.US, "%s,%s,%d,%s%n", algorithm, variant, tasks, dft.format(energyKWh));
            } catch (IOException e) {
                System.err.println("Błąd podczas zapisu energii do CSV: " + e.getMessage());
            }
        }
}