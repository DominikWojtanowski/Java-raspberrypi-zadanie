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
    public void WriteHoldingRegisters(int register,int position)
    {
        server.holdingRegisters[position] = register;
    }
    public void WriteInputRegisters(int input,int position)
    {
        server.inputRegisters[position] = input;
    }
    public void WriteDiscreteInputs(boolean input,int position)
    {
        server.discreteInputs[position] = input;
    }
    public void WriteCoils(boolean coil,int position)
    {
        server.coils[position] = coil;
    }


}

