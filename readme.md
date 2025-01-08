# Using Genetic Algorithms to Train Neural Networks

## HexGrid Environment
 ````
directions:

      bn  cp     
       \ /
   an---A---ap 
       / \
      cn  bp     
 ````

* GridNode
  * Cell[] cellArr
    * List<Part> partList
  * GridNodeArea[][] gridNodeAreaArr
  * List<GridNodeAreaRef> gridNodeAreaRefList

## ToDo
* Upper limit for Energy in a Part.
* DONE Age (die aft n steps)
* Velocity and Accelleration
  * any external force acting on a part is added as acceleration.
  * The accelerations only work for the time step and are not added over time.
  * If a part can then move in the direction of acceleration, its speed/ velocity will be affected.
  * If the particle is blocked, its acceleration is passed on to the other particle as pressure. This pressure acceleration is then also used in the next time step. The pressure is thus passed on to other neighboring particles over time.
  * The floor has to absorb and swallow the pressure.
* Geschwindigkeit und Beschleunigung
  * Problem
    * Geschwindigkeit (Aufgrund von Beschleunigung) darf nur erhöht werden,
      wenn das Teilchen nicht kollidiert.
    * Eine Kollision kann nur festgestellt werden, wenn sich das Teilchen bewegt.
    * Ich kann die Beschleunigungen aufaddieren und daraus eine virtuelle Geschwindigkeit (aMove) machen.
      diese wird im Falle einer Kollision an das Nachbarteilchen weiter gegeben oder
      sonst in eine Geschwindigkeit überführt.
    * Eine bestehende Geschwindigkeit wird normal als vMove behandelt. 
  * Ruhender Part:
    1. Part is accelerated (fields, gravity) += In-Acceleration
    2. Blocked: Part passes out-acceleration/mass in direction to neighbor as in-acceleration.
       Out acceleration is not set to 0.
    3. Move: Out acceleration/mass is converted to velocity.
    4. In-Acceleration -> Out-Acceleration, In-Acceleration = 0
  * Grundgedanke
    * Der Austausch (auch Reflektion) zwischen Teilchen findet nur über die Beschleunigung statt.
    * Ein ruhendes Teilchen das kolliediert, gibt seine Beschleuigung an das andere Teilchen weiter.
      Die Beschleunigung "verschwindet" also in dem anderen Teilchen.
    * **Problem**: Wird ein Teilchen immer wieder beschleunigt, addieren sich die Beschleunigungen über die Zeit. 
      Es wird sich dann irgendwann bewegen.
    * **Lösung**: Die Beschleunigung schwingt (irgendwie) zurück, 
      so das sie bei einem unbewegten Teil an einen gegenüberliegenden Nachbarn weiter (zurück) gegeben wird. 
  * Teilchen bewegt sich mit seiner Geschwindigkeit.
  * Es unterliegt Beschleunigungen die hervorgerufen werden durch
    * Felder
    * Kollisionen (unelastisch)
  * Berechnungen
    1. inAcc für jedes Teichen
    2. inAcc nach outAcc (muss sein, da sich sonst felder (gravitation) addieren) und inAcc auf 0 setzen
       1. move: anhand von outAcc
          Beschleunigungen für alle Teilchen
          hierbei wird die Geschwindigkeit des Teilchens mal seiner Masse in Beschleunigung umgerechnet
       2. collision ->  neue inAcc für sich und nachbar 
  * Wenn sich ein Teilchen ohne Kollision in Richtung einer Beschleunigung bewegen kann,  
    wird es in der Richtung (abhängig von seiner Masse) die Geschwindigkeit beeinflussen. 
  * Kollidiert ein Teilchen 
    * mit einer Wall
      * wird seine Beschleunigung zur Geschwindigkeit addiert und reflektiert
    * mit einem Teilchen
      * wird seine Geschwindigkeit in Beschleunigung umgerechnet
        und abhängig von den Massen und Beschleunigungen an die Teilchen verteilt     
* Tentacles/ Pull-/Push-Fields
  * DONE Move only push/pull to other parts (using Fields)
    * DONE remove direct acceleration
  * Connect/ Release to other Parts
* Sort by energy bevor cloning, perfere parts with higher energy.
* DONE eat energy from other Part (not the whole)
* use Energie
    * DONE eat other parts
    * DONE death if low/ empty
    * DONE simple energy consumption 
    * energy consumption depending on the complexity and (output-) actions
    * slow down if low
    * DONE reproduction if high
* Add Inputs
  * DONE Neighbour Part-Types
  * DONE Field(s)
  * DONE Energie
* reproduction
  * DONE cloning if population shrinks
  * DONE asexual reproduction
  * sexual reproduction
* more complex environment
  * Air, Water, Wallpaper, ...
  * DONE Gravitation
  * DONE Acceleration (Gravitation)
  * Impulse
* Add Outputs
  * DONE Com-Signal-Field
  * DONE Pull-/Push-Fields
    * TODO only between Parts, not on nothing
* UI
  * DONE Zoom and Scroll
  * DONE Save and Load

## DONE

# relativistic mass increase
m(v) = m0 / (sqrt(1 - (v² / c²)))  
m(v) = m0 * (c / (c - v))

