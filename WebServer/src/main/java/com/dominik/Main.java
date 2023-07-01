package com.dominik;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
public class Main {
    @Autowired
    private LifeBitService service;
    private static ModbusServerProgram program;
    private static SNMP_Program snmpProgram;

    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
        program = new ModbusServerProgram(503);
        snmpProgram = new SNMP_Program("udp:0.0.0.0/161","1.3.6.1.2.1.1.1","uzytkownik","authtest","privtest");
    }
    @GetMapping("/lifebits")
    public List<LifeBit> GetAllInfo()
    {
        return service.getAllInfo();
    }
    @GetMapping("/lifebits/{id}")
    public LifeBit GetOneInfo(@PathVariable("id") long id)
    {
        return service.getInfoById(id);
    }
    @GetMapping("/lifebits/errors")
    public ResponseEntity<String> GetErrors()
    {
        String res = service.getError(400);
        System.out.println(res);
        if(res.equals("error"))
        {
            System.out.println("There was an error");
            program.WriteHoldingRegisters(400,1);
            snmpProgram.SendError();
            return new ResponseEntity<String>("The program has stopped working",HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("The system is working properly");
    }
    @PostMapping("/lifebits")
    public void PostInfo(@RequestBody LifeBit Info)
    {
        service.postLifeBit(Info);
    }
    @DeleteMapping("/lifebits")
    public void DeleteAllInfo()
    {
        service.DeleteAllInfo();
    }
}