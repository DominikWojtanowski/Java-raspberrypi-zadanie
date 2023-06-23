package org.example;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ModbusClient client = new ModbusClient("127.0.0.1", 502);
        try {
            client.Connect();
            System.out.println(client.ReadHoldingRegisters(0, 1)[0]);
            System.out.println(client.ReadCoils(0,1)[0]);
            System.out.println(client.ReadDiscreteInputs(0,1)[0]);
            System.out.println(client.ReadInputRegisters(0,1)[0]);
        } catch (ModbusException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
