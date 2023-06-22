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
* Tentacles/ Pull-/Push-Fields
  * Move only push/pull to other parts (using Fields)
    * remove direct acceleration
  * Connect/ Release to other Parts
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
  * Zoom and Scroll
  * Save and Load

## DONE
