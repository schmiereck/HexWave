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
* Acceleration and Velocity
  * any external force acting on a part is added as acceleration.
  * The accelerations only work for the time step and are not added over time.
  * If a part can then move in the direction of acceleration, its speed/ velocity will be affected.
  * If the particle is blocked, its acceleration is passed on to the other particle as pressure. This pressure acceleration is then also used in the next time step. The pressure is thus passed on to other neighboring particles over time.
  * The floor has to absorb and swallow the pressure.
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
