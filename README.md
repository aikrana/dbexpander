# Test de calidad GPIO TSOne (Getins)

Este software tiene como finalidad verificar el correcto funcionamiento de la PCB de alimentación utilizada en el modelo TSOne de TimingSense
haciendo uso de una tabla de testeo compuesta por:

* Un **Raspberry Pi**, donde se aloja el software aquí descrito.
* Un **relé**, utilizado para simular la pulsación del botón de encendido.
* Dos series de **resistencias**, utilizadas para simular el consumo de la CPU (12 voltios) y el lector RFID (24 voltios) del TSOne.
* Dos sensores de tensión (y corriente) **INA219**, utilizados para monitorizar los voltajes salientes de la GPIO (12 y 24 voltios).
* Un **adaptador serie** (RS232), utilizado para la comunicación con la GPIO y poder enviar señales de buzzer o apagado.
* Un **buzzer** de TSOne, utilizado para verificar que la GPIO pita correctamente cuando se le ordena.
* Un **ventilador** de TSOne, utilizado para refrigerar las series de resistencias y evitar un sobrecalientamiento.
* Un **adaptador Wifi USB**, utilizado para dotar de conectividad a la Raspberry Pi.

## Funcionalidades
* Encender y apagar forzosamente la GPIO haciendo uso del relé.
* Hacer pitar o apagar la GPIO a trevés de la comunicación por puerto serie.
* Monitorizar las tensiones de 12 y 24 voltios de la GPIO haciendo uso de los sensores INA219.
* Validar que el valor de estas tensiones se mantenga en unos rangos parametrizados.
* Asegurarse de que todas las acciones de encendido, apagado o apagado forzoso se producen correctamente.
* Registrar los tensiones máximas de cada test para posteriores análisis.
* Ofrecer una interfaz web a través de la cual se pueda llevar a cabo el testeo de las GPIOs, y una vez ejecutado el test, se pueda visualizar su resultado. 


## Procedimiento de uso

1. Conectar la GPIO objetivo de testeo en la tabla. Se debe conectar:
	* El puerto POE (para ilumniar el led de estado).
	* El buzzer.
	* La salida de 24 voltios de lector RFID.
	* La salida de 12 voltios de CPU.
	* La salida de 12 voltios para el ventilador (usada antiguamente para el led).
	* El relé al conector de encendido.
	* El conector de alimentación.
	* El puerto serie.
2. Conectar a la tabla una fuente de alimentación de TSOne y un cargador microUSB de 5 voltios de más de 700 mAh que alimente la Raspberry Pi.
3. Acceder a "http://pi.local" y hacer clic en "Ejecutar test". También es posible detener el test haciendo clic en "Forzar detención".
4. Visualizar en esta misma página los resultados que arroja progresivamente la aplicación. 

## Proceso de testeo
El proceso de testeo de una GPIO se compone de cinco partes, que si se producen satisfactoriamente dan la prueba por válida:
1. Encender la GPIO a través del relé. Anteriormente se verifica que la GPIO está apagada y posteriormente se verifica que se ha encendido correctamente.
2. Hacer pitar la GPIO. Se activa el buzzer un número de veces y durante un tiempo parametrizados haciendo uso de la comunicación RS232.
2. Monitorizar las tensiones durante un tiempo específico. Se verifica que los valores máximos y mínimos de ambas salidas se mantienen dentro de unos rangos pareametrizados.
3. Apagar la GPIO a trevés del puerto Serie. Anteriormente se verifica que la GPIO sigue encendida y posteriormente se verifica que se ha apagado correctamente.
4. Encender la GPIO de nuevo. Se espera 10 segundos para asegurarse de que la GPIO no mantiene ninguna tensión residual. Se verifica que se ha encendido correctamente.
5. Apagar la GPIO forzosamente. Se espera 5 segundos a que la GPIO se haya estabilizado. Se acciona el relé durante un tiempo parametrizado y se verifica que la GPIO se ha apagado correctamente.

## Lenguajes y librerías
Se describen los lenguajes utilizados y las librerías no nativas que se importan para el desarollo:
* **Python 2.7**. Lenguaje utilizado en el grueso de la aplicación a excepción de la parte Web. Permite accionar el relé, monitorizar los sensores de tensión, comunicarse con la GPIO a través del puerto serie, leer la configuración parametrizada, escribir los valores máximos en un archivo CSV, etc.
* Librería **subfact_pi_ina219**. Librería en Pyhon que permite comunicarse con los sensores INA219 mediante el bus I2C. (Necesario el paquete 'python-smbus').
* **YAML**. Utilizado para escribir el archivo de configuración de testeo. Los parametros establecidos en él permiten la parametrización de los tests.
* PHP 5. Usado sobre el servidor Apache para la web. Permite ejecutar el test en Python, mostrar su salida y forzar su detención.
* **HTML/CSS/Javascript/Ajax**. Permiten presentar la interfaz web al cliente, auto-refrescar el resultado del test, ejecutarlo, detenerlo, etc.