# Felder
* ElektrischesFeld
  * https://de.wikipedia.org/wiki/Elektrisches_Feld  
    **Nahwirkung statt Fernwirkung**  
    Zusammenfassend geht man aus heutiger Sicht davon aus, dass die Wechselwirkung zwischen den Ladungen erst vom elektrischen Feld vermittelt wird.  
    Ändert sich die Position einer der Ladungen, so breitet sich die Änderung des Feldes mit Lichtgeschwindigkeit im Raum aus.

# Systematik
* Elementarteilchen
  * Ein Elementarteilchen in drei Raumdimensionen ist immer entweder ein Fermion oder ein Boson.  
    In sehr dünnen Schichten, also zweidimensionalen Systemen, 
    gibt es außer Bosonen und Fermionen die sogenannten Anyonen, 
    die einer eigenen Quantenstatistik mit beliebigem (englisch ‘any’) Spin genügen.
* Quarks und Gluonen
  * Quarks (Fermionen)
    * drei Farben (r, g, b)
    * 6 Flavors: u (up), d (down), c (charm), s (strange), t (top), b (bottom)
      * drei Generationen: (u, d), (c, s), (t, b)
      * drittelzahlige elektrische Ladung Q 
      * Baryonenzahl B
      * Zu jedem Quark existiert ein Antiteilchen mit entgegengesetzten Quantenzahlen.
  * Gluonen
    * acht Farbkombinationen (rB, rG, Rg, gB, bR, bG, (gG-rR)/sqrt(2), (gG+rR-2bB)/sqrt(2))
    * masselose Elementarteilchen mit Spin 1
* Leptonen
  * Elementarteilchen mit halbzahligem Spin (1 / 2, 3 / 2, ...)
  * unterliegen nicht der starken Wechselwirkung
  * Elektron, das Myon und das Tau-Lepton sowie die zugehörigen Neutrinos.
* Fermionen
  * Spin von 1 / 2
* Bosonen
  * ganzzahligen Spin
* Hadronen
  * aus Quarks, Antiquarks und Gluonen zusammengesetzt 
* Elementarladung
  * kleinste in der Natur frei vorkommende elektrische Ladungsmenge.
  
* Elektron
  * Spin von 1 / 2
  * Lepton

# Info
* http://www.mikomma.de/fh/eldy/hertz.html
* Pfadintegral
  * https://de.wikipedia.org/wiki/Pfadintegral
  * Pfadintegral: Die Realität als Summe aller Eventualitäten - Spektrum der Wissenschaft  
    https://www.spektrum.de/news/pfadintegral-die-realitaet-als-summe-aller-eventualitaeten/2197570
* Gittereichtheorie
  * https://de.wikipedia.org/wiki/Gittereichtheorie
  * https://www.spektrum.de/lexikon/physik/gittereichtheorie/5871
* Quantenchromodynamik
  * https://www.spektrum.de/lexikon/physik/quantenchromodynamik/11843
* Elementarteilchen
  * https://www.spektrum.de/lexikon/physik/quarks/11907
  * https://www.spektrum.de/lexikon/physik/leptonen/8965
    * https://www.spektrum.de/lexikon/physik/elektron/4067
    * https://de.wikipedia.org/wiki/Elektron
    * https://www.spektrum.de/lexikon/physik/myon/10054
    * https://www.spektrum.de/lexikon/physik/neutrinos/10248
  * https://www.spektrum.de/lexikon/physik/gluonen/5998
  * https://de.wikipedia.org/wiki/Fermion
* Standardmodell der Elementarteilchen
  * https://de.wikipedia.org/wiki/Datei:Standard_Model_of_Elementary_Particles-de.svg

# Plan
TODO elektr. Felder haben Richtung
* Feld spürt nur andere Felder
  * Verdrängen (gleiche) oder auslöschen (unterschiedliche)
  * Elektrische Felder beeinflussen sich nicht, sie überlagern sich lediglich.  
    Auch die gebogenen Feldlinien sind nur ein Resultat der Überlagerung des jeweils pos und neg Feldes.
* Partikel spürt nur Feld (sein eigenes?)
* https://www.leifiphysik.de/elektrizitaetslehre/ladungen-elektrisches-feld/grundwissen/potential-und-elektrische-spannung
* https://www.leifiphysik.de/elektrizitaetslehre/ladungen-elektrisches-feld/grundwissen/elektrisches-feld-und-feldliniendarstellung#:~:text=F%C3%BCr%20die%20elektrische%20Feldst%C3%A4rke%20im,%CE%B5%200%20%E2%8B%85%20%7C%20Q%20%7C%20r
* https://www.leifiphysik.de/elektrizitaetslehre/ladungen-elektrisches-feld/grundwissen/ueberlagerung-elektrischer-felder
* Visualization of Quantum Physics (Quantum Mechanics) - YouTube  
  https://youtu.be/p7bzE1E5PMY

# Special-Infos
* Eine Weltformel ohne Quantengravitation - Spektrum der Wissenschaft 
   https://www.spektrum.de/news/eine-weltformel-ohne-quantengravitation/2201277
* Ist die Raumzeit gequantelt? - Neue Experimente sollen Vereinbarkeit von Gravitation und Quantenphysik testen - scinexx.de 
  https://www.scinexx.de/?p=278997
