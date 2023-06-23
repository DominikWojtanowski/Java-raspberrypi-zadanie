package com.dominik;

import de.re.easymodbus.server.ModbusServer;

import java.io.IOException;

public class ModbusServerProgram {
    private ModbusServer server;
    ModbusServerProgram(int port)
    {
        server = new ModbusServer();
        server.setPort(port);
        WriteCoils(true);
        WriteDiscreteInputs(true);
        WriteHoldingRegisters(55);
        WriteInputRegisters(102);
        try {
            server.Listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void WriteHoldingRegisters(int register)
    {
        server.holdingRegisters[1] = register;
    }
    public void WriteInputRegisters(int input)
    {
        server.inputRegisters[1] = input;
    }
    public void WriteDiscreteInputs(boolean input)
    {
        server.discreteInputs[1] = input;
    }
    public void WriteCoils(boolean coil)
    {
        server.coils[1] = coil;
    }


}

