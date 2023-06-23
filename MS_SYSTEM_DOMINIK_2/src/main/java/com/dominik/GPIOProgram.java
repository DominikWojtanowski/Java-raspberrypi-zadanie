package com.dominik;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import com.pi4j.context.Context;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
public class GPIOProgram {
    private static SNMP_Program program = new SNMP_Program("udp:0.0.0.0/161","1.3.6.1.2.1.1.1","uzytkownik","authtest","privtest");
    private static final int pin_button = 24; // PIN 18 = BCM 24
    private static final int ms_delay = 1000; // odstep miedzy cyklicznie pobudzanym toggle
    private static final int min_closetime = 20; // czas dzialania programu
    private static AtomicBoolean wasTriggered = new AtomicBoolean(true); // flaga, która sprawdza czy pin zostal pobudzony
    private static AtomicBoolean wasError = new AtomicBoolean(false);
    private static AtomicBoolean stateChanged = new AtomicBoolean(false);
    private static AtomicBoolean isRunning =  new AtomicBoolean(true); // flaga ktora sprawdza czy program dalej dziala
    private static Thread GPIOthread; // wątek w którym jest sprawdzane czy program dobrze dziala i pin jest cyklicznie pobudzany
    GPIOProgram()
    {
        GPIOcontrol();
    }
    private void GPIOcontrol()
    {
        Console console = new Console();
        console.box("\n*--------------------------------------------*\n" +
                "|JAVA Program was made by Dominik Wojtanowski|\n" +
                "*--------------------------------------------*\n");


        var pi4j = Pi4J.newAutoContext();
        //Konfiguracja leda
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("My-GPIO")
                .name("MY LED")
                .address(pin_button)
                .initial(DigitalState.LOW)
                .shutdown(DigitalState.LOW)
                .provider("linuxfs-digital-output");
        var ledoutput = pi4j.create(ledConfig);
        // dodanie wysłuchiwacza, który bedzie wyswietlal kominukat przy zmianie stanu
        //ledoutput.addListener(System.out::println);

        ScheduledExecutorService taskexecutor = Executors.newScheduledThreadPool(1);
        //Zadanie ktore ma pobudzac cyklicznie pin gpio, oraz gdy pin bedzie pobudzony to ustawia flage wasTriggered na true
        Runnable task = () -> {
            /*program.SendError();*/ // Jesli trzeba bedzie przetestowac snmp4j to prosze to odznaczyc
            ledoutput.toggle();
            if(ledoutput.state()==DigitalState.HIGH)
                wasTriggered.set(true);
            if(wasError.get() == true)
                stateChanged.set(true);

        };

        //Zadanie ktore ma imitowac system watchdog i w przypadku pobudzenia piny rebootowac system
        Runnable watchdogtask = () -> {
            while(isRunning.get())
            {
                try
                {
                    if(!wasTriggered.get())
                    {
                        wasError.set(true);
                        try {
                            Thread.sleep(5000);
                            if(stateChanged.get()==false)
                                throw new ProgramError("The gpio pin has not been energized in the next");
                            stateChanged.set(false);
                            wasError.set(false);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    wasTriggered.set(false);
                    try {
                        Thread.sleep(2 * ms_delay);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                catch(ProgramError err)
                {
                    err.show_error();
                    try {
                        program.SendError();
                        CleanUpResources(pi4j,taskexecutor);
                        System.out.println("IN THE NEXT 5 SECONDS THE SYSTEM WILL BE RESTARTED");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        //Wywolanie komendy ktora ma za zadanie zrebootowac system
                        Runtime.getRuntime().exec("shutdown -r now");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        };
        //faktyczna inicjalizacja wątku ktory ma za zadanie imitowac system watchdog
        GPIOthread = new Thread(watchdogtask);
        //rozpoczecie watku
        GPIOthread.start();
        try {
            GPIOthread.join(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Ustawienie zadania ktore cyklicznie pobudza pin, aby wykonywalo sie co ms_delay milisekund
        taskexecutor.scheduleAtFixedRate(task, 0, ms_delay, TimeUnit.MILLISECONDS);

        //ustawie zakonczenia programu, aby odbylo sie po min_closetime minutach
        taskexecutor.schedule(() -> {
            CleanUpResources(pi4j,taskexecutor);
        }, min_closetime, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                CleanUpResources(pi4j,taskexecutor);
            }
        });
    }
    private static void CleanUpResources(Context pi4j,ScheduledExecutorService task)
    {
        pi4j.shutdown();
        task.shutdown();
        isRunning.set(false);
        program.close();
    }




}
