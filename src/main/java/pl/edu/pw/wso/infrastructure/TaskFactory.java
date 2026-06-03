package pl.edu.pw.wso.infrastructure;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskFactory {

    public static List<Cloudlet> createVideoTasks(int brokerId, int taskCount) {
        List<Cloudlet> list = new ArrayList<>();
        
        // Model utylizacji - zakładamy, że zadanie w pełni wykorzystuje przydzielony procesor, RAM i przepustowość
        UtilizationModel utilizationModel = new UtilizationModelFull();
        
        // Stały seed (np. 42), aby przy każdym uruchomieniu eksperymentu zadania były identyczne
        Random random = new Random(42); 

        for (int i = 0; i < taskCount; i++) {
            // Różnicujemy wielkość zadania, co symuluje różne operacje (od małej miniaturki po długą konwersję)
            long length = 20000 + random.nextInt(80000); // od 20k do 100k instrukcji
            int pesNumber = 1; // Wymagany 1 rdzeń (PE) na zadanie
            long fileSize = 100 + random.nextInt(900); // Rozmiar wejściowy (np. 100 - 1000 MB)
            long outputSize = 50 + random.nextInt(450); // Rozmiar wyjściowy po np. kompresji

            Cloudlet task = new Cloudlet(
                    i, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel
            );
            
            task.setUserId(brokerId);
            list.add(task);
        }
        
        return list;
    }
}