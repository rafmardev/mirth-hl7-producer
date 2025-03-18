package org.example;

import org.example.mllp.MLLPClient;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        final Logger LOGGER = Logger.getLogger(Main.class.getName());
        final long seconds = 5L;

        MLLPClient client = new MLLPClient("127.0.0.1", 6661);
        client.setResponseListener(event -> LOGGER.info((event.response())));

        String[] messages = getHL7Messages();

        try (ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1)) {
            IntStream.range(0, messages.length)
                    .forEach(i -> scheduledExecutorService.scheduleAtFixedRate(() -> client.sendMessage(messages[i]),
                            i * seconds, seconds * messages.length, TimeUnit.SECONDS));
        }
    }

    private static String[] getHL7Messages() {
        String hl7MessageADT = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20250313120000||ADT^A01|123456|P|2.5\n" +
                "EVN|A01|20250313120000\n" +
                "PID|1|12345|67890|98765|Doe^John||19800101|M|||1234 Main St^Apt 101^Springfield^IL^62701||(555)555-5555|||EN|M|123456789\n" +
                "PV1|1|I|ICU^01^01|1|234^Smith^John|||SUR||||||123456|A|||||20250313120000|||20250313120000\n";

        String hl7MessageORU = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20250313120100||ORU^R01|987654|P|2.5\n" +
                "PID|1|98765|12345|67890|Jones^Sarah||19750101|F|||5678 Elm St^Apt 202^Chicago^IL^60610||(555)555-5555|||EN|F|987654321\n" +
                "OBR|1|12345|67890|Test^Blood Work|||20250313120000|20250313120000|20250313120000|LAB^Blood Test|A|||20250313120100|||1234^Medical Lab^Smith\n" +
                "OBX|1|NM|12345^Hemoglobin||13.5|g/dL|12.0-15.0|N|||F\n" +
                "OBX|2|NM|12346^Cholesterol||190|mg/dL|120-200|N|||F";

        String hl7MessageORM = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20250313120200||ORM^O01|54321|P|2.5\n" +
                "PID|1|12345|67890|98765|Taylor^Emma||19900515|F|||789 Pine St^Apt 202^Boston^MA^02115||(555)555-5555|||EN|F|123456789\n" +
                "ORC|RE|123456^OrderNum|789101^OrderProvider|321^Physician^John|||20250313120000\n" +
                "OBR|1|12345|67890|Test^X-ray|20250313120000|20250313120000|20250313120000|RAD^X-ray " +
                "Chest|A|||20250313120300|||1234^Radiology Dept^Doe\n";

        String hl7MessageMDM = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20250313120330||MDM^T02|654321|P|2.5\n" +
                "PID|1|12345|67890|98765|Williams^Liam||19951215|M|||123 Oak St^Apt 303^New York^NY^10001||(555)555-5555|||EN|M|987654321\n" +
                "ORC|RE|654321^OrderNum|987654^OrderProvider|321^Physician^Michael|||20250313120000\n" +
                "OBR|1|65432|76543|Test^MRI Scan|20250313120000|20250313120000|20250313120000|MRI^Brain Scan|A|||20250313121000|||1234^Imaging Dept^Brown\n" +
                "MDM|T02|20250313120000|654321^Test^MRI|CONFIRMED\n";

        String hl7MessageSIU = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20250313120400||SIU^S12|789123|P|2.5\n" +
                "SCH|1|123456^AppointmentNum|20250314|20250315|20250316|SCHEDULED|12345^Doctor^John|01|20250314|20250316|20250314\n" +
                "PID|1|23456|78901|12345|Brown^Emily||19901212|F|||345 Birch St^Apt 404^Los Angeles^CA^90001||(555)" +
                "555-5555|||EN|F|123456789\n";

        return new String[]{hl7MessageADT, hl7MessageORU, hl7MessageORM, hl7MessageMDM, hl7MessageSIU};
    }
}