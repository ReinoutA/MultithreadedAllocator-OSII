
# Java synchronisatie cheat sheet

* Elk Java object bevat: 
    * een mutex:
        * kan ge-locked en ge-unlocked worden via het `synchronized` keyword, als volgt:
            ```
            synchronized(object) {
                // lockt het object in het begin
                // als het object al gelocked was (door een andere thread), wacht (slaapt) de thread automatisch tot het object geunlocked wordt.
                // wanneer dat gebeurt, kan de thread verder gaan (en moeten alle andere threads die willen locken, wachten)
                
                // all the code

                // unlockt het object op het einde
            }
            ```
        * meerdere synchronized blocks die op hetzelfde object werken locken dezelfde mutex (dit noemen we heterogene synchronisatie).
    * en een conditie variable, waarop gewacht kan worden (`wait`), en genotified (`notify` en `notifyAll`)
        * om te wachten op de conditievariable, gebruik `obj.wait()` (zonder parameters). meerdere objecten kunnen tegelijk wachten op 1 object
            * om te kunnen wachten op een object, moet je zijn mutex gelocked hebben. Zodra je aan het wachten bent, zal de mutex tijdelijk ge-unlocked worden zodat andere threads ook kunnen komen wachten. (dit is allemaal automatisch)
        * om te notifyen op het object (vanuit een andere thread), gebruik `obj.notify()`
            * `notifyAll()` maakt alle wachtende threads tegelijk wakker, slechts 1 thread lockt de mutex terug (allemaal automatisch). de andere threads zullen daarna, elk op hun beurt, de mutex kunnen locken en het synchronized block kunnen verlaten
            * `notify()` maakt slechts 1 (random gekozen) wachtende thread wakker. de rest blijft slapen (wachten op volgende `notify`)

* Primitive types zoals `int`, `long`, `double` etc zijn geen Java objects, dus hierop kan niet gelocked/gewait/genotified worden.
    * Java arrays (`int[]`) zijn _wel_ Java objects, maar bevatten dus maar 1 mutex en conditie variabele voor de hele array.
    * Spijtig genoeg lijkt Java de syntax `synchronized(array[i])` te ondersteunen, ook al lockt dit dus de mutex van de hele array.

* Een methode van een klasse die `synchronized` is, is precies hetzelfde als dit:
    ```
    public void method() {
        synchronized(this) {
            // all the code
        }
    }
    ```

* Pas op met synchroniseren op "Boxed" object versies van primitieve types (`Integer`, `Long`, etc.). Deze hebben de assignment operator en zijn variaties overloaded staan (`=`, `-=`, `+=`, etc), maar het resultaat is steeds een nieuw/ander object! De volgende code:
    ```
    Long l = ...;
    synchronized(l) {
        l++;
        // `l` is nu een nieuw object waarop we niet langer synchronized zijn! pas op!
    }
    ```
    is dus een geniepige valsstrik.

* Let op met "tight" synchronisatie-loops als volgt:
    ```
    while (cond) {
        synchronized(obj) {
            // all the code
        }
    }
    ```
    We merken dat sommige JVMs een "optimalisatie" lijken te doen waarbij de thread die het meest recent `obj` gelockt heeft,
    met enige prioriteit dat lock terug krijgt. Wanneer meerdere threads dus staan te wachten om `obj` te locken, kan het zijn
    dat 1 thread blijft loopen terwijl die het lock amper opgeeft. Dit is een voorbeeld van starvation.

* Calls naar de `wait` method kunnen "spuriously" returnen, zonder dat het object echt genotified is. Daarom roep je deze best op in een loop, waarbij je enkel uit de loop gaat als de conditie die je wilt dat verandert, ook echt veranderd is:
    ```
    synchronized (obj) {
        while (<condition does not hold>) {
            obj.wait();
        }
        ... // Perform action appropriate to condition
    }
    ```
    (code genomen uit [Java documentatie](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#wait()))

* Simpele check om bugs te vangen met conditie variabelen: als je op een object ooit `wait` callt, check dan meteen of er ten minste ergens een plek is waarop je ook `notify` callt. Anders kunnen threads die in de `wait` belanden nooit meer wakker worden. 

* Let op met `synchronized` methods in interfaces/parent classes/overridden methods. Als de parent method `synchronized` is, maar de child niet, zal het `this` object _niet_ gelockt worden tijdens de method call. Zolang je dit consistent houdt tussen beiden, zal je geen problemen tegenkomen. 
    * meer info: https://rules.sonarsource.com/java/RSPEC-3551
