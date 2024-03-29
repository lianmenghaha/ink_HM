import parser.OutputDoc;
import processor.DrawPdf;
import processor.ProcessorOldSim;
import processor.ProcessorSIM;
import processor.SimDrawPdf;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class test_New2022_Opt {

    public static void main (String[] args) throws FileNotFoundException {

        LocalDateTime start = LocalDateTime.now();
        System.out.println("Program Starts at: "+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(start));

        ProcessorSIM processor = new ProcessorSIM();
        OutputDoc outputDoc = processor.process("input_HM/NEW2022_NM");
        SimDrawPdf drawPdf = new SimDrawPdf(outputDoc, "result_HM/NEW2022_NM");

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start,end);
        LocalDateTime duration_formated = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(duration.toMillis()), ZoneId.of("UTC"));
        System.out.println("Program Ends at: "+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(end));
        System.out.println("Program Run Time is: "+ DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(duration_formated));



    }






}
