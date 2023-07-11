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
