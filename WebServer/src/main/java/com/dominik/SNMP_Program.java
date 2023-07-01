package com.dominik;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.SocketException;

public class SNMP_Program {
    private ResponseEvent<Address> responseEvent;
    private PDU response;
    private ScopedPDU pdu;
    private VariableBinding variableBinding;
    private UserTarget target;
    private OID oid;
    private USM usm;
    private UsmUser user;
    private Snmp snmp;
    private Address targetAddress;

    SNMP_Program(String Address, String Oid, String uzytkownik, String m_authPassword, String m_privPassword) {
        targetAddress = GenericAddress.parse(Address);
        usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES128());
        SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
        SecurityModels.getInstance().addSecurityModel(usm);

        OctetString securityName = new OctetString(uzytkownik);
        OID authProtocol = AuthMD5.ID;
        OID privProtocol = PrivAES128.ID;
        OctetString authPassword = new OctetString(m_authPassword);
        OctetString privPassword = new OctetString(m_privPassword);

        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            try {
                snmp.listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        user = new UsmUser(securityName, authProtocol, authPassword, privProtocol, privPassword);
        snmp.getUSM().addUser(securityName, user);

        target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(securityName);

        oid = new OID(Oid);

        variableBinding = new VariableBinding(oid);
    }

    public void SendError() {
        try {
            pdu = new ScopedPDU();
            pdu.add(variableBinding);
            pdu.setType(PDU.INFORM);

            responseEvent = snmp.inform(pdu, target);
            response = responseEvent.getResponse();

            if (response != null) {
                System.out.println("Odpowiedź SNMP od: " + responseEvent.getPeerAddress() + "o tresci: " + response.getVariableBindings());
            } else {
                System.out.println("Brak odpowiedzi SNMP.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void close()
    {
        try {
            snmp.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
