package org.example;

import org.snmp4j.*;
import org.snmp4j.mp.*;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;

public class Main {
    private static Snmp snmp;

    public static void main(String[] args) {
        try {
            // Inicjalizacja transportu
            TransportMapping transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/161"));
            transport.listen();


            // Konfiguracja SNMPv3
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES128());
            SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
            SecurityModels.getInstance().addSecurityModel(usm);

            ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
            MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
            mtDispatcher.addMessageProcessingModel(new MPv1());
            mtDispatcher.addMessageProcessingModel(new MPv2c());
            mtDispatcher.addMessageProcessingModel(new MPv3());
            snmp = new Snmp(mtDispatcher, transport);



            UsmUser user = new UsmUser(
                    new OctetString("uzytkownik"),
                    AuthMD5.ID,
                    new OctetString("authtest"),
                    PrivAES128.ID,
                    new OctetString("privtest")
            );
            snmp.getUSM().addUser(user.getSecurityName(), user);



            snmp.addCommandResponder(new CommandResponder() {
                @Override
                @SuppressWarnings("rawtypes")
                public <A extends Address> void processPdu(CommandResponderEvent<A> commandResponderEvent) {
                    PDU pdu = commandResponderEvent.getPDU();
                    if (pdu.getType() == PDU.INFORM) {
                        System.out.println("Awaria została skomunikowana");
                        OID oid = pdu.getVariableBindings().get(0).getOid();
                        System.out.println("OID to: " + oid);
                        pdu.setErrorIndex(0);
                        pdu.setErrorStatus(0);
                        pdu.setRequestID(commandResponderEvent.getPDU().getRequestID());
                        pdu.setType(PDU.RESPONSE);
                        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1"),new OctetString("New response just dropped")));


                        try {
                            // Wysłanie odpowiedzi
                            commandResponderEvent.getMessageDispatcher().returnResponsePdu(
                                    commandResponderEvent.getMessageProcessingModel(),
                                    commandResponderEvent.getSecurityModel(),
                                    commandResponderEvent.getSecurityName(),
                                    commandResponderEvent.getSecurityLevel(),
                                    pdu,
                                    commandResponderEvent.getMaxSizeResponsePDU(),
                                    commandResponderEvent.getStateReference(),
                                    new StatusInformation()
                            );
                            System.out.println("Odpowiedz zostala przeslana");

                        } catch (MessageException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });


            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        snmp.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("System działa i nasłuchuje...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