## Descripción del software de testeo

La base del software de testeo es el archivo 'main.py', que es el que se ejecuta para llevar a cabo un test. Cada una de las acciones que lleva a cabo (descritas anteriormente) se señalizan mediante comentarios ("Power On Getins", "Tiempo muerto", "power Off Getins", "brute power Off").

En main se instancian tres clases claves:


### La clase Rele

Permite hacer uso del relé conectado al puerto GPIO4 del Raspberry Pi. Dispone de tres métodos:
* **__init__()**: (constructor) establece la configuración del puerto GPIO4 usado para accionar el relé
* **on()**: enciende el relé.
* **off()**: apaga el relé.

En main, se instancia con el nombre "r" y se utiliza mediante los métodos longRelay() y shortRelay().


### La clase Serie

Permite comunicarse a través del puerto serie con la GPIO. Dispone de cinco métodos:
* **__init__()**: (constructor) establece la configuración del puerto serie para poder comunicarse con la GPIO
* **buzzerOn()**: acciona el buzzer.
* **buzzerOff()**: apaga el buzzer.
* **powerOff()**: apaga la GPIO.
* **powerOffIn(segundos)**: apaga la GPIO con un retraso específico asíncronamente.

En main, se instancia con el nombre "s" y se utiliza en los métodos shortBuzzer(), longBuzzer(), doBuzzerReps(), getinsOff() y getinsOffIn(segundos).


### La clase Monitor

Permite monitorizar asíncronamente las tensiones leídas por los sensores INA219 y validar que sus valores se mantienen dentro de unos rangos parametrizados. Dispone de once métodos:
* **__init__(config)**: (constructor) inicializa las variables de la clase en función de una configuración parametrizada. Construye los objetos relativos a los sensores de tensión en función de la configuración establecida.
* **start()**: inicia el proceso asíncrono de monitorización de tensiones (método mon).
* **startTest(título,clave)**: inicia una nueva monitorización a partir de un título para la misma (usado sólo para identificar los resultados) y de una clave (identifica la sección de la configuración que contiene los rangos de tensión aceptables durante la monitorización)
* **stopTest()**: finaliza la monitorización en curso.
* **isValid()**: indica si el último test realizado ha sido válido o no. Es decir, si las tensiones leídas durante su ejecución se comprendían entre los rangos especificados.
* **getReads()**: devuelve una colección con las lecturas obtenidas durante la última monitorización. Si se inicia una nueva, se borran las anteriores.
* **getResults()**: devuelve una colección con los resultados obtenidos en todas las monitorizaciones llevadas a cabo.
* **getLastResult()**: devuelve una colección relativa al resultado de la última monitorización. El formato de esta es [Título, True|False (test válido/inválido), Mensaje]
* **getTestTitle()**: devuelve el título dado a la última monitorización realizada.
* **mon()**: método que se ejecuta asíncronamente mediante start() y que lleva a cabo la monitorización y validación de lecturas.
* **registerMax()**: método utilizado para almacenar en csv/max.csv las tensiones máximas obtenidas durante todo el test. Se ejecuta al final de main


### El archivo de configuración
Tal y como se observa al inicio de main, se hace uso de un objeto config que, después de construirse con el método loadConfig(), es utilizado para la parametrización de ciertos valores como:
* Repeticiones del buzzer.
* Duraciones de los pitidos largos y cortos.
* Demora entre pitidos y entre repeticiones.
* Duración de accionamientos cortos y largos del relé.
* Direcciones I2C relativas a los sensores INA, cuál monitoriza 24 voltios y cuál monitoriza 12.
* Rangos válidos de tensiones. El título de cada sección se debe corresponder con la clave con la que se ejecuta cada test al que se quiera aplicar este rango [startTest(título,CLAVE)]

Este archivo se encuentra en config/config.yml y para poder procesar su contenido es necesario instalar el paquete 'python-yaml'.


### La web
Dada la simplicidad de la misma, se describe muy breve mente su consistencia.

Bajo el directorio web/ se encuentra 'index.php'. Este es el índice de la web, se encarga de ejecutar el test cuando se clica el botón correspondiente mediante el uso de la función exec() y el archivo exec. Este archivo ejecuta 'main.py' y dirige su salida al archivo 'output.txt'.También ofrece la opción de 'Forzar detención' por la que mata todos los procesos de Python en ejecución. 

Una vez acabado el test, se devuelve su resultado haciendo uso de 'outputGetter.php', que no hace más que leer el contenido de este archivo y dibujarlo como página web, así como mostrar el estado del test (Cancelado | En proceso | Correcto | Fallido). Adicionalmente, durante la ejecución del test se llama mediante Ajax a este fichero cada cinco segundos para conocer el estado actual del test y poder cancelarlo si no se están obteniendo los resultado esperados.
