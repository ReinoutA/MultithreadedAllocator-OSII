# LAB3_Besturingssystemen2
Github repositorie: https://github.com/brechtvandesijpe/LAB3_Besturingssystemen2
→ branch Synchronisatie

## Allocator
De Allocator package bevat volgende onderdelen:
- Allocator interface
- Foute voorbeeldimplementatie van Allocator genaamd MyAllocatorImpl
- Backingstore, om map en unmap calls te doen naar het OS
- MTAllocator als multithreaded implementatie van Allocator, bevat een map van STAllocators
- STAllocator als singlethreaded implementatie van Allocator, bevat een map van Arenas
- Arena een verzameling van geheugenblokken
- Block als voorstelling van een geheugenblok in een arena
- Allocator-, Arena- en BlockException klassen dienen om exception-handling te voorzien binnen de implementatie van Block, Arena en Allocator


## Debugger
De debugger package bevat volgende onderdelen:
- Aparte debuggerklassen voor de klassen Block, Arena, STAllocator en Allocator
- Een Main klasse die alle debuggers oproept en voor de output zorgt
- Een logger die berichten naar de console logt met een timestamp en threadId van de uitvoerende thread en een stacktrace naar waar de logmethode werd opgeroepen, door extra diepte mee te geven in de logmethode kan de stacktrace meer of minder ver weergegeven worden
- Een klasse StackTracer om de stack weer te geven in de logger
- Workerklassen om eenzelfde Block, Arena of Allocator tegelijk te gebruiken om zo de synchronisatie te testen (niet standaard in Main verwerkt)
- TesterFailed- en TesterSuccessException klassen dienen om de exception-handling te voorzien binnen de debugger

### Test cases
De debugger test onze verschillende onderdelen op specifieke manieren:
1. De Multithreaded allocator wordt getest in de AllocatorDebugger op correcte werking in zowel singlethreaded als multithreaded context en doorloopt alle testen die de STAllocatorDebugger doorloopt.
2. De SingleThreadedAllocator wordt getest in de STAllocatorDebugger op correcte werking in de singlethreaded context. Hierbij wordt gekeken naar de allocatie van blokken met groottes machten van twee (1, 2, 4 ... 2048) en vervolgens in veelvouden van 4096. Daarna wordt een reeks van willekeurige allocaties gedaan in de allocator, worden deze vervolgens ge-realloceerd en tot slot gefreed. Na elke stap wordt er gekeken of de addressen al dan niet gealloceerd zijn.
3. De Arenaklasse wordt getest in de ArenaDebugger en zal de correcte werking van de allocate- en free-methoden nakijken door allocatie van twee pagina's binnen een arena. De grootte van paginas variëren op net dezelfde manier als in de STAllocaterDebugger met de corresponderende blokgroottes.
4. De Blockklasse wordt getest in de BlockDebugger en zal individuele blokken gaan testen op correcte werking. Dit gebeurt analoog aan de AllocatorDebugger. We doen hierbij 4 tests op blokken:
    - Een test waarbij we kijken dat een isAccessible op een range die begint buiten de pagina (en eventueel eindigt binnen de pagina) zeker false teruggeeft. (1)
    - Een test waarbij we kijken dat een isAccessible op een range die begint op een adres binnen de pagina en eindigt buiten de pagina zeker false teruggeeft. (2)
    - Een test waarbij we kijken dat een isAccessible op een range die begint op een adres binnen de pagina en eindigt binnen de pagina zeker true teruggeeft. (3)
    - Een test waarbij we kijken dat een isAcessible op elk adres binnen de pagina zeker een true teruggeeft. (4)

<img title="a title" alt="Alt text" src="/memory.png">

## BankingSimulation
Bevat de testcase voor de allocator.