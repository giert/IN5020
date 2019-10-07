start orbd -ORBInitialPort 1050
start java -jar Server.jar -ORBInitialPort 1050
PING localhost -n 120 >NUL
java -jar Client.jar -ORBInitialPort 1050
pause