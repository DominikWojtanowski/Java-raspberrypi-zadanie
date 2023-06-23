MS_SYSTEM_DOMINIK_2 - Program 1\
ModbusClientTest2 - program do testowania modbusa\
SnmpAgentTest - program do testowania serwera snmp\
\
Dodawanie biblioteki EasyModbus uzywajac mavena:
```mvn install:install-file -Dfile=EasyModbusJava.jar -DgroupId=de.re.easymodbus -DartifactId=modbus -Dversion=1.0 -Dpackaging=jar```\
Trzeba to wykonac w plikach ktore tej biblioteki potzebuja\
\
Aby dobrze uruchamiac program powinno sie wpisac ```mvn compile``` potem ```mvn package``` i uruchomic to za pomoca roota ablo za pomoca ```sudo java -jar <nazwa_pliku_jar>```


